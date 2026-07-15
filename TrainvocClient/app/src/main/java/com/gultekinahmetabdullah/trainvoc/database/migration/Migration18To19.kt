package com.gultekinahmetabdullah.trainvoc.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gultekinahmetabdullah.trainvoc.database.ReviewScheduleRow
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.Sm2ToFsrsMigrator

/**
 * Room migration 18 → 19: add the FSRS `review_schedule` table and seed it from
 * the legacy SM-2 progress (SRS engine S1 persistence, issue #99, design doc §5b).
 *
 * Additive and reversible-by-flag: it only CREATEs a new table (+indices) and
 * INSERTs derived rows; no existing table, column, or dictionary content is
 * touched, so a flag-OFF build is byte-identical to today (design doc §8/§12).
 *
 * Seeding is the Kotlin equivalent of the doc's SQL: for every `words` row that
 * carries real SM-2 review history it computes the FSRS state through
 * [Sm2ToFsrsMigrator] — the single source of truth for the approximation, also
 * used as the test oracle — and writes one `review_schedule` row. Words the user
 * never reviewed get no row (design doc §5b: rows are created lazily, not one per
 * dictionary entry); the vast majority of installs (fresh asset, all SM-2 fields
 * at defaults) therefore seed zero rows.
 *
 * Deterministic and idempotent: same inputs → same rows; timestamps come from the
 * injected [now] clock; the seed writes with INSERT OR REPLACE keyed by the
 * `(word_id, user_id)` primary key, so re-running is a no-op.
 *
 * @param now injectable wall clock (epoch-ms) for created_at/updated_at and for
 *   the due date of progressed-but-unscheduled rows; deterministic in tests.
 */
class Migration18To19(
    private val now: () -> Long = System::currentTimeMillis
) : Migration(18, 19) {

    override fun migrate(db: SupportSQLiteDatabase) {
        createReviewScheduleTable(db)
        seedFromSm2(db, now())
    }

    /**
     * DDL mirrors Room's exported schema 19.json exactly (table, column order,
     * index names). Room validates the resulting schema against the entity
     * definitions when it opens the migrated database, so this must not drift.
     */
    private fun createReviewScheduleTable(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `review_schedule` (" +
                "`word_id` INTEGER NOT NULL, `user_id` TEXT NOT NULL, " +
                "`stability` REAL NOT NULL, `difficulty` REAL NOT NULL, " +
                "`due_at` INTEGER NOT NULL, `card_state` TEXT NOT NULL, " +
                "`last_reviewed_at` INTEGER, `reps` INTEGER NOT NULL, " +
                "`lapses` INTEGER NOT NULL, `synced_to_server` INTEGER NOT NULL, " +
                "`created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, " +
                "PRIMARY KEY(`word_id`, `user_id`), " +
                "FOREIGN KEY(`word_id`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_review_schedule_due_at` " +
                "ON `review_schedule` (`user_id`, `due_at`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_review_schedule_synced` " +
                "ON `review_schedule` (`synced_to_server`)"
        )
    }

    /**
     * Seed `review_schedule` from every `words` row with real SM-2 history.
     * A word has history iff it was ever reviewed/scheduled under SM-2 — i.e.
     * `repetitions`/`interval_days` moved off zero or a `next_review_date` was set.
     */
    private fun seedFromSm2(db: SupportSQLiteDatabase, nowMs: Long) {
        val insert = db.compileStatement(
            "INSERT OR REPLACE INTO review_schedule (" +
                "word_id, user_id, stability, difficulty, due_at, card_state, " +
                "last_reviewed_at, reps, lapses, synced_to_server, created_at, updated_at" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )
        db.query(
            "SELECT id, interval_days, easiness_factor, repetitions, " +
                "next_review_date, last_reviewed FROM words " +
                "WHERE repetitions > 0 OR interval_days > 0 OR next_review_date IS NOT NULL"
        ).use { c ->
            while (c.moveToNext()) {
                val wordId = c.getLong(0)
                val intervalDays = c.getInt(1)
                val easeFactor = c.getFloat(2)
                val repetitions = c.getInt(3)
                // Progressed-but-unscheduled rows (no next_review_date) are due now,
                // represented deterministically as epoch 0 (always <= now).
                val nextReviewDate = if (c.isNull(4)) 0L else c.getLong(4)
                val lastReviewed = if (c.isNull(5)) null else c.getLong(5)

                val card = Sm2ToFsrsMigrator.seed(
                    intervalDays = intervalDays,
                    easeFactor = easeFactor,
                    repetitions = repetitions,
                    nextReviewDate = nextReviewDate,
                    lastReviewed = lastReviewed
                )
                val row = ReviewScheduleRow.fromFsrsCard(wordId = wordId, card = card, now = nowMs)

                insert.clearBindings()
                insert.bindLong(1, row.wordId)
                insert.bindString(2, row.userId)
                insert.bindDouble(3, row.stability)
                insert.bindDouble(4, row.difficulty)
                insert.bindLong(5, row.dueAt)
                insert.bindString(6, row.cardState)
                if (row.lastReviewedAt == null) insert.bindNull(7) else insert.bindLong(7, row.lastReviewedAt)
                insert.bindLong(8, row.reps.toLong())
                insert.bindLong(9, row.lapses.toLong())
                insert.bindLong(10, if (row.syncedToServer) 1L else 0L)
                insert.bindLong(11, row.createdAt)
                insert.bindLong(12, row.updatedAt)
                insert.executeInsert()
            }
        }
    }
}
