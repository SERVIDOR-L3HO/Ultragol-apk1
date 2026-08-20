---
name: Android build environment
description: Constraint affecting local Gradle builds for the imported Android project
---

The imported Android project cannot currently be assembled in this Repl because its configured SDK path is `/home/runner/android-sdk`, and no complete Android SDK with the required platforms is installed there. Nix exposes helper tools but not the full SDK needed by Gradle.

**Why:** Repeated Gradle attempts fail during SDK discovery before Java/resource compilation begins, so source errors cannot be ruled in or out until the SDK is provisioned.

**How to apply:** Before debugging APK source changes, provision a complete Android SDK and matching platform/build-tools packages, then rerun `cd Ultragol1 && ./gradlew :app:assembleDebug`.