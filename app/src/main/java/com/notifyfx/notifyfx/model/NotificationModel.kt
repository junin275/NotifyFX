package com.notifyfx.notifyfx.model

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.notifyfx.notifyfx.data.converters.BitmapConverter
import com.notifyfx.notifyfx.data.converters.PendingIntentConverter

/**
 * Internal notification model - decoupled from StatusBarNotification
 * This is what the UI and overlay renderer work with
 */
data class NotificationModel(
    @PrimaryKey val key: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val subText: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    @TypeConverters(BitmapConverter::class) val icon: Bitmap? = null,
    @TypeConverters(BitmapConverter::class) val largeIcon: Bitmap? = null,
    val actions: List<NotificationAction> = emptyList(),
    val category: String? = null,
    val priority: Int = 0,
    val groupKey: String? = null,
    val isGroupSummary: Boolean = false,
    val conversationName: String? = null,
    val conversationId: String? = null,
    val channelId: String? = null,
    @TypeConverters(PendingIntentConverter::class) val contentIntent: PendingIntent? = null
) {
    val displayTitle: String
        get() = if (title.isNotBlank()) title else appName

    val displayText: String
        get() = if (text.isNotBlank()) text else subText ?: ""

    val hasActions: Boolean
        get() = actions.isNotEmpty()

    val hasQuickReply: Boolean
        get() = actions.any { it.isQuickReply }
}

data class NotificationAction(
    val title: String,
    @TypeConverters(PendingIntentConverter::class) val pendingIntent: PendingIntent? = null,
    val isQuickReply: Boolean = false,
    val remoteInputKey: String? = null,
    val remoteInputLabel: String? = null,
    val remoteInputChoices: List<String> = emptyList(),
    val allowFreeFormInput: Boolean = true
)

/**
 * Notification categories for styling
 */
enum class NotificationCategory(
    val systemCategory: String
) {
    MESSAGE("msg"),
    CALL("call"),
    EMAIL("email"),
    SOCIAL("social"),
    PROMO("promo"),
    SYSTEM("sys"),
    SERVICE("service"),
    PROGRESS("progress"),
    TRANSPORT("transport"),
    NAVIGATION("navigation"),
    ALARM("alarm"),
    REMINDER("reminder"),
    EVENT("event"),
    OTHER("other")
}

fun String.toNotificationCategory(): NotificationCategory {
    return when (this) {
        "msg", "message" -> NotificationCategory.MESSAGE
        "call" -> NotificationCategory.CALL
        "email" -> NotificationCategory.EMAIL
        "social" -> NotificationCategory.SOCIAL
        "promo", "promotion" -> NotificationCategory.PROMO
        "sys", "system" -> NotificationCategory.SYSTEM
        "service" -> NotificationCategory.SERVICE
        "progress" -> NotificationCategory.PROGRESS
        "transport" -> NotificationCategory.TRANSPORT
        "navigation" -> NotificationCategory.NAVIGATION
        "alarm" -> NotificationCategory.ALARM
        "reminder" -> NotificationCategory.REMINDER
        "event" -> NotificationCategory.EVENT
        else -> NotificationCategory.OTHER
    }
}