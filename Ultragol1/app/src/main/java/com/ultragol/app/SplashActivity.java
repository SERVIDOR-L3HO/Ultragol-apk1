package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2800;

    private View dot1, dot2, dot3;
    private int dotStep = 0;
    private final Handler dotHandler = new Handler();
    private final Runnable dotRunnable = new Runnable() {
        @Override
        public void run() {
            animateDots();
            dotHandler.postDelayed(this, 380);
        }
    };

    private boolean updateCheckDone = false;
    private boolean splashDone      = false;
    private boolean updateAvailable = false;
    private JSONObject updateData   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo     = findViewById(R.id.splashLogo);
        View divider  = findViewById(R.id.splashDivider);
        View tagline  = findViewById(R.id.splashTagline);
        View progress = findViewById(R.id.splashProgress);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        // ── Logo: escala suave + fade-in estilo Disney+ ──
        if (logo != null) {
            AnimationSet anim = new AnimationSet(true);
            ScaleAnimation scale = new ScaleAnimation(
                0.88f, 1f, 0.88f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setDuration(900);
            AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
            alpha.setDuration(900);
            anim.addAnimation(scale);
            anim.addAnimation(alpha);
            anim.setFillAfter(true);
            logo.startAnimation(anim);
            logo.setAlpha(1f);
        }

        // ── Divider: aparece después del logo ──
        new Handler().postDelayed(() -> {
            if (divider != null) {
                // Animar expansión del divider
                divider.setAlpha(0f);
                divider.getLayoutParams().width = 0;
                divider.requestLayout();

                AlphaAnimation fadeDiv = new AlphaAnimation(0f, 1f);
                fadeDiv.setDuration(600);
                fadeDiv.setFillAfter(true);
                divider.startAnimation(fadeDiv);
                divider.setAlpha(1f);

                // Expandir ancho a 160dp
                android.animation.ValueAnimator widthAnim = android.animation.ValueAnimator.ofInt(0, (int)(160 * getResources().getDisplayMetrics().density));
                widthAnim.setDuration(600);
                widthAnim.addUpdateListener(animation -> {
                    divider.getLayoutParams().width = (int) animation.getAnimatedValue();
                    divider.requestLayout();
                });
                widthAnim.start();
            }
        }, 750);

        // ── Tagline: aparece después ──
        new Handler().postDelayed(() -> {
            if (tagline != null) {
                AlphaAnimation a2 = new AlphaAnimation(0f, 1f);
                a2.setDuration(600);
                a2.setFillAfter(true);
                tagline.startAnimation(a2);
                tagline.setAlpha(1f);
            }
        }, 1100);

        // ── Dots de carga ──
        new Handler().postDelayed(() -> {
            if (progress != null) {
                AlphaAnimation a3 = new AlphaAnimation(0f, 1f);
                a3.setDuration(400);
                a3.setFillAfter(true);
                progress.startAnimation(a3);
                progress.setAlpha(1f);
                dotHandler.post(dotRunnable);
            }
        }, 1400);

        // ── Verificar actualización en paralelo ──
        UpdateChecker.check(this, (needsUpdate, data) -> {
            updateAvailable = needsUpdate;
            updateData      = data;
            updateCheckDone = true;
            if (splashDone) proceed();
        });

        // ── Timer del splash ──
        new Handler().postDelayed(() -> {
            dotHandler.removeCallbacks(dotRunnable);
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
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void animateDots() {
        if (dot1 == null || dot2 == null || dot3 == null) return;
        dotStep = (dotStep + 1) % 3;
        dot1.setBackground(getDrawable(dotStep == 0 ? R.drawable.splash_dot : R.drawable.splash_dot_dim));
        dot2.setBackground(getDrawable(dotStep == 1 ? R.drawable.splash_dot : R.drawable.splash_dot_dim));
        dot3.setBackground(getDrawable(dotStep == 2 ? R.drawable.splash_dot : R.drawable.splash_dot_dim));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dotHandler.removeCallbacks(dotRunnable);
    }
}
