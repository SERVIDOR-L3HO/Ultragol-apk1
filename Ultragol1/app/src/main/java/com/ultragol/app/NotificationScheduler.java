package com.ultragol.app;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * NotificationScheduler — registra el worker periódico de notificaciones inteligentes.
 * Llama a schedule(ctx) desde MainActivity.onCreate().
 */
public class NotificationScheduler {

    private static final String TAG       = "NotifScheduler";
    private static final String WORK_TAG  = "ap_smart_notifications";
    // Intervalo: cada 4 horas (mínimo WorkManager es 15 min, usamos 4h para no molestar)
    private static final long   INTERVAL_HOURS = 4;

    public static void schedule(Context ctx) {
        try {
            // Crear canales primero
            SmartNotificationWorker.createChannels(ctx);

            Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                SmartNotificationWorker.class,
                INTERVAL_HOURS, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_TAG)
                .build();

            // KEEP: si ya existe, no reiniciar (mantener el ciclo actual)
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request);

            Log.d(TAG, "Worker de notificaciones programado cada " + INTERVAL_HOURS + "h");
        } catch (Exception e) {
            Log.e(TAG, "Error programando notificaciones", e);
        }
    }

    /** Forzar una notificación inmediata (por ejemplo, al abrir la app por primera vez). */
    public static void runNow(Context ctx) {
        try {
            SmartNotificationWorker.createChannels(ctx);

            Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

            androidx.work.OneTimeWorkRequest oneShot = new androidx.work.OneTimeWorkRequest.Builder(
                SmartNotificationWorker.class)
                .setConstraints(constraints)
                .addTag(WORK_TAG + "_instant")
                .build();

            WorkManager.getInstance(ctx).enqueue(oneShot);
            Log.d(TAG, "Notificación inmediata encolada");
        } catch (Exception e) {
            Log.e(TAG, "Error encolando notificación inmediata", e);
        }
    }
}
