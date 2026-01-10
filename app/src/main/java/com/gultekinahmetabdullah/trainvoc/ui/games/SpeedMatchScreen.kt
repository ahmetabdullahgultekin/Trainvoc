package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SpeedMatchScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: SpeedMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is SpeedMatchUiState.Loading -> {
            GameLoadingState()
        }

        is SpeedMatchUiState.Playing -> {
            SpeedMatchGameContent(
                gameState = state.gameState,
                onLeftSelected = { viewModel.selectLeft(it) },
                onRightSelected = { viewModel.selectRight(it) },
                onNavigateBack = onNavigateBack
            )

            PauseDialog(
                isPaused = state.gameState.isPaused,
                onResume = { viewModel.togglePause() },
                onQuit = onNavigateBack
            )
        }

        is SpeedMatchUiState.Complete -> {
            SpeedMatchGameContent(
                gameState = state.gameState,
                onLeftSelected = { },
                onRightSelected = { },
                onNavigateBack = onNavigateBack
            )
            GameResultDialog(
                isComplete = true,
                correctAnswers = state.gameState.matchedPairs,
                totalQuestions = state.gameState.totalPairs,
                score = state.gameState.score,
                onPlayAgain = { viewModel.playAgain(difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is SpeedMatchUiState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = { viewModel.startGame(difficulty) },
                onBack = onNavigateBack
            )
        }
    }

    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            onDifficultySelected = { selectedDifficulty ->
                showDifficultyDialog = false
                viewModel.startGame(selectedDifficulty)
            },
            onDismiss = {
                showDifficultyDialog = false
                viewModel.startGame(difficulty)
            }
        )
    }
}

@Composable
private fun SpeedMatchGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.SpeedMatchGame.GameState,
    onLeftSelected: (Int) -> Unit,
    onRightSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    GameScreenTemplate(
        title = "Speed Match",
        onNavigateBack = onNavigateBack,
        progress = gameState.matchedPairs.toFloat() / gameState.totalPairs,
        score = gameState.score,
        timeRemaining = gameState.timeRemaining
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Combo display
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ComboDisplay(combo = gameState.combo)
            }

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Matched", value = "${gameState.matchedPairs}/${gameState.totalPairs}")
                StatCard(label = "Combo", value = "${gameState.combo}x")
                StatCard(label = "Best", value = "${gameState.maxCombo}x")
            }

            Divider()

            // Two columns for matching
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left column (English)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(gameState.leftOptions) { index, option ->
                        val pair = gameState.pairs.find { it.leftOption == option }
                        val isMatched = pair?.isMatched == true
                        val isSelected = gameState.selectedLeft == index

                        MatchOptionButton(
                            text = option,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            onClick = { if (!isMatched) onLeftSelected(index) }
                        )
                    }
                }

                // Right column (Turkish)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(gameState.rightOptions) { index, option ->
                        val pair = gameState.pairs.find { it.rightOption == option }
                        val isMatched = pair?.isMatched == true
                        val isSelected = gameState.selectedRight == index

                        MatchOptionButton(
                            text = option,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            onClick = { if (!isMatched) onRightSelected(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchOptionButton(
    text: String,
    isSelected: Boolean,
    isMatched: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = !isMatched,
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isMatched -> MaterialTheme.colorScheme.tertiary
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surface
            },
            contentColor = when {
                isMatched -> MaterialTheme.colorScheme.onTertiary
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("Back")
                }
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
