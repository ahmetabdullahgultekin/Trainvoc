package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel

@Composable
fun QuizScreen(quizViewModel: QuizViewModel) {
    val question by quizViewModel.currentQuestion.collectAsState()
    val progress by quizViewModel.progress.collectAsState()
    val isTimeUp by quizViewModel.isTimeOver.collectAsState()
    val score by quizViewModel.score.collectAsState()
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            quizViewModel.finalizeQuiz()
        }
    }

    val progressColor by animateColorAsState(
        targetValue = if (selectedAnswer != null && isCorrect != null) {
            if (isCorrect == true) Color.Green else Color.Red
        } else {
            if (isTimeUp) Color.DarkGray else Color(
                red = 1f - progress,
                green = progress,
                blue = 0f
            )
        },
        animationSpec = tween(durationMillis = 500)
    )

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
            // Timer Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            if (question == null) {
                CircularProgressIndicator()
                Text(text = "Loading Questions...", fontSize = 18.sp)
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Which one is the correct meaning of:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "'${question!!.correctWord.word}'",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Answer Buttons
                question!!.choices.forEach { choice ->
                    val backgroundColor by animateColorAsState(
                        targetValue = when {

                            /**
                             *  We have 4 cases here:
                             *
                             *  1. The user has selected an answer and the answer is correct
                             *
                             *  2. The user has selected an answer and the answer is wrong
                             *
                             *  3. The time is up and the correct answer is shown
                             *
                             *  4. The user has not selected an answer yet
                             *
                             *  In the first case, we show the correct answer with a green background
                             *
                             *  In the second case, we show the wrong answer with a red background
                             *  Also, we show the correct answer with a green background
                             *
                             *  In the third case, we show the correct answer with a green background
                             *
                             *  In the fourth case, we show the default background color
                             *
                             */

                            // Show the correct answer with green background when the user selects the correct answer
                            selectedAnswer == choice && isCorrect == true -> Color.Green.copy(
                                alpha = 0.5f
                            )

                            // Show the wrong answer with red background when the user selects the wrong answer
                            selectedAnswer == choice && isCorrect == false -> Color.Red.copy(
                                alpha = 0.5f
                            )
                            // Also, show the correct answer with green background
                            choice == question!!.correctWord && isCorrect == false -> Color.Green.copy(
                                alpha = 0.5f
                            )

                            // Show the correct answer with green background when the time is up
                            isTimeUp && choice == question!!.correctWord -> Color.Green.copy(
                                alpha = 0.8f
                            )

                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                        animationSpec = tween(500), label = ""
                    )

                    val scaleAnim by animateFloatAsState(
                        targetValue = if (selectedAnswer == choice) 1.1f else 1f,
                        animationSpec = tween(300), label = ""
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .scale(scaleAnim)
                            .clickable {
                                if (
                                    selectedAnswer != null
                                    || isCorrect != null
                                    || isTimeUp
                                ) return@clickable
                                selectedAnswer = choice
                                isCorrect = quizViewModel.checkAnswer(choice)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
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
                                Icon(
                                    imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = if (isCorrect == true) "Correct" else "Wrong",
                                    tint = if (isCorrect == true) Color.Green else Color.Red
                                )
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(48.dp)
                ) {
                    Text(text = "Next Question", fontSize = 18.sp)
                }
            }
        }
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
                text = "Score: ",
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