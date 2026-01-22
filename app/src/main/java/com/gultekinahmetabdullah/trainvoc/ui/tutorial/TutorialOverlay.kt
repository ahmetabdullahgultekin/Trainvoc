package com.gultekinahmetabdullah.trainvoc.ui.tutorial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialState

/**
 * Tutorial Overlay (Stub Implementation)
 *
 * This is a minimal stub that renders nothing.
 * Tutorial functionality is disabled for now.
 *
 * Created: January 21, 2026
 * Status: Stub for game restoration
 */
@Composable
fun TutorialOverlay(
    state: TutorialState,
    onNextStep: () -> Unit,
    onSkip: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // No-op: Tutorial overlay disabled in stub
    // Games will function normally without tutorial overlay
}

/**
 * Tutorial Help Button (Stub Implementation)
 *
 * Simple help button for game screens.
 * Shows a help icon that triggers the tutorial when clicked.
 */
@Composable
fun TutorialHelpButton(
    onClick: () -> Unit,
    showPulse: Boolean = false,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Help,
            contentDescription = "Help"
        )
    }
}
