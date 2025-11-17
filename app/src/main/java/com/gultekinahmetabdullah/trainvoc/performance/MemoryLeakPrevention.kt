package com.gultekinahmetabdullah.trainvoc.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * Memory Leak Prevention Utilities
 *
 * Common causes of memory leaks in Android:
 * 1. Static references to Context
 * 2. Non-static inner classes holding Activity references
 * 3. Handler/Runnable leaks
 * 4. AsyncTask leaks
 * 5. Listener leaks (not unregistered)
 * 6. Bitmap leaks
 * 7. CoroutineScope not cancelled
 *
 * This file provides utilities to prevent these common leaks.
 */

/**
 * Lifecycle-aware coroutine scope that auto-cancels
 * Use instead of viewModelScope in Composables
 */
@Composable
fun rememberLifecycleAwareCoroutineScope(): CoroutineScope {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                scope.cancel("Lifecycle destroyed")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            scope.cancel("DisposableEffect disposed")
        }
    }

    return scope
}

/**
 * Execute a block when lifecycle is destroyed
 * Useful for cleaning up resources
 */
@Composable
fun OnLifecycleDestroy(onDestroy: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                onDestroy()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Remember a job that auto-cancels on dispose
 */
@Composable
fun rememberCancellableJob(): Job {
    val job = remember { Job() }

    DisposableEffect(Unit) {
        onDispose {
            job.cancel("Job cancelled on dispose")
        }
    }

    return job
}

/**
 * Execute a suspending task with automatic cleanup
 */
@Composable
fun LaunchedEffectWithCleanup(
    vararg keys: Any?,
    cleanup: () -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(*keys) {
        try {
            block()
        } finally {
            cleanup()
        }
    }
}

/**
 * Memory management utilities
 */
object MemoryManager {
    /**
     * Suggest garbage collection if memory is low
     * Use sparingly - GC is expensive
     */
    fun suggestGcIfNeeded() {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val percentage = (usedMemory.toFloat() / maxMemory * 100).toInt()

        if (percentage > 80) {
            System.gc()
        }
    }

    /**
     * Get current memory usage percentage
     */
    fun getMemoryUsagePercentage(): Int {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        return (usedMemory.toFloat() / maxMemory * 100).toInt()
    }

    /**
     * Check if app is low on memory
     */
    fun isLowMemory(): Boolean {
        return getMemoryUsagePercentage() > 80
    }
}

/**
 * Leak-safe listener wrapper
 * Automatically removes listener on dispose
 */
@Composable
fun <T> rememberLeakSafeListener(
    register: (T) -> Unit,
    unregister: (T) -> Unit,
    listener: T
) {
    DisposableEffect(listener) {
        register(listener)
        onDispose {
            unregister(listener)
        }
    }
}

/**
 * Best practices for avoiding memory leaks:
 *
 * 1. ViewModel Pattern:
 *    - Store data in ViewModels, not Activities/Fragments
 *    - ViewModels survive configuration changes
 *    - Use viewModelScope for coroutines
 *
 * 2. Context Usage:
 *    - Use applicationContext for long-lived objects
 *    - Avoid storing Activity context in static fields
 *    - Use WeakReference if you must hold Activity reference
 *
 * 3. Listeners:
 *    - Always unregister listeners in onPause/onDestroy
 *    - Use lifecycle-aware components
 *    - Use DisposableEffect in Compose
 *
 * 4. Coroutines:
 *    - Cancel coroutines when done
 *    - Use structured concurrency
 *    - Prefer viewModelScope over GlobalScope
 *
 * 5. Bitmaps:
 *    - Recycle bitmaps when done
 *    - Use appropriate inSampleSize
 *    - Consider using Coil/Glide for automatic memory management
 *
 * 6. Collections:
 *    - Clear collections when no longer needed
 *    - Be careful with static collections
 *    - Use WeakHashMap for cache-like structures
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
 *     val scope = rememberLifecycleAwareCoroutineScope()
 *
 *     LaunchedEffectWithCleanup(
 *         cleanup = {
 *             // Clean up resources
 *             viewModel.stopListening()
 *         }
 *     ) {
 *         // Do work
 *         viewModel.startListening()
 *     }
 *
 *     OnLifecycleDestroy {
 *         // Final cleanup
 *         viewModel.cleanup()
 *     }
 * }
 * ```
 */
