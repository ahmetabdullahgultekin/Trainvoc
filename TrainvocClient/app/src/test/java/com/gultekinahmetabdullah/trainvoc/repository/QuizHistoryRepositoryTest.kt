package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistory
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizQuestionResult
import com.gultekinahmetabdullah.trainvoc.test.util.MainDispatcherRule
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class QuizHistoryRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var dao: QuizHistoryDao
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var repository: QuizHistoryRepository

    @Before
    fun setup() {
        dao = mock()
        dispatchers = TestDispatcherProvider()
        repository = QuizHistoryRepository(dao, dispatchers)
    }

    @Test
    fun `insertQuizHistory delegates to dao and returns generated id`() = runTest {
        val history = QuizHistory(
            id = 0,
            timestamp = 1_700_000_000_000L,
            totalQuestions = 10,
            correctAnswers = 8,
            wrongAnswers = 2,
            skippedQuestions = 0,
            timeTaken = "01:30",
            quizType = "LEVEL_A1",
            accuracy = 80f
        )
        whenever(dao.insertQuizHistory(history)).thenReturn(42L)

        val result = repository.insertQuizHistory(history)

        assertEquals(42L, result)
        verify(dao).insertQuizHistory(history)
    }

    @Test
    fun `insertQuestionResults delegates to dao`() = runTest {
        val results = listOf(
            QuizQuestionResult(id = 0, quizId = 42, wordId = 101L, isCorrect = true),
            QuizQuestionResult(id = 0, quizId = 42, wordId = 102L, isCorrect = false)
        )

        repository.insertQuestionResults(results)

        verify(dao).insertQuestionResults(results)
    }

    @Test
    fun `getQuizHistory delegates flow from dao`() = runTest {
        val history = listOf(
            QuizHistory(
                id = 1,
                timestamp = 1_700_000_000_000L,
                totalQuestions = 5,
                correctAnswers = 5,
                wrongAnswers = 0,
                skippedQuestions = 0,
                timeTaken = "00:45",
                quizType = "REVIEW",
                accuracy = 100f
            )
        )
        whenever(dao.getQuizHistory(10)).thenReturn(flowOf(history))

        val emitted = repository.getQuizHistory(10).first()

        assertEquals(history, emitted)
    }

    @Test
    fun `getTotalQuizCount and getAverageAccuracy delegate to dao`() = runTest {
        whenever(dao.getTotalQuizCount()).thenReturn(12)
        whenever(dao.getAverageAccuracy()).thenReturn(87.5f)

        assertEquals(12, repository.getTotalQuizCount())
        assertEquals(87.5f, repository.getAverageAccuracy() ?: 0f, 0.001f)
    }
}
