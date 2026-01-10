package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel

/**
 * Interface for user progress and level management.
 * Handles progress tracking across levels and exams.
 * Follows Interface Segregation Principle.
 */
interface IProgressService {
    /**
     * Check if a level is unlocked for the user.
     */
    suspend fun isLevelUnlocked(level: WordLevel): Boolean

    /**
     * Get total word count for a level.
     */
    suspend fun getWordCountByLevel(level: String): Int

    /**
     * Get learned word count for a level.
     */
    suspend fun getLearnedWordCount(level: String): Int

    /**
     * Get total word count for an exam.
     */
    suspend fun getWordCountByExam(exam: String): Int

    /**
     * Get learned word count for an exam.
     */
    suspend fun getLearnedWordCountByExam(exam: String): Int
}
