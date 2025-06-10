package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel

@Composable
fun QuizScreen(quizViewModel: QuizViewModel, onQuit: (() -> Unit)? = null) {
    val question by quizViewModel.currentQuestion.collectAsState()
    val progress by quizViewModel.progress.collectAsState()
    val isTimeUp by quizViewModel.isTimeOver.collectAsState()
    val score by quizViewModel.score.collectAsState()
    val currentStats by quizViewModel.currentWordStats.collectAsState()
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showStats by rememberSaveable { mutableStateOf(false) }

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
            // Info baloncuğu ve ikon
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    showStats = !showStats
                }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(id = R.string.word_stats),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            AnimatedVisibility(
                visible = showStats && currentStats != null,
                enter = expandIn(
                    expandFrom = Alignment.TopEnd
                ) + fadeIn(),
                exit = shrinkOut(
                    shrinkTowards = Alignment.TopEnd
                ) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(
                                id = R.string.word_stats, question?.correctWord?.word ?: ""
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                id = R.string.correct
                            )
                                    + ": ${currentStats!!.correctCount}"
                        )
                        Text(
                            text = stringResource(
                                id = R.string.wrong
                            )
                                    + ": ${currentStats!!.wrongCount}"
                        )
                        Text(
                            text = stringResource(
                                id = R.string.skipped
                            )
                                    + ": ${currentStats!!.skippedCount}"
                        )
                        Text(
                            text = stringResource(
                                id = R.string.learned
                            )
                                    + ": " + if (currentStats!!.learned) stringResource(id = R.string.yes)
                            else stringResource(id = R.string.no)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    ScoreTitle(score = score)
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    // Animated Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            color = progressColor,
                            trackColor = Color.Transparent
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    if (question == null) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(id = R.string.loading_questions),
                            fontSize = 18.sp
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.which_one_is_the_correct_meaning),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "'${question!!.correctWord.word}'",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Animated Answer Buttons
                        question!!.choices.forEach { choice ->
                            val backgroundColor by animateColorAsState(
                                targetValue = when {
                                    selectedAnswer == choice && isCorrect == true -> Color(
                                        0xFF66BB6A
                                    ).copy(
                                        alpha = 0.7f
                                    )

                                    selectedAnswer == choice && isCorrect == false -> MaterialTheme.colorScheme.error.copy(
                                        alpha = 0.7f
                                    )

                                    choice == question!!.correctWord && isCorrect == false -> Color(
                                        0xFF66BB6A
                                    ).copy(alpha = 0.7f)

                                    isTimeUp && choice == question!!.correctWord -> Color(0xFF66BB6A).copy(
                                        alpha = 0.9f
                                    )

                                    else -> MaterialTheme.colorScheme.primaryContainer
                                },
                                animationSpec = tween(400), label = ""
                            )

                            val scaleAnim by animateFloatAsState(
                                targetValue = if (selectedAnswer == choice) 1.08f else 1f,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = ""
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .scale(scaleAnim)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(
                                        enabled = selectedAnswer == null && isCorrect == null && !isTimeUp
                                    ) {
                                        selectedAnswer = choice
                                        isCorrect = quizViewModel.checkAnswer(choice)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = choice.meaning,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (selectedAnswer == choice) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(if (isCorrect == true) Color(0xFF66BB6A) else MaterialTheme.colorScheme.error),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Close,
                                                contentDescription = if (isCorrect == true) "Correct" else "Wrong",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Next Button
                        Button(
                            onClick = {
                                selectedAnswer = null
                                isCorrect = null
                                quizViewModel.loadNextQuestion()
                            },
                            enabled = selectedAnswer != null || isTimeUp,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.3f
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
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

    BackHandler {
        showExitDialog = true
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(id = R.string.quit_quiz_title)) },
            text = { Text(stringResource(id = R.string.quit_quiz_message)) },
            confirmButton = {
                Button(onClick = {
                    showExitDialog = false
                    onQuit?.invoke()
                }) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { showExitDialog = false }) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}

@Composable
fun ScoreTitle(score: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.score) + ":",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}