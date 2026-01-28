package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun MultipleChoiceGameScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: MultipleChoiceGameViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    // Check for first play and show tutorial
    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.MULTIPLE_CHOICE)) {
            tutorialViewModel.startTutorial(GameType.MULTIPLE_CHOICE)
        }
    }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is MultipleChoiceUiState.Loading -> {
            GameLoadingState()
        }

        is MultipleChoiceUiState.Playing -> {
            MultipleChoiceGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onNavigateBack = onNavigateBack,
                onHelpClick = { tutorialViewModel.startTutorial(GameType.MULTIPLE_CHOICE) }
            )
        }

        is MultipleChoiceUiState.ShowingFeedback -> {
            MultipleChoiceGameContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                correctAnswer = state.correctAnswer,
                onAnswerSelected = { }, // Disabled during feedback
                onNavigateBack = onNavigateBack,
                onHelpClick = { tutorialViewModel.startTutorial(GameType.MULTIPLE_CHOICE) }
            )
        }

        is MultipleChoiceUiState.Complete -> {
            MultipleChoiceGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { },
                onNavigateBack = onNavigateBack,
                onHelpClick = { tutorialViewModel.startTutorial(GameType.MULTIPLE_CHOICE) }
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

        is MultipleChoiceUiState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = { viewModel.startGame(difficulty) },
                onBack = onNavigateBack
            )
        }
    }

    // Difficulty selection dialog
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
    if (tutorialState.isActive && tutorialState.gameType == GameType.MULTIPLE_CHOICE) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun MultipleChoiceGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.MultipleChoiceGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    correctAnswer: String? = null,
    onAnswerSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onHelpClick: () -> Unit = {}
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = stringResource(id = R.string.multiple_choice_game),
        onNavigateBack = onNavigateBack,
        progress = gameState.currentQuestionIndex.toFloat() / gameState.totalQuestions,
        score = gameState.score,
        onHelpClick = onHelpClick
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

                // Question text
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
                                text = stringResource(id = R.string.translate_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when (question.questionType) {
                                    com.gultekinahmetabdullah.trainvoc.games.QuestionType.WORD_TO_DEFINITION -> question.word.word
                                    com.gultekinahmetabdullah.trainvoc.games.QuestionType.DEFINITION_TO_WORD -> question.word.meaning
                                },
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
                    // Show this option as the correct answer if user was wrong
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
                    val accuracy = if (gameState.currentQuestionIndex > 0) {
                        (gameState.correctAnswers.toFloat() / gameState.currentQuestionIndex * 100).toInt()
                    } else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip(
                            label = stringResource(id = R.string.accuracy),
                            value = "$accuracy%"
                        )
                        StatChip(
                            label = stringResource(id = R.string.difficulty),
                            value = question.difficulty.name.lowercase().capitalize()
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

private fun String.capitalize() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase() else it.toString()
}
