package com.ultragol.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

public class ContinueWatchingWidget extends AppWidgetProvider {

    /** Action broadcast when the user taps a carousel item. */
    public static final String ACTION_OPEN = "com.ultragol.app.WIDGET_OPEN";

    // ── Update entry-point ────────────────────────────────────────────────────

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            updateWidget(ctx, mgr, id);
        }
    }

    /** Call this from anywhere in the app to refresh all home-screen widgets. */
    public static void refresh(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, ContinueWatchingWidget.class));
        for (int id : ids) {
            updateWidget(ctx, mgr, id);
        }
    }

    // ── Build RemoteViews ─────────────────────────────────────────────────────

    private static void updateWidget(Context ctx, AppWidgetManager mgr, int widgetId) {
        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_continue_watching);

        // Adapter service — unique data URI so Android distinguishes widgets
        Intent svc = new Intent(ctx, ContinueWatchingWidgetService.class);
        svc.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        svc.setData(Uri.parse(svc.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.wStack, svc);
        rv.setEmptyView(R.id.wStack, R.id.wEmpty);

        // Template PendingIntent — merged with per-item fill-in intents
        Intent template = new Intent(ctx, ContinueWatchingWidget.class);
        template.setAction(ACTION_OPEN);
        template.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;   // Required for fill-in intents on API 31+
        }
        PendingIntent templatePI = PendingIntent.getBroadcast(ctx, widgetId, template, flags);
        rv.setPendingIntentTemplate(R.id.wStack, templatePI);

        mgr.updateAppWidget(widgetId, rv);
        mgr.notifyAppWidgetViewDataChanged(widgetId, R.id.wStack);
    }

    // ── Handle item taps ──────────────────────────────────────────────────────

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);

        if (!ACTION_OPEN.equals(intent.getAction())) return;

        String serverUrl = intent.getStringExtra("server_url");
        long   resumeMs  = intent.getLongExtra("resume_ms", 0);
        String title     = intent.getStringExtra("title");
        String posterUrl = intent.getStringExtra("poster_url");

        Intent target;
        if (serverUrl != null && !serverUrl.isEmpty()) {
            // Open PlayerActivity with the saved server and resume position
            target = new Intent(ctx, PlayerActivity.class);
            target.putExtra("url",       serverUrl);
            target.putExtra("title",     title != null ? title : "");
            target.putExtra("resume_ms", resumeMs);
        } else {
            // Fallback: open the app normally
            target = new Intent(ctx, SplashActivity.class);
        }
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(target);
    }
}
