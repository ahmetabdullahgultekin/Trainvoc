package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for SubmitQuizAnswerUseCase
 *
 * Demonstrates:
 * - Testing quiz answer logic independently
 * - Testing correct/incorrect/skipped paths
 * - Verifying service interactions
 * - Testing score calculations
 * - Testing statistic updates
 */
class SubmitQuizAnswerUseCaseTest {

    private lateinit var wordStatisticsService: IWordStatisticsService
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var useCase: SubmitQuizAnswerUseCase

    private lateinit var testWord: Word
    private lateinit var wrongWord: Word
    private lateinit var testStatistic: Statistic

    @Before
    fun setup() {
        wordStatisticsService = mock()
        dispatchers = TestDispatcherProvider()
        useCase = SubmitQuizAnswerUseCase(wordStatisticsService, dispatchers)

        // Create test data
        testWord = Word(
            word = "apple",
            meaning = "a fruit",
            level = "A1",
            statId = 1
        )

        wrongWord = Word(
            word = "banana",
            meaning = "another fruit",
            level = "A1",
            statId = 2
        )

        testStatistic = Statistic(
            statId = 1,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            secondsSpent = 120,
            learned = false
        )
    }

    @Test
    fun `invoke with correct answer returns success with score increment`() = runTest {
        // Arrange
        whenever(wordStatisticsService.updateWordStats(any(), any())).then { }
        whenever(wordStatisticsService.updateLastAnswered(any())).then { }

        // Act
        val result = useCase(
            selectedWord = testWord,
            correctWord = testWord,
            currentStatistic = testStatistic
        )

        // Assert
        assertTrue("Expected Success result", result is AppResult.Success)
        val answerResult = (result as AppResult.Success).data

        assertTrue("Expected answer to be correct", answerResult.isCorrect)
        assertFalse("Expected answer not to be skipped", answerResult.isSkipped)
        assertEquals("Expected 10 point score increment", 10, answerResult.scoreIncrement)
        assertEquals(
            "Expected correct count to increment",
            testStatistic.correctCount + 1,
            answerResult.newStatistic.correctCount
        )
        assertEquals(
            "Expected wrong count unchanged",
            testStatistic.wrongCount,
            answerResult.newStatistic.wrongCount
        )

        // Verify service interactions
        verify(wordStatisticsService).updateWordStats(any(), eq(testWord))
        verify(wordStatisticsService).updateLastAnswered(testWord.word)
    }

    @Test
    fun `invoke with incorrect answer returns success with no score`() = runTest {
        // Arrange
        whenever(wordStatisticsService.updateWordStats(any(), any())).then { }
        whenever(wordStatisticsService.updateLastAnswered(any())).then { }

        // Act
        val result = useCase(
            selectedWord = wrongWord,
            correctWord = testWord,
            currentStatistic = testStatistic
        )

        // Assert
        assertTrue("Expected Success result", result is AppResult.Success)
        val answerResult = (result as AppResult.Success).data

        assertFalse("Expected answer to be incorrect", answerResult.isCorrect)
        assertFalse("Expected answer not to be skipped", answerResult.isSkipped)
        assertEquals("Expected 0 point score increment", 0, answerResult.scoreIncrement)
        assertEquals(
            "Expected correct count unchanged",
            testStatistic.correctCount,
            answerResult.newStatistic.correctCount
        )
        assertEquals(
            "Expected wrong count to increment",
            testStatistic.wrongCount + 1,
            answerResult.newStatistic.wrongCount
        )

        // Verify service interactions
        verify(wordStatisticsService).updateWordStats(any(), eq(testWord))
        verify(wordStatisticsService).updateLastAnswered(testWord.word)
    }

    @Test
    fun `invoke with null answer marks as skipped`() = runTest {
        // Arrange
        whenever(wordStatisticsService.updateWordStats(any(), any())).then { }
        whenever(wordStatisticsService.updateLastAnswered(any())).then { }

        // Act
        val result = useCase(
            selectedWord = null,
            correctWord = testWord,
            currentStatistic = testStatistic
        )

        // Assert
        assertTrue("Expected Success result", result is AppResult.Success)
        val answerResult = (result as AppResult.Success).data

        assertFalse("Expected answer to be incorrect", answerResult.isCorrect)
        assertTrue("Expected answer to be skipped", answerResult.isSkipped)
        assertEquals("Expected 0 point score increment", 0, answerResult.scoreIncrement)
        assertEquals(
            "Expected correct count unchanged",
            testStatistic.correctCount,
            answerResult.newStatistic.correctCount
        )
        assertEquals(
            "Expected skipped count to increment",
            testStatistic.skippedCount + 1,
            answerResult.newStatistic.skippedCount
        )

        // Verify service interactions
        verify(wordStatisticsService).updateWordStats(any(), eq(testWord))
        verify(wordStatisticsService).updateLastAnswered(testWord.word)
    }

    @Test
    fun `invoke preserves other statistic fields`() = runTest {
        // Arrange
        whenever(wordStatisticsService.updateWordStats(any(), any())).then { }
        whenever(wordStatisticsService.updateLastAnswered(any())).then { }

        // Act
        val result = useCase(
            selectedWord = testWord,
            correctWord = testWord,
            currentStatistic = testStatistic
        )

        // Assert
        val answerResult = (result as AppResult.Success).data

        assertEquals(
            "Expected secondsSpent preserved",
            testStatistic.secondsSpent,
            answerResult.newStatistic.secondsSpent
        )
        assertEquals(
            "Expected learned status preserved",
            testStatistic.learned,
            answerResult.newStatistic.learned
        )
        assertEquals(
            "Expected statId preserved",
            testStatistic.statId,
            answerResult.newStatistic.statId
        )
    }

    @Test
    fun `invoke returns error when service throws exception`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Database write failed")
        whenever(wordStatisticsService.updateWordStats(any(), any()))
            .thenThrow(expectedException)

        // Act
        val result = useCase(
            selectedWord = testWord,
            correctWord = testWord,
            currentStatistic = testStatistic
        )

        // Assert
        assertTrue("Expected Error result", result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue("Expected Database error", error is AppError.Database)
        assertTrue(
            "Expected error message to contain exception details",
            error.message.contains("Database write failed")
        )
        assertEquals("Expected cause to be preserved", expectedException, error.cause)
    }

    @Test
    fun `invoke updates statistics before updating last answered timestamp`() = runTest {
        // Arrange
        val inOrder = inOrder(wordStatisticsService)
        whenever(wordStatisticsService.updateWordStats(any(), any())).then { }
        whenever(wordStatisticsService.updateLastAnswered(any())).then { }

        // Act
        useCase(
            selectedWord = testWord,
            correctWord = testWord,
            currentStatistic = testStatistic
        )

        // Assert - verify order of operations
        inOrder.verify(wordStatisticsService).updateWordStats(any(), any())
        inOrder.verify(wordStatisticsService).updateLastAnswered(any())
    }
}
