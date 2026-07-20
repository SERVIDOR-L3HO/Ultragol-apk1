package com.ultragol.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UltragolApp extends Application {

    private static final String PREF = "crash_log";
    private static final String KEY  = "last_crash";

    /** Count of activities currently in the started state. */
    private int activeActivities = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        setupCrashHandler();
        setupCloudSync();
    }

    // ── Cloud sync lifecycle ──────────────────────────────────────────────────

    private void setupCloudSync() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStarted(Activity a) {
                activeActivities++;
            }

            @Override
            public void onActivityStopped(Activity a) {
                activeActivities--;
                if (activeActivities <= 0) {
                    // App is going to background — push to cloud
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        UserSyncManager.pushToCloud(getApplicationContext(), user.getUid());
                    }
                }
            }

            // Required empty overrides
            @Override public void onActivityCreated(Activity a, Bundle b)  {}
            @Override public void onActivityResumed(Activity a)            {}
            @Override public void onActivityPaused(Activity a)             {}
            @Override public void onActivitySaveInstanceState(Activity a, Bundle b) {}
            @Override public void onActivityDestroyed(Activity a)          {}
        });
    }

    // ── Crash handler ─────────────────────────────────────────────────────────

    private void setupCrashHandler() {
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
        return ctx.getSharedPreferences(PREF, MODE_PRIVATE).getString(KEY, null);
    }

    public static void clearCrash(android.content.Context ctx) {
        ctx.getSharedPreferences(PREF, MODE_PRIVATE).edit().remove(KEY).apply();
    }
}
