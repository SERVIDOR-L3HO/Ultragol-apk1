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

public class CineCardAdapter extends RecyclerView.Adapter<CineCardAdapter.VH> {
    private final Context ctx;
    private final List<ContentItem> items;
    private final String typeLabel;

    public CineCardAdapter(Context ctx, List<ContentItem> items, String typeLabel) {
        this.ctx = ctx;
        this.items = items;
        this.typeLabel = typeLabel;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_cine_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContentItem item = items.get(pos);

        h.title.setText(item.getTitle());
        h.titleBelow.setText(item.getTitle());
        h.year.setText(item.getYear());
        h.rating.setText("★ " + item.getRating());
        h.pillType.setText("TV");
        h.cardType.setText(typeLabel);

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
        TextView title, titleBelow, year, rating, pillType, cardType;
        VH(View v) {
            super(v);
            image      = v.findViewById(R.id.cardImage);
            title      = v.findViewById(R.id.cardTitle);
            titleBelow = v.findViewById(R.id.cardTitleBelow);
            year       = v.findViewById(R.id.cardYear);
            rating     = v.findViewById(R.id.cardRating);
            pillType   = v.findViewById(R.id.cardPillType);
            cardType   = v.findViewById(R.id.cardType);
        }
    }
}
