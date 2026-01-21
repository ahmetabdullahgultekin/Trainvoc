package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import kotlinx.coroutines.flow.first
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
        }
    }

    /**
     * Generate questions for a specific word level
     */
    private suspend fun generateQuestionsByLevel(
        quizType: QuizType,
        level: WordLevel
    ): MutableList<Question> {
        val words = when (quizType) {
            QuizType.NOT_LEARNED -> wordDao.getNotLearnedWordsByLevel(level.name).first()
            QuizType.RANDOM -> wordDao.getRandomWordsByLevel(level.name, 10).first()
            QuizType.LEAST_CORRECT -> wordDao.getLeastCorrectWordsByLevel(level.name, 10).first()
            QuizType.LEAST_WRONG -> wordDao.getLeastWrongWordsByLevel(level.name, 10).first()
            QuizType.LEAST_RECENT -> wordDao.getLeastRecentWordsByLevel(level.name, 10).first()
            QuizType.LEAST_REVIEWED -> wordDao.getLeastReviewedWordsByLevel(level.name, 10).first()
            QuizType.MOST_CORRECT -> wordDao.getMostCorrectWordsByLevel(level.name, 10).first()
            QuizType.MOST_WRONG -> wordDao.getMostWrongWordsByLevel(level.name, 10).first()
            QuizType.MOST_RECENT -> wordDao.getMostRecentWordsByLevel(level.name, 10).first()
            QuizType.MOST_REVIEWED -> wordDao.getMostReviewedWordsByLevel(level.name, 10).first()
        }
        return createQuestions(words)
    }

    /**
     * Generate questions for a specific exam type
     */
    private suspend fun generateQuestionsByExam(
        quizType: QuizType,
        exam: Exam
    ): MutableList<Question> {
        val words = when (quizType) {
            QuizType.NOT_LEARNED -> wordDao.getNotLearnedWordsByExam(exam.abbr).first()
            QuizType.RANDOM -> wordDao.getRandomWordsByExam(exam.abbr, 10).first()
            QuizType.LEAST_CORRECT -> wordDao.getLeastCorrectWordsByExam(exam.abbr, 10).first()
            QuizType.LEAST_WRONG -> wordDao.getLeastWrongWordsByExam(exam.abbr, 10).first()
            QuizType.LEAST_RECENT -> wordDao.getLeastRecentWordsByExam(exam.abbr, 10).first()
            QuizType.LEAST_REVIEWED -> wordDao.getLeastReviewedWordsByExam(exam.abbr, 10).first()
            QuizType.MOST_CORRECT -> wordDao.getMostCorrectWordsByExam(exam.abbr, 10).first()
            QuizType.MOST_WRONG -> wordDao.getMostWrongWordsByExam(exam.abbr, 10).first()
            QuizType.MOST_RECENT -> wordDao.getMostRecentWordsByExam(exam.abbr, 10).first()
            QuizType.MOST_REVIEWED -> wordDao.getMostReviewedWordsByExam(exam.abbr, 10).first()
        }
        return createQuestions(words)
    }

    /**
     * Create Question objects from Word list with random distractors
     */
    private fun createQuestions(words: List<Word>): MutableList<Question> {
        return words.mapIndexed { index, word ->
            val allWords = words.toMutableList()
            allWords.remove(word)
            allWords.shuffle()

            val distractors = allWords.take(3).map { it }
            val options = (distractors + word).shuffled()

            Question(
                id = index,
                word = word,
                options = options,
                correctAnswer = word
            )
        }.toMutableList()
    }
}
