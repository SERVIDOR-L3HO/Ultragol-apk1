package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Syncs all user data between local SharedPreferences and Firestore.
 *
 * Firestore structure:
 *   users/{uid}/{prefName}  →  { key: value, ... }
 *
 * Call pullFromCloud() after login to restore saved data.
 * Call pushToCloud()  when app goes to background to save current data.
 */
public class UserSyncManager {

    private static final String TAG = "UserSyncManager";

    // Global (not per-profile) SharedPreferences names
    private static final String[] GLOBAL_PREFS = {
        "ultragol_profiles",
        "ultragol_session",
        "mylist",
        "adult_prefs"
    };

    // Per-profile SharedPreferences prefixes (suffix: _{profileId})
    private static final String[] PROFILE_PREFIXES = {
        "favorites",
        "continue_watching",
        "watched_episodes"
    };

    public interface SyncCallback {
        void onComplete(boolean success);
    }

    // ── Pull: Firestore → SharedPreferences ──────────────────────────────────

    /**
     * Downloads all user data from Firestore and writes it to local SharedPreferences.
     * @param onDone Called on main thread when done (even on failure).
     */
    public static void pullFromCloud(Context ctx, String uid, Runnable onDone) {
        pullFromCloud(ctx, uid, success -> {
            if (onDone != null) onDone.run();
        });
    }

    /**
     * Reads the user document and the old nested collection. Reading both
     * keeps existing installations upgrade-safe while all new writes use the
     * rules-compatible user document.
     */
    @SuppressWarnings("unchecked")
    public static void pullFromCloud(Context ctx, String uid, SyncCallback onDone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<com.google.firebase.firestore.DocumentSnapshot> userTask =
            db.collection("users").document(uid).get();
        Task<com.google.firebase.firestore.QuerySnapshot> legacyTask =
            db.collection("users").document(uid).collection("prefs").get();

        Tasks.whenAllComplete(userTask, legacyTask).addOnCompleteListener(ignored -> {
            boolean success = false;
            try {
                if (userTask.isSuccessful() && userTask.getResult() != null) {
                    Map<String, Object> groups = userTask.getResult().getData();
                    if (groups != null) {
                        for (Map.Entry<String, Object> entry : groups.entrySet()) {
                            if (entry.getValue() instanceof Map) {
                                writeToPrefs(ctx, entry.getKey(),
                                    (Map<String, Object>) entry.getValue());
                                success = true;
                            }
                        }
                    }
                } else if (userTask.getException() != null) {
                    Log.w(TAG, "User document pull failed", userTask.getException());
                }

                if (legacyTask.isSuccessful() && legacyTask.getResult() != null) {
                    for (com.google.firebase.firestore.DocumentSnapshot doc
                            : legacyTask.getResult().getDocuments()) {
                        Map<String, Object> data = doc.getData();
                        if (data == null) continue;
                        writeToPrefs(ctx, doc.getId(), data);
                        success = true;
                    }
                    Log.d(TAG, "Legacy pull complete: "
                        + legacyTask.getResult().size() + " pref groups");
                } else if (legacyTask.getException() != null) {
                    Log.d(TAG, "Legacy preference pull unavailable",
                        legacyTask.getException());
                }
            } catch (Exception e) {
                Log.w(TAG, "Pull parse error", e);
                success = false;
            }
            Log.d(TAG, "Pull complete: success=" + success);
            if (onDone != null) onDone.onComplete(success);
        });
    }

    // ── Push: SharedPreferences → Firestore ──────────────────────────────────

    /**
     * Uploads all local SharedPreferences data to Firestore.
     * The overload with a callback reports the final Firestore result.
     */
    public static void pushToCloud(Context ctx, String uid) {
        pushToCloud(ctx, uid, null);
    }

    /**
     * Uploads all local preferences into users/{uid}. The callback is invoked
     * after Firestore confirms or rejects the write.
     */
    public static void pushToCloud(Context ctx, String uid, SyncCallback onDone) {
        Map<String, Map<String, Object>> allPrefs = collectAllPrefs(ctx);
        if (allPrefs.isEmpty()) {
            if (onDone != null) onDone.onComplete(true);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> cloudData = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : allPrefs.entrySet()) {
            cloudData.put(entry.getKey(), entry.getValue());
        }

        db.collection("users").document(uid)
            .set(cloudData, SetOptions.merge())
            .addOnSuccessListener(unused -> {
                Log.d(TAG, "Push complete: " + allPrefs.size() + " pref groups");
                if (onDone != null) onDone.onComplete(true);
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "Push failed", e);
                if (onDone != null) onDone.onComplete(false);
            });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Map<String, Map<String, Object>> collectAllPrefs(Context ctx) {
        Map<String, Map<String, Object>> result = new HashMap<>();

        // Global prefs
        for (String name : GLOBAL_PREFS) {
            Map<String, ?> all = ctx.getSharedPreferences(name, Context.MODE_PRIVATE).getAll();
            if (!all.isEmpty()) result.put(name, new HashMap<>(all));
        }

        // Per-profile prefs — enumerate profile IDs from ultragol_profiles
        try {
            String json = ctx.getSharedPreferences("ultragol_profiles", Context.MODE_PRIVATE)
                .getString("profiles", "[]");
            JSONArray profiles = new JSONArray(json);
            for (int i = 0; i < profiles.length(); i++) {
                String profId = profiles.getJSONObject(i).optString("id", "");
                if (profId.isEmpty()) continue;
                for (String prefix : PROFILE_PREFIXES) {
                    String name = prefix + "_" + profId;
                    Map<String, ?> all = ctx.getSharedPreferences(name, Context.MODE_PRIVATE).getAll();
                    if (!all.isEmpty()) result.put(name, new HashMap<>(all));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Profile enum error", e);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static void writeToPrefs(Context ctx, String prefName, Map<String, Object> data) {
        SharedPreferences.Editor ed =
            ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        for (Map.Entry<String, Object> kv : data.entrySet()) {
            String k = kv.getKey();
            Object v = kv.getValue();
            if      (v instanceof String)  ed.putString(k, (String) v);
            else if (v instanceof Boolean) ed.putBoolean(k, (Boolean) v);
            else if (v instanceof Long)    ed.putLong(k, (Long) v);
            else if (v instanceof Integer) ed.putInt(k, (Integer) v);
            else if (v instanceof Double)  ed.putFloat(k, ((Double) v).floatValue());
            else if (v instanceof List) {
                java.util.Set<String> values = new java.util.HashSet<>();
                for (Object item : (List<?>) v) {
                    if (item != null) values.add(String.valueOf(item));
                }
                ed.putStringSet(k, values);
            }
        }
        ed.apply();
    }
}
