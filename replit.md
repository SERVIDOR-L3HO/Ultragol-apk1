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

## Verified setup
- Server starts cleanly via the `Start application` workflow (`npm install && node server.js`)
- Listens on port 5000 (mapped to external port 80 in `.replit`)
- `GET /version` and `GET /notifications` respond correctly on startup
- VAPID keys are auto-generated to `vapid.json` on first run (already present)
- Download URLs in `version.json` / `version_ultra1.json` are patched to the current Replit domain automatically on each server start
- `ADMIN_KEY` secret is **not yet set** — `/admin/*` routes return 503 until it is configured in Replit Secrets

## User preferences
<!-- Add any preferences here -->
