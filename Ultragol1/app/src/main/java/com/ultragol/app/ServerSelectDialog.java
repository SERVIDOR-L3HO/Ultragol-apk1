package com.ultragol.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ultragol.app.models.ContentItem;
import com.ultragol.app.network.StreamingApi;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ServerSelectDialog {

    public static void show(Context context, ContentItem item) {
        Dialog dialog = new Dialog(context, R.style.FullScreenServerDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_server_select);

        Window win = dialog.getWindow();
        if (win != null) {
            win.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                          WindowManager.LayoutParams.MATCH_PARENT);
            win.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Dismiss on background tap
        View dismiss = dialog.findViewById(R.id.dialogDismiss);
        if (dismiss != null) dismiss.setOnClickListener(v -> dialog.dismiss());

        // Content info
        TextView tvTitle = dialog.findViewById(R.id.serverDialogTitle);
        if (tvTitle != null) tvTitle.setText(item.getTitle());

        TextView tvBadge = dialog.findViewById(R.id.dialogBadge);
        if (tvBadge != null) {
            switch (item.getContentType()) {
                case ContentItem.TYPE_SERIES: tvBadge.setText("SERIE"); break;
                case ContentItem.TYPE_ANIME:  tvBadge.setText("ANIME"); break;
                case ContentItem.TYPE_DORAMA: tvBadge.setText("DORAMA"); break;
                default:                      tvBadge.setText("FILM");  break;
            }
        }

        ImageView poster = dialog.findViewById(R.id.dialogPoster);
        if (poster != null && !item.getPosterUrl().isEmpty()) {
            Glide.with(context).load(item.getPosterUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(poster);
        }

        // Episode section (series only)
        boolean isTV = item.getContentType() != ContentItem.TYPE_MOVIE;
        View episodeSection = dialog.findViewById(R.id.episodeSection);
        if (episodeSection != null) episodeSection.setVisibility(isTV ? View.VISIBLE : View.GONE);

        final int[] season  = {1};
        final int[] episode = {1};

        if (isTV) {
            TextView tvS = dialog.findViewById(R.id.tvSeason);
            TextView tvE = dialog.findViewById(R.id.tvEpisode);
            View bSM = dialog.findViewById(R.id.btnSeasonMinus);
            View bSP = dialog.findViewById(R.id.btnSeasonPlus);
            View bEM = dialog.findViewById(R.id.btnEpMinus);
            View bEP = dialog.findViewById(R.id.btnEpPlus);
            View bLoad = dialog.findViewById(R.id.btnLoadEpisode);
            if (bSM != null) bSM.setOnClickListener(v -> { if (season[0]>1) { season[0]--; if(tvS!=null) tvS.setText(String.valueOf(season[0])); }});
            if (bSP != null) bSP.setOnClickListener(v -> { if (season[0]<30){ season[0]++; if(tvS!=null) tvS.setText(String.valueOf(season[0])); }});
            if (bEM != null) bEM.setOnClickListener(v -> { if (episode[0]>1){ episode[0]--; if(tvE!=null) tvE.setText(String.valueOf(episode[0])); }});
            if (bEP != null) bEP.setOnClickListener(v -> { if (episode[0]<200){episode[0]++; if(tvE!=null) tvE.setText(String.valueOf(episode[0])); }});
            if (bLoad != null) bLoad.setOnClickListener(v -> loadServers(context, dialog, item, season[0], episode[0]));
        }

        loadServers(context, dialog, item, season[0], episode[0]);
        dialog.show();
    }

    private static void loadServers(Context ctx, Dialog dialog, ContentItem item, int season, int episode) {
        LinearLayout cnt = dialog.findViewById(R.id.serverContainer);
        View loading = dialog.findViewById(R.id.loadingServers);
        View error   = dialog.findViewById(R.id.errorServers);
        View tabs    = dialog.findViewById(R.id.langTabs);
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
                handler.post(() -> {
                    if (!dialog.isShowing()) return;
                    if (loading != null) loading.setVisibility(View.GONE);
                    render(ctx, dialog, item, data);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    if (!dialog.isShowing()) return;
                    if (loading != null) loading.setVisibility(View.GONE);
                    if (error   != null) error.setVisibility(View.VISIBLE);
                    fallback(ctx, dialog, item);
                });
            }
        });
        exec.shutdown();
    }

    private static void render(Context ctx, Dialog dialog, ContentItem item, StreamingApi.ServerData data) {
        LinearLayout cnt = dialog.findViewById(R.id.serverContainer);
        View tabs = dialog.findViewById(R.id.langTabs);
        if (cnt == null) return;
        cnt.removeAllViews();
        boolean hL = !data.latino.isEmpty(), hE = !data.espanol.isEmpty(), hS = !data.subtitulado.isEmpty();
        int n = (hL?1:0)+(hE?1:0)+(hS?1:0);
        if (n > 1 && tabs != null) {
            tabs.setVisibility(View.VISIBLE);
            setupTabs(ctx, dialog, item, data, hL, hE, hS);
        } else {
            if (tabs != null) tabs.setVisibility(View.GONE);
            addRows(ctx, cnt, dialog, item, hL ? data.latino : hE ? data.espanol : data.subtitulado, 0);
        }
    }

    private static void setupTabs(Context ctx, Dialog dialog, ContentItem item,
                                  StreamingApi.ServerData data, boolean hL, boolean hE, boolean hS) {
        TextView tL = dialog.findViewById(R.id.tabLatino);
        TextView tE = dialog.findViewById(R.id.tabEspanol);
        TextView tS = dialog.findViewById(R.id.tabSubtitulado);
        LinearLayout cnt = dialog.findViewById(R.id.serverContainer);
        if (tL != null) tL.setVisibility(hL ? View.VISIBLE : View.GONE);
        if (tE != null) tE.setVisibility(hE ? View.VISIBLE : View.GONE);
        if (tS != null) tS.setVisibility(hS ? View.VISIBLE : View.GONE);
        setActive(hL ? tL : hE ? tE : tS, tL, tE, tS);
        addRows(ctx, cnt, dialog, item, hL ? data.latino : hE ? data.espanol : data.subtitulado, 0);
        if (tL != null) tL.setOnClickListener(v -> { setActive(tL,tL,tE,tS); cnt.removeAllViews(); addRows(ctx,cnt,dialog,item,data.latino,0); });
        if (tE != null) tE.setOnClickListener(v -> { setActive(tE,tL,tE,tS); cnt.removeAllViews(); addRows(ctx,cnt,dialog,item,data.espanol,0); });
        if (tS != null) tS.setOnClickListener(v -> { setActive(tS,tL,tE,tS); cnt.removeAllViews(); addRows(ctx,cnt,dialog,item,data.subtitulado,0); });
    }

    private static void setActive(TextView active, TextView... all) {
        for (TextView t : all) { if (t == null) continue;
            boolean a = (t == active);
            t.setBackgroundResource(a ? R.drawable.tab_active : R.drawable.tab_inactive);
            t.setTextColor(a ? 0xFFFFFFFF : 0xFF808090);
        }
    }

    private static void addRows(Context ctx, LinearLayout cnt, Dialog dialog,
                                ContentItem item, List<StreamingApi.Server> list, int selectedIdx) {
        if (cnt == null || list == null || list.isEmpty()) return;
        final int[] sel = {selectedIdx};
        for (int i = 0; i < list.size(); i++) {
            final int idx = i;
            StreamingApi.Server srv = list.get(i);
            View row = buildRow(ctx, srv, i, i == selectedIdx);
            final String url = srv.url;
            row.setOnClickListener(v -> {
                sel[0] = idx;
                // rebuild with new selection
                cnt.removeAllViews();
                for (int j = 0; j < list.size(); j++) {
                    final int jj = j;
                    View r = buildRow(ctx, list.get(j), j, j == idx);
                    r.setOnClickListener(vv -> {
                        Intent intent = new Intent(ctx, PlayerActivity.class);
                        intent.putExtra("url", list.get(jj).url);
                        intent.putExtra("title", item.getTitle());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                        dialog.dismiss();
                    });
                    cnt.addView(r);
                }
                // Navigate immediately when not already selected
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", item.getTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            });
            cnt.addView(row);
        }
    }

    /** Builds a single server row: name right-aligned + "STREAM" + radio circle */
    private static View buildRow(Context ctx, StreamingApi.Server srv, int idx, boolean selected) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.END);
        row.setClickable(true);
        row.setFocusable(true);

        // sizing
        int rowH = selected ? dp(ctx, 68) : dp(ctx, 56);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, rowH);
        lp.setMargins(0, 0, 0, dp(ctx, 4));
        row.setLayoutParams(lp);
        row.setBackground(null);

        // text section (name + STREAM), right-aligned
        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setGravity(android.view.Gravity.END);
        LinearLayout.LayoutParams tcLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tcLp.setMarginEnd(dp(ctx, 14));
        textCol.setLayoutParams(tcLp);

        // Server name
        TextView tvName = new TextView(ctx);
        tvName.setText(srv.name);
        tvName.setGravity(android.view.Gravity.END);
        if (selected) {
            tvName.setTextColor(0xFFFFFFFF);
            tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 19f);
            tvName.setTypeface(null, Typeface.BOLD);
        } else {
            tvName.setTextColor(0x88FFFFFF);
            tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f);
            tvName.setTypeface(null, Typeface.NORMAL);
        }
        textCol.addView(tvName);

        // "STREAM" label
        TextView tvStream = new TextView(ctx);
        tvStream.setText("STREAM");
        tvStream.setGravity(android.view.Gravity.END);
        tvStream.setTextColor(selected ? 0x99FFFFFF : 0x44FFFFFF);
        tvStream.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 10f);
        tvStream.setLetterSpacing(0.12f);
        tvStream.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.setMargins(0, dp(ctx, 2), 0, 0);
        tvStream.setLayoutParams(slp);
        textCol.addView(tvStream);

        row.addView(textCol);

        // Radio indicator
        View radio = new View(ctx);
        int radioSize = selected ? dp(ctx, 18) : dp(ctx, 8);
        LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(radioSize, radioSize);
        radio.setLayoutParams(rLp);
        radio.setBackgroundResource(selected ? R.drawable.server_radio_ring : R.drawable.server_radio_dot);
        row.addView(radio);

        return row;
    }

    private static void fallback(Context ctx, Dialog dialog, ContentItem item) {
        LinearLayout cnt = dialog.findViewById(R.id.serverContainer);
        if (cnt == null) return;
        cnt.removeAllViews();
        addRows(ctx, cnt, dialog, item,
            Arrays.asList(new StreamingApi.Server("UnlimPlay", item.getStreamUrl(), "embed")), 0);
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
