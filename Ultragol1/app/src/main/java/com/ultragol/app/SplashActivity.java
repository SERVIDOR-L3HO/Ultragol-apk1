package com.ultragol.app;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000;

    private boolean updateCheckDone = false;
    private boolean splashDone      = false;
    private boolean updateAvailable = false;
    private JSONObject updateData   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View glowInner      = findViewById(R.id.splashGlowInner);
        View logo           = findViewById(R.id.splashLogo);
        View tagline        = findViewById(R.id.splashTagline);
        View progressBar    = findViewById(R.id.splashProgressBar);
        View progressFill   = findViewById(R.id.splashProgressFill);

        // ── 1. Glow interno: fade-in lento y dramático ─────────────────────────
        if (glowInner != null) {
            AlphaAnimation glowAnim = new AlphaAnimation(0f, 1f);
            glowAnim.setDuration(1400);
            glowAnim.setFillAfter(true);
            glowInner.startAnimation(glowAnim);
            glowInner.setAlpha(1f);
        }

        // ── 2. Logo: aparece grande, escala suave (estilo Disney+) ─────────────
        if (logo != null) {
            AnimationSet anim = new AnimationSet(true);

            ScaleAnimation scale = new ScaleAnimation(
                0.82f, 1f, 0.82f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setDuration(1000);
            scale.setInterpolator(new DecelerateInterpolator(2f));

            AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
            alpha.setDuration(1000);

            anim.addAnimation(scale);
            anim.addAnimation(alpha);
            anim.setFillAfter(true);
            logo.startAnimation(anim);
            logo.setAlpha(1f);
        }

        // ── 3. Tagline: aparece suavemente después del logo ────────────────────
        new Handler().postDelayed(() -> {
            if (tagline == null) return;
            AlphaAnimation a = new AlphaAnimation(0f, 1f);
            a.setDuration(700);
            a.setFillAfter(true);
            tagline.startAnimation(a);
            tagline.setAlpha(1f);
        }, 900);

        // ── 4. Barra de progreso full-width al fondo (estilo Netflix) ──────────
        new Handler().postDelayed(() -> {
            if (progressBar == null || progressFill == null) return;

            // Mostrar contenedor
            AlphaAnimation showBar = new AlphaAnimation(0f, 1f);
            showBar.setDuration(300);
            showBar.setFillAfter(true);
            progressBar.startAnimation(showBar);
            progressBar.setAlpha(1f);

            // Obtener ancho real de pantalla
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;

            // Animar relleno de 0 a pantalla completa en ~2000ms
            ValueAnimator fillAnim = ValueAnimator.ofInt(0, screenWidth);
            fillAnim.setDuration(2000);
            fillAnim.setInterpolator(new LinearInterpolator());
            fillAnim.addUpdateListener(animation -> {
                ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
                lp.width = (int) animation.getAnimatedValue();
                progressFill.setLayoutParams(lp);
            });
            fillAnim.start();
        }, 800);

        // ── Verificar actualización en paralelo ────────────────────────────────
        UpdateChecker.check(this, (needsUpdate, data) -> {
            updateAvailable = needsUpdate;
            updateData      = data;
            updateCheckDone = true;
            if (splashDone) proceed();
        });

        // ── Timer del splash ───────────────────────────────────────────────────
        new Handler().postDelayed(() -> {
            splashDone = true;
            if (updateCheckDone) proceed();
        }, SPLASH_DURATION);
    }

    private void proceed() {
        if (updateAvailable && updateData != null) {
            boolean force = updateData.optBoolean("forceUpdate", false);
            showUpdateAndProceed(force);
        } else {
            goToMain();
        }
    }

    private void showUpdateAndProceed(boolean force) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, null);
        android.widget.TextView tvVersion   = dialogView.findViewById(R.id.updateVersion);
        android.widget.TextView tvChangelog = dialogView.findViewById(R.id.updateChangelog);

        String version   = updateData.optString("versionName", "");
        String changelog = updateData.optString("changelog", "");
        String dlUrl     = updateData.optString("downloadUrl", "");

        if (tvVersion != null)
            tvVersion.setText("Versión " + version + " disponible");
        if (tvChangelog != null) {
            if (changelog.isEmpty()) {
                tvChangelog.setVisibility(View.GONE);
            } else {
                tvChangelog.setText(changelog);
            }
        }

        androidx.appcompat.app.AlertDialog.Builder builder =
            new androidx.appcompat.app.AlertDialog.Builder(this, R.style.UpdateDialogTheme)
                .setView(dialogView)
                .setCancelable(!force)
                .setPositiveButton("⬇  Descargar ahora", (d, w) -> {
                    if (!dlUrl.isEmpty()) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                android.net.Uri.parse(dlUrl)));
                        } catch (Exception ignored) {}
                    }
                    if (!force) goToMain();
                });

        if (!force) {
            builder.setNegativeButton("Más tarde", (d, w) -> goToMain());
        }

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }

    private void goToMain() {
        if (isFinishing() || isDestroyed()) return;
        startActivity(new Intent(this, ProfileSelectorActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
