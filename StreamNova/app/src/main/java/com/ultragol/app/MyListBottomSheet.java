package com.ultragol.app;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ultragol.app.models.ContentData;
import com.ultragol.app.models.ContentItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyListBottomSheet extends BottomSheetDialogFragment {

    private static final int[][] GRADIENTS = {
        {0xFF200A00, 0xFF080810}, {0xFF1A0A20, 0xFF080810},
        {0xFF0A1020, 0xFF080810}, {0xFF200A10, 0xFF080810},
        {0xFF0A1818, 0xFF080810}
    };

    public static MyListBottomSheet newInstance() { return new MyListBottomSheet(); }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_my_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<ContentItem> favItems = getFavoritedItems();
        TextView tvCount = view.findViewById(R.id.tvListCount);
        tvCount.setText(favItems.size() + " títulos");

        RecyclerView rv = view.findViewById(R.id.myListRecycler);
        View emptyState = view.findViewById(R.id.emptyList);

        if (favItems.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            rv.setLayoutManager(new LinearLayoutManager(requireContext()));
            rv.setAdapter(new ListAdapter(requireContext(), favItems, this::showDetail));
        }
    }

    private List<ContentItem> getFavoritedItems() {
        Set<String> favTitles = FavoritesManager.get(requireContext()).getFavorites();
        List<ContentItem> all = new ArrayList<>();
        all.addAll(ContentData.getMovies());
        all.addAll(ContentData.getSeries());
        all.addAll(ContentData.getAnime());
        all.addAll(ContentData.getDoramas());
        all.addAll(ContentData.getSports());
        all.addAll(ContentData.getLiveTV());
        all.addAll(ContentData.getHeroBanners());
        List<ContentItem> result = new ArrayList<>();
        for (ContentItem item : all) {
            if (favTitles.contains(item.getTitle())) result.add(item);
        }
        return result;
    }

    private void showDetail(ContentItem item, int idx) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_content_detail, null);
        RelativeLayout heroBg = dialogView.findViewById(R.id.modalHeroBg);
        int gi = idx % GRADIENTS.length;
        GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{GRADIENTS[gi][0], GRADIENTS[gi][1]});
        grad.setCornerRadius(requireContext().getResources()
                .getDimensionPixelSize(R.dimen.card_corner_radius_large));
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
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView).create();
        Window win = dialog.getWindow();
        if (win != null) {
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            win.setDimAmount(0.75f);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.90f);
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
            Toast.makeText(requireContext(), "▶ " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        dialogView.findViewById(R.id.btnModalList).setOnClickListener(v -> {
            FavoritesManager.get(requireContext()).toggle(item.getTitle());
            Toast.makeText(requireContext(), "Eliminado de Mi Lista", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    interface OnItemClick { void onClick(ContentItem item, int idx); }

    static class ListAdapter extends RecyclerView.Adapter<ListAdapter.VH> {
        private final Context ctx;
        private final List<ContentItem> items;
        private final OnItemClick listener;
        ListAdapter(Context ctx, List<ContentItem> items, OnItemClick listener) {
            this.ctx = ctx; this.items = items; this.listener = listener;
        }
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(ctx)
                    .inflate(R.layout.item_search_result, parent, false));
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
                emoji     = v.findViewById(R.id.srEmoji);
                title     = v.findViewById(R.id.srTitle);
                genreYear = v.findViewById(R.id.srGenreYear);
                rating    = v.findViewById(R.id.srRating);
                badge     = v.findViewById(R.id.srBadge);
            }
        }
    }
}
