package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordLevel
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for QuizViewModel
 *
 * Demonstrates:
 * - Testing ViewModel with SavedStateHandle
 * - Testing quiz state management
 * - Testing answer checking logic
 * - Testing score calculation
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private lateinit var quizService: IQuizService
    private lateinit var wordStatisticsService: IWordStatisticsService
    private lateinit var progressService: IProgressService
    private lateinit var quizHistoryDao: QuizHistoryDao
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var viewModel: QuizViewModel

    private val testWord = Word(
        word = "apple",
        meaning = "elma",
        wordLevel = WordLevel.A1
    )

    private val wrongWord = Word(
        word = "banana",
        meaning = "muz",
        wordLevel = WordLevel.A1
    )

    private val testQuestion = Question(
        correctWord = testWord,
        options = listOf(testWord, wrongWord),
        questionType = QuizType.TRANSLATION
    )

    private val testStats = Statistic(
        correctCount = 5,
        wrongCount = 2,
        skippedCount = 1,
        secondsSpent = 120
    )

    @Before
    fun setup() {
        quizService = mock()
        wordStatisticsService = mock()
        progressService = mock()
        quizHistoryDao = mock()
        savedStateHandle = SavedStateHandle()
        dispatchers = TestDispatcherProvider()
    }

    private fun createViewModel(): QuizViewModel {
        return QuizViewModel(
            quizService = quizService,
            wordStatisticsService = wordStatisticsService,
            progressService = progressService,
            quizHistoryDao = quizHistoryDao,
            savedStateHandle = savedStateHandle,
            dispatchers = dispatchers
        )
    }

    @Test
    fun `initial state has default values`() = runTest {
        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.quiz.value)
        assertNull(viewModel.currentQuestion.value)
        assertEquals(0, viewModel.score.value)
        assertEquals(60, viewModel.timeLeft.value)
        assertFalse(viewModel.isQuizFinished.value)
        assertFalse(viewModel.isTimeRunning.value)
        assertFalse(viewModel.isAnswered.value)
    }

    @Test
    fun `checkAnswer with correct answer increments score`() = runTest {
        // Arrange
        setupQuizServiceMock()
        whenever(wordStatisticsService.getWordStats(any())).thenReturn(testStats)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Start quiz and get to a question
        val quiz = Quiz(type = QuizType.TRANSLATION)
        val parameter = QuizParameter.Level(WordLevel.A1)
        viewModel.startQuiz(parameter, quiz)
        advanceUntilIdle()

        val initialScore = viewModel.score.value

        // Act - answer correctly
        val result = viewModel.checkAnswer(testWord)
        advanceUntilIdle()

        // Assert
        assertTrue(result == true)
        assertEquals(initialScore + 1, viewModel.score.value)
    }

    @Test
    fun `checkAnswer with wrong answer does not increment score`() = runTest {
        // Arrange
        setupQuizServiceMock()
        whenever(wordStatisticsService.getWordStats(any())).thenReturn(testStats)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Start quiz
        val quiz = Quiz(type = QuizType.TRANSLATION)
        val parameter = QuizParameter.Level(WordLevel.A1)
        viewModel.startQuiz(parameter, quiz)
        advanceUntilIdle()

        val initialScore = viewModel.score.value

        // Act - answer incorrectly
        val result = viewModel.checkAnswer(wrongWord)
        advanceUntilIdle()

        // Assert
        assertTrue(result == false)
        assertEquals(initialScore, viewModel.score.value)
    }

    @Test
    fun `checkAnswer with null skips question`() = runTest {
        // Arrange
        setupQuizServiceMock()
        whenever(wordStatisticsService.getWordStats(any())).thenReturn(testStats)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Start quiz
        val quiz = Quiz(type = QuizType.TRANSLATION)
        val parameter = QuizParameter.Level(WordLevel.A1)
        viewModel.startQuiz(parameter, quiz)
        advanceUntilIdle()

        // Act - skip question
        val result = viewModel.checkAnswer(null)
        advanceUntilIdle()

        // Assert
        assertNull(result)
    }

    @Test
    fun `savedStateHandle preserves score across process death`() = runTest {
        // Arrange - simulate previous state
        savedStateHandle["score"] = 5
        savedStateHandle["current_index"] = 3
        savedStateHandle["time_left"] = 45

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert - values should be restored
        assertEquals(5, viewModel.score.value)
        assertEquals(45, viewModel.timeLeft.value)
    }

    @Test
    fun `finalizeQuiz resets all state`() = runTest {
        // Arrange
        setupQuizServiceMock()
        whenever(wordStatisticsService.getWordStats(any())).thenReturn(testStats)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Start and progress quiz
        val quiz = Quiz(type = QuizType.TRANSLATION)
        val parameter = QuizParameter.Level(WordLevel.A1)
        viewModel.startQuiz(parameter, quiz)
        advanceUntilIdle()

        // Answer some questions
        viewModel.checkAnswer(testWord)
        advanceUntilIdle()

        // Act
        viewModel.finalizeQuiz()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.isQuizFinished.value)
        assertEquals(0, viewModel.score.value)
        assertEquals(60, viewModel.timeLeft.value)
        assertNull(viewModel.quiz.value)
    }

    @Test
    fun `collectQuizStats loads level progress`() = runTest {
        // Arrange
        whenever(progressService.getWordCountByLevel("A1")).thenReturn(100)
        whenever(progressService.getLearnedWordCount("A1")).thenReturn(75)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.collectQuizStats(QuizParameter.Level(WordLevel.A1))
        advanceUntilIdle()

        // Assert
        assertEquals(100, viewModel.totalWords.value)
        assertEquals(75, viewModel.learnedWords.value)
        assertEquals(75, viewModel.progressPercent.value)
    }

    @Test
    fun `collectQuizStats handles zero total words`() = runTest {
        // Arrange
        whenever(progressService.getWordCountByLevel("C2")).thenReturn(0)
        whenever(progressService.getLearnedWordCount("C2")).thenReturn(0)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.collectQuizStats(QuizParameter.Level(WordLevel.C2))
        advanceUntilIdle()

        // Assert
        assertEquals(0, viewModel.totalWords.value)
        assertEquals(0, viewModel.learnedWords.value)
        assertEquals(0, viewModel.progressPercent.value)
    }

    @Test
    fun `isUserReady starts as true`() = runTest {
        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.isUserReady.value)
    }

    @Test
    fun `currentQuestionNumber starts at 1`() = runTest {
        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(1, viewModel.currentQuestionNumber.value)
    }

    @Test
    fun `progress starts at 1-0`() = runTest {
        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(1.0f, viewModel.progress.value, 0.001f)
    }

    private suspend fun setupQuizServiceMock() {
        whenever(quizService.generateTenQuestions(any(), any())).thenReturn(
            listOf(testQuestion, testQuestion, testQuestion)
        )
    }
}
