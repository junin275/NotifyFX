package com.notifyfx.notifyfx.di

import android.content.Context
import com.notifyfx.notifyfx.data.HistoryRepositoryImpl
import com.notifyfx.notifyfx.data.IHistoryRepository
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.IStyleRepository
import com.notifyfx.notifyfx.data.NotificationRepositoryImpl
import com.notifyfx.notifyfx.data.NotifyFXDatabase
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.data.StyleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotifyFXDatabase {
        return NotifyFXDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context): NotifyFXSettings {
        return NotifyFXSettings(context)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        database: NotifyFXDatabase
    ): INotificationRepository {
        return NotificationRepositoryImpl(context, database)
    }

    @Provides
    @Singleton
    fun provideStyleRepository(database: NotifyFXDatabase): IStyleRepository {
        return StyleRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(database: NotifyFXDatabase): IHistoryRepository {
        return HistoryRepositoryImpl(database)
    }
}