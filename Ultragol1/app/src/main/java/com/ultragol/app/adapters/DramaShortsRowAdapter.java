package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.R;
import com.ultragol.app.network.DramaShortsApi;
import java.util.List;

/**
 * Horizontal card row adapter for the "Shorts Dramas" section on the home screen.
 * Each card shows the video thumbnail, title and channel.
 * Tapping opens the video in the browser.
 */
public class DramaShortsRowAdapter
        extends RecyclerView.Adapter<DramaShortsRowAdapter.VH> {

    private final Context ctx;
    private final List<DramaShortsApi.VideoItem> items;

    public DramaShortsRowAdapter(Context ctx, List<DramaShortsApi.VideoItem> items) {
        this.ctx   = ctx;
        this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.item_drama_short_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        DramaShortsApi.VideoItem item = items.get(pos);

        h.title.setText(item.titulo);
        h.canal.setText(item.canal);
        h.duracion.setText(item.getDuracionStr());

        if (item.thumbnailUrl != null && !item.thumbnailUrl.isEmpty()) {
            Glide.with(ctx)
                    .load(item.thumbnailUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(h.thumb);
        } else {
            h.thumb.setImageResource(R.drawable.gradient_hero);
        }

        h.itemView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getEmbedUrl()));
                ctx.startActivity(intent);
            } catch (Exception ignored) {}
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView  title, canal, duracion;
        VH(View v) {
            super(v);
            thumb    = v.findViewById(R.id.dsThumb);
            title    = v.findViewById(R.id.dsTitle);
            canal    = v.findViewById(R.id.dsCanal);
            duracion = v.findViewById(R.id.dsDuracion);
        }
    }
}
