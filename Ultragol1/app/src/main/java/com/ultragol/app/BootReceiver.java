package com.ultragol.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BootReceiver — reprograma las notificaciones automáticas después de reiniciar el dispositivo.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : "";
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action)) {
            Log.d(TAG, "Boot detectado — reprogramando notificaciones");
            NotificationScheduler.schedule(context);
        }
    }
}
