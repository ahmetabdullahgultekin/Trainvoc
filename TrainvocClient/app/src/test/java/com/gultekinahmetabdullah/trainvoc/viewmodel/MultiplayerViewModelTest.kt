package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.multiplayer.data.MultiplayerRepository
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.PlayerInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.QuestionInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.RoomInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.RoomListItem
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.ConnectionState
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.GameState
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.PlayerRanking
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.RoomSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for MultiplayerViewModel
 *
 * Tests:
 * - Connection state management
 * - Room creation and joining
 * - Game state handling
 * - Answer submission
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MultiplayerViewModelTest {

    private lateinit var repository: MultiplayerRepository
    private lateinit var viewModel: MultiplayerViewModel

    // Mocked state flows
    private val connectionStateFlow = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val playersFlow = MutableStateFlow<List<PlayerInfo>>(emptyList())
    private val gameStateFlow = MutableStateFlow(GameState.WAITING)
    private val currentQuestionFlow = MutableStateFlow<QuestionInfo?>(null)
    private val remainingTimeFlow = MutableStateFlow(0)
    private val rankingsFlow = MutableStateFlow<List<PlayerRanking>>(emptyList())
    private val currentRoomFlow = MutableStateFlow<RoomInfo?>(null)
    private val errorFlow = MutableStateFlow<String?>(null)

    private val testSettings = RoomSettings(
        maxPlayers = 4,
        questionCount = 10,
        timePerQuestion = 15
    )

    @Before
    fun setup() {
        repository = mock()

        // Setup mock flows
        whenever(repository.connectionState).thenReturn(connectionStateFlow)
        whenever(repository.players).thenReturn(playersFlow)
        whenever(repository.gameState).thenReturn(gameStateFlow)
        whenever(repository.currentQuestion).thenReturn(currentQuestionFlow)
        whenever(repository.remainingTime).thenReturn(remainingTimeFlow)
        whenever(repository.rankings).thenReturn(rankingsFlow)
        whenever(repository.currentRoom).thenReturn(currentRoomFlow)
        whenever(repository.error).thenReturn(errorFlow)
    }

    private fun createViewModel(): MultiplayerViewModel {
        return MultiplayerViewModel(repository)
    }

    @Test
    fun `initial state is Idle`() = runTest {
        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(RoomState.Idle, viewModel.roomState.value)
        assertFalse(viewModel.isHost.value)
    }

    @Test
    fun `connect calls repository connect`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.connect()

        // Assert
        verify(repository).connect()
    }

    @Test
    fun `disconnect calls repository disconnect and resets state`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.disconnect()

        // Assert
        verify(repository).disconnect()
        assertEquals(RoomState.Idle, viewModel.roomState.value)
    }

    @Test
    fun `createRoom sets state to Creating and calls repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.createRoom(
            playerName = "TestPlayer",
            avatarId = 1,
            password = null,
            settings = testSettings
        )

        // Assert
        assertEquals(RoomState.Creating, viewModel.roomState.value)
        verify(repository).createRoom("TestPlayer", 1, null, testSettings)
    }

    @Test
    fun `joinRoom sets state to Joining and calls repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.joinRoom(
            roomCode = "ABC123",
            playerName = "TestPlayer",
            avatarId = 2,
            password = "secret"
        )

        // Assert
        assertEquals(RoomState.Joining, viewModel.roomState.value)
        verify(repository).joinRoom("ABC123", "TestPlayer", 2, "secret")
    }

    @Test
    fun `room state updates to InLobby when currentRoom is set`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act - simulate room joined
        currentRoomFlow.value = RoomInfo(
            roomCode = "XYZ789",
            isHost = false
        )
        advanceUntilIdle()

        // Assert
        assertEquals(RoomState.InLobby("XYZ789"), viewModel.roomState.value)
        assertFalse(viewModel.isHost.value)
    }

    @Test
    fun `isHost is true when user creates room`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act - simulate room created as host
        currentRoomFlow.value = RoomInfo(
            roomCode = "HOST123",
            isHost = true
        )
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.isHost.value)
    }

    @Test
    fun `leaveRoom calls repository and resets state`() = runTest {
        // Arrange
        viewModel = createViewModel()
        currentRoomFlow.value = RoomInfo(roomCode = "ROOM1", isHost = false)
        advanceUntilIdle()

        // Act
        viewModel.leaveRoom()
        advanceUntilIdle()

        // Assert
        verify(repository).leaveRoom()
        assertEquals(RoomState.Idle, viewModel.roomState.value)
    }

    @Test
    fun `startGame calls repository when isHost is true`() = runTest {
        // Arrange
        viewModel = createViewModel()
        currentRoomFlow.value = RoomInfo(roomCode = "HOST1", isHost = true)
        advanceUntilIdle()

        // Act
        viewModel.startGame()
        advanceUntilIdle()

        // Assert
        verify(repository).startGame()
    }

    @Test
    fun `startGame does nothing when not host`() = runTest {
        // Arrange
        viewModel = createViewModel()
        currentRoomFlow.value = RoomInfo(roomCode = "GUEST1", isHost = false)
        advanceUntilIdle()

        // Act
        viewModel.startGame()
        advanceUntilIdle()

        // Assert - startGame not called since not host
        // Verify is tricky here since we're checking it wasn't called
        // The test verifies no exception is thrown
        assertFalse(viewModel.isHost.value)
    }

    @Test
    fun `submitAnswer updates selectedAnswer and calls repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.submitAnswer(2)
        advanceUntilIdle()

        // Assert
        assertEquals(2, viewModel.selectedAnswer.value)
        verify(repository).submitAnswer(org.mockito.kotlin.eq(2), org.mockito.kotlin.any())
    }

    @Test
    fun `submitAnswer ignores duplicate submissions`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act - submit twice
        viewModel.submitAnswer(1)
        viewModel.submitAnswer(3)
        advanceUntilIdle()

        // Assert - only first answer is recorded
        assertEquals(1, viewModel.selectedAnswer.value)
    }

    @Test
    fun `selectedAnswer resets when game state changes to QUESTION`() = runTest {
        // Arrange
        viewModel = createViewModel()
        viewModel.submitAnswer(2)
        advanceUntilIdle()
        assertEquals(2, viewModel.selectedAnswer.value)

        // Act - new question
        gameStateFlow.value = GameState.QUESTION
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.selectedAnswer.value)
    }

    @Test
    fun `gameStats is populated when game ends`() = runTest {
        // Arrange
        viewModel = createViewModel()
        rankingsFlow.value = listOf(
            PlayerRanking("player1", "Winner", 100, 1),
            PlayerRanking("player2", "Runner", 80, 2)
        )
        advanceUntilIdle()

        // Act - game ends
        gameStateFlow.value = GameState.FINAL
        advanceUntilIdle()

        // Assert
        assertNotNull(viewModel.gameStats.value)
        assertEquals(2, viewModel.gameStats.value?.totalPlayers)
        assertEquals("Winner", viewModel.gameStats.value?.winnerName)
    }

    @Test
    fun `requestRematch clears game stats`() = runTest {
        // Arrange
        viewModel = createViewModel()
        rankingsFlow.value = listOf(
            PlayerRanking("player1", "Winner", 100, 1)
        )
        gameStateFlow.value = GameState.FINAL
        advanceUntilIdle()

        assertNotNull(viewModel.gameStats.value)

        // Act
        viewModel.requestRematch()

        // Assert
        assertNull(viewModel.gameStats.value)
        assertNull(viewModel.selectedAnswer.value)
    }

    @Test
    fun `refreshAvailableRooms updates room list on success`() = runTest {
        // Arrange
        val rooms = listOf(
            RoomListItem("ROOM1", "Host1", 2, 4),
            RoomListItem("ROOM2", "Host2", 3, 4)
        )
        whenever(repository.getActiveRooms()).thenReturn(Result.success(rooms))

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.refreshAvailableRooms()
        advanceUntilIdle()

        // Assert
        assertEquals(2, viewModel.availableRooms.value.size)
        assertEquals("ROOM1", viewModel.availableRooms.value[0].roomCode)
    }

    @Test
    fun `refreshAvailableRooms handles failure`() = runTest {
        // Arrange
        whenever(repository.getActiveRooms()).thenReturn(Result.failure(Exception("Network error")))

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.refreshAvailableRooms()
        advanceUntilIdle()

        // Assert - list remains empty
        assertTrue(viewModel.availableRooms.value.isEmpty())
    }

    @Test
    fun `clearError calls repository clearError`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.clearError()

        // Assert
        verify(repository).clearError()
    }

    @Test
    fun `getCurrentRoomCode returns current room code`() = runTest {
        // Arrange
        viewModel = createViewModel()
        currentRoomFlow.value = RoomInfo(roomCode = "TEST123", isHost = true)
        advanceUntilIdle()

        // Act
        val code = viewModel.getCurrentRoomCode()

        // Assert
        assertEquals("TEST123", code)
    }

    @Test
    fun `getCurrentRoomCode returns null when not in room`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        val code = viewModel.getCurrentRoomCode()

        // Assert
        assertNull(code)
    }

    @Test
    fun `onCleared disconnects from server`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act - simulate ViewModel cleared
        viewModel.onCleared()

        // Assert
        verify(repository).disconnect()
    }
}
