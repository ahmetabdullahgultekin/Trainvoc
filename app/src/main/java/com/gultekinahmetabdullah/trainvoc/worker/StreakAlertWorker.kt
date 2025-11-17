package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import java.util.Calendar

/**
 * Worker for streak alert notifications
 *
 * Checks the user's learning streak and sends notifications:
 * - Milestone notifications for streak achievements
 * - Warning notifications if streak is about to break
 */
class StreakAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sharedPreferences =
            applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val streakAlertsEnabled = sharedPreferences.getBoolean("streak_alerts_enabled", true)

        if (!streakAlertsEnabled) {
            return Result.success()
        }

        // Get current streak data
        val currentStreak = sharedPreferences.getInt("current_streak", 0)
        val lastPracticeDate = sharedPreferences.getLong("last_practice_date", 0)

        if (currentStreak > 0) {
            val calendar = Calendar.getInstance()
            val today = calendar.timeInMillis
            val oneDayInMillis = 24 * 60 * 60 * 1000

            // Check if user hasn't practiced today
            val daysSinceLastPractice = ((today - lastPracticeDate) / oneDayInMillis).toInt()

            when {
                // User hasn't practiced today - send danger notification
                daysSinceLastPractice >= 1 -> {
                    NotificationHelper.sendStreakAlert(
                        applicationContext,
                        currentStreak,
                        isEndangered = true
                    )
                }
                // Milestone achievements (every 7 days)
                currentStreak > 0 && currentStreak % 7 == 0 -> {
                    NotificationHelper.sendStreakAlert(
                        applicationContext,
                        currentStreak,
                        isEndangered = false
                    )
                }
            }
        }

        return Result.success()
    }
}
