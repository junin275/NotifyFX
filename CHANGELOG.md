# Changelog

All notable changes to NotifyFX will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure
- NotificationListenerService for intercepting system notifications
- NotificationParser for extracting notification data
- NotificationModel for internal representation
- Room database for history and styles
- AccessibilityService-based overlay for custom notification rendering
- Jetpack Compose UI with Material 3
- Notification Designer with real-time preview
- 8 built-in style presets (Minimal, Glass, Material, iOS-like, Compact, AMOLED, Bubble, Modern)
- Custom style editor with 50+ configurable properties
- Per-app style configuration
- Notification history with local storage
- Settings screen with permission management
- Privacy-first architecture (no internet permission)
- GitHub Actions CI/CD pipeline
- Comprehensive documentation

### Changed
- N/A

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- N/A

### Security
- No internet permission declared
- All data stored locally
- Encrypted storage using Android's file-based encryption

---

## [1.0.0] - 2026-01-XX

### Added
- First stable release
- Core notification interception and rendering
- Visual customization system
- Per-app styling
- Local notification history
- Privacy-focused design

---

## Version History

### Pre-Release Versions

- **0.1.0** - Initial prototype with basic notification listener
- **0.2.0** - Added overlay service and basic rendering
- **0.3.0** - Implemented style engine and presets
- **0.4.0** - Added Designer screen with live preview
- **0.5.0** - Per-app styles and history
- **0.9.0** - Beta release with full feature set
- **1.0.0** - Stable release

---

## Release Checklist

Before each release:
- [ ] Update version in `app/build.gradle.kts`
- [ ] Update this CHANGELOG.md
- [ ] Run full test suite (`./gradlew test connectedAndroidTest`)
- [ ] Run lint (`./gradlew lint`)
- [ ] Build release APK (`./gradlew assembleRelease`)
- [ ] Test on multiple devices/API levels
- [ ] Create Git tag (`git tag vX.Y.Z`)
- [ ] GitHub Actions creates release automatically
- [ ] Verify release artifacts