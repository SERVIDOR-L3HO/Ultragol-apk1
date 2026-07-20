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
import com.ultragol.app.fragments.*;

public class MainActivity extends AppCompatActivity {

    private FrameLayout drawerOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerOverlay = findViewById(R.id.drawerOverlay);

        checkAndShowCrash();
        requestNotificationPermission();

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
        View navTv        = drawerOverlay.findViewById(R.id.navTv);
        View navFavorites     = drawerOverlay.findViewById(R.id.navFavorites);
        View navMyList        = drawerOverlay.findViewById(R.id.navMyList);
        View navSwitchProfile = drawerOverlay.findViewById(R.id.navSwitchProfile);
        android.widget.TextView tvCurrentProfileName = drawerOverlay.findViewById(R.id.tvCurrentProfileName);

        if (navInicio    != null) navInicio.setOnClickListener(v    -> navigate(new HomeFragment()));
        if (navSeries    != null) navSeries.setOnClickListener(v    -> navigate(new SeriesFragment()));
        if (navMovies    != null) navMovies.setOnClickListener(v    -> navigate(new MoviesFragment()));
        if (navAnime     != null) navAnime.setOnClickListener(v     -> navigate(new AnimeFragment()));
        if (navDoramas   != null) navDoramas.setOnClickListener(v   -> navigate(new DoramasFragment()));
        if (navDeportes  != null) navDeportes.setOnClickListener(v  -> navigate(new DeportesWebFragment()));
        if (navTv        != null) navTv.setOnClickListener(v        -> navigate(new com.ultragol.app.fragments.TvFragment()));
        if (navFavorites != null) navFavorites.setOnClickListener(v -> navigate(new FavoritesFragment()));
        if (navMyList    != null) navMyList.setOnClickListener(v    -> navigate(new MyListFragment()));
        View navDownloads = drawerOverlay.findViewById(R.id.navDownloads);
        if (navDownloads != null) navDownloads.setOnClickListener(v -> navigate(new com.ultragol.app.fragments.DownloadsFragment()));

        // ── Adult section ──
        View navAdult = drawerOverlay.findViewById(R.id.navAdult);
        if (navAdult != null) navAdult.setOnClickListener(v -> {
            hideMenu();
            if (!AdultManager.isEnabled(this)) {
                Toast.makeText(this, "🔒 La sección Adultos está desactivada.\nActívala en Configuración.", Toast.LENGTH_LONG).show();
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
        if (navSearch    != null) navSearch.setOnClickListener(v    -> {
            hideMenu();
            startActivity(new Intent(this, DramaShortsActivity.class));
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
        // Refresh current profile name and apply kids filter
        ProfileManager.Profile p = ProfileManager.getCurrentProfile(this);
        android.widget.TextView tvPName = drawerOverlay.findViewById(R.id.tvCurrentProfileName);
        if (tvPName != null) tvPName.setText(p != null ? p.name : "Sin perfil activo");
        applyKidsDrawerFilter(p != null && p.isKids);
        drawerOverlay.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(200);
        drawerOverlay.startAnimation(anim);
    }

    /**
     * Show/hide nav items and platforms that are not appropriate for kids.
     * Hides: Animes, Doramas, Deportes, Crunchyroll, At-X, Tokyo MX.
     * Keeps: Inicio, Series, Películas, Buscar, Favoritos, Mi Lista, Netflix, Prime, Disney+, Apple TV+, Hulu, HBO Max.
     */
    private void applyKidsDrawerFilter(boolean isKids) {
        int kidsGone = isKids ? View.GONE : View.VISIBLE;
        setDrawerItemVisibility(R.id.navAnime,        kidsGone);
        setDrawerItemVisibility(R.id.navDoramas,      kidsGone);
        setDrawerItemVisibility(R.id.navDeportes,     kidsGone);
        setDrawerItemVisibility(R.id.navAdult,        View.GONE); // always hidden for kids
        setDrawerItemVisibility(R.id.platCrunchyroll, kidsGone);
        setDrawerItemVisibility(R.id.platAtx,         kidsGone);
        setDrawerItemVisibility(R.id.platTokyoMx,     kidsGone);
        // For adults: restore navAdult visibility based on setting
        if (!isKids) {
            setDrawerItemVisibility(R.id.navAdult, View.VISIBLE);
            // Update lock icon
            TextView lockIcon = drawerOverlay.findViewById(R.id.tvAdultLock);
            if (lockIcon != null) lockIcon.setText(AdultManager.isEnabled(this) ? "🔓" : "🔒");
        }
    }

    private void setDrawerItemVisibility(int viewId, int visibility) {
        View v = drawerOverlay.findViewById(viewId);
        if (v != null) v.setVisibility(visibility);
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
            return;
        }
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (current instanceof DeportesWebFragment) {
            if (((DeportesWebFragment) current).onBackPressed()) return;
        }
        // Pop non-home fragments; exits the app when backstack is empty (at Home)
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

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
        // Programar notificaciones automáticas inteligentes
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
            // Permiso concedido → lanzar notificación inmediata de bienvenida
            NotificationScheduler.runNow(this);
        }
    }

    // ── Adult PIN entry ───────────────────────────────────────────────────────

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

    /** Two-step PIN setup: enter → confirm. Calls onDone(pin) when confirmed. */
    private void showAdultPinSetupDialog(java.util.function.Consumer<String> onDone) {
        showAdultPinCapture("🔑 Establece tu contraseña (4 dígitos)", first ->
            showAdultPinCapture("🔑 Confirma tu contraseña", second -> {
                if (first.equals(second)) {
                    onDone.accept(first);
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
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
                dots[i].setBackgroundResource(i < entered.length() ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
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

    // ── Adult Settings dialog ─────────────────────────────────────────────────

    private void showAdultSettings() {
        boolean enabled = AdultManager.isEnabled(this);
        boolean hasPin  = AdultManager.hasPin(this);

        // Build a simple glass-styled dialog programmatically
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(20 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        // Title
        TextView title = new TextView(this);
        title.setText("🔥  SECCIÓN ADULTOS");
        title.setTextColor(0xFFFFCC77);
        title.setTextSize(16f);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, (int)(4 * getResources().getDisplayMetrics().density));
        root.addView(title);

        // Status line
        TextView status = new TextView(this);
        status.setText(enabled ? "Estado: ACTIVA ✓" : "Estado: DESACTIVADA");
        status.setTextColor(enabled ? 0xFF2ECC71 : 0x99FFFFFF);
        status.setTextSize(13f);
        status.setPadding(0, 0, 0, (int)(18 * getResources().getDisplayMetrics().density));
        root.addView(status);

        // Separator
        View sep = new View(this);
        LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        sepLp.bottomMargin = (int)(16 * getResources().getDisplayMetrics().density);
        sep.setLayoutParams(sepLp);
        sep.setBackgroundColor(0x22FFFFFF);
        root.addView(sep);

        AlertDialog[] dialogRef = {null};
        float dp = getResources().getDisplayMetrics().density;

        // Helper to add a button row
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int)(10 * dp);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(v -> { if (dialogRef[0] != null) dialogRef[0].dismiss(); action.run(); });
            root.addView(btn);
        };

        if (!enabled) {
            // --- ACTIVAR ---
            addBtn.accept("🔓  Activar sección Adultos", () -> {
                if (!hasPin) {
                    // First time: set PIN then enable
                    showAdultPinSetupDialog(pin -> {
                        AdultManager.setPin(this, pin);
                        AdultManager.setEnabled(this, true);
                        Toast.makeText(this, "✅ Sección Adultos activada.", Toast.LENGTH_SHORT).show();
                        refreshAdultLockIcon();
                    });
                } else {
                    // PIN already set: just verify before enabling
                    showAdultPinDialog(() -> {
                        AdultManager.setEnabled(this, true);
                        Toast.makeText(this, "✅ Sección Adultos activada.", Toast.LENGTH_SHORT).show();
                        refreshAdultLockIcon();
                    });
                }
            });
        } else {
            // --- DESACTIVAR ---
            addBtn.accept("🔒  Desactivar sección Adultos", () ->
                showAdultPinDialog(() -> {
                    AdultManager.setEnabled(this, false);
                    Toast.makeText(this, "Sección Adultos desactivada.", Toast.LENGTH_SHORT).show();
                    refreshAdultLockIcon();
                })
            );
        }

        // --- CAMBIAR CONTRASEÑA ---
        addBtn.accept("🔑  " + (hasPin ? "Cambiar contraseña" : "Establecer contraseña"), () -> {
            Runnable doSetup = () -> showAdultPinSetupDialog(pin -> {
                AdultManager.setPin(this, pin);
                Toast.makeText(this, "✅ Contraseña guardada.", Toast.LENGTH_SHORT).show();
            });
            if (hasPin) {
                // Must verify old PIN first
                showAdultPinDialog(doSetup::run);
            } else {
                doSetup.run();
            }
        });

        // --- CANCEL ---
        TextView cancel = new TextView(this);
        cancel.setText("Cancelar");
        cancel.setTextColor(0x77FFFFFF);
        cancel.setTextSize(13f);
        cancel.setGravity(android.view.Gravity.CENTER);
        cancel.setPadding(0, (int)(12*dp), 0, 0);
        cancel.setOnClickListener(v -> { if (dialogRef[0] != null) dialogRef[0].dismiss(); });
        root.addView(cancel);

        AlertDialog d = new AlertDialog.Builder(this, R.style.UpdateDialogTheme)
            .setView(root).setCancelable(true).create();
        dialogRef[0] = d;
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        d.show();
        if (d.getWindow() != null) {
            int sw = getResources().getDisplayMetrics().widthPixels;
            int mg = (int)(24 * getResources().getDisplayMetrics().density);
            d.getWindow().setLayout(sw - mg * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void refreshAdultLockIcon() {
        if (drawerOverlay == null) return;
        TextView lockIcon = drawerOverlay.findViewById(R.id.tvAdultLock);
        if (lockIcon != null) lockIcon.setText(AdultManager.isEnabled(this) ? "🔓" : "🔒");
    }

    private void loadFragment(Fragment fragment) {
        boolean isHome = fragment instanceof HomeFragment;
        FragmentManager fm = getSupportFragmentManager();
        if (isHome) {
            // Clear the full backstack when navigating home via the drawer
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction tx = fm.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment);
        if (!isHome) tx.addToBackStack(null);
        tx.commit();
    }
}
