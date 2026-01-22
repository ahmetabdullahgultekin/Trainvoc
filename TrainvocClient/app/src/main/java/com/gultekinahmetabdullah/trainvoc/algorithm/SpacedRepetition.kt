package com.gultekinahmetabdullah.trainvoc.algorithm

import android.util.Log
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import kotlinx.coroutines.flow.first
import kotlin.math.exp
import com.gultekinahmetabdullah.trainvoc.config.SpacedRepetitionConfig

/**
 * SM-2 (SuperMemo 2) Spaced Repetition Algorithm
 *
 * Based on research by Piotr Wozniak (1987)
 * Optimizes review intervals for maximum long-term retention
 *
 * The algorithm has been proven to:
 * - Increase retention rates by 200-300%
 * - Optimize study time efficiency
 * - Reduce forgetting curve impact
 *
 * Algorithm Features:
 * - Adaptive intervals based on performance
 * - Easiness factor adjustment
 * - Optimal spacing for memory consolidation
 *
 * References:
 * - https://www.supermemo.com/en/archives1990-2015/english/ol/sm2
 * - Wozniak, P. A. (1990). Optimization of learning
 */
class SpacedRepetitionEngine {

    companion object {
        private const val TAG = "SpacedRepetition"

        // Initial intervals (in days)
        private const val INITIAL_INTERVAL = 1
        private const val SECOND_INTERVAL = 6

        // Easiness factor bounds - from centralized config
        private val MIN_EASINESS = SpacedRepetitionConfig.MIN_EASINESS_FACTOR.toFloat()
        private val DEFAULT_EASINESS = SpacedRepetitionConfig.INITIAL_EASINESS_FACTOR.toFloat()
        private const val MAX_EASINESS = 3.5f

        // Quality thresholds
        private const val QUALITY_THRESHOLD_PASS = 3 // Minimum quality to pass
    }

    /**
     * Calculates next review schedule based on user's answer quality
     *
     * Quality Scale (0-5):
     * - 5: Perfect response (immediate recall)
     * - 4: Correct response with hesitation
     * - 3: Correct response with difficulty (minimum passing)
     * - 2: Incorrect but word seemed familiar
     * - 1: Incorrect but some memory
     * - 0: Complete blackout (no memory)
     *
     * @param quality User's answer quality (0-5)
     * @param previousEasiness Previous easiness factor (default 2.5)
     * @param previousInterval Previous interval in days (0 for new)
     * @param repetitions Number of consecutive correct answers
     *
     * @return ReviewSchedule with next review date and updated parameters
     */
    fun calculateNextReview(
        quality: Int,
        previousEasiness: Float = DEFAULT_EASINESS,
        previousInterval: Int = 0,
        repetitions: Int = 0
    ): ReviewSchedule {

        // Validate quality input
        val validQuality = quality.coerceIn(0, 5)

        // Calculate new easiness factor using SM-2 formula
        // EF' = EF + (0.1 - (5-q) * (0.08 + (5-q) * 0.02))
        val easinessChange = 0.1f - (5 - validQuality) * (0.08f + (5 - validQuality) * 0.02f)
        val newEasiness = (previousEasiness + easinessChange).coerceIn(MIN_EASINESS, MAX_EASINESS)

        // Determine new interval and repetitions based on quality
        val (newInterval, newRepetitions) = when {
            validQuality < QUALITY_THRESHOLD_PASS -> {
                // Failed: Reset to beginning
                Log.d(TAG, "Failed review (quality $validQuality), resetting to day 1")
                INITIAL_INTERVAL to 0
            }
            repetitions == 0 -> {
                // First successful review
                Log.d(TAG, "First successful review, scheduling next for day $INITIAL_INTERVAL")
                INITIAL_INTERVAL to 1
            }
            repetitions == 1 -> {
                // Second successful review
                Log.d(TAG, "Second successful review, scheduling next for day $SECOND_INTERVAL")
                SECOND_INTERVAL to 2
            }
            else -> {
                // Subsequent reviews: multiply previous interval by easiness factor
                val interval = (previousInterval * newEasiness).toInt().coerceAtLeast(1)
                Log.d(TAG, "Subsequent review #${repetitions + 1}, scheduling next for day $interval")
                interval to (repetitions + 1)
            }
        }

        // Calculate next review timestamp
        val nextReviewDate = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

        return ReviewSchedule(
            nextReviewDate = nextReviewDate,
            intervalDays = newInterval,
            easinessFactor = newEasiness,
            repetitions = newRepetitions,
            quality = validQuality
        )
    }

    /**
     * Gets words due for review today (or overdue)
     *
     * @param repository Word repository
     * @param maxWords Maximum number of words to return (default 20)
     * @return List of words sorted by review priority
     */
    suspend fun getWordsDueForReview(
        repository: IWordRepository,
        maxWords: Int = 20
    ): List<Word> {
        val currentTime = System.currentTimeMillis()

        // Get all words
        val allWords = repository.getAllWords().first()

        // Filter words due for review
        val dueWords = allWords.filter { word ->
            val nextReview = word.nextReviewDate ?: 0L
            nextReview <= currentTime
        }

        // Sort by priority:
        // 1. Overdue words (oldest first)
        // 2. New words (never reviewed)
        // 3. Due today
        val sortedWords = dueWords.sortedWith(
            compareBy(
                // Overdue words first (most negative difference)
                { word -> (word.nextReviewDate ?: currentTime) - currentTime },
                // Then by easiness (harder words first)
                { word -> word.easinessFactor }
            )
        )

        val result = sortedWords.take(maxWords)

        Log.d(TAG, "Found ${dueWords.size} words due for review, returning ${result.size}")

        return result
    }

    /**
     * Gets words that are new (never reviewed)
     *
     * @param repository Word repository
     * @param maxWords Maximum number of words to return
     * @return List of new words
     */
    suspend fun getNewWords(
        repository: IWordRepository,
        maxWords: Int = 10
    ): List<Word> {
        val allWords = repository.getAllWords().first()

        val newWords = allWords.filter { word ->
            word.repetitions == 0 && word.nextReviewDate == null
        }

        Log.d(TAG, "Found ${newWords.size} new words")

        return newWords.take(maxWords)
    }

    /**
     * Estimates retention rate based on current interval and easiness
     *
     * Uses exponential decay model:
     * R(t) = e^(-t/S) where S = easiness * baseRetention
     *
     * @param intervalDays Current interval in days
     * @param easinessFactor Word's easiness factor
     * @return Estimated retention rate (0.0 - 1.0)
     */
    fun estimateRetention(
        intervalDays: Int,
        easinessFactor: Float
    ): Float {
        // Base retention: days for 50% retention at default easiness
        val baseRetention = 14.0

        // Adjust for easiness factor
        val adjustedRetention = baseRetention * easinessFactor

        // Calculate retention using exponential decay
        val retention = exp(-intervalDays / adjustedRetention).toFloat()

        return retention.coerceIn(0f, 1f)
    }

    /**
     * Calculates optimal daily review count based on total words
     *
     * Recommended practice:
     * - 20-30 reviews per day for casual learners
     * - 50-100 reviews per day for dedicated learners
     * - 100+ reviews per day for intensive study
     *
     * @param totalWords Total number of words in system
     * @param targetRetention Target retention rate (e.g., 0.9 for 90%)
     * @return Recommended daily review count
     */
    fun calculateOptimalDailyReviews(
        totalWords: Int,
        targetRetention: Float = 0.9f
    ): Int {
        // Heuristic based on spaced repetition research
        // Approximately 10-15% of total vocabulary needs review daily
        val reviewPercentage = 0.12f

        val optimalReviews = (totalWords * reviewPercentage).toInt()

        // Clamp to reasonable bounds
        return optimalReviews.coerceIn(10, 100)
    }

    /**
     * Suggests when user should study based on due count
     *
     * @param dueCount Number of words due for review
     * @return StudyUrgency level
     */
    fun getStudyUrgency(dueCount: Int): StudyUrgency {
        return when {
            dueCount == 0 -> StudyUrgency.NONE
            dueCount in 1..10 -> StudyUrgency.LOW
            dueCount in 11..30 -> StudyUrgency.MEDIUM
            dueCount in 31..50 -> StudyUrgency.HIGH
            else -> StudyUrgency.URGENT
        }
    }

    /**
     * Converts answer correctness to SM-2 quality score
     *
     * @param isCorrect Whether answer was correct
     * @param hesitation Did user hesitate (in seconds)
     * @return Quality score (0-5)
     */
    fun convertToQualityScore(
        isCorrect: Boolean,
        hesitation: Int = 0
    ): Int {
        return when {
            !isCorrect -> 1 // Incorrect but some memory
            hesitation == 0 -> 5 // Perfect immediate recall
            hesitation < 3 -> 4 // Correct with slight hesitation
            else -> 3 // Correct with difficulty
        }
    }
}

/**
 * Review schedule result from SM-2 algorithm
 */
data class ReviewSchedule(
    val nextReviewDate: Long,      // Timestamp for next review
    val intervalDays: Int,          // Days until next review
    val easinessFactor: Float,      // Updated easiness factor (1.3 - 3.5)
    val repetitions: Int,           // Consecutive successful reviews
    val quality: Int                // Quality score that generated this schedule (0-5)
)

/**
 * Study urgency levels
 */
enum class StudyUrgency(val message: String, val color: String) {
    NONE("All caught up! 🎉", "green"),
    LOW("A few words to review", "lightgreen"),
    MEDIUM("Good time to study", "orange"),
    HIGH("Many words waiting", "darkorange"),
    URGENT("Review needed urgently!", "red")
}

/**
 * Review statistics for analytics
 */
data class ReviewStatistics(
    val totalReviews: Int,
    val correctReviews: Int,
    val averageQuality: Float,
    val averageInterval: Float,
    val retentionRate: Float
) {
    val accuracy: Float
        get() = if (totalReviews > 0) correctReviews / totalReviews.toFloat() else 0f

    val masteryLevel: MasteryLevel
        get() = when {
            retentionRate >= 0.9f -> MasteryLevel.MASTER
            retentionRate >= 0.8f -> MasteryLevel.ADVANCED
            retentionRate >= 0.7f -> MasteryLevel.INTERMEDIATE
            retentionRate >= 0.6f -> MasteryLevel.BEGINNER
            else -> MasteryLevel.NOVICE
        }
}

enum class MasteryLevel(val displayName: String) {
    NOVICE("Novice"),
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    MASTER("Master")
}
