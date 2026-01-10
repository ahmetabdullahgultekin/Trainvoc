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

@Composable
fun ListeningQuizScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: ListeningQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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
}

@Composable
private fun ListeningQuizGameContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.ListeningQuizGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
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

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Play button
                                Button(
                                    onClick = onPlayAudio,
                                    enabled = !gameState.audioPlayed || gameState.canReplayAudio
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (!gameState.audioPlayed) "Play" else "Replay")
                                }

                                // Replay counter
                                if (gameState.audioPlayed) {
                                    OutlinedButton(
                                        onClick = onReplayAudio,
                                        enabled = gameState.canReplayAudio
                                    ) {
                                        Text("Replays: ${gameState.canReplay}")
                                    }
                                }
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
                    val showCorrect = isSelected && isCorrect == true
                    val showIncorrect = isSelected && isCorrect == false

                    OptionButton(
                        text = option,
                        isSelected = isSelected,
                        isCorrect = if (isSelected) isCorrect else null,
                        onClick = { if (gameState.audioPlayed) onAnswerSelected(option) }
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
                            label = "Replays Left",
                            value = "${gameState.canReplay}"
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
