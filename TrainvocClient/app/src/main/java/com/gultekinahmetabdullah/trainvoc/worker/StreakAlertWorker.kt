package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

/**
 * Worker for streak alert notifications
 *
 * Checks the user's learning streak and sends notifications:
 * - Milestone notifications for streak achievements
 * - Warning notifications if streak is about to break
 *
 * Error Handling:
 * - Returns Result.retry() on transient failures (network, etc.)
 * - Returns Result.failure() on permanent failures
 * - Logs all errors for debugging
 */
@HiltWorker
class StreakAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationPrefs: com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "StreakAlertWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting streak alert worker")

            val streakAlertsEnabled = notificationPrefs.streakAlertsEnabled

            if (!streakAlertsEnabled) {
                Log.i(TAG, "Streak alerts disabled, skipping")
                return Result.success()
            }

            // Get current streak data
            val currentStreak = notificationPrefs.currentStreak
            val lastPracticeDate = notificationPrefs.lastPracticeDate

            if (currentStreak > 0) {
                val calendar = Calendar.getInstance()
                val today = calendar.timeInMillis
                val oneDayInMillis = 24 * 60 * 60 * 1000

                // Check if user hasn't practiced today
                val daysSinceLastPractice = ((today - lastPracticeDate) / oneDayInMillis).toInt()

                when {
                    // User hasn't practiced today - send danger notification
                    daysSinceLastPractice >= 1 -> {
                        Log.d(TAG, "Sending streak endangered alert (streak: $currentStreak, days since last: $daysSinceLastPractice)")
                        NotificationHelper.sendStreakAlert(
                            applicationContext,
                            currentStreak,
                            isEndangered = true
                        )
                    }
                    // Milestone achievements (every 7 days)
                    currentStreak > 0 && currentStreak % 7 == 0 -> {
                        Log.d(TAG, "Sending streak milestone alert (streak: $currentStreak)")
                        NotificationHelper.sendStreakAlert(
                            applicationContext,
                            currentStreak,
                            isEndangered = false
                        )
                    }
                }
            } else {
                Log.d(TAG, "No active streak, skipping alert")
            }

            Log.d(TAG, "Streak alert worker completed successfully")
            Result.success()

        } catch (e: SecurityException) {
            // Notification permission denied - permanent failure
            Log.e(TAG, "Security exception: Notification permission may be denied", e)
            Result.failure()

        } catch (e: IllegalStateException) {
            // Invalid state - permanent failure
            Log.e(TAG, "Illegal state exception in streak alert", e)
            Result.failure()

        } catch (e: Exception) {
            // Generic error - retry if under max attempts
            Log.e(TAG, "Error in streak alert worker", e)

            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Log.w(TAG, "Retrying streak alert (attempt ${runAttemptCount + 1}/$MAX_RETRY_ATTEMPTS)")
                Result.retry()
            } else {
                Log.e(TAG, "Max retry attempts reached, failing")
                Result.failure()
            }
        }
    }
}
