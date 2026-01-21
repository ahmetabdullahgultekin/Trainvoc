package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.WordScrambleGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordScrambleViewModel @Inject constructor(
    private val wordScrambleGame: WordScrambleGame
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordScrambleUiState>(WordScrambleUiState.Loading)
    val uiState: StateFlow<WordScrambleUiState> = _uiState.asStateFlow()

    private val _currentInput = MutableStateFlow("")
    val currentInput: StateFlow<String> = _currentInput.asStateFlow()

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = WordScrambleUiState.Loading
                val gameState = wordScrambleGame.startGame(difficulty = difficulty)
                _uiState.value = WordScrambleUiState.Playing(gameState)
                _currentInput.value = ""
            } catch (e: Exception) {
                _uiState.value = WordScrambleUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun updateInput(input: String) {
        _currentInput.value = input
    }

    fun submitAnswer() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? WordScrambleUiState.Playing)?.gameState ?: return@launch
            val answer = _currentInput.value

            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = wordScrambleGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = WordScrambleUiState.ShowingFeedback(
                gameState = currentState,
                answer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback
            kotlinx.coroutines.delay(1000)

            // Move to next question
            val newGameState = wordScrambleGame.answerQuestion(currentState, answer)
            _currentInput.value = ""

            if (newGameState.isComplete) {
                wordScrambleGame.saveGameResult(newGameState)
                _uiState.value = WordScrambleUiState.Complete(newGameState)
            } else {
                _uiState.value = WordScrambleUiState.Playing(newGameState)
            }
        }
    }

    fun useHint() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? WordScrambleUiState.Playing)?.gameState ?: return@launch
            val (newGameState, newInput) = wordScrambleGame.useHint(currentState, _currentInput.value)

            _uiState.value = WordScrambleUiState.Playing(newGameState)
            _currentInput.value = newInput
        }
    }

    fun skipQuestion() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? WordScrambleUiState.Playing)?.gameState ?: return@launch
            val newGameState = wordScrambleGame.skipQuestion(currentState)
            _currentInput.value = ""

            if (newGameState.isComplete) {
                wordScrambleGame.saveGameResult(newGameState)
                _uiState.value = WordScrambleUiState.Complete(newGameState)
            } else {
                _uiState.value = WordScrambleUiState.Playing(newGameState)
            }
        }
    }

    fun playAgain(difficulty: String = "medium") {
        _currentInput.value = ""
        startGame(difficulty)
    }
}

sealed class WordScrambleUiState {
    object Loading : WordScrambleUiState()
    data class Playing(val gameState: WordScrambleGame.GameState) : WordScrambleUiState()
    data class ShowingFeedback(
        val gameState: WordScrambleGame.GameState,
        val answer: String,
        val isCorrect: Boolean
    ) : WordScrambleUiState()
    data class Complete(val gameState: WordScrambleGame.GameState) : WordScrambleUiState()
    data class Error(val message: String) : WordScrambleUiState()
}
