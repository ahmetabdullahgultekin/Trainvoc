package com.gultekinahmetabdullah.trainvoc.test.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule that swaps [Dispatchers.Main] for a single [TestDispatcher] for the
 * duration of each test, and restores the original afterwards.
 *
 * Why this exists:
 * A [TestDispatcherProvider] built in `@Before` creates its dispatchers eagerly,
 * while `runTest {}` spins up its own scheduler. If the two don't share one
 * scheduler you get `IllegalStateException: Detected use of different schedulers`.
 *
 * Installing this rule makes `Dispatchers.Main` a [TestDispatcher]; `runTest {}`
 * (called with no explicit context) then reuses that same scheduler, and
 * [TestDispatcherProvider] — which now delegates to `Dispatchers.Main` — shares it
 * too. One scheduler, no conflict.
 *
 * Usage:
 * ```kotlin
 * @get:Rule
 * val mainDispatcherRule = MainDispatcherRule()
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
