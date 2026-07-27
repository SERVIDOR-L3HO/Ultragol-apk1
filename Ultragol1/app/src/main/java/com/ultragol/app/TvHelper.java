package com.ultragol.app;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * TvHelper — detects the device category (TV, Tablet, Desktop, Mobile)
 * and provides utilities for focus/D-pad navigation.
 *
 * Supports: Android TV / Google TV (D-pad remote), Tablets (touch),
 *           Laptops/PCs (keyboard + mouse), Android phones (touch).
 */
public final class TvHelper {

    public enum DeviceType { TV, TABLET, DESKTOP, PHONE }

    private TvHelper() {}

    /** Returns true if running on an Android TV / Google TV (leanback). */
    public static boolean isTV(Context ctx) {
        UiModeManager uim = (UiModeManager) ctx.getSystemService(Context.UI_MODE_SERVICE);
        if (uim != null && uim.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }
        // Some TV boxes don't report UI_MODE_TYPE_TELEVISION but do have leanback feature
        return ctx.getPackageManager().hasSystemFeature("android.software.leanback");
    }

    /** Returns true if running on a tablet (sw >= 600dp). */
    public static boolean isTablet(Context ctx) {
        return ctx.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }

    /** Returns true if a physical keyboard is attached (laptop/desktop/PC). */
    public static boolean hasPhysicalKeyboard(Context ctx) {
        Configuration cfg = ctx.getResources().getConfiguration();
        return cfg.keyboard != Configuration.KEYBOARD_NOKEYS
                && cfg.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
    }

    /** Returns true if the primary input is a D-pad (TV remote or gamepad). */
    public static boolean hasDpadInput(Context ctx) {
        Configuration cfg = ctx.getResources().getConfiguration();
        return cfg.navigation == Configuration.NAVIGATION_DPAD
                || cfg.navigation == Configuration.NAVIGATION_TRACKBALL;
    }

    /** Classify the current device. */
    public static DeviceType getDeviceType(Context ctx) {
        if (isTV(ctx)) return DeviceType.TV;
        if (isTablet(ctx)) return DeviceType.TABLET;
        if (hasPhysicalKeyboard(ctx)) return DeviceType.DESKTOP;
        return DeviceType.PHONE;
    }

    /**
     * Makes every item that attaches to a RecyclerView focusable for D-pad / keyboard navigation.
     * Also applies a scale-up StateListAnimator so the focused card pops out visually on TV.
     *
     * Call this right after setting the adapter on any RecyclerView that should be navigable
     * with a remote control, keyboard, or mouse.
     */
    public static void makeFocusable(RecyclerView rv) {
        rv.setDescendantFocusability(RecyclerView.FOCUS_AFTER_DESCENDANTS);
        rv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                applyFocusToCard(view);
            }
            @Override public void onChildViewDetachedFromWindow(View view) {}
        });
    }

    /**
     * Applies TV-friendly focus behaviour to a single card view:
     *  • Makes it focusable (but not in touch mode, so touch still works normally)
     *  • Attaches a scale-up StateListAnimator on API 21+
     *  • Elevates the view on focus via an OnFocusChangeListener
     */
    public static void applyFocusToCard(View card) {
        if (card == null) return;
        card.setFocusable(true);
        card.setFocusableInTouchMode(false); // touch mode: finger taps still work

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Scale animator: focused card zooms to 108%, unfocused returns to 100%
            try {
                StateListAnimator sla = AnimatorInflater.loadStateListAnimator(
                        card.getContext(), R.animator.tv_card_focus);
                card.setStateListAnimator(sla);
            } catch (Exception ignored) {
                // Fallback: manual focus listener
            }
        }

        card.setOnFocusChangeListener((v, hasFocus) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                v.animate()
                        .scaleX(hasFocus ? 1.08f : 1f)
                        .scaleY(hasFocus ? 1.08f : 1f)
                        .setDuration(150)
                        .start();
            }
        });
    }

    /**
     * Returns the screen width in dp.
     */
    public static int getScreenWidthDp(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        return Math.round(dm.widthPixels / dm.density);
    }
}
