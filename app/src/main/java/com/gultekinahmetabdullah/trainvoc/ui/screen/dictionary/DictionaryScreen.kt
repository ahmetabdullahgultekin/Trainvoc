package com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel

/**
 * Enhanced Dictionary Screen with animations and visual polish
 *
 * Features:
 * - Animated card entrance with fade + scale
 * - Enhanced search field with clear button
 * - Empty state when no results
 * - Loading indicator during search
 * - Visual level indicators with color coding
 * - Elevated cards with proper spacing
 * - Smooth content size animations
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DictionaryScreen(navController: NavController, wordViewModel: WordViewModel) {
    var search by remember { mutableStateOf("") }
    val filteredWords by wordViewModel.filteredWords.collectAsState()
    var isSearching by remember { mutableStateOf(false) }

    // Debounced search: Wait 300ms after user stops typing before filtering
    // This improves UX by reducing unnecessary database queries
    LaunchedEffect(search) {
        if (search.isNotEmpty()) {
            isSearching = true
        }
        kotlinx.coroutines.delay(300)  // 300ms debounce delay
        wordViewModel.filterWords(search)
        isSearching = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.mediumLarge)
    ) {
        // Enhanced Search Field
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text(stringResource(id = R.string.search_word)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search_word),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = search.isNotEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    IconButton(onClick = { search = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.medium),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        // Loading Indicator
        AnimatedVisibility(
            visible = isSearching,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.small),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(IconSize.medium),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = stringResource(id = R.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Word List or Empty State
        Box(modifier = Modifier.weight(1f)) {
            val listState = rememberLazyListState()

            if (!isSearching && filteredWords.isEmpty() && search.isNotEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔍",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Text(
                        text = stringResource(id = R.string.no_words_found),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    Text(
                        text = stringResource(id = R.string.try_different_search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredWords,
                        key = { it.word }
                    ) { word ->
                        WordCard(
                            word = word.word,
                            meaning = word.meaning,
                            level = word.level?.ordinal ?: 0,
                            onClick = { navController.navigate(Route.wordDetail(word.word)) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Enhanced Word Card with visual polish
 *
 * Features:
 * - Elevated card design
 * - Level indicator with color coding
 * - Animated content size changes
 * - Improved typography hierarchy
 */
@Composable
fun WordCard(
    word: String,
    meaning: String,
    level: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.extraSmall)
            .animateContentSize()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.low,
            pressedElevation = Elevation.medium
        ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Indicator Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getLevelColor(level)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(Spacing.medium))

            // Word and Meaning Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.extraSmall))
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Returns color based on word difficulty level
 * A1-A2: Green (Easy)
 * B1-B2: Blue (Intermediate)
 * C1-C2: Purple (Advanced)
 */
@Composable
fun getLevelColor(level: Int) = when (level) {
    in 0..2 -> MaterialTheme.colorScheme.primary.copy(alpha = Alpha.high)      // A1-A2
    in 3..4 -> MaterialTheme.colorScheme.tertiary.copy(alpha = Alpha.high)     // B1-B2
    in 5..6 -> MaterialTheme.colorScheme.secondary.copy(alpha = Alpha.high)    // C1-C2
    else -> MaterialTheme.colorScheme.surfaceVariant
}
