package com.gultekinahmetabdullah.trainvoc.ui.tutorial

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

/**
 * Help button for TopAppBar that shows tutorial on demand.
 */
@Composable
fun TutorialHelpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPulse: Boolean = false
) {
    val scale = if (showPulse) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
        pulseScale
    } else {
        1f
    }

    IconButton(
        onClick = onClick,
        modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
            contentDescription = "Show tutorial",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Floating help button for game screens.
 */
@Composable
fun TutorialHelpFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    if (isExpanded) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            icon = {
                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null)
            },
            text = { Text("How to Play") }
        )
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Show tutorial")
        }
    }
}
