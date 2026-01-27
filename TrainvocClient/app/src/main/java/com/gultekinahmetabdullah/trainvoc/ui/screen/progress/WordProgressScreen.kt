package com.gultekinahmetabdullah.trainvoc.ui.screen.progress

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.HomeViewModel
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.StatsColors
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsCorrect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Word Progress Screen
 *
 * Shows detailed learning progress:
 * - Breakdown by CEFR level (A1-C2)
 * - Word status categories (Mastered, Learning, Struggling, Not Started)
 * - Timeline chart (words learned per week)
 * - Spaced repetition schedule
 * - Progress forecasting
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordProgressScreen(
    onBackClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Get real data from ViewModel
    val levelProgress = uiState.levelProgress
    val wordStatusBreakdown = generateWordStatusList(uiState.wordStatusCounts)
    val wordsToReview = uiState.reviewSchedule ?: ReviewSchedule(0, 0, 0, 0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.word_progress)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, stringResource(id = R.string.back))
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Overall Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.overall_progress),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProgressStat(
                            value = uiState.learnedWords.toString(),
                            label = stringResource(id = R.string.words_learned),
                            icon = Icons.Default.CheckCircle,
                            color = MaterialTheme.colorScheme.statsCorrect
                        )

                        ProgressStat(
                            value = "${uiState.totalWords}",
                            label = stringResource(id = R.string.total_words),
                            icon = Icons.Default.Book,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { if (uiState.totalWords > 0) (uiState.learnedWords.toFloat() / uiState.totalWords).coerceIn(0f, 1f) else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.percent_complete, if (uiState.totalWords > 0) ((uiState.learnedWords.toFloat() / uiState.totalWords) * 100).toInt() else 0),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Progress by Level
            Text(
                text = stringResource(id = R.string.progress_by_level),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            levelProgress.forEach { progress ->
                LevelProgressCard(progress)
            }

            // Word Status Breakdown
            Text(
                text = stringResource(id = R.string.word_status),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    wordStatusBreakdown.forEach { status ->
                        WordStatusRow(status)
                    }
                }
            }

            // Review Schedule
            Text(
                text = stringResource(id = R.string.spaced_repetition_schedule),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.due_for_review_today),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = stringResource(id = R.string.words_count_label, wordsToReview.today),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        Icon(
                            Icons.Default.Schedule,
                            stringResource(id = R.string.content_desc_schedule),
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Divider()

                    Spacer(Modifier.height(12.dp))

                    ReviewScheduleRow(stringResource(id = R.string.tomorrow), wordsToReview.tomorrow)
                    ReviewScheduleRow(stringResource(id = R.string.this_week), wordsToReview.thisWeek)
                    ReviewScheduleRow(stringResource(id = R.string.this_month), wordsToReview.thisMonth)
                }
            }

            // Progress Forecast
            Text(
                text = stringResource(id = R.string.progress_forecast),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            stringResource(id = R.string.content_desc_forecast),
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = stringResource(id = R.string.goal_projection),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = stringResource(id = R.string.based_on_pace),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Mock forecast
                    val currentPace = 5 // words per day
                    val targetLevel = "B2"
                    val wordsRemaining = 300
                    val estimatedDays = wordsRemaining / currentPace
                    val estimatedDate = LocalDate.now().plusDays(estimatedDays.toLong())

                    Text(
                        text = stringResource(id = R.string.pace_projection, currentPace, targetLevel, estimatedDays),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.estimated_completion, estimatedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Learning Summary
            Text(
                text = stringResource(id = R.string.learning_summary),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Show actual learning summary based on real data
                    val hasActivity = uiState.learnedWords > 0 || uiState.totalCorrectAnswers > 0

                    if (hasActivity) {
                        // Summary stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SummaryStatItem(
                                value = "${uiState.totalStudyTimeMinutes}",
                                label = stringResource(id = R.string.minutes_studied),
                                icon = Icons.Default.Timer
                            )
                            SummaryStatItem(
                                value = "${uiState.totalCorrectAnswers}",
                                label = stringResource(id = R.string.correct_answers_label),
                                icon = Icons.Default.CheckCircle
                            )
                            SummaryStatItem(
                                value = "${uiState.learnedWords}",
                                label = stringResource(id = R.string.words_learned_label),
                                icon = Icons.Default.School
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Motivational message based on progress
                        val progressPercent = if (uiState.totalWords > 0) {
                            ((uiState.learnedWords.toFloat() / uiState.totalWords) * 100).toInt()
                        } else 0

                        val amazingProgress = stringResource(id = R.string.amazing_progress)
                        val halfwayThere = stringResource(id = R.string.halfway_there)
                        val goodProgress = stringResource(id = R.string.good_progress)
                        val greatStart = stringResource(id = R.string.great_start)
                        val beginJourney = stringResource(id = R.string.begin_journey)

                        val message = when {
                            progressPercent >= 75 -> amazingProgress
                            progressPercent >= 50 -> halfwayThere
                            progressPercent >= 25 -> goodProgress
                            progressPercent >= 10 -> greatStart
                            else -> beginJourney
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    } else {
                        // Empty state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Timeline,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = stringResource(id = R.string.start_learning_progress),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(id = R.string.complete_quizzes_track),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressStat(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = color
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun LevelProgressCard(progress: LevelProgress) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = progress.level.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = progress.level.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = progress.level.color
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = progress.level.longName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = "${progress.learned}/${progress.total}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { if (progress.total > 0) (progress.learned.toFloat() / progress.total).coerceIn(0f, 1f) else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progress.level.color
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(id = R.string.percent_complete_label, if (progress.total > 0) ((progress.learned.toFloat() / progress.total) * 100).toInt() else 0),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WordStatusRow(status: WordStatus) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = status.icon,
                contentDescription = status.label,
                tint = status.color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = status.label,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = stringResource(id = R.string.words_count_label, status.count),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = status.color
        )
    }
}

@Composable
private fun ReviewScheduleRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text = stringResource(id = R.string.words_count_label, count),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

// Data Classes
data class LevelProgress(
    val level: WordLevel,
    val learned: Int,
    val total: Int
)

data class WordStatus(
    val label: String,
    val count: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class ReviewSchedule(
    val today: Int,
    val tomorrow: Int,
    val thisWeek: Int,
    val thisMonth: Int
)

data class WordStatusCounts(
    val mastered: Int,
    val learning: Int,
    val struggling: Int,
    val notStarted: Int
)

// Extension for WordLevel colors - uses theme-aware CEFR colors
val WordLevel.color: Color
    get() = when (this) {
        WordLevel.A1 -> com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors.A1
        WordLevel.A2 -> com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors.A2
        WordLevel.B1 -> com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors.B1
        WordLevel.B2 -> com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors.B2
        WordLevel.C1 -> com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors.C1
        WordLevel.C2 -> com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors.C2
    }

@Composable
private fun SummaryStatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight
        )
    }
}

// Real data converter
@Composable
private fun generateWordStatusList(counts: WordStatusCounts?): List<WordStatus> {
    val masteredLabel = stringResource(id = R.string.mastered)
    val learningLabel = stringResource(id = R.string.status_learning)
    val strugglingLabel = stringResource(id = R.string.struggling)
    val notStartedLabel = stringResource(id = R.string.not_started)

    if (counts == null) {
        return emptyList()
    }

    return listOf(
        WordStatus(
            masteredLabel,
            counts.mastered,
            Icons.Default.CheckCircle,
            StatsColors.correctLight
        ),
        WordStatus(
            learningLabel,
            counts.learning,
            Icons.Default.School,
            StatsColors.goldLight
        ),
        WordStatus(
            strugglingLabel,
            counts.struggling,
            Icons.Default.Warning,
            StatsColors.incorrectLight
        ),
        WordStatus(
            notStartedLabel,
            counts.notStarted,
            Icons.Default.Circle,
            StatsColors.skippedLight
        )
    )
}
