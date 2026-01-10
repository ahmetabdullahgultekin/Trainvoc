package com.gultekinahmetabdullah.trainvoc.algorithm

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.analytics.LearningStats
import kotlin.math.roundToInt

/**
 * Adaptive Difficulty Engine
 *
 * Dynamically adjusts quiz difficulty based on user performance to maintain
 * optimal learning challenge. Uses performance metrics to prevent boredom
 * (too easy) and frustration (too hard).
 *
 * Algorithm:
 * - Tracks recent performance (last 10-20 quizzes)
 * - Calculates accuracy, speed, and consistency
 * - Adjusts difficulty level automatically
 * - Provides optimal quiz parameter suggestions
 *
 * Difficulty Zones:
 * - 90-100% accuracy: TOO EASY → Increase difficulty
 * - 70-90% accuracy: OPTIMAL → Maintain difficulty
 * - 50-70% accuracy: CHALLENGING → Slight decrease
 * - Below 50%: TOO HARD → Decrease difficulty
 *
 * Benefits:
 * - Maintains engagement (Goldilocks zone)
 * - Prevents plateau effect
 * - Accelerates learning progression
 * - Personalized learning path
 *
 * Usage:
 * ```kotlin
 * val engine = AdaptiveDifficultyEngine()
 *
 * // Get difficulty adjustment
 * val adjustment = engine.calculateDifficultyAdjustment(recentQuizzes)
 *
 * // Get suggested quiz parameters
 * val suggestion = engine.suggestQuizParameters(learningStats, currentLevel)
 * ```
 */
class AdaptiveDifficultyEngine {

    companion object {
        // Accuracy thresholds
        private const val ACCURACY_TOO_HIGH = 0.90f  // 90%+: Increase difficulty
        private const val ACCURACY_OPTIMAL_HIGH = 0.70f  // 70-90%: Optimal
        private const val ACCURACY_OPTIMAL_LOW = 0.50f   // 50-70%: Challenging
        // Below 50%: Too hard, decrease difficulty

        // Speed thresholds (seconds per question)
        private const val SPEED_FAST = 5  // Very quick answers
        private const val SPEED_NORMAL = 10  // Normal thinking time
        private const val SPEED_SLOW = 20  // Taking time to think

        // Consistency threshold (standard deviation)
        private const val CONSISTENCY_HIGH = 0.15f  // Very consistent
        private const val CONSISTENCY_MEDIUM = 0.30f  // Somewhat consistent

        // Performance tracking window
        private const val RECENT_QUIZ_WINDOW = 10  // Last 10 quizzes
    }

    /**
     * Calculate difficulty adjustment based on recent performance
     *
     * Analyzes recent quiz results and determines whether difficulty
     * should be increased, decreased, or maintained.
     *
     * @param recentQuizzes List of recent quiz results (newest first)
     * @return DifficultyAdjustment with recommendation and reasoning
     */
    fun calculateDifficultyAdjustment(
        recentQuizzes: List<QuizResult>
    ): DifficultyAdjustment {
        if (recentQuizzes.isEmpty()) {
            return DifficultyAdjustment(
                adjustment = DifficultyChange.MAINTAIN,
                confidence = 0.0f,
                reason = "No quiz history available"
            )
        }

        // Take only recent quizzes
        val recentWindow = recentQuizzes.take(RECENT_QUIZ_WINDOW)

        // Calculate metrics
        val avgAccuracy = recentWindow.map { it.accuracy }.average().toFloat()
        val avgSpeed = recentWindow.map { it.averageTimePerQuestion }.average()
        val consistency = calculateConsistency(recentWindow.map { it.accuracy })

        // Determine adjustment
        val (adjustment, reason) = when {
            avgAccuracy >= ACCURACY_TOO_HIGH -> {
                // Too easy: increase difficulty
                if (avgSpeed < SPEED_FAST && consistency < CONSISTENCY_HIGH) {
                    DifficultyChange.INCREASE to
                            "High accuracy (${"%.1f".format(avgAccuracy * 100)}%) with fast responses. Ready for harder challenges!"
                } else {
                    DifficultyChange.SLIGHT_INCREASE to
                            "High accuracy (${"%.1f".format(avgAccuracy * 100)}%), but responses could be faster. Slight increase recommended."
                }
            }

            avgAccuracy >= ACCURACY_OPTIMAL_HIGH -> {
                // Optimal zone: maintain or slight increase
                if (consistency < CONSISTENCY_HIGH) {
                    DifficultyChange.MAINTAIN to
                            "Optimal performance (${"%.1f".format(avgAccuracy * 100)}% accuracy). Current difficulty is perfect!"
                } else {
                    DifficultyChange.MAINTAIN to
                            "Good accuracy but inconsistent performance. Keep current difficulty to build consistency."
                }
            }

            avgAccuracy >= ACCURACY_OPTIMAL_LOW -> {
                // Challenging but manageable
                if (avgSpeed > SPEED_SLOW) {
                    DifficultyChange.MAINTAIN to
                            "Accuracy is ${"%.1f".format(avgAccuracy * 100)}%, taking time to think. Maintain current level."
                } else {
                    DifficultyChange.SLIGHT_DECREASE to
                            "Accuracy at ${"%.1f".format(avgAccuracy * 100)}%. Slight decrease will improve confidence."
                }
            }

            else -> {
                // Too hard: decrease difficulty
                DifficultyChange.DECREASE to
                        "Accuracy below 50% (${"%.1f".format(avgAccuracy * 100)}%). Reducing difficulty to build confidence."
            }
        }

        // Calculate confidence in recommendation
        val confidence = calculateAdjustmentConfidence(recentWindow.size, consistency)

        return DifficultyAdjustment(
            adjustment = adjustment,
            confidence = confidence,
            reason = reason,
            metrics = PerformanceMetrics(
                accuracy = avgAccuracy,
                averageSpeed = avgSpeed,
                consistency = consistency,
                sampleSize = recentWindow.size
            )
        )
    }

    /**
     * Suggest optimal quiz parameters based on learning stats and current level
     *
     * Analyzes comprehensive learning statistics to recommend:
     * - Optimal quiz type
     * - Difficulty level
     * - Number of questions
     * - Focus areas
     *
     * @param stats Comprehensive learning statistics
     * @param currentLevel User's current learning level
     * @return QuizSuggestion with recommendations
     */
    fun suggestQuizParameters(
        stats: LearningStats,
        currentLevel: WordLevel
    ): QuizSuggestion {
        // Determine quiz type based on learning state
        val suggestedType = when {
            stats.wordsDueToday > 20 -> {
                // Many due words: prioritize review
                Quiz.Random to "You have ${stats.wordsDueToday} words due for review"
            }
            stats.learnedWords < 50 -> {
                // Still learning basics: focus on fundamentals
                Quiz.Random to "Building your foundation with varied practice"
            }
            stats.averageAccuracy > 0.80f -> {
                // High accuracy: mix it up
                Quiz.Random to "High accuracy! Try mixing different question types"
            }
            else -> {
                // Default: random for variety
                Quiz.Random to "Balanced practice with varied questions"
            }
        }

        // Determine optimal level
        val suggestedLevel = determineOptimalLevel(stats, currentLevel)

        // Determine question count
        val questionCount = when {
            stats.averageTimePerWord > 15 -> 5  // Taking time: fewer questions
            stats.currentStreak > 7 -> 15  // On a streak: more questions
            else -> 10  // Default
        }

        // Determine focus areas
        val focusAreas = determineFocusAreas(stats)

        return QuizSuggestion(
            quizType = suggestedType.first,
            quizTypeReason = suggestedType.second,
            level = suggestedLevel.first,
            levelReason = suggestedLevel.second,
            questionCount = questionCount,
            focusAreas = focusAreas,
            estimatedDuration = (questionCount * stats.averageTimePerWord) / 60  // minutes
        )
    }

    /**
     * Determine optimal learning level based on performance
     */
    private fun determineOptimalLevel(
        stats: LearningStats,
        currentLevel: WordLevel
    ): Pair<WordLevel, String> {
        return when {
            stats.learnedWords < 100 -> {
                // Still building basics
                WordLevel.A1 to "Focus on fundamentals (A1)"
            }
            stats.averageAccuracy > 0.85f && stats.learnedWords > 200 -> {
                // Ready for higher level
                val nextLevel = currentLevel.next()
                nextLevel to "Strong performance! Ready for $nextLevel"
            }
            stats.averageAccuracy < 0.60f -> {
                // Struggling: go back to previous level
                val prevLevel = currentLevel.previous()
                prevLevel to "Building confidence with $prevLevel words"
            }
            else -> {
                // Maintain current level
                currentLevel to "Current level ($currentLevel) is optimal"
            }
        }
    }

    /**
     * Determine areas needing focus
     */
    private fun determineFocusAreas(stats: LearningStats): List<String> {
        val areas = mutableListOf<String>()

        if (stats.wordsDueToday > 10) {
            areas.add("Review due words (${stats.wordsDueToday} waiting)")
        }

        if (stats.averageAccuracy < 0.70f) {
            areas.add("Accuracy improvement (current: ${"%.1f".format(stats.averageAccuracy * 100)}%)")
        }

        if (stats.averageTimePerWord > 15) {
            areas.add("Speed practice (avg: ${stats.averageTimePerWord}s per word)")
        }

        if (stats.currentStreak == 0 && stats.longestStreak > 0) {
            areas.add("Rebuild streak (previous best: ${stats.longestStreak} days)")
        }

        if (areas.isEmpty()) {
            areas.add("Balanced practice across all skills")
        }

        return areas
    }

    /**
     * Calculate performance consistency (standard deviation of accuracy)
     */
    private fun calculateConsistency(accuracies: List<Float>): Float {
        if (accuracies.size < 2) return 0f

        val mean = accuracies.average()
        val variance = accuracies.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance).toFloat()
    }

    /**
     * Calculate confidence in adjustment recommendation
     *
     * Higher confidence with:
     * - More data points
     * - More consistent performance
     */
    private fun calculateAdjustmentConfidence(sampleSize: Int, consistency: Float): Float {
        val sizeComponent = (sampleSize.toFloat() / RECENT_QUIZ_WINDOW).coerceIn(0f, 1f)
        val consistencyComponent = when {
            consistency < CONSISTENCY_HIGH -> 1.0f
            consistency < CONSISTENCY_MEDIUM -> 0.7f
            else -> 0.4f
        }

        return (sizeComponent * 0.6f + consistencyComponent * 0.4f)
    }

    /**
     * Get next difficulty level (helper for level progression)
     */
    private fun WordLevel.next(): WordLevel {
        return when (this) {
            WordLevel.A1 -> WordLevel.A2
            WordLevel.A2 -> WordLevel.B1
            WordLevel.B1 -> WordLevel.B2
            WordLevel.B2 -> WordLevel.C1
            WordLevel.C1 -> WordLevel.C2
            WordLevel.C2 -> WordLevel.C2  // Max level
        }
    }

    /**
     * Get previous difficulty level (helper for level regression)
     */
    private fun WordLevel.previous(): WordLevel {
        return when (this) {
            WordLevel.A1 -> WordLevel.A1  // Min level
            WordLevel.A2 -> WordLevel.A1
            WordLevel.B1 -> WordLevel.A2
            WordLevel.B2 -> WordLevel.B1
            WordLevel.C1 -> WordLevel.B2
            WordLevel.C2 -> WordLevel.C1
        }
    }
}

/**
 * Quiz result data for adaptive difficulty
 */
data class QuizResult(
    val accuracy: Float,  // 0.0 - 1.0
    val correctCount: Int,
    val totalQuestions: Int,
    val averageTimePerQuestion: Int,  // seconds
    val level: WordLevel,
    val timestamp: Long
)

/**
 * Difficulty adjustment recommendation
 */
data class DifficultyAdjustment(
    val adjustment: DifficultyChange,
    val confidence: Float,  // 0.0 - 1.0
    val reason: String,
    val metrics: PerformanceMetrics? = null
)

/**
 * Difficulty change recommendation
 */
enum class DifficultyChange(val description: String) {
    DECREASE("Decrease significantly"),
    SLIGHT_DECREASE("Decrease slightly"),
    MAINTAIN("Maintain current level"),
    SLIGHT_INCREASE("Increase slightly"),
    INCREASE("Increase significantly")
}

/**
 * Performance metrics for analysis
 */
data class PerformanceMetrics(
    val accuracy: Float,
    val averageSpeed: Double,  // seconds
    val consistency: Float,  // lower is better
    val sampleSize: Int
)

/**
 * Quiz parameter suggestion
 */
data class QuizSuggestion(
    val quizType: Quiz,
    val quizTypeReason: String,
    val level: WordLevel,
    val levelReason: String,
    val questionCount: Int,
    val focusAreas: List<String>,
    val estimatedDuration: Int  // minutes
)
