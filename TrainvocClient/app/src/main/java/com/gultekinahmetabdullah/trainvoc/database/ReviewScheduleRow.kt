package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsCard
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState

/**
 * Persisted FSRS scheduler state, one row per (word, user) — the S1
 * persistence slice of the SRS engine (see
 * `docs/design/srs-spaced-repetition-engine.md` §5b and issue #99).
 *
 * This is the durable mirror of the pure-Kotlin [FsrsCard] value object plus
 * the sync/bookkeeping columns the design doc's `review_schedule` table adds.
 * The FSRS math never touches Room — it operates on [FsrsCard]; this row is
 * the load/store boundary via [toFsrsCard]/[fromFsrsCard].
 *
 * Keyed by `word_id` (= `words.id`, schema v18's permanent numeric id) with a
 * foreign key into `words`; `user_id` is retained (default `local_user`) so the
 * same table carries the S4 cross-device sync design without another migration.
 *
 * Column-domain note: the shipped [com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm]
 * models `difficulty` on a 1.0–10.0 scale (not the 0.0–1.0 the design doc drafted
 * before the engine existed), and Room does not emit SQL `DEFAULT` clauses from
 * Kotlin property defaults — every row is written with explicit engine-computed or
 * migrator-seeded values, so the doc's literal `DEFAULT 1.0/0.3` are not persisted.
 *
 * @property wordId         FK into `words(id)`; the scheduling subject.
 * @property userId         owner; `local_user` for the offline-first single user.
 * @property stability      estimated days until retrievability drops to 90%.
 * @property difficulty     intrinsic hardness in the FSRS internal range 1.0–10.0.
 * @property dueAt          epoch-ms of the next scheduled review (rows with
 *                          `dueAt <= now` are due).
 * @property cardState      [FsrsState] name: NEW / LEARNING / REVIEW / RELEARNING.
 * @property lastReviewedAt epoch-ms of the previous review, or null if never reviewed.
 * @property reps           total number of reviews.
 * @property lapses         times the card lapsed (rated Again while in REVIEW).
 * @property syncedToServer false = dirty (pending S4 push), true = synced.
 * @property createdAt      epoch-ms the row was first written.
 * @property updatedAt      epoch-ms the row was last written.
 */
@Entity(
    tableName = "review_schedule",
    primaryKeys = ["word_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        // The primary scheduling scan: due rows for a user, ordered by dueAt.
        Index(value = ["user_id", "due_at"], name = "index_review_schedule_due_at"),
        // Cheap lookup of dirty rows for the S4 sync service.
        Index(value = ["synced_to_server"], name = "index_review_schedule_synced")
    ]
)
data class ReviewScheduleRow(
    @ColumnInfo(name = "word_id") val wordId: Long,
    @ColumnInfo(name = "user_id") val userId: String = DEFAULT_USER_ID,
    @ColumnInfo(name = "stability") val stability: Double = 0.0,
    @ColumnInfo(name = "difficulty") val difficulty: Double = 0.0,
    @ColumnInfo(name = "due_at") val dueAt: Long = 0L,
    @ColumnInfo(name = "card_state") val cardState: String = FsrsState.NEW.name,
    @ColumnInfo(name = "last_reviewed_at") val lastReviewedAt: Long? = null,
    @ColumnInfo(name = "reps") val reps: Int = 0,
    @ColumnInfo(name = "lapses") val lapses: Int = 0,
    @ColumnInfo(name = "synced_to_server") val syncedToServer: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = 0L,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = 0L
) {
    /** Reconstruct the pure FSRS state so the algorithm can reschedule this card. */
    fun toFsrsCard(): FsrsCard = FsrsCard(
        stability = stability,
        difficulty = difficulty,
        due = dueAt,
        state = FsrsState.fromName(cardState),
        lastReview = lastReviewedAt,
        reps = reps,
        lapses = lapses
    )

    companion object {
        const val DEFAULT_USER_ID = "local_user"

        /**
         * Build a persistable row from an [FsrsCard]. A row seeded/created for
         * the first time is dirty (`syncedToServer = false`) so S4 will push it;
         * pass [createdAt] to preserve the original creation time on updates.
         */
        fun fromFsrsCard(
            wordId: Long,
            card: FsrsCard,
            now: Long,
            userId: String = DEFAULT_USER_ID,
            createdAt: Long = now,
            syncedToServer: Boolean = false
        ): ReviewScheduleRow = ReviewScheduleRow(
            wordId = wordId,
            userId = userId,
            stability = card.stability,
            difficulty = card.difficulty,
            dueAt = card.due,
            cardState = card.state.name,
            lastReviewedAt = card.lastReview,
            reps = card.reps,
            lapses = card.lapses,
            syncedToServer = syncedToServer,
            createdAt = createdAt,
            updatedAt = now
        )
    }
}
