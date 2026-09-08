package com.notifyfx.notifyfx.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.model.NotificationAction
import com.notifyfx.notifyfx.model.NotificationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OverlayViewModel(
    private val settings: NotifyFXSettings,
    private val notificationRepository: INotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications

    val settingsEnabled = MutableLiveData(true)
    val settingsShowOnLockScreen = MutableLiveData(true)
    val settingsShowInLandscape = MutableLiveData(true)
    val isLocked = MutableLiveData(false)

    init {
        notificationRepository.notifications.onEach { list ->
            _notifications.value = list
        }.launchIn(viewModelScope)
    }

    fun updateSettings(enabled: Boolean, showOnLock: Boolean, showInLandscape: Boolean) {
        settingsEnabled.value = enabled
        settingsShowOnLockScreen.value = showOnLock
        settingsShowInLandscape.value = showInLandscape
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    fun collapse() {
        // No-op for now, can be used for animation triggers
    }

    fun showQuickReply(notification: NotificationModel, action: NotificationAction) {
        // TODO: Implement quick reply UI
    }

    companion object {
        fun provideFactory(settings: NotifyFXSettings, repository: INotificationRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OverlayViewModel(settings, repository) as T
                }
            }
    }
}