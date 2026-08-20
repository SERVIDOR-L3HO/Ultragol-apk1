---
name: Update manifest validation
description: The Android updater consumes an external JSON manifest whose field names and APK hosting may differ from the local server.
---

The updater must accept both camelCase and snake_case manifest fields, and must verify the APK URL responds successfully before announcing an update.

**Why:** A higher remote version can exist while its APK is missing or hosted under a different schema, which otherwise produces a broken download prompt.

**How to apply:** When changing update metadata or its provider, preserve schema compatibility and keep the lightweight ranged-download check before showing the dialog.