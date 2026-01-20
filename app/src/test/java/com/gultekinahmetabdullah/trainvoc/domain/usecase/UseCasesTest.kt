package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive tests for all domain use cases
 *
 * Tests cover:
 * - UpdateWordStatisticsUseCase (correct/wrong/skipped answers)
 * - GenerateQuizQuestionsUseCase (question generation with error handling)
 * - CheckLevelUnlockedUseCase (level unlocking logic)
 * - CalculateProgressUseCase (progress calculation for levels/exams)
 *
 * Uses dependency injection with mocked repository:
 * - MockK for creating test doubles
 * - coEvery/coVerify for suspend functions
 * - Truth for assertions
 * - Result<T> pattern verification
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UseCasesTest : BaseTest() {

    private lateinit var mockRepository: IWordRepository

    // Use Cases under test
    private lateinit var updateWordStatisticsUseCase: UpdateWordStatisticsUseCase
    private lateinit var generateQuizQuestionsUseCase: GenerateQuizQuestionsUseCase
    private lateinit var checkLevelUnlockedUseCase: CheckLevelUnlockedUseCase
    private lateinit var calculateProgressUseCase: CalculateProgressUseCase

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

    private val testQuiz = Quiz(
        id = 1,
        name = "Random Quiz",
        description = "Test quiz",
        color = 0xFF4CAF50,
        type = QuizType.RANDOM
    )

    @Before
    override fun setup() {
        super.setup()

        // Create mock
        mockRepository = mockk(relaxed = true)

        // Create use cases with mocked repository
        updateWordStatisticsUseCase = UpdateWordStatisticsUseCase(mockRepository)
        generateQuizQuestionsUseCase = GenerateQuizQuestionsUseCase(mockRepository)
        checkLevelUnlockedUseCase = CheckLevelUnlockedUseCase(mockRepository)
        calculateProgressUseCase = CalculateProgressUseCase(mockRepository)
    }

    // ========== UpdateWordStatisticsUseCase Tests ==========

    @Test
    fun `onCorrectAnswer increments correct count and updates last answered`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } returns testStatistic
        coEvery { mockRepository.updateWordStats(any(), any()) } just Runs
        coEvery { mockRepository.updateLastAnswered(any()) } just Runs

        // When
        val result = updateWordStatisticsUseCase.onCorrectAnswer(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify {
            mockRepository.updateWordStats(
                match { it.correctCount == testStatistic.correctCount + 1 },
                testWord
            )
        }
        coVerify { mockRepository.updateLastAnswered(testWord.word) }
    }

    @Test
    fun `onCorrectAnswer returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } throws Exception("Database error")

        // When
        val result = updateWordStatisticsUseCase.onCorrectAnswer(testWord)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isNotNull()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Database error")
    }

    @Test
    fun `onWrongAnswer increments wrong count and updates last answered`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } returns testStatistic
        coEvery { mockRepository.updateWordStats(any(), any()) } just Runs
        coEvery { mockRepository.updateLastAnswered(any()) } just Runs

        // When
        val result = updateWordStatisticsUseCase.onWrongAnswer(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify {
            mockRepository.updateWordStats(
                match { it.wrongCount == testStatistic.wrongCount + 1 },
                testWord
            )
        }
        coVerify { mockRepository.updateLastAnswered(testWord.word) }
    }

    @Test
    fun `onWrongAnswer returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } throws Exception("Network error")

        // When
        val result = updateWordStatisticsUseCase.onWrongAnswer(testWord)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network error")
    }

    @Test
    fun `onSkippedAnswer increments skipped count and updates last answered`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } returns testStatistic
        coEvery { mockRepository.updateWordStats(any(), any()) } just Runs
        coEvery { mockRepository.updateLastAnswered(any()) } just Runs

        // When
        val result = updateWordStatisticsUseCase.onSkippedAnswer(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify {
            mockRepository.updateWordStats(
                match { it.skippedCount == testStatistic.skippedCount + 1 },
                testWord
            )
        }
        coVerify { mockRepository.updateLastAnswered(testWord.word) }
    }

    @Test
    fun `getWordStatistics returns statistics successfully`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } returns testStatistic

        // When
        val result = updateWordStatisticsUseCase.getWordStatistics(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testStatistic)
        coVerify { mockRepository.getWordStats(testWord) }
    }

    @Test
    fun `getWordStatistics returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.getWordStats(testWord) } throws Exception("Stats not found")

        // When
        val result = updateWordStatisticsUseCase.getWordStatistics(testWord)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Stats not found")
    }

    // ========== GenerateQuizQuestionsUseCase Tests ==========

    @Test
    fun `invoke returns questions successfully when repository provides data`() = runTest {
        // Given
        val questions = mutableListOf(
            Question(testWord, listOf())
        )
        coEvery {
            mockRepository.generateTenQuestions(any(), any())
        } returns questions

        // When
        val result = generateQuizQuestionsUseCase(
            QuizParameter.Level(WordLevel.A1),
            testQuiz
        )

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)
        coVerify { mockRepository.generateTenQuestions(QuizType.RANDOM, any()) }
    }

    @Test
    fun `invoke returns failure when repository returns empty list`() = runTest {
        // Given
        coEvery {
            mockRepository.generateTenQuestions(any(), any())
        } returns mutableListOf()

        // When
        val result = generateQuizQuestionsUseCase(
            QuizParameter.Level(WordLevel.C2),
            testQuiz
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("No questions could be generated")
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest {
        // Given
        coEvery {
            mockRepository.generateTenQuestions(any(), any())
        } throws Exception("Database unavailable")

        // When
        val result = generateQuizQuestionsUseCase(
            QuizParameter.Level(WordLevel.B1),
            testQuiz
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Database unavailable")
    }

    // ========== CheckLevelUnlockedUseCase Tests ==========

    @Test
    fun `invoke returns true for A1 level (always unlocked)`() = runTest {
        // When
        val result = checkLevelUnlockedUseCase(WordLevel.A1)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
        coVerify(exactly = 0) { mockRepository.isLevelUnlocked(any()) }
    }

    @Test
    fun `invoke returns true for A2 when A1 is completed`() = runTest {
        // Given - A1 is unlocked (completed)
        coEvery { mockRepository.isLevelUnlocked(WordLevel.A1) } returns true

        // When
        val result = checkLevelUnlockedUseCase(WordLevel.A2)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
        coVerify { mockRepository.isLevelUnlocked(WordLevel.A1) }
    }

    @Test
    fun `invoke returns false for B1 when A2 is not completed`() = runTest {
        // Given - A2 is not unlocked (not completed)
        coEvery { mockRepository.isLevelUnlocked(WordLevel.A2) } returns false

        // When
        val result = checkLevelUnlockedUseCase(WordLevel.B1)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isFalse()
        coVerify { mockRepository.isLevelUnlocked(WordLevel.A2) }
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.isLevelUnlocked(any()) } throws Exception("Database error")

        // When
        val result = checkLevelUnlockedUseCase(WordLevel.C1)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Database error")
    }

    @Test
    fun `getAllLevelsStatus returns status for all levels`() = runTest {
        // Given
        coEvery { mockRepository.isLevelUnlocked(WordLevel.A1) } returns true
        coEvery { mockRepository.isLevelUnlocked(WordLevel.A2) } returns true
        coEvery { mockRepository.isLevelUnlocked(WordLevel.B1) } returns false
        coEvery { mockRepository.isLevelUnlocked(WordLevel.B2) } returns false
        coEvery { mockRepository.isLevelUnlocked(WordLevel.C1) } returns false
        coEvery { mockRepository.isLevelUnlocked(WordLevel.C2) } returns false

        // When
        val result = checkLevelUnlockedUseCase.getAllLevelsStatus()

        // Then
        assertThat(result.isSuccess).isTrue()
        val statusMap = result.getOrNull()
        assertThat(statusMap).isNotNull()
        assertThat(statusMap?.get(WordLevel.A1)).isTrue() // Always unlocked
        assertThat(statusMap?.get(WordLevel.A2)).isTrue() // A1 completed
        assertThat(statusMap?.get(WordLevel.B1)).isFalse() // A2 not completed
    }

    // ========== CalculateProgressUseCase Tests ==========

    @Test
    fun `invoke calculates progress correctly for Level parameter`() = runTest {
        // Given
        coEvery { mockRepository.getWordCountByLevel("A1") } returns 100
        coEvery { mockRepository.getLearnedWordCount("A1") } returns 75

        // When
        val result = calculateProgressUseCase(QuizParameter.Level(WordLevel.A1))

        // Then
        assertThat(result.isSuccess).isTrue()
        val progress = result.getOrNull()
        assertThat(progress?.totalWords).isEqualTo(100)
        assertThat(progress?.learnedWords).isEqualTo(75)
        assertThat(progress?.progressPercent).isEqualTo(75)
    }

    @Test
    fun `invoke calculates progress correctly for ExamType parameter`() = runTest {
        // Given
        coEvery { mockRepository.getWordCountByExam("YDS") } returns 500
        coEvery { mockRepository.getLearnedWordCountByExam("YDS") } returns 200

        // When
        val result = calculateProgressUseCase(QuizParameter.ExamType(Exam("YDS")))

        // Then
        assertThat(result.isSuccess).isTrue()
        val progress = result.getOrNull()
        assertThat(progress?.totalWords).isEqualTo(500)
        assertThat(progress?.learnedWords).isEqualTo(200)
        assertThat(progress?.progressPercent).isEqualTo(40)
    }

    @Test
    fun `invoke returns zero percent when total words is zero`() = runTest {
        // Given
        coEvery { mockRepository.getWordCountByLevel("C2") } returns 0
        coEvery { mockRepository.getLearnedWordCount("C2") } returns 0

        // When
        val result = calculateProgressUseCase(QuizParameter.Level(WordLevel.C2))

        // Then
        assertThat(result.isSuccess).isTrue()
        val progress = result.getOrNull()
        assertThat(progress?.totalWords).isEqualTo(0)
        assertThat(progress?.learnedWords).isEqualTo(0)
        assertThat(progress?.progressPercent).isEqualTo(0)
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.getWordCountByLevel(any()) } throws Exception("Database error")

        // When
        val result = calculateProgressUseCase(QuizParameter.Level(WordLevel.B1))

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Database error")
    }

    @Test
    fun `getDailyStats returns daily correct answers`() = runTest {
        // Given
        coEvery { mockRepository.getDailyCorrectAnswers() } returns 42

        // When
        val result = calculateProgressUseCase.getDailyStats()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(42)
        coVerify { mockRepository.getDailyCorrectAnswers() }
    }

    @Test
    fun `getDailyStats returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.getDailyCorrectAnswers() } throws Exception("Stats unavailable")

        // When
        val result = calculateProgressUseCase.getDailyStats()

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Stats unavailable")
    }

    @Test
    fun `getWeeklyStats returns weekly correct answers`() = runTest {
        // Given
        coEvery { mockRepository.getWeeklyCorrectAnswers() } returns 250

        // When
        val result = calculateProgressUseCase.getWeeklyStats()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(250)
        coVerify { mockRepository.getWeeklyCorrectAnswers() }
    }

    @Test
    fun `getWeeklyStats returns failure when repository throws exception`() = runTest {
        // Given
        coEvery { mockRepository.getWeeklyCorrectAnswers() } throws Exception("Stats unavailable")

        // When
        val result = calculateProgressUseCase.getWeeklyStats()

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Stats unavailable")
    }
}
