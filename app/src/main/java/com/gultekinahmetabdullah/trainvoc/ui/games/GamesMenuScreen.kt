package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameCategory
import com.gultekinahmetabdullah.trainvoc.classes.enums.GameType

/**
 * Games Menu Screen
 *
 * Displays all available memory games in a grid layout.
 * Shows game stats, best scores, and allows launching games.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesMenuScreen(
    onNavigateBack: () -> Unit,
    onGameSelected: (GameType) -> Unit,
    viewModel: GamesMenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf(GameCategory.ALL) }

    // Filter games by category
    val filteredGames = remember(selectedCategory) {
        if (selectedCategory == GameCategory.ALL) {
            GameType.entries.toList()
        } else {
            GameType.entries.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Games") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats summary card
            StatsCard(
                totalGamesPlayed = uiState.totalGamesPlayed,
                bestAccuracy = uiState.bestAccuracy,
                favoriteGame = uiState.favoriteGame
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category filter chips
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Games grid - Fixed 2 columns
            if (filteredGames.isEmpty()) {
                EmptyGamesState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    itemsIndexed(
                        items = filteredGames,
                        key = { _, gameType -> gameType.route }
                    ) { _, gameType ->
                        GameCard(
                            gameType = gameType,
                            gamesPlayed = uiState.getGamesPlayed(gameType),
                            bestScore = uiState.getBestScore(gameType),
                            onClick = { onGameSelected(gameType) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    totalGamesPlayed: Int,
    bestAccuracy: Float,
    favoriteGame: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Games,
                label = "Games Played",
                value = totalGamesPlayed.toString()
            )
            StatItem(
                icon = Icons.Default.Star,
                label = "Best Accuracy",
                value = "${bestAccuracy.toInt()}%"
            )
            StatItem(
                icon = Icons.Default.Favorite,
                label = "Favorite",
                value = favoriteGame ?: "None"
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryFilterChips(
    selectedCategory: GameCategory,
    onCategorySelected: (GameCategory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(GameCategory.entries.size) { index ->
            val category = GameCategory.entries[index]
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) }
            )
        }
    }
}

@Composable
private fun GameCard(
    gameType: GameType,
    gamesPlayed: Int,
    bestScore: Int,
    onClick: () -> Unit
) {
    val cardDescription = "${gameType.displayName}. ${gameType.description}. " +
        "$gamesPlayed games played" +
        if (bestScore > 0) ". Best score: $bestScore" else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = cardDescription
            }
            .clickable(onClick = onClick, onClickLabel = "Play ${gameType.displayName}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gameType.gradientColors
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon and Difficulty Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = gameType.icon,
                        contentDescription = gameType.displayName,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )

                    // Difficulty Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = gameType.difficulty.color.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = gameType.difficulty.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Game info
                Column {
                    Text(
                        text = gameType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gameType.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$gamesPlayed played",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        if (bestScore > 0) {
                            Text(
                                text = "Best: $bestScore",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGamesState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No games in this category",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * UI State for Games Menu
 */
data class GamesMenuUiState(
    val totalGamesPlayed: Int = 0,
    val bestAccuracy: Float = 0f,
    val favoriteGame: String? = null,
    val gameStats: Map<GameType, GameStats> = emptyMap()
) {
    fun getGamesPlayed(gameType: GameType): Int =
        gameStats[gameType]?.gamesPlayed ?: 0

    fun getBestScore(gameType: GameType): Int =
        gameStats[gameType]?.bestScore ?: 0
}

data class GameStats(
    val gamesPlayed: Int = 0,
    val bestScore: Int = 0,
    val bestAccuracy: Float = 0f,
    val totalCorrect: Int = 0,
    val totalQuestions: Int = 0
)
