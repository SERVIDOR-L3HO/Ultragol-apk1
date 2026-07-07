package com.ultragol.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import java.util.List;

/**
 * Background service that handles ExoPlayer offline downloads (HLS segments + MP4).
 * Required by ExoPlayer's DownloadManager architecture.
 */
public class VideoDownloadService extends DownloadService {

    private static final int    NOTIFICATION_ID = 9001;
    public  static final String CHANNEL_ID      = "ultragol_downloads";

    public VideoDownloadService() {
        super(NOTIFICATION_ID,
              DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
              CHANNEL_ID,
              R.string.download_channel_name,
              R.string.download_channel_desc);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        return DownloadUtil.getInstance(this).getDownloadManager();
    }

    @Override
    protected Scheduler getScheduler() {
        return null; // No scheduler — downloads only run when app is open
    }

    @Override
    protected Notification getForegroundNotification(
            List<Download> downloads, int notMetRequirements) {
        ensureChannel();
        return new DownloadNotificationHelper(this, CHANNEL_ID)
            .buildProgressNotification(
                this,
                android.R.drawable.stat_sys_download,
                null, null,
                downloads,
                notMetRequirements);
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
}
