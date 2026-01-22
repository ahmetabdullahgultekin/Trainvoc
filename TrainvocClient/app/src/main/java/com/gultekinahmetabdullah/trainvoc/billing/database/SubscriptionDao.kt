package com.gultekinahmetabdullah.trainvoc.billing.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for subscription operations
 */
@Dao
interface SubscriptionDao {

    // ============ Subscription Operations ============

    @Query("SELECT * FROM subscriptions WHERE user_id = :userId LIMIT 1")
    suspend fun getSubscription(userId: String = "local_user"): Subscription?

    @Query("SELECT * FROM subscriptions WHERE user_id = :userId LIMIT 1")
    fun getSubscriptionFlow(userId: String = "local_user"): Flow<Subscription?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription)

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Query("""
        UPDATE subscriptions
        SET tier = :tier,
            period = :period,
            product_id = :productId,
            purchase_token = :purchaseToken,
            order_id = :orderId,
            purchase_time = :purchaseTime,
            expiry_time = :expiryTime,
            auto_renewing = :autoRenewing,
            is_active = :isActive,
            last_verified = :lastVerified,
            payment_state = :paymentState,
            price_paid = :pricePaid,
            currency_code = :currencyCode
        WHERE user_id = :userId
    """)
    suspend fun updateSubscriptionDetails(
        userId: String = "local_user",
        tier: String,
        period: String?,
        productId: String?,
        purchaseToken: String?,
        orderId: String?,
        purchaseTime: Long?,
        expiryTime: Long?,
        autoRenewing: Boolean,
        isActive: Boolean,
        lastVerified: Long,
        paymentState: String,
        pricePaid: Double?,
        currencyCode: String?
    )

    @Query("UPDATE subscriptions SET is_active = :isActive WHERE user_id = :userId")
    suspend fun setSubscriptionActive(userId: String = "local_user", isActive: Boolean)

    @Query("UPDATE subscriptions SET last_verified = :timestamp WHERE user_id = :userId")
    suspend fun updateLastVerified(userId: String = "local_user", timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE subscriptions SET auto_renewing = :autoRenewing WHERE user_id = :userId")
    suspend fun setAutoRenewing(userId: String = "local_user", autoRenewing: Boolean)

    @Query("UPDATE subscriptions SET payment_state = :state WHERE user_id = :userId")
    suspend fun setPaymentState(userId: String = "local_user", state: String)

    @Query("SELECT tier FROM subscriptions WHERE user_id = :userId LIMIT 1")
    suspend fun getCurrentTier(userId: String = "local_user"): String?

    @Query("SELECT tier FROM subscriptions WHERE user_id = :userId LIMIT 1")
    fun getCurrentTierFlow(userId: String = "local_user"): Flow<String?>

    @Query("SELECT is_active FROM subscriptions WHERE user_id = :userId LIMIT 1")
    suspend fun isSubscriptionActive(userId: String = "local_user"): Boolean?

    @Query("SELECT expiry_time FROM subscriptions WHERE user_id = :userId LIMIT 1")
    suspend fun getExpiryTime(userId: String = "local_user"): Long?

    // ============ Purchase History Operations ============

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseRecord(record: PurchaseRecord): Long

    @Query("SELECT * FROM purchase_history ORDER BY purchase_time DESC")
    fun getAllPurchases(): Flow<List<PurchaseRecord>>

    @Query("SELECT * FROM purchase_history WHERE order_id = :orderId LIMIT 1")
    suspend fun getPurchaseByOrderId(orderId: String): PurchaseRecord?

    @Query("SELECT * FROM purchase_history WHERE purchase_token = :token LIMIT 1")
    suspend fun getPurchaseByToken(token: String): PurchaseRecord?

    @Query("SELECT * FROM purchase_history ORDER BY purchase_time DESC LIMIT :limit")
    suspend fun getRecentPurchases(limit: Int = 10): List<PurchaseRecord>

    @Query("SELECT COUNT(*) FROM purchase_history")
    suspend fun getPurchaseCount(): Int

    @Query("SELECT SUM(price_paid) FROM purchase_history")
    suspend fun getTotalRevenue(): Double?

    @Query("""
        SELECT SUM(price_paid)
        FROM purchase_history
        WHERE purchase_time >= :since
    """)
    suspend fun getRevenueSince(since: Long): Double?

    @Query("UPDATE purchase_history SET acknowledged = 1 WHERE order_id = :orderId")
    suspend fun acknowledgePurchase(orderId: String)

    @Query("DELETE FROM purchase_history WHERE purchase_time < :beforeTimestamp")
    suspend fun deleteOldPurchases(beforeTimestamp: Long)

    // ============ Analytics ============

    @Query("""
        SELECT subscription_tier, COUNT(*) as count
        FROM purchase_history
        GROUP BY subscription_tier
    """)
    suspend fun getPurchaseCountByTier(): Map<@MapColumn(columnName = "subscription_tier") String, @MapColumn(columnName = "count") Int>

    @Query("""
        SELECT subscription_period, COUNT(*) as count
        FROM purchase_history
        GROUP BY subscription_period
    """)
    suspend fun getPurchaseCountByPeriod(): Map<@MapColumn(columnName = "subscription_period") String, @MapColumn(columnName = "count") Int>
}
