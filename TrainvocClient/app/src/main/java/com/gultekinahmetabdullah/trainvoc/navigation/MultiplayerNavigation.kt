package com.gultekinahmetabdullah.trainvoc.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.multiplayer.CreateRoomScreen
import com.gultekinahmetabdullah.trainvoc.ui.multiplayer.GameResultsScreen
import com.gultekinahmetabdullah.trainvoc.ui.multiplayer.GameScreen
import com.gultekinahmetabdullah.trainvoc.ui.multiplayer.JoinRoomScreen
import com.gultekinahmetabdullah.trainvoc.ui.multiplayer.LobbyScreen
import com.gultekinahmetabdullah.trainvoc.ui.multiplayer.MultiplayerHomeScreen
import com.gultekinahmetabdullah.trainvoc.viewmodel.MultiplayerViewModel

/**
 * Multiplayer Navigation Graph
 *
 * Provides navigation for all multiplayer game screens:
 * - MultiplayerHomeScreen: Entry point with options to create/join game
 * - CreateRoomScreen: Host creates a new game room
 * - JoinRoomScreen: Player joins an existing room
 * - LobbyScreen: Waiting room before game starts
 * - GameScreen: Active multiplayer quiz game
 * - GameResultsScreen: Final scores and rankings
 *
 * All screens share a MultiplayerViewModel for WebSocket state management.
 */
fun NavGraphBuilder.multiplayerNavGraph(navController: NavHostController) {

    // Multiplayer Home - Entry point
    composable(Route.MULTIPLAYER_HOME) {
        val viewModel: MultiplayerViewModel = hiltViewModel()
        val connectionState by viewModel.connectionState.collectAsState()

        MultiplayerHomeScreen(
            connectionState = connectionState,
            onNavigateBack = { navController.popBackStack() },
            onCreateRoom = { navController.navigate(Route.MULTIPLAYER_CREATE_ROOM) },
            onJoinRoom = { navController.navigate(Route.MULTIPLAYER_JOIN_ROOM) },
            onConnect = { viewModel.connect() },
            onDisconnect = { viewModel.disconnect() }
        )
    }

    // Create Room Screen
    composable(Route.MULTIPLAYER_CREATE_ROOM) {
        val viewModel: MultiplayerViewModel = hiltViewModel()
        val roomState by viewModel.roomState.collectAsState()

        CreateRoomScreen(
            roomState = roomState,
            onNavigateBack = { navController.popBackStack() },
            onCreateRoom = { playerName, avatarId, password, settings ->
                viewModel.createRoom(playerName, avatarId, password, settings)
            },
            onRoomCreated = { roomCode ->
                navController.navigate(Route.multiplayerLobby(roomCode)) {
                    popUpTo(Route.MULTIPLAYER_HOME)
                }
            }
        )
    }

    // Join Room Screen
    composable(Route.MULTIPLAYER_JOIN_ROOM) {
        val viewModel: MultiplayerViewModel = hiltViewModel()
        val roomState by viewModel.roomState.collectAsState()
        val availableRooms by viewModel.availableRooms.collectAsState()

        JoinRoomScreen(
            roomState = roomState,
            availableRooms = availableRooms,
            onNavigateBack = { navController.popBackStack() },
            onJoinRoom = { roomCode, playerName, avatarId, password ->
                viewModel.joinRoom(roomCode, playerName, avatarId, password)
            },
            onRefreshRooms = { viewModel.refreshAvailableRooms() },
            onRoomJoined = { roomCode ->
                navController.navigate(Route.multiplayerLobby(roomCode)) {
                    popUpTo(Route.MULTIPLAYER_HOME)
                }
            }
        )
    }

    // Lobby Screen - Waiting for players
    composable(
        route = Route.MULTIPLAYER_LOBBY,
        arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
    ) { backStackEntry ->
        val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
        val viewModel: MultiplayerViewModel = hiltViewModel()
        val roomState by viewModel.roomState.collectAsState()
        val players by viewModel.players.collectAsState()
        val isHost by viewModel.isHost.collectAsState()

        LobbyScreen(
            roomCode = roomCode,
            roomState = roomState,
            players = players,
            isHost = isHost,
            onNavigateBack = {
                viewModel.leaveRoom()
                navController.popBackStack()
            },
            onStartGame = { viewModel.startGame() },
            onKickPlayer = { playerId -> viewModel.kickPlayer(playerId) },
            onGameStarted = {
                navController.navigate(Route.multiplayerGame(roomCode)) {
                    popUpTo(Route.multiplayerLobby(roomCode)) { inclusive = true }
                }
            }
        )
    }

    // Game Screen - Active gameplay
    composable(
        route = Route.MULTIPLAYER_GAME,
        arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
    ) { backStackEntry ->
        val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
        val viewModel: MultiplayerViewModel = hiltViewModel()
        val gameState by viewModel.gameState.collectAsState()
        val currentQuestion by viewModel.currentQuestion.collectAsState()
        val timeRemaining by viewModel.timeRemaining.collectAsState()
        val players by viewModel.players.collectAsState()
        val selectedAnswer by viewModel.selectedAnswer.collectAsState()

        GameScreen(
            roomCode = roomCode,
            gameState = gameState,
            currentQuestion = currentQuestion,
            timeRemaining = timeRemaining,
            players = players,
            selectedAnswer = selectedAnswer,
            onSubmitAnswer = { answer -> viewModel.submitAnswer(answer) },
            onLeaveGame = {
                viewModel.leaveRoom()
                navController.navigate(Route.MULTIPLAYER_HOME) {
                    popUpTo(Route.MULTIPLAYER_HOME) { inclusive = true }
                }
            },
            onGameEnded = {
                navController.navigate(Route.multiplayerResults(roomCode)) {
                    popUpTo(Route.multiplayerGame(roomCode)) { inclusive = true }
                }
            }
        )
    }

    // Results Screen - Final rankings
    composable(
        route = Route.MULTIPLAYER_RESULTS,
        arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
    ) { backStackEntry ->
        val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
        val viewModel: MultiplayerViewModel = hiltViewModel()
        val finalRankings by viewModel.finalRankings.collectAsState()
        val gameStats by viewModel.gameStats.collectAsState()

        GameResultsScreen(
            roomCode = roomCode,
            rankings = finalRankings,
            stats = gameStats,
            onPlayAgain = {
                viewModel.requestRematch()
                navController.navigate(Route.multiplayerLobby(roomCode)) {
                    popUpTo(Route.multiplayerResults(roomCode)) { inclusive = true }
                }
            },
            onBackToHome = {
                viewModel.disconnect()
                navController.navigate(Route.MULTIPLAYER_HOME) {
                    popUpTo(Route.MULTIPLAYER_HOME) { inclusive = true }
                }
            }
        )
    }
}

/**
 * Navigation Helper Extensions for Multiplayer
 */
fun NavHostController.navigateToMultiplayer() {
    navigate(Route.MULTIPLAYER_HOME)
}

fun NavHostController.navigateToCreateRoom() {
    navigate(Route.MULTIPLAYER_CREATE_ROOM)
}

fun NavHostController.navigateToJoinRoom() {
    navigate(Route.MULTIPLAYER_JOIN_ROOM)
}

fun NavHostController.navigateToLobby(roomCode: String) {
    navigate(Route.multiplayerLobby(roomCode))
}

fun NavHostController.navigateToGame(roomCode: String) {
    navigate(Route.multiplayerGame(roomCode))
}

fun NavHostController.navigateToResults(roomCode: String) {
    navigate(Route.multiplayerResults(roomCode))
}
