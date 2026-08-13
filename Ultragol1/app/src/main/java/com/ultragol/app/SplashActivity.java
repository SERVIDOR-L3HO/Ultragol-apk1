package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
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

        final View glowInner  = findViewById(R.id.splashGlowInner);
        final View ringOuter  = findViewById(R.id.splashRingOuter);
        final View ringMid    = findViewById(R.id.splashRingMid);
        final View logo       = findViewById(R.id.splashLogo);
        final View divider    = findViewById(R.id.splashDivider);
        final View spinner    = findViewById(R.id.splashSpinner);

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

        // ── 2. ANILLOS: fade-in suave ────────────────────────────────────────────
        if (ringOuter != null) {
            ringOuter.animate().alpha(1f).setStartDelay(150).setDuration(900).start();
        }
        if (ringMid != null) {
            ringMid.animate().alpha(1f).setStartDelay(250).setDuration(900).start();
        }

        // ── 3. LOGO: overshoot bounce — llega, rebota levemente ────────────────
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

        // ── 4. DIVISOR + SPINNER: fade-in después del logo ──────────────────────
        new Handler().postDelayed(() -> {
            if (divider != null) divider.animate().alpha(1f).setDuration(500).start();
            if (spinner != null) spinner.animate().alpha(1f).setDuration(500).start();
        }, 1000);

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
