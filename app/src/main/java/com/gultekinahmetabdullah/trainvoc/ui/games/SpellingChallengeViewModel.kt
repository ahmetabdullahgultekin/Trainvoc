package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.SpellingChallengeGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpellingChallengeViewModel @Inject constructor(
    private val spellingChallengeGame: SpellingChallengeGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<SpellingChallengeUiState>(SpellingChallengeUiState.Loading)
    val uiState: StateFlow<SpellingChallengeUiState> = _uiState.asStateFlow()

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = SpellingChallengeUiState.Loading
                val gameState = spellingChallengeGame.startGame(difficulty = difficulty)
                _uiState.value = SpellingChallengeUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = SpellingChallengeUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun updateInput(input: String) {
        val currentState = (_uiState.value as? SpellingChallengeUiState.Playing)?.gameState ?: return
        val newState = spellingChallengeGame.updateInput(currentState, input)
        _uiState.value = SpellingChallengeUiState.Playing(newState)
    }

    fun submitAnswer() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? SpellingChallengeUiState.Playing)?.gameState ?: return@launch
            val question = currentState.currentQuestion ?: return@launch

            val answer = currentState.currentInput
            val isCorrect = answer.trim().equals(question.correctSpelling, ignoreCase = true)

            // Show feedback
            _uiState.value = SpellingChallengeUiState.ShowingFeedback(
                gameState = currentState,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(1500)

            // Move to next question
            val newGameState = spellingChallengeGame.submitAnswer(currentState, answer)

            if (newGameState.isComplete) {
                spellingChallengeGame.saveGameResult(newGameState)
                checkAchievements(newGameState)
                _uiState.value = SpellingChallengeUiState.Complete(newGameState)
            } else {
                _uiState.value = SpellingChallengeUiState.Playing(newGameState)
            }
        }
    }

    fun revealLetter() {
        val currentState = (_uiState.value as? SpellingChallengeUiState.Playing)?.gameState ?: return
        val newState = spellingChallengeGame.revealLetter(currentState)
        _uiState.value = SpellingChallengeUiState.Playing(newState)
    }

    fun skipQuestion() {
        val currentState = (_uiState.value as? SpellingChallengeUiState.Playing)?.gameState ?: return
        val newState = spellingChallengeGame.skipQuestion(currentState)

        if (newState.isComplete) {
            viewModelScope.launch {
                spellingChallengeGame.saveGameResult(newState)
                _uiState.value = SpellingChallengeUiState.Complete(newState)
            }
        } else {
            _uiState.value = SpellingChallengeUiState.Playing(newState)
        }
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: SpellingChallengeGame.GameState) {
        // Check for perfect spellings (no hints used)
        if (gameState.perfectSpellings >= 10) {
            unlockAchievement(Achievement.PERFECT_SPELLING)
        }

        // Check for completing all questions
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.totalQuestions >= 10) {
            unlockAchievement(Achievement.SPELLING_MASTER)
        }

        // Check for high accuracy
        if (gameState.accuracy >= 90 && gameState.totalQuestions >= 15) {
            unlockAchievement(Achievement.SPELLING_ACE)
        }
    }

    private suspend fun unlockAchievement(achievement: Achievement) {
        try {
            gamificationDao.insertAchievement(
                com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement(
                    achievementId = achievement.id,
                    progress = achievement.maxProgress,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            // Achievement already unlocked or error - ignore
        }
    }
}

sealed class SpellingChallengeUiState {
    object Loading : SpellingChallengeUiState()
    data class Playing(val gameState: SpellingChallengeGame.GameState) : SpellingChallengeUiState()
    data class ShowingFeedback(
        val gameState: SpellingChallengeGame.GameState,
        val isCorrect: Boolean
    ) : SpellingChallengeUiState()
    data class Complete(val gameState: SpellingChallengeGame.GameState) : SpellingChallengeUiState()
    data class Error(val message: String) : SpellingChallengeUiState()
}
