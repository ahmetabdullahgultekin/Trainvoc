package com.gultekinahmetabdullah.trainvoc.ui.animations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated Progress Indicators
 *
 * Collection of animated progress indicators for various use cases:
 * - Circular progress with percentage
 * - Linear progress bars
 * - Learning progress indicators
 * - Quiz progress indicators
 */

// ============================================================
// Circular Progress Indicators
// ============================================================

/**
 * Animated Circular Progress Indicator with Percentage
 *
 * @param progress Current progress (0f to 1f)
 * @param modifier Modifier
 * @param size Size of the indicator
 * @param strokeWidth Width of the progress arc
 * @param color Progress color
 * @param backgroundColor Background arc color
 * @param showPercentage Whether to show percentage text
 */
@Composable
fun AnimatedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = AnimationSpecs.DURATION_LONG,
            easing = AnimationSpecs.EasingEmphasized
        ),
        label = "circular_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = this.size
            val radius = (canvasSize.minDimension / 2) - strokeWidth.toPx() / 2

            // Background circle
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    (canvasSize.width - radius * 2) / 2,
                    (canvasSize.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(
                    (canvasSize.width - radius * 2) / 2,
                    (canvasSize.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Learning Progress Ring
 * Specialized circular progress for learning statistics
 */
@Composable
fun LearningProgressRing(
    learned: Int,
    total: Int,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val progress = if (total > 0) learned.toFloat() / total.toFloat() else 0f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AnimatedCircularProgress(
            progress = progress,
            size = size,
            color = color,
            showPercentage = false
        )

        Text(
            text = "$learned / $total",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================
// Linear Progress Indicators
// ============================================================

/**
 * Animated Linear Progress Bar
 *
 * @param progress Current progress (0f to 1f)
 * @param modifier Modifier
 * @param color Progress color
 * @param trackColor Background track color
 * @param height Height of the progress bar
 */
@Composable
fun AnimatedLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "linear_progress"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .progressSemantics(progress)
    ) {
        val strokeWidth = size.height

        // Background track
        drawLine(
            color = trackColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Progress line
        if (animatedProgress > 0f) {
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * animatedProgress, size.height / 2),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Segmented Progress Bar
 * Progress bar divided into segments
 *
 * @param current Current segment
 * @param total Total segments
 */
@Composable
fun SegmentedProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(total) { index ->
            val isActive = index < current
            val progress = if (isActive) 1f else 0f

            AnimatedLinearProgress(
                progress = progress,
                modifier = Modifier.weight(1f),
                color = activeColor,
                trackColor = inactiveColor,
                height = height
            )
        }
    }
}

// ============================================================
// Specialized Progress Indicators
// ============================================================

/**
 * Quiz Progress Indicator
 * Shows current question number and total questions
 */
@Composable
fun QuizProgressIndicator(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question $currentQuestion of $totalQuestions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${(currentQuestion.toFloat() / totalQuestions * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedLinearProgress(
            progress = currentQuestion.toFloat() / totalQuestions.toFloat(),
            height = 6.dp
        )
    }
}

/**
 * Streak Progress Indicator
 * Shows streak progress towards next milestone
 */
@Composable
fun StreakProgressIndicator(
    currentStreak: Int,
    nextMilestone: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentStreak.toFloat() / nextMilestone.toFloat()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥 $currentStreak day streak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "$nextMilestone days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedLinearProgress(
            progress = progress,
            height = 10.dp,
            color = MaterialTheme.colorScheme.error // Fire/streak urgency color
        )
    }
}

/**
 * Level Progress Bar
 * Shows progress towards next level
 */
@Composable
fun LevelProgressBar(
    currentXP: Int,
    requiredXP: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentXP.toFloat() / requiredXP.toFloat()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Level $level",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$currentXP / $requiredXP XP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedLinearProgress(
            progress = progress,
            height = 8.dp
        )
    }
}

/**
 * Loading Pulse Indicator
 * Simple pulsing circle for loading states
 */
@Composable
fun LoadingPulse(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 40.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .progressSemantics(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = this.size.minDimension / 2
            )
        }
    }
}
