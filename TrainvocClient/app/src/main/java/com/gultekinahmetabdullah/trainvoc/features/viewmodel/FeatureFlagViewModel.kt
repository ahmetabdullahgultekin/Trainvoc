package com.gultekinahmetabdullah.trainvoc.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.features.FeatureCategory
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.repository.IFeatureFlagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Feature Flag management screens
 * Handles both admin and user feature flag settings
 */
@HiltViewModel
class FeatureFlagViewModel @Inject constructor(
    private val repository: IFeatureFlagRepository
) : ViewModel() {

    // ========== State ==========

    // Global flags (admin view)
    val globalFlags: StateFlow<List<GlobalFeatureFlag>> = repository.getAllGlobalFlags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User flags (user preferences view)
    val userFlags: StateFlow<List<UserFeatureFlag>> = repository.getAllUserFlags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Cost tracking
    private val _costToday = MutableStateFlow(0.0)
    val costToday: StateFlow<Double> = _costToday.asStateFlow()

    private val _costThisMonth = MutableStateFlow(0.0)
    val costThisMonth: StateFlow<Double> = _costThisMonth.asStateFlow()

    // Cost breakdown by feature
    private val _costBreakdown = MutableStateFlow<Map<FeatureFlag, Double>>(emptyMap())
    val costBreakdown: StateFlow<Map<FeatureFlag, Double>> = _costBreakdown.asStateFlow()

    // Selected category filter
    private val _selectedCategory = MutableStateFlow<FeatureCategory?>(null)
    val selectedCategory: StateFlow<FeatureCategory?> = _selectedCategory.asStateFlow()

    // Filtered features based on category
    val filteredFeatures: StateFlow<List<FeatureFlag>> = combine(
        _selectedCategory,
        globalFlags
    ) { category, _ ->
        if (category == null) {
            FeatureFlag.values().toList()
        } else {
            FeatureFlag.getAllByCategory(category)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeatureFlag.values().toList()
    )

    init {
        loadCostData()
    }

    // ========== Admin Actions (Global Controls) ==========

    /**
     * Toggle a feature on/off globally
     */
    fun toggleGlobalFeature(feature: FeatureFlag) {
        viewModelScope.launch {
            try {
                val currentFlag = repository.getGlobalFlag(feature)
                val newState = !(currentFlag?.enabled ?: feature.defaultEnabled)
                repository.setGlobalFeatureEnabled(feature, newState)
            } catch (e: Exception) {
                _error.value = "Failed to toggle ${feature.displayName}: ${e.message}"
            }
        }
    }

    /**
     * Set global feature enabled state
     */
    fun setGlobalFeatureEnabled(feature: FeatureFlag, enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.setGlobalFeatureEnabled(feature, enabled)
            } catch (e: Exception) {
                _error.value = "Failed to update ${feature.displayName}: ${e.message}"
            }
        }
    }

    /**
     * Set rollout percentage for gradual feature release (A/B testing)
     */
    fun setRolloutPercentage(feature: FeatureFlag, percentage: Int) {
        viewModelScope.launch {
            try {
                repository.setRolloutPercentage(feature, percentage)
            } catch (e: Exception) {
                _error.value = "Failed to set rollout for ${feature.displayName}: ${e.message}"
            }
        }
    }

    /**
     * Set maximum daily API calls for cost control
     */
    fun setDailyLimit(feature: FeatureFlag, limit: Int?) {
        viewModelScope.launch {
            try {
                repository.setMaxDailyUsage(feature, limit)
            } catch (e: Exception) {
                _error.value = "Failed to set daily limit for ${feature.displayName}: ${e.message}"
            }
        }
    }

    /**
     * Reset daily usage counters for all features
     */
    fun resetAllDailyUsage() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.resetAllDailyUsage()
                loadCostData()  // Refresh cost data
            } catch (e: Exception) {
                _error.value = "Failed to reset daily usage: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Enable all features (for testing)
     */
    fun enableAllFeatures() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                FeatureFlag.values().forEach { feature ->
                    repository.setGlobalFeatureEnabled(feature, true)
                }
            } catch (e: Exception) {
                _error.value = "Failed to enable all features: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Disable all expensive features (cost control)
     */
    fun disableExpensiveFeatures() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                FeatureFlag.getAllWithCost().forEach { feature ->
                    repository.setGlobalFeatureEnabled(feature, false)
                }
            } catch (e: Exception) {
                _error.value = "Failed to disable expensive features: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== User Actions (User Preferences) ==========

    /**
     * Toggle a feature on/off for the user
     */
    fun toggleUserFeature(feature: FeatureFlag) {
        viewModelScope.launch {
            try {
                val currentFlag = repository.getUserFlag(feature)
                val newState = !(currentFlag?.userEnabled ?: true)
                repository.setUserFeatureEnabled(feature, newState)
            } catch (e: Exception) {
                _error.value = "Failed to toggle ${feature.displayName}: ${e.message}"
            }
        }
    }

    /**
     * Set user feature preference
     */
    fun setUserFeatureEnabled(feature: FeatureFlag, enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.setUserFeatureEnabled(feature, enabled)
            } catch (e: Exception) {
                _error.value = "Failed to update ${feature.displayName}: ${e.message}"
            }
        }
    }

    /**
     * Enable all user features
     */
    fun enableAllUserFeatures() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                FeatureFlag.getAllUserConfigurable().forEach { feature ->
                    repository.setUserFeatureEnabled(feature, true)
                }
            } catch (e: Exception) {
                _error.value = "Failed to enable all features: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Disable all user features
     */
    fun disableAllUserFeatures() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                FeatureFlag.getAllUserConfigurable().forEach { feature ->
                    repository.setUserFeatureEnabled(feature, false)
                }
            } catch (e: Exception) {
                _error.value = "Failed to disable all features: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== Category Filtering ==========

    fun selectCategory(category: FeatureCategory?) {
        _selectedCategory.value = category
    }

    // ========== Cost Management ==========

    /**
     * Load cost data for dashboard
     */
    private fun loadCostData() {
        viewModelScope.launch {
            try {
                _costToday.value = repository.getTotalCostToday()
                _costThisMonth.value = repository.getTotalCostThisMonth()
                _costBreakdown.value = repository.getCostBreakdownByFeature()
            } catch (e: Exception) {
                _error.value = "Failed to load cost data: ${e.message}"
            }
        }
    }

    /**
     * Refresh cost data manually
     */
    fun refreshCostData() {
        loadCostData()
    }

    // ========== Error Handling ==========

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    // ========== Helper Methods ==========

    /**
     * Get global flag for a specific feature
     */
    fun getGlobalFlag(feature: FeatureFlag): GlobalFeatureFlag? {
        return globalFlags.value.find { it.featureKey == feature.key }
    }

    /**
     * Get user flag for a specific feature
     */
    fun getUserFlag(feature: FeatureFlag): UserFeatureFlag? {
        return userFlags.value.find { it.featureKey == feature.key }
    }

    /**
     * Check if a feature is enabled (global + user + limits)
     */
    fun isFeatureEnabled(feature: FeatureFlag): Boolean {
        val globalFlag = getGlobalFlag(feature) ?: return feature.defaultEnabled

        if (!globalFlag.enabled) return false

        // Check daily limits
        if (feature.hasCost && globalFlag.maxDailyUsage != null) {
            if (globalFlag.currentDailyUsage >= globalFlag.maxDailyUsage) {
                return false
            }
        }

        // Check user preference (if not admin-only)
        if (!feature.adminOnly) {
            val userFlag = getUserFlag(feature)
            if (userFlag?.userEnabled == false) return false
        }

        return true
    }
}
