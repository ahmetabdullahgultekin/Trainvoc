package com.gultekinahmetabdullah.trainvoc.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for GenerateQuizQuestionsUseCase
 *
 * Tests cover:
 * - Successful question generation
 * - Empty question handling
 * - Error handling
 * - Result wrapping
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GenerateQuizQuestionsUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var useCase: GenerateQuizQuestionsUseCase
    private lateinit var mockRepository: IWordRepository

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
        useCase = GenerateQuizQuestionsUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success result when repository returns questions`() = runTest {
        // Given: Repository returns valid questions
        val questions = createSampleQuestions(10)
        val parameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns questions

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Should return success result with questions
        assertTrue(result.isSuccess, "Result should be success")
        assertEquals(10, result.getOrNull()?.size, "Should return 10 questions")
    }

    @Test
    fun `invoke should call repository with correct parameters`() = runTest {
        // Given: Repository returns valid questions
        val questions = createSampleQuestions(10)
        val parameter = QuizParameter.LevelQuizParameter.A1
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns questions

        // When: Use case is invoked
        useCase(parameter, quiz)

        // Then: Repository should be called with correct parameters
        coVerify {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        }
    }

    @Test
    fun `invoke should return failure when repository returns empty list`() = runTest {
        // Given: Repository returns empty list
        val parameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns emptyList()

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Should return failure result
        assertTrue(result.isFailure, "Result should be failure")
        assertFalse(result.isSuccess, "Result should not be success")

        val exception = result.exceptionOrNull()
        assertTrue(
            exception?.message?.contains("No questions could be generated") == true,
            "Error message should indicate no questions generated"
        )
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given: Repository throws exception
        val parameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random
        val testException = RuntimeException("Database error")

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } throws testException

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Should return failure result with exception
        assertTrue(result.isFailure, "Result should be failure")
        assertEquals(testException, result.exceptionOrNull(), "Should contain original exception")
    }

    @Test
    fun `invoke should handle different quiz types correctly`() = runTest {
        // Given: Different quiz types
        val questions = createSampleQuestions(10)
        val parameter = QuizParameter.LevelQuizParameter.B1
        val quiz = Quiz.MostWrong

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns questions

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Should return success and call repository with correct quiz type
        assertTrue(result.isSuccess)
        coVerify {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        }
    }

    @Test
    fun `invoke should handle level parameters correctly`() = runTest {
        // Given: Level parameter
        val questions = createSampleQuestions(10)
        val parameter = QuizParameter.LevelQuizParameter.C2
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns questions

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Should pass level parameter to repository
        assertTrue(result.isSuccess)
        coVerify {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        }
    }

    @Test
    fun `invoke should handle exam parameters correctly`() = runTest {
        // Given: Exam parameter
        val questions = createSampleQuestions(10)
        val parameter = QuizParameter.ExamQuizParameter.TOEFL
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns questions

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Should pass exam parameter to repository
        assertTrue(result.isSuccess)
        coVerify {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        }
    }

    @Test
    fun `invoke should preserve question order from repository`() = runTest {
        // Given: Repository returns questions in specific order
        val q1 = createQuestion("word1", "meaning1", "correct1")
        val q2 = createQuestion("word2", "meaning2", "correct2")
        val q3 = createQuestion("word3", "meaning3", "correct3")
        val questions = listOf(q1, q2, q3)

        val parameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quiz.type, parameter)
        } returns questions

        // When: Use case is invoked
        val result = useCase(parameter, quiz)

        // Then: Questions should be in same order
        val returnedQuestions = result.getOrNull()
        assertEquals(q1, returnedQuestions?.get(0))
        assertEquals(q2, returnedQuestions?.get(1))
        assertEquals(q3, returnedQuestions?.get(2))
    }

    // Helper functions

    private fun createSampleQuestions(count: Int): List<Question> {
        return (1..count).map { index ->
            createQuestion(
                wordOrMeaning = "word$index",
                meaningOrWord = "meaning$index",
                correctAnswer = "correct$index"
            )
        }
    }

    private fun createQuestion(
        wordOrMeaning: String,
        meaningOrWord: String,
        correctAnswer: String
    ): Question {
        return Question(
            wordOrMeaning = wordOrMeaning,
            meaningOrWord = meaningOrWord,
            correctAnswer = correctAnswer,
            options = listOf(correctAnswer, "option2", "option3", "option4"),
            statId = 1
        )
    }
}
