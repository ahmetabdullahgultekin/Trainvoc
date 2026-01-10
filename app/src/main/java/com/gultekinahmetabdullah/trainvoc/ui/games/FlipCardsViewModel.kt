package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.FlipCardsGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlipCardsViewModel @Inject constructor(
    private val flipCardsGame: FlipCardsGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlipCardsUiState>(FlipCardsUiState.Loading)
    val uiState: StateFlow<FlipCardsUiState> = _uiState.asStateFlow()

    fun startGame(gridSize: String = "4x4", difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = FlipCardsUiState.Loading
                val gameState = flipCardsGame.startGame(gridSize = gridSize, difficulty = difficulty)
                _uiState.value = FlipCardsUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = FlipCardsUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun flipCard(cardIndex: Int) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? FlipCardsUiState.Playing)?.gameState ?: return@launch

            if (!currentState.canFlipCard(cardIndex)) return@launch

            val newGameState = flipCardsGame.flipCard(currentState, cardIndex)

            // If two cards are flipped and they don't match, hide them after a delay
            if (newGameState.flippedCards.size == 2) {
                val card1 = newGameState.cards[newGameState.flippedCards[0]]
                val card2 = newGameState.cards[newGameState.flippedCards[1]]

                if (card1.wordId != card2.wordId || card1.type == card2.type) {
                    // Not a match - show cards briefly then hide
                    _uiState.value = FlipCardsUiState.Playing(newGameState)
                    kotlinx.coroutines.delay(1000)
                    val hiddenState = flipCardsGame.hideUnmatchedCards(newGameState)
                    _uiState.value = FlipCardsUiState.Playing(hiddenState)

                    if (hiddenState.isComplete) {
                        finishGame(hiddenState)
                    }
                } else {
                    // Match! Update state immediately
                    _uiState.value = FlipCardsUiState.Playing(newGameState)

                    if (newGameState.isComplete) {
                        finishGame(newGameState)
                    }
                }
            } else {
                _uiState.value = FlipCardsUiState.Playing(newGameState)
            }
        }
    }

    private suspend fun finishGame(gameState: FlipCardsGame.GameState) {
        flipCardsGame.saveGameResult(gameState)
        checkAchievements(gameState)
        _uiState.value = FlipCardsUiState.Complete(gameState)
    }

    private suspend fun checkAchievements(gameState: FlipCardsGame.GameState) {
        // First flip cards game
        unlockAchievement(Achievement.FLIP_CARDS_FIRST)

        // Perfect game (minimum moves)
        if (flipCardsGame.isNewBest(gameState) && gameState.moves == gameState.totalPairs) {
            unlockAchievement(Achievement.FLIP_CARDS_PERFECT)
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
            // Achievement already unlocked or error
        }
    }

    fun playAgain(gridSize: String = "4x4", difficulty: String = "medium") {
        startGame(gridSize, difficulty)
    }
}

sealed class FlipCardsUiState {
    object Loading : FlipCardsUiState()
    data class Playing(val gameState: FlipCardsGame.GameState) : FlipCardsUiState()
    data class Complete(val gameState: FlipCardsGame.GameState) : FlipCardsUiState()
    data class Error(val message: String) : FlipCardsUiState()
}
