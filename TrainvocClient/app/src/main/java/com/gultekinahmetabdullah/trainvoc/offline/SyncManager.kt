package com.gultekinahmetabdullah.trainvoc.offline

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gultekinahmetabdullah.trainvoc.auth.AuthState
import com.gultekinahmetabdullah.trainvoc.auth.AuthRepository
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for coordinating sync operations.
 * Provides a simple interface for triggering sync from different parts of the app.
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
    private val featureFlagManager: FeatureFlagManager,
    private val connectivityManager: NetworkConnectivityManager
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val IMMEDIATE_SYNC_WORK = "immediate_sync_work"
    }

    /**
     * Initialize sync on app startup.
     * Schedules periodic sync if user is authenticated.
     */
    fun initialize() {
        if (isAuthenticatedAndEnabled()) {
            SyncWorker.schedule(context)
            Log.d(TAG, "Sync initialized")
        }
    }

    /**
     * Check if sync is enabled and user is authenticated.
     * Uses synchronous feature flag check.
     */
    fun isAuthenticatedAndEnabled(): Boolean {
        val isOfflineEnabled = featureFlagManager.isEnabledSync(FeatureFlag.OFFLINE_MODE)
        val isAuthenticated = authRepository.authState.value is AuthState.Authenticated ||
                authRepository.authState.value is AuthState.AuthenticatedOffline
        return isOfflineEnabled && isAuthenticated
    }

    /**
     * Queue a word update for sync.
     */
    suspend fun queueWordSync(
        wordId: String,
        wordData: Map<String, Any?>,
        action: SyncAction = SyncAction.UPDATE
    ) {
        if (!isAuthenticatedAndEnabled()) return

        syncRepository.queueAction(
            actionType = action,
            entityType = EntityType.WORD,
            entityId = wordId,
            entityData = wordData
        )
        triggerSyncIfOnline()
    }

    /**
     * Queue a statistic update for sync (e.g., after quiz).
     */
    suspend fun queueStatisticSync(
        statisticId: String,
        statisticData: Map<String, Any?>,
        action: SyncAction = SyncAction.UPDATE
    ) {
        if (!isAuthenticatedAndEnabled()) return

        syncRepository.queueAction(
            actionType = action,
            entityType = EntityType.STATISTIC,
            entityId = statisticId,
            entityData = statisticData,
            priority = 1 // Higher priority for statistics
        )
        triggerSyncIfOnline()
    }

    /**
     * Queue an exam result for sync.
     */
    suspend fun queueExamSync(
        examId: String,
        examData: Map<String, Any?>,
        action: SyncAction = SyncAction.CREATE
    ) {
        if (!isAuthenticatedAndEnabled()) return

        syncRepository.queueAction(
            actionType = action,
            entityType = EntityType.EXAM,
            entityId = examId,
            entityData = examData,
            priority = 2 // High priority for exam results
        )
        triggerSyncIfOnline()
    }

    /**
     * Queue an achievement unlock for sync.
     */
    suspend fun queueAchievementSync(
        achievementId: String,
        achievementData: Map<String, Any?>,
        action: SyncAction = SyncAction.CREATE
    ) {
        if (!isAuthenticatedAndEnabled()) return

        syncRepository.queueAction(
            actionType = action,
            entityType = EntityType.ACHIEVEMENT,
            entityId = achievementId,
            entityData = achievementData,
            priority = 2 // High priority for achievements
        )
        triggerSyncIfOnline()
    }

    /**
     * Queue a user profile update for sync.
     */
    suspend fun queueProfileSync(
        userId: String,
        profileData: Map<String, Any?>
    ) {
        if (!isAuthenticatedAndEnabled()) return

        syncRepository.queueAction(
            actionType = SyncAction.UPDATE,
            entityType = EntityType.USER_PROFILE,
            entityId = userId,
            entityData = profileData
        )
        triggerSyncIfOnline()
    }

    /**
     * Trigger immediate sync if device is online.
     */
    fun triggerSyncIfOnline() {
        if (!isAuthenticatedAndEnabled()) return

        if (connectivityManager.isCurrentlyOnline()) {
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_SYNC_WORK,
                ExistingWorkPolicy.KEEP, // Don't duplicate if already running
                syncRequest
            )
            Log.d(TAG, "Immediate sync triggered")
        }
    }

    /**
     * Force sync now (manual trigger from settings).
     */
    fun forceSync() {
        if (!isAuthenticatedAndEnabled()) {
            Log.w(TAG, "Cannot sync: not authenticated or offline mode disabled")
            return
        }

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_SYNC_WORK,
            ExistingWorkPolicy.REPLACE, // Replace any pending work
            syncRequest
        )
        Log.d(TAG, "Force sync triggered")
    }

    /**
     * Get pending sync count as a Flow.
     */
    fun getPendingCountFlow(): Flow<Int> = syncRepository.getPendingCountFlow()

    /**
     * Get pending sync count.
     */
    suspend fun getPendingCount(): Int = syncRepository.getPendingCount()

    /**
     * Get sync statistics.
     */
    suspend fun getStatistics(): SyncStatistics = syncRepository.getSyncStatistics()

    /**
     * Enable sync after login.
     */
    fun enableSync() {
        SyncWorker.schedule(context)
        Log.d(TAG, "Sync enabled")
    }

    /**
     * Disable sync on logout.
     */
    fun disableSync() {
        SyncWorker.cancel(context)
        Log.d(TAG, "Sync disabled")
    }
}
