package com.gultekinahmetabdullah.trainvoc.ui.utils

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Motion and Animation Utilities
 *
 * Provides utilities to respect user's reduce motion preferences
 * for better accessibility.
 */

/**
 * Check if the system has reduce motion enabled
 */
fun Context.isReduceMotionEnabled(): Boolean {
    return try {
        val scale = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        scale == 0f
    } catch (e: Exception) {
        false
    }
}

/**
 * Composable to check reduce motion setting
 */
@Composable
fun rememberReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        context.isReduceMotionEnabled()
    }
}

/**
 * Get animation spec that respects reduce motion settings
 * Returns snap() if reduce motion is enabled, otherwise returns the provided spec
 */
@Composable
fun <T> respectReduceMotion(
    animationSpec: AnimationSpec<T>
): AnimationSpec<T> {
    val reduceMotionEnabled = rememberReduceMotionEnabled()
    return if (reduceMotionEnabled) {
        snap()
    } else {
        animationSpec
    }
}

/**
 * Tween animation spec that respects reduce motion
 */
@Composable
fun <T> accessibleTween(
    durationMillis: Int = 300,
    delayMillis: Int = 0,
    easing: Easing = FastOutSlowInEasing
): AnimationSpec<T> {
    return respectReduceMotion(
        tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = easing
        )
    )
}

/**
 * Spring animation spec that respects reduce motion
 */
@Composable
fun <T> accessibleSpring(
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessMedium
): AnimationSpec<T> {
    return respectReduceMotion(
        spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        )
    )
}

/**
 * Repeatable animation spec that respects reduce motion
 * Returns snap() if reduce motion is enabled to avoid continuous animations
 */
@Composable
fun <T> accessibleRepeatable(
    iterations: Int = 1,
    animation: DurationBasedAnimationSpec<T> = tween(300),
    repeatMode: RepeatMode = RepeatMode.Restart,
    initialStartOffset: StartOffset = StartOffset(0)
): AnimationSpec<T> {
    val reduceMotionEnabled = rememberReduceMotionEnabled()
    return if (reduceMotionEnabled) {
        snap()
    } else {
        repeatable(
            iterations = iterations,
            animation = animation,
            repeatMode = repeatMode,
            initialStartOffset = initialStartOffset
        )
    }
}

/**
 * Infinite repeatable animation that respects reduce motion
 * Returns a single iteration if reduce motion is enabled
 */
@Composable
fun <T> accessibleInfiniteRepeatable(
    animation: DurationBasedAnimationSpec<T> = tween(1000),
    repeatMode: RepeatMode = RepeatMode.Restart,
    initialStartOffset: StartOffset = StartOffset(0)
): InfiniteRepeatableSpec<T> {
    val reduceMotionEnabled = rememberReduceMotionEnabled()
    return if (reduceMotionEnabled) {
        infiniteRepeatable(
            animation = snap(0), // Effectively disables animation
            repeatMode = repeatMode,
            initialStartOffset = initialStartOffset
        )
    } else {
        infiniteRepeatable(
            animation = animation,
            repeatMode = repeatMode,
            initialStartOffset = initialStartOffset
        )
    }
}
