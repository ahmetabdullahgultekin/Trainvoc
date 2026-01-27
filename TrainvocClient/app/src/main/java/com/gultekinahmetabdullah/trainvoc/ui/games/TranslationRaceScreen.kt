package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun TranslationRaceScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: TranslationRaceViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.TRANSLATION_RACE)) {
            tutorialViewModel.startTutorial(GameType.TRANSLATION_RACE)
        }
    }

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
                correctAnswer = null,
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
                correctAnswer = state.correctAnswer,
                onAnswerSelected = { },
                onNavigateBack = onNavigateBack
            )
        }

        is TranslationRaceUiState.Complete -> {
            TranslationRaceGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                correctAnswer = null,
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

    // Tutorial overlay
    if (tutorialState.isActive && tutorialState.gameType == GameType.TRANSLATION_RACE) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun TranslationRaceGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    correctAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = stringResource(id = R.string.translation_race),
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
                                text = stringResource(id = R.string.combo_display, gameState.combo),
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
                    val englishFlag = stringResource(id = R.string.english_flag)
                    val turkishFlag = stringResource(id = R.string.turkish_flag)
                    val mixedDirection = stringResource(id = R.string.mixed_direction)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (from, to) = when (question.direction) {
                            com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.TranslationDirection.ENGLISH_TO_TURKISH ->
                                Pair(englishFlag, turkishFlag)
                            com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.TranslationDirection.TURKISH_TO_ENGLISH ->
                                Pair(turkishFlag, englishFlag)
                            else -> Pair(mixedDirection, mixedDirection)
                        }

                        Text(
                            text = from,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(id = R.string.content_desc_translation_direction),
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
                                text = stringResource(id = R.string.translate_label),
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
                        StatChip(label = stringResource(id = R.string.correct_count), value = "${gameState.correctAnswers}")
                        StatChip(label = stringResource(id = R.string.combo), value = "${gameState.maxCombo}x")
                        StatChip(label = stringResource(id = R.string.apm), value = "${gameState.answersPerMinute.toInt()}")
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
    val rating = com.gultekinahmetabdullah.trainvoc.games.TranslationRaceGame.getPerformanceRating(gameState)

    val correctAnswersLabel = stringResource(id = R.string.correct_answers)
    val totalAnsweredLabel = stringResource(id = R.string.total_answered)
    val accuracyLabel = stringResource(id = R.string.accuracy)
    val maxComboLabel = stringResource(id = R.string.max_combo)
    val answersPerMinLabel = stringResource(id = R.string.answers_per_min)
    val scoreLabel = stringResource(id = R.string.score)

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(id = R.string.race_complete),
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
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                StatRow(label = correctAnswersLabel, value = "${gameState.correctAnswers}")
                StatRow(label = totalAnsweredLabel, value = "${gameState.correctAnswers + gameState.incorrectAnswers}")
                StatRow(label = accuracyLabel, value = "${gameState.accuracy.toInt()}%")
                StatRow(label = maxComboLabel, value = "${gameState.maxCombo}x")
                StatRow(label = answersPerMinLabel, value = "${gameState.answersPerMinute.toInt()}")
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
