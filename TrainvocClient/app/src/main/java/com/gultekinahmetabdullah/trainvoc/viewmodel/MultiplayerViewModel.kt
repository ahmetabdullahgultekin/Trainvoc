package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.MultiplayerRepository
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.PlayerInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.QuestionInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.RoomListItem
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.ConnectionState
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.GameState
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.PlayerRanking
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.RoomSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing multiplayer game state.
 *
 * Coordinates between the UI layer and MultiplayerRepository/WebSocket client.
 * Maintains all state needed for multiplayer game flow:
 * - Connection state
 * - Room state (creating, joining, in lobby)
 * - Game state (countdown, question, answer reveal, rankings)
 * - Player list and rankings
 */
@HiltViewModel
class MultiplayerViewModel @Inject constructor(
    private val repository: MultiplayerRepository
) : ViewModel() {

    // Connection State - from repository
    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    // Room State
    private val _roomState = MutableStateFlow<RoomState>(RoomState.Idle)
    val roomState: StateFlow<RoomState> = _roomState.asStateFlow()

    private val _availableRooms = MutableStateFlow<List<RoomListItem>>(emptyList())
    val availableRooms: StateFlow<List<RoomListItem>> = _availableRooms.asStateFlow()

    // Host status
    private val _isHost = MutableStateFlow(false)
    val isHost: StateFlow<Boolean> = _isHost.asStateFlow()

    // Players in current room - from repository
    val players: StateFlow<List<PlayerInfo>> = repository.players

    // Game State - from repository
    val gameState: StateFlow<GameState> = repository.gameState

    // Current question - from repository
    val currentQuestion: StateFlow<QuestionInfo?> = repository.currentQuestion

    // Time remaining - from repository
    val timeRemaining: StateFlow<Int> = repository.remainingTime

    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer: StateFlow<Int?> = _selectedAnswer.asStateFlow()

    // Answer time tracking for scoring
    private var questionStartTime: Long = 0

    // Results - from repository
    val finalRankings: StateFlow<List<PlayerRanking>> = repository.rankings

    private val _gameStats = MutableStateFlow<GameStats?>(null)
    val gameStats: StateFlow<GameStats?> = _gameStats.asStateFlow()

    // Error handling - from repository
    val error: StateFlow<String?> = repository.error

    init {
        observeRoomState()
        observeGameState()
    }

    /**
     * Observe room state changes.
     */
    private fun observeRoomState() {
        viewModelScope.launch {
            repository.currentRoom.collect { room ->
                if (room != null) {
                    _roomState.value = RoomState.InLobby(room.roomCode)
                    _isHost.value = room.isHost
                } else if (_roomState.value !is RoomState.Creating && _roomState.value !is RoomState.Joining) {
                    _roomState.value = RoomState.Idle
                    _isHost.value = false
                }
            }
        }
    }

    /**
     * Observe game state for question timing.
     */
    private fun observeGameState() {
        viewModelScope.launch {
            repository.gameState.collect { state ->
                when (state) {
                    GameState.QUESTION -> {
                        questionStartTime = System.currentTimeMillis()
                        _selectedAnswer.value = null
                    }
                    GameState.FINAL -> {
                        val rankings = repository.rankings.value
                        _gameStats.value = GameStats(
                            totalQuestions = 0, // Would need to track this
                            totalPlayers = rankings.size,
                            winnerName = rankings.firstOrNull()?.name ?: ""
                        )
                    }
                    else -> { /* no-op */ }
                }
            }
        }
    }

    /**
     * Get current room code.
     */
    fun getCurrentRoomCode(): String? = repository.currentRoom.value?.roomCode

    /**
     * Connect to the WebSocket server.
     */
    fun connect() {
        repository.connect()
    }

    /**
     * Disconnect from the WebSocket server.
     */
    fun disconnect() {
        repository.disconnect()
        _roomState.value = RoomState.Idle
    }

    /**
     * Create a new game room.
     */
    fun createRoom(
        playerName: String,
        avatarId: Int,
        password: String?,
        settings: RoomSettings
    ) {
        _roomState.value = RoomState.Creating
        repository.createRoom(playerName, avatarId, password, settings)
    }

    /**
     * Join an existing game room.
     */
    fun joinRoom(
        roomCode: String,
        playerName: String,
        avatarId: Int,
        password: String?
    ) {
        _roomState.value = RoomState.Joining
        repository.joinRoom(roomCode, playerName, avatarId, password)
    }

    /**
     * Leave the current room.
     */
    fun leaveRoom() {
        viewModelScope.launch {
            repository.leaveRoom()
            _roomState.value = RoomState.Idle
        }
    }

    /**
     * Start the game (host only).
     */
    fun startGame() {
        if (_isHost.value) {
            viewModelScope.launch {
                repository.startGame()
            }
        }
    }

    /**
     * Kick a player from the room (host only).
     * Note: Requires backend support - currently not implemented.
     */
    fun kickPlayer(playerId: String) {
        // Would need to add this to MultiplayerApi
    }

    /**
     * Submit an answer to the current question.
     */
    fun submitAnswer(answerIndex: Int) {
        if (_selectedAnswer.value == null) {
            _selectedAnswer.value = answerIndex
            val answerTime = System.currentTimeMillis() - questionStartTime
            viewModelScope.launch {
                repository.submitAnswer(answerIndex, answerTime)
            }
        }
    }

    /**
     * Request a rematch (play again with same players).
     */
    fun requestRematch() {
        _gameStats.value = null
        _selectedAnswer.value = null
        // Server would handle resetting room state
    }

    /**
     * Refresh the list of available rooms.
     */
    fun refreshAvailableRooms() {
        viewModelScope.launch {
            repository.getActiveRooms()
                .onSuccess { rooms ->
                    _availableRooms.value = rooms
                }
                .onFailure {
                    // Error is handled through repository.error
                }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        repository.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}

/**
 * Room state for UI.
 */
sealed class RoomState {
    data object Idle : RoomState()
    data object Creating : RoomState()
    data object Joining : RoomState()
    data class InLobby(val roomCode: String) : RoomState()
    data class InGame(val roomCode: String) : RoomState()
    data class Error(val message: String) : RoomState()
}

/**
 * Game stats for results screen.
 */
data class GameStats(
    val totalQuestions: Int,
    val totalPlayers: Int,
    val winnerName: String
)
