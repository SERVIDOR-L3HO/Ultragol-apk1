package com.ultragol.app;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.File;

public class UpdateInstaller {

    public interface ProgressCallback {
        void onProgress(int percent);
        void onComplete();
        void onFailed();
    }

    private long downloadId = -1;
    private BroadcastReceiver receiver;
    private final Context context;

    public UpdateInstaller(Context context) {
        this.context = context.getApplicationContext();
    }

    public void download(String url, ProgressCallback callback) {
        File dest = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "ultragol_update.apk");
        if (dest.exists()) dest.delete();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle("Actualizando Ultragol")
                .setDescription("Descargando nueva versión…")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(dest))
                .setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE);

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = dm.enqueue(request);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id != downloadId) return;
                ctx.unregisterReceiver(this);

                DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
                Cursor cursor = dm.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIdx);
                    cursor.close();
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        callback.onComplete();
                        installApk(dest);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        };

        context.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        new Thread(() -> {
            while (downloadId != -1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) { break; }
                DownloadManager.Query q = new DownloadManager.Query().setFilterById(downloadId);
                Cursor c = dm.query(q);
                if (c != null && c.moveToFirst()) {
                    int totalIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                    int downloadedIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    long total = c.getLong(totalIdx);
                    long downloaded = c.getLong(downloadedIdx);
                    c.close();
                    if (total > 0) {
                        int pct = (int) (downloaded * 100L / total);
                        android.os.Handler main = new android.os.Handler(android.os.Looper.getMainLooper());
                        main.post(() -> callback.onProgress(pct));
                    }
                }
            }
        }).start();
    }

    private void installApk(File file) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider", file);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    public void cancel() {
        if (downloadId != -1) {
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            dm.remove(downloadId);
            downloadId = -1;
        }
        if (receiver != null) {
            try { context.unregisterReceiver(receiver); } catch (Exception ignored) {}
            receiver = null;
        }
    }
}
