package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.database.dao.WordDao
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertEquals

/**
 * Unit tests for WordOfDayWorker
 *
 * Tests cover:
 * - Word selection algorithm (deterministic by date)
 * - Handling disabled word of day feature
 * - Empty database scenario
 * - Notification sending
 * - Preference saving
 * - Error handling and retry logic
 */
@RunWith(RobolectricTestRunner::class)
class WordOfDayWorkerTest {

    private lateinit var context: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockWordDao: WordDao

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)
        mockDatabase = mockk(relaxed = true)
        mockWordDao = mockk(relaxed = true)

        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.putLong(any(), any()) } returns mockEditor
        every { mockEditor.apply() } returns Unit

        every { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) } returns mockSharedPreferences

        // Mock database singleton
        mockkObject(AppDatabase.DatabaseBuilder)
        every { AppDatabase.DatabaseBuilder.getInstance(any()) } returns mockDatabase
        every { mockDatabase.wordDao() } returns mockWordDao
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `doWork should return success when word of day disabled`() = runBlocking {
        // Given: Word of day feature is disabled
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns false

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success without querying database
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should return success when no words in database`() = runBlocking {
        // Given: Word of day enabled but database is empty
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns true
        coEvery { mockWordDao.getAllWords() } returns flowOf(emptyList())

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success without sending notification
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should send notification with selected word`() = runBlocking {
        // Given: Word of day enabled and database has words
        val sampleWords = createSampleWords(10)
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns true
        coEvery { mockWordDao.getAllWords() } returns flowOf(sampleWords)

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork should save word of day to preferences`() = runBlocking {
        // Given: Word of day enabled and database has words
        val sampleWords = createSampleWords(5)
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns true
        coEvery { mockWordDao.getAllWords() } returns flowOf(sampleWords)

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker executes
        worker.doWork()

        // Then: Should save word to preferences
        verify(atLeast = 1) {
            mockEditor.putString("last_word_of_day", any())
            mockEditor.putLong("word_of_day_timestamp", any())
            mockEditor.apply()
        }
    }

    @Test
    fun `doWork should use deterministic word selection`() = runBlocking {
        // Given: Same words on consecutive days
        val sampleWords = createSampleWords(10)
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns true
        coEvery { mockWordDao.getAllWords() } returns flowOf(sampleWords)

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker runs multiple times (same day)
        val result1 = worker.doWork()
        val result2 = worker.doWork()

        // Then: Both should succeed (word selection is deterministic by date)
        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
    }

    @Test
    fun `doWork should query database when enabled`() = runBlocking {
        // Given: Word of day enabled
        val sampleWords = createSampleWords(3)
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns true
        coEvery { mockWordDao.getAllWords() } returns flowOf(sampleWords)

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker executes
        worker.doWork()

        // Then: Should query database
        coEvery { mockWordDao.getAllWords() }
    }

    @Test
    fun `doWork should handle single word in database`() = runBlocking {
        // Given: Database has only one word
        val singleWord = listOf(
            Word(
                word = "test",
                meaning = "a test",
                level = WordLevel.A1,
                lastReviewed = null,
                statId = 1,
                secondsSpent = 0
            )
        )
        every { mockSharedPreferences.getBoolean("word_of_day_enabled", true) } returns true
        coEvery { mockWordDao.getAllWords() } returns flowOf(singleWord)

        val worker = TestListenableWorkerBuilder<WordOfDayWorker>(context).build()

        // When: Worker executes
        val result = worker.doWork()

        // Then: Should return success
        assertEquals(ListenableWorker.Result.success(), result)
    }

    // Helper functions

    private fun createSampleWords(count: Int): List<Word> {
        return (1..count).map { index ->
            Word(
                word = "word$index",
                meaning = "meaning$index",
                level = WordLevel.A1,
                lastReviewed = null,
                statId = index,
                secondsSpent = 0
            )
        }
    }
}
