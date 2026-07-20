package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the Adult section lock state and PIN.
 * - Enabled/disabled toggled from Settings with PIN verification.
 * - On entry: always requires PIN if set.
 */
public class AdultManager {

    private static final String PREFS   = "adult_prefs";
    private static final String KEY_EN  = "adult_enabled";
    private static final String KEY_PIN = "adult_pin";

    public static boolean isEnabled(Context ctx) {
        return prefs(ctx).getBoolean(KEY_EN, false);
    }

    public static void setEnabled(Context ctx, boolean enabled) {
        prefs(ctx).edit().putBoolean(KEY_EN, enabled).apply();
    }

    public static String getPin(Context ctx) {
        return prefs(ctx).getString(KEY_PIN, "");
    }

    public static boolean hasPin(Context ctx) {
        String p = getPin(ctx);
        return p != null && !p.isEmpty();
    }

    public static void setPin(Context ctx, String pin) {
        prefs(ctx).edit().putString(KEY_PIN, pin).apply();
    }

    public static boolean checkPin(Context ctx, String entered) {
        return getPin(ctx).equals(entered);
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
