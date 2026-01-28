package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.data.UpdateNotesManager
import com.gultekinahmetabdullah.trainvoc.ui.components.CircularProgressRing
import com.gultekinahmetabdullah.trainvoc.ui.components.ElevatedCard
import com.gultekinahmetabdullah.trainvoc.ui.components.FeatureCard
import com.gultekinahmetabdullah.trainvoc.ui.components.GlassCard
import com.gultekinahmetabdullah.trainvoc.ui.components.SectionHeader
import com.gultekinahmetabdullah.trainvoc.ui.components.StatChip
import com.gultekinahmetabdullah.trainvoc.ui.components.StatsCard
import com.gultekinahmetabdullah.trainvoc.ui.components.StreakWidget
import com.gultekinahmetabdullah.trainvoc.ui.components.cards.UpdateNotesCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import java.util.concurrent.TimeUnit

/**
 * HomeScreen - Main dashboard with gamification features
 *
 * Displays:
 * - Profile Summary (avatar, level, XP bar)
 * - Daily Goals Card (words, quizzes, study time)
 * - Streak Card (current streak with fire animation)
 * - Quick Actions (quiz, word of day, dictionary, games)
 * - Recent Achievements (horizontal scroll)
 * - Stats Preview (words learned, quizzes taken, accuracy)
 *
 * All data is pulled from real sources via HomeViewModel.
 */
@Composable
fun HomeScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateToStory: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToStats: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToWordOfDay: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToLastQuiz: () -> Unit = {},
    onNavigateToDailyGoals: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToStreakDetail: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToWordProgress: () -> Unit = {},
    onNavigateToDictionary: () -> Unit = {},
    onNavigateToGames: () -> Unit = {},
    onNavigateToMultiplayer: () -> Unit = {},
    onNavigateToChangelog: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    preloadLottie: LottieComposition? = null,
    preloadBg: Painter? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Get username and avatar from SharedPreferences
    val prefs = remember { context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE) }
    val username = prefs.getString("username", null) ?: stringResource(id = R.string.username_placeholder)
    val userAvatar = prefs.getString("avatar", null) ?: "🦊" // Default fox avatar

    // Update Notes management
    val updateNotesManager = remember { UpdateNotesManager.getInstance(context) }
    var shouldShowUpdateNotes by remember { mutableStateOf(updateNotesManager.shouldShowUpdateNotes()) }
    val updateNotes = remember { updateNotesManager.getUpdateNotes() }

    // Lifecycle-aware animations
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val isActive = lifecycleState == Lifecycle.State.RESUMED

    // Background
    val bgPainter = preloadBg ?: painterResource(id = R.drawable.bg_3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("HomeScreenRoot")
    ) {
        // Background - purely decorative, null contentDescription for accessibility
        Image(
            painter = bgPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.15f
        )

        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.md),
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Top spacing
            item { Spacer(modifier = Modifier.height(Spacing.sm)) }

            // 1. UNIFIED HOME HEADER (App branding + Profile + Menu)
            item {
                HomeHeader(
                    username = username,
                    avatar = userAvatar,
                    level = uiState.level,
                    currentXP = uiState.xpCurrent,
                    maxXP = uiState.xpForNextLevel,
                    onProfileClick = onNavigateToProfile,
                    onMenuClick = onOpenMenu
                )
            }

            // 1.5. UPDATE NOTES (if available and not dismissed)
            if (shouldShowUpdateNotes && updateNotes != null) {
                item {
                    UpdateNotesCard(
                        updateNotes = updateNotes,
                        onViewChangelog = {
                            // Mark as seen and navigate to full changelog
                            updateNotesManager.markUpdateNotesSeen()
                            onNavigateToChangelog()
                        },
                        onDismiss = {
                            updateNotesManager.dismissUpdateNotes()
                            shouldShowUpdateNotes = false
                        }
                    )
                }
            }

            // 2. DAILY GOALS & STREAK ROW
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // Daily Goals Card
                    DailyGoalsCard(
                        modifier = Modifier.weight(1f),
                        wordsProgress = uiState.wordsTodayProgress,
                        wordsCount = "${uiState.wordsLearnedToday}/${uiState.wordsGoalToday}",
                        quizzesProgress = uiState.quizzesProgress,
                        quizzesCount = "${uiState.quizzesCompleted}/${uiState.quizzesGoal}",
                        onClick = onNavigateToDailyGoals
                    )

                    // Streak Card - Always show for engagement
                    StreakWidget(
                        streakCount = uiState.currentStreak,
                        onClick = onNavigateToStreakDetail,
                        modifier = Modifier.weight(0.7f),
                        isAtRisk = uiState.isStreakAtRisk,
                        isActivatedToday = uiState.streakStatus == StreakStatus.ACTIVE_TODAY
                    )
                }
            }

            // 3. CONTINUE WHERE YOU LEFT OFF (fixes #187)
            if (uiState.lastQuizResult != null) {
                item {
                    ContinueCard(
                        quizType = uiState.lastQuizResult.quizType,
                        accuracy = uiState.lastQuizResult.accuracy,
                        timestamp = uiState.lastQuizResult.timestamp,
                        onClick = onNavigateToQuiz
                    )
                }
            }

            // 4. PRIMARY CTA - Start Quiz (hero button, fixes #172)
            item {
                HeroCTAButton(
                    emoji = "🎯",
                    title = "Start Quiz",
                    subtitle = "Practice vocabulary now",
                    onClick = onNavigateToQuiz
                )
            }

            // 4. SECONDARY ACTIONS (2x2 grid)
            item {
                SectionHeader(title = "Quick Actions")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "📖",
                        title = "Dictionary",
                        onClick = onNavigateToDictionary,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "⭐",
                        title = "Favorites",
                        onClick = onNavigateToFavorites,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "🎮",
                        title = "Memory Games",
                        onClick = onNavigateToGames,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                    )

                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "👥",
                        title = "Multiplayer",
                        onClick = onNavigateToMultiplayer,
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }

            // 5. STATS PREVIEW - shows all-time data (fixes #202 - distinct from header)
            item {
                SectionHeader(title = "Your Progress")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Book,
                        value = "${uiState.learnedWords}/${uiState.totalWords}",
                        label = "Words",
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    StatsCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Quiz,
                        value = "${uiState.totalQuizzesAllTime}",
                        label = "All Quizzes",
                        iconTint = MaterialTheme.colorScheme.secondary
                    )

                    StatsCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        value = "${uiState.totalStudyTimeMinutes}m",
                        label = "Study Time",
                        iconTint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // 5. RECENT ACHIEVEMENTS
            // Deduplicate achievements by achievement_id to prevent showing same achievement twice
            val uniqueAchievements = uiState.unlockedAchievements.distinctBy { it.achievementId }
            if (uniqueAchievements.isNotEmpty()) {
                item {
                    SectionHeader(title = "Recent Achievements")
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(
                            items = uniqueAchievements.take(5),
                            key = { it.id }
                        ) { userAchievement ->
                            val achievement = userAchievement.getAchievement()
                            if (achievement != null) {
                                AchievementBadgeItem(
                                    emoji = achievement.icon,
                                    title = achievement.title,
                                    onClick = onNavigateToAchievements
                                )
                            }
                        }
                    }
                }
            }

            // 6. MORE OPTIONS
            item {
                SectionHeader(title = "More")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "📚",
                        title = "Story Mode",
                        onClick = onNavigateToStory,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                    )

                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "⭐",
                        title = "Word of Day",
                        onClick = onNavigateToWordOfDay,
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "📊",
                        title = "Statistics",
                        onClick = onNavigateToStats,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    )

                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        emoji = "🏆",
                        title = "Leaderboard",
                        onClick = onNavigateToLeaderboard,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(Spacing.lg)) }
        }
    }
}

@Composable
fun DebouncedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    colors: androidx.compose.material3.ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit
) {
    var isEnabled by remember { mutableStateOf(true) }
    Button(
        onClick = {
            if (isEnabled) {
                isEnabled = false
                onClick()
            }
        },
        enabled = isEnabled,
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        content()
    }
    if (!isEnabled) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(500)
            isEnabled = true
        }
    }
}

// Reusable Navigation Button
@Composable
fun HomeNavButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(110.dp) // Slightly larger square
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(CornerRadius.round), // Softer corners
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = Alpha.high
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .padding(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(CornerRadius.large)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(IconSize.large),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(Spacing.medium))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AnimatedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }
    Box(
        modifier = modifier
            .scale(scale.value)
            .clickable(
                onClick = {
                    // Boş, child composable'ın onClick'i çalışır
                },
                onClickLabel = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scale.animateTo(0.93f, animationSpec = tween(80))
                        try {
                            awaitRelease()
                        } finally {
                            scale.animateTo(1f, animationSpec = tween(120))
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun CategoryCard(
    emoji: String,
    title: String,
    emojiContentDescription: String,
    onClick: () -> Unit
) {
    AnimatedCard(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(CornerRadius.large)
            )
            .clickable(onClick = onClick)
            .padding(Spacing.mediumLarge)
            .size(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.semantics(mergeDescendants = true) {}
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = Spacing.small)
                    .semantics {
                        contentDescription = emojiContentDescription
                    }
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    emoji: String,
    title: String,
    emojiContentDescription: String,
    onClick: () -> Unit
) {
    AnimatedCard(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(CornerRadius.large)
            )
            .clickable(onClick = onClick)
            .padding(Spacing.mediumLarge)
            .size(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.semantics(mergeDescendants = true) {}
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = Spacing.small)
                    .semantics {
                        contentDescription = emojiContentDescription
                    }
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Hafif hareketli renkli arka plan animasyonu
// Lifecycle-aware: pauses animations when app is in background (saves battery)
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    duration: Int = AnimationDuration.backgroundSlow
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val isActive = lifecycleState == Lifecycle.State.RESUMED

    // Only run animations when app is in foreground to save battery
    val color1 = if (isActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "bg")
        infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            targetValue = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "bg1"
        ).value
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }

    val color2 = if (isActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "bg")
        infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
            targetValue = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.10f),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    duration + AnimationDuration.backgroundOffset,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ), label = "bg2"
        ).value
    } else {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f)
    }

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(color1, color2)
                )
            )
            .fillMaxSize()
    )
}

// Private helper functions

/**
 * Returns a time-appropriate greeting message
 */
private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 5 -> "Good night"
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        hour < 21 -> "Good evening"
        else -> "Good night"
    }
}

// Private composable components

/**
 * Unified Home Header - combines app branding, user profile, and menu access
 * This replaces both the AppTopBar and ProfileSummaryCard on the home screen
 * to eliminate the duplicate header issue.
 */
@Composable
private fun HomeHeader(
    username: String,
    avatar: String,
    level: Int,
    currentXP: Int,
    maxXP: Int,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // App branding row with menu button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App logo and name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_generating_tokens_24),
                    contentDescription = stringResource(id = R.string.app_name) + " logo",
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = Spacing.xs)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            // Menu button
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(id = R.string.menu),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Profile card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onProfileClick
        ) {
            Row(
                modifier = Modifier.padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar (emoji or first letter fallback)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatar.ifEmpty { username.take(1).uppercase() },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    // Personalized welcome message (fixes #186)
                    Text(
                        text = getGreeting() + ", $username!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Level $level • $currentXP / $maxXP XP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Arrow indicator for navigation
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyGoalsCard(
    modifier: Modifier = Modifier,
    wordsProgress: Float,
    wordsCount: String,
    quizzesProgress: Float,
    quizzesCount: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Daily Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Words: $wordsCount",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Quizzes: $quizzesCount",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * "Continue where you left off" card - shows the user's last quiz and allows quick restart
 * (fixes #187)
 */
@Composable
private fun ContinueCard(
    quizType: String,
    accuracy: Float,
    timestamp: Long,
    onClick: () -> Unit
) {
    val timeAgo = remember(timestamp) {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> "${days / 7}w ago"
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Replay icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Continue where you left off",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$quizType • ${(accuracy * 100).toInt()}% accuracy • $timeAgo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Hero Call-to-Action button - prominent primary action on HomeScreen
 * Designed to be the clear main action, reducing decision paralysis (fixes #172)
 */
@Composable
private fun HeroCTAButton(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(CornerRadius.large),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AchievementBadgeItem(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(CornerRadius.small))
            .clickable(onClick = onClick)
            .padding(Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}