package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for user progress and level management
 *
 * Separated from WordRepository to follow Single Responsibility Principle.
 * Handles progress tracking across levels and exams.
 *
 * @property wordDao Database access object for word queries
 * @property statisticDao Database access object for statistic operations
 */
@Singleton
class ProgressService @Inject constructor(
    private val wordDao: WordDao,
    private val statisticDao: StatisticDao
) : IProgressService {

    /**
     * Check if a level is unlocked for the user
     *
     * Level progression logic:
     * - A1 is always unlocked
     * - Each subsequent level requires 80% mastery of previous level
     *
     * @param level The word level to check
     * @return true if level is unlocked, false otherwise
     */
    override suspend fun isLevelUnlocked(level: WordLevel): Boolean {
        // A1 is always unlocked
        if (level == WordLevel.A1) return true

        // Get previous level
        val previousLevel = when (level) {
            WordLevel.A2 -> WordLevel.A1
            WordLevel.B1 -> WordLevel.A2
            WordLevel.B2 -> WordLevel.B1
            WordLevel.C1 -> WordLevel.B2
            WordLevel.C2 -> WordLevel.C1
            else -> return true
        }

        // Check if previous level is 80% mastered
        val totalWords = getWordCountByLevel(previousLevel.name)
        if (totalWords == 0) return false

        val learnedWords = getLearnedWordCount(previousLevel.name)
        val masteryPercentage = (learnedWords.toFloat() / totalWords.toFloat()) * 100

        return masteryPercentage >= 80.0f
    }

    /**
     * Get total word count for a level
     *
     * @param level The level name (A1, A2, etc.)
     * @return Total number of words in this level
     */
    override suspend fun getWordCountByLevel(level: String): Int {
        return wordDao.getWordCountByLevel(level)
    }

    /**
     * Get learned word count for a level
     *
     * @param level The level name (A1, A2, etc.)
     * @return Number of learned words in this level
     */
    override suspend fun getLearnedWordCount(level: String): Int {
        return wordDao.getLearnedWordCountByLevel(level)
    }

    /**
     * Get total word count for an exam
     *
     * @param exam The exam abbreviation (YDS, YKS, etc.)
     * @return Total number of words for this exam
     */
    override suspend fun getWordCountByExam(exam: String): Int {
        return wordDao.getWordCountByExam(exam)
    }

    /**
     * Get learned word count for an exam
     *
     * @param exam The exam abbreviation (YDS, YKS, etc.)
     * @return Number of learned words for this exam
     */
    override suspend fun getLearnedWordCountByExam(exam: String): Int {
        return wordDao.getLearnedWordCountByExam(exam)
    }

    /**
     * Get count of mastered words (learned with 5+ correct answers)
     */
    override suspend fun getMasteredWordCount(): Int {
        return wordDao.getMasteredWordCount()
    }

    /**
     * Get count of words being learned (practiced but not mastered)
     */
    override suspend fun getLearningWordCount(): Int {
        return wordDao.getLearningWordCount()
    }

    /**
     * Get count of struggling words (more wrong than correct)
     */
    override suspend fun getStrugglingWordCount(): Int {
        return wordDao.getStrugglingWordCount()
    }

    /**
     * Get count of words not started yet
     */
    override suspend fun getNotStartedWordCount(): Int {
        return wordDao.getNotStartedWordCount()
    }

    /**
     * Get count of words due for review by end of day
     */
    override suspend fun getWordsToReviewByDate(endOfDay: Long): Int {
        return wordDao.getWordsToReviewByDate(endOfDay)
    }

    /**
     * Get count of words to review within a date range
     */
    override suspend fun getWordsToReviewInRange(startDate: Long, endDate: Long): Int {
        return wordDao.getWordsToReviewInRange(startDate, endDate)
    }
}
