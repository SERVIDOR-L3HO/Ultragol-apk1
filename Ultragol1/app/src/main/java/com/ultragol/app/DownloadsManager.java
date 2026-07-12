package com.ultragol.app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import com.ultragol.app.models.ContentItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages offline video downloads via ExoPlayer's DownloadManager (handles HLS + MP4).
 * Stores metadata + poster locally; the actual video data lives in ExoPlayer's cache.
 *
 * Key fields persisted per item:
 *  - localVideoPath : the captured stream URL (m3u8 or mp4) used for ExoPlayer download + playback
 *  - videoState     : last known state (overridden on each getVideoState call by DownloadUtil)
 */
public class DownloadsManager {

    private static final String KEY = "items";

    public interface DownloadCallback {
        void onComplete(boolean success);
    }

    private static String prefsName(Context ctx) {
        return ProfileManager.dataKey(ctx, "downloads");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public static boolean isDownloaded(Context ctx, ContentItem item) {
        for (ContentItem c : getAll(ctx)) {
            if (c.getTmdbId() == item.getTmdbId()) return true;
        }
        return false;
    }

    /**
     * Returns "NONE" | "DOWNLOADING" | "COMPLETE" | "FAILED"
     * Handles both ExoPlayer (HLS) and Android DownloadManager (MP4) downloads.
     */
    public static String getVideoState(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());
        if (stored == null) return "NONE";

        String videoPath = stored.getLocalVideoPath();

        // ── Android DownloadManager path (real .mp4 file) ──────────────────
        if (videoPath != null && videoPath.startsWith("/")) {
            File f = new File(videoPath);
            if (f.exists() && f.length() > 1024) {
                // File complete → persist state
                if (!"COMPLETE".equals(stored.getVideoState()))
                    updateStoredState(ctx, item.getTmdbId(), "COMPLETE");
                return "COMPLETE";
            }
            if (stored.getDownloadId() >= 0) {
                String dmState = queryAndroidDmState(ctx, stored.getDownloadId());
                if (!"NONE".equals(dmState)) {
                    if (!dmState.equals(stored.getVideoState()))
                        updateStoredState(ctx, item.getTmdbId(), dmState);
                    return dmState;
                }
            }
            return stored.getVideoState();
        }

        // ── ExoPlayer / HLS path ────────────────────────────────────────────
        String live = DownloadUtil.getInstance(ctx)
                .getState(String.valueOf(item.getTmdbId()));
        if (!"NONE".equals(live) && !live.equals(stored.getVideoState()))
            updateStoredState(ctx, item.getTmdbId(), live);
        return "NONE".equals(live) ? stored.getVideoState() : live;
    }

    /**
     * Returns 0–100 progress while downloading, or -1 if unknown.
     * Handles both Android DM and ExoPlayer.
     */
    public static int getDownloadProgress(Context ctx, int tmdbId) {
        ContentItem stored = findStored(ctx, tmdbId);
        if (stored != null && stored.getDownloadId() >= 0
                && stored.getLocalVideoPath() != null
                && stored.getLocalVideoPath().startsWith("/")) {
            return queryAndroidDmProgress(ctx, stored.getDownloadId());
        }
        return DownloadUtil.getInstance(ctx).getProgress(String.valueOf(tmdbId));
    }

    // ── Android DownloadManager helpers ──────────────────────────────────────

    private static String queryAndroidDmState(Context ctx, long dlId) {
        try {
            android.app.DownloadManager dm =
                (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm == null) return "NONE";
            android.app.DownloadManager.Query q =
                new android.app.DownloadManager.Query().setFilterById(dlId);
            Cursor c = dm.query(q);
            if (c != null && c.moveToFirst()) {
                int col = c.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS);
                int status = col >= 0 ? c.getInt(col) : -1;
                c.close();
                switch (status) {
                    case android.app.DownloadManager.STATUS_SUCCESSFUL: return "COMPLETE";
                    case android.app.DownloadManager.STATUS_RUNNING:
                    case android.app.DownloadManager.STATUS_PENDING:
                    case android.app.DownloadManager.STATUS_PAUSED:   return "DOWNLOADING";
                    case android.app.DownloadManager.STATUS_FAILED:   return "FAILED";
                }
            }
            if (c != null) c.close();
        } catch (Exception ignored) {}
        return "NONE";
    }

    private static int queryAndroidDmProgress(Context ctx, long dlId) {
        try {
            android.app.DownloadManager dm =
                (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm == null) return -1;
            android.app.DownloadManager.Query q =
                new android.app.DownloadManager.Query().setFilterById(dlId);
            Cursor c = dm.query(q);
            if (c != null && c.moveToFirst()) {
                int colDown = c.getColumnIndex(android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int colTotal = c.getColumnIndex(android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                long down  = colDown  >= 0 ? c.getLong(colDown)  : 0;
                long total = colTotal >= 0 ? c.getLong(colTotal) : 0;
                c.close();
                if (total > 0) return (int) (down * 100 / total);
            }
            if (c != null) c.close();
        } catch (Exception ignored) {}
        return -1;
    }

    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /**
     * Starts a REAL MP4 download via Android's DownloadManager.
     * The file is saved to internal Movies/ActionPlay/ directory as an actual .mp4 file.
     */
    public static void startDirectMp4Download(Context ctx, ContentItem item, String videoUrl) {
        // Destination: app-private Movies directory (no extra permissions needed)
        String filename = sanitizeFilename(item.getTitle())
            + "_" + item.getTmdbId() + ".mp4";
        File dir = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "ActionPlay");
        if (!dir.exists()) dir.mkdirs();
        File destFile = new File(dir, filename);
        String destPath = destFile.getAbsolutePath();

        // Save metadata immediately so the item appears in the list
        saveOrUpdateRecord(ctx, item, "", destPath, "DOWNLOADING", -1L);

        android.app.DownloadManager dm =
            (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            android.app.DownloadManager.Request req =
                new android.app.DownloadManager.Request(Uri.parse(videoUrl));
            req.setTitle(item.getTitle());
            req.setDescription("Action Play — descargando película…");
            req.setNotificationVisibility(
                android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setDestinationUri(Uri.fromFile(destFile));
            req.addRequestHeader("User-Agent", UA);
            req.addRequestHeader("Referer", "https://unlimplay.com/");
            long dlId = dm.enqueue(req);
            // Persist download ID so progress can be tracked
            saveOrUpdateRecord(ctx, item, "", destPath, "DOWNLOADING", dlId);
        }

        // Download poster in background
        new Thread(() -> {
            String posterPath = downloadPoster(ctx, item);
            if (!posterPath.isEmpty()) updatePosterPath(ctx, item.getTmdbId(), posterPath);
        }).start();
    }

    private static String sanitizeFilename(String name) {
        if (name == null) return "video";
        return name.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ _\\-]", "")
                   .trim().replace(" ", "_");
    }

    /**
     * Starts an ExoPlayer offline download for HLS (M3U8) or progressive MP4.
     * Note: for direct MP4 files, prefer startDirectMp4Download() instead.
     */
    public static void startVideoDownload(Context ctx, ContentItem item,
                                          String videoUrl, String referer) {
        // Save metadata immediately so the item shows in the list
        saveOrUpdateRecord(ctx, item, "", videoUrl, "DOWNLOADING", -1L);

        // Kick off ExoPlayer offline download
        DownloadUtil.getInstance(ctx).startDownload(String.valueOf(item.getTmdbId()), videoUrl);

        // Also download poster in background
        new Thread(() -> {
            String posterPath = downloadPoster(ctx, item);
            if (!posterPath.isEmpty()) updatePosterPath(ctx, item.getTmdbId(), posterPath);
        }).start();
    }

    /**
     * Legacy add — saves only metadata + poster (no video download).
     * Used from PlayerActivity's bookmark-style save.
     */
    public static void add(Context ctx, ContentItem item, DownloadCallback callback) {
        if (isDownloaded(ctx, item)) {
            if (callback != null) callback.onComplete(true);
            return;
        }
        saveOrUpdateRecord(ctx, item, "", "", "NONE", -1L);
        new Thread(() -> {
            String localPath = downloadPoster(ctx, item);
            if (!localPath.isEmpty()) updatePosterPath(ctx, item.getTmdbId(), localPath);
            if (callback != null) {
                new android.os.Handler(android.os.Looper.getMainLooper())
                        .post(() -> callback.onComplete(true));
            }
        }).start();
    }

    /** Removes metadata, poster, video file, and cancels any active download. */
    public static void remove(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());

        // Cancel ExoPlayer download (HLS)
        DownloadUtil.getInstance(ctx).removeDownload(String.valueOf(item.getTmdbId()));

        // Cancel Android DownloadManager download + delete .mp4 file
        if (stored != null) {
            if (stored.getDownloadId() >= 0) {
                try {
                    android.app.DownloadManager dm = (android.app.DownloadManager)
                        ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                    if (dm != null) dm.remove(stored.getDownloadId());
                } catch (Exception ignored) {}
            }
            String videoPath = stored.getLocalVideoPath();
            if (videoPath != null && videoPath.startsWith("/")) {
                File vf = new File(videoPath);
                if (vf.exists()) vf.delete();
            }
        }

        // Delete poster file
        File posterFile = posterFile(ctx, item.getTmdbId());
        if (posterFile.exists()) posterFile.delete();

        List<ContentItem> list = getAll(ctx);
        list.removeIf(c -> c.getTmdbId() == item.getTmdbId());
        persistList(ctx, list);
    }

    /** Returns all download records for the current profile. */
    public static List<ContentItem> getAll(Context ctx) {
        List<ContentItem> list = new ArrayList<>();
        try {
            String json = ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .getString(KEY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                ContentItem it = new ContentItem(
                        o.optString("title"), o.optString("genre"), o.optString("year"),
                        o.optString("rating"), o.optString("posterUrl"), o.optString("overview"),
                        o.optInt("type", 0), false, false);
                it.setTmdbId(o.optInt("tmdbId", 0));
                it.setBackdropUrl(o.optString("backdropUrl", ""));
                String local = o.optString("localPosterPath", "");
                if (!local.isEmpty() && new File(local).exists()) it.setLocalPosterPath(local);
                it.setLocalVideoPath(o.optString("localVideoPath", ""));
                it.setDownloadId(o.optLong("downloadId", -1L));
                it.setVideoState(o.optString("videoState", "NONE"));
                list.add(it);
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static String getLocalPosterPath(Context ctx, int tmdbId) {
        File f = posterFile(ctx, tmdbId);
        return f.exists() ? f.getAbsolutePath() : "";
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private static ContentItem findStored(Context ctx, int tmdbId) {
        for (ContentItem c : getAll(ctx)) {
            if (c.getTmdbId() == tmdbId) return c;
        }
        return null;
    }

    private static void saveOrUpdateRecord(Context ctx, ContentItem item,
                                           String localPoster, String localVideo,
                                           String videoState, long downloadId) {
        List<ContentItem> list = getAll(ctx);
        list.removeIf(c -> c.getTmdbId() == item.getTmdbId());
        if (!localPoster.isEmpty()) item.setLocalPosterPath(localPoster);
        if (!localVideo.isEmpty()) item.setLocalVideoPath(localVideo);
        item.setVideoState(videoState);
        item.setDownloadId(downloadId);
        list.add(0, item);
        persistList(ctx, list);
    }

    private static void updateStoredState(Context ctx, int tmdbId, String state) {
        try {
            String json = ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .getString(KEY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optInt("tmdbId", 0) == tmdbId) {
                    o.put("videoState", state);
                    arr.put(i, o);
                    break;
                }
            }
            ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    private static void updatePosterPath(Context ctx, int tmdbId, String localPath) {
        try {
            String json = ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .getString(KEY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optInt("tmdbId", 0) == tmdbId) {
                    o.put("localPosterPath", localPath);
                    arr.put(i, o);
                    break;
                }
            }
            ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    private static void persistList(Context ctx, List<ContentItem> list) {
        try {
            JSONArray arr = new JSONArray();
            for (ContentItem it : list) {
                JSONObject o = new JSONObject();
                o.put("title",           it.getTitle());
                o.put("genre",           it.getGenre());
                o.put("year",            it.getYear());
                o.put("rating",          it.getRating());
                o.put("posterUrl",       it.getPosterUrl());
                o.put("overview",        it.getOverview());
                o.put("type",            it.getContentType());
                o.put("tmdbId",          it.getTmdbId());
                o.put("backdropUrl",     it.getBackdropUrl());
                o.put("localPosterPath", it.getLocalPosterPath());
                o.put("localVideoPath",  it.getLocalVideoPath());
                o.put("downloadId",      it.getDownloadId());
                o.put("videoState",      it.getVideoState());
                arr.put(o);
            }
            ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    private static String downloadPoster(Context ctx, ContentItem item) {
        String url = item.getPosterUrl();
        if (url == null || url.isEmpty()) return "";
        try {
            File dir = postersDir(ctx);
            if (!dir.exists()) dir.mkdirs();
            File dest = posterFile(ctx, item.getTmdbId());
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.connect();
            if (conn.getResponseCode() != 200) return "";
            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[8192]; int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
            }
            conn.disconnect();
            return dest.getAbsolutePath();
        } catch (Exception e) { return ""; }
    }

    private static File postersDir(Context ctx) {
        return new File(ctx.getFilesDir(), "downloads/posters");
    }

    private static File posterFile(Context ctx, int tmdbId) {
        return new File(postersDir(ctx), tmdbId + ".jpg");
    }
}
