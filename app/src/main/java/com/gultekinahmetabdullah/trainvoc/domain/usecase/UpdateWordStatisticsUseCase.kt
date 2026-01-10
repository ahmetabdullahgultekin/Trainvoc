package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import javax.inject.Inject

/**
 * Use Case for updating word statistics after quiz answers.
 * Encapsulates business logic for correct/wrong/skipped answer handling.
 * Follows Dependency Inversion Principle by depending on IWordStatisticsService interface.
 */
class UpdateWordStatisticsUseCase @Inject constructor(
    private val wordStatisticsService: IWordStatisticsService
) {
    /**
     * Updates statistics when a word is answered correctly.
     */
    suspend fun onCorrectAnswer(word: Word): Result<Unit> {
        return try {
            val stats = wordStatisticsService.getWordStats(word)
            val updatedStats = stats.copy(correctCount = stats.correctCount + 1)
            wordStatisticsService.updateWordStats(updatedStats, word)
            wordStatisticsService.updateLastAnswered(word.word)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates statistics when a word is answered incorrectly.
     */
    suspend fun onWrongAnswer(word: Word): Result<Unit> {
        return try {
            val stats = wordStatisticsService.getWordStats(word)
            val updatedStats = stats.copy(wrongCount = stats.wrongCount + 1)
            wordStatisticsService.updateWordStats(updatedStats, word)
            wordStatisticsService.updateLastAnswered(word.word)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates statistics when a word is skipped (time up).
     */
    suspend fun onSkippedAnswer(word: Word): Result<Unit> {
        return try {
            val stats = wordStatisticsService.getWordStats(word)
            val updatedStats = stats.copy(skippedCount = stats.skippedCount + 1)
            wordStatisticsService.updateWordStats(updatedStats, word)
            wordStatisticsService.updateLastAnswered(word.word)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets current statistics for a word.
     */
    suspend fun getWordStatistics(word: Word): Result<Statistic> {
        return try {
            val stats = wordStatisticsService.getWordStats(word)
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
