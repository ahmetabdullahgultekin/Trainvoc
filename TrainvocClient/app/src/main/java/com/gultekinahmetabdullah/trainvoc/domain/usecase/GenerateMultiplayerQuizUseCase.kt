package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.MultiplayerApi
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.QuestionInfo
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for fetching multiplayer quiz questions from the server.
 *
 * Unlike GenerateQuizQuestionsUseCase which generates questions locally,
 * this fetches questions from the backend API for synchronized multiplayer games.
 *
 * Features:
 * - Server-synchronized questions ensure all players see same content
 * - Supports room-specific question configurations
 * - Handles network errors gracefully
 *
 * Example usage:
 * ```kotlin
 * val result = generateMultiplayerQuizUseCase(
 *     roomCode = "ABC123",
 *     playerId = "player-1"
 * )
 * when (result) {
 *     is AppResult.Success -> {
 *         val state = result.data
 *         loadQuestion(state.currentQuestion)
 *         startTimer(state.remainingTime)
 *     }
 *     is AppResult.Error -> showError(result.error.getUserMessage())
 * }
 * ```
 */
class GenerateMultiplayerQuizUseCase @Inject constructor(
    private val multiplayerApi: MultiplayerApi,
    private val dispatchers: DispatcherProvider
) {
    /**
     * Fetch current game state including the current question.
     *
     * @param roomCode The room code to fetch questions for
     * @param playerId The current player's ID
     * @return AppResult containing the game state with question
     */
    suspend operator fun invoke(
        roomCode: String,
        playerId: String
    ): AppResult<MultiplayerGameState> = withContext(dispatchers.io) {
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

            // Fetch game state from server
            val response = multiplayerApi.getGameState(roomCode, playerId)

            // Convert response to domain model
            val gameState = MultiplayerGameState(
                state = response.state,
                remainingTime = response.remainingTime,
                players = response.players.map { player ->
                    MultiplayerPlayer(
                        id = player.id,
                        name = player.name,
                        avatarId = player.avatarId,
                        score = player.score,
                        correctCount = player.correctCount,
                        wrongCount = player.wrongCount
                    )
                },
                currentQuestion = response.currentQuestion?.let { question ->
                    MultiplayerQuestion(
                        text = question.text,
                        options = question.options,
                        index = question.index
                    )
                }
            )

            AppResult.Success(gameState)
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to fetch game state: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Fetch only the current question (lighter request).
     *
     * @param roomCode The room code
     * @param playerId The player ID
     * @return AppResult containing just the current question
     */
    suspend fun fetchCurrentQuestion(
        roomCode: String,
        playerId: String
    ): AppResult<MultiplayerQuestion?> = withContext(dispatchers.io) {
        try {
            val response = multiplayerApi.getGameState(roomCode, playerId)

            val question = response.currentQuestion?.let {
                MultiplayerQuestion(
                    text = it.text,
                    options = it.options,
                    index = it.index
                )
            }

            AppResult.Success(question)
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to fetch question: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Fetch simplified game state (just state and time).
     *
     * @param roomCode The room code
     * @param playerId The player ID
     * @return AppResult containing state and remaining time
     */
    suspend fun fetchSimpleState(
        roomCode: String,
        playerId: String
    ): AppResult<SimpleMultiplayerState> = withContext(dispatchers.io) {
        try {
            val response = multiplayerApi.getSimpleGameState(roomCode, playerId)

            AppResult.Success(
                SimpleMultiplayerState(
                    state = response.state,
                    remainingTime = response.remainingTime
                )
            )
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to fetch game state: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

/**
 * Full multiplayer game state from server.
 */
data class MultiplayerGameState(
    val state: Int,
    val remainingTime: Int,
    val players: List<MultiplayerPlayer>,
    val currentQuestion: MultiplayerQuestion?
) {
    companion object {
        const val STATE_LOBBY = 0
        const val STATE_COUNTDOWN = 1
        const val STATE_QUESTION = 2
        const val STATE_ANSWER_REVEAL = 3
        const val STATE_RANKING = 4
        const val STATE_FINAL = 5
    }

    fun isInLobby(): Boolean = state == STATE_LOBBY
    fun isPlaying(): Boolean = state in STATE_COUNTDOWN..STATE_RANKING
    fun isFinished(): Boolean = state == STATE_FINAL
}

/**
 * Player information in multiplayer game.
 */
data class MultiplayerPlayer(
    val id: String,
    val name: String,
    val avatarId: Int,
    val score: Int,
    val correctCount: Int,
    val wrongCount: Int
)

/**
 * Question in multiplayer game.
 */
data class MultiplayerQuestion(
    val text: String,
    val options: List<String>,
    val index: Int
)

/**
 * Simplified game state (state + time only).
 */
data class SimpleMultiplayerState(
    val state: Int,
    val remainingTime: Int
)
