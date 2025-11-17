package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for word-related operations.
 * Follows Dependency Inversion Principle (SOLID) for better testability and flexibility.
 *
 * This interface abstracts away the data source implementation,
 * making it easier to:
 * - Write unit tests with mock implementations
 * - Swap data sources (local DB, remote API, cache, etc.)
 * - Follow clean architecture principles
 */
interface IWordRepository {

    // Progress Management
    suspend fun resetProgress()

    // Statistics
    suspend fun getCorrectAnswers(): Int
    suspend fun getWrongAnswers(): Int
    suspend fun getSkippedAnswers(): Int
    suspend fun getTotalTimeSpent(): Int
    suspend fun getLastAnswered(): Long

    // Word Operations
    fun getAllWords(): Flow<List<Word>>
    suspend fun getAllWordsAskedInExams(): List<WordAskedInExams>
    suspend fun insertWord(word: Word)
    suspend fun getWordById(wordId: String): Word
    suspend fun getExamsForWord(wordId: String): List<String>

    // Word Statistics
    fun isLearned(statistic: Statistic): Boolean
    suspend fun updateWordStats(statistic: Statistic, word: Word)
    suspend fun getWordStats(word: Word): Statistic
    suspend fun updateLastAnswered(word: String)
    suspend fun updateSecondsSpent(secondsSpent: Int, word: Word)
    suspend fun markWordAsLearned(statId: Long)

    // Statistic Operations
    suspend fun getWordCountByStatId(statId: Int): Int
    suspend fun getLearnedStatisticByValues(
        correctCount: Int,
        wrongCount: Int,
        skippedCount: Int
    ): Statistic?

    suspend fun updateWordStatId(statId: Int, word: String)
    suspend fun insertStatistic(statistic: Statistic): Long

    // Quiz Generation
    suspend fun generateTenQuestions(
        quizType: QuizType,
        quizParameter: QuizParameter
    ): MutableList<Question>

    // Level Management
    suspend fun isLevelUnlocked(level: WordLevel): Boolean
    suspend fun getWordCountByLevel(level: String): Int
    suspend fun getLearnedWordCount(level: String): Int

    // Exam Management
    suspend fun getWordCountByExam(exam: String): Int
    suspend fun getLearnedWordCountByExam(exam: String): Int

    // Analytics
    suspend fun getTotalQuizCount(): Int
    suspend fun getDailyCorrectAnswers(): Int
    suspend fun getWeeklyCorrectAnswers(): Int
    suspend fun getMostWrongWord(): String?
    suspend fun getBestCategory(): String?
}
