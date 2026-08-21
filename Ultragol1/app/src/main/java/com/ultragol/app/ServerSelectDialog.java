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
import com.ultragol.app.network.AnimeApi;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ServerSelectDialog {

    public static void show(Context context, ContentItem item) {
        show(context, item, 1, 1);
    }

    /** Show dialog with already-fetched data — skips the network step entirely. */
    public static void showPreloaded(Context context, ContentItem item,
                                     StreamingApi.ServerData data) {
        showPreloaded(context, item, data, 1, 1);
    }

    public static void showPreloaded(Context context, ContentItem item,
                                     StreamingApi.ServerData data, int initSeason, int initEpisode) {
        Dialog dialog = new Dialog(context, R.style.FullScreenServerDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_server_select);

        Window win = dialog.getWindow();
        if (win != null) {
            win.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                          WindowManager.LayoutParams.MATCH_PARENT);
            win.setBackgroundDrawableResource(android.R.color.transparent);
        }

        View dismiss = dialog.findViewById(R.id.dialogDismiss);
        if (dismiss != null) dismiss.setOnClickListener(v -> dialog.dismiss());

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

        // Hide loading indicator — data already available
        View loading = dialog.findViewById(R.id.loadingServers);
        if (loading != null) loading.setVisibility(View.GONE);

        dialog.show();
        TvHelper.prepareDialog(dialog);
        // Render immediately — no network wait
        render(context, dialog, item, data);
    }

    public static void show(Context context, ContentItem item, int initSeason, int initEpisode) {
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

        loadServers(context, dialog, item, initSeason, initEpisode);
        dialog.show();
        TvHelper.prepareDialog(dialog);
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
                StreamingApi.ServerData data = item.isAnime()
                    ? AnimeApi.fetchEpisodeServers(item, season, episode)
                    : item.getContentType() == ContentItem.TYPE_MOVIE
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
        // Always hide language tabs — show only the best available in priority order:
        // Latino → Español → Subtitulado → English (last resort)
        if (tabs != null) tabs.setVisibility(View.GONE);
        List<StreamingApi.Server> best;
        if (!data.latino.isEmpty())           best = data.latino;
        else if (!data.espanol.isEmpty())     best = data.espanol;
        else if (!data.subtitulado.isEmpty()) best = data.subtitulado;
        else                                  best = data.english;
        addRows(ctx, cnt, dialog, item, best, 0);
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
        final int[] sel       = {selectedIdx};
        final int[] dragRow   = {-1};
        final boolean[] dragging = {false};
        final java.util.ArrayList<View> rows = new java.util.ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            View row = buildRow(ctx, list.get(i), i == selectedIdx);
            row.setFocusable(true);
            row.setFocusableInTouchMode(false);
            row.setClickable(true);
            rows.add(row);
            cnt.addView(row);
        }

        for (int i = 0; i < rows.size(); i++) {
            final int rowIdx = i;
            rows.get(i).setOnClickListener(v -> {
                final String url = list.get(rowIdx).url;
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("item", item);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            });
            rows.get(i).setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN
                        && (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == android.view.KeyEvent.KEYCODE_ENTER
                        || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    v.performClick();
                    return true;
                }
                return false;
            });
        }
        TvHelper.prepareDialog(dialog);

        // Each row handles its own touch so events are always received.
        // ACTION_MOVE tracks cross-row drag and highlights whichever row is under the finger.
        for (int i = 0; i < rows.size(); i++) {
            final int rowIdx = i;
            rows.get(i).setOnTouchListener((v, event) -> {
                int action = event.getAction();

                if (action == android.view.MotionEvent.ACTION_DOWN) {
                    dragging[0] = true;
                    dragRow[0]  = rowIdx;
                    // Block parent ScrollView from stealing the gesture
                    ViewParent p = cnt.getParent();
                    if (p != null) p.requestDisallowInterceptTouchEvent(true);
                    highlightHovered(rows, rowIdx);

                } else if (action == android.view.MotionEvent.ACTION_MOVE && dragging[0]) {
                    // Which row is the finger over right now?
                    int[] cntLoc = new int[2];
                    cnt.getLocationOnScreen(cntLoc);
                    int relY = (int) event.getRawY() - cntLoc[1];
                    int hovered = -1;
                    for (int j = 0; j < cnt.getChildCount(); j++) {
                        View child = cnt.getChildAt(j);
                        if (relY >= child.getTop() && relY <= child.getBottom()) {
                            hovered = j;
                            break;
                        }
                    }
                    if (hovered >= 0 && hovered != dragRow[0]) {
                        dragRow[0] = hovered;
                        highlightHovered(rows, hovered);
                    }

                } else if (action == android.view.MotionEvent.ACTION_UP) {
                    dragging[0] = false;
                    int confirmed = dragRow[0];
                    dragRow[0] = -1;
                    ViewParent p = cnt.getParent();
                    if (p != null) p.requestDisallowInterceptTouchEvent(false);

                    if (confirmed >= 0 && confirmed < list.size()) {
                        sel[0] = confirmed;
                        for (int j = 0; j < rows.size(); j++) {
                            if (j == confirmed) animateSelect(rows.get(j));
                            else                animateDeselect(rows.get(j));
                        }
                        final String url = list.get(confirmed).url;
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(ctx, PlayerActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("title", item.getTitle());
                            intent.putExtra("item", item);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                            dialog.dismiss();
                        }, 150);
                    } else {
                        resetToSelection(rows, sel[0]);
                    }

                } else if (action == android.view.MotionEvent.ACTION_CANCEL) {
                    dragging[0] = false;
                    dragRow[0]  = -1;
                    ViewParent p = cnt.getParent();
                    if (p != null) p.requestDisallowInterceptTouchEvent(false);
                    resetToSelection(rows, sel[0]);
                }
                return true;
            });
        }
    }

    private static void highlightHovered(java.util.ArrayList<View> rows, int hovered) {
        for (int i = 0; i < rows.size(); i++) {
            View row = rows.get(i);
            if (i == hovered) {
                row.setBackgroundResource(R.drawable.server_row_active);
                row.animate().scaleX(1.05f).scaleY(1.05f).alpha(1f).setDuration(80).start();
            } else {
                row.setBackgroundResource(R.drawable.server_row);
                row.animate().scaleX(0.94f).scaleY(0.94f).alpha(0.35f).setDuration(80).start();
            }
        }
    }

    private static void resetToSelection(java.util.ArrayList<View> rows, int sel) {
        for (int i = 0; i < rows.size(); i++) {
            boolean selected = (i == sel);
            rows.get(i).setBackgroundResource(
                    selected ? R.drawable.server_row_active : R.drawable.server_row);
            rows.get(i).animate()
                    .scaleX(selected ? 1f : 0.96f)
                    .scaleY(selected ? 1f : 0.96f)
                    .alpha(selected ? 1f : 0.75f)
                    .setDuration(150).start();
        }
    }

    /**
     * Row structure (children by index):
     *   0 — radio View  (left side)
     *   1 — textCol LinearLayout (fills remaining space)
     *         0: tvName
     *         1: tvStream
     */
    private static View buildRow(Context ctx, StreamingApi.Server srv, boolean selected) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 76));
        lp.setMargins(0, dp(ctx, 5), 0, dp(ctx, 5));
        row.setLayoutParams(lp);

        row.setBackgroundResource(selected ? R.drawable.server_row_active : R.drawable.server_row);
        row.setPadding(dp(ctx, 16), dp(ctx, 12), dp(ctx, 16), dp(ctx, 12));

        if (!selected) {
            row.setScaleX(0.96f);
            row.setScaleY(0.96f);
            row.setAlpha(0.75f);
        }

        // Radio indicator (child 0) — LEFT side
        View radio = new View(ctx);
        int radioSize = dp(ctx, selected ? 24 : 12);
        LinearLayout.LayoutParams radioLp = new LinearLayout.LayoutParams(radioSize, radioSize);
        radioLp.setMarginEnd(dp(ctx, 14));
        radio.setLayoutParams(radioLp);
        radio.setBackgroundResource(selected ? R.drawable.server_radio_ring : R.drawable.server_radio_dot);
        row.addView(radio);     // child 0

        // Text column (child 1) — fills space, LEFT aligned
        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(ctx);
        tvName.setText(srv.name);
        tvName.setGravity(android.view.Gravity.START);
        tvName.setTextColor(selected ? 0xFFFFFFFF : 0x99FFFFFF);
        tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, selected ? 18f : 16f);
        tvName.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
        tvName.setMaxLines(1);
        tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textCol.addView(tvName);

        TextView tvStream = new TextView(ctx);
        tvStream.setText(srv.tipo != null && srv.tipo.equals("directo") ? "DIRECTO" : "STREAM");
        tvStream.setGravity(android.view.Gravity.START);
        tvStream.setTextColor(selected ? 0xAAFF6B00 : 0x44FFFFFF);
        tvStream.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 11f);
        tvStream.setLetterSpacing(0.12f);
        tvStream.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.setMargins(0, dp(ctx, 1), 0, 0);
        tvStream.setLayoutParams(slp);
        textCol.addView(tvStream);

        row.addView(textCol);   // child 1

        return row;
    }

    /** Animate a row into the selected state. Row: child0=radio, child1=textCol */
    private static void animateSelect(View row) {
        if (!(row instanceof LinearLayout)) return;
        LinearLayout ll = (LinearLayout) row;

        row.setBackgroundResource(R.drawable.server_row_active);
        if (ll.getChildCount() >= 2) {
            View radio = ll.getChildAt(0);
            int size = dp(row.getContext(), 24);
            android.view.ViewGroup.LayoutParams rLp = radio.getLayoutParams();
            rLp.width = size; rLp.height = size;
            radio.setLayoutParams(rLp);
            radio.setBackgroundResource(R.drawable.server_radio_ring);

            LinearLayout textCol = (LinearLayout) ll.getChildAt(1);
            if (textCol.getChildCount() >= 2) {
                ((TextView) textCol.getChildAt(0)).setTextColor(0xFFFFFFFF);
                ((TextView) textCol.getChildAt(0)).setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18f);
                ((TextView) textCol.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                ((TextView) textCol.getChildAt(1)).setTextColor(0xAAFF6B00);
            }
        }

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

    /** Animate a row into the deselected state. Row: child0=radio, child1=textCol */
    private static void animateDeselect(View row) {
        if (!(row instanceof LinearLayout)) return;
        LinearLayout ll = (LinearLayout) row;

        row.setBackgroundResource(R.drawable.server_row);
        if (ll.getChildCount() >= 2) {
            View radio = ll.getChildAt(0);
            int size = dp(row.getContext(), 12);
            android.view.ViewGroup.LayoutParams rLp = radio.getLayoutParams();
            rLp.width = size; rLp.height = size;
            radio.setLayoutParams(rLp);
            radio.setBackgroundResource(R.drawable.server_radio_dot);

            LinearLayout textCol = (LinearLayout) ll.getChildAt(1);
            if (textCol.getChildCount() >= 2) {
                ((TextView) textCol.getChildAt(0)).setTextColor(0x99FFFFFF);
                ((TextView) textCol.getChildAt(0)).setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f);
                ((TextView) textCol.getChildAt(0)).setTypeface(null, Typeface.NORMAL);
                ((TextView) textCol.getChildAt(1)).setTextColor(0x44FFFFFF);
            }
        }

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
