package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import com.gultekinahmetabdullah.trainvoc.repository.ITutorialPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.ui.games.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialContentProvider
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for managing tutorial state across game screens.
 * Handles first-play detection and tutorial navigation.
 */
@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val tutorialPrefsRepository: ITutorialPreferencesRepository
) : ViewModel() {

    private val _tutorialState = MutableStateFlow(TutorialState.empty())
    val tutorialState: StateFlow<TutorialState> = _tutorialState.asStateFlow()

    /**
     * Check if this is the first time playing a specific game.
     */
    fun isFirstPlay(gameType: GameType): Boolean {
        return tutorialPrefsRepository.isFirstPlay(gameType)
    }

    /**
     * Start tutorial for a game.
     */
    fun startTutorial(gameType: GameType) {
        val steps = TutorialContentProvider.getTutorialSteps(gameType)
        _tutorialState.value = TutorialState(
            gameType = gameType,
            steps = steps,
            currentStepIndex = 0,
            isActive = true
        )
    }

    /**
     * Move to the next tutorial step.
     */
    fun nextStep() {
        val current = _tutorialState.value
        if (current.currentStepIndex < current.steps.lastIndex) {
            _tutorialState.value = current.copy(
                currentStepIndex = current.currentStepIndex + 1
            )
        } else {
            completeTutorial()
        }
    }

    /**
     * Skip the tutorial entirely.
     */
    fun skipTutorial() {
        val current = _tutorialState.value
        tutorialPrefsRepository.markTutorialCompleted(current.gameType)
        _tutorialState.value = current.copy(isActive = false)
    }

    /**
     * Complete the tutorial (called on last step).
     */
    fun completeTutorial() {
        val current = _tutorialState.value
        tutorialPrefsRepository.markTutorialCompleted(current.gameType)
        _tutorialState.value = current.copy(isActive = false)
    }

    /**
     * Reset tutorial status for a game (allows replaying tutorial).
     */
    fun resetTutorial(gameType: GameType) {
        tutorialPrefsRepository.resetTutorialStatus(gameType)
    }

    /**
     * Dismiss tutorial without marking complete (for help button re-views).
     */
    fun dismissTutorial() {
        val current = _tutorialState.value
        _tutorialState.value = current.copy(isActive = false)
    }
}
