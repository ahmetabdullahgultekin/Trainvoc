package com.gultekinahmetabdullah.trainvoc.ui.screen.extra

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel

@Composable
fun StatsScreen(statsViewModel: StatsViewModel) {
    val totalQuestions by statsViewModel.totalQuestions.collectAsState()
    val correctAnswers by statsViewModel.correctAnswers.collectAsState()
    val incorrectAnswers by statsViewModel.incorrectAnswers.collectAsState()
    val successPercentage by statsViewModel.successPercentage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Quiz Statistics", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        StatCard(title = "Total Questions", value = "$totalQuestions")
        StatCard(title = "Correct Answers", value = "$correctAnswers")
        StatCard(title = "Incorrect Answers", value = "$incorrectAnswers")
        StatCard(title = "Success Rate", value = "%.2f%%".format(successPercentage))

        Spacer(modifier = Modifier.height(32.dp))

        StatsBarChart(correctAnswers, incorrectAnswers)
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
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = Color.Blue)
        }
    }
}

@Composable
fun StatsBarChart(correct: Int, incorrect: Int) {
    val total = correct + incorrect
    val correctPercentage = if (total == 0) 0f else correct.toFloat() / total
    val incorrectPercentage = if (total == 0) 0f else incorrect.toFloat() / total

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Answer Distribution", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(if (total == 0) 0.01f else correctPercentage)
                    .height(30.dp)
                    .background(Color.Green, RoundedCornerShape(8.dp))
            )

            Box(
                modifier = Modifier
                    .weight(if (total == 0) 0.01f else incorrectPercentage)
                    .height(30.dp)
                    .background(Color.Red, RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Correct: $correct", color = Color.Green)
            Text(text = "Incorrect: $incorrect", color = Color.Red)
        }
    }
}

