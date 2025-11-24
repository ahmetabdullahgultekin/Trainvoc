package com.gultekinahmetabdullah.trainvoc.notification

import android.content.Context

/**
 * Interface for notification scheduling operations
 *
 * Abstracts NotificationScheduler to enable mocking in tests.
 * In production, this is implemented by NotificationSchedulerWrapper.
 * In tests, this can be mocked with MockK.
 */
interface INotificationScheduler {

    /**
     * Schedule word quiz notifications with current interval
     */
    fun scheduleWordQuiz(context: Context)

    /**
     * Cancel word quiz notifications
     */
    fun cancelWordQuiz(context: Context)

    /**
     * Schedule daily reminder notification
     */
    fun scheduleDailyReminder(context: Context)

    /**
     * Cancel daily reminder notification
     */
    fun cancelDailyReminder(context: Context)

    /**
     * Schedule streak alert notifications
     */
    fun scheduleStreakAlert(context: Context)

    /**
     * Cancel streak alert notifications
     */
    fun cancelStreakAlert(context: Context)

    /**
     * Schedule word of the day notification
     */
    fun scheduleWordOfDay(context: Context)

    /**
     * Cancel word of the day notification
     */
    fun cancelWordOfDay(context: Context)

    /**
     * Schedule all enabled notifications
     */
    fun scheduleAllNotifications(context: Context)

    /**
     * Cancel all scheduled notifications
     */
    fun cancelAllNotifications(context: Context)
}
