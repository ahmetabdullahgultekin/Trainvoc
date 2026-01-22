package com.gultekinahmetabdullah.trainvoc.ui.animations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/**
 * AnimationSpecs
 *
 * Centralized animation specifications following Material Design motion guidelines.
 * Provides consistent timing and easing across the entire app.
 */
object AnimationSpecs {

    // ============================================================
    // Duration Constants (in milliseconds)
    // ============================================================

    /** Very fast animations (100ms) - Micro-interactions */
    const val DURATION_VERY_SHORT = 100

    /** Short animations (200ms) - Quick transitions */
    const val DURATION_SHORT = 200

    /** Medium animations (300ms) - Standard transitions */
    const val DURATION_MEDIUM = 300

    /** Long animations (400ms) - Emphasized transitions */
    const val DURATION_LONG = 400

    /** Extra long animations (500ms) - Complex transitions */
    const val DURATION_EXTRA_LONG = 500

    // ============================================================
    // Easing Functions
    // ============================================================

    /** Standard easing - Default for most animations */
    val EasingStandard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    /** Emphasized easing - For important state changes */
    val EasingEmphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    /** Decelerate easing - For entering elements */
    val EasingDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

    /** Accelerate easing - For exiting elements */
    val EasingAccelerate = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

    // ============================================================
    // Spring Specifications
    // ============================================================

    /** Low stiffness spring - Smooth, slow bounce */
    val SpringLowStiffness = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Medium stiffness spring - Standard bounce */
    val SpringMediumStiffness = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** High stiffness spring - Quick, snappy bounce */
    val SpringHighStiffness = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /** No bounce spring - Smooth without bounce */
    val SpringNoBounce = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // ============================================================
    // Tween Specifications
    // ============================================================

    /** Fast tween - 200ms with standard easing */
    fun <T> fastTween() = tween<T>(
        durationMillis = DURATION_SHORT,
        easing = EasingStandard
    )

    /** Medium tween - 300ms with standard easing */
    fun <T> mediumTween() = tween<T>(
        durationMillis = DURATION_MEDIUM,
        easing = EasingStandard
    )

    /** Slow tween - 400ms with standard easing */
    fun <T> slowTween() = tween<T>(
        durationMillis = DURATION_LONG,
        easing = EasingStandard
    )

    /** Emphasized tween - 300ms with emphasized easing */
    fun <T> emphasizedTween() = tween<T>(
        durationMillis = DURATION_MEDIUM,
        easing = EasingEmphasized
    )

    // ============================================================
    // Screen Transition Animations
    // ============================================================

    /**
     * Slide in from right transition
     * Used for forward navigation
     */
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = mediumTween()
        ) + fadeIn(animationSpec = mediumTween())
    }

    /**
     * Slide out to left transition
     * Used for forward navigation exit
     */
    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = mediumTween()
        ) + fadeOut(animationSpec = mediumTween())
    }

    /**
     * Slide in from left transition
     * Used for backward navigation
     */
    fun slideInFromLeft(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = mediumTween()
        ) + fadeIn(animationSpec = mediumTween())
    }

    /**
     * Slide out to right transition
     * Used for backward navigation exit
     */
    fun slideOutToRight(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = mediumTween()
        ) + fadeOut(animationSpec = mediumTween())
    }

    /**
     * Fade in transition
     * Subtle entrance
     */
    fun fadeIn(): EnterTransition {
        return fadeIn(animationSpec = mediumTween())
    }

    /**
     * Fade out transition
     * Subtle exit
     */
    fun fadeOut(): ExitTransition {
        return fadeOut(animationSpec = mediumTween())
    }

    /**
     * Scale and fade in transition
     * Emphasized entrance
     */
    fun scaleInFadeIn(): EnterTransition {
        return scaleIn(
            initialScale = 0.8f,
            animationSpec = emphasizedTween()
        ) + fadeIn(animationSpec = emphasizedTween())
    }

    /**
     * Scale and fade out transition
     * Emphasized exit
     */
    fun scaleOutFadeOut(): ExitTransition {
        return scaleOut(
            targetScale = 0.8f,
            animationSpec = emphasizedTween()
        ) + fadeOut(animationSpec = emphasizedTween())
    }

    // ============================================================
    // Repeat Specifications
    // ============================================================

    /**
     * Infinite repeatable animation
     * Used for loading indicators
     */
    fun <T> infiniteRepeatable(
        animation: DurationBasedAnimationSpec<T>,
        repeatMode: RepeatMode = RepeatMode.Restart
    ): InfiniteRepeatableSpec<T> {
        return infiniteRepeatable(
            animation = animation,
            repeatMode = repeatMode
        )
    }

    /**
     * Pulse animation - Infinite scale animation
     */
    fun pulseAnimation(): InfiniteRepeatableSpec<Float> {
        return infiniteRepeatable(
            animation = tween(1000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        )
    }

    /**
     * Rotation animation - Infinite rotation
     */
    fun rotationAnimation(): InfiniteRepeatableSpec<Float> {
        return infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    }
}
