package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Last Quiz Results Screen
 *
 * Shows summary of the last quiz completed with real data from database.
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
    onReviewMissed: () -> Unit = {},
    onWordClick: (String) -> Unit = {},
    viewModel: QuizHistoryViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val lastQuizResult by viewModel.lastQuizResult.collectAsState()
    val missedWords by viewModel.missedWords.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Last Quiz Results") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                ErrorState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = error!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            lastQuizResult == null -> {
                NoQuizState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onStartQuiz = onRetryQuiz
                )
            }
            else -> {
                QuizResultsContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    totalQuestions = lastQuizResult!!.totalQuestions,
                    correctAnswers = lastQuizResult!!.correctAnswers,
                    wrongAnswers = lastQuizResult!!.wrongAnswers,
                    skippedQuestions = lastQuizResult!!.skippedQuestions,
                    accuracy = lastQuizResult!!.accuracy.toInt(),
                    timeTaken = lastQuizResult!!.timeTaken,
                    quizType = lastQuizResult!!.quizType,
                    timestamp = lastQuizResult!!.timestamp,
                    missedWords = missedWords,
                    onRetryQuiz = onRetryQuiz,
                    onReviewMissed = onReviewMissed,
                    onWordClick = onWordClick
                )
            }
        }
    }
}

@Composable
private fun QuizResultsContent(
    modifier: Modifier = Modifier,
    totalQuestions: Int,
    correctAnswers: Int,
    wrongAnswers: Int,
    skippedQuestions: Int,
    accuracy: Int,
    timeTaken: String,
    quizType: String,
    timestamp: Long,
    missedWords: List<com.gultekinahmetabdullah.trainvoc.classes.word.Word>,
    onRetryQuiz: () -> Unit,
    onReviewMissed: () -> Unit,
    onWordClick: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(timestamp))

    LazyColumn(
        modifier = modifier.padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Score Card
        item {
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
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
        }

        // Statistics Card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
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

                    DetailRow("Quiz Type", quizType.replace("_", " "))
                    DetailRow("Total Questions", "$totalQuestions")
                    DetailRow("Time Taken", timeTaken)
                    DetailRow("Correct Answers", "$correctAnswers")
                    DetailRow("Wrong Answers", "$wrongAnswers")
                    DetailRow("Skipped Questions", "$skippedQuestions")
                }
            }
        }

        // Missed Words Section
        if (missedWords.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Missed Words (${missedWords.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        missedWords.take(5).forEach { word ->
                            MissedWordItem(
                                word = word.word,
                                meaning = word.meaning,
                                onClick = { onWordClick(word.word) }
                            )
                        }

                        if (missedWords.size > 5) {
                            Text(
                                text = "+ ${missedWords.size - 5} more words",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
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
                    Text("Start New Quiz")
                }

                if (missedWords.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onReviewMissed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, "Review")
                        Spacer(Modifier.width(8.dp))
                        Text("Review Missed Words")
                    }
                }
            }
        }

        // Info Card
        item {
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
private fun MissedWordItem(
    word: String,
    meaning: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                "View",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

@Composable
private fun NoQuizState(
    modifier: Modifier = Modifier,
    onStartQuiz: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = "No quiz",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "No Quiz Results Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Complete a quiz to see your results here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onStartQuiz) {
                Icon(Icons.Default.PlayArrow, "Start")
                Spacer(Modifier.width(8.dp))
                Text("Start a Quiz")
            }
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}
