package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TranslationRaceScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: TranslationRaceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is TranslationRaceUiState.Loading -> {
            GameLoadingState()
        }

        is TranslationRaceUiState.Playing -> {
            TranslationRaceGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onNavigateBack = onNavigateBack
            )

            PauseDialog(
                isPaused = state.gameState.isPaused,
                onResume = { viewModel.togglePause() },
                onQuit = onNavigateBack
            )
        }

        is TranslationRaceUiState.ShowingFeedback -> {
            TranslationRaceGameContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                onAnswerSelected = { },
                onNavigateBack = onNavigateBack
            )
        }

        is TranslationRaceUiState.Complete -> {
            TranslationRaceGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { },
                onNavigateBack = onNavigateBack
            )

            TranslationRaceResultDialog(
                gameState = state.gameState,
                onPlayAgain = { viewModel.playAgain(difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is TranslationRaceUiState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = { viewModel.startGame(difficulty) },
                onBack = onNavigateBack
            )
        }

        is TranslationRaceUiState.Paused -> {
            // Handled by PauseDialog
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
private fun TranslationRaceGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    onAnswerSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Translation Race",
        onNavigateBack = onNavigateBack,
        progress = gameState.currentQuestionIndex.toFloat() / gameState.totalQuestions,
        score = gameState.score,
        timeRemaining = gameState.timeRemaining
    ) {
        if (question != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Combo display
                if (gameState.combo >= 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF59E0B)
                            )
                        ) {
                            Text(
                                text = "🔥 ${gameState.combo}x COMBO!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }

                // Translation direction indicator
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (from, to) = when (question.direction) {
                            com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.TranslationDirection.ENGLISH_TO_TURKISH ->
                                Pair("🇬🇧 English", "🇹🇷 Turkish")
                            com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.TranslationDirection.TURKISH_TO_ENGLISH ->
                                Pair("🇹🇷 Turkish", "🇬🇧 English")
                            else -> Pair("Mixed", "Mixed")
                        }

                        Text(
                            text = from,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Translation direction",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = to,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Question
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Translate:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = question.questionText,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Options
                items(question.options, key = { it }) { option ->
                    val isSelected = option == selectedAnswer

                    OptionButton(
                        text = option,
                        isSelected = isSelected,
                        isCorrect = if (isSelected) isCorrect else null,
                        onClick = { onAnswerSelected(option) }
                    )
                }

                // Stats
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip(label = "Correct", value = "${gameState.correctAnswers}")
                        StatChip(label = "Combo", value = "${gameState.maxCombo}x")
                        StatChip(label = "APM", value = "${gameState.answersPerMinute.toInt()}")
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslationRaceResultDialog(
    gameState: com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.GameState,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val rating = com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame(
        com.gultekinahmetabdullah.trainvoc.games.GamesDao::class.java.newInstance()
    ).getPerformanceRating(gameState)

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "Race Complete!",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = rating.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                StatRow(label = "Correct Answers", value = "${gameState.correctAnswers}")
                StatRow(label = "Total Answered", value = "${gameState.correctAnswers + gameState.incorrectAnswers}")
                StatRow(label = "Accuracy", value = "${gameState.accuracy.toInt()}%")
                StatRow(label = "Max Combo", value = "${gameState.maxCombo}x")
                StatRow(label = "Answers/Min", value = "${gameState.answersPerMinute.toInt()}")
                StatRow(label = "Score", value = "${gameState.score}")
            }
        },
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text("Play Again")
            }
        },
        dismissButton = {
            TextButton(onClick = onMainMenu) {
                Text("Main Menu")
            }
        }
    )
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatChip(label: String, value: String) {
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
