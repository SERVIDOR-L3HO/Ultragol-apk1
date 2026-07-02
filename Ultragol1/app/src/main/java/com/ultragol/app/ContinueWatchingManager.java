package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.ultragol.app.models.ContentItem;
import org.json.*;
import java.util.*;

public class ContinueWatchingManager {
    private static final String PREFS = "continue_watching";
    private static final String KEY   = "entries";
    private static final int    MAX   = 10;

    public static class Entry {
        public final ContentItem item;
        public final int season, episode;
        public final long timestamp;
        public Entry(ContentItem item, int season, int episode, long timestamp) {
            this.item = item; this.season = season;
            this.episode = episode; this.timestamp = timestamp;
        }
    }

    public static void save(Context ctx, ContentItem item, int season, int episode) {
        List<Entry> list = getAll(ctx);
        list.removeIf(e -> e.item.getTmdbId() == item.getTmdbId());
        list.add(0, new Entry(item, season, episode, System.currentTimeMillis()));
        if (list.size() > MAX) list = list.subList(0, MAX);
        writeList(ctx, list);
    }

    public static void remove(Context ctx, int tmdbId) {
        List<Entry> list = getAll(ctx);
        list.removeIf(e -> e.item.getTmdbId() == tmdbId);
        writeList(ctx, list);
    }

    public static List<Entry> getAll(Context ctx) {
        List<Entry> list = new ArrayList<>();
        try {
            String json = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                ContentItem it = new ContentItem(
                    o.optString("title"), o.optString("genre"), o.optString("year"),
                    o.optString("rating"), o.optString("posterUrl"), o.optString("overview"),
                    o.optInt("type", 0), false, false);
                it.setTmdbId(o.optInt("tmdbId", 0));
                it.setBackdropUrl(o.optString("backdropUrl", ""));
                it.setStreamUrl(o.optString("streamUrl", ""));
                list.add(new Entry(it, o.optInt("season", 1), o.optInt("episode", 1), o.optLong("ts", 0)));
            }
        } catch (Exception ignored) {}
        return list;
    }

    private static void writeList(Context ctx, List<Entry> list) {
        try {
            JSONArray arr = new JSONArray();
            for (Entry e : list) {
                JSONObject o = new JSONObject();
                o.put("title",      e.item.getTitle());
                o.put("genre",      e.item.getGenre());
                o.put("year",       e.item.getYear());
                o.put("rating",     e.item.getRating());
                o.put("posterUrl",  e.item.getPosterUrl());
                o.put("overview",   e.item.getOverview());
                o.put("backdropUrl",e.item.getBackdropUrl());
                o.put("streamUrl",  e.item.getStreamUrl());
                o.put("type",       e.item.getContentType());
                o.put("tmdbId",     e.item.getTmdbId());
                o.put("season",     e.season);
                o.put("episode",    e.episode);
                o.put("ts",         e.timestamp);
                arr.put(o);
            }
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
               .edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }
}
