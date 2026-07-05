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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String url   = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        item = (ContentItem) getIntent().getSerializableExtra("item");

        setContentView(item != null ? R.layout.activity_player_detail : R.layout.activity_player);

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

        if (item != null) bindDetailPanel();
    }

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
            btnLike.setOnClickListener(v -> {
                FavoritesManager.toggle(this, item);
                updateLikeBtn(btnLike);
            });
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
                        Toast.makeText(this,
                            success ? "Descarga completada \u2713" : "Error al descargar",
                            Toast.LENGTH_SHORT).show();
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
        RecyclerView rv    = row.findViewById(R.id.rowRv);
        if (rv != null) rv.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String sectionTitle = item.getContentType() == ContentItem.TYPE_MOVIE
            ? "Pel\u00edculas similares"
            : item.getContentType() == ContentItem.TYPE_ANIME
            ? "Animes similares"
            : "Series similares";
        if (rowTitle != null) rowTitle.setText(sectionTitle);

        Handler h = new Handler(Looper.getMainLooper());
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            try {
                List<ContentItem> related = TmdbApi.fetchSimilar(item.getTmdbId(), item.getContentType());
                if (related.isEmpty()) {
                    switch (item.getContentType()) {
                        case ContentItem.TYPE_ANIME:  related = TmdbApi.fetchAnime();  break;
                        case ContentItem.TYPE_SERIES: related = TmdbApi.fetchSeries(); break;
                        case ContentItem.TYPE_DORAMA: related = TmdbApi.fetchDoramas(); break;
                        default:                      related = TmdbApi.fetchMovies();  break;
                    }
                }
                final List<ContentItem> finalRelated = related;
                h.post(() -> {
                    if (!isFinishing() && rv != null) {
                        rv.setAdapter(new ContentRowAdapter(this, finalRelated));
                    }
                });
            } catch (Exception ignored) {}
        });
        pool.shutdown();
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
