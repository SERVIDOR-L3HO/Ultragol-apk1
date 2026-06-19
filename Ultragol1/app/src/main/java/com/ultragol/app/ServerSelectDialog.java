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
            TextView tvS = view.findViewById(R.id.tvSeason);
            TextView tvE = view.findViewById(R.id.tvEpisode);
            View bSM = view.findViewById(R.id.btnSeasonMinus), bSP = view.findViewById(R.id.btnSeasonPlus);
            View bEM = view.findViewById(R.id.btnEpMinus),    bEP = view.findViewById(R.id.btnEpPlus);
            View bLoad = view.findViewById(R.id.btnLoadEpisode);
            if (bSM != null) bSM.setOnClickListener(v -> { if (season[0]>1) { season[0]--; if(tvS!=null) tvS.setText(String.valueOf(season[0])); }});
            if (bSP != null) bSP.setOnClickListener(v -> { if (season[0]<30){ season[0]++; if(tvS!=null) tvS.setText(String.valueOf(season[0])); }});
            if (bEM != null) bEM.setOnClickListener(v -> { if (episode[0]>1){ episode[0]--; if(tvE!=null) tvE.setText(String.valueOf(episode[0])); }});
            if (bEP != null) bEP.setOnClickListener(v -> { if (episode[0]<100){episode[0]++; if(tvE!=null) tvE.setText(String.valueOf(episode[0])); }});
            if (bLoad != null) bLoad.setOnClickListener(v -> loadServers(context, view, item, season[0], episode[0]));
        }
        loadServers(context, view, item, season[0], episode[0]);
        sheet.show();
    }

    private static void loadServers(Context ctx, View view, ContentItem item, int season, int episode) {
        LinearLayout cnt = view.findViewById(R.id.serverContainer);
        View loading = view.findViewById(R.id.loadingServers);
        View error   = view.findViewById(R.id.errorServers);
        View tabs    = view.findViewById(R.id.langTabs);
        if (cnt     != null) cnt.removeAllViews();
        if (loading != null) loading.setVisibility(View.VISIBLE);
        if (error   != null) error.setVisibility(View.GONE);
        if (tabs    != null) tabs.setVisibility(View.GONE);

        ExecutorService exec = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        exec.execute(() -> {
            try {
                StreamingApi.ServerData data = item.getContentType() == ContentItem.TYPE_MOVIE
                    ? StreamingApi.fetchMovieServers(item.getTmdbId())
                    : StreamingApi.fetchSeriesServers(item.getTmdbId(), season, episode);
                handler.post(() -> { if (loading != null) loading.setVisibility(View.GONE); render(ctx, view, item, data); });
            } catch (Exception e) {
                handler.post(() -> {
                    if (loading != null) loading.setVisibility(View.GONE);
                    if (error   != null) error.setVisibility(View.VISIBLE);
                    fallback(ctx, view, item);
                });
            }
        });
        exec.shutdown();
    }

    private static void render(Context ctx, View view, ContentItem item, StreamingApi.ServerData data) {
        LinearLayout cnt = view.findViewById(R.id.serverContainer);
        View tabs = view.findViewById(R.id.langTabs);
        if (cnt == null) return;
        cnt.removeAllViews();
        boolean hL = !data.latino.isEmpty(), hE = !data.espanol.isEmpty(), hS = !data.subtitulado.isEmpty();
        int n = (hL?1:0)+(hE?1:0)+(hS?1:0);
        if (n > 1 && tabs != null) {
            tabs.setVisibility(View.VISIBLE);
            setupTabs(ctx, view, item, data, hL, hE, hS);
        } else {
            if (tabs != null) tabs.setVisibility(View.GONE);
            addRows(ctx, cnt, item, hL ? data.latino : hE ? data.espanol : data.subtitulado);
        }
    }

    private static void setupTabs(Context ctx, View view, ContentItem item,
                                  StreamingApi.ServerData data, boolean hL, boolean hE, boolean hS) {
        TextView tL = view.findViewById(R.id.tabLatino);
        TextView tE = view.findViewById(R.id.tabEspanol);
        TextView tS = view.findViewById(R.id.tabSubtitulado);
        LinearLayout cnt = view.findViewById(R.id.serverContainer);
        if (tL != null) tL.setVisibility(hL ? View.VISIBLE : View.GONE);
        if (tE != null) tE.setVisibility(hE ? View.VISIBLE : View.GONE);
        if (tS != null) tS.setVisibility(hS ? View.VISIBLE : View.GONE);
        TextView first = hL ? tL : hE ? tE : tS;
        setActive(first, tL, tE, tS);
        addRows(ctx, cnt, item, hL ? data.latino : hE ? data.espanol : data.subtitulado);
        if (tL != null) tL.setOnClickListener(v -> { setActive(tL,tL,tE,tS); cnt.removeAllViews(); addRows(ctx,cnt,item,data.latino); });
        if (tE != null) tE.setOnClickListener(v -> { setActive(tE,tL,tE,tS); cnt.removeAllViews(); addRows(ctx,cnt,item,data.espanol); });
        if (tS != null) tS.setOnClickListener(v -> { setActive(tS,tL,tE,tS); cnt.removeAllViews(); addRows(ctx,cnt,item,data.subtitulado); });
    }

    private static void setActive(TextView active, TextView... all) {
        for (TextView t : all) { if (t == null) continue;
            boolean a = (t == active);
            t.setBackgroundResource(a ? R.drawable.tab_active : R.drawable.tab_inactive);
            t.setTextColor(a ? 0xFFFFFFFF : 0xFF808090);
        }
    }

    private static void addRows(Context ctx, LinearLayout cnt, ContentItem item, List<StreamingApi.Server> list) {
        if (cnt == null || list == null || list.isEmpty()) return;
        for (int i = 0; i < list.size(); i++) {
            StreamingApi.Server srv = list.get(i);
            View row = buildRow(ctx, srv, i+1);
            final String url = srv.url;
            row.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url", url); intent.putExtra("title", item.getTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            });
            cnt.addView(row);
        }
    }

    private static View buildRow(Context ctx, StreamingApi.Server srv, int idx) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,64));
        lp.setMargins(dp(ctx,16), 0, dp(ctx,16), dp(ctx,8));
        row.setLayoutParams(lp);
        row.setBackgroundResource(idx == 1 ? R.drawable.server_row_active : R.drawable.server_row);
        row.setPadding(dp(ctx,16), 0, dp(ctx,16), 0);
        row.setClickable(true); row.setFocusable(true);

        // number badge
        TextView num = new TextView(ctx);
        num.setText(String.valueOf(idx));
        num.setTextColor(idx==1 ? 0xFFFF6B00 : 0xFF606070);
        num.setTextSize(11); num.setTypeface(null, android.graphics.Typeface.BOLD);
        num.setGravity(android.view.Gravity.CENTER);
        num.setBackgroundResource(idx==1 ? R.drawable.badge_active : R.drawable.badge_inactive);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(dp(ctx,28), dp(ctx,28));
        bp.setMarginEnd(dp(ctx,14)); num.setLayoutParams(bp); row.addView(num);

        // info
        LinearLayout info = new LinearLayout(ctx);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView tvN = new TextView(ctx);
        tvN.setText(srv.name); tvN.setTextColor(idx==1 ? 0xFFFFFFFF : 0xBBFFFFFF);
        tvN.setTextSize(14); tvN.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvN);
        TextView tvQ = new TextView(ctx);
        tvQ.setText("HD  \u2022  " + srv.tipo.toUpperCase());
        tvQ.setTextColor(idx==1 ? 0xFF8080A0 : 0xFF404050); tvQ.setTextSize(10);
        info.addView(tvQ); row.addView(info);

        // play
        TextView play = new TextView(ctx);
        play.setText("\u25B6");
        play.setTextColor(idx==1 ? 0xFFFF6B00 : 0xFF404050); play.setTextSize(18);
        row.addView(play);
        return row;
    }

    private static void fallback(Context ctx, View view, ContentItem item) {
        LinearLayout cnt = view.findViewById(R.id.serverContainer);
        if (cnt == null) return; cnt.removeAllViews();
        addRows(ctx, cnt, item, Arrays.asList(new StreamingApi.Server("UnlimPlay", item.getStreamUrl(), "embed")));
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
