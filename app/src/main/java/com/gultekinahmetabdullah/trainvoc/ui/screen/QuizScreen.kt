package com.gultekinahmetabdullah.trainvoc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel

@Composable
fun QuizScreen(quizViewModel: QuizViewModel = viewModel()) {
    val question by quizViewModel.currentQuestion.collectAsState()

    LaunchedEffect(Unit) {
        quizViewModel.loadQuizQuestions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        question?.let { quizQuestion ->
            Text(
                text = "Word: ${quizQuestion.correctWord.word}",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            quizQuestion.incorrectWords.forEach { choice ->
                TextButton(
                    onClick = { quizViewModel.checkAnswer(choice) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = choice.meaning)
                }
            }
        } ?: run {
            Text("Loading...")
        }

    }
}
