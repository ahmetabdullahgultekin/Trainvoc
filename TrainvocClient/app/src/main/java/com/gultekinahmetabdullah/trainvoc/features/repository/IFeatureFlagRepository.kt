package com.gultekinahmetabdullah.trainvoc.features.repository

import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for feature flag operations
 * Abstracts data layer for testability and flexibility
 */
interface IFeatureFlagRepository {

    // ========== Runtime Checks ==========

    /**
     * Check if feature is enabled globally and for the user
     * Takes into account: global enabled, rollout percentage, user preference, daily limits
     */
    suspend fun isFeatureEnabled(feature: FeatureFlag): Boolean

    /**
     * Check if feature is enabled for the current user (includes rollout percentage)
     */
    suspend fun isFeatureEnabledForUser(feature: FeatureFlag): Boolean

    // ========== Admin Controls (Global) ==========

    suspend fun setGlobalFeatureEnabled(feature: FeatureFlag, enabled: Boolean)

    suspend fun setRolloutPercentage(feature: FeatureFlag, percentage: Int)

    suspend fun setMaxDailyUsage(feature: FeatureFlag, maxUsage: Int?)

    fun getAllGlobalFlags(): Flow<List<GlobalFeatureFlag>>

    suspend fun getGlobalFlag(feature: FeatureFlag): GlobalFeatureFlag?

    suspend fun resetDailyUsage()

    suspend fun resetAllDailyUsage()

    // ========== User Preferences ==========

    suspend fun setUserFeatureEnabled(feature: FeatureFlag, enabled: Boolean)

    fun getAllUserFlags(): Flow<List<UserFeatureFlag>>

    suspend fun getUserFlag(feature: FeatureFlag): UserFeatureFlag?

    suspend fun getUserFeaturePreference(feature: FeatureFlag): Boolean?

    // ========== Usage Tracking ==========

    /**
     * Track feature usage for analytics and cost monitoring
     */
    suspend fun trackFeatureUsage(
        feature: FeatureFlag,
        apiCalls: Int = 0,
        estimatedCost: Double = 0.0,
        success: Boolean = true,
        errorMessage: String? = null
    )

    /**
     * Get usage statistics for a feature within a time range
     */
    suspend fun getFeatureUsageStats(
        feature: FeatureFlag,
        startDate: Long,
        endDate: Long
    ): FeatureUsageStats

    // ========== Cost Management ==========

    suspend fun getTotalCostToday(): Double

    suspend fun getTotalCostThisMonth(): Double

    suspend fun getCostBreakdownByFeature(): Map<FeatureFlag, Double>

    // ========== Initialization ==========

    /**
     * Initialize feature flags with default values
     * Called on first app launch or when new features are added
     */
    suspend fun initializeFeatureFlags()
}

/**
 * Statistics for a feature's usage
 */
data class FeatureUsageStats(
    val totalCalls: Int,
    val totalCost: Double,
    val successRate: Double,  // % successful (0.0-1.0)
    val apiCalls: Int,
    val failureCount: Int
)
