package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.AnswerOptionCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizExitDialog
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizQuestionCard
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizScoreCard
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizStatsCard
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel,
    onQuit: (() -> Unit)
) {
    val question by quizViewModel.currentQuestion.collectAsState()
    val progress by quizViewModel.progress.collectAsState()
    val isTimeUp by quizViewModel.isTimeOver.collectAsState()
    val score by quizViewModel.score.collectAsState()
    val currentStats by quizViewModel.currentWordStats.collectAsState()
    val quizParameter by quizViewModel.quizParameter.collectAsState()
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showStats by rememberSaveable { mutableStateOf(false) }

    // Finalize quiz when app goes to background
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE) {
                onQuit.invoke()
                quizViewModel.finalizeQuiz()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            quizViewModel.finalizeQuiz()
        }
    }

    val progressColor by animateColorAsState(
        targetValue = if (selectedAnswer != null && isCorrect != null) {
            if (isCorrect == true) Color(0xFF66BB6A) else MaterialTheme.colorScheme.error
        } else {
            if (isTimeUp) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_2),
                contentScale = ContentScale.FillBounds,
                alpha = 0.10f
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Info icon to toggle stats display
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = { showStats = !showStats }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(id = R.string.word_stats),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            QuizStatsCard(
                showStats = showStats,
                currentStats = currentStats,
                quizParameter = quizParameter,
                question = question,
                quizViewModel = quizViewModel
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.mediumLarge),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    QuizScoreCard(score = score)
                }
                item {
                    Spacer(modifier = Modifier.height(Spacing.small))
                }
                item {
                    // Animated Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Spacing.mediumLarge)
                            .clip(RoundedCornerShape(CornerRadius.small))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(CornerRadius.small)),
                            color = progressColor,
                            trackColor = Color.Transparent
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(Spacing.small))
                }
                item {
                    if (question == null) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(id = R.string.loading_questions),
                            fontSize = 18.sp
                        )
                    } else {
                        val currentQuestion = question!!
                        QuizQuestionCard(word = currentQuestion.correctWord.word)

                        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

                        // Answer options
                        currentQuestion.choices.forEach { choice ->
                            AnswerOptionCard(
                                choice = choice,
                                correctWord = currentQuestion.correctWord,
                                selectedAnswer = selectedAnswer,
                                isCorrect = isCorrect,
                                isTimeUp = isTimeUp,
                                onChoiceClick = { selectedChoice ->
                                    selectedAnswer = selectedChoice
                                    isCorrect = quizViewModel.checkAnswer(selectedChoice)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.large))

                        // Next Button
                        Button(
                            onClick = {
                                selectedAnswer = null
                                isCorrect = null
                                quizViewModel.loadNextQuestion()
                            },
                            enabled = selectedAnswer != null || isTimeUp,
                            shape = RoundedCornerShape(CornerRadius.medium),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.3f
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.small)
                                .height(48.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.next_question),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Handle back button press
    BackHandler {
        showExitDialog = true
    }

    // Register exit handler for QuizScreen
    DisposableEffect(Unit) {
        val callback = {
            showExitDialog = true
        }
        QuizScreenExitHandler.register(callback)
        onDispose {
            QuizScreenExitHandler.unregister(callback)
        }
    }

    // Show exit confirmation dialog
    if (showExitDialog) {
        QuizExitDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                onQuit.invoke()
                quizViewModel.finalizeQuiz()
                showExitDialog = false
            }
        )
    }
}