package com.notifyfx.notifyfx.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.notifyfx.notifyfx.model.NotificationStyle

private val Context.notifyFxDataStore by preferencesDataStore(name = "notifyfx_settings")

class NotifyFXSettings(private val context: Context) {

    val settingsFlow: Flow<Preferences> = context.notifyFxDataStore.data

    val enabled: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.ENABLED] ?: true }
    val hideOriginal: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.HIDE_ORIGINAL] ?: false }
    val notificationHistory: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.NOTIFICATION_HISTORY] ?: true }
    val historyRetentionDays: Flow<Int> = settingsFlow.map { it[SettingsKeys.HISTORY_RETENTION_DAYS] ?: 30 }
    val playSound: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.PLAY_SOUND] ?: true }
    val vibration: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.VIBRATION] ?: true }
    val autoExpand: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.AUTO_EXPAND] ?: false }
    val defaultStyleId: Flow<String> = settingsFlow.map { it[SettingsKeys.DEFAULT_STYLE_ID] ?: NotificationStyle.getDefault().id }
    val showOnLockScreen: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.SHOW_ON_LOCK_SCREEN] ?: true }
    val showInLandscape: Flow<Boolean> = settingsFlow.map { it[SettingsKeys.SHOW_IN_LANDSCAPE] ?: true }

    suspend fun snapshot(): Preferences = settingsFlow.first()

    suspend fun setEnabled(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.ENABLED] = value }
    }

    suspend fun setHideOriginal(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.HIDE_ORIGINAL] = value }
    }

    suspend fun setNotificationHistory(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.NOTIFICATION_HISTORY] = value }
    }

    suspend fun setHistoryRetentionDays(value: Int) {
        context.notifyFxDataStore.edit { it[SettingsKeys.HISTORY_RETENTION_DAYS] = value }
    }

    suspend fun setPlaySound(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.PLAY_SOUND] = value }
    }

    suspend fun setVibration(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.VIBRATION] = value }
    }

    suspend fun setAutoExpand(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.AUTO_EXPAND] = value }
    }

    suspend fun setDefaultStyleId(value: String) {
        context.notifyFxDataStore.edit { it[SettingsKeys.DEFAULT_STYLE_ID] = value }
    }

    suspend fun setShowOnLockScreen(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.SHOW_ON_LOCK_SCREEN] = value }
    }

    suspend fun setShowInLandscape(value: Boolean) {
        context.notifyFxDataStore.edit { it[SettingsKeys.SHOW_IN_LANDSCAPE] = value }
    }
}