package com.ultragol.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.adapters.ContentRowAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.List;
import java.util.concurrent.*;

public class DetailActivity extends AppCompatActivity {

    private ContentItem item;
    private int currentSeason = 1;
    private int totalSeasons  = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        item = (ContentItem) getIntent().getSerializableExtra("item");
        if (item == null) { finish(); return; }

        bindViews();
        loadRelated();
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
        if (btnPlay != null) btnPlay.setOnClickListener(v ->
            ServerSelectDialog.show(this, item));

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

        // Click → mark watched + open server dialog
        card.setOnClickListener(v -> {
            WatchedManager.markWatched(this, item.getTmdbId(), s, e);
            checkBadge.setVisibility(View.VISIBLE);
            thumb.setAlpha(0.55f);
            ServerSelectDialog.show(this, item, s, e);
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

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
