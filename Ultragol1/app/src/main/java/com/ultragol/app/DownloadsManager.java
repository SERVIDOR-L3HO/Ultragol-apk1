package com.ultragol.app;

import android.content.Context;
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
     * Always queries DownloadUtil for the live state.
     */
    public static String getVideoState(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());
        if (stored == null) return "NONE";
        String live = DownloadUtil.getInstance(ctx)
                .getState(String.valueOf(item.getTmdbId()));
        if (!"NONE".equals(live) && !live.equals(stored.getVideoState())) {
            updateStoredState(ctx, item.getTmdbId(), live);
        }
        return "NONE".equals(live) ? stored.getVideoState() : live;
    }

    /**
     * Returns 0–100 progress while downloading, or -1 if unknown.
     */
    public static int getDownloadProgress(Context ctx, int tmdbId) {
        return DownloadUtil.getInstance(ctx).getProgress(String.valueOf(tmdbId));
    }

    /**
     * Starts an ExoPlayer offline download for the given video URL.
     * Works for both HLS (M3U8) and progressive MP4.
     * Saves metadata immediately so the item appears in the downloads list.
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

    /** Removes metadata, poster, and cancels the ExoPlayer download. */
    public static void remove(Context ctx, ContentItem item) {
        // Cancel ExoPlayer download
        DownloadUtil.getInstance(ctx).removeDownload(String.valueOf(item.getTmdbId()));

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
