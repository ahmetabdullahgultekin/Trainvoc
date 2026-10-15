package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistory
import com.gultekinahmetabdullah.trainvoc.quiz.QuizQuestionResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing quiz history records and question-level results.
 */
interface IQuizHistoryRepository {
    suspend fun insertQuizHistory(quizHistory: QuizHistory): Long
    suspend fun insertQuestionResults(questionResults: List<QuizQuestionResult>)
    suspend fun getLastQuizResult(): QuizHistory?
    fun getQuizHistory(limit: Int = 10): Flow<List<QuizHistory>>
    suspend fun getQuizHistoryByType(quizType: String): List<QuizHistory>
    suspend fun getQuestionResults(quizId: Int): List<QuizQuestionResult>
    suspend fun getIncorrectWordIds(quizId: Int): List<Long>
    suspend fun getTotalQuizCount(): Int
    suspend fun getAverageAccuracy(): Float?
    suspend fun getTotalQuestionsAnswered(): Int?
    suspend fun deleteOldQuizzes(cutoffTimestamp: Long)
    suspend fun getCorrectAnswersInRange(startTime: Long, endTime: Long): Int
    suspend fun getTotalQuestionsInRange(startTime: Long, endTime: Long): Int
}
