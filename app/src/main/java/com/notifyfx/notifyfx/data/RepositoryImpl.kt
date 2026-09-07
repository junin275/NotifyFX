package com.notifyfx.notifyfx.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.model.AppStyleMapping
import com.notifyfx.notifyfx.model.NotificationCategory

class NotificationRepositoryImpl(
    private val context: Context,
    private val database: NotifyFXDatabase
) : INotificationRepository {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    override val notifications: StateFlow<List<NotificationModel>> = _notifications

    private val _commands = MutableStateFlow<NotificationCommand>(NotificationCommand.CollapseNotification())
    override val commands: StateFlow<NotificationCommand> = _commands

    override fun postNotification(notification: NotificationModel, autoExpand: Boolean = false) {
        _notifications.update { current ->
            if (current.any { it.key == notification.key }) {
                current.map { if (it.key == notification.key) notification else it }
            } else {
                current + notification
            }
        }

        // Save to history
        CoroutineScope(Dispatchers.IO).launch {
            val entry = NotificationHistoryEntry(
                notificationKey = notification.key,
                packageName = notification.packageName,
                appName = notification.appName,
                title = notification.title,
                text = notification.text,
                subText = notification.subText,
                timestamp = notification.timestamp,
                icon = notification.icon?.let { BitmapConverter().fromBitmap(it) },
                largeIcon = notification.largeIcon?.let { BitmapConverter().fromBitmap(it) },
                actions = notification.actions,
                category = notification.category.toNotificationCategory(),
                channelId = notification.channelId
            )
            database.historyDao().insert(entry)
        }

        if (autoExpand) {
            _commands.value = NotificationCommand.ExpandNotification(notification.key)
        }
    }

    override fun removeNotification(key: String) {
        _notifications.update { current ->
            current.filter { it.key != key }
        }
    }

    override fun removeNotificationsForPackage(packageName: String) {
        _notifications.update { current ->
            current.filter { it.packageName != packageName }
        }
    }

    override fun clearAll() {
        _notifications.value = emptyList()
    }

    override fun sendCommand(command: NotificationCommand) {
        _commands.value = command
    }
}

class StyleRepositoryImpl(
    private val database: NotifyFXDatabase
) : IStyleRepository {

    private val _currentStyle = MutableStateFlow<NotificationStyle>(NotificationStyle.getDefault())
    override val currentStyle: StateFlow<NotificationStyle> = _currentStyle

    override val defaultStyle: LiveData<NotificationStyle?> = database.styleDao().getDefaultStyle()
    override val presets: LiveData<List<NotificationStyle>> = database.styleDao().getPresets()
    override val customStyles: LiveData<List<NotificationStyle>> = database.styleDao().getCustomStyles()
    override val appStyleMappings: LiveData<List<AppStyleMapping>> = database.appStyleDao().getAllMappings()

    override suspend fun getStyle(id: String): NotificationStyle? {
        return database.styleDao().getStyleSync(id)
    }

    override suspend fun saveStyle(style: NotificationStyle) {
        database.styleDao().insert(style)
        if (style.id == _currentStyle.value.id) {
            _currentStyle.value = style
        }
    }

    override suspend fun deleteCustomStyle(id: String) {
        database.styleDao().deleteCustomStyle(id)
    }

    override suspend fun setDefaultStyle(id: String) {
        database.styleDao().clearDefault()
        database.styleDao().setDefault(id)
        getStyle(id)?.let { _currentStyle.value = it }
    }

    override fun getStyleForApp(packageName: String): NotificationStyle? {
        val mapping = database.appStyleDao().getMappingSync(packageName)
        return mapping?.let { database.styleDao().getStyleSync(it.styleId) }
    }

    override suspend fun setAppStyle(mapping: AppStyleMapping) {
        database.appStyleDao().insert(mapping)
    }

    override suspend fun removeAppStyle(packageName: String) {
        database.appStyleDao().deleteMapping(packageName)
    }
}

class HistoryRepositoryImpl(
    private val database: NotifyFXDatabase
) : IHistoryRepository {

    override val history: LiveData<List<NotificationHistoryEntry>> = database.historyDao().getHistory(50, 0)

    override suspend fun saveEntry(entry: NotificationHistoryEntry) {
        database.historyDao().insert(entry)
    }

    override suspend fun getHistory(limit: Int, offset: Int): List<NotificationHistoryEntry> {
        return database.historyDao().getHistorySync(limit, offset)
    }

    override suspend fun getHistoryForApp(packageName: String, limit: Int): List<NotificationHistoryEntry> {
        return database.historyDao().getHistoryForApp(packageName, limit).value ?: emptyList()
    }

    override suspend fun deleteOldEntries(cutoffTime: Long): Int {
        return database.historyDao().deleteOldEntries(cutoffTime)
    }

    override suspend fun clearAll() {
        database.historyDao().clearAll()
    }

    override suspend fun markAsRead(key: String) {
        database.historyDao().markAsRead(key)
    }

    override suspend fun markAsDismissed(key: String) {
        database.historyDao().markAsDismissed(key)
    }
}