package com.ultragol.app;

import android.content.Context;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

/**
 * Singleton wrapper for Google Cast / Chromecast operations.
 * Gracefully degrades when Google Play Services is unavailable.
 */
public class CastManager {

    private static CastManager instance;
    private CastContext castContext;

    private CastManager(Context ctx) {
        try {
            castContext = CastContext.getSharedInstance(ctx.getApplicationContext());
        } catch (Exception ignored) {
            castContext = null;
        }
    }

    public static synchronized CastManager getInstance(Context ctx) {
        if (instance == null) instance = new CastManager(ctx.getApplicationContext());
        return instance;
    }

    public boolean isAvailable()  { return castContext != null; }

    public boolean isCasting() {
        if (castContext == null) return false;
        CastSession s = castContext.getSessionManager().getCurrentCastSession();
        return s != null && s.isConnected();
    }

    public SessionManager getSessionManager() {
        return castContext != null ? castContext.getSessionManager() : null;
    }

    /**
     * Load a video on the currently connected Chromecast.
     * Call after the Cast session is started (onSessionStarted callback).
     */
    public void castVideo(String videoUrl, String title, String posterUrl,
                          boolean isM3u8, long startMs) {
        RemoteMediaClient client = getRemoteClient();
        if (client == null) return;

        MediaMetadata meta = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        meta.putString(MediaMetadata.KEY_TITLE, title != null ? title : "");

        MediaInfo info = new MediaInfo.Builder(videoUrl)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType(isM3u8 ? "application/x-mpegURL" : "video/mp4")
            .setMetadata(meta)
            .build();

        client.load(new MediaLoadRequestData.Builder()
            .setMediaInfo(info)
            .setAutoplay(true)
            .setCurrentTime(startMs)
            .build());
    }

    public void togglePlayPause() {
        RemoteMediaClient c = getRemoteClient();
        if (c == null) return;
        if (c.isPlaying()) c.pause(); else c.play();
    }

    public boolean isCastPlaying() {
        RemoteMediaClient c = getRemoteClient();
        return c != null && c.isPlaying();
    }

    public void stopCasting() {
        if (castContext != null) castContext.getSessionManager().endCurrentSession(true);
    }

    public long getCastPosition() {
        RemoteMediaClient c = getRemoteClient();
        return c != null ? c.getApproximateStreamPosition() : 0L;
    }

    private RemoteMediaClient getRemoteClient() {
        if (!isCasting()) return null;
        CastSession s = castContext.getSessionManager().getCurrentCastSession();
        return s != null ? s.getRemoteMediaClient() : null;
    }
}
