package com.gultekinahmetabdullah.trainvoc.games

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Multiple Choice Game - main game class for dependency injection
 */
@Singleton
class MultipleChoiceGame @Inject constructor(
    private val wordDao: WordDao
) {
    private val gameManager = MultipleChoiceGameManager()
    private var currentState: GameState? = null

    suspend fun startGame(difficulty: String = "medium"): GameState {
        gameManager.reset()
        val words = wordDao.getAllWordsList()

        // Handle empty database - return empty completed game
        if (words.isEmpty()) {
            currentState = GameState(
                questions = emptyList(),
                currentQuestionIndex = 0,
                currentQuestion = null,
                score = 0,
                correctAnswers = 0,
                incorrectAnswers = 0,
                totalQuestions = 0,
                isComplete = true // Mark as complete to prevent further interaction
            )
            return currentState!!
        }

        val questions = mutableListOf<MultipleChoiceQuestion>()

        // Generate up to 10 questions (or fewer if not enough words)
        val questionCount = minOf(10, words.size)
        val shuffledWords = words.shuffled().take(questionCount)
        shuffledWords.forEach { word ->
            questions.add(gameManager.generateQuestion(word, words))
        }

        currentState = GameState(
            questions = questions,
            currentQuestionIndex = 0,
            currentQuestion = questions.firstOrNull(),
            score = 0,
            correctAnswers = 0,
            incorrectAnswers = 0,
            totalQuestions = questions.size,
            isComplete = questions.isEmpty()
        )
        return currentState!!
    }

    suspend fun submitAnswer(answer: String): GameState {
        val state = currentState ?: throw IllegalStateException("Game not started")
        val question = state.currentQuestion ?: return state

        val isCorrect = question.isCorrect(answer)
        gameManager.recordAnswer(isCorrect)
        val score = if (isCorrect) gameManager.calculateScore(true, 5) else 0

        val nextIndex = state.currentQuestionIndex + 1
        val isComplete = nextIndex >= state.questions.size

        currentState = state.copy(
            score = state.score + score,
            correctAnswers = state.correctAnswers + if (isCorrect) 1 else 0,
            incorrectAnswers = state.incorrectAnswers + if (!isCorrect) 1 else 0,
            currentQuestionIndex = nextIndex,
            currentQuestion = if (isComplete) null else state.questions.getOrNull(nextIndex),
            isComplete = isComplete
        )
        return currentState!!
    }

    data class GameState(
        val questions: List<MultipleChoiceQuestion> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val currentQuestion: MultipleChoiceQuestion? = null,
        val score: Int = 0,
        val correctAnswers: Int = 0,
        val incorrectAnswers: Int = 0,
        val totalQuestions: Int = 0,
        val isComplete: Boolean = false
    )
}

/**
 * Multiple Choice Quiz Question
 * Supports both word->definition and definition->word modes
 */
data class MultipleChoiceQuestion(
    val word: Word,
    val correctAnswer: String,
    val options: List<String>,
    val questionType: QuestionType,
    val difficulty: DifficultyLevel
) {
    fun isCorrect(selectedAnswer: String): Boolean {
        return selectedAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
    }
}

enum class QuestionType {
    WORD_TO_DEFINITION,  // Show word, choose definition
    DEFINITION_TO_WORD   // Show definition, choose word
}

enum class DifficultyLevel {
    EASY,     // 3 correct in a row needed to advance
    MEDIUM,   // Starting level
    HARD      // 2 wrong in a row drops to medium
}

/**
 * Game Session tracking
 */
@Entity(tableName = "game_sessions")
data class GameSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "game_type")
    val gameType: String, // "multiple_choice", "flip_cards", etc.

    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "started_at")
    val startedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,

    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int = 0,

    @ColumnInfo(name = "correct_answers")
    val correctAnswers: Int = 0,

    @ColumnInfo(name = "incorrect_answers")
    val incorrectAnswers: Int = 0,

    @ColumnInfo(name = "time_spent_seconds")
    val timeSpentSeconds: Int = 0,

    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String = "MEDIUM",

    @ColumnInfo(name = "score")
    val score: Int = 0
) {
    fun getAccuracy(): Float {
        if (totalQuestions == 0) return 0f
        return (correctAnswers.toFloat() / totalQuestions.toFloat()) * 100f
    }

    fun isComplete(): Boolean = completedAt != null
}

/**
 * Multiple Choice Game Manager
 * Handles question generation, difficulty adaptation, scoring
 */
class MultipleChoiceGameManager {

    private var currentDifficulty = DifficultyLevel.MEDIUM
    private var consecutiveCorrect = 0
    private var consecutiveWrong = 0

    /**
     * Generate a multiple choice question from vocabulary
     * @param targetWord The word to quiz on
     * @param allWords Pool of words to generate distractors
     * @param questionType Type of question to generate
     */
    fun generateQuestion(
        targetWord: Word,
        allWords: List<Word>,
        questionType: QuestionType = QuestionType.WORD_TO_DEFINITION
    ): MultipleChoiceQuestion {
        val correctAnswer = when (questionType) {
            QuestionType.WORD_TO_DEFINITION -> targetWord.meaning
            QuestionType.DEFINITION_TO_WORD -> targetWord.word
        }

        // Generate 3 distractors (wrong answers)
        val distractors = generateDistractors(
            targetWord = targetWord,
            allWords = allWords,
            questionType = questionType,
            count = 3
        )

        // Combine correct answer with distractors and shuffle
        val options = (distractors + correctAnswer).shuffled()

        return MultipleChoiceQuestion(
            word = targetWord,
            correctAnswer = correctAnswer,
            options = options,
            questionType = questionType,
            difficulty = currentDifficulty
        )
    }

    /**
     * Generate smart distractors that are similar to correct answer
     */
    private fun generateDistractors(
        targetWord: Word,
        allWords: List<Word>,
        questionType: QuestionType,
        count: Int
    ): List<String> {
        // Filter out the target word and get potential distractors
        val candidates = allWords
            .filter { it.word != targetWord.word }
            .shuffled()

        val distractors = mutableListOf<String>()

        // Try to get words from same level (smarter distractors)
        val sameLevelWords = candidates
            .filter { it.level == targetWord.level }
            .take(count)

        sameLevelWords.forEach { vocab ->
            val distractor = when (questionType) {
                QuestionType.WORD_TO_DEFINITION -> vocab.meaning
                QuestionType.DEFINITION_TO_WORD -> vocab.word
            }
            if (distractor !in distractors) {
                distractors.add(distractor)
            }
        }

        // Fill remaining slots with random words
        if (distractors.size < count) {
            val remaining = candidates
                .filter { vocab ->
                    val text = when (questionType) {
                        QuestionType.WORD_TO_DEFINITION -> vocab.meaning
                        QuestionType.DEFINITION_TO_WORD -> vocab.word
                    }
                    text !in distractors
                }
                .take(count - distractors.size)

            remaining.forEach { vocab ->
                val distractor = when (questionType) {
                    QuestionType.WORD_TO_DEFINITION -> vocab.meaning
                    QuestionType.DEFINITION_TO_WORD -> vocab.word
                }
                distractors.add(distractor)
            }
        }

        return distractors.take(count)
    }

    /**
     * Record answer and adjust difficulty
     */
    fun recordAnswer(isCorrect: Boolean) {
        if (isCorrect) {
            consecutiveCorrect++
            consecutiveWrong = 0

            // Increase difficulty after 3 correct answers
            if (consecutiveCorrect >= 3 && currentDifficulty != DifficultyLevel.HARD) {
                currentDifficulty = when (currentDifficulty) {
                    DifficultyLevel.EASY -> DifficultyLevel.MEDIUM
                    DifficultyLevel.MEDIUM -> DifficultyLevel.HARD
                    DifficultyLevel.HARD -> DifficultyLevel.HARD
                }
                consecutiveCorrect = 0
            }
        } else {
            consecutiveWrong++
            consecutiveCorrect = 0

            // Decrease difficulty after 2 wrong answers
            if (consecutiveWrong >= 2 && currentDifficulty != DifficultyLevel.EASY) {
                currentDifficulty = when (currentDifficulty) {
                    DifficultyLevel.EASY -> DifficultyLevel.EASY
                    DifficultyLevel.MEDIUM -> DifficultyLevel.EASY
                    DifficultyLevel.HARD -> DifficultyLevel.MEDIUM
                }
                consecutiveWrong = 0
            }
        }
    }

    /**
     * Calculate score for a question based on difficulty and time
     */
    fun calculateScore(isCorrect: Boolean, timeSeconds: Int): Int {
        if (!isCorrect) return 0

        val baseScore = when (currentDifficulty) {
            DifficultyLevel.EASY -> 10
            DifficultyLevel.MEDIUM -> 20
            DifficultyLevel.HARD -> 30
        }

        // Time bonus: faster answers get higher scores
        val timeBonus = when {
            timeSeconds <= 3 -> 10  // Very fast
            timeSeconds <= 5 -> 5   // Fast
            timeSeconds <= 10 -> 2  // Normal
            else -> 0               // Slow
        }

        return baseScore + timeBonus
    }

    /**
     * Reset difficulty to medium (for new game)
     */
    fun reset() {
        currentDifficulty = DifficultyLevel.MEDIUM
        consecutiveCorrect = 0
        consecutiveWrong = 0
    }

    fun getCurrentDifficulty() = currentDifficulty
}
