package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ContextCluesScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: ContextCluesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is ContextCluesUiState.Loading -> {
            GameLoadingState()
        }

        is ContextCluesUiState.Playing -> {
            ContextCluesGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onShowClue = { viewModel.showClue() },
                onSkip = { viewModel.skipQuestion() },
                onNavigateBack = onNavigateBack
            )
        }

        is ContextCluesUiState.ShowingFeedback -> {
            ContextCluesGameContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                onAnswerSelected = { },
                onShowClue = { },
                onSkip = { },
                onNavigateBack = onNavigateBack
            )
        }

        is ContextCluesUiState.Complete -> {
            ContextCluesGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { },
                onShowClue = { },
                onSkip = { },
                onNavigateBack = onNavigateBack
            )

            ContextCluesResultDialog(
                gameState = state.gameState,
                onPlayAgain = { viewModel.playAgain(difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is ContextCluesUiState.Error -> {
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
private fun ContextCluesGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    onAnswerSelected: (String) -> Unit,
    onShowClue: () -> Unit,
    onSkip: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Context Clues",
        onNavigateBack = onNavigateBack,
        progress = gameState.currentQuestionIndex.toFloat() / gameState.totalQuestions,
        score = gameState.score
    ) {
        if (question != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Question counter
                item {
                    Text(
                        text = "Question ${gameState.currentQuestionIndex + 1} of ${gameState.totalQuestions}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Instruction
                item {
                    Text(
                        text = "Read the sentence and guess the meaning of the highlighted word:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Context sentence with highlighted word
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
                                .padding(20.dp)
                        ) {
                            // Build annotated string with highlighted word
                            val annotatedText = buildAnnotatedString {
                                val context = question.context
                                val wordInContext = question.wordInContext
                                val startIndex = context.indexOf(wordInContext, ignoreCase = true)

                                if (startIndex != -1) {
                                    // Text before highlighted word
                                    append(context.substring(0, startIndex))

                                    // Highlighted word
                                    withStyle(
                                        style = SpanStyle(
                                            background = Color(0xFFFEF3C7),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    ) {
                                        append(context.substring(startIndex, startIndex + wordInContext.length))
                                    }

                                    // Text after highlighted word
                                    append(context.substring(startIndex + wordInContext.length))
                                } else {
                                    append(context)
                                }
                            }

                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.5f)
                            )
                        }
                    }
                }

                // Clue card (if shown)
                if (gameState.showingClue && question.additionalClue != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = "Hint icon",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Clue:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = question.additionalClue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }

                // Question
                item {
                    Text(
                        text = "What does \"${question.wordInContext}\" mean?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
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

                // Action buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Clue button
                        OutlinedButton(
                            onClick = onShowClue,
                            modifier = Modifier.weight(1f),
                            enabled = !gameState.showingClue
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "Show clue")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clue")
                        }

                        // Skip button
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Skip question")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skip")
                        }
                    }
                }

                // Stats
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip(
                            label = "Accuracy",
                            value = "${gameState.accuracy.toInt()}%"
                        )
                        StatChip(
                            label = "Clues Used",
                            value = "${gameState.cluesUsed}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContextCluesResultDialog(
    gameState: com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame.GameState,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val comprehensionLevel = com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame(
        com.gultekinahmetabdullah.trainvoc.games.GamesDao::class.java.newInstance()
    ).getComprehensionLevel(gameState)

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "Game Complete!",
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
                    text = comprehensionLevel.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = comprehensionLevel.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                StatRow(label = "Correct Answers", value = "${gameState.correctAnswers}/${gameState.totalQuestions}")
                StatRow(label = "Accuracy", value = "${gameState.accuracy.toInt()}%")
                StatRow(label = "Clues Used", value = "${gameState.cluesUsed}")
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
