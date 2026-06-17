package com.ultragol.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logoContainer  = findViewById(R.id.logoContainer);
        View loadingContainer = findViewById(R.id.loadingContainer);
        View glowCircle = findViewById(R.id.glowCircle);

        // Initial state
        logoContainer.setScaleX(0.5f);
        logoContainer.setScaleY(0.5f);
        logoContainer.setAlpha(0f);
        glowCircle.setScaleX(0f);
        glowCircle.setScaleY(0f);
        glowCircle.setAlpha(0f);

        // 1. Animate glow circle expanding
        AnimatorSet glowAnim = new AnimatorSet();
        glowAnim.playTogether(
            ObjectAnimator.ofFloat(glowCircle, "alpha", 0f, 0.6f).setDuration(800),
            ObjectAnimator.ofFloat(glowCircle, "scaleX", 0f, 1f).setDuration(800),
            ObjectAnimator.ofFloat(glowCircle, "scaleY", 0f, 1f).setDuration(800)
        );
        glowAnim.setInterpolator(new DecelerateInterpolator(2f));
        glowAnim.setStartDelay(100);
        glowAnim.start();

        // 2. Animate logo appearing with overshoot
        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(
            ObjectAnimator.ofFloat(logoContainer, "alpha", 0f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "scaleX", 0.5f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "scaleY", 0.5f, 1f).setDuration(700),
            ObjectAnimator.ofFloat(logoContainer, "translationY", 30f, 0f).setDuration(700)
        );
        logoAnim.setInterpolator(new OvershootInterpolator(1.5f));
        logoAnim.setStartDelay(250);
        logoAnim.start();

        // 3. Pulse glow animation
        new Handler().postDelayed(() -> {
            ObjectAnimator pulse = ObjectAnimator.ofFloat(glowCircle, "alpha", 0.6f, 0.3f, 0.6f);
            pulse.setDuration(1200);
            pulse.setRepeatCount(1);
            pulse.setInterpolator(new AccelerateDecelerateInterpolator());
            pulse.start();
        }, 900);

        // 4. Show loading indicator
        new Handler().postDelayed(() -> {
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(loadingContainer, "alpha", 0f, 1f);
            fadeIn.setDuration(400);
            fadeIn.start();
        }, 1000);

        // 5. Exit animation and launch MainActivity
        new Handler().postDelayed(() -> {
            AnimatorSet exitAnim = new AnimatorSet();
            exitAnim.playTogether(
                ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f).setDuration(350),
                ObjectAnimator.ofFloat(logoContainer, "scaleX", 1f, 1.08f).setDuration(350),
                ObjectAnimator.ofFloat(logoContainer, "scaleY", 1f, 1.08f).setDuration(350),
                ObjectAnimator.ofFloat(glowCircle, "alpha", 0.4f, 0f).setDuration(350),
                ObjectAnimator.ofFloat(loadingContainer, "alpha", 1f, 0f).setDuration(250)
            );
            exitAnim.setInterpolator(new DecelerateInterpolator());
            exitAnim.start();

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 380);
        }, 2600);
    }
}
