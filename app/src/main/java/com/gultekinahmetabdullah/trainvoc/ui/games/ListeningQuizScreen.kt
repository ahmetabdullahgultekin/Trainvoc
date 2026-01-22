package com.gultekinahmetabdullah.trainvoc.ui.games

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun ListeningQuizScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: ListeningQuizViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Initialize TTS
    var ttsInstance: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                localTts?.let { tts ->
                    ttsInstance = tts
                    viewModel.setTextToSpeech(tts)
                }
            }
        }
        ttsInstance = localTts
        localTts?.let { viewModel.setTextToSpeech(it) }

        onDispose {
            ttsInstance?.stop()
            ttsInstance?.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.LISTENING_QUIZ)) {
            tutorialViewModel.startTutorial(GameType.LISTENING_QUIZ)
        }
    }

    LaunchedEffect(Unit) {
        if (!showDifficultyDialog) {
            viewModel.startGame(difficulty)
        }
    }

    when (val state = uiState) {
        is ListeningQuizUiState.Loading -> {
            GameLoadingState()
        }

        is ListeningQuizUiState.Playing -> {
            ListeningQuizGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onPlayAudio = { viewModel.playAudio() },
                onReplayAudio = { viewModel.replayAudio() },
                onNavigateBack = onNavigateBack
            )
        }

        is ListeningQuizUiState.ShowingFeedback -> {
            ListeningQuizGameContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                correctAnswer = state.correctAnswer,
                onAnswerSelected = { },
                onPlayAudio = { },
                onReplayAudio = { },
                onNavigateBack = onNavigateBack
            )
        }

        is ListeningQuizUiState.Complete -> {
            ListeningQuizGameContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                onAnswerSelected = { },
                onPlayAudio = { },
                onReplayAudio = { },
                onNavigateBack = onNavigateBack
            )
            GameResultDialog(
                isComplete = true,
                correctAnswers = state.gameState.correctAnswers,
                totalQuestions = state.gameState.totalQuestions,
                score = state.gameState.correctAnswers * 10,
                onPlayAgain = { viewModel.playAgain(difficulty) },
                onMainMenu = onNavigateBack
            )
        }

        is ListeningQuizUiState.Error -> {
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
    if (tutorialState.isActive && tutorialState.gameType == GameType.LISTENING_QUIZ) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun ListeningQuizGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.ListeningQuizGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    correctAnswer: String? = null,
    onAnswerSelected: (String) -> Unit,
    onPlayAudio: () -> Unit,
    onReplayAudio: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion

    GameScreenTemplate(
        title = "Listening Quiz",
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

                // Audio player section
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Audio",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Text(
                                text = "Listen and select the correct ${
                                    when (question.questionType) {
                                        com.gultekinahmetabdullah.trainvoc.games.ListeningQuizGame.QuestionType.WORD_TO_TRANSLATION -> "translation"
                                        com.gultekinahmetabdullah.trainvoc.games.ListeningQuizGame.QuestionType.TRANSLATION_TO_WORD -> "word"
                                        com.gultekinahmetabdullah.trainvoc.games.ListeningQuizGame.QuestionType.WORD_TO_SPELLING -> "spelling"
                                    }
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            // Single unified play button
                            Button(
                                onClick = {
                                    if (!gameState.audioPlayed) {
                                        onPlayAudio()
                                    } else {
                                        onReplayAudio()
                                    }
                                },
                                enabled = !gameState.audioPlayed || gameState.canReplayAudio,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (!gameState.audioPlayed) {
                                        "Play Audio"
                                    } else {
                                        "Replay (${gameState.canReplay} left)"
                                    }
                                )
                            }
                        }
                    }
                }

                // Instruction
                item {
                    Text(
                        text = "Select your answer:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
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
                        onClick = { if (gameState.audioPlayed) onAnswerSelected(option) }
                    )
                }

                // Stats
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
