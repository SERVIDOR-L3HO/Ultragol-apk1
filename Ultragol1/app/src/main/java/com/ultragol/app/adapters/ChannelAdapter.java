package com.ultragol.app.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.R;
import com.ultragol.app.models.Channel;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.VH> {
    private final Context ctx;
    private final List<Channel> items;
    private final OnChannelClick listener;

    public interface OnChannelClick { void onClick(Channel ch); }

    public ChannelAdapter(Context ctx, List<Channel> items, OnChannelClick listener) {
        this.ctx = ctx; this.items = items; this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Channel ch = items.get(pos);
        h.name.setText(ch.getName());
        h.country.setText(ch.getFlag() + " " + ch.getCountry());
        h.category.setText(ch.getCategory());
        h.liveBadge.setText("● EN VIVO");
        if (!ch.getLogo().isEmpty()) {
            Glide.with(ctx).load(ch.getLogo())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .centerInside().into(h.logo);
        } else { h.logo.setImageResource(R.drawable.ic_channel_placeholder); }
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(ch); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView logo; TextView name, country, category, liveBadge;
        VH(View v) {
            super(v);
            logo      = v.findViewById(R.id.channelLogo);
            name      = v.findViewById(R.id.channelName);
            country   = v.findViewById(R.id.channelCountry);
            category  = v.findViewById(R.id.channelCategory);
            liveBadge = v.findViewById(R.id.channelLive);
        }
    }
}
