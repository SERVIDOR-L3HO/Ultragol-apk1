package com.ultragol.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean splashDone       = false;
    private boolean updateCheckDone  = false;
    private boolean hasUpdate        = false;

    private int    pendingVersionCode;
    private String pendingVersionName;
    private String pendingChangelog;
    private String pendingDownloadUrl;
    private boolean pendingForce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logoContainer    = findViewById(R.id.logoContainer);
        View loadingContainer = findViewById(R.id.loadingContainer);
        View glowCircle       = findViewById(R.id.glowCircle);

        logoContainer.setScaleX(0.5f);
        logoContainer.setScaleY(0.5f);
        logoContainer.setAlpha(0f);
        glowCircle.setScaleX(0f);
        glowCircle.setScaleY(0f);
        glowCircle.setAlpha(0f);

        AnimatorSet glowAnim = new AnimatorSet();
        glowAnim.playTogether(
            ObjectAnimator.ofFloat(glowCircle, "alpha",  0f, 0.6f).setDuration(800),
            ObjectAnimator.ofFloat(glowCircle, "scaleX", 0f, 1f).setDuration(800),
            ObjectAnimator.ofFloat(glowCircle, "scaleY", 0f, 1f).setDuration(800)
        );
        glowAnim.setInterpolator(new DecelerateInterpolator(2f));
        glowAnim.setStartDelay(100);
        glowAnim.start();

        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(
            ObjectAnimator.ofFloat(logoContainer, "alpha",        0f,   1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "scaleX",       0.5f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "scaleY",       0.5f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "translationY", 30f,  0f).setDuration(700)
        );
        logoAnim.setInterpolator(new OvershootInterpolator(1.5f));
        logoAnim.setStartDelay(250);
        logoAnim.start();

        new Handler().postDelayed(() -> {
            ObjectAnimator pulse = ObjectAnimator.ofFloat(glowCircle, "alpha", 0.6f, 0.3f, 0.6f);
            pulse.setDuration(1200);
            pulse.setRepeatCount(1);
            pulse.setInterpolator(new AccelerateDecelerateInterpolator());
            pulse.start();
        }, 900);

        new Handler().postDelayed(() -> {
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(loadingContainer, "alpha", 0f, 1f);
            fadeIn.setDuration(400);
            fadeIn.start();
        }, 1000);

        startUpdateCheck();

        new Handler().postDelayed(() -> {
            AnimatorSet exitAnim = new AnimatorSet();
            exitAnim.playTogether(
                ObjectAnimator.ofFloat(logoContainer,    "alpha",  1f, 0f).setDuration(350),
                ObjectAnimator.ofFloat(logoContainer,    "scaleX", 1f, 1.08f).setDuration(350),
                ObjectAnimator.ofFloat(logoContainer,    "scaleY", 1f, 1.08f).setDuration(350),
                ObjectAnimator.ofFloat(glowCircle,       "alpha",  0.4f, 0f).setDuration(350),
                ObjectAnimator.ofFloat(loadingContainer, "alpha",  1f, 0f).setDuration(250)
            );
            exitAnim.setInterpolator(new DecelerateInterpolator());
            exitAnim.start();

            new Handler().postDelayed(() -> {
                splashDone = true;
                if (updateCheckDone) {
                    if (hasUpdate) showUpdateDialog();
                    else goToMain();
                }
            }, 380);
        }, 2600);
    }

    private int getVersionCode() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getVersionName() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "?";
        }
    }

    private void startUpdateCheck() {
        int currentVersion = getVersionCode();
        UpdateChecker.check(currentVersion, new UpdateChecker.Callback() {
            @Override
            public void onUpdateAvailable(int newVersionCode, String newVersionName,
                                          String changelog, String downloadUrl, boolean forceUpdate) {
                pendingVersionCode = newVersionCode;
                pendingVersionName = newVersionName;
                pendingChangelog   = changelog;
                pendingDownloadUrl = downloadUrl;
                pendingForce       = forceUpdate;
                hasUpdate          = true;
                updateCheckDone    = true;
                if (splashDone) showUpdateDialog();
            }

            @Override
            public void onNoUpdate() {
                updateCheckDone = true;
                if (splashDone) goToMain();
            }

            @Override
            public void onError() {
                updateCheckDone = true;
                if (splashDone) goToMain();
            }
        });
    }

    private void showUpdateDialog() {
        if (isFinishing() || isDestroyed()) return;

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update);
        dialog.setCancelable(!pendingForce);
        dialog.setCanceledOnTouchOutside(!pendingForce);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView tvVersion  = dialog.findViewById(R.id.tvUpdateVersion);
        TextView tvChangelog = dialog.findViewById(R.id.tvChangelog);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        TextView tvProgress = dialog.findViewById(R.id.tvProgress);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        Button btnLater  = dialog.findViewById(R.id.btnLater);

        tvVersion.setText("v" + pendingVersionName + "  ←  v" + getVersionName());
        tvChangelog.setText(pendingChangelog.isEmpty() ? "Correcciones y mejoras generales." : pendingChangelog);

        if (pendingForce) {
            btnLater.setVisibility(View.GONE);
        }

        UpdateInstaller installer = new UpdateInstaller(this);

        btnUpdate.setOnClickListener(v -> {
            btnUpdate.setEnabled(false);
            btnUpdate.setText("Descargando…");
            progressBar.setVisibility(View.VISIBLE);
            tvProgress.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);

            installer.download(pendingDownloadUrl, new UpdateInstaller.ProgressCallback() {
                @Override
                public void onProgress(int percent) {
                    progressBar.setProgress(percent);
                    tvProgress.setText("Descargando… " + percent + "%");
                }
                @Override
                public void onComplete() {
                    tvProgress.setText("Instalando…");
                }
                @Override
                public void onFailed() {
                    btnUpdate.setEnabled(true);
                    btnUpdate.setText("⬇ DESCARGAR ACTUALIZACIÓN");
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setText("Error al descargar. Intenta de nuevo.");
                }
            });
        });

        btnLater.setOnClickListener(v -> {
            installer.cancel();
            dialog.dismiss();
            goToMain();
        });

        dialog.setOnDismissListener(d -> {
            if (!pendingForce) goToMain();
        });

        dialog.show();
    }

    private void goToMain() {
        if (isFinishing() || isDestroyed()) return;
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
