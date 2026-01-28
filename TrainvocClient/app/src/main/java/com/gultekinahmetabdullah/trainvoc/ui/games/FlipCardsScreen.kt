package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun FlipCardsScreen(
    onNavigateBack: () -> Unit,
    gridSize: String = "4x4",
    difficulty: String = "medium",
    viewModel: FlipCardsViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showGridDialog by remember { mutableStateOf(true) }
    var selectedGridSize by remember { mutableStateOf(gridSize) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.FLIP_CARDS)) {
            tutorialViewModel.startTutorial(GameType.FLIP_CARDS)
        }
    }

    LaunchedEffect(selectedGridSize) {
        if (!showGridDialog) {
            viewModel.startGame(selectedGridSize, difficulty)
        }
    }

    when (val state = uiState) {
        is FlipCardsUiState.Loading -> {
            GameLoadingState()
        }

        is FlipCardsUiState.Playing -> {
            FlipCardsContent(
                gameState = state.gameState,
                onCardClick = { viewModel.flipCard(it) },
                onNavigateBack = onNavigateBack
            )
        }

        is FlipCardsUiState.Complete -> {
            FlipCardsContent(
                gameState = state.gameState,
                onCardClick = { },
                onNavigateBack = onNavigateBack
            )
            GameResultDialog(
                isComplete = true,
                correctAnswers = state.gameState.matchedPairs,
                totalQuestions = state.gameState.totalPairs,
                score = calculateScore(state.gameState),
                onPlayAgain = { viewModel.playAgain(selectedGridSize, difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is FlipCardsUiState.Error -> {
            ErrorStateGeneric(
                message = state.message,
                onRetry = { viewModel.startGame(selectedGridSize, difficulty) },
                onBack = onNavigateBack
            )
        }
    }

    if (showGridDialog) {
        GridSizeSelectionDialog(
            onGridSizeSelected = { size ->
                selectedGridSize = size
                showGridDialog = false
                viewModel.startGame(size, difficulty)
            },
            onDismiss = {
                showGridDialog = false
                viewModel.startGame(selectedGridSize, difficulty)
            }
        )
    }

    // Tutorial overlay
    if (tutorialState.isActive && tutorialState.gameType == GameType.FLIP_CARDS) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun FlipCardsContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.FlipCardsGame.GameState,
    onCardClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Responsive grid columns based on screen width and grid size (fixes #208)
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val columns = when (gameState.gridSize) {
        "4x4" -> if (screenWidthDp >= 600) 4 else 4
        "4x6" -> if (screenWidthDp >= 600) 6 else 4
        "6x6" -> if (screenWidthDp >= 600) 6 else 5
        else -> if (screenWidthDp >= 600) 6 else 4
    }

    GameScreenTemplate(
        title = "Flip Cards",
        onNavigateBack = onNavigateBack,
        progress = gameState.matchedPairs.toFloat() / gameState.totalPairs,
        score = calculateScore(gameState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Moves",
                    value = "${gameState.moves}",
                    icon = null
                )
                StatCard(
                    label = "Pairs",
                    value = "${gameState.matchedPairs}/${gameState.totalPairs}",
                    icon = null
                )
                if (gameState.bestMoves != null) {
                    StatCard(
                        label = "Best",
                        value = "${gameState.bestMoves}",
                        icon = null
                    )
                }
            }

            // Cards grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(gameState.cards, key = { index, _ -> index }) { index, card ->
                    FlipCard(
                        content = card.content,
                        isFlipped = card.isFlipped,
                        isMatched = card.isMatched,
                        onClick = { onCardClick(index) },
                        modifier = Modifier.aspectRatio(1f)
                    )
                }
            }

            // New best indicator
            if (gameState.bestMoves != null && gameState.moves < gameState.bestMoves) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "🏆 New Best Score!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GridSizeSelectionDialog(
    onGridSizeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Grid Size") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GridSizeOption("4x4", "8 pairs (Easy)", onClick = { onGridSizeSelected("4x4") })
                GridSizeOption("4x6", "12 pairs (Medium)", onClick = { onGridSizeSelected("4x6") })
                GridSizeOption("6x6", "18 pairs (Hard)", onClick = { onGridSizeSelected("6x6") })
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GridSizeOption(
    gridSize: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = gridSize,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun calculateScore(gameState: com.gultekinahmetabdullah.trainvoc.games.FlipCardsGame.GameState): Int {
    val optimalMoves = gameState.totalPairs
    val efficiency = if (gameState.moves > 0) (optimalMoves.toFloat() / gameState.moves) * 100 else 0f
    return (gameState.matchedPairs * 10 + efficiency).toInt()
}
