package com.ultragol.app;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Reproductor vertical (portrait) de Shorts Dramas.
 * Usa un WebView para cargar el embed de Dailymotion a pantalla completa
 * en modo retrato — el propio player de Dailymotion gestiona el stream,
 * evitando el problema de tokens firmados que bloquea a ExoPlayer.
 */
public class ShortsPlayerActivity extends AppCompatActivity {

    private WebView     webView;
    private View        topBar;
    private ProgressBar loading;
    private boolean     barVisible = true;
    private final Handler handler = new Handler();
    private final Runnable hideBar = () -> {
        if (topBar != null) topBar.animate().alpha(0f).setDuration(400)
                .withEndAction(() -> { if (topBar != null) topBar.setVisibility(View.GONE); })
                .start();
        barVisible = false;
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Portrait, keep screen on, full-screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_shorts_player);

        // Hide system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat ctrl =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        ctrl.hide(WindowInsetsCompat.Type.systemBars());
        ctrl.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        String videoId = getIntent().getStringExtra("video_id");
        String title   = getIntent().getStringExtra("title");

        webView  = findViewById(R.id.shortsWebView);
        topBar   = findViewById(R.id.shortsTopBar);
        loading  = findViewById(R.id.shortsLoading);

        // Back button
        View btnBack = findViewById(R.id.shortsBtnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Title
        TextView tvTitle = findViewById(R.id.shortsTitle);
        if (tvTitle != null && title != null) tvTitle.setText(title);

        // Hide bar after 3 s; show on tap
        scheduleHideBar();
        if (webView != null) {
            webView.setOnClickListener(v -> toggleBar());
        }

        // Configure WebView
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // Mobile UA so Dailymotion serves the vertical/mobile embed layout
        s.setUserAgentString(
            "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        s.setSupportMultipleWindows(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView v, String url, android.graphics.Bitmap fav) {
                if (loading != null) loading.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView v, String url) {
                if (loading != null) loading.setVisibility(View.GONE);
                // Inject CSS to make the video fill portrait screen and auto-play
                v.evaluateJavascript(
                    "(function(){" +
                    "  var s=document.createElement('style');" +
                    "  s.textContent='body,html{margin:0;padding:0;background:#000;overflow:hidden;}'" +
                    "    +'video{width:100vw!important;height:100vh!important;object-fit:cover!important;}';" +
                    "  document.head&&document.head.appendChild(s);" +
                    "  var v=document.querySelector('video');" +
                    "  if(v){v.play();}" +
                    "})();", null);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String scheme = r.getUrl().getScheme();
                return !("http".equals(scheme) || "https".equals(scheme));
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onShowCustomView(View view, CustomViewCallback cb) {
                // Dailymotion tries to go fullscreen — let it use the whole window
                webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });

        // Build embed URL — mobile parameters for portrait layout
        if (videoId != null && !videoId.isEmpty()) {
            String embedUrl = "https://www.dailymotion.com/embed/video/" + videoId
                    + "?autoplay=1"
                    + "&ui-logo=false"
                    + "&ui-start-screen-info=false"
                    + "&sharing-enable=false"
                    + "&queue-enable=false"
                    + "&endscreen-enable=false";
            webView.loadUrl(embedUrl);
        } else {
            finish();
        }
    }

    private void toggleBar() {
        handler.removeCallbacks(hideBar);
        if (barVisible) {
            hideBar.run();
        } else {
            topBar.setVisibility(View.VISIBLE);
            topBar.animate().alpha(1f).setDuration(300).start();
            barVisible = true;
            scheduleHideBar();
        }
    }

    private void scheduleHideBar() {
        handler.removeCallbacks(hideBar);
        handler.postDelayed(hideBar, 3000);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
