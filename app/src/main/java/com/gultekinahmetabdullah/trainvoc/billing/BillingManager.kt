package com.gultekinahmetabdullah.trainvoc.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.gultekinahmetabdullah.trainvoc.billing.database.PurchaseRecord
import com.gultekinahmetabdullah.trainvoc.billing.database.Subscription
import com.gultekinahmetabdullah.trainvoc.billing.database.SubscriptionDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Billing Manager for Google Play Billing integration
 *
 * Features:
 * - Purchase flow for subscriptions
 * - Subscription verification
 * - Restore purchases
 * - Real-time subscription status
 * - Purchase acknowledgement
 *
 * Note: Requires Google Play Billing Library 6.x
 * implementation("com.android.billingclient:billing-ktx:6.1.0")
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionDao: SubscriptionDao
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var billingClient: BillingClient? = null
    private var isInitialized = false

    private val _billingState = MutableStateFlow<BillingState>(BillingState.Initializing)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()

    private val _currentSubscription = MutableStateFlow<Subscription>(Subscription.free())
    val currentSubscription: StateFlow<Subscription> = _currentSubscription.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val availableProducts: StateFlow<List<ProductDetails>> = _availableProducts.asStateFlow()

    init {
        initializeBillingClient()
    }

    /**
     * Initialize Google Play Billing client
     */
    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                handlePurchaseUpdate(billingResult, purchases)
            }
            .enablePendingPurchases()
            .build()

        startConnection()
    }

    /**
     * Start connection to Google Play Billing
     */
    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isInitialized = true
                    _billingState.value = BillingState.Ready

                    // Load products and check existing purchases
                    scope.launch {
                        loadProducts()
                        checkExistingPurchases()
                    }
                } else {
                    _billingState.value = BillingState.Error("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                isInitialized = false
                _billingState.value = BillingState.Disconnected
                // Retry connection
                startConnection()
            }
        })
    }

    /**
     * Load available subscription products from Google Play
     */
    private suspend fun loadProducts() {
        if (!isInitialized) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.PREMIUM.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.PREMIUM.yearlyProductId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.PREMIUM_PLUS.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.PREMIUM_PLUS.yearlyProductId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        withContext(Dispatchers.IO) {
            billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _availableProducts.value = productDetailsList
                }
            }
        }
    }

    /**
     * Check for existing purchases and restore subscription
     */
    private suspend fun checkExistingPurchases() {
        if (!isInitialized) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        withContext(Dispatchers.IO) {
            billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        processPurchases(purchases)
                    }
                }
            }
        }
    }

    /**
     * Launch purchase flow for a subscription
     */
    suspend fun launchPurchaseFlow(
        activity: Activity,
        tier: SubscriptionTier,
        period: SubscriptionPeriod
    ): Result<Unit> {
        if (!isInitialized) {
            return Result.failure(Exception("Billing client not initialized"))
        }

        val productId = when (period) {
            SubscriptionPeriod.MONTHLY -> tier.productId
            SubscriptionPeriod.YEARLY -> tier.yearlyProductId
        }

        val productDetails = _availableProducts.value.find { it.productId == productId }
            ?: return Result.failure(Exception("Product not found: $productId"))

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return Result.failure(Exception("No subscription offer available"))

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        return withContext(Dispatchers.Main) {
            val billingResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
            if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Purchase failed: ${billingResult?.debugMessage}"))
            }
        }
    }

    /**
     * Handle purchase updates from billing client
     */
    private fun handlePurchaseUpdate(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch {
                processPurchases(purchases)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _billingState.value = BillingState.Cancelled
        } else {
            _billingState.value = BillingState.Error("Purchase failed: ${billingResult.debugMessage}")
        }
    }

    /**
     * Process and verify purchases
     */
    private suspend fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                // Acknowledge purchase if not already acknowledged
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }

                // Update subscription in database
                updateSubscriptionFromPurchase(purchase)
            }
        }
    }

    /**
     * Acknowledge a purchase
     */
    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        withContext(Dispatchers.IO) {
            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        subscriptionDao.acknowledgePurchase(purchase.orderId ?: "")
                    }
                }
            }
        }
    }

    /**
     * Update subscription status from purchase
     */
    private suspend fun updateSubscriptionFromPurchase(purchase: Purchase) {
        val productId = purchase.products.firstOrNull() ?: return
        val tier = SubscriptionTier.fromProductId(productId) ?: return
        val period = SubscriptionPeriod.fromProductId(productId)

        val subscription = Subscription(
            tier = tier.tierId,
            period = period.name,
            productId = productId,
            purchaseToken = purchase.purchaseToken,
            orderId = purchase.orderId,
            purchaseTime = purchase.purchaseTime,
            expiryTime = calculateExpiryTime(purchase.purchaseTime, period),
            autoRenewing = purchase.isAutoRenewing,
            isActive = true,
            lastVerified = System.currentTimeMillis(),
            paymentState = "paid",
            acknowledgementState = if (purchase.isAcknowledged) "acknowledged" else "pending"
        )

        subscriptionDao.insertSubscription(subscription)
        _currentSubscription.value = subscription

        // Record purchase in history
        val purchaseRecord = PurchaseRecord(
            productId = productId,
            purchaseToken = purchase.purchaseToken,
            orderId = purchase.orderId ?: "",
            purchaseTime = purchase.purchaseTime,
            acknowledged = purchase.isAcknowledged,
            pricePaid = getPrice(tier, period),
            currencyCode = "USD", // Should get from product details
            subscriptionTier = tier.tierId,
            subscriptionPeriod = period.name
        )

        subscriptionDao.insertPurchaseRecord(purchaseRecord)
        _billingState.value = BillingState.PurchaseSuccess(tier, period)
    }

    /**
     * Calculate expiry time for subscription
     */
    private fun calculateExpiryTime(purchaseTime: Long, period: SubscriptionPeriod): Long {
        return purchaseTime + (period.durationDays * 24 * 60 * 60 * 1000L)
    }

    /**
     * Get price for tier and period
     */
    private fun getPrice(tier: SubscriptionTier, period: SubscriptionPeriod): Double {
        return when (period) {
            SubscriptionPeriod.MONTHLY -> tier.monthlyPrice
            SubscriptionPeriod.YEARLY -> tier.yearlyPrice
        }
    }

    /**
     * Restore purchases
     */
    suspend fun restorePurchases(): Result<Unit> {
        return try {
            checkExistingPurchases()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancel subscription (manage in Google Play)
     */
    suspend fun cancelSubscription(): Result<Unit> {
        // Note: Actual cancellation happens in Google Play Store
        // We just update local status
        val current = _currentSubscription.value
        val updated = current.copy(
            autoRenewing = false,
            paymentState = "canceled"
        )
        subscriptionDao.updateSubscription(updated)
        _currentSubscription.value = updated
        return Result.success(Unit)
    }

    /**
     * Check if user has active Premium subscription
     */
    suspend fun hasPremium(): Boolean {
        val subscription = subscriptionDao.getSubscription() ?: return false
        return subscription.isActive &&
               !subscription.isExpired() &&
               subscription.getTier() != SubscriptionTier.FREE
    }

    /**
     * Check if user has specific tier or higher
     */
    suspend fun hasTier(tier: SubscriptionTier): Boolean {
        val subscription = subscriptionDao.getSubscription() ?: return false
        return subscription.isActive &&
               !subscription.isExpired() &&
               subscription.getTier().includes(tier)
    }

    /**
     * Clean up billing client
     */
    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
        isInitialized = false
    }
}

/**
 * Billing state for UI
 */
sealed class BillingState {
    object Initializing : BillingState()
    object Ready : BillingState()
    object Disconnected : BillingState()
    object Cancelled : BillingState()
    data class PurchaseSuccess(val tier: SubscriptionTier, val period: SubscriptionPeriod) : BillingState()
    data class Error(val message: String) : BillingState()
}
