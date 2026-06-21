package com.ultragol.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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

        checkAndShowCrash();

        if (savedInstanceState == null) loadFragment(new HomeFragment());

        setupDrawer();
    }

    private void setupDrawer() {
        View close = drawerOverlay.findViewById(R.id.drawerClose);
        if (close != null) close.setOnClickListener(v -> hideMenu());

        View search = drawerOverlay.findViewById(R.id.drawerSearch);
        if (search != null) search.setOnClickListener(v -> {
            hideMenu();
            startActivity(new Intent(this, SearchActivity.class));
        });

        // ── Nav items ──
        View navInicio    = drawerOverlay.findViewById(R.id.navInicio);
        View navSeries    = drawerOverlay.findViewById(R.id.navSeries);
        View navMovies    = drawerOverlay.findViewById(R.id.navMovies);
        View navAnime     = drawerOverlay.findViewById(R.id.navAnime);
        View navDoramas   = drawerOverlay.findViewById(R.id.navDoramas);
        View navSearch    = drawerOverlay.findViewById(R.id.navSearch);
        View navDeportes  = drawerOverlay.findViewById(R.id.navDeportes);
        View navFavorites = drawerOverlay.findViewById(R.id.navFavorites);
        View navMyList    = drawerOverlay.findViewById(R.id.navMyList);

        if (navInicio    != null) navInicio.setOnClickListener(v    -> navigate(new HomeFragment()));
        if (navSeries    != null) navSeries.setOnClickListener(v    -> navigate(new SeriesFragment()));
        if (navMovies    != null) navMovies.setOnClickListener(v    -> navigate(new MoviesFragment()));
        if (navAnime     != null) navAnime.setOnClickListener(v     -> navigate(new AnimeFragment()));
        if (navDoramas   != null) navDoramas.setOnClickListener(v   -> navigate(new DoramasFragment()));
        if (navDeportes  != null) navDeportes.setOnClickListener(v  -> navigate(new FootballFragment()));
        if (navFavorites != null) navFavorites.setOnClickListener(v -> navigate(new FavoritesFragment()));
        if (navMyList    != null) navMyList.setOnClickListener(v    -> navigate(new MyListFragment()));
        if (navSearch    != null) navSearch.setOnClickListener(v    -> {
            hideMenu();
            startActivity(new Intent(this, SearchActivity.class));
        });

        // ── Plataformas ──
        // TMDB watch provider IDs (region MX)
        // Netflix=8, Prime Video=119, Disney+=337, Apple TV+=350,
        // Hulu=15, HBO Max=1899, Crunchyroll=283, At-X=1408, Tokyo MX=absent→use anime
        View platNetflix    = drawerOverlay.findViewById(R.id.platNetflix);
        View platPrime      = drawerOverlay.findViewById(R.id.platPrime);
        View platDisney     = drawerOverlay.findViewById(R.id.platDisney);
        View platApple      = drawerOverlay.findViewById(R.id.platApple);
        View platHulu       = drawerOverlay.findViewById(R.id.platHulu);
        View platHbo        = drawerOverlay.findViewById(R.id.platHbo);
        View platCrunchyroll= drawerOverlay.findViewById(R.id.platCrunchyroll);
        View platAtx        = drawerOverlay.findViewById(R.id.platAtx);
        View platTokyoMx    = drawerOverlay.findViewById(R.id.platTokyoMx);

        if (platNetflix != null)
            platNetflix.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("🔴 Netflix", 8, "all")));
        if (platPrime != null)
            platPrime.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("🔵 Prime Video", 119, "all")));
        if (platDisney != null)
            platDisney.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("🔷 Disney+", 337, "all")));
        if (platApple != null)
            platApple.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("⬜ Apple TV+", 350, "all")));
        if (platHulu != null)
            platHulu.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("🟢 Hulu", 15, "all")));
        if (platHbo != null)
            platHbo.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("🟣 HBO Max", 1899, "all")));
        if (platCrunchyroll != null)
            platCrunchyroll.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("🟠 Crunchyroll", 283, "anime")));
        if (platAtx != null)
            platAtx.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("⬜ At-X", 1408, "anime")));
        if (platTokyoMx != null)
            platTokyoMx.setOnClickListener(v ->
                navigate(PlatformFragment.newInstance("📺 Tokyo MX", 2359, "anime")));
    }

    private void checkAndShowCrash() {
        String crash = UltragolApp.getLastCrash(this);
        if (crash == null) return;
        UltragolApp.clearCrash(this);
        TextView tv = new TextView(this);
        tv.setText(crash);
        tv.setTextSize(11);
        tv.setPadding(16, 16, 16, 16);
        tv.setTextIsSelectable(true);
        ScrollView sv = new ScrollView(this);
        sv.addView(tv);
        new AlertDialog.Builder(this)
            .setTitle("CRASH DETECTADO")
            .setView(sv)
            .setPositiveButton("OK", null)
            .show();
    }

    public void navigate(Fragment fragment) {
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
