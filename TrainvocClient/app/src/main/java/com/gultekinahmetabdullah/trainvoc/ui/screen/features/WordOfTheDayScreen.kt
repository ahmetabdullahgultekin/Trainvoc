package com.gultekinahmetabdullah.trainvoc.ui.screen.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.components.LargeButton
import com.gultekinahmetabdullah.trainvoc.ui.components.RollingCatLoaderWithText
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize
import com.gultekinahmetabdullah.trainvoc.ui.components.SecondaryButton
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.AppAnimationSpec
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.StaggerDelay
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordOfDayViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Word of the Day Screen
 *
 * Shows a featured word each day with:
 * - Word and meaning
 * - Level badge
 * - Audio pronunciation (TTS)
 * - Add to favorites option
 * - Practice quiz option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordOfTheDayScreen(
    onBackClick: () -> Unit = {},
    onPractice: (String) -> Unit = {},
    onViewPreviousWords: () -> Unit = {},
    viewModel: WordOfDayViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val wordOfDay by viewModel.wordOfDay.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.word_of_the_day)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    RollingCatLoaderWithText(
                        message = stringResource(id = R.string.loading_word_of_day),
                        size = LoaderSize.large
                    )
                }
            }
            error != null -> {
                ErrorState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = error!!,
                    onRetry = { viewModel.retry() }
                )
            }
            wordOfDay != null -> {
                WordOfDayContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    word = wordOfDay!!.word,
                    meaning = wordOfDay!!.meaning,
                    level = wordOfDay!!.level?.name ?: "Unknown",
                    currentDate = currentDate,
                    isFavorite = isFavorite,
                    onToggleFavorite = { viewModel.toggleFavorite() },
                    onPractice = { onPractice(wordOfDay!!.word) },
                    onViewPreviousWords = onViewPreviousWords
                )
            }
            else -> {
                ErrorState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = stringResource(id = R.string.no_word_available),
                    onRetry = { viewModel.retry() }
                )
            }
        }
    }
}

@Composable
private fun WordOfDayContent(
    modifier: Modifier = Modifier,
    word: String,
    meaning: String,
    level: String,
    currentDate: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onPractice: () -> Unit,
    onViewPreviousWords: () -> Unit
) {
    // Animation state
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Card with gradient background
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(AnimationDuration.medium)) +
                    scaleIn(initialScale = 0.9f, animationSpec = tween(AnimationDuration.medium))
        ) {
            HeroWordCard(
                word = word,
                meaning = meaning,
                level = level,
                currentDate = currentDate,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Streak Tracker Card
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(AnimationDuration.medium, delayMillis = StaggerDelay.short))
        ) {
            StreakTrackerCard()
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Practice Button
        val practiceThisWordText = stringResource(id = R.string.practice_this_word)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(AnimationDuration.medium, delayMillis = StaggerDelay.medium))
        ) {
            LargeButton(
                text = practiceThisWordText,
                onClick = onPractice,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // View Previous Words Button
        val viewPreviousWordsText = stringResource(id = R.string.view_previous_words)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(AnimationDuration.medium, delayMillis = StaggerDelay.long))
        ) {
            SecondaryButton(
                text = viewPreviousWordsText,
                onClick = onViewPreviousWords,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))
    }
}

/**
 * Hero Word Card with gradient background and decorative elements
 */
@Composable
private fun HeroWordCard(
    word: String,
    meaning: String,
    level: String,
    currentDate: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = Elevation.level2)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryContainer,
                            primaryContainer.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            // Favorite button in top right
            val removeFromFavoritesDesc = stringResource(id = R.string.remove_from_favorites)
            val addToFavoritesDesc = stringResource(id = R.string.add_to_favorites)
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Spacing.sm)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) removeFromFavoritesDesc else addToFavoritesDesc,
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with sparkles and date
                Text(
                    text = "✨ ${formatDateWithStyle(currentDate)} ✨",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Decorative divider
                Text(
                    text = "━━━━━━━━━━━━",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Featured Word (all caps, large typography)
                Text(
                    text = word.uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Pronunciation with speaker icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "/${getPronunciation(word)}/ ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontStyle = FontStyle.Italic
                    )
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = stringResource(id = R.string.content_desc_pronunciation),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                // CEFR Level and Category badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LevelBadge(level = level)
                    CategoryBadge(partOfSpeech = getPartOfSpeech(meaning))
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Decorative divider
                Text(
                    text = "━━━━━━━━━━━━",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Definition Section
                Text(
                    text = getPartOfSpeech(meaning),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Decorative divider
                Text(
                    text = "━━━━━━━━━━━━",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Example Section
                val exampleText = getExampleSentence(word, meaning)
                Text(
                    text = exampleText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
    }
}

/**
 * Streak Tracker Card showing last 7 days
 */
@Composable
private fun StreakTrackerCard() {
    // For now, using mock data. This should come from ViewModel in production
    val streakDays = 5 // Mock streak
    val last7Days = remember { generateLast7Days() }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = Elevation.level1)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(id = R.string.content_desc_word_of_day_icon),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(id = R.string.your_streak),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Encouraging message
            val fireEmoji = if (streakDays > 3) " 🔥" else ""
            Text(
                text = stringResource(id = R.string.streak_days_message, streakDays) + fireEmoji,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Last 7 days calendar dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                last7Days.forEachIndexed { index, dayInfo ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay((index * StaggerDelay.short).toLong())
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(AnimationDuration.quick)) +
                                scaleIn(initialScale = 0.3f, animationSpec = tween(AnimationDuration.quick))
                    ) {
                        DayCheckmark(
                            dayLabel = dayInfo.first,
                            isChecked = dayInfo.second
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual day checkmark indicator
 */
@Composable
private fun DayCheckmark(
    dayLabel: String,
    isChecked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(
            text = dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isChecked)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.content_desc_checked),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Level Badge Component
 */
@Composable
private fun LevelBadge(level: String) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(CornerRadius.medium),
        tonalElevation = Elevation.level1
    ) {
        Text(
            text = level,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Category Badge (Part of Speech)
 */
@Composable
private fun CategoryBadge(partOfSpeech: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(CornerRadius.medium),
        tonalElevation = Elevation.level1
    ) {
        Text(
            text = partOfSpeech,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = stringResource(id = R.string.content_desc_error),
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, stringResource(id = R.string.content_desc_retry))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(id = R.string.retry))
            }
        }
    }
}

// Helper functions

/**
 * Format date with a nicer style (e.g., "January 11")
 */
private fun formatDateWithStyle(dateString: String): String {
    return try {
        val date = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).parse(dateString)
        SimpleDateFormat("MMMM d", Locale.getDefault()).format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Get pronunciation (mock implementation - should use actual pronunciation data)
 */
private fun getPronunciation(word: String): String {
    // Mock pronunciation - in production, this should come from database
    return word.lowercase()
}

/**
 * Extract or infer part of speech from meaning
 */
private fun getPartOfSpeech(meaning: String): String {
    // Simple heuristic - in production, this should come from database
    return when {
        meaning.contains("to ", ignoreCase = true) && meaning.indexOf("to") < 10 -> "verb"
        meaning.contains("a person", ignoreCase = true) -> "noun"
        meaning.contains("describe", ignoreCase = true) -> "adjective"
        meaning.contains("manner", ignoreCase = true) -> "adverb"
        else -> "noun"
    }
}

/**
 * Generate example sentence
 */
private fun getExampleSentence(word: String, meaning: String): String {
    // Mock example - in production, this should come from database
    return "\"The ${word.lowercase()} was clearly evident in the situation.\""
}

/**
 * Generate last 7 days data (mock)
 */
private fun generateLast7Days(): List<Pair<String, Boolean>> {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    // Mock: last 5 days checked, last 2 not checked
    return daysOfWeek.mapIndexed { index, day ->
        day to (index < 5)
    }
}
