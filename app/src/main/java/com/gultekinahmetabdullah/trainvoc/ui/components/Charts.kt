package com.gultekinahmetabdullah.trainvoc.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Line Chart Component
 *
 * Displays data as a line chart with gradient fill and animated drawing.
 * Perfect for time-series data like words learned over time.
 *
 * @param data List of ChartData items to display
 * @param modifier Modifier for the chart
 * @param height Height of the chart
 * @param lineColor Color of the line
 * @param fillGradient Whether to fill area under the line with gradient
 */
@Composable
fun LineChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillGradient: Boolean = true
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = AnimationDuration.slow)
        )
    }

    if (data.isEmpty()) return

    val maxValue = data.maxOfOrNull { it.value } ?: 1f
    if (maxValue == 0f) return

    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val stepX = canvasWidth / (data.size - 1).coerceAtLeast(1)

        val points = data.mapIndexed { index, chartData ->
            val x = index * stepX
            val y = canvasHeight - (chartData.value / maxValue * canvasHeight * 0.9f)
            Offset(x, y)
        }

        // Draw gradient fill under the line
        if (fillGradient && points.size > 1) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, canvasHeight)
                points.forEachIndexed { index, point ->
                    if (index == 0) {
                        lineTo(point.x, point.y)
                    } else {
                        val prevPoint = points[index - 1]
                        val controlX1 = prevPoint.x + (point.x - prevPoint.x) / 2
                        val controlY1 = prevPoint.y
                        val controlX2 = prevPoint.x + (point.x - prevPoint.x) / 2
                        val controlY2 = point.y
                        cubicTo(controlX1, controlY1, controlX2, controlY2, point.x, point.y)
                    }
                }
                lineTo(canvasWidth, canvasHeight)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.3f),
                        lineColor.copy(alpha = 0.05f)
                    )
                )
            )
        }

        // Draw the line with animation and data points
        if (points.size > 1) {
            val animatedPoints = points.take((points.size * animatedProgress.value).toInt().coerceAtLeast(1))

            for (i in 0 until animatedPoints.size - 1) {
                val start = animatedPoints[i]
                val end = animatedPoints[i + 1]

                val controlX1 = start.x + (end.x - start.x) / 2
                val controlY1 = start.y
                val controlX2 = start.x + (end.x - start.x) / 2
                val controlY2 = end.y

                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(controlX1, controlY1, controlX2, controlY2, end.x, end.y)
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Draw data points
            animatedPoints.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = 6.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = point
                )
            }
        }
    }
}

/**
 * Pie Chart Component
 *
 * Displays data as a pie chart with percentage labels and smooth animations.
 * Each segment rotates in with a spring effect.
 *
 * @param data List of ChartData items to display
 * @param modifier Modifier for the chart
 * @param size Size of the chart in Dp
 * @param showPercentages Whether to show percentage labels
 */
@Composable
fun PieChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    showPercentages: Boolean = true
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
        val chartSize = size.toPx()
        val center = Offset(chartSize / 2, chartSize / 2)
        val radius = chartSize / 2

        data.forEach { chartData ->
            val sweepAngle = (chartData.value / total) * 360f * animatedProgress.value
            val percentage = (chartData.value / total * 100).toInt()

            // Draw pie slice
            drawArc(
                color = chartData.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset.Zero,
                size = Size(chartSize, chartSize)
            )

            // Note: Percentage labels removed to avoid nativeCanvas dependency
            // Use ChartLegend component to display percentages alongside the chart

            startAngle += sweepAngle
        }
    }
}

/**
 * Horizontal Bar Chart Component
 *
 * Displays data as horizontal bars, useful for comparing categories.
 *
 * @param data List of ChartData items to display
 * @param modifier Modifier for the chart
 * @param maxBarHeight Maximum height of each bar
 */
@Composable
fun HorizontalBarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxBarHeight: Dp = 32.dp
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

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        data.forEach { chartData ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Text(
                    text = chartData.label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(60.dp),
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(maxBarHeight)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val barWidth = (chartData.value / maxValue) * animatedProgress.value
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barWidth)
                            .height(maxBarHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(chartData.color)
                    )
                }

                Text(
                    text = chartData.value.toInt().toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = chartData.color,
                    modifier = Modifier.width(40.dp)
                )
            }
        }
    }
}

/**
 * Milestone Timeline Component
 *
 * Displays a vertical timeline of achievements/milestones.
 *
 * @param milestones List of milestone items
 * @param modifier Modifier for the timeline
 */
@Composable
fun MilestoneTimeline(
    milestones: List<MilestoneData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        milestones.forEachIndexed { index, milestone ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                // Timeline indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(milestone.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = milestone.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (index < milestones.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                // Milestone content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = milestone.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (milestone.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

/**
 * Data class for milestone timeline items
 *
 * @param title Milestone title
 * @param date Date string
 * @param description Optional description
 * @param icon Icon to display
 * @param color Color for the timeline indicator
 */
data class MilestoneData(
    val title: String,
    val date: String,
    val description: String = "",
    val icon: ImageVector,
    val color: Color
)
