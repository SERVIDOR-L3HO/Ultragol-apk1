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

public class ContentRowAdapter extends RecyclerView.Adapter<ContentRowAdapter.VH> {
    private final Context ctx;
    private final List<ContentItem> items;

    public ContentRowAdapter(Context ctx, List<ContentItem> items) {
        this.ctx = ctx; this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_content_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContentItem item = items.get(pos);
        h.title.setText(item.getTitle());
        h.year.setText(item.getYear());
        h.rating.setText(item.getRating());

        // Type badge text + color
        String typeLabel;
        int typeColor;
        switch (item.getContentType()) {
            case ContentItem.TYPE_SERIES:
                typeLabel = "SERIE"; typeColor = Color.parseColor("#00BCD4"); break;
            case ContentItem.TYPE_ANIME:
                typeLabel = "ANIME"; typeColor = Color.parseColor("#E91E63"); break;
            case ContentItem.TYPE_DORAMA:
                typeLabel = "DORAMA"; typeColor = Color.parseColor("#26A69A"); break;
            case ContentItem.TYPE_SPORT:
                typeLabel = "EN VIVO"; typeColor = Color.parseColor("#E53935"); break;
            case ContentItem.TYPE_TV:
                typeLabel = "TV"; typeColor = Color.parseColor("#43A047"); break;
            default:
                typeLabel = "PELICULA"; typeColor = Color.parseColor("#FF8F00"); break;
        }
        h.badge.setText(typeLabel);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setColor(0xCC000000);
        badgeBg.setStroke(2, typeColor);
        badgeBg.setCornerRadius(4f);
        h.badge.setBackground(badgeBg);
        h.badge.setTextColor(typeColor);

        // NUEVO badge
        if ("NUEVO".equals(item.getBadge()) || item.isNew()) {
            h.cardNew.setVisibility(View.VISIBLE);
        } else {
            h.cardNew.setVisibility(View.GONE);
        }

        // Poster image
        if (!item.getPosterUrl().isEmpty()) {
            Glide.with(ctx).load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(h.poster);
        } else {
            h.poster.setImageResource(R.drawable.gradient_hero);
        }

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, DetailActivity.class);
            i.putExtra("item", item);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, badge, rating, year, cardNew;
        VH(View v) {
            super(v);
            poster  = v.findViewById(R.id.cardPoster);
            title   = v.findViewById(R.id.cardTitle);
            badge   = v.findViewById(R.id.cardBadge);
            rating  = v.findViewById(R.id.cardRating);
            year    = v.findViewById(R.id.cardYear);
            cardNew = v.findViewById(R.id.cardNew);
        }
    }
}
