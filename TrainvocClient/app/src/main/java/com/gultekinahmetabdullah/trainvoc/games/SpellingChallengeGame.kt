package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Spelling Challenge Game Logic
 *
 * Players type the correct spelling of words based on audio or translation.
 * Tests spelling accuracy and typing skills.
 */
@Singleton
class SpellingChallengeGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class SpellingQuestion(
        val word: Word,
        val prompt: String, // Turkish translation or audio cue
        val correctSpelling: String,
        val audioUrl: String?,
        val hintsRemaining: Int = 3
    )

    data class GameState(
        val questions: List<SpellingQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val perfectSpellings: Int = 0, // No hints used
        val startTime: Long = System.currentTimeMillis(),
        val currentInput: String = "",
        val revealedLetters: Set<Int> = emptySet() // Positions of revealed letters
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val currentQuestion: SpellingQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() = if (currentQuestionIndex == 0) 0f
                    else (correctAnswers.toFloat() / currentQuestionIndex) * 100f

        val score: Int
            get() = (correctAnswers * 10) + (perfectSpellings * 5)
    }

    /**
     * Start a new Spelling Challenge game
     */
    suspend fun startGame(
        difficulty: String = "medium",
        questionCount: Int = 15
    ): GameState {
        val words = when (difficulty) {
            "easy" -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "A2",
                limit = questionCount
            )
            "medium" -> gamesDao.getWordsForGames(
                minLevel = "A2",
                maxLevel = "B1",
                limit = questionCount
            )
            "hard" -> gamesDao.getWordsForGames(
                minLevel = "B2",
                maxLevel = "C2",
                limit = questionCount
            )
            else -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "B1",
                limit = questionCount
            )
        }

        // Handle empty database
        if (words.isEmpty()) {
            return GameState(questions = emptyList())
        }

        // Filter words suitable for spelling (4-15 letters)
        val suitableWords = words.filter { it.word.length in 4..15 }
        // If no suitable words, use all words
        val wordsToUse = if (suitableWords.isEmpty()) words else suitableWords
        val selectedWords = wordsToUse.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a spelling question
     */
    private fun createQuestion(word: Word): SpellingQuestion {
        val audioUrl = "tts://en/${word.word}" // TTS URL

        return SpellingQuestion(
            word = word,
            prompt = word.meaning,
            correctSpelling = word.word,
            audioUrl = audioUrl
        )
    }

    /**
     * Update current input
     */
    fun updateInput(gameState: GameState, input: String): GameState {
        return gameState.copy(currentInput = input)
    }

    /**
     * Check spelling (real-time feedback)
     */
    fun checkSpelling(question: SpellingQuestion, input: String): SpellingCheckResult {
        if (input.isEmpty()) return SpellingCheckResult.Empty

        val correct = question.correctSpelling.lowercase()
        val typed = input.lowercase()

        return when {
            typed == correct -> SpellingCheckResult.Correct
            correct.startsWith(typed) -> SpellingCheckResult.PartiallyCorrect(
                correctSoFar = input.length,
                total = question.correctSpelling.length
            )
            else -> {
                // Find first mistake position
                val mistakePos = typed.indices.firstOrNull { i ->
                    i >= correct.length || typed[i] != correct[i]
                } ?: 0
                SpellingCheckResult.Incorrect(mistakePosition = mistakePos)
            }
        }
    }

    sealed class SpellingCheckResult {
        object Empty : SpellingCheckResult()
        object Correct : SpellingCheckResult()
        data class PartiallyCorrect(val correctSoFar: Int, val total: Int) : SpellingCheckResult()
        data class Incorrect(val mistakePosition: Int) : SpellingCheckResult()
    }

    /**
     * Submit answer
     */
    fun submitAnswer(gameState: GameState, answer: String): GameState {
        val question = gameState.currentQuestion ?: return gameState
        val isCorrect = answer.trim().equals(question.correctSpelling, ignoreCase = true)
        val isPerfect = isCorrect && gameState.revealedLetters.isEmpty()

        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            correctAnswers = if (isCorrect) gameState.correctAnswers + 1 else gameState.correctAnswers,
            perfectSpellings = if (isPerfect) gameState.perfectSpellings + 1 else gameState.perfectSpellings,
            currentInput = "",
            revealedLetters = emptySet()
        )
    }

    /**
     * Reveal one letter as a hint
     */
    fun revealLetter(gameState: GameState): GameState {
        val question = gameState.currentQuestion ?: return gameState
        val currentInput = gameState.currentInput.lowercase()
        val correctSpelling = question.correctSpelling.lowercase()

        // Find next unrevealed letter
        val nextPosition = (0 until correctSpelling.length).firstOrNull { pos ->
            pos !in gameState.revealedLetters &&
            (pos >= currentInput.length || currentInput[pos] != correctSpelling[pos])
        } ?: return gameState

        val newInput = buildString {
            for (i in correctSpelling.indices) {
                when {
                    i in gameState.revealedLetters || i == nextPosition -> append(correctSpelling[i])
                    i < currentInput.length -> append(currentInput[i])
                    else -> break
                }
            }
        }

        return gameState.copy(
            currentInput = newInput,
            revealedLetters = gameState.revealedLetters + nextPosition
        )
    }

    companion object {
        /**
         * Show word length hint
         */
        fun getWordLengthHint(question: SpellingQuestion): String {
            val length = question.correctSpelling.length
            return "_ ".repeat(length).trim() + " ($length letters)"
        }

        /**
         * Show pattern hint (first and last letters)
         */
        fun getPatternHint(question: SpellingQuestion): String {
            val word = question.correctSpelling
            return if (word.length >= 3) {
                "${word.first()}${"_".repeat(word.length - 2)}${word.last()}"
            } else {
                "_".repeat(word.length)
            }
        }
    }

    /**
     * Skip current question
     */
    fun skipQuestion(gameState: GameState): GameState {
        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            currentInput = "",
            revealedLetters = emptySet()
        )
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val session = GameSession(
            gameType = "spelling_challenge",
            difficultyLevel = "medium",
            totalQuestions = gameState.totalQuestions,
            correctAnswers = gameState.correctAnswers,
            timeSpentSeconds = ((System.currentTimeMillis() - gameState.startTime) / 1000).toInt(),
            completedAt = System.currentTimeMillis()
        )

        gamesDao.insertGameSession(session)
    }

    /**
     * Calculate typing accuracy
     */
    fun calculateTypingAccuracy(typed: String, correct: String): Float {
        if (typed.isEmpty()) return 0f

        val maxLength = maxOf(typed.length, correct.length)
        var matches = 0

        for (i in 0 until minOf(typed.length, correct.length)) {
            if (typed[i].lowercaseChar() == correct[i].lowercaseChar()) {
                matches++
            }
        }

        return (matches.toFloat() / maxLength) * 100f
    }
}
