package com.gultekinahmetabdullah.trainvoc.billing.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.billing.SubscriptionPeriod
import com.gultekinahmetabdullah.trainvoc.billing.SubscriptionTier
import com.gultekinahmetabdullah.trainvoc.ui.components.ButtonLoader

/**
 * Main subscription/pricing screen
 * Shows available subscription tiers with purchase options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    currentTier: SubscriptionTier,
    isLoading: Boolean,
    onPurchase: (SubscriptionTier, SubscriptionPeriod) -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf(SubscriptionPeriod.MONTHLY) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upgrade to Premium") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = onRestore) {
                        Text("Restore")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium subscription unlock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Unlock Your Full Potential",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Get unlimited access to all learning features",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Period selector
            item {
                PeriodSelector(
                    selected = selectedPeriod,
                    onSelect = { selectedPeriod = it }
                )
            }

            // Current tier indicator
            if (currentTier != SubscriptionTier.FREE) {
                item {
                    CurrentSubscriptionCard(tier = currentTier)
                }
            }

            // Pricing cards
            item {
                SubscriptionCard(
                    tier = SubscriptionTier.PREMIUM,
                    period = selectedPeriod,
                    isCurrent = currentTier == SubscriptionTier.PREMIUM,
                    isRecommended = true,
                    isLoading = isLoading,
                    onPurchase = { onPurchase(SubscriptionTier.PREMIUM, selectedPeriod) }
                )
            }

            item {
                SubscriptionCard(
                    tier = SubscriptionTier.PREMIUM_PLUS,
                    period = selectedPeriod,
                    isCurrent = currentTier == SubscriptionTier.PREMIUM_PLUS,
                    isRecommended = false,
                    isLoading = isLoading,
                    onPurchase = { onPurchase(SubscriptionTier.PREMIUM_PLUS, selectedPeriod) }
                )
            }

            // Feature comparison
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "What's Included",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FeatureComparisonTable()
            }

            // Terms
            item {
                TermsAndConditions()
            }
        }
    }
}

/**
 * Period selector (Monthly/Yearly)
 */
@Composable
fun PeriodSelector(
    selected: SubscriptionPeriod,
    onSelect: (SubscriptionPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SubscriptionPeriod.values().forEach { period ->
            val isSelected = period == selected

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(period) }
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = period.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (period == SubscriptionPeriod.YEARLY) {
                        Text(
                            text = "Save 17%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Subscription tier card with pricing and features
 */
@Composable
fun SubscriptionCard(
    tier: SubscriptionTier,
    period: SubscriptionPeriod,
    isCurrent: Boolean,
    isRecommended: Boolean,
    isLoading: Boolean,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val price = when (period) {
        SubscriptionPeriod.MONTHLY -> tier.monthlyPrice
        SubscriptionPeriod.YEARLY -> tier.yearlyPrice
    }

    val monthlyEquivalent = when (period) {
        SubscriptionPeriod.MONTHLY -> price
        SubscriptionPeriod.YEARLY -> price / 12
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isRecommended) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(16.dp)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Recommended badge
            if (isRecommended) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "RECOMMENDED",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // Tier name
            Text(
                text = tier.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            // Price
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$${String.format("%.2f", price)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = "/${period.displayName.lowercase()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Monthly equivalent for yearly
            if (period == SubscriptionPeriod.YEARLY) {
                Text(
                    text = "$${String.format("%.2f", monthlyEquivalent)}/month",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            // Description
            Text(
                text = tier.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // Purchase button
            if (isCurrent) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Current active plan")
                    Spacer(Modifier.width(8.dp))
                    Text("Current Plan")
                }
            } else {
                Button(
                    onClick = onPurchase,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        ButtonLoader(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Subscribe")
                    }
                }
            }
        }
    }
}

/**
 * Current subscription indicator
 */
@Composable
fun CurrentSubscriptionCard(
    tier: SubscriptionTier,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Current subscription tier",
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Current Plan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = tier.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Feature comparison table
 */
@Composable
fun FeatureComparisonTable(
    modifier: Modifier = Modifier
) {
    val features = listOf(
        Feature("Unlimited Audio", Icons.Default.Headphones, free = false, premium = true, premiumPlus = true),
        Feature("Audio Speed Control", Icons.Default.Speed, free = false, premium = true, premiumPlus = true),
        Feature("Offline Audio", Icons.Default.CloudDownload, free = false, premium = true, premiumPlus = true),
        Feature("Visual Learning", Icons.Default.Image, free = false, premium = true, premiumPlus = true),
        Feature("Example Sentences", Icons.Default.Description, free = false, premium = true, premiumPlus = true),
        Feature("Offline Mode", Icons.Default.CloudOff, free = false, premium = true, premiumPlus = true),
        Feature("Cloud Backup", Icons.Default.Backup, free = false, premium = true, premiumPlus = true),
        Feature("AI Tutor", Icons.Default.Psychology, free = false, premium = false, premiumPlus = true),
        Feature("Speech Recognition", Icons.Default.Mic, free = false, premium = false, premiumPlus = true)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        features.forEach { feature ->
            FeatureRow(feature = feature)
        }
    }
}

data class Feature(
    val name: String,
    val icon: ImageVector,
    val free: Boolean,
    val premium: Boolean,
    val premiumPlus: Boolean
)

@Composable
fun FeatureRow(
    feature: Feature,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = feature.icon,
            contentDescription = feature.name,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = feature.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CheckIcon(enabled = feature.premium)
            CheckIcon(enabled = feature.premiumPlus)
        }
    }
}

@Composable
fun CheckIcon(
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = if (enabled) Icons.Default.Check else Icons.Default.Close,
        contentDescription = if (enabled) "Feature included" else "Feature not included",
        modifier = modifier.size(20.dp),
        tint = if (enabled) MaterialTheme.colorScheme.primary
               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    )
}

/**
 * Terms and conditions text
 */
@Composable
fun TermsAndConditions(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Subscriptions auto-renew unless cancelled 24 hours before the period ends.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )

            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
