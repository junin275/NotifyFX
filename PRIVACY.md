# Privacy Policy

**Last Updated: 2026**

## Overview

NotifyFX is designed with privacy as a fundamental principle. This document explains how NotifyFX handles your data.

## 🔒 Core Privacy Principles

### 1. **Local-Only Processing**
- All notification processing happens **entirely on your device**
- No notification content is ever sent to any server
- No cloud synchronization or backup of notification data
- The app does not require `INTERNET` permission

### 2. **No Data Collection**
- No analytics, telemetry, or usage tracking
- No crash reporting to external services
- No advertising identifiers or tracking
- No user profiling or behavioral analysis

### 3. **No Third-Party Services**
- No Firebase, Google Analytics, or similar services
- No crash reporting (Sentry, Crashlytics, etc.)
- No remote configuration or feature flags
- No social media integrations

## 📱 What Data NotifyFX Accesses

### Notification Content (Local Only)
When you grant Notification Listener permission, NotifyFX receives:
- App package name
- Notification title and text
- App icon
- Action buttons (Reply, Open, etc.)
- Timestamp
- Notification category and priority

**This data never leaves your device.**

### Stored Locally (Optional)
If you enable Notification History:
- Notification history stored in local Room database
- Custom notification styles saved locally
- Per-app style preferences stored locally
- All data encrypted by Android's file-based encryption

### Permissions Used
| Permission | Purpose | Data Access |
|------------|---------|-------------|
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Read notifications | Notification content (local only) |
| `SYSTEM_ALERT_WINDOW` | Display custom overlays | None |
| `FOREGROUND_SERVICE` | Keep service alive | None |
| `RECEIVE_BOOT_COMPLETED` | Auto-start on boot | None |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Prevent service killing | None |

**Notice: No `INTERNET` permission is declared.**

## 🗂 Data Storage

### Notification History (Optional)
- Stored in app-private Room database
- Encrypted by Android (file-based encryption)
- Configurable retention (default: 30 days)
- Can be cleared anytime from Settings
- No cloud backup (excluded from Android backup)

### Custom Styles
- Stored in app-private Room database
- Only style parameters (colors, sizes, etc.)
- No notification content in style data

### Settings
- Stored in DataStore (encrypted preferences)
- Only app preferences (enabled/disabled, etc.)

## 🔐 Security

- All data stored in app-private directories
- Protected by Android's sandbox
- Encrypted at rest (Android 10+)
- No root access required
- No network communication possible

## 👤 Your Rights

You have full control over your data:
- **Disable** notification listener anytime (Settings → Apps → NotifyFX)
- **Revoke** overlay permission anytime
- **Clear** all history with one tap
- **Uninstall** app to remove all data completely
- **Export** your styles (planned feature)

## 🚫 What We DON'T Do

- ❌ Send notifications to any server
- ❌ Collect usage statistics
- ❌ Track user behavior
- ❌ Show advertisements
- ❌ Sell or share data
- ❌ Use data for AI training
- ❌ Require account creation
- ❌ Access contacts, location, camera, microphone

## 🌐 Network Traffic

**Zero network requests.** The app does not declare `INTERNET` permission in its manifest. You can verify this:
```bash
aapt dump badging app-debug.apk | grep permission
```

## 📋 Compliance

- **GDPR**: No personal data processed (only local notification content you already see)
- **CCPA**: No sale of personal information
- **Play Store**: Complies with Data Safety section requirements

## 🔍 Verification

The app is open source. You can:
1. Review all source code on GitHub
2. Build the APK yourself
3. Use network monitoring tools to verify zero traffic
4. Audit the manifest for permissions

## 📞 Contact

For privacy questions:
- GitHub Issues: [Privacy Label](https://github.com/yourusername/NotifyFX/issues/new?template=privacy.md)
- Email: privacy@notifyfx.example.com

## 📄 License

This privacy policy is part of NotifyFX, licensed under GPL-3.0.