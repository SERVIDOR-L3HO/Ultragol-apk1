package com.ultragol.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.List;

public class ContinueWatchingWidget extends AppWidgetProvider {

    public static void refresh(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, ContinueWatchingWidget.class));
        if (ids.length > 0) {
            new ContinueWatchingWidget().onUpdate(ctx, mgr, ids);
        }
    }

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        List<ContinueWatchingManager.Entry> entries = ContinueWatchingManager.getAll(ctx);
        for (int id : ids) {
            RemoteViews rv = buildViews(ctx, entries);
            mgr.updateAppWidget(id, rv);
        }
    }

    private RemoteViews buildViews(Context ctx, List<ContinueWatchingManager.Entry> entries) {
        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_continue_watching);

        // Click on widget → open app
        Intent openApp = new Intent(ctx, SplashActivity.class);
        openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, openApp,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        boolean hasItems = !entries.isEmpty();
        rv.setViewVisibility(R.id.wEmpty, hasItems ? android.view.View.GONE : android.view.View.VISIBLE);

        int[][] slots = {
            {R.id.wSlot1, R.id.wTitle1, R.id.wEp1, R.id.wThumb1},
            {R.id.wSlot2, R.id.wTitle2, R.id.wEp2, R.id.wThumb2},
            {R.id.wSlot3, R.id.wTitle3, R.id.wEp3, R.id.wThumb3},
        };

        for (int i = 0; i < slots.length; i++) {
            int slotId  = slots[i][0];
            int titleId = slots[i][1];
            int epId    = slots[i][2];

            if (i < entries.size()) {
                ContinueWatchingManager.Entry e = entries.get(i);
                rv.setViewVisibility(slotId, android.view.View.VISIBLE);
                rv.setTextViewText(titleId, e.item.getTitle());
                String epLabel = e.item.getContentType() == com.ultragol.app.models.ContentItem.TYPE_MOVIE
                    ? "PELÍCULA"
                    : "T" + e.season + " · E" + e.episode;
                rv.setTextViewText(epId, epLabel);
                rv.setOnClickPendingIntent(slotId, pi);
            } else {
                rv.setViewVisibility(slotId, android.view.View.GONE);
            }
        }

        rv.setOnClickPendingIntent(R.id.wEmpty, pi);
        return rv;
    }
}
