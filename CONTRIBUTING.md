# Contributing to NotifyFX

Thank you for your interest in contributing to NotifyFX! This document provides guidelines for contributing to the project.

## 📋 Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct:
- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive criticism
- Respect differing viewpoints and experiences

## 🛠 Development Setup

### Prerequisites

- Android Studio Ladybug (2024.2.1) or later
- JDK 17
- Android SDK 35
- Git

### Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/NotifyFX.git
   cd NotifyFX
   ```
3. Open in Android Studio
4. Let Gradle sync complete
5. Run the app on a device/emulator

## 🌿 Branching Strategy

- `main` - Stable releases only
- `develop` - Integration branch for features
- `feature/*` - New features
- `fix/*` - Bug fixes
- `docs/*` - Documentation updates
- `refactor/*` - Code refactoring

## 📝 Commit Messages

Follow conventional commits:

```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Formatting, missing semicolons, etc.
- `refactor` - Code restructuring
- `test` - Adding tests
- `chore` - Maintenance tasks

Examples:
```
feat(designer): add blur slider to style editor
fix(parser): handle missing EXTRA_TITLE gracefully
docs(readme): update build instructions
refactor(overlay): simplify window layout logic
```

## 🔄 Pull Request Process

1. Create a feature branch from `develop`
2. Make your changes with clear, focused commits
3. Ensure all tests pass: `./gradlew test`
4. Run lint: `./gradlew lint`
5. Update documentation if needed
6. Push to your fork and create a PR against `develop`
7. Fill out the PR template completely
8. Request review from maintainers

### PR Requirements

- [ ] All CI checks pass
- [ ] No lint errors (warnings acceptable)
- [ ] Tests added/updated for new functionality
- [ ] Documentation updated if API changes
- [ ] Commit messages follow convention
- [ ] No merge conflicts with `develop`

## 🎨 Code Style

### Kotlin

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):
- 4-space indentation
- Max line length: 120 characters
- Use trailing commas
- Prefer `val` over `var`
- Use meaningful names

### Compose

- Use `@Composable` functions for UI
- Extract reusable components
- Use `remember` for state
- Follow Material 3 guidelines

### Architecture

- Clean Architecture layers (data, domain, presentation)
- Repository pattern for data access
- StateFlow/LiveData for reactive UI
- Hilt for dependency injection
- Coroutines for async operations

## 🧪 Testing

- Write unit tests for business logic
- Write UI tests for critical flows
- Test on multiple API levels (26, 30, 33, 34, 35)
- Test with different OEM skins (Samsung, Pixel, Motorola, etc.)

Run tests:
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## 🐛 Reporting Bugs

Use the bug report template and include:
- Device model and Android version
- Steps to reproduce
- Expected vs actual behavior
- Logcat output (filter by `NotifyFX`)
- Screenshots if applicable

## 💡 Feature Requests

Use the feature request template and describe:
- The problem you're trying to solve
- Proposed solution
- Alternatives considered
- Mockups or examples if UI-related

## 📦 Areas for Contribution

### High Priority
- Notification action handling (reply, dismiss, custom actions)
- Quick reply / RemoteInput support
- Group notification handling
- Lock screen integration
- Wear OS support

### Medium Priority
- More preset styles
- Animation customization
- Import/export styles
- Backup/restore settings
- Tasker/Automate integration

### Documentation
- API documentation
- User guide
- Video tutorials
- Translation (i18n)

## 🏷 Release Process

1. Update version in `app/build.gradle.kts`
2. Update `CHANGELOG.md`
3. Create PR to `main`
4. Tag release: `git tag v1.0.0`
5. GitHub Actions builds and creates release

## 📞 Getting Help

- 💬 [Discussions](https://github.com/yourusername/NotifyFX/discussions)
- 🐛 [Issues](https://github.com/yourusername/NotifyFX/issues)
- 📧 Email: notifyfx@example.com

## 📄 License

By contributing, you agree that your contributions will be licensed under the GPL-3.0 License.