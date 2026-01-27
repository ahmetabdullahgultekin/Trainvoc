package com.gultekinahmetabdullah.trainvoc.ui.tutorial

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialState

/**
 * Tutorial Overlay
 *
 * Displays a tutorial dialog when active.
 * Shows step-by-step instructions for each game type.
 *
 * Created: January 21, 2026
 * Updated: January 26, 2026 - Implemented working overlay
 */
@Composable
fun TutorialOverlay(
    state: TutorialState,
    onNextStep: () -> Unit,
    onSkip: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.isActive) return

    val isLastStep = state.currentStep >= state.totalSteps - 1
    val progress = if (state.totalSteps > 0) {
        (state.currentStep + 1).toFloat() / state.totalSteps
    } else 0f

    AlertDialog(
        onDismissRequest = onSkip,
        modifier = modifier,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Progress indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.step_of, state.currentStep + 1, state.totalSteps),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        text = {
            Text(
                text = state.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            val gotItText = stringResource(id = R.string.got_it_button)
            val nextText = stringResource(id = R.string.next)
            Button(
                onClick = if (isLastStep) onComplete else onNextStep
            ) {
                Text(if (isLastStep) gotItText else nextText)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isLastStep) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        dismissButton = {
            if (!isLastStep) {
                TextButton(onClick = onSkip) {
                    Text(stringResource(id = R.string.skip_tutorial))
                }
            }
        }
    )
}

/**
 * Tutorial Help Button
 *
 * Shows a help icon that triggers the tutorial when clicked.
 * Optional pulse animation to draw attention.
 */
@Composable
fun TutorialHelpButton(
    onClick: () -> Unit,
    showPulse: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (showPulse) 1.1f else 1f,
        animationSpec = if (showPulse) {
            infiniteRepeatable(
                animation = tween(durationMillis = 800),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            )
        } else {
            tween(durationMillis = 0)
        },
        label = "pulseAnimation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = Icons.Default.Help,
            contentDescription = stringResource(id = R.string.content_desc_help),
            tint = if (showPulse) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * First Play Tutorial Dialog
 *
 * Shows a simple dialog asking if user wants to see the tutorial
 * on their first play of a game.
 */
@Composable
fun FirstPlayDialog(
    gameTitle: String,
    onShowTutorial: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onSkip,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.first_time_playing),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.tutorial_question, gameTitle),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onShowTutorial) {
                Text(stringResource(id = R.string.show_tutorial))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onSkip) {
                Text(stringResource(id = R.string.skip_tutorial))
            }
        }
    )
}
