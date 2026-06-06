package com.gultekinahmetabdullah.trainvoc.srs

import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.Sm2ToFsrsMigrator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the SM-2 → FSRS seeding approximation used by Room migration V18
 * (design doc §5a). Pure JVM.
 */
class Sm2ToFsrsMigratorTest {

    @Test
    fun `repetitions map to the documented lifecycle state`() {
        assertEquals(FsrsState.NEW, Sm2ToFsrsMigrator.stateFor(0))
        assertEquals(FsrsState.LEARNING, Sm2ToFsrsMigrator.stateFor(1))
        assertEquals(FsrsState.LEARNING, Sm2ToFsrsMigrator.stateFor(2))
        assertEquals(FsrsState.REVIEW, Sm2ToFsrsMigrator.stateFor(3))
        assertEquals(FsrsState.REVIEW, Sm2ToFsrsMigrator.stateFor(25))
    }

    @Test
    fun `negative repetitions are treated as NEW`() {
        assertEquals(FsrsState.NEW, Sm2ToFsrsMigrator.stateFor(-1))
    }

    @Test
    fun `interval seeds stability and never below the floor`() {
        val seeded = Sm2ToFsrsMigrator.seed(
            intervalDays = 14, easeFactor = 2.5f, repetitions = 4,
            nextReviewDate = 1_000L, lastReviewed = 500L
        )
        assertEquals(14.0, seeded.stability, 1e-9)
        assertEquals(FsrsState.REVIEW, seeded.state)
        assertEquals(1_000L, seeded.due)
        assertEquals(500L, seeded.lastReview)
        assertEquals(4, seeded.reps)
    }

    @Test
    fun `zero interval seeds default stability of one`() {
        val seeded = Sm2ToFsrsMigrator.seed(
            intervalDays = 0, easeFactor = 2.5f, repetitions = 0,
            nextReviewDate = 0L, lastReviewed = null
        )
        assertEquals(1.0, seeded.stability, 1e-9)
        assertEquals(FsrsState.NEW, seeded.state)
        assertEquals(null, seeded.lastReview)
    }

    @Test
    fun `hardest ease maps to highest difficulty and easiest to lowest`() {
        val hardest = Sm2ToFsrsMigrator.easeToDifficulty(1.3f)
        val easiest = Sm2ToFsrsMigrator.easeToDifficulty(3.5f)
        assertEquals(10.0, hardest, 1e-9)
        assertEquals(1.0, easiest, 1e-9)
        assertTrue("default ease should sit between extremes",
            Sm2ToFsrsMigrator.easeToDifficulty(2.5f) in 1.0..10.0)
    }

    @Test
    fun `difficulty is clamped for out-of-range ease values`() {
        assertEquals(10.0, Sm2ToFsrsMigrator.easeToDifficulty(0.5f), 1e-9) // below min ease
        assertEquals(1.0, Sm2ToFsrsMigrator.easeToDifficulty(9.0f), 1e-9)  // above max ease
    }
}
