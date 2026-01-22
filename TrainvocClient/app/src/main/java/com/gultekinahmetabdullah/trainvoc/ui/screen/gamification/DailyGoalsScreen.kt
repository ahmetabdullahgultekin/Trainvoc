package com.gultekinahmetabdullah.trainvoc.ui.screen.gamification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.components.LinearProgressBar
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.HomeViewModel

/**
 * Daily Goals Screen
 * Shows detailed progress for daily learning goals
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalsScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goals") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item { Spacer(modifier = Modifier.height(Spacing.sm)) }

            // Words Goal
            item {
                GoalCard(
                    title = "Words Learned Today",
                    icon = Icons.Default.Book,
                    current = uiState.wordsLearnedToday,
                    goal = uiState.wordsGoalToday,
                    progress = uiState.wordsTodayProgress
                )
            }

            // Quizzes Goal
            item {
                GoalCard(
                    title = "Quizzes Completed",
                    icon = Icons.Default.Quiz,
                    current = uiState.quizzesCompleted,
                    goal = uiState.quizzesGoal,
                    progress = uiState.quizzesProgress
                )
            }

            // Study Time Goal (placeholder)
            item {
                GoalCard(
                    title = "Study Time (minutes)",
                    icon = Icons.Default.Timer,
                    current = 0,
                    goal = 30,
                    progress = 0f
                )
            }

            item { Spacer(modifier = Modifier.height(Spacing.lg)) }
        }
    }
}

@Composable
private fun GoalCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    current: Int,
    goal: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Daily goal icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$current / $goal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            LinearProgressBar(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
