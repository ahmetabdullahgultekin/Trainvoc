package com.gultekinahmetabdullah.trainvoc

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowInsets.Type
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.MainScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.SplashScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.WelcomeScreen
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModelFactory
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModelFactory
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModelFactory
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModelFactory
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModelFactory
import com.gultekinahmetabdullah.trainvoc.worker.WordNotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

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
            /**
             * Initialize the database and repositories.
             * This is done here to ensure that
             * the database is initialized
             * before the Composable functions are called.
             * We use the AppDatabase singleton
             * to get the instance of the database
             * and create the WordRepository.
             */
            val context = this
            val db = AppDatabase.DatabaseBuilder.getInstance(context)
            val wordRepository = WordRepository(
                db.wordDao(),
                db.statisticDao(),
                db.wordExamCrossRefDao(),
                db.examDao()
            )

            /**
             * Initialize the ViewModels.
             * We use the viewModel() function to get the instances
             * of the ViewModels and pass the repositories to their factories.
             */
            val quizViewModel: QuizViewModel =
                viewModel(factory = QuizViewModelFactory(wordRepository))
            val wordViewModel: WordViewModel =
                viewModel(factory = WordViewModelFactory(wordRepository))
            val statsViewModel: StatsViewModel =
                viewModel(factory = StatsViewModelFactory(wordRepository))
            val settingsViewModel: SettingsViewModel =
                viewModel(factory = SettingsViewModelFactory(context, wordRepository))
            val storyViewModel: StoryViewModel =
                viewModel(factory = StoryViewModelFactory(wordRepository))

            // Dil tercihini al
            val languagePref by settingsViewModel.language.collectAsState()

            // Dil tercihi değiştiğinde locale'ı güncelle
            LaunchedEffect(languagePref) {
                settingsViewModel.updateLocale(languagePref.code)
            }

            /**
             * Set the theme based on user preference.
             * We collect the theme preference from the SettingsViewModel
             * and apply the appropriate theme.
             * If the preference is not set,
             * we use the system default theme.
             */
            val themePref by settingsViewModel.theme.collectAsState()
            val darkTheme = when (themePref) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                else -> isSystemInDarkTheme()
            }

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
            TrainvocTheme(darkTheme = darkTheme) {
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
                                quizViewModel = quizViewModel,
                                wordViewModel = wordViewModel,
                                statsViewModel = statsViewModel,
                                settingsViewModel = settingsViewModel,
                                storyViewModel = storyViewModel,
                                startWordId = wordIdFromNotification
                            )
                        }
                    }
                }
            }
        }

        /** * Initialize the WorkManager for word notifications.
         * We check the shared preferences to see if notifications are enabled.
         * If they are, we enqueue a periodic work request
         * to show word notifications every hour.
         * We also enqueue a one-time work request for testing purposes.
         */
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications", true)
        if (notificationsEnabled) {
            val workRequest =
                PeriodicWorkRequestBuilder<WordNotificationWorker>(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "word_notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

            val testRequest = OneTimeWorkRequestBuilder<WordNotificationWorker>().build()
            WorkManager.getInstance(this).enqueue(testRequest)
        }
        // Android 13+ için çalışma zamanında bildirim izni iste
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}
