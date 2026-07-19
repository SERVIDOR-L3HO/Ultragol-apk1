package com.ultragol.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ultragol.app.R;
import com.ultragol.app.models.TvChannel;
import java.util.ArrayList;
import java.util.List;

public class TvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_CATEGORIES = 0;
    public static final int TYPE_CHANNEL    = 1;
    public static final int TYPE_LOADING    = 2;
    public static final int TYPE_EMPTY      = 3;

    private static final int POS_CATEGORIES = 0;
    private static final int POS_CONTENT    = 1; // channels start here

    private final Context context;
    private final List<TvChannel> channels = new ArrayList<>();
    private final List<String>    categories;
    private String selectedCategory = TvChannel.CAT_TODOS;
    private boolean isLoading = false;

    private OnChannelClickListener clickListener;
    private OnCategoryClickListener categoryListener;

    public interface OnChannelClickListener    { void onChannelClick(TvChannel ch); }
    public interface OnCategoryClickListener   { void onCategoryClick(String cat); }

    public TvAdapter(Context ctx, List<String> categories) {
        this.context    = ctx;
        this.categories = categories;
    }

    public void setOnChannelClickListener(OnChannelClickListener l)   { this.clickListener = l; }
    public void setOnCategoryClickListener(OnCategoryClickListener l) { this.categoryListener = l; }

    public void setChannels(List<TvChannel> list) {
        channels.clear();
        if (list != null) channels.addAll(list);
        isLoading = false;
        notifyDataSetChanged();
    }

    public void addChannels(List<TvChannel> extra) {
        int start = POS_CONTENT + channels.size();
        channels.addAll(extra);
        notifyItemRangeInserted(start, extra.size());
    }

    public void setSelectedCategory(String cat) {
        this.selectedCategory = cat;
        notifyItemChanged(POS_CATEGORIES); // refresh chips
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        notifyDataSetChanged();
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    @Override public int getItemCount() {
        // Categories row + (loading OR channels OR empty)
        int contentCount = isLoading ? 1 : (channels.isEmpty() ? 1 : channels.size());
        return POS_CONTENT + contentCount;
    }

    @Override public int getItemViewType(int pos) {
        if (pos == POS_CATEGORIES) return TYPE_CATEGORIES;
        if (isLoading)             return TYPE_LOADING;
        if (channels.isEmpty())    return TYPE_EMPTY;
        return TYPE_CHANNEL;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        switch (viewType) {
            case TYPE_CATEGORIES:
                return new CategoriesVH(li.inflate(R.layout.item_tv_categories, parent, false));
            case TYPE_LOADING:
                return new LoadingVH(li.inflate(R.layout.item_tv_loading, parent, false));
            case TYPE_EMPTY:
                return new EmptyVH(li.inflate(R.layout.item_tv_empty, parent, false));
            default:
                return new ChannelVH(li.inflate(R.layout.item_tv_channel, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        switch (getItemViewType(pos)) {
            case TYPE_CATEGORIES:
                ((CategoriesVH) holder).bind(categories, selectedCategory);
                break;
            case TYPE_CHANNEL:
                int channelIndex = pos - POS_CONTENT;
                ((ChannelVH) holder).bind(channels.get(channelIndex), channelIndex + 1);
                break;
            default:
                break;
        }
    }

    // ── ViewHolders ───────────────────────────────────────────────────────────

    class CategoriesVH extends RecyclerView.ViewHolder {
        private final LinearLayout chipContainer;
        CategoriesVH(View v) {
            super(v);
            chipContainer = v.findViewById(R.id.chipContainer);
        }
        void bind(List<String> cats, String selected) {
            if (chipContainer == null) return;
            chipContainer.removeAllViews();
            LayoutInflater li = LayoutInflater.from(context);
            for (String cat : cats) {
                View chip = li.inflate(R.layout.item_tv_chip, chipContainer, false);
                TextView tv = chip.findViewById(R.id.tvChip);
                if (tv != null) {
                    tv.setText(cat);
                    boolean active = cat.equals(selected);
                    tv.setBackgroundResource(active ? R.drawable.tv_chip_active : R.drawable.tv_chip_inactive);
                    tv.setTextColor(active ? 0xFFFFFFFF : 0xAAFFFFFF);
                }
                chip.setOnClickListener(v -> {
                    if (categoryListener != null) categoryListener.onCategoryClick(cat);
                });
                chipContainer.addView(chip);
            }
        }
    }

    class ChannelVH extends RecyclerView.ViewHolder {
        private final ImageView ivLogo;
        private final TextView  tvName, tvFlag, tvCat, tvNumber;
        ChannelVH(View v) {
            super(v);
            ivLogo   = v.findViewById(R.id.ivChannelLogo);
            tvName   = v.findViewById(R.id.tvChannelName);
            tvFlag   = v.findViewById(R.id.tvChannelFlag);
            tvCat    = v.findViewById(R.id.tvChannelCategory);
            tvNumber = v.findViewById(R.id.tvChannelNumber);
        }
        void bind(TvChannel ch, int number) {
            if (tvName   != null) tvName.setText(ch.name);
            if (tvFlag   != null) tvFlag.setText(TvChannel.flagOf(ch.country));
            if (tvCat    != null) tvCat.setText(ch.category);
            if (tvNumber != null) tvNumber.setText(String.valueOf(number));

            // Colored logo container background based on category
            if (ivLogo != null) {
                View parent = (View) ivLogo.getParent();
                if (parent != null) {
                    try {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setShape(GradientDrawable.RECTANGLE);
                        gd.setCornerRadius(20f);
                        gd.setColor(categoryColor(ch.category));
                        parent.setBackground(gd);
                    } catch (Exception ignored) {}
                }
                Glide.with(context)
                    .load(ch.logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(android.R.color.transparent)
                    .error(android.R.color.transparent)
                    .centerInside()
                    .into(ivLogo);
            }
            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onChannelClick(ch);
            });
        }

        private int categoryColor(String cat) {
            if (cat == null) return Color.parseColor("#22D50000");
            switch (cat) {
                case TvChannel.CAT_NOTICIAS:        return Color.parseColor("#331565C0");
                case TvChannel.CAT_DEPORTES:        return Color.parseColor("#332E7D32");
                case TvChannel.CAT_ENTRETENIMIENTO: return Color.parseColor("#336A1B9A");
                case TvChannel.CAT_MUSICA:          return Color.parseColor("#33E64A19");
                case TvChannel.CAT_DOCUMENTALES:    return Color.parseColor("#3300695C");
                case TvChannel.CAT_INFANTIL:        return Color.parseColor("#33F57F17");
                case TvChannel.CAT_CIENCIA:         return Color.parseColor("#33283593");
                default:                             return Color.parseColor("#33D50000");
            }
        }
    }

    static class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View v) { super(v); }
    }

    static class EmptyVH extends RecyclerView.ViewHolder {
        EmptyVH(View v) { super(v); }
    }

    /** Returns span size — always 1 (LinearLayoutManager). */
    public int getSpanSize(int position) { return 1; }
}
