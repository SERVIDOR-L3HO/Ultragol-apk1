package com.ultragol.app;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
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
        applyFocusRing(card);

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

        // Mouse hover (PC / Chromebook with a pointer device): same zoom-out feedback as
        // D-pad focus, but doesn't steal keyboard focus — purely visual on ACTION_HOVER_*.
        card.setOnHoverListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_HOVER_ENTER:
                    v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start();
                    break;
                case android.view.MotionEvent.ACTION_HOVER_EXIT:
                    if (!v.isFocused()) {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    }
                    break;
            }
            return false; // never consume: clicks/taps must keep working
        });
    }

    /**
     * Applies the same visible focus treatment to every interactive control in
     * an Activity. This is intentionally tree-based because LoginActivity and
     * several dialogs build their buttons in Java instead of XML.
     */
    public static void installFocusFeedback(View root) {
        if (root == null) return;
        applyToInteractiveView(root);
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                installFocusFeedback(group.getChildAt(i));
            }
        }
    }

    /** Finds the first interactive child in document order for TV startup focus. */
    public static View findFirstFocusable(View root) {
        if (root == null) return null;
        if (root.isFocusable() && root.getVisibility() == View.VISIBLE
                && root.isEnabled()) return root;
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View result = findFirstFocusable(group.getChildAt(i));
                if (result != null) return result;
            }
        }
        return null;
    }

    /** Applies TV focus to a dialog window and starts on its first control. */
    public static void prepareDialog(Dialog dialog) {
        if (dialog == null || !isTV(dialog.getContext()) || dialog.getWindow() == null) return;
        View root = dialog.getWindow().getDecorView();
        installFocusFeedback(root);
        root.post(() -> {
            if (root.findFocus() == null) {
                View first = findFirstFocusable(root);
                if (first != null) first.requestFocus();
            }
        });
    }

    private static void applyToInteractiveView(View view) {
        if (view.isClickable() || view.isFocusable()) {
            applyFocusRing(view);
            view.setFocusableInTouchMode(false);
            if (view.getOnFocusChangeListener() == null) {
                view.setOnFocusChangeListener((v, hasFocus) -> animateFocus(v, hasFocus));
            }
        }
    }

    private static void applyFocusRing(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setForeground(view.getResources().getDrawable(R.drawable.tv_focus_ring,
                    view.getContext().getTheme()));
        }
    }

    private static void animateFocus(View view, boolean hasFocus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.animate()
                    .scaleX(hasFocus ? 1.04f : 1f)
                    .scaleY(hasFocus ? 1.04f : 1f)
                    .setDuration(120)
                    .start();
        }
    }

    /**
     * Returns the screen width in dp.
     */
    public static int getScreenWidthDp(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        return Math.round(dm.widthPixels / dm.density);
    }

    /**
     * Universal keyboard shortcuts shared across activities:
     *  • Escape → back (PC/laptop)
     *  • Ctrl+F or the dedicated Search key → open search
     * Call from an Activity's dispatchKeyEvent before its own key handling.
     * Returns true if the event was consumed.
     */
    public static boolean handleGlobalKeyEvent(android.app.Activity activity, android.view.KeyEvent event) {
        if (event.getAction() != android.view.KeyEvent.ACTION_DOWN) return false;
        int kc = event.getKeyCode();

        if (kc == android.view.KeyEvent.KEYCODE_ESCAPE) {
            activity.onBackPressed();
            return true;
        }

        if (kc == android.view.KeyEvent.KEYCODE_SEARCH
                || (kc == android.view.KeyEvent.KEYCODE_F && event.isCtrlPressed())) {
            if (!(activity instanceof SearchActivity)) {
                activity.startActivity(new android.content.Intent(activity, SearchActivity.class));
            }
            return true;
        }

        return false;
    }
}
