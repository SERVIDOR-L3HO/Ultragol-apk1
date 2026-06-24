package com.ultragol.app;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.VH> {

    private final Context ctx;
    private final ContentItem series;
    private final List<TmdbApi.EpisodeInfo> list = new ArrayList<>();

    public EpisodeAdapter(Context ctx, ContentItem series) {
        this.ctx = ctx;
        this.series = series;
    }

    public void setEpisodes(List<TmdbApi.EpisodeInfo> eps) {
        list.clear();
        list.addAll(eps);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.episode_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TmdbApi.EpisodeInfo ep = list.get(pos);

        h.tvEpBadge.setText("EP " + ep.number);
        h.tvEpTitle.setText(ep.name);

        if (!ep.overview.isEmpty()) {
            h.tvEpOverview.setVisibility(View.VISIBLE);
            h.tvEpOverview.setText(ep.overview);
        } else {
            h.tvEpOverview.setVisibility(View.GONE);
        }

        String code = "S" + String.format("%02d", ep.season) + "E" + String.format("%02d", ep.number);
        h.tvEpCode.setText(series.getTitle() + "  ·  " + code);

        if (ep.runtime > 0) {
            h.tvEpDuration.setVisibility(View.VISIBLE);
            h.tvEpDuration.setText(ep.runtime + " min");
        } else {
            h.tvEpDuration.setVisibility(View.GONE);
        }

        if (!ep.stillUrl.isEmpty()) {
            h.ivStill.setVisibility(View.VISIBLE);
            Glide.with(ctx).load(ep.stillUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .placeholder(android.R.color.transparent)
                .into(h.ivStill);
        } else {
            h.ivStill.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> {
            if (ctx instanceof EpisodePickerActivity) {
                ((EpisodePickerActivity) ctx).onEpisodeSelected(ep.season, ep.number);
            }
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEpBadge, tvEpTitle, tvEpOverview, tvEpCode, tvEpDuration;
        ImageView ivStill;
        VH(View v) {
            super(v);
            tvEpBadge    = v.findViewById(R.id.tvEpBadge);
            tvEpTitle    = v.findViewById(R.id.tvEpTitle);
            tvEpOverview = v.findViewById(R.id.tvEpOverview);
            tvEpCode     = v.findViewById(R.id.tvEpCode);
            tvEpDuration = v.findViewById(R.id.tvEpDuration);
            ivStill      = v.findViewById(R.id.ivEpStill);
        }
    }
}
