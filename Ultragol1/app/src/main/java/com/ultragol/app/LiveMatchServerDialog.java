package com.ultragol.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import java.util.List;

/**
 * Glass-style bottom-sheet dialog for selecting a live stream server.
 * Shows match info at top and a scrollable server list below.
 */
public class LiveMatchServerDialog {

    public static class LiveMatch {
        public final String titulo;
        public final String liga;
        public final String hora;
        public final String fecha;
        public final String logoUrl;
        public final List<String[]> servidores;   // [0]=canal  [1]=url

        public LiveMatch(String titulo, String liga, String hora,
                         String fecha, String logoUrl, List<String[]> servidores) {
            this.titulo    = titulo;
            this.liga      = liga;
            this.hora      = hora;
            this.fecha     = fecha;
            this.logoUrl   = logoUrl;
            this.servidores = servidores;
        }
    }

    public static void show(Context context, LiveMatch match) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ── Root: dim overlay ──────────────────────────────────────────────────────
        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(0xCC000000);
        root.setClickable(true);
        root.setFocusable(true);
        root.setOnClickListener(v -> dialog.dismiss());

        // ── Glass panel ────────────────────────────────────────────────────────────
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setClickable(true);
        panel.setFocusable(true);
        panel.setOnClickListener(v -> { /* absorb */ });

        GradientDrawable panelBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xF2101018, 0xF2080810});
        panelBg.setCornerRadii(new float[]{dp(context,26),dp(context,26), dp(context,26),dp(context,26), 0,0, 0,0});
        panelBg.setStroke(dp(context,1), 0x33FFFFFF);
        panel.setBackground(panelBg);
        panel.setPadding(dp(context,24), dp(context,10), dp(context,24), dp(context,32));

        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        panelLp.gravity = Gravity.BOTTOM;
        panel.setLayoutParams(panelLp);

        // ── Drag handle ────────────────────────────────────────────────────────────
        View handle = new View(context);
        GradientDrawable handleBg = new GradientDrawable();
        handleBg.setColor(0x44FFFFFF);
        handleBg.setCornerRadius(dp(context,3));
        handle.setBackground(handleBg);
        LinearLayout.LayoutParams handleLp = new LinearLayout.LayoutParams(dp(context,40), dp(context,4));
        handleLp.gravity = Gravity.CENTER_HORIZONTAL;
        handleLp.setMargins(0, dp(context,6), 0, dp(context,20));
        handle.setLayoutParams(handleLp);
        panel.addView(handle);

        // ── Match header ──────────────────────────────────────────────────────────
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams headerLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerLp.setMargins(0, 0, 0, dp(context,20));
        header.setLayoutParams(headerLp);

        // Logo circle
        ImageView logoImg = new ImageView(context);
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setShape(GradientDrawable.OVAL);
        logoBg.setColor(0x22FFFFFF);
        logoBg.setStroke(dp(context,2), 0x44FF6B00);
        logoImg.setBackground(logoBg);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(context,58), dp(context,58));
        logoLp.setMarginEnd(dp(context,16));
        logoImg.setLayoutParams(logoLp);
        logoImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        logoImg.setPadding(dp(context,8), dp(context,8), dp(context,8), dp(context,8));

        if (match.logoUrl != null && !match.logoUrl.isEmpty()) {
            Glide.with(context)
                .load(match.logoUrl)
                .transform(new CircleCrop())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(logoImg);
        }
        header.addView(logoImg);

        // Text block
        LinearLayout textBlock = new LinearLayout(context);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // League badge
        TextView tvLeague = new TextView(context);
        tvLeague.setText("🏆  " + (match.liga != null ? match.liga.toUpperCase() : "EN VIVO"));
        tvLeague.setTextColor(0xFFFF6B00);
        tvLeague.setTextSize(10);
        tvLeague.setTypeface(null, Typeface.BOLD);
        tvLeague.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams leagueLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leagueLp.setMargins(0, 0, 0, dp(context,4));
        tvLeague.setLayoutParams(leagueLp);
        textBlock.addView(tvLeague);

        // Match title
        TextView tvTitle = new TextView(context);
        tvTitle.setText(match.titulo);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(15);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(context,4));
        tvTitle.setLayoutParams(titleLp);
        textBlock.addView(tvTitle);

        // Time row
        StringBuilder metaSb = new StringBuilder();
        if (match.hora  != null && !match.hora.isEmpty())  metaSb.append("🕐 ").append(match.hora);
        if (match.fecha != null && !match.fecha.isEmpty()) {
            if (metaSb.length() > 0) metaSb.append("   ·   ");
            metaSb.append("📅 ").append(match.fecha);
        }
        if (metaSb.length() > 0) {
            TextView tvMeta = new TextView(context);
            tvMeta.setText(metaSb.toString());
            tvMeta.setTextColor(0x88FFFFFF);
            tvMeta.setTextSize(11);
            textBlock.addView(tvMeta);
        }

        header.addView(textBlock);

        // Close button
        TextView closeBtn = new TextView(context);
        closeBtn.setText("✕");
        closeBtn.setTextColor(0x66FFFFFF);
        closeBtn.setTextSize(18);
        closeBtn.setGravity(Gravity.CENTER);
        GradientDrawable closeBg = new GradientDrawable();
        closeBg.setShape(GradientDrawable.OVAL);
        closeBg.setColor(0x0FFFFFFF);
        closeBg.setStroke(dp(context,1), 0x22FFFFFF);
        closeBtn.setBackground(closeBg);
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(dp(context,34), dp(context,34));
        closeLp.setMarginStart(dp(context,12));
        closeLp.gravity = Gravity.TOP;
        closeBtn.setLayoutParams(closeLp);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        header.addView(closeBtn);

        panel.addView(header);

        // ── Divider ───────────────────────────────────────────────────────────────
        View divider = new View(context);
        GradientDrawable divBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.TRANSPARENT, 0x66FF6B00, Color.TRANSPARENT});
        divider.setBackground(divBg);
        LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(context,1));
        divLp.setMargins(0, 0, 0, dp(context,18));
        divider.setLayoutParams(divLp);
        panel.addView(divider);

        // ── Section subtitle ─────────────────────────────────────────────────────
        TextView tvSub = new TextView(context);
        tvSub.setText("SELECCIONA UN SERVIDOR");
        tvSub.setTextColor(0x66FFFFFF);
        tvSub.setTextSize(10);
        tvSub.setTypeface(null, Typeface.BOLD);
        tvSub.setLetterSpacing(0.14f);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, 0, 0, dp(context,12));
        tvSub.setLayoutParams(subLp);
        panel.addView(tvSub);

        // ── Server list ───────────────────────────────────────────────────────────
        ScrollView scroll = new ScrollView(context);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollLp.setMargins(0, 0, 0, 0);
        scroll.setLayoutParams(scrollLp);

        LinearLayout serverList = new LinearLayout(context);
        serverList.setOrientation(LinearLayout.VERTICAL);
        serverList.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        String[] icons = {"📺","📡","🖥️","🌐","⚡","🔴","🟠","💎","🟣","🔵"};
        List<String[]> servers = match.servidores;

        for (int i = 0; i < servers.size(); i++) {
            String[] srv   = servers.get(i);
            String canal   = srv[0];
            String url     = srv[1];
            String icon    = icons[i % icons.length];
            int num        = i + 1;

            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setClickable(true);
            row.setFocusable(true);
            row.setBackgroundResource(R.drawable.live_server_btn_bg);
            row.setPadding(dp(context,14), dp(context,14), dp(context,14), dp(context,14));
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, 0, 0, dp(context,8));
            row.setLayoutParams(rowLp);

            // Number circle
            TextView tvNum = new TextView(context);
            tvNum.setText(icon);
            tvNum.setTextSize(16);
            tvNum.setGravity(Gravity.CENTER);
            tvNum.setBackgroundResource(R.drawable.live_server_number_bg);
            LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(dp(context,42), dp(context,42));
            numLp.setMarginEnd(dp(context,14));
            tvNum.setLayoutParams(numLp);
            row.addView(tvNum);

            // Info column
            LinearLayout info = new LinearLayout(context);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvCanal = new TextView(context);
            tvCanal.setText(canal);
            tvCanal.setTextColor(0xFFFFFFFF);
            tvCanal.setTextSize(14);
            tvCanal.setTypeface(null, Typeface.BOLD);
            tvCanal.setMaxLines(1);
            tvCanal.setEllipsize(android.text.TextUtils.TruncateAt.END);
            info.addView(tvCanal);

            TextView tvLabel = new TextView(context);
            tvLabel.setText("Servidor " + num + " de " + servers.size());
            tvLabel.setTextColor(0x55FFFFFF);
            tvLabel.setTextSize(11);
            LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            labelLp.setMargins(0, dp(context,2), 0, 0);
            tvLabel.setLayoutParams(labelLp);
            info.addView(tvLabel);

            row.addView(info);

            // Arrow
            TextView tvArrow = new TextView(context);
            tvArrow.setText("›");
            tvArrow.setTextColor(0x44FF6B00);
            tvArrow.setTextSize(22);
            tvArrow.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams arrowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            arrowLp.setMarginStart(dp(context,10));
            tvArrow.setLayoutParams(arrowLp);
            row.addView(tvArrow);

            final String finalUrl = url;
            final String finalTitle = match.titulo;
            row.setOnClickListener(v -> {
                row.animate().scaleX(0.95f).scaleY(0.95f).setDuration(60)
                    .withEndAction(() -> row.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
                    .start();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("url", finalUrl);
                    intent.putExtra("title", finalTitle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    dialog.dismiss();
                }, 150);
            });

            serverList.addView(row);
        }

        scroll.addView(serverList);
        panel.addView(scroll);

        root.addView(panel);
        dialog.setContentView(root);

        Window win = dialog.getWindow();
        if (win != null) {
            win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            win.setBackgroundDrawableResource(android.R.color.transparent);
            win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // Slide-in animation
        panel.setTranslationY(600f);
        panel.setAlpha(0f);
        dialog.setOnShowListener(d -> panel.animate()
            .translationY(0f).alpha(1f)
            .setDuration(340)
            .setInterpolator(new android.view.animation.DecelerateInterpolator(2f))
            .start());

        dialog.show();
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
