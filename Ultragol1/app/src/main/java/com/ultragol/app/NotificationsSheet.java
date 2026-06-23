package com.ultragol.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import android.content.SharedPreferences;
import android.content.Context;

public class NotificationsSheet extends BottomSheetDialogFragment {

    private LinearLayout notifList;
    private ProgressBar loading;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.sheet_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);

        notifList  = view.findViewById(R.id.notifList);
        loading    = view.findViewById(R.id.notifLoading);
        emptyState = view.findViewById(R.id.notifEmpty);

        TextView clearBtn = view.findViewById(R.id.btnClearAll);
        if (clearBtn != null) {
            clearBtn.setOnClickListener(v -> {
                SharedPreferences prefs = requireContext().getSharedPreferences(
                        "ultragol_notif_prefs", Context.MODE_PRIVATE);
                prefs.edit().remove("shown_ids").apply();
                notifList.removeAllViews();
                notifList.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            });
        }

        fetchNotifications();
    }

    private void fetchNotifications() {
        new Thread(() -> {
            try {
                String urlStr = NotificationChecker.NOTIF_URL;
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() != 200) {
                    showEmpty();
                    return;
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }

                JSONObject json   = new JSONObject(sb.toString());
                JSONArray  notifs = json.optJSONArray("notifications");

                if (notifs == null || notifs.length() == 0) {
                    showEmpty();
                    return;
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    loading.setVisibility(View.GONE);
                    notifList.setVisibility(View.VISIBLE);

                    for (int i = 0; i < notifs.length(); i++) {
                        try {
                            JSONObject n = notifs.getJSONObject(i);
                            String title   = n.optString("title",   "Notificación");
                            String message = n.optString("message", "");
                            addCard(title, message);
                        } catch (Exception ignored) {}
                    }
                });

            } catch (Exception e) {
                showEmpty();
            }
        }).start();
    }

    private void addCard(String title, String message) {
        if (getContext() == null) return;
        View card = LayoutInflater.from(getContext())
                .inflate(R.layout.item_notification_card, notifList, false);

        TextView tvTitle   = card.findViewById(R.id.notifTitle);
        TextView tvMessage = card.findViewById(R.id.notifMessage);

        if (tvTitle   != null) tvTitle.setText(title);
        if (tvMessage != null) tvMessage.setText(message);

        notifList.addView(card);

        View divider = new View(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMarginStart(20);
        lp.setMarginEnd(20);
        divider.setLayoutParams(lp);
        divider.setBackgroundColor(0x11FFFFFF);
        notifList.addView(divider);
    }

    private void showEmpty() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (loading    != null) loading.setVisibility(View.GONE);
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
        });
    }
}
