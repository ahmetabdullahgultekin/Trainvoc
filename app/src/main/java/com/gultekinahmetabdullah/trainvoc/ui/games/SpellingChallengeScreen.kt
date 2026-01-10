package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun SpellingChallengeScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: SpellingChallengeViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.SPELLING_CHALLENGE)) {
            tutorialViewModel.startTutorial(GameType.SPELLING_CHALLENGE)
        }
    }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is SpellingChallengeUiState.Loading -> {
            GameLoadingState()
        }

        is SpellingChallengeUiState.Playing -> {
            SpellingChallengeGameContent(
                gameState = state.gameState,
                isShowingFeedback = false,
                feedbackCorrect = null,
                onInputChange = { viewModel.updateInput(it) },
                onSubmit = { viewModel.submitAnswer() },
                onRevealLetter = { viewModel.revealLetter() },
                onSkip = { viewModel.skipQuestion() },
                onNavigateBack = onNavigateBack
            )
        }

        is SpellingChallengeUiState.ShowingFeedback -> {
            SpellingChallengeGameContent(
                gameState = state.gameState,
                isShowingFeedback = true,
                feedbackCorrect = state.isCorrect,
                onInputChange = { },
                onSubmit = { },
                onRevealLetter = { },
                onSkip = { },
                onNavigateBack = onNavigateBack
            )
        }

        is SpellingChallengeUiState.Complete -> {
            SpellingChallengeGameContent(
                gameState = state.gameState,
                isShowingFeedback = false,
                feedbackCorrect = null,
                onInputChange = { },
                onSubmit = { },
                onRevealLetter = { },
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

        is SpellingChallengeUiState.Error -> {
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
    if (tutorialState.isActive && tutorialState.gameType == GameType.SPELLING_CHALLENGE) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun SpellingChallengeGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.SpellingChallengeGame.GameState,
    isShowingFeedback: Boolean,
    feedbackCorrect: Boolean?,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRevealLetter: () -> Unit,
    onSkip: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Spelling Challenge",
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

                // Turkish prompt
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
                                text = "Spell this word in English:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = question.prompt,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Word length hint
                item {
                    val hint = com.gultekinahmetabdullah.trainvoc.games.SpellingChallengeGame.getWordLengthHint(question)

                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                // Input field
                item {
                    val backgroundColor = when {
                        isShowingFeedback && feedbackCorrect == true -> Color(0xFF10B981)
                        isShowingFeedback && feedbackCorrect == false -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    OutlinedTextField(
                        value = gameState.currentInput,
                        onValueChange = onInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type your answer...") },
                        singleLine = true,
                        enabled = !isShowingFeedback,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = backgroundColor.copy(alpha = 0.1f),
                            unfocusedContainerColor = backgroundColor.copy(alpha = 0.1f)
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Feedback message
                if (isShowingFeedback) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (feedbackCorrect == true)
                                    Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        ) {
                            Text(
                                text = if (feedbackCorrect == true) {
                                    "Correct! ${question.correctSpelling}"
                                } else {
                                    "Incorrect. Correct spelling: ${question.correctSpelling}"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
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
                        // Hint button
                        OutlinedButton(
                            onClick = onRevealLetter,
                            modifier = Modifier.weight(1f),
                            enabled = !isShowingFeedback && gameState.revealedLetters.size < question.correctSpelling.length
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "Show hint")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hint")
                        }

                        // Submit button
                        Button(
                            onClick = onSubmit,
                            modifier = Modifier.weight(1f),
                            enabled = !isShowingFeedback && gameState.currentInput.isNotBlank()
                        ) {
                            Text("Submit")
                        }

                        // Skip button
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f),
                            enabled = !isShowingFeedback
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
                            label = "Perfect",
                            value = "${gameState.perfectSpellings}"
                        )
                        StatChip(
                            label = "Hints Used",
                            value = "${gameState.revealedLetters.size}"
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
