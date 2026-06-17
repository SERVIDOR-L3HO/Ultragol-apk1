package com.ultragol.app;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.models.ContentData;
import com.ultragol.app.models.ContentItem;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView resultsRv;
    private View emptyState;
    private TextView tvResultCount;
    private LinearLayout filterChips;
    private SearchAdapter adapter;
    private List<ContentItem> allItems;
    private List<ContentItem> filtered = new ArrayList<>();
    private String activeFilter = "Todo";

    private static final String[] FILTERS = {"Todo","Películas","Series","Anime","Deportes","TV"};
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

        buildAllItems();
        buildChips();

        adapter = new SearchAdapter(this, filtered, this::showDetail);
        resultsRv.setLayoutManager(new LinearLayoutManager(this));
        resultsRv.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        searchInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
    }

    private void buildAllItems() {
        allItems = new ArrayList<>();
        allItems.addAll(ContentData.getMovies());
        allItems.addAll(ContentData.getSeries());
        allItems.addAll(ContentData.getAnime());
        allItems.addAll(ContentData.getDoramas());
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
        if (active) { d.setColor(0xFFFF6B00); }
        else        { d.setColor(0x1AFFFFFF); d.setStroke(1, 0x33FFFFFF); }
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

    private void showDetail(ContentItem item, int idx) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_content_detail, null);
        RelativeLayout heroBg = dialogView.findViewById(R.id.modalHeroBg);
        int gi = idx % GRADIENTS.length;
        GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{GRADIENTS[gi][0], GRADIENTS[gi][1]});
        grad.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.card_corner_radius_large));
        heroBg.setBackground(grad);
        ((TextView) dialogView.findViewById(R.id.modalEmoji)).setText(item.getEmoji());
        ((TextView) dialogView.findViewById(R.id.modalTitle)).setText(item.getTitle());
        ((TextView) dialogView.findViewById(R.id.modalRating)).setText(item.getRating());
        ((TextView) dialogView.findViewById(R.id.modalGenre)).setText(item.getGenre());
        ((TextView) dialogView.findViewById(R.id.modalYear)).setText(item.getYear());
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
            Toast.makeText(this, "▶ " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        dialogView.findViewById(R.id.btnModalList).setOnClickListener(v -> {
            boolean added = FavoritesManager.get(this).toggle(item.getTitle());
            Toast.makeText(this, added ? "❤ Añadido a Mi Lista" : "Eliminado de Mi Lista",
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
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            ContentItem it = items.get(pos);
            h.emoji.setText(it.getEmoji());
            h.title.setText(it.getTitle());
            h.genreYear.setText(it.getGenreYear());
            h.rating.setText("\u2B50 " + it.getRating());
            h.badge.setText(it.getBadge());
            h.badge.setBackgroundResource(it.isLive() ? R.drawable.badge_live
                    : it.isNew() ? R.drawable.badge_new : R.drawable.badge_background);
            h.itemView.setOnClickListener(v -> listener.onClick(it, pos));
        }
        @Override public int getItemCount() { return items.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView emoji, title, genreYear, rating, badge;
            VH(View v) {
                super(v);
                emoji = v.findViewById(R.id.srEmoji);
                title = v.findViewById(R.id.srTitle);
                genreYear = v.findViewById(R.id.srGenreYear);
                rating = v.findViewById(R.id.srRating);
                badge = v.findViewById(R.id.srBadge);
            }
        }
    }
}
