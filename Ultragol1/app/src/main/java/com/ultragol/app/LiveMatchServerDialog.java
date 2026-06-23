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
 * Premium deep-glass bottom-sheet dialog for live match server selection.
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

    // ── Server icon color palette (premium gradient pairs) ────────────────────
    private static final int[] ICON_COLORS = {
        0xFFFF6B00, 0xFFE53935, 0xFF8E24AA, 0xFF1E88E5,
        0xFF43A047, 0xFFF4511E, 0xFF039BE5, 0xFF6D4C41,
        0xFF00ACC1, 0xFF7CB342, 0xFFFFB300, 0xFF5E35B1
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
        panel.setOnClickListener(v -> { /* absorb */ });

        GradientDrawable panelBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xF50C0C20, 0xF8070712, 0xFA040410});
        panelBg.setCornerRadii(new float[]{
            dp(ctx,30), dp(ctx,30), dp(ctx,30), dp(ctx,30), 0, 0, 0, 0});
        panelBg.setStroke(dp(ctx,1), 0x35FFFFFF);
        panel.setBackground(panelBg);
        panel.setPadding(dp(ctx,20), dp(ctx,10), dp(ctx,20), dp(ctx,40));

        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        panelLp.gravity = Gravity.BOTTOM;
        panel.setLayoutParams(panelLp);

        // ── Drag handle ───────────────────────────────────────────────────────
        View handle = new View(ctx);
        GradientDrawable hBg = new GradientDrawable();
        hBg.setColor(0x55FFFFFF);
        hBg.setCornerRadius(dp(ctx,4));
        handle.setBackground(hBg);
        LinearLayout.LayoutParams hLp = new LinearLayout.LayoutParams(dp(ctx,40), dp(ctx,4));
        hLp.gravity = Gravity.CENTER_HORIZONTAL;
        hLp.setMargins(0, dp(ctx,6), 0, dp(ctx,24));
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

        // ── Logo with double-ring glow ─────────────────────────────────────
        FrameLayout logoFrame = new FrameLayout(ctx);
        LinearLayout.LayoutParams logoFrameLp = new LinearLayout.LayoutParams(
            dp(ctx,76), dp(ctx,76));
        logoFrameLp.setMarginEnd(dp(ctx,16));
        logoFrame.setLayoutParams(logoFrameLp);

        // Outer glow ring
        View outerRing = new View(ctx);
        GradientDrawable outerRingBg = new GradientDrawable();
        outerRingBg.setShape(GradientDrawable.OVAL);
        outerRingBg.setColor(0x00000000);
        outerRingBg.setStroke(dp(ctx,1), 0x44FF6B00);
        outerRing.setBackground(outerRingBg);
        FrameLayout.LayoutParams outerRingLp = new FrameLayout.LayoutParams(
            dp(ctx,76), dp(ctx,76));
        outerRingLp.gravity = Gravity.CENTER;
        outerRing.setLayoutParams(outerRingLp);
        logoFrame.addView(outerRing);

        // Inner logo circle
        ImageView logo = new ImageView(ctx);
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setShape(GradientDrawable.OVAL);
        logoBg.setColor(0x28FFFFFF);
        logoBg.setStroke(dp(ctx,2), 0x99FF6B00);
        logo.setBackground(logoBg);
        FrameLayout.LayoutParams logoLp = new FrameLayout.LayoutParams(dp(ctx,60), dp(ctx,60));
        logoLp.gravity = Gravity.CENTER;
        logo.setLayoutParams(logoLp);
        logo.setPadding(dp(ctx,10), dp(ctx,10), dp(ctx,10), dp(ctx,10));
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (match.logoUrl != null && !match.logoUrl.isEmpty()) {
            Glide.with(ctx).load(match.logoUrl)
                .transform(new CircleCrop())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(logo);
        }
        logoFrame.addView(logo);
        header.addView(logoFrame);

        // Text block
        LinearLayout textBlock = new LinearLayout(ctx);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // League badge
        if (match.liga != null && !match.liga.isEmpty()) {
            TextView tvLig = new TextView(ctx);
            tvLig.setText("🏆  " + match.liga.toUpperCase());
            tvLig.setTextColor(0xFFFF6B00);
            tvLig.setTextSize(9);
            tvLig.setTypeface(null, Typeface.BOLD);
            tvLig.setLetterSpacing(0.10f);
            GradientDrawable ligBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0x33FF6B00, 0x1AFF6B00});
            ligBg.setStroke(dp(ctx,1), 0x55FF6B00);
            ligBg.setCornerRadius(dp(ctx,50));
            tvLig.setBackground(ligBg);
            tvLig.setPadding(dp(ctx,10), dp(ctx,4), dp(ctx,10), dp(ctx,4));
            LinearLayout.LayoutParams lLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lLp.setMargins(0, 0, 0, dp(ctx,7));
            tvLig.setLayoutParams(lLp);
            textBlock.addView(tvLig);
        }

        // Title
        TextView tvTitle = new TextView(ctx);
        tvTitle.setText(match.titulo);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(17);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvTitle.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.setMargins(0, 0, 0, dp(ctx,5));
        tvTitle.setLayoutParams(titleLp);
        textBlock.addView(tvTitle);

        // Meta row (time · date)
        StringBuilder meta = new StringBuilder();
        if (match.hora != null && !match.hora.isEmpty())   meta.append("🕐  ").append(match.hora);
        if (match.fecha != null && !match.fecha.isEmpty()) {
            if (meta.length() > 0) meta.append("   ·   ");
            meta.append("📅  ").append(match.fecha);
        }
        if (meta.length() > 0) {
            TextView tvMeta = new TextView(ctx);
            tvMeta.setText(meta.toString());
            tvMeta.setTextColor(0x77FFFFFF);
            tvMeta.setTextSize(11);
            textBlock.addView(tvMeta);
        }
        header.addView(textBlock);

        // Close button
        TextView closeBtn = new TextView(ctx);
        closeBtn.setText("✕");
        closeBtn.setTextColor(0x99FFFFFF);
        closeBtn.setTextSize(14);
        closeBtn.setGravity(Gravity.CENTER);
        GradientDrawable closeBg = new GradientDrawable();
        closeBg.setShape(GradientDrawable.OVAL);
        closeBg.setColor(0x18FFFFFF);
        closeBg.setStroke(dp(ctx,1), 0x30FFFFFF);
        closeBtn.setBackground(closeBg);
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(dp(ctx,36), dp(ctx,36));
        closeLp.setMarginStart(dp(ctx,12));
        closeLp.gravity = Gravity.TOP;
        closeBtn.setLayoutParams(closeLp);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        header.addView(closeBtn);
        panel.addView(header);

        // ── Gradient divider ──────────────────────────────────────────────────
        View divider = new View(ctx);
        GradientDrawable divBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.TRANSPARENT, 0xAAFF6B00, 0x44FF9500, Color.TRANSPARENT});
        divider.setBackground(divBg);
        LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,1));
        divLp.setMargins(0, 0, 0, dp(ctx,18));
        divider.setLayoutParams(divLp);
        panel.addView(divider);

        // ── "SELECCIONA UN SERVIDOR" subtitle ─────────────────────────────────
        LinearLayout subRow = new LinearLayout(ctx);
        subRow.setOrientation(LinearLayout.HORIZONTAL);
        subRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams subRowLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subRowLp.setMargins(0, 0, 0, dp(ctx,14));
        subRow.setLayoutParams(subRowLp);

        TextView tvSub = new TextView(ctx);
        tvSub.setText("SELECCIONA UN SERVIDOR");
        tvSub.setTextColor(0x66FFFFFF);
        tvSub.setTextSize(10);
        tvSub.setTypeface(null, Typeface.BOLD);
        tvSub.setLetterSpacing(0.14f);
        tvSub.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        subRow.addView(tvSub);

        // Server count pill
        if (!match.servidores.isEmpty()) {
            TextView tvCount = new TextView(ctx);
            tvCount.setText(match.servidores.size() + " canales");
            tvCount.setTextColor(0xFFFF6B00);
            tvCount.setTextSize(10);
            tvCount.setTypeface(null, Typeface.BOLD);
            GradientDrawable countBg = new GradientDrawable();
            countBg.setColor(0x22FF6B00);
            countBg.setStroke(dp(ctx,1), 0x55FF6B00);
            countBg.setCornerRadius(dp(ctx,50));
            tvCount.setBackground(countBg);
            tvCount.setPadding(dp(ctx,10), dp(ctx,4), dp(ctx,10), dp(ctx,4));
            subRow.addView(tvCount);
        }
        panel.addView(subRow);

        // ── Server scroll list ────────────────────────────────────────────────
        ScrollView scroll = new ScrollView(ctx);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollLp.setMargins(0, 0, 0, 0);
        scroll.setLayoutParams(scrollLp);

        LinearLayout list = new LinearLayout(ctx);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < match.servidores.size(); i++) {
            String[] srv    = match.servidores.get(i);
            String canal    = srv[0];
            String url      = srv[1];
            int iconColor   = ICON_COLORS[i % ICON_COLORS.length];
            int r = Color.red(iconColor), g = Color.green(iconColor), b = Color.blue(iconColor);

            // ── Row ───────────────────────────────────────────────────────────
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setClickable(true);
            row.setFocusable(true);

            // Row background: glass with left color accent gradient
            GradientDrawable rowBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                    Color.argb(55, r, g, b),
                    Color.argb(14, 255, 255, 255),
                    Color.argb(9,  255, 255, 255)
                });
            rowBg.setCornerRadius(dp(ctx,16));
            rowBg.setStroke(dp(ctx,1), Color.argb(28, r, g, b));

            // Pressed state background
            GradientDrawable rowBgPressed = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                    Color.argb(90, r, g, b),
                    Color.argb(30, 255, 255, 255),
                    Color.argb(18, 255, 255, 255)
                });
            rowBgPressed.setCornerRadius(dp(ctx,16));
            rowBgPressed.setStroke(dp(ctx,1), Color.argb(70, r, g, b));

            row.setBackground(rowBg);
            row.setPadding(dp(ctx,14), dp(ctx,15), dp(ctx,14), dp(ctx,15));

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, 0, 0, dp(ctx,9));
            row.setLayoutParams(rowLp);

            // ── Icon circle ───────────────────────────────────────────────────
            TextView numView = new TextView(ctx);
            numView.setText(String.valueOf(i + 1));
            numView.setTextColor(iconColor);
            numView.setTextSize(14);
            numView.setTypeface(null, Typeface.BOLD);
            numView.setGravity(Gravity.CENTER);
            GradientDrawable numBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                    Color.argb(50, r, g, b),
                    Color.argb(25, r, g, b)
                });
            numBg.setShape(GradientDrawable.OVAL);
            numBg.setStroke(dp(ctx,2), Color.argb(120, r, g, b));
            numView.setBackground(numBg);
            LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(
                dp(ctx,46), dp(ctx,46));
            numLp.setMarginEnd(dp(ctx,14));
            numView.setLayoutParams(numLp);
            row.addView(numView);

            // ── Info column ───────────────────────────────────────────────────
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
            tvSrvLabel.setTextColor(0x55FFFFFF);
            tvSrvLabel.setTextSize(11);
            LinearLayout.LayoutParams lblLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lblLp.setMargins(0, dp(ctx,3), 0, 0);
            tvSrvLabel.setLayoutParams(lblLp);
            info.addView(tvSrvLabel);
            row.addView(info);

            // ── Arrow pill ────────────────────────────────────────────────────
            TextView arrow = new TextView(ctx);
            arrow.setText("›");
            arrow.setTextSize(22);
            arrow.setTextColor(Color.argb(160, r, g, b));
            arrow.setGravity(Gravity.CENTER);
            GradientDrawable arrowBg = new GradientDrawable();
            arrowBg.setShape(GradientDrawable.RECTANGLE);
            arrowBg.setColor(Color.argb(22, r, g, b));
            arrowBg.setStroke(dp(ctx,1), Color.argb(60, r, g, b));
            arrowBg.setCornerRadius(dp(ctx,8));
            arrow.setBackground(arrowBg);
            LinearLayout.LayoutParams arrLp = new LinearLayout.LayoutParams(
                dp(ctx,32), dp(ctx,32));
            arrLp.setMarginStart(dp(ctx,10));
            arrLp.gravity = Gravity.CENTER_VERTICAL;
            arrow.setLayoutParams(arrLp);
            row.addView(arrow);

            // ── Touch feedback + click ─────────────────────────────────────
            final String fUrl   = url;
            final String fTitle = match.titulo;
            row.setOnClickListener(v -> {
                row.animate().scaleX(0.97f).scaleY(0.97f).setDuration(70)
                    .withEndAction(() ->
                        row.animate().scaleX(1f).scaleY(1f).setDuration(130).start())
                    .start();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(ctx, PlayerActivity.class);
                    intent.putExtra("url",   fUrl);
                    intent.putExtra("title", fTitle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                    dialog.dismiss();
                }, 180);
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

        // ── Slide-up animation ────────────────────────────────────────────────
        panel.setTranslationY(800f);
        panel.setAlpha(0f);
        dialog.setOnShowListener(d ->
            panel.animate()
                .translationY(0f).alpha(1f)
                .setDuration(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator(2.5f))
                .start());

        dialog.show();
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
