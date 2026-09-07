# NotifyFX

**Customize your Android notifications with beautiful, personalized designs.**

NotifyFX is an open-source Android application that intercepts system notifications and re-renders them with fully customizable visual styles. Create unique notification designs, apply different styles per app, and enjoy a modern notification experience - all processed locally on your device.

## ✨ Features

- **🎨 Visual Notification Customization** - Complete control over notification appearance
- **📱 Per-App Styles** - Different designs for WhatsApp, Telegram, Instagram, etc.
- **⚡ Real-time Preview** - See changes instantly in the Notification Designer
- **🎭 8 Built-in Presets** - Minimal, Glass, Material, iOS-like, Compact, AMOLED, Bubble, Modern
- **🔧 Custom Style Editor** - Create your own unique notification designs
- **📜 Notification History** - Local history of all received notifications
- **🔒 Privacy First** - All processing happens locally, no internet required
- **🔋 Battery Optimized** - Minimal background resource usage
- **📦 Open Source** - Licensed under GPL-3.0

## 🎯 Presets

| Preset | Description |
|--------|-------------|
| **Minimal** | Clean, lightweight design with subtle shadows |
| **Glass** | Frosted glass effect with blur and transparency |
| **Material** | Material 3 design with elevation and rounded corners |
| **iOS-like** | iOS-style notifications with blue action buttons |
| **Compact** | Smaller, denser notifications for more content |
| **AMOLED** | True black background for OLED screens |
| **Bubble** | Rounded bubble design with soft shadows |
| **Modern** | Contemporary design with generous spacing |

## 🛠 Technical Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    NotifyFX Architecture                     │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │ Notification │───▶│  Notification│───▶│   Style      │  │
│  │  Listener    │    │   Parser     │    │   Engine     │  │
│  │  Service     │    │              │    │              │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│         │                   │                   │           │
│         ▼                   ▼                   ▼           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Notification Repository                  │  │
│  │  (In-memory StateFlow + Room Database for History)   │  │
│  └──────────────────────────────────────────────────────┘  │
│         │                                       │           │
│         ▼                                       ▼           │
│  ┌──────────────┐                      ┌──────────────┐  │
│  │   Overlay    │                      │   Settings   │  │
│  │   Service    │                      │   & UI       │  │
│  │(Accessibility│                      │ (Compose)    │  │
│  └──────────────┘                      └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Core Components

- **`NotifyFXNotificationListenerService`** - Extends `NotificationListenerService` to intercept `onNotificationPosted` and `onNotificationRemoved`
- **`NotificationParser`** - Safely extracts notification data (title, text, icons, actions, RemoteInput, category, priority, conversation info)
- **`NotificationModel`** - Internal data model decoupled from `StatusBarNotification`
- **`NotifyFXOverlayService`** - `AccessibilityService` using `WindowManager` with `TYPE_ACCESSIBILITY_OVERLAY` for custom notification rendering
- **`Style Engine`** - `NotificationStyle` data class with 50+ customizable properties
- **`Room Database`** - Local storage for notification history and custom styles
- **`Jetpack Compose UI`** - Modern declarative UI with real-time preview

## 📋 Requirements

- Android 8.0+ (API 26+)
- Notification Listener Permission
- Display Over Other Apps Permission
- Battery Optimization Disabled (recommended)

## 🚀 Building

```bash
# Clone the repository
git clone https://github.com/yourusername/NotifyFX.git
cd NotifyFX

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint
```

APKs will be available at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 📦 GitHub Actions

Automated builds on every push:
- ✅ Debug & Release APK compilation
- ✅ Unit tests execution
- ✅ Lint checks
- ✅ Artifact uploads
- ✅ Automatic releases on version tags

## 🔐 Permissions

| Permission | Purpose |
|------------|---------|
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Read system notifications |
| `SYSTEM_ALERT_WINDOW` | Display custom notification overlays |
| `FOREGROUND_SERVICE` | Keep overlay service alive |
| `RECEIVE_BOOT_COMPLETED` | Auto-start services on boot |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Prevent service killing |

**No `INTERNET` permission** - All processing is completely local.

## 📱 Screenshots

*Coming soon - the designer screen with live preview*

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

NotifyFX is licensed under the **GNU General Public License v3.0** - see [LICENSE](LICENSE) for details.

This project references concepts from [SmartIsland](https://github.com/agupta07505/SmartIsland) (GPL-3.0) for notification interception techniques, but contains original implementation for notification rendering and customization.

## 🙏 Acknowledgments

- [SmartIsland](https://github.com/agupta07505/SmartIsland) - Reference for NotificationListenerService patterns
- Android Jetpack Compose team - Modern UI toolkit
- Material Design team - Design system

## 📞 Support

- 🐛 [Report Issues](https://github.com/yourusername/NotifyFX/issues)
- 💡 [Feature Requests](https://github.com/yourusername/NotifyFX/issues/new?template=feature_request.md)
- 📖 [Documentation](https://github.com/yourusername/NotifyFX/wiki)

---

**NotifyFX** - Your notifications, your style. 🎨