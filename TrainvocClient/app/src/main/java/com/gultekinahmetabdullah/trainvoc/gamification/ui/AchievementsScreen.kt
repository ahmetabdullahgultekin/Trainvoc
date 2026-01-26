package com.gultekinahmetabdullah.trainvoc.gamification.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.AchievementCategory
import com.gultekinahmetabdullah.trainvoc.gamification.AchievementProgress
import com.gultekinahmetabdullah.trainvoc.gamification.AchievementTier
import com.gultekinahmetabdullah.trainvoc.ui.theme.tierBronze
import com.gultekinahmetabdullah.trainvoc.ui.theme.tierSilver
import com.gultekinahmetabdullah.trainvoc.ui.theme.tierGold
import com.gultekinahmetabdullah.trainvoc.ui.theme.tierPlatinum
import com.gultekinahmetabdullah.trainvoc.ui.theme.tierDiamond

/**
 * Main achievements screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    achievements: List<AchievementProgress>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }
    var selectedTier by remember { mutableStateOf<AchievementTier?>(null) }
    var selectedAchievement by remember { mutableStateOf<AchievementProgress?>(null) }

    val filteredAchievements = achievements.filter { progress ->
        (selectedCategory == null || progress.achievement.category == selectedCategory) &&
        (selectedTier == null || progress.achievement.tier == selectedTier)
    }

    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    // Responsive design: Determine grid columns based on screen width
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val gridColumns = when {
        screenWidthDp >= 840 -> 3  // Large tablets/desktops
        screenWidthDp >= 600 -> 3  // Small tablets/landscape
        else -> 2                  // Phones
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Achievements")
                        Text(
                            text = "$unlockedCount / $totalCount Unlocked",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
            }

            // Progress overview
            item {
                AchievementOverviewCard(
                    unlockedCount = unlockedCount,
                    totalCount = totalCount
                )
            }

            // Category filter
            item {
                CategoryFilterChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // Tier filter
            item {
                TierFilterChips(
                    selectedTier = selectedTier,
                    onTierSelected = { selectedTier = it }
                )
            }

            // Achievements grid section heading
            item {
                Text(
                    text = "${filteredAchievements.size} Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Achievements grid (responsive)
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((filteredAchievements.size / gridColumns + if (filteredAchievements.size % gridColumns > 0) 1 else 0) * 140).dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredAchievements,
                        key = { achievement -> achievement.achievement.id }
                    ) { progress ->
                        AchievementCard(
                            achievement = progress,
                            isCompact = true,
                            onClick = { selectedAchievement = progress }
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Achievement Detail Dialog
    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { selectedAchievement = null }
        )
    }
}

/**
 * Achievement overview stats card
 */
@Composable
fun AchievementOverviewCard(
    unlockedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val percentage = (unlockedCount.toFloat() / totalCount * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$unlockedCount",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Achievements Unlocked",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = "🏆",
                    fontSize = 64.sp
                )
            }

            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Text(
                text = "$percentage% Complete",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Category filter chips
 */
@Composable
fun CategoryFilterChips(
    selectedCategory: AchievementCategory?,
    onCategorySelected: (AchievementCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )

            AchievementCategory.values().take(3).forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(if (selectedCategory == category) null else category) },
                    label = { Text(category.displayName) }
                )
            }
        }
    }
}

/**
 * Tier filter chips
 */
@Composable
fun TierFilterChips(
    selectedTier: AchievementTier?,
    onTierSelected: (AchievementTier?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Rarity",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTier == null,
                onClick = { onTierSelected(null) },
                label = { Text("All") }
            )

            AchievementTier.values().forEach { tier ->
                FilterChip(
                    selected = selectedTier == tier,
                    onClick = { onTierSelected(if (selectedTier == tier) null else tier) },
                    label = { Text(tier.displayName) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(getTierColor(tier))
                        )
                    }
                )
            }
        }
    }
}

/**
 * Individual achievement card
 */
@Composable
fun AchievementCard(
    achievement: AchievementProgress,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isUnlocked = achievement.isUnlocked

    if (isCompact) {
        // Compact grid layout
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(130.dp)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            colors = CardDefaults.cardColors(
                containerColor = if (isUnlocked) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (achievement.achievement.tier == AchievementTier.DIAMOND && isUnlocked) {
                BorderStroke(2.dp, getTierColor(AchievementTier.DIAMOND))
            } else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Icon/Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) getTierColor(achievement.achievement.tier)
                            else MaterialTheme.colorScheme.surface
                        )
                        .alpha(if (isUnlocked) 1f else 0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.achievement.icon,
                        fontSize = 24.sp
                    )
                }

                // Title
                Text(
                    text = achievement.achievement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSecondaryContainer
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Progress or checkmark
                if (!isUnlocked) {
                    LinearProgressIndicator(
                        progress = { achievement.progressPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = getTierColor(achievement.achievement.tier),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    } else {
        // Full-width card layout
        Card(
            modifier = modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            colors = CardDefaults.cardColors(
                containerColor = if (isUnlocked) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (achievement.achievement.tier == AchievementTier.DIAMOND && isUnlocked) {
                BorderStroke(2.dp, getTierColor(AchievementTier.DIAMOND))
            } else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon/Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) getTierColor(achievement.achievement.tier)
                            else MaterialTheme.colorScheme.surface
                        )
                        .alpha(if (isUnlocked) 1f else 0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.achievement.icon,
                        fontSize = 36.sp
                    )
                }

                // Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = achievement.achievement.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSecondaryContainer
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Tier badge
                        Surface(
                            color = getTierColor(achievement.achievement.tier),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = achievement.achievement.tier.displayName,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Text(
                        text = achievement.achievement.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!isUnlocked) {
                        Spacer(Modifier.height(4.dp))

                        // Progress bar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { achievement.progressPercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )

                            Text(
                                text = "${achievement.currentProgress} / ${achievement.achievement.requirement}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Unlocked date
                        achievement.unlockedAt?.let { timestamp ->
                            Text(
                                text = "Unlocked ${formatDate(timestamp)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Checkmark for unlocked
                if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = getTierColor(achievement.achievement.tier),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Achievement unlock celebration dialog
 */
@Composable
fun AchievementUnlockDialog(
    achievement: Achievement,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 80.sp
                )
                Surface(
                    color = getTierColor(achievement.tier),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = achievement.tier.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        title = {
            Text(
                text = "Achievement Unlocked!",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "Achievement icon")
                Spacer(Modifier.width(8.dp))
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue")
            }
        }
    )
}

/**
 * Get theme-aware color for achievement tier
 * Returns the appropriate color from MaterialTheme based on tier and current theme (light/dark)
 */
@Composable
private fun getTierColor(tier: AchievementTier): Color {
    return when (tier) {
        AchievementTier.BRONZE -> MaterialTheme.colorScheme.tierBronze
        AchievementTier.SILVER -> MaterialTheme.colorScheme.tierSilver
        AchievementTier.GOLD -> MaterialTheme.colorScheme.tierGold
        AchievementTier.PLATINUM -> MaterialTheme.colorScheme.tierPlatinum
        AchievementTier.DIAMOND -> MaterialTheme.colorScheme.tierDiamond
    }
}

/**
 * Format timestamp to relative date
 */
private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        days == 0L -> "today"
        days == 1L -> "yesterday"
        days < 7 -> "$days days ago"
        days < 30 -> "${days / 7} weeks ago"
        days < 365 -> "${days / 30} months ago"
        else -> "${days / 365} years ago"
    }
}

/**
 * Achievement Detail Dialog
 * Shows full details of an achievement when tapped
 */
@Composable
fun AchievementDetailDialog(
    achievement: AchievementProgress,
    onDismiss: () -> Unit
) {
    val isUnlocked = achievement.isUnlocked
    val tierColor = getTierColor(achievement.achievement.tier)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) tierColor
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .alpha(if (isUnlocked) 1f else 0.5f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.achievement.icon,
                    fontSize = 48.sp
                )
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = achievement.achievement.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                // Tier badge
                Surface(
                    color = tierColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = achievement.achievement.tier.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Description
                Text(
                    text = achievement.achievement.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Category chip
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Category: ${achievement.achievement.category.displayName}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Progress section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isUnlocked) {
                            // Unlocked status
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Unlocked",
                                    tint = tierColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Unlocked!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = tierColor
                                )
                            }

                            achievement.unlockedAt?.let { timestamp ->
                                Text(
                                    text = "Achieved ${formatDate(timestamp)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            // Progress bar for locked achievements
                            Text(
                                text = "Progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            LinearProgressIndicator(
                                progress = { achievement.progressPercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Text(
                                text = "${achievement.currentProgress} / ${achievement.achievement.requirement}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${achievement.progressPercentage}% complete",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // XP reward info
                if (isUnlocked) {
                    Text(
                        text = "+${achievement.achievement.tier.xpReward} XP earned",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Reward: +${achievement.achievement.tier.xpReward} XP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
