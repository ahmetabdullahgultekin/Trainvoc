package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.utils.getAdaptiveColumnCount

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Games") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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

            // Games grid - adaptive columns based on screen size
            val columnCount = getAdaptiveColumnCount(
                compact = 2,  // Phone portrait
                medium = 3,   // Tablet or phone landscape
                expanded = 4  // Large tablet
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(GameType.values(), key = { it.route }) { gameType ->
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
private fun GameCard(
    gameType: GameType,
    gamesPlayed: Int,
    bestScore: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
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
                // Icon
                Icon(
                    imageVector = gameType.icon,
                    contentDescription = gameType.displayName,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )

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

/**
 * Game types with metadata
 */
enum class GameType(
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val route: String
) {
    MULTIPLE_CHOICE(
        displayName = "Multiple Choice",
        description = "Choose the correct translation",
        icon = Icons.Default.QuestionAnswer,
        gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
        route = "game/multiple_choice"
    ),
    FLIP_CARDS(
        displayName = "Flip Cards",
        description = "Match pairs of words",
        icon = Icons.Default.Refresh,
        gradientColors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E)),
        route = "game/flip_cards"
    ),
    SPEED_MATCH(
        displayName = "Speed Match",
        description = "Fast-paced matching",
        icon = Icons.Default.Timer,
        gradientColors = listOf(Color(0xFFEAB308), Color(0xFFF59E0B)),
        route = "game/speed_match"
    ),
    FILL_IN_BLANK(
        displayName = "Fill in Blank",
        description = "Complete the sentence",
        icon = Icons.Default.Edit,
        gradientColors = listOf(Color(0xFF10B981), Color(0xFF14B8A6)),
        route = "game/fill_blank"
    ),
    WORD_SCRAMBLE(
        displayName = "Word Scramble",
        description = "Unscramble the letters",
        icon = Icons.Default.Shuffle,
        gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
        route = "game/word_scramble"
    ),
    LISTENING_QUIZ(
        displayName = "Listening Quiz",
        description = "Listen and select",
        icon = Icons.Default.Headphones,
        gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
        route = "game/listening_quiz"
    ),
    PICTURE_MATCH(
        displayName = "Picture Match",
        description = "Match words to images",
        icon = Icons.Default.Image,
        gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFEAB308)),
        route = "game/picture_match"
    ),
    SPELLING_CHALLENGE(
        displayName = "Spelling",
        description = "Type the correct spelling",
        icon = Icons.Default.Spellcheck,
        gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
        route = "game/spelling"
    ),
    TRANSLATION_RACE(
        displayName = "Translation Race",
        description = "Race against time",
        icon = Icons.Default.Speed,
        gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFDC2626)),
        route = "game/translation_race"
    ),
    CONTEXT_CLUES(
        displayName = "Context Clues",
        description = "Learn from context",
        icon = Icons.Default.MenuBook,
        gradientColors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488)),
        route = "game/context_clues"
    )
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
