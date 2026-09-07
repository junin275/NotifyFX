package com.notifyfx.notifyfx.util

import android.app.Notification
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.graphics.drawable.toBitmap
import com.notifyfx.notifyfx.model.NotificationAction
import com.notifyfx.notifyfx.model.NotificationModel

object NotificationParser {

    private const val TAG = "NotificationParser"
    private const val ICON_SIZE = 96
    private const val LARGE_ICON_SIZE = 128

    fun parse(
        sbn: StatusBarNotification,
        packageManager: PackageManager
    ): NotificationModel {
        val notification = sbn.notification
        val extras = notification.extras

        // Package name and app name
        val packageName = sbn.packageName
        val appName = getAppName(packageName, packageManager)

        // Title and text
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
            ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()
            ?: notification.tickerText?.toString()
            ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()

        // Timestamp
        val whenTime = notification.when ?: 0L
        val timestamp = if (whenTime != 0L) whenTime else sbn.postTime

        // Icons
        val icon = loadAppIcon(packageName, packageManager)
        val largeIcon = loadLargeIcon(notification)

        // Actions
        val actions = parseActions(notification)

        // Category and priority
        val category = notification.category
        val priority = getNotificationPriority(notification)

        // Group info
        val groupKey = notification.getGroup()
        val isGroupSummary = (notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0

        // Conversation info (Android 11+)
        val conversationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            extras.getString(Notification.EXTRA_CONVERSATION_TITLE)
        } else null
        val conversationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            extras.getString(Notification.EXTRA_CONVERSATION_ID)
        } else null

        // Channel ID (Android 8+)
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.channelId
        } else null

        return NotificationModel(
            key = sbn.key,
            packageName = packageName,
            appName = appName,
            title = title,
            text = text,
            subText = subText,
            timestamp = timestamp,
            icon = icon,
            largeIcon = largeIcon,
            actions = actions,
            category = category,
            priority = priority,
            groupKey = groupKey,
            isGroupSummary = isGroupSummary,
            conversationName = conversationName,
            conversationId = conversationId,
            channelId = channelId,
            contentIntent = notification.contentIntent
        )
    }

    private fun getAppName(packageName: String, packageManager: PackageManager): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    private fun loadAppIcon(packageName: String, packageManager: PackageManager): Bitmap? {
        return try {
            val drawable = packageManager.getApplicationIcon(packageName)
            drawable.toBitmap(width = ICON_SIZE, height = ICON_SIZE)
        } catch (e: Exception) {
            null
        }
    }

    private fun loadLargeIcon(notification: Notification): Bitmap? {
        // Try EXTRA_LARGE_ICON
        val extraLarge = notification.extras.get(Notification.EXTRA_LARGE_ICON)
        extraLarge?.let {
            return (it as? Bitmap) ?: (it as? Icon)?.loadDrawable(null)?.toBitmap(LARGE_ICON_SIZE, LARGE_ICON_SIZE)
        }

        // Try EXTRA_LARGE_ICON_BIG
        val extraLargeBig = notification.extras.get(Notification.EXTRA_LARGE_ICON_BIG)
        extraLargeBig?.let {
            return (it as? Bitmap) ?: (it as? Icon)?.loadDrawable(null)?.toBitmap(LARGE_ICON_SIZE, LARGE_ICON_SIZE)
        }

        // Try getLargeIcon()
        val largeIconObj = notification.largeIcon
        return try {
            largeIconObj?.loadDrawable(null)?.toBitmap(LARGE_ICON_SIZE, LARGE_ICON_SIZE)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseActions(notification: Notification): List<NotificationAction> {
        return notification.actions?.mapNotNull { action ->
            action.title?.toString()?.let { title ->
                val remoteInput = action.remoteInputs?.firstOrNull()
                val isReply = remoteInput != null || title.lowercase().contains("reply")
                NotificationAction(
                    title = title,
                    pendingIntent = action.actionIntent,
                    isQuickReply = isReply,
                    remoteInputKey = remoteInput?.resultKey,
                    remoteInputLabel = remoteInput?.label?.toString(),
                    remoteInputChoices = remoteInput?.choices?.map { it.toString() } ?: emptyList(),
                    allowFreeFormInput = remoteInput?.allowFreeFormInput ?: true
                )
            }
        } ?: emptyList()
    }

    private fun getNotificationPriority(notification: Notification): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return notification.priority
        }
        return Notification.PRIORITY_DEFAULT
    }

    /**
     * Check if notification should be filtered out (system notifications, etc.)
     */
    fun shouldFilter(sbn: StatusBarNotification, packageManager: PackageManager): Boolean {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // Filter our own package
        if (packageName == "com.notifyfx.notifyfx") return true

        // Filter system packages
        val systemPackages = setOf(
            "android",
            "com.android.systemui",
            "com.android.settings",
            "com.android.permissioncontroller",
            "com.google.android.permissioncontroller",
            "com.android.packageinstaller",
            "com.google.android.packageinstaller"
        )
        if (packageName in systemPackages) return true

        // Filter system-level categories
        val systemCategories = setOf(
            Notification.CATEGORY_SYSTEM,
            Notification.CATEGORY_STATUS,
            Notification.CATEGORY_SERVICE,
            Notification.CATEGORY_ERROR
        )
        if (notification.category in systemCategories) return true

        // Filter system-level apps
        return try {
            val flags = packageManager.getApplicationInfo(packageName, 0).flags
            val isSystem = (flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            isSystem && packageName !in setOf("com.android.chrome", "com.android.providers.downloads", "com.android.vending")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if this is a group summary notification
     */
    fun isGroupSummary(notification: Notification): Boolean {
        return (notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
    }
}