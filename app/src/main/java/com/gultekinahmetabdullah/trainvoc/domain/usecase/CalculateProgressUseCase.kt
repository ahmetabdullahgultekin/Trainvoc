package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import javax.inject.Inject

/**
 * Use Case for calculating quiz/level progress statistics.
 * Follows Dependency Inversion Principle by depending on IWordRepository interface.
 */
class CalculateProgressUseCase @Inject constructor(
    private val repository: IWordRepository
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
                is QuizParameter.Level -> repository.getWordCountByLevel(parameter.wordLevel.name)
                is QuizParameter.ExamType -> repository.getWordCountByExam(parameter.exam.exam)
            }

            val learnedWords = when (parameter) {
                is QuizParameter.Level -> repository.getLearnedWordCount(parameter.wordLevel.name)
                is QuizParameter.ExamType -> repository.getLearnedWordCountByExam(parameter.exam.exam)
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
            val dailyCorrect = repository.getDailyCorrectAnswers()
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
            val weeklyCorrect = repository.getWeeklyCorrectAnswers()
            Result.success(weeklyCorrect)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
