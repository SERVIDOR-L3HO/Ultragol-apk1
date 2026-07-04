package com.ultragol.app;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2800;

    private boolean updateCheckDone = false;
    private boolean splashDone      = false;
    private boolean updateAvailable = false;
    private JSONObject updateData   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View glowOrb   = findViewById(R.id.splashGlowOrb);
        View logo      = findViewById(R.id.splashLogo);
        View divider   = findViewById(R.id.splashDivider);
        View tagline   = findViewById(R.id.splashTagline);
        View progressContainer = findViewById(R.id.splashProgressContainer);
        View progressFill      = findViewById(R.id.splashProgressFill);

        // ── 1. Halo naranja: fade-in suave ─────────────────────────────────────
        if (glowOrb != null) {
            AlphaAnimation glowAnim = new AlphaAnimation(0f, 1f);
            glowAnim.setDuration(1200);
            glowAnim.setFillAfter(true);
            glowOrb.startAnimation(glowAnim);
            glowOrb.setAlpha(1f);
        }

        // ── 2. Logo: scale sutil + fade-in (estilo Disney+) ───────────────────
        if (logo != null) {
            AnimationSet anim = new AnimationSet(true);
            ScaleAnimation scale = new ScaleAnimation(
                0.90f, 1f, 0.90f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setDuration(850);
            scale.setInterpolator(new DecelerateInterpolator(1.5f));
            AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
            alpha.setDuration(850);
            anim.addAnimation(scale);
            anim.addAnimation(alpha);
            anim.setFillAfter(true);
            logo.startAnimation(anim);
            logo.setAlpha(1f);
        }

        // ── 3. Divider naranja: se expande desde el centro ─────────────────────
        new Handler().postDelayed(() -> {
            if (divider == null) return;
            divider.setAlpha(1f);

            AlphaAnimation fadeDiv = new AlphaAnimation(0f, 1f);
            fadeDiv.setDuration(400);
            fadeDiv.setFillAfter(true);
            divider.startAnimation(fadeDiv);

            int targetWidthPx = (int)(160 * getResources().getDisplayMetrics().density);
            ValueAnimator widthAnim = ValueAnimator.ofInt(0, targetWidthPx);
            widthAnim.setDuration(600);
            widthAnim.setInterpolator(new DecelerateInterpolator(1.2f));
            widthAnim.addUpdateListener(animation -> {
                ViewGroup.LayoutParams lp = divider.getLayoutParams();
                lp.width = (int) animation.getAnimatedValue();
                divider.setLayoutParams(lp);
            });
            widthAnim.start();
        }, 700);

        // ── 4. Tagline: fade-in ────────────────────────────────────────────────
        new Handler().postDelayed(() -> {
            if (tagline == null) return;
            AlphaAnimation a2 = new AlphaAnimation(0f, 1f);
            a2.setDuration(600);
            a2.setFillAfter(true);
            tagline.startAnimation(a2);
            tagline.setAlpha(1f);
        }, 1050);

        // ── 5. Barra de progreso: aparece y rellena suavemente ─────────────────
        new Handler().postDelayed(() -> {
            if (progressContainer == null || progressFill == null) return;

            // Mostrar contenedor
            AlphaAnimation showBar = new AlphaAnimation(0f, 1f);
            showBar.setDuration(350);
            showBar.setFillAfter(true);
            progressContainer.startAnimation(showBar);
            progressContainer.setAlpha(1f);

            // Animar relleno de 0 a 100% del ancho del contenedor
            progressContainer.post(() -> {
                int totalWidth = progressContainer.getWidth();
                ValueAnimator fillAnim = ValueAnimator.ofInt(0, totalWidth);
                fillAnim.setDuration(1600);
                fillAnim.setStartDelay(150);
                fillAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                fillAnim.addUpdateListener(animation -> {
                    ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
                    lp.width = (int) animation.getAnimatedValue();
                    progressFill.setLayoutParams(lp);
                });
                fillAnim.start();
            });
        }, 1350);

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
