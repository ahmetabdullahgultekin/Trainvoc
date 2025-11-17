package com.gultekinahmetabdullah.trainvoc.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Shimmer Effect
 *
 * Beautiful shimmer/skeleton loading animation that indicates content is loading.
 * Uses a gradient that moves across the screen to create a shimmer effect.
 */

/**
 * Shimmer modifier
 * Applies shimmer effect to any composable
 *
 * @param isVisible Whether the shimmer is visible (true = loading)
 * @param shimmerColors Colors for the shimmer gradient
 * @param durationMillis Duration of one shimmer cycle
 */
fun Modifier.shimmerEffect(
    isVisible: Boolean = true,
    shimmerColors: List<Color>? = null,
    durationMillis: Int = 1300
): Modifier = composed {
    if (!isVisible) return@composed this

    val colors = shimmerColors ?: listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(translateAnimation - 1000f, translateAnimation - 1000f),
            end = Offset(translateAnimation, translateAnimation)
        )
    )
}

/**
 * Shimmer Box
 * A box with shimmer effect, useful for skeleton screens
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CornerRadius.small)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerEffect()
    )
}

/**
 * Skeleton Loading Components
 * Pre-built shimmer components for common UI patterns
 */

/**
 * Word Card Skeleton
 * Mimics the word card layout while loading
 */
@Composable
fun WordCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.small),
        shape = RoundedCornerShape(CornerRadius.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            // Title shimmer
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(24.dp)
            )

            // Subtitle shimmer
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            // Content shimmers
            repeat(2) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                )
            }
        }
    }
}

/**
 * List Item Skeleton
 * Generic list item shimmer
 */
@Composable
fun ListItemSkeleton(
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        if (showAvatar) {
            // Avatar circle
            ShimmerBox(
                modifier = Modifier.size(40.dp),
                shape = CircleShape
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            // Title
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(18.dp)
            )

            // Subtitle
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Stats Card Skeleton
 * Skeleton for statistics cards
 */
@Composable
fun StatsCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.small),
        shape = RoundedCornerShape(CornerRadius.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            // Title
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Stat value 1
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )

                // Stat value 2
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )
            }

            // Chart placeholder
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(CornerRadius.small)
            )
        }
    }
}

/**
 * Text Skeleton
 * Simple text placeholder
 */
@Composable
fun TextSkeleton(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 16.dp
) {
    ShimmerBox(
        modifier = modifier
            .width(width)
            .height(height)
    )
}

/**
 * Loading Screen
 * Full screen shimmer loading
 */
@Composable
fun ShimmerLoadingScreen(
    itemCount: Int = 5
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        repeat(itemCount) {
            WordCardSkeleton()
        }
    }
}
