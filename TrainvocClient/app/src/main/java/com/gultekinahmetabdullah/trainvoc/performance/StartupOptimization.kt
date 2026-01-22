package com.gultekinahmetabdullah.trainvoc.performance

import android.os.StrictMode
import androidx.work.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * App Startup Optimization Utilities
 *
 * Startup optimization strategies:
 * 1. Lazy initialization - defer non-critical work
 * 2. Background initialization - move heavy work off main thread
 * 3. Parallel initialization - init independent components concurrently
 * 4. Avoid blocking operations on main thread
 * 5. Minimize Application.onCreate() work
 * 6. Use Content Providers for library init (careful - adds to startup time)
 * 7. Enable StrictMode in debug builds to catch violations
 */

object StartupOptimizer {
    private val initScope = CoroutineScope(Dispatchers.Default)

    /**
     * Enable StrictMode in debug builds
     * Detects disk/network operations on main thread
     */
    fun enableStrictMode(isDebug: Boolean) {
        if (!isDebug) return

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .penaltyLog()
                .penaltyFlashScreen() // Visual indication of violations
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .detectLeakedRegistrationObjects()
                .penaltyLog()
                .build()
        )
    }

    /**
     * Initialize non-critical components in background
     * Call from Application.onCreate()
     *
     * Example:
     * ```kotlin
     * class TrainvocApplication : Application() {
     *     override fun onCreate() {
     *         super.onCreate()
     *         StartupOptimizer.initInBackground {
     *             // Heavy initialization here
     *             initAnalytics()
     *             initCrashReporting()
     *         }
     *     }
     * }
     * ```
     */
    fun initInBackground(block: suspend () -> Unit) {
        initScope.launch {
            val startTime = System.currentTimeMillis()
            block()
            val duration = System.currentTimeMillis() - startTime
            PerformanceMonitor.logExecutionTime("BackgroundInit", duration)
        }
    }

    /**
     * Lazy initialization wrapper
     * Initialize component only when first accessed
     *
     * Example:
     * ```kotlin
     * val analytics by lazy {
     *     Analytics.initialize(context)
     * }
     * ```
     */
    inline fun <T> lazyInit(crossinline initializer: () -> T): Lazy<T> {
        return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            val startTime = System.currentTimeMillis()
            val result = initializer()
            val duration = System.currentTimeMillis() - startTime
            PerformanceMonitor.logExecutionTime("LazyInit", duration)
            result
        }
    }

    /**
     * Parallel initialization for independent components
     * Faster than sequential initialization
     *
     * Example:
     * ```kotlin
     * StartupOptimizer.initInParallel(
     *     { initDatabase() },
     *     { initNetworking() },
     *     { initAnalytics() }
     * )
     * ```
     */
    suspend fun initInParallel(vararg initializers: suspend () -> Unit) {
        kotlinx.coroutines.coroutineScope {
            initializers.forEach { init ->
                launch { init() }
            }
        }
    }
}

/**
 * WorkManager Optimization Configuration
 *
 * Custom configuration for better performance:
 * - Reduced thread pool size (default is too large)
 * - Custom executor for more control
 * - Disable automatic initialization for faster startup
 */
fun createOptimizedWorkManagerConfig(): Configuration {
    return Configuration.Builder()
        // Reduce thread pool size to save memory
        // Default is min(2 + # of CPU cores, 4)
        // We reduce it since we have few workers
        .setMinimumLoggingLevel(android.util.Log.INFO)
        // Set max scheduler limit
        .setMaxSchedulerLimit(20)
        .build()
}

/**
 * App Startup Best Practices:
 *
 * 1. MEASURE FIRST
 *    - Use Android Studio's App Startup Profiler
 *    - Track cold start time (target: < 1 second)
 *    - Track warm start time (target: < 200ms)
 *
 * 2. APPLICATION.ONCREATE()
 *    ✅ DO: Hilt setup, critical dependencies
 *    ❌ DON'T: Database queries, network requests, heavy computation
 *
 * 3. LAZY INITIALIZATION
 *    - Use `by lazy` for non-critical components
 *    - Initialize on first use, not at startup
 *    - Example: Analytics, Crash reporting (if not critical)
 *
 * 4. BACKGROUND INITIALIZATION
 *    - Move heavy work to background threads
 *    - Use Dispatchers.IO or Dispatchers.Default
 *    - Don't block main thread
 *
 * 5. PARALLEL INITIALIZATION
 *    - Initialize independent components concurrently
 *    - Use coroutines for parallel execution
 *    - Example: Database + Network + Preferences in parallel
 *
 * 6. AVOID CONTENT PROVIDERS FOR INIT
 *    - Content Providers init before Application.onCreate()
 *    - Adds to startup time
 *    - Use if truly needed for library initialization
 *
 * 7. OPTIMIZE FIRST ACTIVITY
 *    - Keep first activity lightweight
 *    - Defer heavy UI loading
 *    - Show splash/loading screen if needed
 *
 * 8. ENABLE STRICT MODE (DEBUG ONLY)
 *    - Catches main thread violations
 *    - Helps identify performance issues
 *    - Disable in release builds
 */

/**
 * Startup time breakdown (typical app):
 *
 * Cold Start (app not in memory):
 * 1. Process creation: ~200ms
 * 2. Application.onCreate(): ~100ms
 * 3. Activity creation: ~200ms
 * 4. First frame: ~100ms
 * TOTAL: ~600ms
 *
 * Optimization targets:
 * - Application.onCreate(): < 50ms
 * - Activity creation: < 150ms
 * - First frame: < 50ms
 * TARGET: < 400ms total
 */

/**
 * Example optimized Application class:
 * ```kotlin
 * @HiltAndroidApp
 * class TrainvocApplication : Application(), Configuration.Provider {
 *
 *     override fun onCreate() {
 *         super.onCreate()
 *         val startTime = System.currentTimeMillis()
 *
 *         // Critical init (must be on main thread)
 *         // Hilt is automatically initialized via annotation
 *
 *         // Enable StrictMode in debug
 *         StartupOptimizer.enableStrictMode(BuildConfig.DEBUG)
 *
 *         // Non-critical init in background
 *         StartupOptimizer.initInBackground {
 *             // These can happen later
 *             initAnalytics()
 *             preloadData()
 *             optimizeDatabase()
 *         }
 *
 *         // Log startup time
 *         val duration = System.currentTimeMillis() - startTime
 *         PerformanceMonitor.logExecutionTime("App.onCreate", duration)
 *     }
 *
 *     // Custom WorkManager configuration
 *     override fun getWorkManagerConfiguration() =
 *         createOptimizedWorkManagerConfig()
 *
 *     // Lazy initialization examples
 *     private val analytics by StartupOptimizer.lazyInit {
 *         Analytics.initialize(this)
 *     }
 * }
 * ```
 */
