package com.gultekinahmetabdullah.trainvoc.offline

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import android.util.Log

/**
 * Background worker that syncs queued actions when device comes online
 * Runs periodically and when network becomes available
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository,
    private val connectivityManager: NetworkConnectivityManager,
    private val featureFlags: FeatureFlagManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Check if offline mode is enabled
            if (!featureFlags.isEnabled(FeatureFlag.OFFLINE_MODE)) {
                Log.d(TAG, "Offline mode disabled, skipping sync")
                return@withContext Result.success()
            }

            // Check if we're online
            if (!connectivityManager.isCurrentlyOnline()) {
                Log.d(TAG, "Device is offline, skipping sync")
                return@withContext Result.retry()
            }

            // Get pending syncs
            val pendingSyncs = syncRepository.getRetryableSyncs(maxAttempts = 5)

            if (pendingSyncs.isEmpty()) {
                Log.d(TAG, "No pending syncs")
                return@withContext Result.success()
            }

            Log.d(TAG, "Processing ${pendingSyncs.size} pending syncs")

            var successCount = 0
            var failureCount = 0

            // Process each sync
            pendingSyncs.forEach { sync ->
                try {
                    // Process the sync based on entity type
                    when (sync.entityType) {
                        EntityType.WORD -> processSyncedWord(sync)
                        EntityType.STATISTIC -> processSyncedStatistic(sync)
                        EntityType.EXAM -> processSyncedExam(sync)
                        EntityType.ACHIEVEMENT -> processSyncedAchievement(sync)
                        EntityType.USER_PROFILE -> processSyncedUserProfile(sync)
                        EntityType.BACKUP -> processSyncedBackup(sync)
                        EntityType.FEATURE_FLAG -> processSyncedFeatureFlag(sync)
                    }

                    // Mark as synced
                    syncRepository.markSynced(sync.id)
                    successCount++

                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync item ${sync.id}: ${e.message}")
                    syncRepository.recordFailedAttempt(sync.id, e.message ?: "Unknown error")
                    failureCount++
                }
            }

            Log.d(TAG, "Sync complete: $successCount succeeded, $failureCount failed")

            // Cleanup old synced items
            syncRepository.cleanupOldSyncedItems(daysToKeep = 7)

            // Return success if at least some items synced
            if (successCount > 0 || failureCount == 0) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Sync worker failed: ${e.message}")
            Result.retry()
        }
    }

    /**
     * Process synced word data
     * In a real implementation, this would sync to a backend server
     */
    private suspend fun processSyncedWord(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        // For now, we just log and mark as synced since we don't have a backend yet
        Log.d(TAG, "Would sync word: ${sync.entityId} (${sync.actionType})")
    }

    private suspend fun processSyncedStatistic(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        Log.d(TAG, "Would sync statistic: ${sync.entityId} (${sync.actionType})")
    }

    private suspend fun processSyncedExam(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        Log.d(TAG, "Would sync exam: ${sync.entityId} (${sync.actionType})")
    }

    private suspend fun processSyncedAchievement(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        Log.d(TAG, "Would sync achievement: ${sync.entityId} (${sync.actionType})")
    }

    private suspend fun processSyncedUserProfile(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        Log.d(TAG, "Would sync user profile: ${sync.entityId} (${sync.actionType})")
    }

    private suspend fun processSyncedBackup(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        Log.d(TAG, "Would sync backup: ${sync.entityId} (${sync.actionType})")
    }

    private suspend fun processSyncedFeatureFlag(sync: SyncQueue) {
        // TODO: Implement actual sync to backend server
        Log.d(TAG, "Would sync feature flag: ${sync.entityId} (${sync.actionType})")
    }

    companion object {
        private const val TAG = "SyncWorker"
        private const val WORK_NAME = "offline_sync_worker"

        /**
         * Schedule periodic sync worker
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )

            Log.d(TAG, "Sync worker scheduled")
        }

        /**
         * Cancel sync worker
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Sync worker cancelled")
        }
    }
}
