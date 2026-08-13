package com.ultragol.app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ultragol.app.LiveMatchServerDialog;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.R;
import com.ultragol.app.TvHelper;
import com.ultragol.app.adapters.DeportesAdapter;
import com.ultragol.app.models.SportsHighlight;
import com.ultragol.app.models.SportsMatch;
import com.ultragol.app.network.SportsApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Native Deportes (sports) screen: league tabs, a featured live/upcoming
 * match, and EN VIVO / PRÓXIMOS PARTIDOS / MEJORES MOMENTOS tabs, all backed
 * by SportsApi. Replaces the old WebView-based Deportes tab.
 */
public class DeportesFragment extends Fragment {

    private static final int TAB_LIVE       = 0;
    private static final int TAB_UPCOMING   = 1;
    private static final int TAB_HIGHLIGHTS = 2;

    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    private final Handler ui = new Handler(Looper.getMainLooper());
    private volatile boolean destroyed = false;

    private RecyclerView rv;
    private DeportesAdapter adapter;

    private int leagueIndex   = 0;
    private boolean allLeagues = false;
    private int activeTab      = TAB_LIVE;

    private final List<SportsMatch> allMatches = new ArrayList<>();
    private List<SportsHighlight> highlightsCache = null;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_deportes, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        destroyed = false;

        View btnBack = view.findViewById(R.id.dsBtnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> {
            if (isAdded()) requireActivity().onBackPressed();
        });

        View btnRefresh = view.findViewById(R.id.dsBtnRefresh);
        if (btnRefresh != null) btnRefresh.setOnClickListener(v -> {
            v.animate().rotationBy(360f).setDuration(450).start();
            loadCurrentLeague();
        });

        rv = view.findViewById(R.id.rvDeportes);
        adapter = new DeportesAdapter(requireContext(), this::bindHeader);
        adapter.setOnMatchClick(this::onMatchTapped);
        adapter.setOnHighlightClick(this::onHighlightTapped);

        int spanCount = 2;
        GridLayoutManager glm = new GridLayoutManager(requireContext(), spanCount);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) { return position == 0 ? spanCount : 1; }
        });
        rv.setLayoutManager(glm);
        rv.setAdapter(adapter);
        TvHelper.makeFocusable(rv);

        loadCurrentLeague();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyed = true;
        pool.shutdownNow();
        ui.removeCallbacksAndMessages(null);
    }

    private boolean dead() { return destroyed || !isAdded() || getContext() == null; }

    // ── Data loading ─────────────────────────────────────────────────────────

    private void loadCurrentLeague() {
        if (dead()) return;
        setLoading(true);
        String prefix = SportsApi.LEAGUES[leagueIndex][1];
        boolean loadAll = allLeagues;
        pool.execute(() -> {
            List<SportsMatch> result;
            try {
                result = loadAll ? SportsApi.loadAllLeagues(requireContext().getApplicationContext())
                                  : SportsApi.loadLeague(requireContext().getApplicationContext(), prefix);
            } catch (Exception e) {
                result = new ArrayList<>();
            }
            final List<SportsMatch> finalResult = result;
            ui.post(() -> {
                if (dead()) return;
                allMatches.clear();
                allMatches.addAll(finalResult);
                setLoading(false);
                renderActiveTab();
            });
        });
    }

    private void loadHighlightsIfNeeded() {
        if (highlightsCache != null) { renderActiveTab(); return; }
        setLoading(true);
        pool.execute(() -> {
            List<SportsHighlight> result;
            try { result = SportsApi.fetchHighlights(); } catch (Exception e) { result = new ArrayList<>(); }
            final List<SportsHighlight> finalResult = result;
            ui.post(() -> {
                if (dead()) return;
                highlightsCache = finalResult;
                setLoading(false);
                renderActiveTab();
            });
        });
    }

    private void renderActiveTab() {
        if (dead()) return;
        if (activeTab == TAB_HIGHLIGHTS) {
            adapter.submitHighlights(highlightsCache != null ? highlightsCache : new ArrayList<>());
        } else {
            List<SportsMatch> filtered = new ArrayList<>();
            int wanted = activeTab == TAB_LIVE ? SportsMatch.STATUS_LIVE : SportsMatch.STATUS_UPCOMING;
            for (SportsMatch m : allMatches) if (m.status == wanted) filtered.add(m);
            adapter.submitMatches(filtered);
        }
        View v = getView();
        if (v != null) {
            TextView empty = v.findViewById(R.id.dsEmpty);
            if (empty != null) empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void setLoading(boolean loading) {
        View v = getView();
        if (v == null) return;
        ProgressBar pb = v.findViewById(R.id.dsLoading);
        if (pb != null) pb.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    // ── Header (league tabs, hero, banner, sub-tabs) ────────────────────────

    private void bindHeader(View header) {
        buildLeagueTabsOnce(header);
        updateLeagueTabsSelection(header);
        updateSubTabsSelection(header);
        bindHero(header);

        View banner = header.findViewById(R.id.dsBtnAllMatches);
        if (banner != null) banner.setOnClickListener(v -> {
            allLeagues = true;
            activeTab  = TAB_LIVE;
            loadCurrentLeague();
        });

        View tabLive = header.findViewById(R.id.dsTabLive);
        View tabUpcoming = header.findViewById(R.id.dsTabUpcoming);
        View tabHighlights = header.findViewById(R.id.dsTabHighlights);
        if (tabLive != null) tabLive.setOnClickListener(v -> { activeTab = TAB_LIVE; renderActiveTab(); adapter.notifyItemChanged(0); });
        if (tabUpcoming != null) tabUpcoming.setOnClickListener(v -> { activeTab = TAB_UPCOMING; renderActiveTab(); adapter.notifyItemChanged(0); });
        if (tabHighlights != null) tabHighlights.setOnClickListener(v -> { activeTab = TAB_HIGHLIGHTS; loadHighlightsIfNeeded(); adapter.notifyItemChanged(0); });
    }

    private void buildLeagueTabsOnce(View header) {
        LinearLayout tabs = header.findViewById(R.id.dsLeagueTabs);
        if (tabs == null || tabs.getChildCount() == SportsApi.LEAGUES.length) return;
        tabs.removeAllViews();
        for (int i = 0; i < SportsApi.LEAGUES.length; i++) {
            final int idx = i;
            TextView tv = new TextView(requireContext());
            tv.setText(SportsApi.LEAGUES[i][0].toUpperCase());
            tv.setTextSize(11.5f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setPadding(dp(16), dp(9), dp(16), dp(9));
            tv.setClickable(true);
            tv.setFocusable(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            tv.setLayoutParams(lp);
            tv.setTag("league_chip");
            tv.setOnClickListener(v -> {
                leagueIndex = idx;
                allLeagues  = false;
                activeTab   = TAB_LIVE;
                loadCurrentLeague();
            });
            tabs.addView(tv);
        }
    }

    private void updateLeagueTabsSelection(View header) {
        LinearLayout tabs = header.findViewById(R.id.dsLeagueTabs);
        if (tabs == null) return;
        for (int i = 0; i < tabs.getChildCount(); i++) {
            View child = tabs.getChildAt(i);
            if (!(child instanceof TextView)) continue;
            boolean active = !allLeagues && i == leagueIndex;
            child.setBackgroundResource(active ? R.drawable.sport_league_tab_active : R.drawable.sport_league_tab_inactive);
            ((TextView) child).setTextColor(active ? 0xFFFFFFFF : 0xCCFFFFFF);
        }
    }

    private void updateSubTabsSelection(View header) {
        TextView tabLive = header.findViewById(R.id.dsTabLive);
        TextView tabUpcoming = header.findViewById(R.id.dsTabUpcoming);
        TextView tabHighlights = header.findViewById(R.id.dsTabHighlights);
        setTabState(tabLive, activeTab == TAB_LIVE);
        setTabState(tabUpcoming, activeTab == TAB_UPCOMING);
        setTabState(tabHighlights, activeTab == TAB_HIGHLIGHTS);
    }

    private void setTabState(TextView tv, boolean active) {
        if (tv == null) return;
        tv.setBackgroundResource(active ? R.drawable.sport_section_chip_active : R.drawable.sport_section_chip_inactive);
        tv.setTextColor(active ? 0xFFFFFFFF : 0xCCFFFFFF);
    }

    private void bindHero(View header) {
        View hero = header.findViewById(R.id.dsHero);
        if (hero == null) return;

        SportsMatch featured = null;
        for (SportsMatch m : allMatches) if (m.status == SportsMatch.STATUS_LIVE) { featured = m; break; }
        if (featured == null) for (SportsMatch m : allMatches) if (m.status == SportsMatch.STATUS_UPCOMING) { featured = m; break; }

        if (featured == null) {
            hero.setVisibility(View.GONE);
            return;
        }
        hero.setVisibility(View.VISIBLE);
        final SportsMatch match = featured;

        TextView badge = header.findViewById(R.id.dsHeroBadge);
        if (badge != null) {
            boolean live = match.status == SportsMatch.STATUS_LIVE;
            badge.setText(live ? "● EN VIVO" : "● PRÓXIMO");
            badge.setBackgroundColor(live ? Color.parseColor("#E53935") : Color.parseColor("#33FFFFFF"));
        }
        TextView home = header.findViewById(R.id.dsHeroHome);
        TextView away = header.findViewById(R.id.dsHeroAway);
        if (home != null) home.setText(match.homeTeam);
        if (away != null) away.setText(match.awayTeam);

        TextView score = header.findViewById(R.id.dsHeroScore);
        if (score != null) score.setText(match.hasScore() ? match.homeScore + " - " + match.awayScore : "VS");

        android.widget.ImageView logoHome = header.findViewById(R.id.dsHeroLogoHome);
        android.widget.ImageView logoAway = header.findViewById(R.id.dsHeroLogoAway);
        if (logoHome != null && !match.homeLogo.isEmpty()) Glide.with(this).load(match.homeLogo).into(logoHome);
        if (logoAway != null && !match.awayLogo.isEmpty()) Glide.with(this).load(match.awayLogo).into(logoAway);

        View watch = header.findViewById(R.id.dsHeroWatch);
        if (watch != null) watch.setOnClickListener(v -> onMatchTapped(match));
    }

    // ── Taps → PlayerActivity / server dialog ───────────────────────────────

    private void onMatchTapped(SportsMatch match) {
        if (dead()) return;
        if (match.servers.isEmpty()) {
            Toast.makeText(requireContext(), "Aún no hay transmisión disponible para este partido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (match.servers.size() == 1) {
            Intent it = new Intent(requireContext(), PlayerActivity.class);
            it.putExtra("url", match.servers.get(0)[1]);
            it.putExtra("title", match.matchTitle());
            startActivity(it);
            return;
        }
        LiveMatchServerDialog.LiveMatch lm = new LiveMatchServerDialog.LiveMatch(
            match.matchTitle(), match.league, match.time, match.date,
            !match.homeLogo.isEmpty() ? match.homeLogo : match.awayLogo,
            match.servers);
        LiveMatchServerDialog.show(requireContext(), lm);
    }

    private void onHighlightTapped(SportsHighlight highlight) {
        if (dead()) return;
        Intent it = new Intent(requireContext(), PlayerActivity.class);
        it.putExtra("url", highlight.url);
        it.putExtra("title", highlight.title);
        startActivity(it);
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }
}
