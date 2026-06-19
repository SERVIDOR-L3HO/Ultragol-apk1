package com.ultragol.app;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.ultragol.app.fragments.ContentFragment;
import com.ultragol.app.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Phone: BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            setupBottomNav(bottomNav);
        }

        // Tablet/TV: NavigationRailView
        NavigationRailView railNav = findViewById(R.id.railNav);
        if (railNav != null) {
            setupRailNav(railNav);
        }
    }

    private void setupBottomNav(BottomNavigationView nav) {
        nav.setOnItemSelectedListener(item -> {
            Fragment fragment = getFragmentForItem(item.getItemId());
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void setupRailNav(NavigationRailView nav) {
        nav.setOnItemSelectedListener(item -> {
            Fragment fragment = getFragmentForItem(item.getItemId());
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private Fragment getFragmentForItem(int itemId) {
        if (itemId == R.id.nav_home) return new HomeFragment();
        if (itemId == R.id.nav_movies) return ContentFragment.newInstance(ContentFragment.TYPE_MOVIES);
        if (itemId == R.id.nav_series) return ContentFragment.newInstance(ContentFragment.TYPE_SERIES);
        if (itemId == R.id.nav_sports) return ContentFragment.newInstance(ContentFragment.TYPE_SPORTS);
        if (itemId == R.id.nav_live) return ContentFragment.newInstance(ContentFragment.TYPE_LIVE_TV);
        return null;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
}
