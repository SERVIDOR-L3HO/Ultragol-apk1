package com.streamnova.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logoContainer = findViewById(R.id.logoContainer);
        View loadingContainer = findViewById(R.id.loadingContainer);

        logoContainer.setScaleX(0.6f);
        logoContainer.setScaleY(0.6f);
        logoContainer.setAlpha(0f);

        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(
            ObjectAnimator.ofFloat(logoContainer, "alpha", 0f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "scaleX", 0.6f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "scaleY", 0.6f, 1f).setDuration(700)
        );
        logoAnim.setInterpolator(new OvershootInterpolator(1.3f));
        logoAnim.setStartDelay(150);
        logoAnim.start();

        new Handler().postDelayed(() -> {
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(loadingContainer, "alpha", 0f, 1f);
            fadeIn.setDuration(400);
            fadeIn.start();
        }, 900);

        new Handler().postDelayed(() -> {
            AnimatorSet exitAnim = new AnimatorSet();
            exitAnim.playTogether(
                ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f).setDuration(350),
                ObjectAnimator.ofFloat(logoContainer, "scaleX", 1f, 1.1f).setDuration(350),
                ObjectAnimator.ofFloat(logoContainer, "scaleY", 1f, 1.1f).setDuration(350),
                ObjectAnimator.ofFloat(loadingContainer, "alpha", 1f, 0f).setDuration(250)
            );
            exitAnim.setInterpolator(new DecelerateInterpolator());
            exitAnim.start();

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 380);
        }, 2400);
    }
}
