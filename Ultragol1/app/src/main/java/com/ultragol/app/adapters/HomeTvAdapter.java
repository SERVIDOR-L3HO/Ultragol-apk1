package com.ultragol.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ultragol.app.R;
import com.ultragol.app.models.TvChannel;
import java.util.List;

public class HomeTvAdapter extends RecyclerView.Adapter<HomeTvAdapter.VH> {

    public interface OnClickListener { void onClick(TvChannel ch); }

    private final Context context;
    private final List<TvChannel> channels;
    private OnClickListener listener;

    public HomeTvAdapter(Context ctx, List<TvChannel> channels) {
        this.context  = ctx;
        this.channels = channels;
    }

    public void setOnClickListener(OnClickListener l) { this.listener = l; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_home_tv_channel, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TvChannel ch = channels.get(pos);
        h.bind(ch);
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(ch); });
    }

    @Override public int getItemCount() { return channels.size(); }

    class VH extends RecyclerView.ViewHolder {
        final ImageView ivLogo;
        final TextView  tvInitial, tvName, tvCat;
        final View      vColor;

        VH(View v) {
            super(v);
            ivLogo    = v.findViewById(R.id.ivChannelLogo);
            tvInitial = v.findViewById(R.id.tvChannelInitial);
            tvName    = v.findViewById(R.id.tvChannelName);
            tvCat     = v.findViewById(R.id.tvChannelCat);
            vColor    = v.findViewById(R.id.vChannelColor);
        }

        void bind(TvChannel ch) {
            if (tvName    != null) tvName.setText(ch.name);
            if (tvCat     != null) tvCat.setText(ch.category);
            if (tvInitial != null) tvInitial.setText(ch.name.length() > 0
                    ? String.valueOf(ch.name.charAt(0)).toUpperCase() : "T");

            // Colored tinted bg based on category
            if (vColor != null) {
                try {
                    vColor.setBackgroundColor(categoryColor(ch.category));
                } catch (Exception ignored) {}
            }

            if (ivLogo != null) {
                Glide.with(context)
                    .load(ch.logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(android.R.color.transparent)
                    .error(android.R.color.transparent)
                    .centerInside()
                    .into(ivLogo);
            }
        }
    }

    /** Returns a semi-transparent category accent color for the card bg. */
    static int categoryColor(String cat) {
        if (cat == null) return Color.parseColor("#22D50000");
        switch (cat) {
            case TvChannel.CAT_NOTICIAS:        return Color.parseColor("#221565C0");
            case TvChannel.CAT_DEPORTES:        return Color.parseColor("#222E7D32");
            case TvChannel.CAT_ENTRETENIMIENTO: return Color.parseColor("#226A1B9A");
            case TvChannel.CAT_MUSICA:          return Color.parseColor("#22E64A19");
            case TvChannel.CAT_DOCUMENTALES:    return Color.parseColor("#2200695C");
            case TvChannel.CAT_INFANTIL:        return Color.parseColor("#22F57F17");
            case TvChannel.CAT_CIENCIA:         return Color.parseColor("#22283593");
            default:                             return Color.parseColor("#22D50000");
        }
    }
}
