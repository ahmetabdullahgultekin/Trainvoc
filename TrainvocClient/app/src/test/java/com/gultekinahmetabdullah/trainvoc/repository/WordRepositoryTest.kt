package com.gultekinahmetabdullah.trainvoc.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for WordRepository
 *
 * Tests cover:
 * - Word statistics retrieval
 * - Statistics update logic
 * - Learned status calculation
 * - Progress reset
 * - Race condition handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WordRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: WordRepository
    private lateinit var mockWordDao: WordDao
    private lateinit var mockStatisticDao: StatisticDao
    private lateinit var mockWordExamCrossRefDao: WordExamCrossRefDao
    private lateinit var mockExamDao: ExamDao

    @Before
    fun setup() {
        mockWordDao = mockk(relaxed = true)
        mockStatisticDao = mockk(relaxed = true)
        mockWordExamCrossRefDao = mockk(relaxed = true)
        mockExamDao = mockk(relaxed = true)

        repository = WordRepository(
            wordDao = mockWordDao,
            statisticDao = mockStatisticDao,
            wordExamCrossRefDao = mockWordExamCrossRefDao,
            examDao = mockExamDao
        )
    }

    // ========== isLearned Tests ==========

    @Test
    fun `isLearned should return true when correct count is greater than wrong plus skipped`() {
        // Given: Statistic with more correct than wrong+skipped
        val statistic = Statistic(
            statId = 1,
            correctCount = 10,
            wrongCount = 3,
            skippedCount = 2,
            learned = false
        )

        // When: isLearned is called
        val result = repository.isLearned(statistic)

        // Then: Should return true
        assertTrue(result, "Word with 10 correct vs 5 wrong+skipped should be learned")
    }

    @Test
    fun `isLearned should return false when correct count equals wrong plus skipped`() {
        // Given: Statistic with equal correct and wrong+skipped
        val statistic = Statistic(
            statId = 1,
            correctCount = 5,
            wrongCount = 3,
            skippedCount = 2,
            learned = false
        )

        // When: isLearned is called
        val result = repository.isLearned(statistic)

        // Then: Should return false
        assertFalse(result, "Word with equal correct and wrong+skipped should not be learned")
    }

    @Test
    fun `isLearned should return false when correct count is less than wrong plus skipped`() {
        // Given: Statistic with fewer correct than wrong+skipped
        val statistic = Statistic(
            statId = 1,
            correctCount = 3,
            wrongCount = 5,
            skippedCount = 2,
            learned = false
        )

        // When: isLearned is called
        val result = repository.isLearned(statistic)

        // Then: Should return false
        assertFalse(result, "Word with fewer correct than wrong+skipped should not be learned")
    }

    @Test
    fun `isLearned should return false for new word with no attempts`() {
        // Given: New statistic with zero counts
        val statistic = Statistic(
            statId = 1,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0,
            learned = false
        )

        // When: isLearned is called
        val result = repository.isLearned(statistic)

        // Then: Should return false
        assertFalse(result, "New word with no attempts should not be learned")
    }

    // ========== Statistics Retrieval Tests ==========

    @Test
    fun `getCorrectAnswers should return correct count from dao`() = runTest {
        // Given: DAO returns 42 correct answers
        coEvery { mockWordDao.getCorrectAnswers() } returns 42

        // When: getCorrectAnswers is called
        val result = repository.getCorrectAnswers()

        // Then: Should return 42
        assertEquals(42, result)
        coVerify { mockWordDao.getCorrectAnswers() }
    }

    @Test
    fun `getWrongAnswers should return wrong count from dao`() = runTest {
        // Given: DAO returns 15 wrong answers
        coEvery { mockWordDao.getWrongAnswers() } returns 15

        // When: getWrongAnswers is called
        val result = repository.getWrongAnswers()

        // Then: Should return 15
        assertEquals(15, result)
        coVerify { mockWordDao.getWrongAnswers() }
    }

    @Test
    fun `getSkippedAnswers should return skipped count from dao`() = runTest {
        // Given: DAO returns 8 skipped answers
        coEvery { mockWordDao.getSkippedAnswers() } returns 8

        // When: getSkippedAnswers is called
        val result = repository.getSkippedAnswers()

        // Then: Should return 8
        assertEquals(8, result)
        coVerify { mockWordDao.getSkippedAnswers() }
    }

    @Test
    fun `getTotalTimeSpent should return total time from dao`() = runTest {
        // Given: DAO returns 3600 seconds
        coEvery { mockWordDao.getTotalTimeSpent() } returns 3600

        // When: getTotalTimeSpent is called
        val result = repository.getTotalTimeSpent()

        // Then: Should return 3600
        assertEquals(3600, result)
        coVerify { mockWordDao.getTotalTimeSpent() }
    }

    // ========== updateWordStats Tests ==========

    @Test
    fun `updateWordStats should update statistic directly when only one word uses it`() = runTest {
        // Given: Statistic used by only one word
        val word = createSampleWord(statId = 1)
        val statistic = Statistic(
            statId = 1,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            learned = false
        )

        coEvery { mockStatisticDao.getWordCountByStatId(1) } returns 1

        // When: updateWordStats is called
        repository.updateWordStats(statistic, word)

        // Then: Statistic should be updated directly
        coVerify {
            mockStatisticDao.updateStatistic(
                match {
                    it.statId == 1 &&
                    it.correctCount == 5 &&
                    it.wrongCount == 2 &&
                    it.skippedCount == 1 &&
                    it.learned == true // Should be marked as learned (5 > 2+1)
                }
            )
        }
    }

    @Test
    fun `updateWordStats should reuse existing statistic when found`() = runTest {
        // Given: Statistic used by multiple words and matching statistic exists
        val word = createSampleWord(statId = 1)
        val currentStatistic = Statistic(
            statId = 1,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            learned = false
        )
        val existingStatistic = Statistic(
            statId = 99,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            learned = true
        )

        coEvery { mockStatisticDao.getWordCountByStatId(1) } returns 2
        coEvery {
            mockWordDao.getStatByValues(5, 2, 1, true)
        } returns existingStatistic

        // When: updateWordStats is called
        repository.updateWordStats(currentStatistic, word)

        // Then: Word should be updated to use existing statistic
        coVerify {
            mockWordDao.updateWordStatId(99, word.word)
        }
    }

    @Test
    fun `updateWordStats should create new statistic when no match exists`() = runTest {
        // Given: Statistic used by multiple words and no matching statistic exists
        val word = createSampleWord(statId = 1)
        val statistic = Statistic(
            statId = 1,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            learned = false
        )

        coEvery { mockStatisticDao.getWordCountByStatId(1) } returns 2
        coEvery { mockWordDao.getStatByValues(5, 2, 1, true) } returns null
        coEvery { mockStatisticDao.insertStatistic(any()) } returns 99L

        // When: updateWordStats is called
        repository.updateWordStats(statistic, word)

        // Then: New statistic should be inserted and word updated
        coVerify {
            mockStatisticDao.insertStatistic(
                match {
                    it.statId == 0 && // New statistic
                    it.correctCount == 5 &&
                    it.wrongCount == 2 &&
                    it.skippedCount == 1 &&
                    it.learned == true
                }
            )
            mockWordDao.updateWordStatId(99, word.word)
        }
    }

    @Test
    fun `updateWordStats should handle race condition when insert fails`() = runTest {
        // Given: Concurrent insert causes conflict
        val word = createSampleWord(statId = 1)
        val statistic = Statistic(
            statId = 1,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            learned = false
        )
        val raceConditionStatistic = Statistic(
            statId = 100,
            correctCount = 5,
            wrongCount = 2,
            skippedCount = 1,
            learned = true
        )

        coEvery { mockStatisticDao.getWordCountByStatId(1) } returns 2
        coEvery { mockWordDao.getStatByValues(5, 2, 1, true) } returns null andThen raceConditionStatistic
        coEvery { mockStatisticDao.insertStatistic(any()) } returns -1L // Conflict

        // When: updateWordStats is called
        repository.updateWordStats(statistic, word)

        // Then: Should handle race condition and reuse the statistic inserted by another thread
        coVerify {
            mockWordDao.getStatByValues(5, 2, 1, true) // Called twice: once before insert, once after conflict
            mockWordDao.updateWordStatId(100, word.word)
        }
    }

    // ========== resetProgress Tests ==========

    @Test
    fun `resetProgress should reset all statistics and words`() = runTest {
        // When: resetProgress is called
        repository.resetProgress()

        // Then: All reset methods should be called
        coVerify {
            mockStatisticDao.resetProgress()
            mockWordDao.resetAllWordStatIds()
            mockWordDao.resetAllWords()
        }
    }

    // ========== Helper Functions ==========

    private fun createSampleWord(
        word: String = "test",
        meaning: String = "test meaning",
        level: WordLevel = WordLevel.A1,
        statId: Int = 1
    ): Word {
        return Word(
            word = word,
            meaning = meaning,
            level = level,
            lastReviewed = null,
            statId = statId,
            secondsSpent = 0
        )
    }

    private fun createSampleStatistic(
        statId: Int = 1,
        correctCount: Int = 0,
        wrongCount: Int = 0,
        skippedCount: Int = 0,
        learned: Boolean = false
    ): Statistic {
        return Statistic(
            statId = statId,
            correctCount = correctCount,
            wrongCount = wrongCount,
            skippedCount = skippedCount,
            learned = learned
        )
    }
}
