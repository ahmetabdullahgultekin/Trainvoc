package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Translation Race Game Logic
 *
 * Fast-paced translation game where players race against time to translate
 * as many words as possible. Tests quick recall and translation skills.
 */
@Singleton
class TranslationRaceGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class TranslationQuestion(
        val word: Word,
        val questionText: String, // Word to translate
        val direction: TranslationDirection,
        val options: List<String>, // 4 options
        val correctAnswer: String
    )

    enum class TranslationDirection {
        ENGLISH_TO_TURKISH,
        TURKISH_TO_ENGLISH,
        MIXED // Random direction for each question
    }

    data class GameState(
        val questions: List<TranslationQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val incorrectAnswers: Int = 0,
        val timeRemaining: Int = 90, // 90 seconds default
        val startTime: Long = System.currentTimeMillis(),
        val combo: Int = 0,
        val maxCombo: Int = 0,
        val isPaused: Boolean = false,
        val bonusTimeEarned: Int = 0
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size || timeRemaining <= 0

        val currentQuestion: TranslationQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() {
                val total = correctAnswers + incorrectAnswers
                return if (total == 0) 100f else (correctAnswers.toFloat() / total) * 100f
            }

        val score: Int
            get() = (correctAnswers * 10) + (combo * 3) + (bonusTimeEarned * 2)

        val answersPerMinute: Float
            get() {
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                val totalAnswers = correctAnswers + incorrectAnswers
                return if (elapsedSeconds == 0L) 0f
                       else (totalAnswers.toFloat() / elapsedSeconds) * 60f
            }
    }

    /**
     * Start a new Translation Race game
     */
    suspend fun startGame(
        difficulty: String = "medium",
        questionCount: Int = 30,
        direction: TranslationDirection = TranslationDirection.MIXED,
        timeLimit: Int = 90
    ): GameState {
        val words = when (difficulty) {
            "easy" -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "A2",
                limit = questionCount * 2
            )
            "medium" -> gamesDao.getWordsForGames(
                minLevel = "A2",
                maxLevel = "B1",
                limit = questionCount * 2
            )
            "hard" -> gamesDao.getWordsForGames(
                minLevel = "B2",
                maxLevel = "C2",
                limit = questionCount * 2
            )
            else -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "B1",
                limit = questionCount * 2
            )
        }

        val selectedWords = words.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word, words, direction)
        }

        return GameState(
            questions = questions,
            timeRemaining = timeLimit
        )
    }

    /**
     * Create a translation question
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>,
        direction: TranslationDirection
    ): TranslationQuestion {
        // Determine translation direction
        val actualDirection = when (direction) {
            TranslationDirection.MIXED -> {
                if (Math.random() < 0.5) TranslationDirection.ENGLISH_TO_TURKISH
                else TranslationDirection.TURKISH_TO_ENGLISH
            }
            else -> direction
        }

        val (questionText, correctAnswer, distractorPool) = when (actualDirection) {
            TranslationDirection.ENGLISH_TO_TURKISH -> {
                Triple(
                    word.word,
                    word.meaning,
                    allWords.filter { it.word != word.word }.map { it.meaning }
                )
            }
            TranslationDirection.TURKISH_TO_ENGLISH -> {
                Triple(
                    word.meaning,
                    word.word,
                    allWords.filter { it.word != word.word }.map { it.word }
                )
            }
            else -> Triple(word.word, word.meaning, emptyList())
        }

        // Generate distractors
        val distractors = distractorPool
            .distinct()
            .shuffled()
            .take(3)

        val options = (listOf(correctAnswer) + distractors).shuffled()

        return TranslationQuestion(
            word = word,
            questionText = questionText,
            direction = actualDirection,
            options = options,
            correctAnswer = correctAnswer
        )
    }

    /**
     * Check if answer is correct
     */
    fun checkAnswer(question: TranslationQuestion, answer: String): Boolean {
        return answer == question.correctAnswer
    }

    /**
     * Process answer and update game state
     */
    fun answerQuestion(gameState: GameState, answer: String): GameState {
        val question = gameState.currentQuestion ?: return gameState
        val isCorrect = checkAnswer(question, answer)

        // Update combo
        val newCombo = if (isCorrect) gameState.combo + 1 else 0
        val newMaxCombo = maxOf(gameState.maxCombo, newCombo)

        // Bonus time for combo milestones
        val bonusTime = when {
            isCorrect && newCombo == 5 -> 3
            isCorrect && newCombo == 10 -> 5
            isCorrect && newCombo % 15 == 0 -> 7
            else -> 0
        }

        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            correctAnswers = if (isCorrect) gameState.correctAnswers + 1 else gameState.correctAnswers,
            incorrectAnswers = if (!isCorrect) gameState.incorrectAnswers + 1 else gameState.incorrectAnswers,
            combo = newCombo,
            maxCombo = newMaxCombo,
            timeRemaining = gameState.timeRemaining + bonusTime,
            bonusTimeEarned = gameState.bonusTimeEarned + bonusTime
        )
    }

    /**
     * Update time (call every second)
     */
    fun updateTime(gameState: GameState): GameState {
        if (gameState.isPaused || gameState.isComplete) return gameState

        val newTime = maxOf(0, gameState.timeRemaining - 1)
        return gameState.copy(timeRemaining = newTime)
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
        val totalAnswered = gameState.correctAnswers + gameState.incorrectAnswers
        if (totalAnswered == 0) return

        val timeElapsed = (System.currentTimeMillis() - gameState.startTime) / 1000

        val session = GameSession(
            gameType = "translation_race",
            difficultyLevel = "medium",
            totalQuestions = totalAnswered,
            correctAnswers = gameState.correctAnswers,
            timeSpentSeconds = timeElapsed.toInt(),
            completedAt = if (gameState.currentQuestionIndex >= gameState.totalQuestions) System.currentTimeMillis() else null
        )

        gamesDao.insertGameSession(session)
    }

    /**
     * Get combo message for UI
     */
    fun getComboMessage(combo: Int): String? {
        return when (combo) {
            5 -> "🔥 5 COMBO! +3 seconds"
            10 -> "⚡ 10 COMBO! +5 seconds"
            15 -> "🌟 15 COMBO! +7 seconds"
            30 -> "💫 30 COMBO! +7 seconds"
            else -> null
        }
    }

    /**
     * Get performance rating
     */
    fun getPerformanceRating(gameState: GameState): PerformanceRating {
        val accuracy = gameState.accuracy
        val apm = gameState.answersPerMinute

        return when {
            accuracy >= 95 && apm >= 40 -> PerformanceRating.LEGENDARY
            accuracy >= 90 && apm >= 30 -> PerformanceRating.EXCELLENT
            accuracy >= 80 && apm >= 20 -> PerformanceRating.GREAT
            accuracy >= 70 && apm >= 15 -> PerformanceRating.GOOD
            accuracy >= 60 -> PerformanceRating.AVERAGE
            else -> PerformanceRating.NEEDS_PRACTICE
        }
    }

    enum class PerformanceRating(val displayName: String) {
        LEGENDARY("🏆 Legendary!"),
        EXCELLENT("⭐ Excellent!"),
        GREAT("✨ Great!"),
        GOOD("👍 Good!"),
        AVERAGE("📊 Average"),
        NEEDS_PRACTICE("📚 Keep Practicing!")
    }
}
