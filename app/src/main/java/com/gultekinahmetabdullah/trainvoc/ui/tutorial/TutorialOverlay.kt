package com.gultekinahmetabdullah.trainvoc.ui.tutorial

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
