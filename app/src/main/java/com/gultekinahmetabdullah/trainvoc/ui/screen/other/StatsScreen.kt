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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB3E5FC), // Daha canlı mavi
                        Color(0xFFE1BEE7), // Lila
                        Color(0xFFFFFFFF)  // Beyaz
                    )
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
                    color = Color(0xFFFFD600),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.baseline_leaderboard_24),
                    title = stringResource(id = R.string.total_quizzes),
                    value = "$totalQuizCount",
                    color = Color(0xFF64B5F6),
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.outline_timer_24),
                    title = stringResource(id = R.string.total_time_spent),
                    value = "${totalTimeSpent / 60}m ${totalTimeSpent % 60}s",
                    color = Color(0xFF81C784),
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
                    color = Color(0xFFBA68C8),
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(id = R.string.correct_answers),
                    value = "$correctAnswers",
                    color = Color(0xFF66BB6A),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Close,
                    title = stringResource(id = R.string.incorrect_answers),
                    value = "$incorrectAnswers",
                    color = Color(0xFFE57373),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = painterResource(id = R.drawable.baseline_skip_next_24),
                    title = stringResource(id = R.string.skipped_questions),
                    value = "$skippedQuestions",
                    color = Color(0xFFB0BEC5),
                    isPainter = true,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Star,
                    title = stringResource(id = R.string.best_category),
                    value = bestCategory,
                    color = Color(0xFFFFF176),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.Close,
                    title = stringResource(id = R.string.most_wrong_word),
                    value = mostWrongWord,
                    color = Color(0xFFEF5350),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Daily Correct",
                    value = "$dailyCorrect",
                    color = Color(0xFF4DD0E1),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item {
                AnimatedStatCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Weekly Correct",
                    value = "$weeklyCorrect",
                    color = Color(0xFF9575CD),
                    isPainter = false,
                    iconContentDescription = stringResource(id = R.string.statistics_icon)
                )
            }
            item { Spacer(modifier = Modifier.height(Spacing.mediumLarge)) }
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
    val anim = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600), label = "cardAnim"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(70.dp)
            .scale(anim.value),
        shape = RoundedCornerShape(CornerRadius.large),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = Alpha.surfaceMedium))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Spacer(modifier = Modifier.width(Spacing.medium))
                Text(text = title, style = MaterialTheme.typography.bodyLarge, color = color)
            }
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color)
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
                color = Color(0xFF66BB6A),
            )
            AnimatedBar(
                modifier = Modifier.weight(if (skippedRate <= 0f) 0.01f else skippedRate),
                color = Color(0xFFB0BEC5),
            )
            AnimatedBar(
                modifier = Modifier.weight(if (failureRate <= 0f) 0.01f else failureRate),
                color = Color(0xFFE57373),
            )
        }
        Spacer(modifier = Modifier.height(Spacing.small))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.correct_colon, correct),
                color = Color(0xFF66BB6A)
            )
            Text(
                text = stringResource(id = R.string.skipped_colon, skipped),
                color = Color(0xFFB0BEC5)
            )
            Text(
                text = stringResource(id = R.string.incorrect_colon, incorrect),
                color = Color(0xFFE57373)
            )
        }
    }
}

// AnimatedBar fonksiyonunu şu şekilde değiştirin:
@Composable
fun AnimatedBar(modifier: Modifier, color: Color) {
    Box(
        modifier = modifier
            .height(30.dp)
            .background(color, RoundedCornerShape(CornerRadius.small))
    )
}