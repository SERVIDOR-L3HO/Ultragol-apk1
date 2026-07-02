package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;

public class WatchedManager {
    private static final String PREFS = "watched_episodes";

    private static String key(int seriesId, int season, int episode) {
        return seriesId + "_s" + season + "_e" + episode;
    }

    public static void markWatched(Context ctx, int seriesId, int season, int episode) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(key(seriesId, season, episode), true).apply();
    }

    public static void unmarkWatched(Context ctx, int seriesId, int season, int episode) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove(key(seriesId, season, episode)).apply();
    }

    public static void toggle(Context ctx, int seriesId, int season, int episode) {
        if (isWatched(ctx, seriesId, season, episode)) {
            unmarkWatched(ctx, seriesId, season, episode);
        } else {
            markWatched(ctx, seriesId, season, episode);
        }
    }

    public static boolean isWatched(Context ctx, int seriesId, int season, int episode) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(key(seriesId, season, episode), false);
    }
}
