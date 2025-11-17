package com.gultekinahmetabdullah.trainvoc.performance

import android.os.SystemClock
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Performance monitoring utility for tracking app performance metrics
 *
 * Features:
 * - Method execution time tracking
 * - Memory usage monitoring
 * - FPS tracking for UI performance
 * - Database query performance tracking
 * - Network request latency monitoring
 *
 * Usage:
 * ```kotlin
 * // Track method execution time
 * val result = PerformanceMonitor.measureTime("fetchWords") {
 *     wordRepository.getWords()
 * }
 *
 * // Track memory usage
 * PerformanceMonitor.logMemoryUsage("AfterWordLoad")
 *
 * // Track database query
 * PerformanceMonitor.trackDatabaseQuery("getWordsByLevel", duration = 45)
 * ```
 */
object PerformanceMonitor {
    private const val TAG = "PerformanceMonitor"
    internal const val ENABLED = true // Set to false in release builds

    private val executionTimes = ConcurrentHashMap<String, MutableList<Long>>()
    private val queryMetrics = ConcurrentHashMap<String, QueryMetrics>()

    data class QueryMetrics(
        var count: Int = 0,
        var totalDuration: Long = 0,
        var minDuration: Long = Long.MAX_VALUE,
        var maxDuration: Long = 0
    )

    /**
     * Measure execution time of a code block
     * Returns the result of the block execution
     */
    internal inline fun <T> measureTime(tag: String, block: () -> T): T {
        if (!ENABLED) return block()

        val startTime = SystemClock.elapsedRealtime()
        val result = block()
        val duration = SystemClock.elapsedRealtime() - startTime

        logExecutionTime(tag, duration)
        return result
    }

    /**
     * Measure execution time of a suspending function
     */
    internal suspend inline fun <T> measureTimeSuspend(
        tag: String,
        crossinline block: suspend () -> T
    ): T {
        if (!ENABLED) return block()

        val startTime = SystemClock.elapsedRealtime()
        val result = block()
        val duration = SystemClock.elapsedRealtime() - startTime

        logExecutionTime(tag, duration)
        return result
    }

    /**
     * Log execution time for a specific operation
     */
    fun logExecutionTime(tag: String, duration: Long) {
        if (!ENABLED) return

        executionTimes.getOrPut(tag) { mutableListOf() }.add(duration)

        if (duration > 100) {
            Log.w(TAG, "⏱️ SLOW: $tag took ${duration}ms")
        } else {
            Log.d(TAG, "⏱️ $tag: ${duration}ms")
        }
    }

    /**
     * Track database query performance
     */
    fun trackDatabaseQuery(queryName: String, duration: Long) {
        if (!ENABLED) return

        val metrics = queryMetrics.getOrPut(queryName) { QueryMetrics() }
        metrics.count++
        metrics.totalDuration += duration
        metrics.minDuration = minOf(metrics.minDuration, duration)
        metrics.maxDuration = maxOf(metrics.maxDuration, duration)

        if (duration > 50) {
            Log.w(TAG, "🗄️ SLOW QUERY: $queryName took ${duration}ms")
        }
    }

    /**
     * Log current memory usage
     */
    fun logMemoryUsage(tag: String = "") {
        if (!ENABLED) return

        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        val percentage = (usedMemory.toFloat() / maxMemory * 100).toInt()

        val prefix = if (tag.isNotEmpty()) "$tag: " else ""
        Log.d(TAG, "💾 ${prefix}Memory: ${usedMemory}MB / ${maxMemory}MB ($percentage%)")

        if (percentage > 80) {
            Log.w(TAG, "⚠️ HIGH MEMORY USAGE: $percentage% - Consider running GC")
        }
    }

    /**
     * Force garbage collection and log memory improvement
     */
    fun forceGcAndLog(tag: String = "") {
        if (!ENABLED) return

        val beforeMemory = getUsedMemoryMB()
        System.gc()
        Thread.sleep(100) // Give GC time to complete
        val afterMemory = getUsedMemoryMB()
        val freed = beforeMemory - afterMemory

        Log.d(TAG, "🗑️ ${tag}GC: Freed ${freed}MB (${beforeMemory}MB -> ${afterMemory}MB)")
    }

    /**
     * Get current used memory in MB
     */
    private fun getUsedMemoryMB(): Long {
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
    }

    /**
     * Print performance summary
     */
    fun printSummary() {
        if (!ENABLED) return

        Log.d(TAG, "========== PERFORMANCE SUMMARY ==========")

        // Execution times summary
        if (executionTimes.isNotEmpty()) {
            Log.d(TAG, "📊 Execution Times:")
            executionTimes.forEach { (tag, times) ->
                val avg = times.average().toLong()
                val min = times.minOrNull() ?: 0
                val max = times.maxOrNull() ?: 0
                Log.d(TAG, "  $tag: avg=${avg}ms, min=${min}ms, max=${max}ms, count=${times.size}")
            }
        }

        // Database query summary
        if (queryMetrics.isNotEmpty()) {
            Log.d(TAG, "🗄️ Database Queries:")
            queryMetrics.forEach { (query, metrics) ->
                val avg = if (metrics.count > 0) metrics.totalDuration / metrics.count else 0
                Log.d(
                    TAG, "  $query: avg=${avg}ms, min=${metrics.minDuration}ms, " +
                            "max=${metrics.maxDuration}ms, count=${metrics.count}"
                )
            }
        }

        logMemoryUsage("Final")
        Log.d(TAG, "=========================================")
    }

    /**
     * Clear all tracked metrics
     */
    fun clearMetrics() {
        executionTimes.clear()
        queryMetrics.clear()
    }

    /**
     * Start tracking a named operation (returns start time)
     */
    fun startTracking(tag: String): Long {
        return SystemClock.elapsedRealtime()
    }

    /**
     * End tracking and log duration
     */
    fun endTracking(tag: String, startTime: Long) {
        val duration = SystemClock.elapsedRealtime() - startTime
        logExecutionTime(tag, duration)
    }
}

/**
 * Extension function for timing code blocks
 */
internal inline fun <T> timeIt(tag: String, block: () -> T): T {
    return PerformanceMonitor.measureTime(tag, block)
}

/**
 * Extension function for timing suspending code blocks
 */
internal suspend inline fun <T> timeItSuspend(tag: String, crossinline block: suspend () -> T): T {
    return PerformanceMonitor.measureTimeSuspend(tag, block)
}
