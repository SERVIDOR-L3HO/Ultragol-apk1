package com.ultragol.app;

import android.graphics.Color;
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
        View btnFavorite       = findViewById(R.id.btnFavorite);
        View btnMyList         = findViewById(R.id.btnMyList);

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
                String label = g.trim();
                if (label.isEmpty()) continue;
                TextView chip = new TextView(this);
                chip.setText(label);
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
        if (btnFavorite != null) btnFavorite.setOnClickListener(v ->
            Toast.makeText(this, "Agregado a Favoritos", Toast.LENGTH_SHORT).show());
        if (btnMyList != null) btnMyList.setOnClickListener(v ->
            Toast.makeText(this, "Agregado a Mi Lista", Toast.LENGTH_SHORT).show());
    }

    private void loadRelated() {
        View rowRelated = findViewById(R.id.rowRelated);
        if (rowRelated == null) return;

        TextView rowTitle = rowRelated.findViewById(R.id.rowTitle);
        RecyclerView rv   = rowRelated.findViewById(R.id.rowRv);
        if (rv != null) rv.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Title based on type
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
