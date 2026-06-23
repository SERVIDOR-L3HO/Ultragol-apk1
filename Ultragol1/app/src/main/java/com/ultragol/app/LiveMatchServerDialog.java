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
 * Premium glass bottom-sheet dialog for live match server selection.
 */
public class LiveMatchServerDialog {

    // ── Data model ────────────────────────────────────────────────────────────

    public static class LiveMatch {
        public final String titulo;
        public final String liga;
        public final String hora;
        public final String fecha;
        public final String logoUrl;
        public final List<String[]> servidores;   // [0]=canal  [1]=url

        public LiveMatch(String titulo, String liga, String hora,
                         String fecha, String logoUrl, List<String[]> servidores) {
            this.titulo     = titulo;
            this.liga       = liga;
            this.hora       = hora;
            this.fecha      = fecha;
            this.logoUrl    = logoUrl;
            this.servidores = servidores;
        }
    }

    // ── Server icon color palette ─────────────────────────────────────────────
    private static final int[] ICON_COLORS = {
        0xFFFF6B00, 0xFFE53935, 0xFF8E24AA, 0xFF1E88E5,
        0xFF43A047, 0xFFF4511E, 0xFF039BE5, 0xFF6D4C41,
        0xFF00ACC1, 0xFF7CB342, 0xFFFFB300, 0xFF5E35B1
    };

    // ── Icon labels for each slot ─────────────────────────────────────────────
    private static final String[] ICON_LABELS = {
        "1","2","3","4","5","6","7","8","9","10","11","12",
        "13","14","15","16","17","18","19","20"
    };

    // ── Show ─────────────────────────────────────────────────────────────────

    public static void show(Context ctx, LiveMatch match) {
        Dialog dialog = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ── Dim overlay ───────────────────────────────────────────────────────
        FrameLayout root = new FrameLayout(ctx);
        root.setBackgroundColor(0xCC000000);
        root.setClickable(true);
        root.setFocusable(true);
        root.setOnClickListener(v -> dialog.dismiss());

        // ── Glass panel ───────────────────────────────────────────────────────
        LinearLayout panel = new LinearLayout(ctx);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setClickable(true);
        panel.setFocusable(true);
        panel.setOnClickListener(v -> {/* absorb clicks */});

        GradientDrawable panelBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xF50A0A18, 0xF5060610});
        panelBg.setCornerRadii(new float[]{
            dp(ctx,28),dp(ctx,28), dp(ctx,28),dp(ctx,28), 0,0, 0,0});
        panelBg.setStroke(dp(ctx,1), 0x25FFFFFF);
        panel.setBackground(panelBg);
        panel.setPadding(dp(ctx,20), dp(ctx,8), dp(ctx,20), dp(ctx,36));

        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        panelLp.gravity = Gravity.BOTTOM;
        panel.setLayoutParams(panelLp);

        // ── Drag handle ───────────────────────────────────────────────────────
        View handle = new View(ctx);
        GradientDrawable hBg = new GradientDrawable();
        hBg.setColor(0x44FFFFFF);
        hBg.setCornerRadius(dp(ctx,4));
        handle.setBackground(hBg);
        LinearLayout.LayoutParams hLp = new LinearLayout.LayoutParams(dp(ctx,44), dp(ctx,4));
        hLp.gravity = Gravity.CENTER_HORIZONTAL;
        hLp.setMargins(0, dp(ctx,4), 0, dp(ctx,22));
        handle.setLayoutParams(hLp);
        panel.addView(handle);

        // ── Header ────────────────────────────────────────────────────────────
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams headerLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerLp.setMargins(0, 0, 0, dp(ctx,20));
        header.setLayoutParams(headerLp);

        // Logo circle (premium ring)
        ImageView logo = new ImageView(ctx);
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setShape(GradientDrawable.OVAL);
        logoBg.setColor(0x22FFFFFF);
        logoBg.setStroke(dp(ctx,2), 0x66FF6B00);
        logo.setBackground(logoBg);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(ctx,64), dp(ctx,64));
        logoLp.setMarginEnd(dp(ctx,16));
        logo.setLayoutParams(logoLp);
        logo.setPadding(dp(ctx,10), dp(ctx,10), dp(ctx,10), dp(ctx,10));
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if (match.logoUrl != null && !match.logoUrl.isEmpty()) {
            Glide.with(ctx).load(match.logoUrl)
                .transform(new CircleCrop())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(logo);
        }
        header.addView(logo);

        // Text block
        LinearLayout textBlock = new LinearLayout(ctx);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // League pill
        if (match.liga != null && !match.liga.isEmpty()) {
            TextView tvLig = new TextView(ctx);
            tvLig.setText("🏆  " + match.liga.toUpperCase());
            tvLig.setTextColor(0xFFFF6B00);
            tvLig.setTextSize(9);
            tvLig.setTypeface(null, Typeface.BOLD);
            tvLig.setLetterSpacing(0.1f);
            GradientDrawable ligBg = new GradientDrawable();
            ligBg.setColor(0x1AFF6B00);
            ligBg.setStroke(dp(ctx,1), 0x33FF6B00);
            ligBg.setCornerRadius(dp(ctx,50));
            tvLig.setBackground(ligBg);
            tvLig.setPadding(dp(ctx,8), dp(ctx,3), dp(ctx,8), dp(ctx,3));
            LinearLayout.LayoutParams lLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lLp.setMargins(0, 0, 0, dp(ctx,6));
            tvLig.setLayoutParams(lLp);
            textBlock.addView(tvLig);
        }

        // Title
        TextView tvTitle = new TextView(ctx);
        tvTitle.setText(match.titulo);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvTitle.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(ctx,5));
        tvTitle.setLayoutParams(titleLp);
        textBlock.addView(tvTitle);

        // Time row
        StringBuilder meta = new StringBuilder();
        if (match.hora != null && !match.hora.isEmpty())  meta.append("🕐  ").append(match.hora);
        if (match.fecha != null && !match.fecha.isEmpty()) {
            if (meta.length() > 0) meta.append("   ·   ");
            meta.append("📅  ").append(match.fecha);
        }
        if (meta.length() > 0) {
            TextView tvMeta = new TextView(ctx);
            tvMeta.setText(meta.toString());
            tvMeta.setTextColor(0x66FFFFFF);
            tvMeta.setTextSize(11);
            textBlock.addView(tvMeta);
        }

        header.addView(textBlock);

        // Close button
        TextView closeBtn = new TextView(ctx);
        closeBtn.setText("✕");
        closeBtn.setTextColor(0x77FFFFFF);
        closeBtn.setTextSize(16);
        closeBtn.setGravity(Gravity.CENTER);
        GradientDrawable closeBg = new GradientDrawable();
        closeBg.setShape(GradientDrawable.OVAL);
        closeBg.setColor(0x14FFFFFF);
        closeBg.setStroke(dp(ctx,1), 0x22FFFFFF);
        closeBtn.setBackground(closeBg);
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(dp(ctx,36), dp(ctx,36));
        closeLp.setMarginStart(dp(ctx,12));
        closeLp.gravity = Gravity.TOP;
        closeBtn.setLayoutParams(closeLp);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        header.addView(closeBtn);
        panel.addView(header);

        // ── Orange gradient divider ───────────────────────────────────────────
        View divider = new View(ctx);
        GradientDrawable divBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.TRANSPARENT, 0x88FF6B00, Color.TRANSPARENT});
        divider.setBackground(divBg);
        LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,1));
        divLp.setMargins(0, 0, 0, dp(ctx,18));
        divider.setLayoutParams(divLp);
        panel.addView(divider);

        // ── Subtitle ─────────────────────────────────────────────────────────
        TextView tvSub = new TextView(ctx);
        tvSub.setText("SELECCIONA UN SERVIDOR");
        tvSub.setTextColor(0x55FFFFFF);
        tvSub.setTextSize(10);
        tvSub.setTypeface(null, Typeface.BOLD);
        tvSub.setLetterSpacing(0.14f);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, 0, 0, dp(ctx,14));
        tvSub.setLayoutParams(subLp);
        panel.addView(tvSub);

        // ── Server scroll list ────────────────────────────────────────────────
        ScrollView scroll = new ScrollView(ctx);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scroll.setLayoutParams(scrollLp);

        LinearLayout list = new LinearLayout(ctx);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < match.servidores.size(); i++) {
            String[] srv   = match.servidores.get(i);
            String canal   = srv[0];
            String url     = srv[1];
            int iconColor  = ICON_COLORS[i % ICON_COLORS.length];
            String numLabel = ICON_LABELS[Math.min(i, ICON_LABELS.length - 1)];

            // Row container
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setClickable(true);
            row.setFocusable(true);
            row.setBackgroundResource(R.drawable.live_server_row_premium_bg);
            row.setPadding(dp(ctx,14), dp(ctx,14), dp(ctx,14), dp(ctx,14));
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, 0, 0, dp(ctx,8));
            row.setLayoutParams(rowLp);

            // Colored number circle
            TextView numView = new TextView(ctx);
            numView.setText(numLabel);
            numView.setTextColor(0xFFFFFFFF);
            numView.setTextSize(14);
            numView.setTypeface(null, Typeface.BOLD);
            numView.setGravity(Gravity.CENTER);
            GradientDrawable numBg = new GradientDrawable();
            numBg.setShape(GradientDrawable.OVAL);
            numBg.setColor((iconColor & 0x00FFFFFF) | 0x33000000);  // 20% tint
            numBg.setStroke(dp(ctx,2), (iconColor & 0x00FFFFFF) | 0x88000000);
            // Reconstruct properly: low opacity fill, medium opacity border
            numBg.setColor(Color.argb(40,
                Color.red(iconColor), Color.green(iconColor), Color.blue(iconColor)));
            numBg.setStroke(dp(ctx,2), Color.argb(140,
                Color.red(iconColor), Color.green(iconColor), Color.blue(iconColor)));
            numView.setBackground(numBg);
            numView.setTextColor(iconColor);
            LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(
                dp(ctx,44), dp(ctx,44));
            numLp.setMarginEnd(dp(ctx,14));
            numView.setLayoutParams(numLp);
            row.addView(numView);

            // Info column
            LinearLayout info = new LinearLayout(ctx);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvCanal = new TextView(ctx);
            tvCanal.setText(canal);
            tvCanal.setTextColor(0xFFFFFFFF);
            tvCanal.setTextSize(14);
            tvCanal.setTypeface(null, Typeface.BOLD);
            tvCanal.setMaxLines(1);
            tvCanal.setEllipsize(android.text.TextUtils.TruncateAt.END);
            info.addView(tvCanal);

            TextView tvSrvLabel = new TextView(ctx);
            tvSrvLabel.setText("Servidor " + (i + 1) + " de " + match.servidores.size());
            tvSrvLabel.setTextColor(0x44FFFFFF);
            tvSrvLabel.setTextSize(11);
            LinearLayout.LayoutParams lblLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lblLp.setMargins(0, dp(ctx,2), 0, 0);
            tvSrvLabel.setLayoutParams(lblLp);
            info.addView(tvSrvLabel);
            row.addView(info);

            // Arrow
            TextView arrow = new TextView(ctx);
            arrow.setText("›");
            arrow.setTextSize(24);
            arrow.setTextColor(Color.argb(100,
                Color.red(iconColor), Color.green(iconColor), Color.blue(iconColor)));
            arrow.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams arrLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            arrLp.setMarginStart(dp(ctx,10));
            arrow.setLayoutParams(arrLp);
            row.addView(arrow);

            final String fUrl   = url;
            final String fTitle = match.titulo;
            row.setOnClickListener(v -> {
                row.animate().scaleX(0.96f).scaleY(0.96f).setDuration(60)
                    .withEndAction(() ->
                        row.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
                    .start();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(ctx, PlayerActivity.class);
                    intent.putExtra("url",   fUrl);
                    intent.putExtra("title", fTitle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                    dialog.dismiss();
                }, 160);
            });

            list.addView(row);
        }

        scroll.addView(list);
        panel.addView(scroll);
        root.addView(panel);
        dialog.setContentView(root);

        Window win = dialog.getWindow();
        if (win != null) {
            win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                          ViewGroup.LayoutParams.MATCH_PARENT);
            win.setBackgroundDrawableResource(android.R.color.transparent);
            win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // Slide-up animation
        panel.setTranslationY(700f);
        panel.setAlpha(0f);
        dialog.setOnShowListener(d ->
            panel.animate()
                .translationY(0f).alpha(1f)
                .setDuration(360)
                .setInterpolator(new android.view.animation.DecelerateInterpolator(2.2f))
                .start());

        dialog.show();
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
