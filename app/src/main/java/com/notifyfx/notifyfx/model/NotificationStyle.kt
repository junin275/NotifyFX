package com.notifyfx.notifyfx.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.notifyfx.notifyfx.data.converters.BitmapConverter

import com.notifyfx.notifyfx.data.converters.ShadowConfigConverter

/**
 * Complete style configuration for a notification
 * All visual properties are customizable
 */
@Entity(tableName = "notification_styles")
@TypeConverters(
    ShadowConfigConverter::class,
    BitmapConverter::class
)
data class NotificationStyle(
    @PrimaryKey val id: String,
    val name: String,
    val isPreset: Boolean = false,
    val isDefault: Boolean = false,

    // Container
    val width: Float = 360f,           // dp
    val maxWidth: Float = 400f,        // dp
    val minHeight: Float = 80f,        // dp
    val maxHeight: Float = 300f,       // dp
    val cornerRadius: Float = 16f,     // dp
    val padding: Float = 16f,          // dp
    val itemSpacing: Float = 8f,       // dp

    // Background
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val backgroundBlur: Float = 0f,    // 0-100
    val transparency: Float = 0f,      // 0-100
    val useMaterialSurface: Boolean = true,

    // Border
    val borderWidth: Float = 0f,
    val borderColor: Int = 0xFF000000.toInt(),

    // Shadow
    @TypeConverters(ShadowConfigConverter::class) val shadow: ShadowConfig = ShadowConfig(),

    // Icon
    val iconSize: Float = 40f,         // dp
    val iconCornerRadius: Float = 8f,  // dp
    val showIcon: Boolean = true,

    // Typography - Title
    val titleFontFamily: String = "sans-serif-medium",
    val titleFontSize: Float = 16f,    // sp
    val titleColor: Int = 0xFF1C1B1F.toInt(),
    val titleMaxLines: Int = 1,

    // Typography - Text
    val textFontFamily: String = "sans-serif",
    val textFontSize: Float = 14f,     // sp
    val textColor: Int = 0xFF49454F.toInt(),
    val textMaxLines: Int = 3,

    // Typography - SubText
    val subTextFontSize: Float = 12f,  // sp
    val subTextColor: Int = 0xFF79747E.toInt(),

    // Actions
    val actionButtonHeight: Float = 36f,    // dp
    val actionButtonCornerRadius: Float = 18f, // dp
    val actionButtonPadding: Float = 16f,   // dp
    val actionSpacing: Float = 8f,          // dp
    val actionBackgroundColor: Int = 0xFFE8DEF8.toInt(),
    val actionTextColor: Int = 0xFF381E72.toInt(),
    val actionFontSize: Float = 14f,        // sp
    val actionFontFamily: String = "sans-serif-medium",

    // Position
    val horizontalMargin: Float = 16f,   // dp from screen edge
    val verticalMargin: Float = 80f,     // dp from top (below status bar)
    val gravity: Int = 1,                // 0=LEFT, 1=CENTER, 2=RIGHT

    // Animation
    val animationDuration: Long = 300,   // ms
    val enterAnimation: String = "slide_down_fade",
    val exitAnimation: String = "slide_up_fade",

    // Behavior
    val autoDismissDelay: Long = 8000,   // ms, 0 = never
    val swipeToDismiss: Boolean = true,
    val tapToOpen: Boolean = true,

    // Advanced
    val showAppName: Boolean = true,
    val showTimestamp: Boolean = false,
    val showProgressBar: Boolean = true,
    val progressBarHeight: Float = 4f,
    val progressBarColor: Int = 0xFF6750A4.toInt()
) {
    companion object {
        // Preset styles
        val PRESET_MINIMAL = NotificationStyle(
            id = "preset_minimal",
            name = "Minimal",
            isPreset = true,
            width = 340f,
            cornerRadius = 12f,
            padding = 12f,
            backgroundColor = 0xFFFFFFFF.toInt(),
            transparency = 5f,
            shadow = ShadowConfig(elevation = 2f),
            iconSize = 36f,
            titleFontSize = 15f,
            textFontSize = 13f,
            actionButtonHeight = 32f,
            animationDuration = 200L
        )

        val PRESET_GLASS = NotificationStyle(
            id = "preset_glass",
            name = "Glass",
            isPreset = true,
            width = 360f,
            cornerRadius = 20f,
            padding = 16f,
            backgroundColor = 0xCCFFFFFF.toInt(),
            backgroundBlur = 30f,
            transparency = 20f,
            shadow = ShadowConfig(elevation = 8f, blur = 20f),
            iconSize = 44f,
            titleFontSize = 16f,
            textFontSize = 14f,
            actionButtonCornerRadius = 20f,
            actionBackgroundColor = 0x80FFFFFF.toInt(),
            animationDuration = 400L
        )

        val PRESET_MATERIAL = NotificationStyle(
            id = "preset_material",
            name = "Material",
            isPreset = true,
            width = 360f,
            cornerRadius = 16f,
            padding = 16f,
            backgroundColor = 0xFFFFFFFF.toInt(),
            shadow = ShadowConfig(elevation = 6f, blur = 12f),
            iconSize = 40f,
            titleFontSize = 16f,
            textFontSize = 14f,
            actionButtonCornerRadius = 18f,
            actionBackgroundColor = 0xFFE8DEF8.toInt(),
            actionTextColor = 0xFF381E72.toInt(),
            animationDuration = 300L
        )

        val PRESET_IOS = NotificationStyle(
            id = "preset_ios",
            name = "iOS-like",
            isPreset = true,
            width = 340f,
            cornerRadius = 14f,
            padding = 14f,
            backgroundColor = 0xFFFFFFFF.toInt(),
            transparency = 2f,
            shadow = ShadowConfig(elevation = 4f, blur = 16f, color = 0x33000000),
            iconSize = 38f,
            titleFontSize = 15f,
            textFontSize = 13f,
            subTextColor = 0xFF8E8E93.toInt(),
            actionButtonHeight = 40f,
            actionButtonCornerRadius = 12f,
            actionBackgroundColor = 0xFF007AFF.toInt(),
            actionTextColor = 0xFFFFFFFF.toInt(),
            animationDuration = 250L
        )

        val PRESET_COMPACT = NotificationStyle(
            id = "preset_compact",
            name = "Compact",
            isPreset = true,
            width = 320f,
            minHeight = 60f,
            cornerRadius = 10f,
            padding = 10f,
            itemSpacing = 4f,
            backgroundColor = 0xFFFFFFFF.toInt(),
            shadow = ShadowConfig(elevation = 3f),
            iconSize = 32f,
            titleFontSize = 14f,
            textFontSize = 12f,
            textMaxLines = 2,
            actionButtonHeight = 28f,
            animationDuration = 150L
        )

        val PRESET_AMOLED = NotificationStyle(
            id = "preset_amoled",
            name = "AMOLED",
            isPreset = true,
            width = 360f,
            cornerRadius = 16f,
            padding = 16f,
            backgroundColor = 0xFF000000.toInt(),
            transparency = 0f,
            shadow = ShadowConfig(elevation = 0f),
            borderWidth = 1f,
            borderColor = 0xFF333333.toInt(),
            iconSize = 40f,
            titleColor = 0xFFFFFFFF.toInt(),
            textColor = 0xFFB0B0B0.toInt(),
            subTextColor = 0xFF808080.toInt(),
            actionBackgroundColor = 0xFF1A1A1A.toInt(),
            actionTextColor = 0xFFFFFFFF.toInt(),
            animationDuration = 300L
        )

        val PRESET_BUBBLE = NotificationStyle(
            id = "preset_bubble",
            name = "Bubble",
            isPreset = true,
            width = 340f,
            cornerRadius = 24f,
            padding = 18f,
            backgroundColor = 0xFFFFFFFF.toInt(),
            shadow = ShadowConfig(elevation = 12f, blur = 24f, color = 0x40000000),
            iconSize = 44f,
            iconCornerRadius = 12f,
            titleFontSize = 16f,
            textFontSize = 14f,
            actionButtonHeight = 40f,
            actionButtonCornerRadius = 20f,
            actionBackgroundColor = 0xFF6750A4.toInt(),
            actionTextColor = 0xFFFFFFFF.toInt(),
            animationDuration = 350L
        )

        val PRESET_MODERN = NotificationStyle(
            id = "preset_modern",
            name = "Modern",
            isPreset = true,
            width = 380f,
            cornerRadius = 20f,
            padding = 20f,
            itemSpacing = 12f,
            backgroundColor = 0xFFFAFAFA.toInt(),
            backgroundBlur = 10f,
            transparency = 5f,
            shadow = ShadowConfig(elevation = 10f, blur = 30f, color = 0x30000000),
            iconSize = 48f,
            iconCornerRadius = 14f,
            titleFontSize = 17f,
            titleFontFamily = "sans-serif-medium",
            textFontSize = 15f,
            actionButtonHeight = 44f,
            actionButtonCornerRadius = 22f,
            actionBackgroundColor = 0xFF1C1B1F.toInt(),
            actionTextColor = 0xFFFFFFFF.toInt(),
            animationDuration = 400L
        )

        val ALL_PRESETS = listOf(
            PRESET_MINIMAL,
            PRESET_GLASS,
            PRESET_MATERIAL,
            PRESET_IOS,
            PRESET_COMPACT,
            PRESET_AMOLED,
            PRESET_BUBBLE,
            PRESET_MODERN
        )

        fun getPreset(id: String): NotificationStyle? = ALL_PRESETS.find { it.id == id }
        fun getDefault(): NotificationStyle = PRESET_MATERIAL.copy(isDefault = true)
    }
}

data class ShadowConfig(
    val elevation: Float = 4f,
    val blur: Float = 8f,
    val color: Int = 0x33000000,
    val offsetX: Float = 0f,
    val offsetY: Float = 4f
)

/**
 * App-specific style mapping
 */
@Entity(tableName = "app_styles")
data class AppStyleMapping(
    @PrimaryKey val packageName: String,
    val appName: String,
    val styleId: String,
    val isEnabled: Boolean = true,
    val lastSeen: Long = System.currentTimeMillis()
)

/**
 * Preview notification for the designer
 */
data class PreviewNotification(
    val appName: String = "WhatsApp",
    val title: String = "John Doe",
    val text: String = "Hey, how are you doing?",
    val subText: String? = "Just now",
    val icon: Bitmap? = null,
    val largeIcon: Bitmap? = null,
    val actions: List<NotificationAction> = listOf(
        NotificationAction("Reply", isQuickReply = true, remoteInputKey = "reply", remoteInputLabel = "Type a message"),
        NotificationAction("Open", null)
    ),
    val category: String = "msg",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toNotificationModel(key: String = "preview_key"): NotificationModel {
        return NotificationModel(
            key = key,
            packageName = "com.example.preview",
            appName = appName,
            title = title,
            text = text,
            subText = subText,
            timestamp = timestamp,
            icon = icon,
            largeIcon = largeIcon,
            actions = actions,
            category = category
        )
    }
}