package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Context Clues Game Logic
 *
 * Players read sentences and determine word meanings from context.
 * Develops reading comprehension and contextual understanding skills.
 */
@Singleton
class ContextCluesGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class ContextQuestion(
        val word: Word,
        val context: String, // Sentence with the word
        val wordInContext: String, // The actual word as it appears
        val options: List<String>, // 4 meaning options (Turkish translations)
        val correctAnswer: String,
        val additionalClue: String? = null
    )

    data class GameState(
        val questions: List<ContextQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val cluesUsed: Int = 0,
        val startTime: Long = System.currentTimeMillis(),
        val showingClue: Boolean = false
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
            get() = (correctAnswers * 10) - (cluesUsed * 2)
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

        // Select random words for the game
        val selectedWords = words.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word, words)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a context clues question
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>
    ): ContextQuestion {
        // Create a sample context sentence using the word
        val context = "The word '${word.word}' means: ${word.meaning}"

        // The word as it appears in context
        val wordInContext = word.word

        // Generate distractor options (wrong meanings)
        val distractors = generateDistractors(word, allWords, count = 3)

        val options = (listOf(word.meaning) + distractors).shuffled()

        // Generate additional clue
        val additionalClue = generateAdditionalClue(word)

        return ContextQuestion(
            word = word,
            context = context,
            wordInContext = wordInContext,
            options = options,
            correctAnswer = word.meaning,
            additionalClue = additionalClue
        )
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
     * Generate additional contextual clue
     */
    private fun generateAdditionalClue(word: Word): String {
        return buildString {
            append("Level: ${word.level?.name ?: "Unknown"}")
            append("\nFirst letter: ${word.word.firstOrNull()?.uppercase() ?: ""}")
        }
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
            showingClue = false
        )
    }

    /**
     * Show additional clue
     */
    fun showClue(gameState: GameState): GameState {
        if (gameState.showingClue) return gameState

        return gameState.copy(
            cluesUsed = gameState.cluesUsed + 1,
            showingClue = true
        )
    }

    /**
     * Highlight word in context for better visibility
     */
    fun getHighlightedContext(question: ContextQuestion): String {
        val pattern = "\\b${Regex.escape(question.wordInContext)}\\b".toRegex(RegexOption.IGNORE_CASE)
        return question.context.replace(pattern) { match ->
            "**${match.value}**" // Markdown bold
        }
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
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            showingClue = false
        )
    }
}
