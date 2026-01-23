package com.gultekinahmetabdullah.trainvoc.offline

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

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
    private val featureFlags: FeatureFlagManager,
    private val syncApiService: SyncApiService,
    private val preferencesRepository: IPreferencesRepository,
    private val gson: Gson
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
     * Get authorization header with Bearer token.
     * Returns null if user is not logged in.
     */
    private fun getAuthHeader(): String? {
        val token = preferencesRepository.getAuthToken()
        return if (token != null) "Bearer $token" else null
    }

    /**
     * Convert sync queue data to a Map for the API request.
     */
    private fun parseDataJson(dataJson: String?): Map<String, Any?> {
        if (dataJson.isNullOrEmpty()) return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            gson.fromJson(dataJson, type) ?: emptyMap()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse data JSON: ${e.message}")
            emptyMap()
        }
    }

    /**
     * Create a SyncRequest from a SyncQueue item.
     */
    private fun createSyncRequest(sync: SyncQueue): SyncRequest {
        return SyncRequest(
            entityType = sync.entityType.name.lowercase(),
            entityId = sync.entityId,
            data = parseDataJson(sync.entityData),
            timestamp = sync.timestamp,
            action = sync.actionType.name
        )
    }

    /**
     * Process synced word data via the backend API.
     */
    private suspend fun processSyncedWord(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncWord(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("Word sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("Word sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "Word synced successfully: ${sync.entityId}")
    }

    private suspend fun processSyncedStatistic(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncStatistic(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("Statistic sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("Statistic sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "Statistic synced successfully: ${sync.entityId}")
    }

    private suspend fun processSyncedExam(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncExam(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("Exam sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("Exam sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "Exam synced successfully: ${sync.entityId}")
    }

    private suspend fun processSyncedAchievement(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncAchievement(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("Achievement sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("Achievement sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "Achievement synced successfully: ${sync.entityId}")
    }

    private suspend fun processSyncedUserProfile(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncUserProfile(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("User profile sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("User profile sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "User profile synced successfully: ${sync.entityId}")
    }

    private suspend fun processSyncedBackup(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncBackup(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("Backup sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("Backup sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "Backup synced successfully: ${sync.entityId}")
    }

    private suspend fun processSyncedFeatureFlag(sync: SyncQueue) {
        val authHeader = getAuthHeader()
            ?: throw IllegalStateException("User not logged in")

        val request = createSyncRequest(sync)
        val response = syncApiService.syncFeatureFlag(authHeader, request)

        if (!response.isSuccessful) {
            throw Exception("Feature flag sync failed: ${response.code()} - ${response.message()}")
        }

        val body = response.body()
        if (body?.success != true) {
            throw Exception("Feature flag sync rejected: ${body?.message ?: "Unknown error"}")
        }

        Log.d(TAG, "Feature flag synced successfully: ${sync.entityId}")
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
