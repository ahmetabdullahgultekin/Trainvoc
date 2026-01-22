package com.gultekinahmetabdullah.trainvoc.offline

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing offline sync queue
 * Handles queueing actions for later sync and processing sync queue
 */
@Singleton
class SyncRepository @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    @PublishedApi internal val gson: Gson
) {

    // ========== Queue Management ==========

    /**
     * Queue an action for syncing when online
     */
    suspend fun queueAction(
        actionType: SyncAction,
        entityType: EntityType,
        entityId: String,
        entityData: Any,
        priority: Int = 0
    ): Result<Long> {
        return try {
            val jsonData = gson.toJson(entityData)

            val syncQueue = SyncQueue(
                actionType = actionType,
                entityType = entityType,
                entityId = entityId,
                entityData = jsonData,
                priority = priority
            )

            val id = syncQueueDao.insert(syncQueue)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all pending syncs
     */
    suspend fun getPendingSyncs(): List<SyncQueue> {
        return syncQueueDao.getPendingSyncs()
    }

    /**
     * Get pending sync count as Flow
     */
    fun getPendingCountFlow(): Flow<Int> {
        return syncQueueDao.getPendingCountFlow()
    }

    /**
     * Get pending sync count
     */
    suspend fun getPendingCount(): Int {
        return syncQueueDao.getPendingCount()
    }

    // ========== Sync Processing ==========

    /**
     * Mark a sync as completed
     */
    suspend fun markSynced(syncId: Long) {
        syncQueueDao.markAsSynced(syncId)
    }

    /**
     * Record a failed sync attempt
     */
    suspend fun recordFailedAttempt(syncId: Long, error: String) {
        syncQueueDao.recordFailedAttempt(
            id = syncId,
            timestamp = System.currentTimeMillis(),
            error = error
        )
    }

    /**
     * Get retryable syncs (haven't exceeded max attempts)
     */
    suspend fun getRetryableSyncs(maxAttempts: Int = 5): List<SyncQueue> {
        return syncQueueDao.getRetryableSyncs(maxAttempts)
    }

    /**
     * Get failed syncs (exceeded max attempts)
     */
    suspend fun getFailedSyncs(maxAttempts: Int = 5): List<SyncQueue> {
        return syncQueueDao.getFailedSyncs(maxAttempts)
    }

    // ========== Cleanup ==========

    /**
     * Delete successfully synced items older than timestamp
     */
    suspend fun cleanupOldSyncedItems(daysToKeep: Int = 7) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        syncQueueDao.deleteSyncedItemsBefore(cutoffTime)
    }

    /**
     * Delete a specific sync item
     */
    suspend fun deleteSyncItem(syncId: Long) {
        syncQueueDao.deleteById(syncId)
    }

    /**
     * Clear all synced items
     */
    suspend fun clearSyncedItems() {
        syncQueueDao.deleteSyncedItems()
    }

    // ========== Statistics ==========

    /**
     * Get sync statistics
     */
    suspend fun getSyncStatistics(): SyncStatistics {
        return SyncStatistics(
            total = syncQueueDao.getTotalCount(),
            pending = syncQueueDao.getPendingCount(),
            synced = syncQueueDao.getSyncedCount(),
            failed = syncQueueDao.getFailedCount()
        )
    }

    /**
     * Deserialize entity data from JSON
     */
    inline fun <reified T> deserializeEntity(syncQueue: SyncQueue): T? {
        return try {
            gson.fromJson(syncQueue.entityData, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Sync queue statistics
 */
data class SyncStatistics(
    val total: Int,
    val pending: Int,
    val synced: Int,
    val failed: Int
) {
    val successRate: Double get() = if (total > 0) synced.toDouble() / total * 100 else 0.0
}
