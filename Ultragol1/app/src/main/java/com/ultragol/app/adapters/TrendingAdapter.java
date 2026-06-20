package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.R;
import com.ultragol.app.DetailActivity;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.VH> {
    private final Context ctx;
    private final List<ContentItem> items;

    public TrendingAdapter(Context ctx, List<ContentItem> items) {
        this.ctx = ctx; this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_trending_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContentItem item = items.get(pos);

        h.title.setText(item.getTitle());
        h.year.setText(item.getYear());
        h.rating.setText(item.getRating());

        // Type badge
        String typeLabel;
        int typeColor;
        switch (item.getContentType()) {
            case ContentItem.TYPE_SERIES:
                typeLabel = "SERIES"; typeColor = Color.parseColor("#00BCD4"); break;
            case ContentItem.TYPE_ANIME:
                typeLabel = "ANIME"; typeColor = Color.parseColor("#E91E63"); break;
            case ContentItem.TYPE_DORAMA:
                typeLabel = "DORAMA"; typeColor = Color.parseColor("#26A69A"); break;
            default:
                typeLabel = "PELÍCULA"; typeColor = Color.parseColor("#FF8F00"); break;
        }
        h.badge.setText(typeLabel);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(0xCC000000);
        bg.setStroke(2, typeColor);
        bg.setCornerRadius(6f);
        h.badge.setBackground(bg);
        h.badge.setTextColor(typeColor);

        // Prefer backdrop for landscape card
        String img = !item.getBackdropUrl().isEmpty() ? item.getBackdropUrl() : item.getPosterUrl();
        if (!img.isEmpty()) {
            Glide.with(ctx).load(img)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(h.image);
        } else {
            h.image.setImageResource(R.drawable.gradient_hero);
        }

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, DetailActivity.class);
            i.putExtra("item", item);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, badge, year, rating;

        VH(View v) {
            super(v);
            image  = v.findViewById(R.id.trendingImage);
            title  = v.findViewById(R.id.trendingTitle);
            badge  = v.findViewById(R.id.trendingBadge);
            year   = v.findViewById(R.id.trendingYear);
            rating = v.findViewById(R.id.trendingRating);
        }
    }
}
