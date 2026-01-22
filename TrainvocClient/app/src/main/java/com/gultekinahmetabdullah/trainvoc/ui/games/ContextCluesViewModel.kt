package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContextCluesViewModel @Inject constructor(
    private val contextCluesGame: ContextCluesGame,
    private val gamificationManager: GamificationManager
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

    fun revealHint() {
        val currentState = (_uiState.value as? ContextCluesUiState.Playing)?.gameState ?: return
        val newState = contextCluesGame.revealNextHint(currentState)
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
        // Perfect = 90%+ accuracy with at least 10 questions
        val isPerfect = gameState.accuracy >= 90 && gameState.totalQuestions >= 10
        gamificationManager.recordQuizCompleted(isPerfect)
        gamificationManager.recordActivity()
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
