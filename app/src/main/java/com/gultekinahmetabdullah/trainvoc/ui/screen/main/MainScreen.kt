package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import QuizMenuScreen
import StoryScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.AboutScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.HelpScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.SettingsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.StatsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    val isBottomBarVisible = remember { mutableStateOf(false) }

    val parameter = remember { mutableStateOf<QuizParameter?>(null) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (isBottomBarVisible.value) {
                BottomNavigationBar(navController)
            }
        },
        topBar = {
            if (isTopAppBarVisible.value) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(
                                    id = R.drawable.baseline_generating_tokens_24
                                ),
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                    MaterialTheme.colorScheme.primary
                                ),
                                contentDescription = stringResource(id = R.string.app_name) + " logo",
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(end = 10.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    letterSpacing = 2.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface, // updated to new palette
                        scrolledContainerColor = MaterialTheme.colorScheme.surface, // updated
                        navigationIconContentColor = MaterialTheme.colorScheme.primary, // updated
                        titleContentColor = MaterialTheme.colorScheme.primary, // updated
                        actionIconContentColor = MaterialTheme.colorScheme.secondary // updated for accent
                    ),
                    navigationIcon = {
                        if (navBackStackEntry.value?.destination?.route == Route.HOME) {
                            return@TopAppBar
                        }
                        if (
                            navBackStackEntry.value?.destination?.route == Route.QUIZ
                            || navBackStackEntry.value?.destination?.route == Route.QUIZ_MENU
                            || navBackStackEntry.value?.destination?.route == Route.QUIZ_EXAM_MENU
                        ) {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                navController.navigate(Route.HOME)
                            }) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Home",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                showBottomSheet.value = true
                            }
                        }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.HOME,
            modifier = Modifier.padding(paddingValues)
        )
        {
            composable(Route.HOME) {
                HomeScreen(
                    onNavigateToHelp = { navController.navigate(Route.HELP) },
                    onNavigateToStory = { navController.navigate(Route.STORY) },
                    onNavigateToSettings = { navController.navigate(Route.SETTINGS) },
                    onNavigateToStats = { navController.navigate(Route.STATS) },
                    onNavigateToQuiz = { navController.navigate(Route.QUIZ_EXAM_MENU) },
                )
            }
            composable(Route.STORY) {
                StoryScreen(
                    viewModel = storyViewModel,
                    onLevelSelected = { level ->
                        navController.navigate(Route.QUIZ_EXAM_MENU)
                        parameter.value = QuizParameter.Level(level)
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
                QuizScreen(quizViewModel = quizViewModel)
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
        }
    }

    if (showBottomSheet.value) {
        CustomBottomSheet(sheetState, coroutineScope, showBottomSheet, navController)
    }

    LaunchedEffect(startWordId) {
        if (!startWordId.isNullOrEmpty()) {
            navController.navigate(Route.wordDetail(startWordId))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home)
                )
            },
            label = { Text(stringResource(id = R.string.home)) },
            selected = navController.currentDestination?.route == Route.HOME,
            onClick = { navController.navigate(Route.HOME) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Build,
                    contentDescription = stringResource(id = R.string.quiz)
                )
            },
            label = { Text(stringResource(id = R.string.quiz)) },
            selected = navController.currentDestination?.route == Route.QUIZ,
            onClick = { navController.navigate(Route.QUIZ) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = stringResource(id = R.string.stats)
                )
            },
            label = { Text(stringResource(id = R.string.stats)) },
            selected = navController.currentDestination?.route == Route.STATS,
            onClick = { navController.navigate(Route.STATS) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    showBottomSheet: MutableState<Boolean>,
    navController: NavController
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                // Başlık ve kapatma butonu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.more_options),
                        style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Menü seçenekleri listesi
                val menuItems = listOf(
                    Triple(stringResource(id = R.string.home), Icons.Default.Home, Route.HOME),
                    Triple(
                        stringResource(id = R.string.story_mode),
                        Icons.Default.Star,
                        Route.STORY
                    ),
                    Triple(
                        stringResource(id = R.string.stats),
                        Icons.Default.CheckCircle,
                        Route.STATS
                    ),
                    Triple(
                        stringResource(id = R.string.search_word),
                        Icons.Default.Search,
                        Route.DICTIONARY
                    ),
                    Triple(
                        stringResource(id = R.string.manage_words),
                        Icons.Default.Build,
                        Route.MANAGEMENT
                    ),
                    Triple(
                        stringResource(id = R.string.settings),
                        Icons.Default.Settings,
                        Route.SETTINGS
                    ),
                    Triple(stringResource(id = R.string.help), Icons.Default.Info, Route.HELP),
                    Triple(
                        stringResource(id = R.string.about),
                        Icons.Default.AccountCircle,
                        Route.ABOUT
                    ),
                )

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(menuItems.size) { index ->
                        val (text, icon, route) = menuItems[index]
                        androidx.compose.material3.ListItem(
                            headlineContent = {
                                Text(text, style = MaterialTheme.typography.bodyLarge)
                            },
                            leadingContent = {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet.value = false
                                            navController.navigate(route)
                                        }
                                    }
                                }
                                .padding(vertical = 2.dp, horizontal = 2.dp)
                        )
                        if (index < menuItems.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                thickness = 1.dp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            ambientColor = MaterialTheme.colorScheme.error,
                            spotColor = MaterialTheme.colorScheme.error,
                            clip = false
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = Color.White,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.close), color = Color.White
                    )
                }
            }
        },
        onDismissRequest = {
            coroutineScope.launch {
                showBottomSheet.value = false
            }
        }
    )
}

// Yeni BottomSheetButton tanımı
data class BottomSheetButtonData(val text: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun BottomSheetButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = text, fontSize = 18.sp, color = Color.White)
    }
}

// Helper function for navigation & closing the bottom sheet
@OptIn(ExperimentalMaterial3Api::class)
fun navigateAndClose(
    navController: NavController,
    route: String,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>
) {
    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
            showBottomSheet.value = false
            navController.navigate(route)
        }
    }
}
