package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper

/**
 * Worker for daily reminder notifications
 *
 * Sends a daily notification to encourage users to practice
 * their vocabulary learning.
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Check if daily reminders are enabled
        val sharedPreferences = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val dailyRemindersEnabled = sharedPreferences.getBoolean("daily_reminders_enabled", true)

        if (dailyRemindersEnabled) {
            NotificationHelper.sendDailyReminder(applicationContext)
        }

        return Result.success()
    }
}
