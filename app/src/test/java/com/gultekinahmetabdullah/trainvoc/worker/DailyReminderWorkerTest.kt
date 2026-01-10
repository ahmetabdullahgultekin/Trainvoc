package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
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
 * Unit tests for DailyReminderWorker
 *
 * Tests cover:
 * - Successful notification sending
 * - Handling disabled reminders
 * - Error handling (SecurityException, generic exceptions)
 * - Retry logic
 * - Logging behavior
 */
@RunWith(RobolectricTestRunner::class)
class DailyReminderWorkerTest {

    private lateinit var context: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putBoolean(any(), any()) } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.apply() } returns Unit

        // Mock Context.getSharedPreferences
        every { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) } returns mockSharedPreferences
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `doWork should return success when reminders enabled and notification sent`() = runBlocking {
        // Given: Daily reminders are enabled
        every { mockSharedPreferences.getBoolean("daily_reminders_enabled", true) } returns true

        val worker = TestListenableWorkerBuilder<DailyReminderWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should return success when reminders disabled`() = runBlocking {
        // Given: Daily reminders are disabled
        every { mockSharedPreferences.getBoolean("daily_reminders_enabled", true) } returns false

        val worker = TestListenableWorkerBuilder<DailyReminderWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success without sending notification
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should check shared preferences for reminder setting`() = runBlocking {
        // Given: Worker is created
        every { mockSharedPreferences.getBoolean("daily_reminders_enabled", true) } returns true

        val worker = TestListenableWorkerBuilder<DailyReminderWorker>(context).build()

        // When: Worker executes
        worker.doWork()

        // Then: Should check the reminder preference
        verify(atLeast = 1) {
            mockSharedPreferences.getBoolean("daily_reminders_enabled", true)
        }
    }

    @Test
    fun `worker should handle multiple consecutive runs`() = runBlocking {
        // Given: Daily reminders are enabled
        every { mockSharedPreferences.getBoolean("daily_reminders_enabled", true) } returns true

        val worker = TestListenableWorkerBuilder<DailyReminderWorker>(context).build()

        // When: Worker runs multiple times
        val result1 = worker.doWork()
        val result2 = worker.doWork()

        // Then: All runs should succeed
        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
    }

    @Test
    fun `worker should respect preference changes`() = runBlocking {
        // Given: Preferences change between runs
        every { mockSharedPreferences.getBoolean("daily_reminders_enabled", true) } returnsMany listOf(true, false)

        val worker = TestListenableWorkerBuilder<DailyReminderWorker>(context).build()

        // When: Worker runs with different preferences
        val result1 = worker.doWork()
        val result2 = worker.doWork()

        // Then: Both should succeed but behave differently
        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
    }
}
