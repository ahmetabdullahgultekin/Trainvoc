package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun WordScrambleScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: WordScrambleViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    val currentInput by viewModel.currentInput.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.WORD_SCRAMBLE)) {
            tutorialViewModel.startTutorial(GameType.WORD_SCRAMBLE)
        }
    }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is WordScrambleUiState.Loading -> {
            GameLoadingState()
        }

        is WordScrambleUiState.Playing -> {
            WordScrambleContent(
                gameState = state.gameState,
                currentInput = currentInput,
                feedbackMessage = null,
                onInputChange = { viewModel.updateInput(it) },
                onSubmit = { viewModel.submitAnswer() },
                onHint = { viewModel.useHint() },
                onSkip = { viewModel.skipQuestion() },
                onNavigateBack = onNavigateBack
            )
        }

        is WordScrambleUiState.ShowingFeedback -> {
            WordScrambleContent(
                gameState = state.gameState,
                currentInput = state.answer,
                feedbackMessage = if (state.isCorrect) "Correct! ✓" else "Incorrect! ✗",
                onInputChange = { },
                onSubmit = { },
                onHint = { },
                onSkip = { },
                onNavigateBack = onNavigateBack
            )
        }

        is WordScrambleUiState.Complete -> {
            WordScrambleContent(
                gameState = state.gameState,
                currentInput = "",
                feedbackMessage = null,
                onInputChange = { },
                onSubmit = { },
                onHint = { },
                onSkip = { },
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

        is WordScrambleUiState.Error -> {
            ErrorStateGeneric(
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
    if (tutorialState.isActive && tutorialState.gameType == GameType.WORD_SCRAMBLE) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun WordScrambleContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.WordScrambleGame.GameState,
    currentInput: String,
    feedbackMessage: String?,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onHint: () -> Unit,
    onSkip: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Word Scramble",
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

                // Scrambled word
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
                                text = "Unscramble this word:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = question.scrambledWord,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = MaterialTheme.typography.headlineLarge.letterSpacing * 1.5,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hint: ${question.hint}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Input field
                item {
                    GameTextField(
                        value = currentInput,
                        onValueChange = onInputChange,
                        placeholder = "Type your answer...",
                        onSubmit = onSubmit,
                        isError = feedbackMessage?.contains("Incorrect") == true
                    )
                }

                // Feedback message
                if (feedbackMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (feedbackMessage.contains("Correct"))
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = feedbackMessage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Action buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Skip question")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skip")
                        }
                        HintButton(
                            onClick = onHint,
                            hintsRemaining = 99, // Unlimited
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentInput.isNotBlank() && feedbackMessage == null
                    ) {
                        Text("Submit Answer")
                    }
                }

                // Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatsCard(
                            label = "Correct",
                            value = "${gameState.correctAnswers}"
                        )
                        StatsCard(
                            label = "Accuracy",
                            value = "${gameState.accuracy.toInt()}%"
                        )
                        StatsCard(
                            label = "Hints",
                            value = "${gameState.hintsUsed}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(label: String, value: String) {
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

