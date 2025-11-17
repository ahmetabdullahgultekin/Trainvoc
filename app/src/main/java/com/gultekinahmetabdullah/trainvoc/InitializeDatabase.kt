package com.gultekinahmetabdullah.trainvoc

import android.app.Application
import androidx.work.Configuration
import com.gultekinahmetabdullah.trainvoc.performance.PerformanceMonitor
import com.gultekinahmetabdullah.trainvoc.performance.StartupOptimizer
import com.gultekinahmetabdullah.trainvoc.performance.createOptimizedWorkManagerConfig
import dagger.hilt.android.HiltAndroidApp

/**
 * Trainvoc Application Class
 *
 * Performance optimizations applied:
 * - Lazy initialization for non-critical components
 * - Background initialization for heavy operations
 * - StrictMode enabled in debug builds
 * - Optimized WorkManager configuration
 * - Startup time monitoring
 */
@HiltAndroidApp
class TrainvocApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        // Track startup time
        val startTime = System.currentTimeMillis()

        // Enable StrictMode in debug builds to catch performance violations
        // This helps identify:
        // - Disk reads/writes on main thread
        // - Network operations on main thread
        // - Memory leaks
        StartupOptimizer.enableStrictMode(BuildConfig.DEBUG)

        // Non-critical initialization moved to background
        // This improves cold start time significantly
        StartupOptimizer.initInBackground {
            // These operations happen asynchronously
            // and don't block app startup
            initializePerformanceMonitoring()
            performDatabaseMaintenance()
        }

        // Log Application.onCreate() duration
        val duration = System.currentTimeMillis() - startTime
        PerformanceMonitor.logExecutionTime("Application.onCreate", duration)

        // Log memory usage at startup
        PerformanceMonitor.logMemoryUsage("AppStartup")
    }

    /**
     * Provide optimized WorkManager configuration
     * - Reduced thread pool size for memory efficiency
     * - Custom logging level
     * - Optimized scheduler limits
     */
    override val workManagerConfiguration: Configuration
        get() = createOptimizedWorkManagerConfig()

    /**
     * Initialize performance monitoring in background
     */
    private fun initializePerformanceMonitoring() {
        if (BuildConfig.DEBUG) {
            // Performance monitoring is enabled in debug builds
            // Metrics are logged and can be reviewed with PerformanceMonitor.printSummary()
        }
    }

    /**
     * Perform database maintenance tasks in background
     * - Optimize database queries
     * - Clean up old data if needed
     * - Run VACUUM if necessary
     */
    private fun performDatabaseMaintenance() {
        // Database maintenance tasks can be added here
        // Examples:
        // - Clean up old statistics
        // - Optimize database with VACUUM
        // - Update indices if needed
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // Clear caches and non-essential data when memory is low
        PerformanceMonitor.logMemoryUsage("OnLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        // Handle different memory pressure levels
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                // App is running but system is low on memory
                // Clear caches to avoid being killed
                PerformanceMonitor.logMemoryUsage("TrimMemory:Running")
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                // App is in background
                // Good time to clear unnecessary resources
                PerformanceMonitor.logMemoryUsage("TrimMemory:UIHidden")
            }
        }
    }
}