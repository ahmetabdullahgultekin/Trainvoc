package com.gultekinahmetabdullah.trainvoc.testing

import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

/**
 * Test implementation of DispatcherProvider
 *
 * Uses TestDispatcher for all coroutines, making tests deterministic
 * and controllable. All coroutines will execute on the same dispatcher,
 * allowing precise control over execution timing.
 *
 * Usage in tests:
 * ```kotlin
 * class MyViewModelTest : BaseTest() {
 *     private val testDispatchers = TestDispatcherProvider()
 *
 *     @Test
 *     fun `test async operation`() = runTest {
 *         val viewModel = MyViewModel(testDispatchers)
 *         viewModel.loadData()
 *
 *         // Advance time to execute pending coroutines
 *         testDispatcher.scheduler.advanceUntilIdle()
 *
 *         // Assert results
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider : DispatcherProvider {
    private val testDispatcher = StandardTestDispatcher()

    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}
