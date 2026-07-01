package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
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

public class CineHeroAdapter extends RecyclerView.Adapter<CineHeroAdapter.VH> {
    private final Context ctx;
    private final List<ContentItem> items;
    private final String pillTypeLabel;

    public CineHeroAdapter(Context ctx, List<ContentItem> items, String pillTypeLabel) {
        this.ctx = ctx;
        this.items = items;
        this.pillTypeLabel = pillTypeLabel;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_cine_hero, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContentItem item = items.get(pos);

        h.title.setText(item.getTitle());
        h.year.setText(item.getYear());
        h.rating.setText("★ " + item.getRating());
        h.pillType.setText(pillTypeLabel);

        h.pillFeatured.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);

        String imgUrl = !item.getBackdropUrl().isEmpty() ? item.getBackdropUrl() : item.getPosterUrl();
        if (!imgUrl.isEmpty()) {
            Glide.with(ctx).load(imgUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(h.image);
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
        TextView title, year, rating, pillType, pillFeatured;
        VH(View v) {
            super(v);
            image       = v.findViewById(R.id.heroImage);
            title       = v.findViewById(R.id.heroTitle);
            year        = v.findViewById(R.id.heroYear);
            rating      = v.findViewById(R.id.heroRating);
            pillType    = v.findViewById(R.id.heroPillType);
            pillFeatured = v.findViewById(R.id.heroPillFeatured);
        }
    }
}
