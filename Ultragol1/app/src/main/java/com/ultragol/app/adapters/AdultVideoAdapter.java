package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.R;
import com.ultragol.app.models.AdultVideoItem;
import java.util.List;

public class AdultVideoAdapter extends RecyclerView.Adapter<AdultVideoAdapter.VH> {

    private final Context ctx;
    private final List<AdultVideoItem> items;
    private OnLoadMoreListener loadMoreListener;
    private static final int LOAD_TRIGGER = 4; // load more when 4 items from end

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener l) {
        this.loadMoreListener = l;
    }

    public AdultVideoAdapter(Context ctx, List<AdultVideoItem> items) {
        this.ctx   = ctx;
        this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_adult_video, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        AdultVideoItem item = items.get(pos);

        // Thumbnail
        Glide.with(ctx)
            .load(item.getThumbUrl())
            .placeholder(android.R.color.black)
            .error(android.R.color.black)
            .transition(DrawableTransitionOptions.withCrossFade(200))
            .centerCrop()
            .into(h.thumb);

        // Duration
        h.duration.setText(item.getDuration());

        // HD badge - show if duration > 10min (rough heuristic) or title contains HD
        boolean hd = item.getTitle().toUpperCase().contains("HD")
            || item.getTitle().toUpperCase().contains("4K");
        h.hdBadge.setVisibility(hd ? View.VISIBLE : View.GONE);

        // Title
        h.title.setText(item.getTitle());

        // Views
        h.views.setText(item.getViews());

        // Rating
        String rating = item.getRating();
        if (!rating.isEmpty() && !rating.equals("0")) {
            h.rating.setText("♥ " + rating + "%");
            h.rating.setVisibility(View.VISIBLE);
        } else {
            h.rating.setVisibility(View.GONE);
        }

        // Click → open PlayerActivity with embed URL
        h.itemView.setOnClickListener(v -> {
            String embedUrl = item.getEmbedUrl();
            if (embedUrl == null || embedUrl.isEmpty()) return;
            Intent intent = new Intent(ctx, PlayerActivity.class);
            intent.putExtra("url",   embedUrl);
            intent.putExtra("title", item.getTitle());
            ctx.startActivity(intent);
        });

        // Load more trigger
        if (loadMoreListener != null && pos >= items.size() - LOAD_TRIGGER) {
            loadMoreListener.onLoadMore();
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView  duration, hdBadge, title, views, rating;

        VH(@NonNull View v) {
            super(v);
            thumb    = v.findViewById(R.id.ivAdultThumb);
            duration = v.findViewById(R.id.tvAdultDuration);
            hdBadge  = v.findViewById(R.id.tvAdultHD);
            title    = v.findViewById(R.id.tvAdultTitle);
            views    = v.findViewById(R.id.tvAdultViews);
            rating   = v.findViewById(R.id.tvAdultRating);
        }
    }
}
