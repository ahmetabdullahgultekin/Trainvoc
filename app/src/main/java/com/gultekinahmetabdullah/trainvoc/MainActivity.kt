package com.gultekinahmetabdullah.trainvoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gultekinahmetabdullah.trainvoc.classes.Route
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import com.gultekinahmetabdullah.trainvoc.ui.screen.QuizScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.SplashScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.WelcomeScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.WordManagementScreen
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()

            TrainvocTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = Route.SPLASH.name)
                    {
                        composable(Route.SPLASH.name) {
                            SplashScreen(navController = navController)
                        }
                        composable(Route.WELCOME.name) {
                            WelcomeScreen(
                                navController = navController,
                                scaffoldPadding = innerPadding
                            )
                        }
                        composable(Route.QUIZ.name) {
                            QuizScreen()
                        }
                        composable(Route.MANAGEMENT.name) {
                            WordManagementScreen(
                                wordViewModel = WordViewModel(
                                    repository = WordRepository(InitializeDatabase.database.wordDao())
                                )
                            )
                        }
                        composable(Route.USERNAME.name) {
                            UsernameScreen(navController)
                        }
                        composable(Route.SETTINGS.name) {
                            //SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}