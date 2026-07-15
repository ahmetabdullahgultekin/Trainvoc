package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.database.ReviewScheduleRow
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.features.repository.FeatureFlagRepository
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState
import com.gultekinahmetabdullah.trainvoc.srs.domain.SrsSchedulerService
import com.gultekinahmetabdullah.trainvoc.test.util.MainDispatcherRule
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import com.gultekinahmetabdullah.trainvoc.testing.TestData
import com.gultekinahmetabdullah.trainvoc.ui.review.ReviewQueueViewModel
import com.gultekinahmetabdullah.trainvoc.ui.review.ReviewUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * [ReviewQueueViewModel] (SRS engine S2) over a real in-memory Room DB + the real
 * [SrsSchedulerService] under Robolectric: due-queue load with the dictionary
 * join (lemma + senses), reveal, each rating persisting the FSRS-advanced card
 * and advancing, the end-of-session summary, skip (no persist), and empty state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ReviewQueueViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var db: AppDatabase
    private lateinit var service: SrsSchedulerService
    private lateinit var flagRepository: FeatureFlagRepository
    private val dispatchers = TestDispatcherProvider()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Pin Room's suspend-query executors to the test dispatcher so DAO calls
        // made inside the ViewModel's viewModelScope resolve on the same scheduler
        // advanceUntilIdle() drives (otherwise Room resumes on its own thread and
        // the state is still Loading when the assertion runs).
        val testExecutor = mainDispatcherRule.testDispatcher.asExecutor()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor(testExecutor)
            .setTransactionExecutor(testExecutor)
            .build()
        flagRepository = FeatureFlagRepository(db.featureFlagDao())
        service = SrsSchedulerService(
            db.reviewScheduleDao(),
            FsrsAlgorithm(),
            FeatureFlagManager(flagRepository)
        )
    }

    @After
    fun tearDown() = db.close()

    private fun createViewModel() =
        ReviewQueueViewModel(service, db.wordDao(), dispatchers)

    private suspend fun enableSrs() =
        flagRepository.setGlobalFeatureEnabled(FeatureFlag.SRS_ENGINE, true)

    private suspend fun seedWord(id: Long, lemma: String, meaning: String) {
        db.wordDao().insertWord(TestData.word(word = lemma, meaning = meaning, id = id))
    }

    private suspend fun seedTurkish(id: Long, lemma: String) {
        db.wordDao().insertWord(TestData.turkishWord(word = lemma, id = id))
    }

    /** Insert a translation edge (word_translations has no DAO insert). */
    private fun link(wordId: Long, translatedId: Long, sense: Int = 0) {
        db.openHelper.writableDatabase.execSQL(
            "INSERT INTO word_translations (word_id, translated_word_id, sense_index, note, is_primary) VALUES (?, ?, ?, ?, ?)",
            arrayOf<Any?>(wordId, translatedId, sense, null, 1)
        )
    }

    /** A past-due REVIEW row so it is due for any wall clock. */
    private suspend fun seedDue(wordId: Long, dueAt: Long) {
        db.reviewScheduleDao().upsert(
            ReviewScheduleRow(
                wordId = wordId,
                stability = 5.0,
                difficulty = 5.0,
                dueAt = dueAt,
                cardState = FsrsState.REVIEW.name,
                lastReviewedAt = dueAt - 1,
                reps = 1,
                lapses = 0,
                createdAt = dueAt,
                updatedAt = dueAt
            )
        )
    }

    @Test
    fun `an empty schedule shows the empty state`() = runTest {
        enableSrs()
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(ReviewUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `due cards load soonest-first with the dictionary join`() = runTest {
        enableSrs()
        seedWord(1L, "apple", "elma")
        seedTurkish(100L, "elma")
        link(wordId = 1L, translatedId = 100L)
        seedWord(2L, "house", "ev")
        seedDue(wordId = 2L, dueAt = 200) // later
        seedDue(wordId = 1L, dueAt = 100) // sooner → shown first

        val vm = createViewModel()
        advanceUntilIdle()

        val active = vm.uiState.value as ReviewUiState.Active
        assertEquals(1L, active.card.wordId)
        assertEquals("apple", active.card.lemma)
        assertEquals("elma", active.card.meaning)
        assertEquals(listOf("elma"), active.card.senses.firstOrNull()?.translations)
        assertEquals(2, active.remaining)
        assertEquals(0, active.reviewedCount)
        assertEquals(false, active.isRevealed)
    }

    @Test
    fun `reveal flips the current card`() = runTest {
        enableSrs()
        seedWord(1L, "apple", "elma")
        seedDue(wordId = 1L, dueAt = 100)

        val vm = createViewModel()
        advanceUntilIdle()
        vm.reveal()

        assertEquals(true, (vm.uiState.value as ReviewUiState.Active).isRevealed)
    }

    @Test
    fun `rating persists the FSRS-advanced card and advances to the next`() = runTest {
        enableSrs()
        seedWord(1L, "apple", "elma")
        seedWord(2L, "house", "ev")
        seedDue(wordId = 1L, dueAt = 100)
        seedDue(wordId = 2L, dueAt = 200)

        val vm = createViewModel()
        advanceUntilIdle()
        vm.rateCard(FsrsRating.GOOD)
        advanceUntilIdle()

        // First card advanced in place (reps 1 → 2, rescheduled into the future).
        val stored = db.reviewScheduleDao().getByWord(1L)!!
        assertEquals(2, stored.reps)
        assertTrue("card must be rescheduled forward", stored.dueAt > 100)

        // Queue advanced to the second card.
        val active = vm.uiState.value as ReviewUiState.Active
        assertEquals(2L, active.card.wordId)
        assertEquals(1, active.reviewedCount)
        assertEquals(1, active.remaining)
    }

    @Test
    fun `rating the last card ends on a summary with the retention rate`() = runTest {
        enableSrs()
        seedWord(1L, "apple", "elma")
        seedWord(2L, "house", "ev")
        seedDue(wordId = 1L, dueAt = 100)
        seedDue(wordId = 2L, dueAt = 200)

        val vm = createViewModel()
        advanceUntilIdle()
        vm.rateCard(FsrsRating.GOOD)  // recalled
        advanceUntilIdle()
        vm.rateCard(FsrsRating.AGAIN) // lapsed
        advanceUntilIdle()

        val summary = (vm.uiState.value as ReviewUiState.Summary).summary
        assertEquals(2, summary.reviewedCount)
        assertEquals(1, summary.recalledCount) // only the Good counts as recalled
        assertEquals(0.5f, summary.retentionRate, 0.001f)
    }

    @Test
    fun `skip advances without persisting a review`() = runTest {
        enableSrs()
        seedWord(1L, "apple", "elma")
        seedWord(2L, "house", "ev")
        seedDue(wordId = 1L, dueAt = 100)
        seedDue(wordId = 2L, dueAt = 200)

        val vm = createViewModel()
        advanceUntilIdle()
        vm.skipCard()
        advanceUntilIdle()

        // Skipped card is untouched (still reps 1, same due date).
        val skipped = db.reviewScheduleDao().getByWord(1L)!!
        assertEquals(1, skipped.reps)
        assertEquals(100L, skipped.dueAt)

        // Advanced to the next card, nothing counted as reviewed.
        val active = vm.uiState.value as ReviewUiState.Active
        assertEquals(2L, active.card.wordId)
        assertEquals(0, active.reviewedCount)
    }
}
