package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import javax.inject.Inject

/**
 * Use Case for calculating quiz/level progress statistics.
 * Follows Dependency Inversion and Interface Segregation Principles.
 */
class CalculateProgressUseCase @Inject constructor(
    private val progressService: IProgressService,
    private val analyticsService: IAnalyticsService
) {
    /**
     * Data class holding progress information
     */
    data class ProgressInfo(
        val totalWords: Int,
        val learnedWords: Int,
        val progressPercent: Int
    )

    /**
     * Calculates progress for a given quiz parameter (level or exam).
     */
    suspend operator fun invoke(parameter: QuizParameter): Result<ProgressInfo> {
        return try {
            val totalWords = when (parameter) {
                is QuizParameter.Level -> progressService.getWordCountByLevel(parameter.wordLevel.name)
                is QuizParameter.ExamType -> progressService.getWordCountByExam(parameter.exam.exam)
                is QuizParameter.Review -> parameter.wordIds.size
            }

            val learnedWords = when (parameter) {
                is QuizParameter.Level -> progressService.getLearnedWordCount(parameter.wordLevel.name)
                is QuizParameter.ExamType -> progressService.getLearnedWordCountByExam(parameter.exam.exam)
                is QuizParameter.Review -> 0 // Review mode doesn't track learned words
            }

            val progressPercent = if (totalWords > 0) {
                ((learnedWords.toFloat() / totalWords) * 100).toInt()
            } else {
                0
            }

            Result.success(
                ProgressInfo(
                    totalWords = totalWords,
                    learnedWords = learnedWords,
                    progressPercent = progressPercent
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets daily statistics.
     */
    suspend fun getDailyStats(): Result<Int> {
        return try {
            val dailyCorrect = analyticsService.getDailyCorrectAnswers()
            Result.success(dailyCorrect)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets weekly statistics.
     */
    suspend fun getWeeklyStats(): Result<Int> {
        return try {
            val weeklyCorrect = analyticsService.getWeeklyCorrectAnswers()
            Result.success(weeklyCorrect)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
