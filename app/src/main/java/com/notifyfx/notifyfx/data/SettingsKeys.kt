package com.notifyfx.notifyfx.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

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