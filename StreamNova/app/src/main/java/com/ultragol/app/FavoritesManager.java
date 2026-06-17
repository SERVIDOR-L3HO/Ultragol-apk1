package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesManager {

    private static final String PREFS = "ultragol_favs";
    private static final String KEY   = "fav_titles";
    private static FavoritesManager instance;
    private final SharedPreferences prefs;

    private FavoritesManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static FavoritesManager get(Context ctx) {
        if (instance == null) instance = new FavoritesManager(ctx);
        return instance;
    }

    public boolean isFavorite(String title) {
        return getRaw().contains(title);
    }

    public boolean toggle(String title) {
        Set<String> set = new HashSet<>(getRaw());
        boolean added;
        if (set.contains(title)) { set.remove(title); added = false; }
        else                     { set.add(title);    added = true;  }
        prefs.edit().putStringSet(KEY, set).apply();
        return added;
    }

    public List<String> getFavoriteTitles() {
        return new ArrayList<>(getRaw());
    }

    public Set<String> getFavorites() { return getRaw(); }

    public int count() { return getRaw().size(); }

    private Set<String> getRaw() {
        return prefs.getStringSet(KEY, new HashSet<>());
    }
}
