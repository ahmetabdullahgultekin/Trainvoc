package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Word Scramble Game Logic
 *
 * Presents scrambled letters that the user must unscramble to form the correct word.
 * Great for spelling practice and letter recognition.
 */
@Singleton
class WordScrambleGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class ScrambleQuestion(
        val word: Word,
        val scrambledWord: String,
        val hint: String // Translation as hint
    )

    data class GameState(
        val questions: List<ScrambleQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val startTime: Long = System.currentTimeMillis(),
        val hintsUsed: Int = 0
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val currentQuestion: ScrambleQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() = if (currentQuestionIndex == 0) 0f
                    else (correctAnswers.toFloat() / currentQuestionIndex) * 100f

        val score: Int
            get() = (correctAnswers * 10) - (hintsUsed * 2) // 10 points per correct, -2 per hint
    }

    /**
     * Start a new Word Scramble game
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

        // Filter words that are good for scrambling (4-12 letters)
        val suitableWords = words.filter { it.word.length in 4..12 }
        val selectedWords = suitableWords.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a scramble question from a word
     */
    private fun createQuestion(word: Word): ScrambleQuestion {
        val scrambled = scrambleWord(word.word)

        return ScrambleQuestion(
            word = word,
            scrambledWord = scrambled,
            hint = word.meaning
        )
    }

    /**
     * Scramble a word intelligently
     * Ensures the scrambled version is different from the original
     */
    private fun scrambleWord(word: String): String {
        if (word.length <= 3) return word.reversed()

        var scrambled: String
        var attempts = 0
        do {
            scrambled = word.toList().shuffled().joinToString("")
            attempts++
        } while (scrambled == word && attempts < 10)

        // If still the same after 10 attempts, reverse it
        if (scrambled == word) {
            scrambled = word.reversed()
        }

        return scrambled
    }

    /**
     * Check if the answer is correct
     */
    fun checkAnswer(question: ScrambleQuestion, answer: String): Boolean {
        return answer.trim().equals(question.word.word, ignoreCase = true)
    }

    /**
     * Process answer and update game state
     */
    fun answerQuestion(gameState: GameState, answer: String): GameState {
        val question = gameState.currentQuestion ?: return gameState
        val isCorrect = checkAnswer(question, answer)

        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            correctAnswers = if (isCorrect) gameState.correctAnswers + 1 else gameState.correctAnswers
        )
    }

    /**
     * Use a hint (reveal one letter)
     */
    fun useHint(gameState: GameState, currentInput: String): Pair<GameState, String> {
        val question = gameState.currentQuestion ?: return Pair(gameState, "")
        val correctWord = question.word.word

        // Find next letter to reveal
        val revealedLetters = currentInput.length
        if (revealedLetters >= correctWord.length) {
            return Pair(gameState, currentInput)
        }

        val nextLetter = correctWord[revealedLetters]
        val newInput = currentInput + nextLetter

        return Pair(
            gameState.copy(hintsUsed = gameState.hintsUsed + 1),
            newInput
        )
    }

    /**
     * Get letter hint without revealing
     */
    fun getLetterHint(question: ScrambleQuestion, position: Int): String {
        return if (position < question.word.word.length) {
            "Letter ${position + 1}: ${question.word.word[position].uppercase()}"
        } else {
            "No more letters"
        }
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val session = GameSession(
            gameType = "word_scramble",
            difficultyLevel = "medium",
            totalQuestions = gameState.totalQuestions,
            correctAnswers = gameState.correctAnswers,
            timeSpentSeconds = ((System.currentTimeMillis() - gameState.startTime) / 1000).toInt(),
            completedAt = System.currentTimeMillis()
        )

        gamesDao.insertGameSession(session)
    }

    /**
     * Skip current question (penalty)
     */
    fun skipQuestion(gameState: GameState): GameState {
        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1
        )
    }
}
