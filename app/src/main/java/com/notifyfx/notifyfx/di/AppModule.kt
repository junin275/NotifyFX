package com.notifyfx.notifyfx.di

import android.content.Context
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.IHistoryRepository
import com.notifyfx.notifyfx.data.IStyleRepository
import com.notifyfx.notifyfx.data.NotificationRepositoryImpl
import com.notifyfx.notifyfx.data.NotifyFXDatabase
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.data.HistoryRepositoryImpl
import com.notifyfx.notifyfx.data.RepositoryImpl
import com.notifyfx.notifyfx.data.StyleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ApplicationScoped
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Provides
    @ApplicationScoped
    fun provideDatabase(@ApplicationContext context: Context): NotifyFXDatabase {
        return NotifyFXDatabase.getDatabase(context)
    }

    @Provides
    @ApplicationScoped
    fun provideSettings(@ApplicationContext context: Context): NotifyFXSettings {
        return NotifyFXSettings(context)
    }

    @Provides
    @ApplicationScoped
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        database: NotifyFXDatabase
    ): INotificationRepository {
        return NotificationRepositoryImpl(context, database)
    }

    @Provides
    @ApplicationScoped
    fun provideStyleRepository(database: NotifyFXDatabase): IStyleRepository {
        return StyleRepositoryImpl(database)
    }

    @Provides
    @ApplicationScoped
    fun provideHistoryRepository(database: NotifyFXDatabase): IHistoryRepository {
        return HistoryRepositoryImpl(database)
    }
}

@InstallIn(ServiceComponent::class)
@Module
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        database: NotifyFXDatabase
    ): INotificationRepository {
        return NotificationRepositoryImpl(context, database)
    }

    @Provides
    @ServiceScoped
    fun provideSettings(@ApplicationContext context: Context): NotifyFXSettings {
        return NotifyFXSettings(context)
    }
}