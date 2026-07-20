package com.ultragol.app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.R;
import com.ultragol.app.adapters.ContentRowAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.*;
import java.util.concurrent.*;

public class AdultFragment extends Fragment {

    private View rowPopular, rowRomance, rowThriller, rowDrama,
                 rowTopRated, rowSpanish, rowSeries, rowNoir;
    private ProgressBar loading;

    // Category chips data
    private static final String[][] CHIPS = {
        {"🔥 Popular",      "popular"},
        {"💋 Romance",      "romance"},
        {"🔪 Thriller",     "thriller"},
        {"🎭 Drama",        "drama"},
        {"⭐ Top Rated",    "top"},
        {"🌎 En Español",   "spanish"},
        {"📺 Series",       "series"},
        {"🕵️ Noir",         "noir"},
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adult, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button
        View back = view.findViewById(R.id.adultBack);
        if (back != null) back.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        loading   = view.findViewById(R.id.adultLoading);
        rowPopular  = view.findViewById(R.id.rowAdultPopular);
        rowRomance  = view.findViewById(R.id.rowAdultRomance);
        rowThriller = view.findViewById(R.id.rowAdultThriller);
        rowDrama    = view.findViewById(R.id.rowAdultDrama);
        rowTopRated = view.findViewById(R.id.rowAdultTopRated);
        rowSpanish  = view.findViewById(R.id.rowAdultSpanish);
        rowSeries   = view.findViewById(R.id.rowAdultSeries);
        rowNoir     = view.findViewById(R.id.rowAdultNoir);

        // Build category chips
        buildChips(view);

        // Init all rows (horizontal scroll)
        initRow(rowPopular,  "🔥 Más Populares");
        initRow(rowRomance,  "💋 Romance & Pasión");
        initRow(rowThriller, "🔪 Thriller Adulto");
        initRow(rowDrama,    "🎭 Drama Intenso");
        initRow(rowTopRated, "⭐ Mejor Calificados");
        initRow(rowSpanish,  "🌎 En Español");
        initRow(rowSeries,   "📺 Series Maduras");
        initRow(rowNoir,     "🕵️ Crimen & Misterio");

        loadAll();
    }

    private void buildChips(View view) {
        LinearLayout chipRow = view.findViewById(R.id.chipRow);
        if (chipRow == null || !isAdded()) return;

        View[] rows = {rowPopular, rowRomance, rowThriller, rowDrama,
                        rowTopRated, rowSpanish, rowSeries, rowNoir};

        for (int i = 0; i < CHIPS.length; i++) {
            final String label = CHIPS[i][0];
            final View targetRow = rows[i];

            TextView chip = new TextView(requireContext());
            chip.setText(label);
            chip.setTextColor(Color.WHITE);
            chip.setTextSize(12f);
            chip.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            chip.setPadding(dp(14), dp(7), dp(14), dp(7));
            chip.setBackgroundResource(R.drawable.ver_todos_bg);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, dp(8), 0);
            chip.setLayoutParams(lp);

            chip.setOnClickListener(v -> {
                if (targetRow != null) {
                    androidx.core.widget.NestedScrollView nsv =
                        view.findViewById(com.ultragol.app.R.id.adultLoading) != null
                        ? (androidx.core.widget.NestedScrollView) targetRow.getParent().getParent()
                        : null;
                    if (nsv != null) {
                        int[] loc = new int[2];
                        targetRow.getLocationOnScreen(loc);
                        int[] nsvLoc = new int[2];
                        nsv.getLocationOnScreen(nsvLoc);
                        nsv.smoothScrollTo(0, (loc[1] - nsvLoc[1]) + nsv.getScrollY() - dp(16));
                    }
                }
            });

            chipRow.addView(chip);
        }
    }

    private void initRow(View row, String title) {
        if (row == null) return;
        TextView tv = row.findViewById(R.id.rowTitle);
        RecyclerView rv = row.findViewById(R.id.rowRv);
        View verTodos = row.findViewById(R.id.rowVerTodos);
        if (tv != null) tv.setText(title);
        if (verTodos != null) verTodos.setVisibility(View.GONE);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
            rv.setItemViewCacheSize(6);
        }
        // Initially hide until data arrives
        row.setVisibility(View.GONE);
    }

    private void fillRow(View row, List<ContentItem> items) {
        if (row == null || !isAdded() || items == null || items.isEmpty()) return;
        RecyclerView rv = row.findViewById(R.id.rowRv);
        if (rv != null) rv.setAdapter(new ContentRowAdapter(requireContext(), items));
        row.setVisibility(View.VISIBLE);
    }

    private void loadAll() {
        if (!isAdded()) return;
        if (loading != null) loading.setVisibility(View.VISIBLE);

        Handler h = new Handler(android.os.Looper.getMainLooper());
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // Hide loading after first row arrives
        final boolean[] firstLoaded = {false};
        Runnable hideLoading = () -> {
            if (!firstLoaded[0]) {
                firstLoaded[0] = true;
                if (loading != null) loading.setVisibility(View.GONE);
            }
        };

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdult();
            h.post(() -> { try { if (isAdded()) { fillRow(rowPopular, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) { h.post(hideLoading); } });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultRomance();
            h.post(() -> { try { if (isAdded()) { fillRow(rowRomance, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultThriller();
            h.post(() -> { try { if (isAdded()) { fillRow(rowThriller, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultDrama();
            h.post(() -> { try { if (isAdded()) { fillRow(rowDrama, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultTopRated();
            h.post(() -> { try { if (isAdded()) { fillRow(rowTopRated, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultSpanish();
            h.post(() -> { try { if (isAdded()) { fillRow(rowSpanish, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultSeries();
            h.post(() -> { try { if (isAdded()) { fillRow(rowSeries, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.execute(() -> { try {
            List<ContentItem> r = TmdbApi.fetchAdultNoir();
            h.post(() -> { try { if (isAdded()) { fillRow(rowNoir, r); hideLoading.run(); } } catch (Exception ignored) {} });
        } catch (Exception ignored) {} });

        pool.shutdown();
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }
}
