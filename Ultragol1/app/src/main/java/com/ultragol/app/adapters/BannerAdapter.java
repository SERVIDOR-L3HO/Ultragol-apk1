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

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {
    private final Context ctx;
    private final List<ContentItem> items;

    public BannerAdapter(Context ctx, List<ContentItem> items) {
        this.ctx = ctx; this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContentItem item = items.get(pos);
        h.title.setText(item.getTitle());
        h.meta.setText(item.getGenreYear());
        h.rating.setText(item.getRatingDisplay());
        String img = !item.getBackdropUrl().isEmpty() ? item.getBackdropUrl() : item.getPosterUrl();
        if (!img.isEmpty()) Glide.with(ctx).load(img).transition(DrawableTransitionOptions.withCrossFade()).centerCrop().into(h.image);
        else h.image.setImageResource(R.drawable.gradient_hero);
        h.btnPlay.setOnClickListener(v -> {
            Intent i = new Intent(ctx, DetailActivity.class);
            i.putExtra("item", item);
            ctx.startActivity(i);
        });
        h.btnInfo.setOnClickListener(v -> {
            Intent i = new Intent(ctx, DetailActivity.class);
            i.putExtra("item", item);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image; TextView title, meta, rating;
        View btnPlay, btnInfo;
        VH(View v) {
            super(v);
            image  = v.findViewById(R.id.bannerImage);
            title  = v.findViewById(R.id.bannerTitle);
            meta   = v.findViewById(R.id.bannerMeta);
            rating = v.findViewById(R.id.bannerRating);
            btnPlay = v.findViewById(R.id.btnBannerPlay);
            btnInfo = v.findViewById(R.id.btnBannerInfo);
        }
    }
}
