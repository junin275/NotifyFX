package com.notifyfx.notifyfx.data.converters

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapConverter {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        if (byteArray == null) return null
        return android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}

class PendingIntentConverter {
    @TypeConverter
    fun fromPendingIntent(intent: PendingIntent?): String? {
        // We can't serialize PendingIntent, store as null
        return null
    }

    @TypeConverter
    fun toPendingIntent(string: String?): PendingIntent? {
        return null
    }
}

class ShadowConfigConverter {
    @TypeConverter
    fun fromShadowConfig(config: com.notifyfx.notifyfx.model.ShadowConfig): String {
        return "${config.elevation},${config.blur},${config.color},${config.offsetX},${config.offsetY}"
    }

    @TypeConverter
    fun toShadowConfig(string: String): com.notifyfx.notifyfx.model.ShadowConfig {
        val parts = string.split(",")
        if (parts.size != 5) return com.notifyfx.notifyfx.model.ShadowConfig()
        return com.notifyfx.notifyfx.model.ShadowConfig(
            elevation = parts[0].toFloatOrNull() ?: 4f,
            blur = parts[1].toFloatOrNull() ?: 8f,
            color = parts[2].toIntOrNull() ?: 0x33000000,
            offsetX = parts[3].toFloatOrNull() ?: 0f,
            offsetY = parts[4].toFloatOrNull() ?: 4f
        )
    }
}

class NotificationCategoryConverter {
    @TypeConverter
    fun fromCategory(category: com.notifyfx.notifyfx.model.NotificationCategory): String = category.name

    @TypeConverter
    fun toCategory(string: String): com.notifyfx.notifyfx.model.NotificationCategory {
        return com.notifyfx.notifyfx.model.NotificationCategory.valueOf(string)
    }
}

class NotificationActionConverter {
    @TypeConverter
    fun fromActions(actions: List<com.notifyfx.notifyfx.model.NotificationAction>): String {
        return actions.joinToString("|") { action ->
            "${action.title};${action.isQuickReply};${action.remoteInputKey ?: ""};${action.remoteInputLabel ?: ""};${action.allowFreeFormInput}"
        }
    }

    @TypeConverter
    fun toActions(string: String): List<com.notifyfx.notifyfx.model.NotificationAction> {
        if (string.isBlank()) return emptyList()
        return string.split("|").mapNotNull { part ->
            val segments = part.split(";", limit = 5)
            if (segments.size < 2) return@mapNotNull null
            com.notifyfx.notifyfx.model.NotificationAction(
                title = segments[0],
                isQuickReply = segments[1] == "true",
                remoteInputKey = segments[2].takeIf { it.isNotBlank() },
                remoteInputLabel = segments[3].takeIf { it.isNotBlank() },
                allowFreeFormInput = segments.getOrElse(4) { "true" } == "true"
            )
        }
    }
}