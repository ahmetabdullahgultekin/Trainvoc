package com.gultekinahmetabdullah.trainvoc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Rolling Cat Loader Sizes
 * Predefined sizes for consistent usage across the app
 */
object LoaderSize {
    val tiny = 32.dp      // For inline loading indicators
    val small = 48.dp     // For buttons and small spaces
    val medium = 80.dp    // For cards and medium areas
    val large = 120.dp    // For loading screens
    val extraLarge = 160.dp  // For splash/full-screen loading
}

/**
 * Rolling Cat Loader - The Trainvoc branded loading animation
 *
 * A reusable Lottie animation component featuring the rolling cat mascot.
 * Can be used anywhere in the app with different sizes and configurations.
 *
 * @param modifier Modifier for the loader
 * @param size Size of the animation (use LoaderSize presets or custom Dp)
 * @param speed Animation playback speed (1f = normal, 2f = double speed)
 * @param iterations Number of times to play (-1 for infinite)
 */
@Composable
fun RollingCatLoader(
    modifier: Modifier = Modifier,
    size: Dp = LoaderSize.medium,
    speed: Float = 1f,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/anime_rolling_cat.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = true,
        speed = speed
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(size)
    )
}

/**
 * Rolling Cat Loader with Text
 *
 * Shows the rolling cat animation with an optional loading message below.
 * Great for loading states where you want to communicate what's happening.
 *
 * @param message Optional loading message (e.g., "Loading words...", "Please wait...")
 * @param size Size of the animation
 * @param speed Animation playback speed
 * @param modifier Modifier for the container
 */
@Composable
fun RollingCatLoaderWithText(
    message: String? = null,
    size: Dp = LoaderSize.medium,
    speed: Float = 1f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RollingCatLoader(
            size = size,
            speed = speed
        )

        if (!message.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Full Screen Loading Overlay
 *
 * A full-screen loading state with semi-transparent background.
 * Use for blocking operations where user shouldn't interact with the UI.
 *
 * @param isLoading Whether to show the overlay
 * @param message Optional loading message
 * @param backgroundColor Background color (semi-transparent by default)
 */
@Composable
fun FullScreenLoader(
    isLoading: Boolean,
    message: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(CornerRadius.large),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level3),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                RollingCatLoaderWithText(
                    message = message,
                    size = LoaderSize.large,
                    modifier = Modifier.padding(Spacing.xl)
                )
            }
        }
    }
}

/**
 * Loading Card
 *
 * A card component with the rolling cat loader inside.
 * Use for content areas that are loading.
 *
 * @param message Optional loading message
 * @param size Size of the animation
 * @param modifier Modifier for the card
 */
@Composable
fun LoadingCard(
    message: String? = null,
    size: Dp = LoaderSize.medium,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            RollingCatLoaderWithText(
                message = message,
                size = size
            )
        }
    }
}

/**
 * Inline Loader
 *
 * A compact loader for inline usage (buttons, list items, etc.)
 * Shows just the animation without any container.
 *
 * @param size Size of the animation (defaults to small)
 * @param modifier Modifier for the loader
 */
@Composable
fun InlineLoader(
    size: Dp = LoaderSize.small,
    modifier: Modifier = Modifier
) {
    RollingCatLoader(
        size = size,
        speed = 1.2f, // Slightly faster for inline contexts
        modifier = modifier
    )
}

/**
 * Loading Screen Content
 *
 * A centered loading state for use as screen content.
 * Use when the entire screen content is loading.
 *
 * @param message Optional loading message
 * @param size Size of the animation
 */
@Composable
fun LoadingScreenContent(
    message: String? = "Loading...",
    size: Dp = LoaderSize.large
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RollingCatLoaderWithText(
            message = message,
            size = size
        )
    }
}

/**
 * Button Loading State
 *
 * A tiny loader for button loading states.
 * Use inside buttons when an action is in progress.
 */
@Composable
fun ButtonLoader(
    modifier: Modifier = Modifier
) {
    RollingCatLoader(
        size = LoaderSize.tiny,
        speed = 1.5f, // Faster for button context
        modifier = modifier
    )
}

/**
 * Skeleton with Loader
 *
 * Shows a shimmer-like placeholder with the rolling cat in the center.
 * Use for content cards that are loading.
 *
 * @param height Height of the skeleton placeholder
 * @param modifier Modifier for the skeleton
 */
@Composable
fun SkeletonWithLoader(
    height: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(CornerRadius.medium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RollingCatLoader(size = LoaderSize.small)
        }
    }
}
