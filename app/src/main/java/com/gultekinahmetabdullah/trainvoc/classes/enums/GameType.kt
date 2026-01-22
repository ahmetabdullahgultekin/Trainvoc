package com.gultekinahmetabdullah.trainvoc.classes.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Unified GameType enum - Single source of truth for all game types
 *
 * Contains both identification (for ViewModels) and UI properties (for screens).
 * This enum is used by:
 * - TutorialViewModel for tracking first-play status
 * - Game screens for UI rendering (icons, colors, descriptions)
 * - Navigation for routing to specific games
 */
enum class GameType(
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val route: String,
    val difficulty: GameDifficulty,
    val category: GameCategory
) {
    MULTIPLE_CHOICE(
        displayName = "Multiple Choice",
        description = "Choose the correct translation",
        icon = Icons.Default.QuestionAnswer,
        gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
        route = "game/multiple_choice",
        difficulty = GameDifficulty.EASY,
        category = GameCategory.VOCABULARY
    ),
    FLIP_CARDS(
        displayName = "Flip Cards",
        description = "Match pairs of words",
        icon = Icons.Default.Refresh,
        gradientColors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E)),
        route = "game/flip_cards",
        difficulty = GameDifficulty.EASY,
        category = GameCategory.MEMORY
    ),
    SPEED_MATCH(
        displayName = "Speed Match",
        description = "Fast-paced matching",
        icon = Icons.Default.Timer,
        gradientColors = listOf(Color(0xFFEAB308), Color(0xFFF59E0B)),
        route = "game/speed_match",
        difficulty = GameDifficulty.HARD,
        category = GameCategory.SPEED
    ),
    FILL_IN_BLANK(
        displayName = "Fill in Blank",
        description = "Complete the sentence",
        icon = Icons.Default.Edit,
        gradientColors = listOf(Color(0xFF10B981), Color(0xFF14B8A6)),
        route = "game/fill_blank",
        difficulty = GameDifficulty.MEDIUM,
        category = GameCategory.VOCABULARY
    ),
    WORD_SCRAMBLE(
        displayName = "Word Scramble",
        description = "Unscramble the letters",
        icon = Icons.Default.Shuffle,
        gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
        route = "game/word_scramble",
        difficulty = GameDifficulty.MEDIUM,
        category = GameCategory.VOCABULARY
    ),
    LISTENING_QUIZ(
        displayName = "Listening Quiz",
        description = "Listen and select",
        icon = Icons.Default.Headphones,
        gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
        route = "game/listening_quiz",
        difficulty = GameDifficulty.MEDIUM,
        category = GameCategory.LISTENING
    ),
    PICTURE_MATCH(
        displayName = "Picture Match",
        description = "Match words to images",
        icon = Icons.Default.Image,
        gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFEAB308)),
        route = "game/picture_match",
        difficulty = GameDifficulty.EASY,
        category = GameCategory.MEMORY
    ),
    SPELLING_CHALLENGE(
        displayName = "Spelling",
        description = "Type the correct spelling",
        icon = Icons.Default.Spellcheck,
        gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
        route = "game/spelling",
        difficulty = GameDifficulty.HARD,
        category = GameCategory.VOCABULARY
    ),
    TRANSLATION_RACE(
        displayName = "Translation Race",
        description = "Race against time",
        icon = Icons.Default.Speed,
        gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFDC2626)),
        route = "game/translation_race",
        difficulty = GameDifficulty.HARD,
        category = GameCategory.SPEED
    ),
    CONTEXT_CLUES(
        displayName = "Context Clues",
        description = "Learn from context",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        gradientColors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488)),
        route = "game/context_clues",
        difficulty = GameDifficulty.MEDIUM,
        category = GameCategory.VOCABULARY
    )
}

/**
 * Game difficulty levels
 */
enum class GameDifficulty(
    val displayName: String,
    val color: Color
) {
    EASY("Easy", Color(0xFF22C55E)),
    MEDIUM("Medium", Color(0xFFF59E0B)),
    HARD("Hard", Color(0xFFEF4444))
}

/**
 * Game categories for filtering
 */
enum class GameCategory(val displayName: String) {
    ALL("All Games"),
    VOCABULARY("Vocabulary"),
    MEMORY("Memory"),
    LISTENING("Listening"),
    SPEED("Speed")
}
