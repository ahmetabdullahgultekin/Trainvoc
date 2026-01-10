package com.gultekinahmetabdullah.trainvoc.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for sync queue operations
 * Manages offline data synchronization
 */
@Dao
interface SyncQueueDao {

    // ========== Insert Operations ==========

    @Insert
    suspend fun insert(syncQueue: SyncQueue): Long

    @Insert
    suspend fun insertAll(syncQueues: List<SyncQueue>)

    // ========== Query Operations ==========

    @Query("SELECT * FROM sync_queue WHERE synced = 0 ORDER BY priority DESC, timestamp ASC")
    suspend fun getPendingSyncs(): List<SyncQueue>

    @Query("SELECT * FROM sync_queue WHERE synced = 0 ORDER BY priority DESC, timestamp ASC")
    fun getPendingSyncsFlow(): Flow<List<SyncQueue>>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE synced = 0")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM sync_queue WHERE synced = 0")
    fun getPendingCountFlow(): Flow<Int>

    @Query("SELECT * FROM sync_queue WHERE entityType = :entityType AND synced = 0 ORDER BY timestamp ASC")
    suspend fun getPendingSyncsByType(entityType: EntityType): List<SyncQueue>

    @Query("SELECT * FROM sync_queue WHERE entityId = :entityId AND synced = 0")
    suspend fun getPendingSyncsForEntity(entityId: String): List<SyncQueue>

    @Query("SELECT * FROM sync_queue WHERE id = :id")
    suspend fun getSyncById(id: Long): SyncQueue?

    // ========== Update Operations ==========

    @Update
    suspend fun update(syncQueue: SyncQueue)

    @Query("""
        UPDATE sync_queue
        SET synced = 1
        WHERE id = :id
    """)
    suspend fun markAsSynced(id: Long)

    @Query("""
        UPDATE sync_queue
        SET attemptCount = attemptCount + 1,
            lastAttempt = :timestamp,
            lastError = :error
        WHERE id = :id
    """)
    suspend fun recordFailedAttempt(id: Long, timestamp: Long, error: String)

    // ========== Delete Operations ==========

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sync_queue WHERE synced = 1")
    suspend fun deleteSyncedItems()

    @Query("DELETE FROM sync_queue WHERE synced = 1 AND timestamp < :beforeTimestamp")
    suspend fun deleteSyncedItemsBefore(beforeTimestamp: Long)

    @Query("DELETE FROM sync_queue")
    suspend fun deleteAll()

    // ========== Retry Management ==========

    @Query("""
        SELECT * FROM sync_queue
        WHERE synced = 0
        AND attemptCount < :maxAttempts
        ORDER BY priority DESC, timestamp ASC
    """)
    suspend fun getRetryableSyncs(maxAttempts: Int = 5): List<SyncQueue>

    @Query("""
        SELECT * FROM sync_queue
        WHERE synced = 0
        AND attemptCount >= :maxAttempts
    """)
    suspend fun getFailedSyncs(maxAttempts: Int = 5): List<SyncQueue>

    // ========== Statistics ==========

    @Query("SELECT COUNT(*) FROM sync_queue")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM sync_queue WHERE synced = 1")
    suspend fun getSyncedCount(): Int

    @Query("SELECT COUNT(*) FROM sync_queue WHERE synced = 0 AND attemptCount >= 5")
    suspend fun getFailedCount(): Int
}
