package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.DetailActivity;
import com.ultragol.app.R;
import com.ultragol.app.models.ContentItem;
import java.util.*;

/**
 * Infinite-scroll grid adapter for the "Explorar Todo" section.
 *
 * Item types:
 *  TYPE_NORMAL   → 1-column portrait card
 *  TYPE_FEATURED → 2-column landscape "destacado" card (every 5th item, index % 5 == 4)
 *  TYPE_LOADING  → 2-column loading footer
 *
 * Usage with GridLayoutManager (2 columns):
 *   GridLayoutManager glm = new GridLayoutManager(ctx, 2);
 *   glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
 *       public int getSpanSize(int pos) { return adapter.getSpanSize(pos); }
 *   });
 */
public class InfiniteDiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_NORMAL   = 0;
    public static final int TYPE_FEATURED = 1;
    public static final int TYPE_LOADING  = 2;

    private final Context ctx;
    private final List<ContentItem> items = new ArrayList<>();
    private boolean isLoading = false;
    private boolean hasMore   = true;
    private OnLoadMoreListener loadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public InfiniteDiscoverAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener l) {
        this.loadMoreListener = l;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /** Append a new page of items (call from UI thread). */
    public void addPage(List<ContentItem> newItems) {
        boolean wasLoading = isLoading;
        isLoading = false;
        if (wasLoading) notifyItemRemoved(items.size());
        int start = items.size();
        items.addAll(newItems);
        notifyItemRangeInserted(start, newItems.size());
    }

    /** Show spinner footer (call from UI thread before starting fetch). */
    public void showLoading() {
        if (isLoading) return;
        isLoading = true;
        notifyItemInserted(items.size());
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public int getRealItemCount() { return items.size(); }

    @Override
    public int getItemViewType(int pos) {
        if (pos >= items.size()) return TYPE_LOADING;
        return (pos % 5 == 4) ? TYPE_FEATURED : TYPE_NORMAL;
    }

    /** Call this from GridLayoutManager.SpanSizeLookup */
    public int getSpanSize(int pos) {
        return (getItemViewType(pos) == TYPE_NORMAL) ? 1 : 2;
    }

    @Override
    public int getItemCount() {
        return items.size() + (isLoading ? 1 : 0);
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(ctx);
        switch (viewType) {
            case TYPE_FEATURED:
                return new FeaturedVH(inf.inflate(R.layout.item_discover_featured, parent, false));
            case TYPE_LOADING:
                return new LoadingVH(inf.inflate(R.layout.item_discover_loading, parent, false));
            default:
                return new NormalVH(inf.inflate(R.layout.item_discover_card, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (holder instanceof LoadingVH) return;

        ContentItem item = items.get(pos);

        // Trigger load-more when within 4 items of the current end
        if (pos >= items.size() - 4 && hasMore && !isLoading && loadMoreListener != null) {
            loadMoreListener.onLoadMore();
        }

        if (holder instanceof FeaturedVH) {
            ((FeaturedVH) holder).bind(item);
        } else {
            ((NormalVH) holder).bind(item);
        }
    }

    // ── Normal 1-column card ──────────────────────────────────────────────────

    class NormalVH extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, badge;

        NormalVH(View v) {
            super(v);
            poster = v.findViewById(R.id.discoverPoster);
            title  = v.findViewById(R.id.discoverTitle);
            badge  = v.findViewById(R.id.discoverBadge);
        }

        void bind(ContentItem item) {
            title.setText(item.getTitle());

            // Type badge color + text
            String typeLabel;
            int typeColor;
            switch (item.getContentType()) {
                case ContentItem.TYPE_SERIES: typeLabel = "SERIE";    typeColor = 0xFF00BCD4; break;
                case ContentItem.TYPE_ANIME:  typeLabel = "ANIME";    typeColor = 0xFFE91E63; break;
                case ContentItem.TYPE_DORAMA: typeLabel = "DORAMA";   typeColor = 0xFF26A69A; break;
                default:                      typeLabel = "PELÍCULA"; typeColor = 0xFFFF8F00; break;
            }
            badge.setText(typeLabel);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setColor(0xCC000000);
            bg.setStroke(2, typeColor);
            bg.setCornerRadius(4f);
            badge.setBackground(bg);
            badge.setTextColor(typeColor);

            // Poster image
            String imgUrl = item.getPosterUrl();
            if (!imgUrl.isEmpty()) {
                Glide.with(ctx).load(imgUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop().into(poster);
            } else {
                poster.setImageResource(R.drawable.gradient_hero);
            }

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(ctx, DetailActivity.class);
                i.putExtra("item", item);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            });
        }
    }

    // ── Featured 2-column card ────────────────────────────────────────────────

    class FeaturedVH extends RecyclerView.ViewHolder {
        ImageView backdrop;
        TextView title, meta;

        FeaturedVH(View v) {
            super(v);
            backdrop = v.findViewById(R.id.featuredBackdrop);
            title    = v.findViewById(R.id.featuredTitle);
            meta     = v.findViewById(R.id.featuredMeta);
        }

        void bind(ContentItem item) {
            title.setText(item.getTitle());
            meta.setText(item.getYear() + "  ★ " + item.getRating());

            // Prefer backdrop (landscape), fall back to poster
            String imgUrl = !item.getBackdropUrl().isEmpty()
                ? item.getBackdropUrl() : item.getPosterUrl();
            if (!imgUrl.isEmpty()) {
                Glide.with(ctx).load(imgUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop().into(backdrop);
            } else {
                backdrop.setImageResource(R.drawable.gradient_hero);
            }

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(ctx, DetailActivity.class);
                i.putExtra("item", item);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            });
        }
    }

    // ── Loading footer ────────────────────────────────────────────────────────

    static class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View v) { super(v); }
    }
}
