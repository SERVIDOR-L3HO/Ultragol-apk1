package com.ultragol.app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ultragol.app.LiveMatchServerDialog;
import com.ultragol.app.PlayerActivity;
import com.ultragol.app.R;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Agenda-style sports screen — each league loads its "marcadores" data
 * and renders as a horizontal-scroll event row, like GOLXU.
 */
public class FootballFragment extends Fragment {

    private static final String API = "https://ultragol-api-3--maricarmen43549.replit.app";

    // {emoji, label, api-prefix, accent-color}
    private static final Object[][] LEAGUES = {
        {"🇲🇽", "LIGA MX",     "",             0xFFFF6B00},
        {"🏴",  "PREMIER",     "premier/",     0xFF5C9AFF},
        {"🇪🇸", "LA LIGA",     "laliga/",      0xFFFF3B3B},
        {"🇮🇹", "SERIE A",     "seriea/",      0xFF00C4FF},
        {"🇩🇪", "BUNDESLIGA",  "bundesliga/",  0xFFFF6B00},
        {"🇫🇷", "LIGUE 1",     "ligue1/",      0xFF5C9AFF},
    };

    private LinearLayout contentContainer;
    private ProgressBar  progressBar;
    private TextView     errorView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_football, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        contentContainer = view.findViewById(R.id.footballContent);
        progressBar      = view.findViewById(R.id.footballLoading);
        errorView        = view.findViewById(R.id.footballError);
        loadAgenda();
    }

    // ─── Agenda builder ───────────────────────────────────────────────────────────

    private void loadAgenda() {
        contentContainer.removeAllViews();

        // 1) EN VIVO — Gol 3 live carousel
        buildGol3Carousel();

        // 2) Each league section (async)
        for (Object[] league : LEAGUES) {
            String emoji  = (String) league[0];
            String label  = (String) league[1];
            String prefix = (String) league[2];
            int    color  = (int)    league[3];

            // Reserve placeholder row immediately so order is stable
            LinearLayout sectionHolder = new LinearLayout(requireContext());
            sectionHolder.setOrientation(LinearLayout.VERTICAL);
            sectionHolder.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            contentContainer.addView(sectionHolder);

            // Build header now
            addAgendaHeader(sectionHolder, emoji, label, "PRÓXIMOS EVENTOS", color);

            // Add skeleton loading row
            ProgressBar mini = new ProgressBar(requireContext(), null,
                android.R.attr.progressBarStyleSmall);
            mini.setIndeterminateTintList(
                android.content.res.ColorStateList.valueOf(color));
            LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            mlp.setMargins(dp(18), dp(4), 0, dp(16));
            mini.setLayoutParams(mlp);
            sectionHolder.addView(mini);

            // Fetch async
            final String path = "/" + prefix + "marcadores";
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String json = fetch(path);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (!isAdded()) return;
                        sectionHolder.removeView(mini);
                        buildLeagueRow(sectionHolder, json, color);
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (!isAdded()) return;
                        sectionHolder.removeView(mini);
                        addNoDataLabel(sectionHolder);
                    });
                }
            });
        }

        // 3) IPTV Canales section (async, at the bottom)
        LinearLayout iptv = new LinearLayout(requireContext());
        iptv.setOrientation(LinearLayout.VERTICAL);
        iptv.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        contentContainer.addView(iptv);
        addAgendaHeader(iptv, "📺", "CANALES IPTV", "TRANSMISIONES", 0xFFFFB800);
        ProgressBar iptvBar = new ProgressBar(requireContext(), null,
            android.R.attr.progressBarStyleSmall);
        iptvBar.setIndeterminateTintList(
            android.content.res.ColorStateList.valueOf(0xFFFFB800));
        LinearLayout.LayoutParams iblp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        iblp.setMargins(dp(18), dp(4), 0, dp(16));
        iptvBar.setLayoutParams(iblp);
        iptv.addView(iptvBar);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = fetch("/canales");
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    iptv.removeView(iptvBar);
                    buildIptvList(iptv, json);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    iptv.removeView(iptvBar);
                    addNoDataLabel(iptv);
                });
            }
        });
    }

    // ─── Gol-3 live carousel ─────────────────────────────────────────────────────

    private void buildGol3Carousel() {
        LinearLayout section = new LinearLayout(requireContext());
        section.setOrientation(LinearLayout.VERTICAL);
        section.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        contentContainer.addView(section);

        addAgendaHeader(section, "🔥", "EN VIVO", "PARTIDOS AHORA", 0xFFFF3B3B);

        HorizontalScrollView hsv = new HorizontalScrollView(requireContext());
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout.LayoutParams hsvLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        hsvLp.setMargins(0, 0, 0, dp(8));
        hsv.setLayoutParams(hsvLp);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(16), dp(4), dp(16), dp(16));

        // Skeleton while loading
        for (int i = 0; i < 3; i++) row.addView(makeCarouselSkeleton());
        hsv.addView(row);
        section.addView(hsv);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = fetch("/gol-3");
                List<LiveMatchServerDialog.LiveMatch> matches = parseGol3(json);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    row.removeAllViews();
                    if (matches.isEmpty()) {
                        addNoDataLabel(section);
                    } else {
                        for (LiveMatchServerDialog.LiveMatch m : matches) {
                            row.addView(makeLiveMatchCard(m));
                        }
                    }
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    row.removeAllViews();
                    addNoDataLabel(section);
                });
            }
        });
    }

    private List<LiveMatchServerDialog.LiveMatch> parseGol3(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray arr   = root.optJSONArray("transmisiones");
        if (arr == null) arr = new JSONArray(json);

        // Group by title preserving order
        LinkedHashMap<String, List<String[]>> grouped = new LinkedHashMap<>();
        LinkedHashMap<String, JSONObject>     meta    = new LinkedHashMap<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject item  = arr.getJSONObject(i);
            String titulo    = item.optString("titulo", "Partido " + (i + 1));
            String canal     = item.optString("canal", "Servidor " + (i + 1));
            String url       = item.optString("url", "");
            if (url.isEmpty()) continue;

            if (!grouped.containsKey(titulo)) {
                grouped.put(titulo, new ArrayList<>());
                meta.put(titulo, item);
            }
            grouped.get(titulo).add(new String[]{canal, url});
        }

        List<LiveMatchServerDialog.LiveMatch> result = new ArrayList<>();
        for (String titulo : grouped.keySet()) {
            JSONObject m = meta.get(titulo);
            result.add(new LiveMatchServerDialog.LiveMatch(
                titulo,
                m.optString("liga", ""),
                m.optString("hora", ""),
                m.optString("fecha", ""),
                m.optString("logoUrl", ""),
                grouped.get(titulo)
            ));
        }
        return result;
    }

    private View makeLiveMatchCard(LiveMatchServerDialog.LiveMatch match) {
        int CARD_W = dp(188);
        int CARD_H = dp(238);

        // ── Card container ────────────────────────────────────────────────────────
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setClickable(true);
        card.setFocusable(true);
        card.setBackgroundResource(R.drawable.live_match_card_premium);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(CARD_W, CARD_H);
        lp.setMarginEnd(dp(12));
        card.setLayoutParams(lp);

        // ── Top visual area ───────────────────────────────────────────────────────
        FrameLayout topArea = new FrameLayout(requireContext());
        LinearLayout.LayoutParams topLp = new LinearLayout.LayoutParams(MATCH_PARENT, dp(126));
        topArea.setLayoutParams(topLp);

        // Gradient background for top area
        GradientDrawable topBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xFF200800, 0xFF0F0F1E});
        topBg.setCornerRadii(new float[]{dp(17),dp(17), dp(17),dp(17), 0,0, 0,0});
        topArea.setBackground(topBg);

        // Centered logo circle
        ImageView logoImg = new ImageView(requireContext());
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setShape(GradientDrawable.OVAL);
        logoBg.setColor(0x22FFFFFF);
        logoBg.setStroke(dp(2), 0x55FFFFFF);
        logoImg.setBackground(logoBg);
        int logoSz = dp(76);
        FrameLayout.LayoutParams logoLp = new FrameLayout.LayoutParams(logoSz, logoSz);
        logoLp.gravity = Gravity.CENTER;
        logoImg.setLayoutParams(logoLp);
        logoImg.setPadding(dp(10), dp(10), dp(10), dp(10));

        if (!match.logoUrl.isEmpty() && isAdded()) {
            Glide.with(this)
                .load(match.logoUrl)
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .into(logoImg);
        }
        topArea.addView(logoImg);

        // EN VIVO badge — top left
        TextView tvLive = new TextView(requireContext());
        tvLive.setText("● EN VIVO");
        tvLive.setTextColor(0xFF00E676);
        tvLive.setTextSize(8);
        tvLive.setTypeface(null, Typeface.BOLD);
        tvLive.setLetterSpacing(0.1f);
        GradientDrawable liveBg = new GradientDrawable();
        liveBg.setColor(0x1A00E676);
        liveBg.setStroke(dp(1), 0x4400E676);
        liveBg.setCornerRadius(dp(50));
        tvLive.setBackground(liveBg);
        tvLive.setPadding(dp(7), dp(3), dp(7), dp(3));
        FrameLayout.LayoutParams liveLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        liveLp.gravity = Gravity.TOP | Gravity.START;
        liveLp.setMargins(dp(10), dp(10), 0, 0);
        tvLive.setLayoutParams(liveLp);
        topArea.addView(tvLive);

        // Server count badge — top right (only if > 1)
        int srvCount = match.servidores.size();
        if (srvCount > 1) {
            TextView tvSrv = new TextView(requireContext());
            tvSrv.setText("📡 " + srvCount);
            tvSrv.setTextColor(0xFFFF6B00);
            tvSrv.setTextSize(8);
            tvSrv.setTypeface(null, Typeface.BOLD);
            GradientDrawable srvBg = new GradientDrawable();
            srvBg.setColor(0x1AFF6B00);
            srvBg.setStroke(dp(1), 0x55FF6B00);
            srvBg.setCornerRadius(dp(50));
            tvSrv.setBackground(srvBg);
            tvSrv.setPadding(dp(7), dp(3), dp(7), dp(3));
            FrameLayout.LayoutParams srvLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            srvLp.gravity = Gravity.TOP | Gravity.END;
            srvLp.setMargins(0, dp(10), dp(10), 0);
            tvSrv.setLayoutParams(srvLp);
            topArea.addView(tvSrv);
        }

        // Bottom fade overlay on top area
        View fade = new View(requireContext());
        GradientDrawable fadeBg = new GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            new int[]{0xCC1A0A00, Color.TRANSPARENT});
        fade.setBackground(fadeBg);
        FrameLayout.LayoutParams fadeLp = new FrameLayout.LayoutParams(MATCH_PARENT, dp(45));
        fadeLp.gravity = Gravity.BOTTOM;
        fade.setLayoutParams(fadeLp);
        topArea.addView(fade);

        card.addView(topArea);

        // ── Bottom info area ──────────────────────────────────────────────────────
        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f);
        infoLp.setMargins(dp(12), dp(10), dp(12), dp(12));
        info.setLayoutParams(infoLp);

        // League
        String liga = match.liga != null && !match.liga.isEmpty()
            ? match.liga.toUpperCase() : "FÚTBOL";
        TextView tvLeague = new TextView(requireContext());
        tvLeague.setText("🏆  " + liga);
        tvLeague.setTextColor(0xFFFF6B00);
        tvLeague.setTextSize(9);
        tvLeague.setTypeface(null, Typeface.BOLD);
        tvLeague.setLetterSpacing(0.08f);
        tvLeague.setMaxLines(1);
        tvLeague.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams leagueLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        leagueLp.setMargins(0, 0, 0, dp(5));
        tvLeague.setLayoutParams(leagueLp);
        info.addView(tvLeague);

        // Match title
        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(match.titulo);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(12);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvTitle.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(5));
        tvTitle.setLayoutParams(titleLp);
        info.addView(tvTitle);

        // Time
        if (match.hora != null && !match.hora.isEmpty()) {
            TextView tvTime = new TextView(requireContext());
            tvTime.setText("🕐  " + match.hora + (match.fecha != null && !match.fecha.isEmpty() ? "  ·  " + match.fecha : ""));
            tvTime.setTextColor(0x77FFFFFF);
            tvTime.setTextSize(10);
            tvTime.setMaxLines(1);
            tvTime.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams timeLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            timeLp.setMargins(0, 0, 0, dp(8));
            tvTime.setLayoutParams(timeLp);
            info.addView(tvTime);
        }

        // CTA button
        TextView tvCta = new TextView(requireContext());
        String ctaLabel = srvCount > 1
            ? "▶  " + srvCount + " servidores"
            : "▶  Ver ahora";
        tvCta.setText(ctaLabel);
        tvCta.setTextColor(0xFFFFFFFF);
        tvCta.setTextSize(11);
        tvCta.setTypeface(null, Typeface.BOLD);
        tvCta.setGravity(Gravity.CENTER);
        GradientDrawable ctaBg = new GradientDrawable();
        ctaBg.setColor(0xFFFF6B00);
        ctaBg.setCornerRadius(dp(10));
        tvCta.setBackground(ctaBg);
        tvCta.setPadding(dp(8), dp(8), dp(8), dp(8));
        LinearLayout.LayoutParams ctaLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ctaLp.setMargins(0, dp(4), 0, 0);
        tvCta.setLayoutParams(ctaLp);
        info.addView(tvCta);

        card.addView(info);

        // ── Click handler ─────────────────────────────────────────────────────────
        card.setOnClickListener(v -> {
            card.animate().scaleX(0.93f).scaleY(0.93f).setDuration(70)
                .withEndAction(() -> card.animate().scaleX(1f).scaleY(1f).setDuration(140).start())
                .start();
            if (!isAdded()) return;
            if (srvCount == 1) {
                // Single server — launch player directly
                Intent intent = new Intent(requireContext(), PlayerActivity.class);
                intent.putExtra("url",   match.servidores.get(0)[1]);
                intent.putExtra("title", match.titulo);
                startActivity(intent);
            } else {
                // Multiple servers — show glass dialog
                LiveMatchServerDialog.show(requireContext(), match);
            }
        });

        return card;
    }

    /** Skeleton placeholder card shown while loading */
    private View makeCarouselSkeleton() {
        View sk = new View(requireContext());
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{0x0AFFFFFF, 0x14FFFFFF, 0x0AFFFFFF});
        bg.setCornerRadius(dp(18));
        sk.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(188), dp(238));
        lp.setMarginEnd(dp(12));
        sk.setLayoutParams(lp);
        return sk;
    }

    // ─── League match row ─────────────────────────────────────────────────────────

    private void buildLeagueRow(LinearLayout parent, String json, int accentColor) {
        try {
            JSONArray arr = null;
            try {
                JSONObject root = new JSONObject(json);
                arr = findArray(root, "partidos","matches","fixtures","resultados","marcadores","data");
            } catch (Exception e) {
                arr = new JSONArray(json);
            }
            if (arr == null || arr.length() == 0) { addNoDataLabel(parent); return; }

            HorizontalScrollView hsv = new HorizontalScrollView(requireContext());
            hsv.setHorizontalScrollBarEnabled(false);
            hsv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            LinearLayout.LayoutParams hsvLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            hsvLp.setMargins(0, 0, 0, dp(8));
            hsv.setLayoutParams(hsvLp);

            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(dp(16), dp(4), dp(16), dp(14));

            for (int i = 0; i < arr.length(); i++) {
                try {
                    row.addView(makeMatchCard(arr.getJSONObject(i), accentColor));
                } catch (Exception ignored) {}
            }

            hsv.addView(row);
            parent.addView(hsv);

        } catch (Exception e) {
            addNoDataLabel(parent);
        }
    }

    private View makeMatchCard(JSONObject match, int accentColor) {
        String nameL  = getTeamName(match, "local");
        String nameV  = getTeamName(match, "visitante");
        String logoL  = getTeamLogo(match, "local");
        String logoV  = getTeamLogo(match, "visitante");
        String gL     = getTeamScore(match, "local");
        String gV     = getTeamScore(match, "visitante");
        String estado = match.optString("estado", match.optString("status", ""));
        String fecha  = match.optString("fecha",  match.optString("date",   ""));
        String hora   = match.optString("hora",   match.optString("time",   ""));
        String minuto = match.optString("minuto", match.optString("minute", ""));

        boolean isLive   = estado.toLowerCase().contains("vivo") ||
                           estado.toLowerCase().contains("live") ||
                           minuto.matches("\\d+.*");
        boolean hasScore = !gL.equals("-") && !gV.equals("-");

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(isLive ? R.drawable.agenda_match_live_card : R.drawable.agenda_match_card);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(158), dp(185));
        lp.setMarginEnd(dp(10));
        card.setLayoutParams(lp);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));

        // ── Top: LIVE or time badge ──
        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams trLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        trLp.setMargins(0, 0, 0, dp(8));
        topRow.setLayoutParams(trLp);

        if (isLive) {
            TextView liveBadge = new TextView(requireContext());
            liveBadge.setText("  LIVE  ");
            liveBadge.setTextColor(0xFFFFFFFF);
            liveBadge.setTextSize(9);
            liveBadge.setTypeface(null, Typeface.BOLD);
            liveBadge.setLetterSpacing(0.1f);
            liveBadge.setBackgroundResource(R.drawable.agenda_live_badge);
            liveBadge.setPadding(dp(6), dp(2), dp(6), dp(2));
            topRow.addView(liveBadge);
            if (!minuto.isEmpty()) {
                TextView tvMin = new TextView(requireContext());
                tvMin.setText("  " + minuto);
                tvMin.setTextColor(0xAAFF6B00);
                tvMin.setTextSize(10);
                tvMin.setTypeface(null, Typeface.BOLD);
                topRow.addView(tvMin);
            }
        } else if (!hora.isEmpty() || !fecha.isEmpty()) {
            String timeStr = hora.isEmpty() ? fecha : hora;
            TextView tvTime = new TextView(requireContext());
            tvTime.setText("  " + timeStr + "  ");
            tvTime.setTextColor(0xCCFFFFFF);
            tvTime.setTextSize(11);
            tvTime.setTypeface(null, Typeface.BOLD);
            tvTime.setBackgroundResource(R.drawable.agenda_time_badge);
            tvTime.setPadding(dp(6), dp(3), dp(6), dp(3));
            topRow.addView(tvTime);
        }
        card.addView(topRow);

        // ── Logos row ──
        LinearLayout logosRow = new LinearLayout(requireContext());
        logosRow.setOrientation(LinearLayout.HORIZONTAL);
        logosRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lrLp = new LinearLayout.LayoutParams(MATCH_PARENT, dp(52));
        lrLp.setMargins(0, dp(2), 0, dp(2));
        logosRow.setLayoutParams(lrLp);

        // Left logo
        ImageView logoLeft = makeLogoView(logoL);
        logoLeft.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        logosRow.addView(logoLeft);

        // VS spacer
        TextView tvVs = new TextView(requireContext());
        tvVs.setText("vs");
        tvVs.setTextColor(0x55FFFFFF);
        tvVs.setTextSize(11);
        tvVs.setGravity(Gravity.CENTER);
        tvVs.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        logosRow.addView(tvVs);

        // Right logo
        ImageView logoRight = makeLogoView(logoV);
        logoRight.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        logosRow.addView(logoRight);

        card.addView(logosRow);

        // ── Score or separator ──
        if (hasScore) {
            TextView tvScore = new TextView(requireContext());
            tvScore.setText(gL + "  —  " + gV);
            tvScore.setTextColor(accentColor);
            tvScore.setTextSize(22);
            tvScore.setTypeface(null, Typeface.BOLD);
            tvScore.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            slp.setMargins(0, dp(4), 0, dp(6));
            tvScore.setLayoutParams(slp);
            card.addView(tvScore);
        } else {
            View divider = new View(requireContext());
            divider.setBackgroundColor(0x15FFFFFF);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(MATCH_PARENT, dp(1));
            dlp.setMargins(0, dp(6), 0, dp(6));
            divider.setLayoutParams(dlp);
            card.addView(divider);
        }

        // ── Team names ──
        LinearLayout namesRow = new LinearLayout(requireContext());
        namesRow.setOrientation(LinearLayout.HORIZONTAL);
        namesRow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        TextView tvNameL = new TextView(requireContext());
        tvNameL.setText(nameL);
        tvNameL.setTextColor(0xEEFFFFFF);
        tvNameL.setTextSize(11);
        tvNameL.setTypeface(null, Typeface.BOLD);
        tvNameL.setMaxLines(2);
        tvNameL.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));

        TextView tvNameV = new TextView(requireContext());
        tvNameV.setText(nameV);
        tvNameV.setTextColor(0xEEFFFFFF);
        tvNameV.setTextSize(11);
        tvNameV.setTypeface(null, Typeface.BOLD);
        tvNameV.setMaxLines(2);
        tvNameV.setGravity(Gravity.END);
        tvNameV.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));

        namesRow.addView(tvNameL);
        namesRow.addView(tvNameV);
        card.addView(namesRow);

        // Bottom date if has score + date
        if (hasScore && !fecha.isEmpty()) {
            TextView tvDate = new TextView(requireContext());
            tvDate.setText(fecha);
            tvDate.setTextColor(0x55FFFFFF);
            tvDate.setTextSize(10);
            tvDate.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams datelp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            datelp.setMargins(0, dp(5), 0, 0);
            tvDate.setLayoutParams(datelp);
            card.addView(tvDate);
        }

        return card;
    }

    private ImageView makeLogoView(String url) {
        ImageView iv = new ImageView(requireContext());
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (!url.isEmpty() && isAdded()) {
            Glide.with(this)
                 .load(url)
                 .apply(new RequestOptions().transform(new RoundedCorners(dp(6))))
                 .placeholder(R.drawable.ic_channel_placeholder)
                 .error(R.drawable.ic_channel_placeholder)
                 .into(iv);
        }
        return iv;
    }

    // ─── IPTV list ────────────────────────────────────────────────────────────────

    private void buildIptvList(LinearLayout parent, String json) {
        try {
            JSONArray arr;
            try {
                JSONObject root = new JSONObject(json);
                arr = findArray(root, "canales","channels","data","items");
                if (arr == null) arr = new JSONArray(json);
            } catch (Exception e) {
                arr = new JSONArray(json);
            }
            if (arr.length() == 0) { addNoDataLabel(parent); return; }

            LinearLayout listHolder = new LinearLayout(requireContext());
            listHolder.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            hlp.setMargins(dp(16), 0, dp(16), dp(8));
            listHolder.setLayoutParams(hlp);
            parent.addView(listHolder);

            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject c  = arr.getJSONObject(i);
                    String nombre = c.optString("nombre", c.optString("name",     "Canal "+(i+1)));
                    String bandera= c.optString("bandera",c.optString("flag",     "📺"));
                    String pUrl   = c.optString("player_url", c.optString("url",  ""));
                    String pais   = c.optString("pais",   c.optString("country",  ""));
                    if (pUrl.isEmpty()) continue;
                    listHolder.addView(makeChannelRow(bandera + "  " + nombre, pais, pUrl));
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            addNoDataLabel(parent);
        }
    }

    private View makeChannelRow(String name, String country, String url) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.agenda_channel_card);
        row.setClickable(true);
        row.setFocusable(true);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(6));
        row.setLayoutParams(lp);

        LinearLayout textBlock = new LinearLayout(requireContext());
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));

        TextView tvName = new TextView(requireContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(13);
        tvName.setTypeface(null, Typeface.BOLD);
        textBlock.addView(tvName);

        if (!country.isEmpty()) {
            TextView tvCountry = new TextView(requireContext());
            tvCountry.setText(country);
            tvCountry.setTextColor(0x55FFFFFF);
            tvCountry.setTextSize(11);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            cp.setMargins(0, dp(2), 0, 0);
            tvCountry.setLayoutParams(cp);
            textBlock.addView(tvCountry);
        }

        TextView tvPlay = new TextView(requireContext());
        tvPlay.setText("▶  VER");
        tvPlay.setTextColor(0xFFFF6B00);
        tvPlay.setTextSize(12);
        tvPlay.setTypeface(null, Typeface.BOLD);

        row.addView(textBlock);
        row.addView(tvPlay);

        row.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", name);
            startActivity(intent);
        });
        return row;
    }

    // ─── Section header ───────────────────────────────────────────────────────────

    private void addAgendaHeader(LinearLayout parent, String emoji, String title,
                                  String subtitle, int accentColor) {
        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        wlp.setMargins(0, dp(18), 0, dp(10));
        wrapper.setLayoutParams(wlp);

        LinearLayout titleRow = new LinearLayout(requireContext());
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // Gold accent bar
        View bar = new View(requireContext());
        bar.setBackgroundColor(accentColor);
        LinearLayout.LayoutParams barlp = new LinearLayout.LayoutParams(dp(3), dp(22));
        barlp.setMargins(dp(16), 0, dp(12), 0);
        bar.setLayoutParams(barlp);
        titleRow.addView(bar);

        // Emoji
        TextView tvEmoji = new TextView(requireContext());
        tvEmoji.setText(emoji + "  ");
        tvEmoji.setTextSize(16);
        titleRow.addView(tvEmoji);

        // Title
        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setLetterSpacing(0.06f);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        titleRow.addView(tvTitle);

        wrapper.addView(titleRow);

        // Subtitle
        TextView tvSub = new TextView(requireContext());
        tvSub.setText(subtitle);
        tvSub.setTextColor(0x66FFFFFF);
        tvSub.setTextSize(11);
        tvSub.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams sublp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        sublp.setMargins(dp(31), dp(3), 0, 0);
        tvSub.setLayoutParams(sublp);
        wrapper.addView(tvSub);

        parent.addView(wrapper);
    }

    private void addNoDataLabel(LinearLayout parent) {
        TextView tv = new TextView(requireContext());
        tv.setText("Sin datos disponibles");
        tv.setTextColor(0x44FFFFFF);
        tv.setTextSize(12);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.setMargins(dp(31), 0, 0, dp(16));
        tv.setLayoutParams(lp);
        parent.addView(tv);
    }

    // ─── JSON Helpers ─────────────────────────────────────────────────────────────

    private String getTeamName(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            for (String f : new String[]{"nombre","name","nombreCorto"}) {
                String n = team.optString(f, "");
                if (!n.isEmpty() && !n.equals("null")) return n;
            }
        }
        String flat = match.optString(key, "");
        if (!flat.startsWith("{") && !flat.isEmpty()) return flat;
        return key.equals("local") ? "Local" : "Visitante";
    }

    private String getTeamScore(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            Object m = team.opt("marcador");
            if (m != null && !m.toString().equals("null") && !m.toString().isEmpty())
                return m.toString();
        }
        boolean isLocal = key.equals("local");
        int v = match.optInt(isLocal ? "marcador_local" : "marcador_visitante",
                match.optInt(isLocal ? "score_home"     : "score_away",
                match.optInt(isLocal ? "goles_local"    : "goles_visitante", -1)));
        return v >= 0 ? String.valueOf(v) : "-";
    }

    private String getTeamLogo(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            for (String f : new String[]{"logo","logo_url","escudo","badge"}) {
                String l = team.optString(f, "");
                if (!l.isEmpty() && !l.equals("null")) return l;
            }
        }
        return match.optString(key + "_logo", "");
    }

    private JSONArray findArray(JSONObject root, String... keys) {
        for (String k : keys) {
            JSONArray arr = root.optJSONArray(k);
            if (arr != null && arr.length() > 0) return arr;
        }
        return null;
    }

    private String extractUrl(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String url = obj.optString("url", obj.optString("stream_url",
                         obj.optString("embed_url", obj.optString("player_url",
                         obj.optString("hls", obj.optString("m3u8",""))))));
            if (!url.isEmpty()) return url;
            java.util.Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                Object v = obj.opt(keys.next());
                if (v instanceof String && ((String)v).startsWith("http")) return (String)v;
            }
        } catch (Exception ignored) {}
        if (json.trim().startsWith("http")) return json.trim();
        return "";
    }

    private String fetch(String path) throws Exception {
        URL url = new URL(API + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept","application/json");
        c.setConnectTimeout(12000);
        c.setReadTimeout(15000);
        int code = c.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }
}
