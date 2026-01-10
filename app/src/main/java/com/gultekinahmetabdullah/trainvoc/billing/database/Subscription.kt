package com.gultekinahmetabdullah.trainvoc.billing.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gultekinahmetabdullah.trainvoc.billing.SubscriptionPeriod
import com.gultekinahmetabdullah.trainvoc.billing.SubscriptionTier

/**
 * Entity for storing user's subscription status
 *
 * Tracks current subscription, purchase history, and renewal status
 */
@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String = "local_user", // For multi-user support in future

    @ColumnInfo(name = "tier")
    val tier: String = SubscriptionTier.FREE.tierId,

    @ColumnInfo(name = "period")
    val period: String? = null, // MONTHLY or YEARLY

    @ColumnInfo(name = "product_id")
    val productId: String? = null,

    @ColumnInfo(name = "purchase_token")
    val purchaseToken: String? = null,

    @ColumnInfo(name = "order_id")
    val orderId: String? = null,

    @ColumnInfo(name = "purchase_time")
    val purchaseTime: Long? = null,

    @ColumnInfo(name = "expiry_time")
    val expiryTime: Long? = null,

    @ColumnInfo(name = "auto_renewing")
    val autoRenewing: Boolean = false,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,

    @ColumnInfo(name = "last_verified")
    val lastVerified: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "payment_state")
    val paymentState: String = "none", // none, pending, paid, free_trial, grace_period, on_hold, paused, canceled

    @ColumnInfo(name = "acknowledgement_state")
    val acknowledgementState: String = "acknowledged",

    @ColumnInfo(name = "price_paid")
    val pricePaid: Double? = null,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String? = null
) {
    /**
     * Get subscription tier enum
     */
    fun getTier(): SubscriptionTier {
        return SubscriptionTier.fromTierId(tier)
    }

    /**
     * Get subscription period enum
     */
    fun getPeriod(): SubscriptionPeriod? {
        return period?.let {
            try {
                SubscriptionPeriod.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Check if subscription is expired
     */
    fun isExpired(): Boolean {
        return expiryTime?.let { it < System.currentTimeMillis() } ?: false
    }

    /**
     * Check if subscription is in grace period
     */
    fun isInGracePeriod(): Boolean {
        return paymentState == "grace_period"
    }

    /**
     * Check if subscription needs verification
     */
    fun needsVerification(): Boolean {
        val daysSinceVerification = (System.currentTimeMillis() - lastVerified) / (1000 * 60 * 60 * 24)
        return daysSinceVerification > 1 // Verify at least daily
    }

    /**
     * Get days until expiry
     */
    fun daysUntilExpiry(): Int? {
        return expiryTime?.let {
            val daysLeft = (it - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
            daysLeft.toInt()
        }
    }

    companion object {
        /**
         * Create a free tier subscription
         */
        fun free(): Subscription {
            return Subscription(
                tier = SubscriptionTier.FREE.tierId,
                isActive = true,
                paymentState = "none"
            )
        }
    }
}

/**
 * Purchase history record
 */
@Entity(tableName = "purchase_history")
data class PurchaseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "purchase_token")
    val purchaseToken: String,

    @ColumnInfo(name = "order_id")
    val orderId: String,

    @ColumnInfo(name = "purchase_time")
    val purchaseTime: Long,

    @ColumnInfo(name = "acknowledged")
    val acknowledged: Boolean,

    @ColumnInfo(name = "price_paid")
    val pricePaid: Double,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "subscription_tier")
    val subscriptionTier: String,

    @ColumnInfo(name = "subscription_period")
    val subscriptionPeriod: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
