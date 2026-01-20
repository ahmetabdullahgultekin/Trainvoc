package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Unit tests for GetUserStatsUseCase
 *
 * Demonstrates:
 * - Testing business logic independently of ViewModel
 * - Mocking repository dependencies
 * - Testing ratio calculations
 * - Testing error handling
 * - Using TestDispatcherProvider for deterministic tests
 */
class GetUserStatsUseCaseTest {

    private lateinit var analyticsService: IAnalyticsService
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var useCase: GetUserStatsUseCase

    @Before
    fun setup() {
        analyticsService = mock()
        dispatchers = TestDispatcherProvider()
        useCase = GetUserStatsUseCase(analyticsService, dispatchers)
    }

    @Test
    fun `invoke returns success with correct statistics`() = runTest {
        // Arrange
        whenever(analyticsService.getCorrectAnswers()).thenReturn(80)
        whenever(analyticsService.getWrongAnswers()).thenReturn(15)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(5)
        whenever(analyticsService.getTotalTimeSpent()).thenReturn(3600)
        whenever(analyticsService.getLastAnswered()).thenReturn(1642345678000L)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(100)
        whenever(analyticsService.getDailyCorrectAnswers()).thenReturn(10)
        whenever(analyticsService.getWeeklyCorrectAnswers()).thenReturn(50)
        whenever(analyticsService.getMostWrongWord()).thenReturn("difficult")
        whenever(analyticsService.getBestCategory()).thenReturn("A2")

        // Act
        val result = useCase()

        // Assert
        assertTrue("Expected Success result", result is AppResult.Success)
        val stats = (result as AppResult.Success).data

        assertEquals("Expected 80 correct answers", 80, stats.correctAnswers)
        assertEquals("Expected 15 incorrect answers", 15, stats.incorrectAnswers)
        assertEquals("Expected 5 skipped", 5, stats.skippedQuestions)
        assertEquals("Expected 100 total questions", 100, stats.totalQuestions)
        assertEquals("Expected 3600 seconds", 3600, stats.totalTimeSpent)
        assertEquals("Expected 100 quiz count", 100, stats.totalQuizCount)
        assertEquals("Expected 10 daily correct", 10, stats.dailyCorrect)
        assertEquals("Expected 50 weekly correct", 50, stats.weeklyCorrect)
        assertEquals("Expected 'difficult' as most wrong", "difficult", stats.mostWrongWord)
        assertEquals("Expected 'A2' as best category", "A2", stats.bestCategory)
    }

    @Test
    fun `invoke calculates ratios correctly`() = runTest {
        // Arrange
        whenever(analyticsService.getCorrectAnswers()).thenReturn(75)
        whenever(analyticsService.getWrongAnswers()).thenReturn(20)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(5)
        whenever(analyticsService.getTotalTimeSpent()).thenReturn(0)
        whenever(analyticsService.getLastAnswered()).thenReturn(0L)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(100)
        whenever(analyticsService.getDailyCorrectAnswers()).thenReturn(0)
        whenever(analyticsService.getWeeklyCorrectAnswers()).thenReturn(0)
        whenever(analyticsService.getMostWrongWord()).thenReturn(null)
        whenever(analyticsService.getBestCategory()).thenReturn(null)

        // Act
        val result = useCase()

        // Assert
        val stats = (result as AppResult.Success).data

        // Total = 75 + 20 + 5 = 100
        // Success = 75/100 = 0.75
        // Failure = 20/100 = 0.20
        // Skipped = 5/100 = 0.05
        assertEquals("Expected 0.75 success ratio", 0.75f, stats.successRatio, 0.001f)
        assertEquals("Expected 0.20 failure ratio", 0.20f, stats.failureRatio, 0.001f)
        assertEquals("Expected 0.05 skipped ratio", 0.05f, stats.skippedRatio, 0.001f)
    }

    @Test
    fun `invoke handles zero questions gracefully`() = runTest {
        // Arrange
        whenever(analyticsService.getCorrectAnswers()).thenReturn(0)
        whenever(analyticsService.getWrongAnswers()).thenReturn(0)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(0)
        whenever(analyticsService.getTotalTimeSpent()).thenReturn(0)
        whenever(analyticsService.getLastAnswered()).thenReturn(0L)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(0)
        whenever(analyticsService.getDailyCorrectAnswers()).thenReturn(0)
        whenever(analyticsService.getWeeklyCorrectAnswers()).thenReturn(0)
        whenever(analyticsService.getMostWrongWord()).thenReturn(null)
        whenever(analyticsService.getBestCategory()).thenReturn(null)

        // Act
        val result = useCase()

        // Assert
        val stats = (result as AppResult.Success).data

        // All ratios should be 0 (not NaN or Infinity)
        assertEquals("Expected 0 success ratio", 0f, stats.successRatio, 0.001f)
        assertEquals("Expected 0 failure ratio", 0f, stats.failureRatio, 0.001f)
        assertEquals("Expected 0 skipped ratio", 0f, stats.skippedRatio, 0.001f)
    }

    @Test
    fun `invoke formats timestamp as N-A when zero`() = runTest {
        // Arrange
        setupBasicMocks()
        whenever(analyticsService.getLastAnswered()).thenReturn(0L)

        // Act
        val result = useCase()

        // Assert
        val stats = (result as AppResult.Success).data
        assertEquals("Expected N/A for zero timestamp", "N/A", stats.lastAnswered)
    }

    @Test
    fun `invoke handles null optional fields with defaults`() = runTest {
        // Arrange
        setupBasicMocks()
        whenever(analyticsService.getMostWrongWord()).thenReturn(null)
        whenever(analyticsService.getBestCategory()).thenReturn(null)

        // Act
        val result = useCase()

        // Assert
        val stats = (result as AppResult.Success).data
        assertEquals("Expected '-' for null most wrong word", "-", stats.mostWrongWord)
        assertEquals("Expected '-' for null best category", "-", stats.bestCategory)
    }

    @Test
    fun `invoke returns error when service throws exception`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Database connection failed")
        whenever(analyticsService.getCorrectAnswers()).thenThrow(expectedException)

        // Act
        val result = useCase()

        // Assert
        assertTrue("Expected Error result", result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue("Expected Database error", error is AppError.Database)
        assertTrue(
            "Expected error message to contain exception details",
            error.message.contains("Database connection failed")
        )
        assertEquals("Expected cause to be preserved", expectedException, error.cause)
    }

    /**
     * Helper to setup basic mocks with valid data
     */
    private suspend fun setupBasicMocks() {
        whenever(analyticsService.getCorrectAnswers()).thenReturn(10)
        whenever(analyticsService.getWrongAnswers()).thenReturn(5)
        whenever(analyticsService.getSkippedAnswers()).thenReturn(2)
        whenever(analyticsService.getTotalTimeSpent()).thenReturn(600)
        whenever(analyticsService.getLastAnswered()).thenReturn(1642345678000L)
        whenever(analyticsService.getTotalQuizCount()).thenReturn(17)
        whenever(analyticsService.getDailyCorrectAnswers()).thenReturn(3)
        whenever(analyticsService.getWeeklyCorrectAnswers()).thenReturn(8)
        whenever(analyticsService.getMostWrongWord()).thenReturn("test")
        whenever(analyticsService.getBestCategory()).thenReturn("A1")
    }
}
