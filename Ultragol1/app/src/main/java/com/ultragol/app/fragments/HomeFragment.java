package com.ultragol.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import androidx.viewpager2.widget.ViewPager2;
import com.ultragol.app.*;
import com.ultragol.app.adapters.*;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class HomeFragment extends Fragment {

    // Hero banner
    private ViewPager2 hero;
    private LinearLayout dots;
    private final Handler autoHandler = new Handler();
    private int bannerPage = 0, bannerCount = 0;
    private BannerAdapter bannerAdapter;
    private final List<ContentItem> bannerItems = new ArrayList<>();

    // Trending carousel
    private ViewPager2 trendingPager;
    private final List<ContentItem> trendingItems = new ArrayList<>();
    private TrendingAdapter trendingAdapter;
    private int trendingPage = 0;

    // Content rows
    private View rowTrending, rowTop10, rowNew, rowMovies, rowSeries, rowAnime, rowDoramas;
    private View rowLiveGlass;

    private static final String GOL3_API = "https://ultragol-api-3--maricarmen43549.replit.app/gol-3";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        setupTopBar(view);
        setupHero(view);
        setupTrendingCarousel(view);
        setupRows(view);
        loadAll();
    }

    private void setupTopBar(View view) {
        View search = view.findViewById(R.id.btnSearch);
        if (search != null) search.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), SearchActivity.class)));

        View bell = view.findViewById(R.id.btnBell);
        if (bell != null) bell.setOnClickListener(v -> {
            com.ultragol.app.NotificationsSheet sheet = new com.ultragol.app.NotificationsSheet();
            sheet.show(getParentFragmentManager(), "notifications");
        });

        View menu = view.findViewById(R.id.btnMenu);
        if (menu != null) menu.setOnClickListener(v -> {
            if (requireActivity() instanceof com.ultragol.app.MainActivity) {
                ((com.ultragol.app.MainActivity) requireActivity()).showMenu();
            }
        });
    }

    // ── Hero banner ──────────────────────────────────────────────────────────

    private void setupHero(View view) {
        hero = view.findViewById(R.id.heroBanner);
        dots = view.findViewById(R.id.bannerDots);
        bannerAdapter = new BannerAdapter(requireContext(), bannerItems);
        if (hero == null) return;
        hero.setAdapter(bannerAdapter);
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

    // ── Trending carousel ────────────────────────────────────────────────────

    private void setupTrendingCarousel(View view) {
        try {
            View carouselRoot = view.findViewById(R.id.rowTrendingCarousel);
            if (carouselRoot == null) return;

            trendingPager   = carouselRoot.findViewById(R.id.trendingPager);
            View btnPrev    = carouselRoot.findViewById(R.id.trendingPrev);
            View btnNext    = carouselRoot.findViewById(R.id.trendingNext);

            trendingAdapter = new TrendingAdapter(requireContext(), trendingItems);
            if (trendingPager == null) return;
            trendingPager.setAdapter(trendingAdapter);
            trendingPager.setOffscreenPageLimit(1);
            trendingPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override public void onPageSelected(int p) { trendingPage = p; }
            });

            if (btnPrev != null) btnPrev.setOnClickListener(v -> {
                if (trendingPage > 0) trendingPager.setCurrentItem(trendingPage - 1, true);
            });
            if (btnNext != null) btnNext.setOnClickListener(v -> {
                if (trendingPage < trendingItems.size() - 1)
                    trendingPager.setCurrentItem(trendingPage + 1, true);
            });
        } catch (Exception ignored) {}
    }

    // ── Content rows ─────────────────────────────────────────────────────────

    private void setupRows(View view) {
        rowTrending  = view.findViewById(R.id.rowTrending);
        rowTop10     = view.findViewById(R.id.rowTop10);
        rowNew       = view.findViewById(R.id.rowNew);
        rowMovies    = view.findViewById(R.id.rowMovies);
        rowSeries    = view.findViewById(R.id.rowSeries);
        rowAnime     = view.findViewById(R.id.rowAnime);
        rowDoramas   = view.findViewById(R.id.rowDoramas);
        rowLiveGlass = view.findViewById(R.id.rowLiveGlass);

        initRow(rowTrending, "Tendencias",          null);
        initRow(rowNew,      "Últimos Estrenos",    new MoviesFragment());
        initRow(rowMovies,   "Películas Populares", new MoviesFragment());
        initRow(rowSeries,   "Series Populares",    new SeriesFragment());
        initRow(rowAnime,    "Animes",              new AnimeFragment());
        initRow(rowDoramas,  "Doramas",             new DoramasFragment());
        initRow(rowTop10,    "Top 10",              new MoviesFragment());

        // Init live glass carousel RecyclerView
        if (rowLiveGlass != null) {
            RecyclerView rv = rowLiveGlass.findViewById(R.id.liveCarouselRv);
            if (rv != null) rv.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void initRow(View row, String title, Fragment verTodosTarget) {
        if (row == null) return;
        TextView tv   = row.findViewById(R.id.rowTitle);
        RecyclerView rv = row.findViewById(R.id.rowRv);
        View verTodos = row.findViewById(R.id.rowVerTodos);
        if (tv != null) tv.setText(title);
        if (rv != null) rv.setLayoutManager(
            new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        if (verTodos != null) {
            if (verTodosTarget != null) {
                verTodos.setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigate(verTodosTarget);
                    }
                });
            } else {
                verTodos.setVisibility(View.GONE);
            }
        }
    }

    // ── Load data ─────────────────────────────────────────────────────────────

    private void loadAll() {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        Handler h = new Handler(android.os.Looper.getMainLooper());

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchTrending();
            h.post(() -> {
                try {
                    if (!isAdded()) return;
                    // Hero banner (first 6)
                    bannerItems.clear();
                    bannerItems.addAll(r.size() > 6 ? r.subList(0, 6) : r);
                    if (bannerAdapter != null) bannerAdapter.notifyDataSetChanged();
                    bannerCount = bannerItems.size();
                    buildDots();
                    // Trending carousel (all results)
                    trendingItems.clear();
                    trendingItems.addAll(r);
                    if (trendingAdapter != null) trendingAdapter.notifyDataSetChanged();
                    // Small cards row
                    fillRow(rowTrending, r);
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchNewMovies();
            h.post(() -> { try { if (isAdded()) fillRow(rowNew, r); } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchMovies();
            h.post(() -> { try { if (isAdded()) fillRow(rowMovies, r); } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchSeries();
            h.post(() -> { try { if (isAdded()) fillRow(rowSeries, r); } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAnime();
            h.post(() -> { try { if (isAdded()) fillRow(rowAnime, r); } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchDoramas();
            h.post(() -> { try { if (isAdded()) fillRow(rowDoramas, r); } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchTopMovies();
            h.post(() -> { try { if (isAdded()) fillRow(rowTop10, r); } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        // Live glass carousel — independent, uses its own executor
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<LiveMatchServerDialog.LiveMatch> matches = fetchAndParseGol3();
                h.post(() -> {
                    try {
                        if (!isAdded() || rowLiveGlass == null) return;
                        RecyclerView rv = rowLiveGlass.findViewById(R.id.liveCarouselRv);
                        if (rv == null) return;
                        if (matches.isEmpty()) {
                            rowLiveGlass.setVisibility(View.GONE);
                        } else {
                            rv.setAdapter(new GlassLiveAdapter(requireContext(), matches));
                            rowLiveGlass.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ignored) {}
                });
            } catch (Exception ignored) {
                h.post(() -> { if (rowLiveGlass != null) rowLiveGlass.setVisibility(View.GONE); });
            }
        });

        pool.shutdown();
    }

    // ── Gol-3 fetch & parse ───────────────────────────────────────────────────

    private List<LiveMatchServerDialog.LiveMatch> fetchAndParseGol3() throws Exception {
        URL url = new URL(GOL3_API);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(10000);
        c.setReadTimeout(12000);
        if (c.getResponseCode() != 200) throw new Exception("HTTP " + c.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JSONObject root = new JSONObject(sb.toString());
        JSONArray arr   = root.optJSONArray("transmisiones");
        if (arr == null || arr.length() == 0) return Collections.emptyList();

        LinkedHashMap<String, List<String[]>> grouped = new LinkedHashMap<>();
        LinkedHashMap<String, JSONObject>     meta    = new LinkedHashMap<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.getJSONObject(i);
            String titulo   = item.optString("titulo", "Partido " + (i + 1));
            String canal    = item.optString("canal", "Servidor " + (i + 1));
            String streamUrl = item.optString("url", "");
            if (streamUrl.isEmpty()) continue;
            if (!grouped.containsKey(titulo)) {
                grouped.put(titulo, new ArrayList<>());
                meta.put(titulo, item);
            }
            grouped.get(titulo).add(new String[]{canal, streamUrl});
        }

        List<LiveMatchServerDialog.LiveMatch> result = new ArrayList<>();
        for (String titulo : grouped.keySet()) {
            JSONObject m = meta.get(titulo);
            result.add(new LiveMatchServerDialog.LiveMatch(
                titulo,
                m.optString("liga", ""),
                m.optString("hora", ""),
                m.optString("fecha", ""),
                m.optString("logoUrl", ""),
                grouped.get(titulo)
            ));
        }
        return result;
    }

    private void fillRow(View row, List<ContentItem> items) {
        if (row == null || !isAdded()) return;
        RecyclerView rv = row.findViewById(R.id.rowRv);
        if (rv != null) rv.setAdapter(new ContentRowAdapter(requireContext(), items));
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        autoHandler.removeCallbacksAndMessages(null);
    }
}
