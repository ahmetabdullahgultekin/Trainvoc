package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Book
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
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
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

@Composable
fun HomeScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateToStory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGames: () -> Unit,
    // Phase 1 - New Navigation Callbacks
    onNavigateToProfile: () -> Unit = {},
    onNavigateToWordOfDay: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToLastQuiz: () -> Unit = {},
    // Phase 2 & 3 - Gamification & Engagement Navigation
    onNavigateToDailyGoals: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToStreakDetail: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToWordProgress: () -> Unit = {},
    onNavigateToDictionary: () -> Unit = {},
    preloadLottie: LottieComposition? = null,
    preloadBg: Painter? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Use Lottie animation and image with preload
    val composition = preloadLottie
        ?: rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_diamond.json")).value
    // Lifecycle-aware animations: pause when app is in background to save battery
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val isActive = lifecycleState == Lifecycle.State.RESUMED

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isActive, // Only play when app is active
    )
    val bgPainter = preloadBg ?: painterResource(id = R.drawable.bg_3)

    // Button Scaling Animation (lifecycle-aware)
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(isActive) {
        if (isActive) {
            scaleAnim.animateTo(
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(AnimationDuration.buttonPulse, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            scaleAnim.snapTo(1f) // Reset to original size when paused
        }
    }

    // Start animations slowly on first launch, then speed up
    val animDuration = remember { mutableIntStateOf(AnimationDuration.screenInit) }
    LaunchedEffect(Unit) {
        animDuration.intValue = AnimationDuration.screenInit
        kotlinx.coroutines.delay(1200)
        animDuration.intValue = AnimationDuration.buttonPulse
    }

    val isScreenVisible = rememberUpdatedState(true)
    DisposableEffect(isScreenVisible.value) {
        onDispose {
            // Here you could pause Lottie if needed
        }
    }

    // Root container with background image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("HomeScreenRoot")
    ) {
        // Layer background animation and image on top of each other
        Box(modifier = Modifier.matchParentSize()) {
            AnimatedBackground(
                modifier = Modifier.matchParentSize(),
                duration = animDuration.intValue
            )
            // bg_3 image as semi-transparent overlay on top, lower alpha
            Image(
                painter = bgPainter,
                contentDescription = "Background decoration",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds,
                alpha = 0.1f // Opacity increased, animation will be more visible
            )
        }
        // Main content on top
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.mediumLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            state = rememberLazyListState(),
            content = {
                item {
                    // App Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = stringResource(id = R.string.app_icon_desc),
                        modifier = Modifier
                            .size(120.dp)
                            .padding(Spacing.small)
                            .testTag("AppLogo")
                    )

                    Spacer(modifier = Modifier.height(Spacing.mediumLarge))

                    // Welcome Message
                    Text(
                        text = stringResource(id = R.string.home_welcome),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.testTag("WelcomeText")
                    )

                    Spacer(modifier = Modifier.height(Spacing.small))

                    Text(
                        text = stringResource(id = R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .padding(horizontal = Spacing.mediumLarge)
                            .align(Alignment.Center)
                            .testTag("SubtitleText"),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    /**
                     * GAMIFICATION FEATURES - PLANNED FOR FUTURE RELEASE
                     *
                     * The following sections display gamification UI elements that are
                     * currently showing placeholder/mock data. These features are planned
                     * for future implementation:
                     *
                     * 1. User Profile & XP System
                     *    - Dynamic username from user preferences
                     *    - Level progression based on quiz performance
                     *    - Experience points (XP) tracking
                     *
                     * 2. Daily Tasks & Achievements
                     *    - Task tracking (quizzes solved, words learned, achievements earned)
                     *    - Achievement/badge system
                     *    - Progress persistence
                     *
                     * 3. Quiz Categories
                     *    - General, Targeted, and Quick quiz modes
                     *    - Category-specific statistics
                     *
                     * 4. Quick Access Features
                     *    - Word of the Day
                     *    - Favorites management
                     *    - Quiz history
                     *
                     * Implementation Status: UI completed, backend logic pending
                     * Estimated Completion: Next major release
                     */

                    // XP Bar & Avatar Card (Real Data from ViewModel)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar Card with Level
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = stringResource(id = R.string.app_icon_desc),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.username_placeholder),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.level_colon,
                                            uiState.level.toString()
                                        ),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        // XP Bar with Real Progress
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.total_score),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(uiState.xpProgress.coerceIn(0.01f, 1f))
                                        .height(16.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                )
                            }
                            Text(
                                text = stringResource(
                                    id = R.string.xp_progress,
                                    uiState.xpCurrent,
                                    uiState.xpForNextLevel
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    // Streak and Word Progress Row - CLICKABLE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Streak Display - Clickable to StreakDetail
                        if (uiState.currentStreak > 0) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onNavigateToStreakDetail() }
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "\uD83D\uDD25 ${uiState.currentStreak}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "day streak",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        // Words Progress - Clickable to WordProgress
                        if (uiState.totalWords > 0) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onNavigateToWordProgress() }
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "\uD83D\uDCDA ${uiState.learnedWords}/${uiState.totalWords}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "words learned",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Lottie Animation
                    val lottieDesc = stringResource(id = R.string.lottie_desc)
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .size(200.dp)
                            .semantics {
                                contentDescription = lottieDesc
                            }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Call-to-Action Buttons - Equal treatment for Quiz, Story, and Games
                    DebouncedButton(
                        onClick = onNavigateToQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scaleAnim.value)
                            .testTag("QuizButton"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.start_quiz),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DebouncedButton(
                        onClick = onNavigateToStory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scaleAnim.value)
                            .testTag("StoryButton"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.story_mode),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DebouncedButton(
                        onClick = onNavigateToGames,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scaleAnim.value)
                            .testTag("GamesMainButton"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.games),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HomeNavButton(
                            stringResource(id = R.string.games),
                            Icons.Default.SportsEsports,
                            onNavigateToGames,
                            "GamesButton"
                        )
                        HomeNavButton(
                            stringResource(id = R.string.settings),
                            Icons.Default.Settings,
                            onNavigateToSettings,
                            "SettingsButton"
                        )
                        HomeNavButton(
                            stringResource(id = R.string.help),
                            Icons.Default.Info,
                            onNavigateToHelp,
                            "HelpButton"
                        )
                        HomeNavButton(
                            stringResource(id = R.string.stats),
                            Icons.Default.CheckCircle,
                            onNavigateToStats,
                            "StatsButton"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Tasks and Achievements (Gamification) - CLICKABLE SECTIONS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        // Daily Tasks Header - Clickable
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDailyGoals() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.daily_tasks),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "View All →",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Task 1 - Quizzes (Real Data)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(id = R.string.task_solve_quizzes),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(id = R.string.task_progress, uiState.quizzesCompleted, uiState.quizzesGoal),
                                    color = if (uiState.quizzesCompleted >= uiState.quizzesGoal)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.secondary
                                )
                            }
                            // Task 2 - Words Learned (Real Data)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(id = R.string.task_learn_words),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(id = R.string.task_progress, uiState.wordsLearnedToday, uiState.wordsGoalToday),
                                    color = if (uiState.wordsLearnedToday >= uiState.wordsGoalToday)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.secondary
                                )
                            }
                            // Task 3 - Achievements (Real Data)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(id = R.string.task_earn_achievement),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(id = R.string.task_progress, uiState.achievementsUnlocked.coerceAtMost(1), 1),
                                    color = if (uiState.achievementsUnlocked >= 1)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Achievements Header - Clickable
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAchievements() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.achievements),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "View All →",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Dynamic Achievements Display
                        if (uiState.unlockedAchievements.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.unlockedAchievements.take(5)) { userAchievement ->
                                    val achievement = userAchievement.getAchievement()
                                    if (achievement != null) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.semantics(mergeDescendants = true) {}
                                        ) {
                                            Text(
                                                achievement.icon,
                                                fontSize = 32.sp,
                                                modifier = Modifier.semantics {
                                                    contentDescription = achievement.title
                                                }
                                            )
                                            Text(
                                                achievement.title,
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // No achievements unlocked yet - show placeholder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.semantics(mergeDescendants = true) {}
                                ) {
                                    Text(
                                        "🔒",
                                        fontSize = 32.sp,
                                        modifier = Modifier.semantics {
                                            contentDescription = context.getString(R.string.achievement_locked)
                                        }
                                    )
                                    Text(
                                        stringResource(id = R.string.achievement_first_quiz),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.semantics(mergeDescendants = true) {}
                                ) {
                                    Text(
                                        "🔒",
                                        fontSize = 32.sp,
                                        modifier = Modifier.semantics {
                                            contentDescription = context.getString(R.string.achievement_locked)
                                        }
                                    )
                                    Text(
                                        stringResource(id = R.string.achievement_streak_day),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.semantics(mergeDescendants = true) {}
                                ) {
                                    Text(
                                        "🔒",
                                        fontSize = 32.sp,
                                        modifier = Modifier.semantics {
                                            contentDescription = context.getString(R.string.achievement_locked)
                                        }
                                    )
                                    Text(
                                        stringResource(id = R.string.achievement_100_words),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.mediumLarge))

                    // Category-based Quiz Selection (Animated Cards)
                    Text(
                        text = stringResource(id = R.string.categories),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        item {
                            CategoryCard(
                                emoji = "🧠",
                                title = stringResource(id = R.string.category_general),
                                emojiContentDescription = stringResource(id = R.string.category_brain_training),
                                onClick = onNavigateToQuiz
                            )
                        }
                        item {
                            CategoryCard(
                                emoji = "🎯",
                                title = stringResource(id = R.string.category_targeted),
                                emojiContentDescription = stringResource(id = R.string.category_target_practice),
                                onClick = onNavigateToStory
                            )
                        }
                        item {
                            CategoryCard(
                                emoji = "⚡",
                                title = stringResource(id = R.string.category_quick),
                                emojiContentDescription = stringResource(id = R.string.category_quick_quiz),
                                onClick = onNavigateToGames
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.mediumLarge))

                    // Quick Access Buttons (Word of the Day, Favorites, Last Quiz)
                    Text(
                        text = stringResource(id = R.string.quick_access),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        item {
                            QuickAccessCard(
                                emoji = "🌟",
                                title = stringResource(id = R.string.word_of_the_day),
                                emojiContentDescription = stringResource(id = R.string.quick_access_word_of_day),
                                onClick = onNavigateToWordOfDay
                            )
                        }
                        item {
                            QuickAccessCard(
                                emoji = "❤️",
                                title = stringResource(id = R.string.favorites),
                                emojiContentDescription = stringResource(id = R.string.quick_access_favorites),
                                onClick = onNavigateToFavorites
                            )
                        }
                        item {
                            QuickAccessCard(
                                emoji = "⏱️",
                                title = stringResource(id = R.string.last_quiz),
                                emojiContentDescription = stringResource(id = R.string.quick_access_last_quiz),
                                onClick = onNavigateToLastQuiz
                            )
                        }
                        item {
                            QuickAccessCard(
                                emoji = "🏆",
                                title = "Leaderboard",
                                emojiContentDescription = "View leaderboard rankings",
                                onClick = onNavigateToLeaderboard
                            )
                        }
                        item {
                            QuickAccessCard(
                                emoji = "📖",
                                title = "Dictionary",
                                emojiContentDescription = "Browse dictionary",
                                onClick = onNavigateToDictionary
                            )
                        }
                        item {
                            QuickAccessCard(
                                emoji = "📊",
                                title = "Progress",
                                emojiContentDescription = "View word progress",
                                onClick = onNavigateToWordProgress
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        ) // End of LazyColumn
    }
    // AnimatedBackground is no longer called here
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