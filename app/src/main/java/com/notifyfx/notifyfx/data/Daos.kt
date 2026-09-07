package com.notifyfx.notifyfx.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.model.AppStyleMapping

@Dao
interface NotificationHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: NotificationHistoryEntry): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<NotificationHistoryEntry>)

    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getHistory(limit: Int, offset: Int): LiveData<List<NotificationHistoryEntry>>

    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getHistorySync(limit: Int, offset: Int): List<NotificationHistoryEntry>

    @Query("SELECT * FROM notification_history WHERE packageName = :packageName ORDER BY timestamp DESC LIMIT :limit")
    fun getHistoryForApp(packageName: String, limit: Int): LiveData<List<NotificationHistoryEntry>>

    @Query("SELECT COUNT(*) FROM notification_history")
    suspend fun getTotalCount(): Int

    @Query("DELETE FROM notification_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOldEntries(cutoffTime: Long): Int

    @Query("DELETE FROM notification_history")
    suspend fun clearAll()

    @Query("UPDATE notification_history SET isRead = 1 WHERE notificationKey = :key")
    suspend fun markAsRead(key: String)

    @Query("UPDATE notification_history SET isDismissed = 1 WHERE notificationKey = :key")
    suspend fun markAsDismissed(key: String)
}

@Dao
interface NotificationStyleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(style: NotificationStyle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(styles: List<NotificationStyle>)

    @Query("SELECT * FROM notification_styles WHERE id = :id")
    fun getStyle(id: String): LiveData<NotificationStyle?>

    @Query("SELECT * FROM notification_styles WHERE id = :id")
    suspend fun getStyleSync(id: String): NotificationStyle?

    @Query("SELECT * FROM notification_styles WHERE isPreset = 1")
    fun getPresets(): LiveData<List<NotificationStyle>>

    @Query("SELECT * FROM notification_styles WHERE isPreset = 0")
    fun getCustomStyles(): LiveData<List<NotificationStyle>>

    @Query("SELECT * FROM notification_styles WHERE isDefault = 1")
    fun getDefaultStyle(): LiveData<NotificationStyle?>

    @Query("SELECT * FROM notification_styles")
    fun getAllStyles(): LiveData<List<NotificationStyle>>

    @Query("DELETE FROM notification_styles WHERE id = :id AND isPreset = 0")
    suspend fun deleteCustomStyle(id: String): Int

    @Query("UPDATE notification_styles SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefault()

    @Query("UPDATE notification_styles SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: String)
}

@Dao
interface AppStyleMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mapping: AppStyleMapping)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mappings: List<AppStyleMapping>)

    @Query("SELECT * FROM app_styles WHERE packageName = :packageName")
    fun getMapping(packageName: String): LiveData<AppStyleMapping?>

    @Query("SELECT * FROM app_styles WHERE packageName = :packageName")
    suspend fun getMappingSync(packageName: String): AppStyleMapping?

    @Query("SELECT * FROM app_styles WHERE isEnabled = 1 ORDER BY lastSeen DESC")
    fun getAllMappings(): LiveData<List<AppStyleMapping>>

    @Query("SELECT * FROM app_styles WHERE isEnabled = 1 ORDER BY lastSeen DESC")
    suspend fun getAllMappingsSync(): List<AppStyleMapping>

    @Query("DELETE FROM app_styles WHERE packageName = :packageName")
    suspend fun deleteMapping(packageName: String)

    @Query("UPDATE app_styles SET lastSeen = :time WHERE packageName = :packageName")
    suspend fun updateLastSeen(packageName: String, time: Long)
}