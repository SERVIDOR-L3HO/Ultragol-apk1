package com.streamnova.app.adapters;

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
import com.streamnova.app.R;
import com.streamnova.app.models.ContentItem;
import java.util.List;

public class ContentRowAdapter extends RecyclerView.Adapter<ContentRowAdapter.ViewHolder> {

    private final Context context;
    private final List<ContentItem> items;

    private static final int[] GRADIENT_STARTS = {
        0xFF3D0000, 0xFF1A1A1A, 0xFF3D0A0A, 0xFF2A1010, 0xFF5C0000, 0xFF200000
    };
    private static final int[] GRADIENT_ENDS = {
        0xFF0A0000, 0xFF050505, 0xFF1A0000, 0xFF0A0505, 0xFF200000, 0xFF0A0A0A
    };

    public ContentRowAdapter(Context context, List<ContentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
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

        holder.titleText.setText(item.getTitle());
        holder.genreText.setText(item.getGenreYear());
        holder.ratingText.setText(item.getRatingDisplay());
        holder.thumbnailEmoji.setText(item.getEmoji());
        holder.badgeView.setText(item.getBadge());

        if (item.isLive()) {
            holder.badgeView.setBackgroundResource(R.drawable.badge_live);
        } else if (item.isNew()) {
            holder.badgeView.setBackgroundResource(R.drawable.badge_new);
        } else {
            holder.badgeView.setBackgroundResource(R.drawable.badge_background);
        }
        holder.badgeView.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> showDetailDialog(item, idx));
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

        ((TextView) dialogView.findViewById(R.id.modalEmoji)).setText(item.getEmoji());
        ((TextView) dialogView.findViewById(R.id.modalTitle)).setText(item.getTitle());
        ((TextView) dialogView.findViewById(R.id.modalRating)).setText(item.getRating());
        ((TextView) dialogView.findViewById(R.id.modalGenre)).setText(item.getGenre());
        ((TextView) dialogView.findViewById(R.id.modalYear)).setText(item.getYear());

        TextView modalBadge = dialogView.findViewById(R.id.modalBadge);
        modalBadge.setText(item.getBadge());
        if (item.isLive()) {
            modalBadge.setBackgroundResource(R.drawable.badge_live);
        } else if (item.isNew()) {
            modalBadge.setBackgroundResource(R.drawable.badge_new);
        } else {
            modalBadge.setBackgroundResource(R.drawable.badge_background);
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
            .setView(dialogView)
            .create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.75f);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.70f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        dialogView.setScaleX(0.85f);
        dialogView.setScaleY(0.85f);
        dialogView.setAlpha(0f);

        dialog.show();

        dialogView.animate()
            .scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(220)
            .setInterpolator(new DecelerateInterpolator(1.5f))
            .start();

        dialogView.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnModalPlay).setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(context, "▶ Reproduciendo: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        dialogView.findViewById(R.id.btnModalList).setOnClickListener(v ->
            Toast.makeText(context, "＋ Añadido a Mi Lista", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout thumbnailContainer;
        TextView thumbnailEmoji, titleText, genreText, ratingText, badgeView;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnailContainer = itemView.findViewById(R.id.thumbnailContainer);
            thumbnailEmoji = itemView.findViewById(R.id.thumbnailEmoji);
            titleText = itemView.findViewById(R.id.titleText);
            genreText = itemView.findViewById(R.id.genreText);
            ratingText = itemView.findViewById(R.id.ratingText);
            badgeView = itemView.findViewById(R.id.badgeView);
        }
    }
}
