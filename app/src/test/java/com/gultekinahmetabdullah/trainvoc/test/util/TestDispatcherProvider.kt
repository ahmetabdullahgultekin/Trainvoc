package com.gultekinahmetabdullah.trainvoc.test.util

import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

/**
 * Test implementation of DispatcherProvider
 *
 * Uses TestDispatcher for all coroutine operations to enable:
 * - Deterministic test execution
 * - Instant execution (no real delays)
 * - Full control over coroutine timing
 * - Fast, reliable tests
 *
 * Usage in tests:
 * ```kotlin
 * @Before
 * fun setup() {
 *     val dispatchers = TestDispatcherProvider()
 *     viewModel = MyViewModel(repository, dispatchers)
 * }
 *
 * @Test
 * fun `test async operation`() = runTest {
 *     viewModel.loadData()
 *     // Test dispatcher ensures immediate execution
 *     assertEquals(expected, viewModel.state.value)
 * }
 * ```
 *
 * Benefits over using real dispatchers in tests:
 * - Tests run faster (no thread switching overhead)
 * - Tests are deterministic (no race conditions)
 * - Can advance time manually for testing delays
 * - No need for Thread.sleep() in tests
 */
class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : DispatcherProvider {
    /**
     * All dispatchers return the same TestDispatcher
     * This ensures all coroutines run on the test scheduler
     */
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}
