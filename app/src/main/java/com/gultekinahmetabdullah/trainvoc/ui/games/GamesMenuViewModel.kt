package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.GamesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesMenuViewModel @Inject constructor(
    private val gamesDao: GamesDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesMenuUiState())
    val uiState: StateFlow<GamesMenuUiState> = _uiState.asStateFlow()

    init {
        loadGameStats()
    }

    private fun loadGameStats() {
        viewModelScope.launch {
            try {
                // Load total games played
                val totalGames = gamesDao.getCompletedGamesCount()

                // Load stats for each game type
                val gameStats = mutableMapOf<GameType, GameStats>()

                GameType.values().forEach { gameType ->
                    val gameTypeName = getGameTypeName(gameType)
                    val gamesPlayed = gamesDao.getCompletedGamesCount(gameType = gameTypeName)
                    val sessions = gamesDao.getGameSessions(gameType = gameTypeName, limit = 100)

                    if (sessions.isNotEmpty()) {
                        val bestAccuracy = sessions.maxOfOrNull { session ->
                            if (session.totalQuestions > 0)
                                (session.correctAnswers.toFloat() / session.totalQuestions) * 100f
                            else 0f
                        } ?: 0f

                        val totalCorrect = sessions.sumOf { it.correctAnswers }
                        val totalQuestions = sessions.sumOf { it.totalQuestions }

                        val bestScore = sessions.maxOfOrNull { session ->
                            calculateScore(session.correctAnswers, session.totalQuestions)
                        } ?: 0

                        gameStats[gameType] = GameStats(
                            gamesPlayed = gamesPlayed,
                            bestScore = bestScore,
                            bestAccuracy = bestAccuracy,
                            totalCorrect = totalCorrect,
                            totalQuestions = totalQuestions
                        )
                    }
                }

                // Find favorite game (most played)
                val favoriteGame = gameStats.maxByOrNull { it.value.gamesPlayed }?.key?.displayName

                // Calculate overall best accuracy
                val allSessions = gamesDao.getAllGameSessions(limit = 1000)
                val bestAccuracy = if (allSessions.isNotEmpty()) {
                    allSessions.maxOfOrNull { session ->
                        if (session.totalQuestions > 0)
                            (session.correctAnswers.toFloat() / session.totalQuestions) * 100f
                        else 0f
                    } ?: 0f
                } else 0f

                _uiState.value = GamesMenuUiState(
                    totalGamesPlayed = totalGames,
                    bestAccuracy = bestAccuracy,
                    favoriteGame = favoriteGame,
                    gameStats = gameStats
                )
            } catch (e: Exception) {
                // Handle error - keep default state
                e.printStackTrace()
            }
        }
    }

    private fun getGameTypeName(gameType: GameType): String {
        return when (gameType) {
            GameType.MULTIPLE_CHOICE -> "multiple_choice"
            GameType.FLIP_CARDS -> "flip_cards"
            GameType.SPEED_MATCH -> "speed_match"
            GameType.FILL_IN_BLANK -> "fill_in_blank"
            GameType.WORD_SCRAMBLE -> "word_scramble"
            GameType.LISTENING_QUIZ -> "listening_quiz"
            GameType.PICTURE_MATCH -> "picture_match"
            GameType.SPELLING_CHALLENGE -> "spelling_challenge"
            GameType.TRANSLATION_RACE -> "translation_race"
            GameType.CONTEXT_CLUES -> "context_clues"
        }
    }

    private fun calculateScore(correctAnswers: Int, totalQuestions: Int): Int {
        val accuracy = if (totalQuestions > 0)
            (correctAnswers.toFloat() / totalQuestions) * 100f
        else 0f
        return (correctAnswers * 10 + accuracy * 2).toInt()
    }

    fun refreshStats() {
        loadGameStats()
    }
}
