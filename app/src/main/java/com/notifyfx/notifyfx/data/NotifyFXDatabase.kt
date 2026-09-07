package com.notifyfx.notifyfx.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.notifyfx.notifyfx.data.converters.BitmapConverter
import com.notifyfx.notifyfx.data.converters.NotificationActionConverter
import com.notifyfx.notifyfx.data.converters.NotificationCategoryConverter
import com.notifyfx.notifyfx.data.converters.ShadowConfigConverter
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.model.AppStyleMapping
import com.notifyfx.notifyfx.model.ShadowConfig

@Database(
    entities = [
        NotificationHistoryEntry::class,
        NotificationStyle::class,
        AppStyleMapping::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    BitmapConverter::class,
    NotificationActionConverter::class,
    NotificationCategoryConverter::class,
    ShadowConfigConverter::class
)
abstract class NotifyFXDatabase : RoomDatabase() {
    abstract fun historyDao(): NotificationHistoryDao
    abstract fun styleDao(): NotificationStyleDao
    abstract fun appStyleDao(): AppStyleMappingDao

    companion object {
        @Volatile private var INSTANCE: NotifyFXDatabase? = null

        fun getDatabase(context: Context): NotifyFXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotifyFXDatabase::class.java,
                    "notifyfx_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}