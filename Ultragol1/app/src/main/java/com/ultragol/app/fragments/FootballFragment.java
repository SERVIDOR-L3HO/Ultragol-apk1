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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Premium glass sports screen.
 * ANR-safe: staggered UI posts, item limits, destroyed-flag guard.
 */
public class FootballFragment extends Fragment {

    private static final String API          = "https://ultragol-api-3--maricarmen43549.replit.app";
    private static final int    MAX_MATCHES  = 8;   // per league
    private static final int    MAX_CHANNELS = 25;  // IPTV list
    private static final long   UI_STAGGER   = 80L; // ms between heavy UI posts

    private final ExecutorService pool = Executors.newFixedThreadPool(3);
    private final Handler         ui   = new Handler(Looper.getMainLooper());
    // ▶ destroyed flag — guards ALL ui.post() callbacks after onDestroyView
    private volatile boolean destroyed = false;

    private static final Object[][] LEAGUES = {
        {"🇲🇽", "LIGA MX",    "",            0xFFFF6B00},
        {"🏴",  "PREMIER",    "premier/",    0xFF5C9AFF},
        {"🇪🇸", "LA LIGA",    "laliga/",     0xFFFF3B3B},
        {"🇮🇹", "SERIE A",    "seriea/",     0xFF00C4FF},
        {"🇩🇪", "BUNDESLIGA", "bundesliga/", 0xFFFFD600},
        {"🇫🇷", "LIGUE 1",    "ligue1/",     0xFF5C9AFF},
        {"🇺🇸", "MLS",        "mls/",        0xFF00E676},
    };

    private LinearLayout contentContainer;

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup p, @Nullable Bundle s) {
        return i.inflate(R.layout.fragment_football, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        super.onViewCreated(view, s);
        destroyed = false;
        contentContainer = view.findViewById(R.id.footballContent);

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (isAdded()) requireActivity().onBackPressed();
            });
        }

        View btnRefresh = view.findViewById(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> {
                if (btnRefresh instanceof TextView) {
                    ((TextView) btnRefresh).animate()
                        .rotation(((TextView) btnRefresh).getRotation() + 360f)
                        .setDuration(500).start();
                }
                reload();
            });
        }

        reload();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyed = true;           // stop all pending callbacks
        pool.shutdownNow();
        ui.removeCallbacksAndMessages(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Safety guard — use at the top of every ui.post lambda
    // ─────────────────────────────────────────────────────────────────────────

    private boolean dead() {
        return destroyed || !isAdded() || getView() == null || getContext() == null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reload
    // ─────────────────────────────────────────────────────────────────────────

    private void reload() {
        if (dead()) return;
        contentContainer.removeAllViews();

        // stagger index so heavy UI builds don't pile up at once
        final AtomicInteger stagger = new AtomicInteger(0);

        // 1) EN VIVO carousel
        buildGol3Carousel(stagger.getAndIncrement());

        // 2) Each league (parallel network, staggered UI build)
        for (Object[] league : LEAGUES) {
            String emoji  = (String) league[0];
            String label  = (String) league[1];
            String prefix = (String) league[2];
            int    color  = (int)    league[3];

            LinearLayout holder = new LinearLayout(requireContext());
            holder.setOrientation(LinearLayout.VERTICAL);
            holder.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            contentContainer.addView(holder);

            addSectionHeader(holder, emoji, label, "PRÓXIMOS EVENTOS", color);

            View skeleton = makeSkeletonRow(color);
            holder.addView(skeleton);

            final long delay  = UI_STAGGER * stagger.getAndIncrement();
            final String path = "/" + prefix + "marcadores";

            pool.execute(() -> {
                String json = null;
                try { json = fetch(path); } catch (Exception ignored) {}
                final String result = json;
                ui.postDelayed(() -> {
                    if (dead()) return;
                    holder.removeView(skeleton);
                    if (result != null) buildLeagueRow(holder, result, color);
                    else                addNoDataLabel(holder);
                }, delay);
            });
        }

        // 3) IPTV channels (always last, biggest stagger)
        LinearLayout iptv = new LinearLayout(requireContext());
        iptv.setOrientation(LinearLayout.VERTICAL);
        iptv.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        contentContainer.addView(iptv);
        addSectionHeader(iptv, "📺", "CANALES EN VIVO", "IPTV · DEPORTES", 0xFFFFB800);

        View iptvSkel = makeSkeletonRow(0xFFFFB800);
        iptv.addView(iptvSkel);

        final long iptvDelay = UI_STAGGER * stagger.get();
        pool.execute(() -> {
            String json = null;
            try { json = fetch("/canales"); } catch (Exception ignored) {}
            final String result = json;
            ui.postDelayed(() -> {
                if (dead()) return;
                iptv.removeView(iptvSkel);
                if (result != null) buildIptvList(iptv, result);
                else                addNoDataLabel(iptv);
            }, iptvDelay);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EN VIVO carousel
    // ─────────────────────────────────────────────────────────────────────────

    private void buildGol3Carousel(int staggerIndex) {
        LinearLayout section = new LinearLayout(requireContext());
        section.setOrientation(LinearLayout.VERTICAL);
        section.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        contentContainer.addView(section);

        addSectionHeader(section, "🔥", "EN VIVO", "PARTIDOS AHORA", 0xFFFF3B3B);

        HorizontalScrollView hsv = new HorizontalScrollView(requireContext());
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout.LayoutParams hsvLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        hsvLp.setMargins(0, 0, 0, dp(4));
        hsv.setLayoutParams(hsvLp);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(16), dp(4), dp(16), dp(16));

        for (int i = 0; i < 3; i++) row.addView(makeCarouselSkeleton());
        hsv.addView(row);
        section.addView(hsv);

        pool.execute(() -> {
            List<LiveMatchServerDialog.LiveMatch> matches = null;
            try {
                String json = fetch("/gol-3");
                matches = parseGol3(json);
            } catch (Exception ignored) {}

            final List<LiveMatchServerDialog.LiveMatch> result = matches;
            ui.postDelayed(() -> {
                if (dead()) return;
                row.removeAllViews();
                if (result != null && !result.isEmpty()) {
                    for (LiveMatchServerDialog.LiveMatch m : result) {
                        row.addView(makeLiveMatchCard(m));
                    }
                } else {
                    addNoDataLabel(section);
                }
            }, UI_STAGGER * staggerIndex);
        });
    }

    private List<LiveMatchServerDialog.LiveMatch> parseGol3(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray arr   = root.optJSONArray("transmisiones");
        if (arr == null) arr = new JSONArray(json);

        LinkedHashMap<String, List<String[]>> grouped = new LinkedHashMap<>();
        LinkedHashMap<String, JSONObject>     meta    = new LinkedHashMap<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.getJSONObject(i);
            String titulo   = item.optString("titulo", "Partido " + (i + 1));
            String canal    = item.optString("canal", "Servidor " + (i + 1));
            String url      = item.optString("url", "");
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
                grouped.get(titulo)));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Live match card (premium)
    // ─────────────────────────────────────────────────────────────────────────

    private View makeLiveMatchCard(LiveMatchServerDialog.LiveMatch match) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setClickable(true);
        card.setFocusable(true);
        card.setBackgroundResource(R.drawable.sport_live_card_bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(200), WRAP_CONTENT);
        lp.setMarginEnd(dp(12));
        card.setLayoutParams(lp);

        // Top visual area
        FrameLayout topArea = new FrameLayout(requireContext());
        topArea.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, dp(128)));

        GradientDrawable topBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xFF1E0A00, 0xFF0E0E20, 0xFF07071A});
        topBg.setCornerRadii(new float[]{dp(20), dp(20), dp(20), dp(20), 0, 0, 0, 0});
        topArea.setBackground(topBg);

        // Double-ring logo
        FrameLayout logoFrame = new FrameLayout(requireContext());
        FrameLayout.LayoutParams lfLp = new FrameLayout.LayoutParams(dp(88), dp(88));
        lfLp.gravity = Gravity.CENTER;
        logoFrame.setLayoutParams(lfLp);

        View outerRing = new View(requireContext());
        GradientDrawable orBg = new GradientDrawable();
        orBg.setShape(GradientDrawable.OVAL);
        orBg.setColor(0x00000000);
        orBg.setStroke(dp(1), 0x44FF6B00);
        outerRing.setBackground(orBg);
        outerRing.setLayoutParams(new FrameLayout.LayoutParams(dp(88), dp(88)));
        logoFrame.addView(outerRing);

        ImageView logoImg = new ImageView(requireContext());
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setShape(GradientDrawable.OVAL);
        logoBg.setColor(0x20FFFFFF);
        logoBg.setStroke(dp(2), 0x99FF6B00);
        logoImg.setBackground(logoBg);
        FrameLayout.LayoutParams liLp = new FrameLayout.LayoutParams(dp(70), dp(70));
        liLp.gravity = Gravity.CENTER;
        logoImg.setLayoutParams(liLp);
        logoImg.setPadding(dp(10), dp(10), dp(10), dp(10));
        logoImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (!match.logoUrl.isEmpty() && isAdded()) {
            Glide.with(this).load(match.logoUrl).transform(new CircleCrop())
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder).into(logoImg);
        }
        logoFrame.addView(logoImg);
        topArea.addView(logoFrame);

        // EN VIVO badge
        LinearLayout lvBadge = makeLiveBadge();
        FrameLayout.LayoutParams lvLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lvLp.gravity = Gravity.TOP | Gravity.START;
        lvLp.setMargins(dp(10), dp(10), 0, 0);
        lvBadge.setLayoutParams(lvLp);
        topArea.addView(lvBadge);

        // Server count
        int srvCount = match.servidores.size();
        if (srvCount > 1) {
            TextView tvSrv = new TextView(requireContext());
            tvSrv.setText("📡 " + srvCount);
            tvSrv.setTextColor(0xFFFF6B00);
            tvSrv.setTextSize(8);
            tvSrv.setTypeface(null, Typeface.BOLD);
            GradientDrawable srvBg = new GradientDrawable();
            srvBg.setColor(0x22FF6B00);
            srvBg.setStroke(dp(1), 0x66FF6B00);
            srvBg.setCornerRadius(dp(50));
            tvSrv.setBackground(srvBg);
            tvSrv.setPadding(dp(8), dp(4), dp(8), dp(4));
            FrameLayout.LayoutParams sLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            sLp.gravity = Gravity.TOP | Gravity.END;
            sLp.setMargins(0, dp(10), dp(10), 0);
            tvSrv.setLayoutParams(sLp);
            topArea.addView(tvSrv);
        }

        // Bottom fade
        View fade = new View(requireContext());
        GradientDrawable fadeBg = new GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            new int[]{0xCC1A0800, Color.TRANSPARENT});
        fade.setBackground(fadeBg);
        FrameLayout.LayoutParams fadeLp = new FrameLayout.LayoutParams(MATCH_PARENT, dp(40));
        fadeLp.gravity = Gravity.BOTTOM;
        fade.setLayoutParams(fadeLp);
        topArea.addView(fade);
        card.addView(topArea);

        // Info area
        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        infoLp.setMargins(dp(14), dp(12), dp(14), dp(14));
        info.setLayoutParams(infoLp);

        // League pill
        String liga = (match.liga != null && !match.liga.isEmpty()) ? match.liga.toUpperCase() : "FÚTBOL";
        TextView tvLeague = new TextView(requireContext());
        tvLeague.setText("🏆  " + liga);
        tvLeague.setTextColor(0xFFFF6B00);
        tvLeague.setTextSize(8.5f);
        tvLeague.setTypeface(null, Typeface.BOLD);
        tvLeague.setLetterSpacing(0.08f);
        tvLeague.setMaxLines(1);
        tvLeague.setEllipsize(android.text.TextUtils.TruncateAt.END);
        GradientDrawable lpBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0x33FF6B00, 0x1AFF6B00});
        lpBg.setStroke(dp(1), 0x55FF6B00);
        lpBg.setCornerRadius(dp(50));
        tvLeague.setBackground(lpBg);
        tvLeague.setPadding(dp(9), dp(4), dp(9), dp(4));
        LinearLayout.LayoutParams leagLp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        leagLp.setMargins(0, 0, 0, dp(8));
        tvLeague.setLayoutParams(leagLp);
        info.addView(tvLeague);

        // Title
        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(match.titulo);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(12.5f);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvTitle.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(5));
        tvTitle.setLayoutParams(titleLp);
        info.addView(tvTitle);

        // Time
        if (match.hora != null && !match.hora.isEmpty()) {
            String timeStr = "🕐  " + match.hora;
            if (match.fecha != null && !match.fecha.isEmpty()) timeStr += "  ·  " + match.fecha;
            TextView tvTime = new TextView(requireContext());
            tvTime.setText(timeStr);
            tvTime.setTextColor(0x88FFFFFF);
            tvTime.setTextSize(10);
            tvTime.setMaxLines(1);
            tvTime.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams timeLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            timeLp.setMargins(0, 0, 0, dp(10));
            tvTime.setLayoutParams(timeLp);
            info.addView(tvTime);
        }

        // CTA
        TextView tvCta = new TextView(requireContext());
        tvCta.setText(srvCount > 1 ? "▶  " + srvCount + " servidores" : "▶  Ver ahora");
        tvCta.setTextColor(0xFFFF6B00);
        tvCta.setTextSize(11);
        tvCta.setTypeface(null, Typeface.BOLD);
        tvCta.setLetterSpacing(0.04f);
        tvCta.setGravity(Gravity.CENTER);
        tvCta.setBackgroundResource(R.drawable.live_cta_glass_btn);
        tvCta.setPadding(dp(8), dp(9), dp(8), dp(9));
        info.addView(tvCta);
        card.addView(info);

        card.setOnClickListener(v -> {
            card.animate().scaleX(0.94f).scaleY(0.94f).setDuration(70)
                .withEndAction(() -> card.animate().scaleX(1f).scaleY(1f).setDuration(130).start()).start();
            if (dead()) return;
            if (srvCount == 1) {
                Intent it = new Intent(requireContext(), PlayerActivity.class);
                it.putExtra("url",   match.servidores.get(0)[1]);
                it.putExtra("title", match.titulo);
                startActivity(it);
            } else {
                LiveMatchServerDialog.show(requireContext(), match);
            }
        });
        return card;
    }

    private View makeCarouselSkeleton() {
        View sk = new View(requireContext());
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{0x08FFFFFF, 0x12FFFFFF, 0x08FFFFFF});
        bg.setCornerRadius(dp(20));
        sk.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(200), dp(250));
        lp.setMarginEnd(dp(12));
        sk.setLayoutParams(lp);
        return sk;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // League score cards
    // ─────────────────────────────────────────────────────────────────────────

    private void buildLeagueRow(LinearLayout parent, String json, int accentColor) {
        try {
            JSONArray arr;
            try {
                JSONObject root = new JSONObject(json);
                arr = findArray(root, "partidos", "matches", "fixtures", "resultados", "marcadores", "data");
            } catch (Exception e) { arr = new JSONArray(json); }
            if (arr == null || arr.length() == 0) { addNoDataLabel(parent); return; }

            HorizontalScrollView hsv = new HorizontalScrollView(requireContext());
            hsv.setHorizontalScrollBarEnabled(false);
            hsv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            LinearLayout.LayoutParams hsvLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            hsvLp.setMargins(0, 0, 0, dp(6));
            hsv.setLayoutParams(hsvLp);

            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(dp(16), dp(4), dp(16), dp(14));

            // ▶ MAX_MATCHES cap prevents building too many views at once
            int limit = Math.min(arr.length(), MAX_MATCHES);
            for (int i = 0; i < limit; i++) {
                try { row.addView(makeMatchCard(arr.getJSONObject(i), accentColor)); }
                catch (Exception ignored) {}
            }

            hsv.addView(row);
            parent.addView(hsv);
        } catch (Exception e) { addNoDataLabel(parent); }
    }

    private View makeMatchCard(JSONObject match, int accentColor) {
        String nameL  = getTeamName(match, "local");
        String nameV  = getTeamName(match, "visitante");
        String logoL  = getTeamLogo(match, "local");
        String logoV  = getTeamLogo(match, "visitante");
        String gL     = getTeamScore(match, "local");
        String gV     = getTeamScore(match, "visitante");
        String estado = match.optString("estado", match.optString("status", ""));
        String fecha  = match.optString("fecha",  match.optString("date", ""));
        String hora   = match.optString("hora",   match.optString("time", ""));
        String minuto = match.optString("minuto", match.optString("minute", ""));

        boolean isLive   = estado.toLowerCase().contains("vivo") ||
                           estado.toLowerCase().contains("live") ||
                           minuto.matches("\\d+.*");
        boolean hasScore = !gL.equals("-") && !gV.equals("-");

        int r = Color.red(accentColor), g = Color.green(accentColor), b = Color.blue(accentColor);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setClickable(false);

        GradientDrawable cardBg = new GradientDrawable();
        if (isLive) {
            cardBg.setColor(Color.argb(22, 255, 68, 68));
            cardBg.setStroke(dp(1), Color.argb(60, 229, 57, 53));
        } else {
            cardBg.setColor(Color.argb(14, r, g, b));
            cardBg.setStroke(dp(1), Color.argb(28, r, g, b));
        }
        cardBg.setCornerRadius(dp(18));
        card.setBackground(cardBg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(168), WRAP_CONTENT);
        lp.setMarginEnd(dp(10));
        card.setLayoutParams(lp);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));

        // Badge row
        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams trLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        trLp.setMargins(0, 0, 0, dp(10));
        topRow.setLayoutParams(trLp);

        if (isLive) {
            LinearLayout lb = makeLiveBadge();
            topRow.addView(lb);
            if (!minuto.isEmpty()) {
                TextView tvMin = new TextView(requireContext());
                tvMin.setText("  " + minuto + "'");
                tvMin.setTextColor(Color.argb(190, r, g, b));
                tvMin.setTextSize(10);
                tvMin.setTypeface(null, Typeface.BOLD);
                topRow.addView(tvMin);
            }
        } else if (!hora.isEmpty() || !fecha.isEmpty()) {
            TextView tvTime = new TextView(requireContext());
            tvTime.setText(hora.isEmpty() ? fecha : hora);
            tvTime.setTextColor(0xCCFFFFFF);
            tvTime.setTextSize(10);
            tvTime.setTypeface(null, Typeface.BOLD);
            GradientDrawable tb = new GradientDrawable();
            tb.setColor(0x15FFFFFF);
            tb.setStroke(dp(1), 0x22FFFFFF);
            tb.setCornerRadius(dp(50));
            tvTime.setBackground(tb);
            tvTime.setPadding(dp(8), dp(3), dp(8), dp(3));
            topRow.addView(tvTime);
        }
        card.addView(topRow);

        // Logos + score row
        LinearLayout logosRow = new LinearLayout(requireContext());
        logosRow.setOrientation(LinearLayout.HORIZONTAL);
        logosRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lrLp = new LinearLayout.LayoutParams(MATCH_PARENT, dp(56));
        lrLp.setMargins(0, 0, 0, dp(4));
        logosRow.setLayoutParams(lrLp);

        LinearLayout leftTeam = new LinearLayout(requireContext());
        leftTeam.setGravity(Gravity.CENTER_HORIZONTAL);
        leftTeam.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, 1f));
        ImageView logoLeft = makeLogoView(logoL);
        logoLeft.setLayoutParams(new LinearLayout.LayoutParams(dp(46), dp(46)));
        leftTeam.addView(logoLeft);
        logosRow.addView(leftTeam);

        LinearLayout center = new LinearLayout(requireContext());
        center.setGravity(Gravity.CENTER);
        center.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        if (hasScore) {
            TextView tvScore = new TextView(requireContext());
            tvScore.setText(gL + " — " + gV);
            tvScore.setTextColor(isLive ? 0xFFFF5555 : accentColor);
            tvScore.setTextSize(19);
            tvScore.setTypeface(null, Typeface.BOLD);
            tvScore.setGravity(Gravity.CENTER);
            tvScore.setPadding(dp(8), 0, dp(8), 0);
            center.addView(tvScore);
        } else {
            TextView tvVs = new TextView(requireContext());
            tvVs.setText("VS");
            tvVs.setTextColor(0x44FFFFFF);
            tvVs.setTextSize(12);
            tvVs.setTypeface(null, Typeface.BOLD);
            tvVs.setGravity(Gravity.CENTER);
            tvVs.setPadding(dp(10), 0, dp(10), 0);
            center.addView(tvVs);
        }
        logosRow.addView(center);

        LinearLayout rightTeam = new LinearLayout(requireContext());
        rightTeam.setGravity(Gravity.CENTER_HORIZONTAL);
        rightTeam.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, 1f));
        ImageView logoRight = makeLogoView(logoV);
        logoRight.setLayoutParams(new LinearLayout.LayoutParams(dp(46), dp(46)));
        rightTeam.addView(logoRight);
        logosRow.addView(rightTeam);
        card.addView(logosRow);

        // Separator
        View sep = new View(requireContext());
        sep.setBackgroundColor(Color.argb(22, r, g, b));
        LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(MATCH_PARENT, dp(1));
        sepLp.setMargins(0, dp(2), 0, dp(7));
        sep.setLayoutParams(sepLp);
        card.addView(sep);

        // Team names
        LinearLayout namesRow = new LinearLayout(requireContext());
        namesRow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        TextView tvNL = new TextView(requireContext());
        tvNL.setText(nameL);
        tvNL.setTextColor(0xDDFFFFFF);
        tvNL.setTextSize(10.5f);
        tvNL.setTypeface(null, Typeface.BOLD);
        tvNL.setMaxLines(2);
        tvNL.setGravity(Gravity.START);
        tvNL.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        namesRow.addView(tvNL);

        View pad = new View(requireContext());
        pad.setLayoutParams(new LinearLayout.LayoutParams(dp(8), 1));
        namesRow.addView(pad);

        TextView tvNV = new TextView(requireContext());
        tvNV.setText(nameV);
        tvNV.setTextColor(0xDDFFFFFF);
        tvNV.setTextSize(10.5f);
        tvNV.setTypeface(null, Typeface.BOLD);
        tvNV.setMaxLines(2);
        tvNV.setGravity(Gravity.END);
        tvNV.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        namesRow.addView(tvNV);
        card.addView(namesRow);

        // Date
        if (!fecha.isEmpty()) {
            TextView tvDate = new TextView(requireContext());
            tvDate.setText(fecha.length() > 16 ? fecha.substring(0, 16) : fecha);
            tvDate.setTextColor(0x55FFFFFF);
            tvDate.setTextSize(9.5f);
            tvDate.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams dLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            dLp.setMargins(0, dp(5), 0, 0);
            tvDate.setLayoutParams(dLp);
            card.addView(tvDate);
        }
        return card;
    }

    private ImageView makeLogoView(String url) {
        ImageView iv = new ImageView(requireContext());
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (!url.isEmpty() && isAdded()) {
            Glide.with(this).load(url)
                .apply(new RequestOptions().transform(new RoundedCorners(dp(8))))
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder).into(iv);
        }
        return iv;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // IPTV list (capped at MAX_CHANNELS)
    // ─────────────────────────────────────────────────────────────────────────

    private void buildIptvList(LinearLayout parent, String json) {
        try {
            JSONArray arr;
            try {
                JSONObject root = new JSONObject(json);
                arr = findArray(root, "canales", "channels", "data", "items");
                if (arr == null) arr = new JSONArray(json);
            } catch (Exception e) { arr = new JSONArray(json); }
            if (arr.length() == 0) { addNoDataLabel(parent); return; }

            LinearLayout listHolder = new LinearLayout(requireContext());
            listHolder.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            hlp.setMargins(dp(14), 0, dp(14), dp(8));
            listHolder.setLayoutParams(hlp);
            parent.addView(listHolder);

            // ▶ MAX_CHANNELS cap — prevents hundreds of views on UI thread at once
            int limit = Math.min(arr.length(), MAX_CHANNELS);
            for (int i = 0; i < limit; i++) {
                try {
                    JSONObject c  = arr.getJSONObject(i);
                    String nombre = c.optString("nombre", c.optString("name", "Canal " + (i + 1)));
                    String bandera= c.optString("bandera", c.optString("flag", "📺"));
                    String pUrl   = c.optString("player_url", c.optString("url", ""));
                    String pais   = c.optString("pais", c.optString("country", ""));
                    if (pUrl.isEmpty()) continue;
                    listHolder.addView(makeChannelRow(bandera + "  " + nombre, pais, pUrl, i));
                } catch (Exception ignored) {}
            }

            // "Ver más" label if there are more
            if (arr.length() > MAX_CHANNELS) {
                TextView tvMore = new TextView(requireContext());
                tvMore.setText("+ " + (arr.length() - MAX_CHANNELS) + " canales más disponibles");
                tvMore.setTextColor(0x66FFFFFF);
                tvMore.setTextSize(11.5f);
                tvMore.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams moreLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                moreLp.setMargins(0, dp(4), 0, dp(8));
                tvMore.setLayoutParams(moreLp);
                listHolder.addView(tvMore);
            }
        } catch (Exception e) { addNoDataLabel(parent); }
    }

    private View makeChannelRow(String name, String country, String url, int index) {
        int[] palette = {0xFFFF6B00, 0xFF5C9AFF, 0xFFFF3B3B, 0xFF00C4FF, 0xFFFFD600, 0xFF00E676};
        int ac = palette[index % palette.length];
        int r  = Color.red(ac), g = Color.green(ac), b = Color.blue(ac);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setFocusable(true);

        GradientDrawable rowBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.argb(40, r, g, b), Color.argb(10, 255, 255, 255), Color.argb(8, 255, 255, 255)});
        rowBg.setCornerRadius(dp(14));
        rowBg.setStroke(dp(1), Color.argb(30, r, g, b));
        row.setBackground(rowBg);
        row.setPadding(dp(14), dp(13), dp(14), dp(13));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(lp);

        // Number circle
        TextView numView = new TextView(requireContext());
        numView.setText(String.valueOf(index + 1));
        numView.setTextColor(ac);
        numView.setTextSize(12);
        numView.setTypeface(null, Typeface.BOLD);
        numView.setGravity(Gravity.CENTER);
        GradientDrawable numBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{Color.argb(45, r, g, b), Color.argb(22, r, g, b)});
        numBg.setShape(GradientDrawable.OVAL);
        numBg.setStroke(dp(2), Color.argb(110, r, g, b));
        numView.setBackground(numBg);
        LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(dp(40), dp(40));
        numLp.setMarginEnd(dp(12));
        numView.setLayoutParams(numLp);
        row.addView(numView);

        // Text block
        LinearLayout text = new LinearLayout(requireContext());
        text.setOrientation(LinearLayout.VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));

        TextView tvName = new TextView(requireContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(13);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setMaxLines(1);
        tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
        text.addView(tvName);

        if (!country.isEmpty()) {
            TextView tvC = new TextView(requireContext());
            tvC.setText(country);
            tvC.setTextColor(0x66FFFFFF);
            tvC.setTextSize(10.5f);
            LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            cLp.setMargins(0, dp(3), 0, 0);
            tvC.setLayoutParams(cLp);
            text.addView(tvC);
        }
        row.addView(text);

        // Play pill
        TextView tvPlay = new TextView(requireContext());
        tvPlay.setText("▶  VER");
        tvPlay.setTextColor(ac);
        tvPlay.setTextSize(10.5f);
        tvPlay.setTypeface(null, Typeface.BOLD);
        tvPlay.setLetterSpacing(0.04f);
        GradientDrawable playBg = new GradientDrawable();
        playBg.setColor(Color.argb(28, r, g, b));
        playBg.setStroke(dp(1), Color.argb(80, r, g, b));
        playBg.setCornerRadius(dp(50));
        tvPlay.setBackground(playBg);
        tvPlay.setPadding(dp(12), dp(6), dp(12), dp(6));
        LinearLayout.LayoutParams pLp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        pLp.setMarginStart(dp(8));
        tvPlay.setLayoutParams(pLp);
        row.addView(tvPlay);

        row.setOnClickListener(v -> {
            if (dead()) return;
            row.animate().scaleX(0.97f).scaleY(0.97f).setDuration(60)
                .withEndAction(() -> row.animate().scaleX(1f).scaleY(1f).setDuration(110).start()).start();
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", name);
            startActivity(intent);
        });
        return row;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Section header
    // ─────────────────────────────────────────────────────────────────────────

    private void addSectionHeader(LinearLayout parent, String emoji, String title,
                                   String subtitle, int accentColor) {
        int r = Color.red(accentColor), g = Color.green(accentColor), b = Color.blue(accentColor);

        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        wlp.setMargins(0, dp(22), 0, dp(10));
        wrapper.setLayoutParams(wlp);

        LinearLayout titleRow = new LinearLayout(requireContext());
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // Gradient accent bar
        View bar = new View(requireContext());
        GradientDrawable barBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{accentColor, Color.argb(70, r, g, b)});
        barBg.setCornerRadius(dp(4));
        bar.setBackground(barBg);
        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(dp(3), dp(26));
        barLp.setMargins(dp(16), 0, dp(12), 0);
        bar.setLayoutParams(barLp);
        titleRow.addView(bar);

        TextView tvEmoji = new TextView(requireContext());
        tvEmoji.setText(emoji + "  ");
        tvEmoji.setTextSize(17);
        titleRow.addView(tvEmoji);

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(16.5f);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setLetterSpacing(0.06f);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        titleRow.addView(tvTitle);

        // Accent badge
        TextView tvBadge = new TextView(requireContext());
        tvBadge.setText("● LIVE");
        tvBadge.setTextColor(accentColor);
        tvBadge.setTextSize(9);
        tvBadge.setTypeface(null, Typeface.BOLD);
        tvBadge.setLetterSpacing(0.06f);
        GradientDrawable bdBg = new GradientDrawable();
        bdBg.setColor(Color.argb(28, r, g, b));
        bdBg.setStroke(dp(1), Color.argb(70, r, g, b));
        bdBg.setCornerRadius(dp(50));
        tvBadge.setBackground(bdBg);
        tvBadge.setPadding(dp(10), dp(5), dp(10), dp(5));
        LinearLayout.LayoutParams bdLp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        bdLp.setMarginEnd(dp(16));
        tvBadge.setLayoutParams(bdLp);
        titleRow.addView(tvBadge);

        wrapper.addView(titleRow);

        // Subtitle
        TextView tvSub = new TextView(requireContext());
        tvSub.setText(subtitle);
        tvSub.setTextColor(0x66FFFFFF);
        tvSub.setTextSize(10.5f);
        tvSub.setLetterSpacing(0.04f);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        subLp.setMargins(dp(31), dp(4), 0, 0);
        tvSub.setLayoutParams(subLp);
        wrapper.addView(tvSub);

        // Bottom shimmer line
        View line = new View(requireContext());
        GradientDrawable lineBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.argb(0, r, g, b), Color.argb(70, r, g, b), Color.TRANSPARENT});
        line.setBackground(lineBg);
        LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(MATCH_PARENT, dp(1));
        lineLp.setMargins(dp(16), dp(8), dp(16), 0);
        line.setLayoutParams(lineLp);
        wrapper.addView(line);

        parent.addView(wrapper);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shared helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Reusable EN VIVO badge with pulsing dot */
    private LinearLayout makeLiveBadge() {
        LinearLayout badge = new LinearLayout(requireContext());
        badge.setOrientation(LinearLayout.HORIZONTAL);
        badge.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0x2200E676);
        bg.setStroke(dp(1), 0x5500E676);
        bg.setCornerRadius(dp(50));
        badge.setBackground(bg);
        badge.setPadding(dp(7), dp(4), dp(9), dp(4));

        View dot = new View(requireContext());
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(0xFF00E676);
        dot.setBackground(dotBg);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(5), dp(5));
        dotLp.setMarginEnd(dp(5));
        dot.setLayoutParams(dotLp);
        badge.addView(dot);

        TextView tv = new TextView(requireContext());
        tv.setText("EN VIVO");
        tv.setTextColor(0xFF00E676);
        tv.setTextSize(8.5f);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setLetterSpacing(0.07f);
        badge.addView(tv);
        return badge;
    }

    private View makeSkeletonRow(int accentColor) {
        int r = Color.red(accentColor), g = Color.green(accentColor), b = Color.blue(accentColor);

        LinearLayout sk = new LinearLayout(requireContext());
        sk.setOrientation(LinearLayout.HORIZONTAL);
        sk.setPadding(dp(16), dp(4), dp(16), dp(14));
        sk.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        for (int i = 0; i < 3; i++) {
            View block = new View(requireContext());
            GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.argb(12, r, g, b), Color.argb(20, r, g, b), Color.argb(12, r, g, b)});
            bg.setCornerRadius(dp(18));
            block.setBackground(bg);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(168), dp(170));
            lp.setMarginEnd(dp(10));
            block.setLayoutParams(lp);
            sk.addView(block);
        }

        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        ProgressBar pb = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleSmall);
        pb.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(accentColor));
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        pbLp.setMargins(dp(18), dp(4), 0, dp(4));
        pb.setLayoutParams(pbLp);

        wrapper.addView(sk);
        wrapper.addView(pb);
        return wrapper;
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

    // ─────────────────────────────────────────────────────────────────────────
    // JSON helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String getTeamName(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            for (String f : new String[]{"nombre", "name", "nombreCorto"}) {
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
                match.optInt(isLocal ? "score_home" : "score_away",
                match.optInt(isLocal ? "goles_local" : "goles_visitante", -1)));
        return v >= 0 ? String.valueOf(v) : "-";
    }

    private String getTeamLogo(JSONObject match, String key) {
        JSONObject team = match.optJSONObject(key);
        if (team != null) {
            for (String f : new String[]{"logo", "logo_url", "escudo", "badge"}) {
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

    private String fetch(String path) throws Exception {
        URL url = new URL(API + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setConnectTimeout(10000);
        c.setReadTimeout(12000);
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
