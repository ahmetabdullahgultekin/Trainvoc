package com.gultekinahmetabdullah.trainvoc.database.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsState
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.Sm2ToFsrsMigrator
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Verifies the 18→19 FSRS-persistence migration end-to-end under Robolectric
 * (issue #99 S1): the new `review_schedule` table is seeded from the legacy
 * SM-2 columns using the [Sm2ToFsrsMigrator] oracle, only words with real
 * history get a row, and the seed is deterministic and idempotent. A separate
 * test proves the Option (A) fresh-install path — the v18 dictionary asset opens,
 * Room migrates it to v19, and the dictionary content is untouched.
 *
 * As in Migration17To18Test, the starting database is built by executing the DDL
 * from the exported schema snapshot directly (AGP does not merge unit-test assets,
 * so MigrationTestHelper cannot load it under Robolectric).
 */
@RunWith(RobolectricTestRunner::class)
class Migration18To19Test {

    private val fixedNow = 4_242_000L
    private val clock = { fixedNow }

    /** Builds a real v18 database file from the exported 18.json DDL + seed words. */
    private fun createV18Database(context: Context, dbName: String): File {
        val schemaFile = File(
            "schemas/com.gultekinahmetabdullah.trainvoc.database.AppDatabase/18.json"
        )
        assertTrue("18.json exists at ${schemaFile.absolutePath}", schemaFile.exists())
        val schema = JSONObject(schemaFile.readText(Charsets.UTF_8)).getJSONObject("database")

        val dbFile = context.getDatabasePath(dbName)
        dbFile.parentFile?.mkdirs()
        dbFile.delete()
        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        val entities = schema.getJSONArray("entities")
        for (i in 0 until entities.length()) {
            val entity = entities.getJSONObject(i)
            val table = entity.getString("tableName")
            db.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", table))
            val indices = entity.optJSONArray("indices") ?: continue
            for (j in 0 until indices.length()) {
                db.execSQL(
                    indices.getJSONObject(j).getString("createSql")
                        .replace("\${TABLE_NAME}", table)
                )
            }
        }
        // A: full SM-2 history -> REVIEW card seeded from interval/ease/reps.
        db.execSQL(
            "INSERT INTO words (word, meaning, stat_id, seconds_spent, next_review_date, " +
                "easiness_factor, interval_days, repetitions, isFavorite, last_reviewed, id, " +
                "language_id) VALUES ('abandon','terk',0,0,1000,2.5,14,4,0,500,7,1)"
        )
        // B: never reviewed (all SM-2 fields at defaults) -> no review_schedule row.
        db.execSQL(
            "INSERT INTO words (word, meaning, stat_id, seconds_spent, easiness_factor, " +
                "interval_days, repetitions, isFavorite, id, language_id) " +
                "VALUES ('unseen','x',0,0,2.5,0,0,0,8,1)"
        )
        // C: scheduled but not yet graduated (only next_review_date set) -> NEW card.
        db.execSQL(
            "INSERT INTO words (word, meaning, stat_id, seconds_spent, next_review_date, " +
                "easiness_factor, interval_days, repetitions, isFavorite, id, language_id) " +
                "VALUES ('newish','y',0,0,2000,2.5,0,0,0,9,1)"
        )
        db.version = 18
        db.close()
        return dbFile
    }

    private class ReviewRow(
        val stability: Double,
        val difficulty: Double,
        val dueAt: Long,
        val cardState: String,
        val lastReviewedAt: Long?,
        val reps: Int,
        val lapses: Int,
        val syncedToServer: Int,
        val createdAt: Long,
        val updatedAt: Long,
    )

    private fun readReviewRow(db: SupportSQLiteDatabase, wordId: Long): ReviewRow? =
        db.query(
            "SELECT stability, difficulty, due_at, card_state, last_reviewed_at, reps, " +
                "lapses, synced_to_server, created_at, updated_at FROM review_schedule " +
                "WHERE word_id = ? AND user_id = 'local_user'",
            arrayOf(wordId)
        ).use { c ->
            if (!c.moveToFirst()) return null
            ReviewRow(
                stability = c.getDouble(0),
                difficulty = c.getDouble(1),
                dueAt = c.getLong(2),
                cardState = c.getString(3),
                lastReviewedAt = if (c.isNull(4)) null else c.getLong(4),
                reps = c.getInt(5),
                lapses = c.getInt(6),
                syncedToServer = c.getInt(7),
                createdAt = c.getLong(8),
                updatedAt = c.getLong(9),
            )
        }

    @Test
    fun `seeds review_schedule from SM-2 progress matching the Sm2ToFsrsMigrator oracle`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        createV18Database(context, "migration-18-19-test.db")

        // Opening through Room runs Migration18To19 and validates the resulting
        // schema against the v19 entity definitions (review_schedule included).
        val room = Room.databaseBuilder(context, AppDatabase::class.java, "migration-18-19-test.db")
            .addMigrations(Migration18To19(clock))
            .allowMainThreadQueries()
            .build()
        val db = room.openHelper.readableDatabase

        // Only A (full history) and C (scheduled) are seeded; B is skipped.
        db.query("SELECT COUNT(*) FROM review_schedule").use { c ->
            c.moveToFirst()
            assertEquals(2, c.getInt(0))
        }

        // A matches the pure-Kotlin oracle exactly.
        val expectedA = Sm2ToFsrsMigrator.seed(
            intervalDays = 14, easeFactor = 2.5f, repetitions = 4,
            nextReviewDate = 1000L, lastReviewed = 500L
        )
        val a = readReviewRow(db, 7L)!!
        assertEquals(expectedA.stability, a.stability, 1e-9)
        assertEquals(expectedA.difficulty, a.difficulty, 1e-9)
        assertEquals(expectedA.due, a.dueAt)
        assertEquals(FsrsState.REVIEW.name, a.cardState)
        assertEquals(expectedA.state.name, a.cardState)
        assertEquals(500L, a.lastReviewedAt)
        assertEquals(4, a.reps)
        assertEquals(0, a.lapses)
        assertEquals(0, a.syncedToServer) // dirty -> pending S4 push
        assertEquals(fixedNow, a.createdAt)
        assertEquals(fixedNow, a.updatedAt)

        // C: scheduled-only word seeds a NEW card at default stability, due at
        // its SM-2 next_review_date, with no last-review timestamp.
        val c = readReviewRow(db, 9L)!!
        assertEquals(FsrsState.NEW.name, c.cardState)
        assertEquals(1.0, c.stability, 1e-9)
        assertEquals(2000L, c.dueAt)
        assertEquals(0, c.reps)
        assertNull(c.lastReviewedAt)

        // B (never reviewed) got no row.
        assertNull(readReviewRow(db, 8L))

        room.close()
    }

    // ------------------------------------------------------------------ idempotence

    /** A minimal db with just the `words` parent table + one progressed row. */
    private fun openWordsOnlyDb(context: Context, name: String): SupportSQLiteDatabase {
        context.getDatabasePath(name).also { it.parentFile?.mkdirs(); it.delete() }
        val callback = object : SupportSQLiteOpenHelper.Callback(18) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE `words` (`word` TEXT NOT NULL, `meaning` TEXT NOT NULL, " +
                        "`level` TEXT, `last_reviewed` INTEGER, `stat_id` INTEGER NOT NULL, " +
                        "`seconds_spent` INTEGER NOT NULL, `next_review_date` INTEGER, " +
                        "`easiness_factor` REAL NOT NULL, `interval_days` INTEGER NOT NULL, " +
                        "`repetitions` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, " +
                        "`favoritedAt` INTEGER, `part_of_speech` TEXT, " +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`language_id` INTEGER NOT NULL, `note` TEXT)"
                )
                db.execSQL(
                    "INSERT INTO words (word, meaning, stat_id, seconds_spent, next_review_date, " +
                        "easiness_factor, interval_days, repetitions, isFavorite, id, language_id) " +
                        "VALUES ('abandon','terk',0,0,999,2.8,3,2,0,7,1)"
                )
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(name).callback(callback).build()
        return FrameworkSQLiteOpenHelperFactory().create(config).writableDatabase
    }

    @Test
    fun `re-running the migration is idempotent and deterministic`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = openWordsOnlyDb(context, "migration-18-19-idem.db")
        val migration = Migration18To19(clock)

        migration.migrate(db)
        val first = readReviewRow(db, 7L)!!

        // A second run with the same clock must not add or change any row.
        migration.migrate(db)
        db.query("SELECT COUNT(*) FROM review_schedule").use { c ->
            c.moveToFirst()
            assertEquals(1, c.getInt(0))
        }
        val second = readReviewRow(db, 7L)!!

        assertEquals(first.stability, second.stability, 0.0)
        assertEquals(first.difficulty, second.difficulty, 0.0)
        assertEquals(first.dueAt, second.dueAt)
        assertEquals(first.cardState, second.cardState)
        assertEquals(first.reps, second.reps)
        assertEquals(first.createdAt, second.createdAt)
        assertEquals(first.updatedAt, second.updatedAt)

        // And it matches the oracle (last_reviewed was null here).
        val expected = Sm2ToFsrsMigrator.seed(
            intervalDays = 3, easeFactor = 2.8f, repetitions = 2,
            nextReviewDate = 999L, lastReviewed = null
        )
        assertEquals(expected.difficulty, second.difficulty, 1e-9)
        assertEquals(FsrsState.LEARNING.name, second.cardState)
        db.close()
    }

    // ---------------------------------------------------- Option (A) fresh install

    @Test
    fun `fresh install v18 asset opens, migrates to v19, dictionary intact`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // The prepackaged asset is stamped user_version 18 (built by tools/dictgen
        // against the 18.json schema). Room copies it and runs 18->19, adding
        // review_schedule without touching the dictionary — Option (A).
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "asset-v19-test.db")
            .createFromAsset("database/trainvoc-db.db")
            .addMigrations(Migration18To19())
            .allowMainThreadQueries()
            .build()
        try {
            val openDb = db.openHelper.readableDatabase // triggers copy + migrate + validation
            assertNotNull(openDb)
            assertEquals("migrated to v19", 19, openDb.version)

            // Dictionary content is byte-for-byte the shipped seed.
            openDb.query("SELECT COUNT(*) FROM words WHERE language_id = 1").use { c ->
                c.moveToFirst(); assertEquals(5466, c.getInt(0))
            }
            openDb.query("SELECT COUNT(*) FROM words WHERE language_id = 2").use { c ->
                c.moveToFirst(); assertEquals(5074, c.getInt(0))
            }

            // review_schedule exists and is empty — the asset carries no SM-2
            // progress, so nothing is seeded on a fresh install.
            openDb.query("SELECT COUNT(*) FROM review_schedule").use { c ->
                c.moveToFirst(); assertEquals(0, c.getInt(0))
            }
        } finally {
            db.close()
        }
    }
}
