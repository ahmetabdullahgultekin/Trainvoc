package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.FillInTheBlankGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FillInTheBlankViewModel @Inject constructor(
    private val fillInTheBlankGame: FillInTheBlankGame
) : ViewModel() {

    private val _uiState = MutableStateFlow<FillInTheBlankUiState>(FillInTheBlankUiState.Loading)
    val uiState: StateFlow<FillInTheBlankUiState> = _uiState.asStateFlow()

    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = FillInTheBlankUiState.Loading
                val gameState = fillInTheBlankGame.startGame(difficulty = difficulty)
                _uiState.value = FillInTheBlankUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = FillInTheBlankUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun selectAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? FillInTheBlankUiState.Playing)?.gameState ?: return@launch

            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = fillInTheBlankGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = FillInTheBlankUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback
            kotlinx.coroutines.delay(1000)

            // Move to next question
            val newGameState = fillInTheBlankGame.answerQuestion(currentState, answer)
            _showHint.value = false

            if (newGameState.isComplete) {
                fillInTheBlankGame.saveGameResult(newGameState)
                _uiState.value = FillInTheBlankUiState.Complete(newGameState)
            } else {
                _uiState.value = FillInTheBlankUiState.Playing(newGameState)
            }
        }
    }

    fun toggleHint() {
        _showHint.value = !_showHint.value
    }

    fun playAgain(difficulty: String = "medium") {
        _showHint.value = false
        startGame(difficulty)
    }
}

sealed class FillInTheBlankUiState {
    object Loading : FillInTheBlankUiState()
    data class Playing(val gameState: FillInTheBlankGame.GameState) : FillInTheBlankUiState()
    data class ShowingFeedback(
        val gameState: FillInTheBlankGame.GameState,
        val selectedAnswer: String,
        val isCorrect: Boolean
    ) : FillInTheBlankUiState()
    data class Complete(val gameState: FillInTheBlankGame.GameState) : FillInTheBlankUiState()
    data class Error(val message: String) : FillInTheBlankUiState()
}
