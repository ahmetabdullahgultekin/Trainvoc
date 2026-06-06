package com.gultekinahmetabdullah.trainvoc.srs

import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsCard
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Unit tests for the FSRS-5 scheduling algorithm (S1 deliverable).
 *
 * Pure JVM — no Android, no Robolectric, no emulator. Assertions cover the
 * published FSRS-5 invariants (forgetting curve, initial stability/difficulty,
 * grade ordering, state machine) plus deterministic reference vectors computed
 * from the universal default parameters.
 */
class FsrsAlgorithmTest {

    private val fsrs = FsrsAlgorithm()
    private val now = 1_700_000_000_000L // fixed clock for determinism
    private val day = FsrsAlgorithm.MILLIS_PER_DAY
    private val w = FsrsAlgorithm.DEFAULT_PARAMETERS

    // ---- Forgetting curve ----

    @Test
    fun `forgetting curve is 1 at elapsed zero`() {
        assertEquals(1.0, fsrs.forgettingCurve(0.0, 10.0), 1e-9)
    }

    @Test
    fun `retrievability is approximately 0_9 when elapsed equals stability`() {
        // By construction the curve passes through 0.9 at t == S.
        val r = fsrs.forgettingCurve(10.0, 10.0)
        assertEquals(0.9, r, 1e-6)
    }

    @Test
    fun `retrievability decreases monotonically with elapsed time`() {
        val s = 20.0
        var prev = 1.0
        for (t in 1..100) {
            val r = fsrs.forgettingCurve(t.toDouble(), s)
            assertTrue("R must be non-increasing", r <= prev + 1e-12)
            assertTrue("R must stay in [0,1]", r in 0.0..1.0)
            prev = r
        }
    }

    // ---- Initial (NEW card) scheduling ----

    @Test
    fun `new card initial stability equals the per-rating weight`() {
        val newCard = FsrsCard.newCard(now)
        assertEquals(w[0], fsrs.schedule(newCard, FsrsRating.AGAIN, now).stability, 1e-9)
        assertEquals(w[1], fsrs.schedule(newCard, FsrsRating.HARD, now).stability, 1e-9)
        assertEquals(w[2], fsrs.schedule(newCard, FsrsRating.GOOD, now).stability, 1e-9)
        assertEquals(w[3], fsrs.schedule(newCard, FsrsRating.EASY, now).stability, 1e-9)
    }

    @Test
    fun `new card initial difficulty matches the FSRS-5 formula and is clamped 1 to 10`() {
        val newCard = FsrsCard.newCard(now)
        for (rating in FsrsRating.entries) {
            val expected = (w[4] - Math.exp(w[5] * (rating.value - 1)) + 1.0)
                .coerceIn(1.0, 10.0)
            val actual = fsrs.schedule(newCard, rating, now).difficulty
            assertEquals("difficulty for $rating", expected, actual, 1e-9)
            assertTrue(actual in 1.0..10.0)
        }
    }

    @Test
    fun `new card transitions to LEARNING on Again or Hard and REVIEW on Good or Easy`() {
        val newCard = FsrsCard.newCard(now)
        assertEquals(FsrsState.LEARNING, fsrs.schedule(newCard, FsrsRating.AGAIN, now).state)
        assertEquals(FsrsState.LEARNING, fsrs.schedule(newCard, FsrsRating.HARD, now).state)
        assertEquals(FsrsState.REVIEW, fsrs.schedule(newCard, FsrsRating.GOOD, now).state)
        assertEquals(FsrsState.REVIEW, fsrs.schedule(newCard, FsrsRating.EASY, now).state)
    }

    @Test
    fun `better grades yield longer or equal intervals on a new card`() {
        val newCard = FsrsCard.newCard(now)
        val again = fsrs.schedule(newCard, FsrsRating.AGAIN, now).scheduledDays
        val hard = fsrs.schedule(newCard, FsrsRating.HARD, now).scheduledDays
        val good = fsrs.schedule(newCard, FsrsRating.GOOD, now).scheduledDays
        val easy = fsrs.schedule(newCard, FsrsRating.EASY, now).scheduledDays
        assertTrue("again <= hard ($again,$hard)", again <= hard)
        assertTrue("hard <= good ($hard,$good)", hard <= good)
        assertTrue("good <= easy ($good,$easy)", good <= easy)
        assertTrue("easy strictly longest", easy > again)
    }

    @Test
    fun `due date equals now plus scheduled interval`() {
        val newCard = FsrsCard.newCard(now)
        val scheduled = fsrs.schedule(newCard, FsrsRating.GOOD, now)
        assertEquals(now + scheduled.scheduledDays * day, scheduled.due)
        assertTrue("interval at least 1 day", scheduled.scheduledDays >= 1)
    }

    // ---- Review-card scheduling ----

    @Test
    fun `correct review grows stability and increments reps`() {
        val newCard = FsrsCard.newCard(now)
        val first = fsrs.schedule(newCard, FsrsRating.GOOD, now)
        // Review again one interval later, recalled correctly.
        val later = now + first.scheduledDays * day
        val second = fsrs.schedule(first, FsrsRating.GOOD, later)

        assertTrue("stability should grow on recall", second.stability > first.stability)
        assertEquals(2, second.reps)
        assertEquals(0, second.lapses)
        assertEquals(FsrsState.REVIEW, second.state)
    }

    @Test
    fun `lapse on a review card sets RELEARNING and increments lapses`() {
        val newCard = FsrsCard.newCard(now)
        val reviewCard = fsrs.schedule(newCard, FsrsRating.EASY, now) // -> REVIEW
        val later = now + reviewCard.scheduledDays * day
        val lapsed = fsrs.schedule(reviewCard, FsrsRating.AGAIN, later)

        assertEquals(FsrsState.RELEARNING, lapsed.state)
        assertEquals(1, lapsed.lapses)
        assertTrue("post-lapse stability stays positive", lapsed.stability > 0.0)
    }

    @Test
    fun `a stable mature card rated Again does not collapse to a one-day interval`() {
        // ADR-0001 core argument vs SM-2: a long-stable card must not crater on one miss.
        val mature = FsrsCard(
            stability = 100.0,
            difficulty = 5.0,
            due = now,
            state = FsrsState.REVIEW,
            lastReview = now - 90 * day,
            reps = 12,
            lapses = 0,
            scheduledDays = 100
        )
        val lapsed = fsrs.schedule(mature, FsrsRating.AGAIN, now)
        // SM-2 would reset interval to 1 day; FSRS keeps meaningful residual stability.
        assertTrue(
            "post-lapse stability should remain well above the trivial floor, was ${lapsed.stability}",
            lapsed.stability > 1.0
        )
    }

    @Test
    fun `difficulty stays within 1 to 10 across a long mixed review history`() {
        var card = FsrsCard.newCard(now)
        var clock = now
        val ratings = listOf(
            FsrsRating.GOOD, FsrsRating.AGAIN, FsrsRating.HARD, FsrsRating.GOOD,
            FsrsRating.EASY, FsrsRating.GOOD, FsrsRating.AGAIN, FsrsRating.GOOD
        )
        repeat(5) {
            for (r in ratings) {
                card = fsrs.schedule(card, r, clock)
                assertTrue("difficulty out of range: ${card.difficulty}", card.difficulty in 1.0..10.0)
                assertTrue("stability must stay positive: ${card.stability}", card.stability > 0.0)
                clock += card.scheduledDays * day
            }
        }
    }

    @Test
    fun `nextIntervalDays is the inverse of the forgetting curve at desired retention`() {
        // With S days of stability and 0.9 desired retention, the interval should be
        // ~S days (since R(S) == 0.9 by construction).
        val s = 25.0
        val interval = fsrs.nextIntervalDays(s)
        val rAtInterval = fsrs.forgettingCurve(interval.toDouble(), s)
        assertTrue(
            "R at scheduled interval should be near 0.9, was $rAtInterval",
            abs(rAtInterval - 0.9) < 0.05
        )
    }

    @Test
    fun `custom desired retention shortens the interval`() {
        val aggressive = FsrsAlgorithm(desiredRetention = 0.97)
        val relaxed = FsrsAlgorithm(desiredRetention = 0.80)
        val s = 50.0
        assertTrue(
            "higher retention target => shorter interval",
            aggressive.nextIntervalDays(s) < relaxed.nextIntervalDays(s)
        )
    }

    @Test
    fun `interval is capped by the configured maximum`() {
        val capped = FsrsAlgorithm(maximumIntervalDays = 30L)
        assertEquals(30L, capped.nextIntervalDays(100_000.0))
    }

    @Test
    fun `rejects wrong-sized parameter vectors`() {
        try {
            FsrsAlgorithm(parameters = DoubleArray(5))
            throw AssertionError("expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("parameters"))
        }
    }
}
