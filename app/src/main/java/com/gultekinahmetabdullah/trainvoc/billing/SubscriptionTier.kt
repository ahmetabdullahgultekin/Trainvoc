package com.gultekinahmetabdullah.trainvoc.billing

/**
 * Subscription tiers for Trainvoc
 *
 * Pricing Strategy:
 * - FREE: Basic features, limited usage
 * - PREMIUM: $4.99/month - All Phase 1 features unlimited
 * - PREMIUM_PLUS: $9.99/month - Premium + future AI features
 */
enum class SubscriptionTier(
    val tierId: String,
    val displayName: String,
    val description: String,
    val monthlyPrice: Double,
    val yearlyPrice: Double,
    val productId: String,
    val yearlyProductId: String
) {
    FREE(
        tierId = "free",
        displayName = "Free",
        description = "Basic vocabulary learning with limited features",
        monthlyPrice = 0.0,
        yearlyPrice = 0.0,
        productId = "",
        yearlyProductId = ""
    ),

    PREMIUM(
        tierId = "premium",
        displayName = "Premium",
        description = "Unlimited access to all learning features",
        monthlyPrice = 4.99,
        yearlyPrice = 49.99, // ~17% discount
        productId = "trainvoc_premium_monthly",
        yearlyProductId = "trainvoc_premium_yearly"
    ),

    PREMIUM_PLUS(
        tierId = "premium_plus",
        displayName = "Premium+",
        description = "Premium + AI tutor & advanced features",
        monthlyPrice = 9.99,
        yearlyPrice = 99.99, // ~17% discount
        productId = "trainvoc_premium_plus_monthly",
        yearlyProductId = "trainvoc_premium_plus_yearly"
    );

    companion object {
        fun fromProductId(productId: String): SubscriptionTier? {
            return values().find {
                it.productId == productId || it.yearlyProductId == productId
            }
        }

        fun fromTierId(tierId: String): SubscriptionTier {
            return values().find { it.tierId == tierId } ?: FREE
        }
    }

    /**
     * Check if this tier includes another tier's features
     */
    fun includes(other: SubscriptionTier): Boolean {
        return when (this) {
            FREE -> other == FREE
            PREMIUM -> other == FREE || other == PREMIUM
            PREMIUM_PLUS -> true // Includes all tiers
        }
    }

    /**
     * Check if tier is a paid subscription
     */
    val isPaid: Boolean
        get() = this != FREE

    /**
     * Get savings percentage for yearly subscription
     */
    val yearlySavings: Int
        get() = if (monthlyPrice > 0) {
            ((1 - (yearlyPrice / (monthlyPrice * 12))) * 100).toInt()
        } else {
            0
        }
}

/**
 * Subscription period
 */
enum class SubscriptionPeriod(
    val displayName: String,
    val durationDays: Int
) {
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365);

    companion object {
        fun fromProductId(productId: String): SubscriptionPeriod {
            return if (productId.contains("yearly")) YEARLY else MONTHLY
        }
    }
}

/**
 * Premium features available per tier
 */
data class PremiumFeatures(
    val tier: SubscriptionTier,

    // Audio features
    val unlimitedAudio: Boolean,
    val audioSpeedControl: Boolean,
    val offlineAudioCache: Boolean,

    // Image features
    val unlimitedImages: Boolean,
    val offlineImageCache: Boolean,

    // Learning features
    val unlimitedExamples: Boolean,
    val advancedQuizzes: Boolean,
    val customizableReviews: Boolean,

    // Data & Sync
    val cloudBackup: Boolean,
    val crossDeviceSync: Boolean,
    val exportData: Boolean,

    // Future features (Premium+)
    val aiTutor: Boolean,
    val speechRecognition: Boolean,
    val translationQuality: String, // "basic", "advanced", "native"
    val customVocabularyLists: Int, // -1 = unlimited
    val dailyGoalCustomization: Boolean
) {
    companion object {
        fun forTier(tier: SubscriptionTier): PremiumFeatures {
            return when (tier) {
                SubscriptionTier.FREE -> PremiumFeatures(
                    tier = tier,
                    unlimitedAudio = false,
                    audioSpeedControl = false,
                    offlineAudioCache = false,
                    unlimitedImages = false,
                    offlineImageCache = false,
                    unlimitedExamples = false,
                    advancedQuizzes = false,
                    customizableReviews = false,
                    cloudBackup = false,
                    crossDeviceSync = false,
                    exportData = false,
                    aiTutor = false,
                    speechRecognition = false,
                    translationQuality = "basic",
                    customVocabularyLists = 3,
                    dailyGoalCustomization = false
                )

                SubscriptionTier.PREMIUM -> PremiumFeatures(
                    tier = tier,
                    unlimitedAudio = true,
                    audioSpeedControl = true,
                    offlineAudioCache = true,
                    unlimitedImages = true,
                    offlineImageCache = true,
                    unlimitedExamples = true,
                    advancedQuizzes = true,
                    customizableReviews = true,
                    cloudBackup = true,
                    crossDeviceSync = true,
                    exportData = true,
                    aiTutor = false,
                    speechRecognition = false,
                    translationQuality = "advanced",
                    customVocabularyLists = -1, // Unlimited
                    dailyGoalCustomization = true
                )

                SubscriptionTier.PREMIUM_PLUS -> PremiumFeatures(
                    tier = tier,
                    unlimitedAudio = true,
                    audioSpeedControl = true,
                    offlineAudioCache = true,
                    unlimitedImages = true,
                    offlineImageCache = true,
                    unlimitedExamples = true,
                    advancedQuizzes = true,
                    customizableReviews = true,
                    cloudBackup = true,
                    crossDeviceSync = true,
                    exportData = true,
                    aiTutor = true, // Exclusive
                    speechRecognition = true, // Exclusive
                    translationQuality = "native",
                    customVocabularyLists = -1,
                    dailyGoalCustomization = true
                )
            }
        }
    }
}
