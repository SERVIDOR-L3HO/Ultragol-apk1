package com.ultragol.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.ultragol.app.R;
import com.ultragol.app.adapters.BannerAdapter;
import com.ultragol.app.adapters.ContentRowAdapter;
import com.ultragol.app.models.ContentData;

public class HomeFragment extends Fragment {

    private ViewPager2 heroBanner;
    private LinearLayout bannerIndicators;
    private Handler autoScrollHandler = new Handler();
    private int currentBannerPage = 0;
    private int bannerCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupBanner(view);
        setupRows(view);
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
        setupRow(view, R.id.rowTrending, getString(R.string.section_trending), ContentData.getTrending());
        setupRow(view, R.id.rowNew, getString(R.string.section_new), ContentData.getNewReleases());
        setupRow(view, R.id.rowRecommended, getString(R.string.section_recommended), ContentData.getMovies());
        setupRow(view, R.id.rowAnime, getString(R.string.section_anime), ContentData.getAnime());
        setupRow(view, R.id.rowSports, getString(R.string.section_sports), ContentData.getSports());
    }

    private void setupRow(View parentView, int rowId, String title, java.util.List<com.streamnova.app.models.ContentItem> items) {
        View rowView = parentView.findViewById(rowId);
        if (rowView == null) return;

        TextView sectionTitle = rowView.findViewById(R.id.sectionTitle);
        RecyclerView recyclerView = rowView.findViewById(R.id.rowRecyclerView);

        if (sectionTitle != null) sectionTitle.setText(title);

        if (recyclerView != null) {
            LinearLayoutManager lm = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(lm);
            recyclerView.setAdapter(new ContentRowAdapter(requireContext(), items));
            recyclerView.setHasFixedSize(false);
        }
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoScrollHandler.removeCallbacksAndMessages(null);
    }
}
