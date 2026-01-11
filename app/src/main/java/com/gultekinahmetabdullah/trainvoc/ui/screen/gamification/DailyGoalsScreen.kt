package com.gultekinahmetabdullah.trainvoc.ui.screen.gamification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.animations.rememberHapticPerformer
import com.gultekinahmetabdullah.trainvoc.ui.components.ChartData
import com.gultekinahmetabdullah.trainvoc.ui.components.CircularProgressRing
import com.gultekinahmetabdullah.trainvoc.ui.components.VerticalBarChart
import com.gultekinahmetabdullah.trainvoc.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Daily Goals Screen - Enhanced Version
 *
 * Comprehensive daily goal management with:
 * - Goal cards with sliders (Words, Quizzes, Study Time)
 * - Current progress tracking with circular progress rings
 * - 7-day completion history chart
 * - Quick presets (Casual, Regular, Intense)
 * - Haptic feedback and animations
 * - Motivational messages
 *
 * UI/UX Improvements:
 * - Material 3 design system
 * - Elevated cards with 12dp corner radius
 * - Slide-in animations with stagger
 * - Color-coded progress indicators
 * - Haptic feedback on slider changes
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
    var quizzesGoal by remember { mutableIntStateOf(prefs.getInt("daily_quizzes_goal", 5)) }
    var studyTimeGoal by remember { mutableIntStateOf(prefs.getInt("daily_study_time_goal", 30)) }

    // Mock current progress (in real app, this would come from ViewModel/Repository)
    var currentWords by remember { mutableIntStateOf(prefs.getInt("today_words_learned", 0)) }
    var currentQuizzes by remember { mutableIntStateOf(prefs.getInt("today_quizzes_completed", 0)) }
    var currentStudyTime by remember { mutableIntStateOf(prefs.getInt("today_study_minutes", 0)) }

    // Animation visibility states
    var showContent by remember { mutableStateOf(false) }

    val showResetDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Trigger animations on load
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goals") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Save button at bottom
            Surface(
                tonalElevation = Elevation.level2,
                shadowElevation = Elevation.level2
            ) {
                Button(
                    onClick = {
                        // Save to SharedPreferences
                        prefs.edit()
                            .putInt("daily_words_goal", wordsGoal)
                            .putInt("daily_quizzes_goal", quizzesGoal)
                            .putInt("daily_study_time_goal", studyTimeGoal)
                            // Save current progress (for demo purposes)
                            .putInt("today_words_learned", currentWords)
                            .putInt("today_quizzes_completed", currentQuizzes)
                            .putInt("today_study_minutes", currentStudyTime)
                            .apply()

                        onSave(DailyGoalSettings(wordsGoal, quizzesGoal, studyTimeGoal))

                        // Show confirmation
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Goals saved successfully!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md)
                        .height(ComponentSize.buttonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Save, "Save")
                    Spacer(Modifier.width(Spacing.sm))
                    Text("Save Goals", style = MaterialTheme.typography.titleMedium)
                }
            }
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
            // === MOTIVATION MESSAGE ===
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(AppAnimationDuration.medium)) +
                        slideInVertically(animationSpec = tween(AppAnimationDuration.medium))
            ) {
                MotivationCard(
                    wordsProgress = if (wordsGoal > 0) currentWords.toFloat() / wordsGoal else 0f,
                    quizzesProgress = if (quizzesGoal > 0) currentQuizzes.toFloat() / quizzesGoal else 0f,
                    timeProgress = if (studyTimeGoal > 0) currentStudyTime.toFloat() / studyTimeGoal else 0f
                )
            }

            // === CURRENT PROGRESS SECTION ===
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short
                    )
                )
            ) {
                CurrentProgressSection(
                    currentWords = currentWords,
                    wordsGoal = wordsGoal,
                    currentQuizzes = currentQuizzes,
                    quizzesGoal = quizzesGoal,
                    currentStudyTime = currentStudyTime,
                    studyTimeGoal = studyTimeGoal
                )
            }

            // === GOAL SLIDERS SECTION ===
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short * 2
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short * 2
                    )
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = "Set Your Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Words Goal
                    GoalSliderCard(
                        icon = Icons.Default.Book,
                        title = "Words to Learn",
                        currentValue = currentWords,
                        goalValue = wordsGoal,
                        onValueChange = { wordsGoal = it.roundToInt() },
                        valueRange = 1f..50f,
                        steps = 48
                    )

                    // Quizzes Goal
                    GoalSliderCard(
                        icon = Icons.Default.Quiz,
                        title = "Quizzes to Complete",
                        currentValue = currentQuizzes,
                        goalValue = quizzesGoal,
                        onValueChange = { quizzesGoal = it.roundToInt() },
                        valueRange = 1f..20f,
                        steps = 18
                    )

                    // Study Time Goal
                    GoalSliderCard(
                        icon = Icons.Default.Timer,
                        title = "Study Time",
                        currentValue = currentStudyTime,
                        goalValue = studyTimeGoal,
                        onValueChange = { studyTimeGoal = it.roundToInt() },
                        valueRange = 5f..120f,
                        steps = 22,
                        unit = "min"
                    )
                }
            }

            // === QUICK PRESETS SECTION ===
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short * 3
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short * 3
                    )
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        PresetChip(
                            title = "Casual",
                            subtitle = "5 words, 2 quizzes\n15 min",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                wordsGoal = 5
                                quizzesGoal = 2
                                studyTimeGoal = 15
                            }
                        )

                        PresetChip(
                            title = "Regular",
                            subtitle = "10 words, 5 quizzes\n30 min",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                wordsGoal = 10
                                quizzesGoal = 5
                                studyTimeGoal = 30
                            }
                        )

                        PresetChip(
                            title = "Intense",
                            subtitle = "20 words, 10 quizzes\n60 min",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                wordsGoal = 20
                                quizzesGoal = 10
                                studyTimeGoal = 60
                            }
                        )
                    }
                }
            }

            // === HISTORY CHART SECTION ===
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short * 4
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = AppAnimationDuration.medium,
                        delayMillis = StaggerDelay.short * 4
                    )
                )
            ) {
                HistoryChartSection()
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
