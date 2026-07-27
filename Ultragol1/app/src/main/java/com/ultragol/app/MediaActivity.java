package com.ultragol.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Collections;

// ── Continue Watching ─────────────────────────────────────────────────────────
import com.ultragol.app.models.ContentItem;

// ── Cast / TV Streaming ───────────────────────────────────────────────────────
import android.widget.ImageView;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouteSelector;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MediaActivity extends AppCompatActivity {

    public static final int RESULT_RETRY = Activity.RESULT_FIRST_USER + 1;
    static final int REQUEST_CODE = 1001;

    private static final int CONTROLS_HIDE_MS = 3000;
    private static final int SEEK_INCREMENT_MS = 10_000;
    private static final String PREFS_NAME = "media_progress";

    // Player
    private ExoPlayer player;
    private PlayerView playerView;
    private DefaultTrackSelector trackSelector;

    // UI
    private View controlsOverlay;
    private ProgressBar pbLoading;
    private ImageButton btnBack, btnSettings, btnPlayPause, btnRewind, btnForward, btnFitCrop;
    private ImageButton btnLock, btnServer, btnPip;
    private View lockOverlay, unlockHint;
    private TextView tvTitle, tvTime, btnSpeedChip, btnSleepTimerView, tvSleepTimerDisplay;
    private SeekBar seekBar;
    private ScrollView settingsPanel;
    private RadioGroup rgSpeed, rgQuality, rgSubtitles;
    private View tvQualityHeader, tvSubsHeader;
    private View gestureIndicator;
    private TextView tvGestureIcon, tvGesturePercent;
    private CardView resumeCard;
    private TextView tvResumeText, tvTapLeft, tvTapRight;

    // ── Cast fields ───────────────────────────────────────────────────────────
    private ImageButton btnCast;
    private MiniCastController miniCastController;
    private CastManager castManager;
    private CastDevice currentCastDevice;   // active DLNA or AirPlay device
    private SessionManagerListener<CastSession> castSessionListener;
    private boolean isCasting = false;

    // State
    private String videoUrl, videoTitle, referer, posterUrl;
    private boolean isM3u8;
    private boolean controlsVisible = false;
    private boolean isFitMode = true;
    private boolean settingsPanelVisible = false;
    private boolean isSeekBarDragging = false;
    private boolean playerReady = false;
    private boolean consumedByButton = false;
    private boolean screenLocked = false;

    // Speed cycling
    private static final float[]  SPEEDS       = {0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f};
    private static final String[] SPEED_LABELS = {"0.5×", "0.75×", "1×", "1.25×", "1.5×", "2×"};
    private int speedIndex = 2;

    // Sleep timer
    private long sleepTimerEndMs = 0;
    private final Runnable hideUnlockHintRunnable = () -> {
        if (unlockHint != null) unlockHint.animate().alpha(0f).setDuration(400)
                .withEndAction(() -> unlockHint.setVisibility(View.GONE)).start();
    };
    private final Runnable sleepCountdownUpdater = new Runnable() {
        @Override public void run() {
            if (sleepTimerEndMs <= 0 || tvSleepTimerDisplay == null) return;
            long remaining = sleepTimerEndMs - System.currentTimeMillis();
            if (remaining <= 0) {
                if (player != null) player.setPlayWhenReady(false);
                cancelSleepTimer();
                return;
            }
            long m = remaining / 60000;
            long s = (remaining % 60000) / 1000;
            tvSleepTimerDisplay.setText(String.format(Locale.US, "⏱ %d:%02d", m, s));
            mainHandler.postDelayed(this, 1000);
        }
    };

    // PiP
    private boolean isInPipMode = false;
    private static final String PIP_ACTION   = "com.ultragol.app.PIP_CTRL";
    private static final String PIP_EXTRA    = "pip_ctrl";
    private static final int    CTRL_PLAY_PAUSE = 1;
    private final BroadcastReceiver pipReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context ctx, Intent intent) {
            if (PIP_ACTION.equals(intent.getAction())
                    && intent.getIntExtra(PIP_EXTRA, 0) == CTRL_PLAY_PAUSE) {
                togglePlayPause();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) updatePipActions();
            }
        }
    };

    // Gesture
    private GestureDetector gestureDetector;
    private AudioManager audioManager;
    private float gestureStartX, gestureStartY;
    private int gestureStartVolume;
    private float gestureStartBrightness;
    private boolean isGestureBrightness, isGestureVolume, isGestureSeek;
    private long gestureStartPosition;
    private boolean gestureActive = false;
    private float gestureThresholdPx;

    // Handlers
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideControlsRunnable = this::hideControls;
    private final Runnable progressUpdater = new Runnable() {
        @Override public void run() {
            if (player != null) updateProgress();
            mainHandler.postDelayed(this, 500);
        }
    };

    // Track info
    private final List<TrackInfo> videoTracks = new ArrayList<>();
    private final List<TrackInfo> textTracks = new ArrayList<>();
    private Tracks.Group videoTrackGroup = null;
    private Tracks.Group textTrackGroup = null;

    // Offline playback flag — uses ExoPlayer cache instead of network
    private boolean useOffline = false;

    // Continue-watching tracking
    private ContentItem watchedItem;
    private String serverUrlForSave;
    private int watchedSeason  = 1;
    private int watchedEpisode = 1;

    // Prefs
    private SharedPreferences prefs;

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        hideSystemBars();

        setContentView(R.layout.activity_media);

        videoUrl         = getIntent().getStringExtra("url");
        videoTitle       = getIntent().getStringExtra("title");
        referer          = getIntent().getStringExtra("referer");
        posterUrl        = getIntent().getStringExtra("poster_url");
        isM3u8           = getIntent().getBooleanExtra("is_m3u8", false);
        useOffline       = getIntent().getBooleanExtra("use_offline", false);
        watchedItem      = (ContentItem) getIntent().getSerializableExtra("item");
        serverUrlForSave = getIntent().getStringExtra("server_url");
        watchedSeason    = getIntent().getIntExtra("season", 1);
        watchedEpisode   = getIntent().getIntExtra("episode", 1);

        if (videoUrl == null || videoUrl.isEmpty()) { finish(); return; }

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gestureThresholdPx = 14f * getResources().getDisplayMetrics().density;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        bindViews();
        setupControls();
        setupGestures();
        setupPlayer();
        checkResumePosition();
        setupCast();
    }

    @Override protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IntentFilter filter = new IntentFilter(PIP_ACTION);
            registerReceiver(pipReceiver, filter);
        }
    }

    @Override protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try { unregisterReceiver(pipReceiver); } catch (Exception ignored) {}
        }
    }

    @Override protected void onPause() {
        super.onPause();
        // Unregister Cast session listener
        if (castManager != null && castManager.isAvailable() && castSessionListener != null) {
            SessionManager sm = castManager.getSessionManager();
            if (sm != null) sm.removeSessionManagerListener(castSessionListener, CastSession.class);
        }
        if (!isInPipMode && player != null) {
            saveProgress(player.getCurrentPosition());
            player.setPlayWhenReady(false);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (!isInPipMode) {
            hideSystemBars();
            if (player != null && !isCasting) player.setPlayWhenReady(true);
        }
        // Register Cast session listener
        if (castManager != null && castManager.isAvailable() && castSessionListener != null) {
            SessionManager sm = castManager.getSessionManager();
            if (sm != null) sm.addSessionManagerListener(castSessionListener, CastSession.class);
        }
    }

    @Override protected void onDestroy() {
        mainHandler.removeCallbacksAndMessages(null);
        if (player != null) {
            saveProgress(player.getCurrentPosition());
            player.release();
            player = null;
        }
        // Stop DLNA / AirPlay if active
        if (currentCastDevice != null) {
            if (currentCastDevice.getType() == CastDevice.Type.DLNA)
                DLNAManager.getInstance().stopVideo(currentCastDevice);
            else if (currentCastDevice.getType() == CastDevice.Type.AIRPLAY)
                AirPlayManager.getInstance().stopVideo(currentCastDevice);
        }
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (screenLocked) { showUnlockHint(); return; }
        if (settingsPanelVisible) { closeSettings(); return; }
        if (player != null) saveProgress(player.getCurrentPosition());
        super.onBackPressed();
    }

    // ─── D-pad / Remote control / Keyboard ────────────────────────────────────
    //
    // Supported controls (all device types):
    //   TV remote    : DPAD_CENTER=play-pause, ←→=seek±10s, ↑↓=show controls
    //   Gamepad      : A=play-pause, ←→=seek, ↑↓=show controls
    //   PC keyboard  : Space/Enter=play-pause, ←→=seek, F=fullscreen, Esc=back
    //   Media keys   : MEDIA_PLAY_PAUSE, MEDIA_FAST_FORWARD, MEDIA_REWIND
    //
    // Rule: any key shows the controls and resets the auto-hide timer.
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        if (event.getAction() != android.view.KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        if (isInPipMode) return super.dispatchKeyEvent(event);

        int kc = event.getKeyCode();

        // ── Back / Escape ─────────────────────────────────────────────────────
        if (kc == android.view.KeyEvent.KEYCODE_BACK
                || kc == android.view.KeyEvent.KEYCODE_ESCAPE) {
            onBackPressed();
            return true;
        }

        // ── Screen locked: only unlock hint, no other actions ────────────────
        if (screenLocked) {
            showUnlockHint();
            return true;
        }

        // ── Media keys (headset / Bluetooth remote / keyboard media row) ──────
        if (kc == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || kc == android.view.KeyEvent.KEYCODE_HEADSETHOOK) {
            showControls();
            togglePlayPause();
            return true;
        }
        if (kc == android.view.KeyEvent.KEYCODE_MEDIA_PLAY) {
            showControls();
            if (player != null && !player.isPlaying()) { player.play(); updatePlayPauseBtn(); }
            return true;
        }
        if (kc == android.view.KeyEvent.KEYCODE_MEDIA_PAUSE) {
            showControls();
            if (player != null && player.isPlaying()) { player.pause(); updatePlayPauseBtn(); }
            return true;
        }
        if (kc == android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || kc == android.view.KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD) {
            showControls();
            seekBy(SEEK_INCREMENT_MS);
            return true;
        }
        if (kc == android.view.KeyEvent.KEYCODE_MEDIA_REWIND
                || kc == android.view.KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD) {
            showControls();
            seekBy(-SEEK_INCREMENT_MS);
            return true;
        }

        // ── D-pad / Arrow keys ────────────────────────────────────────────────
        if (kc == android.view.KeyEvent.KEYCODE_DPAD_CENTER
                || kc == android.view.KeyEvent.KEYCODE_ENTER
                || kc == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER
                || kc == android.view.KeyEvent.KEYCODE_SPACE) {
            if (!controlsVisible) {
                showControls();
            } else {
                togglePlayPause();
            }
            return true;
        }

        if (kc == android.view.KeyEvent.KEYCODE_DPAD_RIGHT
                || kc == android.view.KeyEvent.KEYCODE_MOVE_END) {
            showControls();
            seekBy(SEEK_INCREMENT_MS);
            return true;
        }

        if (kc == android.view.KeyEvent.KEYCODE_DPAD_LEFT
                || kc == android.view.KeyEvent.KEYCODE_MOVE_HOME) {
            showControls();
            seekBy(-SEEK_INCREMENT_MS);
            return true;
        }

        if (kc == android.view.KeyEvent.KEYCODE_DPAD_UP
                || kc == android.view.KeyEvent.KEYCODE_DPAD_DOWN) {
            // Show controls (volume adjustment is handled by system)
            showControls();
            return true;
        }

        // ── Long-seek: Page Up/Down or Channel +/- ────────────────────────────
        if (kc == android.view.KeyEvent.KEYCODE_PAGE_UP
                || kc == android.view.KeyEvent.KEYCODE_CHANNEL_UP) {
            showControls();
            seekBy(SEEK_INCREMENT_MS * 9);   // +90 s
            return true;
        }
        if (kc == android.view.KeyEvent.KEYCODE_PAGE_DOWN
                || kc == android.view.KeyEvent.KEYCODE_CHANNEL_DOWN) {
            showControls();
            seekBy(-SEEK_INCREMENT_MS * 9);  // -90 s
            return true;
        }

        // ── PC-specific shortcuts ─────────────────────────────────────────────
        if (kc == android.view.KeyEvent.KEYCODE_F) {
            // F = toggle fit/crop
            toggleFitCrop();
            showControls();
            return true;
        }
        if (kc == android.view.KeyEvent.KEYCODE_S) {
            // S = settings panel
            showControls();
            if (settingsPanelVisible) closeSettings(); else openSettings();
            return true;
        }

        // ── Any other key: just show the controls overlay ─────────────────────
        if (!controlsVisible) {
            showControls();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    /** Al presionar Home mientras reproduce → entra en PiP automáticamente */
    @Override public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && player != null && player.isPlaying()) {
            enterPipMode();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPip, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPip, newConfig);
        isInPipMode = isInPip;
        if (isInPip) {
            // Ocultar controles, gestos, card de reanudar
            if (controlsOverlay != null) controlsOverlay.setVisibility(View.GONE);
            if (resumeCard != null) resumeCard.setVisibility(View.GONE);
            if (gestureIndicator != null) gestureIndicator.setVisibility(View.GONE);
            mainHandler.removeCallbacks(hideControlsRunnable);
        } else {
            // Volver a pantalla completa
            hideSystemBars();
            if (player != null && !player.isPlaying()) showControls();
        }
    }

    // ─── View binding ─────────────────────────────────────────────────────────

    private void bindViews() {
        playerView       = findViewById(R.id.playerView);
        controlsOverlay  = findViewById(R.id.controlsOverlay);
        pbLoading        = findViewById(R.id.pbLoading);
        btnBack          = findViewById(R.id.btnBack);
        btnSettings      = findViewById(R.id.btnSettings);
        btnPlayPause     = findViewById(R.id.btnPlayPause);
        btnRewind        = findViewById(R.id.btnRewind);
        btnForward       = findViewById(R.id.btnForward);
        btnFitCrop       = findViewById(R.id.btnFitCrop);
        tvTitle          = findViewById(R.id.tvMediaTitle);
        tvTime           = findViewById(R.id.tvTime);
        seekBar          = findViewById(R.id.seekBar);
        settingsPanel    = findViewById(R.id.settingsPanel);
        rgSpeed          = findViewById(R.id.rgSpeed);
        rgQuality        = findViewById(R.id.rgQuality);
        rgSubtitles      = findViewById(R.id.rgSubtitles);
        tvQualityHeader  = findViewById(R.id.tvQualityHeader);
        tvSubsHeader     = findViewById(R.id.tvSubsHeader);
        gestureIndicator = findViewById(R.id.gestureIndicator);
        tvGestureIcon    = findViewById(R.id.tvGestureIcon);
        tvGesturePercent = findViewById(R.id.tvGesturePercent);
        resumeCard       = findViewById(R.id.resumeCard);
        tvResumeText     = findViewById(R.id.tvResumeText);
        tvTapLeft        = findViewById(R.id.tvTapLeft);
        tvTapRight       = findViewById(R.id.tvTapRight);

        btnLock             = findViewById(R.id.btnLock);
        btnServer           = findViewById(R.id.btnServer);
        btnPip              = findViewById(R.id.btnPip);
        btnCast             = findViewById(R.id.btnCast);

        // Mini cast controller
        View miniCastBar = findViewById(R.id.miniCastBar);
        if (miniCastBar != null) {
            miniCastController = new MiniCastController(miniCastBar, new MiniCastController.Listener() {
                @Override public void onPlayPause()     { onMiniCastPlayPause(); }
                @Override public void onStop()          { stopAllCasting(true); }
                @Override public void onReturnToPhone() { stopAllCasting(true); }
            });
        }
        lockOverlay         = findViewById(R.id.lockOverlay);
        unlockHint          = findViewById(R.id.unlockHint);
        btnSpeedChip        = findViewById(R.id.btnSpeedChip);
        btnSleepTimerView   = findViewById(R.id.btnSleepTimer);
        tvSleepTimerDisplay = findViewById(R.id.tvSleepTimer);

        if (tvTitle != null && videoTitle != null) tvTitle.setText(videoTitle);
    }

    // ─── Controls setup ───────────────────────────────────────────────────────

    @SuppressLint("ClickableViewAccessibility")
    private void setupControls() {
        btnBack.setOnClickListener(v -> {
            consumedByButton = true;
            if (player != null) saveProgress(player.getCurrentPosition());
            finish();
        });
        btnSettings.setOnClickListener(v -> {
            consumedByButton = true;
            if (settingsPanelVisible) closeSettings(); else openSettings();
        });
        btnPlayPause.setOnClickListener(v -> { consumedByButton = true; togglePlayPause(); });
        btnRewind.setOnClickListener(v -> { consumedByButton = true; seekBy(-SEEK_INCREMENT_MS); });
        btnForward.setOnClickListener(v -> { consumedByButton = true; seekBy(SEEK_INCREMENT_MS); });
        btnFitCrop.setOnClickListener(v -> { consumedByButton = true; toggleFitCrop(); });

        if (btnLock   != null) btnLock.setOnClickListener(v -> { consumedByButton = true; activateLock(); });
        if (btnServer != null) btnServer.setOnClickListener(v -> { consumedByButton = true; changeServer(); });
        if (btnCast   != null) btnCast.setOnClickListener(v -> { consumedByButton = true; showCastPanel(); });
        if (btnPip    != null) btnPip.setOnClickListener(v -> {
            consumedByButton = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) enterPipMode();
        });
        if (btnSpeedChip    != null) btnSpeedChip.setOnClickListener(v -> { consumedByButton = true; cycleSpeed(); });
        if (btnSleepTimerView != null) btnSleepTimerView.setOnClickListener(v -> { consumedByButton = true; openSettings(); });

        View btnClose = settingsPanel.findViewById(R.id.btnSettingsClose);
        if (btnClose != null) btnClose.setOnClickListener(v -> closeSettings());

        View btnServerSt = settingsPanel.findViewById(R.id.btnServerFromSettings);
        if (btnServerSt != null) btnServerSt.setOnClickListener(v -> changeServer());

        RadioGroup rgSleepTimer = settingsPanel.findViewById(R.id.rgSleepTimer);
        if (rgSleepTimer != null) {
            rgSleepTimer.setOnCheckedChangeListener((g, id) -> {
                if      (id == R.id.rbTimerOff)  setSleepTimer(0);
                else if (id == R.id.rbTimer15)   setSleepTimer(15 * 60 * 1000L);
                else if (id == R.id.rbTimer30)   setSleepTimer(30 * 60 * 1000L);
                else if (id == R.id.rbTimer60)   setSleepTimer(60 * 60 * 1000L);
                else if (id == R.id.rbTimerEnd)  setSleepTimer(-1L);
            });
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                if (fromUser && player != null) {
                    long dur = player.getDuration();
                    if (dur > 0) updateTimeText((long)(p / 1000f * dur), dur);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar s) {
                consumedByButton = true;
                isSeekBarDragging = true;
                mainHandler.removeCallbacks(hideControlsRunnable);
            }
            @Override public void onStopTrackingTouch(SeekBar s) {
                if (player != null) {
                    long dur = player.getDuration();
                    if (dur > 0) player.seekTo((long)(s.getProgress() / 1000f * dur));
                }
                isSeekBarDragging = false;
                scheduleHideControls();
            }
        });

        // Speed — also syncs chip label
        rgSpeed.setOnCheckedChangeListener((group, id) -> {
            float sp = 1f; String lbl = "1×";
            if      (id == R.id.rbSpeed050) { sp = 0.5f;  lbl = "0.5×";  speedIndex = 0; }
            else if (id == R.id.rbSpeed075) { sp = 0.75f; lbl = "0.75×"; speedIndex = 1; }
            else if (id == R.id.rbSpeed100) { sp = 1f;    lbl = "1×";    speedIndex = 2; }
            else if (id == R.id.rbSpeed125) { sp = 1.25f; lbl = "1.25×"; speedIndex = 3; }
            else if (id == R.id.rbSpeed150) { sp = 1.5f;  lbl = "1.5×";  speedIndex = 4; }
            else if (id == R.id.rbSpeed200) { sp = 2f;    lbl = "2×";    speedIndex = 5; }
            if (player != null) player.setPlaybackParameters(new PlaybackParameters(sp));
            if (btnSpeedChip != null) btnSpeedChip.setText(lbl);
        });
    }

    // ─── Player ───────────────────────────────────────────────────────────────

    private void setupPlayer() {
        trackSelector = new DefaultTrackSelector(this);

        player = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();

        playerView.setPlayer(player);
        playerView.setUseController(false);

        String ua = "Mozilla/5.0 (Linux; Android 12; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36";

        DataSource.Factory dsFactory;
        if (videoUrl != null && videoUrl.startsWith("file://")) {
            // Local file on disk — DefaultDataSource handles file:// natively
            dsFactory = new com.google.android.exoplayer2.upstream.DefaultDataSource.Factory(this);
        } else if (useOffline) {
            // Serve from ExoPlayer's offline cache (downloaded HLS segments)
            dsFactory = DownloadUtil.getInstance(this).buildCacheDataSourceFactory();
        } else {
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", ua);
            if (referer != null && !referer.isEmpty()) headers.put("Referer", referer);
            dsFactory = new DefaultHttpDataSource.Factory()
                    .setUserAgent(ua)
                    .setDefaultRequestProperties(headers)
                    .setConnectTimeoutMs(15_000)
                    .setReadTimeoutMs(20_000);
        }

        com.google.android.exoplayer2.source.MediaSource src;
        if (isM3u8) {
            src = new HlsMediaSource.Factory(dsFactory)
                    .createMediaSource(MediaItem.fromUri(videoUrl));
        } else {
            src = new ProgressiveMediaSource.Factory(dsFactory)
                    .createMediaSource(MediaItem.fromUri(videoUrl));
        }

        player.addListener(new Player.Listener() {
            @Override public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        pbLoading.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_READY:
                        pbLoading.setVisibility(View.GONE);
                        if (!playerReady) {
                            playerReady = true;
                            updatePlayPauseBtn();
                            populateTrackOptions();
                            mainHandler.post(progressUpdater);
                        }
                        break;
                    case Player.STATE_ENDED:
                        pbLoading.setVisibility(View.GONE);
                        showControls();
                        mainHandler.removeCallbacks(hideControlsRunnable);
                        break;
                    default:
                        pbLoading.setVisibility(View.GONE);
                        break;
                }
            }
            @Override public void onIsPlayingChanged(boolean isPlaying) {
                updatePlayPauseBtn();
                if (isPlaying) scheduleHideControls();
                else mainHandler.removeCallbacks(hideControlsRunnable);
            }
            @Override public void onPlayerError(PlaybackException error) {
                pbLoading.setVisibility(View.GONE);
                showErrorSnackbar();
            }
        });

        player.setMediaSource(src);
        player.prepare();
        player.setPlayWhenReady(true);

        showControls();
    }

    // ─── Track selection ──────────────────────────────────────────────────────

    private void populateTrackOptions() {
        if (player == null) return;
        Tracks tracks = player.getCurrentTracks();
        videoTracks.clear();
        textTracks.clear();
        videoTrackGroup = null;
        textTrackGroup = null;

        for (Tracks.Group group : tracks.getGroups()) {
            if (group.getType() == C.TRACK_TYPE_VIDEO) {
                videoTrackGroup = group;
                for (int i = 0; i < group.length; i++) {
                    Format fmt = group.getTrackFormat(i);
                    String label = fmt.height > 0 ? fmt.height + "p" : ("Video " + (i + 1));
                    videoTracks.add(new TrackInfo(i, label));
                }
            } else if (group.getType() == C.TRACK_TYPE_TEXT) {
                textTrackGroup = group;
                for (int i = 0; i < group.length; i++) {
                    Format fmt = group.getTrackFormat(i);
                    String label = fmt.label != null ? fmt.label
                            : (fmt.language != null ? fmt.language : ("Sub " + (i + 1)));
                    textTracks.add(new TrackInfo(i, label));
                }
            }
        }

        // Quality
        if (!videoTracks.isEmpty() && tvQualityHeader != null && rgQuality != null) {
            tvQualityHeader.setVisibility(View.VISIBLE);
            rgQuality.setVisibility(View.VISIBLE);
            rgQuality.removeAllViews();
            RadioButton rbAuto = makeRadioBtn("Auto", R.id.rbQualityAuto);
            rbAuto.setChecked(true);
            rgQuality.addView(rbAuto);
            for (TrackInfo t : videoTracks) rgQuality.addView(makeRadioBtn(t.label, View.generateViewId()));
            Tracks.Group finalVGroup = videoTrackGroup;
            rgQuality.setOnCheckedChangeListener((g, id) -> {
                if (id == R.id.rbQualityAuto) {
                    trackSelector.setParameters(trackSelector.buildUponParameters()
                            .clearVideoSizeConstraints());
                } else {
                    int pos = rgQuality.indexOfChild(rgQuality.findViewById(id)) - 1;
                    if (pos >= 0 && pos < videoTracks.size() && finalVGroup != null) {
                        trackSelector.setParameters(trackSelector.buildUponParameters()
                                .clearVideoSizeConstraints()
                                .addOverride(new TrackSelectionOverride(
                                        finalVGroup.getMediaTrackGroup(),
                                        videoTracks.get(pos).index)));
                    }
                }
            });
        }

        // Subtitles
        if (tvSubsHeader != null && rgSubtitles != null) {
            rgSubtitles.removeAllViews();
            RadioButton rbOff = makeRadioBtn("Off", R.id.rbSubsOff);
            rbOff.setChecked(true);
            rgSubtitles.addView(rbOff);
            for (TrackInfo t : textTracks) rgSubtitles.addView(makeRadioBtn(t.label, View.generateViewId()));
            tvSubsHeader.setVisibility(View.VISIBLE);
            rgSubtitles.setVisibility(View.VISIBLE);
            Tracks.Group finalTGroup = textTrackGroup;
            rgSubtitles.setOnCheckedChangeListener((g, id) -> {
                if (id == R.id.rbSubsOff) {
                    trackSelector.setParameters(trackSelector.buildUponParameters()
                            .setRendererDisabled(C.TRACK_TYPE_TEXT, true));
                } else {
                    int pos = rgSubtitles.indexOfChild(rgSubtitles.findViewById(id)) - 1;
                    if (pos >= 0 && pos < textTracks.size() && finalTGroup != null) {
                        trackSelector.setParameters(trackSelector.buildUponParameters()
                                .setRendererDisabled(C.TRACK_TYPE_TEXT, false)
                                .addOverride(new TrackSelectionOverride(
                                        finalTGroup.getMediaTrackGroup(),
                                        textTracks.get(pos).index)));
                    }
                }
            });
        }
    }

    private RadioButton makeRadioBtn(String text, int id) {
        RadioButton rb = new RadioButton(this);
        rb.setId(id);
        rb.setText(text);
        rb.setTextColor(0xFFFFFFFF);
        rb.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFFE50914));
        rb.setPadding(rb.getPaddingLeft(), 4, rb.getPaddingRight(), 4);
        return rb;
    }

    // ─── Controls visibility ──────────────────────────────────────────────────

    private void showControls() {
        if (isInPipMode || screenLocked) return;
        controlsVisible = true;
        controlsOverlay.setVisibility(View.VISIBLE);
        controlsOverlay.animate().alpha(1f).setDuration(200).start();
        scheduleHideControls();
    }

    private void hideControls() {
        controlsVisible = false;
        controlsOverlay.animate().alpha(0f).setDuration(300)
                .withEndAction(() -> {
                    if (!controlsVisible) controlsOverlay.setVisibility(View.GONE);
                }).start();
    }

    private void scheduleHideControls() {
        mainHandler.removeCallbacks(hideControlsRunnable);
        if (player != null && player.isPlaying() && !isSeekBarDragging && !settingsPanelVisible) {
            mainHandler.postDelayed(hideControlsRunnable, CONTROLS_HIDE_MS);
        }
    }

    // ─── Playback ─────────────────────────────────────────────────────────────

    private void togglePlayPause() {
        if (player == null) return;
        if (player.isPlaying()) player.pause();
        else player.play();
        updatePlayPauseBtn();
    }

    private void updatePlayPauseBtn() {
        if (btnPlayPause == null || player == null) return;
        btnPlayPause.setImageResource(player.isPlaying()
                ? R.drawable.ic_media_pause : R.drawable.ic_media_play);
    }

    private void seekBy(long ms) {
        if (player == null) return;
        long dur = player.getDuration();
        long newPos = Math.max(0, Math.min(player.getCurrentPosition() + ms, dur > 0 ? dur : Long.MAX_VALUE));
        player.seekTo(newPos);
        scheduleHideControls();
    }

    private void toggleFitCrop() {
        isFitMode = !isFitMode;
        playerView.setResizeMode(isFitMode
                ? AspectRatioFrameLayout.RESIZE_MODE_FIT
                : AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
    }

    // ─── Progress ─────────────────────────────────────────────────────────────

    private void updateProgress() {
        if (player == null || isSeekBarDragging) return;
        long pos = player.getCurrentPosition();
        long dur = player.getDuration();
        if (dur > 0) seekBar.setProgress((int)(pos * 1000f / dur));
        updateTimeText(pos, dur);
    }

    private void updateTimeText(long pos, long dur) {
        if (tvTime != null) tvTime.setText(formatTime(pos) + " / " + formatTime(Math.max(0, dur)));
    }

    private String formatTime(long ms) {
        if (ms <= 0) return "0:00";
        long h = TimeUnit.MILLISECONDS.toHours(ms);
        long m = TimeUnit.MILLISECONDS.toMinutes(ms) % 60;
        long s = TimeUnit.MILLISECONDS.toSeconds(ms) % 60;
        return h > 0 ? String.format(Locale.US, "%d:%02d:%02d", h, m, s)
                     : String.format(Locale.US, "%d:%02d", m, s);
    }

    // ─── Settings panel ───────────────────────────────────────────────────────

    private void openSettings() {
        settingsPanelVisible = true;
        settingsPanel.setVisibility(View.VISIBLE);
        settingsPanel.setTranslationX(settingsPanel.getWidth() > 0 ? settingsPanel.getWidth() : 270 * (int)getResources().getDisplayMetrics().density);
        settingsPanel.animate().translationX(0).setDuration(250).start();
        mainHandler.removeCallbacks(hideControlsRunnable);
    }

    private void closeSettings() {
        settingsPanelVisible = false;
        settingsPanel.animate()
                .translationX(settingsPanel.getWidth() > 0 ? settingsPanel.getWidth() : 800)
                .setDuration(250)
                .withEndAction(() -> settingsPanel.setVisibility(View.GONE))
                .start();
        scheduleHideControls();
    }

    // ─── Gestures ─────────────────────────────────────────────────────────────

    private void setupGestures() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!consumedByButton && !gestureActive) {
                    if (controlsVisible) hideControls();
                    else showControls();
                }
                return true;
            }
            @Override public boolean onDoubleTap(MotionEvent e) {
                consumedByButton = true;
                float halfW = playerView.getWidth() / 2f;
                if (e.getX() < halfW) {
                    seekBy(-SEEK_INCREMENT_MS);
                    showTapFeedback(false);
                } else {
                    seekBy(SEEK_INCREMENT_MS);
                    showTapFeedback(true);
                }
                return true;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);
    }

    private void showTapFeedback(boolean forward) {
        TextView tv = forward ? tvTapRight : tvTapLeft;
        if (tv == null) return;
        tv.setAlpha(1f);
        tv.setVisibility(View.VISIBLE);
        tv.animate().alpha(0f).setDuration(600)
                .withEndAction(() -> tv.setVisibility(View.GONE)).start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            consumedByButton = false;
            gestureActive = false;
        }
        gestureDetector.onTouchEvent(event);
        handleSwipeGesture(event);
        return super.dispatchTouchEvent(event);
    }

    private void handleSwipeGesture(MotionEvent event) {
        if (settingsPanelVisible) return;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                gestureStartX = event.getX();
                gestureStartY = event.getY();
                isGestureBrightness = false;
                isGestureVolume = false;
                isGestureSeek = false;
                if (player != null) gestureStartPosition = player.getCurrentPosition();
                gestureStartVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                gestureStartBrightness = getCurrentBrightnessPercent();
                break;

            case MotionEvent.ACTION_MOVE:
                if (consumedByButton) break;
                float dx = event.getX() - gestureStartX;
                float dy = event.getY() - gestureStartY;

                if (!isGestureBrightness && !isGestureVolume && !isGestureSeek) {
                    if (Math.abs(dx) > gestureThresholdPx && Math.abs(dx) > Math.abs(dy) * 1.5f) {
                        isGestureSeek = true;
                        gestureActive = true;
                    } else if (Math.abs(dy) > gestureThresholdPx && Math.abs(dy) > Math.abs(dx) * 1.5f) {
                        gestureActive = true;
                        if (gestureStartX < playerView.getWidth() / 2f) isGestureBrightness = true;
                        else isGestureVolume = true;
                    }
                }

                if (isGestureSeek && player != null) {
                    long dur = player.getDuration();
                    if (dur > 0) {
                        long delta = (long)(dx / playerView.getWidth() * dur * 0.6f);
                        long newPos = Math.max(0, Math.min(gestureStartPosition + delta, dur));
                        long diffSec = (newPos - gestureStartPosition) / 1000;
                        updateTimeText(newPos, dur);
                        seekBar.setProgress((int)(newPos * 1000f / dur));
                        showGestureIndicator("⏩", (diffSec >= 0 ? "+" : "") + diffSec + "s");
                        if (!controlsVisible) showControls();
                    }
                } else if (isGestureBrightness) {
                    int delta = (int)(-dy / playerView.getHeight() * 100f);
                    int newPct = Math.max(0, Math.min(100, (int) gestureStartBrightness + delta));
                    setBrightness(newPct);
                } else if (isGestureVolume) {
                    int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int delta = (int)(-dy / playerView.getHeight() * maxVol);
                    int newVol = Math.max(0, Math.min(maxVol, gestureStartVolume + delta));
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0);
                    int pct = (int)(newVol * 100f / maxVol);
                    showGestureIndicator("🔊", pct + "%");
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isGestureSeek && player != null) {
                    long dur = player.getDuration();
                    if (dur > 0) player.seekTo((long)(seekBar.getProgress() / 1000f * dur));
                }
                hideGestureIndicator();
                isGestureBrightness = false;
                isGestureVolume = false;
                isGestureSeek = false;
                if (gestureActive) scheduleHideControls();
                break;
        }
    }

    // ─── Lock screen ──────────────────────────────────────────────────────────

    private void activateLock() {
        screenLocked = true;
        hideControls();
        if (lockOverlay != null) {
            lockOverlay.setVisibility(View.VISIBLE);
            lockOverlay.setOnClickListener(v -> showUnlockHint());
        }
        View btnUnlockBtn = unlockHint != null ? unlockHint.findViewById(R.id.btnUnlock) : null;
        if (btnUnlockBtn != null) btnUnlockBtn.setOnClickListener(v -> deactivateLock());
    }

    private void deactivateLock() {
        screenLocked = false;
        if (lockOverlay != null) lockOverlay.setVisibility(View.GONE);
        if (unlockHint  != null) { unlockHint.clearAnimation(); unlockHint.setVisibility(View.GONE); }
        showControls();
    }

    private void showUnlockHint() {
        if (unlockHint == null) return;
        mainHandler.removeCallbacks(hideUnlockHintRunnable);
        unlockHint.setAlpha(1f);
        unlockHint.setVisibility(View.VISIBLE);
        mainHandler.postDelayed(hideUnlockHintRunnable, 2200);
    }

    // ─── Change server ────────────────────────────────────────────────────────

    private void changeServer() {
        if (player != null) { saveProgress(player.getCurrentPosition()); player.pause(); }
        setResult(RESULT_RETRY);
        finish();
    }

    // ─── Speed cycling ────────────────────────────────────────────────────────

    private void cycleSpeed() {
        speedIndex = (speedIndex + 1) % SPEEDS.length;
        float sp = SPEEDS[speedIndex];
        if (player != null) player.setPlaybackParameters(new PlaybackParameters(sp));
        if (btnSpeedChip != null) btnSpeedChip.setText(SPEED_LABELS[speedIndex]);
        // Sync radio in settings
        if (rgSpeed != null) {
            int[] ids = {R.id.rbSpeed050, R.id.rbSpeed075, R.id.rbSpeed100,
                         R.id.rbSpeed125, R.id.rbSpeed150, R.id.rbSpeed200};
            if (speedIndex < ids.length) rgSpeed.check(ids[speedIndex]);
        }
        scheduleHideControls();
    }

    // ─── Sleep timer ──────────────────────────────────────────────────────────

    private void setSleepTimer(long durationMs) {
        mainHandler.removeCallbacks(sleepCountdownUpdater);
        sleepTimerEndMs = 0;
        if (tvSleepTimerDisplay != null) tvSleepTimerDisplay.setVisibility(View.GONE);
        if (btnSleepTimerView != null) btnSleepTimerView.setTextColor(0xAAFFFFFF);
        if (durationMs == 0) return;
        if (durationMs < 0) {
            if (tvSleepTimerDisplay != null) {
                tvSleepTimerDisplay.setText("⏱ Fin");
                tvSleepTimerDisplay.setVisibility(View.VISIBLE);
            }
            if (btnSleepTimerView != null) btnSleepTimerView.setTextColor(0xFFE50914);
            return;
        }
        sleepTimerEndMs = System.currentTimeMillis() + durationMs;
        if (tvSleepTimerDisplay != null) tvSleepTimerDisplay.setVisibility(View.VISIBLE);
        if (btnSleepTimerView != null) btnSleepTimerView.setTextColor(0xFFE50914);
        mainHandler.post(sleepCountdownUpdater);
    }

    private void cancelSleepTimer() {
        mainHandler.removeCallbacks(sleepCountdownUpdater);
        sleepTimerEndMs = 0;
        if (tvSleepTimerDisplay != null) tvSleepTimerDisplay.setVisibility(View.GONE);
        if (btnSleepTimerView != null) btnSleepTimerView.setTextColor(0xAAFFFFFF);
    }

    // ─── Picture-in-Picture ───────────────────────────────────────────────────

    private void enterPipMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        try {
            PictureInPictureParams params = buildPipParams();
            enterPictureInPictureMode(params);
        } catch (Exception ignored) {}
    }

    private void updatePipActions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !isInPipMode) return;
        try {
            setPictureInPictureParams(buildPipParams());
        } catch (Exception ignored) {}
    }

    private PictureInPictureParams buildPipParams() {
        boolean playing = player != null && player.isPlaying();

        Intent intent = new Intent(PIP_ACTION).putExtra(PIP_EXTRA, CTRL_PLAY_PAUSE);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getBroadcast(this, CTRL_PLAY_PAUSE, intent, flags);

        Icon icon = Icon.createWithResource(this,
                playing ? R.drawable.ic_media_pause : R.drawable.ic_media_play);
        String label = playing ? "Pausar" : "Reproducir";

        RemoteAction action = new RemoteAction(icon, label, label, pi);

        return new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(16, 9))
                .setActions(Collections.singletonList(action))
                .build();
    }

    // ─── Brightness / volume ──────────────────────────────────────────────────

    private float getCurrentBrightnessPercent() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (lp.screenBrightness >= 0) return lp.screenBrightness * 100f;
        try {
            int sys = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            return sys * 100f / 255f;
        } catch (Exception e) { return 50f; }
    }

    private void setBrightness(int percent) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Math.max(0.01f, percent / 100f);
        getWindow().setAttributes(lp);
        showGestureIndicator("🔆", percent + "%");
    }

    // ─── Gesture indicator ────────────────────────────────────────────────────

    private void showGestureIndicator(String icon, String pct) {
        if (gestureIndicator == null) return;
        gestureIndicator.setVisibility(View.VISIBLE);
        if (tvGestureIcon != null) tvGestureIcon.setText(icon);
        if (tvGesturePercent != null) tvGesturePercent.setText(pct);
    }

    private void hideGestureIndicator() {
        if (gestureIndicator != null) gestureIndicator.setVisibility(View.GONE);
    }

    // ─── Resume / progress ────────────────────────────────────────────────────

    private void saveProgress(long posMs) {
        if (videoUrl == null || posMs < 5000) return;
        // Save raw position keyed by URL (used by checkResumePosition)
        prefs.edit().putLong(videoUrl, posMs).apply();
        // Also persist progress in ContinueWatchingManager so the home section
        // and widget always show the correct thumbnail + resume position
        if (watchedItem != null) {
            long duration = (player != null) ? player.getDuration() : 0;
            int pct = (duration > 0) ? (int) ((posMs * 100L) / duration) : 10;
            pct = Math.max(1, Math.min(99, pct));
            ContinueWatchingManager.save(this, watchedItem,
                    watchedSeason, watchedEpisode,
                    pct, posMs,
                    serverUrlForSave != null ? serverUrlForSave : "");
            ContinueWatchingWidget.refresh(this);
        }
    }

    private void checkResumePosition() {
        if (videoUrl == null || resumeCard == null) return;
        // If launched from "Continuar viendo" with an explicit resume position, use it
        long intentMs = getIntent().getLongExtra("resume_ms", 0);
        long savedPos = intentMs > 5000 ? intentMs : prefs.getLong(videoUrl, 0);
        if (savedPos < 5000) return;

        if (tvResumeText != null) tvResumeText.setText("Continuar desde " + formatTime(savedPos));
        resumeCard.setVisibility(View.VISIBLE);

        View btnResume = resumeCard.findViewById(R.id.btnResume);
        View btnStart  = resumeCard.findViewById(R.id.btnFromStart);

        if (btnResume != null) btnResume.setOnClickListener(v -> {
            if (player != null) player.seekTo(savedPos);
            resumeCard.setVisibility(View.GONE);
        });
        if (btnStart != null) btnStart.setOnClickListener(v -> {
            prefs.edit().remove(videoUrl).apply();
            resumeCard.setVisibility(View.GONE);
        });

        mainHandler.postDelayed(() -> resumeCard.setVisibility(View.GONE), 5000);
    }

    // ─── Error ────────────────────────────────────────────────────────────────

    private void showErrorSnackbar() {
        View root = findViewById(android.R.id.content);
        Snackbar sb = Snackbar.make(root,
                "Error de reproducción. ¿Cambiar servidor?",
                Snackbar.LENGTH_INDEFINITE);
        sb.setAction("Cambiar", v -> { setResult(RESULT_RETRY); finish(); });
        sb.setActionTextColor(0xFFE50914);
        sb.show();
    }

    // ─── System UI ────────────────────────────────────────────────────────────

    private void hideSystemBars() {
        WindowInsetsControllerCompat ctrl =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (ctrl != null) {
            ctrl.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            ctrl.hide(WindowInsetsCompat.Type.systemBars());
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static class TrackInfo {
        final int index;
        final String label;
        TrackInfo(int index, String label) { this.index = index; this.label = label; }
    }

    // ─── Cast / TV Streaming ──────────────────────────────────────────────────

    private void setupCast() {
        castManager = CastManager.getInstance(this);

        if (!castManager.isAvailable()) {
            // No Google Play Services — hide cast button
            if (btnCast != null) btnCast.setVisibility(View.GONE);
            return;
        }

        castSessionListener = new SessionManagerListener<CastSession>() {
            @Override public void onSessionStarting(CastSession s)              {}
            @Override public void onSessionStarted(CastSession s, String id)    { onCastSessionStarted(s); }
            @Override public void onSessionStartFailed(CastSession s, int e) {
                showCastError("No se pudo conectar. ¿Reintentar?");
            }
            @Override public void onSessionResuming(CastSession s, String id)   {}
            @Override public void onSessionResumed(CastSession s, boolean b)    { onCastSessionStarted(s); }
            @Override public void onSessionResumeFailed(CastSession s, int e)   {}
            @Override public void onSessionEnding(CastSession s)                {}
            @Override public void onSessionEnded(CastSession s, int e)          { onCastSessionEnded(); }
            @Override public void onSessionSuspended(CastSession s, int e)      { onCastSessionEnded(); }
        };

        // If already casting (e.g. app re-opened), show mini controller
        if (castManager.isCasting()) {
            isCasting = true;
            if (player != null) player.setPlayWhenReady(false);
            if (miniCastController != null)
                miniCastController.show(videoTitle, "Chromecast", posterUrl);
            castManager.castVideo(videoUrl, videoTitle, posterUrl, isM3u8,
                player != null ? player.getCurrentPosition() : 0);
            updateCastButton();
        }

        // Check for available routes and tint button accordingly
        updateCastButton();
    }

    private void showCastPanel() {
        CastBottomSheet sheet = CastBottomSheet.newInstance();
        sheet.setVideoInfo(videoUrl, videoTitle, isM3u8);
        sheet.setCallback(device -> {
            mainHandler.post(() -> handleDeviceSelected(device));
        });
        sheet.show(getSupportFragmentManager(), "castPanel");
    }

    private void handleDeviceSelected(CastDevice device) {
        if (device == null) return;
        long position = player != null ? player.getCurrentPosition() : 0;

        switch (device.getType()) {
            case CHROMECAST:
                // Select route → Cast SDK triggers session → onCastSessionStarted fires
                if (device.getRouteInfo() != null) {
                    try {
                        MediaRouter.getInstance(this).selectRoute(device.getRouteInfo());
                    } catch (Exception e) {
                        showCastError("No se pudo conectar al Chromecast.");
                    }
                }
                break;

            case DLNA:
                if (device.isInUse()) return;
                currentCastDevice = device;
                isCasting = true;
                if (player != null) {
                    saveProgress(player.getCurrentPosition());
                    player.setPlayWhenReady(false);
                }
                if (miniCastController != null)
                    miniCastController.show(videoTitle, device.getName(), posterUrl);
                updateCastButton();

                if (isM3u8) {
                    showCastError("Tu TV puede no soportar HLS (m3u8). Intenta con un servidor mp4 directo.");
                }
                DLNAManager.getInstance().playVideo(device, videoUrl, () -> {
                    isCasting = false;
                    currentCastDevice = null;
                    if (miniCastController != null) miniCastController.hide();
                    updateCastButton();
                    showCastError("No se pudo conectar al TV DLNA. ¿Reintentar?");
                });
                break;

            case AIRPLAY:
                if (device.isInUse()) return;
                currentCastDevice = device;
                isCasting = true;
                if (player != null) {
                    saveProgress(player.getCurrentPosition());
                    player.setPlayWhenReady(false);
                }
                if (miniCastController != null)
                    miniCastController.show(videoTitle, device.getName(), posterUrl);
                updateCastButton();

                AirPlayManager.getInstance().playVideo(device, videoUrl, position, () -> {
                    isCasting = false;
                    currentCastDevice = null;
                    if (miniCastController != null) miniCastController.hide();
                    updateCastButton();
                    showCastError("Apple TV no respondió. Verifica que esté en la misma red WiFi.");
                });
                break;
        }
    }

    private void onCastSessionStarted(CastSession session) {
        isCasting = true;
        long pos = player != null ? player.getCurrentPosition() : 0;
        if (player != null) {
            saveProgress(pos);
            player.setPlayWhenReady(false);
        }
        String deviceName = "Chromecast";
        try { deviceName = session.getCastDevice().getFriendlyName(); } catch (Exception ignored) {}
        final String dName = deviceName;
        mainHandler.post(() -> {
            if (miniCastController != null)
                miniCastController.show(videoTitle, dName, posterUrl);
            updateCastButton();
        });
        castManager.castVideo(videoUrl, videoTitle, posterUrl, isM3u8, pos);
    }

    private void onCastSessionEnded() {
        isCasting = false;
        long resumePos = castManager != null ? castManager.getCastPosition() : 0;
        mainHandler.post(() -> {
            if (miniCastController != null) miniCastController.hide();
            updateCastButton();
            if (player != null) {
                if (resumePos > 0) player.seekTo(resumePos);
                player.setPlayWhenReady(true);
            }
        });
    }

    private void onMiniCastPlayPause() {
        if (!isCasting) return;
        if (currentCastDevice != null) {
            // DLNA or AirPlay
            boolean nowPaused = miniCastController != null && miniCastController.isPlaying();
            if (currentCastDevice.getType() == CastDevice.Type.DLNA)
                DLNAManager.getInstance().togglePause(currentCastDevice, nowPaused);
            else if (currentCastDevice.getType() == CastDevice.Type.AIRPLAY)
                AirPlayManager.getInstance().togglePause(currentCastDevice, nowPaused, null);
        } else if (castManager != null) {
            // Chromecast
            castManager.togglePlayPause();
        }
        if (miniCastController != null)
            miniCastController.setPlaying(!miniCastController.isPlaying());
    }

    private void stopAllCasting(boolean resumeLocal) {
        if (currentCastDevice != null) {
            if (currentCastDevice.getType() == CastDevice.Type.DLNA)
                DLNAManager.getInstance().stopVideo(currentCastDevice);
            else if (currentCastDevice.getType() == CastDevice.Type.AIRPLAY)
                AirPlayManager.getInstance().stopVideo(currentCastDevice);
            currentCastDevice = null;
        }
        if (castManager != null && castManager.isCasting()) {
            castManager.stopCasting();
        }
        isCasting = false;
        if (miniCastController != null) miniCastController.hide();
        updateCastButton();
        if (resumeLocal && player != null) player.setPlayWhenReady(true);
    }

    private void updateCastButton() {
        if (btnCast == null) return;
        if (isCasting) {
            // Blue — actively casting
            btnCast.setColorFilter(0xFF4FC3F7);
        } else {
            // Check if any cast routes are available
            boolean hasRoutes = false;
            if (castManager != null && castManager.isAvailable()) {
                try {
                    String appId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
                    MediaRouteSelector sel = new MediaRouteSelector.Builder()
                        .addControlCategory(CastMediaControlIntent.categoryForCast(appId))
                        .build();
                    for (MediaRouter.RouteInfo r : MediaRouter.getInstance(this).getRoutes()) {
                        if (r.matchesSelector(sel) && !r.isDefault()) { hasRoutes = true; break; }
                    }
                } catch (Exception ignored) {}
            }
            // White = devices available, dim gray = no devices
            btnCast.setColorFilter(hasRoutes ? 0xFFFFFFFF : 0x66FFFFFF);
        }
    }

    private void showCastError(String message) {
        View root = findViewById(android.R.id.content);
        if (root != null) Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}
