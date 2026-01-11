package com.gultekinahmetabdullah.trainvoc.ui.screen.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordOfDayViewModel

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
                title = { Text("Word of the Day") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
                    CircularProgressIndicator()
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
                    onPractice = { onPractice(wordOfDay!!.word) }
                )
            }
            else -> {
                ErrorState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = "No word available",
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
    onPractice: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Word Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pronunciation button
                OutlinedButton(
                    onClick = { /* TODO: Play TTS audio */ }
                ) {
                    Icon(Icons.Default.VolumeUp, "Listen", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Listen")
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Meaning
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Level badge
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Level: $level",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onToggleFavorite,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    "Toggle Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isFavorite) "Favorited" else "Favorite")
            }

            Button(
                onClick = onPractice,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, "Practice")
                Spacer(Modifier.width(8.dp))
                Text("Practice")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    "Info",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "A new word is featured every day. Come back tomorrow for a new word!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
                contentDescription = "Error",
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
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}
