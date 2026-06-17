package com.ultragol.app.fragments;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.ultragol.app.MyListBottomSheet;
import com.ultragol.app.NotificationBottomSheet;
import com.ultragol.app.ProfileBottomSheet;
import com.ultragol.app.R;
import com.ultragol.app.SearchActivity;
import com.ultragol.app.adapters.BannerAdapter;
import com.ultragol.app.adapters.ContentRowAdapter;
import com.ultragol.app.models.ContentData;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 heroBanner;
    private LinearLayout bannerIndicators;
    private Handler autoScrollHandler = new Handler();
    private int currentBannerPage = 0;
    private int bannerCount = 0;

    private View rowTrending, rowNew, rowRecommended, rowAnime, rowSports;

    private TextView chipAll, chipSports, chipMovies, chipSeries, chipAnime, chipDoramas;
    private TextView activeChip;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupBanner(view);
        setupRows(view);
        setupTopIcons(view);
        setupChips(view);
    }

    private void setupTopIcons(View view) {
        view.findViewById(R.id.searchIcon).setOnClickListener(v -> {
            animatePulse(v);
            startActivity(new Intent(requireContext(), SearchActivity.class));
        });

        view.findViewById(R.id.notifIcon).setOnClickListener(v -> {
            animatePulse(v);
            NotificationBottomSheet.newInstance()
                    .show(requireActivity().getSupportFragmentManager(), "notifs");
        });

        view.findViewById(R.id.profileIcon).setOnClickListener(v -> {
            animatePulse(v);
            new ProfileBottomSheet()
                .setOnMyListClick(this::showMyList)
                .show(requireActivity().getSupportFragmentManager(), "profile");
        });
    }

    private void showMyList() {
        MyListBottomSheet.newInstance()
                .show(requireActivity().getSupportFragmentManager(), "mylist");
    }

    private void setupChips(View view) {
        chipAll     = view.findViewById(R.id.chipAll);
        chipSports  = view.findViewById(R.id.chipSports);
        chipMovies  = view.findViewById(R.id.chipMovies);
        chipSeries  = view.findViewById(R.id.chipSeries);
        chipAnime   = view.findViewById(R.id.chipAnime);
        chipDoramas = view.findViewById(R.id.chipDoramas);
        activeChip  = chipAll;

        chipAll.setOnClickListener(v -> selectChip(chipAll, "all", view));
        chipSports.setOnClickListener(v -> selectChip(chipSports, "sports", view));
        chipMovies.setOnClickListener(v -> selectChip(chipMovies, "movies", view));
        chipSeries.setOnClickListener(v -> selectChip(chipSeries, "series", view));
        chipAnime.setOnClickListener(v -> selectChip(chipAnime, "anime", view));
        chipDoramas.setOnClickListener(v -> selectChip(chipDoramas, "doramas", view));
    }

    private void selectChip(TextView chip, String filter, View root) {
        if (chip == activeChip) return;
        animatePulse(chip);
        activeChip.setBackgroundResource(R.drawable.chip_inactive);
        activeChip.setTextColor(0xFFB0B0CC);
        chip.setBackgroundResource(R.drawable.chip_active);
        chip.setTextColor(0xFFFFFFFF);
        activeChip = chip;
        applyFilter(filter, root);
    }

    private void applyFilter(String filter, View root) {
        switch (filter) {
            case "sports":
                showRows(false, false, false, false, true);
                refreshRow(rowSports, "⚽ Deportes en Vivo", ContentData.getSports());
                break;
            case "movies":
                showRows(false, false, true, false, false);
                refreshRow(rowRecommended, "🎬 Películas", ContentData.getMovies());
                break;
            case "series":
                showRows(false, true, false, false, false);
                refreshRow(rowNew, "📺 Series", ContentData.getSeries());
                break;
            case "anime":
                showRows(false, false, false, true, false);
                refreshRow(rowAnime, "⚡ Anime", ContentData.getAnime());
                break;
            case "doramas":
                showRows(false, false, false, true, false);
                refreshRow(rowAnime, "🌸 Doramas", ContentData.getDoramas());
                break;
            default:
                showRows(true, true, true, true, true);
                refreshRow(rowTrending,   getString(R.string.section_trending),     ContentData.getTrending());
                refreshRow(rowNew,        getString(R.string.section_new),          ContentData.getNewReleases());
                refreshRow(rowRecommended,getString(R.string.section_recommended),  ContentData.getMovies());
                refreshRow(rowAnime,      getString(R.string.section_anime),        ContentData.getAnime());
                refreshRow(rowSports,     getString(R.string.section_sports),       ContentData.getSports());
                break;
        }
    }

    private void showRows(boolean trending, boolean newR, boolean recommended, boolean anime, boolean sports) {
        animateRowVisibility(rowTrending,   trending);
        animateRowVisibility(rowNew,        newR);
        animateRowVisibility(rowRecommended,recommended);
        animateRowVisibility(rowAnime,      anime);
        animateRowVisibility(rowSports,     sports);
    }

    private void animateRowVisibility(View row, boolean show) {
        if (row == null) return;
        if (show) {
            row.setVisibility(View.VISIBLE);
            row.animate().alpha(1f).translationY(0f).setDuration(250)
                    .setInterpolator(new DecelerateInterpolator()).start();
        } else {
            row.animate().alpha(0f).translationY(20f).setDuration(200)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> row.setVisibility(View.GONE)).start();
        }
    }

    private void refreshRow(View rowView, String title, List<ContentItem> items) {
        if (rowView == null) return;
        TextView sectionTitle = rowView.findViewById(R.id.sectionTitle);
        RecyclerView rv = rowView.findViewById(R.id.rowRecyclerView);
        if (sectionTitle != null) sectionTitle.setText(title);
        if (rv != null) {
            rv.setAdapter(new ContentRowAdapter(requireContext(), items));
        }
    }

    private void setupBanner(View view) {
        heroBanner = view.findViewById(R.id.heroBanner);
        bannerIndicators = view.findViewById(R.id.bannerIndicators);

        BannerAdapter bannerAdapter = new BannerAdapter(requireContext(), ContentData.getHeroBanners());
        heroBanner.setAdapter(bannerAdapter);
        bannerCount = ContentData.getHeroBanners().size();

        heroBanner.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1 - absPos * 0.3f);
            page.setScaleY(1 - absPos * 0.05f);
        });

        setupIndicators();

        heroBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentBannerPage = position;
                updateIndicators(position);
            }
        });

        startAutoScroll();
    }

    private void setupIndicators() {
        bannerIndicators.removeAllViews();
        for (int i = 0; i < bannerCount; i++) {
            View dot = new View(requireContext());
            int size = i == 0 ? dpToPx(8) : dpToPx(6);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0 ? R.drawable.dot_indicator : R.drawable.dot_inactive);
            bannerIndicators.addView(dot);
        }
    }

    private void updateIndicators(int activePos) {
        if (bannerIndicators == null) return;
        for (int i = 0; i < bannerIndicators.getChildCount(); i++) {
            View dot = bannerIndicators.getChildAt(i);
            boolean active = i == activePos;
            int size = active ? dpToPx(8) : dpToPx(6);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(active ? R.drawable.dot_indicator : R.drawable.dot_inactive);
        }
    }

    private void startAutoScroll() {
        autoScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (heroBanner != null && bannerCount > 0) {
                    currentBannerPage = (currentBannerPage + 1) % bannerCount;
                    heroBanner.setCurrentItem(currentBannerPage, true);
                    autoScrollHandler.postDelayed(this, 4000);
                }
            }
        }, 4000);
    }

    private void setupRows(View view) {
        rowTrending   = view.findViewById(R.id.rowTrending);
        rowNew        = view.findViewById(R.id.rowNew);
        rowRecommended= view.findViewById(R.id.rowRecommended);
        rowAnime      = view.findViewById(R.id.rowAnime);
        rowSports     = view.findViewById(R.id.rowSports);

        setupRow(rowTrending,   getString(R.string.section_trending),    ContentData.getTrending());
        setupRow(rowNew,        getString(R.string.section_new),         ContentData.getNewReleases());
        setupRow(rowRecommended,getString(R.string.section_recommended), ContentData.getMovies());
        setupRow(rowAnime,      getString(R.string.section_anime),       ContentData.getAnime());
        setupRow(rowSports,     getString(R.string.section_sports),      ContentData.getSports());
    }

    private void setupRow(View rowView, String title, List<ContentItem> items) {
        if (rowView == null) return;
        TextView sectionTitle = rowView.findViewById(R.id.sectionTitle);
        RecyclerView recyclerView = rowView.findViewById(R.id.rowRecyclerView);
        if (sectionTitle != null) sectionTitle.setText(title);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(new ContentRowAdapter(requireContext(), items));
            recyclerView.setHasFixedSize(false);
        }
    }

    private void animatePulse(View view) {
        view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80)
            .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
            .start();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoScrollHandler.removeCallbacksAndMessages(null);
    }
}
