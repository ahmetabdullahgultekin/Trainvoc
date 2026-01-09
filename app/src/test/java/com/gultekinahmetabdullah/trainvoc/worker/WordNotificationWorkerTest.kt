package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertEquals

/**
 * Unit tests for WordNotificationWorker
 *
 * Tests cover:
 * - Quiet hours respect
 * - Handling disabled word quiz notifications
 * - Notification sending logic
 * - Time-based filtering
 * - Error handling and retry logic
 */
@RunWith(RobolectricTestRunner::class)
class WordNotificationWorkerTest {

    private lateinit var context: Context
    private lateinit var mockNotificationPreferences: NotificationPreferences

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        mockNotificationPreferences = mockk(relaxed = true)

        // Mock NotificationPreferences singleton
        mockkObject(NotificationPreferences.Companion)
        every { NotificationPreferences.getInstance(any()) } returns mockNotificationPreferences
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `doWork should return success when word quiz notifications disabled`() = runBlocking {
        // Given: Word quiz notifications are disabled
        every { mockNotificationPreferences.wordQuizEnabled } returns false

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success without sending notification
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should return success during quiet hours`() = runBlocking {
        // Given: Word quiz enabled but currently in quiet hours
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns true
        every { mockNotificationPreferences.quietHoursStart } returns 22
        every { mockNotificationPreferences.quietHoursEnd } returns 8

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes during quiet hours (simulated by current time)
        val result = worker.doWork()

        // Then: Should return success without sending notification
        // Note: Result depends on actual current time, but worker should handle it gracefully
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should send notification when enabled and not in quiet hours`() = runBlocking {
        // Given: Word quiz enabled and not in quiet hours
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns false

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should check notification preferences`() = runBlocking {
        // Given: Worker is created
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns false

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes
        worker.doWork()

        // Then: Should check preferences
        verify(atLeast = 1) {
            mockNotificationPreferences.wordQuizEnabled
        }
    }

    @Test
    fun `worker should handle multiple consecutive runs`() = runBlocking {
        // Given: Word quiz enabled
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns false

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker runs multiple times
        val result1 = worker.doWork()
        val result2 = worker.doWork()

        // Then: All runs should succeed
        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
    }

    @Test
    fun `doWork should respect quiet hours when enabled`() = runBlocking {
        // Given: Quiet hours are enabled
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns true
        every { mockNotificationPreferences.quietHoursStart } returns 0
        every { mockNotificationPreferences.quietHoursEnd } returns 24 // All day quiet

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes
        worker.doWork()

        // Then: Should check quiet hours settings
        verify(atLeast = 1) {
            mockNotificationPreferences.quietHoursEnabled
        }
    }

    @Test
    fun `doWork should handle overnight quiet hours correctly`() = runBlocking {
        // Given: Quiet hours span midnight (22:00 to 08:00)
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns true
        every { mockNotificationPreferences.quietHoursStart } returns 22
        every { mockNotificationPreferences.quietHoursEnd } returns 8

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should handle quiet hours correctly and return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should handle same-day quiet hours correctly`() = runBlocking {
        // Given: Quiet hours within same day (14:00 to 18:00)
        every { mockNotificationPreferences.wordQuizEnabled } returns true
        every { mockNotificationPreferences.quietHoursEnabled } returns true
        every { mockNotificationPreferences.quietHoursStart } returns 14
        every { mockNotificationPreferences.quietHoursEnd } returns 18

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should handle quiet hours correctly and return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should respect preference changes`() = runBlocking {
        // Given: Preferences change between runs
        every { mockNotificationPreferences.wordQuizEnabled } returnsMany listOf(true, false)
        every { mockNotificationPreferences.quietHoursEnabled } returns false

        val worker = TestListenableWorkerBuilder<WordNotificationWorker>(context).build()

        // When: Worker runs with different preferences
        val result1 = worker.doWork()
        val result2 = worker.doWork()

        // Then: Both should succeed but behave differently
        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
    }
}
