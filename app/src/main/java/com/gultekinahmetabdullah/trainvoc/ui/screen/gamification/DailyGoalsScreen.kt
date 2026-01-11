package com.gultekinahmetabdullah.trainvoc.ui.screen.gamification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import kotlin.math.roundToInt

/**
 * Daily Goals Screen
 *
 * Allows users to customize their daily learning targets:
 * - Words to learn
 * - Reviews to complete
 * - Quizzes to finish
 * - Study time in minutes
 *
 * Features:
 * - Sliders for each goal type
 * - Visual progress indicators
 * - Goal suggestions based on level
 * - Save/reset functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalsScreen(
    onBackClick: () -> Unit = {},
    onSave: (DailyGoalSettings) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)

    // Load current goals from SharedPreferences
    var wordsGoal by remember { mutableIntStateOf(prefs.getInt("daily_words_goal", 10)) }
    var reviewsGoal by remember { mutableIntStateOf(prefs.getInt("daily_reviews_goal", 20)) }
    var quizzesGoal by remember { mutableIntStateOf(prefs.getInt("daily_quizzes_goal", 5)) }
    var studyTimeGoal by remember { mutableIntStateOf(prefs.getInt("daily_study_time_goal", 15)) }

    val showResetDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goals") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Reset button
                    IconButton(onClick = { showResetDialog.value = true }) {
                        Icon(Icons.Default.RestartAlt, "Reset to Defaults")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Save button at bottom
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 3.dp
            ) {
                Button(
                    onClick = {
                        // Save to SharedPreferences
                        prefs.edit()
                            .putInt("daily_words_goal", wordsGoal)
                            .putInt("daily_reviews_goal", reviewsGoal)
                            .putInt("daily_quizzes_goal", quizzesGoal)
                            .putInt("daily_study_time_goal", studyTimeGoal)
                            .apply()

                        onSave(DailyGoalSettings(wordsGoal, reviewsGoal, quizzesGoal, studyTimeGoal))

                        // Show confirmation
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar(
                                message = "Goals saved successfully!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Save, "Save")
                    Spacer(Modifier.width(8.dp))
                    Text("Save Goals")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with description
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Flag,
                            "Goals",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Set Your Daily Targets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Customize your daily learning goals. We'll help you track your progress and stay motivated!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Words Goal
            GoalSliderCard(
                icon = Icons.Default.Book,
                title = "Words to Learn",
                subtitle = "New words to study each day",
                value = wordsGoal,
                onValueChange = { wordsGoal = it.roundToInt() },
                valueRange = 5f..100f,
                steps = 18 // 5, 10, 15, 20, ..., 100
            )

            // Reviews Goal
            GoalSliderCard(
                icon = Icons.Default.Replay,
                title = "Reviews to Complete",
                subtitle = "Practice words with spaced repetition",
                value = reviewsGoal,
                onValueChange = { reviewsGoal = it.roundToInt() },
                valueRange = 10f..500f,
                steps = 48 // 10, 20, 30, ..., 500
            )

            // Quizzes Goal
            GoalSliderCard(
                icon = Icons.Default.Quiz,
                title = "Quizzes to Finish",
                subtitle = "Complete quiz sessions",
                value = quizzesGoal,
                onValueChange = { quizzesGoal = it.roundToInt() },
                valueRange = 1f..20f,
                steps = 18 // 1, 2, 3, ..., 20
            )

            // Study Time Goal
            GoalSliderCard(
                icon = Icons.Default.Timer,
                title = "Study Time (minutes)",
                subtitle = "Total daily learning time",
                value = studyTimeGoal,
                onValueChange = { studyTimeGoal = it.roundToInt() },
                valueRange = 5f..120f,
                steps = 22 // 5, 10, 15, ..., 120
            )

            // Suggested Presets
            Text(
                text = "Suggested Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetCard(
                    title = "Beginner",
                    subtitle = "5 words, 10 reviews",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        wordsGoal = 5
                        reviewsGoal = 10
                        quizzesGoal = 2
                        studyTimeGoal = 10
                    }
                )

                PresetCard(
                    title = "Intermediate",
                    subtitle = "10 words, 20 reviews",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        wordsGoal = 10
                        reviewsGoal = 20
                        quizzesGoal = 5
                        studyTimeGoal = 15
                    }
                )

                PresetCard(
                    title = "Advanced",
                    subtitle = "15 words, 30 reviews",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        wordsGoal = 15
                        reviewsGoal = 30
                        quizzesGoal = 7
                        studyTimeGoal = 20
                    }
                )
            }

            // Info card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        "Info",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Tip: Start with smaller goals and gradually increase as you build your learning habit!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog.value) {
        AlertDialog(
            onDismissRequest = { showResetDialog.value = false },
            title = { Text("Reset to Defaults?") },
            text = { Text("This will reset all goals to the recommended intermediate level.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        wordsGoal = 10
                        reviewsGoal = 20
                        quizzesGoal = 5
                        studyTimeGoal = 15
                        showResetDialog.value = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GoalSliderCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Int,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                icon,
                                title,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))

            Slider(
                value = value.toFloat(),
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps
            )
        }
    }
}

@Composable
private fun PresetCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class DailyGoalSettings(
    val wordsGoal: Int,
    val reviewsGoal: Int,
    val quizzesGoal: Int,
    val studyTimeGoal: Int
)
