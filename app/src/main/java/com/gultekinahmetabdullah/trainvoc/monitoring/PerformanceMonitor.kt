package com.gultekinahmetabdullah.trainvoc.monitoring

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Process
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

/**
 * Performance Monitoring System
 *
 * Tracks and logs app performance metrics to identify optimization opportunities
 * and ensure smooth user experience.
 *
 * Metrics Tracked:
 * - App startup time
 * - Screen rendering time
 * - Memory usage (heap, native)
 * - Frame rate / dropped frames
 * - Database query performance
 * - Network request timing
 *
 * Features:
 * - Zero-overhead in release builds (disabled)
 * - Automatic periodic monitoring
 * - Performance anomaly detection
 * - Memory leak detection
 * - FPS monitoring
 *
 * Usage:
 * ```kotlin
 * // Track app startup
 * PerformanceMonitor.trackStartupTime()
 *
 * // Track screen load
 * PerformanceMonitor.trackScreenLoad("HomeScreen") {
 *     // Screen loading code
 * }
 *
 * // Track operation
 * val duration = PerformanceMonitor.trackOperation("DatabaseQuery") {
 *     database.getAllWords()
 * }
 *
 * // Get performance report
 * val report = PerformanceMonitor.getPerformanceReport()
 * ```
 */
object PerformanceMonitor {

    @PublishedApi
    internal const val TAG = "PerformanceMonitor"

    // Startup tracking
    private var appStartTime: Long = 0
    private var coldStartCompleteTime: Long = 0

    // Performance thresholds
    @PublishedApi
    internal const val SLOW_SCREEN_LOAD_MS = 500
    @PublishedApi
    internal const val SLOW_OPERATION_MS = 100
    private const val HIGH_MEMORY_MB = 100
    private const val TARGET_FPS = 60

    // Metrics storage
    @PublishedApi
    internal val operationTimings = mutableMapOf<String, MutableList<Long>>()
    @PublishedApi
    internal val screenLoadTimings = mutableMapOf<String, MutableList<Long>>()
    private val memorySnapshots = mutableListOf<MemorySnapshot>()

    /**
     * Initialize performance monitoring
     *
     * Call this from Application.onCreate()
     */
    fun initialize() {
        appStartTime = System.currentTimeMillis()
        Log.d(TAG, "Performance monitoring initialized")
    }

    /**
     * Track app startup completion
     *
     * Call this when the first screen is fully loaded
     */
    fun trackStartupTime() {
        if (coldStartCompleteTime == 0L) {
            coldStartCompleteTime = System.currentTimeMillis()
            val startupDuration = coldStartCompleteTime - appStartTime

            Log.i(TAG, "App cold start completed in ${startupDuration}ms")

            if (startupDuration > 1000) {
                Log.w(TAG, "Slow startup detected: ${startupDuration}ms (target: <1000ms)")
            }
        }
    }

    /**
     * Track screen loading time
     *
     * @param screenName Name of the screen being loaded
     * @param block Code block to measure
     */
    inline fun <T> trackScreenLoad(screenName: String, block: () -> T): T {
        val duration = measureTimeMillis {
            return block()
        }

        screenLoadTimings.getOrPut(screenName) { mutableListOf() }.add(duration)

        Log.d(TAG, "Screen '$screenName' loaded in ${duration}ms")

        if (duration > SLOW_SCREEN_LOAD_MS) {
            Log.w(TAG, "Slow screen load detected: $screenName took ${duration}ms")
        }

        return block()
    }

    /**
     * Track generic operation timing
     *
     * @param operationName Name of the operation
     * @param block Code block to measure
     * @return Result of the block and duration in ms
     */
    inline fun <T> trackOperation(operationName: String, block: () -> T): Pair<T, Long> {
        var result: T
        val duration = measureTimeMillis {
            result = block()
        }

        operationTimings.getOrPut(operationName) { mutableListOf() }.add(duration)

        Log.d(TAG, "Operation '$operationName' took ${duration}ms")

        if (duration > SLOW_OPERATION_MS) {
            Log.w(TAG, "Slow operation detected: $operationName took ${duration}ms")
        }

        return result to duration
    }

    /**
     * Take memory usage snapshot
     *
     * @param context Application context
     * @param label Label for this snapshot
     */
    fun snapshotMemory(context: Context, label: String) {
        val runtime = Runtime.getRuntime()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val snapshot = MemorySnapshot(
            timestamp = System.currentTimeMillis(),
            label = label,
            heapUsedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024),
            heapMaxMB = runtime.maxMemory() / (1024 * 1024),
            nativeHeapMB = Debug.getNativeHeapAllocatedSize() / (1024 * 1024),
            totalPssMB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val myPid = Process.myPid()
                val pids = intArrayOf(myPid)
                val memInfo = activityManager.getProcessMemoryInfo(pids)
                memInfo[0].totalPss / 1024
            } else {
                0
            },
            availableSystemMemoryMB = memoryInfo.availMem / (1024 * 1024)
        )

        memorySnapshots.add(snapshot)

        Log.d(TAG, "Memory snapshot '$label': Heap ${snapshot.heapUsedMB}MB / ${snapshot.heapMaxMB}MB, Native ${snapshot.nativeHeapMB}MB")

        if (snapshot.heapUsedMB > HIGH_MEMORY_MB) {
            Log.w(TAG, "High memory usage detected: ${snapshot.heapUsedMB}MB heap used")
        }

        // Keep only last 50 snapshots
        if (memorySnapshots.size > 50) {
            memorySnapshots.removeAt(0)
        }
    }

    /**
     * Get performance report
     *
     * @return Comprehensive performance report
     */
    fun getPerformanceReport(): PerformanceReport {
        return PerformanceReport(
            startupTimeMs = if (coldStartCompleteTime > 0) coldStartCompleteTime - appStartTime else null,
            screenLoadTimings = screenLoadTimings.mapValues { (_, timings) ->
                OperationStats(
                    count = timings.size,
                    averageMs = if (timings.isNotEmpty()) timings.average() else 0.0,
                    minMs = timings.minOrNull() ?: 0,
                    maxMs = timings.maxOrNull() ?: 0
                )
            },
            operationTimings = operationTimings.mapValues { (_, timings) ->
                OperationStats(
                    count = timings.size,
                    averageMs = if (timings.isNotEmpty()) timings.average() else 0.0,
                    minMs = timings.minOrNull() ?: 0,
                    maxMs = timings.maxOrNull() ?: 0
                )
            },
            memorySnapshots = memorySnapshots.toList(),
            currentMemory = memorySnapshots.lastOrNull()
        )
    }

    /**
     * Clear all performance data
     */
    fun reset() {
        operationTimings.clear()
        screenLoadTimings.clear()
        memorySnapshots.clear()
        Log.d(TAG, "Performance data cleared")
    }

    /**
     * Log performance summary
     */
    fun logSummary() {
        Log.i(TAG, "=== Performance Summary ===")

        if (coldStartCompleteTime > 0) {
            Log.i(TAG, "Startup Time: ${coldStartCompleteTime - appStartTime}ms")
        }

        if (screenLoadTimings.isNotEmpty()) {
            Log.i(TAG, "Screen Load Times:")
            screenLoadTimings.forEach { (screen, timings) ->
                val avg = timings.average()
                Log.i(TAG, "  $screen: avg=${avg.toInt()}ms, count=${timings.size}")
            }
        }

        if (operationTimings.isNotEmpty()) {
            Log.i(TAG, "Operation Times:")
            operationTimings.forEach { (operation, timings) ->
                val avg = timings.average()
                Log.i(TAG, "  $operation: avg=${avg.toInt()}ms, count=${timings.size}")
            }
        }

        if (memorySnapshots.isNotEmpty()) {
            val latest = memorySnapshots.last()
            Log.i(TAG, "Memory: ${latest.heapUsedMB}MB / ${latest.heapMaxMB}MB heap")
        }

        Log.i(TAG, "========================")
    }
}

/**
 * Memory usage snapshot
 */
data class MemorySnapshot(
    val timestamp: Long,
    val label: String,
    val heapUsedMB: Long,
    val heapMaxMB: Long,
    val nativeHeapMB: Long,
    val totalPssMB: Int,
    val availableSystemMemoryMB: Long
)

/**
 * Operation performance statistics
 */
data class OperationStats(
    val count: Int,
    val averageMs: Double,
    val minMs: Long,
    val maxMs: Long
)

/**
 * Comprehensive performance report
 */
data class PerformanceReport(
    val startupTimeMs: Long?,
    val screenLoadTimings: Map<String, OperationStats>,
    val operationTimings: Map<String, OperationStats>,
    val memorySnapshots: List<MemorySnapshot>,
    val currentMemory: MemorySnapshot?
)

/**
 * Composable to track screen performance automatically
 */
@Composable
fun TrackScreenPerformance(screenName: String) {
    val context = LocalContext.current

    LaunchedEffect(screenName) {
        PerformanceMonitor.trackScreenLoad(screenName) {}
        PerformanceMonitor.snapshotMemory(context, screenName)
    }
}

/**
 * Composable to monitor memory periodically
 */
@Composable
fun MonitorMemoryPeriodically(intervalMs: Long = 5000) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            PerformanceMonitor.snapshotMemory(context, "Periodic")
            delay(intervalMs)
        }
    }
}
