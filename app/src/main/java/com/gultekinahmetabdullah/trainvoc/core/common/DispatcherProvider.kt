package com.gultekinahmetabdullah.trainvoc.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dispatcher provider interface
 *
 * Provides coroutine dispatchers for different execution contexts.
 * This abstraction makes coroutines testable by allowing injection
 * of test dispatchers.
 *
 * Usage in ViewModel:
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     private val dispatchers: DispatcherProvider
 * ) : ViewModel() {
 *     fun loadData() {
 *         viewModelScope.launch(dispatchers.io) {
 *             // IO operation
 *         }
 *     }
 * }
 * ```
 */
interface DispatcherProvider {
    /**
     * Main thread dispatcher
     * Use for UI updates
     */
    val main: CoroutineDispatcher

    /**
     * IO dispatcher
     * Use for network calls, database operations, file I/O
     */
    val io: CoroutineDispatcher

    /**
     * Default dispatcher
     * Use for CPU-intensive work
     */
    val default: CoroutineDispatcher

    /**
     * Unconfined dispatcher
     * Use sparingly, mainly for testing
     */
    val unconfined: CoroutineDispatcher
}

/**
 * Production implementation of DispatcherProvider
 * Uses real Android dispatchers
 */
@Singleton
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
