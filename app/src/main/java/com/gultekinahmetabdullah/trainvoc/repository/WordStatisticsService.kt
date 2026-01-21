package com.gultekinahmetabdullah.trainvoc.repository

import androidx.room.Transaction
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for word-level statistics management
 *
 * Separated from WordRepository to follow Single Responsibility Principle.
 * Handles statistics tracking, updates, and learning status determination.
 *
 * @property wordDao Database access object for word queries
 * @property statisticDao Database access object for statistic operations
 */
@Singleton
class WordStatisticsService @Inject constructor(
    private val wordDao: WordDao,
    private val statisticDao: StatisticDao
) : IWordStatisticsService {

    /**
     * Check if a word is considered learned
     *
     * A word is learned if correct answers exceed wrong + skipped answers.
     *
     * @param statistic The word's statistics
     * @return true if word is learned, false otherwise
     */
    override fun isLearned(statistic: Statistic): Boolean {
        return statistic.correctCount > (statistic.wrongCount + statistic.skippedCount)
    }

    /**
     * Get statistics for a specific word
     *
     * @param word The word to get statistics for
     * @return The word's statistics
     */
    override suspend fun getWordStats(word: Word): Statistic {
        return statisticDao.getStatisticById(word.statId)
    }

    /**
     * Update word statistics after a quiz answer
     *
     * This handles the complex logic of:
     * 1. Updating learned status
     * 2. Sharing statistics between words with same values
     * 3. Creating new statistics when needed
     * 4. Handling race conditions
     *
     * @param statistic The updated statistics
     * @param word The word to update
     */
    @Transaction
    override suspend fun updateWordStats(statistic: Statistic, word: Word) {
        val updatedStatistic = statistic.copy(learned = isLearned(statistic))
        val wordCount = statisticDao.getWordCountByStatId(word.statId)

        if (wordCount == 1) {
            // Statistic belongs only to this word, update it directly
            statisticDao.updateStatistic(updatedStatistic.copy(statId = word.statId))
        } else {
            // Check if a Statistic with these exact values already exists
            val existingStatistic = wordDao.getStatByValues(
                updatedStatistic.correctCount,
                updatedStatistic.wrongCount,
                updatedStatistic.skippedCount,
                updatedStatistic.learned
            )

            if (existingStatistic != null && existingStatistic.learned == updatedStatistic.learned) {
                // Reuse existing statistic if it's not already assigned to this word
                if (word.statId != existingStatistic.statId) {
                    wordDao.updateWordStatId(existingStatistic.statId, word.word)
                }
            } else {
                // Try to insert new statistic
                val newStatId = statisticDao.insertStatistic(
                    updatedStatistic.copy(statId = 0)
                )

                // Handle race condition: if insert returned -1 (conflict due to UNIQUE constraint)
                if (newStatId == -1L) {
                    // Another thread inserted the same statistic, query for it again
                    val raceConditionStat = wordDao.getStatByValues(
                        updatedStatistic.correctCount,
                        updatedStatistic.wrongCount,
                        updatedStatistic.skippedCount,
                        updatedStatistic.learned
                    )
                    if (raceConditionStat != null) {
                        wordDao.updateWordStatId(raceConditionStat.statId, word.word)
                    }
                } else {
                    // Successfully inserted, use the new stat ID
                    wordDao.updateWordStatId(newStatId.toInt(), word.word)
                }
            }
        }
    }

    /**
     * Update last answered timestamp for a word
     *
     * @param word The word identifier
     */
    override suspend fun updateLastAnswered(word: String) {
        wordDao.updateLastAnsweredTime(word)
    }

    /**
     * Update time spent on a word
     *
     * @param secondsSpent Number of seconds spent
     * @param word The word
     */
    override suspend fun updateSecondsSpent(secondsSpent: Int, word: Word) {
        val currentSecondsSpent = statisticDao.getSecondsSpentByStatId(word.statId)
        statisticDao.updateSecondsSpent(word.statId, currentSecondsSpent + secondsSpent)
    }

    /**
     * Mark a word as learned by its statistic ID
     *
     * @param statId The statistic ID
     */
    override suspend fun markWordAsLearned(statId: Long) {
        statisticDao.markAsLearned(statId)
    }
}
