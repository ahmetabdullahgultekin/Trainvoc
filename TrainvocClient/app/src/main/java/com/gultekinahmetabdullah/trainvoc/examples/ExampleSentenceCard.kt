package com.gultekinahmetabdullah.trainvoc.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.ui.components.InlineLoader
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize
import kotlinx.coroutines.launch

/**
 * Example sentence card component
 * Displays example sentences with translations and context
 */
@Composable
fun ExampleSentencesList(
    wordId: String,
    exampleSentenceDao: ExampleSentenceDao,
    featureFlags: FeatureFlagManager,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var examples by remember { mutableStateOf<List<ExampleSentence>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val examplesEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.EXAMPLE_SENTENCES)

    if (!examplesEnabled) return

    // Load examples
    LaunchedEffect(wordId) {
        isLoading = true
        scope.launch {
            examples = exampleSentenceDao.getExamplesForWord(wordId)
            isLoading = false
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Example Sentences",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when {
            isLoading -> {
                InlineLoader(
                    size = LoaderSize.small,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
            examples.isEmpty() -> {
                Text(
                    text = "No example sentences available yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(examples) { example ->
                        ExampleSentenceCard(
                            example = example,
                            onFavoriteClick = {
                                scope.launch {
                                    exampleSentenceDao.setFavorite(
                                        example.id,
                                        !example.isFavorite
                                    )
                                    // Refresh list
                                    examples = exampleSentenceDao.getExamplesForWord(wordId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleSentenceCard(
    example: ExampleSentence,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Context and difficulty badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Context badge
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = example.context.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = getContextChipColors(example.context),
                    modifier = Modifier.height(24.dp)
                )

                // Difficulty badge
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = example.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = getDifficultyChipColors(example.difficulty),
                    modifier = Modifier.height(24.dp)
                )
            }

            // Sentence
            Text(
                text = example.sentence,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Translation
            Text(
                text = example.translation,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Favorite button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (example.isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = "Favorite",
                        tint = if (example.isFavorite) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun getContextChipColors(context: UsageContext): ChipColors {
    return when (context) {
        UsageContext.FORMAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        UsageContext.INFORMAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        UsageContext.SLANG -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
        UsageContext.TECHNICAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
        else -> AssistChipDefaults.assistChipColors()
    }
}

@Composable
private fun getDifficultyChipColors(difficulty: ExampleDifficulty): ChipColors {
    return when (difficulty) {
        ExampleDifficulty.BEGINNER -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        ExampleDifficulty.INTERMEDIATE -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        ExampleDifficulty.ADVANCED -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }
}
