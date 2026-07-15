package com.gultekinahmetabdullah.trainvoc.srs

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.database.ReviewScheduleDao
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.features.repository.FeatureFlagRepository
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState
import com.gultekinahmetabdullah.trainvoc.srs.domain.SrsSchedulerService
import com.gultekinahmetabdullah.trainvoc.testing.TestData
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
 * [SrsSchedulerService] (SRS engine S3) against a real in-memory Room DB and the
 * real feature-flag stack under Robolectric. Proves the flag gate (a never-seeded
 * flag reads OFF and writes nothing), the quiz-outcome → FSRS-grade mapping, and
 * that a correct answer schedules a longer interval than an incorrect one.
 */
@RunWith(RobolectricTestRunner::class)
class SrsSchedulerServiceTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ReviewScheduleDao
    private lateinit var featureFlags: FeatureFlagManager
    private lateinit var flagRepository: FeatureFlagRepository
    private lateinit var service: SrsSchedulerService

    private val now = 1_000_000_000L
    private val millisPerDay = FsrsAlgorithm.MILLIS_PER_DAY

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.reviewScheduleDao()
        flagRepository = FeatureFlagRepository(db.featureFlagDao())
        featureFlags = FeatureFlagManager(flagRepository)
        service = SrsSchedulerService(dao, FsrsAlgorithm(), featureFlags)
    }

    @After
    fun tearDown() = db.close()

    /** review_schedule.word_id FK-references words(id); seed the parent word. */
    private suspend fun seedWord(id: Long) {
        db.wordDao().insertWord(TestData.word(word = "w$id", meaning = "m$id", id = id))
    }

    private suspend fun enableSrs() =
        flagRepository.setGlobalFeatureEnabled(FeatureFlag.SRS_ENGINE, true)

    @Test
    fun `flag defaults OFF - onQuizAnswer writes nothing and rate returns null`() = runTest {
        seedWord(1L)

        // Flag never seeded → isEnabled() falls back to defaultEnabled (false).
        assertEquals(false, service.isEnabled())

        service.onQuizAnswer(wordId = 1L, wasCorrect = true, now = now)
        assertNull(service.rate(wordId = 1L, rating = FsrsRating.GOOD, now = now))

        assertEquals(0, dao.count())
    }

    @Test
    fun `flag ON - a correct quiz answer lazily creates a REVIEW row`() = runTest {
        seedWord(1L)
        enableSrs()
        assertEquals(true, service.isEnabled())

        service.onQuizAnswer(wordId = 1L, wasCorrect = true, now = now)

        val row = dao.getByWord(1L)!!
        assertEquals(FsrsState.REVIEW.name, row.cardState) // correct → Good → REVIEW
        assertEquals(1, row.reps)
        assertTrue("due date must be scheduled in the future", row.dueAt > now)
    }

    @Test
    fun `flag ON - an incorrect quiz answer creates a LEARNING row due sooner`() = runTest {
        seedWord(1L); seedWord(2L)
        enableSrs()

        service.onQuizAnswer(wordId = 1L, wasCorrect = true, now = now)   // Good
        service.onQuizAnswer(wordId = 2L, wasCorrect = false, now = now)  // Again

        val correct = dao.getByWord(1L)!!
        val incorrect = dao.getByWord(2L)!!

        assertEquals(FsrsState.REVIEW.name, correct.cardState)
        assertEquals(FsrsState.LEARNING.name, incorrect.cardState) // incorrect → Again → LEARNING
        assertTrue(
            "a correct answer must schedule a longer interval than an incorrect one",
            correct.dueAt > incorrect.dueAt
        )
    }

    @Test
    fun `rate advances an existing card in place (reps increment, no duplicate row)`() = runTest {
        seedWord(1L)
        enableSrs()

        val first = service.rate(wordId = 1L, rating = FsrsRating.GOOD, now = now)!!
        assertEquals(1, first.reps)

        // Second review one interval later.
        val second = service.rate(
            wordId = 1L,
            rating = FsrsRating.GOOD,
            now = now + 3 * millisPerDay
        )!!

        assertEquals(2, second.reps)
        assertEquals(1, dao.count()) // upsert-in-place, not a second row
        assertEquals(now, dao.getByWord(1L)!!.createdAt) // creation time preserved
    }

    @Test
    fun `dueCount reflects rows whose due date has passed`() = runTest {
        seedWord(1L)
        enableSrs()

        // Schedule at now → due strictly in the future, so not yet due at `now`.
        service.rate(wordId = 1L, rating = FsrsRating.GOOD, now = now)
        assertEquals(0, service.dueCount(now = now))

        // Far enough ahead, the card is due again.
        assertEquals(1, service.dueCount(now = now + 3650 * millisPerDay))
    }
}
