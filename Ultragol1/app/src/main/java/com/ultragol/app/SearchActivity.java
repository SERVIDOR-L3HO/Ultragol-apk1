package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.ultragol.app.adapters.HomeTvAdapter;
import com.ultragol.app.fragments.TvFragment;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.models.TvChannel;
import com.ultragol.app.network.DramaShortsApi;
import com.ultragol.app.network.AnimeApi;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private HomeTvAdapter            tvAdapter;

    private final List<ContentItem>              results      = new ArrayList<>();
    private final List<ContentItem>              animeResults = new ArrayList<>();
    private final List<ContentItem>              tmdbResults  = new ArrayList<>();
    private final List<DramaShortsApi.VideoItem> shortsItems  = new ArrayList<>();
    private final List<TvChannel>                tvResults    = new ArrayList<>();

    private LinearLayout tvSection;

    private final Handler  handler         = new Handler();
    private       Runnable searchRunnable;
    private boolean        kidsMode        = false;

    // Track in-flight searches so late results don't overwrite newer ones
    private volatile int   searchSeq       = 0;
    private volatile boolean animeLoaded   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput   = findViewById(R.id.searchInput);
        resultsGrid   = findViewById(R.id.resultsGrid);
        shortsRow     = findViewById(R.id.shortsRow);
        shortsSection = findViewById(R.id.shortsSection);
        tvSection     = findViewById(R.id.tvSection);
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
        TvHelper.makeFocusable(resultsGrid);

        // Shorts Dramas horizontal row
        shortsAdapter = new DramaShortsRowAdapter(this, shortsItems);
        shortsRow.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        shortsRow.setAdapter(shortsAdapter);
        TvHelper.makeFocusable(shortsRow);

        // TV channels horizontal row
        RecyclerView tvRow = findViewById(R.id.tvRow);
        tvAdapter = new HomeTvAdapter(this, tvResults);
        tvAdapter.setOnClickListener(ch -> {
            Intent tvIntent = new Intent(this, MediaActivity.class);
            tvIntent.putExtra("url",     ch.url);
            tvIntent.putExtra("title",   ch.name);
            tvIntent.putExtra("is_m3u8", true);
            tvIntent.putExtra("referer", "");
            startActivity(tvIntent);
        });
        tvRow.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        tvRow.setAdapter(tvAdapter);
        TvHelper.makeFocusable(tvRow);

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
        // ── TV channel search (instant, local filter) ─────────────────────────
        doSearchTv(query);

        final int seq = ++searchSeq;
        animeLoaded = false;
        animeResults.clear();
        tmdbResults.clear();
        results.clear();
        adapter.notifyDataSetChanged();

        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (emptyState  != null) emptyState .setVisibility(View.GONE);

        // ── Anime search first: anime API uses slugs, never TMDB ids ───────────
        ExecutorService animeExec = Executors.newSingleThreadExecutor();
        animeExec.execute(() -> {
            List<ContentItem> anime = new ArrayList<>();
            try { anime = AnimeApi.search(query); } catch (Exception ignored) {}
            final List<ContentItem> animeResult = anime;
            runOnUiThread(() -> {
                if (seq != searchSeq) return;
                animeLoaded = true;
                animeResults.clear();
                animeResults.addAll(animeResult);
                mergeContentResults();
            });
        });
        animeExec.shutdown();

        // ── TMDB search (kept independent and appended after anime) ───────────
        ExecutorService tmdbExec = Executors.newSingleThreadExecutor();
        tmdbExec.execute(() -> {
            List<ContentItem> r = new ArrayList<>();
            try {
                r = kidsMode ? TmdbApi.searchMultiKids(query) : TmdbApi.searchMulti(query);
            } catch (Exception ignored) {}
            final List<ContentItem> tmdbResult = r;
            runOnUiThread(() -> {
                if (seq != searchSeq) return; // stale
                tmdbResults.clear();
                tmdbResults.addAll(tmdbResult);
                mergeContentResults();
                if (loadingView != null) loadingView.setVisibility(View.GONE);
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

    // ── TV search (instant, runs on UI thread) ────────────────────────────────

    private void doSearchTv(String query) {
        String q = query.toLowerCase(Locale.getDefault());
        List<TvChannel> all = TvFragment.getCachedChannels();
        List<TvChannel> matches = new ArrayList<>();
        for (TvChannel ch : all) {
            if (ch.name.toLowerCase(Locale.getDefault()).contains(q)
                    || (ch.category != null && ch.category.toLowerCase(Locale.getDefault()).contains(q))
                    || (ch.country  != null && ch.country.toLowerCase(Locale.getDefault()).contains(q))) {
                matches.add(ch);
                if (matches.size() >= 30) break; // max 30 TV results
            }
        }
        tvResults.clear();
        tvResults.addAll(matches);
        if (tvAdapter != null) tvAdapter.notifyDataSetChanged();
        if (tvSection != null) tvSection.setVisibility(matches.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateVisibility() {
        boolean hasResults = !results.isEmpty() || !shortsItems.isEmpty() || !tvResults.isEmpty();
        if (emptyState  != null) emptyState .setVisibility(hasResults ? View.GONE    : View.VISIBLE);
        if (scrollView  != null) scrollView .setVisibility(hasResults ? View.VISIBLE : View.GONE);
    }

    /**
     * Merge independent providers without allowing a late empty response from
     * one provider to erase valid results from another provider.
     */
    private void mergeContentResults() {
        results.clear();
        results.addAll(animeResults);
        results.addAll(tmdbResults);
        adapter.notifyDataSetChanged();
        updateVisibility();
    }

    private void showEmpty() {
        searchSeq++;
        results.clear();     adapter.notifyDataSetChanged();
        shortsItems.clear(); shortsAdapter.notifyDataSetChanged();
        tvResults.clear();   if (tvAdapter != null) tvAdapter.notifyDataSetChanged();
        if (loadingView   != null) loadingView  .setVisibility(View.GONE);
        if (emptyState    != null) emptyState   .setVisibility(View.VISIBLE);
        if (scrollView    != null) scrollView   .setVisibility(View.GONE);
        if (shortsSection != null) shortsSection.setVisibility(View.GONE);
        if (tvSection     != null) tvSection    .setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (TvHelper.handleGlobalKeyEvent(this, event)) return true;
        return super.dispatchKeyEvent(event);
    }
}
