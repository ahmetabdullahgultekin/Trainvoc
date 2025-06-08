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

        // Hide system bars (status and navigation)
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

        installSplashScreen()

        enableEdgeToEdge()

        setContent {
            val context = this
            val db = AppDatabase.DatabaseBuilder.getInstance(context)
            val wordRepository = WordRepository(
                db.wordDao(),
                db.statisticDao(),
                db.wordExamCrossRefDao(),
                db.examDao()
            )
            val quizViewModel: QuizViewModel =
                viewModel(factory = QuizViewModelFactory(wordRepository))
            val wordViewModel: WordViewModel =
                viewModel(factory = WordViewModelFactory(wordRepository))
            val statsViewModel: StatsViewModel =
                viewModel(factory = StatsViewModelFactory(wordRepository))
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(context, wordRepository)
            )
            val storyViewModel: StoryViewModel =
                viewModel(factory = StoryViewModelFactory(wordRepository))
            val themePref by settingsViewModel.theme.collectAsState()
            val darkTheme = when (themePref) {
                getString(R.string.light) -> false
                getString(R.string.dark) -> true
                else -> isSystemInDarkTheme()
            }
            val navController = rememberNavController()

            // Bildirimden kelime id'si geldiyse, MainScreen'e yönlendir
            val wordIdFromNotification = intent?.getStringExtra("wordId")
            LaunchedEffect(wordIdFromNotification) {
                if (!wordIdFromNotification.isNullOrEmpty()) {
                    navController.navigate(Route.MAIN) // Sadece MAIN'e yönlendir
                }
            }

            TrainvocTheme(darkTheme = darkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = Route.SPLASH) {
                        composable(Route.SPLASH) {
                            SplashScreen(navController = navController)
                        }
                        composable(Route.WELCOME) {
                            WelcomeScreen(
                                navController = navController,
                                scaffoldPadding = innerPadding
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
        // Bildirim işini başlat (sadece bir kez başlatılır)
        val workRequest =
            PeriodicWorkRequestBuilder<WordNotificationWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "word_notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        // Test için tek seferlik bildirim gönder
        val testRequest = OneTimeWorkRequestBuilder<WordNotificationWorker>().build()
        WorkManager.getInstance(this).enqueue(testRequest)

        // Android 13+ için çalışma zamanında bildirim izni iste
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}
