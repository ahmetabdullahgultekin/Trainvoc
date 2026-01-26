package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
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
 * @property statisticDao Database access object for date-filtered statistics
 */
@Singleton
class AnalyticsService @Inject constructor(
    private val wordDao: WordDao,
    private val statisticDao: StatisticDao
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
     * NOTE: Uses last_reviewed timestamp to filter words reviewed today.
     * Returns cumulative correct count for those words (approximate metric).
     * For exact per-answer tracking, a QuizHistory table would be needed.
     *
     * @return Sum of correct answers for words reviewed today
     */
    override suspend fun getDailyCorrectAnswers(): Int {
        return statisticDao.getDailyCorrectAnswers() ?: 0
    }

    /**
     * Get correct answers count for this week
     *
     * NOTE: Uses last_reviewed timestamp to filter words reviewed this week.
     * Returns cumulative correct count for those words (approximate metric).
     * For exact per-answer tracking, a QuizHistory table would be needed.
     *
     * @return Sum of correct answers for words reviewed this week
     */
    override suspend fun getWeeklyCorrectAnswers(): Int {
        return statisticDao.getWeeklyCorrectAnswers() ?: 0
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
     * Returns the level with the highest total correct answers.
     *
     * @return Category name (e.g., "A1", "B2"), or null if no data
     */
    override suspend fun getBestCategory(): String? {
        return wordDao.getBestCategory()
    }
}
