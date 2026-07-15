package com.gultekinahmetabdullah.trainvoc.database.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Verifies the 17→18 relational migration end-to-end under Robolectric:
 * user progress carries over by lemma, custom words survive with high ids,
 * relational edges exist, and Room validates the resulting schema against
 * the entity definitions when it opens the migrated database.
 *
 * The v17 database is built by executing the DDL from the exported schema
 * snapshot app/schemas/.../17.json directly (AGP does not merge unit-test
 * assets, so MigrationTestHelper cannot load it under Robolectric).
 *
 * Emulator caveat: this runs against Robolectric's SQLite, which can differ
 * slightly from device SQLite; a device smoke test is still worthwhile.
 */
@RunWith(RobolectricTestRunner::class)
class Migration17To18Test {

    private val dbName = "migration-test.db"

    /** Builds a real v17 database file from the exported 17.json DDL. */
    private fun createV17Database(context: Context): File {
        val schemaFile = File(
            "schemas/com.gultekinahmetabdullah.trainvoc.database.AppDatabase/17.json"
        )
        assertTrue("17.json exists at ${schemaFile.absolutePath}", schemaFile.exists())
        val schema = JSONObject(schemaFile.readText(Charsets.UTF_8))
            .getJSONObject("database")

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
        db.version = 17
        return dbFile.also { seedV17(db); db.close() }
    }

    private fun seedV17(db: SQLiteDatabase) {
        // Seed word with progress (old shape: word string is the PK).
        db.execSQL(
            """INSERT INTO words (word, meaning, level, last_reviewed, stat_id,
               seconds_spent, next_review_date, easiness_factor, interval_days,
               repetitions, isFavorite, favoritedAt, part_of_speech)
               VALUES ('Abandon', 'terk etmek', 'B2', 111, 5, 42, 999, 2.8, 3, 2,
               1, 123, 'verb')"""
        )
        // Custom user word not present in the seed manifest.
        db.execSQL(
            """INSERT INTO words (word, meaning, level, last_reviewed, stat_id,
               seconds_spent, next_review_date, easiness_factor, interval_days,
               repetitions, isFavorite, favoritedAt, part_of_speech)
               VALUES ('mycustomword', '(1) özel anlam (2) ikinci anlam', NULL, NULL,
               7, 9, NULL, 2.5, 0, 0, 0, NULL, NULL)"""
        )
        db.execSQL("INSERT INTO exams (exam) VALUES ('YDS')")
        db.execSQL("INSERT INTO word_exam_cross_ref (word, exam) VALUES ('abandon', 'YDS')")
        db.execSQL(
            "INSERT INTO word_of_day (wordId, date, wasViewed) VALUES ('Abandon', '2026-07-01', 1)"
        )
    }

    @Test
    fun `progress and custom words survive 17 to 18`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        createV17Database(context)

        // Opening through Room runs Migration17To18 and validates the
        // resulting schema against the v18 entity definitions.
        val room = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(Migration17To18(context))
            .allowMainThreadQueries()
            .build()
        val db = room.openHelper.readableDatabase

        // Manifest word got its manifest id (< 1_000_000) and kept progress.
        db.query(
            "SELECT id, stat_id, seconds_spent, next_review_date, easiness_factor, " +
                "interval_days, repetitions, isFavorite, favoritedAt, part_of_speech, " +
                "language_id FROM words WHERE word = 'abandon' AND language_id = 1"
        ).use { c ->
            assertTrue("abandon exists", c.moveToFirst())
            assertTrue("manifest id", c.getLong(0) < 1_000_000)
            assertEquals(5, c.getInt(1))
            assertEquals(42, c.getInt(2))
            assertEquals(999L, c.getLong(3))
            assertEquals(2.8f, c.getFloat(4), 0.001f)
            assertEquals(3, c.getInt(5))
            assertEquals(2, c.getInt(6))
            assertEquals(1, c.getInt(7))
            assertEquals(123L, c.getLong(8))
            assertEquals("verb", c.getString(9))
        }

        // Custom word re-inserted with a high id, progress kept, senses unpacked.
        var customId = -1L
        db.query("SELECT id, stat_id FROM words WHERE word = 'mycustomword'").use { c ->
            assertTrue("custom word exists", c.moveToFirst())
            customId = c.getLong(0)
            assertTrue("custom id >= 1_000_000", customId >= 1_000_000)
            assertEquals(7, c.getInt(1))
        }
        db.query(
            "SELECT COUNT(DISTINCT sense_index) FROM word_translations WHERE word_id = $customId"
        ).use { c ->
            c.moveToFirst()
            assertEquals("custom word has 2 senses", 2, c.getInt(0))
        }

        // Turkish words are first-class rows.
        db.query(
            "SELECT COUNT(*) FROM words WHERE language_id = 2"
        ).use { c ->
            c.moveToFirst()
            assertTrue("TR words exist", c.getInt(0) > 4000)
        }

        // abandon has multi-sense translations and its YDS crossref by id.
        db.query(
            """SELECT COUNT(*) FROM word_translations t
               JOIN words w ON w.id = t.word_id WHERE w.word = 'abandon'"""
        ).use { c ->
            c.moveToFirst()
            assertTrue("abandon has translations", c.getInt(0) >= 2)
        }
        db.query(
            """SELECT COUNT(*) FROM word_exam_cross_ref x
               JOIN words w ON w.id = x.word_id
               WHERE w.word = 'abandon' AND x.exam = 'YDS'"""
        ).use { c ->
            c.moveToFirst()
            assertEquals("YDS ref remapped by id", 1, c.getInt(0))
        }

        // word_of_day remapped from 'Abandon' string to the numeric id.
        db.query(
            """SELECT COUNT(*) FROM word_of_day d
               JOIN words w ON w.id = d.wordId WHERE w.word = 'abandon'"""
        ).use { c ->
            c.moveToFirst()
            assertEquals(1, c.getInt(0))
        }

        room.close()
    }

    @Test
    fun `fresh install asset db opens and validates at v18`() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "asset-test.db")
            .createFromAsset("database/trainvoc-db.db")
            .allowMainThreadQueries()
            .build()
        try {
            val openDb = db.openHelper.readableDatabase // triggers copy + validation
            assertNotNull(openDb)
            openDb.query("SELECT COUNT(*) FROM words WHERE language_id = 1").use { c ->
                c.moveToFirst()
                assertTrue("EN words seeded", c.getInt(0) > 5000)
            }
            openDb.query(
                """SELECT COUNT(*) FROM word_translations t
                   JOIN words w ON w.id = t.word_id WHERE w.word = 'go'"""
            ).use { c ->
                c.moveToFirst()
                assertTrue("go has translations", c.getInt(0) >= 1)
            }
        } finally {
            db.close()
        }
    }
}
