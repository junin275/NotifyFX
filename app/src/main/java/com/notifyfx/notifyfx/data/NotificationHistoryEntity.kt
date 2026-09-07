package com.notifyfx.notifyfx.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.notifyfx.notifyfx.data.converters.BitmapConverter
import com.notifyfx.notifyfx.data.converters.NotificationActionConverter
import com.notifyfx.notifyfx.data.converters.NotificationCategoryConverter
import com.notifyfx.notifyfx.model.NotificationCategory
import com.notifyfx.notifyfx.model.NotificationAction
import androidx.room.TypeConverters

@Entity(tableName = "notification_history")
@TypeConverters(
    BitmapConverter::class,
    NotificationActionConverter::class,
    NotificationCategoryConverter::class
)
data class NotificationHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val notificationKey: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val subText: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val icon: ByteArray? = null,
    val largeIcon: ByteArray? = null,
    val actions: List<NotificationAction> = emptyList(),
    val category: NotificationCategory = NotificationCategory.OTHER,
    val channelId: String? = null,
    val isRead: Boolean = false,
    val isDismissed: Boolean = false
) {
    fun toNotificationModel(): com.notifyfx.notifyfx.model.NotificationModel {
        return com.notifyfx.notifyfx.model.NotificationModel(
            key = notificationKey,
            packageName = packageName,
            appName = appName,
            title = title,
            text = text,
            subText = subText,
            timestamp = timestamp,
            icon = icon?.let { BitmapConverter().toBitmap(it) },
            largeIcon = largeIcon?.let { BitmapConverter().toBitmap(it) },
            actions = actions,
            category = category.systemCategory,
            channelId = channelId
        )
    }
}