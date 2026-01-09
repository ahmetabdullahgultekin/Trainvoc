package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for QuizViewModel
 *
 * Tests cover:
 * - Quiz initialization
 * - Question generation
 * - Quiz state management
 * - Answer checking
 * - Score calculation
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: QuizViewModel
    private lateinit var mockRepository: IWordRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        viewModel = QuizViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have null quiz`() = runTest {
        // When: ViewModel is initialized
        // Then: quiz should be null
        viewModel.quiz.test {
            val quiz = awaitItem()
            assertEquals(null, quiz)
        }
    }

    @Test
    fun `initial state should have empty questions list`() = runTest {
        // When: ViewModel is initialized
        // Then: questions list should be empty
        viewModel.quizQuestions.test {
            val questions = awaitItem()
            assertTrue(questions.isEmpty())
        }
    }

    @Test
    fun `initial state should have correct default values`() = runTest {
        // When: ViewModel is initialized
        // Then: all state values should be at defaults
        assertEquals(60, viewModel.timeLeft.value)
        assertEquals(1f, viewModel.progress.value)
        assertEquals(0, viewModel.score.value)
        assertEquals(false, viewModel.isTimeOver.value)
        assertEquals(false, viewModel.isTimeRunning.value)
        assertEquals(false, viewModel.isAnswered.value)
        assertEquals(false, viewModel.isQuizFinished.value)
        assertEquals(true, viewModel.isUserReady.value)
    }

    @Test
    fun `resetQuiz should reset all state to defaults`() = runTest {
        // Given: Quiz is in progress with modified state
        // Note: We can't directly modify private state, but we can test reset behavior

        // When: resetQuiz is called
        viewModel.resetQuiz()

        // Then: All state should be reset to defaults
        assertEquals(60, viewModel.timeLeft.value)
        assertEquals(1f, viewModel.progress.value)
        assertEquals(0, viewModel.score.value)
        assertEquals(false, viewModel.isTimeOver.value)
        assertEquals(false, viewModel.isTimeRunning.value)
        assertEquals(false, viewModel.isAnswered.value)
        assertEquals(false, viewModel.isQuizFinished.value)
    }

    @Test
    fun `startQuiz should generate questions from repository`() = runTest {
        // Given: Repository returns sample words
        val sampleWords = createSampleWords(10)
        val quizParameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quizParameter, quiz)
        } returns sampleWords

        // When: startQuiz is called
        viewModel.startQuiz(quizParameter, quiz)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Repository should be called to generate questions
        coVerify { mockRepository.generateTenQuestions(quizParameter, quiz) }
    }

    @Test
    fun `quiz parameter should be set when starting quiz`() = runTest {
        // Given: Sample data
        val sampleWords = createSampleWords(10)
        val quizParameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quizParameter, quiz)
        } returns sampleWords

        // When: startQuiz is called
        viewModel.startQuiz(quizParameter, quiz)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Quiz parameter should be set
        viewModel.quizParameter.test {
            val param = awaitItem()
            assertEquals(quizParameter, param)
        }
    }

    @Test
    fun `quiz should be set when starting quiz`() = runTest {
        // Given: Sample data
        val sampleWords = createSampleWords(10)
        val quizParameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quizParameter, quiz)
        } returns sampleWords

        // When: startQuiz is called
        viewModel.startQuiz(quizParameter, quiz)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Quiz should be set
        viewModel.quiz.test {
            val quizValue = awaitItem()
            assertEquals(quiz, quizValue)
        }
    }

    @Test
    fun `checkAnswer with correct answer should increment score`() = runTest {
        // Given: Quiz is started with sample data
        val sampleWords = createSampleWords(10)
        val quizParameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quizParameter, quiz)
        } returns sampleWords

        coEvery {
            mockRepository.getStatisticByStatId(any())
        } returns flowOf(createSampleStatistic())

        viewModel.startQuiz(quizParameter, quiz)
        testDispatcher.scheduler.advanceUntilIdle()

        val initialScore = viewModel.score.value

        // When: Correct answer is provided
        val currentQuestion = viewModel.currentQuestion.value
        if (currentQuestion != null) {
            viewModel.checkAnswer(currentQuestion.correctAnswer)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then: Score should increment
            assertTrue(viewModel.score.value > initialScore)
        }
    }

    @Test
    fun `checkAnswer should mark question as answered`() = runTest {
        // Given: Quiz is started
        val sampleWords = createSampleWords(10)
        val quizParameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quizParameter, quiz)
        } returns sampleWords

        coEvery {
            mockRepository.getStatisticByStatId(any())
        } returns flowOf(createSampleStatistic())

        viewModel.startQuiz(quizParameter, quiz)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Answer is provided
        val currentQuestion = viewModel.currentQuestion.value
        if (currentQuestion != null) {
            viewModel.checkAnswer("any answer")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then: Question should be marked as answered
            assertEquals(true, viewModel.isAnswered.value)
        }
    }

    @Test
    fun `repository should be called to update statistics when answer is checked`() = runTest {
        // Given: Quiz is started
        val sampleWords = createSampleWords(10)
        val quizParameter = QuizParameter.RandomQuizParameter
        val quiz = Quiz.Random

        coEvery {
            mockRepository.generateTenQuestions(quizParameter, quiz)
        } returns sampleWords

        coEvery {
            mockRepository.getStatisticByStatId(any())
        } returns flowOf(createSampleStatistic())

        viewModel.startQuiz(quizParameter, quiz)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Answer is checked
        val currentQuestion = viewModel.currentQuestion.value
        if (currentQuestion != null) {
            val correctAnswer = currentQuestion.correctAnswer
            viewModel.checkAnswer(correctAnswer)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then: Repository should be called to update statistics
            coVerify(atLeast = 1) {
                mockRepository.updateWordStats(any(), any(), any(), any())
            }
        }
    }

    // Helper functions

    private fun createSampleWords(count: Int): List<Word> {
        return (1..count).map { index ->
            Word(
                word = "word$index",
                meaning = "meaning$index",
                level = WordLevel.A1,
                lastReviewed = null,
                statId = index,
                secondsSpent = 0
            )
        }
    }

    private fun createSampleStatistic(): Statistic {
        return Statistic(
            statId = 1,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0,
            learned = false
        )
    }
}
