package com.gultekinahmetabdullah.trainvoc.ui.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animated Components
 *
 * Collection of reusable animated components and modifiers
 * for consistent, delightful micro-interactions throughout the app.
 */

// ============================================================
// Animated Modifiers
// ============================================================

/**
 * Press scale animation
 * Scales down on press, springs back on release
 *
 * @param pressScale Scale factor when pressed (default 0.95)
 * @param onClick Click callback
 */
fun Modifier.pressClickable(
    pressScale: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = AnimationSpecs.SpringMediumStiffness,
        label = "press_scale"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = { onClick() }
            )
        }
}

/**
 * Bounce animation modifier
 * Adds a quick bounce effect when appearing
 */
fun Modifier.bounceIn(): Modifier = composed {
    val scale = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/**
 * Shake animation modifier
 * Horizontal shake motion (useful for errors)
 */
fun Modifier.shake(trigger: Boolean): Modifier = composed {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            // Shake sequence: 0 -> 10 -> -10 -> 5 -> -5 -> 0
            repeat(2) {
                shakeOffset.animateTo(10f, tween(50))
                shakeOffset.animateTo(-10f, tween(50))
            }
            shakeOffset.animateTo(5f, tween(50))
            shakeOffset.animateTo(-5f, tween(50))
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    this.graphicsLayer {
        translationX = shakeOffset.value
    }
}

/**
 * Pulse animation modifier
 * Gentle pulsing scale animation (useful for notifications)
 */
fun Modifier.pulse(enabled: Boolean = true): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    if (enabled) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}

/**
 * Rotation animation modifier
 * Infinite rotation (useful for loading spinners)
 */
fun Modifier.rotate(enabled: Boolean = true): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (enabled) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    if (enabled) {
        this.graphicsLayer {
            rotationZ = angle
        }
    } else {
        this
    }
}

// ============================================================
// Animated Composables
// ============================================================

/**
 * Animated Button
 * Button with press animation and haptic feedback
 */
@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    val haptic = rememberHapticPerformer()

    Button(
        onClick = {
            haptic.click()
            onClick()
        },
        modifier = modifier.pressClickable(onClick = {}),
        enabled = enabled,
        colors = colors,
        content = content
    )
}

/**
 * Card Flip Animation
 * Flips a card to reveal content on the back
 *
 * @param isFlipped State whether card is flipped
 * @param front Front content
 * @param back Back content
 */
@Composable
fun FlippableCard(
    isFlipped: Boolean,
    modifier: Modifier = Modifier,
    front: @Composable BoxScope.() -> Unit,
    back: @Composable BoxScope.() -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.DURATION_MEDIUM,
            easing = AnimationSpecs.EasingStandard
        ),
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        // Front side (visible when rotation = 0)
        if (rotation <= 90f) {
            Box(content = front)
        }
        // Back side (visible when rotation > 90)
        else {
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                },
                content = back
            )
        }
    }
}

/**
 * Expand/Collapse Animation
 * Animates height from 0 to content height
 */
@Composable
fun ExpandableContent(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val expandTransition = updateTransition(
        targetState = isExpanded,
        label = "expand"
    )

    val alpha by expandTransition.animateFloat(
        transitionSpec = { tween(AnimationSpecs.DURATION_MEDIUM) },
        label = "alpha"
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    val scale by expandTransition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "scale"
    ) { expanded ->
        if (expanded) 1f else 0.8f
    }

    if (isExpanded || expandTransition.currentState || expandTransition.targetState) {
        Column(
            modifier = modifier
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                },
            content = content
        )
    }
}

/**
 * Animated Visibility with Scale
 * Shows/hides content with scale animation
 */
@Composable
fun ScaleAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale_visibility"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(AnimationSpecs.DURATION_SHORT),
        label = "alpha_visibility"
    )

    if (visible || scale > 0f) {
        Box(
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
        ) {
            content()
        }
    }
}

/**
 * Staggered List Item Animation
 * Animates list items with staggered delay
 */
@Composable
fun StaggeredListItem(
    index: Int,
    delayMillis: Int = 50,
    content: @Composable () -> Unit
) {
    val offsetY = remember { Animatable(50f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay((index * delayMillis).toLong())
        // Run animations in parallel
        val job1 = launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        val job2 = launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(AnimationSpecs.DURATION_MEDIUM)
            )
        }
        job1.join()
        job2.join()
    }

    Box(
        modifier = Modifier.graphicsLayer {
            translationY = offsetY.value
            this.alpha = alpha.value
        }
    ) {
        content()
    }
}
