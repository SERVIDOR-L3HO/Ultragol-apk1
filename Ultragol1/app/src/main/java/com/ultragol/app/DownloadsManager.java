package com.ultragol.app;

import android.app.DownloadManager;
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
 * Manages real MP4 downloads via Android DownloadManager plus metadata/poster caching.
 * Stored per-profile using ProfileManager.
 */
public class DownloadsManager {

    private static final String KEY = "items";

    public interface DownloadCallback {
        void onComplete(boolean success);
    }

    // ── Prefs ─────────────────────────────────────────────────────────────────

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
     * Syncs state from Android DownloadManager if a downloadId is active.
     */
    public static String getVideoState(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());
        if (stored == null) return "NONE";
        String state = stored.getVideoState();
        if ("DOWNLOADING".equals(state) && stored.getDownloadId() >= 0) {
            // Check real status from system DownloadManager
            int dmStatus = queryDmStatus(ctx, stored.getDownloadId());
            if (dmStatus == DownloadManager.STATUS_SUCCESSFUL) {
                updateStoredState(ctx, item.getTmdbId(), "COMPLETE");
                return "COMPLETE";
            } else if (dmStatus == DownloadManager.STATUS_FAILED) {
                updateStoredState(ctx, item.getTmdbId(), "FAILED");
                return "FAILED";
            }
        }
        return state;
    }

    /**
     * Returns 0–100 progress while downloading, 100 if complete, -1 if unknown.
     */
    public static int getDownloadProgress(Context ctx, long downloadId) {
        if (downloadId < 0) return -1;
        DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query().setFilterById(downloadId);
        try (Cursor c = dm.query(q)) {
            if (c != null && c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) return 100;
                if (status == DownloadManager.STATUS_RUNNING) {
                    long total = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    long done  = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    return (total > 0) ? (int) (done * 100L / total) : 0;
                }
            }
        } catch (Exception ignored) {}
        return -1;
    }

    /**
     * Start real MP4 download via Android DownloadManager.
     * Saves metadata immediately so it appears in the downloads list.
     */
    public static void startVideoDownload(Context ctx, ContentItem item,
                                          String videoUrl, String referer) {
        // Save metadata with DOWNLOADING state so it shows in the list immediately
        saveOrUpdateRecord(ctx, item, "", videoUrl, "DOWNLOADING", -1L);

        try {
            DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);

            String safeName = item.getTitle()
                    .replaceAll("[^a-zA-Z0-9\\-_ áéíóúÁÉÍÓÚñÑ]", "")
                    .trim().replace(" ", "_");
            if (safeName.isEmpty()) safeName = "video_" + item.getTmdbId();
            String fileName = safeName + ".mp4";

            File dir = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Ultragol");
            if (!dir.exists()) dir.mkdirs();
            String localPath = new File(dir, fileName).getAbsolutePath();

            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(videoUrl));
            req.setTitle(item.getTitle());
            req.setDescription("Descargando video...");
            req.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.addRequestHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            if (referer != null && !referer.isEmpty()) {
                req.addRequestHeader("Referer", referer);
            }
            req.setDestinationInExternalFilesDir(ctx, Environment.DIRECTORY_MOVIES,
                    "Ultragol/" + fileName);

            long downloadId = dm.enqueue(req);
            // Update record with real downloadId and local path
            saveOrUpdateRecord(ctx, item, "", localPath, "DOWNLOADING", downloadId);

            // Also download poster in background
            new Thread(() -> {
                String posterPath = downloadPoster(ctx, item);
                if (!posterPath.isEmpty()) updatePosterPath(ctx, item.getTmdbId(), posterPath);
            }).start();

        } catch (Exception e) {
            saveOrUpdateRecord(ctx, item, "", "", "FAILED", -1L);
        }
    }

    /**
     * Legacy add — saves only metadata + poster (no real video file).
     * Kept for backward compatibility.
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

    /** Removes metadata, poster, and cancels/deletes the video download. */
    public static void remove(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());
        if (stored != null) {
            // Cancel DownloadManager download if active
            long dlId = stored.getDownloadId();
            if (dlId >= 0) {
                try {
                    DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                    dm.remove(dlId);
                } catch (Exception ignored) {}
            }
            // Delete local video file
            String vPath = stored.getLocalVideoPath();
            if (vPath != null && !vPath.isEmpty()) {
                File f = new File(vPath);
                if (f.exists()) f.delete();
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
                o.put("title",          it.getTitle());
                o.put("genre",          it.getGenre());
                o.put("year",           it.getYear());
                o.put("rating",         it.getRating());
                o.put("posterUrl",      it.getPosterUrl());
                o.put("overview",       it.getOverview());
                o.put("type",           it.getContentType());
                o.put("tmdbId",         it.getTmdbId());
                o.put("backdropUrl",    it.getBackdropUrl());
                o.put("localPosterPath",it.getLocalPosterPath());
                o.put("localVideoPath", it.getLocalVideoPath());
                o.put("downloadId",     it.getDownloadId());
                o.put("videoState",     it.getVideoState());
                arr.put(o);
            }
            ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    private static int queryDmStatus(Context ctx, long downloadId) {
        DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query().setFilterById(downloadId);
        try (Cursor c = dm.query(q)) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            }
        } catch (Exception ignored) {}
        return -1;
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
