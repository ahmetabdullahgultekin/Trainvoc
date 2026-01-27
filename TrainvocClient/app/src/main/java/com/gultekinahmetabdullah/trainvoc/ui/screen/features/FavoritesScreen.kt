package com.gultekinahmetabdullah.trainvoc.ui.screen.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.ui.components.RollingCatLoaderWithText
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize
import com.gultekinahmetabdullah.trainvoc.ui.components.SwipeToDeleteCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.FavoritesViewModel

/**
 * Favorites Screen
 *
 * Shows user's favorited words with search and filter capabilities.
 * Features:
 * - List of favorited words
 * - Search and filter
 * - Practice favorites
 * - Remove from favorites
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit = {},
    onPracticeFavorites: () -> Unit = {},
    onWordClick: (String) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val favoriteWords by viewModel.favoriteWords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.favorites_count, favoriteWords.size))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.back))
                    }
                },
                actions = {
                    if (favoriteWords.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, stringResource(id = R.string.content_desc_clear_all))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (favoriteWords.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onPracticeFavorites,
                    icon = { Icon(Icons.Default.PlayArrow, stringResource(id = R.string.content_desc_practice)) },
                    text = { Text(stringResource(id = R.string.practice_all)) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                placeholder = { Text(stringResource(id = R.string.search_favorites)) },
                leadingIcon = { Icon(Icons.Default.Search, stringResource(id = R.string.content_desc_search)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, stringResource(id = R.string.content_desc_clear))
                        }
                    }
                },
                singleLine = true
            )

            when {
                isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        RollingCatLoaderWithText(
                            message = stringResource(id = R.string.loading_favorites),
                            size = LoaderSize.medium
                        )
                    }
                }
                favoriteWords.isEmpty() -> {
                    // Empty state
                    EmptyFavoritesState(
                        hasSearchQuery = searchQuery.isNotEmpty()
                    )
                }
                else -> {
                    // Favorites list with swipe-to-delete
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp) // Space for FAB
                    ) {
                        items(
                            items = favoriteWords,
                            key = { it.word }
                        ) { word ->
                            SwipeToDeleteCard(
                                onDelete = { viewModel.removeFromFavorites(word.word) },
                                modifier = Modifier.animateItem()
                            ) {
                                FavoriteWordCard(
                                    word = word,
                                    onClick = { onWordClick(word.word) },
                                    onRemove = { viewModel.removeFromFavorites(word.word) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Clear all confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(id = R.string.clear_all_favorites)) },
            text = {
                Text(stringResource(id = R.string.clear_favorites_message, favoriteWords.size))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showClearDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.clear_all), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun EmptyFavoritesState(hasSearchQuery: Boolean) {
    val noMatchingFavoritesText = stringResource(id = R.string.no_matching_favorites)
    val noFavoritesYetText = stringResource(id = R.string.no_favorites_yet)
    val tryDifferentSearchText = stringResource(id = R.string.try_different_search)
    val tapHeartText = stringResource(id = R.string.tap_heart_to_add)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (hasSearchQuery) Icons.Default.SearchOff else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(id = R.string.content_desc_no_favorites),
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (hasSearchQuery) noMatchingFavoritesText else noFavoritesYetText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (hasSearchQuery)
                    tryDifferentSearchText
                else
                    tapHeartText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteWordCard(
    word: Word,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                word.level?.let { level ->
                    Spacer(modifier = Modifier.height(4.dp))
                    SuggestionChip(
                        onClick = { },
                        label = { Text(level.name) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Favorite,
                    stringResource(id = R.string.content_desc_remove_favorite),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
