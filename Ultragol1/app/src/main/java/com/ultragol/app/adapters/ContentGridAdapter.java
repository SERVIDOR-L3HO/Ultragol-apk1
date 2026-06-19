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

public class ContentGridAdapter extends RecyclerView.Adapter<ContentGridAdapter.VH> {
    private final Context ctx;
    private final List<ContentItem> items;

    public ContentGridAdapter(Context ctx, List<ContentItem> items) {
        this.ctx = ctx; this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_content_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContentItem item = items.get(pos);
        h.title.setText(item.getTitle());
        h.year.setText(item.getYear());
        if (!item.getPosterUrl().isEmpty())
            Glide.with(ctx).load(item.getPosterUrl()).transition(DrawableTransitionOptions.withCrossFade()).centerCrop().into(h.poster);
        else h.poster.setImageResource(R.drawable.gradient_hero);
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, DetailActivity.class);
            i.putExtra("item", item);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView poster; TextView title, year;
        VH(View v) {
            super(v);
            poster = v.findViewById(R.id.gridPoster);
            title  = v.findViewById(R.id.gridTitle);
            year   = v.findViewById(R.id.gridYear);
        }
    }
}
