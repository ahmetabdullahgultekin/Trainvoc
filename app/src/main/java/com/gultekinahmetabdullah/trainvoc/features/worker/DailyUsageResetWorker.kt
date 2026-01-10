package com.gultekinahmetabdullah.trainvoc.features.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker to reset daily usage counters for feature flags
 * Runs once per day at midnight to reset API call counters
 */
@HiltWorker
class DailyUsageResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val featureFlagManager: FeatureFlagManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Reset all daily usage counters
            featureFlagManager.resetDailyUsage()

            Result.success()
        } catch (e: Exception) {
            // Retry on failure
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "daily_usage_reset_worker"

        /**
         * Schedule the daily usage reset worker
         * Runs once per day at midnight
         */
        fun schedule(context: Context) {
            val currentTime = Calendar.getInstance()
            val midnight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If midnight has passed today, schedule for tomorrow
                if (before(currentTime)) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val initialDelay = midnight.timeInMillis - currentTime.timeInMillis

            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyUsageResetWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,  // Keep existing work if already scheduled
                dailyWorkRequest
            )
        }

        /**
         * Cancel the daily usage reset worker
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
