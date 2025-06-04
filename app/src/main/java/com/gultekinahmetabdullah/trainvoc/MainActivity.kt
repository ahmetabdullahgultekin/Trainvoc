package com.gultekinahmetabdullah.trainvoc

import android.os.Bundle
import android.view.View
import android.view.WindowInsets.*
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

        // Hide system bars (status and navigation)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
            androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(Type.systemBars())
                controller.systemBarsBehavior =
                    androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }

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
