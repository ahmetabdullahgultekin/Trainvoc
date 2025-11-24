package com.gultekinahmetabdullah.trainvoc.notification

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of INotificationScheduler
 *
 * Delegates all calls to the NotificationScheduler object.
 * This wrapper enables dependency injection and testing.
 */
@Singleton
class NotificationSchedulerWrapper @Inject constructor() : INotificationScheduler {

    override fun scheduleWordQuiz(context: Context) {
        NotificationScheduler.scheduleWordQuiz(context)
    }

    override fun cancelWordQuiz(context: Context) {
        NotificationScheduler.cancelWordQuiz(context)
    }

    override fun scheduleDailyReminder(context: Context) {
        NotificationScheduler.scheduleDailyReminder(context)
    }

    override fun cancelDailyReminder(context: Context) {
        NotificationScheduler.cancelDailyReminder(context)
    }

    override fun scheduleStreakAlert(context: Context) {
        NotificationScheduler.scheduleStreakAlert(context)
    }

    override fun cancelStreakAlert(context: Context) {
        NotificationScheduler.cancelStreakAlert(context)
    }

    override fun scheduleWordOfDay(context: Context) {
        NotificationScheduler.scheduleWordOfDay(context)
    }

    override fun cancelWordOfDay(context: Context) {
        NotificationScheduler.cancelWordOfDay(context)
    }

    override fun scheduleAllNotifications(context: Context) {
        NotificationScheduler.scheduleAllNotifications(context)
    }

    override fun cancelAllNotifications(context: Context) {
        NotificationScheduler.cancelAllNotifications(context)
    }
}
