package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import javax.inject.Inject

/**
 * Lightweight use case for checking if an answer is correct.
 *
 * Unlike SubmitQuizAnswerUseCase, this doesn't update statistics.
 * Ideal for:
 * - Multiplayer games (stats tracked server-side)
 * - Preview/practice mode
 * - Quick validation without side effects
 *
 * Example usage:
 * ```kotlin
 * val result = checkAnswerUseCase(
 *     selectedIndex = 2,
 *     correctIndex = 2,
 *     answerTimeMs = 1500
 * )
 * when (result) {
 *     is AppResult.Success -> {
 *         val checkResult = result.data
 *         if (checkResult.isCorrect) {
 *             showCorrectAnimation()
 *             score += checkResult.calculateScore(30_000) // 30s max
 *         }
 *     }
 * }
 * ```
 */
class CheckAnswerUseCase @Inject constructor() {

    /**
     * Check if the selected answer is correct.
     *
     * @param selectedIndex The index of the user's selected answer
     * @param correctIndex The index of the correct answer
     * @param answerTimeMs Time taken to answer in milliseconds
     * @return AppResult containing the check result
     */
    operator fun invoke(
        selectedIndex: Int,
        correctIndex: Int,
        answerTimeMs: Long
    ): AppResult<CheckResult> {
        val isCorrect = selectedIndex == correctIndex

        return AppResult.Success(
            CheckResult(
                isCorrect = isCorrect,
                selectedIndex = selectedIndex,
                correctIndex = correctIndex,
                answerTimeMs = answerTimeMs
            )
        )
    }

    /**
     * Check answer with string comparison (for word-based quizzes).
     *
     * @param selectedAnswer The user's selected answer text
     * @param correctAnswer The correct answer text
     * @param answerTimeMs Time taken to answer in milliseconds
     * @param ignoreCase Whether to ignore case when comparing (default true)
     * @return AppResult containing the check result
     */
    operator fun invoke(
        selectedAnswer: String,
        correctAnswer: String,
        answerTimeMs: Long,
        ignoreCase: Boolean = true
    ): AppResult<CheckResult> {
        val isCorrect = if (ignoreCase) {
            selectedAnswer.equals(correctAnswer, ignoreCase = true)
        } else {
            selectedAnswer == correctAnswer
        }

        return AppResult.Success(
            CheckResult(
                isCorrect = isCorrect,
                selectedAnswer = selectedAnswer,
                correctAnswer = correctAnswer,
                answerTimeMs = answerTimeMs
            )
        )
    }
}

/**
 * Result of checking an answer.
 *
 * @param isCorrect Whether the answer was correct
 * @param selectedIndex The index selected (if index-based)
 * @param correctIndex The correct index (if index-based)
 * @param selectedAnswer The answer selected (if text-based)
 * @param correctAnswer The correct answer (if text-based)
 * @param answerTimeMs Time taken to answer in milliseconds
 */
data class CheckResult(
    val isCorrect: Boolean,
    val selectedIndex: Int? = null,
    val correctIndex: Int? = null,
    val selectedAnswer: String? = null,
    val correctAnswer: String? = null,
    val answerTimeMs: Long
) {
    /**
     * Calculate score based on answer correctness and time.
     *
     * Scoring algorithm:
     * - Base score for correct answer: 1000 points
     * - Time bonus: Up to 500 points based on speed
     * - Wrong answer: 0 points
     *
     * @param maxTimeMs Maximum time allowed for the question
     * @return Calculated score
     */
    fun calculateScore(maxTimeMs: Long): Int {
        if (!isCorrect) return 0

        val baseScore = 1000
        val maxTimeBonus = 500

        // Calculate time bonus (faster = more points)
        val timeRatio = 1.0 - (answerTimeMs.toDouble() / maxTimeMs).coerceIn(0.0, 1.0)
        val timeBonus = (maxTimeBonus * timeRatio).toInt()

        return baseScore + timeBonus
    }

    /**
     * Calculate multiplayer score (similar to Kahoot-style scoring).
     *
     * @param maxTimeMs Maximum time allowed for the question
     * @param basePoints Base points for correct answer
     * @return Calculated score for multiplayer
     */
    fun calculateMultiplayerScore(maxTimeMs: Long, basePoints: Int = 1000): Int {
        if (!isCorrect) return 0

        // Time-based scoring: faster answers get more points
        val timeRatio = 1.0 - (answerTimeMs.toDouble() / maxTimeMs).coerceIn(0.0, 1.0)
        return (basePoints * (0.5 + 0.5 * timeRatio)).toInt()
    }
}
