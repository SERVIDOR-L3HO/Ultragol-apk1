package com.ultragol.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.network.DramaShortsApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Shorts Dramas — estilo YouTube Shorts.
 * Grilla de 2 columnas con tarjetas verticales (portrait).
 * Tabs: Recientes · Populares · Tendencias
 */
public class DramaShortsActivity extends AppCompatActivity {

    // ── Views ─────────────────────────────────────────────────────────────────
    private LinearLayout            tabBar;
    private RecyclerView            grid;
    private View                    loadingView;

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<DramaShortsApi.VideoItem> items   = new ArrayList<>();
    private       GridAdapter                    adapter;
    private       int                            activeTab = 0;          // 0 Recientes, 1 Populares, 2 Tendencias
    private final String[]                        tabLabels = {"Recientes", "Populares", "Tendencias"};
    private final TextView[]                      tabViews  = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_shorts);

        tabBar      = findViewById(R.id.dsTabBar);
        grid        = findViewById(R.id.dsGrid);
        loadingView = findViewById(R.id.dsLoading);

        // Back
        View btnBack = findViewById(R.id.dsBtnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Grid 2 columns
        adapter = new GridAdapter();
        grid.setLayoutManager(new GridLayoutManager(this, 2));
        grid.setAdapter(adapter);

        // Tabs
        buildTabs();
        switchTab(0);
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private void buildTabs() {
        for (int i = 0; i < tabLabels.length; i++) {
            final int idx = i;
            TextView tv = new TextView(this);
            tv.setText(tabLabels[i]);
            tv.setTextColor(0xAAFFFFFF);
            tv.setTextSize(13f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setAllCaps(false);
            tv.setClickable(true);
            tv.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginEnd(dp(4));
            tv.setLayoutParams(lp);
            tv.setPadding(dp(18), 0, dp(18), 0);
            tv.setGravity(Gravity.CENTER);

            tv.setOnClickListener(v -> switchTab(idx));
            tabViews[i] = tv;
            tabBar.addView(tv);
        }
    }

    private void switchTab(int idx) {
        activeTab = idx;

        // Update tab visual
        for (int i = 0; i < tabViews.length; i++) {
            if (tabViews[i] == null) continue;
            boolean active = (i == idx);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(20));
            if (active) {
                bg.setColor(0xFFE91E63);
                tabViews[i].setTextColor(Color.WHITE);
            } else {
                bg.setColor(0x22FFFFFF);
                tabViews[i].setTextColor(0x88FFFFFF);
            }
            tabViews[i].setBackground(bg);
        }

        // Load content
        items.clear();
        adapter.notifyDataSetChanged();
        loadingView.setVisibility(View.VISIBLE);
        grid.setVisibility(View.GONE);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<DramaShortsApi.VideoItem> result = new ArrayList<>();
            try {
                switch (idx) {
                    case 0: result = DramaShortsApi.fetchRecientes(1);           break;
                    case 1: result = DramaShortsApi.fetchPopulares("es", 1); break;
                    case 2: result = DramaShortsApi.fetchTendencias("es", 1);    break;
                }
            } catch (Exception ignored) {}

            final List<DramaShortsApi.VideoItem> finalResult = result;
            runOnUiThread(() -> {
                loadingView.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);
                items.clear();
                items.addAll(finalResult);
                adapter.notifyDataSetChanged();
                grid.scrollToPosition(0);
            });
        });
    }

    // ── Player ────────────────────────────────────────────────────────────────

    private void openPlayer(DramaShortsApi.VideoItem item) {
        Intent intent = new Intent(this, ShortsPlayerActivity.class);
        intent.putExtra("video_id", item.id);
        intent.putExtra("title",    item.titulo);
        startActivity(intent);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    private class GridAdapter extends RecyclerView.Adapter<GridAdapter.VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(DramaShortsActivity.this)
                    .inflate(R.layout.item_short_portrait, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            DramaShortsApi.VideoItem item = items.get(pos);
            h.title   .setText(item.titulo);
            h.canal   .setText(item.canal);
            h.duracion.setText(item.getDuracionStr());

            Glide.with(DramaShortsActivity.this)
                    .load(item.thumbnailUrl)
                    .placeholder(R.drawable.gradient_hero)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(h.thumb);

            h.itemView.setOnClickListener(v -> openPlayer(item));
        }

        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            android.widget.ImageView thumb;
            android.widget.TextView title, canal, duracion;
            VH(View v) {
                super(v);
                thumb    = v.findViewById(R.id.spThumb);
                title    = v.findViewById(R.id.spTitle);
                canal    = v.findViewById(R.id.spCanal);
                duracion = v.findViewById(R.id.spDuracion);
            }
        }
    }
}
