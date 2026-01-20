package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.database.WordDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for aggregated analytics and performance metrics
 *
 * Separated from WordRepository to follow Single Responsibility Principle.
 * Provides overall statistics for the user's learning journey.
 *
 * @property wordDao Database access object for analytics queries
 */
@Singleton
class AnalyticsService @Inject constructor(
    private val wordDao: WordDao
) : IAnalyticsService {

    /**
     * Get total correct answer count
     */
    override suspend fun getCorrectAnswers(): Int = wordDao.getCorrectAnswers()

    /**
     * Get total wrong answer count
     */
    override suspend fun getWrongAnswers(): Int = wordDao.getWrongAnswers()

    /**
     * Get total skipped answer count
     */
    override suspend fun getSkippedAnswers(): Int = wordDao.getSkippedAnswers()

    /**
     * Get total time spent learning (in seconds)
     */
    override suspend fun getTotalTimeSpent(): Int = wordDao.getTotalTimeSpent()

    /**
     * Get timestamp of last answered question
     */
    override suspend fun getLastAnswered(): Long = wordDao.getLastAnswered()

    /**
     * Get total quiz count
     */
    override suspend fun getTotalQuizCount(): Int {
        // Calculate as sum of all answers
        return getCorrectAnswers() + getWrongAnswers() + getSkippedAnswers()
    }

    /**
     * Get correct answers count for today
     *
     * TODO: Implement time-based filtering in WordDao
     * Currently returns total correct answers as placeholder
     *
     * @return Number of correct answers today
     */
    override suspend fun getDailyCorrectAnswers(): Int {
        // Placeholder: requires getCorrectAnswersSince() in WordDao
        return getCorrectAnswers()
    }

    /**
     * Get correct answers count for this week
     *
     * TODO: Implement time-based filtering in WordDao
     * Currently returns total correct answers as placeholder
     *
     * @return Number of correct answers this week
     */
    override suspend fun getWeeklyCorrectAnswers(): Int {
        // Placeholder: requires getCorrectAnswersSince() in WordDao
        return getCorrectAnswers()
    }

    /**
     * Get the word with most wrong answers
     *
     * @return Word text, or null if no data
     */
    override suspend fun getMostWrongWord(): String? {
        return wordDao.getMostWrongWord()
    }

    /**
     * Get the best performing category
     *
     * Categories are word levels (A1, A2, etc.)
     *
     * TODO: Implement category analytics in WordDao
     * Currently returns null as placeholder
     *
     * @return Category name, or null if no data
     */
    override suspend fun getBestCategory(): String? {
        // Placeholder: requires getBestPerformingLevel() in WordDao
        return null
    }
}
