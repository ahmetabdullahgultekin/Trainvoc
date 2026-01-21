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
     * NOTE: Current schema limitation - statistics are cumulative without
     * per-answer timestamps. Returns total correct answers.
     *
     * To implement true daily tracking:
     * - Add QuizHistory table with (word_id, timestamp, is_correct)
     * - Query: SELECT COUNT(*) WHERE is_correct AND timestamp >= today_start
     *
     * @return Number of correct answers (total, not filtered by date)
     */
    override suspend fun getDailyCorrectAnswers(): Int {
        // Returns total as placeholder until QuizHistory table is implemented
        return getCorrectAnswers()
    }

    /**
     * Get correct answers count for this week
     *
     * NOTE: Current schema limitation - statistics are cumulative without
     * per-answer timestamps. Returns total correct answers.
     *
     * To implement true weekly tracking:
     * - Add QuizHistory table with (word_id, timestamp, is_correct)
     * - Query: SELECT COUNT(*) WHERE is_correct AND timestamp >= week_start
     *
     * @return Number of correct answers (total, not filtered by date)
     */
    override suspend fun getWeeklyCorrectAnswers(): Int {
        // Returns total as placeholder until QuizHistory table is implemented
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
     * Returns the level with the highest total correct answers.
     *
     * @return Category name (e.g., "A1", "B2"), or null if no data
     */
    override suspend fun getBestCategory(): String? {
        return wordDao.getBestCategory()
    }
}
