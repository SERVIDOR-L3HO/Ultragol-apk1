package com.ultragol.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    public interface Callback {
        void onResult(boolean updateAvailable, JSONObject data);
    }

    private static final String VERSION_URL =
            "https://da8cd50c-b6a4-475c-8783-70b87f2e70be-00-3s6o17dujacbo.spock.replit.dev/ultragol1/version";

    public static void check(Context context, Callback callback) {
        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(VERSION_URL).openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.setRequestMethod("GET");

                int code = conn.getResponseCode();
                if (code != 200) {
                    postResult(callback, false, null);
                    return;
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }

                JSONObject json        = new JSONObject(sb.toString());
                int remoteVersionCode  = json.optInt("versionCode", 0);
                int currentVersionCode = getCurrentVersionCode(context);

                boolean needsUpdate = remoteVersionCode > currentVersionCode;
                postResult(callback, needsUpdate, needsUpdate ? json : null);

            } catch (Exception e) {
                postResult(callback, false, null);
            }
        }).start();
    }

    private static void postResult(Callback cb, boolean update, JSONObject data) {
        new Handler(Looper.getMainLooper()).post(() -> cb.onResult(update, data));
    }

    private static int getCurrentVersionCode(Context ctx) {
        try {
            return ctx.getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static void showUpdateDialog(Context context, JSONObject data) {
        if (data == null) return;

        String versionName  = data.optString("versionName", "");
        String changelog    = data.optString("changelog",   "");
        String downloadUrl  = data.optString("downloadUrl", "");
        boolean forceUpdate = data.optBoolean("forceUpdate", false);

        // Build the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);

        TextView tvVersion   = dialogView.findViewById(R.id.updateVersion);
        TextView tvChangelog = dialogView.findViewById(R.id.updateChangelog);

        if (tvVersion   != null) tvVersion.setText("Versión " + versionName + " disponible");
        if (tvChangelog != null) {
            if (changelog.isEmpty()) {
                tvChangelog.setVisibility(View.GONE);
            } else {
                tvChangelog.setText(changelog);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.UpdateDialogTheme)
                .setView(dialogView)
                .setCancelable(!forceUpdate)
                .setPositiveButton("⬇  Descargar ahora", (dialog, which) -> {
                    if (!downloadUrl.isEmpty()) {
                        try {
                            context.startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            );
                        } catch (Exception ignored) {}
                    }
                });

        if (!forceUpdate) {
            builder.setNegativeButton("Más tarde", null);
        }

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }
}
