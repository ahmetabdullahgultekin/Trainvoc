package com.gultekinahmetabdullah.trainvoc.billing.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.billing.BillingManager
import com.gultekinahmetabdullah.trainvoc.billing.BillingState
import com.gultekinahmetabdullah.trainvoc.billing.SubscriptionPeriod
import com.gultekinahmetabdullah.trainvoc.billing.SubscriptionTier
import com.gultekinahmetabdullah.trainvoc.billing.database.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for subscription management
 */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val billingManager: BillingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _currentSubscription = MutableStateFlow<Subscription>(Subscription.free())
    val currentSubscription: StateFlow<Subscription> = _currentSubscription.asStateFlow()

    init {
        loadSubscription()
        observeBillingState()
    }

    /**
     * Load current subscription
     */
    private fun loadSubscription() {
        viewModelScope.launch {
            billingManager.currentSubscription.collect { subscription ->
                _currentSubscription.value = subscription
                _uiState.value = SubscriptionUiState.Ready(subscription.getTier())
            }
        }
    }

    /**
     * Observe billing state changes
     */
    private fun observeBillingState() {
        viewModelScope.launch {
            billingManager.billingState.collect { state ->
                when (state) {
                    is BillingState.Initializing -> {
                        _uiState.value = SubscriptionUiState.Loading
                    }
                    is BillingState.Ready -> {
                        val current = _currentSubscription.value
                        _uiState.value = SubscriptionUiState.Ready(current.getTier())
                    }
                    is BillingState.PurchaseSuccess -> {
                        _uiState.value = SubscriptionUiState.PurchaseSuccess(state.tier, state.period)
                    }
                    is BillingState.Error -> {
                        _uiState.value = SubscriptionUiState.Error(state.message)
                    }
                    is BillingState.Cancelled -> {
                        val current = _currentSubscription.value
                        _uiState.value = SubscriptionUiState.Ready(current.getTier())
                    }
                    is BillingState.Disconnected -> {
                        _uiState.value = SubscriptionUiState.Error("Billing service disconnected")
                    }
                }
            }
        }
    }

    /**
     * Start purchase flow
     */
    fun purchaseSubscription(
        activity: Activity,
        tier: SubscriptionTier,
        period: SubscriptionPeriod
    ) {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Processing

            val result = billingManager.launchPurchaseFlow(activity, tier, period)

            result.onFailure { error ->
                _uiState.value = SubscriptionUiState.Error(
                    error.message ?: "Purchase failed"
                )
            }
        }
    }

    /**
     * Restore purchases
     */
    fun restorePurchases() {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Processing

            val result = billingManager.restorePurchases()

            result.onSuccess {
                val current = _currentSubscription.value
                _uiState.value = if (current.getTier() != SubscriptionTier.FREE) {
                    SubscriptionUiState.RestoreSuccess(current.getTier())
                } else {
                    SubscriptionUiState.Error("No purchases to restore")
                }
            }.onFailure { error ->
                _uiState.value = SubscriptionUiState.Error(
                    error.message ?: "Restore failed"
                )
            }
        }
    }

    /**
     * Cancel subscription
     */
    fun cancelSubscription() {
        viewModelScope.launch {
            val result = billingManager.cancelSubscription()

            result.onSuccess {
                _uiState.value = SubscriptionUiState.Cancelled
            }.onFailure { error ->
                _uiState.value = SubscriptionUiState.Error(
                    error.message ?: "Cancellation failed"
                )
            }
        }
    }

    /**
     * Check if user has Premium access
     */
    suspend fun hasPremium(): Boolean {
        return billingManager.hasPremium()
    }

    /**
     * Check if user has specific tier
     */
    suspend fun hasTier(tier: SubscriptionTier): Boolean {
        return billingManager.hasTier(tier)
    }

    /**
     * Dismiss error/success states
     */
    fun dismissMessage() {
        val current = _currentSubscription.value
        _uiState.value = SubscriptionUiState.Ready(current.getTier())
    }
}

/**
 * UI state for subscription screen
 */
sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()
    data class Ready(val currentTier: SubscriptionTier) : SubscriptionUiState()
    object Processing : SubscriptionUiState()
    data class PurchaseSuccess(val tier: SubscriptionTier, val period: SubscriptionPeriod) : SubscriptionUiState()
    data class RestoreSuccess(val tier: SubscriptionTier) : SubscriptionUiState()
    object Cancelled : SubscriptionUiState()
    data class Error(val message: String) : SubscriptionUiState()
}
