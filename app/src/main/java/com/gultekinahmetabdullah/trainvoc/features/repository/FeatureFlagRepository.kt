package com.gultekinahmetabdullah.trainvoc.features.repository

import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.FeatureFlagDao
import com.gultekinahmetabdullah.trainvoc.features.database.FeatureUsageLog
import com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Implementation of feature flag repository
 * Manages feature flags using Room database
 */
@Singleton
class FeatureFlagRepository @Inject constructor(
    private val dao: FeatureFlagDao
) : IFeatureFlagRepository {

    // ========== Runtime Checks ==========

    override suspend fun isFeatureEnabled(feature: FeatureFlag): Boolean {
        // 1. Check global flag
        val globalFlag = dao.getGlobalFlag(feature.key) ?: run {
            // If not initialized, return default
            return feature.defaultEnabled
        }

        if (!globalFlag.enabled) {
            return false
        }

        // 2. Check rollout percentage (A/B testing)
        if (globalFlag.rolloutPercentage < 100) {
            // Use deterministic random based on feature key for consistency
            val userPercentile = Random(feature.key.hashCode()).nextInt(100)
            if (userPercentile >= globalFlag.rolloutPercentage) {
                return false
            }
        }

        // 3. Check user preference (if not admin-only)
        if (!feature.adminOnly) {
            val userPref = getUserFeaturePreference(feature)
            if (userPref == false) {
                return false
            }
        }

        // 4. Check daily limits (for features with cost)
        if (feature.hasCost) {
            val maxUsage = globalFlag.maxDailyUsage
            if (maxUsage != null && globalFlag.currentDailyUsage >= maxUsage) {
                return false
            }
        }

        return true
    }

    override suspend fun isFeatureEnabledForUser(feature: FeatureFlag): Boolean {
        val globalFlag = dao.getGlobalFlag(feature.key) ?: return feature.defaultEnabled

        if (!globalFlag.enabled) return false

        // Check rollout percentage
        if (globalFlag.rolloutPercentage < 100) {
            val userPercentile = Random(feature.key.hashCode()).nextInt(100)
            if (userPercentile >= globalFlag.rolloutPercentage) {
                return false
            }
        }

        return true
    }

    // ========== Admin Controls (Global) ==========

    override suspend fun setGlobalFeatureEnabled(feature: FeatureFlag, enabled: Boolean) {
        val existing = dao.getGlobalFlag(feature.key)

        if (existing != null) {
            dao.updateGlobalFlag(
                existing.copy(
                    enabled = enabled,
                    lastModified = System.currentTimeMillis()
                )
            )
        } else {
            // Create new global flag
            dao.insertGlobalFlag(
                GlobalFeatureFlag(
                    featureKey = feature.key,
                    enabled = enabled,
                    lastResetDate = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun setRolloutPercentage(feature: FeatureFlag, percentage: Int) {
        require(percentage in 0..100) { "Rollout percentage must be between 0 and 100" }

        val existing = dao.getGlobalFlag(feature.key)

        if (existing != null) {
            dao.updateGlobalFlag(
                existing.copy(
                    rolloutPercentage = percentage,
                    lastModified = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertGlobalFlag(
                GlobalFeatureFlag(
                    featureKey = feature.key,
                    enabled = feature.defaultEnabled,
                    rolloutPercentage = percentage,
                    lastResetDate = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun setMaxDailyUsage(feature: FeatureFlag, maxUsage: Int?) {
        val existing = dao.getGlobalFlag(feature.key)

        if (existing != null) {
            dao.updateGlobalFlag(
                existing.copy(
                    maxDailyUsage = maxUsage,
                    lastModified = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertGlobalFlag(
                GlobalFeatureFlag(
                    featureKey = feature.key,
                    enabled = feature.defaultEnabled,
                    maxDailyUsage = maxUsage,
                    lastResetDate = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }

    override fun getAllGlobalFlags(): Flow<List<GlobalFeatureFlag>> {
        return dao.getAllGlobalFlags()
    }

    override suspend fun getGlobalFlag(feature: FeatureFlag): GlobalFeatureFlag? {
        return dao.getGlobalFlag(feature.key)
    }

    override suspend fun resetDailyUsage() {
        val currentTime = System.currentTimeMillis()
        dao.resetAllDailyUsage(currentTime)
    }

    override suspend fun resetAllDailyUsage() {
        dao.resetAllDailyUsage(System.currentTimeMillis())
    }

    // ========== User Preferences ==========

    override suspend fun setUserFeatureEnabled(feature: FeatureFlag, enabled: Boolean) {
        val existing = dao.getUserFlag(feature.key)

        if (existing != null) {
            dao.updateUserFlag(
                existing.copy(userEnabled = enabled)
            )
        } else {
            dao.insertUserFlag(
                UserFeatureFlag(
                    featureKey = feature.key,
                    userEnabled = enabled
                )
            )
        }
    }

    override fun getAllUserFlags(): Flow<List<UserFeatureFlag>> {
        return dao.getAllUserFlags()
    }

    override suspend fun getUserFlag(feature: FeatureFlag): UserFeatureFlag? {
        return dao.getUserFlag(feature.key)
    }

    override suspend fun getUserFeaturePreference(feature: FeatureFlag): Boolean? {
        return dao.getUserFlag(feature.key)?.userEnabled
    }

    // ========== Usage Tracking ==========

    override suspend fun trackFeatureUsage(
        feature: FeatureFlag,
        apiCalls: Int,
        estimatedCost: Double,
        success: Boolean,
        errorMessage: String?
    ) {
        // Insert usage log
        dao.insertUsageLog(
            FeatureUsageLog(
                featureKey = feature.key,
                apiCallsMade = apiCalls,
                estimatedCost = estimatedCost,
                success = success,
                errorMessage = errorMessage
            )
        )

        // Update global flag stats
        if (apiCalls > 0) {
            dao.incrementDailyUsage(feature.key, apiCalls)
        }

        if (estimatedCost > 0) {
            dao.addCost(feature.key, estimatedCost)
        }

        // Update user flag stats
        dao.recordUsage(feature.key)
    }

    override suspend fun getFeatureUsageStats(
        feature: FeatureFlag,
        startDate: Long,
        endDate: Long
    ): FeatureUsageStats {
        val logs = dao.getFeatureUsageInRange(feature.key, startDate, endDate)

        val totalCalls = logs.size
        val totalCost = logs.sumOf { it.estimatedCost }
        val apiCalls = logs.sumOf { it.apiCallsMade }
        val successCount = logs.count { it.success }
        val failureCount = logs.count { !it.success }
        val successRate = if (totalCalls > 0) successCount.toDouble() / totalCalls else 0.0

        return FeatureUsageStats(
            totalCalls = totalCalls,
            totalCost = totalCost,
            successRate = successRate,
            apiCalls = apiCalls,
            failureCount = failureCount
        )
    }

    // ========== Cost Management ==========

    override suspend fun getTotalCostToday(): Double {
        val startOfDay = getStartOfDay()
        val now = System.currentTimeMillis()
        return dao.getTotalCostInRange(startOfDay, now) ?: 0.0
    }

    override suspend fun getTotalCostThisMonth(): Double {
        val startOfMonth = getStartOfMonth()
        val now = System.currentTimeMillis()
        return dao.getTotalCostInRange(startOfMonth, now) ?: 0.0
    }

    override suspend fun getCostBreakdownByFeature(): Map<FeatureFlag, Double> {
        val startOfMonth = getStartOfMonth()
        val now = System.currentTimeMillis()

        return FeatureFlag.values().associateWith { feature ->
            dao.getFeatureCostInRange(feature.key, startOfMonth, now) ?: 0.0
        }
    }

    // ========== Initialization ==========

    override suspend fun initializeFeatureFlags() {
        // Check if already initialized
        val existingFlags = dao.getAllGlobalFlags().first()

        if (existingFlags.isEmpty()) {
            // First time initialization
            val globalFlags = FeatureFlag.values().map { feature ->
                GlobalFeatureFlag(
                    featureKey = feature.key,
                    enabled = feature.defaultEnabled,
                    rolloutPercentage = 100,
                    maxDailyUsage = getDefaultDailyLimit(feature),
                    lastResetDate = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )
            }

            val userFlags = FeatureFlag.values()
                .filter { !it.adminOnly }  // Only user-configurable features
                .map { feature ->
                    UserFeatureFlag(
                        featureKey = feature.key,
                        userEnabled = true  // Default to enabled
                    )
                }

            dao.initializeDefaultFlags(globalFlags, userFlags)
        } else {
            // Add any new features that don't exist yet
            val existingKeys = existingFlags.map { it.featureKey }.toSet()
            val newFeatures = FeatureFlag.values().filter { it.key !in existingKeys }

            if (newFeatures.isNotEmpty()) {
                val newGlobalFlags = newFeatures.map { feature ->
                    GlobalFeatureFlag(
                        featureKey = feature.key,
                        enabled = feature.defaultEnabled,
                        rolloutPercentage = 100,
                        maxDailyUsage = getDefaultDailyLimit(feature),
                        lastResetDate = System.currentTimeMillis(),
                        lastModified = System.currentTimeMillis()
                    )
                }

                val newUserFlags = newFeatures
                    .filter { !it.adminOnly }
                    .map { feature ->
                        UserFeatureFlag(
                            featureKey = feature.key,
                            userEnabled = true
                        )
                    }

                dao.insertGlobalFlags(newGlobalFlags)
                dao.insertUserFlags(newUserFlags)
            }
        }
    }

    // ========== Helper Methods ==========

    private fun getStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getStartOfMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Get default daily API call limits based on estimated costs
     */
    private fun getDefaultDailyLimit(feature: FeatureFlag): Int? {
        if (!feature.hasCost) return null

        return when (feature) {
            FeatureFlag.AUDIO_PRONUNCIATION,
            FeatureFlag.TEXT_TO_SPEECH -> 10000  // TTS: $0.001 per call = $10/day max

            FeatureFlag.SPEECH_RECOGNITION,
            FeatureFlag.PRONUNCIATION_SCORING -> 1000  // Speech: $0.006 per call = $6/day max

            FeatureFlag.AI_TUTOR -> 100  // GPT-4: $0.03 per call = $3/day max

            FeatureFlag.IMAGES_VISUAL_AIDS -> null  // Unsplash: Free (5000/hr)

            else -> 5000  // Default conservative limit
        }
    }
}
