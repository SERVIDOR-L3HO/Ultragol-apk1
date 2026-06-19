package com.ultragol.app;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout fullscreenContainer;
    private View webviewContainer, customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player);

        String url   = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        webView             = findViewById(R.id.playerWebView);
        progressBar         = findViewById(R.id.playerProgress);
        fullscreenContainer = findViewById(R.id.fullscreenContainer);
        webviewContainer    = findViewById(R.id.webviewContainer);

        TextView tvTitle = findViewById(R.id.playerTitle);
        if (title != null && tvTitle != null) tvTitle.setText(title);
        View btnBack = findViewById(R.id.btnPlayerBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true); s.setUseWideViewPort(true);
        s.setSupportZoom(false); s.setBuiltInZoomControls(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 12; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36");
        s.setSupportMultipleWindows(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String scheme = r.getUrl().getScheme();
                if ("http".equals(scheme) || "https".equals(scheme)) { v.loadUrl(r.getUrl().toString()); return true; }
                return true;
            }
            @Override public void onPageStarted(WebView v, String u, Bitmap f) { if (progressBar != null) progressBar.setVisibility(View.VISIBLE); }
            @Override public void onPageFinished(WebView v, String u) { if (progressBar != null) progressBar.setVisibility(View.GONE); }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onShowCustomView(View view, CustomViewCallback cb) {
                if (customView != null) { cb.onCustomViewHidden(); return; }
                customView = view; customViewCallback = cb;
                webviewContainer.setVisibility(View.GONE);
                fullscreenContainer.setVisibility(View.VISIBLE);
                fullscreenContainer.addView(view);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                hideSystemUI();
            }
            @Override public void onHideCustomView() {
                if (customView == null) return;
                fullscreenContainer.removeView(customView); customView = null;
                fullscreenContainer.setVisibility(View.GONE);
                webviewContainer.setVisibility(View.VISIBLE);
                customViewCallback.onCustomViewHidden();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            @Override public void onProgressChanged(WebView v, int p) {
                if (progressBar != null) { progressBar.setProgress(p); progressBar.setVisibility(p == 100 ? View.GONE : View.VISIBLE); }
            }
            @Override public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                WebView popup = new WebView(PlayerActivity.this);
                popup.getSettings().setJavaScriptEnabled(true);
                popup.setWebViewClient(new WebViewClient() {
                    @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) { webView.loadUrl(r.getUrl().toString()); return true; }
                });
                ((WebView.WebViewTransport) resultMsg.obj).setWebView(popup);
                resultMsg.sendToTarget(); return true;
            }
        });

        if (url != null) webView.loadUrl(url);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override public void onBackPressed() {
        if (customView != null) webView.getWebChromeClient().onHideCustomView();
        else if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
    @Override protected void onPause()   { super.onPause();   webView.onPause(); }
    @Override protected void onResume()  { super.onResume();  webView.onResume(); }
    @Override protected void onDestroy() { webView.destroy(); super.onDestroy(); }
}
