package com.gultekinahmetabdullah.trainvoc.test.util

import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Test implementation of [DispatcherProvider].
 *
 * Every dispatcher delegates to [Dispatchers.Main]. In tests that install
 * [MainDispatcherRule] (or otherwise call `Dispatchers.setMain(testDispatcher)`),
 * `Dispatchers.Main` is a `TestDispatcher`, so all production coroutines launched
 * through this provider run on the SAME scheduler that `runTest {}` uses.
 *
 * This deliberately does NOT create its own `StandardTestDispatcher()`: doing so
 * spawns a second scheduler and triggers
 * `IllegalStateException: Detected use of different schedulers` the moment a
 * `withContext(dispatchers.io)` call interleaves with `runTest`'s scheduler.
 *
 * Usage:
 * ```kotlin
 * @get:Rule
 * val mainDispatcherRule = MainDispatcherRule()
 *
 * private val dispatchers = TestDispatcherProvider()
 *
 * @Test
 * fun `test async operation`() = runTest {
 *     val viewModel = MyViewModel(repository, dispatchers)
 *     advanceUntilIdle()
 *     assertEquals(expected, viewModel.state.value)
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Main
    override val unconfined: CoroutineDispatcher = Dispatchers.Main
}
