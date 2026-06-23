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

    private static final long SPLASH_DURATION = 2400;

    private View dot1, dot2, dot3;
    private int dotStep = 0;
    private final Handler dotHandler = new Handler();
    private final Runnable dotRunnable = new Runnable() {
        @Override
        public void run() {
            animateDots();
            dotHandler.postDelayed(this, 350);
        }
    };

    // Update check result (set from background thread, read on main thread)
    private boolean updateCheckDone   = false;
    private boolean splashDone        = false;
    private boolean updateAvailable   = false;
    private JSONObject updateData     = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo     = findViewById(R.id.splashLogo);
        View tagline  = findViewById(R.id.splashTagline);
        View progress = findViewById(R.id.splashProgress);
        View ringO    = findViewById(R.id.ringOuter);
        View ringM    = findViewById(R.id.ringMid);
        View ringI    = findViewById(R.id.ringInner);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        // Rings fade in
        animateFadeIn(ringO, 0, 800);
        animateFadeIn(ringM, 150, 800);
        animateFadeIn(ringI, 300, 800);

        // Logo scale + fade
        AnimationSet anim = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(0.6f, 1f, 0.6f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(700);
        AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        alpha.setDuration(700);
        anim.addAnimation(scale);
        anim.addAnimation(alpha);
        if (logo != null) logo.startAnimation(anim);

        // Tagline
        new Handler().postDelayed(() -> {
            if (tagline != null) {
                AlphaAnimation a2 = new AlphaAnimation(0f, 1f);
                a2.setDuration(500);
                tagline.setVisibility(View.VISIBLE);
                tagline.startAnimation(a2);
            }
        }, 600);

        // Dots
        new Handler().postDelayed(() -> {
            if (progress != null) {
                AlphaAnimation a3 = new AlphaAnimation(0f, 1f);
                a3.setDuration(300);
                progress.setVisibility(View.VISIBLE);
                progress.startAnimation(a3);
                dotHandler.post(dotRunnable);
            }
        }, 900);

        // ── Start update check in parallel with the splash animation ──
        UpdateChecker.check(this, (needsUpdate, data) -> {
            updateAvailable = needsUpdate;
            updateData      = data;
            updateCheckDone = true;
            if (splashDone) proceed(); // splash already finished → go now
        });

        // ── Splash timer ──
        new Handler().postDelayed(() -> {
            dotHandler.removeCallbacks(dotRunnable);
            splashDone = true;
            if (updateCheckDone) proceed(); // check already done → go now
            // else wait for check callback above
        }, SPLASH_DURATION);
    }

    private void proceed() {
        NotificationChecker.check(this);
        if (updateAvailable && updateData != null) {
            // Show update dialog — then go to MainActivity when user decides
            boolean force = updateData.optBoolean("forceUpdate", false);
            showUpdateAndProceed(force);
        } else {
            goToMain();
        }
    }

    private void showUpdateAndProceed(boolean force) {
        // Show dialog; on dismiss (if not forced) go to main
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, null);
        android.widget.TextView tvVersion   = dialogView.findViewById(R.id.updateVersion);
        android.widget.TextView tvChangelog = dialogView.findViewById(R.id.updateChangelog);

        String version  = updateData.optString("versionName", "");
        String changelog = updateData.optString("changelog", "");
        String dlUrl    = updateData.optString("downloadUrl", "");

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

    private void animateFadeIn(View v, long delay, long duration) {
        if (v == null) return;
        new Handler().postDelayed(() -> {
            AlphaAnimation a = new AlphaAnimation(0f, 1f);
            a.setDuration(duration);
            v.startAnimation(a);
        }, delay);
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
