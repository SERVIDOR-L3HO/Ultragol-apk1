#!/bin/bash
export JAVA_HOME=/nix/store/xad649j61kwkh0id5wvyiab5rliprp4d-openjdk-17.0.15+6/lib/openjdk
export ANDROID_SDK_ROOT=/home/runner/android-sdk
export ANDROID_HOME=/home/runner/android-sdk
export GRADLE_OPTS="-Xmx1536m -Dorg.gradle.jvmargs=-Xmx1536m"
cd /home/runner/workspace/Ultragol1
./gradlew assembleDebug --no-daemon --max-workers=2 2>&1
echo "EXIT_CODE=$?"
