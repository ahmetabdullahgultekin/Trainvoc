package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.data.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Listening Quiz Game Logic
 *
 * Players listen to audio pronunciation and select the correct word or translation.
 * Great for pronunciation practice and listening comprehension.
 */
@Singleton
class ListeningQuizGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class ListeningQuestion(
        val word: Word,
        val audioUrl: String, // URL or path to audio file
        val options: List<String>, // 4 options
        val correctAnswer: String,
        val questionType: QuestionType
    )

    enum class QuestionType {
        WORD_TO_TRANSLATION, // Hear English, select Turkish
        TRANSLATION_TO_WORD, // Hear Turkish, select English
        WORD_TO_SPELLING     // Hear English, select correct spelling
    }

    data class GameState(
        val questions: List<ListeningQuestion>,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val startTime: Long = System.currentTimeMillis(),
        val audioPlayed: Boolean = false, // Whether current question audio has been played
        val canReplay: Int = 3 // Number of replays allowed
    ) {
        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val currentQuestion: ListeningQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val totalQuestions: Int
            get() = questions.size

        val accuracy: Float
            get() = if (currentQuestionIndex == 0) 0f
                    else (correctAnswers.toFloat() / currentQuestionIndex) * 100f

        val canReplayAudio: Boolean
            get() = canReplay > 0
    }

    /**
     * Start a new Listening Quiz game
     */
    suspend fun startGame(
        difficulty: String = "medium",
        questionCount: Int = 10,
        questionType: QuestionType = QuestionType.WORD_TO_TRANSLATION
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

        // Filter words with audio (for now, assume all words have TTS capability)
        val wordsWithAudio = words.take(questionCount)

        val questions = wordsWithAudio.map { word ->
            createQuestion(word, words, questionType)
        }

        return GameState(questions = questions)
    }

    /**
     * Create a listening question
     */
    private fun createQuestion(
        word: Word,
        allWords: List<Word>,
        questionType: QuestionType
    ): ListeningQuestion {
        // Generate audio URL (using TTS or pre-recorded audio)
        val audioUrl = when (questionType) {
            QuestionType.WORD_TO_TRANSLATION,
            QuestionType.WORD_TO_SPELLING -> generateAudioUrl(word.english, "en")
            QuestionType.TRANSLATION_TO_WORD -> generateAudioUrl(word.turkish, "tr")
        }

        // Generate options based on question type
        val (correctAnswer, options) = when (questionType) {
            QuestionType.WORD_TO_TRANSLATION -> {
                // Hear English, select Turkish
                val distractors = allWords
                    .filter { it.id != word.id }
                    .map { it.turkish }
                    .distinct()
                    .shuffled()
                    .take(3)
                Pair(word.turkish, (listOf(word.turkish) + distractors).shuffled())
            }
            QuestionType.TRANSLATION_TO_WORD -> {
                // Hear Turkish, select English
                val distractors = allWords
                    .filter { it.id != word.id }
                    .map { it.english }
                    .distinct()
                    .shuffled()
                    .take(3)
                Pair(word.english, (listOf(word.english) + distractors).shuffled())
            }
            QuestionType.WORD_TO_SPELLING -> {
                // Hear English, select correct spelling
                val distractors = generateSpellingVariants(word.english)
                    .filter { it != word.english }
                    .take(3)
                Pair(word.english, (listOf(word.english) + distractors).shuffled())
            }
        }

        return ListeningQuestion(
            word = word,
            audioUrl = audioUrl,
            options = options,
            correctAnswer = correctAnswer,
            questionType = questionType
        )
    }

    /**
     * Generate audio URL for Text-to-Speech
     * In production, this would integrate with a TTS service or use pre-recorded audio
     */
    private fun generateAudioUrl(text: String, language: String): String {
        // Placeholder: In real implementation, use:
        // - Google TTS API
        // - Android TextToSpeech
        // - Pre-recorded audio files
        return "tts://$language/$text"
    }

    /**
     * Generate spelling variants for distractor options
     */
    private fun generateSpellingVariants(word: String): List<String> {
        val variants = mutableListOf<String>()

        // Common spelling mistakes
        if (word.length > 3) {
            // Swap adjacent letters
            for (i in 0 until word.length - 1) {
                val chars = word.toCharArray()
                val temp = chars[i]
                chars[i] = chars[i + 1]
                chars[i + 1] = temp
                variants.add(String(chars))
            }

            // Double a letter
            for (i in word.indices) {
                val variant = word.substring(0, i + 1) + word[i] + word.substring(i + 1)
                variants.add(variant)
            }

            // Remove a letter
            for (i in word.indices) {
                val variant = word.substring(0, i) + word.substring(i + 1)
                if (variant.length >= 3) variants.add(variant)
            }
        }

        return variants.distinct()
    }

    /**
     * Mark audio as played
     */
    fun markAudioPlayed(gameState: GameState): GameState {
        return gameState.copy(audioPlayed = true)
    }

    /**
     * Replay audio (uses one replay)
     */
    fun replayAudio(gameState: GameState): GameState {
        if (!gameState.canReplayAudio) return gameState
        return gameState.copy(canReplay = gameState.canReplay - 1)
    }

    /**
     * Check if answer is correct
     */
    fun checkAnswer(question: ListeningQuestion, answer: String): Boolean {
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
            audioPlayed = false // Reset for next question
        )
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val session = GameSession(
            gameType = "listening_quiz",
            difficulty = "medium",
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
    fun getHint(question: ListeningQuestion): String {
        return when (question.questionType) {
            QuestionType.WORD_TO_TRANSLATION -> "English word: ${question.word.english}"
            QuestionType.TRANSLATION_TO_WORD -> "Turkish word: ${question.word.turkish}"
            QuestionType.WORD_TO_SPELLING -> "First letter: ${question.word.english.first().uppercase()}"
        }
    }
}
