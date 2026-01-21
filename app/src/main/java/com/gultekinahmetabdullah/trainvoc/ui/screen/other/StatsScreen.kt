package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.components.ChartData
import com.gultekinahmetabdullah.trainvoc.ui.components.ProgressRing
import com.gultekinahmetabdullah.trainvoc.ui.components.VerticalBarChart
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsAchievement
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsAverage
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsCategory
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsCorrect
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsGold
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsGradient
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsIncorrect
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsQuiz
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsSkipped
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsTime
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

    val totalQuizCount by statsViewModel.totalQuizCount.collectAsState()
    val dailyCorrect by statsViewModel.dailyCorrect.collectAsState()
    val weeklyCorrect by statsViewModel.weeklyCorrect.collectAsState()
    val mostWrongWord by statsViewModel.mostWrongWord.collectAsState()
    val bestCategory by statsViewModel.bestCategory.collectAsState()

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(
            if (successRate > 0.7f) "animations/celebration.json"
            else "animations/anime_book.json"
        )
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 3,
        isPlaying = true,
        speed = 1f,
    )

    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = statsGradient(isDarkTheme)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.mediumLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Animation
            item {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(150.dp),
                    progress = { progress }
                )
            }
            item {
                Text(
                    text = stringResource(id = R.string.quiz_statistics),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item { Spacer(modifier = Modifier.height(Spacing.medium)) }

            // SECTION 1: Key Metrics (Score, Quizzes, Time) - Condensed into one card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low),
                    shape = RoundedCornerShape(CornerRadius.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = Alpha.high)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.medium)
                    ) {
                        // Score Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Total score",
                                    tint = MaterialTheme.colorScheme.statsGold,
                                    modifier = Modifier.size(IconSize.medium)
                                )
                                Spacer(modifier = Modifier.width(Spacing.small))
                                Text(
                                    text = stringResource(id = R.string.total_score),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "${correctAnswers * 10}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.statsGold
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.small))

                        // Quizzes and Time Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.total_quizzes),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$totalQuizCount",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.statsTime
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = stringResource(id = R.string.total_time_spent),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${totalTimeSpent / 60}m ${totalTimeSpent % 60}s",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.statsAchievement
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.small))

                        // Best Category & Most Wrong Word
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.best_category),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = bestCategory,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.statsCategory
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = stringResource(id = R.string.most_wrong_word),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = mostWrongWord,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.statsIncorrect
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.medium)) }

            // SECTION 2: Success Rate with Progress Ring + Answer Counts
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low),
                    shape = RoundedCornerShape(CornerRadius.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = Alpha.high)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.medium),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Progress Ring
                        ProgressRing(
                            progress = successRate,
                            color = MaterialTheme.colorScheme.statsCorrect,
                            size = 100.dp
                        )

                        // Answer Breakdown
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Correct answers",
                                    tint = MaterialTheme.colorScheme.statsCorrect,
                                    modifier = Modifier.size(IconSize.small)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$correctAnswers ${stringResource(id = R.string.correct_label)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.statsCorrect
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Incorrect answers",
                                    tint = MaterialTheme.colorScheme.statsIncorrect,
                                    modifier = Modifier.size(IconSize.small)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$incorrectAnswers ${stringResource(id = R.string.incorrect_label)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.statsIncorrect
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$skippedQuestions ${stringResource(id = R.string.skipped_label)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.statsSkipped
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.medium)) }

            // SECTION 3: Performance Trends - Condensed Bar Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.low),
                    shape = RoundedCornerShape(CornerRadius.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = Alpha.high)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.medium)
                    ) {
                        Text(
                            text = stringResource(id = R.string.performance_trends),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.small))

                        VerticalBarChart(
                            data = listOf(
                                ChartData(
                                    label = stringResource(id = R.string.daily_label),
                                    value = dailyCorrect.toFloat(),
                                    color = MaterialTheme.colorScheme.statsQuiz
                                ),
                                ChartData(
                                    label = stringResource(id = R.string.weekly_label),
                                    value = weeklyCorrect.toFloat(),
                                    color = MaterialTheme.colorScheme.statsAverage
                                ),
                                ChartData(
                                    label = stringResource(id = R.string.total_label),
                                    value = correctAnswers.toFloat(),
                                    color = MaterialTheme.colorScheme.statsCorrect
                                )
                            ),
                            maxHeight = 100.dp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.medium)) }

            // Refresh Button
            item {
                Button(onClick = {
                    scope.launch { statsViewModel.fillStats() }
                }) {
                    Text(text = stringResource(id = R.string.refresh_stats))
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.large)) }
        }
    }
}