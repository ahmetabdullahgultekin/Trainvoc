package com.gultekinahmetabdullah.trainvoc.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Success and Error Animations
 *
 * Delightful animations to celebrate success or indicate errors.
 * Includes checkmark, error cross, and confetti animations.
 */

// ============================================================
// Checkmark Animation
// ============================================================

/**
 * Animated Checkmark
 * Draws an animated checkmark with circle background
 *
 * @param isVisible Whether to show and animate the checkmark
 * @param color Checkmark color
 */
@Composable
fun AnimatedCheckmark(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmark_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(200),
        label = "checkmark_alpha"
    )

    if (isVisible || scale > 0f) {
        Box(
            modifier = modifier
                .size(80.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = color,
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale)
            )
        }
    }
}

/**
 * Animated Error Cross
 * Draws an animated X with circle background
 */
@Composable
fun AnimatedErrorCross(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "error_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isVisible) 0f else -90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "error_rotation"
    )

    if (isVisible || scale > 0f) {
        Box(
            modifier = modifier
                .size(80.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                tint = color,
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale)
            )
        }
    }
}

// ============================================================
// Confetti Animation
// ============================================================

/**
 * Confetti particle data class
 */
private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val velocityY: Float,
    val velocityX: Float,
    val rotation: Float
)

/**
 * Confetti Animation
 * Celebration animation with falling confetti particles
 *
 * @param trigger Triggers the confetti burst when changed
 */
@Composable
fun ConfettiAnimation(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    val confettiColors = listOf(
        Color(0xFFFFD600), // Gold
        Color(0xFFFF6F00), // Orange
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
    )

    var particles by remember { mutableStateOf<List<ConfettiParticle>>(emptyList()) }
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_progress"
    )

    LaunchedEffect(trigger) {
        if (trigger) {
            // Generate confetti particles
            particles = List(particleCount) {
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = -0.1f,
                    color = confettiColors.random(),
                    size = Random.nextFloat() * 10f + 5f,
                    velocityY = Random.nextFloat() * 0.5f + 0.3f,
                    velocityX = Random.nextFloat() * 0.4f - 0.2f,
                    rotation = Random.nextFloat() * 360f
                )
            }

            // Clear particles after animation completes
            delay(3000)
            particles = emptyList()
        }
    }

    if (particles.isNotEmpty()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { particle ->
                val currentY = particle.y + particle.velocityY * animatedProgress
                val currentX = particle.x + particle.velocityX * animatedProgress * 0.5f

                if (currentY <= 1f) {
                    drawCircle(
                        color = particle.color,
                        radius = particle.size,
                        center = Offset(
                            x = size.width * currentX,
                            y = size.height * currentY
                        )
                    )
                }
            }
        }
    }
}

/**
 * Success Celebration
 * Complete success animation with checkmark and confetti
 */
@Composable
fun SuccessCelebration(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    showConfetti: Boolean = true,
    onComplete: (() -> Unit)? = null
) {
    var showCheckmark by remember { mutableStateOf(false) }
    var triggerConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Show checkmark first
            showCheckmark = true
            delay(200)

            // Then trigger confetti
            if (showConfetti) {
                triggerConfetti = true
            }

            // Call onComplete after animation
            delay(1000)
            onComplete?.invoke()
        } else {
            showCheckmark = false
            triggerConfetti = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Confetti in background
        if (showConfetti) {
            ConfettiAnimation(trigger = triggerConfetti)
        }

        // Checkmark in center
        AnimatedCheckmark(
            isVisible = showCheckmark,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Error Indication
 * Error animation with shake and cross
 */
@Composable
fun ErrorIndication(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onComplete: (() -> Unit)? = null
) {
    var showError by remember { mutableStateOf(false) }
    var shakeError by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            showError = true
            delay(100)
            shakeError = true
            delay(500)
            shakeError = false
            delay(500)
            onComplete?.invoke()
        } else {
            showError = false
            shakeError = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .shake(shakeError),
        contentAlignment = Alignment.Center
    ) {
        AnimatedErrorCross(
            isVisible = showError,
            color = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Progress Checkmark
 * Simple checkmark that appears when a task is complete
 */
@Composable
fun ProgressCheckmark(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "progress_checkmark"
    )

    if (isCompleted || scale > 0f) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Completed",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .size(24.dp)
                .scale(scale)
        )
    }
}
