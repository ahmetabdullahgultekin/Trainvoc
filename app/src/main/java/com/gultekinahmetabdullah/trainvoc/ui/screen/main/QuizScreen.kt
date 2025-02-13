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
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel

@Composable
fun QuizScreen(quizViewModel: QuizViewModel, onBack: () -> Unit) {
    val question by quizViewModel.currentQuestion.collectAsState()
    val progress by quizViewModel.progress.collectAsState()
    val hasStarted by quizViewModel.isTimeRunning.collectAsState(initial = false)
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    /*
    LaunchedEffect(Unit) {
        if (!hasStarted) {
            quizViewModel.startQuiz(quiz = quiz)
        }
    }
     */

    DisposableEffect(Unit) {
        onDispose {
            quizViewModel.finalizeQuiz()
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
            // Timer Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
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
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
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

                            // Show the correct answer whatever the user selected
                            selectedAnswer != null && choice == question!!.correctWord -> Color.Green.copy(
                                alpha = 0.8f
                            )

                            selectedAnswer == choice && isCorrect == true -> Color.Green.copy(
                                alpha = 0.8f
                            )

                            selectedAnswer == choice && isCorrect == false -> Color.Red.copy(
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
                                if (selectedAnswer != null || isCorrect != null) return@clickable
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
                    enabled = selectedAnswer != null,
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