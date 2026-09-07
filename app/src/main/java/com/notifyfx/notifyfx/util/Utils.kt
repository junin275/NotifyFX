package com.notifyfx.notifyfx.util

import android.content.Context
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.isNotificationListenerEnabled(): Boolean {
    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_NOTIFICATION_LISTENERS
    ) ?: return false
    return enabledServices.contains("com.notifyfx.notifyfx")
}

fun Context.isOverlayPermissionGranted(): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        return Settings.canDrawOverlays(this)
    }
    return true
}

fun Context.isBatteryOptimizationDisabled(): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        val powerManager = getSystemService(android.os.PowerManager::class.java)
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }
    return true
}

inline fun <T> runCatchingLogged(
    tag: String,
    message: String,
    block: () -> T
): T? {
    return try {
        block()
    } catch (e: Exception) {
        android.util.Log.e(tag, message, e)
        null
    }
}

suspend fun <T> runSuspendCatchingLogged(
    tag: String,
    message: String,
    block: suspend () -> T
): T? = try {
    block()
} catch (e: Exception) {
    android.util.Log.e(tag, message, e)
    null
}