package com.gultekinahmetabdullah.trainvoc

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowInsets.Type
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import com.gultekinahmetabdullah.trainvoc.notification.NotificationScheduler
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.MainScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.SplashScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.WelcomeScreen
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Override attachBaseContext to apply locale configuration when activity is created.
     * This is the proper way to handle locale changes on Android N (API 24) and above.
     */
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language", null) ?: "en"

        val locale = java.util.Locale(languageCode)
        java.util.Locale.setDefault(locale)

        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * In Android 11 (API level 30) and higher, the system bars are hidden
         * by default when the app is in immersive mode.
         * To ensure that the system bars are hidden
         * and the app is in immersive mode,
         * we can use the WindowInsetsControllerCompat
         * to control the visibility of the system bars.
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
            androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
                .let { controller ->
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

        /**
         * Install the splash screen.
         * This is required to show the splash screen
         * while the app is loading.
         */
        installSplashScreen()

        // Enable edge-to-edge mode for the activity
        enableEdgeToEdge()

        /** * Set the content view for the activity.
         * This is where we define the Composable functions
         * and set up the navigation graph.
         */
        setContent {
            // Initialize ViewModels using Hilt
            val quizViewModel: QuizViewModel = hiltViewModel()
            val wordViewModel: WordViewModel = hiltViewModel()
            val statsViewModel: StatsViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val storyViewModel: StoryViewModel = hiltViewModel()

            /**
             * Set the theme based on user preference.
             * We collect the theme preference from the SettingsViewModel
             * and apply the appropriate theme.
             * If the preference is not set,
             * we use the system default theme.
             */
            val themePref by settingsViewModel.theme.collectAsState()
            val colorPalettePref by settingsViewModel.colorPalette.collectAsState()

            val darkTheme = when (themePref) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                ThemePreference.AMOLED -> true
                else -> isSystemInDarkTheme()
            }

            val amoledMode = themePref == ThemePreference.AMOLED

            // Initialize the navigation controller
            val navController = rememberNavController()

            /** * Handle the intent that started the activity.
             * If the intent contains a wordId, we navigate to the MAIN screen.
             * This is useful for handling notifications that open the app.
             */
            val wordIdFromNotification = intent?.getStringExtra("wordId")
            LaunchedEffect(wordIdFromNotification) {
                if (!wordIdFromNotification.isNullOrEmpty()) {
                    navController.navigate(Route.MAIN) // Sadece MAIN'e yönlendir
                }
            }

            /** * Set the content of the activity using the TrainvocTheme.
             * We wrap the Scaffold and NavHost inside the TrainvocTheme
             * to apply the theme to the entire app.
             */
            TrainvocTheme(
                darkTheme = darkTheme,
                amoledMode = amoledMode,
                colorPalette = colorPalettePref
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = Route.SPLASH) {
                        composable(Route.SPLASH) {
                            SplashScreen(navController = navController)
                        }
                        composable(Route.WELCOME) {
                            WelcomeScreen(
                                navController = navController,
                                scaffoldPadding = innerPadding,
                                settingsViewModel = settingsViewModel
                            )
                        }
                        composable(Route.USERNAME) {
                            UsernameScreen(navController)
                        }
                        composable(Route.MAIN) {
                            MainScreen(
                                startWordId = wordIdFromNotification
                            )
                        }
                    }
                }
            }
        }

        /**
         * Initialize the notification system
         *
         * This sets up:
         * - Notification channels for different types of notifications
         * - Scheduled workers for daily reminders, streak alerts, and word of the day
         * - Handles notification permissions for Android 13+
         */

        // Create notification channels (required for Android 8.0+)
        NotificationHelper.createNotificationChannels(this)

        // Schedule all enabled notifications
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications", true)
        if (notificationsEnabled) {
            NotificationScheduler.scheduleAllNotifications(this)
        }

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}
