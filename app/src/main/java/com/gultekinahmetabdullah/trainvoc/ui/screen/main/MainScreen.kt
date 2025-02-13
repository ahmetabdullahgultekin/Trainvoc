package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gultekinahmetabdullah.trainvoc.InitializeDatabase
import com.gultekinahmetabdullah.trainvoc.classes.Route
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import com.gultekinahmetabdullah.trainvoc.ui.screen.extra.AboutScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.extra.HelpScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.extra.SettingsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.extra.StatsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    val showBottomSheet = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val repository = WordRepository(InitializeDatabase.database.wordDao())
    val quizViewModel = QuizViewModel(repository)
    val wordViewModel = WordViewModel(repository)
    val statsViewModel = StatsViewModel(repository)
    val isTopAppBarVisible = remember { mutableStateOf(true) }
    val isBottomBarVisible = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible.value) {
                BottomNavigationBar(navController)
            }
        },
        topBar = {
            if (isTopAppBarVisible.value) {
                TopAppBar(
                    title = { Text("TrainVoc") },
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        if (navBackStackEntry.value?.destination?.route == Route.HOME.name) {
                            return@TopAppBar
                        }
                        if (
                            navBackStackEntry.value?.destination?.route == Route.QUIZ.name ||
                            navBackStackEntry.value?.destination?.route == Route.QUIZ_MENU.name
                        ) {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            IconButton(onClick = {
                                navController.navigate(Route.HOME.name)
                            }) {
                                Icon(Icons.Default.Home, contentDescription = "Home")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                showBottomSheet.value = true
                            }
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.HOME.name,
            modifier = Modifier.padding(paddingValues)
        )
        {
            composable(Route.HOME.name) {
                HomeScreen(
                    onNavigateToHelp = { navController.navigate(Route.HELP.name) },
                    onNavigateToSettings = { navController.navigate(Route.SETTINGS.name) },
                    onNavigateToStats = { navController.navigate(Route.STATS.name) },
                    onNavigateToQuiz = { navController.navigate(Route.QUIZ_MENU.name) },
                )
            }
            composable(Route.QUIZ_MENU.name) {
                QuizMenuScreen(
                    onQuizSelected = { quiz ->
                        quizViewModel.startQuiz(quiz)
                        navController.navigate(Route.QUIZ.name)
                    }
                )
            }
            composable(Route.QUIZ.name) {
                QuizScreen(quizViewModel = quizViewModel, onBack = { navController.popBackStack() })
            }
            composable(Route.MANAGEMENT.name) {
                WordManagementScreen(wordViewModel = wordViewModel)
            }
            composable(Route.USERNAME.name) {
                UsernameScreen(navController)
            }
            composable(Route.SETTINGS.name) {
                SettingsScreen(navController, SettingsViewModel(LocalContext.current))
            }
            composable(Route.HELP.name) {
                HelpScreen()
            }
            composable(Route.ABOUT.name) {
                AboutScreen()
            }
            composable(Route.STATS.name) {
                StatsScreen(statsViewModel = statsViewModel)
            }
        }
    }

    if (showBottomSheet.value) {
        CustomBottomSheet(sheetState, coroutineScope, showBottomSheet, navController)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == Route.HOME.name,
            onClick = { navController.navigate(Route.HOME.name) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Build, contentDescription = "Quiz") },
            label = { Text("Quiz") },
            selected = navController.currentDestination?.route == Route.QUIZ.name,
            onClick = { navController.navigate(Route.QUIZ.name) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Stats") },
            label = { Text("Stats") },
            selected = navController.currentDestination?.route == Route.STATS.name,
            onClick = { navController.navigate(Route.STATS.name) }
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
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "More Options",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                BottomSheetButton("Manage Words") {
                    navigateAndClose(
                        navController,
                        Route.MANAGEMENT.name,
                        coroutineScope,
                        sheetState,
                        showBottomSheet
                    )
                }
                BottomSheetButton("Settings") {
                    navigateAndClose(
                        navController,
                        Route.SETTINGS.name,
                        coroutineScope,
                        sheetState,
                        showBottomSheet
                    )
                }
                BottomSheetButton("Help") {
                    navigateAndClose(
                        navController,
                        Route.HELP.name,
                        coroutineScope,
                        sheetState,
                        showBottomSheet
                    )
                }
                BottomSheetButton("About") {
                    navigateAndClose(
                        navController,
                        Route.ABOUT.name,
                        coroutineScope,
                        sheetState,
                        showBottomSheet
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close button
                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Close", color = Color.White)
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

// Helper composable for buttons inside the bottom sheet
@Composable
fun BottomSheetButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
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
