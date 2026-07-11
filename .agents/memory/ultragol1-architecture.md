---
name: Ultragol1 dual rendering architecture
description: The app has two separate, independently-styled rendering systems for what looks like similar content — edits to one do not affect the other.
---

Ultragol1 (Android app in `Ultragol1/`) renders sports/match content two different ways depending on where it's shown:

1. **Home tab** (`HomeFragment.java` + `fragment_home.xml`) is fully native: a `NestedScrollView` with multiple horizontal `RecyclerView` rows. The live-match carousel there uses `GlassLiveAdapter.java` inflating `item_live_glass_card.xml`, styled via native drawables (`live_card_main_bg.xml`, `live_cta_glass_btn.xml`, `league_pill_premium_bg.xml`, etc.) and colors in `values/colors.xml` / `values/styles.xml` (`orange_primary`, `colorPrimary`).

2. **"Deportes" nav-drawer item** (`DeportesWebFragment.java`) loads `file:///android_asset/ultrax/index.html` into a `WebView`, rendered by `assets/ultrax/app.js` (function `renderImportantMatches`, CSS classes prefixed `rim-`) and `assets/ultrax/styles.css`.

**Why this matters:** a color/style/performance fix applied only to `app.js`/`styles.css` (the WebView path) will NOT appear on the Home tab, and vice versa — they must be edited separately. When a user reports a visual or performance issue "on the home screen" vs. "in Deportes", confirm which of the two systems is actually involved before editing, e.g. by grepping both `res/layout|res/drawable` (native) and `assets/ultrax` (WebView) for the relevant text/class.

**How to apply:** when doing an app-wide color/theme change ("all orange buttons to red"), sweep both trees: `Ultragol1/app/src/main/res/**/*.xml` (colors.xml, styles.xml, and any drawable/layout with literal hex) AND `Ultragol1/app/src/main/assets/ultrax/{app.js,styles.css}`. A `grep -rli` for the old hex values across both trees before declaring done is the reliable check — `xmllint` is not installed in this environment, so don't rely on it to validate Android XML; a Gradle build is the real validation.

## Persistent Home-screen scroll jank (fixed)

The Home tab's root layout was a `NestedScrollView` > `LinearLayout` containing several small horizontal `RecyclerView` rows plus one paginated/"infinite" `GridLayoutManager` `RecyclerView` (`discoverGrid`, up to ~40 pages) set to `wrap_content` height with `nestedScrollingEnabled=false`.

**Why this caused persistent, worsening stutter:** a RecyclerView with `wrap_content` height inside a scrolling parent must lay out ALL of its children just to compute its own height — it can never recycle off-screen items, because "off-screen" from the RecyclerView's own perspective (not the outer ScrollView's) never happens. Every paginated load added more permanently-inflated views (images via Glide, gradients, click listeners), so jank scaled with how far the user scrolled/how many pages had loaded — cosmetic overdraw fixes did not touch this.

**The fix:** removed the nested RecyclerView entirely. The screen is now a single top-level `RecyclerView` using `ConcatAdapter`: one adapter contributes a single full-width "header" item (topbar, hero banner, continue-watching, all the horizontal rows — everything that used to be directly in the NestedScrollView), and the second adapter is the existing infinite-discover adapter, whose items are now genuine RecyclerView rows getting real recycling. `GridLayoutManager.SpanSizeLookup` gives the header item span=2 (full width) and delegates everything else to the discover adapter's own span logic (position - 1, to account for the header occupying index 0).

**How to apply:** if a "still laggy on scroll" report on this app ever resurfaces, check first whether someone reintroduced a RecyclerView/ListView with `wrap_content` height nested inside a ScrollView/NestedScrollView anywhere — that antipattern is the actual root cause here, not decorative view overdraw (which was a real but minor contributor, already trimmed from `item_live_glass_card.xml`).
