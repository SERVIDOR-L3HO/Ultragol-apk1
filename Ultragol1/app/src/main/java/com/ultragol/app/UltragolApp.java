package com.ultragol.app;

import android.app.Application;
import android.content.SharedPreferences;

public class UltragolApp extends Application {

    private static final String PREF = "crash_log";
    private static final String KEY  = "last_crash";

    @Override
    public void onCreate() {
        super.onCreate();

        final Thread.UncaughtExceptionHandler defaultHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(throwable.toString()).append("\n");
                for (StackTraceElement e : throwable.getStackTrace()) {
                    sb.append("  at ").append(e.toString()).append("\n");
                    if (sb.length() > 3000) break;
                }
                Throwable cause = throwable.getCause();
                if (cause != null) {
                    sb.append("Caused by: ").append(cause.toString()).append("\n");
                    for (StackTraceElement e : cause.getStackTrace()) {
                        sb.append("  at ").append(e.toString()).append("\n");
                        if (sb.length() > 4000) break;
                    }
                }
                getSharedPreferences(PREF, MODE_PRIVATE)
                        .edit().putString(KEY, sb.toString()).commit();
            } catch (Exception ignored) {}

            if (defaultHandler != null) defaultHandler.uncaughtException(thread, throwable);
        });
    }

    public static String getLastCrash(android.content.Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, MODE_PRIVATE);
        return sp.getString(KEY, null);
    }

    public static void clearCrash(android.content.Context ctx) {
        ctx.getSharedPreferences(PREF, MODE_PRIVATE).edit().remove(KEY).apply();
    }
}
