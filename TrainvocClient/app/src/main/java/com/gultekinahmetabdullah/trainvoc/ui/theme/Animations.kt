package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Animation Specifications for UI/UX Improvement Plan
 *
 * Material Motion easing functions and animation specifications
 * following Material Design 3 guidelines.
 */

/**
 * Easing Functions (from plan)
 * Standard Material Motion easing curves
 */
object AppEasing {
    /** Standard easing - default for most transitions (enter & exit) */
    val standard: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    /** Decelerate easing - incoming elements (enter) */
    val decelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

    /** Accelerate easing - outgoing elements (exit) */
    val accelerate: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

    /** Emphasized easing - expressive, attention-grabbing animations */
    val emphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    /** Legacy easing for backward compatibility */
    val fastOutSlowIn: Easing = FastOutSlowInEasing
    val linear: Easing = LinearEasing
}

/**
 * Animation Durations (from plan)
 * Consistent timing across all animations
 */
object AppAnimationDuration {
    // Material Motion durations
    const val instant = 100      // State changes, toggles
    const val quick = 200        // Button presses, small transitions
    const val medium = 300       // Screen transitions, card animations (default)
    const val slow = 500         // Large movements, page transitions
    const val gentle = 700       // Celebration animations
    const val countUp = 1000     // Number count-up animations

    // Specific animation durations
    const val fadeIn = medium
    const val fadeOut = quick
    const val slideIn = medium
    const val slideOut = medium
    const val scaleIn = medium
    const val scaleOut = quick
    const val shake = 400        // Error shake animation
    const val pulse = 1000       // Pulsing animations
}

/**
 * Spring Configurations
 * For natural, physics-based animations
 */
object AppSpring {
    /** Default spring - balanced bounce */
    val default = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Bouncy spring - more playful */
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Stiff spring - quick, minimal bounce */
    val stiff = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    /** Gentle spring - smooth and slow */
    val gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * Common Animation Specs
 * Pre-configured animation specifications for consistent use
 */
object AppAnimationSpec {
    /** Fade in animation */
    val fadeIn = tween<Float>(
        durationMillis = AppAnimationDuration.fadeIn,
        easing = AppEasing.standard
    )

    /** Fade out animation */
    val fadeOut = tween<Float>(
        durationMillis = AppAnimationDuration.fadeOut,
        easing = AppEasing.standard
    )

    /** Scale in animation (for dialogs, modals) */
    val scaleIn = tween<Float>(
        durationMillis = AppAnimationDuration.scaleIn,
        easing = AppEasing.emphasized
    )

    /** Scale out animation */
    val scaleOut = tween<Float>(
        durationMillis = AppAnimationDuration.scaleOut,
        easing = AppEasing.accelerate
    )

    /** Slide animation */
    val slide = tween<Float>(
        durationMillis = AppAnimationDuration.medium,
        easing = AppEasing.standard
    )

    /** Quick button press animation */
    val buttonPress = tween<Float>(
        durationMillis = AppAnimationDuration.quick,
        easing = AppEasing.standard
    )

    /** Count up animation (for scores, XP) */
    val countUp = tween<Float>(
        durationMillis = AppAnimationDuration.countUp,
        easing = AppEasing.decelerate
    )

    /** Celebration animation */
    val celebration = tween<Float>(
        durationMillis = AppAnimationDuration.gentle,
        easing = AppEasing.emphasized
    )
}

/**
 * Stagger Delay (for sequential animations)
 */
object StaggerDelay {
    const val extraShort = 50    // Very quick stagger
    const val short = 100        // Quick stagger (card entrances)
    const val medium = 150       // Medium stagger
    const val long = 200         // Slower stagger
}

/**
 * Helper Composables for common animations
 */

/**
 * Infinite pulsing animation
 * Used for loading indicators, attention-grabbing elements
 */
@Composable
fun rememberPulseAnimation(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = AppAnimationDuration.pulse
): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
}

/**
 * Rotation animation for loading spinners
 */
@Composable
fun rememberRotationAnimation(
    durationMillis: Int = 1000
): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationDegrees"
    )
}

/**
 * Animation delays for specific UI elements (from plan)
 */
object AnimationDelay {
    const val statsCardStagger = 100     // Delay between stat cards appearing
    const val wordCardStagger = 50       // Delay between word cards in dictionary
    const val gameCardStagger = 80       // Delay between game cards
    const val achievementBadgeStagger = 120  // Delay between achievement badges
}

/**
 * Usage Examples:
 *
 * ```kotlin
 * // Fade in animation
 * AnimatedVisibility(
 *     visible = isVisible,
 *     enter = fadeIn(animationSpec = AppAnimationSpec.fadeIn),
 *     exit = fadeOut(animationSpec = AppAnimationSpec.fadeOut)
 * ) { Content() }
 *
 * // Scale animation
 * val scale by animateFloatAsState(
 *     targetValue = if (selected) 1.0f else 0.95f,
 *     animationSpec = AppAnimationSpec.buttonPress
 * )
 *
 * // Count up animation
 * val animatedValue by animateIntAsState(
 *     targetValue = finalScore,
 *     animationSpec = AppAnimationSpec.countUp
 * )
 * ```
 */
