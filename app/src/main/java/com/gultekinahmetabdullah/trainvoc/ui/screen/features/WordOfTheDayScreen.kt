package com.gultekinahmetabdullah.trainvoc.ui.screen.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

/**
 * Word of the Day Screen
 *
 * Shows a featured word each day with:
 * - Word and meaning
 * - Example sentences
 * - Audio pronunciation (TTS)
 * - Add to favorites option
 * - Practice quiz option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordOfTheDayScreen(
    onBackClick: () -> Unit = {},
    onPractice: () -> Unit = {}
) {
    // TODO: Fetch actual word of the day from database
    // For now, showing placeholder content
    val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word of the Day") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    // TODO: Replace with actual word from database
                    Text(
                        text = "Serendipity",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Meaning
                    Text(
                        text = "The occurrence of events by chance in a happy or beneficial way",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Level badge
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Level: C1",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Example Sentences
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Examples",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // TODO: Load actual examples from database
                    ExampleItem("The best discoveries are made by serendipity.")
                    ExampleItem("It was pure serendipity that we met at the coffee shop.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Toggle favorite */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FavoriteBorder, "Add to Favorites")
                    Spacer(Modifier.width(8.dp))
                    Text("Favorite")
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
}

@Composable
private fun ExampleItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
