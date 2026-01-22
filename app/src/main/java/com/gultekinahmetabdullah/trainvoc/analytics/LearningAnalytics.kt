package com.gultekinahmetabdullah.trainvoc.analytics

import android.util.Log
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Learning Analytics Engine
 *
 * Provides comprehensive learning statistics and insights:
 * - Progress tracking
 * - Performance metrics
 * - Retention analysis
 * - Goal predictions
 * - Study patterns
 */
class LearningAnalytics(
    private val repository: IWordRepository
) {

    companion object {
        private const val TAG = "LearningAnalytics"
        private const val MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L
    }

    /**
     * Gets comprehensive learning statistics
     *
     * @return LearningStats with all metrics
     */
    suspend fun getAnalytics(): LearningStats {
        try {
            val allWords = repository.getAllWords().first()
            val allStats = repository.getAllStatistics()

            val learnedCount = allStats.count { it.learned }
            val inProgressCount = allWords.count { word ->
                word.repetitions > 0 && !isWordLearned(word, allStats)
            }

            // Review statistics
            val wordsDueToday = getWordsDueToday(allWords)
            val wordsReviewedToday = getWordsReviewedToday(allWords)

            // Performance metrics
            val accuracy = calculateAccuracy(allStats)
            val currentStreak = calculateStreak(allWords)

            // Time statistics
            val totalStudyTime = allWords.sumOf { it.secondsSpent }
            val avgTimePerWord = if (allWords.isNotEmpty()) {
                totalStudyTime / allWords.size
            } else 0

            // Retention calculation
            val retention = calculateAverageRetention(allWords)

            return LearningStats(
                totalWords = allWords.size,
                learnedWords = learnedCount,
                wordsInProgress = inProgressCount,
                wordsDueToday = wordsDueToday.size,
                wordsReviewedToday = wordsReviewedToday.size,
                averageRetention = retention,
                averageAccuracy = accuracy,
                currentStreak = currentStreak,
                longestStreak = getLongestStreak(),
                totalStudyTime = totalStudyTime,
                averageTimePerWord = avgTimePerWord,
                weeklyProgress = getWeeklyProgress(allWords),
                monthlyProgress = getMonthlyProgress(allWords),
                levelDistribution = getLevelDistribution(allWords)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating analytics", e)
            return LearningStats.empty()
        }
    }

    /**
     * Gets progress chart data for visualization
     *
     * @param days Number of days to include
     * @return List of daily progress data points
     */
    suspend fun getProgressChartData(days: Int = 30): List<ChartDataPoint> {
        val allWords = repository.getAllWords().first()
        val calendar = Calendar.getInstance()
        val dataPoints = mutableListOf<ChartDataPoint>()

        for (i in days downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis() - (i * MILLISECONDS_PER_DAY)
            val dayStart = getDayStart(calendar.timeInMillis)
            val dayEnd = dayStart + MILLISECONDS_PER_DAY

            val wordsReviewedThatDay = allWords.count { word ->
                val lastReview = word.lastReviewed ?: 0
                lastReview in dayStart..dayEnd
            }

            dataPoints.add(
                ChartDataPoint(
                    date = dayStart,
                    value = wordsReviewedThatDay,
                    label = formatDate(dayStart)
                )
            )
        }

        return dataPoints
    }

    /**
     * Predicts when user will reach their goal
     *
     * @param targetWords Target number of words to learn
     * @return GoalPrediction with estimated completion
     */
    suspend fun predictGoalCompletion(targetWords: Int): GoalPrediction {
        val allWords = repository.getAllWords().first()
        val allStats = repository.getAllStatistics()

        val currentLearned = allStats.count { it.learned }
        val remainingWords = (targetWords - currentLearned).coerceAtLeast(0)

        // Calculate learning rate (words per day) from last 30 days
        val learningRate = calculateLearningRate(allWords, 30)

        val daysToGoal = if (learningRate > 0) {
            (remainingWords / learningRate).roundToInt()
        } else {
            0
        }

        val targetDate = System.currentTimeMillis() + (daysToGoal * MILLISECONDS_PER_DAY)
        val confidence = calculatePredictionConfidence(learningRate, allWords.size)

        return GoalPrediction(
            targetDate = targetDate,
            estimatedDays = daysToGoal,
            confidence = confidence,
            currentProgress = currentLearned,
            targetProgress = targetWords
        )
    }

    // Private helper functions

    private fun isWordLearned(word: Word, allStats: List<com.gultekinahmetabdullah.trainvoc.classes.word.Statistic>): Boolean {
        val stat = allStats.find { it.statId == word.statId }
        return stat?.learned == true
    }

    private fun getWordsDueToday(words: List<Word>): List<Word> {
        val now = System.currentTimeMillis()
        return words.filter { word ->
            val nextReview = word.nextReviewDate ?: 0
            nextReview <= now && nextReview > 0
        }
    }

    private fun getWordsReviewedToday(words: List<Word>): List<Word> {
        val todayStart = getDayStart(System.currentTimeMillis())
        return words.filter { word ->
            val lastReview = word.lastReviewed ?: 0
            lastReview >= todayStart
        }
    }

    private fun calculateAccuracy(stats: List<com.gultekinahmetabdullah.trainvoc.classes.word.Statistic>): Float {
        val totalAnswers = stats.sumOf { it.correctCount + it.wrongCount }
        val correctAnswers = stats.sumOf { it.correctCount }

        return if (totalAnswers > 0) {
            (correctAnswers.toFloat() / totalAnswers) * 100
        } else 0f
    }

    private fun calculateStreak(words: List<Word>): Int {
        val calendar = Calendar.getInstance()
        var streak = 0

        for (i in 0..365) {
            val dayStart = getDayStart(calendar.timeInMillis)
            val dayEnd = dayStart + MILLISECONDS_PER_DAY

            val reviewedThatDay = words.any { word ->
                val lastReview = word.lastReviewed ?: 0
                lastReview in dayStart..dayEnd
            }

            if (reviewedThatDay) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    private fun getLongestStreak(): Int {
        // Calculate longest streak from historical word review data
        // This provides analytics-based longest streak calculation
        // Note: GamificationDao also tracks longestStreak in StreakTracking table

        return try {
            // Get all word reviews as a historical activity log
            val allWords = kotlinx.coroutines.runBlocking {
                repository.getAllWords().first()
            }

            val reviewDates = mutableSetOf<Long>()

            // Collect all unique review dates
            allWords.forEach { word ->
                val lastReview = word.lastReviewed ?: 0
                if (lastReview > 0) {
                    reviewDates.add(getDayStart(lastReview))
                }
            }

            if (reviewDates.isEmpty()) return 0

            // Sort dates chronologically
            val sortedDates = reviewDates.sorted()

            var longestStreak = 1
            var currentStreak = 1

            // Calculate longest consecutive streak
            for (i in 1 until sortedDates.size) {
                val dayDiff = (sortedDates[i] - sortedDates[i - 1]) / MILLISECONDS_PER_DAY

                if (dayDiff == 1L) {
                    // Consecutive day
                    currentStreak++
                    longestStreak = maxOf(longestStreak, currentStreak)
                } else {
                    // Streak broken, reset
                    currentStreak = 1
                }
            }

            longestStreak
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating longest streak: ${e.message}")
            0
        }
    }

    private fun calculateAverageRetention(words: List<Word>): Float {
        val wordsWithReviews = words.filter { it.repetitions > 0 }

        if (wordsWithReviews.isEmpty()) return 0f

        val totalRetention = wordsWithReviews.sumOf { word ->
            // Simple retention estimate based on repetitions and interval
            val baseRetention = 0.7 // 70% base retention
            val repetitionBonus = word.repetitions * 0.05 // 5% per repetition
            (baseRetention + repetitionBonus).coerceAtMost(0.95)
        }

        return (totalRetention / wordsWithReviews.size).toFloat() * 100
    }

    private fun getWeeklyProgress(words: List<Word>): Int {
        val weekStart = System.currentTimeMillis() - (7 * MILLISECONDS_PER_DAY)
        return words.count { word ->
            val lastReview = word.lastReviewed ?: 0
            lastReview >= weekStart
        }
    }

    private fun getMonthlyProgress(words: List<Word>): Int {
        val monthStart = System.currentTimeMillis() - (30 * MILLISECONDS_PER_DAY)
        return words.count { word ->
            val lastReview = word.lastReviewed ?: 0
            lastReview >= monthStart
        }
    }

    private fun getLevelDistribution(words: List<Word>): Map<WordLevel, Int> {
        return words.groupBy { it.level ?: WordLevel.A1 }
            .mapValues { it.value.size }
    }

    private fun calculateLearningRate(words: List<Word>, days: Int): Float {
        val startDate = System.currentTimeMillis() - (days * MILLISECONDS_PER_DAY)
        val recentlyLearned = words.count { word ->
            val lastReview = word.lastReviewed ?: 0
            lastReview >= startDate && word.repetitions >= 3
        }

        return recentlyLearned.toFloat() / days
    }

    private fun calculatePredictionConfidence(rate: Float, totalWords: Int): Float {
        // Confidence based on:
        // 1. Consistency of learning rate
        // 2. Amount of historical data
        val dataConfidence = (totalWords / 1000f).coerceAtMost(1f)
        val rateConfidence = if (rate > 0) 0.8f else 0.2f

        return ((dataConfidence + rateConfidence) / 2) * 100
    }

    private fun getDayStart(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
}

/**
 * Comprehensive learning statistics
 */
data class LearningStats(
    val totalWords: Int,
    val learnedWords: Int,
    val wordsInProgress: Int,
    val wordsDueToday: Int,
    val wordsReviewedToday: Int,
    val averageRetention: Float,
    val averageAccuracy: Float,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalStudyTime: Int,
    val averageTimePerWord: Int,
    val weeklyProgress: Int,
    val monthlyProgress: Int,
    val levelDistribution: Map<WordLevel, Int>
) {
    companion object {
        fun empty() = LearningStats(
            totalWords = 0,
            learnedWords = 0,
            wordsInProgress = 0,
            wordsDueToday = 0,
            wordsReviewedToday = 0,
            averageRetention = 0f,
            averageAccuracy = 0f,
            currentStreak = 0,
            longestStreak = 0,
            totalStudyTime = 0,
            averageTimePerWord = 0,
            weeklyProgress = 0,
            monthlyProgress = 0,
            levelDistribution = emptyMap()
        )
    }
}

/**
 * Data point for progress charts
 */
data class ChartDataPoint(
    val date: Long,
    val value: Int,
    val label: String
)

/**
 * Goal completion prediction
 */
data class GoalPrediction(
    val targetDate: Long,
    val estimatedDays: Int,
    val confidence: Float,
    val currentProgress: Int,
    val targetProgress: Int
) {
    val progressPercentage: Float
        get() = if (targetProgress > 0) {
            (currentProgress.toFloat() / targetProgress) * 100
        } else 0f
}
