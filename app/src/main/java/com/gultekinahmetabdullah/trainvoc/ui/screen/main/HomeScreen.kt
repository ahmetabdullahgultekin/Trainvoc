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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
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
    preloadLottie: LottieComposition? = null, // Type corrected
    preloadBg: Painter? = null
) {
    val context = LocalContext.current

    // Use Lottie animation and image with preload
    val composition = preloadLottie
        ?: rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_diamond.json")).value
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
    )
    val bgPainter = preloadBg ?: painterResource(id = R.drawable.bg_3)

    // Button Scaling Animation
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Start animations slowly on first launch, then speed up
    val animDuration = remember { mutableIntStateOf(2000) }
    LaunchedEffect(Unit) {
        animDuration.intValue = 2000
        kotlinx.coroutines.delay(1200)
        animDuration.intValue = 1000
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
                contentDescription = null,
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
                    // Warning Text for Test Mode
                    Text(
                        text = stringResource(id = R.string.test_mode_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(Spacing.small)
                            .background(
                                color = MaterialTheme.colorScheme.error.copy(alpha = Alpha.surfaceVariant),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(Spacing.medium),
                        fontSize = 18.sp
                    )
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.testTag("WelcomeText")
                    )

                    Spacer(modifier = Modifier.height(Spacing.small))

                    Text(
                        text = stringResource(id = R.string.home_subtitle),
                        fontSize = 16.sp,
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

                    // XP Bar & Avatar Card (Gamification)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar Card
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
                                        text = stringResource(id = R.string.username_placeholder), // Placeholder: awaiting user profile implementation
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.level_colon,
                                            "2"
                                        ), // Placeholder: awaiting XP system implementation
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        // XP Bar
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
                                        .fillMaxWidth(0.4f) // Placeholder: 40% progress (1200/3000 XP)
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
                                    1200,
                                    3000
                                ), // Placeholder: awaiting XP tracking system
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.End)
                            )
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

                    // Main Call-to-Action Button
                    DebouncedButton(
                        onClick = onNavigateToQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scaleAnim.value)
                            .testTag("QuizButton"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = stringResource(id = R.string.start_quiz), fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.start_quiz_button)
                        )
                    }
                    DebouncedButton(
                        onClick = onNavigateToStory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scaleAnim.value)
                            .testTag("StoryButton"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(text = stringResource(id = R.string.story_mode), fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.start_quiz_button)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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

                    // Daily Tasks and Achievements (Gamification)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.daily_tasks),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Task 1
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(id = R.string.task_solve_quizzes),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(id = R.string.task_progress, 0, 3),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            // Task 2
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(id = R.string.task_learn_words),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(id = R.string.task_progress, 2, 10),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            // Task 3
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(id = R.string.task_earn_achievement),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(id = R.string.task_progress, 0, 1),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(id = R.string.achievements),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Badge 1
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.semantics(mergeDescendants = true) {}
                            ) {
                                Text(
                                    "🥇",
                                    fontSize = 32.sp,
                                    modifier = Modifier.semantics {
                                        contentDescription =
                                            context.getString(R.string.achievement_first_place)
                                    }
                                )
                                Text(
                                    stringResource(id = R.string.achievement_first_quiz),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            // Badge 2
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.semantics(mergeDescendants = true) {}
                            ) {
                                Text(
                                    "🔥",
                                    fontSize = 32.sp,
                                    modifier = Modifier.semantics {
                                        contentDescription =
                                            context.getString(R.string.achievement_streak)
                                    }
                                )
                                Text(
                                    stringResource(id = R.string.achievement_streak_day),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            // Badge 3
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.semantics(mergeDescendants = true) {}
                            ) {
                                Text(
                                    "📚",
                                    fontSize = 32.sp,
                                    modifier = Modifier.semantics {
                                        contentDescription =
                                            context.getString(R.string.achievement_books)
                                    }
                                )
                                Text(
                                    stringResource(id = R.string.achievement_100_words),
                                    style = MaterialTheme.typography.bodySmall
                                )
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        CategoryCard(
                            emoji = "🧠",
                            title = stringResource(id = R.string.category_general),
                            emojiContentDescription = stringResource(id = R.string.category_brain_training),
                            onClick = { /* Planned feature */ }
                        )
                        CategoryCard(
                            emoji = "🎯",
                            title = stringResource(id = R.string.category_targeted),
                            emojiContentDescription = stringResource(id = R.string.category_target_practice),
                            onClick = { /* Planned feature */ }
                        )
                        CategoryCard(
                            emoji = "⚡",
                            title = stringResource(id = R.string.category_quick),
                            emojiContentDescription = stringResource(id = R.string.category_quick_quiz),
                            onClick = { /* Planned feature */ }
                        )
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        QuickAccessCard(
                            emoji = "🌟",
                            title = stringResource(id = R.string.word_of_the_day),
                            emojiContentDescription = stringResource(id = R.string.quick_access_word_of_day),
                            onClick = { /* Planned feature */ }
                        )
                        QuickAccessCard(
                            emoji = "❤️",
                            title = stringResource(id = R.string.favorites),
                            emojiContentDescription = stringResource(id = R.string.quick_access_favorites),
                            onClick = { /* Planned feature */ }
                        )
                        QuickAccessCard(
                            emoji = "⏱️",
                            title = stringResource(id = R.string.last_quiz),
                            emojiContentDescription = stringResource(id = R.string.quick_access_last_quiz),
                            onClick = { /* Planned feature */ }
                        )
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
                fontSize = 15.sp,
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
    duration: Int = 40000
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
                animation = tween(duration + 12000, easing = FastOutSlowInEasing),
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