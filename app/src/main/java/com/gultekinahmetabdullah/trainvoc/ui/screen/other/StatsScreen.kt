package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.components.ChartData
import com.gultekinahmetabdullah.trainvoc.ui.components.ChartLegend
import com.gultekinahmetabdullah.trainvoc.ui.components.DonutChart
import com.gultekinahmetabdullah.trainvoc.ui.components.ProgressRing
import com.gultekinahmetabdullah.trainvoc.ui.components.VerticalBarChart
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.UnlockedLeaf
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
            item {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(220.dp),
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
            item { Spacer(modifier = Modifier.height(Spacing.mediumLarge)) }
            // Animasyonlu StatCard'lar
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Star,
                    title = stringResource(id = R.string.total_score),
                    value = "${correctAnswers * 10}",
                    color = MaterialTheme.colorScheme.statsGold,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.baseline_leaderboard_24),
                    title = stringResource(id = R.string.total_quizzes),
                    value = "$totalQuizCount",
                    color = MaterialTheme.colorScheme.statsTime,
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.outline_timer_24),
                    title = stringResource(id = R.string.total_time_spent),
                    value = "${totalTimeSpent / 60}m ${totalTimeSpent % 60}s",
                    color = MaterialTheme.colorScheme.statsAchievement,
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.baseline_bar_chart_24),
                    title = stringResource(id = R.string.avg_time_per_question),
                    // Show just two decimal places
                    value = "${
                        (totalTimeSpent.toDouble() / totalQuestions.toDouble()).let {
                            "%.2f".format(it)
                        }
                    }s",
                    color = MaterialTheme.colorScheme.statsAverage,
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(id = R.string.correct_answers),
                    value = "$correctAnswers",
                    color = MaterialTheme.colorScheme.statsCorrect,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Close,
                    title = stringResource(id = R.string.incorrect_answers),
                    value = "$incorrectAnswers",
                    color = MaterialTheme.colorScheme.statsIncorrect,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.baseline_skip_next_24),
                    title = stringResource(id = R.string.skipped_questions),
                    value = "$skippedQuestions",
                    color = MaterialTheme.colorScheme.statsSkipped,
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Star,
                    title = stringResource(id = R.string.best_category),
                    value = bestCategory,
                    color = MaterialTheme.colorScheme.statsCategory,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Close,
                    title = stringResource(id = R.string.most_wrong_word),
                    value = mostWrongWord,
                    color = MaterialTheme.colorScheme.statsIncorrect,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Daily Correct",
                    value = "$dailyCorrect",
                    color = MaterialTheme.colorScheme.statsQuiz,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Weekly Correct",
                    value = "$weeklyCorrect",
                    color = MaterialTheme.colorScheme.statsAverage,
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item { Spacer(modifier = Modifier.height(Spacing.mediumLarge)) }

            // Progress Ring - Success Rate
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
                            .padding(Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProgressRing(
                            progress = successRate,
                            color = MaterialTheme.colorScheme.statsCorrect,
                            size = 160.dp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.large)) }

            // Donut Chart - Answer Distribution
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
                        modifier = Modifier.padding(Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.answer_distribution),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DonutChart(
                                data = listOf(
                                    ChartData(
                                        label = stringResource(id = R.string.correct_answers),
                                        value = correctAnswers.toFloat(),
                                        color = MaterialTheme.colorScheme.statsCorrect
                                    ),
                                    ChartData(
                                        label = stringResource(id = R.string.incorrect_answers),
                                        value = incorrectAnswers.toFloat(),
                                        color = MaterialTheme.colorScheme.statsIncorrect
                                    ),
                                    ChartData(
                                        label = stringResource(id = R.string.skipped_questions),
                                        value = skippedQuestions.toFloat(),
                                        color = MaterialTheme.colorScheme.statsSkipped
                                    )
                                ),
                                size = 180.dp,
                                strokeWidth = 32.dp
                            )

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            ChartLegend(
                                data = listOf(
                                    ChartData(
                                        label = stringResource(id = R.string.correct_answers),
                                        value = correctAnswers.toFloat(),
                                        color = MaterialTheme.colorScheme.statsCorrect
                                    ),
                                    ChartData(
                                        label = stringResource(id = R.string.incorrect_answers),
                                        value = incorrectAnswers.toFloat(),
                                        color = MaterialTheme.colorScheme.statsIncorrect
                                    ),
                                    ChartData(
                                        label = stringResource(id = R.string.skipped_questions),
                                        value = skippedQuestions.toFloat(),
                                        color = MaterialTheme.colorScheme.statsSkipped
                                    )
                                )
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.large)) }

            // Vertical Bar Chart - Time-based Performance
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
                        modifier = Modifier.padding(Spacing.large)
                    ) {
                        Text(
                            text = "Performance Trends",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))

                        VerticalBarChart(
                            data = listOf(
                                ChartData(
                                    label = "Daily",
                                    value = dailyCorrect.toFloat(),
                                    color = MaterialTheme.colorScheme.statsQuiz
                                ),
                                ChartData(
                                    label = "Weekly",
                                    value = weeklyCorrect.toFloat(),
                                    color = MaterialTheme.colorScheme.statsAverage
                                ),
                                ChartData(
                                    label = "Total",
                                    value = correctAnswers.toFloat(),
                                    color = MaterialTheme.colorScheme.statsCorrect
                                )
                            ),
                            maxHeight = 140.dp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.large)) }

            // Original Horizontal Bar Chart
            item {
                StatsBarChart(
                    correctAnswers, incorrectAnswers, skippedQuestions,
                    successRate, failureRate, skippedRate
                )
            }
            item { Spacer(modifier = Modifier.height(Spacing.large)) }
            item {
                Button(onClick = {
                    scope.launch { statsViewModel.fillStats() }
                }) {
                    Text(text = "Refresh Stats")
                }
            }
        }
    }
}

@Composable
fun AnimatedStatCard(
    icon: Any, // ImageVector veya Painter olabilir
    title: String,
    value: String,
    color: Color,
    isPainter: Boolean = false,
    iconContentDescription: String? = null
) {
    val cardShape = remember { RoundedCornerShape(CornerRadius.large) }

    val anim = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = AnimationDuration.statCard), label = "cardAnim"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(70.dp)
            .scale(anim.value),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = Alpha.surfaceMedium))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPainter) {
                    Image(
                        painter = icon as Painter,
                        contentDescription = iconContentDescription,
                        modifier = Modifier.size(IconSize.large)
                    )
                } else {
                    Icon(
                        icon as ImageVector,
                        contentDescription = iconContentDescription,
                        tint = color,
                        modifier = Modifier.size(IconSize.large)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                maxLines = 1
            )
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
        Text(
            text = stringResource(id = R.string.answer_distribution),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(Spacing.mediumLarge))
        // StatsBarChart içindeki Row'da:
        Row(modifier = Modifier.fillMaxWidth()) {
            AnimatedBar(
                modifier = Modifier.weight(if (successRate <= 0f) 0.01f else successRate),
                color = MaterialTheme.colorScheme.statsCorrect,
            )
            AnimatedBar(
                modifier = Modifier.weight(if (skippedRate <= 0f) 0.01f else skippedRate),
                color = MaterialTheme.colorScheme.statsSkipped,
            )
            AnimatedBar(
                modifier = Modifier.weight(if (failureRate <= 0f) 0.01f else failureRate),
                color = MaterialTheme.colorScheme.statsIncorrect,
            )
        }
        Spacer(modifier = Modifier.height(Spacing.small))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.correct_colon, correct),
                color = MaterialTheme.colorScheme.statsCorrect
            )
            Text(
                text = stringResource(id = R.string.skipped_colon, skipped),
                color = MaterialTheme.colorScheme.statsSkipped
            )
            Text(
                text = stringResource(id = R.string.incorrect_colon, incorrect),
                color = MaterialTheme.colorScheme.statsIncorrect
            )
        }
    }
}

// AnimatedBar fonksiyonunu şu şekilde değiştirin:
@Composable
fun AnimatedBar(modifier: Modifier, color: Color) {
    val barShape = remember { RoundedCornerShape(CornerRadius.small) }
    Box(
        modifier = modifier
            .height(30.dp)
            .background(color, barShape)
    )
}