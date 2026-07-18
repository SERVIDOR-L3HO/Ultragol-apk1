package com.ultragol.app;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ultragol.app.models.ContentItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ContinueWatchingWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ContinueWatchingFactory(getApplicationContext(), intent);
    }

    static class ContinueWatchingFactory implements RemoteViewsFactory {
        private final Context ctx;
        private List<ContinueWatchingManager.Entry> entries;

        ContinueWatchingFactory(Context ctx, Intent intent) {
            this.ctx = ctx;
        }

        @Override public void onCreate()          { loadData(); }
        @Override public void onDataSetChanged()  { loadData(); }
        @Override public void onDestroy()         {}

        private void loadData() {
            entries = ContinueWatchingManager.getAll(ctx);
        }

        @Override public int getCount() {
            return entries != null ? entries.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (entries == null || position >= entries.size()) return null;

            ContinueWatchingManager.Entry e = entries.get(position);
            RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_continue_item);

            // Poster image
            Bitmap poster = loadBitmap(e.item.getPosterUrl());
            if (poster != null) {
                rv.setImageViewBitmap(R.id.wItemPoster, poster);
            } else {
                // Fallback: try backdrop
                Bitmap backdrop = loadBitmap(e.item.getBackdropUrl());
                if (backdrop != null) rv.setImageViewBitmap(R.id.wItemPoster, backdrop);
            }

            // Title
            rv.setTextViewText(R.id.wItemTitle, e.item.getTitle());

            // Episode / movie badge
            String epLabel = e.item.getContentType() == ContentItem.TYPE_MOVIE
                    ? "PELÍCULA"
                    : "T" + e.season + " · E" + e.episode;
            rv.setTextViewText(R.id.wItemEp, epLabel);

            // Progress
            int pct = Math.max(1, e.progressPercent);
            rv.setProgressBar(R.id.wItemProgressBar, 100, pct, false);
            rv.setTextViewText(R.id.wItemProgressText, pct + "%");

            // Per-item fill-in intent (merged with the template in ContinueWatchingWidget)
            Intent fillIn = new Intent();
            fillIn.putExtra("server_url",  e.serverUrl);
            fillIn.putExtra("resume_ms",   e.progressMs);
            fillIn.putExtra("title",       e.item.getTitle());
            fillIn.putExtra("poster_url",  e.item.getPosterUrl());
            rv.setOnClickFillInIntent(R.id.wItemRoot, fillIn);

            return rv;
        }

        @Override public RemoteViews getLoadingView()  { return null; }
        @Override public int getViewTypeCount()        { return 1; }
        @Override public long getItemId(int pos)       { return pos; }
        @Override public boolean hasStableIds()        { return false; }

        /** Loads and downsamples a bitmap from a URL. Returns null on failure. */
        private Bitmap loadBitmap(String urlStr) {
            if (urlStr == null || urlStr.isEmpty()) return null;
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2; // Downsample 2× to stay well under IPC limit
                Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
                is.close();
                conn.disconnect();
                return bm;
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
