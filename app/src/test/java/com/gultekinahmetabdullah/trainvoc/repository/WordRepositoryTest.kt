package com.gultekinahmetabdullah.trainvoc.repository

import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for WordRepository
 *
 * Tests cover:
 * - Reset progress functionality
 * - Statistics retrieval (correct/wrong/skipped answers)
 * - Word statistics management (complex updateWordStats logic)
 * - Quiz question generation
 * - Level unlocking logic
 * - Word and exam operations
 * - Analytics queries
 *
 * Uses dependency injection with mocked DAOs:
 * - MockK for creating test doubles
 * - coEvery/coVerify for suspend functions
 * - Truth for assertions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WordRepositoryTest : BaseTest() {

    private lateinit var repository: WordRepository
    private lateinit var mockWordDao: WordDao
    private lateinit var mockStatisticDao: StatisticDao
    private lateinit var mockWordExamCrossRefDao: WordExamCrossRefDao
    private lateinit var mockExamDao: ExamDao

    // Test data
    private val testWord = Word(
        word = "hello",
        meaning = "merhaba",
        level = WordLevel.A1,
        lastReviewed = 0L,
        statId = 1,
        secondsSpent = 0
    )

    private val testStatistic = Statistic(
        statId = 1,
        correctCount = 2,
        wrongCount = 1,
        skippedCount = 1,
        learned = false
    )

    @Before
    override fun setup() {
        super.setup()

        // Create mocks
        mockWordDao = mockk(relaxed = true)
        mockStatisticDao = mockk(relaxed = true)
        mockWordExamCrossRefDao = mockk(relaxed = true)
        mockExamDao = mockk(relaxed = true)

        // Create repository with mocked dependencies
        repository = WordRepository(
            wordDao = mockWordDao,
            statisticDao = mockStatisticDao,
            wordExamCrossRefDao = mockWordExamCrossRefDao,
            examDao = mockExamDao
        )
    }

    @Test
    fun `resetProgress calls all necessary DAO methods`() = runTest {
        // Given
        coEvery { mockStatisticDao.resetProgress() } just Runs
        coEvery { mockWordDao.resetAllWordStatIds() } just Runs
        coEvery { mockWordDao.resetAllWords() } just Runs

        // When
        repository.resetProgress()

        // Then
        coVerify(exactly = 1) { mockStatisticDao.resetProgress() }
        coVerify(exactly = 1) { mockWordDao.resetAllWordStatIds() }
        coVerify(exactly = 1) { mockWordDao.resetAllWords() }
    }

    @Test
    fun `getCorrectAnswers returns value from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getCorrectAnswers() } returns 42

        // When
        val result = repository.getCorrectAnswers()

        // Then
        assertThat(result).isEqualTo(42)
        coVerify { mockWordDao.getCorrectAnswers() }
    }

    @Test
    fun `getWrongAnswers returns value from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getWrongAnswers() } returns 15

        // When
        val result = repository.getWrongAnswers()

        // Then
        assertThat(result).isEqualTo(15)
        coVerify { mockWordDao.getWrongAnswers() }
    }

    @Test
    fun `getSkippedAnswers returns value from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getSkippedAnswers() } returns 8

        // When
        val result = repository.getSkippedAnswers()

        // Then
        assertThat(result).isEqualTo(8)
        coVerify { mockWordDao.getSkippedAnswers() }
    }

    @Test
    fun `isLearned returns true when correct count is greater than wrong plus skipped`() {
        // Given - correctCount (3) > wrongCount (1) + skippedCount (1)
        val statistic = Statistic(
            statId = 1,
            correctCount = 3,
            wrongCount = 1,
            skippedCount = 1,
            learned = false
        )

        // When
        val result = repository.isLearned(statistic)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isLearned returns false when correct count is not greater than wrong plus skipped`() {
        // Given - correctCount (2) NOT > wrongCount (1) + skippedCount (1)
        val statistic = Statistic(
            statId = 1,
            correctCount = 2,
            wrongCount = 1,
            skippedCount = 1,
            learned = false
        )

        // When
        val result = repository.isLearned(statistic)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `updateWordStats with single word updates statistic in place`() = runTest {
        // Given - Only 1 word uses this statistic
        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 1
        coEvery { mockStatisticDao.updateStatistic(any()) } just Runs
        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 1

        // When
        repository.updateWordStats(testStatistic, testWord)

        // Then
        coVerify { mockStatisticDao.updateStatistic(match { it.statId == testWord.statId }) }
        coVerify(exactly = 0) { mockWordDao.updateWordStatId(any(), any()) }
    }

    @Test
    fun `updateWordStats with shared statistic reuses existing matching statistic`() = runTest {
        // Given - Multiple words share this statistic
        val existingStatistic = Statistic(
            statId = 5,
            correctCount = testStatistic.correctCount,
            wrongCount = testStatistic.wrongCount,
            skippedCount = testStatistic.skippedCount,
            learned = false
        )

        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 3
        coEvery { mockWordDao.getStatByValues(any(), any(), any(), any()) } returns existingStatistic
        coEvery { mockWordDao.updateWordStatId(any(), any()) } just Runs
        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 2
        coEvery { mockStatisticDao.deleteStatistic(any()) } just Runs

        // When
        repository.updateWordStats(testStatistic, testWord)

        // Then
        coVerify { mockWordDao.updateWordStatId(existingStatistic.statId, testWord.word) }
        coVerify(exactly = 0) { mockStatisticDao.insertStatistic(any()) }
    }

    @Test
    fun `updateWordStats with shared statistic creates new when no match exists`() = runTest {
        // Given - Multiple words share this statistic, but no matching stat exists
        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 3
        coEvery { mockWordDao.getStatByValues(any(), any(), any(), any()) } returns null
        coEvery { mockStatisticDao.insertStatistic(any()) } returns 10L
        coEvery { mockWordDao.updateWordStatId(any(), any()) } just Runs
        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 2
        coEvery { mockStatisticDao.deleteStatistic(any()) } just Runs

        // When
        repository.updateWordStats(testStatistic, testWord)

        // Then
        coVerify { mockStatisticDao.insertStatistic(any()) }
        coVerify { mockWordDao.updateWordStatId(10, testWord.word) }
    }

    @Test
    fun `updateWordStats handles race condition when insert fails`() = runTest {
        // Given - Insert returns -1 (conflict), then we query for the race condition stat
        val raceConditionStat = Statistic(
            statId = 7,
            correctCount = testStatistic.correctCount,
            wrongCount = testStatistic.wrongCount,
            skippedCount = testStatistic.skippedCount,
            learned = false
        )

        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 3
        coEvery { mockWordDao.getStatByValues(any(), any(), any(), any()) } returns null andThen raceConditionStat
        coEvery { mockStatisticDao.insertStatistic(any()) } returns -1L
        coEvery { mockWordDao.updateWordStatId(any(), any()) } just Runs
        coEvery { mockStatisticDao.getWordCountByStatId(testWord.statId) } returns 2
        coEvery { mockStatisticDao.deleteStatistic(any()) } just Runs

        // When
        repository.updateWordStats(testStatistic, testWord)

        // Then
        coVerify { mockStatisticDao.insertStatistic(any()) }
        coVerify(exactly = 2) { mockWordDao.getStatByValues(any(), any(), any(), any()) }
        coVerify { mockWordDao.updateWordStatId(raceConditionStat.statId, testWord.word) }
    }

    @Test
    fun `updateWordStats deletes old statistic when no words use it`() = runTest {
        // Given
        val oldStatId = testWord.statId
        coEvery { mockStatisticDao.getWordCountByStatId(oldStatId) } returns 1 andThen 0
        coEvery { mockStatisticDao.updateStatistic(any()) } just Runs
        coEvery { mockStatisticDao.deleteStatistic(any()) } just Runs

        // When
        repository.updateWordStats(testStatistic, testWord)

        // Then
        coVerify { mockStatisticDao.deleteStatistic(oldStatId) }
    }

    @Test
    fun `getWordStats returns statistic from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getStatById(testWord.statId) } returns testStatistic

        // When
        val result = repository.getWordStats(testWord)

        // Then
        assertThat(result).isEqualTo(testStatistic)
        coVerify { mockWordDao.getStatById(testWord.statId) }
    }

    @Test
    fun `updateLastAnswered updates word with current timestamp`() = runTest {
        // Given
        coEvery { mockWordDao.updateLastReviewed(any(), any()) } just Runs

        // When
        repository.updateLastAnswered(testWord.word)

        // Then
        coVerify { mockWordDao.updateLastReviewed(eq(testWord.word), more(0L)) }
    }

    @Test
    fun `updateSecondsSpent updates word with seconds`() = runTest {
        // Given
        val seconds = 45
        coEvery { mockWordDao.updateSecondsSpent(any(), any()) } just Runs

        // When
        repository.updateSecondsSpent(seconds, testWord)

        // Then
        coVerify { mockWordDao.updateSecondsSpent(seconds, testWord.word) }
    }

    @Test
    fun `generateTenQuestions returns empty list when insufficient words`() = runTest {
        // Given - Only 5 words available (need at least 10)
        val words = List(5) { index ->
            Word("word$index", "meaning$index", WordLevel.A1, 0L, 1, 0)
        }
        coEvery { mockWordDao.getWordsByQuery(any()) } returns words

        // When
        val result = repository.generateTenQuestions(
            QuizType.RANDOM,
            QuizParameter.Level(WordLevel.A1)
        )

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `generateTenQuestions returns 10 questions when sufficient words available`() = runTest {
        // Given - 50 words available
        val words = List(50) { index ->
            Word("word$index", "meaning$index", WordLevel.A1, 0L, 1, 0)
        }
        coEvery { mockWordDao.getWordsByQuery(any()) } returns words

        // When
        val result = repository.generateTenQuestions(
            QuizType.RANDOM,
            QuizParameter.Level(WordLevel.A1)
        )

        // Then
        assertThat(result).hasSize(10)
        result.forEach { question ->
            assertThat(question.incorrectWords).doesNotContain(question.correctWord)
            assertThat(question.choices).hasSize(4) // 1 correct + 3 incorrect
            assertThat(question.choices).contains(question.correctWord)
        }
    }

    @Test
    fun `generateTenQuestions with ExamType parameter filters correctly`() = runTest {
        // Given
        val words = List(50) { index ->
            Word("word$index", "meaning$index", WordLevel.A1, 0L, 1, 0)
        }
        coEvery { mockWordDao.getWordsByQuery(any()) } returns words

        // When
        val result = repository.generateTenQuestions(
            QuizType.NOT_LEARNED,
            QuizParameter.ExamType(Exam("YDS"))
        )

        // Then
        assertThat(result).hasSize(10)
        coVerify { mockWordDao.getWordsByQuery(any()) }
    }

    @Test
    fun `isLevelUnlocked returns true for A1 level`() = runTest {
        // When
        val result = repository.isLevelUnlocked(WordLevel.A1)

        // Then
        assertThat(result).isTrue()
        coVerify(exactly = 0) { mockWordDao.getLevelUnlockerWordCount(any()) }
    }

    @Test
    fun `isLevelUnlocked returns true when all words learned`() = runTest {
        // Given - All 100 words in B1 are learned
        coEvery { mockWordDao.getLevelUnlockerWordCount("B1") } returns 100
        coEvery { mockWordDao.getWordCountByLevel("B1") } returns 100

        // When
        val result = repository.isLevelUnlocked(WordLevel.B1)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isLevelUnlocked returns false when not all words learned`() = runTest {
        // Given - Only 50 of 100 words in B2 are learned
        coEvery { mockWordDao.getLevelUnlockerWordCount("B2") } returns 50
        coEvery { mockWordDao.getWordCountByLevel("B2") } returns 100

        // When
        val result = repository.isLevelUnlocked(WordLevel.B2)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getWordById returns word from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getWord(testWord.word) } returns testWord

        // When
        val result = repository.getWordById(testWord.word)

        // Then
        assertThat(result).isEqualTo(testWord)
        coVerify { mockWordDao.getWord(testWord.word) }
    }

    @Test
    fun `getExamsForWord returns exam names from CrossRefDao`() = runTest {
        // Given
        val exams = listOf("YDS", "TOEFL", "IELTS")
        coEvery { mockWordExamCrossRefDao.getExamNamesByWord(testWord.word) } returns exams

        // When
        val result = repository.getExamsForWord(testWord.word)

        // Then
        assertThat(result).isEqualTo(exams)
        coVerify { mockWordExamCrossRefDao.getExamNamesByWord(testWord.word) }
    }

    @Test
    fun `getTotalQuizCount returns value from StatisticDao`() = runTest {
        // Given
        coEvery { mockStatisticDao.getTotalAnsweredQuizCount() } returns 250

        // When
        val result = repository.getTotalQuizCount()

        // Then
        assertThat(result).isEqualTo(250)
        coVerify { mockStatisticDao.getTotalAnsweredQuizCount() }
    }

    @Test
    fun `getDailyCorrectAnswers returns value from StatisticDao`() = runTest {
        // Given
        coEvery { mockStatisticDao.getDailyCorrectAnswers() } returns 25

        // When
        val result = repository.getDailyCorrectAnswers()

        // Then
        assertThat(result).isEqualTo(25)
        coVerify { mockStatisticDao.getDailyCorrectAnswers() }
    }

    @Test
    fun `getWeeklyCorrectAnswers returns value from StatisticDao`() = runTest {
        // Given
        coEvery { mockStatisticDao.getWeeklyCorrectAnswers() } returns 150

        // When
        val result = repository.getWeeklyCorrectAnswers()

        // Then
        assertThat(result).isEqualTo(150)
        coVerify { mockStatisticDao.getWeeklyCorrectAnswers() }
    }

    @Test
    fun `getMostWrongWord returns word from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getMostWrongWord() } returns "difficult"

        // When
        val result = repository.getMostWrongWord()

        // Then
        assertThat(result).isEqualTo("difficult")
        coVerify { mockWordDao.getMostWrongWord() }
    }

    @Test
    fun `getBestCategory returns category from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getBestCategory() } returns "A1"

        // When
        val result = repository.getBestCategory()

        // Then
        assertThat(result).isEqualTo("A1")
        coVerify { mockWordDao.getBestCategory() }
    }

    @Test
    fun `getWordCountByLevel returns count from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getWordCountByLevel("C1") } returns 500

        // When
        val result = repository.getWordCountByLevel("C1")

        // Then
        assertThat(result).isEqualTo(500)
        coVerify { mockWordDao.getWordCountByLevel("C1") }
    }

    @Test
    fun `getLearnedWordCount returns count from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getLevelUnlockerWordCount("B1") } returns 75

        // When
        val result = repository.getLearnedWordCount("B1")

        // Then
        assertThat(result).isEqualTo(75)
        coVerify { mockWordDao.getLevelUnlockerWordCount("B1") }
    }

    @Test
    fun `getWordCountByExam returns count from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getWordCountByExam("YDS") } returns 300

        // When
        val result = repository.getWordCountByExam("YDS")

        // Then
        assertThat(result).isEqualTo(300)
        coVerify { mockWordDao.getWordCountByExam("YDS") }
    }

    @Test
    fun `getLearnedWordCountByExam returns count from WordDao`() = runTest {
        // Given
        coEvery { mockWordDao.getLearnedWordCountByExam("TOEFL") } returns 120

        // When
        val result = repository.getLearnedWordCountByExam("TOEFL")

        // Then
        assertThat(result).isEqualTo(120)
        coVerify { mockWordDao.getLearnedWordCountByExam("TOEFL") }
    }
}
