package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for core word data operations.
 * Follows Interface Segregation Principle - now focused only on word CRUD.
 *
 * Related interfaces:
 * - IQuizService: Quiz generation
 * - IWordStatisticsService: Word-level statistics
 * - IProgressService: Progress and level management
 * - IAnalyticsService: Aggregated analytics
 *
 * This interface abstracts away the data source implementation,
 * making it easier to:
 * - Write unit tests with mock implementations
 * - Swap data sources (local DB, remote API, cache, etc.)
 * - Follow clean architecture principles
 */
interface IWordRepository {
    // Word CRUD Operations
    fun getAllWords(): Flow<List<Word>>
    suspend fun getAllWordsAskedInExams(): List<WordAskedInExams>
    suspend fun insertWord(word: Word)
    suspend fun getWordById(wordId: String): Word
    suspend fun getExamsForWord(wordId: String): List<String>

    // Statistic Operations
    suspend fun getAllStatistics(): List<Statistic>
    suspend fun getWordCountByStatId(statId: Int): Int
    suspend fun getLearnedStatisticByValues(
        correctCount: Int,
        wrongCount: Int,
        skippedCount: Int
    ): Statistic?
    suspend fun updateWordStatId(statId: Int, word: String)
    suspend fun insertStatistic(statistic: Statistic): Long

    // Progress Management
    suspend fun resetProgress()
}
