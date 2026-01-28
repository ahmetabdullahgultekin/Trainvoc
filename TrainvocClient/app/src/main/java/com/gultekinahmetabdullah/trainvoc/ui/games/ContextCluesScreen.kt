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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun ContextCluesScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: ContextCluesViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.CONTEXT_CLUES)) {
            tutorialViewModel.startTutorial(GameType.CONTEXT_CLUES)
        }
    }

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
                correctAnswer = null,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onRevealHint = { viewModel.revealHint() },
                onSkip = { viewModel.skipQuestion() },
                onNavigateBack = onNavigateBack
            )
        }

        is ContextCluesUiState.ShowingFeedback -> {
            ContextCluesGameContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                correctAnswer = state.gameState.currentQuestion?.correctAnswer,
                onAnswerSelected = { },
                onRevealHint = { },
                onSkip = { },
                onNavigateBack = onNavigateBack
            )
        }

        is ContextCluesUiState.Complete -> {
            ContextCluesGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                correctAnswer = null,
                onAnswerSelected = { },
                onRevealHint = { },
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

    // Tutorial overlay
    if (tutorialState.isActive && tutorialState.gameType == GameType.CONTEXT_CLUES) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun ContextCluesGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    correctAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onRevealHint: () -> Unit,
    onSkip: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = stringResource(id = R.string.word_detective),
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
                        text = stringResource(id = R.string.question_counter, gameState.currentQuestionIndex + 1, gameState.totalQuestions),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Instruction
                item {
                    Text(
                        text = stringResource(id = R.string.use_hints_instruction),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Hints Card - Show revealed hints progressively
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = stringResource(id = R.string.content_desc_hints),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.hints_counter, question.hintsRevealed, question.hints.size),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            // Show revealed hints
                            question.currentHints.forEachIndexed { index, hint ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = hint,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Question prompt
                item {
                    Text(
                        text = stringResource(id = R.string.what_is_turkish_meaning),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
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

                // Action buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Reveal more hints button
                        OutlinedButton(
                            onClick = onRevealHint,
                            modifier = Modifier.weight(1f),
                            enabled = gameState.canRevealMoreHints
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = stringResource(id = R.string.content_desc_reveal_hint))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(id = R.string.more_hints))
                        }

                        // Skip button
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = stringResource(id = R.string.content_desc_skip_question))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(id = R.string.skip))
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
                            label = stringResource(id = R.string.accuracy),
                            value = "${gameState.accuracy.toInt()}%"
                        )
                        StatChip(
                            label = stringResource(id = R.string.clues_used),
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
    val comprehensionLevel = com.gultekinahmetabdullah.trainvoc.games.ContextCluesGame.getComprehensionLevel(gameState)

    val correctAnswersLabel = stringResource(id = R.string.correct_answers)
    val accuracyLabel = stringResource(id = R.string.accuracy)
    val cluesUsedLabel = stringResource(id = R.string.clues_used)
    val scoreLabel = stringResource(id = R.string.score)

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(id = R.string.game_complete),
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                StatRow(label = correctAnswersLabel, value = "${gameState.correctAnswers}/${gameState.totalQuestions}")
                StatRow(label = accuracyLabel, value = "${gameState.accuracy.toInt()}%")
                StatRow(label = cluesUsedLabel, value = "${gameState.cluesUsed}")
                StatRow(label = scoreLabel, value = "${gameState.score}")
            }
        },
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text(stringResource(id = R.string.play_again))
            }
        },
        dismissButton = {
            TextButton(onClick = onMainMenu) {
                Text(stringResource(id = R.string.main_menu))
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
