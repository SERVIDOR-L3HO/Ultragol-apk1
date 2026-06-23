package com.ultragol.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.ultragol.app.LiveMatchServerDialog;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.R;
import java.util.List;

public class GlassLiveAdapter extends RecyclerView.Adapter<GlassLiveAdapter.VH> {

    private final Context context;
    private final List<LiveMatchServerDialog.LiveMatch> items;

    public GlassLiveAdapter(Context context, List<LiveMatchServerDialog.LiveMatch> items) {
        this.context = context;
        this.items   = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
            .inflate(R.layout.item_live_glass_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        LiveMatchServerDialog.LiveMatch match = items.get(pos);

        // Logo
        if (match.logoUrl != null && !match.logoUrl.isEmpty()) {
            Glide.with(context)
                .load(match.logoUrl)
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .into(h.logo);
        } else {
            h.logo.setImageResource(R.drawable.ic_channel_placeholder);
        }

        // League
        String liga = match.liga != null && !match.liga.isEmpty()
            ? "🏆  " + match.liga.toUpperCase() : "🏆  FÚTBOL EN VIVO";
        h.league.setText(liga);

        // Title
        h.title.setText(match.titulo);

        // Time
        StringBuilder time = new StringBuilder();
        if (match.hora  != null && !match.hora.isEmpty())  time.append("🕐 ").append(match.hora);
        if (match.fecha != null && !match.fecha.isEmpty()) {
            if (time.length() > 0) time.append("  ·  ");
            time.append("📅 ").append(match.fecha);
        }
        if (time.length() > 0) {
            h.time.setText(time.toString());
            h.time.setVisibility(View.VISIBLE);
        } else {
            h.time.setVisibility(View.GONE);
        }

        // Server count badge
        int count = match.servidores.size();
        if (count > 1) {
            h.serverCount.setText("📡 " + count);
            h.serverCount.setVisibility(View.VISIBLE);
        } else {
            h.serverCount.setVisibility(View.GONE);
        }

        // CTA label
        h.cta.setText(count > 1 ? "▶  " + count + " servidores" : "▶  Ver ahora");

        // Click
        h.card.setOnClickListener(v -> {
            h.card.animate().scaleX(0.95f).scaleY(0.95f).setDuration(70)
                .withEndAction(() -> h.card.animate().scaleX(1f).scaleY(1f).setDuration(130).start())
                .start();
            if (count == 1) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("url",   match.servidores.get(0)[1]);
                intent.putExtra("title", match.titulo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                LiveMatchServerDialog.show(context, match);
            }
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final View     card;
        final ImageView logo;
        final TextView  league, title, time, cta, serverCount;

        VH(View v) {
            super(v);
            card        = v.findViewById(R.id.glassCard);
            logo        = v.findViewById(R.id.cardLogo);
            league      = v.findViewById(R.id.cardLeague);
            title       = v.findViewById(R.id.cardTitle);
            time        = v.findViewById(R.id.cardTime);
            cta         = v.findViewById(R.id.cardCta);
            serverCount = v.findViewById(R.id.cardServerCount);
        }
    }
}
