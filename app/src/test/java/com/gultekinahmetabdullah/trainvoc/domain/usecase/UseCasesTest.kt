package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
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
 * Uses dependency injection with mocked services:
 * - MockK for creating test doubles
 * - coEvery/coVerify for suspend functions
 * - Truth for assertions
 * - Result<T> pattern verification
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UseCasesTest : BaseTest() {

    // Mock services
    private lateinit var mockWordStatisticsService: IWordStatisticsService
    private lateinit var mockQuizService: IQuizService
    private lateinit var mockProgressService: IProgressService
    private lateinit var mockAnalyticsService: IAnalyticsService

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

        // Create mocks
        mockWordStatisticsService = mockk(relaxed = true)
        mockQuizService = mockk(relaxed = true)
        mockProgressService = mockk(relaxed = true)
        mockAnalyticsService = mockk(relaxed = true)

        // Create use cases with mocked services
        updateWordStatisticsUseCase = UpdateWordStatisticsUseCase(mockWordStatisticsService)
        generateQuizQuestionsUseCase = GenerateQuizQuestionsUseCase(mockQuizService)
        checkLevelUnlockedUseCase = CheckLevelUnlockedUseCase(mockProgressService)
        calculateProgressUseCase = CalculateProgressUseCase(mockProgressService, mockAnalyticsService)
    }

    // ========== UpdateWordStatisticsUseCase Tests ==========

    @Test
    fun `onCorrectAnswer increments correct count and updates last answered`() = runTest {
        // Given
        coEvery { mockWordStatisticsService.getWordStats(testWord) } returns testStatistic
        coEvery { mockWordStatisticsService.updateWordStats(any(), any()) } just Runs
        coEvery { mockWordStatisticsService.updateLastAnswered(any()) } just Runs

        // When
        val result = updateWordStatisticsUseCase.onCorrectAnswer(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify {
            mockWordStatisticsService.updateWordStats(
                match { it.correctCount == testStatistic.correctCount + 1 },
                testWord
            )
        }
        coVerify { mockWordStatisticsService.updateLastAnswered(testWord.word) }
    }

    @Test
    fun `onCorrectAnswer returns failure when service throws exception`() = runTest {
        // Given
        coEvery { mockWordStatisticsService.getWordStats(testWord) } throws Exception("Database error")

        // When
        val result = updateWordStatisticsUseCase.onCorrectAnswer(testWord)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isNotNull()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Database error")
    }

    @Test
    fun `onWrongAnswer increments wrong count`() = runTest {
        // Given
        coEvery { mockWordStatisticsService.getWordStats(testWord) } returns testStatistic
        coEvery { mockWordStatisticsService.updateWordStats(any(), any()) } just Runs
        coEvery { mockWordStatisticsService.updateLastAnswered(any()) } just Runs

        // When
        val result = updateWordStatisticsUseCase.onWrongAnswer(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify {
            mockWordStatisticsService.updateWordStats(
                match { it.wrongCount == testStatistic.wrongCount + 1 },
                testWord
            )
        }
    }

    @Test
    fun `onSkippedAnswer increments skipped count`() = runTest {
        // Given
        coEvery { mockWordStatisticsService.getWordStats(testWord) } returns testStatistic
        coEvery { mockWordStatisticsService.updateWordStats(any(), any()) } just Runs
        coEvery { mockWordStatisticsService.updateLastAnswered(any()) } just Runs

        // When
        val result = updateWordStatisticsUseCase.onSkippedAnswer(testWord)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify {
            mockWordStatisticsService.updateWordStats(
                match { it.skippedCount == testStatistic.skippedCount + 1 },
                testWord
            )
        }
    }

    // ========== GenerateQuizQuestionsUseCase Tests ==========

    @Test
    fun `invoke returns questions when quiz service succeeds`() = runTest {
        // Given
        val incorrectWord1 = testWord.copy(word = "goodbye", meaning = "güle güle")
        val incorrectWord2 = testWord.copy(word = "thanks", meaning = "teşekkürler")
        val incorrectWord3 = testWord.copy(word = "yes", meaning = "evet")
        val testQuestions = mutableListOf(
            Question(
                correctWord = testWord,
                incorrectWords = listOf(incorrectWord1, incorrectWord2, incorrectWord3)
            )
        )
        coEvery {
            mockQuizService.generateTenQuestions(testQuiz.type, any())
        } returns testQuestions

        // When
        val result = generateQuizQuestionsUseCase(
            QuizParameter.Level(WordLevel.A1),
            testQuiz
        )

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `invoke returns failure when no questions generated`() = runTest {
        // Given
        coEvery {
            mockQuizService.generateTenQuestions(testQuiz.type, any())
        } returns mutableListOf()

        // When
        val result = generateQuizQuestionsUseCase(
            QuizParameter.Level(WordLevel.A1),
            testQuiz
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("No questions")
    }

    @Test
    fun `invoke returns failure when quiz service throws exception`() = runTest {
        // Given
        coEvery {
            mockQuizService.generateTenQuestions(any(), any())
        } throws Exception("Service error")

        // When
        val result = generateQuizQuestionsUseCase(
            QuizParameter.Level(WordLevel.A1),
            testQuiz
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Service error")
    }

    // ========== CheckLevelUnlockedUseCase Tests ==========

    @Test
    fun `invoke returns true for A1 level always`() = runTest {
        // When
        val result = checkLevelUnlockedUseCase(WordLevel.A1)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `invoke returns true for A2 when A1 is completed`() = runTest {
        // Given
        coEvery { mockProgressService.isLevelUnlocked(WordLevel.A1) } returns true

        // When
        val result = checkLevelUnlockedUseCase(WordLevel.A2)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `invoke returns false for A2 when A1 is not completed`() = runTest {
        // Given
        coEvery { mockProgressService.isLevelUnlocked(WordLevel.A1) } returns false

        // When
        val result = checkLevelUnlockedUseCase(WordLevel.A2)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }

    // ========== CalculateProgressUseCase Tests ==========

    @Test
    fun `invoke calculates correct progress percentage for level`() = runTest {
        // Given
        val parameter = QuizParameter.Level(WordLevel.A1)
        coEvery { mockProgressService.getWordCountByLevel("A1") } returns 100
        coEvery { mockProgressService.getLearnedWordCount("A1") } returns 50

        // When
        val result = calculateProgressUseCase(parameter)

        // Then
        assertThat(result.isSuccess).isTrue()
        val progressInfo = result.getOrNull()!!
        assertThat(progressInfo.totalWords).isEqualTo(100)
        assertThat(progressInfo.learnedWords).isEqualTo(50)
        assertThat(progressInfo.progressPercent).isEqualTo(50)
    }

    @Test
    fun `invoke returns zero progress when no words exist`() = runTest {
        // Given
        val parameter = QuizParameter.Level(WordLevel.C2)
        coEvery { mockProgressService.getWordCountByLevel("C2") } returns 0
        coEvery { mockProgressService.getLearnedWordCount("C2") } returns 0

        // When
        val result = calculateProgressUseCase(parameter)

        // Then
        assertThat(result.isSuccess).isTrue()
        val progressInfo = result.getOrNull()!!
        assertThat(progressInfo.progressPercent).isEqualTo(0)
    }

    @Test
    fun `getDailyStats returns daily correct answers`() = runTest {
        // Given
        coEvery { mockAnalyticsService.getDailyCorrectAnswers() } returns 15

        // When
        val result = calculateProgressUseCase.getDailyStats()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(15)
    }

    @Test
    fun `getWeeklyStats returns weekly correct answers`() = runTest {
        // Given
        coEvery { mockAnalyticsService.getWeeklyCorrectAnswers() } returns 75

        // When
        val result = calculateProgressUseCase.getWeeklyStats()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(75)
    }
}
