package com.ultragol.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.ultragol.app.fragments.*;

public class MainActivity extends AppCompatActivity {

    private FrameLayout drawerOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerOverlay = findViewById(R.id.drawerOverlay);

        if (savedInstanceState == null) loadFragment(new HomeFragment());

        setupDrawer();
    }

    private void setupDrawer() {
        // Close button
        View close = drawerOverlay.findViewById(R.id.drawerClose);
        if (close != null) close.setOnClickListener(v -> hideMenu());

        // Search inside drawer
        View search = drawerOverlay.findViewById(R.id.drawerSearch);
        if (search != null) search.setOnClickListener(v -> {
            hideMenu();
            startActivity(new Intent(this, SearchActivity.class));
        });

        // Nav items
        View navInicio  = drawerOverlay.findViewById(R.id.navInicio);
        View navSeries  = drawerOverlay.findViewById(R.id.navSeries);
        View navMovies  = drawerOverlay.findViewById(R.id.navMovies);
        View navAnime   = drawerOverlay.findViewById(R.id.navAnime);
        View navDoramas = drawerOverlay.findViewById(R.id.navDoramas);
        View navSearch  = drawerOverlay.findViewById(R.id.navSearch);

        if (navInicio  != null) navInicio.setOnClickListener(v  -> navigate(new HomeFragment()));
        if (navSeries  != null) navSeries.setOnClickListener(v  -> navigate(new SeriesFragment()));
        if (navMovies  != null) navMovies.setOnClickListener(v  -> navigate(new MoviesFragment()));
        if (navAnime   != null) navAnime.setOnClickListener(v   -> navigate(new AnimeFragment()));
        if (navDoramas != null) navDoramas.setOnClickListener(v -> navigate(new SportsFragment()));
        if (navSearch  != null) navSearch.setOnClickListener(v  -> {
            hideMenu();
            startActivity(new Intent(this, SearchActivity.class));
        });
    }

    private void navigate(Fragment fragment) {
        hideMenu();
        loadFragment(fragment);
    }

    public void showMenu() {
        if (drawerOverlay == null) return;
        drawerOverlay.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(200);
        drawerOverlay.startAnimation(anim);
    }

    public void hideMenu() {
        if (drawerOverlay == null) return;
        AlphaAnimation anim = new AlphaAnimation(1f, 0f);
        anim.setDuration(150);
        anim.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override public void onAnimationStart(android.view.animation.Animation a) {}
            @Override public void onAnimationRepeat(android.view.animation.Animation a) {}
            @Override public void onAnimationEnd(android.view.animation.Animation a) {
                drawerOverlay.setVisibility(View.GONE);
            }
        });
        drawerOverlay.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        if (drawerOverlay != null && drawerOverlay.getVisibility() == View.VISIBLE) {
            hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
}
