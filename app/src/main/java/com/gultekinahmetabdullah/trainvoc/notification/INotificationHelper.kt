package com.gultekinahmetabdullah.trainvoc.notification

import android.content.Context

/**
 * Interface for notification helper operations
 *
 * Abstracts NotificationHelper to enable mocking in tests.
 * In production, this is implemented by NotificationHelperWrapper.
 * In tests, this can be mocked with MockK.
 */
interface INotificationHelper {

    /**
     * Send a word quiz notification immediately for testing
     */
    fun sendWordQuizNotification(context: Context)

    /**
     * Send a daily reminder notification
     */
    fun sendDailyReminder(context: Context)

    /**
     * Send a streak alert notification
     *
     * @param streakCount Current streak count
     * @param isEndangered Whether the streak is in danger of breaking
     */
    fun sendStreakAlert(context: Context, streakCount: Int, isEndangered: Boolean)

    /**
     * Send a word of the day notification
     *
     * @param wordId Optional word ID to feature
     */
    fun sendWordOfTheDay(context: Context, wordId: Int? = null)

    /**
     * Create all notification channels
     * Should be called at app startup
     */
    fun createNotificationChannels(context: Context)
}
