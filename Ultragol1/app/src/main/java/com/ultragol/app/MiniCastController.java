package com.ultragol.app;

import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;

/**
 * Controller for the mini cast bar shown at the bottom of MediaActivity
 * while content is being transmitted to a TV.
 *
 * The bar is always visible over the ExoPlayer controls when casting,
 * allowing the phone to act as a remote control.
 */
public class MiniCastController {

    public interface Listener {
        void onPlayPause();
        void onStop();
        void onReturnToPhone();
    }

    private final View        root;
    private final TextView    tvTitle;
    private final TextView    tvDevice;
    private final ImageView   ivPoster;
    private final ImageButton btnPlayPause;
    private final ImageButton btnStop;
    private final View        btnLocal;
    private final Listener    listener;

    private boolean playing = true;

    public MiniCastController(View root, Listener listener) {
        this.root     = root;
        this.listener = listener;

        tvTitle      = root.findViewById(R.id.miniCastTitle);
        tvDevice     = root.findViewById(R.id.miniCastDevice);
        ivPoster     = root.findViewById(R.id.miniCastPoster);
        btnPlayPause = root.findViewById(R.id.miniCastPlayPause);
        btnStop      = root.findViewById(R.id.miniCastStop);
        btnLocal     = root.findViewById(R.id.miniCastLocal);

        if (btnPlayPause != null) btnPlayPause.setOnClickListener(v -> {
            if (listener != null) listener.onPlayPause();
        });
        if (btnStop != null) btnStop.setOnClickListener(v -> {
            if (listener != null) listener.onStop();
        });
        if (btnLocal != null) btnLocal.setOnClickListener(v -> {
            if (listener != null) listener.onReturnToPhone();
        });
    }

    /** Show the mini controller, set title, device name and load poster. */
    public void show(String title, String deviceName, String posterUrl) {
        root.setVisibility(View.VISIBLE);
        if (tvTitle  != null) tvTitle.setText(title != null ? title : "");
        if (tvDevice != null) tvDevice.setText("📺 Transmitiendo en: " + deviceName);
        if (ivPoster != null && posterUrl != null && !posterUrl.isEmpty()) {
            try {
                Glide.with(root.getContext())
                    .load(posterUrl)
                    .centerCrop()
                    .into(ivPoster);
            } catch (Exception ignored) {}
        }
        setPlaying(true);
    }

    public void hide() {
        root.setVisibility(View.GONE);
    }

    public boolean isVisible() {
        return root.getVisibility() == View.VISIBLE;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(
                playing ? R.drawable.ic_media_pause : R.drawable.ic_media_play);
        }
    }

    public boolean isPlaying() { return playing; }
}
