package com.gultekinahmetabdullah.trainvoc.multiplayer.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for multiplayer game REST API.
 *
 * These endpoints complement the WebSocket connection for
 * operations that don't require real-time updates.
 */
interface MultiplayerApi {

    // ============ Room Management ============

    /**
     * Get list of active rooms.
     */
    @GET("api/game/rooms")
    suspend fun getActiveRooms(): List<RoomListItem>

    /**
     * Get room details.
     */
    @GET("api/game/{roomCode}")
    suspend fun getRoomDetails(
        @Path("roomCode") roomCode: String
    ): RoomDetails

    /**
     * Get players in a room.
     */
    @GET("api/game/players")
    suspend fun getPlayersInRoom(
        @Query("roomCode") roomCode: String
    ): List<PlayerInfo>

    /**
     * Leave a room.
     */
    @POST("api/game/rooms/{roomCode}/leave")
    suspend fun leaveRoom(
        @Path("roomCode") roomCode: String,
        @Query("playerId") playerId: String
    )

    /**
     * Disband a room (host only).
     */
    @POST("api/game/rooms/{roomCode}/disband")
    suspend fun disbandRoom(
        @Path("roomCode") roomCode: String
    )

    // ============ Game Control ============

    /**
     * Start the game (host only, transitions LOBBY → COUNTDOWN).
     */
    @POST("api/game/rooms/{roomCode}/start")
    suspend fun startGame(
        @Path("roomCode") roomCode: String
    )

    /**
     * Advance to next question (host only, ANSWER_REVEAL → COUNTDOWN).
     */
    @POST("api/game/next")
    suspend fun nextQuestion(
        @Query("roomCode") roomCode: String
    )

    /**
     * Submit an answer.
     */
    @POST("api/game/answer")
    suspend fun submitAnswer(
        @Body request: AnswerRequest
    ): AnswerResponse

    // ============ Game State ============

    /**
     * Get full game state with player list.
     */
    @GET("api/game/state")
    suspend fun getGameState(
        @Query("roomCode") roomCode: String,
        @Query("playerId") playerId: String
    ): GameStateResponse

    /**
     * Get simple game state (just state and time).
     */
    @GET("api/game/state-simple")
    suspend fun getSimpleGameState(
        @Query("roomCode") roomCode: String,
        @Query("playerId") playerId: String
    ): SimpleGameStateResponse

}

// ============ Response DTOs ============

data class AnswerResponse(
    val success: Boolean,
    val message: String?,
    val score: Int?,
    val correct: Boolean?
)

data class GameStateResponse(
    val state: Int,
    val remainingTime: Int,
    val players: List<PlayerStateInfo>,
    val currentQuestion: QuestionResponse?
)

data class SimpleGameStateResponse(
    val state: Int,
    val remainingTime: Int
)

data class PlayerStateInfo(
    val id: String,
    val name: String,
    val avatarId: Int,
    val score: Int,
    val correctCount: Int,
    val wrongCount: Int
)

data class QuestionResponse(
    val text: String,
    val options: List<String>,
    val index: Int
)
