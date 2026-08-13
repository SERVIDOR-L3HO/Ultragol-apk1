package com.ultragol.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ultragol.app.models.ContentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages real offline video downloads:
 *  - Direct MP4 → Android's system DownloadManager, saved into the public
 *    Movies/ACTIONPLAY folder so it shows up in the Gallery/Files apps.
 *  - HLS (m3u8) → HlsDownloadEngine (run inside VideoDownloadService), which
 *    fetches every segment and remuxes them into a real standalone .mp4 in
 *    the same public folder via MediaStore.
 *
 * localVideoPath always ends up holding a real, playable location once a
 * download completes: either a content:// MediaStore URI or (pre-API29) an
 * absolute file path — never a bare remote stream URL.
 */
public class DownloadsManager {

    private static final String KEY = "items";
    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /** requestPermissions code used by {@link #ensureStoragePermission(Activity)}. */
    public static final int REQ_STORAGE_PERMISSION = 2101;

    public interface DownloadCallback {
        void onComplete(boolean success);
    }

    private static String prefsName(Context ctx) {
        return ProfileManager.dataKey(ctx, "downloads");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * On API 23-28 saving into the public Movies folder needs WRITE_EXTERNAL_STORAGE
     * granted at runtime; API 29+ goes through MediaStore/scoped storage and needs
     * nothing. Returns true when a download may proceed, otherwise asks for the
     * permission and returns false so the caller can bail out and let the user retry.
     */
    public static boolean ensureStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return true;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE_PERMISSION);
        return false;
    }

    public static boolean isDownloaded(Context ctx, ContentItem item) {
        for (ContentItem c : getAll(ctx)) {
            if (c.getTmdbId() == item.getTmdbId()) return true;
        }
        return false;
    }

    /** Returns "NONE" | "DOWNLOADING" | "COMPLETE" | "FAILED". */
    public static String getVideoState(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());
        if (stored == null) return "NONE";

        // Direct-mp4 downloads run through Android's system DownloadManager — poll it live.
        if (stored.getDownloadId() >= 0 && !"COMPLETE".equals(stored.getVideoState())) {
            String dmState = queryAndroidDmState(ctx, stored.getDownloadId());
            if (!"NONE".equals(dmState)) {
                if ("COMPLETE".equals(dmState)) {
                    finalizeAndroidDmDownload(ctx, item, stored.getDownloadId());
                } else if (!dmState.equals(stored.getVideoState())) {
                    updateStoredState(ctx, item.getTmdbId(), dmState);
                }
                return dmState;
            }
        }
        // HLS downloads (VideoDownloadService) keep this record updated directly.
        return stored.getVideoState();
    }

    /** Returns 0-100 while downloading, or -1 if unknown. */
    public static int getDownloadProgress(Context ctx, int tmdbId) {
        ContentItem stored = findStored(ctx, tmdbId);
        if (stored == null) return -1;
        if (stored.getDownloadId() >= 0) return queryAndroidDmProgress(ctx, stored.getDownloadId());
        return stored.getDownloadProgress();
    }

    /**
     * Starts a real MP4 download via Android's system DownloadManager, saved into the
     * public Movies/ACTIONPLAY folder (Gallery/Files-app visible, survives being copied
     * off-device, standard DownloadManager retry/resume behavior included for free).
     */
    public static void startDirectMp4Download(Context ctx, ContentItem item, String videoUrl) {
        String filename = HlsDownloadEngine.sanitizeFilename(item.getTitle()) + "_" + item.getTmdbId() + ".mp4";

        saveOrUpdateRecord(ctx, item, "", "", "DOWNLOADING", -1L);

        android.app.DownloadManager dm =
            (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            try {
                android.app.DownloadManager.Request req =
                    new android.app.DownloadManager.Request(Uri.parse(videoUrl));
                req.setTitle(item.getTitle());
                req.setDescription("Action Play — descargando película…");
                req.setNotificationVisibility(
                    android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_MOVIES, HlsDownloadEngine.FOLDER_NAME + "/" + filename);
                //noinspection deprecation — harmless no-op on API29+, still needed pre-29
                req.allowScanningByMediaScanner();
                req.addRequestHeader("User-Agent", UA);
                req.addRequestHeader("Referer", "https://unlimplay.com/");
                long dlId = dm.enqueue(req);
                saveOrUpdateRecord(ctx, item, "", "", "DOWNLOADING", dlId);
            } catch (Exception e) {
                updateStoredState(ctx, item.getTmdbId(), "FAILED");
            }
        }

        new Thread(() -> {
            String posterPath = downloadPoster(ctx, item);
            if (!posterPath.isEmpty()) updatePosterPath(ctx, item.getTmdbId(), posterPath);
        }).start();
    }

    /** Starts a real HLS (m3u8) download, run end-to-end inside VideoDownloadService. */
    public static void startVideoDownload(Context ctx, ContentItem item, String videoUrl, String referer) {
        saveOrUpdateRecord(ctx, item, "", "", "DOWNLOADING", -1L);

        Intent intent = new Intent(ctx, VideoDownloadService.class);
        intent.putExtra(VideoDownloadService.EXTRA_TMDB_ID, item.getTmdbId());
        intent.putExtra(VideoDownloadService.EXTRA_URL, videoUrl);
        intent.putExtra(VideoDownloadService.EXTRA_REFERER, referer != null ? referer : "");
        intent.putExtra(VideoDownloadService.EXTRA_TITLE, item.getTitle());
        ContextCompat.startForegroundService(ctx, intent);

        new Thread(() -> {
            String posterPath = downloadPoster(ctx, item);
            if (!posterPath.isEmpty()) updatePosterPath(ctx, item.getTmdbId(), posterPath);
        }).start();
    }

    // ── Callbacks from VideoDownloadService ───────────────────────────────────

    public static void updateProgress(Context ctx, int tmdbId, int percent) {
        updateRecord(ctx, tmdbId, o -> {
            o.put("videoState", "DOWNLOADING");
            o.put("downloadProgress", percent);
        });
    }

    public static void markComplete(Context ctx, int tmdbId, String contentUri) {
        updateRecord(ctx, tmdbId, o -> {
            o.put("videoState", "COMPLETE");
            o.put("downloadProgress", 100);
            o.put("localVideoPath", contentUri != null ? contentUri : "");
        });
    }

    public static void markFailed(Context ctx, int tmdbId) {
        updateRecord(ctx, tmdbId, o -> o.put("videoState", "FAILED"));
    }

    /** Removes metadata, poster, video file/MediaStore entry, and cancels any active download. */
    public static void remove(Context ctx, ContentItem item) {
        ContentItem stored = findStored(ctx, item.getTmdbId());
        if (stored != null) {
            if (stored.getDownloadId() >= 0) {
                try {
                    android.app.DownloadManager dm = (android.app.DownloadManager)
                        ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                    if (dm != null) dm.remove(stored.getDownloadId());
                } catch (Exception ignored) {}
            }
            deleteVideoFile(ctx, stored.getLocalVideoPath());
        }

        File posterFile = posterFile(ctx, item.getTmdbId());
        if (posterFile.exists()) posterFile.delete();

        List<ContentItem> list = getAll(ctx);
        list.removeIf(c -> c.getTmdbId() == item.getTmdbId());
        persistList(ctx, list);
    }

    private static void deleteVideoFile(Context ctx, String path) {
        if (path == null || path.isEmpty()) return;
        try {
            if (path.startsWith("content://")) {
                ctx.getContentResolver().delete(Uri.parse(path), null, null);
            } else if (path.startsWith("/")) {
                File f = new File(path);
                if (f.exists()) f.delete();
            }
        } catch (Exception ignored) {}
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
                it.setDownloadProgress(o.optInt("downloadProgress", -1));
                list.add(it);
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static String getLocalPosterPath(Context ctx, int tmdbId) {
        File f = posterFile(ctx, tmdbId);
        return f.exists() ? f.getAbsolutePath() : "";
    }

    // ── Android DownloadManager helpers (direct MP4 only) ─────────────────────

    private static void finalizeAndroidDmDownload(Context ctx, ContentItem item, long downloadId) {
        String uri = resolveDownloadedUri(ctx, downloadId);
        saveOrUpdateRecord(ctx, item, "", uri != null ? uri : "", "COMPLETE", downloadId);
    }

    private static String resolveDownloadedUri(Context ctx, long downloadId) {
        try {
            android.app.DownloadManager dm =
                (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm == null) return null;
            Uri uri = dm.getUriForDownloadedFile(downloadId);
            return uri != null ? uri.toString() : null;
        } catch (Exception e) { return null; }
    }

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

    private interface FieldUpdater { void apply(JSONObject o) throws JSONException; }

    private static void updateRecord(Context ctx, int tmdbId, FieldUpdater updater) {
        try {
            String json = ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .getString(KEY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optInt("tmdbId", 0) == tmdbId) {
                    updater.apply(o);
                    arr.put(i, o);
                    break;
                }
            }
            ctx.getSharedPreferences(prefsName(ctx), Context.MODE_PRIVATE)
                    .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    private static void updateStoredState(Context ctx, int tmdbId, String state) {
        updateRecord(ctx, tmdbId, o -> o.put("videoState", state));
    }

    private static void updatePosterPath(Context ctx, int tmdbId, String localPath) {
        updateRecord(ctx, tmdbId, o -> o.put("localPosterPath", localPath));
    }

    private static void persistList(Context ctx, List<ContentItem> list) {
        try {
            JSONArray arr = new JSONArray();
            for (ContentItem it : list) {
                JSONObject o = new JSONObject();
                o.put("title",            it.getTitle());
                o.put("genre",            it.getGenre());
                o.put("year",             it.getYear());
                o.put("rating",           it.getRating());
                o.put("posterUrl",        it.getPosterUrl());
                o.put("overview",         it.getOverview());
                o.put("type",             it.getContentType());
                o.put("tmdbId",           it.getTmdbId());
                o.put("backdropUrl",      it.getBackdropUrl());
                o.put("localPosterPath",  it.getLocalPosterPath());
                o.put("localVideoPath",   it.getLocalVideoPath());
                o.put("downloadId",       it.getDownloadId());
                o.put("videoState",       it.getVideoState());
                o.put("downloadProgress", it.getDownloadProgress());
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
