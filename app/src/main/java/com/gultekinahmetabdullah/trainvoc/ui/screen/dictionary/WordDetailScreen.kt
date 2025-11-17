package com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsCorrect
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsIncorrect
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsSkipped
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsTime
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import kotlinx.coroutines.launch

/**
 * Enhanced Word Detail Screen with visual polish and statistics
 *
 * Features:
 * - Animated entrance
 * - Visual hierarchy with sections
 * - Color-coded statistics
 * - Progress bars for success rate
 * - Icon-enhanced information display
 * - Better loading and error states
 */
@Composable
fun WordDetailScreen(wordId: String, wordViewModel: WordViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var word by remember {
        mutableStateOf<com.gultekinahmetabdullah.trainvoc.classes.word.Word?>(null)
    }
    var statistic by remember {
        mutableStateOf<com.gultekinahmetabdullah.trainvoc.classes.word.Statistic?>(null)
    }
    var exams by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(wordId) {
        coroutineScope.launch {
            val detail = wordViewModel.getWordFullDetail(wordId)
            word = detail?.word
            statistic = detail?.statistic
            exams = detail?.exams ?: emptyList()
            isLoading = false
        }
    }

    // Loading State
    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.medium))
            Text(
                text = stringResource(id = R.string.loading),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // Error State
    val currentWord = word
    if (currentWord == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(Spacing.medium))
            Text(
                text = stringResource(id = R.string.word_not_found),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // Main Content
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.95f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.mediumLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            // Word Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = Elevation.medium
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.large)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentWord.word,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            // Level Badge
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(getLevelColor(currentWord.level?.ordinal ?: 0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = currentWord.level.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "LVL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text(
                            text = currentWord.meaning,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Statistics Card
            item {
                val currentStatistic = statistic
                if (currentStatistic != null) {
                    val total = currentStatistic.correctCount +
                            currentStatistic.wrongCount +
                            currentStatistic.skippedCount
                    val successRate = if (total > 0)
                        currentStatistic.correctCount.toFloat() / total
                    else 0f

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = Elevation.low
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(Spacing.large)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Statistics",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(IconSize.large)
                                )
                                Spacer(modifier = Modifier.width(Spacing.small))
                                Text(
                                    text = stringResource(id = R.string.statistics),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            // Success Rate Progress
                            Text(
                                text = stringResource(
                                    id = R.string.success_rate_percent,
                                    (successRate * 100).toInt()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(Spacing.extraSmall))
                            LinearProgressIndicator(
                                progress = { successRate },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(CornerRadius.small)),
                                color = MaterialTheme.colorScheme.statsCorrect,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(Spacing.medium))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(Spacing.medium))

                            // Statistics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatisticItem(
                                    icon = Icons.Default.CheckCircle,
                                    label = stringResource(id = R.string.correct),
                                    value = currentStatistic.correctCount.toString(),
                                    color = MaterialTheme.colorScheme.statsCorrect
                                )
                                StatisticItem(
                                    icon = Icons.Default.Close,
                                    label = stringResource(id = R.string.wrong),
                                    value = currentStatistic.wrongCount.toString(),
                                    color = MaterialTheme.colorScheme.statsIncorrect
                                )
                                StatisticItem(
                                    icon = painterResource(id = R.drawable.baseline_skip_next_24),
                                    label = stringResource(id = R.string.skipped),
                                    value = currentStatistic.skippedCount.toString(),
                                    color = MaterialTheme.colorScheme.statsSkipped
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.medium))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(Spacing.medium))

                            // Time Spent
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_timer_24),
                                    contentDescription = "Time",
                                    tint = MaterialTheme.colorScheme.statsTime,
                                    modifier = Modifier.size(IconSize.medium)
                                )
                                Spacer(modifier = Modifier.width(Spacing.small))
                                Text(
                                    text = stringResource(
                                        id = R.string.total_seconds,
                                        currentWord.secondsSpent
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Last Reviewed
                            Spacer(modifier = Modifier.height(Spacing.small))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_bar_chart_24),
                                    contentDescription = "Last Reviewed",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(IconSize.medium)
                                )
                                Spacer(modifier = Modifier.width(Spacing.small))
                                Text(
                                    text = stringResource(
                                        id = R.string.last_reviewed,
                                        currentWord.lastReviewed?.toString() ?: "-"
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Exam History Card
            if (exams.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = Elevation.low
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(Spacing.large)) {
                            Text(
                                text = stringResource(id = R.string.exam_history),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Spacing.medium))
                        }
                    }
                }
                items(exams) { exam ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.small),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = Alpha.surfaceMedium
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(Spacing.medium))
                            Text(
                                text = exam,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Statistic Item Composable with icon, label, and value
 */
@Composable
fun StatisticItem(
    icon: Any,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (icon) {
            is androidx.compose.ui.graphics.vector.ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(IconSize.large)
                )
            }
            is androidx.compose.ui.graphics.painter.Painter -> {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(IconSize.large)
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.extraSmall))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
