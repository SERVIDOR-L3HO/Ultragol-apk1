package com.ultragol.app.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.R;
import com.ultragol.app.adapters.CineCardAdapter;
import com.ultragol.app.adapters.CineHeroAdapter;
import com.ultragol.app.models.ContentItem;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public abstract class CineBaseFragment extends Fragment {

    protected LinearLayout container;
    protected ProgressBar  loading;
    private boolean firstSectionLoaded = false;

    protected abstract String getFragmentTitle();
    protected abstract String getHeroPillLabel();
    protected abstract String getCardTypeLabel();
    protected abstract void   loadAllSections();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_cine, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        TextView titleView = view.findViewById(R.id.cineTitle);
        if (titleView != null) titleView.setText(getFragmentTitle());
        view.findViewById(R.id.cineBack).setOnClickListener(v -> requireActivity().onBackPressed());
        container = view.findViewById(R.id.cineContainer);
        loading   = view.findViewById(R.id.cineLoading);
        if (loading != null) loading.setVisibility(View.VISIBLE);
        loadAllSections();
    }

    protected void loadHeroSection(String subLabel, String mainTitle, int accentColor,
                                   Callable<List<ContentItem>> fetcher) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<ContentItem> items = fetcher.call();
                if (items == null || items.isEmpty()) return;
                List<ContentItem> heroItems = items.subList(0, Math.min(5, items.size()));
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    hideLoadingOnce();
                    addHeroHeader(subLabel, mainTitle, accentColor);
                    addHeroRecyclerView(heroItems);
                });
            } catch (Exception ignored) {}
        });
    }

    protected void loadCardsSection(String subLabel, String mainTitle, int accentColor,
                                    Callable<List<ContentItem>> fetcher) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<ContentItem> items = fetcher.call();
                if (items == null || items.isEmpty()) return;
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    hideLoadingOnce();
                    addSectionView(subLabel, mainTitle, accentColor, items);
                });
            } catch (Exception ignored) {}
        });
    }

    private void hideLoadingOnce() {
        if (!firstSectionLoaded && loading != null) {
            loading.setVisibility(View.GONE);
            firstSectionLoaded = true;
        }
    }

    private void addHeroHeader(String subLabel, String mainTitle, int accentColor) {
        View header = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_cine_hero_header, container, false);

        TextView playIcon  = header.findViewById(R.id.heroHeaderPlayIcon);
        TextView subLbl    = header.findViewById(R.id.heroHeaderSubLabel);
        TextView mainTitle_ = header.findViewById(R.id.heroHeaderMainTitle);

        if (playIcon  != null) playIcon.setTextColor(accentColor);
        if (subLbl    != null) subLbl.setText(subLabel);
        if (mainTitle_ != null) mainTitle_.setText(mainTitle);

        container.addView(header);
    }

    private void addHeroRecyclerView(List<ContentItem> items) {
        RecyclerView rv = new RecyclerView(requireContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        rv.setLayoutParams(lp);
        rv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(new CineHeroAdapter(requireContext(), items, getHeroPillLabel()));
        container.addView(rv);
    }

    private void addSectionView(String subLabel, String mainTitle, int accentColor,
                                List<ContentItem> items) {
        View section = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_cine_section, container, false);

        View     accentBar  = section.findViewById(R.id.sectionAccentBar);
        TextView playIcon   = section.findViewById(R.id.sectionPlayIcon);
        TextView subLbl     = section.findViewById(R.id.sectionSubLabel);
        TextView mainLbl    = section.findViewById(R.id.sectionMainTitle);
        TextView navPrev    = section.findViewById(R.id.sectionNavPrev);
        TextView navNext    = section.findViewById(R.id.sectionNavNext);
        RecyclerView rv     = section.findViewById(R.id.sectionRv);

        if (accentBar != null) accentBar.setBackgroundColor(accentColor);
        if (playIcon  != null) playIcon.setTextColor(accentColor);
        if (subLbl    != null) subLbl.setText(subLabel);
        if (mainLbl   != null) mainLbl.setText(mainTitle);

        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            rv.setAdapter(new CineCardAdapter(requireContext(), items, getCardTypeLabel()));

            int pageWidthPx = dpToPx(170 * 2);

            if (navPrev != null) {
                navPrev.setOnClickListener(v -> {
                    rv.smoothScrollBy(-pageWidthPx, 0);
                });
            }
            if (navNext != null) {
                navNext.setOnClickListener(v -> {
                    rv.smoothScrollBy(pageWidthPx, 0);
                });
            }
        }

        container.addView(section);
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
