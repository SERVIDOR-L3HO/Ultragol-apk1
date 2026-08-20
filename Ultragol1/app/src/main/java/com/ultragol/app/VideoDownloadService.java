package com.ultragol.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

/**
 * Foreground service that runs a real HLS download (HlsDownloadEngine) end to
 * end: fetch every segment, remux to a standalone .mp4, save it into the
 * public Movies/ACTIONPLAY gallery folder, and report progress/completion
 * back into DownloadsManager's persisted records.
 */
public class VideoDownloadService extends Service {

    public static final String EXTRA_TMDB_ID     = "tmdbId";
    public static final String EXTRA_URL         = "url";
    public static final String EXTRA_REFERER     = "referer";
    public static final String EXTRA_TITLE       = "title";
    public static final String EXTRA_CONTENT_TYPE = "contentType";

    public static final String CHANNEL_ID = "ultragol_downloads";
    private static final int NOTIF_ID_BASE = 9100;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) { stopSelf(startId); return START_NOT_STICKY; }

        final int tmdbId      = intent.getIntExtra(EXTRA_TMDB_ID, 0);
        final String url      = intent.getStringExtra(EXTRA_URL);
        final String referer  = intent.getStringExtra(EXTRA_REFERER);
        final String title    = intent.getStringExtra(EXTRA_TITLE);
        final int contentType = intent.getIntExtra(EXTRA_CONTENT_TYPE, 0);
        final int notifId     = NOTIF_ID_BASE + tmdbId;

        ensureChannel();
        startForeground(notifId, buildProgressNotification(title, 0));

        new Thread(() -> {
            HlsDownloadEngine.Result result = HlsDownloadEngine.download(
                getApplicationContext(), url, referer, title, contentType,
                percent -> {
                    DownloadsManager.updateProgress(getApplicationContext(), tmdbId, percent);
                    notify(notifId, buildProgressNotification(title, percent));
                });

            if (result.success) {
                DownloadsManager.markComplete(getApplicationContext(), tmdbId, result.contentUri);
                notify(notifId, buildFinalNotification(title, true, null));
            } else {
                DownloadsManager.markFailed(getApplicationContext(), tmdbId);
                notify(notifId, buildFinalNotification(title, false, result.error));
            }

            stopForeground(false);
            stopSelf(startId);
        }).start();

        return START_NOT_STICKY;
    }

    private void notify(int id, Notification n) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(id, n);
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null && nm.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.download_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
                ch.setDescription(getString(R.string.download_channel_desc));
                nm.createNotificationChannel(ch);
            }
        }
    }

    private Notification buildProgressNotification(String title, int percent) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Descargando " + (title != null ? title : ""))
            .setContentText(percent + "%")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setProgress(100, percent, false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build();
    }

    private Notification buildFinalNotification(String title, boolean success, String error) {
        String text = success || error == null || error.isEmpty()
            ? (title != null ? title : "")
            : (title != null ? title + " — " + error : error);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(success ? "Descarga completada ✓" : "Error al descargar")
            .setContentText(text)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(false)
            .setAutoCancel(true)
            .build();
    }
}
