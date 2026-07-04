package com.ultragol.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.ultragol.app.models.ContentItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages offline downloads: saves content metadata in SharedPreferences
 * and downloads the poster image to the app's private files directory.
 *
 * Stored per-profile, matching the FavoritesManager pattern.
 */
public class DownloadsManager {

    private static final String KEY = "items";

    public interface DownloadCallback {
        /** Called on the main thread when poster download finishes (success or failure). */
        void onComplete(boolean success);
    }

    // ── Prefs ─────────────────────────────────────────────────────────────────

    private static String prefsName(Context ctx) {
        return ProfileManager.dataKey(ctx, "downloads");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Returns true if the item's metadata has been saved for offline access. */
    public static boolean isDownloaded(Context ctx, ContentItem item) {
        for (ContentItem c : getAll(ctx)) {
            if (c.getTmdbId() == item.getTmdbId()) return true;
        }
        return false;
    }

    /**
     * Saves metadata and kicks off a background poster download.
     * Calls {@code callback} on the main thread when done.
     */
    public static void add(Context ctx, ContentItem item, DownloadCallback callback) {
        if (isDownloaded(ctx, item)) {
            if (callback != null) callback.onComplete(true);
            return;
        }
        // Save metadata immediately so it shows up in the list right away
        saveMetadata(ctx, item, "");

        // Download poster in background
        new Thread(() -> {
            String localPath = downloadPoster(ctx, item);
            // Update saved entry with local poster path
            if (!localPath.isEmpty()) {
                updatePosterPath(ctx, item.getTmdbId(), localPath);
            }
            if (callback != null) {
                new android.os.Handler(android.os.Looper.getMainLooper())
                        .post(() -> callback.onComplete(true));
            }
        }).start();
    }

    /** Removes metadata and deletes cached poster image. */
    public static void remove(Context ctx, ContentItem item) {
        // Delete poster file
        File posterFile = posterFile(ctx, item.getTmdbId());
        if (posterFile.exists()) posterFile.delete();

        // Remove from list
        List<ContentItem> list = getAll(ctx);
        list.removeIf(c -> c.getTmdbId() == item.getTmdbId());
        persistList(ctx, list);
    }

    /** Returns all downloaded items for the current profile. */
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
                // Prefer local poster if it exists
                String local = o.optString("localPosterPath", "");
                if (!local.isEmpty() && new File(local).exists()) {
                    it.setLocalPosterPath(local);
                }
                list.add(it);
            }
        } catch (Exception ignored) {}
        return list;
    }

    /**
     * Returns the local poster file path for an item if it has been cached,
     * or an empty string if not available.
     */
    public static String getLocalPosterPath(Context ctx, int tmdbId) {
        File f = posterFile(ctx, tmdbId);
        return f.exists() ? f.getAbsolutePath() : "";
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private static void saveMetadata(Context ctx, ContentItem item, String localPath) {
        List<ContentItem> list = getAll(ctx);
        // Avoid duplicates
        list.removeIf(c -> c.getTmdbId() == item.getTmdbId());
        if (!localPath.isEmpty()) item.setLocalPosterPath(localPath);
        list.add(0, item);
        persistList(ctx, list);
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
                o.put("title", it.getTitle());
                o.put("genre", it.getGenre());
                o.put("year", it.getYear());
                o.put("rating", it.getRating());
                o.put("posterUrl", it.getPosterUrl());
                o.put("overview", it.getOverview());
                o.put("type", it.getContentType());
                o.put("tmdbId", it.getTmdbId());
                o.put("backdropUrl", it.getBackdropUrl());
                o.put("localPosterPath", it.getLocalPosterPath());
                arr.put(o);
            }
            ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    /** Downloads the poster image and saves it to private storage. Returns the file path or "". */
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
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
            }
            conn.disconnect();
            return dest.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }

    private static File postersDir(Context ctx) {
        return new File(ctx.getFilesDir(), "downloads/posters");
    }

    private static File posterFile(Context ctx, int tmdbId) {
        return new File(postersDir(ctx), tmdbId + ".jpg");
    }
}
