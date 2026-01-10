package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.PictureMatchGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureMatchViewModel @Inject constructor(
    private val pictureMatchGame: PictureMatchGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<PictureMatchUiState>(PictureMatchUiState.Loading)
    val uiState: StateFlow<PictureMatchUiState> = _uiState.asStateFlow()

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = PictureMatchUiState.Loading
                val gameState = pictureMatchGame.startGame(difficulty = difficulty)
                _uiState.value = PictureMatchUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = PictureMatchUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun selectAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? PictureMatchUiState.Playing)?.gameState ?: return@launch
            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = pictureMatchGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = PictureMatchUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(1000)

            // Move to next question
            val newGameState = pictureMatchGame.answerQuestion(currentState, answer)

            if (newGameState.isComplete) {
                pictureMatchGame.saveGameResult(newGameState)
                checkAchievements(newGameState)
                _uiState.value = PictureMatchUiState.Complete(newGameState)
            } else {
                _uiState.value = PictureMatchUiState.Playing(newGameState)
            }
        }
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: PictureMatchGame.GameState) {
        // Check for perfect score
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.totalQuestions >= 5) {
            unlockAchievement(Achievement.PERFECT_10)
        }

        // Award quiz completion achievement
        unlockAchievement(Achievement.QUIZ_10)
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

sealed class PictureMatchUiState {
    object Loading : PictureMatchUiState()
    data class Playing(val gameState: PictureMatchGame.GameState) : PictureMatchUiState()
    data class ShowingFeedback(
        val gameState: PictureMatchGame.GameState,
        val selectedAnswer: String,
        val isCorrect: Boolean
    ) : PictureMatchUiState()
    data class Complete(val gameState: PictureMatchGame.GameState) : PictureMatchUiState()
    data class Error(val message: String) : PictureMatchUiState()
}
