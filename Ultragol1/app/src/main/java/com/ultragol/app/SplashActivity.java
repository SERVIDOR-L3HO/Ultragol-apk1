package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.splashLogo);
        View tagline = findViewById(R.id.splashTagline);
        View progress = findViewById(R.id.splashProgress);

        // Animate logo
        AnimationSet anim = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(600);
        AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        alpha.setDuration(600);
        anim.addAnimation(scale);
        anim.addAnimation(alpha);
        if (logo != null) logo.startAnimation(anim);

        new Handler().postDelayed(() -> {
            if (tagline != null) {
                AlphaAnimation a2 = new AlphaAnimation(0f, 1f);
                a2.setDuration(400);
                tagline.setVisibility(View.VISIBLE);
                tagline.startAnimation(a2);
            }
        }, 500);

        new Handler().postDelayed(() -> {
            if (progress != null) progress.setVisibility(View.VISIBLE);
        }, 800);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2200);
    }
}
