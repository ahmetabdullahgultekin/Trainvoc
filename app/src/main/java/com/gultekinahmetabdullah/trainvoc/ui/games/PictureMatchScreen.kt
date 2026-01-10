package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PictureMatchScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: PictureMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is PictureMatchUiState.Loading -> {
            GameLoadingState()
        }

        is PictureMatchUiState.Playing -> {
            PictureMatchGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                correctAnswer = null,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onNavigateBack = onNavigateBack
            )
        }

        is PictureMatchUiState.ShowingFeedback -> {
            PictureMatchGameContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                correctAnswer = state.correctAnswer,
                onAnswerSelected = { },
                onNavigateBack = onNavigateBack
            )
        }

        is PictureMatchUiState.Complete -> {
            PictureMatchGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                correctAnswer = null,
                onAnswerSelected = { },
                onNavigateBack = onNavigateBack
            )
            GameResultDialog(
                isComplete = true,
                correctAnswers = state.gameState.correctAnswers,
                totalQuestions = state.gameState.totalQuestions,
                score = state.gameState.score,
                onPlayAgain = { viewModel.playAgain(difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is PictureMatchUiState.Error -> {
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
private fun PictureMatchGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.PictureMatchGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    correctAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Picture Match",
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

                // Streak indicator
                if (gameState.streakCount >= 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Text(
                                text = "🔥 ${gameState.streakCount} Streak! Keep going!",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                // Display card with meaning/word
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = question.displayText,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 28.sp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Instruction
                item {
                    val instructionText = when (question.questionType) {
                        com.gultekinahmetabdullah.trainvoc.games.PictureMatchGame.QuestionType.MEANING_TO_WORD ->
                            "Select the English word for this meaning:"
                        com.gultekinahmetabdullah.trainvoc.games.PictureMatchGame.QuestionType.WORD_TO_MEANING ->
                            "Select the Turkish meaning:"
                    }
                    Text(
                        text = instructionText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Options
                items(question.options, key = { it }) { option ->
                    val isSelected = option == selectedAnswer
                    val isTheCorrectAnswer = isCorrect == false && option == correctAnswer

                    OptionButton(
                        text = option,
                        isSelected = isSelected,
                        isCorrect = if (isSelected) isCorrect else null,
                        isTheCorrectAnswer = isTheCorrectAnswer,
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
                        StatChip(
                            label = "Accuracy",
                            value = "${gameState.accuracy.toInt()}%"
                        )
                        StatChip(
                            label = "Best Streak",
                            value = "${gameState.bestStreak}"
                        )
                    }
                }
            }
        }
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
