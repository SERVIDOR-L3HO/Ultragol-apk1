package com.ultragol.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.StreamingApi;
import java.util.List;

public class ServerSelectDialog {

    public static void show(Context context, ContentItem item) {
        if (item.getContentType() == ContentItem.TYPE_SPORT
                || item.getContentType() == ContentItem.TYPE_TV) {
            launchUrl(context, item.getStreamUrl(), item.getTitle());
            return;
        }

        BottomSheetDialog sheet = new BottomSheetDialog(context, R.style.GlassBottomSheetTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server_select, null);
        sheet.setContentView(view);

        ((TextView) view.findViewById(R.id.serverDialogTitle)).setText(item.getTitle());

        boolean isTV = item.getContentType() != ContentItem.TYPE_MOVIE;
        View episodeSection = view.findViewById(R.id.episodeSection);
        if (episodeSection != null) episodeSection.setVisibility(isTV ? View.VISIBLE : View.GONE);

        final int[] season  = {1};
        final int[] episode = {1};
        final TextView tvSeason  = isTV ? (TextView) view.findViewById(R.id.tvSeason)  : null;
        final TextView tvEpisode = isTV ? (TextView) view.findViewById(R.id.tvEpisode) : null;

        View loadingLayout  = view.findViewById(R.id.serverLoadingLayout);
        LinearLayout servers = view.findViewById(R.id.serversLayout);
        View langTabs       = view.findViewById(R.id.langTabs);
        TextView tabLatino  = view.findViewById(R.id.tabLatino);
        TextView tabEspanol = view.findViewById(R.id.tabEspanol);

        if (loadingLayout != null) loadingLayout.setVisibility(View.VISIBLE);
        if (servers      != null) servers.setVisibility(View.GONE);
        if (langTabs     != null) langTabs.setVisibility(View.GONE);

        View b1 = view.findViewById(R.id.btnServer1);
        View b2 = view.findViewById(R.id.btnServer2);
        View b3 = view.findViewById(R.id.btnServer3);
        if (b1 != null) b1.setVisibility(View.GONE);
        if (b2 != null) b2.setVisibility(View.GONE);
        if (b3 != null) b3.setVisibility(View.GONE);

        Handler main = new Handler(Looper.getMainLooper());
        final StreamingApi.ServerData[] cachedData = {null};
        final String[] activeTab = {"latino"};

        Runnable doFetch = () -> {
            int s = season[0];
            int e = episode[0];
            new Thread(() -> {
                try {
                    StreamingApi.ServerData data = isTV
                        ? StreamingApi.fetchSeriesServers(item.getTmdbId(), s, e)
                        : StreamingApi.fetchMovieServers(item.getTmdbId());
                    main.post(() -> {
                        if (!sheet.isShowing()) return;
                        cachedData[0] = data;
                        if (loadingLayout != null) loadingLayout.setVisibility(View.GONE);
                        boolean hasLat = !data.latino.isEmpty();
                        boolean hasEsp = !data.espanol.isEmpty();
                        if (hasLat && hasEsp && langTabs != null) {
                            langTabs.setVisibility(View.VISIBLE);
                            setTab(tabLatino, tabEspanol, true);
                            activeTab[0] = "latino";
                        } else if (hasEsp) {
                            activeTab[0] = "espanol";
                        }
                        if (servers != null) servers.setVisibility(View.VISIBLE);
                        populateServers(context, sheet, servers, data, activeTab[0], item);
                    });
                } catch (Exception ex) {
                    main.post(() -> {
                        if (!sheet.isShowing()) return;
                        if (loadingLayout != null) loadingLayout.setVisibility(View.GONE);
                        if (servers      != null) servers.setVisibility(View.VISIBLE);
                        showFallback(context, sheet, servers, item, s, e);
                    });
                }
            }).start();
        };

        if (tabLatino  != null) tabLatino.setOnClickListener(v  -> {
            activeTab[0] = "latino";
            setTab(tabLatino, tabEspanol, true);
            if (cachedData[0] != null) populateServers(context, sheet, servers, cachedData[0], "latino", item);
        });
        if (tabEspanol != null) tabEspanol.setOnClickListener(v -> {
            activeTab[0] = "espanol";
            setTab(tabLatino, tabEspanol, false);
            if (cachedData[0] != null) populateServers(context, sheet, servers, cachedData[0], "espanol", item);
        });

        if (tvSeason != null) {
            view.findViewById(R.id.btnSeasonMinus).setOnClickListener(v -> {
                if (season[0] > 1) { season[0]--; tvSeason.setText(String.valueOf(season[0])); }
            });
            view.findViewById(R.id.btnSeasonPlus).setOnClickListener(v -> {
                if (season[0] < 20) { season[0]++; tvSeason.setText(String.valueOf(season[0])); }
            });
            view.findViewById(R.id.btnEpMinus).setOnClickListener(v -> {
                if (episode[0] > 1) { episode[0]--; tvEpisode.setText(String.valueOf(episode[0])); }
            });
            view.findViewById(R.id.btnEpPlus).setOnClickListener(v -> {
                if (episode[0] < 50) { episode[0]++; tvEpisode.setText(String.valueOf(episode[0])); }
            });
        }

        doFetch.run();
        sheet.show();
    }

    private static void setTab(TextView tabL, TextView tabE, boolean latinoActive) {
        if (tabL == null || tabE == null) return;
        if (latinoActive) {
            tabL.setBackgroundResource(R.drawable.server_chip_active);
            tabL.setTextColor(0xFFFFFFFF);
            tabE.setBackgroundResource(R.drawable.server_chip);
            tabE.setTextColor(0xFFB0B0CC);
        } else {
            tabE.setBackgroundResource(R.drawable.server_chip_active);
            tabE.setTextColor(0xFFFFFFFF);
            tabL.setBackgroundResource(R.drawable.server_chip);
            tabL.setTextColor(0xFFB0B0CC);
        }
    }

    private static void populateServers(Context ctx, BottomSheetDialog sheet,
                                         LinearLayout container, StreamingApi.ServerData data,
                                         String lang, ContentItem item) {
        if (container == null) return;
        container.removeAllViews();
        List<StreamingApi.Server> list = "latino".equals(lang) ? data.latino : data.espanol;
        if (list.isEmpty()) {
            TextView empty = new TextView(ctx);
            empty.setText("No hay servidores disponibles.");
            empty.setTextColor(0xFF9090B0);
            empty.setTextSize(13f);
            empty.setPadding(0, dp(ctx, 12), 0, dp(ctx, 8));
            container.addView(empty);
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            StreamingApi.Server s = list.get(i);
            if (s.url == null || s.url.isEmpty()) continue;
            addServerButton(ctx, container, sheet, cap(s.name), item.getTitle() + " — " + cap(s.name), s.url);
        }
    }

    private static void showFallback(Context ctx, BottomSheetDialog sheet,
                                      LinearLayout container, ContentItem item, int s, int e) {
        if (container == null) return;
        container.removeAllViews();
        boolean mov = item.getContentType() == ContentItem.TYPE_MOVIE;
        int id = item.getTmdbId();
        String p = "?sub=es&lang=es&audio=es&muted=0&autoplay=1";
        String[][] fb = mov ? new String[][]{
            {"UnlimPlay",  "https://unlimplay.com/play/embed/movie/" + id + p},
            {"VidSrc",     "https://vidsrc.to/embed/movie/" + id},
            {"2Embed",     "https://www.2embed.cc/embed/" + id}
        } : new String[][]{
            {"UnlimPlay",  "https://unlimplay.com/play/embed/tv/" + id + "/" + s + "/" + e + p},
            {"VidSrc",     "https://vidsrc.to/embed/tv/" + id + "/" + s + "-" + e},
            {"2Embed",     "https://www.2embed.cc/embedtv/" + id + "&s=" + s + "&e=" + e}
        };
        for (String[] f : fb) {
            addServerButton(ctx, container, sheet, f[0], item.getTitle() + " — " + f[0], f[1]);
        }
    }

    private static void addServerButton(Context ctx, LinearLayout container, BottomSheetDialog sheet,
                                         String label, String title, String url) {
        View btn = LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_1, container, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 52));
        lp.setMargins(0, 0, 0, dp(ctx, 8));
        btn.setLayoutParams(lp);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp(ctx, 12));
        bg.setColor(0x1AFF6B00);
        bg.setStroke(1, 0x33FF6B00);
        btn.setBackground(bg);
        TextView tv = btn.findViewById(android.R.id.text1);
        tv.setText(label);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(14f);
        tv.setPadding(dp(ctx, 16), 0, dp(ctx, 16), 0);
        tv.setGravity(android.view.Gravity.CENTER_VERTICAL);
        btn.setClickable(true);
        btn.setOnClickListener(v -> { sheet.dismiss(); launchUrl(ctx, url, title); });
        container.addView(btn);
    }

    private static void launchUrl(Context ctx, String url, String title) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(ctx, "Servidor no disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(ctx, PlayerActivity.class);
        i.putExtra("url",   url);
        i.putExtra("title", title);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }

    private static String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static int dp(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }

    public static String buildUrl(ContentItem item, int serverIdx, int s, int e) {
        int id = item.getTmdbId();
        boolean mov = item.getContentType() == ContentItem.TYPE_MOVIE;
        String p = "?sub=es&lang=es&audio=es&muted=0&autoplay=1";
        switch (serverIdx) {
            case 0: return mov
                ? "https://unlimplay.com/play/embed/movie/" + id + p
                : "https://unlimplay.com/play/embed/tv/"    + id + "/" + s + "/" + e + p;
            case 1: return mov
                ? "https://vidsrc.to/embed/movie/" + id
                : "https://vidsrc.to/embed/tv/"    + id + "/" + s + "-" + e;
            default: return mov
                ? "https://www.2embed.cc/embed/" + id
                : "https://www.2embed.cc/embedtv/" + id + "&s=" + s + "&e=" + e;
        }
    }
}
