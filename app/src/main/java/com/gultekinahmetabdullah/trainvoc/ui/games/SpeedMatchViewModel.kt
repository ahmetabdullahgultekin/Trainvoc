package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.SpeedMatchGame
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
class SpeedMatchViewModel @Inject constructor(
    private val speedMatchGame: SpeedMatchGame,
    private val gamificationManager: GamificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SpeedMatchUiState>(SpeedMatchUiState.Loading)
    val uiState: StateFlow<SpeedMatchUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = SpeedMatchUiState.Loading
                val gameState = speedMatchGame.startGame(difficulty = difficulty)
                _uiState.value = SpeedMatchUiState.Playing(gameState)
                startTimer()
            } catch (e: Exception) {
                _uiState.value = SpeedMatchUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun selectLeft(index: Int) {
        val currentState = (_uiState.value as? SpeedMatchUiState.Playing)?.gameState ?: return
        val newState = speedMatchGame.selectLeft(currentState, index)
        _uiState.value = SpeedMatchUiState.Playing(newState)
    }

    fun selectRight(index: Int) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? SpeedMatchUiState.Playing)?.gameState ?: return@launch
            val newState = speedMatchGame.selectRight(currentState, index)

            _uiState.value = SpeedMatchUiState.Playing(newState)

            if (newState.isComplete) {
                timerJob?.cancel()
                speedMatchGame.saveGameResult(newState)
                checkAchievements(newState)
                _uiState.value = SpeedMatchUiState.Complete(newState)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentState = (_uiState.value as? SpeedMatchUiState.Playing)?.gameState ?: break

                if (currentState.isPaused) continue

                val newState = speedMatchGame.updateTime(currentState)
                _uiState.value = SpeedMatchUiState.Playing(newState)

                if (newState.isComplete) {
                    speedMatchGame.saveGameResult(newState)
                    checkAchievements(newState)
                    _uiState.value = SpeedMatchUiState.Complete(newState)
                    break
                }
            }
        }
    }

    fun togglePause() {
        val currentState = (_uiState.value as? SpeedMatchUiState.Playing)?.gameState ?: return
        val newState = speedMatchGame.togglePause(currentState)
        _uiState.value = SpeedMatchUiState.Playing(newState)
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: SpeedMatchGame.GameState) {
        // Perfect = all pairs matched with no incorrect attempts
        val isPerfect = gameState.matchedPairs == gameState.totalPairs &&
                gameState.incorrectAttempts == 0
        gamificationManager.recordQuizCompleted(isPerfect)
        gamificationManager.recordActivity()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

sealed class SpeedMatchUiState {
    object Loading : SpeedMatchUiState()
    data class Playing(val gameState: SpeedMatchGame.GameState) : SpeedMatchUiState()
    data class Complete(val gameState: SpeedMatchGame.GameState) : SpeedMatchUiState()
    data class Error(val message: String) : SpeedMatchUiState()
}
