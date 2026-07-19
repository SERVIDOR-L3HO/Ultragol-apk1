package com.ultragol.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.network.DramaShortsApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Pantalla completa de Shorts Dramas.
 * Muestra recientes, populares y tendencias; también permite buscar por título.
 */
public class DramaShortsActivity extends AppCompatActivity {

    // ── UI ────────────────────────────────────────────────────────────────────
    private EditText          searchInput;
    private View              loadingView, emptyView;
    private RecyclerView      grid;
    private TextView          tabRecientes, tabPopulares, tabTendencias;

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<DramaShortsApi.VideoItem> items = new ArrayList<>();
    private       GridAdapter adapter;
    private       String  activeTab   = "recientes";
    private final Handler handler     = new Handler();
    private       Runnable searchJob;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drama_shorts);

        searchInput  = findViewById(R.id.dsSearchInput);
        loadingView  = findViewById(R.id.dsLoading);
        emptyView    = findViewById(R.id.dsEmpty);
        grid         = findViewById(R.id.dsGrid);
        tabRecientes = findViewById(R.id.tabRecientes);
        tabPopulares = findViewById(R.id.tabPopulares);
        tabTendencias= findViewById(R.id.tabTendencias);

        // Back button
        View btnBack = findViewById(R.id.dsBtnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Grid
        adapter = new GridAdapter();
        grid.setLayoutManager(new GridLayoutManager(this, 2));
        grid.setAdapter(adapter);

        // Tabs
        tabRecientes .setOnClickListener(v -> selectTab("recientes"));
        tabPopulares .setOnClickListener(v -> selectTab("populares"));
        tabTendencias.setOnClickListener(v -> selectTab("tendencias"));

        // Search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                if (searchJob != null) handler.removeCallbacks(searchJob);
                String q = s.toString().trim();
                if (q.length() < 2) {
                    selectTab(activeTab); // revert to tab content
                    return;
                }
                searchJob = () -> doSearch(q);
                handler.postDelayed(searchJob, 500);
            }
        });

        // Load default tab
        selectTab("recientes");
    }

    // ── Tab handling ──────────────────────────────────────────────────────────

    private void selectTab(String tab) {
        activeTab = tab;

        // Highlight active tab
        int active   = 0xFFFFFFFF;
        int inactive = 0x88FFFFFF;
        tabRecientes .setTextColor(tab.equals("recientes")  ? active : inactive);
        tabPopulares .setTextColor(tab.equals("populares")  ? active : inactive);
        tabTendencias.setTextColor(tab.equals("tendencias") ? active : inactive);

        loadContent(tab);
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadContent(String tab) {
        showLoading(true);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            List<DramaShortsApi.VideoItem> result = new ArrayList<>();
            try {
                switch (tab) {
                    case "populares":
                        result = DramaShortsApi.fetchPopulares("es", 1); break;
                    case "tendencias":
                        result = DramaShortsApi.fetchTendencias("es", 1); break;
                    default:
                        result = DramaShortsApi.fetchRecientes(1); break;
                }
            } catch (Exception ignored) {}
            final List<DramaShortsApi.VideoItem> r = result;
            runOnUiThread(() -> {
                items.clear(); items.addAll(r);
                adapter.notifyDataSetChanged();
                showLoading(false);
                emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
        exec.shutdown();
    }

    private void doSearch(String query) {
        showLoading(true);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            List<DramaShortsApi.VideoItem> result = new ArrayList<>();
            try { result = DramaShortsApi.buscar(query, "es", 1); } catch (Exception ignored) {}
            final List<DramaShortsApi.VideoItem> r = result;
            runOnUiThread(() -> {
                items.clear(); items.addAll(r);
                adapter.notifyDataSetChanged();
                showLoading(false);
                emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
        exec.shutdown();
    }

    private void showLoading(boolean loading) {
        if (loadingView != null) loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (emptyView   != null) emptyView  .setVisibility(View.GONE);
    }

    // ── Grid Adapter ──────────────────────────────────────────────────────────

    class GridAdapter extends RecyclerView.Adapter<GridAdapter.VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(DramaShortsActivity.this)
                    .inflate(R.layout.item_drama_short_grid, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            DramaShortsApi.VideoItem item = items.get(pos);
            h.title   .setText(item.titulo);
            h.canal   .setText(item.canal);
            h.duracion.setText(item.getDuracionStr());
            h.vistas  .setText(item.getVistasStr() + " vistas");

            if (item.thumbnailUrl != null && !item.thumbnailUrl.isEmpty()) {
                Glide.with(DramaShortsActivity.this)
                        .load(item.thumbnailUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(h.thumb);
            } else {
                h.thumb.setImageResource(R.drawable.gradient_hero);
            }

            h.itemView.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(item.getEmbedUrl())));
                } catch (Exception ignored) {}
            });
        }

        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView thumb;
            TextView  title, canal, duracion, vistas;
            VH(View v) {
                super(v);
                thumb    = v.findViewById(R.id.dsgThumb);
                title    = v.findViewById(R.id.dsgTitle);
                canal    = v.findViewById(R.id.dsgCanal);
                duracion = v.findViewById(R.id.dsgDuracion);
                vistas   = v.findViewById(R.id.dsgVistas);
            }
        }
    }
}
