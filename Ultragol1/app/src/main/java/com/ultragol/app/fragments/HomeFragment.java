package com.ultragol.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import androidx.viewpager2.widget.ViewPager2;
import com.ultragol.app.*;
import com.ultragol.app.adapters.*;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.*;
import java.util.concurrent.*;

public class HomeFragment extends Fragment {
    private ViewPager2 hero;
    private LinearLayout dots;
    private final Handler autoHandler = new Handler();
    private int bannerPage = 0, bannerCount = 0;
    private BannerAdapter bannerAdapter;
    private final List<ContentItem> bannerItems = new ArrayList<>();
    private View rowTrending, rowTop10, rowNew, rowMovies, rowSeries, rowAnime, rowDoramas;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        setupHero(view);
        setupRows(view);
        setupTopBar(view);
        loadAll();
    }

    private void setupTopBar(View view) {
        View search  = view.findViewById(R.id.btnSearch);
        View profile = view.findViewById(R.id.btnProfile);
        if (search  != null) search.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), SearchActivity.class)));
        if (profile != null) profile.setOnClickListener(v ->
            Toast.makeText(requireContext(), "Perfil", Toast.LENGTH_SHORT).show());
    }

    private void setupHero(View view) {
        hero = view.findViewById(R.id.heroBanner);
        dots = view.findViewById(R.id.bannerDots);
        bannerAdapter = new BannerAdapter(requireContext(), bannerItems);
        if (hero == null) return;
        hero.setAdapter(bannerAdapter);
        hero.setPageTransformer((page, pos) -> {
            float abs = Math.abs(pos);
            page.setAlpha(1 - abs * 0.25f);
            page.setTranslationX(page.getWidth() * pos * -0.12f);
        });
        hero.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int p) { bannerPage = p; updateDots(p); }
        });
        autoHandler.postDelayed(autoScroll, 5000);
    }

    private final Runnable autoScroll = new Runnable() {
        @Override public void run() {
            if (hero != null && bannerCount > 0) {
                bannerPage = (bannerPage + 1) % bannerCount;
                hero.setCurrentItem(bannerPage, true);
                autoHandler.postDelayed(this, 5000);
            }
        }
    };

    private void buildDots() {
        if (dots == null) return;
        dots.removeAllViews();
        for (int i = 0; i < bannerCount; i++) {
            View d = new View(requireContext());
            int sz = i == 0 ? dp(10) : dp(6);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(sz, sz);
            p.setMargins(dp(4), 0, dp(4), 0);
            d.setLayoutParams(p);
            d.setBackgroundResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
            dots.addView(d);
        }
    }

    private void updateDots(int active) {
        if (dots == null) return;
        for (int i = 0; i < dots.getChildCount(); i++) {
            View d = dots.getChildAt(i);
            boolean a = i == active;
            int sz = a ? dp(10) : dp(6);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(sz, sz);
            p.setMargins(dp(4), 0, dp(4), 0);
            d.setLayoutParams(p);
            d.setBackgroundResource(a ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void setupRows(View view) {
        rowTrending = view.findViewById(R.id.rowTrending);
        rowTop10    = view.findViewById(R.id.rowTop10);
        rowNew      = view.findViewById(R.id.rowNew);
        rowMovies   = view.findViewById(R.id.rowMovies);
        rowSeries   = view.findViewById(R.id.rowSeries);
        rowAnime    = view.findViewById(R.id.rowAnime);
        rowDoramas  = view.findViewById(R.id.rowDoramas);
        initRow(rowTrending, "🔥 Tendencias"); initRow(rowTop10, "🏆 Top 10");
        initRow(rowNew, "✨ Estrenos"); initRow(rowMovies, "🎬 Películas");
        initRow(rowSeries, "📺 Series"); initRow(rowAnime, "🎌 Anime");
        initRow(rowDoramas, "🌸 Doramas");
    }

    private void initRow(View row, String title) {
        if (row == null) return;
        TextView tv = row.findViewById(R.id.rowTitle);
        RecyclerView rv = row.findViewById(R.id.rowRv);
        if (tv != null) tv.setText(title);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadAll() {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        Handler h = new Handler(android.os.Looper.getMainLooper());

        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchTrending();
            h.post(() -> { if(isAdded()){ bannerItems.clear(); if(r.size()>5)bannerItems.addAll(r.subList(0,5)); else bannerItems.addAll(r);
                bannerAdapter.notifyDataSetChanged(); bannerCount=bannerItems.size(); buildDots(); fillRow(rowTrending, r); }}); } catch(Exception ignored){}});
        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchTopMovies();
            h.post(() -> { if(isAdded()) fillRow(rowTop10, r); }); } catch(Exception ignored){}});
        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchNewMovies();
            h.post(() -> { if(isAdded()) fillRow(rowNew, r); }); } catch(Exception ignored){}});
        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchMovies();
            h.post(() -> { if(isAdded()) fillRow(rowMovies, r); }); } catch(Exception ignored){}});
        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchSeries();
            h.post(() -> { if(isAdded()) fillRow(rowSeries, r); }); } catch(Exception ignored){}});
        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchAnime();
            h.post(() -> { if(isAdded()) fillRow(rowAnime, r); }); } catch(Exception ignored){}});
        pool.execute(() -> { try { List<ContentItem> r = TmdbApi.fetchDoramas();
            h.post(() -> { if(isAdded()) fillRow(rowDoramas, r); }); } catch(Exception ignored){}});
        pool.shutdown();
    }

    private void fillRow(View row, List<ContentItem> items) {
        if (row == null || !isAdded()) return;
        RecyclerView rv = row.findViewById(R.id.rowRv);
        if (rv != null) rv.setAdapter(new ContentRowAdapter(requireContext(), items));
    }

    private int dp(int v) { return Math.round(v * requireContext().getResources().getDisplayMetrics().density); }

    @Override public void onDestroyView() { super.onDestroyView(); autoHandler.removeCallbacksAndMessages(null); }
}
