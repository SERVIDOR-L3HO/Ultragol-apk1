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
            "https://raw.githubusercontent.com/SERVIDOR-L3HO/Ultragol-apk1/main/version.json";

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
                int remoteVersionCode  = getInt(json, "versionCode", "version_code");
                int currentVersionCode = getCurrentVersionCode(context);
                String downloadUrl     = getString(json, "downloadUrl", "apk_url");

                // Nunca mostrar una actualización que no tenga un APK descargable.
                boolean needsUpdate = remoteVersionCode > currentVersionCode
                        && isDownloadAvailable(downloadUrl);
                if (needsUpdate) {
                    // Normaliza el formato para que SplashActivity pueda mostrarlo
                    // aunque el proveedor use snake_case.
                    json.put("versionCode", remoteVersionCode);
                    json.put("versionName", getString(json, "versionName", "version_name"));
                    json.put("downloadUrl", downloadUrl);
                    json.put("changelog", getString(json, "changelog", "release_notes"));
                    json.put("forceUpdate", getBoolean(json, "forceUpdate", "required"));
                }
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

    private static int getInt(JSONObject data, String camelCase, String snakeCase) {
        if (data.has(camelCase)) return data.optInt(camelCase, 0);
        return data.optInt(snakeCase, 0);
    }

    private static String getString(JSONObject data, String camelCase, String snakeCase) {
        String value = data.optString(camelCase, "").trim();
        return value.isEmpty() ? data.optString(snakeCase, "").trim() : value;
    }

    private static boolean getBoolean(JSONObject data, String camelCase, String snakeCase) {
        return data.has(camelCase)
                ? data.optBoolean(camelCase, false)
                : data.optBoolean(snakeCase, false);
    }

    private static boolean isDownloadAvailable(String downloadUrl) {
        if (downloadUrl.isEmpty()) return false;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(downloadUrl);
            if (!"https".equalsIgnoreCase(url.getProtocol())) return false;

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=0-0");
            conn.setInstanceFollowRedirects(true);

            int code = conn.getResponseCode();
            long length = conn.getContentLengthLong();
            return code >= 200 && code < 300 && length != 0;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public static void showUpdateDialog(Context context, JSONObject data) {
        if (data == null) return;

        String versionName  = getString(data, "versionName", "version_name");
        String changelog    = getString(data, "changelog", "release_notes");
        String downloadUrl  = getString(data, "downloadUrl", "apk_url");
        boolean forceUpdate = getBoolean(data, "forceUpdate", "required");

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
