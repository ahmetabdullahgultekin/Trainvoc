package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for submitting and scoring a quiz answer
 *
 * Extracts complex business logic from QuizViewModel. Handles:
 * - Determining if answer is correct
 * - Updating word statistics
 * - Calculating score increments
 * - Managing learned status
 *
 * Example usage in ViewModel:
 * ```kotlin
 * val result = submitQuizAnswerUseCase(
 *     selectedWord = userSelection,
 *     correctWord = question.correctAnswer,
 *     currentStatistic = currentStats
 * )
 * when (result) {
 *     is AppResult.Success -> {
 *         score += result.data.scoreIncrement
 *         updateUI(result.data.isCorrect)
 *     }
 *     is AppResult.Error -> showError(result.error)
 * }
 * ```
 */
class SubmitQuizAnswerUseCase @Inject constructor(
    private val wordStatisticsService: IWordStatisticsService,
    private val dispatchers: DispatcherProvider
) {
    /**
     * Submit a quiz answer and update statistics
     *
     * @param selectedWord The word selected by the user (null for skipped)
     * @param correctWord The correct answer word
     * @param currentStatistic Current statistics for the word
     * @return AppResult containing answer result details
     */
    suspend operator fun invoke(
        selectedWord: Word?,
        correctWord: Word,
        currentStatistic: Statistic
    ): AppResult<AnswerResult> = withContext(dispatchers.io) {
        try {
            val result = when {
                selectedWord == null -> {
                    // Skipped question
                    val updatedStats = currentStatistic.copy(
                        skippedCount = currentStatistic.skippedCount + 1
                    )
                    wordStatisticsService.updateWordStats(updatedStats, correctWord)
                    wordStatisticsService.updateLastAnswered(correctWord.word)

                    AnswerResult(
                        isCorrect = false,
                        isSkipped = true,
                        scoreIncrement = 0,
                        newStatistic = updatedStats
                    )
                }

                selectedWord.word == correctWord.word -> {
                    // Correct answer
                    val updatedStats = currentStatistic.copy(
                        correctCount = currentStatistic.correctCount + 1
                    )
                    wordStatisticsService.updateWordStats(updatedStats, correctWord)
                    wordStatisticsService.updateLastAnswered(correctWord.word)

                    AnswerResult(
                        isCorrect = true,
                        isSkipped = false,
                        scoreIncrement = 10,
                        newStatistic = updatedStats
                    )
                }

                else -> {
                    // Wrong answer
                    val updatedStats = currentStatistic.copy(
                        wrongCount = currentStatistic.wrongCount + 1
                    )
                    wordStatisticsService.updateWordStats(updatedStats, correctWord)
                    wordStatisticsService.updateLastAnswered(correctWord.word)

                    AnswerResult(
                        isCorrect = false,
                        isSkipped = false,
                        scoreIncrement = 0,
                        newStatistic = updatedStats
                    )
                }
            }

            AppResult.Success(result)
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Database(
                    message = "Failed to submit answer: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

/**
 * Result of submitting a quiz answer
 *
 * Contains all information needed to update UI and track progress.
 */
data class AnswerResult(
    val isCorrect: Boolean,
    val isSkipped: Boolean,
    val scoreIncrement: Int,
    val newStatistic: Statistic
)
