package com.gultekinahmetabdullah.trainvoc.repository

/**
 * Interface for aggregated analytics and performance metrics.
 * Provides overall statistics for the user's learning journey.
 * Follows Interface Segregation Principle.
 */
interface IAnalyticsService {
    /**
     * Get total correct answer count.
     */
    suspend fun getCorrectAnswers(): Int

    /**
     * Get total wrong answer count.
     */
    suspend fun getWrongAnswers(): Int

    /**
     * Get total skipped answer count.
     */
    suspend fun getSkippedAnswers(): Int

    /**
     * Get total time spent learning (in seconds).
     */
    suspend fun getTotalTimeSpent(): Int

    /**
     * Get timestamp of last answered question.
     */
    suspend fun getLastAnswered(): Long

    /**
     * Get total quiz count.
     */
    suspend fun getTotalQuizCount(): Int

    /**
     * Get correct answers count for today.
     */
    suspend fun getDailyCorrectAnswers(): Int

    /**
     * Get correct answers count for this week.
     */
    suspend fun getWeeklyCorrectAnswers(): Int

    /**
     * Get the word with most wrong answers.
     */
    suspend fun getMostWrongWord(): String?

    /**
     * Get the best performing category.
     */
    suspend fun getBestCategory(): String?
}
