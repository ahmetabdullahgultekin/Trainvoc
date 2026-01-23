package com.gultekinahmetabdullah.trainvoc.multiplayer.websocket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket client for multiplayer game communication.
 *
 * Handles:
 * - Connection management with automatic reconnection
 * - Message sending and receiving
 * - Connection state tracking
 * - Event-based communication via SharedFlow
 */
@Singleton
class GameWebSocketClient @Inject constructor(
    private val gson: Gson
) {
    companion object {
        private const val TAG = "GameWebSocketClient"
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val RECONNECT_DELAY_MS = 3000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var webSocket: WebSocket? = null
    private var reconnectAttempts = 0
    private var shouldReconnect = false
    private var currentUrl: String? = null

    // Connection state
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Incoming messages
    private val _messages = MutableSharedFlow<WebSocketMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<WebSocketMessage> = _messages.asSharedFlow()

    // Events (room created, joined, errors, etc.)
    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    /**
     * Connect to the WebSocket server.
     *
     * @param baseUrl Base URL of the backend (e.g., "wss://api.trainvoc.com")
     */
    fun connect(baseUrl: String) {
        if (_connectionState.value is ConnectionState.Connected ||
            _connectionState.value is ConnectionState.Connecting) {
            Log.d(TAG, "Already connected or connecting")
            return
        }

        val wsUrl = "$baseUrl/ws/game"
        currentUrl = wsUrl
        shouldReconnect = true
        reconnectAttempts = 0

        doConnect(wsUrl)
    }

    private fun doConnect(url: String) {
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                _connectionState.value = ConnectionState.Connected
                reconnectAttempts = 0
                scope.launch {
                    _events.emit(GameEvent.Connected)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: $text")
                handleMessage(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code - $reason")
                webSocket.close(NORMAL_CLOSURE_STATUS, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code - $reason")
                _connectionState.value = ConnectionState.Disconnected
                scope.launch {
                    _events.emit(GameEvent.Disconnected(reason))
                }
                attemptReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}", t)
                _connectionState.value = ConnectionState.Error(t.message ?: "Unknown error")
                scope.launch {
                    _events.emit(GameEvent.Error(t.message ?: "Connection failed"))
                }
                attemptReconnect()
            }
        })
    }

    private fun attemptReconnect() {
        if (!shouldReconnect || reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.d(TAG, "Not reconnecting: shouldReconnect=$shouldReconnect, attempts=$reconnectAttempts")
            return
        }

        reconnectAttempts++
        Log.d(TAG, "Attempting reconnect #$reconnectAttempts")

        scope.launch {
            kotlinx.coroutines.delay(RECONNECT_DELAY_MS * reconnectAttempts)
            currentUrl?.let { doConnect(it) }
        }
    }

    /**
     * Disconnect from the WebSocket server.
     */
    fun disconnect() {
        shouldReconnect = false
        webSocket?.close(NORMAL_CLOSURE_STATUS, "Client disconnect")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    /**
     * Send a message to the server.
     *
     * @param message The message to send
     * @return true if sent successfully, false otherwise
     */
    fun send(message: WebSocketMessage): Boolean {
        val json = gson.toJson(message.toJson())
        Log.d(TAG, "Sending message: $json")
        return webSocket?.send(json) ?: false
    }

    /**
     * Create a room.
     */
    fun createRoom(
        playerName: String,
        avatarId: Int,
        hashedPassword: String? = null,
        settings: RoomSettings = RoomSettings()
    ): Boolean {
        val message = CreateRoomMessage(
            name = playerName,
            avatarId = avatarId,
            hashedPassword = hashedPassword,
            settings = settings
        )
        return send(message)
    }

    /**
     * Join an existing room.
     */
    fun joinRoom(
        roomCode: String,
        playerName: String,
        avatarId: Int,
        password: String? = null
    ): Boolean {
        val message = JoinRoomMessage(
            roomCode = roomCode,
            name = playerName,
            avatarId = avatarId,
            password = password
        )
        return send(message)
    }

    private fun handleMessage(text: String) {
        scope.launch {
            try {
                val json = gson.fromJson(text, JsonObject::class.java)
                val type = json.get("type")?.asString ?: return@launch

                when (type) {
                    "roomCreated" -> {
                        val roomCode = json.get("roomCode")?.asString ?: ""
                        val playerId = json.get("playerId")?.asString
                        _events.emit(GameEvent.RoomCreated(roomCode, playerId))
                    }
                    "roomJoined" -> {
                        val roomCode = json.get("roomCode")?.asString ?: ""
                        val playerId = json.get("playerId")?.asString ?: ""
                        _events.emit(GameEvent.RoomJoined(roomCode, playerId))
                    }
                    "playerJoined" -> {
                        val playerId = json.get("playerId")?.asString ?: ""
                        val playerName = json.get("playerName")?.asString ?: ""
                        _events.emit(GameEvent.PlayerJoined(playerId, playerName))
                    }
                    "playerLeft" -> {
                        val playerId = json.get("playerId")?.asString ?: ""
                        _events.emit(GameEvent.PlayerLeft(playerId))
                    }
                    "gameStateChanged" -> {
                        val state = json.get("state")?.asInt ?: 0
                        val remainingTime = json.get("remainingTime")?.asInt ?: 0
                        _events.emit(GameEvent.StateChanged(GameState.fromOrdinal(state), remainingTime))
                    }
                    "question" -> {
                        val questionText = json.get("text")?.asString ?: ""
                        val options = json.getAsJsonArray("options")?.map { it.asString } ?: emptyList()
                        val questionIndex = json.get("questionIndex")?.asInt ?: 0
                        _events.emit(GameEvent.QuestionReceived(questionText, options, questionIndex))
                    }
                    "answerResult" -> {
                        val correct = json.get("correct")?.asBoolean ?: false
                        val correctIndex = json.get("correctIndex")?.asInt ?: 0
                        val score = json.get("score")?.asInt ?: 0
                        _events.emit(GameEvent.AnswerResult(correct, correctIndex, score))
                    }
                    "rankings" -> {
                        val players = parsePlayerRankings(json)
                        _events.emit(GameEvent.Rankings(players))
                    }
                    "gameEnded" -> {
                        val players = parsePlayerRankings(json)
                        _events.emit(GameEvent.GameEnded(players))
                    }
                    "error" -> {
                        val message = json.get("message")?.asString ?: "Unknown error"
                        _events.emit(GameEvent.Error(message))
                    }
                    else -> {
                        Log.w(TAG, "Unknown message type: $type")
                        _messages.emit(RawMessage(type, json))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing message: ${e.message}", e)
            }
        }
    }

    private fun parsePlayerRankings(json: JsonObject): List<PlayerRanking> {
        return try {
            val playersArray = json.getAsJsonArray("players") ?: return emptyList()
            playersArray.mapIndexed { index, element ->
                val player = element.asJsonObject
                PlayerRanking(
                    rank = index + 1,
                    playerId = player.get("id")?.asString ?: "",
                    name = player.get("name")?.asString ?: "",
                    score = player.get("score")?.asInt ?: 0,
                    correctCount = player.get("correctCount")?.asInt ?: 0,
                    avatarId = player.get("avatarId")?.asInt ?: 0
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing player rankings", e)
            emptyList()
        }
    }
}

// ============ Connection State ============

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

// ============ Game State Enum ============

enum class GameState(val value: Int) {
    LOBBY(0),
    COUNTDOWN(1),
    QUESTION(2),
    ANSWER_REVEAL(3),
    RANKING(4),
    FINAL(5);

    companion object {
        fun fromOrdinal(ordinal: Int): GameState {
            return entries.getOrElse(ordinal) { LOBBY }
        }
    }
}

// ============ WebSocket Messages ============

sealed class WebSocketMessage {
    abstract fun toJson(): Map<String, Any?>
}

data class CreateRoomMessage(
    val name: String,
    val avatarId: Int,
    val hashedPassword: String? = null,
    val settings: RoomSettings = RoomSettings()
) : WebSocketMessage() {
    override fun toJson() = mapOf(
        "type" to "create",
        "name" to name,
        "avatarId" to avatarId,
        "hashedPassword" to hashedPassword,
        "settings" to mapOf(
            "questionDuration" to settings.questionDuration,
            "optionCount" to settings.optionCount,
            "level" to settings.level,
            "totalQuestionCount" to settings.totalQuestionCount,
            "hostWantsToJoin" to settings.hostWantsToJoin
        )
    )
}

data class JoinRoomMessage(
    val roomCode: String,
    val name: String,
    val avatarId: Int,
    val password: String? = null
) : WebSocketMessage() {
    override fun toJson() = mapOf(
        "type" to "join",
        "roomCode" to roomCode,
        "name" to name,
        "avatarId" to avatarId,
        "password" to password
    )
}

data class RawMessage(
    val type: String,
    val data: JsonObject
) : WebSocketMessage() {
    override fun toJson() = mapOf("type" to type)
}

// ============ Room Settings ============

data class RoomSettings(
    val questionDuration: Int = 60,
    val optionCount: Int = 4,
    val level: String = "A1",
    val totalQuestionCount: Int = 5,
    val hostWantsToJoin: Boolean = true
)

// ============ Game Events ============

sealed class GameEvent {
    object Connected : GameEvent()
    data class Disconnected(val reason: String) : GameEvent()
    data class Error(val message: String) : GameEvent()

    data class RoomCreated(val roomCode: String, val playerId: String?) : GameEvent()
    data class RoomJoined(val roomCode: String, val playerId: String) : GameEvent()
    data class PlayerJoined(val playerId: String, val playerName: String) : GameEvent()
    data class PlayerLeft(val playerId: String) : GameEvent()

    data class StateChanged(val state: GameState, val remainingTime: Int) : GameEvent()
    data class QuestionReceived(val text: String, val options: List<String>, val index: Int) : GameEvent()
    data class AnswerResult(val correct: Boolean, val correctIndex: Int, val score: Int) : GameEvent()
    data class Rankings(val players: List<PlayerRanking>) : GameEvent()
    data class GameEnded(val finalRankings: List<PlayerRanking>) : GameEvent()
}

// ============ Player Ranking ============

data class PlayerRanking(
    val rank: Int,
    val playerId: String,
    val name: String,
    val score: Int,
    val correctCount: Int,
    val avatarId: Int
)
