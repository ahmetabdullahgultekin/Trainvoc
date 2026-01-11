package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Last Quiz Results Screen
 *
 * Shows summary of the last quiz completed
 * Features:
 * - Total questions, correct/wrong/skipped
 * - Score and accuracy percentage
 * - Time taken
 * - Retry quiz option
 * - Review missed words option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastQuizResultsScreen(
    onBackClick: () -> Unit = {},
    onRetryQuiz: () -> Unit = {},
    onReviewMissed: () -> Unit = {}
) {
    // TODO: Load actual quiz results from database
    // Placeholder data for now
    val totalQuestions = 10
    val correctAnswers = 7
    val wrongAnswers = 2
    val skippedQuestions = 1
    val accuracy = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
    val timeTaken = "2:45"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Last Quiz Results") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quiz Completed!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$accuracy%",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Accuracy",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        QuickStat(Icons.Default.CheckCircle, correctAnswers.toString(), "Correct")
                        QuickStat(Icons.Default.Cancel, wrongAnswers.toString(), "Wrong")
                        QuickStat(Icons.Default.SkipNext, skippedQuestions.toString(), "Skipped")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    DetailRow("Total Questions", "$totalQuestions")
                    DetailRow("Time Taken", timeTaken)
                    DetailRow("Correct Answers", "$correctAnswers")
                    DetailRow("Wrong Answers", "$wrongAnswers")
                    DetailRow("Skipped Questions", "$skippedQuestions")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRetryQuiz,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, "Retry")
                    Spacer(Modifier.width(8.dp))
                    Text("Retry This Quiz")
                }

                OutlinedButton(
                    onClick = onReviewMissed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Assignment, "Review")
                    Spacer(Modifier.width(8.dp))
                    Text("Review Missed Words")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        "Info",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Practice makes perfect! Keep reviewing missed words to improve your score.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
