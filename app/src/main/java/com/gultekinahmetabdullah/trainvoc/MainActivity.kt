package com.gultekinahmetabdullah.trainvoc

import android.os.Bundle
import android.view.View
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
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.MainScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.SplashScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.WelcomeScreen
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // Hide both the navigation bar and the status bar.
        //window.decorView.systemUiVisibility = View.INVISIBLE
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

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
                        composable(Route.USERNAME.name) {
                            UsernameScreen(navController)
                        }
                        composable(Route.MAIN.name) {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}