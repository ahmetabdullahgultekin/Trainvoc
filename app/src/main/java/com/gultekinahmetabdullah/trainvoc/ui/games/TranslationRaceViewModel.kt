package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationRaceViewModel @Inject constructor(
    private val translationRaceGame: TranslationRaceGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<TranslationRaceUiState>(TranslationRaceUiState.Loading)
    val uiState: StateFlow<TranslationRaceUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = TranslationRaceUiState.Loading
                val gameState = translationRaceGame.startGame(difficulty = difficulty)
                _uiState.value = TranslationRaceUiState.Playing(gameState)
                startTimer()
            } catch (e: Exception) {
                _uiState.value = TranslationRaceUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun selectAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? TranslationRaceUiState.Playing)?.gameState ?: return@launch
            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = translationRaceGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = TranslationRaceUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(600)

            // Move to next question
            val newGameState = translationRaceGame.answerQuestion(currentState, answer)

            if (newGameState.isComplete) {
                timerJob?.cancel()
                translationRaceGame.saveGameResult(newGameState)
                checkAchievements(newGameState)
                _uiState.value = TranslationRaceUiState.Complete(newGameState)
            } else {
                _uiState.value = TranslationRaceUiState.Playing(newGameState)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentState = (_uiState.value as? TranslationRaceUiState.Playing)?.gameState ?: break

                if (currentState.isPaused) continue

                val newState = translationRaceGame.updateTime(currentState)
                _uiState.value = TranslationRaceUiState.Playing(newState)

                if (newState.isComplete) {
                    translationRaceGame.saveGameResult(newState)
                    checkAchievements(newState)
                    _uiState.value = TranslationRaceUiState.Complete(newState)
                    break
                }
            }
        }
    }

    fun togglePause() {
        val currentState = (_uiState.value as? TranslationRaceUiState.Playing)?.gameState ?: return
        val newState = translationRaceGame.togglePause(currentState)
        _uiState.value = TranslationRaceUiState.Playing(newState)
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: TranslationRaceGame.GameState) {
        // Check for high combo
        if (gameState.maxCombo >= 15) {
            unlockAchievement(Achievement.COMBO_15)
        }

        // Check for speed demon (high answers per minute)
        if (gameState.answersPerMinute >= 40 && gameState.correctAnswers >= 20) {
            unlockAchievement(Achievement.SPEED_DEMON)
        }

        // Check for high accuracy
        if (gameState.accuracy >= 95 && (gameState.correctAnswers + gameState.incorrectAnswers) >= 20) {
            unlockAchievement(Achievement.TRANSLATION_MASTER)
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

sealed class TranslationRaceUiState {
    object Loading : TranslationRaceUiState()
    data class Playing(val gameState: TranslationRaceGame.GameState) : TranslationRaceUiState()
    data class Paused(val gameState: TranslationRaceGame.GameState) : TranslationRaceUiState()
    data class ShowingFeedback(
        val gameState: TranslationRaceGame.GameState,
        val selectedAnswer: String,
        val isCorrect: Boolean
    ) : TranslationRaceUiState()
    data class Complete(val gameState: TranslationRaceGame.GameState) : TranslationRaceUiState()
    data class Error(val message: String) : TranslationRaceUiState()
}
