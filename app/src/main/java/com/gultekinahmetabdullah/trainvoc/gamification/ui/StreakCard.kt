package com.gultekinahmetabdullah.trainvoc.gamification.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.gamification.StreakTracking

/**
 * Large streak display card with fire animation
 */
@Composable
fun StreakCard(
    streak: StreakTracking,
    onStreakFreezeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )

    val fireColors = when {
        streak.currentStreak >= 100 -> listOf(Color(0xFFFF6B35), Color(0xFFFF9F1C), Color(0xFFFFE66D))
        streak.currentStreak >= 30 -> listOf(Color(0xFFFF6B35), Color(0xFFFF9F1C))
        streak.currentStreak >= 7 -> listOf(Color(0xFFFF6B35), Color(0xFFFFA07A))
        else -> listOf(Color(0xFFFF6347), Color(0xFFFF7F50))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fire emoji with animation
                Text(
                    text = "🔥",
                    fontSize = 72.sp,
                    modifier = Modifier.scale(if (streak.currentStreak > 0) fireScale else 1f)
                )

                // Streak counter
                Text(
                    text = "${streak.currentStreak}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Day Streak",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Status message
                Surface(
                    color = when {
                        streak.isActiveToday() -> MaterialTheme.colorScheme.primary
                        streak.daysUntilBreak() == 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.errorContainer
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = streak.getStatusMessage(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (streak.isActiveToday()) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StreakStat(
                        label = "Longest",
                        value = "${streak.longestStreak}",
                        icon = "🏆"
                    )

                    StreakStat(
                        label = "Active Days",
                        value = "${streak.totalActiveDays}",
                        icon = "📅"
                    )

                    if (streak.streakFreezeCount > 0) {
                        StreakStat(
                            label = "Freezes",
                            value = "${streak.streakFreezeCount}",
                            icon = "❄️"
                        )
                    }
                }

                // Streak freeze button (Premium)
                if (!streak.isActiveToday() && streak.daysUntilBreak() == 0) {
                    OutlinedButton(
                        onClick = onStreakFreezeClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AcUnit, contentDescription = "Streak freeze - preserve your streak")
                        Spacer(Modifier.width(8.dp))
                        Text("Use Streak Freeze (Premium)")
                    }
                }
            }
        }
    }
}

/**
 * Individual streak stat display
 */
@Composable
fun StreakStat(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

/**
 * Compact streak indicator for app bar
 */
@Composable
fun CompactStreakIndicator(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = if (currentStreak > 0) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🔥",
                fontSize = 20.sp
            )
            Text(
                text = "$currentStreak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Streak milestone celebration dialog
 */
@Composable
fun StreakMilestoneDialog(
    milestone: Int,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(
                text = "🎉",
                fontSize = 64.sp
            )
        },
        title = {
            Text(
                text = "$milestone Day Streak!",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Congratulations on your incredible dedication!",
                    textAlign = TextAlign.Center
                )

                val message = when {
                    milestone >= 365 -> "You're a legend! 🏆"
                    milestone >= 100 -> "Unstoppable! 🔥"
                    milestone >= 30 -> "Amazing commitment! ⭐"
                    milestone >= 7 -> "Keep it going! 💪"
                    else -> "Great start! 🎯"
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "Share streak achievement")
                Spacer(Modifier.width(8.dp))
                Text("Share Achievement")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue")
            }
        }
    )
}
