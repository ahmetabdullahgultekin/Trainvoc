package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.data.Word
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

        // Filter words that have example sentences
        val wordsWithExamples = words.filter { !it.exampleSentence.isNullOrBlank() }
        val selectedWords = wordsWithExamples.shuffled().take(questionCount)

        val questions = selectedWords.map { word ->
            createQuestion(word, words)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a fill-in-the-blank question from a word
     */
    private suspend fun createQuestion(
        word: Word,
        allWords: List<Word>
    ): FillInTheBlankQuestion {
        val exampleSentence = word.exampleSentence ?: ""

        // Create the sentence with a blank
        // Replace the word (case-insensitive) with a blank marker
        val wordPattern = "\\b${Regex.escape(word.english)}\\b".toRegex(RegexOption.IGNORE_CASE)
        val match = wordPattern.find(exampleSentence)

        val (sentenceWithBlank, blankPosition) = if (match != null) {
            val position = match.range.first
            val before = exampleSentence.substring(0, match.range.first)
            val after = exampleSentence.substring(match.range.last + 1)
            Pair("$before _____ $after", position)
        } else {
            // Fallback: put blank at the end
            Pair("$exampleSentence: _____", exampleSentence.length)
        }

        // Generate distractor options (wrong answers)
        val distractors = generateDistractors(word, allWords, count = 3)

        // Combine correct answer with distractors and shuffle
        val options = (listOf(word.english) + distractors).shuffled()

        return FillInTheBlankQuestion(
            word = word,
            sentenceWithBlank = sentenceWithBlank.trim(),
            correctAnswer = word.english,
            options = options,
            blankPosition = blankPosition
        )
    }

    /**
     * Generate distractor options (wrong answers)
     */
    private suspend fun generateDistractors(
        targetWord: Word,
        allWords: List<Word>,
        count: Int
    ): List<String> {
        // Get words of similar level and part of speech
        val similarWords = allWords
            .filter { it.id != targetWord.id }
            .filter { it.level == targetWord.level }
            .filter { it.partOfSpeech == targetWord.partOfSpeech }
            .shuffled()
            .take(count)

        // If not enough similar words, add any other words
        if (similarWords.size < count) {
            val additionalWords = allWords
                .filter { it.id != targetWord.id }
                .filter { !similarWords.contains(it) }
                .shuffled()
                .take(count - similarWords.size)
            return (similarWords + additionalWords).map { it.english }
        }

        return similarWords.map { it.english }
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
            difficulty = "medium", // TODO: Store difficulty in GameState
            totalQuestions = gameState.totalQuestions,
            correctAnswers = gameState.correctAnswers,
            timeSeconds = ((System.currentTimeMillis() - gameState.startTime) / 1000).toInt(),
            completed = true,
            completedAt = System.currentTimeMillis()
        )

        gamesDao.insertGameSession(session)
    }

    /**
     * Get hint for current question
     */
    fun getHint(question: FillInTheBlankQuestion): String {
        return buildString {
            append("Part of speech: ${question.word.partOfSpeech}\n")
            append("First letter: ${question.correctAnswer.first().uppercase()}\n")
            append("Length: ${question.correctAnswer.length} letters")
        }
    }
}
