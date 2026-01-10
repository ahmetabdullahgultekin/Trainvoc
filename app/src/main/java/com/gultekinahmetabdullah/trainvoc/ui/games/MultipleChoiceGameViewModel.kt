package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.MultipleChoiceGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultipleChoiceGameViewModel @Inject constructor(
    private val multipleChoiceGame: MultipleChoiceGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<MultipleChoiceUiState>(MultipleChoiceUiState.Loading)
    val uiState: StateFlow<MultipleChoiceUiState> = _uiState.asStateFlow()

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = MultipleChoiceUiState.Loading
                val gameState = multipleChoiceGame.startGame(difficulty = difficulty)
                _uiState.value = MultipleChoiceUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = MultipleChoiceUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun selectAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? MultipleChoiceUiState.Playing)?.gameState ?: return@launch

            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = question.isCorrect(answer)

            // Show feedback
            _uiState.value = MultipleChoiceUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(1000)

            // Move to next question using submitAnswer
            val newGameState = multipleChoiceGame.submitAnswer(answer)

            if (newGameState.isComplete) {
                // Check achievements
                checkAchievements(newGameState)

                _uiState.value = MultipleChoiceUiState.Complete(newGameState)
            } else {
                _uiState.value = MultipleChoiceUiState.Playing(newGameState)
            }
        }
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: MultipleChoiceGame.GameState) {
        // Award quiz completion achievement
        unlockAchievement(Achievement.QUIZ_10)

        // Check for perfect score
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.totalQuestions >= 5) {
            unlockAchievement(Achievement.PERFECT_10)
        }
    }

    private suspend fun unlockAchievement(achievement: Achievement) {
        try {
            gamificationDao.insertAchievement(
                com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement(
                    achievementId = achievement.id,
                    progress = achievement.requirement,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            // Achievement already unlocked or error - ignore
        }
    }
}

sealed class MultipleChoiceUiState {
    object Loading : MultipleChoiceUiState()
    data class Playing(val gameState: MultipleChoiceGame.GameState) : MultipleChoiceUiState()
    data class ShowingFeedback(
        val gameState: MultipleChoiceGame.GameState,
        val selectedAnswer: String,
        val isCorrect: Boolean
    ) : MultipleChoiceUiState()
    data class Complete(val gameState: MultipleChoiceGame.GameState) : MultipleChoiceUiState()
    data class Error(val message: String) : MultipleChoiceUiState()
}
