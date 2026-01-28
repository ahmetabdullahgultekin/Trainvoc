package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.ui.components.ElevatedCard
import com.gultekinahmetabdullah.trainvoc.ui.components.InfoCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun QuizMenuScreen(
    onQuizSelected: (Quiz) -> Unit,
    viewModel: QuizMenuViewModel = hiltViewModel()
) {
    val quizStats by viewModel.quizStats.collectAsState()
    val hasAnyQuizHistory by viewModel.hasAnyQuizHistory.collectAsState()

    // Responsive design: Determine grid columns based on screen width
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val gridColumns = when {
        screenWidthDp >= 840 -> 3  // Large tablets/desktops - 3 columns
        screenWidthDp >= 600 -> 3  // Small tablets/landscape - 3 columns
        else -> 2                  // Phones - 2 columns
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_2),
                contentScale = ContentScale.FillBounds,
                alpha = Alpha.surfaceLight
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
        ) {
            // Header
            Text(
                text = stringResource(id = R.string.select_quiz_type),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = Spacing.md)
            )

            // Empty state for new users
            if (!hasAnyQuizHistory) {
                InfoCard(
                    icon = Icons.Default.Quiz,
                    title = stringResource(id = R.string.start_first_quiz_title),
                    message = stringResource(id = R.string.start_first_quiz_message),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.md)
                )
            }

            // Quiz Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = Quiz.quizTypes.size,
                    key = { index -> Quiz.quizTypes[index].id }
                ) { index ->
                    val quiz = Quiz.quizTypes[index]
                    val stats = quizStats[quiz.id]
                    val isNew = isQuizTypeNew(quiz)

                    // Staggered animation delay
                    val delay = index * AnimationDelay.gameCardStagger

                    AnimatedQuizTypeCard(
                        quiz = quiz,
                        bestScore = stats?.bestScore,
                        timesPlayed = stats?.timesPlayed ?: 0,
                        isNew = isNew,
                        animationDelay = delay,
                        onClick = { onQuizSelected(quiz) }
                    )
                }
            }
        }
    }
}

/**
 * Determines if a quiz type is new (for badge display)
 * Currently marks quiz types added in recent versions
 */
private fun isQuizTypeNew(quiz: Quiz): Boolean {
    // Mark quiz types 8+ as "new" (Most Wrong, Most Recent, Most Reviewed)
    return quiz.id >= 8
}

/**
 * Animated Quiz Type Card
 * Displays quiz type with icon, stats, and animations
 */
@Composable
fun AnimatedQuizTypeCard(
    quiz: Quiz,
    bestScore: Float?,
    timesPlayed: Int,
    isNew: Boolean,
    animationDelay: Int,
    onClick: () -> Unit
) {
    // Staggered entrance animation
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    // Press interaction
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Scale animation on press
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "cardScale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = spring()),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.scale(scale)) {
            ElevatedCard(
                onClick = onClick,
                elevation = Elevation.level1,
                cornerRadius = CornerRadius.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ComponentSize.gameCardHeight)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.md),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top: Icon and Title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Icon
                            Box(
                                modifier = Modifier
                                    .size(IconSize.extraLarge)
                                    .clip(RoundedCornerShape(CornerRadius.small))
                                    .background(Color(quiz.color).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getQuizIcon(quiz),
                                    contentDescription = quiz.name,
                                    tint = Color(quiz.color),
                                    modifier = Modifier.size(IconSize.medium)
                                )
                            }

                            // New badge
                            if (isNew) {
                                Surface(
                                    shape = RoundedCornerShape(CornerRadius.extraSmall),
                                    color = MaterialTheme.colorScheme.secondary
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.badge_new_quiz),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        // Middle: Quiz name
                        Text(
                            text = quiz.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )

                        // Bottom: Stats
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            if (bestScore != null) {
                                Text(
                                    text = stringResource(id = R.string.best_score_percent, (bestScore * 100).toInt()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (timesPlayed > 0) {
                                Text(
                                    text = if (timesPlayed == 1) stringResource(id = R.string.played_count_single, timesPlayed) else stringResource(id = R.string.played_count_plural, timesPlayed),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else if (bestScore == null) {
                                Text(
                                    text = stringResource(id = R.string.not_played_yet),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Returns appropriate icon for each quiz type
 */
private fun getQuizIcon(quiz: Quiz): ImageVector {
    return when (quiz.name) {
        "Not Learned" -> Icons.Default.School
        "Random" -> Icons.Default.Shuffle
        "Least Correct" -> Icons.Default.TrendingDown
        "Least Wrong" -> Icons.Default.CheckCircle
        "Least Recent" -> Icons.Default.History
        "Least Reviewed" -> Icons.Default.BookmarkBorder
        "Most Correct" -> Icons.Default.TrendingUp
        "Most Wrong" -> Icons.Default.ErrorOutline
        "Most Recent" -> Icons.Default.Schedule
        "Most Reviewed" -> Icons.Default.Bookmark
        else -> Icons.Default.Quiz
    }
}