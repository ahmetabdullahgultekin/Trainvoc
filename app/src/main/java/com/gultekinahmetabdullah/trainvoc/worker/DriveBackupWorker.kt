package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackupResult
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackupService
import com.gultekinahmetabdullah.trainvoc.cloud.GoogleAuthManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker for automatic Google Drive backups
 *
 * This worker runs periodically (e.g., daily) to automatically backup user data to Google Drive.
 * It only runs when:
 * - User is signed in to Google Drive
 * - Device is connected to unmetered network (WiFi)
 * - Battery is not low
 *
 * Features:
 * - Automatic daily backups
 * - WiFi-only to save data
 * - Battery-conscious (only when not low)
 * - Error handling and retry logic
 * - Cleanup of old backups
 *
 * WorkManager Configuration:
 * ```kotlin
 * val constraints = Constraints.Builder()
 *     .setRequiredNetworkType(NetworkType.UNMETERED)  // WiFi only
 *     .setRequiresBatteryNotLow(true)                  // Good battery level
 *     .build()
 *
 * val request = PeriodicWorkRequestBuilder<DriveBackupWorker>(
 *     repeatInterval = 1,
 *     repeatIntervalTimeUnit = TimeUnit.DAYS
 * )
 *     .setConstraints(constraints)
 *     .setBackoffCriteria(
 *         BackoffPolicy.EXPONENTIAL,
 *         WorkRequest.MIN_BACKOFF_MILLIS,
 *         TimeUnit.MILLISECONDS
 *     )
 *     .build()
 *
 * WorkManager.getInstance(context)
 *     .enqueueUniquePeriodicWork(
 *         "drive_auto_backup",
 *         ExistingPeriodicWorkPolicy.KEEP,
 *         request
 *     )
 * ```
 */
@HiltWorker
class DriveBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authManager: GoogleAuthManager,
    private val backupService: DriveBackupService
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "DriveBackupWorker"
        const val WORK_NAME = "drive_auto_backup"

        // Input parameters
        const val KEY_CLEANUP_OLD_BACKUPS = "cleanup_old_backups"

        // Output parameters
        const val KEY_BACKUP_FILE_ID = "backup_file_id"
        const val KEY_BACKUP_SIZE = "backup_size"
        const val KEY_WORD_COUNT = "word_count"
        const val KEY_DELETED_COUNT = "deleted_count"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting automatic Drive backup (attempt ${runAttemptCount + 1})")

            // Check if user is signed in
            if (!authManager.isSignedIn()) {
                Log.w(TAG, "Auto backup skipped: User not signed in to Google Drive")
                return Result.success()  // Not a failure - just skip this time
            }

            // Perform backup
            when (val result = backupService.uploadBackup()) {
                is DriveBackupResult.Success -> {
                    Log.i(TAG, "Auto backup successful: ${result.fileName}")

                    // Cleanup old backups if requested
                    val shouldCleanup = inputData.getBoolean(KEY_CLEANUP_OLD_BACKUPS, true)
                    var deletedCount = 0

                    if (shouldCleanup) {
                        Log.d(TAG, "Cleaning up old backups...")
                        deletedCount = backupService.cleanupOldBackups()
                    }

                    // Return success with output data
                    val outputData = androidx.work.Data.Builder()
                        .putString(KEY_BACKUP_FILE_ID, result.fileId)
                        .putLong(KEY_BACKUP_SIZE, result.sizeBytes)
                        .putInt(KEY_WORD_COUNT, result.wordCount)
                        .putInt(KEY_DELETED_COUNT, deletedCount)
                        .build()

                    Result.success(outputData)
                }

                is DriveBackupResult.Failure -> {
                    Log.e(TAG, "Auto backup failed: ${result.error}")

                    // Determine if we should retry
                    val shouldRetry = shouldRetryOnError(result.error)

                    if (shouldRetry && runAttemptCount < 3) {
                        Log.d(TAG, "Will retry auto backup (attempt ${runAttemptCount + 1}/3)")
                        Result.retry()
                    } else {
                        Log.w(TAG, "Auto backup failed permanently after ${runAttemptCount + 1} attempts")
                        Result.failure()
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in auto backup worker", e)

            // Retry on unexpected errors
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Determine if we should retry based on error message
     *
     * Retryable errors:
     * - Network errors
     * - Timeout errors
     * - Temporary API errors
     *
     * Non-retryable errors:
     * - Authentication errors
     * - Quota exceeded
     * - Invalid data
     */
    private fun shouldRetryOnError(error: String): Boolean {
        val retryableKeywords = listOf(
            "network",
            "timeout",
            "connection",
            "unavailable",
            "temporary"
        )

        val nonRetryableKeywords = listOf(
            "not signed in",
            "authentication",
            "permission",
            "quota",
            "invalid"
        )

        val errorLower = error.lowercase()

        // Don't retry if non-retryable keyword found
        if (nonRetryableKeywords.any { errorLower.contains(it) }) {
            return false
        }

        // Retry if retryable keyword found
        if (retryableKeywords.any { errorLower.contains(it) }) {
            return true
        }

        // Default: retry unknown errors
        return true
    }

    /**
     * Called when worker is stopped unexpectedly
     *
     * Perform cleanup if needed
     */
    override suspend fun onStopped() {
        super.onStopped()
        Log.d(TAG, "Auto backup worker stopped")
    }
}
