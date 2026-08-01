package com.ultragol.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ultragol.app.R;
import com.ultragol.app.models.TvChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Horizontal carousel adapter for the "Destacados" featured section
 * at the top of the TV fragment.  Shows up to {@link #MAX_FEATURED}
 * channels in large glass cards.
 */
public class TvFeaturedAdapter extends RecyclerView.Adapter<TvFeaturedAdapter.FeaturedVH> {

    private static final int MAX_FEATURED = 10;

    private final Context context;
    private final List<TvChannel> items = new ArrayList<>();
    private TvAdapter.OnChannelClickListener clickListener;

    public TvFeaturedAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setOnChannelClickListener(TvAdapter.OnChannelClickListener l) {
        this.clickListener = l;
    }

    /** Replace the featured list (trims to MAX_FEATURED automatically). */
    public void setChannels(List<TvChannel> channels) {
        items.clear();
        int limit = Math.min(channels.size(), MAX_FEATURED);
        for (int i = 0; i < limit; i++) items.add(channels.get(i));
        notifyDataSetChanged();
    }

    @Override public int getItemCount() { return items.size(); }

    @NonNull @Override
    public FeaturedVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_tv_featured_card, parent, false);
        return new FeaturedVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedVH h, int pos) {
        TvChannel ch = items.get(pos);

        if (h.tvName     != null) h.tvName.setText(ch.name);
        if (h.tvFlag     != null) h.tvFlag.setText(TvChannel.flagOf(ch.country));
        if (h.tvCategory != null) h.tvCategory.setText(ch.category);

        // Category-coloured wash on the card background
        if (h.vColorWash != null) {
            try {
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.RECTANGLE);
                gd.setCornerRadius(context.getResources().getDisplayMetrics().density * 22);
                gd.setColor(categoryColor(ch.category));
                h.vColorWash.setBackground(gd);
            } catch (Exception ignored) {}
        }

        if (h.ivLogo != null) {
            Glide.with(context)
                    .load(ch.logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.actionplay_placeholder)
                    .error(R.drawable.actionplay_placeholder)
                    .centerInside()
                    .into(h.ivLogo);
        }

        h.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onChannelClick(ch);
        });
    }

    // ── Category colour wash ──────────────────────────────────────────────────

    private int categoryColor(String cat) {
        if (cat == null) return Color.parseColor("#33D50000");
        switch (cat) {
            case TvChannel.CAT_NOTICIAS:        return Color.parseColor("#331565C0");
            case TvChannel.CAT_DEPORTES:        return Color.parseColor("#332E7D32");
            case TvChannel.CAT_ENTRETENIMIENTO: return Color.parseColor("#336A1B9A");
            case TvChannel.CAT_MUSICA:          return Color.parseColor("#33E64A19");
            case TvChannel.CAT_DOCUMENTALES:    return Color.parseColor("#3300695C");
            case TvChannel.CAT_INFANTIL:        return Color.parseColor("#33F57F17");
            case TvChannel.CAT_CIENCIA:         return Color.parseColor("#33283593");
            default:                             return Color.parseColor("#33D50000");
        }
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class FeaturedVH extends RecyclerView.ViewHolder {
        final ImageView ivLogo;
        final TextView  tvName, tvFlag, tvCategory;
        final View      vColorWash;

        FeaturedVH(View v) {
            super(v);
            ivLogo     = v.findViewById(R.id.ivFeaturedLogo);
            tvName     = v.findViewById(R.id.tvFeaturedName);
            tvFlag     = v.findViewById(R.id.tvFeaturedFlag);
            tvCategory = v.findViewById(R.id.tvFeaturedCategory);
            vColorWash = v.findViewById(R.id.vFeaturedColorWash);
        }
    }
}
