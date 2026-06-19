package com.ultragol.app;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    public interface Callback {
        void onUpdateAvailable(int newVersionCode, String newVersionName,
                               String changelog, String downloadUrl, boolean forceUpdate);
        void onNoUpdate();
        void onError();
    }

    public static final String VERSION_URL = "https://f955201c-2f31-4b8f-8504-aa1c68c1ecb0-00-bqclkmgvxr7s.worf.replit.dev/version";

    public static void check(int currentVersionCode, Callback callback) {
        Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                URL url = new URL(VERSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);

                int code = conn.getResponseCode();
                if (code != 200) {
                    main.post(callback::onError);
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                int serverCode = json.getInt("versionCode");

                if (serverCode > currentVersionCode) {
                    String versionName = json.optString("versionName", "Nueva versión");
                    String changelog   = json.optString("changelog", "");
                    String downloadUrl = json.optString("downloadUrl", "");
                    boolean force      = json.optBoolean("forceUpdate", false);
                    main.post(() -> callback.onUpdateAvailable(
                            serverCode, versionName, changelog, downloadUrl, force));
                } else {
                    main.post(callback::onNoUpdate);
                }
            } catch (Exception e) {
                main.post(callback::onError);
            }
        }).start();
    }
}
