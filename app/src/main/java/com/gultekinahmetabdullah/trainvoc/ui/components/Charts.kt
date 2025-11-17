package com.gultekinahmetabdullah.trainvoc.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Donut Chart Component
 *
 * A circular chart showing distribution of values with smooth animations.
 * Each segment animates in with a spring effect.
 *
 * @param data List of ChartData items to display
 * @param modifier Modifier for the chart
 * @param size Size of the chart in Dp
 * @param strokeWidth Width of the donut ring
 */
@Composable
fun DonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 40.dp
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val total = data.sumOf { it.value.toDouble() }.toFloat()
    if (total == 0f) return

    var startAngle = -90f

    Canvas(modifier = modifier.size(size)) {
        val strokeWidthPx = strokeWidth.toPx()
        val chartSize = size.toPx() - strokeWidthPx

        data.forEach { chartData ->
            val sweepAngle = (chartData.value / total) * 360f * animatedProgress.value

            drawArc(
                color = chartData.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(chartSize, chartSize),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            startAngle += sweepAngle
        }
    }
}

/**
 * Progress Ring Component
 *
 * A circular progress indicator with percentage text in the center.
 * Animates from 0 to target progress with smooth easing.
 *
 * @param progress Progress value from 0f to 1f
 * @param modifier Modifier for the ring
 * @param size Size of the ring in Dp
 * @param strokeWidth Width of the ring stroke
 * @param color Color of the progress arc
 * @param backgroundColor Background color of the track
 */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 16.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = AnimationDuration.progress)
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val chartSize = size.toPx() - strokeWidthPx

            // Background circle
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(chartSize, chartSize),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(chartSize, chartSize),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        // Percentage Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedProgress.value * 100).toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Success Rate",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Vertical Bar Chart Component
 *
 * Displays data as vertical bars with labels.
 * Each bar animates from bottom to top with smooth easing.
 *
 * @param data List of ChartData items to display
 * @param modifier Modifier for the chart
 * @param maxHeight Maximum height of the tallest bar
 */
@Composable
fun VerticalBarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 120.dp
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = AnimationDuration.progress)
        )
    }

    val maxValue = data.maxOfOrNull { it.value } ?: 1f
    if (maxValue == 0f) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { chartData ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Value Text
                Text(
                    text = chartData.value.toInt().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = chartData.color
                )
                Spacer(modifier = Modifier.height(Spacing.extraSmall))

                // Bar
                val barHeight = maxHeight * (chartData.value / maxValue) * animatedProgress.value
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(barHeight)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawRoundRect(
                            color = chartData.color,
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                8.dp.toPx(),
                                8.dp.toPx()
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.small))

                // Label Text
                Text(
                    text = chartData.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Chart Legend Component
 *
 * Displays a legend for chart data with colored indicators and labels.
 *
 * @param data List of ChartData items
 * @param modifier Modifier for the legend
 */
@Composable
fun ChartLegend(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        data.forEach { chartData ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                // Color Box
                Box(
                    modifier = Modifier
                        .size(16.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawRoundRect(
                            color = chartData.color,
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                4.dp.toPx(),
                                4.dp.toPx()
                            )
                        )
                    }
                }

                // Label
                Text(
                    text = chartData.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                // Value
                Text(
                    text = chartData.value.toInt().toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = chartData.color
                )
            }
        }
    }
}

/**
 * Data class for chart items
 *
 * @param label Display label for the data
 * @param value Numeric value
 * @param color Color for the data visualization
 */
data class ChartData(
    val label: String,
    val value: Float,
    val color: Color
)
