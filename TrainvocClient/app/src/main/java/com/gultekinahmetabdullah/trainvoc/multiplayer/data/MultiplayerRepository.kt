package com.gultekinahmetabdullah.trainvoc.multiplayer.data

import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.GameEvent
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.GameState
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.GameWebSocketClient
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.ConnectionState
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.PlayerRanking
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.RoomSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for multiplayer game data and operations.
 *
 * Combines WebSocket events with REST API calls for a complete
 * multiplayer experience. Provides reactive state flows for UI consumption.
 */
@Singleton
class MultiplayerRepository @Inject constructor(
    private val webSocketClient: GameWebSocketClient,
    private val multiplayerApi: MultiplayerApi
) {
    companion object {
        private const val TAG = "MultiplayerRepository"
    }

    // WebSocket URL from BuildConfig (configurable per build type)
    private val wsBaseUrl: String
        get() = com.gultekinahmetabdullah.trainvoc.BuildConfig.WS_BASE_URL

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ============ Connection State ============

    val connectionState: StateFlow<ConnectionState> = webSocketClient.connectionState

    // ============ Room State ============

    private val _currentRoom = MutableStateFlow<RoomInfo?>(null)
    val currentRoom: StateFlow<RoomInfo?> = _currentRoom.asStateFlow()

    private val _players = MutableStateFlow<List<PlayerInfo>>(emptyList())
    val players: StateFlow<List<PlayerInfo>> = _players.asStateFlow()

    private val _playerId = MutableStateFlow<String?>(null)
    val playerId: StateFlow<String?> = _playerId.asStateFlow()

    // ============ Game State ============

    private val _gameState = MutableStateFlow(GameState.LOBBY)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime.asStateFlow()

    private val _currentQuestion = MutableStateFlow<QuestionInfo?>(null)
    val currentQuestion: StateFlow<QuestionInfo?> = _currentQuestion.asStateFlow()

    private val _lastAnswerResult = MutableStateFlow<AnswerResultInfo?>(null)
    val lastAnswerResult: StateFlow<AnswerResultInfo?> = _lastAnswerResult.asStateFlow()

    private val _rankings = MutableStateFlow<List<PlayerRanking>>(emptyList())
    val rankings: StateFlow<List<PlayerRanking>> = _rankings.asStateFlow()

    // ============ Error State ============

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeWebSocketEvents()
    }

    private fun observeWebSocketEvents() {
        scope.launch {
            webSocketClient.events.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: GameEvent) {
        when (event) {
            is GameEvent.Connected -> {
                _error.value = null
            }
            is GameEvent.Disconnected -> {
                // Keep room info for reconnection
            }
            is GameEvent.Error -> {
                _error.value = event.message
            }
            is GameEvent.RoomCreated -> {
                _currentRoom.value = RoomInfo(
                    roomCode = event.roomCode,
                    isHost = true
                )
                _playerId.value = event.playerId
            }
            is GameEvent.RoomJoined -> {
                _currentRoom.value = RoomInfo(
                    roomCode = event.roomCode,
                    isHost = false
                )
                _playerId.value = event.playerId
            }
            is GameEvent.PlayerJoined -> {
                val newPlayer = PlayerInfo(
                    id = event.playerId,
                    name = event.playerName,
                    avatarId = 0,
                    score = 0
                )
                _players.value = _players.value + newPlayer
            }
            is GameEvent.PlayerLeft -> {
                _players.value = _players.value.filter { it.id != event.playerId }
            }
            is GameEvent.StateChanged -> {
                _gameState.value = event.state
                _remainingTime.value = event.remainingTime
            }
            is GameEvent.QuestionReceived -> {
                _currentQuestion.value = QuestionInfo(
                    text = event.text,
                    options = event.options,
                    index = event.index
                )
                _lastAnswerResult.value = null
            }
            is GameEvent.AnswerResult -> {
                _lastAnswerResult.value = AnswerResultInfo(
                    correct = event.correct,
                    correctIndex = event.correctIndex,
                    score = event.score
                )
            }
            is GameEvent.Rankings -> {
                _rankings.value = event.players
            }
            is GameEvent.GameEnded -> {
                _rankings.value = event.finalRankings
                _gameState.value = GameState.FINAL
            }
        }
    }

    // ============ Connection Methods ============

    fun connect() {
        webSocketClient.connect(wsBaseUrl)
    }

    fun disconnect() {
        webSocketClient.disconnect()
        resetState()
    }

    // ============ Room Methods ============

    fun createRoom(
        playerName: String,
        avatarId: Int,
        password: String? = null,
        settings: RoomSettings = RoomSettings()
    ): Boolean {
        return webSocketClient.createRoom(
            playerName = playerName,
            avatarId = avatarId,
            hashedPassword = password?.let { hashPassword(it) },
            settings = settings
        )
    }

    fun joinRoom(
        roomCode: String,
        playerName: String,
        avatarId: Int,
        password: String? = null
    ): Boolean {
        return webSocketClient.joinRoom(
            roomCode = roomCode,
            playerName = playerName,
            avatarId = avatarId,
            password = password
        )
    }

    suspend fun leaveRoom(): Result<Unit> {
        val room = _currentRoom.value ?: return Result.failure(Exception("Not in a room"))
        val player = _playerId.value ?: return Result.failure(Exception("No player ID"))

        return try {
            multiplayerApi.leaveRoom(room.roomCode, player)
            resetState()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startGame(): Result<Unit> {
        val room = _currentRoom.value ?: return Result.failure(Exception("Not in a room"))
        if (!room.isHost) return Result.failure(Exception("Only host can start the game"))

        return try {
            multiplayerApi.startGame(room.roomCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ Game Methods ============

    suspend fun submitAnswer(
        selectedIndex: Int,
        answerTimeMs: Long
    ): Result<Unit> {
        val room = _currentRoom.value ?: return Result.failure(Exception("Not in a room"))
        val player = _playerId.value ?: return Result.failure(Exception("No player ID"))
        val question = _currentQuestion.value ?: return Result.failure(Exception("No current question"))

        return try {
            multiplayerApi.submitAnswer(
                AnswerRequest(
                    roomCode = room.roomCode,
                    playerId = player,
                    answer = question.options.getOrNull(selectedIndex) ?: "",
                    answerTime = answerTimeMs,
                    isCorrect = false, // Server calculates this
                    optionPickRate = 0.25 // Server should track this
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun nextQuestion(): Result<Unit> {
        val room = _currentRoom.value ?: return Result.failure(Exception("Not in a room"))

        return try {
            multiplayerApi.nextQuestion(room.roomCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ Room List ============

    suspend fun getActiveRooms(): Result<List<RoomListItem>> {
        return try {
            val rooms = multiplayerApi.getActiveRooms()
            Result.success(rooms)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRoomDetails(roomCode: String): Result<RoomDetails> {
        return try {
            val details = multiplayerApi.getRoomDetails(roomCode)
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ Utility Methods ============

    private fun resetState() {
        _currentRoom.value = null
        _playerId.value = null
        _players.value = emptyList()
        _gameState.value = GameState.LOBBY
        _remainingTime.value = 0
        _currentQuestion.value = null
        _lastAnswerResult.value = null
        _rankings.value = emptyList()
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }

    private fun hashPassword(password: String): String {
        // SHA-256 hash for secure password hashing
        val bytes = java.security.MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

// ============ Data Classes ============

data class RoomInfo(
    val roomCode: String,
    val isHost: Boolean
)

data class PlayerInfo(
    val id: String,
    val name: String,
    val avatarId: Int,
    val score: Int
)

data class QuestionInfo(
    val text: String,
    val options: List<String>,
    val index: Int
)

data class AnswerResultInfo(
    val correct: Boolean,
    val correctIndex: Int,
    val score: Int
)

data class RoomListItem(
    val roomCode: String,
    val hostName: String,
    val playerCount: Int,
    val maxPlayers: Int,
    val hasPassword: Boolean,
    val level: String
)

data class RoomDetails(
    val roomCode: String,
    val hostId: String,
    val state: GameState,
    val players: List<PlayerInfo>,
    val settings: RoomSettings
)

data class AnswerRequest(
    val roomCode: String,
    val playerId: String,
    val answer: String,
    val answerTime: Long,
    val isCorrect: Boolean,
    val optionPickRate: Double
)
