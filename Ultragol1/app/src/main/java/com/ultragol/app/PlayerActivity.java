package com.ultragol.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ultragol.app.adapters.ContentRowAdapter;
import com.ultragol.app.AirPlayManager;
import com.ultragol.app.CastBottomSheet;
import com.ultragol.app.CastDevice;
import com.ultragol.app.ContinueWatchingManager;
import com.ultragol.app.DLNAManager;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout fullscreenContainer;
    private View webviewContainer, customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private ContentItem item;

    // ── Inline player resize / fullscreen ────────────────────────────────────
    private static final int PLAYER_MIN_H_DP = 160;
    private static final int PLAYER_MAX_H_DP = 400;
    private boolean isPlayerFullscreen = false;
    private ViewGroup playerNormalParent = null;
    private int playerNormalParentIndex  = 0;
    private int playerNormalHeightPx     = 0;

    // ── M3U8 / MP4 interception ───────────────────────────────────────────────
    private volatile String capturedVideoUrl = null;
    private volatile String capturedReferer  = null;
    private volatile boolean capturedIsM3u8  = false;
    private String currentEmbedUrl           = null;
    private String videoTitle                = null;

    // ── Extra flags ───────────────────────────────────────────────────────────
    /** If true: once stream URL is captured, auto-open CastBottomSheet instead of MediaActivity */
    private boolean autoCast   = false;
    /** If true: keep WebView visible (manual quality / source selection) */
    private boolean manualMode = false;

    private static final long MIN_MP4_BYTES  = 1024 * 1024L; // 1 MB hint

    // ── JavaScript extracted from inject string for readability ───────────────
    private static final String JS_EXTRACT =
        "(function(){"
        + "try{"
        // JW Player
        + "if(window.jwplayer&&jwplayer().getPlaylistItem()){"
        +   "var pi=jwplayer().getPlaylistItem();"
        +   "var src=pi.file||(pi.sources&&pi.sources[0]&&pi.sources[0].file);"
        +   "if(src){window.HTMLOUT.onUrl(src,'jwplayer');return;}"
        + "}"
        // Video.js
        + "if(window.videojs&&videojs.players){"
        +   "var keys=Object.keys(videojs.players);"
        +   "for(var i=0;i<keys.length;i++){"
        +     "var p=videojs.players[keys[i]];"
        +     "if(p&&p.currentSrc&&p.currentSrc()){"
        +       "window.HTMLOUT.onUrl(p.currentSrc(),'videojs');return;"
        +     "}"
        +   "}"
        + "}"
        // HLS.js
        + "if(window.Hls&&Hls.instances&&Hls.instances.length>0&&Hls.instances[0].url){"
        +   "window.HTMLOUT.onUrl(Hls.instances[0].url,'hlsjs');return;"
        + "}"
        // DOM <source>
        + "var srcs=document.querySelectorAll('source[src]');"
        + "for(var j=0;j<srcs.length;j++){"
        +   "var s=srcs[j].src;"
        +   "if(s&&(s.includes('.m3u8')||s.includes('.mp4'))){"
        +     "window.HTMLOUT.onUrl(s,'source');return;"
        +   "}"
        + "}"
        // Inline scripts
        + "var scripts=document.querySelectorAll('script');"
        + "for(var k=0;k<scripts.length;k++){"
        +   "var text=scripts[k].innerText;"
        +   "var m=text.match(/[\"'](https?:\\/\\/[^\"']+\\.m3u8[^\"']*)[\"']/);"
        +   "if(m){window.HTMLOUT.onUrl(m[1],'script');return;}"
        +   "var mp=text.match(/[\"'](https?:\\/\\/[^\"']+\\.mp4[^\"']*)[\"']/);"
        +   "if(mp){window.HTMLOUT.onUrl(mp[1],'script_mp4');return;}"
        + "}"
        + "}catch(e){}"
        + "})();";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String url = getIntent().getStringExtra("url");
        videoTitle  = getIntent().getStringExtra("title");
        item        = (ContentItem) getIntent().getSerializableExtra("item");
        autoCast    = getIntent().getBooleanExtra("autocast",    false);
        manualMode  = getIntent().getBooleanExtra("manual_mode", false);

        setContentView(item != null ? R.layout.activity_player_detail : R.layout.activity_player);

        webView             = findViewById(R.id.playerWebView);
        progressBar         = findViewById(R.id.playerProgress);
        fullscreenContainer = findViewById(R.id.fullscreenContainer);
        webviewContainer    = findViewById(R.id.webviewContainer);

        TextView tvTitle = findViewById(R.id.playerTitle);
        if (videoTitle != null && tvTitle != null) tvTitle.setText(videoTitle);
        View btnBack = findViewById(R.id.btnPlayerBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // Desktop UA → el sitio siempre sirve la versión desktop con video directo (sin anuncios de mobile)
        s.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        s.setSupportMultipleWindows(true);

        // ── JavaScript interface to receive URLs extracted from JS ────────────
        webView.addJavascriptInterface(new HtmlOutInterface(), "HTMLOUT");

        webView.setWebViewClient(new WebViewClient() {

            // ── Intercept every network request from the WebView ──────────────
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String reqUrl = request.getUrl().toString();
                String ref    = request.getRequestHeaders().get("Referer");

                boolean isM3u8 = reqUrl.contains(".m3u8");
                boolean isMp4  = reqUrl.contains(".mp4") || reqUrl.contains(".MP4");

                if ((isM3u8 || isMp4) && capturedVideoUrl == null) {
                    // For MP4 try to check Content-Length header via a HEAD-like approach:
                    // We trust the URL pattern — if it ends with .mp4 we capture it.
                    // Large-size check is not possible without a blocking network call,
                    // so we accept any mp4 URL and filter in the player if needed.
                    capturedVideoUrl = reqUrl;
                    capturedReferer  = ref != null ? ref : currentEmbedUrl;
                    capturedIsM3u8   = isM3u8;

                    new Handler(Looper.getMainLooper()).post(() ->
                            onVideoUrlCaptured(capturedVideoUrl, capturedReferer, capturedIsM3u8));
                }

                return null; // never block the request
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String scheme = r.getUrl().getScheme();
                if ("http".equals(scheme) || "https".equals(scheme)) {
                    v.loadUrl(r.getUrl().toString());
                    return true;
                }
                return true;
            }

            @Override public void onPageStarted(WebView v, String u, Bitmap f) {
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                // Forzar viewport desktop antes de que el sitio lo sobreescriba
                v.evaluateJavascript(
                    "javascript:(function(){" +
                    "var m=document.querySelector('meta[name=viewport]');" +
                    "if(!m){m=document.createElement('meta');m.name='viewport';" +
                    "document.head&&document.head.appendChild(m);}" +
                    "if(m)m.content='width=1280,initial-scale=1';" +
                    "})();", null);
            }

            @Override public void onPageFinished(WebView v, String u) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Intento 1: inmediato
                if (capturedVideoUrl == null) {
                    v.evaluateJavascript("javascript:" + JS_EXTRACT, null);
                }
                // Intento 2: a los 2 s (algunos players cargan el src tarde)
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (capturedVideoUrl == null && !isFinishing()) {
                        v.evaluateJavascript("javascript:" + JS_EXTRACT, null);
                    }
                }, 2000);
                // Intento 3: a los 5 s (para players lentos o con anuncio previo)
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (capturedVideoUrl == null && !isFinishing()) {
                        v.evaluateJavascript("javascript:" + JS_EXTRACT, null);
                    }
                }, 5000);
            }
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
                if (progressBar != null) {
                    progressBar.setProgress(p);
                    progressBar.setVisibility(p == 100 ? View.GONE : View.VISIBLE);
                }
            }
            @Override public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                WebView popup = new WebView(PlayerActivity.this);
                popup.getSettings().setJavaScriptEnabled(true);
                popup.setWebViewClient(new WebViewClient() {
                    @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                        webView.loadUrl(r.getUrl().toString());
                        return true;
                    }
                });
                ((WebView.WebViewTransport) resultMsg.obj).setWebView(popup);
                resultMsg.sendToTarget();
                return true;
            }
        });

        // ── Cast button (simple player layout) — always visible ──────────────
        View castContainer = findViewById(R.id.btnCastContainer);
        if (castContainer != null) {
            // alpha=0.45 set in XML (searching state); tap always works
            castContainer.setOnClickListener(v -> showCastOptions());
            if (autoCast) {
                TextView waitLbl = findViewById(R.id.btnPlayerCastWaiting);
                if (waitLbl != null) waitLbl.setText("preparando…");
            }
        }

        // ── Fullscreen button ─────────────────────────────────────────────────
        View btnFullscreen = findViewById(R.id.btnPlayerFullscreen);
        if (btnFullscreen != null) {
            btnFullscreen.setOnClickListener(v -> togglePlayerFullscreen());
        }

        // ── Resize handle drag ────────────────────────────────────────────────
        setupResizeHandle();

        if (url != null) {
            currentEmbedUrl = url;
            webView.loadUrl(url);
        }

        if (item != null) bindDetailPanel();

        // ── Save to "Continuar Viendo" history when player opens ─────────────
        if (item != null) {
            // Save with the server URL so "Continuar viendo" can re-open the same source
            ContinueWatchingManager.save(this, item, 1, 1, 10, 0,
                    url != null ? url : "");
        }
    }

    // ── Resize handle ─────────────────────────────────────────────────────────
    private void setupResizeHandle() {
        View resizeHandle = webviewContainer != null ? webviewContainer.findViewById(R.id.playerResizeHandle) : null;
        if (resizeHandle == null) return;
        final int minH = dpToPx(PLAYER_MIN_H_DP);
        final int maxH = dpToPx(PLAYER_MAX_H_DP);
        final int[] startY = {0};
        final int[] startH = {0};
        resizeHandle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY[0] = (int) event.getRawY();
                    startH[0] = webviewContainer.getHeight();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int dy  = (int) event.getRawY() - startY[0];
                    int newH = Math.max(minH, Math.min(maxH, startH[0] + dy));
                    ViewGroup.LayoutParams lp = webviewContainer.getLayoutParams();
                    lp.height = newH;
                    webviewContainer.setLayoutParams(lp);
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
            }
            return false;
        });
    }

    // ── Fullscreen toggle ─────────────────────────────────────────────────────
    private void togglePlayerFullscreen() {
        if (isPlayerFullscreen) {
            exitPlayerFullscreen();
        } else {
            enterPlayerFullscreen();
        }
    }

    private void enterPlayerFullscreen() {
        if (webviewContainer == null || fullscreenContainer == null) return;
        // Remember normal position
        playerNormalParent      = (ViewGroup) webviewContainer.getParent();
        playerNormalParentIndex = playerNormalParent.indexOfChild(webviewContainer);
        playerNormalHeightPx    = webviewContainer.getHeight();
        // Move to fullscreen container
        playerNormalParent.removeView(webviewContainer);
        FrameLayout.LayoutParams fslp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webviewContainer.setLayoutParams(fslp);
        fullscreenContainer.addView(webviewContainer);
        fullscreenContainer.setVisibility(View.VISIBLE);
        isPlayerFullscreen = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideSystemUI();
        updateFullscreenIcon(true);
    }

    private void exitPlayerFullscreen() {
        if (webviewContainer == null || fullscreenContainer == null || playerNormalParent == null) return;
        fullscreenContainer.removeView(webviewContainer);
        fullscreenContainer.setVisibility(View.GONE);
        int restoreH = playerNormalHeightPx > 0 ? playerNormalHeightPx : dpToPx(212);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, restoreH);
        webviewContainer.setLayoutParams(lp);
        playerNormalParent.addView(webviewContainer, playerNormalParentIndex);
        isPlayerFullscreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        updateFullscreenIcon(false);
    }

    private void updateFullscreenIcon(boolean fullscreen) {
        if (webviewContainer == null) return;
        ImageView btn = webviewContainer.findViewById(R.id.btnPlayerFullscreen);
        if (btn != null) {
            btn.setImageResource(fullscreen
                ? R.drawable.ic_media_fullscreen_exit
                : R.drawable.ic_media_fullscreen);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ── Called when a video URL is captured (from intercept or JS) ────────────
    private void onVideoUrlCaptured(String url, String referer, boolean isM3u8) {
        // ── Hide intercepting overlay, show STREAM badge ──────────────────────
        if (webviewContainer != null) {
            View overlay = webviewContainer.findViewById(R.id.interceptingOverlay);
            if (overlay != null) overlay.setVisibility(View.GONE);
            TextView badge = webviewContainer.findViewById(R.id.playerStreamBadge);
            if (badge != null) {
                badge.setVisibility(View.VISIBLE);
                badge.animate().alpha(1f).setDuration(300).start();
            }
        }
        // ── Channel player: highlight 📡 button ──────────────────────────────
        View castContainer = findViewById(R.id.btnCastContainer);
        if (castContainer != null) {
            castContainer.animate().alpha(1f).setDuration(300).start();
            TextView waitLbl = findViewById(R.id.btnPlayerCastWaiting);
            if (waitLbl != null) waitLbl.setText("listo ·");
        }
        // ── Movie detail panel: mark Transmitir as ready ──────────────────────
        View btnCastDetail = findViewById(R.id.pdBtnCast);
        if (btnCastDetail != null) updateDetailCastBtn(btnCastDetail, true);

        if (manualMode) {
            // Stay in WebView so user can choose quality/source manually
            Toast.makeText(this,
                "Stream capturado ✓  Usa los controles del reproductor para elegir calidad",
                Toast.LENGTH_LONG).show();
            return;
        }

        if (autoCast) {
            // Auto-open cast selector instead of launching MediaActivity
            Toast.makeText(this, "Stream listo — elige dispositivo para transmitir", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(this::showCastOptions, 300);
            return;
        }

        // Default: launch native ExoPlayer
        Intent intent = new Intent(this, MediaActivity.class);
        intent.putExtra("url",        url);
        intent.putExtra("title",      videoTitle != null ? videoTitle : "");
        intent.putExtra("referer",    referer != null ? referer : "");
        intent.putExtra("is_m3u8",   isM3u8);
        // Pass item and server so MediaActivity can update ContinueWatchingManager
        if (item != null) intent.putExtra("item", item);
        intent.putExtra("server_url", currentEmbedUrl != null ? currentEmbedUrl : "");
        intent.putExtra("poster_url", item != null ? item.getPosterUrl() : "");
        // Forward resume position (e.g. coming from widget or ContinueWatchingAdapter)
        long resumeMsFromIntent = getIntent().getLongExtra("resume_ms", 0);
        if (resumeMsFromIntent > 0) intent.putExtra("resume_ms", resumeMsFromIntent);
        startActivityForResult(intent, MediaActivity.REQUEST_CODE);
    }

    // ── Update the "Transmitir" button appearance in the detail panel ─────────
    private void updateDetailCastBtn(View btn, boolean ready) {
        if (!(btn instanceof android.view.ViewGroup)) return;
        android.view.ViewGroup vg = (android.view.ViewGroup) btn;
        // child 0 = ImageView (icon), child 1 = TextView (label)
        for (int i = 0; i < vg.getChildCount(); i++) {
            android.view.View child = vg.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setText(ready ? "Transmitir ✓" : "Buscando…");
                ((TextView) child).setTextColor(ready ? 0xFF4FC3F7 : 0xAAFFFFFF);
            }
            if (child instanceof android.widget.ImageView) {
                ((android.widget.ImageView) child).setAlpha(ready ? 1f : 0.5f);
            }
        }
        btn.setAlpha(ready ? 1f : 0.6f);
    }

    // ── Cast options sheet (DLNA / AirPlay / Chromecast) ─────────────────────
    private void showCastOptions() {
        if (capturedVideoUrl == null || capturedVideoUrl.isEmpty()) {
            Toast.makeText(this,
                "Espera mientras se captura el stream…", Toast.LENGTH_SHORT).show();
            return;
        }
        final String castUrl   = capturedVideoUrl;
        final String castTitle = videoTitle != null ? videoTitle : "";
        final boolean castM3u8 = capturedIsM3u8;

        CastBottomSheet sheet = CastBottomSheet.newInstance();
        sheet.setVideoInfo(castUrl, castTitle, castM3u8);
        sheet.setCallback(device -> {
            if (device == null) return;
            switch (device.getType()) {
                case DLNA:
                    Toast.makeText(this, "Conectando a " + device.getName() + "…", Toast.LENGTH_SHORT).show();
                    DLNAManager.getInstance().playVideo(device, castUrl, () ->
                        runOnUiThread(() -> Toast.makeText(this,
                            "No se pudo conectar al TV DLNA. ¿Estás en la misma red WiFi?",
                            Toast.LENGTH_LONG).show()));
                    break;
                case AIRPLAY:
                    Toast.makeText(this, "Conectando a " + device.getName() + "…", Toast.LENGTH_SHORT).show();
                    AirPlayManager.getInstance().playVideo(device, castUrl, 0, () ->
                        runOnUiThread(() -> Toast.makeText(this,
                            "Apple TV no respondió. Verifica que esté en la misma red WiFi.",
                            Toast.LENGTH_LONG).show()));
                    break;
                case CHROMECAST:
                    Intent castIntent = new Intent(this, MediaActivity.class);
                    castIntent.putExtra("url",     castUrl);
                    castIntent.putExtra("title",   castTitle);
                    castIntent.putExtra("referer", capturedReferer != null ? capturedReferer : "");
                    castIntent.putExtra("is_m3u8", castM3u8);
                    startActivityForResult(castIntent, MediaActivity.REQUEST_CODE);
                    Toast.makeText(this, "Abriendo para Chromecast…", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        sheet.show(getSupportFragmentManager(), "castSheet");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MediaActivity.REQUEST_CODE && resultCode == MediaActivity.RESULT_RETRY) {
            // User wants to try another server — reset so next embed can be captured
            capturedVideoUrl = null;
            capturedReferer  = null;
            Toast.makeText(this, "Selecciona otro servidor", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Detail panel ──────────────────────────────────────────────────────────

    private void bindDetailPanel() {
        TextView pdTitle    = findViewById(R.id.pdTitle);
        TextView pdMeta     = findViewById(R.id.pdMeta);
        TextView pdOverview = findViewById(R.id.pdOverview);

        if (pdTitle != null) pdTitle.setText(item.getTitle());
        if (pdOverview != null) pdOverview.setText(item.getOverview());
        if (pdMeta != null) {
            StringBuilder sb = new StringBuilder();
            if (!item.getYear().isEmpty()) sb.append(item.getYear());
            if (!item.getGenre().isEmpty()) {
                if (sb.length() > 0) sb.append("   \u00B7   ");
                sb.append(item.getGenre());
            }
            if (!item.getRating().isEmpty()) {
                if (sb.length() > 0) sb.append("   \u00B7   ");
                sb.append("\u2605 ").append(item.getRating());
            }
            pdMeta.setText(sb.toString());
        }

        LinearLayout btnLike = findViewById(R.id.pdBtnLike);
        if (btnLike != null) {
            updateLikeBtn(btnLike);
            btnLike.setOnClickListener(v -> { FavoritesManager.toggle(this, item); updateLikeBtn(btnLike); });
        }

        LinearLayout btnDownload = findViewById(R.id.pdBtnDownload);
        if (btnDownload != null) {
            updateDownloadBtn(btnDownload);
            btnDownload.setOnClickListener(v -> {
                if (DownloadsManager.isDownloaded(this, item)) {
                    DownloadsManager.remove(this, item);
                    updateDownloadBtn(btnDownload);
                    Toast.makeText(this, "Descarga eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    DownloadsManager.add(this, item, success -> {
                        updateDownloadBtn(btnDownload);
                        Toast.makeText(this, success ? "Descarga completada \u2713" : "Error al descargar", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }

        View btnCastDetail = findViewById(R.id.pdBtnCast);
        if (btnCastDetail != null) {
            // Show ready state if URL was already captured (e.g. returning from MediaActivity)
            updateDetailCastBtn(btnCastDetail, capturedVideoUrl != null && !capturedVideoUrl.isEmpty());

            btnCastDetail.setOnClickListener(v -> {
                String castUrl = capturedVideoUrl != null ? capturedVideoUrl : "";
                if (castUrl.isEmpty()) {
                    // Stream not captured yet → activate autocast and wait for WebView intercept
                    autoCast   = true;
                    manualMode = false;
                    updateDetailCastBtn(btnCastDetail, false);
                    Toast.makeText(this,
                        "Buscando stream… el selector aparecerá automáticamente al detectarlo",
                        Toast.LENGTH_LONG).show();
                    return;
                }
                // URL ready → show cast sheet immediately
                showCastOptions();
            });
        }

        View btnShare = findViewById(R.id.pdBtnShare);
        if (btnShare != null) btnShare.setOnClickListener(v -> {
            ServerSelectDialog.show(this, item, 1, 1);
        });

        loadSimilar();
    }

    private void updateLikeBtn(LinearLayout btn) {
        boolean isFav = FavoritesManager.isFav(this, item);
        TextView icon  = findViewById(R.id.pdLikeIcon);
        TextView label = findViewById(R.id.pdLikeLabel);
        if (icon != null) {
            icon.setText(isFav ? "\u2665" : "\u2661");
            icon.setTextColor(isFav ? android.graphics.Color.parseColor("#FF5252") : android.graphics.Color.WHITE);
        }
        if (label != null) {
            label.setText(isFav ? "Te gusta" : "Me gusta");
            label.setTextColor(isFav ? android.graphics.Color.parseColor("#FF5252") : android.graphics.Color.WHITE);
        }
    }

    private void updateDownloadBtn(LinearLayout btn) {
        boolean downloaded = DownloadsManager.isDownloaded(this, item);
        TextView icon  = findViewById(R.id.pdDownloadIcon);
        TextView label = findViewById(R.id.pdDownloadLabel);
        if (icon != null) {
            icon.setText(downloaded ? "\u2713" : "\u2b07");
            icon.setTextColor(downloaded ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#4FC3F7"));
        }
        if (label != null) {
            label.setText(downloaded ? "Descargado" : "Descargar");
            label.setTextColor(downloaded ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#4FC3F7"));
        }
    }

    private void loadSimilar() {
        View row = findViewById(R.id.pdRowSimilar);
        if (row == null) return;
        TextView rowTitle = row.findViewById(R.id.rowTitle);
        RecyclerView rv   = row.findViewById(R.id.rowRv);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        String sectionTitle = item.getContentType() == ContentItem.TYPE_MOVIE ? "Pel\u00edculas similares"
                : item.getContentType() == ContentItem.TYPE_ANIME ? "Animes similares" : "Series similares";
        if (rowTitle != null) rowTitle.setText(sectionTitle);
        Handler h = new Handler(Looper.getMainLooper());
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            try {
                List<ContentItem> related = TmdbApi.fetchSimilar(item.getTmdbId(), item.getContentType());
                if (related.isEmpty()) {
                    switch (item.getContentType()) {
                        case ContentItem.TYPE_ANIME:  related = TmdbApi.fetchAnime(); break;
                        case ContentItem.TYPE_SERIES: related = TmdbApi.fetchSeries(); break;
                        case ContentItem.TYPE_DORAMA: related = TmdbApi.fetchDoramas(); break;
                        default:                      related = TmdbApi.fetchMovies(); break;
                    }
                }
                final List<ContentItem> fin = related;
                h.post(() -> { if (!isFinishing() && rv != null) rv.setAdapter(new ContentRowAdapter(this, fin)); });
            } catch (Exception ignored) {}
        });
        pool.shutdown();
    }

    // ── System UI ─────────────────────────────────────────────────────────────

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override public void onBackPressed() {
        if (isPlayerFullscreen) { exitPlayerFullscreen(); return; }
        if (customView != null) { webView.getWebChromeClient().onHideCustomView(); return; }
        if (webView.canGoBack()) { webView.goBack(); return; }
        super.onBackPressed();
    }
    @Override protected void onPause()   { super.onPause();   webView.onPause(); }
    @Override protected void onResume()  { super.onResume();  webView.onResume(); }
    @Override protected void onDestroy() { webView.destroy(); super.onDestroy(); }

    // ── Inner class: receives URLs from injected JavaScript ──────────────────

    private final class HtmlOutInterface {
        @JavascriptInterface
        public void onUrl(final String url, final String source) {
            if (url == null || url.isEmpty() || capturedVideoUrl != null) return;
            boolean isM3u8 = url.contains(".m3u8") || url.contains("m3u8");
            boolean isMp4  = url.contains(".mp4");
            if (!isM3u8 && !isMp4) return;

            capturedVideoUrl = url;
            capturedReferer  = currentEmbedUrl;
            capturedIsM3u8   = isM3u8;

            new Handler(Looper.getMainLooper()).post(() ->
                    onVideoUrlCaptured(url, currentEmbedUrl, isM3u8));
        }
    }
}
