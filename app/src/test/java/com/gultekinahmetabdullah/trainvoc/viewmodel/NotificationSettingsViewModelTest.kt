package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.notification.INotificationHelper
import com.gultekinahmetabdullah.trainvoc.notification.INotificationScheduler
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for NotificationSettingsViewModel
 *
 * Tests cover:
 * - Word quiz enable/disable functionality
 * - Frequency interval changes
 * - Level filter toggling
 * - Exam filter toggling
 * - StateFlow emissions
 * - Interaction with NotificationScheduler
 *
 * Uses dependency injection with mocked dependencies:
 * - MockK for creating test doubles
 * - Turbine for testing StateFlow emissions
 * - Truth for assertions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest : BaseTest() {

    private lateinit var viewModel: NotificationSettingsViewModel
    private lateinit var mockContext: Context
    private lateinit var mockPrefs: NotificationPreferences
    private lateinit var mockScheduler: INotificationScheduler
    private lateinit var mockHelper: INotificationHelper

    @Before
    override fun setup() {
        super.setup()

        // Create mocks
        mockContext = mockk(relaxed = true)
        mockPrefs = mockk(relaxed = true)
        mockScheduler = mockk(relaxed = true)
        mockHelper = mockk(relaxed = true)

        // Setup default mock behaviors for preferences
        every { mockPrefs.wordQuizEnabled } returns false
        every { mockPrefs.wordQuizIntervalMinutes } returns 60
        every { mockPrefs.enabledLevels } returns setOf("A1", "A2")
        every { mockPrefs.enabledExams } returns setOf("YDS")
        every { mockPrefs.includeLearnedWords } returns false
        every { mockPrefs.includeLowAccuracyWords } returns true
        every { mockPrefs.quietHoursEnabled } returns false
        every { mockPrefs.quietHoursStart } returns 22
        every { mockPrefs.quietHoursEnd } returns 8
        every { mockPrefs.dailyRemindersEnabled } returns true
        every { mockPrefs.streakAlertsEnabled } returns true
        every { mockPrefs.wordOfDayEnabled } returns true

        // Create ViewModel with mocked dependencies
        viewModel = NotificationSettingsViewModel(
            context = mockContext,
            prefs = mockPrefs,
            scheduler = mockScheduler,
            notificationHelper = mockHelper
        )
    }

    @Test
    fun `initial state reflects preference values`() {
        // When - ViewModel is created with mocked preferences
        // (already done in setup)

        // Then - StateFlows should reflect initial preference values
        assertThat(viewModel.wordQuizEnabled.value).isFalse()
        assertThat(viewModel.wordQuizInterval.value).isEqualTo(60)
        assertThat(viewModel.enabledLevels.value).isEqualTo(setOf("A1", "A2"))
        assertThat(viewModel.enabledExams.value).isEqualTo(setOf("YDS"))
        assertThat(viewModel.includeLearnedWords.value).isFalse()
        assertThat(viewModel.includeLowAccuracyWords.value).isTrue()
        assertThat(viewModel.quietHoursEnabled.value).isFalse()
        assertThat(viewModel.quietHoursStart.value).isEqualTo(22)
        assertThat(viewModel.quietHoursEnd.value).isEqualTo(8)
        assertThat(viewModel.dailyRemindersEnabled.value).isTrue()
        assertThat(viewModel.streakAlertsEnabled.value).isTrue()
        assertThat(viewModel.wordOfDayEnabled.value).isTrue()
    }

    @Test
    fun `setWordQuizEnabled true updates preference and schedules notifications`() = runTest {
        // Given
        every { mockPrefs.wordQuizEnabled = any() } just Runs

        // When
        viewModel.setWordQuizEnabled(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.wordQuizEnabled = true }
        verify { mockScheduler.scheduleWordQuiz(mockContext) }
        verify(exactly = 0) { mockScheduler.cancelWordQuiz(any()) }
        assertThat(viewModel.wordQuizEnabled.value).isTrue()
    }

    @Test
    fun `setWordQuizEnabled false updates preference and cancels notifications`() = runTest {
        // Given
        every { mockPrefs.wordQuizEnabled = any() } just Runs

        // When
        viewModel.setWordQuizEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.wordQuizEnabled = false }
        verify { mockScheduler.cancelWordQuiz(mockContext) }
        verify(exactly = 0) { mockScheduler.scheduleWordQuiz(any()) }
        assertThat(viewModel.wordQuizEnabled.value).isFalse()
    }

    @Test
    fun `setWordQuizInterval updates preference and reschedules when enabled`() = runTest {
        // Given
        every { mockPrefs.wordQuizEnabled } returns true
        every { mockPrefs.wordQuizIntervalMinutes = any() } just Runs
        val newInterval = 30

        // When
        viewModel.setWordQuizInterval(newInterval)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.wordQuizIntervalMinutes = newInterval }
        verify { mockScheduler.scheduleWordQuiz(mockContext) }
        assertThat(viewModel.wordQuizInterval.value).isEqualTo(30)
    }

    @Test
    fun `setWordQuizInterval updates preference but does not reschedule when disabled`() = runTest {
        // Given
        every { mockPrefs.wordQuizEnabled } returns false
        every { mockPrefs.wordQuizIntervalMinutes = any() } just Runs
        val newInterval = 120

        // When
        viewModel.setWordQuizInterval(newInterval)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.wordQuizIntervalMinutes = newInterval }
        verify(exactly = 0) { mockScheduler.scheduleWordQuiz(any()) }
        assertThat(viewModel.wordQuizInterval.value).isEqualTo(120)
    }

    @Test
    fun `toggleLevel adds level when enabled`() = runTest {
        // Given
        val level = "B1"
        every { mockPrefs.enabledLevels = any() } just Runs

        // When
        viewModel.toggleLevel(level, true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.enabledLevels = match { it.contains(level) && it.contains("A1") && it.contains("A2") } }
        assertThat(viewModel.enabledLevels.value).contains(level)
    }

    @Test
    fun `toggleLevel removes level when disabled`() = runTest {
        // Given
        val level = "A1"  // A1 is in the initial set
        every { mockPrefs.enabledLevels = any() } just Runs

        // When
        viewModel.toggleLevel(level, false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.enabledLevels = match { !it.contains(level) && it.contains("A2") } }
        assertThat(viewModel.enabledLevels.value).doesNotContain(level)
    }

    @Test
    fun `toggleExam adds exam when enabled`() = runTest {
        // Given
        val exam = "TOEFL"
        every { mockPrefs.enabledExams = any() } just Runs

        // When
        viewModel.toggleExam(exam, true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.enabledExams = match { it.contains(exam) && it.contains("YDS") } }
        assertThat(viewModel.enabledExams.value).contains(exam)
    }

    @Test
    fun `toggleExam removes exam when disabled`() = runTest {
        // Given
        val exam = "YDS"  // YDS is in the initial set
        every { mockPrefs.enabledExams = any() } just Runs

        // When
        viewModel.toggleExam(exam, false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.enabledExams = match { !it.contains(exam) } }
        assertThat(viewModel.enabledExams.value).doesNotContain(exam)
    }

    @Test
    fun `setIncludeLearnedWords updates preference and StateFlow`() = runTest {
        // Given
        every { mockPrefs.includeLearnedWords = any() } just Runs

        // When
        viewModel.setIncludeLearnedWords(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.includeLearnedWords = true }
        assertThat(viewModel.includeLearnedWords.value).isTrue()
    }

    @Test
    fun `setIncludeLowAccuracyWords updates preference and StateFlow`() = runTest {
        // Given
        every { mockPrefs.includeLowAccuracyWords = any() } just Runs

        // When
        viewModel.setIncludeLowAccuracyWords(false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.includeLowAccuracyWords = false }
        assertThat(viewModel.includeLowAccuracyWords.value).isFalse()
    }

    @Test
    fun `setQuietHoursEnabled updates preference and StateFlow`() = runTest {
        // Given
        every { mockPrefs.quietHoursEnabled = any() } just Runs

        // When
        viewModel.setQuietHoursEnabled(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.quietHoursEnabled = true }
        assertThat(viewModel.quietHoursEnabled.value).isTrue()
    }

    @Test
    fun `setQuietHoursStart updates preference and StateFlow`() = runTest {
        // Given
        every { mockPrefs.quietHoursStart = any() } just Runs

        // When
        viewModel.setQuietHoursStart(23)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.quietHoursStart = 23 }
        assertThat(viewModel.quietHoursStart.value).isEqualTo(23)
    }

    @Test
    fun `setQuietHoursEnd updates preference and StateFlow`() = runTest {
        // Given
        every { mockPrefs.quietHoursEnd = any() } just Runs

        // When
        viewModel.setQuietHoursEnd(7)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPrefs.quietHoursEnd = 7 }
        assertThat(viewModel.quietHoursEnd.value).isEqualTo(7)
    }

    @Test
    fun `sendTestNotification calls NotificationHelper`() = runTest {
        // When
        viewModel.sendTestNotification()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockHelper.sendWordQuizNotification(mockContext) }
    }

    /**
     * This test demonstrates Turbine for testing StateFlow emissions
     */
    @Test
    fun `wordQuizEnabled StateFlow emits correct values using Turbine`() = runTest {
        // Given
        every { mockPrefs.wordQuizEnabled = any() } just Runs

        // When/Then - Use Turbine to test emissions
        viewModel.wordQuizEnabled.test {
            // Initial value
            assertThat(awaitItem()).isFalse()

            // Enable word quiz
            viewModel.setWordQuizEnabled(true)
            assertThat(awaitItem()).isTrue()

            // Disable word quiz
            viewModel.setWordQuizEnabled(false)
            assertThat(awaitItem()).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wordQuizInterval StateFlow emits correct values using Turbine`() = runTest {
        // Given
        every { mockPrefs.wordQuizIntervalMinutes = any() } just Runs

        // When/Then - Use Turbine to test emissions
        viewModel.wordQuizInterval.test {
            // Initial value
            assertThat(awaitItem()).isEqualTo(60)

            // Update interval
            viewModel.setWordQuizInterval(30)
            assertThat(awaitItem()).isEqualTo(30)

            // Update again
            viewModel.setWordQuizInterval(120)
            assertThat(awaitItem()).isEqualTo(120)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
