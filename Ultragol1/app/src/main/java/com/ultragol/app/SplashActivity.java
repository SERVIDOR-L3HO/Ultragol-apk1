package com.ultragol.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONObject;
import com.ultragol.app.UserSyncManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3200;

    private boolean updateCheckDone = false;
    private boolean splashDone      = false;
    private boolean updateAvailable = false;
    private JSONObject updateData   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final View glowInner    = findViewById(R.id.splashGlowInner);
        final View logo         = findViewById(R.id.splashLogo);
        final View tagline      = findViewById(R.id.splashTagline);
        final View progressBar  = findViewById(R.id.splashProgressBar);
        final CandyStripeView progressFill = findViewById(R.id.splashProgressFill);

        // ── 1. GLOW: aparece expandiéndose desde pequeño ───────────────────────
        if (glowInner != null) {
            glowInner.setScaleX(0.4f);
            glowInner.setScaleY(0.4f);
            glowInner.setAlpha(0f);

            glowInner.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .setInterpolator(new DecelerateInterpolator(1.8f))
                .withEndAction(() -> startGlowPulse(glowInner))
                .start();
        }

        // ── 2. LOGO: overshoot bounce — llega, rebota levemente ────────────────
        if (logo != null) {
            logo.setScaleX(0.5f);
            logo.setScaleY(0.5f);
            logo.setAlpha(0f);

            logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(300)
                .setDuration(900)
                .setInterpolator(new OvershootInterpolator(1.6f))
                .start();
        }

        // ── 3. TAGLINE: desliza desde abajo + fade ─────────────────────────────
        new Handler().postDelayed(() -> {
            if (tagline == null) return;

            AnimationSet anim = new AnimationSet(true);

            TranslateAnimation slide = new TranslateAnimation(
                0, 0, 40f, 0f);
            slide.setDuration(700);
            slide.setInterpolator(new DecelerateInterpolator(1.5f));

            AlphaAnimation fade = new AlphaAnimation(0f, 1f);
            fade.setDuration(700);

            anim.addAnimation(slide);
            anim.addAnimation(fade);
            anim.setFillAfter(true);
            tagline.startAnimation(anim);
            tagline.setAlpha(1f);
        }, 900);

        // ── 4. BARRA PROGRESO: full-width, se llena con easing ─────────────────
        new Handler().postDelayed(() -> {
            if (progressBar == null || progressFill == null) return;

            progressBar.animate()
                .alpha(1f)
                .setDuration(400)
                .start();

            progressFill.startAnimating();

            progressBar.post(() -> {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;

                ValueAnimator fillAnim = ValueAnimator.ofInt(0, screenWidth);
                fillAnim.setDuration(2200);
                fillAnim.setInterpolator(new LinearInterpolator());
                fillAnim.addUpdateListener(animation -> {
                    ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
                    lp.width = (int) animation.getAnimatedValue();
                    progressFill.setLayoutParams(lp);
                });
                fillAnim.start();
            });
        }, 700);

        // ── Verificar actualización en paralelo ────────────────────────────────
        UpdateChecker.check(this, (needsUpdate, data) -> {
            updateAvailable = needsUpdate;
            updateData      = data;
            updateCheckDone = true;
            if (splashDone) proceed();
        });

        new Handler().postDelayed(() -> {
            splashDone = true;
            if (updateCheckDone) proceed();
        }, SPLASH_DURATION);
    }

    /** Pulso suave del glow: respira 0.85 ↔ 1.0 en loop */
    private void startGlowPulse(View glow) {
        if (glow == null || isFinishing() || isDestroyed()) return;
        glow.animate()
            .scaleX(0.85f).scaleY(0.85f)
            .alpha(0.65f)
            .setDuration(1100)
            .setInterpolator(new AccelerateInterpolator(1.2f))
            .withEndAction(() -> glow.animate()
                .scaleX(1f).scaleY(1f)
                .alpha(1f)
                .setDuration(1100)
                .setInterpolator(new DecelerateInterpolator(1.2f))
                .withEndAction(() -> startGlowPulse(glow))
                .start())
            .start();
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
        com.google.firebase.auth.FirebaseUser user =
            FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Do not open the selector until the cloud pull has completed.
            // Otherwise a fresh install sees an empty local list, creates a
            // duplicate profile, and that duplicate can overwrite the account.
            UserSyncManager.pullFromCloud(getApplicationContext(), user.getUid(),
                success -> runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    startActivity(new Intent(this, ProfileSelectorActivity.class));
                    overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                    finish();
                }));
            return;
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
