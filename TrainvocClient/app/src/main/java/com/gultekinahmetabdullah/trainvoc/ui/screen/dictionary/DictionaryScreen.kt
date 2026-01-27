package com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.ui.components.CEFRLevelChip
import com.gultekinahmetabdullah.trainvoc.ui.components.ErrorState
import com.gultekinahmetabdullah.trainvoc.ui.components.InfoCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDelay
import com.gultekinahmetabdullah.trainvoc.ui.theme.AppAnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Enhanced Dictionary Screen following UI/UX Improvement Plan
 *
 * Features:
 * - Material 3 SearchBar with debounce and suggestions
 * - Filter chips for CEFR levels (A1-C2) and Favorites
 * - Word cards with pronunciation, favorite, and quick actions
 * - Pull to refresh
 * - Alphabet fast scroll with haptic feedback
 * - Staggered card entrance animations
 * - Empty states for no results and no favorites
 * - Shimmer loading effect
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DictionaryScreen(navController: NavController, wordViewModel: WordViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarActive by remember { mutableStateOf(false) }
    val allWords by wordViewModel.words.collectAsState()
    val filteredWords by wordViewModel.filteredWords.collectAsState()
    val isLoadingWords by wordViewModel.isLoading.collectAsState()
    val loadError by wordViewModel.loadError.collectAsState()
    var isSearching by remember { mutableStateOf(false) }

    // Filter state
    val selectedFilters = remember { mutableStateListOf<String>() }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    // Search history
    val searchHistory = remember { mutableStateListOf<String>() }

    // Pull to refresh
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Alphabet fast scroll state
    var showAlphabetScroll by remember { mutableStateOf(false) }
    var currentScrollLetter by remember { mutableStateOf("") }
    val view = LocalView.current

    // Debounced search
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
        }
        delay(300)  // 300ms debounce delay
        wordViewModel.filterWords(searchQuery)
        isSearching = false
    }

    // Handle pull to refresh
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(1000)
            isRefreshing = false
        }
    }

    // Apply filters to displayed words
    val displayedWords by remember {
        derivedStateOf {
            val baseList = if (searchQuery.isEmpty()) {
                allWords.map { it.word }
            } else {
                filteredWords
            }

            var result = baseList

            // Filter by CEFR levels
            if (selectedFilters.isNotEmpty()) {
                result = result.filter { word ->
                    word.level?.name in selectedFilters
                }
            }

            // Filter by favorites
            if (showFavoritesOnly) {
                result = result.filter { it.isFavorite }
            }

            result.sortedBy { it.word }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.md)
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            // Material 3 SearchBar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { query ->
                    if (query.isNotEmpty() && !searchHistory.contains(query)) {
                        searchHistory.add(0, query)
                        if (searchHistory.size > 5) searchHistory.removeLast()
                    }
                    isSearchBarActive = false
                },
                active = isSearchBarActive,
                onActiveChange = { isSearchBarActive = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.search_word)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search_word)
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = searchQuery.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.clear_search)
                            )
                        }
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                // Search history/suggestions
                if (searchHistory.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.recent_searches),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(Spacing.md)
                    )
                    searchHistory.forEach { historyItem ->
                        DropdownMenuItem(
                            text = { Text(historyItem) },
                            onClick = {
                                searchQuery = historyItem
                                isSearchBarActive = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.search_history),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Favorites filter chip
                FilterChip(
                    selected = showFavoritesOnly,
                    onClick = { showFavoritesOnly = !showFavoritesOnly },
                    label = { Text(stringResource(id = R.string.favorites)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (showFavoritesOnly) "Remove favorites filter" else "Filter favorites",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                // CEFR level filter chips
                WordLevel.entries.forEach { level ->
                    CEFRFilterChip(
                        level = level,
                        selected = level.name in selectedFilters,
                        onClick = {
                            if (level.name in selectedFilters) {
                                selectedFilters.remove(level.name)
                            } else {
                                selectedFilters.add(level.name)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Word List, Empty State, Error State, or Shimmer Loading
            Box(modifier = Modifier.weight(1f)) {
                when {
                    loadError != null -> {
                        // Error state - failed to load words
                        ErrorState(
                            message = loadError ?: stringResource(id = R.string.error_loading_dictionary),
                            onRetry = { wordViewModel.retryLoadWords() }
                        )
                    }
                    isLoadingWords && allWords.isEmpty() -> {
                        // Initial loading state
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = Spacing.sm)
                        ) {
                            items(10) {
                                ShimmerWordCard()
                            }
                        }
                    }
                    isSearching -> {
                        // Shimmer loading state during search
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = Spacing.sm)
                        ) {
                            items(10) {
                                ShimmerWordCard()
                            }
                        }
                    }
                    displayedWords.isEmpty() -> {
                        // Empty State - handle all empty scenarios
                        val hasActiveFilters = searchQuery.isNotEmpty() || selectedFilters.isNotEmpty() || showFavoritesOnly
                        EmptyState(
                            showFavoritesOnly = showFavoritesOnly,
                            hasActiveFilters = hasActiveFilters,
                            onBrowseClick = {
                                showFavoritesOnly = false
                                selectedFilters.clear()
                                searchQuery = ""
                            }
                        )
                    }
                    else -> {
                        // Word List with alphabet fast scroll using Row layout
                        val listState = rememberLazyListState()

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            // Main word list - takes remaining space
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = Spacing.md)
                            ) {
                                itemsIndexed(
                                    items = displayedWords,
                                    key = { _, word -> word.word }
                                ) { index, word ->
                                    // Staggered entrance animation
                                    val scale by animateFloatAsState(
                                        targetValue = 1f,
                                        animationSpec = tween(
                                            durationMillis = AppAnimationDuration.medium,
                                            delayMillis = (index * AnimationDelay.wordCardStagger).coerceAtMost(500)
                                        ),
                                        label = "cardScale"
                                    )

                                    val alpha by animateFloatAsState(
                                        targetValue = 1f,
                                        animationSpec = tween(
                                            durationMillis = AppAnimationDuration.medium,
                                            delayMillis = (index * AnimationDelay.wordCardStagger).coerceAtMost(500)
                                        ),
                                        label = "cardAlpha"
                                    )

                                    DictionaryWordCard(
                                        word = word,
                                        onCardClick = { navController.navigate(Route.wordDetail(word.word)) },
                                        onFavoriteClick = {
                                            wordViewModel.toggleFavorite(word.word, !word.isFavorite)
                                        },
                                        onAudioClick = {
                                            wordViewModel.speakWord(word.word)
                                        },
                                        onPracticeClick = {
                                            // Navigate to quiz with this word
                                            navController.navigate(Route.QUIZ)
                                        },
                                        onShareClick = {
                                            // Share word via Android Intent
                                            val shareText = buildString {
                                                append("📚 ${word.word}\n")
                                                append("📖 ${word.meaning}\n")
                                                word.level?.let { level ->
                                                    append("📊 Level: ${level.name} (${level.longName})\n")
                                                }
                                                append("\n✨ Shared from Trainvoc")
                                            }
                                            val shareIntent = android.content.Intent().apply {
                                                action = android.content.Intent.ACTION_SEND
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Word: ${word.word}")
                                            }
                                            context.startActivity(
                                                android.content.Intent.createChooser(shareIntent, "Share word")
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Spacing.xs)
                                            .scale(scale)
                                            .alpha(alpha)
                                            .animateItem()
                                    )
                                }
                            }

                            // Alphabet Fast Scroll - in Row, aligned vertically
                            if (displayedWords.isNotEmpty() && !isSearching) {
                                AlphabetFastScroll(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    onLetterSelected = { letter ->
                                        // Haptic feedback
                                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                        currentScrollLetter = letter

                                        // Scroll to first word starting with letter
                                        val targetIndex = displayedWords.indexOfFirst {
                                            it.word.firstOrNull()?.uppercaseChar() == letter.firstOrNull()
                                        }
                                        if (targetIndex != -1) {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(targetIndex)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        // Large letter preview when scrolling (overlay on top)
                        if (currentScrollLetter.isNotEmpty()) {
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Responsive size: 15% of min dimension, clamped between 60dp and 100dp
                                val previewSize = (minOf(maxWidth, maxHeight) * 0.15f).coerceIn(60.dp, 100.dp)

                                Surface(
                                    modifier = Modifier.size(previewSize),
                                    shape = RoundedCornerShape(CornerRadius.large),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shadowElevation = Elevation.level3
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = currentScrollLetter,
                                            style = MaterialTheme.typography.displayLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            LaunchedEffect(currentScrollLetter) {
                                delay(500)
                                currentScrollLetter = ""
                            }
                        }
                    }
                }
            }
        }

    }
}

/**
 * CEFR Level Filter Chip with color coding
 */
@Composable
fun CEFRFilterChip(
    level: WordLevel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColor = when (level) {
        WordLevel.A1 -> CEFRColors.A1
        WordLevel.A2 -> CEFRColors.A2
        WordLevel.B1 -> CEFRColors.B1
        WordLevel.B2 -> CEFRColors.B2
        WordLevel.C1 -> CEFRColors.C1
        WordLevel.C2 -> CEFRColors.C2
    }

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = tween(durationMillis = AppAnimationDuration.quick),
        label = "chipScale"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(level.name) },
        modifier = modifier.scale(scale),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = levelColor,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * Dictionary Word Card with all features from UI/UX plan
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DictionaryWordCard(
    word: Word,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAudioClick: () -> Unit,
    onPracticeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showQuickActions by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = { showQuickActions = true }
            ),
        shape = RoundedCornerShape(CornerRadius.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.Top
        ) {
            // Word information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Word with pronunciation button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onAudioClick,
                        modifier = Modifier.size(48.dp)  // Minimum touch target for accessibility
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = stringResource(id = R.string.pronunciation),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Part of speech and level chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // Display part of speech if available
                    word.partOfSpeech?.let { pos ->
                        Text(
                            text = pos,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (word.level != null) {
                        CEFRLevelChip(level = word.level.name)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Definition (short)
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Favorite toggle
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(48.dp)  // Minimum touch target for accessibility
            ) {
                Icon(
                    imageVector = if (word.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (word.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (word.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick actions menu
        DropdownMenu(
            expanded = showQuickActions,
            onDismissRequest = { showQuickActions = false }
        ) {
            val favoriteText = if (word.isFavorite)
                stringResource(id = R.string.remove_from_favorites)
            else
                stringResource(id = R.string.add_to_favorites)
            DropdownMenuItem(
                text = { Text(favoriteText) },
                onClick = {
                    onFavoriteClick()
                    showQuickActions = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (word.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = favoriteText
                    )
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.practice)) },
                onClick = {
                    onPracticeClick()
                    showQuickActions = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(id = R.string.practice_word)
                    )
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.share)) },
                onClick = {
                    onShareClick()
                    showQuickActions = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(id = R.string.share_word)
                    )
                }
            )
        }
    }
}

/**
 * Shimmer loading card for skeleton state
 */
@Composable
fun ShimmerWordCard(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(CornerRadius.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Title shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Subtitle shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        )
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Body shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

/**
 * Empty state for no results, no favorites, or empty database
 */
@Composable
fun EmptyState(
    showFavoritesOnly: Boolean,
    hasActiveFilters: Boolean,
    onBrowseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            showFavoritesOnly -> {
                // No favorites empty state
                InfoCard(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = stringResource(id = R.string.no_favorites_title),
                    message = stringResource(id = R.string.no_favorites_message),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            hasActiveFilters -> {
                // No search/filter results empty state
                InfoCard(
                    icon = Icons.Default.Search,
                    title = stringResource(id = R.string.no_words_found),
                    message = stringResource(id = R.string.try_different_search),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                // Database is empty - no words at all
                InfoCard(
                    icon = Icons.Default.Info,
                    title = stringResource(id = R.string.dictionary_empty_title),
                    message = stringResource(id = R.string.dictionary_empty_message),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Alphabet fast scroll component
 */
@Composable
fun AlphabetFastScroll(
    modifier: Modifier = Modifier,
    onLetterSelected: (String) -> Unit
) {
    val alphabet = ('A'..'Z').map { it.toString() }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    val alphabetNavDesc = stringResource(id = R.string.content_desc_alphabet_nav)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(40.dp)  // Increased from 32.dp for better touch targets
            .semantics {
                contentDescription = alphabetNavDesc
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragOffset = offset
                        // Find letter at position
                        val letterHeight = size.height / alphabet.size
                        val index = (offset.y / letterHeight).toInt().coerceIn(0, alphabet.size - 1)
                        onLetterSelected(alphabet[index])
                    },
                    onDrag = { change, _ ->
                        dragOffset = change.position
                        // Find letter at position
                        val letterHeight = size.height / alphabet.size
                        val index = (dragOffset.y / letterHeight).toInt().coerceIn(0, alphabet.size - 1)
                        onLetterSelected(alphabet[index])
                    },
                    onDragEnd = {
                        isDragging = false
                    }
                )
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabet.forEach { letter ->
            val jumpToLetterDesc = stringResource(id = R.string.content_desc_jump_letter, letter)
            Text(
                text = letter,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .semantics { contentDescription = jumpToLetterDesc }
            )
        }
    }
}

/**
 * Returns color based on CEFR level
 */
@Composable
fun getCEFRColor(level: WordLevel?): Color = when (level) {
    WordLevel.A1 -> CEFRColors.A1
    WordLevel.A2 -> CEFRColors.A2
    WordLevel.B1 -> CEFRColors.B1
    WordLevel.B2 -> CEFRColors.B2
    WordLevel.C1 -> CEFRColors.C1
    WordLevel.C2 -> CEFRColors.C2
    null -> MaterialTheme.colorScheme.surfaceVariant
}
