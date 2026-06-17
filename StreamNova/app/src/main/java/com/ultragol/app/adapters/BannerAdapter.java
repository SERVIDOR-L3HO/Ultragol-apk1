package com.ultragol.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import androidx.recyclerview.widget.RecyclerView;
import com.ultragol.app.FavoritesManager;
import com.ultragol.app.R;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private final Context context;
    private final List<ContentItem> items;

    private static final int[][] GRADIENTS = {
        {0xFF200A00, 0xFF080810},
        {0xFF1A0A20, 0xFF080810},
        {0xFF0A1020, 0xFF080810},
        {0xFF200A10, 0xFF080810},
        {0xFF0A1818, 0xFF080810}
    };

    public BannerAdapter(Context context, List<ContentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentItem item = items.get(position);
        int idx = position % GRADIENTS.length;

        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{GRADIENTS[idx][0], GRADIENTS[idx][1]}
        );
        gradient.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.card_corner_radius_large));
        holder.bannerBg.setBackground(gradient);

        holder.bannerTitle.setText(item.getTitle());
        holder.bannerDescription.setText(item.getGenre());
        holder.bannerEmoji.setText(item.getEmoji());
        if (holder.bannerYear != null) holder.bannerYear.setText(item.getYear());
        if (holder.bannerRating != null) holder.bannerRating.setText("⭐ " + item.getRating());

        if (item.isLive()) {
            holder.bannerBadge.setText("EN VIVO");
            holder.bannerBadge.setBackgroundResource(R.drawable.badge_live);
        } else if (item.isNew()) {
            holder.bannerBadge.setText("NUEVO");
            holder.bannerBadge.setBackgroundResource(R.drawable.badge_new);
        } else {
            holder.bannerBadge.setText("DESTACADO");
            holder.bannerBadge.setBackgroundResource(R.drawable.badge_background);
        }

        updateFavBtn(holder, item.getTitle());

        holder.btnPlay.setOnClickListener(v ->
            Toast.makeText(context, "▶ Reproduciendo: " + item.getTitle(), Toast.LENGTH_SHORT).show());
        holder.btnInfo.setOnClickListener(v -> showDetailDialog(item, idx));
        holder.bannerBg.setOnClickListener(v -> showDetailDialog(item, idx));
        holder.btnFavorite.setOnClickListener(v -> {
            boolean added = FavoritesManager.get(context).toggle(item.getTitle());
            updateFavBtn(holder, item.getTitle());
            animateHeart(holder.btnFavorite);
            Toast.makeText(context, added ? "❤ Añadido a Mi Lista" : "💔 Eliminado de Mi Lista",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void updateFavBtn(ViewHolder holder, String title) {
        boolean isFav = FavoritesManager.get(context).isFavorite(title);
        holder.btnFavorite.setTextColor(isFav ? 0xFFFF4466 : 0x55FF6B8A);
        holder.btnFavorite.setText(isFav ? "❤" : "🤍");
    }

    private void animateHeart(View view) {
        view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(120)
            .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
            .start();
    }

    private void showDetailDialog(ContentItem item, int gradIdx) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_content_detail, null);

        RelativeLayout heroBg = dialogView.findViewById(R.id.modalHeroBg);
        int safeIdx = gradIdx % GRADIENTS.length;
        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{GRADIENTS[safeIdx][0], GRADIENTS[safeIdx][1]}
        );
        gradient.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.card_corner_radius_large));
        heroBg.setBackground(gradient);

        ((TextView) dialogView.findViewById(R.id.modalEmoji)).setText(item.getEmoji());
        ((TextView) dialogView.findViewById(R.id.modalTitle)).setText(item.getTitle());
        ((TextView) dialogView.findViewById(R.id.modalRating)).setText(item.getRating());
        ((TextView) dialogView.findViewById(R.id.modalGenre)).setText(item.getGenre());
        ((TextView) dialogView.findViewById(R.id.modalYear)).setText(item.getYear());
        TextView modalBadge = dialogView.findViewById(R.id.modalBadge);
        modalBadge.setText(item.getBadge());
        if (item.isLive())      modalBadge.setBackgroundResource(R.drawable.badge_live);
        else if (item.isNew())  modalBadge.setBackgroundResource(R.drawable.badge_new);
        else                    modalBadge.setBackgroundResource(R.drawable.badge_background);

        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.75f);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90f);
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
            Toast.makeText(context, "▶ Reproduciendo: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        dialogView.findViewById(R.id.btnModalList).setOnClickListener(v -> {
            boolean added = FavoritesManager.get(context).toggle(item.getTitle());
            Toast.makeText(context, added ? "❤ Añadido a Mi Lista" : "Eliminado de Mi Lista",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout bannerBg;
        TextView bannerTitle, bannerDescription, bannerEmoji, bannerBadge;
        TextView bannerYear, bannerRating, btnPlay, btnInfo, btnFavorite;

        ViewHolder(View itemView) {
            super(itemView);
            bannerBg          = itemView.findViewById(R.id.bannerBg);
            bannerTitle       = itemView.findViewById(R.id.bannerTitle);
            bannerDescription = itemView.findViewById(R.id.bannerDescription);
            bannerEmoji       = itemView.findViewById(R.id.bannerEmoji);
            bannerBadge       = itemView.findViewById(R.id.bannerBadge);
            bannerYear        = itemView.findViewById(R.id.bannerYear);
            bannerRating      = itemView.findViewById(R.id.bannerRating);
            btnPlay           = itemView.findViewById(R.id.btnPlay);
            btnInfo           = itemView.findViewById(R.id.btnInfo);
            btnFavorite       = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
