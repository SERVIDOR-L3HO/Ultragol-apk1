package com.ultragol.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.R;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.adapters.ChannelAdapter;
import com.ultragol.app.models.Channel;
import com.ultragol.app.network.StreamingApi;
import java.util.*;
import java.util.concurrent.Executors;

public class SportsFragment extends Fragment {
    private ChannelAdapter adapter;
    private final List<Channel> allChannels = new ArrayList<>();
    private final List<Channel> filtered    = new ArrayList<>();
    private View loadingView, emptyView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_sports, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        loadingView = view.findViewById(R.id.sportsLoading);
        emptyView   = view.findViewById(R.id.sportsEmpty);

        RecyclerView grid = view.findViewById(R.id.channelsGrid);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ChannelAdapter(requireContext(), filtered, ch -> {
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra("url", ch.getPlayerUrl());
            intent.putExtra("title", ch.getDisplayName());
            startActivity(intent);
        });
        grid.setAdapter(adapter);

        // Category chips
        setupCategoryChips(view);

        // Search
        EditText search = view.findViewById(R.id.sportsSearch);
        if (search != null) {
            search.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) { filterChannels(s.toString()); }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        loadChannels("sports");
    }

    private void setupCategoryChips(View view) {
        String[] cats = {"sports", "news", "entertainment", "music", "movies", "kids"};
        String[] labels = {"⚽ Deportes", "📰 Noticias", "🎭 Entretenimiento", "🎵 Música", "🎬 Películas", "👶 Niños"};
        LinearLayout chipContainer = view.findViewById(R.id.categoryChips);
        if (chipContainer == null) return;
        for (int i = 0; i < cats.length; i++) {
            final String cat = cats[i];
            TextView chip = new TextView(requireContext());
            chip.setText(labels[i]);
            chip.setTextSize(12); chip.setTextColor(i == 0 ? 0xFFFFFFFF : 0xFF909099);
            chip.setBackgroundResource(i == 0 ? R.drawable.tab_active : R.drawable.tab_inactive);
            chip.setPadding(dp(14), dp(8), dp(14), dp(8));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            chip.setLayoutParams(lp);
            chip.setClickable(true); chip.setFocusable(true);
            chip.setOnClickListener(v -> {
                for (int j = 0; j < chipContainer.getChildCount(); j++) {
                    View c = chipContainer.getChildAt(j);
                    c.setBackgroundResource(R.drawable.tab_inactive);
                    if (c instanceof TextView) ((TextView) c).setTextColor(0xFF909099);
                }
                chip.setBackgroundResource(R.drawable.tab_active);
                chip.setTextColor(0xFFFFFFFF);
                loadChannels(cat);
            });
            chipContainer.addView(chip);
        }
    }

    private void loadChannels(String category) {
        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (emptyView   != null) emptyView.setVisibility(View.GONE);
        allChannels.clear(); filtered.clear(); adapter.notifyDataSetChanged();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Channel> r = StreamingApi.fetchAllChannels(category);
                requireActivity().runOnUiThread(() -> {
                    allChannels.addAll(r); filtered.addAll(r);
                    adapter.notifyDataSetChanged();
                    if (loadingView != null) loadingView.setVisibility(View.GONE);
                    if (emptyView   != null) emptyView.setVisibility(r.isEmpty() ? View.VISIBLE : View.GONE);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    if (loadingView != null) loadingView.setVisibility(View.GONE);
                    if (emptyView   != null) emptyView.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void filterChannels(String query) {
        filtered.clear();
        if (query.isEmpty()) { filtered.addAll(allChannels); }
        else {
            String q = query.toLowerCase();
            for (Channel ch : allChannels)
                if (ch.getName().toLowerCase().contains(q) || ch.getCountry().toLowerCase().contains(q))
                    filtered.add(ch);
        }
        adapter.notifyDataSetChanged();
    }

    private int dp(int v) { return Math.round(v * requireContext().getResources().getDisplayMetrics().density); }
}
