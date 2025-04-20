package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import kotlinx.coroutines.launch

@Composable
fun StatsScreen(statsViewModel: StatsViewModel) {
    val correctAnswers by statsViewModel.correctAnswers.collectAsState()
    val incorrectAnswers by statsViewModel.incorrectAnswers.collectAsState()
    val skippedQuestions by statsViewModel.skippedQuestions.collectAsState()
    val totalQuestions by statsViewModel.totalQuestions.collectAsState()
    val successRate by statsViewModel.successRatio.collectAsState()
    val failureRate by statsViewModel.failureRatio.collectAsState()
    val skippedRate by statsViewModel.skippedRatio.collectAsState()
    val totalTimeSpent by statsViewModel.totalTimeSpent.collectAsState()
    val lastAnswered by statsViewModel.lastAnswered.collectAsState()
    val scope = statsViewModel.viewModelScope

    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_book.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 3,
        isPlaying = true,
        speed = 1f,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_1), // Replace with your image resource
                contentScale = ContentScale.FillBounds
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(256.dp),
                    progress = { progress }
                )
            }
            item {
                Text(text = "Quiz Statistics", style = MaterialTheme.typography.headlineMedium)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            items(
                listOf(
                    "Total Questions" to "$totalQuestions",
                    "Correct Answers" to "$correctAnswers",
                    "Incorrect Answers" to "$incorrectAnswers",
                    "Skipped Questions" to "$skippedQuestions",
                    "Success Rate" to "%.2f%%".format(successRate * 100),
                    "Failure Rate" to "%.2f%%".format(failureRate * 100),
                    "Skipped Rate" to "%.2f%%".format(skippedRate * 100),
                    "Total Score" to "${correctAnswers * 10}",
                    "Total Time Spent" to "${totalTimeSpent / 60}m ${totalTimeSpent % 60}s",
                    "Last Answered" to lastAnswered
                )
            ) { (title, value) ->
                StatCard(title = title, value = value)
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                StatsBarChart(
                    correctAnswers, incorrectAnswers, skippedQuestions,
                    successRate, failureRate, skippedRate
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                Button(onClick = {
                    scope.launch {
                        statsViewModel.fillStats()
                    }
                }) {
                    Text(text = "Refresh Stats")
                }
            }

        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
        }
    }
}

@Composable
fun StatsBarChart(
    correct: Int,
    incorrect: Int,
    skipped: Int,
    successRate: Float,
    failureRate: Float,
    skippedRate: Float
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Answer Distribution", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(if (successRate <= 0f) 0.01f else successRate)
                    .height(30.dp)
                    .background(Color.Green, RoundedCornerShape(8.dp))
            )
            Box(
                modifier = Modifier
                    .weight(if (skippedRate <= 0f) 0.01f else skippedRate)
                    .height(30.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp))
            )
            Box(
                modifier = Modifier
                    .weight(if (failureRate <= 0f) 0.01f else failureRate)
                    .height(30.dp)
                    .background(Color.Red, RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Correct: $correct", color = Color.Green)
            Text(text = "Skipped: $skipped", color = Color.White)
            Text(text = "Incorrect: $incorrect", color = Color.Red)
        }
    }
}

