package com.gultekinahmetabdullah.trainvoc.offline

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit interface for sync API endpoints.
 * Connects to the backend sync service for offline-first data synchronization.
 */
interface SyncApiService {

    /**
     * Sync a batch of entities.
     */
    @POST("api/v1/sync/batch")
    suspend fun batchSync(
        @Header("Authorization") token: String,
        @Body request: BatchSyncRequest
    ): Response<BatchSyncResponse>

    /**
     * Sync a single word.
     */
    @POST("api/v1/sync/words")
    suspend fun syncWord(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Sync word statistics.
     */
    @POST("api/v1/sync/statistics")
    suspend fun syncStatistic(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Sync exam results.
     */
    @POST("api/v1/sync/exams")
    suspend fun syncExam(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Sync achievements.
     */
    @POST("api/v1/sync/achievements")
    suspend fun syncAchievement(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Sync user profile.
     */
    @POST("api/v1/sync/profile")
    suspend fun syncUserProfile(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Sync backup.
     */
    @POST("api/v1/sync/backup")
    suspend fun syncBackup(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Sync feature flags.
     */
    @POST("api/v1/sync/feature-flags")
    suspend fun syncFeatureFlag(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>

    /**
     * Get server changes since timestamp.
     */
    @GET("api/v1/sync/changes")
    suspend fun getServerChanges(
        @Header("Authorization") token: String,
        @Query("since") since: Long
    ): Response<ServerChangesResponse>

    /**
     * Get sync status.
     */
    @GET("api/v1/sync/status")
    suspend fun getSyncStatus(
        @Header("Authorization") token: String
    ): Response<SyncStatusResponse>

    companion object {
        const val BASE_URL = "https://api.trainvoc.com/"
    }
}

// ============ Request DTOs ============

data class SyncRequest(
    val entityType: String,
    val entityId: String,
    val data: Map<String, Any?>,
    val timestamp: Long,
    val action: String // CREATE, UPDATE, DELETE
)

data class BatchSyncRequest(
    val items: List<SyncRequest>,
    val clientTimestamp: Long,
    val deviceId: String
)

// ============ Response DTOs ============

data class SyncResponse(
    val success: Boolean,
    val message: String,
    val entityType: String,
    val entityId: String,
    val syncedAt: String,
    val conflicts: List<ConflictInfo>
)

data class BatchSyncResponse(
    val totalItems: Int,
    val successCount: Int,
    val failureCount: Int,
    val conflictCount: Int,
    val syncedAt: String,
    val results: List<SyncResponse>
)

data class ConflictInfo(
    val field: String,
    val localValue: Any?,
    val serverValue: Any?,
    val localTimestamp: Long,
    val serverTimestamp: Long
)

data class ServerChangesResponse(
    val changes: List<Map<String, Any>>,
    val serverTime: Long
)

data class SyncStatusResponse(
    val userId: Long,
    val lastSyncTime: Long,
    val entityStatus: Map<String, EntitySyncStatus>
)

data class EntitySyncStatus(
    val lastSync: Long,
    val pendingCount: Int
)
