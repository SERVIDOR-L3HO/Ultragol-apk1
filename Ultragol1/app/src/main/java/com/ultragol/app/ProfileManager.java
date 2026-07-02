package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.*;
import java.util.*;

public class ProfileManager {

    private static final String PREFS_PROFILES = "ultragol_profiles";
    private static final String PREFS_SESSION  = "ultragol_session";
    private static final String KEY_LIST       = "profiles";
    private static final String KEY_CURRENT    = "current_profile_id";
    public  static final int    MAX_PROFILES   = 5;

    public static final String[] AVATAR_COLORS = {
        "#FF6B00", "#9B59B6", "#1ABC9C",
        "#E74C3C", "#3498DB", "#F1C40F",
        "#E67E22", "#2ECC71"
    };

    // ─────────────────────────────────────────────────────────────────────────

    public static class Profile {
        public String id;
        public String name;
        public String avatarColor;  // hex string
        public String pin;          // "" if no PIN
        public boolean isKids;
        public long createdAt;

        public Profile() {}

        public String getInitial() {
            return name != null && !name.isEmpty()
                ? String.valueOf(name.charAt(0)).toUpperCase(Locale.getDefault())
                : "?";
        }

        public boolean hasPin() { return pin != null && !pin.isEmpty(); }
    }

    // ─────────────────────────────────────────────────────────────────────────

    public static List<Profile> getAll(Context ctx) {
        List<Profile> list = new ArrayList<>();
        try {
            String json = prefs(ctx).getString(KEY_LIST, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                list.add(fromJson(arr.getJSONObject(i)));
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static void save(Context ctx, Profile p) {
        List<Profile> list = getAll(ctx);
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id.equals(p.id)) { list.set(i, p); found = true; break; }
        }
        if (!found && list.size() < MAX_PROFILES) list.add(p);
        writeAll(ctx, list);
    }

    public static void delete(Context ctx, String id) {
        List<Profile> list = getAll(ctx);
        list.removeIf(p -> p.id.equals(id));
        writeAll(ctx, list);
        if (id.equals(getCurrentId(ctx))) setCurrentId(ctx, "");
    }

    public static Profile newProfile(String name, String color) {
        Profile p = new Profile();
        p.id          = "prof_" + System.currentTimeMillis();
        p.name        = name;
        p.avatarColor = color;
        p.pin         = "";
        p.isKids      = false;
        p.createdAt   = System.currentTimeMillis();
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Current profile

    public static String getCurrentId(Context ctx) {
        return session(ctx).getString(KEY_CURRENT, "");
    }

    public static void setCurrentId(Context ctx, String id) {
        session(ctx).edit().putString(KEY_CURRENT, id).apply();
    }

    public static Profile getCurrentProfile(Context ctx) {
        String id = getCurrentId(ctx);
        for (Profile p : getAll(ctx)) if (p.id.equals(id)) return p;
        List<Profile> all = getAll(ctx);
        return all.isEmpty() ? null : all.get(0);
    }

    /** SharedPreferences key prefix for per-profile data storage */
    public static String dataKey(Context ctx, String base) {
        String id = getCurrentId(ctx);
        return id.isEmpty() ? base : base + "_" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_PROFILES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences session(Context ctx) {
        return ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
    }

    private static void writeAll(Context ctx, List<Profile> list) {
        try {
            JSONArray arr = new JSONArray();
            for (Profile p : list) arr.put(toJson(p));
            prefs(ctx).edit().putString(KEY_LIST, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    private static Profile fromJson(JSONObject o) throws JSONException {
        Profile p = new Profile();
        p.id          = o.optString("id", "");
        p.name        = o.optString("name", "");
        p.avatarColor = o.optString("color", "#FF6B00");
        p.pin         = o.optString("pin", "");
        p.isKids      = o.optBoolean("kids", false);
        p.createdAt   = o.optLong("ts", 0);
        return p;
    }

    private static JSONObject toJson(Profile p) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id",    p.id);
        o.put("name",  p.name);
        o.put("color", p.avatarColor);
        o.put("pin",   p.pin);
        o.put("kids",  p.isKids);
        o.put("ts",    p.createdAt);
        return o;
    }
}
