package com.ultragol.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;

/**
 * Syncs all user data between local SharedPreferences and Firestore.
 *
 * Firestore structure:
 *   users/{uid}/prefs/{prefName}  →  { key: value, ... }
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

    // ── Pull: Firestore → SharedPreferences ──────────────────────────────────

    /**
     * Downloads all user data from Firestore and writes it to local SharedPreferences.
     * @param onDone Called on main thread when done (even on failure).
     */
    public static void pullFromCloud(Context ctx, String uid, Runnable onDone) {
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("prefs")
            .get()
            .addOnSuccessListener(snapshot -> {
                try {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshot.getDocuments()) {
                        String prefName = doc.getId();
                        Map<String, Object> data = doc.getData();
                        if (data == null) continue;
                        writeToPrefs(ctx, prefName, data);
                    }
                    Log.d(TAG, "Pull complete: " + snapshot.size() + " pref groups");
                } catch (Exception e) {
                    Log.w(TAG, "Pull parse error", e);
                }
                if (onDone != null) onDone.run();
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "Pull failed", e);
                if (onDone != null) onDone.run();
            });
    }

    // ── Push: SharedPreferences → Firestore ──────────────────────────────────

    /**
     * Uploads all local SharedPreferences data to Firestore.
     * Fire-and-forget — does not block.
     */
    public static void pushToCloud(Context ctx, String uid) {
        Map<String, Map<String, Object>> allPrefs = collectAllPrefs(ctx);
        if (allPrefs.isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (Map.Entry<String, Map<String, Object>> entry : allPrefs.entrySet()) {
            String prefName = entry.getKey();
            Map<String, Object> data = entry.getValue();
            db.collection("users").document(uid)
                .collection("prefs").document(prefName)
                .set(data, SetOptions.merge())
                .addOnFailureListener(e -> Log.w(TAG, "Push failed for " + prefName, e));
        }
        Log.d(TAG, "Push triggered: " + allPrefs.size() + " pref groups");
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
        }
        ed.apply();
    }
}
