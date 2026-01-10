package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Speed Match Game Logic
 *
 * Fast-paced matching game where players match words with translations
 * before time runs out. Tests quick recall and translation skills.
 */
@Singleton
class SpeedMatchGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class MatchPair(
        val word: Word,
        val leftOption: String, // English word
        val rightOption: String, // Turkish translation
        val isMatched: Boolean = false
    )

    data class GameState(
        val pairs: List<MatchPair>,
        val leftOptions: List<String>, // English words (shuffled)
        val rightOptions: List<String>, // Turkish translations (shuffled)
        val selectedLeft: Int? = null, // Index of selected left option
        val selectedRight: Int? = null, // Index of selected right option
        val matchedPairs: Int = 0,
        val incorrectAttempts: Int = 0,
        val combo: Int = 0, // Current combo streak
        val maxCombo: Int = 0, // Best combo in this game
        val timeRemaining: Int = 60, // Seconds remaining
        val startTime: Long = System.currentTimeMillis(),
        val isPaused: Boolean = false
    ) {
        val totalPairs: Int
            get() = pairs.size

        val isComplete: Boolean
            get() = matchedPairs >= totalPairs || timeRemaining <= 0

        val accuracy: Float
            get() {
                val totalAttempts = matchedPairs + incorrectAttempts
                return if (totalAttempts == 0) 100f
                       else (matchedPairs.toFloat() / totalAttempts) * 100f
            }

        val score: Int
            get() = (matchedPairs * 10) + (combo * 5) - (incorrectAttempts * 2)

        val isTimedOut: Boolean
            get() = timeRemaining <= 0 && matchedPairs < totalPairs
    }

    /**
     * Start a new Speed Match game
     */
    suspend fun startGame(
        difficulty: String = "medium",
        pairCount: Int = 10,
        timeLimit: Int = 60
    ): GameState {
        val words = when (difficulty) {
            "easy" -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "A2",
                limit = pairCount
            )
            "medium" -> gamesDao.getWordsForGames(
                minLevel = "A2",
                maxLevel = "B1",
                limit = pairCount
            )
            "hard" -> gamesDao.getWordsForGames(
                minLevel = "B2",
                maxLevel = "C2",
                limit = pairCount
            )
            else -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "B1",
                limit = pairCount
            )
        }

        // Handle empty database
        if (words.isEmpty()) {
            return GameState(
                pairs = emptyList(),
                leftOptions = emptyList(),
                rightOptions = emptyList(),
                timeRemaining = 0 // Game ends immediately
            )
        }

        val pairs = words.map { word ->
            MatchPair(
                word = word,
                leftOption = word.word,
                rightOption = word.meaning
            )
        }

        // Shuffle the options separately
        val leftOptions = pairs.map { it.leftOption }.shuffled()
        val rightOptions = pairs.map { it.rightOption }.shuffled()

        return GameState(
            pairs = pairs,
            leftOptions = leftOptions,
            rightOptions = rightOptions,
            timeRemaining = timeLimit
        )
    }

    /**
     * Select left option (English word)
     */
    fun selectLeft(gameState: GameState, index: Int): GameState {
        if (gameState.isPaused || gameState.isComplete) return gameState
        return gameState.copy(selectedLeft = index)
    }

    /**
     * Select right option (Turkish translation) and check for match
     */
    fun selectRight(gameState: GameState, index: Int): GameState {
        if (gameState.isPaused || gameState.isComplete) return gameState

        val leftIndex = gameState.selectedLeft ?: return gameState.copy(selectedRight = index)

        val leftWord = gameState.leftOptions.getOrNull(leftIndex) ?: return gameState
        val rightWord = gameState.rightOptions.getOrNull(index) ?: return gameState

        // Check if they match
        val matchingPair = gameState.pairs.find {
            it.leftOption == leftWord && it.rightOption == rightWord && !it.isMatched
        }

        return if (matchingPair != null) {
            // Match found!
            val newCombo = gameState.combo + 1
            val updatedPairs = gameState.pairs.map {
                if (it == matchingPair) it.copy(isMatched = true) else it
            }

            gameState.copy(
                pairs = updatedPairs,
                matchedPairs = gameState.matchedPairs + 1,
                selectedLeft = null,
                selectedRight = null,
                combo = newCombo,
                maxCombo = maxOf(gameState.maxCombo, newCombo),
                // Bonus time for high combos
                timeRemaining = if (newCombo >= 3) gameState.timeRemaining + 2 else gameState.timeRemaining
            )
        } else {
            // No match
            gameState.copy(
                incorrectAttempts = gameState.incorrectAttempts + 1,
                selectedLeft = null,
                selectedRight = null,
                combo = 0 // Reset combo on mistake
            )
        }
    }

    /**
     * Update time (call this every second)
     */
    fun updateTime(gameState: GameState): GameState {
        if (gameState.isPaused || gameState.isComplete) return gameState

        val newTimeRemaining = maxOf(0, gameState.timeRemaining - 1)
        return gameState.copy(timeRemaining = newTimeRemaining)
    }

    /**
     * Pause/unpause game
     */
    fun togglePause(gameState: GameState): GameState {
        return gameState.copy(isPaused = !gameState.isPaused)
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete && !gameState.isTimedOut) return

        val timeElapsed = 60 - gameState.timeRemaining

        // Save game session
        val session = GameSession(
            gameType = "speed_match",
            difficultyLevel = "medium",
            totalQuestions = gameState.totalPairs,
            correctAnswers = gameState.matchedPairs,
            timeSpentSeconds = timeElapsed,
            completedAt = if (gameState.matchedPairs >= gameState.totalPairs) System.currentTimeMillis() else null
        )
        gamesDao.insertGameSession(session)

        // Save speed match stats
        val stats = SpeedMatchStats(
            pairCount = gameState.totalPairs,
            completionTimeMs = (timeElapsed * 1000).toLong(),
            mistakes = gameState.incorrectAttempts,
            comboMax = gameState.maxCombo,
            score = gameState.score,
            completed = gameState.matchedPairs >= gameState.totalPairs,
            completedAt = System.currentTimeMillis()
        )
        gamesDao.insertSpeedMatchStats(stats)
    }

    /**
     * Check if achieved combo milestone
     */
    fun isComboMilestone(combo: Int): Boolean {
        return combo in listOf(5, 10, 15, 20)
    }

    /**
     * Get combo bonus message
     */
    fun getComboMessage(combo: Int): String {
        return when {
            combo >= 20 -> "🔥 LEGENDARY! x$combo"
            combo >= 15 -> "⚡ AMAZING! x$combo"
            combo >= 10 -> "🌟 GREAT! x$combo"
            combo >= 5 -> "✨ NICE! x$combo"
            combo >= 3 -> "👍 GOOD x$combo"
            else -> ""
        }
    }
}
