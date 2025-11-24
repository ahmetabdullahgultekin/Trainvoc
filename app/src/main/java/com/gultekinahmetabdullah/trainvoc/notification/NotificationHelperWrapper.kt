package com.gultekinahmetabdullah.trainvoc.notification

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of INotificationHelper
 *
 * Delegates all calls to the NotificationHelper object.
 * This wrapper enables dependency injection and testing.
 */
@Singleton
class NotificationHelperWrapper @Inject constructor() : INotificationHelper {

    override fun sendWordQuizNotification(context: Context) {
        NotificationHelper.sendWordQuizNotification(context)
    }

    override fun sendDailyReminder(context: Context) {
        NotificationHelper.sendDailyReminder(context)
    }

    override fun sendStreakAlert(context: Context, streakCount: Int, isEndangered: Boolean) {
        NotificationHelper.sendStreakAlert(context, streakCount, isEndangered)
    }

    override fun sendWordOfTheDay(context: Context, wordId: Int?) {
        NotificationHelper.sendWordOfTheDay(context, wordId)
    }

    override fun createNotificationChannels(context: Context) {
        NotificationHelper.createNotificationChannels(context)
    }
}
