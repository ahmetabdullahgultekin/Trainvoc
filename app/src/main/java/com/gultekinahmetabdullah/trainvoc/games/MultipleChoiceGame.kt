package com.gultekinahmetabdullah.trainvoc.games

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

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
