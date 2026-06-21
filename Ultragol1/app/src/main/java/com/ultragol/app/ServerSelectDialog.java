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
        final java.util.ArrayList<View> rows = new java.util.ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            View row = buildRow(ctx, list.get(i), i == selectedIdx);
            rows.add(row);
            cnt.addView(row);
        }

        for (int i = 0; i < rows.size(); i++) {
            final int idx = i;
            final String url = list.get(i).url;
            rows.get(i).setOnClickListener(v -> {
                if (idx != sel[0]) {
                    animateDeselect(rows.get(sel[0]));
                    animateSelect(rows.get(idx));
                    sel[0] = idx;
                }
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", item.getTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            });
        }
    }

    /**
     * Row structure (children by index):
     *   0 — spacer (weight=1, pushes content right)
     *   1 — textCol LinearLayout
     *         0: tvName
     *         1: tvStream
     *   2 — radio View
     */
    private static View buildRow(Context ctx, StreamingApi.Server srv, boolean selected) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setFocusable(true);

        // Fixed height for all rows — animation handles the visual "weight"
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 62));
        lp.setMargins(0, dp(ctx, 4), 0, dp(ctx, 4));
        row.setLayoutParams(lp);

        row.setBackgroundResource(selected ? R.drawable.server_row_active : R.drawable.server_row);
        row.setPadding(dp(ctx, 18), dp(ctx, 10), dp(ctx, 18), dp(ctx, 10));

        // Initial scale: selected = full, unselected = slightly shrunk
        if (!selected) {
            row.setScaleX(0.96f);
            row.setScaleY(0.96f);
            row.setAlpha(0.75f);
        }

        // Spacer (child 0)
        View spacer = new View(ctx);
        row.addView(spacer, new LinearLayout.LayoutParams(0, 0, 1f));

        // Text column (child 1)
        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setGravity(android.view.Gravity.END);
        LinearLayout.LayoutParams tcLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tcLp.setMarginEnd(dp(ctx, 16));
        textCol.setLayoutParams(tcLp);

        TextView tvName = new TextView(ctx);
        tvName.setText(srv.name);
        tvName.setGravity(android.view.Gravity.END);
        tvName.setTextColor(selected ? 0xFFFFFFFF : 0x88FFFFFF);
        tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, selected ? 20f : 15f);
        tvName.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
        textCol.addView(tvName);

        TextView tvStream = new TextView(ctx);
        tvStream.setText("STREAM");
        tvStream.setGravity(android.view.Gravity.END);
        tvStream.setTextColor(selected ? 0xCCFFFFFF : 0x44FFFFFF);
        tvStream.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 10f);
        tvStream.setLetterSpacing(0.12f);
        tvStream.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.setMargins(0, dp(ctx, 2), 0, 0);
        tvStream.setLayoutParams(slp);
        textCol.addView(tvStream);

        row.addView(textCol);   // child 1

        // Radio indicator (child 2)
        View radio = new View(ctx);
        int radioSize = dp(ctx, selected ? 22 : 10);
        radio.setLayoutParams(new LinearLayout.LayoutParams(radioSize, radioSize));
        radio.setBackgroundResource(selected ? R.drawable.server_radio_ring : R.drawable.server_radio_dot);
        row.addView(radio);     // child 2

        return row;
    }

    /** Animate a row into the selected state (scale up, full opacity, swap visuals). */
    private static void animateSelect(View row) {
        if (!(row instanceof LinearLayout)) return;
        LinearLayout ll = (LinearLayout) row;

        // Swap background and text immediately
        row.setBackgroundResource(R.drawable.server_row_active);
        if (ll.getChildCount() >= 3) {
            LinearLayout textCol = (LinearLayout) ll.getChildAt(1);
            if (textCol.getChildCount() >= 2) {
                ((TextView) textCol.getChildAt(0)).setTextColor(0xFFFFFFFF);
                ((TextView) textCol.getChildAt(0)).setTextSize(
                    android.util.TypedValue.COMPLEX_UNIT_SP, 20f);
                ((TextView) textCol.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                ((TextView) textCol.getChildAt(1)).setTextColor(0xCCFFFFFF);
            }
            View radio = ll.getChildAt(2);
            int size = dp(row.getContext(), 22);
            android.view.ViewGroup.LayoutParams rLp = radio.getLayoutParams();
            rLp.width = size; rLp.height = size;
            radio.setLayoutParams(rLp);
            radio.setBackgroundResource(R.drawable.server_radio_ring);
        }

        // Spring-like scale + fade in
        row.setScaleX(0.88f);
        row.setScaleY(0.88f);
        row.setAlpha(0.5f);
        row.animate()
            .scaleX(1.04f).scaleY(1.04f).alpha(1f)
            .setDuration(180)
            .setInterpolator(new android.view.animation.DecelerateInterpolator())
            .withEndAction(() ->
                row.animate().scaleX(1f).scaleY(1f)
                    .setDuration(100)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.5f))
                    .start())
            .start();
    }

    /** Animate a row into the deselected state (scale down, dim, swap visuals). */
    private static void animateDeselect(View row) {
        if (!(row instanceof LinearLayout)) return;
        LinearLayout ll = (LinearLayout) row;

        // Swap background and text immediately
        row.setBackgroundResource(R.drawable.server_row);
        if (ll.getChildCount() >= 3) {
            LinearLayout textCol = (LinearLayout) ll.getChildAt(1);
            if (textCol.getChildCount() >= 2) {
                ((TextView) textCol.getChildAt(0)).setTextColor(0x88FFFFFF);
                ((TextView) textCol.getChildAt(0)).setTextSize(
                    android.util.TypedValue.COMPLEX_UNIT_SP, 15f);
                ((TextView) textCol.getChildAt(0)).setTypeface(null, Typeface.NORMAL);
                ((TextView) textCol.getChildAt(1)).setTextColor(0x44FFFFFF);
            }
            View radio = ll.getChildAt(2);
            int size = dp(row.getContext(), 10);
            android.view.ViewGroup.LayoutParams rLp = radio.getLayoutParams();
            rLp.width = size; rLp.height = size;
            radio.setLayoutParams(rLp);
            radio.setBackgroundResource(R.drawable.server_radio_dot);
        }

        // Shrink and dim
        row.animate()
            .scaleX(0.96f).scaleY(0.96f).alpha(0.75f)
            .setDuration(180)
            .setInterpolator(new android.view.animation.DecelerateInterpolator())
            .start();
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
