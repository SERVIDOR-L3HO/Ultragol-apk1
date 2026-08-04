package com.ultragol.app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages a Watch Party room via Firebase Realtime Database.
 *
 * DB structure:
 *   watchparty/rooms/{CODE}/
 *     contentId    : String
 *     passwordHash : String   (SHA-256 hex, or "" for open rooms)
 *     hostUid      : String
 *     createdAt    : long
 *     participants/{uid}/
 *       name       : String
 *       joinedAt   : long
 *     sync/
 *       action     : "play" | "pause" | "seek"
 *       positionMs : long
 *       updatedAt  : long
 *     chat/{pushId}/
 *       sender     : String  (null → system message)
 *       text       : String
 *       isSystem   : boolean
 *       timestamp  : long
 */
public class WatchPartyManager {

    private static final String TAG  = "WatchPartyManager";
    private static final String ROOT = "watchparty/rooms";

    // ── Listener interface ────────────────────────────────────────────────────
    public interface Listener {
        void onJoined(String roomCode, boolean isHost, int participantCount);
        void onParticipantsChanged(int count, String[] names);
        /** sender == null → system/event message */
        void onChatMessage(String sender, String text);
        /** action = "play" | "pause" | "seek",  positionMs = milliseconds */
        void onSync(String action, long positionMs);
        void onError(String reason);
        void onDisconnected();
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private Listener listener;
    private final String myUid = UUID.randomUUID().toString();
    private String currentCode;
    private boolean isHost;

    // Firebase refs and listeners (kept so we can remove them on disconnect)
    private DatabaseReference roomRef;
    private ValueEventListener  participantsListener;
    private ChildEventListener  chatListener;
    private ValueEventListener  syncListener;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void setListener(Listener l) { this.listener = l; }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Create a new room. Generates a unique 6-char code and writes it to Firebase.
     * @param contentId  Identifier for the content (e.g. TMDB id).
     * @param password   Optional password. Pass "" for open room.
     * @param name       Host's display name.
     */
    public void createRoom(String contentId, String password, String name) {
        isHost = true;
        String code = generateCode();
        DatabaseReference ref = db().child(ROOT).child(code);

        // Check code is not taken (very unlikely but safe)
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (snap.exists()) {
                    // Code collision — try again
                    createRoom(contentId, password, name);
                    return;
                }
                String hash = hashPassword(password);
                Map<String, Object> room = new HashMap<>();
                room.put("contentId",    contentId);
                room.put("passwordHash", hash);
                room.put("hostUid",      myUid);
                room.put("createdAt",    System.currentTimeMillis());
                ref.setValue(room, (err, r) -> {
                    if (err != null) {
                        notify(() -> { if (listener != null) listener.onError("Error al crear la sala: " + err.getMessage()); });
                        return;
                    }
                    currentCode = code;
                    roomRef     = ref;
                    joinAsParticipant(ref, code, name, true);
                });
            }
            @Override public void onCancelled(DatabaseError e) {
                notify(() -> { if (listener != null) listener.onError(e.getMessage()); });
            }
        });
    }

    /**
     * Join an existing room by its code.
     * @param code      6-char room code (case-insensitive).
     * @param password  Room password, or "" for open rooms.
     * @param name      Guest's display name.
     */
    public void joinRoom(String code, String password, String name) {
        isHost = false;
        String upper = code.toUpperCase().trim();
        DatabaseReference ref = db().child(ROOT).child(upper);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) {
                    notify(() -> { if (listener != null) listener.onError("Sala no encontrada. Verifica el código."); });
                    return;
                }
                // Check password
                String storedHash = snap.child("passwordHash").getValue(String.class);
                if (storedHash != null && !storedHash.isEmpty()) {
                    if (!storedHash.equals(hashPassword(password))) {
                        notify(() -> { if (listener != null) listener.onError("Contraseña incorrecta."); });
                        return;
                    }
                }
                // Check capacity
                long count = snap.child("participants").getChildrenCount();
                if (count >= 20) {
                    notify(() -> { if (listener != null) listener.onError("La sala está llena (máx 20 personas)."); });
                    return;
                }
                currentCode = upper;
                roomRef     = ref;
                joinAsParticipant(ref, upper, name, false);
            }
            @Override public void onCancelled(DatabaseError e) {
                notify(() -> { if (listener != null) listener.onError(e.getMessage()); });
            }
        });
    }

    /** Host: broadcast play to all participants. */
    public void syncPlay(long positionMs) {
        writeSync("play", positionMs);
    }

    /** Host: broadcast pause to all participants. */
    public void syncPause(long positionMs) {
        writeSync("pause", positionMs);
    }

    /** Send a chat message to the room. */
    public void sendChat(String text) {
        if (roomRef == null) return;
        Map<String, Object> msg = new HashMap<>();
        msg.put("sender",    myDisplayName);
        msg.put("text",      text);
        msg.put("isSystem",  false);
        msg.put("timestamp", System.currentTimeMillis());
        roomRef.child("chat").push().setValue(msg);
    }

    /** Leave the room and clean up all listeners. */
    public void disconnect() {
        if (roomRef == null) return;
        // Remove ourselves from participants
        roomRef.child("participants").child(myUid).removeValue();
        // Post a system message
        pushSystem(roomRef, myDisplayName + " salió de la sala.");
        // Remove listeners
        if (participantsListener != null) roomRef.child("participants").removeEventListener(participantsListener);
        if (syncListener        != null) roomRef.child("sync").removeEventListener(syncListener);
        if (chatListener        != null) roomRef.child("chat").removeEventListener(chatListener);
        roomRef = null;
    }

    public String getCurrentRoomCode() { return currentCode; }
    public boolean isHost()            { return isHost; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String myDisplayName = "Usuario";

    private void joinAsParticipant(DatabaseReference ref, String code, String name, boolean host) {
        myDisplayName = name;
        DatabaseReference mySlot = ref.child("participants").child(myUid);

        // Write our entry
        Map<String, Object> me = new HashMap<>();
        me.put("name",     name);
        me.put("joinedAt", System.currentTimeMillis());
        mySlot.setValue(me, (err, r) -> {
            if (err != null) {
                notify(() -> { if (listener != null) listener.onError("No se pudo unir: " + err.getMessage()); });
                return;
            }
            // Auto-remove on disconnect
            mySlot.onDisconnect().removeValue();

            // Post system message
            if (!host) pushSystem(ref, "👋 " + name + " se unió a la sala.");

            // Start listening
            attachParticipantsListener(ref);
            attachChatListener(ref);
            attachSyncListener(ref);

            // Notify joined
            ref.child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot snap) {
                    int cnt = (int) snap.getChildrenCount();
                    notify(() -> { if (listener != null) listener.onJoined(code, host, cnt); });
                }
                @Override public void onCancelled(DatabaseError e) {}
            });
        });
    }

    private void attachParticipantsListener(DatabaseReference ref) {
        participantsListener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                int count = (int) snap.getChildrenCount();
                String[] names = new String[count];
                int i = 0;
                for (DataSnapshot child : snap.getChildren()) {
                    String n = child.child("name").getValue(String.class);
                    names[i++] = n != null ? n : "?";
                }
                notify(() -> { if (listener != null) listener.onParticipantsChanged(count, names); });
            }
            @Override public void onCancelled(DatabaseError e) {}
        };
        ref.child("participants").addValueEventListener(participantsListener);
    }

    private boolean firstSyncSkip = true; // skip the initial value on attach

    private void attachSyncListener(DatabaseReference ref) {
        syncListener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (firstSyncSkip) { firstSyncSkip = false; return; } // don't fire on initial attach
                if (!snap.exists()) return;
                String action = snap.child("action").getValue(String.class);
                Long   pos    = snap.child("positionMs").getValue(Long.class);
                if (action == null) return;
                long posMs = pos != null ? pos : 0L;
                notify(() -> { if (listener != null) listener.onSync(action, posMs); });
            }
            @Override public void onCancelled(DatabaseError e) {}
        };
        ref.child("sync").addValueEventListener(syncListener);
    }

    private boolean chatInitialized = false;

    private void attachChatListener(DatabaseReference ref) {
        // Only listen to new messages from now on
        long now = System.currentTimeMillis();
        chatListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot snap, String prev) {
                Long ts = snap.child("timestamp").getValue(Long.class);
                if (ts != null && ts < now) return; // skip old messages
                Boolean sys    = snap.child("isSystem").getValue(Boolean.class);
                String  sender = snap.child("sender").getValue(String.class);
                String  text   = snap.child("text").getValue(String.class);
                if (text == null) return;
                boolean isSystem = sys != null && sys;
                notify(() -> { if (listener != null) listener.onChatMessage(isSystem ? null : sender, text); });
            }
            @Override public void onChildChanged(DataSnapshot s, String p) {}
            @Override public void onChildRemoved(DataSnapshot s) {}
            @Override public void onChildMoved(DataSnapshot s, String p) {}
            @Override public void onCancelled(DatabaseError e) {}
        };
        ref.child("chat").addChildEventListener(chatListener);
    }

    private void writeSync(String action, long positionMs) {
        if (roomRef == null || !isHost) return;
        Map<String, Object> sync = new HashMap<>();
        sync.put("action",     action);
        sync.put("positionMs", positionMs);
        sync.put("updatedAt",  System.currentTimeMillis());
        roomRef.child("sync").setValue(sync);
    }

    private void pushSystem(DatabaseReference ref, String text) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("sender",    null);
        msg.put("text",      text);
        msg.put("isSystem",  true);
        msg.put("timestamp", System.currentTimeMillis());
        ref.child("chat").push().setValue(msg);
    }

    private static FirebaseDatabase db() {
        return FirebaseDatabase.getInstance();
    }

    private static String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) sb.append(chars.charAt((int)(Math.random() * chars.length())));
        return sb.toString();
    }

    private static String hashPassword(String password) {
        if (password == null || password.isEmpty()) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return password; // fallback (shouldn't happen)
        }
    }

    private void notify(Runnable r) {
        mainHandler.post(r);
    }
}
