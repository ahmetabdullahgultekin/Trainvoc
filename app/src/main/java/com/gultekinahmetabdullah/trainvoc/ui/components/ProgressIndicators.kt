package com.gultekinahmetabdullah.trainvoc.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.AppEasing
import com.gultekinahmetabdullah.trainvoc.ui.theme.ComponentSize

/**
 * Circular Progress Indicator with animated count-up
 * Used in quiz results and progress tracking screens
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param percentage Display percentage text
 * @param subtitle Optional subtitle text (e.g., "Score: 17/20")
 * @param size Diameter of the circle
 * @param strokeWidth Width of the progress arc
 * @param color Color of the progress arc (defaults to theme-based color)
 * @param animate Whether to animate the progress on appear
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    percentage: String,
    subtitle: String? = null,
    size: Dp = ComponentSize.circularProgressSize,
    strokeWidth: Dp = ComponentSize.circularProgressStroke,
    color: Color? = null,
    animate: Boolean = true
) {
    // Determine color based on progress
    val progressColor = color ?: when {
        progress >= 0.9f -> MaterialTheme.colorScheme.tertiary // Excellent (>90%)
        progress >= 0.7f -> Color(0xFF4CAF50) // Good (70-89%)
        progress >= 0.5f -> Color(0xFFF9A825) // Okay (50-69%)
        else -> MaterialTheme.colorScheme.error // Needs work (<50%)
    }

    // Animate progress from 0 to target
    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) progress else progress,
        animationSpec = tween(
            durationMillis = AnimationDuration.countUp,
            easing = AppEasing.emphasized
        ),
        label = "circularProgress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        // Background circle
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = size.toPx()
            val strokeWidthPx = strokeWidth.toPx()
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
                text = percentage,
                style = MaterialTheme.typography.displayMedium,
                color = progressColor
            )
            if (subtitle != null) {
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
 * Simple circular progress ring without text
 * Used for smaller progress indicators
 */
@Composable
fun CircularProgressRing(
    progress: Float,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = AnimationDuration.medium,
            easing = AppEasing.standard
        ),
        label = "ringProgress"
    )

    Canvas(modifier = modifier.size(size)) {
        val canvasSize = size.toPx()
        val strokeWidthPx = strokeWidth.toPx()

        // Background circle
        drawCircle(
            color = backgroundColor,
            radius = (canvasSize - strokeWidthPx) / 2,
            center = Offset(canvasSize / 2, canvasSize / 2),
            style = Stroke(width = strokeWidthPx)
        )

        // Progress arc
        val sweepAngle = 360f * animatedProgress
        drawArc(
            color = color,
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
}

/**
 * XP/Level Progress Bar
 * Shows progress towards next level with current and target XP
 */
@Composable
fun XPProgressBar(
    currentXP: Int,
    targetXP: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentXP.toFloat() / targetXP.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = AnimationDuration.slow,
            easing = AppEasing.emphasized
        ),
        label = "xpProgress"
    )

    Column(modifier = modifier) {
        Text(
            text = "Level $level",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            modifier = Modifier.size(width = 200.dp, height = 8.dp)
        ) {
            // Background
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.2f),
                    size = size
                )
            }

            // Progress
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRoundRect(
                    color = Color(0xFF9C27B0), // XP purple color
                    size = Size(size.width * animatedProgress, size.height)
                )
            }
        }

        Text(
            text = "$currentXP / $targetXP XP",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
