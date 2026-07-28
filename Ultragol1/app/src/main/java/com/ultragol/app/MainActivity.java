package com.ultragol.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.ultragol.app.fragments.*;

/**
 * MainActivity — hosts the side-navigation drawer and the main fragment container.
 *
 * Responsive behaviour:
 *   • Phone  → Floating overlay drawer (hamburger menu). Portrait orientation.
 *   • Tablet → Same overlay drawer but landscape allowed.
 *   • TV     → Persistent side rail (always visible). Full D-pad + remote support.
 *   • PC/Laptop → Keyboard shortcuts (Escape = back, Ctrl+F = search, ← = focus nav).
 */
public class MainActivity extends AppCompatActivity {

    // The overlay FrameLayout that wraps all nav items.
    // On TV (layout-television) this is a fixed 240dp panel always visible;
    // on phone/tablet it is a full-screen overlay shown/hidden on demand.
    private FrameLayout drawerOverlay;

    /** True when running on an Android TV / Google TV (leanback). */
    private boolean isTV;

    /** True when the nav panel is currently visible (TV: always true). */
    private boolean menuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // Android picks layout-television/ on TV

        isTV = TvHelper.isTV(this);

        drawerOverlay = findViewById(R.id.drawerOverlay);

        if (isTV && drawerOverlay != null) {
            // On TV the panel is always visible — remove any GONE state the layout might set
            drawerOverlay.setVisibility(View.VISIBLE);
            menuVisible = true;
        }

        checkAndShowCrash();
        requestNotificationPermission();

        if (savedInstanceState == null) loadFragment(new HomeFragment());

        setupDrawer();

        // On TV, give initial focus to the first nav item so the remote works immediately
        if (isTV) {
            drawerOverlay.post(() -> {
                View first = drawerOverlay.findViewById(R.id.navInicio);
                if (first != null) first.requestFocus();
            });
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onStop() {
        super.onStop();
        // Guardar todos los datos locales en Firestore cuando la app va al fondo
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserSyncManager.pushToCloud(getApplicationContext(), user.getUid());
        }
    }

    // ── D-pad / Keyboard navigation ──────────────────────────────────────────

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Only act on ACTION_DOWN to avoid double-firing
        if (event.getAction() != KeyEvent.ACTION_DOWN) return super.dispatchKeyEvent(event);

        int kc = event.getKeyCode();

        // ── Universal keyboard shortcuts (all device types) ──────────────────

        // Escape → back (useful on PC/laptop)
        if (kc == KeyEvent.KEYCODE_ESCAPE) {
            onBackPressed();
            return true;
        }

        // Ctrl+F or Search key → open search
        if (kc == KeyEvent.KEYCODE_SEARCH
                || (kc == KeyEvent.KEYCODE_F && event.isCtrlPressed())) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        // ── TV-specific D-pad routing ────────────────────────────────────────

        if (isTV) {
            // MENU or DPAD_LEFT from content area → move focus to nav rail
            if (kc == KeyEvent.KEYCODE_MENU) {
                focusNavRail();
                return true;
            }

            // DPAD_LEFT: if focus is already in the content area → move it to nav rail
            if (kc == KeyEvent.KEYCODE_DPAD_LEFT) {
                View focused = getCurrentFocus();
                if (focused != null && !isInsideNavRail(focused)) {
                    focusNavRail();
                    return true;
                }
                // If already in nav rail, let Android handle (move focus within nav)
            }

            // DPAD_RIGHT: if focus is inside nav rail → move it to content area
            if (kc == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View focused = getCurrentFocus();
                if (focused != null && isInsideNavRail(focused)) {
                    View contentArea = findViewById(R.id.fragmentContainer);
                    if (contentArea != null) {
                        View next = contentArea.focusSearch(View.FOCUS_RIGHT);
                        if (next != null) next.requestFocus();
                        else contentArea.requestFocus();
                    }
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    /** True if the given view is a descendant of the nav rail (drawerOverlay). */
    private boolean isInsideNavRail(View v) {
        if (drawerOverlay == null) return false;
        View current = v;
        while (current != null) {
            if (current == drawerOverlay) return true;
            if (!(current.getParent() instanceof View)) break;
            current = (View) current.getParent();
        }
        return false;
    }

    /** Moves D-pad focus to the first item in the nav rail. */
    private void focusNavRail() {
        if (drawerOverlay == null) return;
        View first = drawerOverlay.findViewById(R.id.navInicio);
        if (first != null) first.requestFocus();
    }

    // ── Drawer / Nav rail setup ──────────────────────────────────────────────

    private void setupDrawer() {
        if (drawerOverlay == null) return;

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
        View navMusica    = drawerOverlay.findViewById(R.id.navMusica);
        View navTv        = drawerOverlay.findViewById(R.id.navTv);
        View navFavorites     = drawerOverlay.findViewById(R.id.navFavorites);
        View navMyList        = drawerOverlay.findViewById(R.id.navMyList);
        View navSwitchProfile = drawerOverlay.findViewById(R.id.navSwitchProfile);
        android.widget.TextView tvCurrentProfileName =
                drawerOverlay.findViewById(R.id.tvCurrentProfileName);

        if (navInicio    != null) navInicio.setOnClickListener(v    -> navigate(new HomeFragment()));
        if (navSeries    != null) navSeries.setOnClickListener(v    -> navigate(new SeriesFragment()));
        if (navMovies    != null) navMovies.setOnClickListener(v    -> navigate(new MoviesFragment()));
        if (navAnime     != null) navAnime.setOnClickListener(v     -> navigate(new AnimeFragment()));
        if (navDoramas   != null) navDoramas.setOnClickListener(v   -> navigate(new DoramasFragment()));
        if (navDeportes  != null) navDeportes.setOnClickListener(v  -> navigate(new DeportesWebFragment()));
        if (navMusica    != null) navMusica.setOnClickListener(v    -> navigate(new MusicaWebFragment()));
        if (navTv        != null) navTv.setOnClickListener(v        -> navigate(new com.ultragol.app.fragments.TvFragment()));
        if (navFavorites != null) navFavorites.setOnClickListener(v -> navigate(new FavoritesFragment()));
        if (navMyList    != null) navMyList.setOnClickListener(v    -> navigate(new MyListFragment()));

        View navDownloads = drawerOverlay.findViewById(R.id.navDownloads);
        if (navDownloads != null) navDownloads.setOnClickListener(v ->
                navigate(new com.ultragol.app.fragments.DownloadsFragment()));

        // ── Adult section ──
        View navAdult = drawerOverlay.findViewById(R.id.navAdult);
        if (navAdult != null) navAdult.setOnClickListener(v -> {
            hideMenu();
            if (!AdultManager.isEnabled(this)) {
                Toast.makeText(this, "🔒 La sección Adultos está desactivada.\nActívala en Configuración.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (!AdultManager.hasPin(this)) {
                navigate(new AdultFragment());
                return;
            }
            showAdultPinDialog(() -> navigate(new AdultFragment()));
        });

        // ── Settings ──
        View navSettings = drawerOverlay.findViewById(R.id.navSettings);
        if (navSettings != null) navSettings.setOnClickListener(v -> {
            hideMenu();
            showAdultSettings();
        });

        if (navSwitchProfile != null) navSwitchProfile.setOnClickListener(v -> {
            hideMenu();
            startActivity(new Intent(this, ProfileSelectorActivity.class));
        });
        if (navSearch != null) navSearch.setOnClickListener(v -> {
            hideMenu();
            startActivity(new Intent(this, DramaShortsActivity.class));
        });

        // ── Platforms ──
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
            platNetflix.setOnClickListener(v -> navigate(PlatformFragment.newInstance("🔴 Netflix", 8, "all")));
        if (platPrime != null)
            platPrime.setOnClickListener(v -> navigate(PlatformFragment.newInstance("🔵 Prime Video", 119, "all")));
        if (platDisney != null)
            platDisney.setOnClickListener(v -> navigate(PlatformFragment.newInstance("🔷 Disney+", 337, "all")));
        if (platApple != null)
            platApple.setOnClickListener(v -> navigate(PlatformFragment.newInstance("⬜ Apple TV+", 350, "all")));
        if (platHulu != null)
            platHulu.setOnClickListener(v -> navigate(PlatformFragment.newInstance("🟢 Hulu", 15, "all")));
        if (platHbo != null)
            platHbo.setOnClickListener(v -> navigate(PlatformFragment.newInstance("🟣 HBO Max", 1899, "all")));
        if (platCrunchyroll != null)
            platCrunchyroll.setOnClickListener(v -> navigate(PlatformFragment.newInstance("🟠 Crunchyroll", 283, "anime")));
        if (platAtx != null)
            platAtx.setOnClickListener(v -> navigate(PlatformFragment.newInstance("⬜ At-X", 1408, "anime")));
        if (platTokyoMx != null)
            platTokyoMx.setOnClickListener(v -> navigate(PlatformFragment.newInstance("📺 Tokyo MX", 2359, "anime")));
    }

    // ── Navigation helpers ───────────────────────────────────────────────────

    /** Navigate to a fragment. On TV the nav rail stays open; on phone the drawer closes. */
    public void navigate(Fragment fragment) {
        hideMenu();
        loadFragment(fragment);
        // On TV: return focus to the content area after navigation
        if (isTV) {
            View contentArea = findViewById(R.id.fragmentContainer);
            if (contentArea != null) contentArea.post(contentArea::requestFocus);
        }
    }

    /** Show the navigation panel (overlay on phone; no-op on TV — always visible). */
    public void showMenu() {
        if (isTV) {
            // Nav rail is always visible; just move focus into it
            focusNavRail();
            return;
        }
        if (drawerOverlay == null) return;
        // Refresh current profile name and apply kids filter
        ProfileManager.Profile p = ProfileManager.getCurrentProfile(this);
        android.widget.TextView tvPName = drawerOverlay.findViewById(R.id.tvCurrentProfileName);
        if (tvPName != null) tvPName.setText(p != null ? p.name : "Sin perfil activo");
        applyKidsDrawerFilter(p != null && p.isKids);
        drawerOverlay.setVisibility(View.VISIBLE);
        menuVisible = true;
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(200);
        drawerOverlay.startAnimation(anim);
    }

    /** Hide the navigation panel (overlay on phone; no-op on TV). */
    public void hideMenu() {
        if (isTV) return;  // Nav rail is permanent on TV — never hide it
        if (drawerOverlay == null || !menuVisible) return;
        menuVisible = false;
        AlphaAnimation anim = new AlphaAnimation(1f, 0f);
        anim.setDuration(150);
        anim.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override public void onAnimationStart(android.view.animation.Animation a) {}
            @Override public void onAnimationRepeat(android.view.animation.Animation a) {}
            @Override public void onAnimationEnd(android.view.animation.Animation a) {
                if (drawerOverlay != null) drawerOverlay.setVisibility(View.GONE);
            }
        });
        drawerOverlay.startAnimation(anim);
    }

    // ── Back handling ────────────────────────────────────────────────────────

    @Override
    public void onBackPressed() {
        if (!isTV && drawerOverlay != null && drawerOverlay.getVisibility() == View.VISIBLE) {
            hideMenu();
            return;
        }
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (current instanceof DeportesWebFragment) {
            if (((DeportesWebFragment) current).onBackPressed()) return;
        }
        if (current instanceof MusicaWebFragment) {
            if (((MusicaWebFragment) current).onBackPressed()) return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            // On TV after popping back: return focus to nav rail
            if (isTV) focusNavRail();
            return;
        }
        super.onBackPressed();
    }

    // ── Kids drawer filter ───────────────────────────────────────────────────

    private void applyKidsDrawerFilter(boolean isKids) {
        int kidsGone = isKids ? View.GONE : View.VISIBLE;
        setDrawerItemVisibility(R.id.navAnime,        kidsGone);
        setDrawerItemVisibility(R.id.navDoramas,      kidsGone);
        setDrawerItemVisibility(R.id.navDeportes,     kidsGone);
        setDrawerItemVisibility(R.id.navAdult,        View.GONE);
        setDrawerItemVisibility(R.id.platCrunchyroll, kidsGone);
        setDrawerItemVisibility(R.id.platAtx,         kidsGone);
        setDrawerItemVisibility(R.id.platTokyoMx,     kidsGone);
        if (!isKids) {
            setDrawerItemVisibility(R.id.navAdult, View.VISIBLE);
            TextView lockIcon = drawerOverlay.findViewById(R.id.tvAdultLock);
            if (lockIcon != null) lockIcon.setText(AdultManager.isEnabled(this) ? "🔓" : "🔒");
        }
    }

    private void setDrawerItemVisibility(int viewId, int visibility) {
        if (drawerOverlay == null) return;
        View v = drawerOverlay.findViewById(viewId);
        if (v != null) v.setVisibility(visibility);
    }

    private void refreshAdultLockIcon() {
        if (drawerOverlay == null) return;
        TextView lockIcon = drawerOverlay.findViewById(R.id.tvAdultLock);
        if (lockIcon != null) lockIcon.setText(AdultManager.isEnabled(this) ? "🔓" : "🔒");
    }

    // ── Fragment loading ─────────────────────────────────────────────────────

    private void loadFragment(Fragment fragment) {
        boolean isHome = fragment instanceof HomeFragment;
        FragmentManager fm = getSupportFragmentManager();
        if (isHome) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction tx = fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment);
        if (!isHome) tx.addToBackStack(null);
        tx.commit();
    }

    // ── Permission ───────────────────────────────────────────────────────────

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return;
            }
        }
        NotificationChecker.check(this);
        NotificationScheduler.schedule(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @androidx.annotation.NonNull String[] permissions,
            @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            NotificationChecker.check(this);
            NotificationScheduler.runNow(this);
        }
    }

    // ── Crash reporter ───────────────────────────────────────────────────────

    private void checkAndShowCrash() {
        String crash = UltragolApp.getLastCrash(this);
        if (crash == null) return;
        UltragolApp.clearCrash(this);
        TextView tv = new TextView(this);
        tv.setText(crash);
        tv.setTextSize(11);
        tv.setPadding(16, 16, 16, 16);
        tv.setTextIsSelectable(true);
        android.widget.ScrollView sv = new android.widget.ScrollView(this);
        sv.addView(tv);
        new AlertDialog.Builder(this)
                .setTitle("CRASH DETECTADO")
                .setView(sv)
                .setPositiveButton("OK", null)
                .show();
    }

    // ── Adult PIN entry ──────────────────────────────────────────────────────

    private void showAdultPinDialog(Runnable onSuccess) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pin_entry, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
                .setView(v).setCancelable(true).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvName = v.findViewById(R.id.pinProfileName);
        if (tvName != null) tvName.setText("🔥 Sección Adultos — Ingresa tu contraseña");

        View[] dots = {
            v.findViewById(R.id.dot1), v.findViewById(R.id.dot2),
            v.findViewById(R.id.dot3), v.findViewById(R.id.dot4)
        };
        TextView tvError = v.findViewById(R.id.tvPinError);
        StringBuilder entered = new StringBuilder();

        Runnable updateDots = () -> {
            for (int i = 0; i < 4; i++) {
                dots[i].setBackgroundResource(
                    i < entered.length() ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
            }
        };
        int[] keyIds = {R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                        R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9};
        for (int d = 0; d <= 9; d++) {
            final int digit = d;
            v.findViewById(keyIds[d]).setOnClickListener(btn -> {
                if (entered.length() >= 4) return;
                entered.append(digit);
                updateDots.run();
                if (tvError != null) tvError.setVisibility(View.INVISIBLE);
                if (entered.length() == 4) {
                    if (AdultManager.checkPin(this, entered.toString())) {
                        dialog.dismiss();
                        onSuccess.run();
                    } else {
                        if (tvError != null) tvError.setVisibility(View.VISIBLE);
                        entered.setLength(0);
                        updateDots.run();
                    }
                }
            });
        }
        View keyDel = v.findViewById(R.id.keyDel);
        if (keyDel != null) keyDel.setOnClickListener(btn -> {
            if (entered.length() > 0) { entered.deleteCharAt(entered.length() - 1); updateDots.run(); }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            int sw = getResources().getDisplayMetrics().widthPixels;
            int mg = (int)(20 * getResources().getDisplayMetrics().density);
            dialog.getWindow().setLayout(sw - mg * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showAdultPinSetupDialog(java.util.function.Consumer<String> onDone) {
        showAdultPinCapture("🔑 Establece tu contraseña (4 dígitos)", first ->
            showAdultPinCapture("🔑 Confirma tu contraseña", second -> {
                if (first.equals(second)) {
                    onDone.accept(first);
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden. Inténtalo de nuevo.",
                            Toast.LENGTH_SHORT).show();
                    showAdultPinSetupDialog(onDone);
                }
            })
        );
    }

    private void showAdultPinCapture(String title, java.util.function.Consumer<String> onCaptured) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pin_entry, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
                .setView(v).setCancelable(true).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvName = v.findViewById(R.id.pinProfileName);
        if (tvName != null) tvName.setText(title);

        View[] dots = {
            v.findViewById(R.id.dot1), v.findViewById(R.id.dot2),
            v.findViewById(R.id.dot3), v.findViewById(R.id.dot4)
        };
        StringBuilder entered = new StringBuilder();
        Runnable updateDots = () -> {
            for (int i = 0; i < 4; i++)
                dots[i].setBackgroundResource(i < entered.length()
                        ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
        };
        int[] keyIds = {R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                        R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9};
        for (int d = 0; d <= 9; d++) {
            final int digit = d;
            v.findViewById(keyIds[d]).setOnClickListener(btn -> {
                if (entered.length() >= 4) return;
                entered.append(digit);
                updateDots.run();
                if (entered.length() == 4) { dialog.dismiss(); onCaptured.accept(entered.toString()); }
            });
        }
        View keyDel = v.findViewById(R.id.keyDel);
        if (keyDel != null) keyDel.setOnClickListener(btn -> {
            if (entered.length() > 0) { entered.deleteCharAt(entered.length() - 1); updateDots.run(); }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            int sw = getResources().getDisplayMetrics().widthPixels;
            int mg = (int)(20 * getResources().getDisplayMetrics().density);
            dialog.getWindow().setLayout(sw - mg * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // ── Adult Settings dialog ────────────────────────────────────────────────

    private void showAdultSettings() {
        boolean enabled = AdultManager.isEnabled(this);
        boolean hasPin  = AdultManager.hasPin(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(20 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("🔥  SECCIÓN ADULTOS");
        title.setTextColor(0xFFFFCC77);
        title.setTextSize(16f);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, (int)(4 * getResources().getDisplayMetrics().density));
        root.addView(title);

        TextView status = new TextView(this);
        status.setText(enabled ? "Estado: ACTIVA ✓" : "Estado: DESACTIVADA");
        status.setTextColor(enabled ? 0xFF2ECC71 : 0x99FFFFFF);
        status.setTextSize(13f);
        status.setPadding(0, 0, 0, (int)(18 * getResources().getDisplayMetrics().density));
        root.addView(status);

        View sep = new View(this);
        LinearLayout.LayoutParams sepLp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        sepLp.bottomMargin = (int)(16 * getResources().getDisplayMetrics().density);
        sep.setLayoutParams(sepLp);
        sep.setBackgroundColor(0x22FFFFFF);
        root.addView(sep);

        AlertDialog[] dialogRef = {null};
        float dp = getResources().getDisplayMetrics().density;

        java.util.function.BiConsumer<String, Runnable> addBtn = (label, action) -> {
            TextView btn = new TextView(this);
            btn.setText(label);
            btn.setTextColor(0xEEFFFFFF);
            btn.setTextSize(14f);
            btn.setPadding((int)(14*dp), (int)(14*dp), (int)(14*dp), (int)(14*dp));
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(10 * dp);
            bg.setColor(0x18FFFFFF);
            btn.setBackground(bg);
            btn.setFocusable(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int)(10 * dp);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(v -> { if (dialogRef[0] != null) dialogRef[0].dismiss(); action.run(); });
            root.addView(btn);
        };

        if (!enabled) {
            addBtn.accept("🔓  Activar sección Adultos", () -> {
                if (!hasPin) {
                    showAdultPinSetupDialog(pin -> {
                        AdultManager.setPin(this, pin);
                        AdultManager.setEnabled(this, true);
                        Toast.makeText(this, "✅ Sección Adultos activada.", Toast.LENGTH_SHORT).show();
                        refreshAdultLockIcon();
                    });
                } else {
                    showAdultPinDialog(() -> {
                        AdultManager.setEnabled(this, true);
                        Toast.makeText(this, "✅ Sección Adultos activada.", Toast.LENGTH_SHORT).show();
                        refreshAdultLockIcon();
                    });
                }
            });
        } else {
            addBtn.accept("🔒  Desactivar sección Adultos", () ->
                showAdultPinDialog(() -> {
                    AdultManager.setEnabled(this, false);
                    Toast.makeText(this, "Sección Adultos desactivada.", Toast.LENGTH_SHORT).show();
                    refreshAdultLockIcon();
                })
            );
        }

        addBtn.accept("🔑  " + (hasPin ? "Cambiar contraseña" : "Establecer contraseña"), () -> {
            Runnable doSetup = () -> showAdultPinSetupDialog(pin -> {
                AdultManager.setPin(this, pin);
                Toast.makeText(this, "✅ Contraseña guardada.", Toast.LENGTH_SHORT).show();
            });
            if (hasPin) showAdultPinDialog(doSetup::run); else doSetup.run();
        });

        TextView cancel = new TextView(this);
        cancel.setText("Cancelar");
        cancel.setTextColor(0x77FFFFFF);
        cancel.setTextSize(13f);
        cancel.setGravity(android.view.Gravity.CENTER);
        cancel.setPadding(0, (int)(12*dp), 0, 0);
        cancel.setFocusable(true);
        cancel.setOnClickListener(v -> { if (dialogRef[0] != null) dialogRef[0].dismiss(); });
        root.addView(cancel);

        AlertDialog d = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
                .setView(root).setCancelable(true).create();
        dialogRef[0] = d;
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
        if (d.getWindow() != null) {
            int sw = getResources().getDisplayMetrics().widthPixels;
            int mg = (int)(24 * getResources().getDisplayMetrics().density);
            d.getWindow().setLayout(sw - mg * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
