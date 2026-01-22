package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Picture Match Game Logic
 *
 * Players match words with their meanings using visual cards.
 * Shows the Turkish meaning and players select the correct English word.
 */
@Singleton
class PictureMatchGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class PictureQuestion(
        val word: Word,
        val displayText: String, // The meaning or hint to display
        val options: List<String>, // 4 word options (always distinct)
        val correctAnswer: String,
        val questionType: QuestionType
    )

    enum class QuestionType {
        MEANING_TO_WORD,  // Show meaning, select word
        WORD_TO_MEANING   // Show word, select meaning
    }

    data class GameState(
        val questions: List<PictureQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val startTime: Long = System.currentTimeMillis(),
        val streakCount: Int = 0,
        val bestStreak: Int = 0
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val currentQuestion: PictureQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() = if (currentQuestionIndex == 0) 0f
                    else (correctAnswers.toFloat() / currentQuestionIndex) * 100f

        val score: Int
            get() = correctAnswers * 10 + bestStreak * 5
    }

    /**
     * Start a new Picture Match game
     */
    suspend fun startGame(
        difficulty: String = "medium",
        questionCount: Int = 12,
        questionType: QuestionType = QuestionType.MEANING_TO_WORD
    ): GameState {
        val words = when (difficulty) {
            "easy" -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "A2",
                limit = questionCount * 3 // Get more words for better distractor variety
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

        // Filter words with unique meanings and words
        val uniqueWords = words.distinctBy { it.word.lowercase() }
            .filter { it.meaning.isNotBlank() }

        // Select random words for the game
        val selectedWords = uniqueWords.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word, uniqueWords, questionType)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a picture match question
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>,
        questionType: QuestionType
    ): PictureQuestion {
        return when (questionType) {
            QuestionType.MEANING_TO_WORD -> {
                // Show Turkish meaning, select English word
                val distractors = allWords
                    .filter { it.word.lowercase() != word.word.lowercase() }
                    .filter { it.meaning.lowercase() != word.meaning.lowercase() }
                    .map { it.word }
                    .distinct()
                    .shuffled()
                    .take(3)

                val options = (listOf(word.word) + distractors).shuffled()

                PictureQuestion(
                    word = word,
                    displayText = word.meaning,
                    options = options,
                    correctAnswer = word.word,
                    questionType = questionType
                )
            }
            QuestionType.WORD_TO_MEANING -> {
                // Show English word, select Turkish meaning
                val distractors = allWords
                    .filter { it.word.lowercase() != word.word.lowercase() }
                    .filter { it.meaning.lowercase() != word.meaning.lowercase() }
                    .map { it.meaning }
                    .distinct()
                    .shuffled()
                    .take(3)

                val options = (listOf(word.meaning) + distractors).shuffled()

                PictureQuestion(
                    word = word,
                    displayText = word.word,
                    options = options,
                    correctAnswer = word.meaning,
                    questionType = questionType
                )
            }
        }
    }

    /**
     * Check if answer is correct
     */
    fun checkAnswer(question: PictureQuestion, answer: String): Boolean {
        return answer == question.correctAnswer
    }

    /**
     * Process answer and update game state
     */
    fun answerQuestion(gameState: GameState, answer: String): GameState {
        val question = gameState.currentQuestion ?: return gameState
        val isCorrect = checkAnswer(question, answer)

        val newStreak = if (isCorrect) gameState.streakCount + 1 else 0
        val newBestStreak = maxOf(gameState.bestStreak, newStreak)

        return gameState.copy(
            currentQuestionIndex = gameState.currentQuestionIndex + 1,
            correctAnswers = if (isCorrect) gameState.correctAnswers + 1 else gameState.correctAnswers,
            streakCount = newStreak,
            bestStreak = newBestStreak
        )
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val session = GameSession(
            gameType = "picture_match",
            difficultyLevel = "medium",
            totalQuestions = gameState.totalQuestions,
            correctAnswers = gameState.correctAnswers,
            timeSpentSeconds = ((System.currentTimeMillis() - gameState.startTime) / 1000).toInt(),
            completedAt = System.currentTimeMillis()
        )

        gamesDao.insertGameSession(session)
    }

    /**
     * Get hint for current question
     */
    fun getHint(question: PictureQuestion): String {
        return buildString {
            when (question.questionType) {
                QuestionType.MEANING_TO_WORD -> {
                    append("The answer starts with: ${question.correctAnswer.first().uppercaseChar()}\n")
                }
                QuestionType.WORD_TO_MEANING -> {
                    append("The meaning is in Turkish\n")
                }
            }
            append("Level: ${question.word.level?.name ?: "Unknown"}")
        }
    }
}
