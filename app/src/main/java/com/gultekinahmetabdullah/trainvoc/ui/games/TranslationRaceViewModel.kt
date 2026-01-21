package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationManager
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
    private val gamificationManager: GamificationManager
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

            // Show feedback with correct answer
            _uiState.value = TranslationRaceUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect,
                correctAnswer = question.correctAnswer
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

                // Get game state from either Playing or ShowingFeedback state
                val currentState = when (val state = _uiState.value) {
                    is TranslationRaceUiState.Playing -> state.gameState
                    is TranslationRaceUiState.ShowingFeedback -> state.gameState
                    is TranslationRaceUiState.Complete -> break
                    else -> continue // Skip for Loading/Error states
                }

                if (currentState.isPaused) continue

                val newState = translationRaceGame.updateTime(currentState)

                // Update the state while preserving the current UI state type
                when (val state = _uiState.value) {
                    is TranslationRaceUiState.Playing -> {
                        _uiState.value = TranslationRaceUiState.Playing(newState)
                    }
                    is TranslationRaceUiState.ShowingFeedback -> {
                        _uiState.value = TranslationRaceUiState.ShowingFeedback(
                            gameState = newState,
                            selectedAnswer = state.selectedAnswer,
                            isCorrect = state.isCorrect,
                            correctAnswer = state.correctAnswer
                        )
                    }
                    else -> { /* Do nothing */ }
                }

                if (newState.isComplete) {
                    try {
                        translationRaceGame.saveGameResult(newState)
                        checkAchievements(newState)
                    } catch (e: Exception) {
                        // Ignore save errors
                    }
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
        // Perfect = 95%+ accuracy with at least 10 answers
        val totalAnswers = gameState.correctAnswers + gameState.incorrectAnswers
        val isPerfect = gameState.accuracy >= 95 && totalAnswers >= 10
        gamificationManager.recordQuizCompleted(isPerfect)
        gamificationManager.recordActivity()
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
        val isCorrect: Boolean,
        val correctAnswer: String
    ) : TranslationRaceUiState()
    data class Complete(val gameState: TranslationRaceGame.GameState) : TranslationRaceUiState()
    data class Error(val message: String) : TranslationRaceUiState()
}
