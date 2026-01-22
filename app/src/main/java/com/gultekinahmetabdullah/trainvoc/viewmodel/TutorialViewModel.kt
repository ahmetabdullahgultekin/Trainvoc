package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Tutorial ViewModel (Stub Implementation)
 *
 * This is a minimal stub implementation to allow games to compile.
 * Tutorial functionality is disabled - all games will proceed without tutorials.
 *
 * Created: January 21, 2026
 * Status: Stub for game restoration
 */
@HiltViewModel
class TutorialViewModel @Inject constructor() : ViewModel() {

    private val _tutorialState = MutableStateFlow(TutorialState())
    val tutorialState: StateFlow<TutorialState> = _tutorialState.asStateFlow()

    /**
     * Check if this is the first time playing a game
     * Always returns false (no tutorial shown)
     */
    fun isFirstPlay(gameType: GameType): Boolean {
        return false // Tutorials disabled in stub
    }

    /**
     * Start tutorial for a game
     * No-op in stub implementation
     */
    fun startTutorial(gameType: GameType) {
        // No-op: tutorials disabled
    }

    /**
     * Advance to next tutorial step
     * No-op in stub implementation
     */
    fun nextStep() {
        // No-op: tutorials disabled
    }

    /**
     * Skip current tutorial
     * No-op in stub implementation
     */
    fun skipTutorial() {
        // No-op: tutorials disabled
    }

    /**
     * Mark tutorial as complete
     * No-op in stub implementation
     */
    fun completeTutorial() {
        // No-op: tutorials disabled
    }
}

/**
 * Tutorial State
 * Minimal state for stub implementation
 */
data class TutorialState(
    val isActive: Boolean = false,
    val gameType: GameType? = null,
    val currentStep: Int = 0,
    val totalSteps: Int = 0,
    val title: String = "",
    val description: String = "",
    val highlightElement: String? = null
)
