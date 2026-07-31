package com.ultragol.app.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.ultragol.app.R;
import com.ultragol.app.TvHelper;
import com.ultragol.app.adapters.ContentGridAdapter;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.TmdbApi;
import java.util.*;
import java.util.concurrent.Executors;

public class PlatformFragment extends Fragment {

    private static final String ARG_NAME     = "platform_name";
    private static final String ARG_PROVIDER = "provider_id";
    private static final String ARG_TYPE     = "content_type";
    private static final String ARG_COLOR    = "platform_color";
    private static final String ARG_LABEL    = "platform_label"; // clean name without emoji

    // ── Brand colors per platform ──────────────────────────────────────────────
    public static final int COLOR_NETFLIX     = 0xFFE50914;
    public static final int COLOR_PRIME       = 0xFF00A8E1;
    public static final int COLOR_DISNEY      = 0xFF113CCF;
    public static final int COLOR_APPLE       = 0xFFCCCCCC;
    public static final int COLOR_HULU        = 0xFF1CE783;
    public static final int COLOR_HBO         = 0xFF9B59B6;
    public static final int COLOR_CRUNCHYROLL = 0xFFF47521;
    public static final int COLOR_ATX         = 0xFF4FC3F7;
    public static final int COLOR_TOKYOMX     = 0xFF1976D2;

    public static PlatformFragment newInstance(String platformName, int providerId,
                                               String type, int brandColor, String cleanLabel) {
        PlatformFragment f = new PlatformFragment();
        Bundle b = new Bundle();
        b.putString(ARG_NAME,     platformName);
        b.putInt(ARG_PROVIDER,    providerId);
        b.putString(ARG_TYPE,     type);
        b.putInt(ARG_COLOR,       brandColor);
        b.putString(ARG_LABEL,    cleanLabel);
        f.setArguments(b);
        return f;
    }

    // ── Backward compat: old 3-arg factory keeps working ──────────────────────
    public static PlatformFragment newInstance(String platformName, int providerId, String type) {
        int color = guessColor(platformName);
        return newInstance(platformName, providerId, type, color, cleanName(platformName));
    }

    private static int guessColor(String name) {
        if (name == null) return COLOR_NETFLIX;
        String n = name.toLowerCase();
        if (n.contains("netflix"))     return COLOR_NETFLIX;
        if (n.contains("prime"))       return COLOR_PRIME;
        if (n.contains("disney"))      return COLOR_DISNEY;
        if (n.contains("apple"))       return COLOR_APPLE;
        if (n.contains("hulu"))        return COLOR_HULU;
        if (n.contains("hbo"))         return COLOR_HBO;
        if (n.contains("crunchyroll")) return COLOR_CRUNCHYROLL;
        if (n.contains("at-x"))        return COLOR_ATX;
        if (n.contains("tokyo"))       return COLOR_TOKYOMX;
        return 0xFFCC1111;
    }

    private static String cleanName(String name) {
        if (name == null) return "Plataforma";
        // Strip leading emoji/symbols (common pattern: "🔴 Netflix" → "Netflix")
        return name.replaceAll("^[^\\p{L}\\d]+", "").trim();
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_platform, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);

        Bundle args        = getArguments();
        String platformName = args != null ? args.getString(ARG_NAME,  "Plataforma") : "Plataforma";
        String label        = args != null ? args.getString(ARG_LABEL, cleanName(platformName)) : cleanName(platformName);
        int    providerId   = args != null ? args.getInt(ARG_PROVIDER, 8)   : 8;
        String type         = args != null ? args.getString(ARG_TYPE, "all") : "all";
        int    brandColor   = args != null ? args.getInt(ARG_COLOR, COLOR_NETFLIX) : COLOR_NETFLIX;

        // ── Header: back button ──────────────────────────────────────────────
        View btnBack = view.findViewById(R.id.platformBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }

        // ── Header: platform name with brand color ────────────────────────────
        TextView tvName = view.findViewById(R.id.platformName);
        if (tvName != null) {
            tvName.setText(label);
            tvName.setTextColor(brandColor);
        }

        // ── Header: colored dot ───────────────────────────────────────────────
        View dot = view.findViewById(R.id.platformDot);
        if (dot != null) {
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            gd.setColor(brandColor);
            dot.setBackground(gd);
        }

        // ── Header: accent bar at the bottom ──────────────────────────────────
        View accentBar = view.findViewById(R.id.platformAccentBar);
        if (accentBar != null) {
            accentBar.setBackgroundColor(brandColor);
        }

        // ── Header: subtle background tint ───────────────────────────────────
        FrameLayout header = view.findViewById(R.id.platformHeader);
        if (header != null) {
            // Very subtle brand color wash (5% opacity)
            int tint = (brandColor & 0x00FFFFFF) | 0x0D000000;
            header.setBackgroundColor(tint);
        }

        // ── Content type badge ────────────────────────────────────────────────
        TextView badge = view.findViewById(R.id.platformBadge);
        if (badge != null) {
            String badgeText = null;
            if ("anime".equals(type))       badgeText = "ANIME";
            else if ("movie".equals(type))  badgeText = "PELÍCULAS";
            if (badgeText != null) {
                badge.setText(badgeText);
                badge.setVisibility(View.VISIBLE);
            }
        }

        // ── Grid ──────────────────────────────────────────────────────────────
        RecyclerView grid = view.findViewById(R.id.platformGrid);
        ProgressBar  pb   = view.findViewById(R.id.platformLoading);

        List<ContentItem>    items   = new ArrayList<>();
        ContentGridAdapter   adapter = new ContentGridAdapter(requireContext(), items);
        int cols = getResources().getInteger(R.integer.content_grid_columns);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), cols));
        grid.setAdapter(adapter);
        TvHelper.makeFocusable(grid);
        if (pb != null) pb.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<ContentItem> r = new ArrayList<>();
                if ("anime".equals(type)) {
                    r.addAll(TmdbApi.fetchByProvider(providerId, "tv"));
                } else if ("movie".equals(type)) {
                    r.addAll(TmdbApi.fetchByProvider(providerId, "movie"));
                } else {
                    r.addAll(TmdbApi.fetchByProvider(providerId, "movie"));
                    r.addAll(TmdbApi.fetchByProvider(providerId, "tv"));
                }
                requireActivity().runOnUiThread(() -> {
                    items.addAll(r);
                    adapter.notifyDataSetChanged();
                    if (pb != null) pb.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    if (pb != null) pb.setVisibility(View.GONE);
                });
            }
        });
    }
}
