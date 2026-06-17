package com.ultragol.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ultragol.app.models.ContentData;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

public class SearchActivity extends AppCompatActivity {


    private EditText searchInput;
    private RecyclerView resultsRv;
    private View emptyState;
    private TextView tvResultCount;
    private LinearLayout filterChips;
    private SearchAdapter adapter;
    private List<ContentItem> allItems = new ArrayList<>();
    private final List<ContentItem> filtered = new ArrayList<>();
    private String activeFilter = "Todo";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private static final String[] FILTERS = {"Todo", "Películas", "Series", "Anime", "Deportes", "TV"};
    private static final int[][] GRADIENTS = {
        {0xFF200A00, 0xFF080810},
        {0xFF1A0A20, 0xFF080810},
        {0xFF0A1020, 0xFF080810},
        {0xFF200A10, 0xFF080810},
        {0xFF0A1818, 0xFF080810}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput   = findViewById(R.id.searchInput);
        resultsRv     = findViewById(R.id.searchResults);
        emptyState    = findViewById(R.id.emptyState);
        tvResultCount = findViewById(R.id.tvResultCount);
        filterChips   = findViewById(R.id.filterChips);

        buildLocalItems();
        buildChips();

        adapter = new SearchAdapter(this, filtered, this::showDetail);
        resultsRv.setLayoutManager(new LinearLayoutManager(this));
        resultsRv.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleSearch(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        searchInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);

        filter("");
    }

    private void scheduleSearch(String query) {
        if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
        if (query.trim().length() >= 2) {
            searchRunnable = () -> searchTmdb(query.trim());
            searchHandler.postDelayed(searchRunnable, 500);
        } else {
            filter(query);
        }
    }

    private void searchTmdb(String query) {
        new Thread(() -> {
            try {
                List<ContentItem> results = TmdbApi.searchMulti(query);
                List<ContentItem> localSports = new ArrayList<>();
                for (ContentItem item : allItems) {
                    if (item.getContentType() == ContentItem.TYPE_SPORT
                            || item.getContentType() == ContentItem.TYPE_TV) {
                        if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            localSports.add(item);
                        }
                    }
                }
                results.addAll(localSports);
                new Handler(Looper.getMainLooper()).post(() -> {
                    allItems.clear();
                    allItems.addAll(results);
                    allItems.addAll(ContentData.getSports());
                    allItems.addAll(ContentData.getLiveTV());
                    filter(query);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> filter(query));
            }
        }).start();
    }

    private void buildLocalItems() {
        allItems.clear();
        allItems.addAll(ContentData.getSports());
        allItems.addAll(ContentData.getLiveTV());
    }

    private void buildChips() {
        for (String f : FILTERS) {
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(34));
            lp.setMarginEnd(dpToPx(8));
            chip.setLayoutParams(lp);
            chip.setText(f);
            chip.setTextSize(12f);
            chip.setTextColor(f.equals(activeFilter) ? 0xFFFFFFFF : 0xFF9090C0);
            chip.setBackground(createChip(f.equals(activeFilter)));
            chip.setPadding(dpToPx(14), 0, dpToPx(14), 0);
            chip.setGravity(android.view.Gravity.CENTER);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setOnClickListener(v -> selectChip(f));
            filterChips.addView(chip);
        }
    }

    private GradientDrawable createChip(boolean active) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dpToPx(20));
        if (active) d.setColor(0xFFFF6B00);
        else { d.setColor(0x1AFFFFFF); d.setStroke(1, 0x33FFFFFF); }
        return d;
    }

    private void selectChip(String filter) {
        activeFilter = filter;
        for (int i = 0; i < filterChips.getChildCount(); i++) {
            TextView chip = (TextView) filterChips.getChildAt(i);
            boolean sel = chip.getText().toString().equals(filter);
            chip.setTextColor(sel ? 0xFFFFFFFF : 0xFF9090C0);
            chip.setBackground(createChip(sel));
        }
        filter(searchInput.getText().toString());
    }

    private void filter(String query) {
        filtered.clear();
        String q = query.toLowerCase().trim();
        for (ContentItem item : allItems) {
            if (!matchesFilter(item)) continue;
            if (q.isEmpty() || item.getTitle().toLowerCase().contains(q)
                    || item.getGenre().toLowerCase().contains(q)) {
                filtered.add(item);
            }
        }
        adapter.notifyDataSetChanged();
        boolean empty = filtered.isEmpty();
        resultsRv.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        tvResultCount.setText(q.isEmpty() ? allItems.size() + " títulos disponibles"
                : filtered.size() + " resultados para \"" + query + "\"");
    }

    private boolean matchesFilter(ContentItem item) {
        switch (activeFilter) {
            case "Películas": return item.getContentType() == ContentItem.TYPE_MOVIE;
            case "Series":    return item.getContentType() == ContentItem.TYPE_SERIES;
            case "Anime":     return item.getContentType() == ContentItem.TYPE_ANIME
                                  || item.getContentType() == ContentItem.TYPE_DORAMA;
            case "Deportes":  return item.getContentType() == ContentItem.TYPE_SPORT;
            case "TV":        return item.getContentType() == ContentItem.TYPE_TV;
            default:          return true;
        }
    }

    private void openStream(ContentItem item) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getStreamUrl()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Reproduciendo: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetail(ContentItem item, int idx) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_content_detail, null);
        RelativeLayout heroBg = dialogView.findViewById(R.id.modalHeroBg);
        int gi = idx % GRADIENTS.length;
        GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{GRADIENTS[gi][0], GRADIENTS[gi][1]});
        grad.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.card_corner_radius_large));
        heroBg.setBackground(grad);

        ImageView modalImage = dialogView.findViewById(R.id.modalImage);
        if (item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(this).load(item.getPosterUrl()).centerCrop().into(modalImage);
        }

        ((TextView) dialogView.findViewById(R.id.modalTitle)).setText(item.getTitle());
        ((TextView) dialogView.findViewById(R.id.modalRating)).setText(item.getRating());
        ((TextView) dialogView.findViewById(R.id.modalGenre)).setText(item.getGenre());
        ((TextView) dialogView.findViewById(R.id.modalYear)).setText(item.getYear());

        TextView overview = dialogView.findViewById(R.id.modalOverview);
        if (item.getOverview() != null && !item.getOverview().isEmpty()) {
            overview.setText(item.getOverview());
            overview.setVisibility(View.VISIBLE);
        }

        TextView badge = dialogView.findViewById(R.id.modalBadge);
        badge.setText(item.getBadge());
        badge.setBackgroundResource(item.isLive() ? R.drawable.badge_live
                : item.isNew() ? R.drawable.badge_new : R.drawable.badge_background);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        Window win = dialog.getWindow();
        if (win != null) {
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            win.setDimAmount(0.75f);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
        }
        dialogView.setScaleX(0.85f); dialogView.setScaleY(0.85f); dialogView.setAlpha(0f);
        dialog.show();
        dialogView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(220)
                .setInterpolator(new DecelerateInterpolator(1.5f)).start();
        dialogView.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnModalPlay).setOnClickListener(v -> {
            dialog.dismiss();
            openStream(item);
        });
        dialogView.findViewById(R.id.btnModalList).setOnClickListener(v -> {
            boolean added = FavoritesManager.get(this).toggle(item.getTitle());
            Toast.makeText(this, added ? "Añadido a Mi Lista" : "Eliminado de Mi Lista",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    interface OnItemClick { void onClick(ContentItem item, int index); }

    static class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> {
        private final Context ctx;
        private final List<ContentItem> items;
        private final OnItemClick listener;

        SearchAdapter(Context ctx, List<ContentItem> items, OnItemClick listener) {
            this.ctx = ctx; this.items = items; this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_search_result, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            ContentItem it = items.get(pos);
            if (it.getPosterUrl() != null && !it.getPosterUrl().isEmpty()) {
                Glide.with(ctx).load(it.getPosterUrl()).centerCrop().into(h.image);
            } else {
                h.image.setImageDrawable(null);
            }
            h.title.setText(it.getTitle());
            h.genreYear.setText(it.getGenreYear());
            h.rating.setText("\u2605 " + it.getRating());
            h.badge.setText(it.getBadge());
            h.badge.setBackgroundResource(it.isLive() ? R.drawable.badge_live
                    : it.isNew() ? R.drawable.badge_new : R.drawable.badge_background);
            h.itemView.setOnClickListener(v -> listener.onClick(it, pos));
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title, genreYear, rating, badge;
            VH(View v) {
                super(v);
                image     = v.findViewById(R.id.srImage);
                title     = v.findViewById(R.id.srTitle);
                genreYear = v.findViewById(R.id.srGenreYear);
                rating    = v.findViewById(R.id.srRating);
                badge     = v.findViewById(R.id.srBadge);
            }
        }
    }
}
