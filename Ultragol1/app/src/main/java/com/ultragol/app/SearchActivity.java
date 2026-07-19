package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.adapters.DramaShortsRowAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.DramaShortsApi;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private EditText         searchInput;
    private NestedScrollView scrollView;
    private RecyclerView     resultsGrid;
    private RecyclerView     shortsRow;
    private LinearLayout     shortsSection;
    private TextView         emptyState;
    private View             loadingView;

    private ContentGridAdapter       adapter;
    private DramaShortsRowAdapter    shortsAdapter;

    private final List<ContentItem>           results      = new ArrayList<>();
    private final List<DramaShortsApi.VideoItem> shortsItems = new ArrayList<>();

    private final Handler  handler         = new Handler();
    private       Runnable searchRunnable;
    private boolean        kidsMode        = false;

    // Track in-flight searches so late results don't overwrite newer ones
    private volatile int   searchSeq       = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput   = findViewById(R.id.searchInput);
        resultsGrid   = findViewById(R.id.resultsGrid);
        shortsRow     = findViewById(R.id.shortsRow);
        shortsSection = findViewById(R.id.shortsSection);
        emptyState    = findViewById(R.id.emptyState);
        loadingView   = findViewById(R.id.loadingSearch);
        scrollView    = findViewById(R.id.searchScroll);

        // Kids mode
        try {
            ProfileManager.Profile prof = ProfileManager.getCurrentProfile(this);
            kidsMode = prof != null && prof.isKids;
        } catch (Exception ignored) {}

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // TMDB grid
        adapter = new ContentGridAdapter(this, results);
        resultsGrid.setLayoutManager(new GridLayoutManager(this, 3));
        resultsGrid.setAdapter(adapter);

        // Shorts Dramas horizontal row
        shortsAdapter = new DramaShortsRowAdapter(this, shortsItems);
        shortsRow.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        shortsRow.setAdapter(shortsAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                String q = s.toString().trim();
                if (q.length() < 2) { showEmpty(); return; }
                searchRunnable = () -> doSearch(q);
                handler.postDelayed(searchRunnable, 500);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        searchInput.requestFocus();
    }

    // ── Search ────────────────────────────────────────────────────────────────

    private void doSearch(final String query) {
        final int seq = ++searchSeq;

        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (emptyState  != null) emptyState .setVisibility(View.GONE);

        // ── TMDB search ───────────────────────────────────────────────────────
        ExecutorService tmdbExec = Executors.newSingleThreadExecutor();
        tmdbExec.execute(() -> {
            List<ContentItem> r = new ArrayList<>();
            try {
                r = kidsMode ? TmdbApi.searchMultiKids(query) : TmdbApi.searchMulti(query);
            } catch (Exception ignored) {}
            final List<ContentItem> tmdbResult = r;
            runOnUiThread(() -> {
                if (seq != searchSeq) return; // stale
                results.clear();
                results.addAll(tmdbResult);
                adapter.notifyDataSetChanged();
                if (loadingView != null) loadingView.setVisibility(View.GONE);
                updateVisibility();
            });
        });
        tmdbExec.shutdown();

        // ── Dailymotion / Shorts Dramas search (skip in kids mode) ───────────
        if (!kidsMode) {
            ExecutorService dmExec = Executors.newSingleThreadExecutor();
            dmExec.execute(() -> {
                List<DramaShortsApi.VideoItem> sr = new ArrayList<>();
                try {
                    sr = DramaShortsApi.buscar(query, "es", 1);
                    if (sr.isEmpty()) {
                        // Try without language filter for broader results
                        sr = DramaShortsApi.buscar(query, "en", 1);
                    }
                } catch (Exception ignored) {}
                final List<DramaShortsApi.VideoItem> dmResult = sr;
                runOnUiThread(() -> {
                    if (seq != searchSeq) return; // stale
                    shortsItems.clear();
                    shortsItems.addAll(dmResult);
                    shortsAdapter.notifyDataSetChanged();
                    shortsSection.setVisibility(
                            dmResult.isEmpty() ? View.GONE : View.VISIBLE);
                    updateVisibility();
                });
            });
            dmExec.shutdown();
        }
    }

    private void updateVisibility() {
        boolean hasResults = !results.isEmpty() || !shortsItems.isEmpty();
        if (emptyState  != null) emptyState .setVisibility(hasResults ? View.GONE  : View.VISIBLE);
        if (scrollView  != null) scrollView .setVisibility(hasResults ? View.VISIBLE : View.GONE);
    }

    private void showEmpty() {
        searchSeq++;
        results.clear();     adapter.notifyDataSetChanged();
        shortsItems.clear(); shortsAdapter.notifyDataSetChanged();
        if (loadingView   != null) loadingView  .setVisibility(View.GONE);
        if (emptyState    != null) emptyState   .setVisibility(View.VISIBLE);
        if (scrollView    != null) scrollView   .setVisibility(View.GONE);
        if (shortsSection != null) shortsSection.setVisibility(View.GONE);
    }
}
