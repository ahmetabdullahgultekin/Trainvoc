package com.gultekinahmetabdullah.trainvoc.srs.algorithm

/**
 * FSRS card lifecycle state (design doc §5a, ADR-0001 state machine).
 *
 * ```
 * NEW ──any rating──▶ LEARNING ──Good/Easy──▶ REVIEW ──Again──▶ RELEARNING
 *                                               ▲                    │
 *                                               └──── Good/Easy ─────┘
 * ```
 */
enum class FsrsState {
    /** Never reviewed. */
    NEW,

    /** Seen, not yet graduated to long-term review. */
    LEARNING,

    /** Graduated; on the long-term forgetting curve. */
    REVIEW,

    /** Lapsed from REVIEW; being relearned. */
    RELEARNING;

    companion object {
        fun fromName(name: String): FsrsState =
            entries.firstOrNull { it.name == name } ?: NEW
    }
}
