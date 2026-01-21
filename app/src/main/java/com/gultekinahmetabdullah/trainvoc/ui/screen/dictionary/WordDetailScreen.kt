package com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.components.CEFRLevelChip
import com.gultekinahmetabdullah.trainvoc.ui.components.ElevatedCard
import com.gultekinahmetabdullah.trainvoc.ui.components.PrimaryButton
import com.gultekinahmetabdullah.trainvoc.ui.components.SecondaryButton
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsCorrect
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsIncorrect
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsSkipped
import com.gultekinahmetabdullah.trainvoc.ui.theme.statsTime
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enhanced Word Detail Screen following UI/UX Improvement Plan
 *
 * Features:
 * - Hero word section with large typography
 * - IPA pronunciation with audio button
 * - Section cards for Definition, Examples, Synonyms, Usage Frequency
 * - Animated entrance with staggered section fade-in
 * - Expandable sections for long content
 * - Synonym chips with navigation
 * - Action buttons (Favorite, Practice, Share)
 * - Visual polish and animations
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordDetailScreen(
    wordId: String,
    wordViewModel: WordViewModel,
    onNavigateToQuiz: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var word by remember {
        mutableStateOf<com.gultekinahmetabdullah.trainvoc.classes.word.Word?>(null)
    }
    var statistic by remember {
        mutableStateOf<com.gultekinahmetabdullah.trainvoc.classes.word.Statistic?>(null)
    }
    var exams by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var sectionsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(wordId) {
        coroutineScope.launch {
            val detail = wordViewModel.getWordFullDetail(wordId)
            word = detail?.word
            statistic = detail?.statistic
            exams = detail?.exams ?: emptyList()
            isFavorite = detail?.word?.isFavorite ?: false
            isLoading = false
            delay(100)
            sectionsVisible = true
        }
    }

    // Loading State
    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.md))
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
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Word not found",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = stringResource(id = R.string.word_not_found),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // Main Content with Hero Section
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Hero Word Section
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(AnimationDuration.medium)) +
                        slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = tween(AnimationDuration.medium)
                        )
            ) {
                HeroWordSection(
                    word = currentWord.word,
                    pronunciation = getIPAPronunciation(currentWord.word),
                    level = currentWord.level?.toString() ?: "A1",
                    exams = exams,
                    isFavorite = isFavorite,
                    onFavoriteClick = {
                        isFavorite = !isFavorite
                        wordViewModel.toggleFavorite(currentWord.word, isFavorite)
                    },
                    onAudioClick = {
                        wordViewModel.speakWord(currentWord.word)
                    }
                )
            }
        }

        // Definition Section
        item {
            AnimatedVisibility(
                visible = sectionsVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        delayMillis = 100
                    )
                )
            ) {
                DefinitionSection(
                    partOfSpeech = getPartOfSpeech(currentWord.meaning),
                    definition = currentWord.meaning
                )
            }
        }

        // Examples Section
        item {
            AnimatedVisibility(
                visible = sectionsVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        delayMillis = 200
                    )
                )
            ) {
                ExamplesSection(
                    word = currentWord.word,
                    examples = getExamples(currentWord.word, currentWord.meaning),
                    onExampleClick = { example ->
                        wordViewModel.speakWord(example)
                    }
                )
            }
        }

        // Synonyms Section
        item {
            AnimatedVisibility(
                visible = sectionsVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        delayMillis = 300
                    )
                )
            ) {
                val synonyms = getSynonyms(currentWord.word)
                if (synonyms.isNotEmpty()) {
                    SynonymsSection(
                        synonyms = synonyms,
                        onSynonymClick = { synonym ->
                            // TODO: Navigate to synonym's detail page
                        }
                    )
                }
            }
        }

        // Usage Frequency Section
        item {
            AnimatedVisibility(
                visible = sectionsVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        delayMillis = 400
                    )
                )
            ) {
                UsageFrequencySection(
                    frequency = getUsageFrequency(currentWord.level?.ordinal ?: 0)
                )
            }
        }

        // Statistics Card
        item {
            AnimatedVisibility(
                visible = sectionsVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        delayMillis = 500
                    )
                )
            ) {
                val currentStatistic = statistic
                if (currentStatistic != null) {
                    StatisticsSection(
                        statistic = currentStatistic,
                        secondsSpent = currentWord.secondsSpent,
                        lastReviewed = currentWord.lastReviewed
                    )
                }
            }
        }

        // Action Buttons
        item {
            AnimatedVisibility(
                visible = sectionsVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        delayMillis = 600
                    )
                )
            ) {
                ActionButtonsSection(
                    isFavorite = isFavorite,
                    onFavoriteClick = {
                        isFavorite = !isFavorite
                        wordViewModel.toggleFavorite(currentWord.word, isFavorite)
                    },
                    onPracticeClick = {
                        // Launch practice quiz with this specific word
                        onNavigateToQuiz(currentWord.word)
                    },
                    onShareClick = {
                        // Share word definition using Android share intent
                        val shareText = buildString {
                            append("📚 ${currentWord.word}\n")
                            append("📖 ${currentWord.meaning}\n")
                            if (exams.isNotEmpty()) {
                                append("🎓 Exams: ${exams.joinToString(", ")}\n")
                            }
                            append("\n✨ Shared from Trainvoc")
                        }
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Word: ${currentWord.word}")
                        }
                        context.startActivity(
                            android.content.Intent.createChooser(shareIntent, "Share word")
                        )
                    }
                )
            }
        }
    }
}

/**
 * Hero Word Section with large typography and badges
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroWordSection(
    word: String,
    pronunciation: String,
    level: String,
    exams: List<String>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onAudioClick: () -> Unit
) {
    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "heartScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Word title with favorite
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = word.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.scale(heartScale)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(IconSize.large)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Pronunciation with audio
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pronunciation,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            IconButton(
                onClick = onAudioClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Play pronunciation",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Level and exam badges
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            CEFRLevelChip(
                level = level,
                modifier = Modifier.padding(horizontal = Spacing.xs)
            )
            exams.take(3).forEach { exam ->
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = exam,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.padding(horizontal = Spacing.xs)
                )
            }
        }
    }
}

/**
 * Definition Section Card
 */
@Composable
fun DefinitionSection(
    partOfSpeech: String,
    definition: String
) {
    ElevatedCard(
        elevation = Elevation.level1
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "No related words",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(IconSize.medium)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "Definition",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = partOfSpeech,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = definition,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}

/**
 * Examples Section Card with bullet list
 */
@Composable
fun ExamplesSection(
    word: String,
    examples: List<String>,
    onExampleClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(examples.size <= 3) }

    ElevatedCard(
        elevation = Elevation.level1
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.md)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💬",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "Examples",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (examples.size > 3) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Show less" else "Show more"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            val displayedExamples = if (expanded) examples else examples.take(3)
            displayedExamples.forEach { example ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExampleClick(example) }
                        .padding(vertical = Spacing.sm)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = Spacing.sm)
                    )
                    Text(
                        text = buildAnnotatedString {
                            val parts = example.split(word, ignoreCase = true)
                            parts.forEachIndexed { index, part ->
                                append(part)
                                if (index < parts.size - 1) {
                                    withStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(word)
                                    }
                                }
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Synonyms Section with chip layout
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SynonymsSection(
    synonyms: List<String>,
    onSynonymClick: (String) -> Unit
) {
    ElevatedCard(
        elevation = Elevation.level1
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔄",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "Synonyms",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                synonyms.forEach { synonym ->
                    SuggestionChip(
                        onClick = { onSynonymClick(synonym) },
                        label = {
                            Text(
                                text = synonym,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Usage Frequency Section with progress bar
 */
@Composable
fun UsageFrequencySection(
    frequency: UsageFrequency
) {
    ElevatedCard(
        elevation = Elevation.level1
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "Usage Frequency",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { frequency.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(CornerRadius.small)),
                    color = frequency.color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Text(
                    text = frequency.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = frequency.color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Statistics Section Card
 */
@Composable
fun StatisticsSection(
    statistic: com.gultekinahmetabdullah.trainvoc.classes.word.Statistic,
    secondsSpent: Int,
    lastReviewed: Long?
) {
    val total = statistic.correctCount +
            statistic.wrongCount +
            statistic.skippedCount
    val successRate = if (total > 0)
        statistic.correctCount.toFloat() / total
    else 0f

    ElevatedCard(
        elevation = Elevation.level1
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Statistics",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(IconSize.large)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(id = R.string.statistics),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Success Rate Progress
            Text(
                text = stringResource(
                    id = R.string.success_rate_percent,
                    (successRate * 100).toInt()
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            LinearProgressIndicator(
                progress = { successRate },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(CornerRadius.small)),
                color = MaterialTheme.colorScheme.statsCorrect,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.md))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.md))

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    label = stringResource(id = R.string.correct),
                    value = statistic.correctCount.toString(),
                    color = MaterialTheme.colorScheme.statsCorrect
                )
                StatisticItem(
                    icon = Icons.Default.Close,
                    label = stringResource(id = R.string.wrong),
                    value = statistic.wrongCount.toString(),
                    color = MaterialTheme.colorScheme.statsIncorrect
                )
                StatisticItem(
                    icon = painterResource(id = R.drawable.baseline_skip_next_24),
                    label = stringResource(id = R.string.skipped),
                    value = statistic.skippedCount.toString(),
                    color = MaterialTheme.colorScheme.statsSkipped
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.md))

            // Time Spent
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_timer_24),
                    contentDescription = "Time",
                    tint = MaterialTheme.colorScheme.statsTime,
                    modifier = Modifier.size(IconSize.medium)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(
                        id = R.string.total_seconds,
                        secondsSpent
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Last Reviewed
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_bar_chart_24),
                    contentDescription = "Last Reviewed",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(IconSize.medium)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(
                        id = R.string.last_reviewed,
                        lastReviewed?.toString() ?: "-"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Action Buttons Section
 */
@Composable
fun ActionButtonsSection(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            SecondaryButton(
                text = if (isFavorite) "Favorited" else "Favorite",
                onClick = onFavoriteClick,
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = "Share",
                onClick = onShareClick,
                icon = Icons.Default.Share,
                modifier = Modifier.weight(1f)
            )
        }

        PrimaryButton(
            text = "Practice This Word",
            onClick = onPracticeClick,
            modifier = Modifier.fillMaxWidth()
        )
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
        Spacer(modifier = Modifier.height(Spacing.xs))
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

// ============================================================
// Helper Data Classes and Functions
// ============================================================

/**
 * Usage frequency data class with color coding
 */
data class UsageFrequency(
    val label: String,
    val progress: Float,
    val color: Color
)

/**
 * Get usage frequency based on CEFR level
 */
@Composable
fun getUsageFrequency(level: Int): UsageFrequency {
    return when (level) {
        0, 1 -> UsageFrequency(
            label = "Very Common",
            progress = 0.9f,
            color = Color(0xFF4CAF50)
        )
        2, 3 -> UsageFrequency(
            label = "Common",
            progress = 0.7f,
            color = Color(0xFF2196F3)
        )
        4 -> UsageFrequency(
            label = "Uncommon",
            progress = 0.4f,
            color = Color(0xFFFFA726)
        )
        else -> UsageFrequency(
            label = "Rare",
            progress = 0.2f,
            color = Color(0xFFE91E63)
        )
    }
}

/**
 * Get IPA pronunciation (placeholder - would come from dictionary API)
 */
fun getIPAPronunciation(word: String): String {
    // TODO: Implement actual IPA lookup from dictionary API
    return when (word.lowercase()) {
        "eloquent" -> "/ˈeləkwənt/"
        "abandon" -> "/əˈbændən/"
        "ability" -> "/əˈbɪləti/"
        else -> "/${word.lowercase()}/"
    }
}

/**
 * Get part of speech from meaning (simple heuristic)
 */
fun getPartOfSpeech(meaning: String): String {
    // TODO: Implement proper part of speech detection
    return when {
        meaning.contains("to ", ignoreCase = true) -> "verb"
        meaning.contains("the ", ignoreCase = true) -> "noun"
        meaning.contains("describing", ignoreCase = true) -> "adjective"
        else -> "noun"
    }
}

/**
 * Get example sentences (placeholder - would come from database or API)
 */
fun getExamples(word: String, meaning: String): List<String> {
    // TODO: Implement actual examples from database
    return listOf(
        "She gave an $word speech at the conference.",
        "His $word words moved the audience.",
        "The professor is known for being $word."
    )
}

/**
 * Get synonyms (placeholder - would come from thesaurus API)
 */
fun getSynonyms(word: String): List<String> {
    // TODO: Implement actual synonym lookup from thesaurus API
    return when (word.lowercase()) {
        "eloquent" -> listOf("articulate", "expressive", "fluent", "persuasive")
        "abandon" -> listOf("desert", "forsake", "leave", "quit")
        "ability" -> listOf("capability", "capacity", "skill", "talent")
        else -> emptyList()
    }
}
