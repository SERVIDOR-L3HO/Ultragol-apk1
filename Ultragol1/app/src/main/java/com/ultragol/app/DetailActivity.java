package com.ultragol.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.models.ContentItem;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.ViewGroup;

public class DetailActivity extends AppCompatActivity {

    private ContentItem item;
    private boolean overviewExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        item = (ContentItem) getIntent().getSerializableExtra("item");
        if (item == null) { finish(); return; }

        ImageView   backdrop     = findViewById(R.id.detailBackdrop);
        ImageView   poster       = findViewById(R.id.detailPoster);
        TextView    title        = findViewById(R.id.detailTitle);
        TextView    meta         = findViewById(R.id.detailMeta);
        TextView    rating       = findViewById(R.id.detailRating);
        TextView    overview     = findViewById(R.id.detailOverview);
        TextView    badge        = findViewById(R.id.detailBadge);
        TextView    statYear     = findViewById(R.id.statYear);
        TextView    statEpisodes = findViewById(R.id.statEpisodes);
        TextView    statSeasons  = findViewById(R.id.statSeasons);
        TextView    btnReadMore  = findViewById(R.id.btnReadMore);
        View        ratingRow    = findViewById(R.id.ratingRow);
        LinearLayout genreChips = findViewById(R.id.genreChips);
        View        btnPlay      = findViewById(R.id.btnPlay);
        View        btnBack      = findViewById(R.id.btnDetailBack);

        // ── Text fields ──────────────────────────────────────────────────────────
        if (title != null)    title.setText(item.getTitle());
        if (badge != null)    badge.setText(item.getBadge());

        // Meta: "Año • ⭐ Rating"
        if (meta != null) {
            String metaText = "";
            if (!item.getYear().isEmpty())   metaText += item.getYear();
            if (!item.getRating().isEmpty()) {
                if (!metaText.isEmpty()) metaText += "  •  ⭐ ";
                metaText += item.getRating();
            }
            meta.setText(metaText);
        }

        // Rating row (hidden — rating is already in meta line above)
        if (ratingRow != null) ratingRow.setVisibility(View.GONE);
        if (rating != null)    rating.setText(item.getRating());

        // Overview + Leer más
        if (overview != null) {
            overview.setText(item.getOverview());
            if (btnReadMore != null) {
                overview.post(() -> {
                    if (overview.getLineCount() > 4) {
                        btnReadMore.setVisibility(View.VISIBLE);
                    }
                });
                btnReadMore.setOnClickListener(v -> {
                    overviewExpanded = !overviewExpanded;
                    if (overviewExpanded) {
                        overview.setMaxLines(Integer.MAX_VALUE);
                        overview.setEllipsize(null);
                        btnReadMore.setText("Ver menos");
                    } else {
                        overview.setMaxLines(4);
                        overview.setEllipsize(android.text.TextUtils.TruncateAt.END);
                        btnReadMore.setText("Leer más");
                    }
                });
            }
        }

        // ── Stats ────────────────────────────────────────────────────────────────
        if (statYear != null)
            statYear.setText(item.getYear().isEmpty() ? "—" : item.getYear());

        boolean isSeries = item.getContentType() == ContentItem.TYPE_SERIES
                        || item.getContentType() == ContentItem.TYPE_ANIME
                        || item.getContentType() == ContentItem.TYPE_DORAMA;
        if (statEpisodes != null) statEpisodes.setText(isSeries ? "•••" : "—");
        if (statSeasons  != null) statSeasons.setText( isSeries ? "1"   : "—");

        // ── Genre chips ──────────────────────────────────────────────────────────
        if (genreChips != null && !item.getGenre().isEmpty()) {
            String[] genres = item.getGenre().split("[,/•]");
            for (String g : genres) {
                String label = g.trim();
                if (label.isEmpty()) continue;

                TextView chip = new TextView(this);
                chip.setText(label);
                chip.setTextColor(0xFFFFFFFF);
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

                GradientDrawable chipBg = new GradientDrawable();
                chipBg.setShape(GradientDrawable.RECTANGLE);
                chipBg.setColor(0x00000000);
                chipBg.setStroke(dp(1), 0x99FFFFFF);
                chipBg.setCornerRadius(dp(20));
                chip.setBackground(chipBg);

                int padH = dp(12), padV = dp(5);
                chip.setPadding(padH, padV, padH, padV);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMarginEnd(dp(8));
                chip.setLayoutParams(lp);
                genreChips.addView(chip);
            }
        }

        // ── Images ───────────────────────────────────────────────────────────────
        if (backdrop != null && !item.getBackdropUrl().isEmpty()) {
            Glide.with(this).load(item.getBackdropUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backdrop);
        }
        if (poster != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(this).load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(poster);
        }

        // ── Buttons ──────────────────────────────────────────────────────────────
        if (btnBack != null) btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        });
        if (btnPlay != null) btnPlay.setOnClickListener(v ->
            ServerSelectDialog.show(this, item));
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value,
            getResources().getDisplayMetrics());
    }
}
