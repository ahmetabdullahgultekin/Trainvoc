package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gultekinahmetabdullah.trainvoc.ui.animations.AnimationSpecs
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary.WordManagementScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.components.AppBottomBar
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.components.AppBottomSheet
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.components.AppTopBar
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.AboutScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.HelpScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.SettingsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.StatsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizExamMenuScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.settings.NotificationSettingsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizMenuScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    quizViewModel: QuizViewModel,
    wordViewModel: WordViewModel,
    statsViewModel: StatsViewModel,
    settingsViewModel: SettingsViewModel,
    storyViewModel: StoryViewModel,
    startWordId: String? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    val showBottomSheet = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val isTopAppBarVisible = remember { mutableStateOf(true) }
    val isBottomBarVisible = remember { mutableStateOf(true) } // Always show bottom bar

    val parameter = remember { mutableStateOf<QuizParameter?>(null) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (isBottomBarVisible.value) {
                AppBottomBar(navController)
            }
        },
        topBar = {
            if (isTopAppBarVisible.value) {
                AppTopBar(
                    navBackStackEntry = navBackStackEntry.value,
                    navController = navController,
                    onMenuClick = { showBottomSheet.value = true }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Route.HOME,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { AnimationSpecs.slideInFromRight() },
                exitTransition = { AnimationSpecs.slideOutToLeft() },
                popEnterTransition = { AnimationSpecs.slideInFromLeft() },
                popExitTransition = { AnimationSpecs.slideOutToRight() }
            ) {
                composable(Route.HOME) {
                    HomeScreen(
                        onNavigateToHelp = { navController.navigate(Route.HELP) },
                        onNavigateToStory = { navController.navigate(Route.STORY) },
                        onNavigateToSettings = { navController.navigate(Route.SETTINGS) },
                        onNavigateToStats = { navController.navigate(Route.STATS) },
                        onNavigateToQuiz = { navController.navigate(Route.QUIZ_EXAM_MENU) },
                        onNavigateToGames = { navController.navigate(Route.GAMES_MENU) },
                        // Phase 1 - New navigation callbacks
                        onNavigateToProfile = { navController.navigate(Route.PROFILE) },
                        onNavigateToWordOfDay = { navController.navigate(Route.WORD_OF_DAY) },
                        onNavigateToFavorites = { navController.navigate(Route.FAVORITES) },
                        onNavigateToLastQuiz = { navController.navigate(Route.LAST_QUIZ_RESULTS) },
                        // Phase 2 & 3 - Gamification & Engagement
                        onNavigateToDailyGoals = { navController.navigate(Route.DAILY_GOALS) },
                        onNavigateToAchievements = { navController.navigate(Route.ACHIEVEMENTS) },
                        onNavigateToStreakDetail = { navController.navigate(Route.STREAK_DETAIL) },
                        onNavigateToLeaderboard = { navController.navigate(Route.LEADERBOARD) },
                        onNavigateToWordProgress = { navController.navigate(Route.WORD_PROGRESS) },
                        onNavigateToDictionary = { navController.navigate(Route.DICTIONARY) },
                    )
                }
                composable(Route.STORY) {
                    StoryScreen(
                        viewModel = storyViewModel,
                        onLevelSelected = { level ->
                            parameter.value = QuizParameter.Level(level)

                            parameter.value?.let {
                                quizViewModel.startQuiz(
                                    it,
                                    Quiz.quizTypes[0]
                                ) // Default to first quiz type
                            }
                            navController.navigate(Route.QUIZ)
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Route.QUIZ_EXAM_MENU) {
                    QuizExamMenuScreen(
                        onExamSelected = { quizParameter ->
                            navController.navigate(Route.QUIZ_MENU)
                            parameter.value = quizParameter
                        }
                    )
                }
                composable(Route.QUIZ_MENU) {
                    QuizMenuScreen(
                        onQuizSelected = { quiz ->
                            parameter.value?.let {
                                quizViewModel.startQuiz(it, quiz)
                                navController.navigate(Route.QUIZ)
                            }
                        }
                    )
                }
                composable(Route.QUIZ) {
                    QuizScreen(
                        quizViewModel = quizViewModel,
                        onQuit = { navController.navigate(Route.HOME) },
                    )
                }
                composable(Route.MANAGEMENT) {
                    WordManagementScreen(wordViewModel = wordViewModel)
                }
                composable(Route.USERNAME) {
                    UsernameScreen(navController)
                }
                composable(Route.SETTINGS) {
                    SettingsScreen(navController, settingsViewModel)
                }
                composable(Route.BACKUP) {
                    com.gultekinahmetabdullah.trainvoc.ui.backup.BackupScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Route.NOTIFICATION_SETTINGS) {
                    NotificationSettingsScreen(navController = navController)
                }
                composable(Route.ACCESSIBILITY_SETTINGS) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.other.AccessibilitySettingsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Route.HELP) {
                    HelpScreen()
                }
                composable(Route.ABOUT) {
                    AboutScreen()
                }
                composable(Route.STATS) {
                    StatsScreen(statsViewModel = statsViewModel)
                }
                composable(Route.DICTIONARY) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary.DictionaryScreen(
                        navController = navController,
                        wordViewModel = wordViewModel
                    )
                }
                composable(
                    route = Route.WORD_DETAIL,
                    arguments = listOf(navArgument("wordId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val wordId = backStackEntry.arguments?.getString("wordId") ?: ""
                    com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary.WordDetailScreen(
                        wordId = wordId,
                        wordViewModel = wordViewModel
                    )
                }

                // Phase 1 - New Screens
                composable(Route.PROFILE) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.profile.ProfileScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Route.WORD_OF_DAY) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.features.WordOfTheDayScreen(
                        onBackClick = { navController.popBackStack() },
                        onPractice = { navController.navigate(Route.QUIZ) }
                    )
                }
                composable(Route.FAVORITES) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.features.FavoritesScreen(
                        onBackClick = { navController.popBackStack() },
                        onPracticeFavorites = { navController.navigate(Route.QUIZ) }
                    )
                }
                composable(Route.LAST_QUIZ_RESULTS) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.LastQuizResultsScreen(
                        onBackClick = { navController.popBackStack() },
                        onRetryQuiz = { navController.navigate(Route.QUIZ) },
                        onReviewMissed = { /* TODO: Navigate to review mode */ }
                    )
                }

                // Phase 2 - Additional Screens (TODO: Implement these screens)
                // composable(Route.DAILY_GOALS) { ... }
                // composable(Route.ACHIEVEMENTS) { ... }
                // composable(Route.STREAK_DETAIL) { ... }

                // Phase 3 - Engagement Features (TODO: Implement these screens)
                // composable(Route.LEADERBOARD) { ... }
                // composable(Route.WORD_PROGRESS) { ... }

                // Memory Games Navigation (TODO: Implement games)
                // gamesNavGraph(navController)
            }
        }
    }

    if (showBottomSheet.value) {
        AppBottomSheet(
            sheetState = sheetState,
            coroutineScope = coroutineScope,
            showBottomSheet = showBottomSheet,
            navController = navController
        )
    }

    LaunchedEffect(startWordId) {
        if (!startWordId.isNullOrEmpty()) {
            navController.navigate(Route.wordDetail(startWordId))
        }
    }
}
