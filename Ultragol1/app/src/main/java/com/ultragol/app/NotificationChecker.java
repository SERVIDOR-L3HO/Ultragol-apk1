package com.ultragol.app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class NotificationChecker {

    public static final String NOTIF_URL =
            "https://da8cd50c-b6a4-475c-8783-70b87f2e70be-00-3s6o17dujacbo.spock.replit.dev/notifications";
    private static final String CHANNEL_ID  = "ultragol_notifs";
    private static final String PREFS_NAME  = "ultragol_notif_prefs";
    private static final String SHOWN_KEY   = "shown_ids";

    public static void check(Context context) {
        createChannel(context);
        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(NOTIF_URL).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() != 200) return;

                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }

                JSONObject json   = new JSONObject(sb.toString());
                JSONArray  notifs = json.optJSONArray("notifications");
                if (notifs == null || notifs.length() == 0) return;

                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                Set<String> shown = new HashSet<>(prefs.getStringSet(SHOWN_KEY, new HashSet<>()));

                for (int i = 0; i < notifs.length(); i++) {
                    JSONObject n  = notifs.getJSONObject(i);
                    String     id = n.optString("id", "");
                    if (id.isEmpty() || shown.contains(id)) continue;

                    String title   = n.optString("title",   "Ultragol1");
                    String message = n.optString("message", "");
                    int    notifId = Math.abs(id.hashCode());

                    shown.add(id);
                    new Handler(Looper.getMainLooper()).post(
                            () -> showNotification(context, notifId, title, message));
                }

                prefs.edit().putStringSet(SHOWN_KEY, shown).apply();

            } catch (Exception ignored) {}
        }).start();
    }

    private static void showNotification(Context context, int id, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(id, builder.build());
    }

    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones Ultragol",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Avisos y actualizaciones de Ultragol1");
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}
