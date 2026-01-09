package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Worker that sends interactive word quiz notifications.
 *
 * Features:
 * - Respects quiet hours settings
 * - Uses filtered word selection based on user preferences
 * - Sends notifications with action buttons for interactive learning
 *
 * The actual notification building is handled by NotificationHelper.sendWordQuizNotification()
 * which creates notifications with "I Know It", "Show Answer", and "Skip" buttons.
 *
 * Error Handling:
 * - Returns Result.retry() on transient failures (network, etc.)
 * - Returns Result.failure() on permanent failures
 * - Logs all errors for debugging
 */
class WordNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "WordNotificationWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting word notification worker")

            val prefs = NotificationPreferences.getInstance(applicationContext)

            // Check if notifications are enabled
            if (!prefs.wordQuizEnabled) {
                Log.i(TAG, "Word quiz notifications disabled, skipping")
                return@withContext Result.success()
            }

            // Check quiet hours
            if (isQuietHours(prefs)) {
                Log.i(TAG, "Currently in quiet hours, skipping notification")
                return@withContext Result.success()
            }

            // Send the interactive word quiz notification
            NotificationHelper.sendWordQuizNotification(applicationContext)

            Log.d(TAG, "Word quiz notification sent successfully")
            Result.success()

        } catch (e: SecurityException) {
            // Notification permission denied - permanent failure
            Log.e(TAG, "Security exception: Notification permission may be denied", e)
            Result.failure()

        } catch (e: IllegalStateException) {
            // Invalid state - permanent failure
            Log.e(TAG, "Illegal state exception in word notification", e)
            Result.failure()

        } catch (e: Exception) {
            // Generic error - retry if under max attempts
            Log.e(TAG, "Error in word notification worker", e)

            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Log.w(TAG, "Retrying word notification (attempt ${runAttemptCount + 1}/$MAX_RETRY_ATTEMPTS)")
                Result.retry()
            } else {
                Log.e(TAG, "Max retry attempts reached, failing")
                Result.failure()
            }
        }
    }

    /**
     * Check if current time is within quiet hours
     */
    private fun isQuietHours(prefs: NotificationPreferences): Boolean {
        if (!prefs.quietHoursEnabled) return false

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        val startHour = prefs.quietHoursStart
        val endHour = prefs.quietHoursEnd

        // Handle overnight quiet hours (e.g., 22:00 to 08:00)
        return if (startHour > endHour) {
            // Quiet hours span midnight
            currentHour >= startHour || currentHour < endHour
        } else {
            // Quiet hours within same day
            currentHour >= startHour && currentHour < endHour
        }
    }
}
