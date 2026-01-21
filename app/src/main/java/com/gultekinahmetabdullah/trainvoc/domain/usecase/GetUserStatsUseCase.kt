package com.gultekinahmetabdullah.trainvoc.domain.usecase

import android.icu.text.DateFormat
import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for retrieving user statistics
 *
 * Extracts business logic from StatsViewModel. Handles:
 * - Fetching all analytics data from repository
 * - Calculating success/failure/skip ratios
 * - Formatting timestamps
 * - Aggregating related statistics
 *
 * Example usage in ViewModel:
 * ```kotlin
 * viewModelScope.launch {
 *     when (val result = getUserStatsUseCase()) {
 *         is AppResult.Success -> updateUI(result.data)
 *         is AppResult.Error -> showError(result.error)
 *         is AppResult.Loading -> showLoading()
 *     }
 * }
 * ```
 */
class GetUserStatsUseCase @Inject constructor(
    private val analyticsService: IAnalyticsService,
    private val dispatchers: DispatcherProvider
) {
    /**
     * Get comprehensive user statistics
     *
     * @return AppResult containing UserStats or error
     */
    suspend operator fun invoke(): AppResult<UserStats> = withContext(dispatchers.io) {
        try {
            // Fetch raw data from analytics service
            val correctAnswers = analyticsService.getCorrectAnswers()
            val incorrectAnswers = analyticsService.getWrongAnswers()
            val skippedQuestions = analyticsService.getSkippedAnswers()
            val totalTimeSpent = analyticsService.getTotalTimeSpent()
            val lastAnsweredTimestamp = analyticsService.getLastAnswered()
            val totalQuizCount = analyticsService.getTotalQuizCount()
            val dailyCorrect = analyticsService.getDailyCorrectAnswers()
            val weeklyCorrect = analyticsService.getWeeklyCorrectAnswers()
            val mostWrongWord = analyticsService.getMostWrongWord()
            val bestCategory = analyticsService.getBestCategory()

            // Calculate derived values
            val totalQuestions = correctAnswers + incorrectAnswers + skippedQuestions

            val successRatio = calculateRatio(correctAnswers, totalQuestions)
            val failureRatio = calculateRatio(incorrectAnswers, totalQuestions)
            val skippedRatio = calculateRatio(skippedQuestions, totalQuestions)

            val lastAnswered = formatTimestamp(lastAnsweredTimestamp)

            // Return aggregated statistics
            AppResult.Success(
                UserStats(
                    correctAnswers = correctAnswers,
                    incorrectAnswers = incorrectAnswers,
                    skippedQuestions = skippedQuestions,
                    totalQuestions = totalQuestions,
                    successRatio = successRatio,
                    failureRatio = failureRatio,
                    skippedRatio = skippedRatio,
                    totalTimeSpent = totalTimeSpent,
                    lastAnswered = lastAnswered,
                    totalQuizCount = totalQuizCount,
                    dailyCorrect = dailyCorrect,
                    weeklyCorrect = weeklyCorrect,
                    mostWrongWord = mostWrongWord ?: "-",
                    bestCategory = bestCategory ?: "-"
                )
            )
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Database(
                    message = "Failed to load statistics: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Calculate ratio with division by zero protection
     */
    private fun calculateRatio(count: Int, total: Int): Float {
        return if (total == 0) 0f else count.toFloat() / total
    }

    /**
     * Format timestamp to readable date/time string
     */
    private fun formatTimestamp(timestamp: Long): String {
        return if (timestamp == 0L) {
            "N/A"
        } else {
            DateFormat.getDateTimeInstance().format(timestamp)
        }
    }
}

/**
 * Aggregated user statistics
 *
 * Contains all statistics needed for the stats screen, with calculated
 * ratios and formatted timestamps.
 */
data class UserStats(
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val skippedQuestions: Int,
    val totalQuestions: Int,
    val successRatio: Float,
    val failureRatio: Float,
    val skippedRatio: Float,
    val totalTimeSpent: Int,
    val lastAnswered: String,
    val totalQuizCount: Int,
    val dailyCorrect: Int,
    val weeklyCorrect: Int,
    val mostWrongWord: String,
    val bestCategory: String
)
