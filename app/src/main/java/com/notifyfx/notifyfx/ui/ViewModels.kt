package com.notifyfx.notifyfx.ui

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.data.IStyleRepository
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.model.AppStyleMapping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application,
    private val notificationRepository: INotificationRepository,
    private val settings: NotifyFXSettings,
    private val styleRepository: IStyleRepository
) : AndroidViewModel(application) {

    val notifications = notificationRepository.notifications
    val settingsEnabled = MutableLiveData(true)
    val notificationListenerEnabled = MutableLiveData(false)
    val overlayPermissionGranted = MutableLiveData(false)
    val batteryOptimizationDisabled = MutableLiveData(false)

    init {
        viewModelScope.launch {
            settings.settingsFlow.collect { prefs ->
                settingsEnabled.value = prefs[NotifyFXSettings.SettingsKeys.ENABLED] ?: true
            }
        }

        // Check permissions
        checkPermissions()
    }

    private fun checkPermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationListenerEnabled.postValue(isNotificationListenerEnabled())
            overlayPermissionGranted.postValue(isOverlayPermissionGranted())
            batteryOptimizationDisabled.postValue(isBatteryOptimizationDisabled())
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            application.contentResolver,
            Settings.Secure.ENABLED_NOTIFICATION_LISTENERS
        ) ?: return false
        return enabledServices.contains("com.notifyfx.notifyfx")
    }

    private fun isOverlayPermissionGranted(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(application)
        }
        return true
    }

    private fun isBatteryOptimizationDisabled(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val powerManager = application.getSystemService(android.os.PowerManager::class.java)
            return powerManager.isIgnoringBatteryOptimizations(application.packageName)
        }
        return true
    }

    fun refreshPermissions() {
        checkPermissions()
    }

    fun toggleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settings.setEnabled(enabled)
        }
    }
}

class DesignerViewModel(
    application: Application,
    private val styleRepository: IStyleRepository
) : AndroidViewModel(application) {

    val currentStyle = MutableLiveData<NotificationStyle>(NotificationStyle.getDefault())
    val presets = styleRepository.presets
    val customStyles = styleRepository.customStyles

    fun loadStyle(style: NotificationStyle) {
        currentStyle.value = style
    }

    fun createCustomStyle(baseStyle: NotificationStyle, name: String): NotificationStyle {
        return baseStyle.copy(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            isPreset = false
        )
    }

    fun saveCurrentStyle() {
        currentStyle.value?.let { styleRepository.saveStyle(it) }
    }
}

class SettingsViewModel(
    application: Application,
    private val settings: NotifyFXSettings,
    private val notificationRepository: INotificationRepository,
    private val styleRepository: IStyleRepository
) : AndroidViewModel(application) {

    val enabled = MutableLiveData(true)
    val hideOriginal = MutableLiveData(false)
    val notificationHistory = MutableLiveData(true)
    val historyRetentionDays = MutableLiveData(30)
    val playSound = MutableLiveData(true)
    val vibration = MutableLiveData(true)
    val autoExpand = MutableLiveData(false)
    val showOnLockScreen = MutableLiveData(true)
    val showInLandscape = MutableLiveData(true)
    val defaultStyle = styleRepository.defaultStyle

    init {
        viewModelScope.launch {
            settings.settingsFlow.collect { prefs ->
                enabled.postValue(prefs[NotifyFXSettings.SettingsKeys.ENABLED] ?: true)
                hideOriginal.postValue(prefs[NotifyFXSettings.SettingsKeys.HIDE_ORIGINAL] ?: false)
                notificationHistory.postValue(prefs[NotifyFXSettings.SettingsKeys.NOTIFICATION_HISTORY] ?: true)
                historyRetentionDays.postValue(prefs[NotifyFXSettings.SettingsKeys.HISTORY_RETENTION_DAYS] ?: 30)
                playSound.postValue(prefs[NotifyFXSettings.SettingsKeys.PLAY_SOUND] ?: true)
                vibration.postValue(prefs[NotifyFXSettings.SettingsKeys.VIBRATION] ?: true)
                autoExpand.postValue(prefs[NotifyFXSettings.SettingsKeys.AUTO_EXPAND] ?: false)
                showOnLockScreen.postValue(prefs[NotifyFXSettings.SettingsKeys.SHOW_ON_LOCK_SCREEN] ?: true)
                showInLandscape.postValue(prefs[NotifyFXSettings.SettingsKeys.SHOW_IN_LANDSCAPE] ?: true)
            }
        }
    }

    fun setEnabled(value: Boolean) {
        viewModelScope.launch { settings.setEnabled(value) }
    }

    fun setHideOriginal(value: Boolean) {
        viewModelScope.launch { settings.setHideOriginal(value) }
    }

    fun setNotificationHistory(value: Boolean) {
        viewModelScope.launch { settings.setNotificationHistory(value) }
    }

    fun setHistoryRetentionDays(value: Int) {
        viewModelScope.launch { settings.setHistoryRetentionDays(value) }
    }

    fun setPlaySound(value: Boolean) {
        viewModelScope.launch { settings.setPlaySound(value) }
    }

    fun setVibration(value: Boolean) {
        viewModelScope.launch { settings.setVibration(value) }
    }

    fun setAutoExpand(value: Boolean) {
        viewModelScope.launch { settings.setAutoExpand(value) }
    }

    fun setShowOnLockScreen(value: Boolean) {
        viewModelScope.launch { settings.setShowOnLockScreen(value) }
    }

    fun setShowInLandscape(value: Boolean) {
        viewModelScope.launch { settings.setShowInLandscape(value) }
    }

    fun setDefaultStyle(styleId: String) {
        viewModelScope.launch { settings.setDefaultStyleId(styleId) }
        styleRepository.setDefaultStyle(styleId)
    }

    fun clearAllNotifications() {
        notificationRepository.clearAll()
    }
}

class HistoryViewModel(
    application: Application,
    private val historyRepository: com.notifyfx.notifyfx.data.IHistoryRepository
) : AndroidViewModel(application) {

    val history = historyRepository.history
    val isLoading = MutableLiveData(false)

    fun loadMore(offset: Int, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            historyRepository.getHistory(limit, offset)
            isLoading.postValue(false)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }
}

class AppStylesViewModel(
    application: Application,
    private val styleRepository: IStyleRepository
) : AndroidViewModel(application) {

    val appStyles = styleRepository.appStyleMappings
    val allStyles = styleRepository.getAllStyles()
    val recentApps = MutableLiveData<List<AppInfo>>()

    data class AppInfo(
        val packageName: String,
        val appName: String,
        val icon: android.graphics.drawable.Drawable?
    )

    init {
        loadRecentApps()
    }

    private fun loadRecentApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = application.packageManager
            val apps = styleRepository.appStyleMappings.value?.mapNotNull { mapping ->
                try {
                    val appInfo = pm.getApplicationInfo(mapping.packageName, 0)
                    val label = pm.getApplicationLabel(appInfo).toString()
                    val icon = pm.getApplicationIcon(appInfo)
                    AppInfo(mapping.packageName, label, icon)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            recentApps.postValue(apps)
        }
    }

    fun setAppStyle(packageName: String, styleId: String) {
        val mapping = AppStyleMapping(packageName, "", styleId)
        viewModelScope.launch {
            styleRepository.setAppStyle(mapping)
            loadRecentApps()
        }
    }

    fun removeAppStyle(packageName: String) {
        viewModelScope.launch {
            styleRepository.removeAppStyle(packageName)
            loadRecentApps()
        }
    }
}