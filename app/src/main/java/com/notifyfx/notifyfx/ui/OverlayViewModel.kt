package com.notifyfx.notifyfx.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.model.NotificationStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverlayViewModel(
    application: Application,
    private val settings: NotifyFXSettings,
    private val notificationRepository: INotificationRepository
) : AndroidViewModel(application) {

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

    fun showQuickReply(notification: NotificationModel, action: com.notifyfx.notifyfx.model.NotificationAction) {
        // TODO: Implement quick reply UI
    }

    companion object {
        fun provideFactory(settings: NotifyFXSettings, repository: INotificationRepository) =
            OverlayViewModelFactory(settings, repository)
    }
}

class OverlayViewModelFactory(
    private val settings: NotifyFXSettings,
    private val repository: INotificationRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
        return OverlayViewModel(
            androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                (repository as? com.notifyfx.notifyfx.data.NotificationRepositoryImpl)?.context?.applicationContext
                    ?: throw IllegalStateException("Context not available")
            ).application,
            settings,
            repository
        ) as T
    }
}