package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContextCluesViewModel @Inject constructor(
    private val contextCluesGame: ContextCluesGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContextCluesUiState>(ContextCluesUiState.Loading)
    val uiState: StateFlow<ContextCluesUiState> = _uiState.asStateFlow()

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = ContextCluesUiState.Loading
                val gameState = contextCluesGame.startGame(difficulty = difficulty)
                _uiState.value = ContextCluesUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = ContextCluesUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun showClue() {
        val currentState = (_uiState.value as? ContextCluesUiState.Playing)?.gameState ?: return
        val newState = contextCluesGame.showClue(currentState)
        _uiState.value = ContextCluesUiState.Playing(newState)
    }

    fun selectAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? ContextCluesUiState.Playing)?.gameState ?: return@launch
            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = contextCluesGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = ContextCluesUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(1000)

            // Move to next question
            val newGameState = contextCluesGame.answerQuestion(currentState, answer)

            if (newGameState.isComplete) {
                contextCluesGame.saveGameResult(newGameState)
                checkAchievements(newGameState)
                _uiState.value = ContextCluesUiState.Complete(newGameState)
            } else {
                _uiState.value = ContextCluesUiState.Playing(newGameState)
            }
        }
    }

    fun skipQuestion() {
        val currentState = (_uiState.value as? ContextCluesUiState.Playing)?.gameState ?: return
        val newState = contextCluesGame.skipQuestion(currentState)

        if (newState.isComplete) {
            viewModelScope.launch {
                contextCluesGame.saveGameResult(newState)
                _uiState.value = ContextCluesUiState.Complete(newState)
            }
        } else {
            _uiState.value = ContextCluesUiState.Playing(newState)
        }
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: ContextCluesGame.GameState) {
        val comprehensionLevel = contextCluesGame.getComprehensionLevel(gameState)

        // Check for excellent comprehension
        if (comprehensionLevel == ContextCluesGame.ComprehensionLevel.EXCELLENT) {
            unlockAchievement(Achievement.CONTEXT_MASTER)
        }

        // Check for completing without clues
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.cluesUsed == 0) {
            unlockAchievement(Achievement.NO_CLUES_NEEDED)
        }

        // Check for high accuracy
        if (gameState.accuracy >= 90 && gameState.totalQuestions >= 10) {
            unlockAchievement(Achievement.READING_EXPERT)
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

sealed class ContextCluesUiState {
    object Loading : ContextCluesUiState()
    data class Playing(val gameState: ContextCluesGame.GameState) : ContextCluesUiState()
    data class ShowingFeedback(
        val gameState: ContextCluesGame.GameState,
        val selectedAnswer: String,
        val isCorrect: Boolean
    ) : ContextCluesUiState()
    data class Complete(val gameState: ContextCluesGame.GameState) : ContextCluesUiState()
    data class Error(val message: String) : ContextCluesUiState()
}
