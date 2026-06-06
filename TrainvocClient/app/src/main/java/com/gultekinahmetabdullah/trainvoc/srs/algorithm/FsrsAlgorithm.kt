package com.gultekinahmetabdullah.trainvoc.srs.algorithm

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * FSRS-5 (Free Spaced Repetition Scheduler, version 5) scheduling algorithm.
 *
 * Pure Kotlin, no Android dependencies — fully unit-testable on the JVM. This is
 * the S1 deliverable of the SRS engine (see `docs/design/srs-spaced-repetition-engine.md`
 * and `docs/adr/0001-srs-algorithm-fsrs-vs-sm2.md`). It is a mechanical port of the
 * open-spaced-repetition reference (fsrs4anki / py-fsrs), preserving the published
 * default weights and the two-component (stability + difficulty) memory model.
 *
 * The single entry point is [schedule]: `schedule(card, rating, now) -> card`.
 * It is stateless and deterministic given an injected [now] clock, which is what
 * makes the test vectors below reproducible.
 *
 * @param parameters the 19 FSRS-5 model weights (w0..w18). Defaults to the
 * published universal parameters; Phase 8 may substitute per-user optimized weights.
 * @param desiredRetention target probability of recall at review time (default 0.9).
 * @param maximumIntervalDays a hard cap on the scheduled interval.
 */
class FsrsAlgorithm(
    private val parameters: DoubleArray = DEFAULT_PARAMETERS,
    private val desiredRetention: Double = 0.9,
    private val maximumIntervalDays: Long = 36_500L
) {
    init {
        require(parameters.size == DEFAULT_PARAMETERS.size) {
            "FSRS-5 requires ${DEFAULT_PARAMETERS.size} parameters, got ${parameters.size}"
        }
        require(desiredRetention in 0.01..0.99) {
            "desiredRetention must be in (0,1), got $desiredRetention"
        }
    }

    /**
     * Applies a [rating] to [card] at wall-clock [now] (epoch ms) and returns the
     * updated card with new stability, difficulty, state, and due date.
     */
    fun schedule(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        return if (card.state == FsrsState.NEW || card.lastReview == null) {
            scheduleNew(card, rating, now)
        } else {
            scheduleExisting(card, rating, now)
        }
    }

    private fun scheduleNew(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        val difficulty = initDifficulty(rating)
        val stability = initStability(rating)
        val nextState = if (rating == FsrsRating.AGAIN || rating == FsrsRating.HARD) {
            FsrsState.LEARNING
        } else {
            FsrsState.REVIEW
        }
        return commit(card, rating, stability, difficulty, nextState, now, lapsed = false)
    }

    private fun scheduleExisting(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        val elapsedDays = max(0.0, (now - (card.lastReview ?: now)) / MILLIS_PER_DAY.toDouble())
        val retrievability = forgettingCurve(elapsedDays, card.stability)

        val newDifficulty = nextDifficulty(card.difficulty, rating)

        val lapsed = rating == FsrsRating.AGAIN
        val newStability = if (lapsed) {
            nextForgetStability(newDifficulty, card.stability, retrievability)
        } else {
            nextRecallStability(newDifficulty, card.stability, retrievability, rating)
        }

        val nextState = when {
            lapsed -> FsrsState.RELEARNING
            else -> FsrsState.REVIEW
        }
        return commit(card, rating, newStability, newDifficulty, nextState, now, lapsed)
    }

    private fun commit(
        card: FsrsCard,
        rating: FsrsRating,
        stability: Double,
        difficulty: Double,
        nextState: FsrsState,
        now: Long,
        lapsed: Boolean
    ): FsrsCard {
        val clampedStability = max(stability, MIN_STABILITY)
        val intervalDays = nextIntervalDays(clampedStability)
        val due = now + intervalDays * MILLIS_PER_DAY
        return card.copy(
            stability = clampedStability,
            difficulty = clampDifficulty(difficulty),
            state = nextState,
            due = due,
            lastReview = now,
            reps = card.reps + 1,
            lapses = if (lapsed) card.lapses + 1 else card.lapses,
            scheduledDays = intervalDays
        )
    }

    /**
     * Retrievability (probability of recall) [t] days after the last review, given
     * memory [stability]. FSRS-5 forgetting curve: `(1 + FACTOR * t / S) ^ DECAY`.
     */
    fun forgettingCurve(elapsedDays: Double, stability: Double): Double {
        if (stability <= 0.0) return 0.0
        return (1.0 + FACTOR * elapsedDays / stability).pow(DECAY)
    }

    /**
     * The next interval, in whole days, at which retrievability falls to
     * [desiredRetention]. Inverse of the forgetting curve, clamped to [1, max].
     */
    fun nextIntervalDays(stability: Double): Long {
        val raw = (stability / FACTOR) * (desiredRetention.pow(1.0 / DECAY) - 1.0)
        return raw.roundToLong().coerceIn(1L, maximumIntervalDays)
    }

    // ---- FSRS-5 component formulas (w-indices match the reference order) ----

    private fun initStability(rating: FsrsRating): Double =
        max(parameters[rating.value - 1], MIN_STABILITY)

    private fun initDifficulty(rating: FsrsRating): Double =
        clampDifficulty(parameters[4] - exp(parameters[5] * (rating.value - 1)) + 1.0)

    private fun nextDifficulty(difficulty: Double, rating: FsrsRating): Double {
        // Linear damping toward the easy anchor + mean reversion (FSRS-5).
        val deltaD = -parameters[6] * (rating.value - 3)
        val damped = difficulty + linearDamping(deltaD, difficulty)
        val anchor = initDifficulty(FsrsRating.EASY)
        return clampDifficulty(parameters[7] * anchor + (1.0 - parameters[7]) * damped)
    }

    private fun linearDamping(deltaD: Double, difficulty: Double): Double =
        deltaD * (10.0 - difficulty) / 9.0

    private fun nextRecallStability(
        difficulty: Double,
        stability: Double,
        retrievability: Double,
        rating: FsrsRating
    ): Double {
        val hardPenalty = if (rating == FsrsRating.HARD) parameters[15] else 1.0
        val easyBonus = if (rating == FsrsRating.EASY) parameters[16] else 1.0
        return stability * (
            1.0 + exp(parameters[8]) *
                (11.0 - difficulty) *
                stability.pow(-parameters[9]) *
                (exp((1.0 - retrievability) * parameters[10]) - 1.0) *
                hardPenalty *
                easyBonus
            )
    }

    private fun nextForgetStability(
        difficulty: Double,
        stability: Double,
        retrievability: Double
    ): Double {
        return parameters[11] *
            difficulty.pow(-parameters[12]) *
            ((stability + 1.0).pow(parameters[13]) - 1.0) *
            exp((1.0 - retrievability) * parameters[14])
    }

    private fun clampDifficulty(d: Double): Double = min(max(d, 1.0), 10.0)

    companion object {
        /** Days in milliseconds. */
        const val MILLIS_PER_DAY: Long = 86_400_000L

        /** FSRS-5 forgetting-curve decay constant. */
        const val DECAY: Double = -0.5

        /** FSRS-5 forgetting-curve factor: 0.9^(1/DECAY) - 1. */
        val FACTOR: Double = 0.9.pow(1.0 / DECAY) - 1.0

        /** Stability floor to avoid degenerate (zero/negative) memory. */
        const val MIN_STABILITY: Double = 0.01

        /**
         * Published universal FSRS-5 default parameters (w0..w18), as shipped by
         * open-spaced-repetition. These are calibrated on the Anki review dataset
         * and usable immediately (design doc §11 R1; ADR-0001).
         */
        val DEFAULT_PARAMETERS: DoubleArray = doubleArrayOf(
            0.40255, 1.18385, 3.173, 15.69105,   // w0..w3  initial stability per rating
            7.1949, 0.5345, 1.4604, 0.0046,      // w4..w7  difficulty init + update
            1.54575, 0.1192, 1.01925,            // w8..w10 recall stability
            1.9395, 0.11, 0.29605, 2.2698,       // w11..w14 forget stability
            0.2315, 2.9898, 0.51655, 0.6621      // w15..w18 hard/easy + short-term
        )
    }
}
