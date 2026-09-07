# ProGuard rules for NotifyFX

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class com.notifyfx.notifyfx.Hilt_* { *; }

# Keep Room entities and DAOs
-keep class com.notifyfx.notifyfx.data.** { *; }

# Keep Compose ViewModels
-keep class com.notifyfx.notifyfx.ui.** { *; }

# Keep model classes for serialization
-keep class com.notifyfx.notifyfx.model.** { *; }

# Keep services
-keep class com.notifyfx.notifyfx.service.** { *; }

# Keep receiver
-keep class com.notifyfx.notifyfx.receiver.** { *; }

# Keep OverlayViewTreeOwners
-keep class com.notifyfx.notifyfx.overlay.** { *; }