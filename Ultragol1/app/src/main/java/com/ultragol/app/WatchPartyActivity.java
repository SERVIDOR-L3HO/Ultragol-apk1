package com.ultragol.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.models.ContentItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Watch Party Activity — lets users create or join a synchronized viewing room.
 *
 * Intent extras:
 *   "item"  : ContentItem (required) — the content being watched
 *   "server": String (optional)       — override server base URL
 */
public class WatchPartyActivity extends AppCompatActivity implements WatchPartyManager.Listener {

    // ── Static launcher ───────────────────────────────────────────────────────
    public static void start(Context ctx, ContentItem item) {
        Intent i = new Intent(ctx, WatchPartyActivity.class);
        i.putExtra("item", item);
        ctx.startActivity(i);
    }

    // ── Views ─────────────────────────────────────────────────────────────────
    private ScrollView    lobbyView;
    private LinearLayout  roomView;
    private TextView      roomCodeBadge;
    private TextView      participantsLabel;
    private LinearLayout  hostControls;
    private TextView      syncBar;
    private TextView      statusMsg;
    private EditText      chatInput;
    private RecyclerView  chatRv;

    // ── State ─────────────────────────────────────────────────────────────────
    private ContentItem   item;
    private WatchPartyManager manager;
    private ChatAdapter   chatAdapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private boolean       inRoom = false;

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_party);

        item = (ContentItem) getIntent().getSerializableExtra("item");

        bindViews();
        setupMovieCard();
        setupButtons();
        setupChat();

        manager = new WatchPartyManager();
        manager.setListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null) manager.disconnect();
    }

    // ── View setup ────────────────────────────────────────────────────────────
    private void bindViews() {
        lobbyView         = findViewById(R.id.wpLobbyView);
        roomView          = findViewById(R.id.wpRoomView);
        roomCodeBadge     = findViewById(R.id.wpRoomCodeBadge);
        participantsLabel = findViewById(R.id.wpParticipantsLabel);
        hostControls      = findViewById(R.id.wpHostControls);
        syncBar           = findViewById(R.id.wpSyncBar);
        statusMsg         = findViewById(R.id.wpStatusMsg);
        chatInput         = findViewById(R.id.wpChatInput);
        chatRv            = findViewById(R.id.wpChatRv);

        View btnBack = findViewById(R.id.wpBtnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupMovieCard() {
        if (item == null) return;
        TextView titleView = findViewById(R.id.wpMovieTitle);
        TextView metaView  = findViewById(R.id.wpMovieMeta);
        ImageView poster   = findViewById(R.id.wpMoviePoster);

        if (titleView != null) titleView.setText(item.getTitle());
        if (metaView  != null) {
            String meta = item.getYear();
            if (!item.getGenre().isEmpty()) meta += " · " + item.getGenre();
            metaView.setText(meta);
        }
        if (poster != null && item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(this).load(item.getPosterUrl())
                 .transition(DrawableTransitionOptions.withCrossFade())
                 .into(poster);
        }
    }

    private void setupButtons() {
        // CREATE room
        TextView btnCreate = findViewById(R.id.wpBtnCreate);
        if (btnCreate != null) btnCreate.setOnClickListener(v -> {
            EditText passField = findViewById(R.id.wpCreatePassword);
            EditText nameField = findViewById(R.id.wpCreateName);
            String pass = passField != null ? passField.getText().toString().trim() : "";
            String name = nameField != null ? nameField.getText().toString().trim() : "";
            if (TextUtils.isEmpty(name)) {
                showStatus("Ingresa tu nombre para continuar.", true);
                return;
            }
            showStatus("Creando sala…", false);
            setButtonsEnabled(false);
            String contentId = item != null ? String.valueOf(item.getTmdbId()) : "unknown";
            manager.createRoom(contentId, pass, name);
        });

        // JOIN room
        TextView btnJoin = findViewById(R.id.wpBtnJoin);
        if (btnJoin != null) btnJoin.setOnClickListener(v -> {
            EditText codeField = findViewById(R.id.wpJoinCode);
            EditText passField = findViewById(R.id.wpJoinPassword);
            EditText nameField = findViewById(R.id.wpJoinName);
            String code = codeField != null ? codeField.getText().toString().trim() : "";
            String pass = passField != null ? passField.getText().toString().trim() : "";
            String name = nameField != null ? nameField.getText().toString().trim() : "";
            if (TextUtils.isEmpty(code)) {
                showStatus("Ingresa el código de sala.", true);
                return;
            }
            if (TextUtils.isEmpty(name)) {
                showStatus("Ingresa tu nombre para continuar.", true);
                return;
            }
            showStatus("Uniéndote a la sala " + code.toUpperCase() + "…", false);
            setButtonsEnabled(false);
            manager.joinRoom(code, pass, name);
        });
    }

    private void setupChat() {
        chatAdapter = new ChatAdapter(messages);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        chatRv.setLayoutManager(llm);
        chatRv.setAdapter(chatAdapter);

        // Send on keyboard "Done"
        chatInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendChat();
                return true;
            }
            return false;
        });

        TextView btnSend = findViewById(R.id.wpBtnSend);
        if (btnSend != null) btnSend.setOnClickListener(v -> sendChat());
    }

    private void enterRoom(String code, boolean isHost) {
        inRoom = true;

        // Switch lobby → room view
        lobbyView.setVisibility(View.GONE);
        roomView.setVisibility(View.VISIBLE);

        // Show code badge in header
        roomCodeBadge.setText(code);
        roomCodeBadge.setVisibility(View.VISIBLE);

        // Show/hide controls based on role
        hostControls.setVisibility(isHost ? View.VISIBLE : View.GONE);
        syncBar.setVisibility(isHost ? View.GONE : View.VISIBLE);

        // Host sync controls
        if (isHost) {
            TextView btnPlay  = findViewById(R.id.wpBtnSyncPlay);
            TextView btnPause = findViewById(R.id.wpBtnSyncPause);
            if (btnPlay  != null) btnPlay.setOnClickListener(v -> manager.syncPlay(0));
            if (btnPause != null) btnPause.setOnClickListener(v -> manager.syncPause(0));
        }

        // Copy room code
        TextView btnCopy = findViewById(R.id.wpBtnCopyCode);
        if (btnCopy != null) btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Código de sala", code));
            Toast.makeText(this, "Código copiado: " + code, Toast.LENGTH_SHORT).show();
        });

        // Welcome system message
        addSystemMessage(isHost
            ? "🎉 Sala creada. Código: " + code + ". Comparte el código con tus amigos."
            : "✅ Te uniste a la sala " + code + ". ¡A disfrutar!");
    }

    private void sendChat() {
        String text = chatInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;
        chatInput.setText("");
        manager.sendChat(text);
    }

    private void addMessage(String sender, String text) {
        messages.add(new ChatMessage(sender, text, false));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRv.scrollToPosition(messages.size() - 1);
    }

    private void addSystemMessage(String text) {
        messages.add(new ChatMessage(null, text, true));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRv.scrollToPosition(messages.size() - 1);
    }

    private void showStatus(String msg, boolean isError) {
        if (statusMsg != null) {
            statusMsg.setText(msg);
            statusMsg.setTextColor(isError
                ? android.graphics.Color.parseColor("#FF6B6B")
                : android.graphics.Color.parseColor("#AAFFFFFF"));
            statusMsg.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        View b1 = findViewById(R.id.wpBtnCreate);
        View b2 = findViewById(R.id.wpBtnJoin);
        if (b1 != null) b1.setEnabled(enabled);
        if (b2 != null) b2.setEnabled(enabled);
    }

    // ── WatchPartyManager.Listener ────────────────────────────────────────────

    @Override
    public void onJoined(String roomCode, boolean isHost, int participantCount) {
        enterRoom(roomCode, isHost);
        onParticipantsChanged(participantCount, new String[]{});
    }

    @Override
    public void onParticipantsChanged(int count, String[] names) {
        String label = count == 1 ? "👤 Solo tú" : "👥 " + count + " personas en la sala";
        participantsLabel.setText(label);
    }

    @Override
    public void onChatMessage(String sender, String text) {
        if (sender == null) addSystemMessage(text);
        else addMessage(sender, text);
    }

    @Override
    public void onSync(String action, long positionMs) {
        // Update the sync bar for guests
        switch (action) {
            case "play":
                syncBar.setText("▶ El host inició la reproducción");
                syncBar.setVisibility(View.VISIBLE);
                break;
            case "pause":
                syncBar.setText("⏸ El host pausó la reproducción");
                syncBar.setVisibility(View.VISIBLE);
                break;
            case "seek":
                syncBar.setText("⏩ El host cambió la posición");
                syncBar.setVisibility(View.VISIBLE);
                break;
        }
        // Fade out after 3 seconds
        syncBar.postDelayed(() -> syncBar.setVisibility(View.GONE), 3000);

        // TODO (future): if native ExoPlayer is running in the same session, sync it directly
        // via a shared singleton / LocalBroadcast
    }

    @Override
    public void onError(String reason) {
        showStatus("❌ " + reason, true);
        setButtonsEnabled(true);
    }

    @Override
    public void onDisconnected() {
        if (inRoom) {
            Toast.makeText(this, "Desconectado de la sala", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // ── Chat data model ───────────────────────────────────────────────────────

    static class ChatMessage {
        final String sender;
        final String text;
        final boolean isSystem;
        ChatMessage(String sender, String text, boolean isSystem) {
            this.sender   = sender;
            this.text     = text;
            this.isSystem = isSystem;
        }
    }

    // ── Chat RecyclerView adapter ─────────────────────────────────────────────

    static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_SYSTEM = 0;
        private static final int TYPE_CHAT   = 1;

        private final List<ChatMessage> items;
        ChatAdapter(List<ChatMessage> items) { this.items = items; }

        @Override public int getItemViewType(int pos) {
            return items.get(pos).isSystem ? TYPE_SYSTEM : TYPE_CHAT;
        }

        @NonNull @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View v = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.item_chat_message, parent, false);
            return new ChatVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
            ChatVH vh = (ChatVH) holder;
            ChatMessage msg = items.get(pos);
            if (msg.isSystem) {
                vh.systemMsg.setVisibility(View.VISIBLE);
                vh.bubble.setVisibility(View.GONE);
                vh.systemMsg.setText(msg.text);
            } else {
                vh.systemMsg.setVisibility(View.GONE);
                vh.bubble.setVisibility(View.VISIBLE);
                vh.sender.setText(msg.sender);
                vh.text.setText(msg.text);
            }
        }

        @Override public int getItemCount() { return items.size(); }

        static class ChatVH extends RecyclerView.ViewHolder {
            TextView systemMsg, sender, text;
            LinearLayout bubble;
            ChatVH(View v) {
                super(v);
                systemMsg = v.findViewById(R.id.chatSystemMsg);
                bubble    = v.findViewById(R.id.chatBubble);
                sender    = v.findViewById(R.id.chatSender);
                text      = v.findViewById(R.id.chatText);
            }
        }
    }
}
