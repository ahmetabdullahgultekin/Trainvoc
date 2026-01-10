package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

/**
 * Interface for word-level statistics management.
 * Handles individual word performance tracking and updates.
 * Follows Interface Segregation Principle.
 */
interface IWordStatisticsService {
    /**
     * Check if a word is considered learned based on its statistics.
     */
    fun isLearned(statistic: Statistic): Boolean

    /**
     * Get statistics for a specific word.
     */
    suspend fun getWordStats(word: Word): Statistic

    /**
     * Update word statistics after quiz answer.
     */
    suspend fun updateWordStats(statistic: Statistic, word: Word)

    /**
     * Update last answered timestamp for a word.
     */
    suspend fun updateLastAnswered(word: String)

    /**
     * Update time spent on a word.
     */
    suspend fun updateSecondsSpent(secondsSpent: Int, word: Word)

    /**
     * Mark a word as learned by its stat ID.
     */
    suspend fun markWordAsLearned(statId: Long)
}
