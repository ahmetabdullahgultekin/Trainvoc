package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fill in the Blank Game Logic
 *
 * Presents sentences with missing words that the user must fill in.
 * Uses example sentences from the word database.
 */
@Singleton
class FillInTheBlankGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class FillInTheBlankQuestion(
        val word: Word,
        val sentenceWithBlank: String,
        val correctAnswer: String,
        val options: List<String>, // 4 options including the correct one
        val blankPosition: Int // Position of the blank in the sentence
    )

    data class GameState(
        val questions: List<FillInTheBlankQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val startTime: Long = System.currentTimeMillis()
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val currentQuestion: FillInTheBlankQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() = if (currentQuestionIndex == 0) 0f
                    else (correctAnswers.toFloat() / currentQuestionIndex) * 100f
    }

    /**
     * Start a new Fill in the Blank game
     * @param difficulty Game difficulty (easy, medium, hard)
     * @param questionCount Number of questions to generate
     * @return Initial game state
     */
    suspend fun startGame(
        difficulty: String = "medium",
        questionCount: Int = 10
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
     * Create a fill-in-the-blank question from a word
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>
    ): FillInTheBlankQuestion {
        // Create a sentence with a blank for the word
        val sentenceWithBlank = "The meaning of _____ is: ${word.meaning}"
        val blankPosition = 15 // Position after "The meaning of "

        // Generate distractor options (wrong answers)
        val distractors = generateDistractors(word, allWords, count = 3)

        // Combine correct answer with distractors and shuffle
        val options = (listOf(word.word) + distractors).shuffled()

        return FillInTheBlankQuestion(
            word = word,
            sentenceWithBlank = sentenceWithBlank.trim(),
            correctAnswer = word.word,
            options = options,
            blankPosition = blankPosition
        )
    }

    /**
     * Generate distractor options (wrong answers)
     */
    private fun generateDistractors(
        targetWord: Word,
        allWords: List<Word>,
        count: Int
    ): List<String> {
        // Get words of similar level
        val similarWords = allWords
            .filter { it.word != targetWord.word }
            .filter { it.level == targetWord.level }
            .shuffled()
            .take(count)

        // If not enough similar words, add any other words
        if (similarWords.size < count) {
            val additionalWords = allWords
                .filter { it.word != targetWord.word }
                .filter { !similarWords.contains(it) }
                .shuffled()
                .take(count - similarWords.size)
            return (similarWords + additionalWords).map { it.word }
        }

        return similarWords.map { it.word }
    }

    /**
     * Check if the answer is correct
     */
    fun checkAnswer(question: FillInTheBlankQuestion, answer: String): Boolean {
        return answer.equals(question.correctAnswer, ignoreCase = true)
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
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val session = GameSession(
            gameType = "fill_in_blank",
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
    fun getHint(question: FillInTheBlankQuestion): String {
        return buildString {
            append("Level: ${question.word.level?.name ?: "Unknown"}\n")
            append("First letter: ${question.correctAnswer.firstOrNull()?.uppercase() ?: "?"}\n")
            append("Length: ${question.correctAnswer.length} letters")
        }
    }
}
