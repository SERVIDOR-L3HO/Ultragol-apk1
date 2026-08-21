package com.ultragol.app;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.ultragol.app.models.Channel;

/**
 * Premium bottom-sheet that appears when a user taps a channel card.
 * Shows channel info + streaming options (play / cast / local stream / quality).
 */
public class ChannelStreamDialog {

    public static void show(Context ctx, Channel channel) {
        Dialog dialog = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ── Dim overlay ───────────────────────────────────────────────────────
        FrameLayout root = new FrameLayout(ctx);
        root.setBackgroundColor(0xCC000000);
        root.setClickable(true);
        root.setOnClickListener(v -> dialog.dismiss());

        // ── Glass panel ───────────────────────────────────────────────────────
        LinearLayout panel = new LinearLayout(ctx);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setClickable(true);
        panel.setFocusable(true);
        panel.setOnClickListener(v -> { /* absorb */ });

        GradientDrawable panelBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xF50D0D22, 0xF8070714, 0xFA040412});
        panelBg.setCornerRadii(new float[]{
            dp(ctx,28), dp(ctx,28), dp(ctx,28), dp(ctx,28), 0, 0, 0, 0});
        panelBg.setStroke(dp(ctx,1), 0x30FFFFFF);
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
        hLp.setMargins(0, dp(ctx,6), 0, dp(ctx,22));
        handle.setLayoutParams(hLp);
        panel.addView(handle);

        // ── Channel header ────────────────────────────────────────────────────
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams headerLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerLp.setMargins(0, 0, 0, dp(ctx,20));
        header.setLayoutParams(headerLp);

        // Logo box
        ImageView logo = new ImageView(ctx);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        logo.setPadding(dp(ctx,8), dp(ctx,8), dp(ctx,8), dp(ctx,8));
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setColor(0x22FFFFFF);
        logoBg.setCornerRadius(dp(ctx,14));
        logo.setBackground(logoBg);
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(dp(ctx,72), dp(ctx,56));
        logoLp.setMarginEnd(dp(ctx,16));
        logo.setLayoutParams(logoLp);
        if (channel.getLogo() != null && !channel.getLogo().isEmpty()) {
            Glide.with(ctx).load(channel.getLogo())
                .placeholder(android.R.drawable.ic_menu_slideshow)
                .error(android.R.drawable.ic_menu_slideshow)
                .into(logo);
        } else {
            logo.setImageResource(android.R.drawable.ic_menu_slideshow);
        }
        header.addView(logo);

        // Info column
        LinearLayout infoCol = new LinearLayout(ctx);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(ctx);
        tvName.setText(channel.getDisplayName());
        tvName.setTextColor(0xFFFFFFFF);
        tvName.setTextSize(16);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setMaxLines(2);
        tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
        infoCol.addView(tvName);

        if (channel.getCountry() != null && !channel.getCountry().isEmpty()) {
            TextView tvCountry = new TextView(ctx);
            tvCountry.setText(channel.getCountry());
            tvCountry.setTextColor(0x66FFFFFF);
            tvCountry.setTextSize(11);
            LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            clp.setMargins(0, dp(ctx,3), 0, dp(ctx,7));
            tvCountry.setLayoutParams(clp);
            infoCol.addView(tvCountry);
        }

        // EN VIVO badge
        TextView liveBadge = new TextView(ctx);
        liveBadge.setText("● EN VIVO");
        liveBadge.setTextColor(0xFFFFFFFF);
        liveBadge.setTextSize(9);
        liveBadge.setTypeface(null, Typeface.BOLD);
        GradientDrawable liveBg = new GradientDrawable();
        liveBg.setColor(0xFFE53935);
        liveBg.setCornerRadius(dp(ctx,5));
        liveBadge.setBackground(liveBg);
        liveBadge.setPadding(dp(ctx,7), dp(ctx,3), dp(ctx,7), dp(ctx,3));
        infoCol.addView(liveBadge);

        header.addView(infoCol);
        panel.addView(header);

        // ── Separator + section label ─────────────────────────────────────────
        View sep = new View(ctx);
        sep.setBackgroundColor(0x1AFFFFFF);
        LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,1));
        sepLp.setMargins(0, 0, 0, dp(ctx,16));
        sep.setLayoutParams(sepLp);
        panel.addView(sep);

        TextView tvSection = new TextView(ctx);
        tvSection.setText("OPCIONES DE TRANSMISIÓN");
        tvSection.setTextColor(0x66FFFFFF);
        tvSection.setTextSize(10);
        tvSection.setTypeface(null, Typeface.BOLD);
        tvSection.setAllCaps(true);
        LinearLayout.LayoutParams secLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secLp.setMargins(0, 0, 0, dp(ctx,12));
        tvSection.setLayoutParams(secLp);
        panel.addView(tvSection);

        // ── Option 1: Ver en pantalla ─────────────────────────────────────────
        LinearLayout btnVer = makeOptionRow(ctx,
            "▶", 0xFF4FC3F7,
            "Ver en pantalla",
            "Reproduce en el teléfono · WebView + player nativo");
        LinearLayout.LayoutParams verLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        verLp.setMargins(0, 0, 0, dp(ctx,10));
        btnVer.setLayoutParams(verLp);
        btnVer.setOnClickListener(v -> {
            animatePressRow(btnVer);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url",   channel.getPlayerUrl());
                intent.putExtra("title", channel.getDisplayName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            }, 150);
        });
        panel.addView(btnVer);

        // ── Option 2: Transmitir al TV ────────────────────────────────────────
        LinearLayout btnCast = makeOptionRow(ctx,
            "📡", 0xFFFF9800,
            "Transmitir al TV",
            "DLNA · AirPlay · Chromecast — captura automática");
        LinearLayout.LayoutParams castLp2 = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        castLp2.setMargins(0, 0, 0, dp(ctx,10));
        btnCast.setLayoutParams(castLp2);
        btnCast.setOnClickListener(v -> {
            animatePressRow(btnCast);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url",      channel.getPlayerUrl());
                intent.putExtra("title",    channel.getDisplayName());
                intent.putExtra("autocast", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            }, 150);
        });
        panel.addView(btnCast);

        // ── Option 3: Transmisión local (IP) ─────────────────────────────────
        LinearLayout btnLocal = makeOptionRow(ctx,
            "📶", 0xFF00E5A0,
            "Transmisión local (Red Wi-Fi)",
            "Abre en VLC, navegador o TV de tu red local");
        LinearLayout.LayoutParams localLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        localLp.setMargins(0, 0, 0, dp(ctx,10));
        btnLocal.setLayoutParams(localLp);
        btnLocal.setOnClickListener(v -> {
            animatePressRow(btnLocal);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                showLocalStreamModal(ctx, channel);
            }, 150);
        });
        panel.addView(btnLocal);

        // ── Option 4: Calidad / fuente ────────────────────────────────────────
        LinearLayout btnQuality = makeOptionRow(ctx,
            "⚙", 0xFFAB47BC,
            "Cambiar fuente",
            "Carga el embed en WebView y elige calidad manualmente");
        LinearLayout.LayoutParams qualLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        qualLp.setMargins(0, 0, 0, dp(ctx,20));
        btnQuality.setLayoutParams(qualLp);
        btnQuality.setOnClickListener(v -> {
            animatePressRow(btnQuality);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(ctx, PlayerActivity.class);
                intent.putExtra("url",       channel.getPlayerUrl());
                intent.putExtra("title",     channel.getDisplayName());
                intent.putExtra("manual_mode", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            }, 150);
        });
        panel.addView(btnQuality);

        // ── Cancel ────────────────────────────────────────────────────────────
        TextView btnCancel = new TextView(ctx);
        btnCancel.setText("Cerrar");
        btnCancel.setTextColor(0x66FFFFFF);
        btnCancel.setTextSize(14);
        btnCancel.setGravity(Gravity.CENTER);
        btnCancel.setClickable(true);
        btnCancel.setFocusable(true);
        LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,44));
        btnCancel.setLayoutParams(cancelLp);
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        panel.addView(btnCancel);

        root.addView(panel);
        dialog.setContentView(root);
        Window w = dialog.getWindow();
        if (w != null) w.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        TvHelper.prepareDialog(dialog);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOCAL STREAM MODAL — glass creativo con servidor local en el dispositivo
    // ─────────────────────────────────────────────────────────────────────────

    private static void showLocalStreamModal(Context ctx, Channel channel) {
        Dialog modal = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        modal.requestWindowFeature(Window.FEATURE_NO_TITLE);

        String localIp  = getLocalIpAddress(ctx);
        boolean hasIp   = localIp != null && !localIp.isEmpty();
        String rawUrl   = channel.getPlayerUrl();

        // Arrancar el servidor local inmediatamente
        LocalStreamServer server = LocalStreamServer.getInstance();
        if (hasIp && rawUrl != null && !rawUrl.isEmpty()) {
            server.start(rawUrl);
        }

        // URL a mostrar: local (http://ip:4445/v.mkv) o la directa como fallback
        final String[] shownUrl = { hasIp ? server.getLocalUrl(localIp) : (rawUrl != null ? rawUrl : "") };

        // ── Dim overlay ───────────────────────────────────────────────────────
        FrameLayout root = new FrameLayout(ctx);
        root.setBackgroundColor(0xCC000000);
        root.setClickable(true);
        root.setOnClickListener(v -> { server.stop(); modal.dismiss(); });

        // ── Glass card centrado ───────────────────────────────────────────────
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> { /* absorb */ });

        GradientDrawable cardBg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{0xF0091A2A, 0xF0032015, 0xF0050F1A});
        cardBg.setCornerRadius(dp(ctx, 28));
        cardBg.setStroke(dp(ctx, 1), 0x4000E5A0);
        card.setBackground(cardBg);
        card.setPadding(dp(ctx, 24), dp(ctx, 28), dp(ctx, 24), dp(ctx, 28));

        FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.gravity = Gravity.CENTER;
        cardLp.setMargins(dp(ctx, 20), 0, dp(ctx, 20), 0);
        card.setLayoutParams(cardLp);

        // ── Título ────────────────────────────────────────────────────────────
        LinearLayout titleRow = new LinearLayout(ctx);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleRowLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleRowLp.setMargins(0, 0, 0, dp(ctx, 6));
        titleRow.setLayoutParams(titleRowLp);

        TextView wifiIcon = new TextView(ctx);
        wifiIcon.setText("📶");
        wifiIcon.setTextSize(22);
        wifiIcon.setGravity(Gravity.CENTER);
        GradientDrawable iconCircle = new GradientDrawable();
        iconCircle.setShape(GradientDrawable.OVAL);
        iconCircle.setColor(0x2500E5A0);
        iconCircle.setStroke(dp(ctx, 2), 0x6000E5A0);
        wifiIcon.setBackground(iconCircle);
        LinearLayout.LayoutParams wifiLp = new LinearLayout.LayoutParams(dp(ctx, 50), dp(ctx, 50));
        wifiLp.setMarginEnd(dp(ctx, 14));
        wifiIcon.setLayoutParams(wifiLp);
        titleRow.addView(wifiIcon);

        LinearLayout titleCol = new LinearLayout(ctx);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(ctx);
        tvTitle.setText("Transmisión local (PC / Navegador)");
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        titleCol.addView(tvTitle);

        TextView tvSub = new TextView(ctx);
        tvSub.setText(channel.getDisplayName());
        tvSub.setTextColor(0x88FFFFFF);
        tvSub.setTextSize(11);
        tvSub.setMaxLines(1);
        tvSub.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams subLp2 = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp2.setMargins(0, dp(ctx, 2), 0, 0);
        tvSub.setLayoutParams(subLp2);
        titleCol.addView(tvSub);

        titleRow.addView(titleCol);
        card.addView(titleRow);

        // ── Estado Wi-Fi ──────────────────────────────────────────────────────
        LinearLayout chipRow = new LinearLayout(ctx);
        chipRow.setOrientation(LinearLayout.HORIZONTAL);
        chipRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams chipRowLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipRowLp.setMargins(0, dp(ctx, 4), 0, dp(ctx, 16));
        chipRow.setLayoutParams(chipRowLp);

        TextView chipDot = new TextView(ctx);
        chipDot.setText("●");
        chipDot.setTextSize(8);
        chipDot.setTextColor(hasIp ? 0xFF00E5A0 : 0xFFFF5252);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dotLp.setMarginEnd(dp(ctx, 5));
        chipDot.setLayoutParams(dotLp);
        chipRow.addView(chipDot);

        TextView chipLabel = new TextView(ctx);
        chipLabel.setText(hasIp
            ? "Servidor local activo · " + localIp + ":" + LocalStreamServer.PORT
            : "Sin red Wi-Fi activa");
        chipLabel.setTextColor(hasIp ? 0x9900E5A0 : 0x99FF5252);
        chipLabel.setTextSize(10);
        chipRow.addView(chipLabel);
        card.addView(chipRow);

        // ── URL box ───────────────────────────────────────────────────────────
        TextView labelUrl = new TextView(ctx);
        labelUrl.setText("INGRESA ESTA URL EN VLC/PC O NAVEGADOR DE TU TV");
        labelUrl.setTextColor(0x55FFFFFF);
        labelUrl.setTextSize(9);
        labelUrl.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams labelUrlLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelUrlLp.setMargins(0, 0, 0, dp(ctx, 6));
        labelUrl.setLayoutParams(labelUrlLp);
        card.addView(labelUrl);

        FrameLayout urlBox = new FrameLayout(ctx);
        GradientDrawable urlBoxBg = new GradientDrawable();
        urlBoxBg.setColor(0x1A00E5A0);
        urlBoxBg.setCornerRadius(dp(ctx, 14));
        urlBoxBg.setStroke(dp(ctx, 1), 0x3300E5A0);
        urlBox.setBackground(urlBoxBg);
        urlBox.setPadding(dp(ctx, 14), dp(ctx, 12), dp(ctx, 14), dp(ctx, 12));
        LinearLayout.LayoutParams urlBoxLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        urlBoxLp.setMargins(0, 0, 0, dp(ctx, 6));
        urlBox.setLayoutParams(urlBoxLp);

        TextView tvUrl = new TextView(ctx);
        tvUrl.setText(shownUrl[0]);
        tvUrl.setTextColor(0xFF00E5A0);
        tvUrl.setTextSize(12);
        tvUrl.setTypeface(Typeface.MONOSPACE);
        tvUrl.setSingleLine(false);
        tvUrl.setMaxLines(3);
        tvUrl.setEllipsize(android.text.TextUtils.TruncateAt.END);
        urlBox.addView(tvUrl);
        card.addView(urlBox);

        // Hint
        TextView tvHint = new TextView(ctx);
        tvHint.setText("Ingresa esta URL en VLC/PC o el navegador de tu TV o reproductor.");
        tvHint.setTextColor(0x55FFFFFF);
        tvHint.setTextSize(10);
        LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hintLp.setMargins(0, 0, 0, dp(ctx, 16));
        tvHint.setLayoutParams(hintLp);
        card.addView(tvHint);

        // ── Toggle: Usar streaming local para apps ────────────────────────────
        LinearLayout toggleRow = new LinearLayout(ctx);
        toggleRow.setOrientation(LinearLayout.HORIZONTAL);
        toggleRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams toggleRowLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toggleRowLp.setMargins(0, 0, 0, dp(ctx, 4));
        toggleRow.setLayoutParams(toggleRowLp);

        LinearLayout toggleTextCol = new LinearLayout(ctx);
        toggleTextCol.setOrientation(LinearLayout.VERTICAL);
        toggleTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvToggleLabel = new TextView(ctx);
        tvToggleLabel.setText("Usar streaming local para apps");
        tvToggleLabel.setTextColor(0xFFFFFFFF);
        tvToggleLabel.setTextSize(13);
        tvToggleLabel.setTypeface(null, Typeface.BOLD);
        toggleTextCol.addView(tvToggleLabel);
        toggleRow.addView(toggleTextCol);

        android.widget.Switch toggleSwitch = new android.widget.Switch(ctx);
        toggleSwitch.setChecked(hasIp && server.isRunning());
        int[] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{} }[0];
        toggleSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked && rawUrl != null && !rawUrl.isEmpty()) {
                server.start(rawUrl);
                String localUrl = server.getLocalUrl(localIp != null ? localIp : "127.0.0.1");
                shownUrl[0] = localUrl;
                tvUrl.setText(localUrl);
                chipDot.setTextColor(0xFF00E5A0);
                chipLabel.setText("Servidor local activo · " + (localIp != null ? localIp : "127.0.0.1") + ":" + LocalStreamServer.PORT);
            } else {
                server.stop();
                shownUrl[0] = rawUrl != null ? rawUrl : "";
                tvUrl.setText(shownUrl[0]);
                chipDot.setTextColor(0xFFFF5252);
                chipLabel.setText("Servidor local detenido");
            }
        });
        toggleRow.addView(toggleSwitch);
        card.addView(toggleRow);

        TextView tvToggleNote = new TextView(ctx);
        tvToggleNote.setText("Nota: El streaming local limpia y optimiza la URL internamente para mejorar la compatibilidad y fluidez en aplicaciones externas.");
        tvToggleNote.setTextColor(0x44FFFFFF);
        tvToggleNote.setTextSize(9);
        LinearLayout.LayoutParams noteNp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noteNp.setMargins(0, dp(ctx, 4), 0, dp(ctx, 20));
        tvToggleNote.setLayoutParams(noteNp);
        card.addView(tvToggleNote);

        // ── Separator + Enviar a aplicaciones externas ────────────────────────
        View sep2 = new View(ctx);
        sep2.setBackgroundColor(0x1AFFFFFF);
        LinearLayout.LayoutParams sep2Lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 1));
        sep2Lp.setMargins(0, 0, 0, dp(ctx, 16));
        sep2.setLayoutParams(sep2Lp);
        card.addView(sep2);

        TextView tvExtLabel = new TextView(ctx);
        tvExtLabel.setText("Enviar a aplicaciones externas");
        tvExtLabel.setTextColor(0xFFFFFFFF);
        tvExtLabel.setTextSize(14);
        tvExtLabel.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams extLabelLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        extLabelLp.setMargins(0, 0, 0, dp(ctx, 12));
        tvExtLabel.setLayoutParams(extLabelLp);
        card.addView(tvExtLabel);

        // Botones externos
        LinearLayout extBtns = new LinearLayout(ctx);
        extBtns.setOrientation(LinearLayout.HORIZONTAL);
        extBtns.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams extBtnsLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        extBtnsLp.setMargins(0, 0, 0, dp(ctx, 20));
        extBtns.setLayoutParams(extBtnsLp);

        extBtns.addView(makeExtButton(ctx, "📋\nCOPIAR\nURL", 0xFF00E5A0, () -> {
            android.content.ClipboardManager cm = (android.content.ClipboardManager)
                ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(android.content.ClipData.newPlainText("Stream URL", shownUrl[0]));
                Toast.makeText(ctx, "URL copiada", Toast.LENGTH_SHORT).show();
            }
        }));
        extBtns.addView(makeExtButton(ctx, "↗\nCOMPARTIR", 0xFF4FC3F7, () -> {
            Intent si = new Intent(Intent.ACTION_SEND);
            si.setType("text/plain");
            si.putExtra(Intent.EXTRA_TEXT, shownUrl[0]);
            si.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(Intent.createChooser(si, "Compartir stream")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }));
        extBtns.addView(makeExtButton(ctx, "▶\nVLC", 0xFFFF9800, () -> {
            try {
                Intent vlc = new Intent(Intent.ACTION_VIEW);
                vlc.setDataAndType(android.net.Uri.parse(shownUrl[0]), "video/*");
                vlc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(vlc);
            } catch (Exception e) {
                Toast.makeText(ctx, "No se encontró un reproductor", Toast.LENGTH_SHORT).show();
            }
        }));
        card.addView(extBtns);

        // Cerrar
        TextView btnClose = new TextView(ctx);
        btnClose.setText("Cerrar");
        btnClose.setTextColor(0x55FFFFFF);
        btnClose.setTextSize(13);
        btnClose.setGravity(Gravity.CENTER);
        btnClose.setClickable(true);
        btnClose.setFocusable(true);
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 40));
        btnClose.setLayoutParams(closeLp);
        btnClose.setOnClickListener(v -> { server.stop(); modal.dismiss(); });
        card.addView(btnClose);

        root.addView(card);
        modal.setContentView(root);
        Window w = modal.getWindow();
        if (w != null) w.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        modal.show();
        TvHelper.prepareDialog(modal);
    }

    private static TextView makeExtButton(Context ctx, String label, int color, Runnable action) {
        int r = Color.red(color), g = Color.green(color), b = Color.blue(color);
        TextView btn = new TextView(ctx);
        btn.setText(label);
        btn.setGravity(Gravity.CENTER);
        btn.setTextSize(9);
        btn.setTextColor(color);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setClickable(true);
        btn.setFocusable(true);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(25, r, g, b));
        bg.setCornerRadius(dp(ctx, 12));
        bg.setStroke(dp(ctx, 1), Color.argb(80, r, g, b));
        btn.setBackground(bg);
        btn.setPadding(dp(ctx, 10), dp(ctx, 10), dp(ctx, 10), dp(ctx, 10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(ctx, 64), 1f);
        lp.setMargins(dp(ctx, 4), 0, dp(ctx, 4), 0);
        btn.setLayoutParams(lp);
        btn.setOnClickListener(v -> {
            btn.animate().scaleX(0.95f).scaleY(0.95f).setDuration(60)
                .withEndAction(() -> btn.animate().scaleX(1f).scaleY(1f).setDuration(100).start()).start();
            action.run();
        });
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns the device's current Wi-Fi IP, or null if not connected. */
    @SuppressWarnings("deprecation")
    private static String getLocalIpAddress(Context ctx) {
        try {
            WifiManager wm = (WifiManager) ctx.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
            if (wm == null) return null;
            WifiInfo info = wm.getConnectionInfo();
            int ip = info.getIpAddress();
            if (ip == 0) return null;
            return Formatter.formatIpAddress(ip);
        } catch (Exception e) {
            return null;
        }
    }

    private static void animatePressRow(View row) {
        row.animate().scaleX(0.97f).scaleY(0.97f).setDuration(70)
            .withEndAction(() -> row.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
            .start();
    }

    private static TextView makeActionButton(Context ctx, String label, int color, boolean filled) {
        int r = Color.red(color), g = Color.green(color), b = Color.blue(color);
        TextView btn = new TextView(ctx);
        btn.setText(label);
        btn.setGravity(Gravity.CENTER);
        btn.setTextSize(12);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setClickable(true);
        btn.setFocusable(true);

        GradientDrawable bg = new GradientDrawable();
        if (filled) {
            bg.setColor(Color.argb(220, r, g, b));
            btn.setTextColor(0xFF000000);
        } else {
            bg.setColor(Color.argb(25, r, g, b));
            btn.setTextColor(color);
            bg.setStroke(dp(ctx, 1), Color.argb(120, r, g, b));
        }
        bg.setCornerRadius(dp(ctx, 14));
        btn.setBackground(bg);
        return btn;
    }

    /** Build one option row in the main sheet. */
    private static LinearLayout makeOptionRow(Context ctx, String icon, int iconColor,
                                              String title, String subtitle) {
        int r = Color.red(iconColor), g = Color.green(iconColor), b = Color.blue(iconColor);

        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setFocusable(true);

        GradientDrawable rowBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.argb(45,r,g,b), Color.argb(16,255,255,255), Color.argb(10,255,255,255)});
        rowBg.setCornerRadius(dp(ctx,16));
        rowBg.setStroke(dp(ctx,1), Color.argb(30,r,g,b));
        row.setBackground(rowBg);
        row.setPadding(dp(ctx,14), dp(ctx,14), dp(ctx,14), dp(ctx,14));

        // Icon circle
        TextView tvIcon = new TextView(ctx);
        tvIcon.setText(icon);
        tvIcon.setTextSize(20);
        tvIcon.setGravity(Gravity.CENTER);
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.argb(40,r,g,b));
        iconBg.setStroke(dp(ctx,2), Color.argb(100,r,g,b));
        tvIcon.setBackground(iconBg);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(ctx,48), dp(ctx,48));
        iconLp.setMarginEnd(dp(ctx,14));
        tvIcon.setLayoutParams(iconLp);
        row.addView(tvIcon);

        // Text column
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(ctx);
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, Typeface.BOLD);
        col.addView(tvTitle);

        TextView tvSub = new TextView(ctx);
        tvSub.setText(subtitle);
        tvSub.setTextColor(0x55FFFFFF);
        tvSub.setTextSize(10);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, dp(ctx,3), 0, 0);
        tvSub.setLayoutParams(subLp);
        col.addView(tvSub);

        row.addView(col);

        // Arrow
        TextView arrow = new TextView(ctx);
        arrow.setText("›");
        arrow.setTextSize(22);
        arrow.setTextColor(Color.argb(150,r,g,b));
        arrow.setGravity(Gravity.CENTER);
        GradientDrawable arrowBg = new GradientDrawable();
        arrowBg.setColor(Color.argb(20,r,g,b));
        arrowBg.setStroke(dp(ctx,1), Color.argb(60,r,g,b));
        arrowBg.setCornerRadius(dp(ctx,8));
        arrow.setBackground(arrowBg);
        LinearLayout.LayoutParams arrLp = new LinearLayout.LayoutParams(dp(ctx,30), dp(ctx,30));
        arrLp.gravity = Gravity.CENTER_VERTICAL;
        arrow.setLayoutParams(arrLp);
        row.addView(arrow);

        return row;
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
