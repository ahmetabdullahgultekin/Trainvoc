package com.gultekinahmetabdullah.trainvoc.ui.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.ui.animations.AnimationSpecs
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Full-screen tutorial overlay with step-by-step guidance.
 * Displays on top of game content with dimmed background.
 */
@Composable
fun TutorialOverlay(
    state: TutorialState,
    onNextStep: () -> Unit,
    onSkip: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStep = state.currentStep ?: return

    val dimAlpha by animateFloatAsState(
        targetValue = if (state.isActive) 0.85f else 0f,
        animationSpec = AnimationSpecs.mediumTween(),
        label = "dim_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = dimAlpha))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = currentStep.action == TutorialAction.CONTINUE
            ) {
                if (state.isLastStep || currentStep.action == TutorialAction.COMPLETE) {
                    onComplete()
                } else {
                    onNextStep()
                }
            }
    ) {
        AnimatedVisibility(
            visible = state.isActive,
            enter = AnimationSpecs.scaleInFadeIn(),
            exit = AnimationSpecs.scaleOutFadeOut()
        ) {
            TutorialCard(
                step = currentStep,
                stepIndex = state.currentStepIndex,
                totalSteps = state.steps.size,
                progress = state.progress,
                onNext = {
                    if (currentStep.action == TutorialAction.COMPLETE || state.isLastStep) {
                        onComplete()
                    } else {
                        onNextStep()
                    }
                },
                onSkip = onSkip,
                modifier = Modifier
                    .align(getCardAlignment(currentStep.pointerDirection))
                    .padding(Spacing.large)
            )
        }
    }
}

@Composable
private fun TutorialCard(
    step: TutorialStep,
    stepIndex: Int,
    totalSteps: Int,
    progress: Float,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(CornerRadius.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with step counter and skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step ${stepIndex + 1} of $totalSteps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (step.showSkip && step.action != TutorialAction.COMPLETE) {
                    TextButton(onClick = onSkip) {
                        Text("Skip")
                    }
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.small),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Lottie animation
            step.lottieAnimation?.let { animationPath ->
                TutorialLottieAnimation(
                    assetPath = animationPath,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = Spacing.medium)
                )
            }

            // Title
            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            // Description
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Action button
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = when (step.action) {
                        TutorialAction.COMPLETE -> "Got it!"
                        TutorialAction.PRACTICE -> "Try it"
                        else -> "Next"
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = when (step.action) {
                        TutorialAction.COMPLETE -> Icons.Default.Check
                        TutorialAction.PRACTICE -> Icons.Default.PlayArrow
                        else -> Icons.AutoMirrored.Filled.ArrowForward
                    },
                    contentDescription = null
                )
            }

            // Tap hint
            if (step.action == TutorialAction.CONTINUE) {
                Spacer(modifier = Modifier.height(Spacing.small))
                Text(
                    text = "Tap anywhere to continue",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun TutorialLottieAnimation(
    assetPath: String,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(assetPath)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

private fun getCardAlignment(direction: PointerDirection): Alignment {
    return when (direction) {
        PointerDirection.TOP -> Alignment.TopCenter
        PointerDirection.BOTTOM -> Alignment.BottomCenter
        PointerDirection.LEFT -> Alignment.CenterStart
        PointerDirection.RIGHT -> Alignment.CenterEnd
    }
}
