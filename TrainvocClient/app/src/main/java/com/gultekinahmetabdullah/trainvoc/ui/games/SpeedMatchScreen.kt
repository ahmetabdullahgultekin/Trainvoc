package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun SpeedMatchScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: SpeedMatchViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.SPEED_MATCH)) {
            tutorialViewModel.startTutorial(GameType.SPEED_MATCH)
        }
    }

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

    // Tutorial overlay
    if (tutorialState.isActive && tutorialState.gameType == GameType.SPEED_MATCH) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
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
        title = stringResource(id = R.string.speed_match),
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
                StatCard(label = stringResource(id = R.string.matched), value = "${gameState.matchedPairs}/${gameState.totalPairs}")
                StatCard(label = stringResource(id = R.string.combo), value = "${gameState.combo}x")
                StatCard(label = stringResource(id = R.string.best), value = "${gameState.maxCombo}x")
            }

            HorizontalDivider()

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
                    itemsIndexed(gameState.leftOptions, key = { _, option -> option }) { index, option ->
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
                    itemsIndexed(gameState.rightOptions, key = { _, option -> option }) { index, option ->
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
    val backgroundColor = when {
        isMatched -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Bright green for matched
        isSelected -> androidx.compose.ui.graphics.Color(0xFF2196F3) // Bright blue for selected
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isMatched || isSelected -> androidx.compose.ui.graphics.Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = !isMatched,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.8f),
            disabledContentColor = contentColor.copy(alpha = 0.8f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected || isMatched) FontWeight.Bold else FontWeight.Normal,
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
                text = stringResource(id = R.string.error_title),
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
                    Text(stringResource(id = R.string.back))
                }
                Button(onClick = onRetry) {
                    Text(stringResource(id = R.string.retry))
                }
            }
        }
    }
}
