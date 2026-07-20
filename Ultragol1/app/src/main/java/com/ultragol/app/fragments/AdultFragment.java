package com.ultragol.app.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

    // ── Chip definitions: { label, key }
    //    key matches AdultContentApi.FEED_* or CAT_* constants
    private static final String[][] CHIPS = {
        { "TRENDING",   AdultContentApi.FEED_TRENDING },
        { "NUEVOS",     AdultContentApi.FEED_NEWEST   },
        { "TOP RATED",  AdultContentApi.FEED_TOP      },
        { "AMATEUR",    AdultContentApi.CAT_AMATEUR   },
        { "MILF",       AdultContentApi.CAT_MILF      },
        { "LATINA",     AdultContentApi.CAT_LATINA    },
        { "ASIÁTICO",   AdultContentApi.CAT_ASIAN     },
        { "POV",        AdultContentApi.CAT_POV       },
        { "LÉSBICO",    AdultContentApi.CAT_LESBIAN   },
        { "MADURAS",    AdultContentApi.CAT_MATURE    },
        { "WEBCAM",     AdultContentApi.CAT_WEBCAM    },
        { "HENTAI",     AdultContentApi.CAT_HENTAI    },
        { "THREESOME",  AdultContentApi.CAT_THREESOME },
        { "BIG TITS",   AdultContentApi.CAT_BIG_TITS  },
        { "ANAL",       AdultContentApi.CAT_ANAL      },
        { "MASAJE",     AdultContentApi.CAT_MASSAGE   },
    };

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<AdultVideoItem> items = new ArrayList<>();
    private AdultVideoAdapter adapter;
    private RecyclerView grid;
    private ProgressBar loading;
    private TextView emptyView;

    private String  currentKey  = AdultContentApi.FEED_TRENDING;
    private int     currentPage = 1;
    private boolean isLoading   = false;
    private boolean hasMore     = true;

    private int selectedChipIndex = 0;
    private final List<TextView> chips = new ArrayList<>();

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adult, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View back = view.findViewById(R.id.adultBack);
        if (back != null) back.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        View searchBtn = view.findViewById(R.id.adultBtnSearch);
        if (searchBtn != null) searchBtn.setOnClickListener(v -> showSearchDialog());

        loading   = view.findViewById(R.id.adultLoading);
        grid      = view.findViewById(R.id.adultGrid);
        emptyView = view.findViewById(R.id.adultEmpty);

        adapter = new AdultVideoAdapter(requireContext(), items);
        adapter.setOnLoadMoreListener(this::loadNextPage);
        GridLayoutManager glm = new GridLayoutManager(requireContext(), 2);
        grid.setLayoutManager(glm);
        grid.setItemAnimator(null);
        grid.setHasFixedSize(false);
        grid.setAdapter(adapter);

        buildChips(view);
        loadPage(1);
    }

    // ── Chip row ──────────────────────────────────────────────────────────────

    private void buildChips(View view) {
        LinearLayout row = view.findViewById(R.id.adultChipRow);
        if (row == null) return;
        row.removeAllViews();
        chips.clear();

        for (int i = 0; i < CHIPS.length; i++) {
            final int idx = i;
            TextView chip = new TextView(requireContext());
            chip.setText(CHIPS[i][0]);
            chip.setTextSize(11.5f);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
            chip.setLetterSpacing(0.06f);
            chip.setPadding(dp(14), dp(8), dp(14), dp(8));
            chip.setAllCaps(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, dp(7), 0);
            chip.setLayoutParams(lp);
            chip.setClickable(true);
            chip.setFocusable(true);

            styleChip(chip, i == 0);
            chip.setOnClickListener(v -> selectChip(idx));
            row.addView(chip);
            chips.add(chip);
        }
    }

    /** Selected: gradient red→orange pill. Unselected: dark pill with border. */
    private void styleChip(TextView chip, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp(50)); // pill

        if (selected) {
            bg.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            bg.setColors(new int[]{ 0xFFD01414, 0xFFFF4F1A });
            chip.setTextColor(Color.WHITE);
            chip.setElevation(dp(2));
        } else {
            bg.setColor(0xFF1A1A1E);
            bg.setStroke(dp(1), 0x44FFFFFF);
            chip.setTextColor(0x99FFFFFF);
            chip.setElevation(0f);
        }
        chip.setBackground(bg);
    }

    private void selectChip(int idx) {
        if (idx == selectedChipIndex) return;

        if (selectedChipIndex >= 0 && selectedChipIndex < chips.size()) {
            styleChip(chips.get(selectedChipIndex), false);
        }
        selectedChipIndex = idx;
        styleChip(chips.get(idx), true);

        currentKey  = CHIPS[idx][1];
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
            if (loading   != null) loading.setVisibility(View.VISIBLE);
            if (grid      != null) grid.setVisibility(View.GONE);
            if (emptyView != null) emptyView.setVisibility(View.GONE);
        }

        final String keySnap = currentKey;
        Handler h = new Handler(android.os.Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AdultVideoItem> result = AdultContentApi.fetch(keySnap, page);

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
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                } else {
                    hasMore = false;
                    if (fetchedPage == 1 && emptyView != null) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
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
        android.app.AlertDialog.Builder b =
            new android.app.AlertDialog.Builder(requireContext());
        b.setTitle("Buscar");

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
        for (int i = 0; i < chips.size(); i++) styleChip(chips.get(i), false);
        selectedChipIndex = -1;

        currentKey  = "search";
        items.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        hasMore     = true;

        if (loading   != null) loading.setVisibility(View.VISIBLE);
        if (grid      != null) grid.setVisibility(View.GONE);
        if (emptyView != null) emptyView.setVisibility(View.GONE);

        Handler h = new Handler(android.os.Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AdultVideoItem> result = AdultContentApi.search(query);
            h.post(() -> {
                if (!isAdded()) return;
                if (loading != null) loading.setVisibility(View.GONE);
                if (grid    != null) grid.setVisibility(View.VISIBLE);
                if (!result.isEmpty()) {
                    items.addAll(result);
                    adapter.notifyDataSetChanged();
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                } else if (emptyView != null) {
                    emptyView.setVisibility(View.VISIBLE);
                }
                hasMore = false;
            });
        });
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }
}
