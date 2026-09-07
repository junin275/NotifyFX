package com.notifyfx.notifyfx.data

import androidx.lifecycle.LiveData
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.model.AppStyleMapping
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface INotificationRepository {
    val notifications: StateFlow<List<NotificationModel>>
    val commands: StateFlow<NotificationCommand>

    fun postNotification(notification: NotificationModel, autoExpand: Boolean = false)
    fun removeNotification(key: String)
    fun removeNotificationsForPackage(packageName: String)
    fun clearAll()
    fun sendCommand(command: NotificationCommand)
}

sealed interface NotificationCommand {
    data class CancelNotification(val key: String) : NotificationCommand
    data class ExpandNotification(val key: String) : NotificationCommand
    data class CollapseNotification : NotificationCommand
    data class ActionClicked(val key: String, val actionIndex: Int) : NotificationCommand
    data class QuickReply(val key: String, val text: String) : NotificationCommand
}

interface IStyleRepository {
    val currentStyle: StateFlow<NotificationStyle>
    val defaultStyle: LiveData<NotificationStyle?>
    val presets: LiveData<List<NotificationStyle>>
    val customStyles: LiveData<List<NotificationStyle>>

    suspend fun getStyle(id: String): NotificationStyle?
    suspend fun saveStyle(style: NotificationStyle)
    suspend fun deleteCustomStyle(id: String)
    suspend fun setDefaultStyle(id: String)
    fun getStyleForApp(packageName: String): NotificationStyle?
    suspend fun setAppStyle(mapping: AppStyleMapping)
    suspend fun removeAppStyle(packageName: String)
    val appStyleMappings: LiveData<List<AppStyleMapping>>
}

interface IHistoryRepository {
    val history: LiveData<List<NotificationHistoryEntry>>
    suspend fun saveEntry(entry: NotificationHistoryEntry)
    suspend fun getHistory(limit: Int, offset: Int): List<NotificationHistoryEntry>
    suspend fun getHistoryForApp(packageName: String, limit: Int): List<NotificationHistoryEntry>
    suspend fun deleteOldEntries(cutoffTime: Long): Int
    suspend fun clearAll()
    suspend fun markAsRead(key: String)
    suspend fun markAsDismissed(key: String)
}