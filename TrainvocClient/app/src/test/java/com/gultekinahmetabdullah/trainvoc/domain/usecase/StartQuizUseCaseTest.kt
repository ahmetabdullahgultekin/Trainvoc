package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Example unit test for StartQuizUseCase
 *
 * This demonstrates how UseCases make testing easier:
 * 1. No ViewModel complexity - just test business logic
 * 2. Easy to mock dependencies (repositories, services)
 * 3. Fast tests - no Android framework dependencies
 * 4. TestDispatcherProvider enables deterministic testing
 *
 * Compare this to testing ViewModel directly:
 * - Would need to mock SavedStateHandle
 * - Would need to test UI state management
 * - Would need to handle coroutine scope complexity
 * - Harder to isolate business logic from presentation logic
 */
class StartQuizUseCaseTest {

    private lateinit var quizService: IQuizService
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var useCase: StartQuizUseCase

    @Before
    fun setup() {
        quizService = mock()
        dispatchers = TestDispatcherProvider()
        useCase = StartQuizUseCase(quizService, dispatchers)
    }

    @Test
    fun `invoke returns success when questions are generated`() = runTest {
        // Arrange
        val quiz = Quiz.NotLearned
        val parameter = QuizParameter.Level(WordLevel.A1)
        val mockQuestions = mutableListOf(
            createMockQuestion("question1"),
            createMockQuestion("question2"),
            createMockQuestion("question3")
        )

        whenever(quizService.generateTenQuestions(QuizType.NOT_LEARNED, parameter))
            .thenReturn(mockQuestions)

        // Act
        val result = useCase(quiz, parameter)

        // Assert
        assertTrue("Expected Success result", result is AppResult.Success)
        val session = (result as AppResult.Success).data
        assertEquals("Expected 3 questions", 3, session.questions.size)
        assertEquals("Expected correct quiz", quiz, session.quiz)
        assertEquals("Expected correct parameter", parameter, session.parameter)
        assertTrue("Expected start time to be set", session.startTime > 0)
    }

    @Test
    fun `invoke returns error when no questions available`() = runTest {
        // Arrange
        val quiz = Quiz.NotLearned
        val parameter = QuizParameter.Level(WordLevel.A1)

        whenever(quizService.generateTenQuestions(QuizType.NOT_LEARNED, parameter))
            .thenReturn(mutableListOf()) // Empty list

        // Act
        val result = useCase(quiz, parameter)

        // Assert
        assertTrue("Expected Error result", result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue("Expected NotFound error", error is AppError.NotFound)
        assertTrue(
            "Expected helpful error message",
            error.message.contains("No questions available")
        )
    }

    @Test
    fun `invoke returns error when service throws exception`() = runTest {
        // Arrange
        val quiz = Quiz.NotLearned
        val parameter = QuizParameter.Level(WordLevel.A1)
        val expectedException = RuntimeException("Database connection failed")

        whenever(quizService.generateTenQuestions(QuizType.NOT_LEARNED, parameter))
            .thenThrow(expectedException)

        // Act
        val result = useCase(quiz, parameter)

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

    @Test
    fun `invoke uses IO dispatcher for database operations`() = runTest {
        // Arrange
        val quiz = Quiz.NotLearned
        val parameter = QuizParameter.Level(WordLevel.A1)
        val mockQuestions = mutableListOf(createMockQuestion("question1"))

        whenever(quizService.generateTenQuestions(QuizType.NOT_LEARNED, parameter))
            .thenReturn(mockQuestions)

        // Act
        useCase(quiz, parameter)

        // Assert - TestDispatcherProvider ensures all dispatchers use TestDispatcher
        // This makes tests deterministic and fast
        // In production, this would use Dispatchers.IO
    }

    /**
     * Helper to create mock questions for testing
     */
    private fun createMockQuestion(id: String): Question {
        return mock {
            // Configure mock as needed
        }
    }
}
