package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.ui.animations.ConfettiAnimation
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.AnswerOptionCard
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizExitDialog
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizQuestionCard
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizScoreCard
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components.QuizStatsCard
import com.gultekinahmetabdullah.trainvoc.ui.components.RollingCatLoaderWithText
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize
import com.gultekinahmetabdullah.trainvoc.ui.animations.rememberHapticPerformer
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.ComponentSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.Success
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val timeLeft by quizViewModel.timeLeft.collectAsState()
    val currentQuestionNumber by quizViewModel.currentQuestionNumber.collectAsState()
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showStats by rememberSaveable { mutableStateOf(false) }
    var currentStreak by remember { mutableStateOf(0) }
    var triggerConfetti by remember { mutableStateOf(false) }

    // Coroutine scope for confetti animation
    val coroutineScope = rememberCoroutineScope()

    // Haptic feedback for answer responses
    val haptic = rememberHapticPerformer()

    // Auto-advance to next question after answer is checked
    LaunchedEffect(isCorrect) {
        if (isCorrect != null) {
            // Delay based on answer correctness
            val delayTime = if (isCorrect == true) 1000L else 2000L
            delay(delayTime)

            // Reset state and load next question
            selectedAnswer = null
            isCorrect = null
            quizViewModel.loadNextQuestion()
        }
    }

    // Consolidated effect: lifecycle observation + exit handler + cleanup
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE) {
                onQuit.invoke()
                quizViewModel.finalizeQuiz()
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        val exitCallback = {
            showExitDialog = true
        }
        QuizScreenExitHandler.register(exitCallback)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            QuizScreenExitHandler.unregister(exitCallback)
            quizViewModel.finalizeQuiz()
        }
    }

    // Animated progress bar color with spring animation
    val progressColor by animateColorAsState(
        targetValue = if (selectedAnswer != null && isCorrect != null) {
            if (isCorrect == true) Success else MaterialTheme.colorScheme.error
        } else {
            if (isTimeUp) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = AnimationDuration.quick),
        label = "progressColor"
    )

    // Animated progress value with spring animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "progressAnimation"
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
            // Confetti overlay for streak correct answers
            AnimatedVisibility(visible = triggerConfetti) {
                ConfettiAnimation(
                    trigger = triggerConfetti,
                    modifier = Modifier.fillMaxSize()
                )
            }

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
                    QuizScoreCard(
                        score = score,
                        streak = currentStreak
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(Spacing.small))
                }
                item {
                    // Question Counter and Timer Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.small),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Question counter
                        Text(
                            text = stringResource(id = R.string.question_number, currentQuestionNumber),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Timer display
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val timerColor = when {
                                timeLeft <= 10 -> MaterialTheme.colorScheme.error
                                timeLeft <= 20 -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            }
                            Text(
                                text = "\u23F1",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${timeLeft}s",
                                style = MaterialTheme.typography.titleMedium,
                                color = timerColor
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(Spacing.small))
                }
                item {
                    // Animated Progress Bar - 4dp height with spring animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ComponentSize.progressBarHeight)
                            .clip(RoundedCornerShape(CornerRadius.small))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.xl),
                            contentAlignment = Alignment.Center
                        ) {
                            RollingCatLoaderWithText(
                                message = stringResource(id = R.string.loading_questions),
                                size = LoaderSize.large
                            )
                        }
                    } else {
                        val currentQuestion = question!!
                        QuizQuestionCard(word = currentQuestion.correctWord.word)

                        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

                        // Answer options
                        currentQuestion.choices.forEach { choice ->
                            key(choice.word) {
                                AnswerOptionCard(
                                    choice = choice,
                                    correctWord = currentQuestion.correctWord,
                                    selectedAnswer = selectedAnswer,
                                    isCorrect = isCorrect,
                                    isTimeUp = isTimeUp,
                                    onChoiceClick = { selectedChoice ->
                                        selectedAnswer = selectedChoice
                                        isCorrect = quizViewModel.checkAnswer(selectedChoice)

                                        // Update streak and trigger confetti
                                        if (isCorrect == true) {
                                            currentStreak++
                                            haptic.success()

                                            // Trigger confetti if streak >= 3
                                            if (currentStreak >= 3) {
                                                triggerConfetti = true
                                                // Reset confetti trigger after animation
                                                coroutineScope.launch {
                                                    delay(3000)
                                                    triggerConfetti = false
                                                }
                                            }
                                        } else {
                                            currentStreak = 0
                                            triggerConfetti = false
                                            haptic.error()
                                        }
                                    }
                                )
                            }
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