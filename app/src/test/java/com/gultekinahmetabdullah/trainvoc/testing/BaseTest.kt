package com.gultekinahmetabdullah.trainvoc.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Base test class providing common test infrastructure
 *
 * Features:
 * - Coroutine test dispatcher for deterministic async testing
 * - InstantTaskExecutor for LiveData/StateFlow testing
 * - Automatic MockK cleanup after each test
 *
 * Usage:
 * ```kotlin
 * class MyViewModelTest : BaseTest() {
 *     // Your tests here
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {

    /**
     * Ensures LiveData/StateFlow updates happen synchronously in tests
     * This makes testing reactive state changes deterministic
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Test dispatcher for coroutines
     * Provides control over coroutine execution in tests
     */
    protected val testDispatcher = StandardTestDispatcher()

    /**
     * Setup before each test
     * Configures test environment
     */
    @Before
    open fun setup() {
        // Set Main dispatcher to test dispatcher
        // This ensures all coroutines launched on Main use the test dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Cleanup after each test
     * Resets environment to avoid test pollution
     */
    @After
    open fun tearDown() {
        // Reset Main dispatcher to original
        Dispatchers.resetMain()

        // Clear all MockK mocks
        clearAllMocks()
    }
}
