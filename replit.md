# Ultragol Update Server

A Node.js/Express server that manages APK distribution and push notifications for the Ultragol Android app.

## Stack
- **Runtime**: Node.js
- **Framework**: Express
- **Push notifications**: web-push (VAPID)
- **File uploads**: multer

## How to run
The workflow `Start application` runs:
```
npm install --prefer-offline --no-audit --no-fund && node server.js
```
Server listens on port 5000.

## Key endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/version` | Current version info (ultragol1) |
| GET | `/ultragol1/version` | Same as above |
| GET | `/ultra1/version` | ultra1 version info |
| GET | `/ultragol1/download` | Download ultra.apk |
| GET | `/ultra1/download` | Download ultra1.apk |
| GET | `/notifications` | Active in-app notifications |
| GET | `/api/gol` | Live match proxy (ultrago-xi.vercel.app) |
| POST | `/admin/update` | Upload new APK + version (requires ADMIN_KEY) |
| POST | `/admin/notify` | Create notification + send push (requires ADMIN_KEY) |
| GET | `/push/vapid-public-key` | VAPID public key for push subscriptions |
| POST | `/push/subscribe` | Register push subscription |

## Environment variables / secrets
| Variable | Required | Description |
|----------|----------|-------------|
| `ADMIN_KEY` | Yes | Protects all `/admin/*` routes |
| `SESSION_SECRET` | Optional | Available in environment |

## Data files
- `version.json` / `version_ultra1.json` — version metadata; download URLs are auto-fixed on startup
- `notifications.json` — active notifications list
- `subscriptions.json` — push subscription endpoints
- `vapid.json` — auto-generated VAPID keys (generated once on first run)
- `apks/` — uploaded APK files

## Android account/profile persistence

The Android app uses Firebase Authentication from `Ultragol1/app/google-services.json`.
Profiles and local preferences are synchronized to the authenticated account at:
```
users/{firebaseUid}/{preferenceGroup}
```
The app also reads the previous `users/{uid}/prefs/{group}` layout for existing
installations, but new writes use the account document layout so they match the
Firebase rules used by the web module. On a fresh install, the splash screen waits
for the cloud pull before opening the profile selector to avoid creating a duplicate
empty profile.

The Android OAuth client ID must remain the web client ID from
`google-services.json`; using a client ID from another Firebase project causes
Google sign-in to fail before profile synchronization starts.

## Watch Party — Ver en grupo
Real-time synchronized group viewing feature added to the Android app.

### How it works
- **Button**: "🎬 Ver en grupo" replaces the old Subtítulos/Doblaje buttons in `activity_player_detail.xml`.
- **Android Activity**: `WatchPartyActivity.java` — lobby to create/join a room, then a chat+sync room view.
- **Android WebSocket client**: `WatchPartyManager.java` — uses OkHttp WebSocket; handles room create/join, sync, and chat.
- **Server WebSocket**: `server.js` — `/watchparty/ws` endpoint using the `ws` npm package.
  - Rooms are password-protected (SHA-256 hashed), max 20 participants, auto-destroy when empty.
  - Host can sync play/pause to all participants; chat is broadcast to all in room.
- **Room codes**: 6-character alphanumeric, randomly generated.
- **OkHttp dependency**: added `com.squareup.okhttp3:okhttp:4.12.0` to `Ultragol1/app/build.gradle`.
- **Server URL**: defaults to `https://ultragol-update-server.replit.app`; override via Intent extra `"server"` for testing.

### Key files
| File | Purpose |
|------|---------|
| `Ultragol1/app/src/main/java/com/ultragol/app/WatchPartyActivity.java` | Room UI: lobby, chat, sync controls |
| `Ultragol1/app/src/main/java/com/ultragol/app/WatchPartyManager.java` | OkHttp WebSocket client |
| `Ultragol1/app/src/main/res/layout/activity_watch_party.xml` | Room screen layout |
| `Ultragol1/app/src/main/res/layout/item_chat_message.xml` | Chat bubble item |
| `Ultragol1/app/src/main/res/drawable/btn_watch_party_bg.xml` | Purple gradient button bg |
| `server.js` (end) | `/watchparty/ws` WebSocket room server |

## Verified setup
- Server starts cleanly via the `Start application` workflow (`npm install && node server.js`)
- Listens on port 5000 (mapped to external port 80 in `.replit`)
- `GET /version` and `GET /notifications` respond correctly on startup
- VAPID keys are auto-generated to `vapid.json` on first run (already present)
- Download URLs in `version.json` / `version_ultra1.json` are patched to the current Replit domain automatically on each server start
- `ADMIN_KEY` secret is **not yet set** — `/admin/*` routes return 503 until it is configured in Replit Secrets

## Android app — Responsive / Multi-device support (Ultragol1/)

All changes are in `Ultragol1/app/src/main/`.

### New files added
| File | Purpose |
|------|---------|
| `java/.../TvHelper.java` | Device detection (TV/Tablet/Desktop/Phone) + RecyclerView D-pad helper |
| `res/animator/tv_card_focus.xml` | StateListAnimator: card scales to 108% on focus |
| `res/drawable/tv_nav_item_focused.xml` | Focus selector for nav items (red accent bar on left) |
| `res/drawable/tv_card_focus_ring.xml` | Focus ring drawable for content cards |
| `res/values/dimens.xml` | Base dimensions (phone) |
| `res/values-large/dimens.xml` | Tablet dimensions (sw600dp+) |
| `res/values-television/dimens.xml` | TV dimensions (bigger text, larger cards, more spacing) |
| `res/values/integers.xml` | Grid column counts: home=2, content=3, tv=2, adult=2 |
| `res/values-large/integers.xml` | Tablet: home=3, content=4, tv=3, adult=3 |
| `res/values-television/integers.xml` | TV: home=4, content=5, tv=4, adult=4 |
| `res/layout-television/activity_main.xml` | TV layout: persistent 240dp side nav + content area |
| `res/values-television/styles.xml` | TV theme (fullscreen, focus colours) |

### Modified files
| File | Change |
|------|--------|
| `AndroidManifest.xml` | Removed `portrait` locks from all activities; added `configChanges=uiMode` so TV remote doesn't restart activities |
| `MainActivity.java` | TV mode detection, `showMenu()`/`hideMenu()` TV overrides, D-pad routing (`dispatchKeyEvent`), keyboard shortcuts (Esc=back, Ctrl+F=search) |
| `MediaActivity.java` | Full `dispatchKeyEvent`: DPAD_CENTER=play/pause, ←→=seek±10s, PageUp/Down=±90s, Media keys, Space/Enter, S=settings, F=fit/crop |
| `fragments/HomeFragment.java` | Responsive grid columns from `R.integer.home_grid_columns`; `TvHelper.makeFocusable(rvHome)` |
| `fragments/MoviesFragment.java` | Responsive columns + TvHelper |
| `fragments/FavoritesFragment.java` | Responsive columns + TvHelper |
| `fragments/MyListFragment.java` | Responsive columns + TvHelper |
| `fragments/PlatformFragment.java` | Responsive columns + TvHelper |
| `fragments/SportsFragment.java` | Responsive columns + TvHelper |
| `fragments/DownloadsFragment.java` | Responsive columns + TvHelper |
| `fragments/AdultFragment.java` | Responsive columns + TvHelper |
| `fragments/CineBaseFragment.java` | TvHelper on hero + section RecyclerViews |

### How responsive layout works
- **Phone**: portrait/landscape, 2-col discover grid, 3-col content, bottom drawer
- **Tablet** (sw600dp+): landscape allowed, 3-col discover, 4-col content, larger text
- **TV / Google TV**: `layout-television/activity_main.xml` loads automatically (Android resource qualifiers). Persistent side nav rail always visible. Full D-pad navigation. 4-col discover, 5-col content. All RecyclerView items scale 8% on focus.
- **PC/Laptop**: keyboard shortcuts work on all layouts. Tab/Shift+Tab = focus traversal (native). Enter/Space = click. Esc = back.

### Remote control key mapping (TV)
| Key | Action |
|-----|--------|
| D-pad center / Enter / Space | Show controls → Play/Pause |
| ← / → | Seek −10s / +10s |
| Page Down/Up or CH−/+ | Seek −90s / +90s |
| ↑ / ↓ | Show controls |
| Media Play/Pause | Toggle |
| Media FF / RW | Seek ±10s |
| S | Settings panel |
| F | Toggle fit/crop |
| Esc / Back | Back |

## User preferences
<!-- Add any preferences here -->
