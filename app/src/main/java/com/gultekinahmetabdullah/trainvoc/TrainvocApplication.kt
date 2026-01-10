package com.gultekinahmetabdullah.trainvoc

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.features.worker.DailyUsageResetWorker
import com.gultekinahmetabdullah.trainvoc.performance.PerformanceMonitor
import com.gultekinahmetabdullah.trainvoc.performance.StartupOptimizer
import com.gultekinahmetabdullah.trainvoc.performance.createOptimizedWorkManagerConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application class for Trainvoc
 * Handles app-wide initialization and configuration
 */
@HiltAndroidApp
class TrainvocApplication : Application(), Configuration.Provider {

    @Inject
    lateinit fun featureFlagManager: FeatureFlagManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Application-scoped coroutine scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        val startTime = System.currentTimeMillis()

        // Critical init on main thread
        // Hilt is automatically initialized via @HiltAndroidApp annotation

        // Enable StrictMode in debug builds to catch performance violations
        StartupOptimizer.enableStrictMode(BuildConfig.DEBUG)

        // Non-critical initialization in background
        StartupOptimizer.initInBackground {
            initializeFeatureFlags()
            scheduleDailyReset()
        }

        // Log startup time
        val duration = System.currentTimeMillis() - startTime
        PerformanceMonitor.logExecutionTime("App.onCreate", duration)
    }

    /**
     * Initialize feature flags system
     * - Sets up default feature flags in database
     * - Must be called on app startup
     */
    private suspend fun initializeFeatureFlags() {
        try {
            featureFlagManager.initialize()
        } catch (e: Exception) {
            // Log error but don't crash app
            e.printStackTrace()
        }
    }

    /**
     * Schedule daily usage reset worker
     * - Resets API call counters at midnight
     * - Runs once per day automatically
     */
    private fun scheduleDailyReset() {
        try {
            DailyUsageResetWorker.schedule(this)
        } catch (e: Exception) {
            // Log error but don't crash app
            e.printStackTrace()
        }
    }

    /**
     * Custom WorkManager configuration for Hilt
     * - Enables Hilt dependency injection in Workers
     * - Uses optimized configuration for performance
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setMaxSchedulerLimit(20)
            .build()
    }
}
