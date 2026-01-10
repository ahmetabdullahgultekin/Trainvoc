package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FillInTheBlankScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: FillInTheBlankViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is FillInTheBlankUiState.Loading -> {
            GameLoadingState()
        }

        is FillInTheBlankUiState.Playing -> {
            FillInTheBlankContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                showHint = showHint,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onHintToggle = { viewModel.toggleHint() },
                onNavigateBack = onNavigateBack
            )
        }

        is FillInTheBlankUiState.ShowingFeedback -> {
            FillInTheBlankContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                showHint = showHint,
                onAnswerSelected = { },
                onHintToggle = { },
                onNavigateBack = onNavigateBack
            )
        }

        is FillInTheBlankUiState.Complete -> {
            FillInTheBlankContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                showHint = false,
                onAnswerSelected = { },
                onHintToggle = { },
                onNavigateBack = onNavigateBack
            )
            GameResultDialog(
                isComplete = true,
                correctAnswers = state.gameState.correctAnswers,
                totalQuestions = state.gameState.totalQuestions,
                score = state.gameState.totalQuestions * 10,
                onPlayAgain = { viewModel.playAgain(difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is FillInTheBlankUiState.Error -> {
            ErrorStateContent(
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
private fun FillInTheBlankContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.FillInTheBlankGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    showHint: Boolean,
    onAnswerSelected: (String) -> Unit,
    onHintToggle: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Fill in the Blank",
        onNavigateBack = onNavigateBack,
        progress = gameState.currentQuestionIndex.toFloat() / gameState.totalQuestions,
        score = gameState.correctAnswers * 10
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

                // Sentence with blank
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Complete the sentence:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Highlight the blank
                            Text(
                                text = buildAnnotatedString {
                                    val sentence = question.sentenceWithBlank
                                    val blankIndex = sentence.indexOf("_____")
                                    if (blankIndex >= 0) {
                                        append(sentence.substring(0, blankIndex))
                                        withStyle(
                                            SpanStyle(
                                                background = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("_____")
                                        }
                                        append(sentence.substring(blankIndex + 5))
                                    } else {
                                        append(sentence)
                                    }
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Hint button
                item {
                    OutlinedButton(
                        onClick = onHintToggle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Toggle hint")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (showHint) "Hide Hint" else "Show Hint")
                    }
                }

                // Hint card
                item {
                    AnimatedVisibility(
                        visible = showHint,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Text(
                                text = com.gultekinahmetabdullah.trainvoc.games.FillInTheBlankGame().getHint(question),
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
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

                // Accuracy
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Accuracy",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${gameState.accuracy.toInt()}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorStateContent(
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
