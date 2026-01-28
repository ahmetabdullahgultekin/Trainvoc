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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType
import com.gultekinahmetabdullah.trainvoc.ui.tutorial.TutorialOverlay
import com.gultekinahmetabdullah.trainvoc.viewmodel.TutorialViewModel

@Composable
fun FillInTheBlankScreen(
    onNavigateBack: () -> Unit,
    difficulty: String = "medium",
    viewModel: FillInTheBlankViewModel = hiltViewModel(),
    tutorialViewModel: TutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    var showDifficultyDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (tutorialViewModel.isFirstPlay(GameType.FILL_IN_BLANK)) {
            tutorialViewModel.startTutorial(GameType.FILL_IN_BLANK)
        }
    }

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
            val hintText = state.gameState.currentQuestion?.let { viewModel.getHint(it) } ?: ""
            FillInTheBlankContent(
                gameState = state.gameState,
                selectedAnswer = null,
                isCorrect = null,
                showHint = showHint,
                hintText = hintText,
                onAnswerSelected = { viewModel.selectAnswer(it) },
                onHintToggle = { viewModel.toggleHint() },
                onNavigateBack = onNavigateBack
            )
        }

        is FillInTheBlankUiState.ShowingFeedback -> {
            val hintText = state.gameState.currentQuestion?.let { viewModel.getHint(it) } ?: ""
            FillInTheBlankContent(
                gameState = state.gameState,
                selectedAnswer = state.selectedAnswer,
                isCorrect = state.isCorrect,
                showHint = showHint,
                hintText = hintText,
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
                hintText = "",
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

    // Tutorial overlay
    if (tutorialState.isActive && tutorialState.gameType == GameType.FILL_IN_BLANK) {
        TutorialOverlay(
            state = tutorialState,
            onNextStep = { tutorialViewModel.nextStep() },
            onSkip = { tutorialViewModel.skipTutorial() },
            onComplete = { tutorialViewModel.completeTutorial() }
        )
    }
}

@Composable
private fun FillInTheBlankContent(
    gameState: com.gultekinahmetabdullah.trainvoc.games.FillInTheBlankGame.GameState,
    selectedAnswer: String?,
    isCorrect: Boolean?,
    showHint: Boolean,
    hintText: String,
    onAnswerSelected: (String) -> Unit,
    onHintToggle: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val question = gameState.currentQuestion
    var userInput by remember(gameState.currentQuestionIndex) { mutableStateOf("") }
    var hasSubmitted by remember(gameState.currentQuestionIndex) { mutableStateOf(false) }

    // Update hasSubmitted when feedback is shown
    LaunchedEffect(isCorrect) {
        if (isCorrect != null) {
            hasSubmitted = true
        }
    }

    GameScreenTemplate(
        title = stringResource(id = R.string.fill_in_blank),
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
                        text = stringResource(id = R.string.question_counter, gameState.currentQuestionIndex + 1, gameState.totalQuestions),
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
                                text = stringResource(id = R.string.type_missing_word),
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

                // Text input field
                item {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { if (!hasSubmitted) userInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !hasSubmitted,
                        label = { Text(stringResource(id = R.string.your_answer)) },
                        placeholder = { Text(stringResource(id = R.string.type_the_word)) },
                        singleLine = true,
                        isError = isCorrect == false,
                        supportingText = if (isCorrect == false) {
                            { Text(stringResource(id = R.string.correct_answer_display, question.correctAnswer), color = MaterialTheme.colorScheme.error) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isCorrect == true) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = when (isCorrect) {
                                true -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                false -> MaterialTheme.colorScheme.error
                                null -> MaterialTheme.colorScheme.outline
                            }
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = {
                                if (userInput.isNotBlank() && !hasSubmitted) {
                                    onAnswerSelected(userInput.trim())
                                }
                            }
                        )
                    )
                }

                // Submit button
                item {
                    Button(
                        onClick = {
                            if (userInput.isNotBlank() && !hasSubmitted) {
                                onAnswerSelected(userInput.trim())
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = userInput.isNotBlank() && !hasSubmitted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (isCorrect) {
                                true -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                false -> MaterialTheme.colorScheme.error
                                null -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        val correctResultText = stringResource(id = R.string.correct_result)
                        val incorrectResultText = stringResource(id = R.string.incorrect_result)
                        val submitText = stringResource(id = R.string.submit)
                        Text(
                            text = when (isCorrect) {
                                true -> correctResultText
                                false -> incorrectResultText
                                null -> submitText
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Hint button
                item {
                    OutlinedButton(
                        onClick = onHintToggle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = stringResource(id = R.string.content_desc_toggle_hint))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (showHint) stringResource(id = R.string.hide_hint) else stringResource(id = R.string.show_hint))
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
                                text = hintText,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
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
                                text = stringResource(id = R.string.accuracy),
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
