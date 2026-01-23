package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.AnswerRequest
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.MultiplayerApi
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for submitting answers in multiplayer games.
 *
 * Handles the server communication for answer submission,
 * including proper error handling and response parsing.
 *
 * Key differences from single-player SubmitQuizAnswerUseCase:
 * - Answers are validated server-side (prevents cheating)
 * - Score is calculated server-side with time-based bonuses
 * - Updates are broadcast to all players via WebSocket
 *
 * Example usage:
 * ```kotlin
 * val result = submitMultiplayerAnswerUseCase(
 *     roomCode = "ABC123",
 *     playerId = "player-1",
 *     selectedIndex = 2,
 *     selectedAnswer = "merhaba",
 *     answerTimeMs = 1500
 * )
 * when (result) {
 *     is AppResult.Success -> {
 *         val answerResult = result.data
 *         showAnswerFeedback(answerResult.isCorrect)
 *         updateScore(answerResult.score)
 *     }
 *     is AppResult.Error -> showError(result.error.getUserMessage())
 * }
 * ```
 */
class SubmitMultiplayerAnswerUseCase @Inject constructor(
    private val multiplayerApi: MultiplayerApi,
    private val dispatchers: DispatcherProvider
) {
    /**
     * Submit an answer to the multiplayer server.
     *
     * @param roomCode The room code
     * @param playerId The player's ID
     * @param selectedIndex The index of the selected answer
     * @param selectedAnswer The text of the selected answer
     * @param answerTimeMs Time taken to answer in milliseconds
     * @return AppResult containing the server's response
     */
    suspend operator fun invoke(
        roomCode: String,
        playerId: String,
        selectedIndex: Int,
        selectedAnswer: String,
        answerTimeMs: Long
    ): AppResult<MultiplayerAnswerResult> = withContext(dispatchers.io) {
        try {
            // Validate inputs
            if (roomCode.isBlank()) {
                return@withContext AppResult.Error(
                    AppError.Validation("Room code is required")
                )
            }
            if (playerId.isBlank()) {
                return@withContext AppResult.Error(
                    AppError.Validation("Player ID is required")
                )
            }
            if (answerTimeMs < 0) {
                return@withContext AppResult.Error(
                    AppError.Validation("Invalid answer time")
                )
            }

            // Submit answer to server
            val response = multiplayerApi.submitAnswer(
                AnswerRequest(
                    roomCode = roomCode,
                    playerId = playerId,
                    answer = selectedAnswer,
                    answerTime = answerTimeMs,
                    isCorrect = false, // Server determines correctness
                    optionPickRate = 0.0 // Server tracks this
                )
            )

            if (!response.success) {
                return@withContext AppResult.Error(
                    AppError.Validation(response.message ?: "Failed to submit answer")
                )
            }

            AppResult.Success(
                MultiplayerAnswerResult(
                    isCorrect = response.correct ?: false,
                    score = response.score ?: 0,
                    serverMessage = response.message,
                    selectedIndex = selectedIndex,
                    answerTimeMs = answerTimeMs
                )
            )
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to submit answer: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Submit a skipped answer (timed out or skipped by user).
     *
     * @param roomCode The room code
     * @param playerId The player's ID
     * @param questionTimeMs Total time allowed for the question
     * @return AppResult indicating the skip was recorded
     */
    suspend fun submitSkip(
        roomCode: String,
        playerId: String,
        questionTimeMs: Long
    ): AppResult<MultiplayerAnswerResult> = withContext(dispatchers.io) {
        try {
            val response = multiplayerApi.submitAnswer(
                AnswerRequest(
                    roomCode = roomCode,
                    playerId = playerId,
                    answer = "", // Empty answer indicates skip
                    answerTime = questionTimeMs, // Full time elapsed
                    isCorrect = false,
                    optionPickRate = 0.0
                )
            )

            AppResult.Success(
                MultiplayerAnswerResult(
                    isCorrect = false,
                    score = 0,
                    serverMessage = "Question skipped",
                    selectedIndex = -1,
                    answerTimeMs = questionTimeMs,
                    wasSkipped = true
                )
            )
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to record skip: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

/**
 * Result of submitting a multiplayer answer.
 *
 * @param isCorrect Whether the answer was correct
 * @param score Points earned for this answer
 * @param serverMessage Optional message from server
 * @param selectedIndex The index that was selected
 * @param answerTimeMs Time taken to answer
 * @param wasSkipped Whether the question was skipped
 */
data class MultiplayerAnswerResult(
    val isCorrect: Boolean,
    val score: Int,
    val serverMessage: String?,
    val selectedIndex: Int,
    val answerTimeMs: Long,
    val wasSkipped: Boolean = false
) {
    /**
     * Get feedback text for the answer.
     */
    fun getFeedbackText(): String = when {
        wasSkipped -> "Time's up!"
        isCorrect -> "Correct! +$score points"
        else -> "Wrong answer"
    }

    /**
     * Get appropriate emoji for feedback.
     */
    fun getFeedbackEmoji(): String = when {
        wasSkipped -> "⏰"
        isCorrect -> "✅"
        else -> "❌"
    }
}
