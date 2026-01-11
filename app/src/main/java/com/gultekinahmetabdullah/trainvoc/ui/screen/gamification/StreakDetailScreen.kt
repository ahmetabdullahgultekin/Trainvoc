package com.gultekinahmetabdullah.trainvoc.ui.screen.gamification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.components.AchievementBadge
import com.gultekinahmetabdullah.trainvoc.ui.components.SecondaryButton
import com.gultekinahmetabdullah.trainvoc.ui.components.StatsCard
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.HomeViewModel
import com.gultekinahmetabdullah.trainvoc.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.min

/**
 * Streak Detail Screen
 *
 * Shows comprehensive streak information:
 * - Hero counter with animated fire
 * - 365-day calendar heatmap (GitHub-style)
 * - Streak milestones with badges
 * - Detailed statistics
 * - Comparison and motivation
 * - Share functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakDetailScreen(
    onBackClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentStreak = uiState.currentStreak

    // TODO: Get actual data from database
    val longestStreak = remember { maxOf(currentStreak, 15) }
    val totalActiveDays = remember { currentStreak + 10 } // Placeholder
    val streakPercentage = remember {
        val totalDays = 365
        ((totalActiveDays.toFloat() / totalDays) * 100).toInt()
    }

    // Mock activity data for heatmap (365 days)
    val activityData = remember {
        generateMockActivityData(currentStreak)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Streak Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // 1. Hero Counter with Fire Animation
            HeroStreakCounter(currentStreak = currentStreak)

            // 2. Stats Cards Row
            StatsCardsRow(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalActiveDays = totalActiveDays,
                streakPercentage = streakPercentage
            )

            // 3. Comparison Card
            ComparisonCard(
                currentStreak = currentStreak,
                longestStreak = longestStreak
            )

            // 4. Motivation Section
            MotivationSection(currentStreak = currentStreak)

            // 5. Calendar Heatmap (365 days)
            CalendarHeatmapSection(activityData = activityData)

            // 6. Milestones Section
            MilestonesSection(currentStreak = currentStreak)

            // 7. Share Button
            ShareStreakButton(currentStreak = currentStreak)

            // 8. Info Card
            InfoCard()
        }
    }
}

/**
 * Hero Counter Component
 * Large animated streak number with fire emoji/animation
 */
@Composable
private fun HeroStreakCounter(currentStreak: Int) {
    // Count-up animation
    var displayedStreak by remember { mutableStateOf(0) }

    LaunchedEffect(currentStreak) {
        val duration = AnimationDuration.countUp
        val steps = min(currentStreak, 100) // Limit animation steps
        val delayPerStep = if (steps > 0) duration / steps else 0

        for (i in 0..currentStreak) {
            displayedStreak = i
            if (i < currentStreak) {
                delay(delayPerStep.toLong())
            }
        }
    }

    // Fire scale animation (pulsing)
    val infiniteTransition = rememberInfiniteTransition(label = "fireAnimation")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fireScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fire Animation (size based on streak)
            val (fireEmoji, fireSize) = when {
                currentStreak >= 100 -> "🔥🔥🔥🔥" to 96.dp // Epic fire
                currentStreak >= 30 -> "🔥🔥🔥" to 80.dp // Large fire
                currentStreak >= 7 -> "🔥🔥" to 64.dp // Medium fire
                currentStreak >= 1 -> "🔥" to 48.dp // Small fire
                else -> "💨" to 48.dp // No streak
            }

            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            ) {
                Text(
                    text = fireEmoji,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5f,
                    modifier = Modifier
                        .scale(if (currentStreak > 0) fireScale else 1f)
                        .padding(bottom = Spacing.md)
                )
            }

            // Animated Counter
            Text(
                text = displayedStreak.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.displayLarge.fontSize * 2f,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = if (currentStreak == 1) "Day" else "Days",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(Spacing.sm))

            // Status message
            Text(
                text = getStatusMessage(currentStreak),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Stats Cards Row
 * Four key statistics in a grid
 */
@Composable
private fun StatsCardsRow(
    currentStreak: Int,
    longestStreak: Int,
    totalActiveDays: Int,
    streakPercentage: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatsCard(
                icon = Icons.Default.LocalFireDepartment,
                value = currentStreak.toString(),
                label = "Current Streak",
                iconTint = Color(0xFFFF6B35),
                modifier = Modifier.weight(1f)
            )

            StatsCard(
                icon = Icons.Default.EmojiEvents,
                value = longestStreak.toString(),
                label = "Longest Streak",
                iconTint = Color(0xFFFFD600),
                modifier = Modifier.weight(1f)
            )
        }

        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatsCard(
                icon = Icons.Default.CalendarMonth,
                value = totalActiveDays.toString(),
                label = "Total Days",
                iconTint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            StatsCard(
                icon = Icons.Default.TrendingUp,
                value = "$streakPercentage%",
                label = "Streak Rate",
                iconTint = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Comparison Card
 * Shows progress toward longest streak
 */
@Composable
private fun ComparisonCard(
    currentStreak: Int,
    longestStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Best Streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "$longestStreak days",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Icon(
                    Icons.Default.EmojiEvents,
                    "Trophy",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFFFD600)
                )
            }

            Spacer(Modifier.height(Spacing.md))

            // Progress bar
            val progress = if (longestStreak > 0) {
                (currentStreak.toFloat() / longestStreak).coerceIn(0f, 1f)
            } else 0f

            Column {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(CornerRadius.small)),
                    color = Color(0xFF4CAF50),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(Spacing.sm))

                Text(
                    text = when {
                        currentStreak >= longestStreak -> "New record! Keep going!"
                        currentStreak >= longestStreak * 0.8f -> "Almost there! ${longestStreak - currentStreak} days to beat your record!"
                        currentStreak >= longestStreak * 0.5f -> "Halfway to your best streak!"
                        else -> "${longestStreak - currentStreak} days to your longest streak"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

/**
 * Motivation Section
 * Dynamic motivational message based on streak
 */
@Composable
private fun MotivationSection(currentStreak: Int) {
    val (message, icon) = getMotivationalMessage(currentStreak)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                "Motivation",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(Modifier.width(Spacing.md))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MilestoneCard(
    icon: String,
    title: String,
    subtitle: String,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Surface(
                color = if (isUnlocked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (isUnlocked) {
                Icon(
                    Icons.Default.CheckCircle,
                    "Unlocked",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Default.Lock,
                    "Locked",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun DayActivitySquare(
    dayName: String,
    hasActivity: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (hasActivity) {
                        Color(0xFF4CAF50) // Green for activity
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant // Gray for no activity
                    }
                )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = dayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
