package com.gultekinahmetabdullah.trainvoc.billing

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.billing.database.SubscriptionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Premium gate manager for controlling access to Premium features
 *
 * Integrates with FeatureFlagManager to provide dual-layer feature control:
 * 1. Admin feature flags (global on/off)
 * 2. User subscription tier (Premium/Premium+)
 */
@Singleton
class PremiumGate @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) {

    /**
     * Check if user has access to Premium features
     */
    suspend fun hasPremium(): Boolean {
        val subscription = subscriptionDao.getSubscription() ?: return false
        return subscription.isActive &&
               !subscription.isExpired() &&
               subscription.getTier() != SubscriptionTier.FREE
    }

    /**
     * Check if user has access to specific tier
     */
    suspend fun hasTier(tier: SubscriptionTier): Boolean {
        val subscription = subscriptionDao.getSubscription() ?: return false
        return subscription.isActive &&
               !subscription.isExpired() &&
               subscription.getTier().includes(tier)
    }

    /**
     * Check if user has Premium+ features
     */
    suspend fun hasPremiumPlus(): Boolean {
        return hasTier(SubscriptionTier.PREMIUM_PLUS)
    }

    /**
     * Get current subscription tier as Flow
     */
    fun getCurrentTierFlow(): Flow<SubscriptionTier> {
        return subscriptionDao.getSubscriptionFlow().map { subscription ->
            subscription?.getTier() ?: SubscriptionTier.FREE
        }
    }

    /**
     * Check if specific feature requires Premium
     */
    fun requiresPremium(featureName: String): Boolean {
        return when (featureName) {
            // Audio features
            "AUDIO_SPEED_CONTROL" -> true
            "OFFLINE_AUDIO_CACHE" -> true

            // Image features
            "OFFLINE_IMAGE_CACHE" -> true

            // Learning features
            "ADVANCED_QUIZZES" -> true
            "CUSTOMIZABLE_REVIEWS" -> true

            // Data features
            "CLOUD_BACKUP" -> true
            "EXPORT_DATA" -> true

            // Free features
            "AUDIO_PRONUNCIATION" -> false
            "TEXT_TO_SPEECH" -> false
            "IMAGES_VISUAL_AIDS" -> false
            "EXAMPLE_SENTENCES" -> false
            "OFFLINE_MODE" -> false

            else -> false
        }
    }

    /**
     * Check if specific feature requires Premium+
     */
    fun requiresPremiumPlus(featureName: String): Boolean {
        return when (featureName) {
            "AI_TUTOR" -> true
            "SPEECH_RECOGNITION" -> true
            "PRONUNCIATION_ANALYSIS" -> true
            "NATIVE_TRANSLATION" -> true
            else -> false
        }
    }
}

/**
 * Composable that shows Premium upgrade prompt if user doesn't have access
 */
@Composable
fun PremiumFeatureGate(
    hasPremium: Boolean,
    onUpgradeClick: () -> Unit,
    content: @Composable () -> Unit
) {
    if (hasPremium) {
        content()
    } else {
        PremiumUpgradePrompt(onUpgradeClick = onUpgradeClick)
    }
}

/**
 * Premium upgrade prompt UI
 */
@Composable
fun PremiumUpgradePrompt(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Premium feature locked - upgrade required",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Premium Feature",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Upgrade to Premium to unlock this feature and many more!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Button(
                onClick = onUpgradeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upgrade to Premium")
            }
        }
    }
}

/**
 * Inline Premium badge for UI elements
 */
@Composable
fun PremiumBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Premium required",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Premium",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/**
 * Remember Premium status in Compose
 */
@Composable
fun rememberHasPremium(premiumGate: PremiumGate): State<Boolean> {
    return premiumGate.getCurrentTierFlow()
        .map { it != SubscriptionTier.FREE }
        .collectAsState(initial = false)
}
