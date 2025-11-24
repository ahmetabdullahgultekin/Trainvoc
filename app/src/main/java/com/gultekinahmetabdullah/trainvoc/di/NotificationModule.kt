package com.gultekinahmetabdullah.trainvoc.di

import android.content.Context
import com.gultekinahmetabdullah.trainvoc.notification.INotificationHelper
import com.gultekinahmetabdullah.trainvoc.notification.INotificationScheduler
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelperWrapper
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import com.gultekinahmetabdullah.trainvoc.notification.NotificationSchedulerWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for notification-related dependencies
 *
 * Provides:
 * - NotificationPreferences as a singleton
 * - NotificationScheduler wrapper for testability
 * - NotificationHelper wrapper for testability
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    /**
     * Provides NotificationPreferences singleton
     *
     * Uses the existing singleton pattern but makes it available
     * through dependency injection for testability.
     */
    @Provides
    @Singleton
    fun provideNotificationPreferences(
        @ApplicationContext context: Context
    ): NotificationPreferences {
        return NotificationPreferences.getInstance(context)
    }

    /**
     * Provides NotificationScheduler wrapper
     *
     * Wraps the NotificationScheduler object in an interface
     * to enable mocking in tests.
     */
    @Provides
    @Singleton
    fun provideNotificationScheduler(): INotificationScheduler {
        return NotificationSchedulerWrapper()
    }

    /**
     * Provides NotificationHelper wrapper
     *
     * Wraps the NotificationHelper object in an interface
     * to enable mocking in tests.
     */
    @Provides
    @Singleton
    fun provideNotificationHelper(): INotificationHelper {
        return NotificationHelperWrapper()
    }
}
