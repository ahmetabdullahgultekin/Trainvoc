package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordQueryBuilder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for quiz question generation
 *
 * Separated from WordRepository to follow Single Responsibility Principle.
 * Handles only quiz-related operations like question generation and selection.
 *
 * @property wordDao Database access object for word queries
 */
@Singleton
class QuizService @Inject constructor(
    private val wordDao: WordDao
) : IQuizService {

    /**
     * Generate 10 quiz questions based on quiz type and parameters
     *
     * @param quizType The type of quiz (NOT_LEARNED, RANDOM, etc.)
     * @param quizParameter The parameters (Level or ExamType)
     * @return Mutable list of 10 questions
     */
    override suspend fun generateTenQuestions(
        quizType: QuizType,
        quizParameter: QuizParameter
    ): MutableList<Question> {
        return when (quizParameter) {
            is QuizParameter.Level -> generateQuestionsByLevel(quizType, quizParameter.wordLevel)
            is QuizParameter.ExamType -> generateQuestionsByExam(quizType, quizParameter.exam)
            is QuizParameter.Review -> generateQuestionsByWordIds(quizParameter.wordIds)
        }
    }

    /**
     * Generate questions for a specific word level using dynamic query builder
     */
    private suspend fun generateQuestionsByLevel(
        quizType: QuizType,
        level: WordLevel
    ): MutableList<Question> {
        val query = WordQueryBuilder.buildQuery(
            quizType = quizType,
            level = level.name,
            exam = null,
            limit = 10
        )
        val words = wordDao.getWordsByQuery(query)
        return createQuestions(words)
    }

    /**
     * Generate questions for a specific exam type using dynamic query builder
     * Note: "Mixed" exam returns all words (null exam filter)
     */
    private suspend fun generateQuestionsByExam(
        quizType: QuizType,
        exam: Exam
    ): MutableList<Question> {
        // "Mixed" exam means all words (no exam filter)
        val examFilter = if (exam.exam == "Mixed") null else exam.exam
        val query = WordQueryBuilder.buildQuery(
            quizType = quizType,
            level = null,
            exam = examFilter,
            limit = 10
        )
        val words = wordDao.getWordsByQuery(query)
        return createQuestions(words)
    }

    /**
     * Generate questions for specific word IDs (Review mode)
     * Used for reviewing missed words from previous quizzes
     *
     * @param wordIds List of word IDs to create questions from
     * @return Mutable list of questions for the specified words
     */
    private suspend fun generateQuestionsByWordIds(
        wordIds: List<String>
    ): MutableList<Question> {
        val words = wordIds.mapNotNull { wordId ->
            wordDao.getWord(wordId) // Returns null if word not found
        }

        if (words.isEmpty()) {
            return mutableListOf()
        }

        return createQuestionsWithDistractors(words)
    }

    /**
     * Create Question objects from Word list with random distractors from DB
     * This method fetches additional words for distractors when needed
     */
    private suspend fun createQuestionsWithDistractors(words: List<Word>): MutableList<Question> {
        // Get additional words for distractors if we don't have enough
        val distractorPool = if (words.size < 4) {
            // Need to fetch more words for distractors
            val query = WordQueryBuilder.buildQuery(
                quizType = com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType.RANDOM,
                level = null,
                exam = null,
                limit = 20
            )
            wordDao.getWordsByQuery(query).toMutableList()
        } else {
            words.toMutableList()
        }

        return words.map { word ->
            val availableDistractors = distractorPool.filter { it.word != word.word }.toMutableList()
            availableDistractors.shuffle()
            val incorrectWords = availableDistractors.take(3)

            Question(
                correctWord = word,
                incorrectWords = incorrectWords
            )
        }.toMutableList()
    }

    /**
     * Create Question objects from Word list with random distractors
     */
    private fun createQuestions(words: List<Word>): MutableList<Question> {
        return words.map { word ->
            val allWords = words.toMutableList()
            allWords.remove(word)
            allWords.shuffle()

            val incorrectWords = allWords.take(3)

            Question(
                correctWord = word,
                incorrectWords = incorrectWords
            )
        }.toMutableList()
    }
}
