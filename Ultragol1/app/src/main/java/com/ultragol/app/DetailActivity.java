package com.ultragol.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.adapters.ContentRowAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.StreamingApi;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DetailActivity extends AppCompatActivity {

    private ContentItem item;
    private int currentSeason = 1;
    private int totalSeasons  = 1;

    // Loading-servers overlay
    private final Handler loadingHandler = new Handler(Looper.getMainLooper());
    private final List<Animator> dotAnimators = new ArrayList<>();

    // Trailer
    private String trailerKey = "";
    private WebView backdropWebView;
    private boolean backdropMuted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        item = (ContentItem) getIntent().getSerializableExtra("item");
        if (item == null) { finish(); return; }

        backdropWebView = findViewById(R.id.trailerBackdropWebView);
        bindViews();
        loadRelated();
        loadTrailer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backdropWebView != null) backdropWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backdropWebView != null) backdropWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backdropWebView != null) {
            backdropWebView.stopLoading();
            backdropWebView.destroy();
            backdropWebView = null;
        }
        loadingHandler.removeCallbacksAndMessages(null);
    }

    private void bindViews() {
        ImageView backdrop     = findViewById(R.id.detailBackdrop);
        ImageView poster       = findViewById(R.id.detailPoster);
        TextView  typeBadge    = findViewById(R.id.detailTypeBadge);
        TextView  ratingBadge  = findViewById(R.id.detailBadge);
        TextView  title        = findViewById(R.id.detailTitle);
        TextView  meta         = findViewById(R.id.detailMeta);
        TextView  rating       = findViewById(R.id.detailRating);
        TextView  overview     = findViewById(R.id.detailOverview);
        LinearLayout genreChips = findViewById(R.id.genreChips);
        View btnPlay           = findViewById(R.id.btnPlay);
        View btnBack           = findViewById(R.id.btnDetailBack);
        LinearLayout btnFavorite = findViewById(R.id.btnFavorite);
        LinearLayout btnMyList   = findViewById(R.id.btnMyList);

        // ── Title ──────────────────────────────────────────────────────────────
        if (title != null) title.setText(item.getTitle());

        // ── Type badge (PELÍCULA / SERIE / ANIME / DORAMA) ────────────────────
        if (typeBadge != null) {
            String label;
            int badgeColor;
            switch (item.getContentType()) {
                case ContentItem.TYPE_SERIES:
                    label = "SERIE";    badgeColor = Color.parseColor("#00838F"); break;
                case ContentItem.TYPE_ANIME:
                    label = "ANIME";    badgeColor = Color.parseColor("#AD1457"); break;
                case ContentItem.TYPE_DORAMA:
                    label = "DORAMA";   badgeColor = Color.parseColor("#00695C"); break;
                case ContentItem.TYPE_SPORT:
                    label = "EN VIVO";  badgeColor = Color.parseColor("#C62828"); break;
                case ContentItem.TYPE_TV:
                    label = "TV";       badgeColor = Color.parseColor("#2E7D32"); break;
                default:
                    label = "PELÍCULA"; badgeColor = Color.parseColor("#CC1111"); break;
            }
            typeBadge.setText(label);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setColor(badgeColor);
            bg.setCornerRadius(dp(5));
            typeBadge.setBackground(bg);
        }

        // ── Rating badge (C, B, A etc.) ────────────────────────────────────────
        if (ratingBadge != null) ratingBadge.setText(item.getBadge());

        // ── Meta: "2026  1h 25m  ES" ───────────────────────────────────────────
        if (meta != null) {
            StringBuilder sb = new StringBuilder();
            if (!item.getYear().isEmpty()) sb.append(item.getYear());
            if (!item.getRating().isEmpty()) {
                if (sb.length() > 0) sb.append("   \u00B7   ");
                sb.append(item.getRating());
            }
            sb.append("   \u00B7   ES");
            meta.setText(sb.toString());
        }

        // ── ◆ Rating number ────────────────────────────────────────────────────
        if (rating != null) {
            String r = item.getRating();
            rating.setText(r.isEmpty() ? "—" : r);
        }

        // ── Genre chips ────────────────────────────────────────────────────────
        if (genreChips != null && !item.getGenre().isEmpty()) {
            String[] genres = item.getGenre().split("[,/•]");
            for (String g : genres) {
                String lbl = g.trim();
                if (lbl.isEmpty()) continue;
                TextView chip = new TextView(this);
                chip.setText(lbl);
                chip.setTextColor(0xCCFFFFFF);
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                GradientDrawable chipBg = new GradientDrawable();
                chipBg.setShape(GradientDrawable.RECTANGLE);
                chipBg.setColor(0x00000000);
                chipBg.setStroke(dp(1), 0x55FFFFFF);
                chipBg.setCornerRadius(dp(20));
                chip.setBackground(chipBg);
                int padH = dp(14), padV = dp(6);
                chip.setPadding(padH, padV, padH, padV);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMarginEnd(dp(8));
                chip.setLayoutParams(lp);
                genreChips.addView(chip);
            }
        }

        // ── Overview ───────────────────────────────────────────────────────────
        if (overview != null) overview.setText(item.getOverview());

        // ── Images ─────────────────────────────────────────────────────────────
        if (backdrop != null && !item.getBackdropUrl().isEmpty()) {
            Glide.with(this).load(item.getBackdropUrl())
                .transition(DrawableTransitionOptions.withCrossFade()).into(backdrop);
        }
        if (poster != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(this).load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade()).into(poster);
        }

        // ── Buttons ─────────────────────────────────────────────────────────────
        if (btnBack != null) btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        });
        // ── REPRODUCIR: pulse + shimmer animation ────────────────────────────
        startReproducirAnimations(btnPlay);

        if (btnPlay != null) btnPlay.setOnClickListener(v -> {
            fetchAndPlay(item, 1, 1);
        });

        // ── Ver Tráiler button ────────────────────────────────────────────────
        View btnTrailer = findViewById(R.id.btnTrailer);
        if (btnTrailer != null) btnTrailer.setOnClickListener(v -> {
            if (!trailerKey.isEmpty()) showTrailerModal(trailerKey);
        });

        // ── Muted badge toggle (tap to unmute/mute backdrop) ─────────────────
        View mutedBadge = findViewById(R.id.trailerMutedBadge);
        TextView tvMutedIcon = findViewById(R.id.tvMutedIcon);
        if (mutedBadge != null) mutedBadge.setOnClickListener(v -> {
            backdropMuted = !backdropMuted;
            String js = backdropMuted
                ? "document.getElementById('yt').contentWindow.postMessage('{\"event\":\"command\",\"func\":\"mute\",\"args\":\"\"}','*')"
                : "document.getElementById('yt').contentWindow.postMessage('{\"event\":\"command\",\"func\":\"unMute\",\"args\":\"\"}','*')";
            if (backdropWebView != null) backdropWebView.evaluateJavascript(js, null);
            if (tvMutedIcon != null) tvMutedIcon.setText(backdropMuted ? "🔇" : "🔊");
        });

        // ── Favorito button ───────────────────────────────────────────────────
        if (btnFavorite != null) {
            updateFavoriteBtn(btnFavorite);
            btnFavorite.setOnClickListener(v -> {
                FavoritesManager.toggle(this, item);
                updateFavoriteBtn(btnFavorite);
                boolean isFav = FavoritesManager.isFav(this, item);
                Toast.makeText(this,
                    isFav ? "Agregado a Favoritos" : "Eliminado de Favoritos",
                    Toast.LENGTH_SHORT).show();
            });
        }

        // ── Mi Lista button ───────────────────────────────────────────────────
        if (btnMyList != null) {
            updateMyListBtn(btnMyList);
            btnMyList.setOnClickListener(v -> {
                MyListManager.toggle(this, item);
                updateMyListBtn(btnMyList);
                boolean inList = MyListManager.isInList(this, item);
                Toast.makeText(this,
                    inList ? "Agregado a Mi Lista" : "Eliminado de Mi Lista",
                    Toast.LENGTH_SHORT).show();
            });
        }

        setupEpisodeSection();
    }

    private void updateFavoriteBtn(LinearLayout btn) {
        boolean isFav = FavoritesManager.isFav(this, item);
        if (btn.getChildCount() >= 2) {
            TextView icon  = (TextView) btn.getChildAt(0);
            TextView label = (TextView) btn.getChildAt(1);
            icon.setText(isFav ? "♥  " : "♡  ");
            icon.setTextColor(isFav ? Color.parseColor("#FF5252") : Color.WHITE);
            label.setText(isFav ? "Favorito ✓" : "Favorito");
            label.setTextColor(isFav ? Color.parseColor("#FF5252") : Color.WHITE);
        }
    }

    private void updateMyListBtn(LinearLayout btn) {
        boolean inList = MyListManager.isInList(this, item);
        if (btn.getChildCount() >= 2) {
            TextView icon  = (TextView) btn.getChildAt(0);
            TextView label = (TextView) btn.getChildAt(1);
            icon.setText(inList ? "⊞  " : "⊟  ");
            icon.setTextColor(inList ? Color.parseColor("#4FC3F7") : Color.WHITE);
            label.setText(inList ? "Mi Lista ✓" : "Mi Lista");
            label.setTextColor(inList ? Color.parseColor("#4FC3F7") : Color.WHITE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EPISODE SECTION — glass UI
    // ══════════════════════════════════════════════════════════════════════════

    private void setupEpisodeSection() {
        View section = findViewById(R.id.episodeSectionDetail);
        if (section == null) return;

        boolean isTV = item.getContentType() != ContentItem.TYPE_MOVIE;
        section.setVisibility(isTV ? View.VISIBLE : View.GONE);
        if (!isTV) return;

        // Wire overview into Información tab
        TextView tvInfoOv = findViewById(R.id.tvInfoOverview);
        if (tvInfoOv != null) tvInfoOv.setText(item.getOverview());

        // Tab switching
        TextView tabEp   = findViewById(R.id.tabEpisodios);
        TextView tabInfo = findViewById(R.id.tabInformacion);
        View     epContent   = findViewById(R.id.episodeTabContent);
        View     infoContent = findViewById(R.id.infoTabContent);
        View     indicator   = findViewById(R.id.tabIndicatorEp);

        if (tabEp != null) tabEp.setOnClickListener(v -> {
            setTabActive(tabEp, tabInfo, indicator, true);
            if (epContent   != null) epContent.setVisibility(View.VISIBLE);
            if (infoContent != null) infoContent.setVisibility(View.GONE);
        });
        if (tabInfo != null) tabInfo.setOnClickListener(v -> {
            setTabActive(tabInfo, tabEp, indicator, false);
            if (epContent   != null) epContent.setVisibility(View.GONE);
            if (infoContent != null) infoContent.setVisibility(View.VISIBLE);
        });

        // Season selector pill
        View seasonSel = findViewById(R.id.seasonSelector);
        if (seasonSel != null) seasonSel.setOnClickListener(v -> showSeasonPicker());

        // Fetch season count then load season 1
        loadSeasonCount();
    }

    private void setTabActive(TextView active, TextView inactive, View indicator, boolean isEpisodes) {
        if (active   != null) { active.setTextColor(0xFFFFFFFF);   active.setTypeface(null, Typeface.BOLD); }
        if (inactive != null) { inactive.setTextColor(0x44FFFFFF); inactive.setTypeface(null, Typeface.NORMAL); }
        if (indicator != null) indicator.setVisibility(isEpisodes ? View.VISIBLE : View.GONE);
    }

    private void loadSeasonCount() {
        Handler h = new Handler(android.os.Looper.getMainLooper());
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            try {
                int seasons = TmdbApi.fetchSeriesSeasonCount(item.getTmdbId());
                h.post(() -> {
                    if (isFinishing()) return;
                    totalSeasons = Math.max(1, seasons);
                    updateSeasonPill();
                    loadEpisodes(currentSeason);
                });
            } catch (Exception e) {
                h.post(() -> {
                    if (!isFinishing()) loadEpisodes(1);
                });
            }
        });
        pool.shutdown();
    }

    private void updateSeasonPill() {
        TextView pill = findViewById(R.id.tvSeasonPill);
        if (pill != null) {
            String label = totalSeasons > 1
                ? "Temporada " + currentSeason + " de " + totalSeasons
                : "Temporada " + currentSeason;
            pill.setText(label);
        }
    }

    private void showSeasonPicker() {
        if (totalSeasons <= 1) return;
        String[] items = new String[totalSeasons];
        for (int i = 0; i < totalSeasons; i++) items[i] = "Temporada " + (i + 1);

        new AlertDialog.Builder(this)
            .setTitle("Seleccionar temporada")
            .setItems(items, (d, which) -> {
                currentSeason = which + 1;
                updateSeasonPill();
                loadEpisodes(currentSeason);
            })
            .show();
    }

    private void loadEpisodes(int season) {
        View loadFrame  = findViewById(R.id.epLoadingFrame);
        LinearLayout cnt = findViewById(R.id.episodeListContainer);
        if (loadFrame != null) loadFrame.setVisibility(View.VISIBLE);
        if (cnt       != null) { cnt.setVisibility(View.GONE); cnt.removeAllViews(); }

        Handler h = new Handler(android.os.Looper.getMainLooper());
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            try {
                List<TmdbApi.EpisodeInfo> eps = TmdbApi.fetchSeasonEpisodes(item.getTmdbId(), season);
                h.post(() -> {
                    if (isFinishing()) return;
                    if (loadFrame != null) loadFrame.setVisibility(View.GONE);
                    buildEpisodeCards(eps, season);
                });
            } catch (Exception e) {
                h.post(() -> {
                    if (isFinishing()) return;
                    if (loadFrame != null) loadFrame.setVisibility(View.GONE);
                    buildEpisodeCards(null, season);
                });
            }
        });
        pool.shutdown();
    }

    private void buildEpisodeCards(List<TmdbApi.EpisodeInfo> eps, int season) {
        LinearLayout cnt = findViewById(R.id.episodeListContainer);
        if (cnt == null) return;
        cnt.removeAllViews();
        cnt.setVisibility(View.VISIBLE);

        if (eps == null || eps.isEmpty()) {
            // Fallback: show E1..E12 placeholder cards
            for (int i = 1; i <= 12; i++) {
                addEpisodeCard(cnt, season, i, "Episodio " + i, "", "", 0);
            }
            return;
        }

        for (TmdbApi.EpisodeInfo ep : eps) {
            addEpisodeCard(cnt, season, ep.number, ep.name, ep.overview, ep.stillUrl, ep.runtime);
        }
    }

    private void addEpisodeCard(LinearLayout parent, int season, int epNum,
                                String name, String overview, String stillUrl, int runtime) {
        // Card container
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setClickable(true);
        card.setFocusable(true);
        card.setBackgroundResource(R.drawable.ep_card_glass);

        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(cardLp);

        // Thumbnail
        ImageView thumb = new ImageView(this);
        int thumbW = dp(120), thumbH = dp(76);
        LinearLayout.LayoutParams thumbLp = new LinearLayout.LayoutParams(thumbW, thumbH);
        thumb.setLayoutParams(thumbLp);
        thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

        GradientDrawable thumbBg = new GradientDrawable();
        thumbBg.setColor(0x1AFFFFFF);
        thumbBg.setCornerRadii(new float[]{dp(12), dp(12), 0, 0, 0, 0, dp(12), dp(12)});
        thumb.setBackground(thumbBg);

        String imageUrl = (stillUrl != null && !stillUrl.isEmpty()) ? stillUrl : item.getPosterUrl();
        if (!imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(thumb);
        }
        card.addView(thumb);

        // Right column
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(android.view.Gravity.CENTER_VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, thumbH, 1f));
        col.setPadding(dp(12), dp(10), dp(10), dp(10));

        // Episode badge row
        LinearLayout badgeRow = new LinearLayout(this);
        badgeRow.setOrientation(LinearLayout.HORIZONTAL);
        badgeRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        badgeRow.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // E-number badge
        TextView badge = new TextView(this);
        badge.setText("E" + epNum);
        badge.setTextColor(0xFFFF6B00);
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setLetterSpacing(0.06f);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(0x22FF6B00);
        badgeBg.setStroke(dp(1), 0x55FF6B00);
        badgeBg.setCornerRadius(dp(4));
        badge.setBackground(badgeBg);
        int bPadH = dp(6), bPadV = dp(2);
        badge.setPadding(bPadH, bPadV, bPadH, bPadV);
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeLp.setMarginEnd(dp(7));
        badge.setLayoutParams(badgeLp);
        badgeRow.addView(badge);

        // Episode title
        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setMaxLines(1);
        tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0,
            ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        badgeRow.addView(tvName);
        col.addView(badgeRow);

        // Overview
        if (overview != null && !overview.isEmpty()) {
            TextView tvOv = new TextView(this);
            tvOv.setText(overview);
            tvOv.setTextColor(0x88FFFFFF);
            tvOv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
            tvOv.setMaxLines(2);
            tvOv.setEllipsize(android.text.TextUtils.TruncateAt.END);
            tvOv.setLineSpacing(0, 1.3f);
            LinearLayout.LayoutParams ovLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ovLp.setMargins(0, dp(4), 0, 0);
            tvOv.setLayoutParams(ovLp);
            col.addView(tvOv);
        }

        // Runtime chip (if available)
        if (runtime > 0) {
            TextView tvTime = new TextView(this);
            tvTime.setText(runtime + " min");
            tvTime.setTextColor(0x55FFFFFF);
            tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
            LinearLayout.LayoutParams timeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            timeLp.setMargins(0, dp(3), 0, 0);
            tvTime.setLayoutParams(timeLp);
            col.addView(tvTime);
        }

        card.addView(col);

        // ── Watched indicator (✓) — top-right corner overlay ─────────────────
        final int s = season, e = epNum;
        boolean watched = WatchedManager.isWatched(this, item.getTmdbId(), s, e);

        TextView checkBadge = new TextView(this);
        checkBadge.setText("✓ Vista");
        checkBadge.setTextColor(0xFF4CAF50);
        checkBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f);
        checkBadge.setTypeface(null, Typeface.BOLD);
        checkBadge.setLetterSpacing(0.04f);
        GradientDrawable checkBg = new GradientDrawable();
        checkBg.setColor(0x1A4CAF50);
        checkBg.setStroke(dp(1), 0x554CAF50);
        checkBg.setCornerRadius(dp(4));
        checkBadge.setBackground(checkBg);
        checkBadge.setPadding(dp(5), dp(2), dp(5), dp(2));

        LinearLayout.LayoutParams checkLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        checkLp.setMargins(dp(6), 0, 0, 0);
        checkBadge.setLayoutParams(checkLp);
        checkBadge.setVisibility(watched ? View.VISIBLE : View.GONE);

        // Insert check badge into the badge row (after episode title)
        badgeRow.addView(checkBadge);

        // Dim thumbnail overlay if watched
        thumb.setAlpha(watched ? 0.55f : 1.0f);

        // Click → mark watched + prefetch servers in parallel with animation
        card.setOnClickListener(v -> {
            WatchedManager.markWatched(this, item.getTmdbId(), s, e);
            checkBadge.setVisibility(View.VISIBLE);
            thumb.setAlpha(0.55f);
            fetchAndPlay(item, s, e);
        });

        // Long-press → toggle watched without opening player
        card.setOnLongClickListener(v -> {
            WatchedManager.toggle(this, item.getTmdbId(), s, e);
            boolean nowWatched = WatchedManager.isWatched(this, item.getTmdbId(), s, e);
            checkBadge.setVisibility(nowWatched ? View.VISIBLE : View.GONE);
            thumb.setAlpha(nowWatched ? 0.55f : 1.0f);
            Toast.makeText(this,
                nowWatched ? "Marcado como vista" : "Desmarcado",
                Toast.LENGTH_SHORT).show();
            return true;
        });

        parent.addView(card);
    }

    // ══════════════════════════════════════════════════════════════════════════

    private void loadRelated() {
        View rowRelated = findViewById(R.id.rowRelated);
        if (rowRelated == null) return;

        TextView rowTitle = rowRelated.findViewById(R.id.rowTitle);
        RecyclerView rv   = rowRelated.findViewById(R.id.rowRv);
        if (rv != null) rv.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String sectionTitle = item.getContentType() == ContentItem.TYPE_MOVIE
            ? "Películas Relacionadas"
            : item.getContentType() == ContentItem.TYPE_ANIME
            ? "Animes Relacionados"
            : "Series Relacionadas";
        if (rowTitle != null) rowTitle.setText(sectionTitle);

        Handler h = new Handler(android.os.Looper.getMainLooper());
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            try {
                List<ContentItem> related;
                switch (item.getContentType()) {
                    case ContentItem.TYPE_ANIME:  related = TmdbApi.fetchAnime();  break;
                    case ContentItem.TYPE_SERIES: related = TmdbApi.fetchSeries(); break;
                    case ContentItem.TYPE_DORAMA: related = TmdbApi.fetchDoramas(); break;
                    default:                      related = TmdbApi.fetchMovies();  break;
                }
                h.post(() -> {
                    if (!isFinishing() && rv != null) {
                        rv.setAdapter(new ContentRowAdapter(this, related));
                    }
                });
            } catch (Exception ignored) {}
        });
        pool.shutdown();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FETCH + PLAY — fetch servers in parallel with the loading animation
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Shows the loading overlay immediately, starts the server fetch in parallel,
     * then opens the dialog as soon as the data is ready (min 900ms for visual polish).
     */
    private void fetchAndPlay(ContentItem ci, int season, int episode) {
        showLoadingOverlay();

        long startMs = System.currentTimeMillis();
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            StreamingApi.ServerData data = null;
            try {
                data = ci.getContentType() == ContentItem.TYPE_MOVIE
                    ? StreamingApi.fetchMovieServers(ci.getTmdbId())
                    : StreamingApi.fetchSeriesServers(ci.getTmdbId(), season, episode);
            } catch (Exception ignored) {}

            final StreamingApi.ServerData finalData = data;
            long elapsed   = System.currentTimeMillis() - startMs;
            long minShow   = 900L;  // keep overlay visible at least 900ms
            long remaining = Math.max(0L, minShow - elapsed);

            loadingHandler.postDelayed(() -> {
                if (isFinishing()) return;
                hideLoadingOverlay(() -> {
                    ContinueWatchingManager.save(this, ci, season, episode);
                    ContinueWatchingWidget.refresh(this);
                    if (finalData != null && (!finalData.latino.isEmpty()
                            || !finalData.espanol.isEmpty() || !finalData.subtitulado.isEmpty())) {
                        ServerSelectDialog.showPreloaded(this, ci, finalData, season, episode);
                    } else {
                        // Build fallback inline — no second network request
                        StreamingApi.ServerData fallback = new StreamingApi.ServerData();
                        fallback.latino.add(new StreamingApi.Server(
                            "UnlimPlay", ci.getStreamUrl(), "embed"));
                        ServerSelectDialog.showPreloaded(this, ci, fallback, season, episode);
                    }
                });
            }, remaining);
        });
        exec.shutdown();
    }

    private void showLoadingOverlay() {
        FrameLayout overlay = findViewById(R.id.loadingServersOverlay);
        if (overlay == null) return;

        for (Animator a : dotAnimators) a.cancel();
        dotAnimators.clear();

        overlay.setAlpha(0f);
        overlay.setVisibility(View.VISIBLE);
        overlay.animate().alpha(1f).setDuration(200)
            .setInterpolator(new AccelerateDecelerateInterpolator()).start();

        TextView tvTitle    = overlay.findViewById(R.id.tvLoadingTitle);
        TextView tvSubtitle = overlay.findViewById(R.id.tvLoadingSubtitle);
        if (tvTitle != null) {
            ObjectAnimator a = ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0.4f, 1f);
            a.setDuration(900); a.setRepeatCount(ObjectAnimator.INFINITE); a.start();
            dotAnimators.add(a);
        }
        if (tvSubtitle != null) {
            ObjectAnimator a = ObjectAnimator.ofFloat(tvSubtitle, "alpha", 0.6f, 1f, 0.6f);
            a.setDuration(900); a.setRepeatCount(ObjectAnimator.INFINITE); a.start();
            dotAnimators.add(a);
        }
        animateDot(overlay, R.id.loadingDot1, 0);
        animateDot(overlay, R.id.loadingDot2, 250);
        animateDot(overlay, R.id.loadingDot3, 500);

        View spinner = overlay.findViewById(R.id.loadingSpinner);
        if (spinner != null) {
            spinner.setScaleX(0f); spinner.setScaleY(0f);
            spinner.animate().scaleX(1f).scaleY(1f).setDuration(350)
                .setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
    }

    private void hideLoadingOverlay(Runnable onHidden) {
        FrameLayout overlay = findViewById(R.id.loadingServersOverlay);
        if (overlay == null) { onHidden.run(); return; }
        overlay.animate().alpha(0f).setDuration(200).withEndAction(() -> {
            overlay.setVisibility(View.GONE);
            for (Animator a : dotAnimators) a.cancel();
            dotAnimators.clear();
            onHidden.run();
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPRODUCIR — continuous pulse + shimmer sweep
    // ══════════════════════════════════════════════════════════════════════════

    private void startReproducirAnimations(View btn) {
        if (btn == null) return;

        // Subtle scale pulse
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.025f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.025f, 1f);
        scaleX.setDuration(1800);
        scaleY.setDuration(1800);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();

        // Shimmer sweep across button
        View shimmer = btn.findViewById(R.id.btnPlayShimmer);
        if (shimmer != null && btn instanceof FrameLayout) {
            btn.post(() -> {
                float btnW = btn.getWidth();
                shimmer.setTranslationX(-80f);
                ObjectAnimator sweep = ObjectAnimator.ofFloat(shimmer, "translationX", -80f, btnW + 80f);
                sweep.setDuration(1600);
                sweep.setStartDelay(600);
                sweep.setRepeatCount(ObjectAnimator.INFINITE);
                sweep.setRepeatMode(ObjectAnimator.RESTART);
                sweep.setInterpolator(new LinearInterpolator());

                ObjectAnimator shimmerAlpha = ObjectAnimator.ofFloat(shimmer, "alpha", 0f, 0.5f, 0f);
                shimmerAlpha.setDuration(1600);
                shimmerAlpha.setStartDelay(600);
                shimmerAlpha.setRepeatCount(ObjectAnimator.INFINITE);
                shimmerAlpha.setRepeatMode(ObjectAnimator.RESTART);
                shimmerAlpha.setInterpolator(new LinearInterpolator());

                sweep.start();
                shimmerAlpha.start();
            });
        }
    }

    private void animateDot(View parent, int dotId, long delay) {
        View dot = parent.findViewById(dotId);
        if (dot == null) return;
        ObjectAnimator anim = ObjectAnimator.ofFloat(dot, "alpha", 0.15f, 1f, 0.15f);
        anim.setDuration(750);
        anim.setStartDelay(delay);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        dotAnimators.add(anim);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRAILER — fetch key → backdrop autoplay → modal
    // ══════════════════════════════════════════════════════════════════════════

    private void loadTrailer() {
        if (item.getTmdbId() == 0) return;
        boolean isMovie = item.getContentType() == ContentItem.TYPE_MOVIE;
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            String key = TmdbApi.fetchYouTubeTrailerKey(item.getTmdbId(), isMovie);
            loadingHandler.post(() -> {
                if (isFinishing()) return;
                trailerKey = key;
                if (!key.isEmpty()) {
                    // Show "Ver Tráiler" button
                    View btn = findViewById(R.id.btnTrailer);
                    if (btn != null) {
                        btn.setVisibility(View.VISIBLE);
                        btn.setAlpha(0f);
                        btn.animate().alpha(1f).setDuration(500).start();
                    }
                    // Start backdrop autoplay (muted)
                    setupBackdropWebView(key);
                }
            });
        });
        exec.shutdown();
    }

    private static final String CHROME_UA =
        "Mozilla/5.0 (Linux; Android 11; Mobile) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";

    @android.annotation.SuppressLint({"SetJavaScriptEnabled","RequiresFeature"})
    private void setupBackdropWebView(String key) {
        if (backdropWebView == null) return;

        // Accept cookies so YouTube can authenticate the embed
        android.webkit.CookieManager cm = android.webkit.CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(backdropWebView, true);

        WebSettings ws = backdropWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setDomStorageEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setUserAgentString(CHROME_UA);
        backdropWebView.setBackgroundColor(Color.BLACK);

        String html = "<!DOCTYPE html><html>"
            + "<head><style>*{margin:0;padding:0;overflow:hidden;background:#000}"
            + "html,body{width:100%;height:100%}iframe{width:100%;height:200%}"
            + "</style></head><body>"
            + "<iframe id='yt'"
            + " src='https://www.youtube-nocookie.com/embed/" + key
            + "?autoplay=1&mute=1&controls=0&loop=1&playlist=" + key
            + "&playsinline=1&showinfo=0&rel=0&iv_load_policy=3&modestbranding=1&fs=0'"
            + " frameborder='0'"
            + " allow='autoplay;encrypted-media'></iframe>"
            + "</body></html>";

        backdropWebView.loadDataWithBaseURL(
            "https://www.youtube.com", html, "text/html", "utf-8", null);

        // Crossfade backdrop image → trailer WebView after short delay
        loadingHandler.postDelayed(() -> {
            if (isFinishing() || backdropWebView == null) return;
            backdropWebView.animate().alpha(1f).setDuration(1200)
                .setInterpolator(new AccelerateDecelerateInterpolator()).start();

            ImageView backdrop = findViewById(R.id.detailBackdrop);
            if (backdrop != null) {
                backdrop.animate().alpha(0f).setDuration(1200).start();
            }

            View mutedBadge = findViewById(R.id.trailerMutedBadge);
            if (mutedBadge != null) {
                mutedBadge.setVisibility(View.VISIBLE);
                mutedBadge.setAlpha(0f);
                mutedBadge.animate().alpha(1f).setDuration(600).start();
            }
        }, 2500);
    }

    @android.annotation.SuppressLint("SetJavaScriptEnabled")
    private void showTrailerModal(String key) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_trailer);

        Window win = dialog.getWindow();
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int margin  = dp(20);
        int dlgW    = screenW - margin * 2;

        if (win != null) {
            win.setBackgroundDrawableResource(android.R.color.transparent);
            win.setLayout(dlgW, WindowManager.LayoutParams.WRAP_CONTENT);
            win.setGravity(Gravity.CENTER);
            win.setDimAmount(0.75f);
        }

        // Title
        TextView tvTitle = dialog.findViewById(R.id.trailerTitle);
        if (tvTitle != null) tvTitle.setText(item.getTitle() + " — Tráiler");

        // Resize WebView height to 16:9
        WebView wv = dialog.findViewById(R.id.trailerWebView);
        if (wv != null) {
            int videoH = dlgW * 9 / 16;
            wv.getLayoutParams().height = videoH;
            wv.requestLayout();

            android.webkit.CookieManager cm = android.webkit.CookieManager.getInstance();
            cm.setAcceptCookie(true);
            cm.setAcceptThirdPartyCookies(wv, true);

            WebSettings ws = wv.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setMediaPlaybackRequiresUserGesture(false);
            ws.setDomStorageEnabled(true);
            ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
            ws.setUserAgentString(CHROME_UA);
            ws.setLoadWithOverviewMode(true);
            ws.setUseWideViewPort(true);
            wv.setBackgroundColor(Color.BLACK);
            wv.setWebChromeClient(new WebChromeClient());

            // Load via HTML iframe with youtube-nocookie to avoid Error 150/153
            String html = "<!DOCTYPE html><html>"
                + "<head><style>*{margin:0;padding:0;background:#000}"
                + "html,body,iframe{width:100%;height:100%;border:none}</style></head>"
                + "<body><iframe"
                + " src='https://www.youtube-nocookie.com/embed/" + key
                + "?autoplay=1&controls=1&playsinline=1&rel=0&modestbranding=1'"
                + " allow='autoplay;encrypted-media;fullscreen'"
                + " allowfullscreen></iframe></body></html>";
            wv.loadDataWithBaseURL(
                "https://www.youtube.com", html, "text/html", "utf-8", null);
        }

        // Close button
        View closeBtn = dialog.findViewById(R.id.trailerClose);
        if (closeBtn != null) closeBtn.setOnClickListener(v -> {
            if (wv != null) { wv.stopLoading(); wv.loadUrl("about:blank"); }
            dialog.dismiss();
        });

        // Expand / fullscreen toggle
        final boolean[] isFullscreen = {false};
        TextView expandIcon = dialog.findViewById(R.id.trailerExpandIcon);
        View expandBtn = dialog.findViewById(R.id.trailerExpand);
        if (expandBtn != null) expandBtn.setOnClickListener(v -> {
            isFullscreen[0] = !isFullscreen[0];
            if (win != null) {
                if (isFullscreen[0]) {
                    win.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                    if (wv != null) {
                        wv.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
                        wv.requestLayout();
                    }
                    if (expandIcon != null) expandIcon.setText("⊡");
                } else {
                    win.setLayout(dlgW, WindowManager.LayoutParams.WRAP_CONTENT);
                    if (wv != null) {
                        wv.getLayoutParams().height = dlgW * 9 / 16;
                        wv.requestLayout();
                    }
                    if (expandIcon != null) expandIcon.setText("⛶");
                }
            }
        });

        // Animate in
        View root = dialog.findViewById(android.R.id.content);
        dialog.show();
        if (win != null) {
            View decor = win.getDecorView();
            decor.setAlpha(0f);
            decor.setScaleX(0.92f);
            decor.setScaleY(0.92f);
            decor.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(280)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        }

        dialog.setOnDismissListener(d -> {
            if (wv != null) { wv.stopLoading(); wv.loadUrl("about:blank"); wv.destroy(); }
        });
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
