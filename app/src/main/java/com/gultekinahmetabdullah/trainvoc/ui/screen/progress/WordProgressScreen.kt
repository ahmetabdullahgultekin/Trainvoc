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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.HomeViewModel
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
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

    // TODO: Load from ViewModel
    // Current: Mock data for demonstration
    val levelProgress = remember { generateMockLevelProgress() }
    val wordStatusBreakdown = remember { generateMockWordStatus() }
    val wordsToReview = remember { generateMockReviewSchedule() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Progress") },
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
                        text = "Overall Progress",
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
                            label = "Words Learned",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF4CAF50)
                        )

                        ProgressStat(
                            value = "${uiState.totalWords}",
                            label = "Total Words",
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
                        text = "${if (uiState.totalWords > 0) ((uiState.learnedWords.toFloat() / uiState.totalWords) * 100).toInt() else 0}% Complete",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Progress by Level
            Text(
                text = "Progress by Level",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            levelProgress.forEach { progress ->
                LevelProgressCard(progress)
            }

            // Word Status Breakdown
            Text(
                text = "Word Status",
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
                text = "Spaced Repetition Schedule",
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
                                text = "Due for Review Today",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "${wordsToReview.today} words",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        Icon(
                            Icons.Default.Schedule,
                            "Schedule",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Divider()

                    Spacer(Modifier.height(12.dp))

                    ReviewScheduleRow("Tomorrow", wordsToReview.tomorrow)
                    ReviewScheduleRow("This Week", wordsToReview.thisWeek)
                    ReviewScheduleRow("This Month", wordsToReview.thisMonth)
                }
            }

            // Progress Forecast
            Text(
                text = "Progress Forecast",
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
                            "Forecast",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Goal Projection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Based on your current learning pace",
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
                        text = "At your current pace of $currentPace words/day, you'll reach $targetLevel level in approximately $estimatedDays days",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Estimated Completion: ${estimatedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Learning Timeline (Placeholder)
            Text(
                text = "Learning Timeline",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Last 4 Weeks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Simple bar chart representation
                    val weeklyData = listOf(12, 18, 15, 22) // words learned per week

                    weeklyData.forEachIndexed { index, count ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Week ${index + 1}",
                                modifier = Modifier.width(60.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            LinearProgressIndicator(
                                progress = { (count / 30f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "$count",
                                modifier = Modifier.width(30.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (index < weeklyData.size - 1) {
                            Spacer(Modifier.height(8.dp))
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
                text = "${if (progress.total > 0) ((progress.learned.toFloat() / progress.total) * 100).toInt() else 0}% complete",
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
            text = "${status.count} words",
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
            text = "$count words",
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

// Extension for WordLevel colors
val WordLevel.color: Color
    get() = when (this) {
        WordLevel.A1 -> Color(0xFF4CAF50) // Green
        WordLevel.A2 -> Color(0xFF8BC34A) // Light Green
        WordLevel.B1 -> Color(0xFFFFC107) // Amber
        WordLevel.B2 -> Color(0xFFFF9800) // Orange
        WordLevel.C1 -> Color(0xFFFF5722) // Deep Orange
        WordLevel.C2 -> Color(0xFFF44336) // Red
    }

// Mock data generators
private fun generateMockLevelProgress(): List<LevelProgress> {
    return listOf(
        LevelProgress(WordLevel.A1, 45, 50),
        LevelProgress(WordLevel.A2, 38, 80),
        LevelProgress(WordLevel.B1, 22, 120),
        LevelProgress(WordLevel.B2, 8, 150),
        LevelProgress(WordLevel.C1, 2, 200),
        LevelProgress(WordLevel.C2, 0, 250)
    )
}

private fun generateMockWordStatus(): List<WordStatus> {
    return listOf(
        WordStatus(
            "Mastered",
            85,
            Icons.Default.CheckCircle,
            Color(0xFF4CAF50)
        ),
        WordStatus(
            "Learning",
            42,
            Icons.Default.School,
            Color(0xFFFFC107)
        ),
        WordStatus(
            "Struggling",
            18,
            Icons.Default.Warning,
            Color(0xFFFF5722)
        ),
        WordStatus(
            "Not Started",
            705,
            Icons.Default.Circle,
            Color(0xFF9E9E9E)
        )
    )
}

private fun generateMockReviewSchedule(): ReviewSchedule {
    return ReviewSchedule(
        today = 15,
        tomorrow = 12,
        thisWeek = 42,
        thisMonth = 128
    )
}
