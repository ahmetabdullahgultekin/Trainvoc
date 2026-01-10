package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper

/**
 * Worker for daily reminder notifications
 *
 * Sends a daily notification to encourage users to practice
 * their vocabulary learning.
 *
 * Error Handling:
 * - Returns Result.retry() on transient failures (network, etc.)
 * - Returns Result.failure() on permanent failures
 * - Logs all errors for debugging
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "DailyReminderWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting daily reminder worker")

            // Check if daily reminders are enabled
            val sharedPreferences =
                applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val dailyRemindersEnabled = sharedPreferences.getBoolean("daily_reminders_enabled", true)

            if (!dailyRemindersEnabled) {
                Log.i(TAG, "Daily reminders disabled, skipping")
                return Result.success()
            }

            // Send notification
            NotificationHelper.sendDailyReminder(applicationContext)

            Log.d(TAG, "Daily reminder sent successfully")
            Result.success()

        } catch (e: SecurityException) {
            // Notification permission denied - permanent failure
            Log.e(TAG, "Security exception: Notification permission may be denied", e)
            Result.failure()

        } catch (e: IllegalStateException) {
            // Invalid state - permanent failure
            Log.e(TAG, "Illegal state exception in daily reminder", e)
            Result.failure()

        } catch (e: Exception) {
            // Generic error - retry if under max attempts
            Log.e(TAG, "Error sending daily reminder", e)

            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Log.w(TAG, "Retrying daily reminder (attempt ${runAttemptCount + 1}/$MAX_RETRY_ATTEMPTS)")
                Result.retry()
            } else {
                Log.e(TAG, "Max retry attempts reached, failing")
                Result.failure()
            }
        }
    }
}
