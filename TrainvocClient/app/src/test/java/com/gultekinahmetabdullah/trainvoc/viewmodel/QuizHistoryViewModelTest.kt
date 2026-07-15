package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistory
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import com.gultekinahmetabdullah.trainvoc.test.util.MainDispatcherRule
import com.gultekinahmetabdullah.trainvoc.testing.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for QuizHistoryViewModel (schema v18: question results and
 * missed-word lookups are keyed by the Long words.id).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var quizHistoryDao: QuizHistoryDao
    private lateinit var wordDao: WordDao
    private lateinit var viewModel: QuizHistoryViewModel

    private val lastQuiz = QuizHistory(
        id = 7,
        timestamp = 1_700_000_000_000L,
        totalQuestions = 10,
        correctAnswers = 8,
        wrongAnswers = 2,
        skippedQuestions = 0,
        timeTaken = "02:30",
        quizType = "LEVEL_A1",
        accuracy = 80f
    )

    @Before
    fun setup() {
        quizHistoryDao = mock()
        wordDao = mock()
    }

    private suspend fun stubEmptyHistory() {
        whenever(quizHistoryDao.getLastQuizResult()).thenReturn(null)
        whenever(quizHistoryDao.getTotalQuizCount()).thenReturn(0)
        whenever(quizHistoryDao.getAverageAccuracy()).thenReturn(null)
    }

    private fun createViewModel() = QuizHistoryViewModel(quizHistoryDao, wordDao)

    @Test
    fun `init loads the last quiz result and its missed words by word id`() = runTest {
        whenever(quizHistoryDao.getLastQuizResult()).thenReturn(lastQuiz)
        whenever(quizHistoryDao.getIncorrectWordIds(7)).thenReturn(listOf(42L, 99L))
        whenever(quizHistoryDao.getTotalQuizCount()).thenReturn(3)
        whenever(quizHistoryDao.getAverageAccuracy()).thenReturn(75f)
        val missed = TestData.word(word = "apple", meaning = "elma", id = 42L)
        whenever(wordDao.getWordById(42L)).thenReturn(missed)
        whenever(wordDao.getWordById(99L)).thenReturn(null) // word deleted since the quiz

        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(lastQuiz, viewModel.lastQuizResult.value)
        // The deleted word (id 99) is skipped instead of crashing the screen.
        assertEquals(listOf(missed), viewModel.missedWords.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `init loads overall statistics`() = runTest {
        stubEmptyHistory()
        whenever(quizHistoryDao.getTotalQuizCount()).thenReturn(12)
        whenever(quizHistoryDao.getAverageAccuracy()).thenReturn(66.5f)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(12, viewModel.totalQuizCount.value)
        assertEquals(66.5f, viewModel.averageAccuracy.value, 0.001f)
    }

    @Test
    fun `init with no history leaves result null and loading false`() = runTest {
        stubEmptyHistory()

        viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.lastQuizResult.value)
        assertTrue(viewModel.missedWords.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
        assertEquals(0f, viewModel.averageAccuracy.value, 0.001f)
    }

    @Test
    fun `saveQuizResult computes accuracy and persists per-question word ids`() = runTest {
        stubEmptyHistory()
        whenever(quizHistoryDao.insertQuizHistory(any())).thenReturn(55L)
        viewModel = createViewModel()
        advanceUntilIdle()

        val quizId = viewModel.saveQuizResult(
            totalQuestions = 4,
            correctAnswers = 3,
            wrongAnswers = 1,
            skippedQuestions = 0,
            timeTaken = "01:00",
            quizType = "EXAM_TOEFL",
            questionResults = listOf(42L to true, 99L to false)
        )
        advanceUntilIdle()

        assertEquals(55L, quizId)
        verify(quizHistoryDao).insertQuizHistory(argThat {
            totalQuestions == 4 && correctAnswers == 3 &&
                    quizType == "EXAM_TOEFL" && accuracy == 75f
        })
        verify(quizHistoryDao).insertQuestionResults(argThat {
            size == 2 &&
                    this[0].quizId == 55 && this[0].wordId == 42L && this[0].isCorrect &&
                    this[1].wordId == 99L && !this[1].isCorrect
        })
    }

    @Test
    fun `saveQuizResult with zero questions stores zero accuracy`() = runTest {
        stubEmptyHistory()
        whenever(quizHistoryDao.insertQuizHistory(any())).thenReturn(1L)
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.saveQuizResult(
            totalQuestions = 0,
            correctAnswers = 0,
            wrongAnswers = 0,
            skippedQuestions = 0,
            timeTaken = "00:00",
            quizType = "LEVEL_A1",
            questionResults = emptyList()
        )
        advanceUntilIdle()

        verify(quizHistoryDao).insertQuizHistory(argThat { accuracy == 0f })
    }

    @Test
    fun `dao failure surfaces as error state instead of crashing`() = runTest {
        whenever(quizHistoryDao.getLastQuizResult())
            .thenThrow(RuntimeException("disk I/O error"))
        whenever(quizHistoryDao.getTotalQuizCount()).thenReturn(0)
        whenever(quizHistoryDao.getAverageAccuracy()).thenReturn(null)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.error.value?.contains("disk I/O error") == true)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `clearError resets the error state`() = runTest {
        whenever(quizHistoryDao.getLastQuizResult())
            .thenThrow(RuntimeException("boom"))
        whenever(quizHistoryDao.getTotalQuizCount()).thenReturn(0)
        whenever(quizHistoryDao.getAverageAccuracy()).thenReturn(null)

        viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.error.value != null)

        viewModel.clearError()

        assertNull(viewModel.error.value)
    }

    @Test
    fun `refresh reloads the last quiz result`() = runTest {
        stubEmptyHistory()
        viewModel = createViewModel()
        advanceUntilIdle()
        assertNull(viewModel.lastQuizResult.value)

        // A quiz was completed elsewhere; refresh must pick it up.
        whenever(quizHistoryDao.getLastQuizResult()).thenReturn(lastQuiz)
        whenever(quizHistoryDao.getIncorrectWordIds(7)).thenReturn(emptyList())
        whenever(quizHistoryDao.getTotalQuizCount()).thenReturn(1)
        whenever(quizHistoryDao.getAverageAccuracy()).thenReturn(80f)

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(lastQuiz, viewModel.lastQuizResult.value)
        assertEquals(1, viewModel.totalQuizCount.value)
    }
}
