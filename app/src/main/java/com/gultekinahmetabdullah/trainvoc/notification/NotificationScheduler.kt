package com.gultekinahmetabdullah.trainvoc.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gultekinahmetabdullah.trainvoc.worker.DailyReminderWorker
import com.gultekinahmetabdullah.trainvoc.worker.StreakAlertWorker
import com.gultekinahmetabdullah.trainvoc.worker.WordOfDayWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Manages scheduling of all notification workers
 *
 * Handles:
 * - Scheduling periodic work requests
 * - Calculating optimal notification times
 * - Canceling scheduled notifications
 * - Rescheduling when settings change
 */
object NotificationScheduler {

    private const val DAILY_REMINDER_WORK = "daily_reminder_work"
    private const val STREAK_ALERT_WORK = "streak_alert_work"
    private const val WORD_OF_DAY_WORK = "word_of_day_work"

    /**
     * Schedule all enabled notifications
     */
    fun scheduleAllNotifications(context: Context) {
        scheduleDailyReminder(context)
        scheduleStreakAlert(context)
        scheduleWordOfDay(context)
    }

    /**
     * Schedule daily reminder notification
     * Runs once per day at the user's preferred time
     */
    fun scheduleDailyReminder(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isEnabled = sharedPreferences.getBoolean("daily_reminders_enabled", true)

        if (isEnabled) {
            // Calculate initial delay to run at preferred time (default 9 AM)
            val preferredHour = sharedPreferences.getInt("reminder_hour", 9)
            val initialDelay = calculateInitialDelay(preferredHour)

            val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DAILY_REMINDER_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } else {
            cancelDailyReminder(context)
        }
    }

    /**
     * Schedule streak alert notifications
     * Checks twice per day (morning and evening)
     */
    fun scheduleStreakAlert(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isEnabled = sharedPreferences.getBoolean("streak_alerts_enabled", true)

        if (isEnabled) {
            // Check every 12 hours for streak status
            val workRequest = PeriodicWorkRequestBuilder<StreakAlertWorker>(
                12, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                STREAK_ALERT_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } else {
            cancelStreakAlert(context)
        }
    }

    /**
     * Schedule word of the day notification
     * Runs once per day at a specific time
     */
    fun scheduleWordOfDay(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isEnabled = sharedPreferences.getBoolean("word_of_day_enabled", true)

        if (isEnabled) {
            // Default to 8 AM for word of the day
            val wordOfDayHour = sharedPreferences.getInt("word_of_day_hour", 8)
            val initialDelay = calculateInitialDelay(wordOfDayHour)

            val workRequest = PeriodicWorkRequestBuilder<WordOfDayWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORD_OF_DAY_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } else {
            cancelWordOfDay(context)
        }
    }

    /**
     * Cancel daily reminder notification
     */
    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK)
    }

    /**
     * Cancel streak alert notification
     */
    fun cancelStreakAlert(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(STREAK_ALERT_WORK)
    }

    /**
     * Cancel word of the day notification
     */
    fun cancelWordOfDay(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORD_OF_DAY_WORK)
    }

    /**
     * Cancel all scheduled notifications
     */
    fun cancelAllNotifications(context: Context) {
        cancelDailyReminder(context)
        cancelStreakAlert(context)
        cancelWordOfDay(context)
    }

    /**
     * Calculate initial delay to run at specific hour today or tomorrow
     */
    private fun calculateInitialDelay(targetHour: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Set target time to today at specified hour
        calendar.set(Calendar.HOUR_OF_DAY, targetHour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        var targetTime = calendar.timeInMillis

        // If target time has passed today, schedule for tomorrow
        if (targetTime <= now) {
            targetTime += TimeUnit.DAYS.toMillis(1)
        }

        return targetTime - now
    }

    /**
     * Update streak data after user completes a quiz
     * Call this after each quiz session
     */
    fun updateStreakData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val lastPracticeDate = sharedPreferences.getLong("last_practice_date", 0)
        val currentStreak = sharedPreferences.getInt("current_streak", 0)

        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        val oneDayInMillis = TimeUnit.DAYS.toMillis(1)

        val daysSinceLastPractice = ((today - lastPracticeDate) / oneDayInMillis).toInt()

        val newStreak = when {
            lastPracticeDate == 0L -> 1 // First practice
            daysSinceLastPractice == 0 -> currentStreak // Same day, maintain streak
            daysSinceLastPractice == 1 -> currentStreak + 1 // Consecutive day
            else -> 1 // Streak broken, restart
        }

        sharedPreferences.edit()
            .putInt("current_streak", newStreak)
            .putLong("last_practice_date", today)
            .apply()

        // If this is a milestone (7, 14, 30 days), show immediate notification
        if (newStreak in listOf(7, 14, 30, 60, 90, 365)) {
            NotificationHelper.sendStreakAlert(context, newStreak, isEndangered = false)
        }
    }
}
