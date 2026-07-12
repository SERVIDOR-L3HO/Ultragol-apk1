package com.ultragol.app;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.ultragol.app.models.ContentItem;

/**
 * Receives ACTION_DOWNLOAD_COMPLETE from Android's DownloadManager.
 * Shows a success or failure notification and broadcasts a local
 * intent so DownloadsFragment can refresh immediately.
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    public static final String ACTION_REFRESH =
        "com.ultragol.app.DOWNLOAD_STATUS_CHANGED";

    private static final String CH_DOWNLOADS = "action_play_downloads";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) return;

        long dlId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (dlId < 0) return;

        // Find the matching ContentItem by downloadId
        ContentItem match = null;
        for (ContentItem item : DownloadsManager.getAll(ctx)) {
            if (item.getDownloadId() == dlId) {
                match = item;
                break;
            }
        }
        if (match == null) return; // Not one of our downloads

        // Query Android DM for final status
        boolean success = false;
        String reasonMsg = "";
        try {
            DownloadManager dm =
                (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query q =
                new DownloadManager.Query().setFilterById(dlId);
            Cursor c = dm != null ? dm.query(q) : null;
            if (c != null && c.moveToFirst()) {
                int statusCol = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int reasonCol = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                int status = statusCol >= 0 ? c.getInt(statusCol) : -1;
                int reason = reasonCol >= 0 ? c.getInt(reasonCol) : 0;
                success = (status == DownloadManager.STATUS_SUCCESSFUL);
                if (!success) reasonMsg = " (código " + reason + ")";
            }
            if (c != null) c.close();
        } catch (Exception ignored) {}

        // Show system notification
        ensureChannel(ctx);
        String title  = match.getTitle() != null ? match.getTitle() : "Película";
        String notifTitle = success
            ? "✅ Descarga completada"
            : "❌ Error en descarga";
        String notifText = success
            ? "\"" + title + "\" ya está disponible sin internet"
            : "No se pudo descargar \"" + title + "\"" + reasonMsg;

        NotificationManager nm =
            (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx, CH_DOWNLOADS)
                .setSmallIcon(success
                    ? android.R.drawable.stat_sys_download_done
                    : android.R.drawable.stat_notify_error)
                .setContentTitle(notifTitle)
                .setContentText(notifText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifText))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
            nm.notify((int) (dlId % Integer.MAX_VALUE), nb.build());
        }

        // Broadcast so DownloadsFragment refreshes immediately
        ctx.sendBroadcast(new Intent(ACTION_REFRESH));
    }

    private void ensureChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null && nm.getNotificationChannel(CH_DOWNLOADS) == null) {
                NotificationChannel ch = new NotificationChannel(
                    CH_DOWNLOADS,
                    "Descargas",
                    NotificationManager.IMPORTANCE_HIGH);
                ch.setDescription("Estado de las descargas de películas");
                nm.createNotificationChannel(ch);
            }
        }
    }
}
