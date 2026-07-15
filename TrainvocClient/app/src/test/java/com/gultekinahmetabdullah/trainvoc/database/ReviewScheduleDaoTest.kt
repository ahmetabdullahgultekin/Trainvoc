package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState
import com.gultekinahmetabdullah.trainvoc.testing.TestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * ReviewScheduleDao (SRS engine S1) against a real in-memory Room database under
 * Robolectric: upsert-replace semantics, due filtering/ordering, next-due,
 * by-word lookup, and the dirty-row sync surface.
 *
 * Rows FK into `words(id)`, so each schedule row's parent word is inserted first.
 */
@RunWith(RobolectricTestRunner::class)
class ReviewScheduleDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ReviewScheduleDao

    private val now = 1_000_000L

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.reviewScheduleDao()
    }

    @After
    fun tearDown() = db.close()

    /** Insert the parent word so the review_schedule FK is satisfied. */
    private suspend fun seedWord(id: Long) {
        db.wordDao().insertWord(TestData.word(word = "w$id", meaning = "m$id", id = id))
    }

    private fun row(
        wordId: Long,
        dueAt: Long,
        state: FsrsState = FsrsState.REVIEW,
        synced: Boolean = false,
    ) = ReviewScheduleRow(
        wordId = wordId,
        stability = 5.0,
        difficulty = 5.0,
        dueAt = dueAt,
        cardState = state.name,
        lastReviewedAt = dueAt - 1,
        reps = 1,
        lapses = 0,
        syncedToServer = synced,
        createdAt = now,
        updatedAt = now,
    )

    @Test
    fun `upsert inserts then replaces on the same word_id user_id key`() = runTest {
        seedWord(1L)

        dao.upsert(row(1L, dueAt = now - 10))
        assertEquals(1, dao.count())

        // Same (word_id, user_id) -> update in place, not a second row.
        dao.upsert(row(1L, dueAt = now + 999).copy(cardState = FsrsState.LEARNING.name))

        assertEquals(1, dao.count())
        val stored = dao.getByWord(1L)!!
        assertEquals(now + 999, stored.dueAt)
        assertEquals(FsrsState.LEARNING.name, stored.cardState)
    }

    @Test
    fun `getByWord returns null for an unscheduled word`() = runTest {
        assertNull(dao.getByWord(42L))
    }

    @Test
    fun `due queries count and return only rows due at or before now`() = runTest {
        seedWord(1L); seedWord(2L); seedWord(3L)
        dao.upsert(row(1L, dueAt = now - 100)) // due (past)
        dao.upsert(row(2L, dueAt = now))       // due (exactly now)
        dao.upsert(row(3L, dueAt = now + 100)) // not due (future)

        assertEquals(2, dao.getDueCount(now))
        assertEquals(2, dao.getDueCountFlow(now).first())
        assertEquals(listOf(1L, 2L), dao.getDueFlow(now).first().map { it.wordId })
    }

    @Test
    fun `getDueFlow orders by dueAt ascending and honors the limit`() = runTest {
        seedWord(1L); seedWord(2L); seedWord(3L)
        dao.upsert(row(1L, dueAt = now - 10))
        dao.upsert(row(2L, dueAt = now - 30))
        dao.upsert(row(3L, dueAt = now - 20))

        assertEquals(listOf(2L, 3L, 1L), dao.getDueFlow(now).first().map { it.wordId })
        assertEquals(listOf(2L, 3L), dao.getDueFlow(now, limit = 2).first().map { it.wordId })
    }

    @Test
    fun `getNextDueAt returns the earliest due date or null when empty`() = runTest {
        assertNull(dao.getNextDueAt())

        seedWord(1L); seedWord(2L)
        dao.upsert(row(1L, dueAt = now + 500))
        dao.upsert(row(2L, dueAt = now + 200))

        assertEquals(now + 200, dao.getNextDueAt())
    }

    @Test
    fun `dirty rows are pending until markSynced flips them`() = runTest {
        seedWord(1L); seedWord(2L); seedWord(3L)
        dao.upsert(row(1L, dueAt = now, synced = false))
        dao.upsert(row(2L, dueAt = now, synced = false))
        dao.upsert(row(3L, dueAt = now, synced = true))

        assertEquals(setOf(1L, 2L), dao.getDirtyRows().map { it.wordId }.toSet())

        dao.markSynced(listOf(1L))

        assertEquals(setOf(2L), dao.getDirtyRows().map { it.wordId }.toSet())
    }

    @Test
    fun `queries are scoped by user_id`() = runTest {
        seedWord(1L)
        dao.upsert(row(1L, dueAt = now).copy(userId = "other_user"))

        assertEquals(0, dao.getDueCount(now)) // default local_user has no rows
        assertEquals(1, dao.getDueCount(now, userId = "other_user"))
        assertTrue(dao.getDirtyRows().isEmpty())
    }
}
