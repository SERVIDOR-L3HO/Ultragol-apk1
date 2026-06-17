package com.ultragol.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.FavoritesManager;
import com.ultragol.app.R;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class ContentRowAdapter extends RecyclerView.Adapter<ContentRowAdapter.ViewHolder> {


    private final Context context;
    private final List<ContentItem> items;

    private static final int[] GRADIENT_STARTS = {
        0xFF1A0A20, 0xFF0A1020, 0xFF200A10, 0xFF0A1818, 0xFF200A00, 0xFF0A1020
    };
    private static final int[] GRADIENT_ENDS = {
        0xFF080810, 0xFF080810, 0xFF080810, 0xFF080810, 0xFF080810, 0xFF080810
    };

    public ContentRowAdapter(Context context, List<ContentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentItem item = items.get(position);
        int type = item.getContentType();
        int idx = type % GRADIENT_STARTS.length;

        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{GRADIENT_STARTS[idx], GRADIENT_ENDS[idx]}
        );
        gradient.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.card_corner_radius));
        holder.thumbnailContainer.setBackground(gradient);

        if (item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(context)
                .load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.thumbnailImage);
        } else {
            holder.thumbnailImage.setImageDrawable(null);
        }

        holder.titleText.setText(item.getTitle());
        holder.genreText.setText(item.getGenreYear());
        holder.ratingText.setText("\u2605 " + item.getRating());
        holder.badgeView.setText(item.getBadge());

        if (item.isLive())       holder.badgeView.setBackgroundResource(R.drawable.badge_live);
        else if (item.isNew())   holder.badgeView.setBackgroundResource(R.drawable.badge_new);
        else                     holder.badgeView.setBackgroundResource(R.drawable.badge_background);
        holder.badgeView.setVisibility(View.VISIBLE);

        if (holder.qualityTag != null) {
            holder.qualityTag.setText(item.isLive() ? "EN VIVO" : "4K");
            holder.qualityTag.setTextColor(item.isLive() ? 0xFFFF3B00 : 0xFFFF6B00);
        }

        updateFavBtn(holder, item.getTitle());
        holder.itemView.setOnClickListener(v -> showDetailDialog(item, idx));

        if (holder.favoriteBtn != null) {
            holder.favoriteBtn.setOnClickListener(v -> {
                boolean added = FavoritesManager.get(context).toggle(item.getTitle());
                updateFavBtn(holder, item.getTitle());
                animateHeart(holder.favoriteBtn);
                Toast.makeText(context,
                        added ? "Añadido" : "Eliminado",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateFavBtn(ViewHolder holder, String title) {
        if (holder.favoriteBtn == null) return;
        boolean isFav = FavoritesManager.get(context).isFavorite(title);
        holder.favoriteBtn.setTextColor(isFav ? 0xFFFF4466 : 0x44FF6B8A);
        holder.favoriteBtn.setText(isFav ? "\u2665" : "\u2661");
    }

    private void animateHeart(View view) {
        view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(120)
            .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
            .start();
    }

    private void openStream(ContentItem item) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getStreamUrl()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Reproduciendo: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetailDialog(ContentItem item, int gradIdx) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_content_detail, null);

        RelativeLayout heroBg = dialogView.findViewById(R.id.modalHeroBg);
        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{GRADIENT_STARTS[gradIdx], GRADIENT_ENDS[gradIdx]}
        );
        gradient.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.card_corner_radius_large));
        heroBg.setBackground(gradient);

        ImageView modalImage = dialogView.findViewById(R.id.modalImage);
        if (item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(context).load(item.getPosterUrl()).centerCrop().into(modalImage);
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

        TextView modalBadge = dialogView.findViewById(R.id.modalBadge);
        modalBadge.setText(item.getBadge());
        if (item.isLive())     modalBadge.setBackgroundResource(R.drawable.badge_live);
        else if (item.isNew()) modalBadge.setBackgroundResource(R.drawable.badge_new);
        else                   modalBadge.setBackgroundResource(R.drawable.badge_background);

        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.75f);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        dialogView.setScaleX(0.85f); dialogView.setScaleY(0.85f); dialogView.setAlpha(0f);
        dialog.show();
        dialogView.animate().scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(220).setInterpolator(new DecelerateInterpolator(1.5f)).start();
        dialogView.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnModalPlay).setOnClickListener(v -> {
            dialog.dismiss();
            openStream(item);
        });
        dialogView.findViewById(R.id.btnModalList).setOnClickListener(v -> {
            boolean added = FavoritesManager.get(context).toggle(item.getTitle());
            Toast.makeText(context, added ? "Añadido a Mi Lista" : "Eliminado de Mi Lista",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout thumbnailContainer;
        ImageView thumbnailImage;
        TextView titleText, genreText, ratingText, badgeView, qualityTag, favoriteBtn;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnailContainer = itemView.findViewById(R.id.thumbnailContainer);
            thumbnailImage     = itemView.findViewById(R.id.thumbnailImage);
            titleText          = itemView.findViewById(R.id.titleText);
            genreText          = itemView.findViewById(R.id.genreText);
            ratingText         = itemView.findViewById(R.id.ratingText);
            badgeView          = itemView.findViewById(R.id.badgeView);
            qualityTag         = itemView.findViewById(R.id.qualityTag);
            favoriteBtn        = itemView.findViewById(R.id.favoriteBtn);
        }
    }
}
