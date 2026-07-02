package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.*;
import com.ultragol.app.models.ContentItem;
import java.util.List;

public class ContinueWatchingAdapter extends RecyclerView.Adapter<ContinueWatchingAdapter.VH> {

    private final Context ctx;
    private final List<ContinueWatchingManager.Entry> list;
    private Runnable onChanged;

    public ContinueWatchingAdapter(Context ctx, List<ContinueWatchingManager.Entry> list) {
        this.ctx = ctx;
        this.list = list;
    }

    public void setOnChangedListener(Runnable r) { this.onChanged = r; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_continue_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ContinueWatchingManager.Entry e = list.get(pos);
        ContentItem item = e.item;

        // Thumbnail
        if (!item.getPosterUrl().isEmpty()) {
            Glide.with(ctx).load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(h.thumb);
        } else {
            h.thumb.setImageDrawable(null);
        }

        // Title
        h.title.setText(item.getTitle());

        // Episode badge
        if (item.getContentType() == ContentItem.TYPE_MOVIE) {
            h.epBadge.setText("PELÍCULA");
        } else {
            h.epBadge.setText("T" + e.season + " · E" + e.episode);
        }

        // Open detail on tap
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, DetailActivity.class);
            intent.putExtra("item", item);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        });

        // Remove on ×
        h.remove.setOnClickListener(v -> {
            int idx = h.getAdapterPosition();
            if (idx == RecyclerView.NO_ID) return;
            ContinueWatchingManager.remove(ctx, item.getTmdbId());
            list.remove(idx);
            notifyItemRemoved(idx);
            if (onChanged != null) onChanged.run();
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, epBadge, remove;
        VH(View v) {
            super(v);
            thumb   = v.findViewById(R.id.cwThumb);
            title   = v.findViewById(R.id.cwTitle);
            epBadge = v.findViewById(R.id.cwEpBadge);
            remove  = v.findViewById(R.id.cwRemove);
        }
    }
}
