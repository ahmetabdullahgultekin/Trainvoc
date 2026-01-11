package com.gultekinahmetabdullah.trainvoc.ui.screen.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Favorites Screen
 *
 * Shows user's favorited words
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
    onPracticeFavorites: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onPracticeFavorites,
                icon = { Icon(Icons.Default.PlayArrow, "Practice") },
                text = { Text("Practice All") }
            )
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
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                placeholder = { Text("Search favorites...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true
            )

            // TODO: Load actual favorites from database
            // For now, showing empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "No favorites",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "No favorites yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Tap the heart icon on any word to add it to your favorites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // TODO: Replace empty state with actual favorites list
            // LazyColumn {
            //     items(favorites) { word ->
            //         FavoriteWordCard(word, onRemove = { ... })
            //     }
            // }
        }
    }
}

// TODO: Implement when favorites database functionality is ready
//@Composable
//private fun FavoriteWordCard(
//    word: Word,
//    onRemove: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = Spacing.medium, vertical = Spacing.small)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = word.word,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = word.meaning,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//            IconButton(onClick = onRemove) {
//                Icon(
//                    Icons.Default.Favorite,
//                    "Remove from favorites",
//                    tint = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}
