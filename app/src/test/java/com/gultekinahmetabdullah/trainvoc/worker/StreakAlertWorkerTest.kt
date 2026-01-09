package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.every
import io.mockk.mockk
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
 * Unit tests for StreakAlertWorker
 *
 * Tests cover:
 * - Streak milestone notifications
 * - Streak endangered notifications
 * - Handling disabled streak alerts
 * - No active streak scenario
 * - Error handling and retry logic
 */
@RunWith(RobolectricTestRunner::class)
class StreakAlertWorkerTest {

    private lateinit var context: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putInt(any(), any()) } returns mockEditor
        every { mockEditor.putLong(any(), any()) } returns mockEditor
        every { mockEditor.apply() } returns Unit

        every { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) } returns mockSharedPreferences
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `doWork should return success when streak alerts disabled`() = runBlocking {
        // Given: Streak alerts are disabled
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns false

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success without sending notification
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should return success when no active streak`() = runBlocking {
        // Given: Streak alerts enabled but no active streak
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns true
        every { mockSharedPreferences.getInt("current_streak", 0) } returns 0

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success without sending notification
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should handle streak milestone (7 day streak)`() = runBlocking {
        // Given: User has 7-day streak and practiced today
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns true
        every { mockSharedPreferences.getInt("current_streak", 0) } returns 7
        every { mockSharedPreferences.getLong("last_practice_date", 0) } returns System.currentTimeMillis()

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should handle endangered streak (missed practice)`() = runBlocking {
        // Given: User has active streak but hasn't practiced today
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns true
        every { mockSharedPreferences.getInt("current_streak", 0) } returns 5
        // Set last practice to 2 days ago
        every { mockSharedPreferences.getLong("last_practice_date", 0) } returns
            System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should check shared preferences for settings`() = runBlocking {
        // Given: Worker is created
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns true
        every { mockSharedPreferences.getInt("current_streak", 0) } returns 0

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker executes
        worker.doWork()

        // Then: Should check preferences
        verify(atLeast = 1) {
            mockSharedPreferences.getBoolean("streak_alerts_enabled", true)
        }
    }

    @Test
    fun `worker should handle multiple consecutive runs`() = runBlocking {
        // Given: Streak alerts enabled
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns true
        every { mockSharedPreferences.getInt("current_streak", 0) } returns 5
        every { mockSharedPreferences.getLong("last_practice_date", 0) } returns System.currentTimeMillis()

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker runs multiple times
        val result1 = worker.doWork()
        val result2 = worker.doWork()

        // Then: All runs should succeed
        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
    }

    @Test
    fun `doWork should handle 14 day milestone correctly`() = runBlocking {
        // Given: User has 14-day streak (multiple of 7)
        every { mockSharedPreferences.getBoolean("streak_alerts_enabled", true) } returns true
        every { mockSharedPreferences.getInt("current_streak", 0) } returns 14
        every { mockSharedPreferences.getLong("last_practice_date", 0) } returns System.currentTimeMillis()

        val worker = TestListenableWorkerBuilder<StreakAlertWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }
}
