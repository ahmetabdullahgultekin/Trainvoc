package com.gultekinahmetabdullah.trainvoc.features.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for feature flag operations
 */
@Dao
interface FeatureFlagDao {

    // ========== GLOBAL FEATURE FLAGS (Admin Controls) ==========

    @Query("SELECT * FROM feature_flags_global")
    fun getAllGlobalFlags(): Flow<List<GlobalFeatureFlag>>

    @Query("SELECT * FROM feature_flags_global WHERE feature_key = :featureKey")
    suspend fun getGlobalFlag(featureKey: String): GlobalFeatureFlag?

    @Query("SELECT * FROM feature_flags_global WHERE feature_key = :featureKey")
    fun getGlobalFlagFlow(featureKey: String): Flow<GlobalFeatureFlag?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalFlag(flag: GlobalFeatureFlag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalFlags(flags: List<GlobalFeatureFlag>)

    @Update
    suspend fun updateGlobalFlag(flag: GlobalFeatureFlag)

    @Query("UPDATE feature_flags_global SET enabled = :enabled WHERE feature_key = :featureKey")
    suspend fun setGlobalEnabled(featureKey: String, enabled: Boolean)

    @Query("UPDATE feature_flags_global SET rollout_percentage = :percentage WHERE feature_key = :featureKey")
    suspend fun setRolloutPercentage(featureKey: String, percentage: Int)

    @Query("UPDATE feature_flags_global SET max_daily_usage = :maxUsage WHERE feature_key = :featureKey")
    suspend fun setMaxDailyUsage(featureKey: String, maxUsage: Int?)

    @Query("UPDATE feature_flags_global SET current_daily_usage = current_daily_usage + :increment WHERE feature_key = :featureKey")
    suspend fun incrementDailyUsage(featureKey: String, increment: Int)

    @Query("UPDATE feature_flags_global SET current_daily_usage = 0, last_reset_date = :resetTime WHERE feature_key = :featureKey")
    suspend fun resetDailyUsage(featureKey: String, resetTime: Long = System.currentTimeMillis())

    @Query("UPDATE feature_flags_global SET current_daily_usage = 0, last_reset_date = :resetTime")
    suspend fun resetAllDailyUsage(resetTime: Long = System.currentTimeMillis())

    @Query("UPDATE feature_flags_global SET total_cost = total_cost + :cost WHERE feature_key = :featureKey")
    suspend fun addCost(featureKey: String, cost: Double)

    // ========== USER FEATURE FLAGS (User Preferences) ==========

    @Query("SELECT * FROM feature_flags_user")
    fun getAllUserFlags(): Flow<List<UserFeatureFlag>>

    @Query("SELECT * FROM feature_flags_user WHERE feature_key = :featureKey")
    suspend fun getUserFlag(featureKey: String): UserFeatureFlag?

    @Query("SELECT * FROM feature_flags_user WHERE feature_key = :featureKey")
    fun getUserFlagFlow(featureKey: String): Flow<UserFeatureFlag?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFlag(flag: UserFeatureFlag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFlags(flags: List<UserFeatureFlag>)

    @Update
    suspend fun updateUserFlag(flag: UserFeatureFlag)

    @Query("UPDATE feature_flags_user SET user_enabled = :enabled WHERE feature_key = :featureKey")
    suspend fun setUserEnabled(featureKey: String, enabled: Boolean)

    @Query("UPDATE feature_flags_user SET has_used_feature = 1, usage_count = usage_count + 1, last_used = :timestamp WHERE feature_key = :featureKey")
    suspend fun recordUsage(featureKey: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE feature_flags_user SET feedback_provided = 1, feedback_rating = :rating WHERE feature_key = :featureKey")
    suspend fun setFeedback(featureKey: String, rating: Int)

    // ========== USAGE LOGS (Analytics & Cost Tracking) ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageLog(log: FeatureUsageLog)

    @Query("SELECT * FROM feature_usage_log WHERE feature_key = :featureKey ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentUsageLogs(featureKey: String, limit: Int = 100): List<FeatureUsageLog>

    @Query("SELECT * FROM feature_usage_log WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getUsageLogsByTimeRange(startTime: Long, endTime: Long): List<FeatureUsageLog>

    @Query("SELECT * FROM feature_usage_log WHERE feature_key = :featureKey AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getFeatureUsageInRange(featureKey: String, startTime: Long, endTime: Long): List<FeatureUsageLog>

    @Query("SELECT SUM(estimated_cost) FROM feature_usage_log WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getTotalCostInRange(startTime: Long, endTime: Long): Double?

    @Query("SELECT SUM(estimated_cost) FROM feature_usage_log WHERE feature_key = :featureKey AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getFeatureCostInRange(featureKey: String, startTime: Long, endTime: Long): Double?

    @Query("SELECT SUM(api_calls_made) FROM feature_usage_log WHERE feature_key = :featureKey AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getFeatureApiCallsInRange(featureKey: String, startTime: Long, endTime: Long): Int?

    @Query("SELECT COUNT(*) FROM feature_usage_log WHERE feature_key = :featureKey AND success = 1 AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getSuccessfulUsageCount(featureKey: String, startTime: Long, endTime: Long): Int

    @Query("SELECT COUNT(*) FROM feature_usage_log WHERE feature_key = :featureKey AND success = 0 AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getFailedUsageCount(featureKey: String, startTime: Long, endTime: Long): Int

    @Query("DELETE FROM feature_usage_log WHERE timestamp < :beforeTime")
    suspend fun deleteOldLogs(beforeTime: Long)

    // ========== COMBINED QUERIES ==========

    /**
     * Check if feature is enabled at both global and user level
     */
    @Transaction
    suspend fun isFeatureFullyEnabled(featureKey: String): Boolean {
        val globalFlag = getGlobalFlag(featureKey) ?: return false
        if (!globalFlag.enabled) return false

        // Check daily limit if exists
        if (globalFlag.maxDailyUsage != null) {
            if (globalFlag.currentDailyUsage >= globalFlag.maxDailyUsage) {
                return false
            }
        }

        return true
    }

    /**
     * Initialize all feature flags with defaults
     */
    @Transaction
    suspend fun initializeDefaultFlags(globalFlags: List<GlobalFeatureFlag>, userFlags: List<UserFeatureFlag>) {
        insertGlobalFlags(globalFlags)
        insertUserFlags(userFlags)
    }
}
