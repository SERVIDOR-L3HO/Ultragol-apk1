package com.ultragol.app;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.network.DramaShortsApi;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Pantalla de Shorts Dramas — diseño estilo Doramas.
 * Hero banner + filas horizontales por categoría.
 * Los videos se abren con el reproductor interno (PlayerActivity).
 */
public class DramaShortsActivity extends AppCompatActivity {

    private LinearLayout    container;
    private View            loadingView;
    private NestedScrollView scrollView;
    private boolean         firstLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_shorts);

        loadingView = findViewById(R.id.dsLoading);
        scrollView  = findViewById(R.id.dsScroll);
        container   = findViewById(R.id.dsContainer);

        // Back button
        View btnBack = findViewById(R.id.dsBtnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Show loading
        loadingView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        // Load all sections in parallel
        loadHeroSection();
        loadSection("RECIENTES", "DRAMA CORTO RECIENTE",  0xFFE91E63,
                () -> DramaShortsApi.fetchRecientes(1));
        loadSection("POPULARES", "MÁS VISTOS",            0xFFFF6B00,
                () -> DramaShortsApi.fetchPopulares("es", 1));
        loadSection("TENDENCIAS", "EN TENDENCIA",          0xFF2196F3,
                () -> DramaShortsApi.fetchTendencias("es", 1));
    }

    // ── Hero ──────────────────────────────────────────────────────────────────

    private void loadHeroSection() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<DramaShortsApi.VideoItem> items = DramaShortsApi.fetchRecientes(1);
                if (items == null || items.isEmpty()) return;
                final DramaShortsApi.VideoItem hero = items.get(0);
                runOnUiThread(() -> {
                    showContent();
                    addHeroView(hero);
                });
            } catch (Exception ignored) {}
        });
    }

    private void addHeroView(DramaShortsApi.VideoItem hero) {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.item_ds_hero, container, false);

        ImageView img      = v.findViewById(R.id.dsHeroImage);
        TextView  title    = v.findViewById(R.id.dsHeroTitle);
        TextView  canal    = v.findViewById(R.id.dsHeroCanal);
        TextView  duracion = v.findViewById(R.id.dsHeroDuracion);

        title.setText(hero.titulo);
        canal.setText(hero.canal);
        duracion.setText(hero.getDuracionStr());

        if (hero.thumbnailUrl != null && !hero.thumbnailUrl.isEmpty()) {
            Glide.with(this)
                    .load(hero.thumbnailUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(img);
        }

        v.setOnClickListener(view -> openPlayer(hero));

        // Hero must be first — insert at position 0
        container.addView(v, 0);
    }

    // ── Horizontal sections ───────────────────────────────────────────────────

    private void loadSection(String subLabel, String mainTitle, int accentColor,
                             Callable<List<DramaShortsApi.VideoItem>> fetcher) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<DramaShortsApi.VideoItem> items = fetcher.call();
                if (items == null || items.isEmpty()) return;
                runOnUiThread(() -> {
                    showContent();
                    addSectionView(subLabel, mainTitle, accentColor, items);
                });
            } catch (Exception ignored) {}
        });
    }

    private void addSectionView(String subLabel, String mainTitle, int accentColor,
                                List<DramaShortsApi.VideoItem> items) {
        View section = LayoutInflater.from(this)
                .inflate(R.layout.item_cine_section, container, false);

        View     accentBar = section.findViewById(R.id.sectionAccentBar);
        TextView playIcon  = section.findViewById(R.id.sectionPlayIcon);
        TextView subLbl    = section.findViewById(R.id.sectionSubLabel);
        TextView mainLbl   = section.findViewById(R.id.sectionMainTitle);
        TextView navPrev   = section.findViewById(R.id.sectionNavPrev);
        TextView navNext   = section.findViewById(R.id.sectionNavNext);
        RecyclerView rv    = section.findViewById(R.id.sectionRv);

        if (accentBar != null) accentBar.setBackgroundColor(accentColor);
        if (playIcon  != null) playIcon.setTextColor(accentColor);
        if (subLbl    != null) subLbl.setText(subLabel);
        if (mainLbl   != null) mainLbl.setText(mainTitle);

        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.HORIZONTAL, false));
            rv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            rv.setAdapter(new DsCardAdapter(items));

            int pageWidthPx = dpToPx(160 * 2);
            if (navPrev != null) navPrev.setOnClickListener(v -> rv.smoothScrollBy(-pageWidthPx, 0));
            if (navNext != null) navNext.setOnClickListener(v -> rv.smoothScrollBy( pageWidthPx, 0));
        }

        container.addView(section);
    }

    // ── Player ────────────────────────────────────────────────────────────────

    private void openPlayer(DramaShortsApi.VideoItem item) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url",   item.getEmbedUrl());
        intent.putExtra("title", item.titulo);
        startActivity(intent);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void showContent() {
        if (!firstLoaded) {
            firstLoaded = true;
            loadingView.setVisibility(View.GONE);
            scrollView .setVisibility(View.VISIBLE);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // ── Card adapter for horizontal rows ─────────────────────────────────────

    private class DsCardAdapter
            extends RecyclerView.Adapter<DsCardAdapter.VH> {

        private final List<DramaShortsApi.VideoItem> items;

        DsCardAdapter(List<DramaShortsApi.VideoItem> items) { this.items = items; }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(DramaShortsActivity.this)
                    .inflate(R.layout.item_ds_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            DramaShortsApi.VideoItem item = items.get(pos);
            h.title  .setText(item.titulo);
            h.canal  .setText(item.canal);
            h.duracion.setText(item.getDuracionStr());

            if (item.thumbnailUrl != null && !item.thumbnailUrl.isEmpty()) {
                Glide.with(DramaShortsActivity.this)
                        .load(item.thumbnailUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(h.thumb);
            } else {
                h.thumb.setImageResource(R.drawable.gradient_hero);
            }

            h.itemView.setOnClickListener(v -> openPlayer(item));
        }

        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView thumb;
            TextView  title, canal, duracion;
            VH(View v) {
                super(v);
                thumb    = v.findViewById(R.id.dscThumb);
                title    = v.findViewById(R.id.dscTitle);
                canal    = v.findViewById(R.id.dscCanal);
                duracion = v.findViewById(R.id.dscDuracion);
            }
        }
    }
}
