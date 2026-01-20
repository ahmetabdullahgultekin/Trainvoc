package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Unit tests for StatsViewModel
 *
 * Demonstrates:
 * - Testing ViewModel with mocked dependencies
 * - Using TestDispatcherProvider for deterministic coroutine testing
 * - Testing StateFlow updates
 * - Testing init block execution
 * - Testing ratio calculations in ViewModel
 * - Testing data aggregation
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    private lateinit var analyticsService: IAnalyticsService
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setup() {
        analyticsService = mock()
        dispatchers = TestDispatcherProvider()
    }

    @Test
    fun `init loads statistics automatically`() = runTest {
        // Arrange
        setupBasicAnalyticsMocks()

        // Act
        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle() // Process all pending coroutines

        // Assert
        assertEquals("Expected 100 correct answers", 100, viewModel.correctAnswers.value)
        assertEquals("Expected 50 incorrect answers", 50, viewModel.incorrectAnswers.value)
        assertEquals("Expected 25 skipped questions", 25, viewModel.skippedQuestions.value)
        assertEquals("Expected 175 total questions", 175, viewModel.totalQuestions.value)
    }

    @Test
    fun `fillStats updates all statistics flows`() = runTest {
        // Arrange
        setupBasicAnalyticsMocks()
        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Act
        viewModel.fillStats()
        advanceUntilIdle()

        // Assert - verify all flows are updated
        assertEquals(100, viewModel.correctAnswers.value)
        assertEquals(50, viewModel.incorrectAnswers.value)
        assertEquals(25, viewModel.skippedQuestions.value)
        assertEquals(175, viewModel.totalQuestions.value)
        assertEquals(600, viewModel.totalTimeSpent.value)
        assertEquals(175, viewModel.totalQuizCount.value)
        assertEquals(100, viewModel.dailyCorrect.value)
        assertEquals(100, viewModel.weeklyCorrect.value)
        assertEquals("difficult", viewModel.mostWrongWord.value)
        assertEquals("A2", viewModel.bestCategory.value)
    }

    @Test
    fun `fillStats calculates success ratio correctly`() = runTest {
        // Arrange - 80 correct out of 100 total
        whenever(analyticsService.getCorrectAnswers()).thenReturn(80)
        whenever(analyticsService.getWrongAnswers()).thenReturn(15)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(5)
        setupOtherAnalyticsMocks()

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert - 80/100 = 0.8
        assertEquals("Expected 0.8 success ratio", 0.8f, viewModel.successRatio.value, 0.001f)
        assertEquals("Expected 0.15 failure ratio", 0.15f, viewModel.failureRatio.value, 0.001f)
        assertEquals("Expected 0.05 skipped ratio", 0.05f, viewModel.skippedRatio.value, 0.001f)
    }

    @Test
    fun `fillStats handles zero questions gracefully`() = runTest {
        // Arrange - no questions answered
        whenever(analyticsService.getCorrectAnswers()).thenReturn(0)
        whenever(analyticsService.getWrongAnswers()).thenReturn(0)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(0)
        setupOtherAnalyticsMocks()

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert - should be 0, not NaN or Infinity
        assertEquals("Expected 0 success ratio", 0f, viewModel.successRatio.value, 0.001f)
        assertEquals("Expected 0 failure ratio", 0f, viewModel.failureRatio.value, 0.001f)
        assertEquals("Expected 0 skipped ratio", 0f, viewModel.skippedRatio.value, 0.001f)
        assertEquals("Expected 0 total questions", 0, viewModel.totalQuestions.value)
    }

    @Test
    fun `fillStats formats last answered timestamp`() = runTest {
        // Arrange
        setupBasicAnalyticsMocks()
        whenever(analyticsService.getLastAnswered()).thenReturn(1642345678000L) // Valid timestamp

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert - should be formatted, not "N/A"
        assertNotEquals("Expected formatted timestamp", "N/A", viewModel.lastAnswered.value)
        assertTrue(
            "Expected formatted date string",
            viewModel.lastAnswered.value.isNotEmpty()
        )
    }

    @Test
    fun `fillStats shows N-A for zero timestamp`() = runTest {
        // Arrange
        setupBasicAnalyticsMocks()
        whenever(analyticsService.getLastAnswered()).thenReturn(0L)

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert
        assertEquals("Expected N/A for zero timestamp", "N/A", viewModel.lastAnswered.value)
    }

    @Test
    fun `fillStats handles null optional fields with defaults`() = runTest {
        // Arrange
        setupBasicAnalyticsMocks()
        whenever(analyticsService.getMostWrongWord()).thenReturn(null)
        whenever(analyticsService.getBestCategory()).thenReturn(null)

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert
        assertEquals("Expected '-' for null most wrong word", "-", viewModel.mostWrongWord.value)
        assertEquals("Expected '-' for null best category", "-", viewModel.bestCategory.value)
    }

    @Test
    fun `totalQuizCount equals sum of all answers`() = runTest {
        // Arrange
        whenever(analyticsService.getCorrectAnswers()).thenReturn(45)
        whenever(analyticsService.getWrongAnswers()).thenReturn(30)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(15)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(90)
        setupOtherAnalyticsMocks()

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert
        assertEquals("Expected 90 total quiz count", 90, viewModel.totalQuizCount.value)
        assertEquals(
            "Total questions should match quiz count",
            viewModel.totalQuizCount.value,
            viewModel.totalQuestions.value
        )
    }

    @Test
    fun `fillStats can be called multiple times`() = runTest {
        // Arrange
        setupBasicAnalyticsMocks()
        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        val firstCorrect = viewModel.correctAnswers.value

        // Update mock to return different values
        whenever(analyticsService.getCorrectAnswers()).thenReturn(150)

        // Act - call fillStats again
        viewModel.fillStats()
        advanceUntilIdle()

        // Assert - values should update
        assertEquals("Expected updated correct count", 150, viewModel.correctAnswers.value)
        assertNotEquals("Count should have changed", firstCorrect, viewModel.correctAnswers.value)
    }

    @Test
    fun `ratios sum to approximately 1-0`() = runTest {
        // Arrange
        whenever(analyticsService.getCorrectAnswers()).thenReturn(70)
        whenever(analyticsService.getWrongAnswers()).thenReturn(20)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(10)
        setupOtherAnalyticsMocks()

        viewModel = StatsViewModel(analyticsService, dispatchers)
        advanceUntilIdle()

        // Assert - ratios should sum to 1.0 (or close due to floating point)
        val sum = viewModel.successRatio.value +
                viewModel.failureRatio.value +
                viewModel.skippedRatio.value

        assertEquals("Expected ratios to sum to 1.0", 1.0f, sum, 0.001f)
    }

    /**
     * Helper to setup basic analytics mocks with typical values
     */
    private suspend fun setupBasicAnalyticsMocks() {
        whenever(analyticsService.getCorrectAnswers()).thenReturn(100)
        whenever(analyticsService.getWrongAnswers()).thenReturn(50)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(25)
        whenever(analyticsService.getTotalTimeSpent()).thenReturn(600)
        whenever(analyticsService.getLastAnswered()).thenReturn(1642345678000L)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(175)
        whenever(analyticsService.getDailyCorrectAnswers()).thenReturn(100)
        whenever(analyticsService.getWeeklyCorrectAnswers()).thenReturn(100)
        whenever(analyticsService.getMostWrongWord()).thenReturn("difficult")
        whenever(analyticsService.getBestCategory()).thenReturn("A2")
    }

    /**
     * Helper to setup other analytics mocks (non-answer counts)
     */
    private suspend fun setupOtherAnalyticsMocks() {
        whenever(analyticsService.getTotalTimeSpent()).thenReturn(600)
        whenever(analyticsService.getLastAnswered()).thenReturn(1642345678000L)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(
            // Calculate from answer counts
            analyticsService.getCorrectAnswers() +
                    analyticsService.getWrongAnswers() +
                    analyticsService.getSkippedAnswers()
        )
        whenever(analyticsService.getDailyCorrectAnswers()).thenReturn(10)
        whenever(analyticsService.getWeeklyCorrectAnswers()).thenReturn(50)
        whenever(analyticsService.getMostWrongWord()).thenReturn("test")
        whenever(analyticsService.getBestCategory()).thenReturn("A1")
    }
}
