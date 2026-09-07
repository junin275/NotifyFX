package com.notifyfx.notifyfx.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import com.notifyfx.notifyfx.model.NotificationStyle

object SettingsKeys {
    val ENABLED = booleanPreferencesKey("enabled")
    val HIDE_ORIGINAL = booleanPreferencesKey("hide_original")
    val NOTIFICATION_HISTORY = booleanPreferencesKey("notification_history")
    val HISTORY_RETENTION_DAYS = intPreferencesKey("history_retention_days")
    val PLAY_SOUND = booleanPreferencesKey("play_sound")
    val VIBRATION = booleanPreferencesKey("vibration")
    val AUTO_EXPAND = booleanPreferencesKey("auto_expand")
    val DEFAULT_STYLE_ID = stringPreferencesKey("default_style_id")
    val SHOW_ON_LOCK_SCREEN = booleanPreferencesKey("show_on_lock_screen")
    val SHOW_IN_LANDSCAPE = booleanPreferencesKey("show_in_landscape")
}

class NotifyFXSettings private constructor(
    private val dataStore: androidx.datastore.preferences.core.PreferencesDataStore
) {
    companion object {
        private const val DATA_STORE_NAME = "notifyfx_settings"
        @Volatile private var INSTANCE: NotifyFXSettings? = null

        fun getInstance(context: Context): NotifyFXSettings {
            return INSTANCE ?: synchronized(this) {
                val context = context.applicationContext
                val dataStore = androidx.datastore.preferences.core.PreferencesDataStoreBuilder(context, DATA_STORE_NAME).build()
                INSTANCE = NotifyFXSettings(dataStore)
                INSTANCE!!
            }
        }
    }

    val settingsFlow: Flowable<Preferences> = dataStore.data

    val enabled: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.ENABLED] ?: true }
    val hideOriginal: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.HIDE_ORIGINAL] ?: false }
    val notificationHistory: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.NOTIFICATION_HISTORY] ?: true }
    val historyRetentionDays: Flowable<Int> = settingsFlow.map { it[SettingsKeys.HISTORY_RETENTION_DAYS] ?: 30 }
    val playSound: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.PLAY_SOUND] ?: true }
    val vibration: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.VIBRATION] ?: true }
    val autoExpand: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.AUTO_EXPAND] ?: false }
    val defaultStyleId: Flowable<String> = settingsFlow.map { it[SettingsKeys.DEFAULT_STYLE_ID] ?: NotificationStyle.getDefault().id }
    val showOnLockScreen: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.SHOW_ON_LOCK_SCREEN] ?: true }
    val showInLandscape: Flowable<Boolean> = settingsFlow.map { it[SettingsKeys.SHOW_IN_LANDSCAPE] ?: true }

    suspend fun setEnabled(value: Boolean) {
        dataStore.edit { it[SettingsKeys.ENABLED] = value }
    }

    suspend fun setHideOriginal(value: Boolean) {
        dataStore.edit { it[SettingsKeys.HIDE_ORIGINAL] = value }
    }

    suspend fun setNotificationHistory(value: Boolean) {
        dataStore.edit { it[SettingsKeys.NOTIFICATION_HISTORY] = value }
    }

    suspend fun setHistoryRetentionDays(value: Int) {
        dataStore.edit { it[SettingsKeys.HISTORY_RETENTION_DAYS] = value }
    }

    suspend fun setPlaySound(value: Boolean) {
        dataStore.edit { it[SettingsKeys.PLAY_SOUND] = value }
    }

    suspend fun setVibration(value: Boolean) {
        dataStore.edit { it[SettingsKeys.VIBRATION] = value }
    }

    suspend fun setAutoExpand(value: Boolean) {
        dataStore.edit { it[SettingsKeys.AUTO_EXPAND] = value }
    }

    suspend fun setDefaultStyleId(value: String) {
        dataStore.edit { it[SettingsKeys.DEFAULT_STYLE_ID] = value }
    }

    suspend fun setShowOnLockScreen(value: Boolean) {
        dataStore.edit { it[SettingsKeys.SHOW_ON_LOCK_SCREEN] = value }
    }

    suspend fun setShowInLandscape(value: Boolean) {
        dataStore.edit { it[SettingsKeys.SHOW_IN_LANDSCAPE] = value }
    }
}