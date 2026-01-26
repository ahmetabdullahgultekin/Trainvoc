package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Tutorial ViewModel
 *
 * Manages tutorial state and first-play detection for game screens.
 * Stores tutorial completion status in SharedPreferences.
 *
 * Created: January 21, 2026
 * Updated: January 26, 2026 - Implemented working tutorial logic
 */
@HiltViewModel
class TutorialViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        TUTORIAL_PREFS_NAME, Context.MODE_PRIVATE
    )

    private val _tutorialState = MutableStateFlow(TutorialState())
    val tutorialState: StateFlow<TutorialState> = _tutorialState.asStateFlow()

    /**
     * Check if this is the first time playing a game
     */
    fun isFirstPlay(gameType: GameType): Boolean {
        val key = FIRST_PLAY_PREFIX + gameType.name
        return !prefs.getBoolean(key, false)
    }

    /**
     * Start tutorial for a game
     * Updates state with game-specific tutorial content
     */
    fun startTutorial(gameType: GameType) {
        val tutorialContent = getTutorialContent(gameType)
        _tutorialState.value = TutorialState(
            isActive = true,
            gameType = gameType,
            currentStep = 0,
            totalSteps = tutorialContent.steps.size,
            title = tutorialContent.title,
            description = tutorialContent.steps.firstOrNull() ?: "",
            steps = tutorialContent.steps
        )
    }

    /**
     * Advance to next tutorial step
     */
    fun nextStep() {
        val current = _tutorialState.value
        if (current.currentStep < current.totalSteps - 1) {
            val nextStep = current.currentStep + 1
            _tutorialState.value = current.copy(
                currentStep = nextStep,
                description = current.steps.getOrElse(nextStep) { "" }
            )
        } else {
            completeTutorial()
        }
    }

    /**
     * Skip current tutorial
     * Marks game as played without completing tutorial
     */
    fun skipTutorial() {
        val gameType = _tutorialState.value.gameType
        if (gameType != null) {
            markAsPlayed(gameType)
        }
        _tutorialState.value = TutorialState()
    }

    /**
     * Mark tutorial as complete
     * Saves completion status to SharedPreferences
     */
    fun completeTutorial() {
        val gameType = _tutorialState.value.gameType
        if (gameType != null) {
            markAsPlayed(gameType)
        }
        _tutorialState.value = TutorialState()
    }

    /**
     * Dismiss tutorial without marking as complete
     * Used when user wants to see tutorial again next time
     */
    fun dismissTutorial() {
        _tutorialState.value = TutorialState()
    }

    /**
     * Reset all tutorials (for testing or user preference)
     */
    fun resetAllTutorials() {
        prefs.edit().clear().apply()
    }

    private fun markAsPlayed(gameType: GameType) {
        val key = FIRST_PLAY_PREFIX + gameType.name
        prefs.edit().putBoolean(key, true).apply()
    }

    /**
     * Get tutorial content for a specific game type
     */
    private fun getTutorialContent(gameType: GameType): TutorialContent {
        return when (gameType) {
            GameType.MULTIPLE_CHOICE -> TutorialContent(
                title = "Multiple Choice Quiz",
                steps = listOf(
                    "Read the word shown at the top of the screen.",
                    "Select the correct meaning from the four options below.",
                    "You'll earn points for correct answers. Faster answers earn more points!",
                    "Complete all questions to see your final score."
                )
            )
            GameType.PICTURE_MATCH -> TutorialContent(
                title = "Picture Match",
                steps = listOf(
                    "Look at the picture shown on screen.",
                    "Choose the word that best matches the picture.",
                    "Some pictures may have multiple related words - pick the best match!",
                    "Train your visual vocabulary association."
                )
            )
            GameType.FILL_IN_THE_BLANK -> TutorialContent(
                title = "Fill in the Blank",
                steps = listOf(
                    "Read the sentence with a missing word.",
                    "Type the correct word in the blank space.",
                    "Pay attention to context clues in the sentence.",
                    "Spelling counts - make sure to type carefully!"
                )
            )
            GameType.CONTEXT_CLUES -> TutorialContent(
                title = "Context Clues",
                steps = listOf(
                    "Read the passage or sentence carefully.",
                    "Use the context to figure out the meaning of the highlighted word.",
                    "Select the definition that best fits the context.",
                    "This helps you learn to understand new words in real situations."
                )
            )
            GameType.SPELLING_CHALLENGE -> TutorialContent(
                title = "Spelling Challenge",
                steps = listOf(
                    "Listen to the word being pronounced.",
                    "Type the correct spelling of the word.",
                    "You can replay the audio if needed.",
                    "Focus on commonly misspelled words to improve your accuracy."
                )
            )
            GameType.WORD_SCRAMBLE -> TutorialContent(
                title = "Word Scramble",
                steps = listOf(
                    "Look at the scrambled letters on screen.",
                    "Rearrange them to form the correct word.",
                    "Use the hint (definition) if you need help.",
                    "Great for building spelling and vocabulary skills!"
                )
            )
            GameType.LISTENING_QUIZ -> TutorialContent(
                title = "Listening Quiz",
                steps = listOf(
                    "Press the play button to hear the word.",
                    "Select the correct word from the options.",
                    "Train your ear to recognize English pronunciation.",
                    "You can replay the audio as many times as you need."
                )
            )
            else -> TutorialContent(
                title = "How to Play",
                steps = listOf(
                    "Follow the on-screen instructions.",
                    "Select or type your answer.",
                    "Complete all questions to finish the game.",
                    "Good luck and have fun learning!"
                )
            )
        }
    }

    companion object {
        private const val TUTORIAL_PREFS_NAME = "tutorial_prefs"
        private const val FIRST_PLAY_PREFIX = "first_play_"
    }
}

/**
 * Tutorial content for a game
 */
private data class TutorialContent(
    val title: String,
    val steps: List<String>
)

/**
 * Tutorial State
 */
data class TutorialState(
    val isActive: Boolean = false,
    val gameType: GameType? = null,
    val currentStep: Int = 0,
    val totalSteps: Int = 0,
    val title: String = "",
    val description: String = "",
    val highlightElement: String? = null,
    val steps: List<String> = emptyList()
)
