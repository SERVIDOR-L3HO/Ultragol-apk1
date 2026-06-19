package com.ultragol.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ultragol.app.fragments.AnimeFragment;
import com.ultragol.app.fragments.HomeFragment;
import com.ultragol.app.fragments.MoviesFragment;
import com.ultragol.app.fragments.SeriesFragment;
import com.ultragol.app.fragments.SportsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) loadFragment(new HomeFragment());

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        if (nav != null) {
            nav.setOnItemSelectedListener(item -> {
                Fragment f = null;
                int id = item.getItemId();
                if (id == R.id.nav_home)    f = new HomeFragment();
                else if (id == R.id.nav_movies)  f = new MoviesFragment();
                else if (id == R.id.nav_series)  f = new SeriesFragment();
                else if (id == R.id.nav_anime)   f = new AnimeFragment();
                else if (id == R.id.nav_sports)  f = new SportsFragment();
                if (f != null) { loadFragment(f); return true; }
                return false;
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
}
