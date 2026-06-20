package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.ultragol.app.models.ContentItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MyListManager {
    private static final String PREFS = "mylist";
    private static final String KEY   = "items";

    public static void toggle(Context ctx, ContentItem item) {
        if (isInList(ctx, item)) remove(ctx, item); else add(ctx, item);
    }

    public static boolean isInList(Context ctx, ContentItem item) {
        for (ContentItem c : getAll(ctx)) if (c.getTmdbId() == item.getTmdbId()) return true;
        return false;
    }

    public static void add(Context ctx, ContentItem item) {
        List<ContentItem> list = getAll(ctx);
        for (ContentItem c : list) if (c.getTmdbId() == item.getTmdbId()) return;
        list.add(0, item);
        save(ctx, list);
    }

    public static void remove(Context ctx, ContentItem item) {
        List<ContentItem> list = getAll(ctx);
        list.removeIf(c -> c.getTmdbId() == item.getTmdbId());
        save(ctx, list);
    }

    public static List<ContentItem> getAll(Context ctx) {
        List<ContentItem> list = new ArrayList<>();
        try {
            String json = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                ContentItem item = new ContentItem(
                    o.optString("title"), o.optString("genre"), o.optString("year"),
                    o.optString("rating"), o.optString("posterUrl"), o.optString("overview"),
                    o.optInt("type", 0), false, false);
                item.setTmdbId(o.optInt("tmdbId", 0));
                item.setBackdropUrl(o.optString("backdropUrl", ""));
                list.add(item);
            }
        } catch (Exception ignored) {}
        return list;
    }

    private static void save(Context ctx, List<ContentItem> list) {
        try {
            JSONArray arr = new JSONArray();
            for (ContentItem item : list) {
                JSONObject o = new JSONObject();
                o.put("title", item.getTitle()); o.put("genre", item.getGenre());
                o.put("year", item.getYear()); o.put("rating", item.getRating());
                o.put("posterUrl", item.getPosterUrl()); o.put("overview", item.getOverview());
                o.put("type", item.getContentType()); o.put("tmdbId", item.getTmdbId());
                o.put("backdropUrl", item.getBackdropUrl());
                arr.put(o);
            }
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) {}
    }
}
