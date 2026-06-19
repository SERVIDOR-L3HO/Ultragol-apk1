package com.ultragol.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.StreamingApi;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSelectDialog {

    public static void show(Context context, ContentItem item) {
        BottomSheetDialog sheet = new BottomSheetDialog(context, R.style.GlassBottomSheetTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server_select, null);
        sheet.setContentView(view);
        sheet.getBehavior().setPeekHeight(1000);

        TextView tvTitle = view.findViewById(R.id.serverDialogTitle);
        if (tvTitle != null) tvTitle.setText(item.getTitle());

        boolean isTV = item.getContentType() != ContentItem.TYPE_MOVIE;
        View episodeSection = view.findViewById(R.id.episodeSection);
        if (episodeSection != null) episodeSection.setVisibility(isTV ? View.VISIBLE : View.GONE);

        final int[] season  = {1};
        final int[] episode = {1};

        if (isTV) {
            TextView tvSeason  = view.findViewById(R.id.tvSeason);
            TextView tvEpisode = view.findViewById(R.id.tvEpisode);

            View btnSM = view.findViewById(R.id.btnSeasonMinus);
            View btnSP = view.findViewById(R.id.btnSeasonPlus);
            View btnEM = view.findViewById(R.id.btnEpMinus);
            View btnEP = view.findViewById(R.id.btnEpPlus);
            View btnLoad = view.findViewById(R.id.btnLoadEpisode);

            if (btnSM != null) btnSM.setOnClickListener(v -> {
                if (season[0] > 1) { season[0]--; if (tvSeason != null) tvSeason.setText(String.valueOf(season[0])); }
            });
            if (btnSP != null) btnSP.setOnClickListener(v -> {
                if (season[0] < 30) { season[0]++; if (tvSeason != null) tvSeason.setText(String.valueOf(season[0])); }
            });
            if (btnEM != null) btnEM.setOnClickListener(v -> {
                if (episode[0] > 1) { episode[0]--; if (tvEpisode != null) tvEpisode.setText(String.valueOf(episode[0])); }
            });
            if (btnEP != null) btnEP.setOnClickListener(v -> {
                if (episode[0] < 100) { episode[0]++; if (tvEpisode != null) tvEpisode.setText(String.valueOf(episode[0])); }
            });
            if (btnLoad != null) btnLoad.setOnClickListener(v ->
                loadServers(context, view, item, season[0], episode[0]));
        }

        loadServers(context, view, item, season[0], episode[0]);
        sheet.show();
    }

    private static void loadServers(Context context, View view,
                                    ContentItem item, int season, int episode) {
        LinearLayout serverContainer = view.findViewById(R.id.serverContainer);
        View loadingView = view.findViewById(R.id.loadingServers);
        View errorView   = view.findViewById(R.id.errorServers);
        View langTabs    = view.findViewById(R.id.langTabs);

        if (serverContainer != null) serverContainer.removeAllViews();
        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (errorView   != null) errorView.setVisibility(View.GONE);
        if (langTabs    != null) langTabs.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                StreamingApi.ServerData data = (item.getContentType() == ContentItem.TYPE_MOVIE)
                    ? StreamingApi.fetchMovieServers(item.getTmdbId())
                    : StreamingApi.fetchSeriesServers(item.getTmdbId(), season, episode);
                handler.post(() -> {
                    if (loadingView != null) loadingView.setVisibility(View.GONE);
                    renderServers(context, view, item, data);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    if (loadingView != null) loadingView.setVisibility(View.GONE);
                    if (errorView   != null) errorView.setVisibility(View.VISIBLE);
                    showFallback(context, view, item);
                });
            }
        });
        executor.shutdown();
    }

    private static void renderServers(Context context, View view,
                                      ContentItem item, StreamingApi.ServerData data) {
        LinearLayout serverContainer = view.findViewById(R.id.serverContainer);
        View langTabs = view.findViewById(R.id.langTabs);
        if (serverContainer == null) return;
        serverContainer.removeAllViews();

        boolean hasL = !data.latino.isEmpty();
        boolean hasE = !data.espanol.isEmpty();
        boolean hasS = !data.subtitulado.isEmpty();
        int langCount = (hasL ? 1 : 0) + (hasE ? 1 : 0) + (hasS ? 1 : 0);

        if (langCount > 1 && langTabs != null) {
            langTabs.setVisibility(View.VISIBLE);
            setupLangTabs(context, view, item, data, hasL, hasE, hasS);
        } else {
            if (langTabs != null) langTabs.setVisibility(View.GONE);
            List<StreamingApi.Server> list = hasL ? data.latino : hasE ? data.espanol : data.subtitulado;
            addServerRows(context, serverContainer, item, list);
        }
    }

    private static void setupLangTabs(Context context, View view, ContentItem item,
                                      StreamingApi.ServerData data,
                                      boolean hasL, boolean hasE, boolean hasS) {
        TextView tabL = view.findViewById(R.id.tabLatino);
        TextView tabE = view.findViewById(R.id.tabEspanol);
        TextView tabS = view.findViewById(R.id.tabSubtitulado);
        LinearLayout cnt = view.findViewById(R.id.serverContainer);

        if (tabL != null) tabL.setVisibility(hasL ? View.VISIBLE : View.GONE);
        if (tabE != null) tabE.setVisibility(hasE ? View.VISIBLE : View.GONE);
        if (tabS != null) tabS.setVisibility(hasS ? View.VISIBLE : View.GONE);

        // Show first available language
        TextView firstTab = hasL ? tabL : hasE ? tabE : tabS;
        List<StreamingApi.Server> firstList = hasL ? data.latino : hasE ? data.espanol : data.subtitulado;
        setActiveTab(firstTab, tabL, tabE, tabS);
        addServerRows(context, cnt, item, firstList);

        if (tabL != null) tabL.setOnClickListener(v -> {
            setActiveTab(tabL, tabL, tabE, tabS); cnt.removeAllViews();
            addServerRows(context, cnt, item, data.latino);
        });
        if (tabE != null) tabE.setOnClickListener(v -> {
            setActiveTab(tabE, tabL, tabE, tabS); cnt.removeAllViews();
            addServerRows(context, cnt, item, data.espanol);
        });
        if (tabS != null) tabS.setOnClickListener(v -> {
            setActiveTab(tabS, tabL, tabE, tabS); cnt.removeAllViews();
            addServerRows(context, cnt, item, data.subtitulado);
        });
    }

    private static void setActiveTab(TextView active, TextView... all) {
        for (TextView t : all) {
            if (t == null) continue;
            boolean isActive = (t == active);
            t.setBackgroundResource(isActive ? R.drawable.chip_active : R.drawable.chip_inactive);
            t.setTextColor(isActive ? 0xFFFFFFFF : 0xFFB0B0CC);
        }
    }

    private static void addServerRows(Context ctx, LinearLayout container,
                                      ContentItem item, List<StreamingApi.Server> servers) {
        if (container == null || servers == null || servers.isEmpty()) return;
        for (int i = 0; i < servers.size(); i++) {
            StreamingApi.Server srv = servers.get(i);
            View row = buildRow(ctx, srv, i + 1);
            final String url = srv.url;
            row.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", item.getTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            });
            container.addView(row);
        }
    }

    private static View buildRow(Context ctx, StreamingApi.Server server, int idx) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 62));
        lp.setMargins(dp(ctx, 16), 0, dp(ctx, 16), dp(ctx, 8));
        row.setLayoutParams(lp);
        row.setBackgroundResource(idx == 1 ? R.drawable.server_chip_active : R.drawable.server_chip);
        row.setPadding(dp(ctx, 14), 0, dp(ctx, 14), 0);
        row.setClickable(true);
        row.setFocusable(true);

        // Badge circle with index number
        TextView badge = new TextView(ctx);
        badge.setText(String.valueOf(idx));
        badge.setTextColor(idx == 1 ? 0xFFFF6B00 : 0xFF5050A0);
        badge.setTextSize(10);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);
        badge.setGravity(android.view.Gravity.CENTER);
        badge.setBackgroundResource(idx == 1 ? R.drawable.dot_indicator : R.drawable.dot_inactive);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(dp(ctx, 26), dp(ctx, 26));
        bp.setMarginEnd(dp(ctx, 12));
        badge.setLayoutParams(bp);
        row.addView(badge);

        // Name + quality label
        LinearLayout info = new LinearLayout(ctx);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(ctx);
        tvName.setText(server.name);
        tvName.setTextColor(idx == 1 ? 0xFFFFFFFF : 0xCCFFFFFF);
        tvName.setTextSize(14);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvName);

        TextView tvSub = new TextView(ctx);
        tvSub.setText("HD  •  " + server.tipo.toUpperCase());
        tvSub.setTextColor(idx == 1 ? 0xFF7070A0 : 0xFF404060);
        tvSub.setTextSize(10);
        info.addView(tvSub);
        row.addView(info);

        // Play arrow
        TextView play = new TextView(ctx);
        play.setText("▶");
        play.setTextColor(idx == 1 ? 0xFFFF6B00 : 0xFF404060);
        play.setTextSize(16);
        row.addView(play);

        return row;
    }

    private static void showFallback(Context ctx, View view, ContentItem item) {
        LinearLayout cnt = view.findViewById(R.id.serverContainer);
        if (cnt == null) return;
        cnt.removeAllViews();
        addServerRows(ctx, cnt, item,
            Arrays.asList(new StreamingApi.Server("UnlimPlay", item.getStreamUrl(), "embed")));
    }

    private static int dp(Context ctx, int val) {
        return Math.round(val * ctx.getResources().getDisplayMetrics().density);
    }
}
