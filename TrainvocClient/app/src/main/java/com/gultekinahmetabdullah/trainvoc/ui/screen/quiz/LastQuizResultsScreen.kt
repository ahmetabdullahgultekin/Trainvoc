package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.animations.ConfettiAnimation
import com.gultekinahmetabdullah.trainvoc.ui.animations.StaggeredListItem
import com.gultekinahmetabdullah.trainvoc.ui.components.StatsCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.*
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizHistoryViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Last Quiz Results Screen
 *
 * Shows summary of the last quiz completed with real data from database.
 * Features:
 * - Total questions, correct/wrong/skipped
 * - Score and accuracy percentage
 * - Time taken
 * - Retry quiz option
 * - Review missed words option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastQuizResultsScreen(
    onBackClick: () -> Unit = {},
    onRetryQuiz: () -> Unit = {},
    onReviewMissed: (List<String>) -> Unit = {},
    onWordClick: (String) -> Unit = {},
    viewModel: QuizHistoryViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val lastQuizResult by viewModel.lastQuizResult.collectAsState()
    val missedWords by viewModel.missedWords.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Last Quiz Results") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                ErrorState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = error!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            lastQuizResult == null -> {
                NoQuizState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onStartQuiz = onRetryQuiz
                )
            }
            else -> {
                QuizResultsContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    totalQuestions = lastQuizResult!!.totalQuestions,
                    correctAnswers = lastQuizResult!!.correctAnswers,
                    wrongAnswers = lastQuizResult!!.wrongAnswers,
                    skippedQuestions = lastQuizResult!!.skippedQuestions,
                    accuracy = lastQuizResult!!.accuracy.toInt(),
                    timeTaken = lastQuizResult!!.timeTaken,
                    quizType = lastQuizResult!!.quizType,
                    timestamp = lastQuizResult!!.timestamp,
                    missedWords = missedWords,
                    onRetryQuiz = onRetryQuiz,
                    onReviewMissed = onReviewMissed,
                    onWordClick = onWordClick
                )
            }
        }
    }
}

@Composable
private fun QuizResultsContent(
    modifier: Modifier = Modifier,
    totalQuestions: Int,
    correctAnswers: Int,
    wrongAnswers: Int,
    skippedQuestions: Int,
    accuracy: Int,
    timeTaken: String,
    quizType: String,
    timestamp: Long,
    missedWords: List<com.gultekinahmetabdullah.trainvoc.classes.word.Word>,
    onRetryQuiz: () -> Unit,
    onReviewMissed: (List<String>) -> Unit,
    onWordClick: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(timestamp))

    // Animation states
    var showCelebration by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var triggerConfetti by remember { mutableStateOf(false) }

    // Determine celebration based on score
    val celebration = remember(accuracy) {
        when {
            accuracy >= 90 -> Celebration(
                text = "Outstanding!",
                icon = Icons.Default.EmojiEvents,
                showConfetti = true
            )
            accuracy >= 70 -> Celebration(
                text = "Great job!",
                icon = Icons.Default.Star,
                showConfetti = false
            )
            accuracy >= 50 -> Celebration(
                text = "Keep practicing!",
                icon = Icons.Default.ThumbUp,
                showConfetti = false
            )
            else -> Celebration(
                text = "Don't give up!",
                icon = Icons.Default.EmojiEmotions,
                showConfetti = false
            )
        }
    }

    // Launch animation sequence
    LaunchedEffect(Unit) {
        delay(100)
        showCelebration = true
        delay(300)
        showProgress = true
        delay(200)
        if (celebration.showConfetti) {
            triggerConfetti = true
        }
        delay(400)
        showStats = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Confetti overlay
        if (celebration.showConfetti) {
            ConfettiAnimation(
                trigger = triggerConfetti,
                modifier = Modifier.fillMaxSize(),
                particleCount = 50
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Celebration Header with Animation
            item {
                AnimatedVisibility(
                    visible = showCelebration,
                    enter = fadeIn(
                        animationSpec = tween(AppAnimationDuration.medium)
                    ) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(AppAnimationDuration.medium, easing = AppEasing.emphasized)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = celebration.icon,
                            contentDescription = celebration.text,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = celebration.text,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Circular Progress Ring with Count-up Animation
            item {
                AnimatedVisibility(
                    visible = showProgress,
                    enter = fadeIn(animationSpec = tween(AppAnimationDuration.medium))
                ) {
                    CircularProgressRing(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.lg),
                        progress = accuracy / 100f,
                        score = accuracy,
                        subtitle = "Score: $correctAnswers/$totalQuestions",
                        animate = true
                    )
                }
            }

            // Stats Cards with Staggered Animation
            if (showStats) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        StaggeredListItem(index = 0, delayMillis = StaggerDelay.short) {
                            StatsCard(
                                icon = Icons.Default.Timer,
                                value = timeTaken,
                                label = "Time",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        StaggeredListItem(index = 1, delayMillis = StaggerDelay.short) {
                            StatsCard(
                                icon = Icons.Default.EmojiEvents,
                                value = "+${accuracy * 10}",
                                label = "XP Earned",
                                modifier = Modifier.weight(1f),
                                iconTint = MaterialTheme.colorScheme.statsAverage
                            )
                        }
                    }
                }
            }

            // Performance Breakdown
            if (showStats) {
                item {
                    StaggeredListItem(index = 2, delayMillis = StaggerDelay.short) {
                        PerformanceBreakdown(
                            correctAnswers = correctAnswers,
                            wrongAnswers = wrongAnswers,
                            skippedQuestions = skippedQuestions,
                            totalQuestions = totalQuestions
                        )
                    }
                }
            }

            // Mistakes Review
            if (missedWords.isNotEmpty() && showStats) {
                item {
                    StaggeredListItem(index = 3, delayMillis = StaggerDelay.short) {
                        MistakesReview(
                            missedWords = missedWords,
                            onWordClick = onWordClick
                        )
                    }
                }
            }

            // Action Buttons
            if (showStats) {
                item {
                    StaggeredListItem(index = 4, delayMillis = StaggerDelay.short) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            Button(
                                onClick = onRetryQuiz,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, "Retry")
                                Spacer(Modifier.width(Spacing.sm))
                                Text("Start New Quiz")
                            }

                            if (missedWords.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { onReviewMissed(missedWords.map { it.word }) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Assignment, "Review")
                                    Spacer(Modifier.width(Spacing.sm))
                                    Text("Review Missed Words (${missedWords.size})")
                                }
                            }
                        }
                    }
                }
            }

            // Info Card
            if (showStats) {
                item {
                    StaggeredListItem(index = 5, delayMillis = StaggerDelay.short) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    "Info",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(Spacing.md))
                                Text(
                                    text = "Practice makes perfect! Keep reviewing missed words to improve your score.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Data class for celebration details
 */
private data class Celebration(
    val text: String,
    val icon: ImageVector,
    val showConfetti: Boolean
)

/**
 * Circular Progress Ring with animated count-up
 * Displays the quiz score as a circular progress indicator
 */
@Composable
private fun CircularProgressRing(
    progress: Float,
    score: Int,
    subtitle: String,
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    // Determine color based on progress
    val progressColor = when {
        progress >= 0.9f -> MaterialTheme.colorScheme.tertiary // Excellent (>90%)
        progress >= 0.7f -> MaterialTheme.colorScheme.statsCorrect // Good (70-89%)
        progress >= 0.5f -> MaterialTheme.colorScheme.statsGold // Okay (50-69%)
        else -> MaterialTheme.colorScheme.error // Needs work (<50%)
    }

    // Animate progress from 0 to target
    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) progress else progress,
        animationSpec = tween(
            durationMillis = AppAnimationDuration.countUp,
            easing = AppEasing.emphasized
        ),
        label = "circularProgress"
    )

    // Animate score count-up
    val animatedScore by animateIntAsState(
        targetValue = if (animate) score else score,
        animationSpec = tween(
            durationMillis = AppAnimationDuration.countUp,
            easing = AppEasing.decelerate
        ),
        label = "scoreCountUp"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(ComponentSize.circularProgressSize)
        ) {
            // Background circle
            Canvas(modifier = Modifier.size(ComponentSize.circularProgressSize)) {
                val canvasSize = size.width
                val strokeWidthPx = ComponentSize.circularProgressStroke.toPx()
                val radius = (canvasSize - strokeWidthPx) / 2

                // Draw background circle
                drawCircle(
                    color = progressColor.copy(alpha = 0.1f),
                    radius = radius,
                    center = Offset(canvasSize / 2, canvasSize / 2),
                    style = Stroke(width = strokeWidthPx)
                )

                // Draw progress arc
                val sweepAngle = 360f * animatedProgress
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                    size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                )
            }

            // Center text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$animatedScore%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Performance Breakdown with animated horizontal bars
 */
@Composable
private fun PerformanceBreakdown(
    correctAnswers: Int,
    wrongAnswers: Int,
    skippedQuestions: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
        shape = RoundedCornerShape(CornerRadius.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Text(
                text = "Performance Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Correct answers bar
            PerformanceBar(
                label = "Correct",
                count = correctAnswers,
                total = totalQuestions,
                color = MaterialTheme.colorScheme.statsCorrect,
                icon = Icons.Default.CheckCircle
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Wrong answers bar
            PerformanceBar(
                label = "Wrong",
                count = wrongAnswers,
                total = totalQuestions,
                color = MaterialTheme.colorScheme.error,
                icon = Icons.Default.Cancel
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Skipped questions bar
            PerformanceBar(
                label = "Skipped",
                count = skippedQuestions,
                total = totalQuestions,
                color = MaterialTheme.colorScheme.statsSkipped,
                icon = Icons.Default.SkipNext
            )
        }
    }
}

/**
 * Individual performance bar with animation
 */
@Composable
private fun PerformanceBar(
    label: String,
    count: Int,
    total: Int,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) count.toFloat() / total.toFloat() else 0f

    // Animate bar width
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = AppAnimationDuration.slow,
            easing = AppEasing.emphasized
        ),
        label = "barProgress_$label"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(70.dp)
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(CornerRadius.small))
        ) {
            // Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = Color.Gray.copy(alpha = 0.2f))
            }

            // Progress
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
            ) {
                drawRect(color = color)
            }
        }

        Spacer(modifier = Modifier.width(Spacing.sm))

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Mistakes Review section with expandable list
 */
@Composable
private fun MistakesReview(
    missedWords: List<com.gultekinahmetabdullah.trainvoc.classes.word.Word>,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
        shape = RoundedCornerShape(CornerRadius.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Review Mistakes (${missedWords.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + androidx.compose.animation.expandVertically(),
                exit = fadeOut() + androidx.compose.animation.shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    missedWords.forEach { word ->
                        MissedWordItem(
                            word = word.word,
                            meaning = word.meaning,
                            onClick = { onWordClick(word.word) }
                        )
                    }
                }
            }

            // Show preview if not expanded
            if (!isExpanded && missedWords.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(top = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    missedWords.take(3).forEach { word ->
                        MissedWordItem(
                            word = word.word,
                            meaning = word.meaning,
                            onClick = { onWordClick(word.word) }
                        )
                    }

                    if (missedWords.size > 3) {
                        Text(
                            text = "Tap to see ${missedWords.size - 3} more...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = Spacing.xs)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissedWordItem(
    word: String,
    meaning: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
        shape = RoundedCornerShape(CornerRadius.small),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                "View details",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
private fun NoQuizState(
    modifier: Modifier = Modifier,
    onStartQuiz: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = "No quiz",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "No Quiz Results Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Complete a quiz to see your results here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onStartQuiz) {
                Icon(Icons.Default.PlayArrow, "Start")
                Spacer(Modifier.width(8.dp))
                Text("Start a Quiz")
            }
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}
