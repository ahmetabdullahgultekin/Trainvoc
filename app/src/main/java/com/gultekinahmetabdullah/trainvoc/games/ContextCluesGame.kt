package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Context Clues Game Logic (Word Detective)
 *
 * Players discover word meanings using progressive hints.
 * Tests vocabulary deduction skills and strengthens word associations.
 *
 * Players see hints about the word and must guess its meaning from options.
 */
@Singleton
class ContextCluesGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class ContextQuestion(
        val word: Word,
        val hints: List<String>, // Progressive hints
        val options: List<String>, // 4 meaning options (Turkish translations)
        val correctAnswer: String,
        val hintsRevealed: Int = 1 // How many hints are currently shown
    ) {
        val currentHints: List<String>
            get() = hints.take(hintsRevealed)
    }

    data class GameState(
        val questions: List<ContextQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val cluesUsed: Int = 0,
        val startTime: Long = System.currentTimeMillis()
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val currentQuestion: ContextQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() = if (currentQuestionIndex == 0) 0f
                    else (correctAnswers.toFloat() / currentQuestionIndex) * 100f

        val score: Int
            get() = maxOf(0, (correctAnswers * 10) - (cluesUsed * 2))

        // Check if more hints can be revealed for current question
        val canRevealMoreHints: Boolean
            get() = currentQuestion?.let { it.hintsRevealed < it.hints.size } ?: false
    }

    /**
     * Start a new Context Clues game
     */
    suspend fun startGame(
        difficulty: String = "medium",
        questionCount: Int = 12
    ): GameState {
        val words = when (difficulty) {
            "easy" -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "A2",
                limit = questionCount * 3
            )
            "medium" -> gamesDao.getWordsForGames(
                minLevel = "A2",
                maxLevel = "B1",
                limit = questionCount * 3
            )
            "hard" -> gamesDao.getWordsForGames(
                minLevel = "B2",
                maxLevel = "C2",
                limit = questionCount * 3
            )
            else -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "B1",
                limit = questionCount * 3
            )
        }

        // Handle empty database
        if (words.isEmpty()) {
            return GameState(questions = emptyList())
        }

        // Select random words for the game
        val selectedWords = words.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word, words)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a context clues question with progressive hints
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>
    ): ContextQuestion {
        // Generate progressive hints (from easy to revealing)
        val hints = generateProgressiveHints(word)

        // Generate distractor options (wrong meanings)
        val distractors = generateDistractors(word, allWords, count = 3)

        val options = (listOf(word.meaning) + distractors).shuffled()

        return ContextQuestion(
            word = word,
            hints = hints,
            options = options,
            correctAnswer = word.meaning,
            hintsRevealed = 1
        )
    }

    /**
     * Generate progressive hints for the word
     */
    private fun generateProgressiveHints(word: Word): List<String> {
        val hints = mutableListOf<String>()

        // Hint 1: Word length and level
        hints.add("${word.word.length} letters • Level: ${word.level?.name ?: "A1"}")

        // Hint 2: First letter
        hints.add("Starts with: ${word.word.first().uppercaseChar()}")

        // Hint 3: First and last letters
        if (word.word.length > 2) {
            hints.add("Starts with '${word.word.first().uppercaseChar()}' and ends with '${word.word.last()}'")
        }

        // Hint 4: Reveal the word itself
        hints.add("The word is: ${word.word}")

        return hints
    }

    /**
     * Generate distractor options (wrong meanings)
     */
    private fun generateDistractors(
        targetWord: Word,
        allWords: List<Word>,
        count: Int
    ): List<String> {
        // Prioritize words from the same level
        val similarWords = allWords
            .filter { it.word != targetWord.word }
            .filter { it.level == targetWord.level }
            .map { it.meaning }
            .distinct()
            .shuffled()
            .take(count)

        // Fill with any other words if needed
        if (similarWords.size < count) {
            val additional = allWords
                .filter { it.word != targetWord.word }
                .filter { !similarWords.contains(it.meaning) }
                .map { it.meaning }
                .distinct()
                .shuffled()
                .take(count - similarWords.size)
            return similarWords + additional
        }

        return similarWords
    }

    /**
     * Check if answer is correct
     */
    fun checkAnswer(question: ContextQuestion, answer: String): Boolean {
        return answer == question.correctAnswer
    }

    /**
     * Process answer and update game state
     */
    fun answerQuestion(gameState: GameState, answer: String): GameState {
        val question = gameState.currentQuestion ?: return gameState
        val isCorrect = checkAnswer(question, answer)

        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            correctAnswers = if (isCorrect) gameState.correctAnswers + 1 else gameState.correctAnswers,
            // Reset hints for next question
            questions = gameState.questions.mapIndexed { index, q ->
                if (index == gameState.currentQuestionIndex + 1) {
                    q.copy(hintsRevealed = 1)
                } else q
            }
        )
    }

    /**
     * Reveal next hint for current question
     */
    fun revealNextHint(gameState: GameState): GameState {
        val question = gameState.currentQuestion ?: return gameState

        // Check if there are more hints to reveal
        if (question.hintsRevealed >= question.hints.size) return gameState

        // Update the current question with more hints revealed
        val updatedQuestions = gameState.questions.mapIndexed { index, q ->
            if (index == gameState.currentQuestionIndex) {
                q.copy(hintsRevealed = q.hintsRevealed + 1)
            } else q
        }

        return gameState.copy(
            questions = updatedQuestions,
            cluesUsed = gameState.cluesUsed + 1
        )
    }

    /**
     * Get word difficulty indicator
     */
    fun getDifficultyIndicator(word: Word): String {
        return when (word.level?.name) {
            "A1", "A2" -> "Beginner"
            "B1", "B2" -> "Intermediate"
            "C1", "C2" -> "Advanced"
            else -> "Beginner"
        }
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val session = GameSession(
            gameType = "context_clues",
            difficultyLevel = "medium",
            totalQuestions = gameState.totalQuestions,
            correctAnswers = gameState.correctAnswers,
            timeSpentSeconds = ((System.currentTimeMillis() - gameState.startTime) / 1000).toInt(),
            completedAt = System.currentTimeMillis()
        )

        gamesDao.insertGameSession(session)
    }

    companion object {
        /**
         * Get comprehension level based on performance
         */
        fun getComprehensionLevel(gameState: GameState): ComprehensionLevel {
            val accuracy = gameState.accuracy
            val clueUsageRate = if (gameState.currentQuestionIndex == 0) 0f
                               else (gameState.cluesUsed.toFloat() / gameState.currentQuestionIndex) * 100f

            return when {
                accuracy >= 90 && clueUsageRate < 20 -> ComprehensionLevel.EXCELLENT
                accuracy >= 80 && clueUsageRate < 40 -> ComprehensionLevel.GOOD
                accuracy >= 70 && clueUsageRate < 60 -> ComprehensionLevel.FAIR
                accuracy >= 60 -> ComprehensionLevel.DEVELOPING
                else -> ComprehensionLevel.NEEDS_IMPROVEMENT
            }
        }
    }

    enum class ComprehensionLevel(val displayName: String, val description: String) {
        EXCELLENT(
            "🌟 Excellent",
            "Outstanding contextual understanding!"
        ),
        GOOD(
            "👍 Good",
            "Strong reading comprehension skills!"
        ),
        FAIR(
            "📚 Fair",
            "Decent understanding, keep practicing!"
        ),
        DEVELOPING(
            "🌱 Developing",
            "Making progress, practice more!"
        ),
        NEEDS_IMPROVEMENT(
            "📖 Needs Improvement",
            "Focus on reading comprehension practice"
        )
    }

    /**
     * Skip current question
     */
    fun skipQuestion(gameState: GameState): GameState {
        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1
        )
    }
}
