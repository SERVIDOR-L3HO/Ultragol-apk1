package com.ultragol.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ultragol.app.adapters.ContentRowAdapter;
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

    // ── M3U8 / MP4 interception ───────────────────────────────────────────────
    private volatile String capturedVideoUrl = null;
    private volatile String capturedReferer  = null;
    private volatile boolean capturedIsM3u8  = false;
    private String currentEmbedUrl           = null;
    private String videoTitle                = null;

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
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 12; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36");
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
            }

            @Override public void onPageFinished(WebView v, String u) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // If shouldInterceptRequest didn't fire (URL hidden in JS),
                // try to extract via JavaScript injection.
                if (capturedVideoUrl == null) {
                    v.evaluateJavascript("javascript:" + JS_EXTRACT, null);
                }
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

        if (url != null) {
            currentEmbedUrl = url;
            webView.loadUrl(url);
        }

        if (item != null) bindDetailPanel();
    }

    // ── Called when a video URL is captured (from intercept or JS) ────────────
    private void onVideoUrlCaptured(String url, String referer, boolean isM3u8) {
        // Launch the native ExoPlayer
        Intent intent = new Intent(this, MediaActivity.class);
        intent.putExtra("url",      url);
        intent.putExtra("title",    videoTitle != null ? videoTitle : "");
        intent.putExtra("referer",  referer != null ? referer : "");
        intent.putExtra("is_m3u8",  isM3u8);
        startActivityForResult(intent, MediaActivity.REQUEST_CODE);
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

        View btnReport = findViewById(R.id.pdBtnReport);
        if (btnReport != null) btnReport.setOnClickListener(v ->
            Toast.makeText(this, "Gracias por tu reporte, lo revisaremos pronto", Toast.LENGTH_SHORT).show());

        View btnShare = findViewById(R.id.pdBtnShare);
        if (btnShare != null) btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Mira \"" + item.getTitle() + "\" en Ultragol");
            startActivity(Intent.createChooser(share, "Compartir"));
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
        if (customView != null) webView.getWebChromeClient().onHideCustomView();
        else if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
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
