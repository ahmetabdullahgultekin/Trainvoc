package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistory
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizQuestionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IQuizHistoryRepository] delegating to Room's [QuizHistoryDao]
 * on the IO dispatcher.
 */
@Singleton
class QuizHistoryRepository @Inject constructor(
    private val quizHistoryDao: QuizHistoryDao,
    private val dispatchers: DispatcherProvider
) : IQuizHistoryRepository {

    override suspend fun insertQuizHistory(quizHistory: QuizHistory): Long =
        withContext(dispatchers.io) {
            quizHistoryDao.insertQuizHistory(quizHistory)
        }

    override suspend fun insertQuestionResults(questionResults: List<QuizQuestionResult>) =
        withContext(dispatchers.io) {
            quizHistoryDao.insertQuestionResults(questionResults)
        }

    override suspend fun getLastQuizResult(): QuizHistory? =
        withContext(dispatchers.io) {
            quizHistoryDao.getLastQuizResult()
        }

    override fun getQuizHistory(limit: Int): Flow<List<QuizHistory>> =
        quizHistoryDao.getQuizHistory(limit)

    override suspend fun getQuizHistoryByType(quizType: String): List<QuizHistory> =
        withContext(dispatchers.io) {
            quizHistoryDao.getQuizHistoryByType(quizType)
        }

    override suspend fun getQuestionResults(quizId: Int): List<QuizQuestionResult> =
        withContext(dispatchers.io) {
            quizHistoryDao.getQuestionResults(quizId)
        }

    override suspend fun getIncorrectWordIds(quizId: Int): List<Long> =
        withContext(dispatchers.io) {
            quizHistoryDao.getIncorrectWordIds(quizId)
        }

    override suspend fun getTotalQuizCount(): Int =
        withContext(dispatchers.io) {
            quizHistoryDao.getTotalQuizCount()
        }

    override suspend fun getAverageAccuracy(): Float? =
        withContext(dispatchers.io) {
            quizHistoryDao.getAverageAccuracy()
        }

    override suspend fun getTotalQuestionsAnswered(): Int? =
        withContext(dispatchers.io) {
            quizHistoryDao.getTotalQuestionsAnswered()
        }

    override suspend fun deleteOldQuizzes(cutoffTimestamp: Long) =
        withContext(dispatchers.io) {
            quizHistoryDao.deleteOldQuizzes(cutoffTimestamp)
        }

    override suspend fun getCorrectAnswersInRange(startTime: Long, endTime: Long): Int =
        withContext(dispatchers.io) {
            quizHistoryDao.getCorrectAnswersInRange(startTime, endTime)
        }

    override suspend fun getTotalQuestionsInRange(startTime: Long, endTime: Long): Int =
        withContext(dispatchers.io) {
            quizHistoryDao.getTotalQuestionsInRange(startTime, endTime)
        }
}
