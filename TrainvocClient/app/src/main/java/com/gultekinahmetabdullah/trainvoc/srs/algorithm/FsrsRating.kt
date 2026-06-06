package com.gultekinahmetabdullah.trainvoc.srs.algorithm

/**
 * The four FSRS review grades a user can give a card.
 *
 * Replaces SM-2's 0–5 quality scale (see ADR-0001). The integer [value] matches
 * the FSRS reference implementation's 1–4 grade convention.
 *
 * Quiz outcomes map to two of these (design doc §4): a correct answer → [GOOD],
 * an incorrect answer → [AGAIN]. The Review Queue UI exposes all four.
 */
enum class FsrsRating(val value: Int) {
    /** Complete failure to recall (maps from a wrong quiz answer). */
    AGAIN(1),

    /** Recalled, but with serious difficulty. */
    HARD(2),

    /** Recalled correctly with some effort (maps from a correct quiz answer). */
    GOOD(3),

    /** Recalled effortlessly. */
    EASY(4);

    companion object {
        fun fromValue(value: Int): FsrsRating =
            entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Unknown FSRS rating value: $value")

        /** Maps a binary quiz outcome to an FSRS grade (design doc §4). */
        fun fromQuizCorrect(wasCorrect: Boolean): FsrsRating = if (wasCorrect) GOOD else AGAIN
    }
}
