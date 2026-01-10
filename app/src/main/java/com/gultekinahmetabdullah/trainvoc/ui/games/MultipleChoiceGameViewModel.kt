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
            val isCorrect = multipleChoiceGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = MultipleChoiceUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(1000)

            // Move to next question
            val newGameState = multipleChoiceGame.answerQuestion(currentState, answer)

            if (newGameState.isComplete) {
                // Save results
                multipleChoiceGame.saveGameResult(newGameState)

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
        // Check if first quiz completed
        val quizCount = gamificationDao.getCompletedQuizzesCount()
        if (quizCount == 1) {
            unlockAchievement(Achievement.QUIZ_10)
        }

        // Check for perfect score
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.totalQuestions >= 5) {
            unlockAchievement(Achievement.QUIZ_PERFECT_FIRST)
        }

        // Check milestone achievements
        when (quizCount) {
            10 -> unlockAchievement(Achievement.QUIZ_10)
            50 -> unlockAchievement(Achievement.QUIZ_50)
            100 -> unlockAchievement(Achievement.QUIZ_100)
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
