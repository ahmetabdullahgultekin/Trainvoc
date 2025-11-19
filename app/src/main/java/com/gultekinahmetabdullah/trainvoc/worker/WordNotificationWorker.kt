package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
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
 */
class WordNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val prefs = NotificationPreferences.getInstance(applicationContext)

        // Check if notifications are enabled
        if (!prefs.wordQuizEnabled) {
            return@withContext Result.success()
        }

        // Check quiet hours
        if (isQuietHours(prefs)) {
            return@withContext Result.success()
        }

        // Send the interactive word quiz notification
        NotificationHelper.sendWordQuizNotification(applicationContext)

        Result.success()
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
