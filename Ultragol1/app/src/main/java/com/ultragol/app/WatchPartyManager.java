package com.ultragol.app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Manages the WebSocket connection to the Watch Party server.
 * Handles room creation, joining, chat, and playback sync messages.
 */
public class WatchPartyManager {

    private static final String TAG = "WatchPartyManager";

    public interface Listener {
        /** Called when successfully joined/created a room. roomCode is the 6-char code. */
        void onJoined(String roomCode, boolean isHost, int participantCount);
        /** Called when participants list changes. */
        void onParticipantsChanged(int count, String[] names);
        /** Called when a chat message arrives. sender==null means it's a system event. */
        void onChatMessage(String sender, String text);
        /** Called when the host sends a sync command: action="play"|"pause"|"seek", position=ms */
        void onSync(String action, long positionMs);
        /** Called on any error (connection failed, wrong password, etc.) */
        void onError(String reason);
        /** Called when disconnected. */
        void onDisconnected();
    }

    private final OkHttpClient client;
    private WebSocket webSocket;
    private Listener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String myName;
    private String currentRoomCode;
    private boolean isHost;

    public WatchPartyManager() {
        client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // no timeout for WebSocket
            .build();
    }

    public void setListener(Listener l) {
        this.listener = l;
    }

    /**
     * Create a new room. The server will return a room code.
     * @param serverBaseUrl  The Replit server base URL (https://...).
     * @param contentId      An identifier for the content being watched.
     * @param password       Optional password, empty string for open room.
     * @param name           Display name for the host.
     */
    public void createRoom(String serverBaseUrl, String contentId, String password, String name) {
        this.myName = name;
        this.isHost = true;
        String wsUrl = serverBaseUrl.replace("https://", "wss://")
                                    .replace("http://",  "ws://")
                       + "/watchparty/ws";
        connect(wsUrl, () -> {
            try {
                JSONObject msg = new JSONObject();
                msg.put("action", "create_room");
                msg.put("contentId", contentId);
                msg.put("password", password);
                msg.put("name", name);
                send(msg.toString());
            } catch (JSONException e) {
                Log.e(TAG, "JSON error", e);
            }
        });
    }

    /**
     * Join an existing room by code.
     * @param serverBaseUrl  The Replit server base URL (https://...).
     * @param code           6-char room code.
     * @param password       Room password, or empty string.
     * @param name           Display name for this participant.
     */
    public void joinRoom(String serverBaseUrl, String code, String password, String name) {
        this.myName = name;
        this.isHost = false;
        String wsUrl = serverBaseUrl.replace("https://", "wss://")
                                    .replace("http://",  "ws://")
                       + "/watchparty/ws";
        connect(wsUrl, () -> {
            try {
                JSONObject msg = new JSONObject();
                msg.put("action", "join_room");
                msg.put("code", code.toUpperCase().trim());
                msg.put("password", password);
                msg.put("name", name);
                send(msg.toString());
            } catch (JSONException e) {
                Log.e(TAG, "JSON error", e);
            }
        });
    }

    /** Host sends a play sync command to all participants. */
    public void syncPlay(long positionMs) {
        sendSync("play", positionMs);
    }

    /** Host sends a pause sync command to all participants. */
    public void syncPause(long positionMs) {
        sendSync("pause", positionMs);
    }

    /** Send a chat message. */
    public void sendChat(String text) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("action", "chat");
            msg.put("text", text);
            send(msg.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error", e);
        }
    }

    /** Disconnect and clean up. */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Leaving room");
            webSocket = null;
        }
    }

    public String getCurrentRoomCode() { return currentRoomCode; }
    public boolean isHost() { return isHost; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void connect(String wsUrl, Runnable onOpen) {
        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, Response response) {
                onOpen.run();
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                handleMessage(text);
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response response) {
                String reason = t.getMessage() != null ? t.getMessage() : "Error de conexión";
                mainHandler.post(() -> { if (listener != null) listener.onError(reason); });
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                mainHandler.post(() -> { if (listener != null) listener.onDisconnected(); });
            }
        });
    }

    private void send(String json) {
        if (webSocket != null) webSocket.send(json);
    }

    private void sendSync(String action, long positionMs) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("action", "sync");
            msg.put("syncAction", action);
            msg.put("positionMs", positionMs);
            send(msg.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error", e);
        }
    }

    private void handleMessage(String text) {
        try {
            JSONObject obj = new JSONObject(text);
            String type = obj.optString("type");
            switch (type) {
                case "room_created":
                case "room_joined": {
                    currentRoomCode = obj.getString("code");
                    isHost = "room_created".equals(type);
                    int count = obj.optInt("participants", 1);
                    mainHandler.post(() -> {
                        if (listener != null) listener.onJoined(currentRoomCode, isHost, count);
                    });
                    break;
                }
                case "participants_update": {
                    int count = obj.optInt("count", 1);
                    org.json.JSONArray namesArr = obj.optJSONArray("names");
                    String[] names = new String[namesArr != null ? namesArr.length() : 0];
                    if (namesArr != null) {
                        for (int i = 0; i < namesArr.length(); i++) names[i] = namesArr.getString(i);
                    }
                    mainHandler.post(() -> {
                        if (listener != null) listener.onParticipantsChanged(count, names);
                    });
                    break;
                }
                case "chat": {
                    String sender = obj.optString("sender", "?");
                    String msg    = obj.optString("text", "");
                    mainHandler.post(() -> {
                        if (listener != null) listener.onChatMessage(sender, msg);
                    });
                    break;
                }
                case "system": {
                    String msg = obj.optString("text", "");
                    mainHandler.post(() -> {
                        if (listener != null) listener.onChatMessage(null, msg);
                    });
                    break;
                }
                case "sync": {
                    String syncAction = obj.optString("syncAction", "play");
                    long posMs = obj.optLong("positionMs", 0);
                    mainHandler.post(() -> {
                        if (listener != null) listener.onSync(syncAction, posMs);
                    });
                    break;
                }
                case "error": {
                    String reason = obj.optString("message", "Error desconocido");
                    mainHandler.post(() -> {
                        if (listener != null) listener.onError(reason);
                    });
                    break;
                }
                default:
                    Log.d(TAG, "Unknown message type: " + type);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse message: " + text, e);
        }
    }
}
