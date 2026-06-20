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

        // Description
        String desc = item.getOverview();
        if (desc != null && !desc.isEmpty()) {
            h.meta.setText(desc);
            h.meta.setVisibility(View.VISIBLE);
        } else {
            h.meta.setVisibility(View.GONE);
        }

        // Genre tags (inline with dot separator)
        String genre = item.getGenre();
        if (genre != null && !genre.isEmpty()) {
            String[] parts = genre.split("[,/]");
            if (parts.length > 0) {
                h.genre1.setText(parts[0].trim());
                h.genre1.setVisibility(View.VISIBLE);
            }
            if (parts.length > 1) {
                h.genreDot.setVisibility(View.VISIBLE);
                h.genre2.setText(parts[1].trim());
                h.genre2.setVisibility(View.VISIBLE);
            }
        } else {
            h.genre1.setVisibility(View.GONE);
            h.genreDot.setVisibility(View.GONE);
            h.genre2.setVisibility(View.GONE);
        }

        // Stars (out of 5, based on rating/10)
        try {
            double rating = Double.parseDouble(item.getRating());
            int stars = (int) Math.round(rating / 2.0);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) sb.append(i < stars ? "★" : "☆");
            h.stars.setText(sb.toString());
            h.ratingPct.setText(String.format("%.0f%%", rating * 10));
        } catch (Exception e) {
            h.stars.setText("★★★★☆");
            h.ratingPct.setText("");
        }

        // Year
        h.year.setText(item.getYear());

        // Backdrop image (prefer backdrop, fallback to poster)
        String img = !item.getBackdropUrl().isEmpty() ? item.getBackdropUrl() : item.getPosterUrl();
        if (!img.isEmpty()) {
            Glide.with(ctx).load(img)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(h.image);
        } else {
            h.image.setImageResource(R.drawable.gradient_hero);
        }

        View.OnClickListener openDetail = v -> {
            Intent i = new Intent(ctx, DetailActivity.class);
            i.putExtra("item", item);
            ctx.startActivity(i);
        };
        h.btnPlay.setOnClickListener(openDetail);
        h.btnInfo.setOnClickListener(openDetail);
        if (h.btnBookmark != null) h.btnBookmark.setOnClickListener(openDetail);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, meta, genre1, genre2, genre3, genreDot, stars, ratingPct, year;
        View btnPlay, btnInfo, btnBookmark;

        VH(View v) {
            super(v);
            image      = v.findViewById(R.id.bannerImage);
            title      = v.findViewById(R.id.bannerTitle);
            meta       = v.findViewById(R.id.bannerMeta);
            genre1     = v.findViewById(R.id.bannerGenre1);
            genre2     = v.findViewById(R.id.bannerGenre2);
            genre3     = v.findViewById(R.id.bannerGenre3);
            genreDot   = v.findViewById(R.id.bannerGenreDot);
            stars      = v.findViewById(R.id.bannerStars);
            ratingPct  = v.findViewById(R.id.bannerRatingPct);
            year       = v.findViewById(R.id.bannerYear);
            btnPlay    = v.findViewById(R.id.btnBannerPlay);
            btnInfo    = v.findViewById(R.id.btnBannerInfo);
            btnBookmark= v.findViewById(R.id.btnBannerBookmark);
        }
    }
}
