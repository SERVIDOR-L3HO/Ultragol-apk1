package com.ultragol.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ultragol.app.R;
import com.ultragol.app.TvHelper;
import com.ultragol.app.models.SportsHighlight;
import com.ultragol.app.models.SportsMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Header (position 0, full span) + a grid of either SportsMatch or
 * SportsHighlight cards depending on the active tab, backing DeportesFragment.
 */
public class DeportesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER    = 0;
    private static final int TYPE_MATCH     = 1;
    private static final int TYPE_HIGHLIGHT = 2;

    public interface HeaderBinder { void bind(View header); }
    public interface OnMatchClick { void onClick(SportsMatch match); }
    public interface OnHighlightClick { void onClick(SportsHighlight highlight); }

    private final Context ctx;
    private final HeaderBinder headerBinder;
    private OnMatchClick onMatchClick;
    private OnHighlightClick onHighlightClick;

    private boolean showingHighlights = false;
    private final List<SportsMatch> matches = new ArrayList<>();
    private final List<SportsHighlight> highlights = new ArrayList<>();

    public DeportesAdapter(Context ctx, HeaderBinder headerBinder) {
        this.ctx = ctx;
        this.headerBinder = headerBinder;
    }

    public void setOnMatchClick(OnMatchClick l) { this.onMatchClick = l; }
    public void setOnHighlightClick(OnHighlightClick l) { this.onHighlightClick = l; }

    public void submitMatches(List<SportsMatch> list) {
        showingHighlights = false;
        matches.clear();
        matches.addAll(list);
        notifyDataSetChanged();
    }

    public void submitHighlights(List<SportsHighlight> list) {
        showingHighlights = true;
        highlights.clear();
        highlights.addAll(list);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return showingHighlights ? highlights.isEmpty() : matches.isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        return showingHighlights ? TYPE_HIGHLIGHT : TYPE_MATCH;
    }

    @Override
    public int getItemCount() {
        return 1 + (showingHighlights ? highlights.size() : matches.size());
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inf.inflate(R.layout.view_deportes_header, parent, false);
            return new HeaderVH(v);
        } else if (viewType == TYPE_HIGHLIGHT) {
            View v = inf.inflate(R.layout.item_sports_highlight_card, parent, false);
            return new HighlightVH(v);
        } else {
            View v = inf.inflate(R.layout.item_sports_match_card, parent, false);
            return new MatchVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            headerBinder.bind(holder.itemView);
            return;
        }
        int idx = position - 1;
        if (holder instanceof MatchVH && idx < matches.size()) {
            bindMatch((MatchVH) holder, matches.get(idx));
        } else if (holder instanceof HighlightVH && idx < highlights.size()) {
            bindHighlight((HighlightVH) holder, highlights.get(idx));
        }
    }

    private void bindMatch(MatchVH h, SportsMatch m) {
        h.home.setText(m.homeTeam);
        h.away.setText(m.awayTeam);
        h.league.setText(m.league);

        if (m.status == SportsMatch.STATUS_LIVE) {
            h.badge.setVisibility(View.VISIBLE);
            h.badge.setText(m.minute != null && !m.minute.isEmpty() ? "● " + m.minute + "'" : "● EN VIVO");
        } else {
            h.badge.setVisibility(View.GONE);
        }

        if (m.hasScore()) {
            h.score.setText(m.homeScore + " - " + m.awayScore);
        } else {
            h.score.setText("VS");
        }

        String timeStr = m.time;
        if (m.date != null && !m.date.isEmpty()) timeStr = timeStr.isEmpty() ? m.date : timeStr + " · " + m.date;
        h.time.setText(timeStr);

        loadLogo(h.logoHome, m.homeLogo);
        loadLogo(h.logoAway, m.awayLogo);

        int n = m.servers.size();
        h.watch.setText(n > 1 ? "▶  " + n + " SERVIDORES" : "▶  WATCH NOW");
        h.itemView.setOnClickListener(v -> { if (onMatchClick != null) onMatchClick.onClick(m); });
    }

    private void bindHighlight(HighlightVH h, SportsHighlight v) {
        h.title.setText(v.title);
        h.category.setText(v.category != null ? v.category.toUpperCase() : "VIDEO");
        if (!v.thumbnail.isEmpty()) {
            Glide.with(ctx).load(v.thumbnail)
                .apply(new RequestOptions().transform(new RoundedCorners(8)))
                .into(h.thumb);
        }
        h.itemView.setOnClickListener(view -> { if (onHighlightClick != null) onHighlightClick.onClick(v); });
    }

    private void loadLogo(ImageView iv, String url) {
        if (url == null || url.isEmpty()) {
            iv.setImageDrawable(null);
            return;
        }
        Glide.with(ctx).load(url).into(iv);
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        HeaderVH(View v) { super(v); }
    }

    static class MatchVH extends RecyclerView.ViewHolder {
        TextView badge, league, home, away, score, time, watch;
        ImageView logoHome, logoAway;
        MatchVH(View v) {
            super(v);
            badge    = v.findViewById(R.id.smBadge);
            league   = v.findViewById(R.id.smLeague);
            home     = v.findViewById(R.id.smHome);
            away     = v.findViewById(R.id.smAway);
            score    = v.findViewById(R.id.smScore);
            time     = v.findViewById(R.id.smTime);
            watch    = v.findViewById(R.id.smWatch);
            logoHome = v.findViewById(R.id.smLogoHome);
            logoAway = v.findViewById(R.id.smLogoAway);
        }
    }

    static class HighlightVH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, category;
        HighlightVH(View v) {
            super(v);
            thumb    = v.findViewById(R.id.shThumb);
            title    = v.findViewById(R.id.shTitle);
            category = v.findViewById(R.id.shCategory);
        }
    }
}
