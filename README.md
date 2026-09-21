# RESET Android Beta

RESET Beta 0.1.0 — Android wrapper for `https://reset.ct.ws/`.

## What this beta adds
- Native Android WebView shell
- Persistent WebView storage for login/session state
- Android back navigation
- Native scheduled bedtime reminders at 60/40/20 minutes before sleep
- Notification permission handling for Android 13+
- Fallback to inexact alarms when exact-alarm access is unavailable
- GitHub Actions cloud APK build

## Security
This repository contains no PHP/MySQL credentials. The Android app only talks to the HTTPS RESET backend.

## Build online
Open GitHub Actions → Build RESET Beta → Run workflow. The APK is uploaded as a workflow artifact.

## Important
The exact-alarm permission is intentionally treated as optional. If unavailable, RESET uses an inexact alarm so the beta can still function. Play Store release policy for exact alarms must be reviewed before production publishing.
