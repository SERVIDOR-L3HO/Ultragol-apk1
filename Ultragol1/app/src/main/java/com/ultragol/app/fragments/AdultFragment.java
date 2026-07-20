package com.ultragol.app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.R;
import com.ultragol.app.adapters.AdultVideoAdapter;
import com.ultragol.app.models.AdultVideoItem;
import com.ultragol.app.network.AdultContentApi;
import java.util.*;
import java.util.concurrent.*;

public class AdultFragment extends Fragment {

    // ── Category chip definitions ─────────────────────────────────────────────
    private static final Object[][] CHIPS = {
        // { label, type, value }  type="feed" or "cat"
        { "🔥 Trending",   "feed", "trending"  },
        { "🆕 Nuevos",     "feed", "newest"    },
        { "⭐ Top Rated",  "feed", "top"       },
        { "👩 Amateur",    "cat",  AdultContentApi.CAT_AMATEUR   },
        { "💋 MILF",       "cat",  AdultContentApi.CAT_MILF      },
        { "🌶️ Latina",     "cat",  AdultContentApi.CAT_LATINA    },
        { "🎌 Asiático",   "cat",  AdultContentApi.CAT_ASIAN     },
        { "👁️ POV",        "cat",  AdultContentApi.CAT_POV       },
        { "💑 Lésbico",    "cat",  AdultContentApi.CAT_LESBIAN   },
        { "👴 Maduras",    "cat",  AdultContentApi.CAT_MATURE    },
        { "🌙 Romántico",  "cat",  AdultContentApi.CAT_ROMANTIC  },
        { "💻 Webcam",     "cat",  AdultContentApi.CAT_WEBCAM    },
        { "🎌 Hentai",     "cat",  AdultContentApi.CAT_HENTAI    },
    };

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<AdultVideoItem> items = new ArrayList<>();
    private AdultVideoAdapter adapter;
    private RecyclerView grid;
    private ProgressBar loading;

    private String currentFeed = "trending";
    private int    currentCat  = -1;
    private int    currentPage = 1;
    private boolean isLoading  = false;
    private boolean hasMore    = true;

    private int selectedChipIndex = 0;
    private final List<TextView> chips = new ArrayList<>();

    @Nullable @Override
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

        // Search button
        View searchBtn = view.findViewById(R.id.adultBtnSearch);
        if (searchBtn != null) searchBtn.setOnClickListener(v -> showSearchDialog());

        loading = view.findViewById(R.id.adultLoading);
        grid    = view.findViewById(R.id.adultGrid);

        // Set up 2-column grid
        adapter = new AdultVideoAdapter(requireContext(), items);
        adapter.setOnLoadMoreListener(this::loadNextPage);
        GridLayoutManager glm = new GridLayoutManager(requireContext(), 2);
        grid.setLayoutManager(glm);
        grid.setItemAnimator(null);
        grid.setHasFixedSize(false);
        grid.setAdapter(adapter);

        // Build category chips
        buildChips(view);

        // Load first page
        loadPage(1);
    }

    // ── Chip row ──────────────────────────────────────────────────────────────

    private void buildChips(View view) {
        LinearLayout row = view.findViewById(R.id.adultChipRow);
        if (row == null) return;
        row.removeAllViews();
        chips.clear();

        for (int i = 0; i < CHIPS.length; i++) {
            String label = (String) CHIPS[i][0];
            final int idx = i;

            TextView chip = new TextView(requireContext());
            chip.setText(label);
            chip.setTextSize(12f);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
            chip.setPadding(dp(14), dp(7), dp(14), dp(7));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, dp(8), 0);
            chip.setLayoutParams(lp);
            chip.setClickable(true);
            chip.setFocusable(true);

            styleChip(chip, i == 0);

            chip.setOnClickListener(v -> selectChip(idx));
            row.addView(chip);
            chips.add(chip);
        }
    }

    private void styleChip(TextView chip, boolean selected) {
        if (selected) {
            chip.setTextColor(Color.WHITE);
            chip.setBackgroundResource(R.drawable.ver_todos_bg); // existing orange-tinted bg
        } else {
            chip.setTextColor(0xAAFFFFFF);
            chip.setBackgroundColor(0x22FFFFFF);
        }
    }

    private void selectChip(int idx) {
        if (idx == selectedChipIndex) return;

        // Deselect old
        if (selectedChipIndex < chips.size()) {
            styleChip(chips.get(selectedChipIndex), false);
        }
        selectedChipIndex = idx;
        styleChip(chips.get(idx), true);

        // Update feed/category
        String type  = (String) CHIPS[idx][1];
        Object value = CHIPS[idx][2];

        if ("feed".equals(type)) {
            currentFeed = (String) value;
            currentCat  = -1;
        } else {
            currentFeed = "cat";
            currentCat  = (int) value;
        }

        // Reset and reload
        items.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        hasMore     = true;
        loadPage(1);
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadPage(int page) {
        if (isLoading || !hasMore) return;
        isLoading = true;

        if (page == 1) {
            if (loading != null) loading.setVisibility(View.VISIBLE);
            if (grid    != null) grid.setVisibility(View.GONE);
        }

        Handler h = new Handler(android.os.Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AdultVideoItem> result = new ArrayList<>();
            try {
                if ("cat".equals(currentFeed)) {
                    result = AdultContentApi.fetchCategoryPage(currentCat, page);
                } else switch (currentFeed) {
                    case "newest":  result = (page == 1) ? AdultContentApi.fetchNewest()   : AdultContentApi.fetchPage(page); break;
                    case "top":     result = (page == 1) ? AdultContentApi.fetchTopRated() : AdultContentApi.fetchPage(page); break;
                    default:        result = AdultContentApi.fetchPage(page); break;
                }
            } catch (Exception ignored) {}

            final List<AdultVideoItem> finalResult = result;
            final int fetchedPage = page;

            h.post(() -> {
                if (!isAdded()) return;
                isLoading = false;

                if (fetchedPage == 1) {
                    if (loading != null) loading.setVisibility(View.GONE);
                    if (grid    != null) grid.setVisibility(View.VISIBLE);
                }

                if (!finalResult.isEmpty()) {
                    int from = items.size();
                    items.addAll(finalResult);
                    adapter.notifyItemRangeInserted(from, finalResult.size());
                    currentPage = fetchedPage + 1;
                } else {
                    hasMore = false;
                }
            });
        });
    }

    private void loadNextPage() {
        if (!isLoading && hasMore && isAdded()) {
            loadPage(currentPage);
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    private void showSearchDialog() {
        if (!isAdded()) return;
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(requireContext());
        b.setTitle("🔍 Buscar");

        final EditText et = new EditText(requireContext());
        et.setHint("Buscar videos...");
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setPadding(dp(16), dp(12), dp(16), dp(12));
        b.setView(et);

        b.setPositiveButton("Buscar", (dlg, which) -> {
            String q = et.getText().toString().trim();
            if (q.isEmpty()) return;
            performSearch(q);
        });
        b.setNegativeButton("Cancelar", null);
        b.show();
    }

    private void performSearch(String query) {
        // Deselect all chips
        for (int i = 0; i < chips.size(); i++) styleChip(chips.get(i), false);
        selectedChipIndex = -1;

        currentFeed = "search_" + query;
        currentCat  = -1;
        items.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        hasMore     = true;

        if (loading != null) loading.setVisibility(View.VISIBLE);
        if (grid    != null) grid.setVisibility(View.GONE);

        Handler h = new Handler(android.os.Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AdultVideoItem> result = new ArrayList<>();
            try { result = AdultContentApi.search(query); } catch (Exception ignored) {}
            final List<AdultVideoItem> fin = result;
            h.post(() -> {
                if (!isAdded()) return;
                if (loading != null) loading.setVisibility(View.GONE);
                if (grid    != null) grid.setVisibility(View.VISIBLE);
                items.addAll(fin);
                adapter.notifyDataSetChanged();
                hasMore = false; // single page search result
            });
        });
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }
}
