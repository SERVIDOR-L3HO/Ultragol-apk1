package com.ultragol.app;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.upstream.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Singleton that manages ExoPlayer offline downloads (HLS + MP4).
 * Provides the shared DownloadManager and Cache used across the app.
 */
public class DownloadUtil {

    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static DownloadUtil instance;

    private final DownloadManager downloadManager;
    private final SimpleCache      downloadCache;
    private final Context          appCtx;

    private DownloadUtil(Context ctx) {
        appCtx = ctx.getApplicationContext();
        StandaloneDatabaseProvider dbProvider = new StandaloneDatabaseProvider(appCtx);
        File cacheDir = new File(appCtx.getFilesDir(), "exo_dl_cache");
        downloadCache = new SimpleCache(cacheDir, new NoOpCacheEvictor(), dbProvider);

        DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory()
            .setUserAgent(UA)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(30_000);

        downloadManager = new DownloadManager(
            appCtx, dbProvider, downloadCache, httpFactory,
            Executors.newFixedThreadPool(4));
        downloadManager.setMaxParallelDownloads(2);
    }

    public static synchronized DownloadUtil getInstance(Context ctx) {
        if (instance == null) instance = new DownloadUtil(ctx.getApplicationContext());
        return instance;
    }

    public DownloadManager getDownloadManager() { return downloadManager; }
    public SimpleCache     getDownloadCache()    { return downloadCache; }

    /**
     * Builds a DataSource.Factory that serves content from the offline cache first,
     * falling back to the network. Use this in MediaActivity for offline playback.
     */
    public DataSource.Factory buildCacheDataSourceFactory() {
        DefaultHttpDataSource.Factory http = new DefaultHttpDataSource.Factory().setUserAgent(UA);
        return new CacheDataSource.Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(http)
            .setCacheWriteDataSinkFactory(null); // read-only from existing cache
    }

    /** Start downloading content at {@code videoUrl}. Uses the tmdbId as unique key. */
    public void startDownload(String downloadId, String videoUrl) {
        DownloadRequest request = new DownloadRequest.Builder(downloadId, Uri.parse(videoUrl))
            .build();
        DownloadService.sendAddDownload(appCtx, VideoDownloadService.class, request, false);
    }

    /** Remove (cancel + delete) a download by its ID. */
    public void removeDownload(String downloadId) {
        DownloadService.sendRemoveDownload(appCtx, VideoDownloadService.class, downloadId, false);
    }

    /** Returns 0–100 progress, or -1 if not downloading / unknown. */
    public int getProgress(String downloadId) {
        try {
            Download dl = downloadManager.getDownloadIndex().getDownload(downloadId);
            if (dl == null) return -1;
            if (dl.state == Download.STATE_COMPLETED)  return 100;
            if (dl.state == Download.STATE_DOWNLOADING) return (int) dl.getPercentDownloaded();
        } catch (IOException ignored) {}
        return -1;
    }

    /** Returns "NONE" | "DOWNLOADING" | "COMPLETE" | "FAILED" */
    public String getState(String downloadId) {
        try {
            Download dl = downloadManager.getDownloadIndex().getDownload(downloadId);
            if (dl == null) return "NONE";
            switch (dl.state) {
                case Download.STATE_COMPLETED:   return "COMPLETE";
                case Download.STATE_DOWNLOADING:
                case Download.STATE_QUEUED:
                case Download.STATE_RESTARTING:  return "DOWNLOADING";
                case Download.STATE_FAILED:      return "FAILED";
                default:                         return "NONE";
            }
        } catch (IOException ignored) {}
        return "NONE";
    }
}
