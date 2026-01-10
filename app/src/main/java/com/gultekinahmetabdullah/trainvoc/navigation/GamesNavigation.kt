package com.gultekinahmetabdullah.trainvoc.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gultekinahmetabdullah.trainvoc.ui.games.*

/**
 * Games Navigation Setup
 *
 * Add this to your existing NavHost in MainActivity or your main navigation setup:
 *
 * ```kotlin
 * NavHost(navController = navController, startDestination = "home") {
 *     // ... your existing routes ...
 *
 *     // Add games navigation
 *     gamesNavGraph(navController)
 * }
 * ```
 */

fun NavGraphBuilder.gamesNavGraph(navController: NavHostController) {

    // Games Menu
    composable(GamesRoutes.GAMES_MENU) {
        GamesMenuScreen(
            onNavigateBack = { navController.popBackStack() },
            onGameSelected = { gameType ->
                navController.navigate(gameType.route)
            }
        )
    }

    // 1. Multiple Choice Game
    composable(GamesRoutes.MULTIPLE_CHOICE) {
        MultipleChoiceGameScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 2. Fill in the Blank Game
    composable(GamesRoutes.FILL_IN_BLANK) {
        FillInTheBlankScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 3. Word Scramble Game
    composable(GamesRoutes.WORD_SCRAMBLE) {
        WordScrambleScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 4. Flip Cards Game
    composable(GamesRoutes.FLIP_CARDS) {
        FlipCardsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 5. Speed Match Game
    composable(GamesRoutes.SPEED_MATCH) {
        SpeedMatchScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 6. Listening Quiz Game
    composable(GamesRoutes.LISTENING_QUIZ) {
        ListeningQuizScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 7. Picture Match Game
    composable(GamesRoutes.PICTURE_MATCH) {
        PictureMatchScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 8. Spelling Challenge Game
    composable(GamesRoutes.SPELLING_CHALLENGE) {
        SpellingChallengeScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 9. Translation Race Game
    composable(GamesRoutes.TRANSLATION_RACE) {
        TranslationRaceScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // 10. Context Clues Game
    composable(GamesRoutes.CONTEXT_CLUES) {
        ContextCluesScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

/**
 * Games Routes Constants
 */
object GamesRoutes {
    const val GAMES_MENU = "games_menu"
    const val MULTIPLE_CHOICE = "game/multiple_choice"
    const val FILL_IN_BLANK = "game/fill_blank"
    const val WORD_SCRAMBLE = "game/word_scramble"
    const val FLIP_CARDS = "game/flip_cards"
    const val SPEED_MATCH = "game/speed_match"
    const val LISTENING_QUIZ = "game/listening_quiz"
    const val PICTURE_MATCH = "game/picture_match"
    const val SPELLING_CHALLENGE = "game/spelling"
    const val TRANSLATION_RACE = "game/translation_race"
    const val CONTEXT_CLUES = "game/context_clues"
}

/**
 * Navigation Helper Extension Functions
 */
fun NavHostController.navigateToGamesMenu() {
    navigate(GamesRoutes.GAMES_MENU)
}

fun NavHostController.navigateToGame(gameRoute: String) {
    navigate(gameRoute)
}
