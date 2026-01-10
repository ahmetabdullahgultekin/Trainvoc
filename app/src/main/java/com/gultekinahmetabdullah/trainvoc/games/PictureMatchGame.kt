package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Picture Match Game Logic
 *
 * Players match words with corresponding images.
 * Visual learning helps with memory retention and makes learning fun.
 */
@Singleton
class PictureMatchGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class PictureQuestion(
        val word: Word,
        val imageUrl: String,
        val options: List<String>, // 4 word options
        val correctAnswer: String,
        val showWord: Boolean // If false, show image and select word; if true, reverse
    )

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
        showWord: Boolean = false // Default: show image, select word
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

        // Select random words for the game (all words use placeholder images)
        val selectedWords = words.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word, words, showWord)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a picture match question
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>,
        showWord: Boolean
    ): PictureQuestion {
        // Get image URL (generate placeholder)
        val imageUrl = generatePlaceholderImageUrl(word.word)

        // Generate distractor options
        val distractors = allWords
            .filter { it.word != word.word }
            .filter { it.level == word.level } // Same level for fair difficulty
            .map { it.word }
            .distinct()
            .shuffled()
            .take(3)

        val options = (listOf(word.word) + distractors).shuffled()

        return PictureQuestion(
            word = word,
            imageUrl = imageUrl,
            options = options,
            correctAnswer = word.word,
            showWord = showWord
        )
    }

    /**
     * Generate placeholder image URL
     * In production, use actual image database or API (Unsplash, Pexels, etc.)
     */
    private fun generatePlaceholderImageUrl(word: String): String {
        // Using Unsplash Source API as placeholder
        // In production, store actual image URLs in database
        return "https://source.unsplash.com/400x400/?$word"
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
            append("Meaning: ${question.word.meaning}\n")
            append("Level: ${question.word.level?.name ?: "Unknown"}")
        }
    }

    /**
     * Preload images for smoother experience
     */
    fun getImagesToPreload(gameState: GameState): List<String> {
        return gameState.questions.map { it.imageUrl }
    }
}
