package com.gultekinahmetabdullah.trainvoc.features

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.gultekinahmetabdullah.trainvoc.features.repository.IFeatureFlagRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main service for feature flag checks
 * Use this throughout the app to check if features are enabled
 *
 * Usage examples:
 * ```
 * // In ViewModels
 * if (featureFlags.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)) {
 *     playAudio()
 *     featureFlags.trackUsage(FeatureFlag.AUDIO_PRONUNCIATION, apiCalls = 1, estimatedCost = 0.001)
 * }
 *
 * // In Composables
 * val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)
 * if (audioEnabled) {
 *     AudioButton()
 * }
 *
 * // Require feature (throws if disabled)
 * featureFlags.requireFeature(FeatureFlag.PREMIUM_SUBSCRIPTION)
 * ```
 */
@Singleton
class FeatureFlagManager @Inject constructor(
    private val repository: IFeatureFlagRepository
) {

    // ========== Quick Check Methods ==========

    /**
     * Main method: Is feature enabled?
     * Checks: Global enabled + Rollout % + User preference + Daily limits
     *
     * @param feature The feature to check
     * @return true if feature is enabled for current user, false otherwise
     */
    suspend fun isEnabled(feature: FeatureFlag): Boolean {
        return repository.isFeatureEnabled(feature)
    }

    /**
     * Check if feature is enabled (synchronous version)
     * WARNING: This blocks the thread. Use isEnabled() with coroutines when possible.
     *
     * @param feature The feature to check
     * @return true if enabled, false if disabled or error
     */
    fun isEnabledSync(feature: FeatureFlag): Boolean {
        return try {
            kotlinx.coroutines.runBlocking {
                isEnabled(feature)
            }
        } catch (e: Exception) {
            // If error checking, default to feature's default setting
            feature.defaultEnabled
        }
    }

    /**
     * Require a feature to be enabled, throw exception if not
     * Use this when a feature is absolutely required for an operation
     *
     * @throws FeatureDisabledException if feature is disabled
     */
    suspend fun requireFeature(feature: FeatureFlag) {
        if (!isEnabled(feature)) {
            throw FeatureDisabledException(feature)
        }
    }

    /**
     * Execute a block only if feature is enabled
     * Returns the result of the block, or null if feature is disabled
     */
    suspend fun <T> withFeature(feature: FeatureFlag, block: suspend () -> T): T? {
        return if (isEnabled(feature)) {
            block()
        } else {
            null
        }
    }

    /**
     * Execute different blocks based on feature enabled state
     */
    suspend fun <T> ifFeature(
        feature: FeatureFlag,
        enabled: suspend () -> T,
        disabled: suspend () -> T
    ): T {
        return if (isEnabled(feature)) {
            enabled()
        } else {
            disabled()
        }
    }

    // ========== Usage Tracking ==========

    /**
     * Track usage after using a feature
     * Call this after successfully using a feature with API costs
     *
     * @param feature The feature that was used
     * @param apiCalls Number of API calls made (for cost tracking)
     * @param estimatedCost Estimated cost in USD
     */
    suspend fun trackUsage(
        feature: FeatureFlag,
        apiCalls: Int = 0,
        estimatedCost: Double = 0.0
    ) {
        repository.trackFeatureUsage(
            feature = feature,
            apiCalls = apiCalls,
            estimatedCost = estimatedCost,
            success = true
        )
    }

    /**
     * Track failed usage of a feature
     * Call this when a feature operation fails
     */
    suspend fun trackFailure(
        feature: FeatureFlag,
        errorMessage: String? = null,
        apiCalls: Int = 0,
        estimatedCost: Double = 0.0
    ) {
        repository.trackFeatureUsage(
            feature = feature,
            apiCalls = apiCalls,
            estimatedCost = estimatedCost,
            success = false,
            errorMessage = errorMessage
        )
    }

    /**
     * Execute a block with automatic usage tracking
     * Tracks success/failure automatically
     */
    suspend fun <T> executeWithTracking(
        feature: FeatureFlag,
        apiCalls: Int = 0,
        estimatedCost: Double = 0.0,
        block: suspend () -> T
    ): Result<T> {
        return try {
            val result = block()
            trackUsage(feature, apiCalls, estimatedCost)
            Result.success(result)
        } catch (e: Exception) {
            trackFailure(feature, e.message, apiCalls, estimatedCost)
            Result.failure(e)
        }
    }

    // ========== Composable Helpers ==========

    /**
     * Remember feature enabled state for Compose UI
     * Automatically updates when feature flag changes
     *
     * Usage:
     * ```
     * val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)
     * if (audioEnabled) {
     *     AudioButton()
     * }
     * ```
     */
    @Composable
    fun rememberFeatureEnabled(feature: FeatureFlag): State<Boolean> {
        return produceState(initialValue = feature.defaultEnabled, feature) {
            value = try {
                isEnabled(feature)
            } catch (e: Exception) {
                feature.defaultEnabled
            }
        }
    }

    // ========== Admin Methods ==========

    /**
     * Initialize feature flags (call on app startup)
     */
    suspend fun initialize() {
        repository.initializeFeatureFlags()
    }

    /**
     * Reset daily usage counters (call at midnight)
     */
    suspend fun resetDailyUsage() {
        repository.resetDailyUsage()
    }
}

/**
 * Composable function to conditionally show UI based on feature flag
 *
 * Usage:
 * ```
 * FeatureGate(FeatureFlag.AUDIO_PRONUNCIATION) {
 *     AudioButton()
 * }
 * ```
 */
@Composable
fun FeatureGate(
    feature: FeatureFlag,
    featureFlags: FeatureFlagManager,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isEnabled by featureFlags.rememberFeatureEnabled(feature)

    if (isEnabled) {
        content()
    } else {
        fallback()
    }
}

/**
 * Composable to show different UI based on feature state
 */
@Composable
fun FeatureSwitch(
    feature: FeatureFlag,
    featureFlags: FeatureFlagManager,
    enabled: @Composable () -> Unit,
    disabled: @Composable () -> Unit = {}
) {
    val isEnabled by featureFlags.rememberFeatureEnabled(feature)

    if (isEnabled) {
        enabled()
    } else {
        disabled()
    }
}
