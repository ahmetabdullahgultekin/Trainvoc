package com.gultekinahmetabdullah.trainvoc.gamification.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.gamification.DailyGoal

/**
 * Daily goals card with progress bars
 */
@Composable
fun DailyGoalsCard(
    goals: DailyGoal,
    onCustomizeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val overallProgress = goals.getOverallProgress()
    val isComplete = goals.isAllGoalsMet()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isComplete) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )

                    Column {
                        Text(
                            text = "Daily Goals",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isComplete) "All goals completed! 🎉"
                                   else "$overallProgress% Complete",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isComplete) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onCustomizeClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Customize goals")
                }
            }

            Divider()

            // Individual goal progress bars
            GoalProgressRow(
                icon = Icons.Default.Book,
                label = "Words",
                current = goals.wordsToday,
                target = goals.wordsGoal,
                progress = goals.getWordsProgress(),
                color = Color(0xFF4CAF50)
            )

            GoalProgressRow(
                icon = Icons.Default.Replay,
                label = "Reviews",
                current = goals.reviewsToday,
                target = goals.reviewsGoal,
                progress = goals.getReviewsProgress(),
                color = Color(0xFF2196F3)
            )

            GoalProgressRow(
                icon = Icons.Default.Quiz,
                label = "Quizzes",
                current = goals.quizzesToday,
                target = goals.quizzesGoal,
                progress = goals.getQuizzesProgress(),
                color = Color(0xFFFF9800)
            )

            GoalProgressRow(
                icon = Icons.Default.AccessTime,
                label = "Time",
                current = goals.timeTodayMinutes,
                target = goals.timeGoalMinutes,
                progress = goals.getTimeProgress(),
                color = Color(0xFF9C27B0),
                unit = "min"
            )

            // Overall progress
            if (!isComplete) {
                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = overallProgress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Lifetime stats
            if (goals.goalsCompletedTotal > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${goals.goalsCompletedTotal} days with all goals completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual goal progress row
 */
@Composable
fun GoalProgressRow(
    icon: ImageVector,
    label: String,
    current: Int,
    target: Int,
    progress: Float,
    color: Color,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    val isComplete = current >= target

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isComplete) color else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isComplete) FontWeight.Bold else FontWeight.Normal
                )

                Text(
                    text = "$current / $target $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isComplete) color else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isComplete) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        if (isComplete) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Complete",
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Goal customization dialog
 */
@Composable
fun GoalCustomizationDialog(
    currentGoals: DailyGoal,
    onDismiss: () -> Unit,
    onSave: (wordsGoal: Int, reviewsGoal: Int, quizzesGoal: Int, timeGoal: Int) -> Unit
) {
    var wordsGoal by remember { mutableStateOf(currentGoals.wordsGoal) }
    var reviewsGoal by remember { mutableStateOf(currentGoals.reviewsGoal) }
    var quizzesGoal by remember { mutableStateOf(currentGoals.quizzesGoal) }
    var timeGoal by remember { mutableStateOf(currentGoals.timeGoalMinutes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Daily Goals") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Set your personalized daily learning targets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            wordsGoal = 5
                            reviewsGoal = 10
                            quizzesGoal = 3
                            timeGoal = 10
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Beginner")
                    }

                    OutlinedButton(
                        onClick = {
                            wordsGoal = 10
                            reviewsGoal = 20
                            quizzesGoal = 5
                            timeGoal = 15
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Default")
                    }

                    OutlinedButton(
                        onClick = {
                            wordsGoal = 20
                            reviewsGoal = 40
                            quizzesGoal = 10
                            timeGoal = 30
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Advanced")
                    }
                }

                Divider()

                // Custom sliders
                GoalSlider(
                    label = "Words",
                    value = wordsGoal,
                    onValueChange = { wordsGoal = it },
                    valueRange = 1f..50f
                )

                GoalSlider(
                    label = "Reviews",
                    value = reviewsGoal,
                    onValueChange = { reviewsGoal = it },
                    valueRange = 5f..100f
                )

                GoalSlider(
                    label = "Quizzes",
                    value = quizzesGoal,
                    onValueChange = { quizzesGoal = it },
                    valueRange = 1f..20f
                )

                GoalSlider(
                    label = "Time (minutes)",
                    value = timeGoal,
                    onValueChange = { timeGoal = it },
                    valueRange = 5f..60f
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(wordsGoal, reviewsGoal, quizzesGoal, timeGoal)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Goal slider for customization
 */
@Composable
fun GoalSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$value",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) - 1).toInt()
        )
    }
}

/**
 * Compact daily goals indicator
 */
@Composable
fun CompactDailyGoalsIndicator(
    progress: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = when {
            progress == 100 -> MaterialTheme.colorScheme.primary
            progress >= 75 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (progress == 100) Icons.Default.CheckCircle
                             else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (progress == 100) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (progress == 100) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
