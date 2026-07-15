package com.gultekinahmetabdullah.trainvoc.database.migration

import android.content.Context
import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gultekinahmetabdullah.trainvoc.database.seed.MeaningParser
import org.json.JSONObject
import java.util.Locale

/**
 * Room migration 17 → 18: string-keyed bilingual words → relational,
 * multilingual schema with numeric ids.
 *
 * Strategy: rebuild from the bundled seed manifest
 * (assets/database/seed_v18.json, produced by tools/dictgen) and carry the
 * user's learning progress over by lemma:
 *
 *  1. languages + new words table; manifest words inserted WITH their
 *     manifest ids, so upgraders and fresh installs share identical ids.
 *  2. Progress columns (SM-2 state, favorites, statistics link, time
 *     spent) are copied from the old rows onto the matching new rows by
 *     case-insensitive lemma. NOTHING the user learned is dropped.
 *  3. Old rows with no manifest match (user-added custom words) are
 *     re-inserted with ids >= 1_000_000 (seed updates can never collide)
 *     and their packed meaning strings are unpacked with MeaningParser
 *     into Turkish word rows + translation edges.
 *  4. word_exam_cross_ref, synonyms, word_of_day and
 *     quiz_question_results are rebuilt keyed by word id; legacy rows are
 *     remapped by lemma (unresolvable rows are dropped — they were
 *     already unreachable: the old seed's crossrefs did not even match
 *     the words table's casing).
 *  5. srs_cards is cleared: its word_id column previously matched no real
 *     key; from v18 on it means words.id.
 *
 * The DDL below mirrors Room's exported schema 18.json exactly (tables,
 * column order, index names). The dictgen DB builder executes the same
 * exported DDL, and the schema-parity test guards against drift.
 */
class Migration17To18(private val context: Context) : Migration(17, 18) {

    override fun migrate(db: SupportSQLiteDatabase) {
        val manifest = JSONObject(
            context.assets.open("database/seed_v18.json")
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
        )

        // ---- 1. Preserve legacy data in memory (all small) ----------------
        val oldWords = readOldWords(db)
        val oldCrossRefs = readPairs(db, "SELECT word, exam FROM word_exam_cross_ref")
        val oldSynonyms = readPairs(db, "SELECT word, synonym FROM synonyms")
        val oldWordOfDay = readWordOfDay(db)
        val oldQuizResults = readQuizResults(db)

        // ---- 2. Drop the tables being rebuilt (children first) ------------
        db.execSQL("DROP TABLE IF EXISTS quiz_question_results")
        db.execSQL("DROP TABLE IF EXISTS word_of_day")
        db.execSQL("DROP TABLE IF EXISTS synonyms")
        db.execSQL("DROP TABLE IF EXISTS word_exam_cross_ref")
        db.execSQL("DROP TABLE IF EXISTS words")

        // ---- 3. Create the v18 tables (DDL = Room's exported 18.json) -----
        createV18Tables(db)

        // ---- 4. Seed from the manifest ------------------------------------
        val languages = manifest.getJSONArray("languages")
        for (i in 0 until languages.length()) {
            val lang = languages.getJSONObject(i)
            db.execSQL(
                "INSERT INTO languages (id, code, name) VALUES (?, ?, ?)",
                arrayOf(lang.getLong("id"), lang.getString("code"), lang.getString("name"))
            )
        }

        val enIdByLemma = HashMap<String, Long>()
        val trIdByLemma = HashMap<String, Long>()
        val words = manifest.getJSONArray("words")
        val insertWord = db.compileStatement(
            "INSERT INTO words (id, word, language_id, meaning, level, note, " +
                "stat_id, seconds_spent, easiness_factor, interval_days, " +
                "repetitions, isFavorite) VALUES (?, ?, ?, ?, ?, ?, 0, 0, 2.5, 0, 0, 0)"
        )
        for (i in 0 until words.length()) {
            val w = words.getJSONObject(i)
            val id = w.getLong("id")
            val lemma = w.getString("lemma")
            val lang = w.getLong("lang")
            insertWord.clearBindings()
            insertWord.bindLong(1, id)
            insertWord.bindString(2, lemma)
            insertWord.bindLong(3, lang)
            insertWord.bindString(4, w.optString("meaning", ""))
            if (w.isNull("level")) insertWord.bindNull(5)
            else insertWord.bindString(5, w.getString("level"))
            if (w.isNull("note")) insertWord.bindNull(6)
            else insertWord.bindString(6, w.getString("note"))
            insertWord.executeInsert()
            when (lang) {
                1L -> enIdByLemma[lemma.lowercase(Locale.ROOT)] = id
                2L -> trIdByLemma[MeaningParser.turkishLower(lemma)] = id
            }
        }

        val translations = manifest.getJSONArray("translations")
        val insertTranslation = db.compileStatement(
            "INSERT INTO word_translations (word_id, translated_word_id, " +
                "sense_index, note, is_primary) VALUES (?, ?, ?, ?, ?)"
        )
        for (i in 0 until translations.length()) {
            val t = translations.getJSONObject(i)
            insertTranslation.clearBindings()
            insertTranslation.bindLong(1, t.getLong("wordId"))
            insertTranslation.bindLong(2, t.getLong("translatedWordId"))
            insertTranslation.bindLong(3, t.getLong("senseIndex"))
            if (t.isNull("note")) insertTranslation.bindNull(4)
            else insertTranslation.bindString(4, t.getString("note"))
            insertTranslation.bindLong(5, if (t.getBoolean("isPrimary")) 1 else 0)
            insertTranslation.executeInsert()
        }

        val synonyms = manifest.getJSONArray("synonyms")
        for (i in 0 until synonyms.length()) {
            val s = synonyms.getJSONObject(i)
            db.execSQL(
                "INSERT OR IGNORE INTO synonyms (word_id, synonym_word_id) VALUES (?, ?)",
                arrayOf(s.getLong("wordId"), s.getLong("synonymWordId"))
            )
        }

        val wordExams = manifest.getJSONArray("wordExams")
        for (i in 0 until wordExams.length()) {
            val x = wordExams.getJSONObject(i)
            db.execSQL(
                "INSERT OR IGNORE INTO word_exam_cross_ref (word_id, exam) VALUES (?, ?)",
                arrayOf(x.getLong("wordId"), x.getString("exam"))
            )
        }

        // ---- 5. Carry user progress over by lemma -------------------------
        // User-added words get ids >= 1_000_000 (seed ids stay < 1_000_000).
        // sqlite_sequence has no unique constraint, so INSERT OR REPLACE
        // would create a duplicate row; the manifest inserts above already
        // created the row, so UPDATE it (with a defensive insert fallback).
        db.execSQL("UPDATE sqlite_sequence SET seq = 999999 WHERE name = 'words'")
        db.execSQL(
            "INSERT INTO sqlite_sequence (name, seq) SELECT 'words', 999999 " +
                "WHERE NOT EXISTS (SELECT 1 FROM sqlite_sequence WHERE name = 'words')"
        )

        val progressUpdate = db.compileStatement(
            "UPDATE words SET last_reviewed = ?, stat_id = ?, seconds_spent = ?, " +
                "next_review_date = ?, easiness_factor = ?, interval_days = ?, " +
                "repetitions = ?, isFavorite = ?, favoritedAt = ?, " +
                "part_of_speech = COALESCE(?, part_of_speech) WHERE id = ?"
        )
        val legacyIdByLemma = HashMap<String, Long>()
        for (old in oldWords) {
            val key = old.word.trim().lowercase(Locale.ROOT)
            var newId = enIdByLemma[key]
            if (newId == null) {
                // Custom word: re-insert (auto id >= 1_000_000), then unpack
                // its packed meaning into TR rows + translation edges.
                newId = insertCustomWord(db, old, trIdByLemma)
            }
            legacyIdByLemma[key] = newId
            if (old.hasProgress()) {
                progressUpdate.clearBindings()
                bindNullableLong(progressUpdate, 1, old.lastReviewed)
                progressUpdate.bindLong(2, old.statId)
                progressUpdate.bindLong(3, old.secondsSpent)
                bindNullableLong(progressUpdate, 4, old.nextReviewDate)
                progressUpdate.bindDouble(5, old.easinessFactor)
                progressUpdate.bindLong(6, old.intervalDays)
                progressUpdate.bindLong(7, old.repetitions)
                progressUpdate.bindLong(8, old.isFavorite)
                bindNullableLong(progressUpdate, 9, old.favoritedAt)
                if (old.partOfSpeech == null) progressUpdate.bindNull(10)
                else progressUpdate.bindString(10, old.partOfSpeech)
                progressUpdate.bindLong(11, newId)
                progressUpdate.executeUpdateDelete()
            }
        }

        // ---- 6. Remap legacy relational rows -------------------------------
        for ((word, exam) in oldCrossRefs) {
            val id = legacyIdByLemma[word.trim().lowercase(Locale.ROOT)] ?: continue
            db.execSQL(
                "INSERT OR IGNORE INTO word_exam_cross_ref (word_id, exam) VALUES (?, ?)",
                arrayOf<Any?>(id, exam)
            )
        }
        for ((a, b) in oldSynonyms) {
            val idA = legacyIdByLemma[a.trim().lowercase(Locale.ROOT)]
                ?: enIdByLemma[a.trim().lowercase(Locale.ROOT)] ?: continue
            val idB = legacyIdByLemma[b.trim().lowercase(Locale.ROOT)]
                ?: enIdByLemma[b.trim().lowercase(Locale.ROOT)] ?: continue
            if (idA == idB) continue
            db.execSQL(
                "INSERT OR IGNORE INTO synonyms (word_id, synonym_word_id) VALUES (?, ?)",
                arrayOf(minOf(idA, idB), maxOf(idA, idB))
            )
        }
        for (row in oldWordOfDay) {
            val id = legacyIdByLemma[row.word.trim().lowercase(Locale.ROOT)] ?: continue
            db.execSQL(
                "INSERT INTO word_of_day (id, wordId, date, wasViewed) VALUES (?, ?, ?, ?)",
                arrayOf<Any?>(row.id, id, row.date, row.wasViewed)
            )
        }
        for (row in oldQuizResults) {
            val id = legacyIdByLemma[row.word.trim().lowercase(Locale.ROOT)] ?: continue
            db.execSQL(
                "INSERT INTO quiz_question_results (id, quizId, wordId, isCorrect) " +
                    "VALUES (?, ?, ?, ?)",
                arrayOf(row.id, row.quizId, id, row.isCorrect)
            )
        }

        // ---- 7. srs_cards word ids meant nothing before v18 ----------------
        db.execSQL("DELETE FROM srs_cards")
    }

    // ------------------------------------------------------------------ DDL

    private fun createV18Tables(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `languages` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`code` TEXT NOT NULL, `name` TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_languages_code` " +
                "ON `languages` (`code`)"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `words` (" +
                "`word` TEXT NOT NULL, `meaning` TEXT NOT NULL, `level` TEXT, " +
                "`last_reviewed` INTEGER, `stat_id` INTEGER NOT NULL, " +
                "`seconds_spent` INTEGER NOT NULL, `next_review_date` INTEGER, " +
                "`easiness_factor` REAL NOT NULL, `interval_days` INTEGER NOT NULL, " +
                "`repetitions` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, " +
                "`favoritedAt` INTEGER, `part_of_speech` TEXT, " +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`language_id` INTEGER NOT NULL, `note` TEXT)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_words_word_language_id` " +
                "ON `words` (`word`, `language_id`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_language_id` ON `words` (`language_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_level` ON `words` (`level`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_stat_id` ON `words` (`stat_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_last_reviewed` ON `words` (`last_reviewed`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_next_review_date` ON `words` (`next_review_date`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_isFavorite` ON `words` (`isFavorite`)")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `word_translations` (" +
                "`word_id` INTEGER NOT NULL, `translated_word_id` INTEGER NOT NULL, " +
                "`sense_index` INTEGER NOT NULL, `note` TEXT, " +
                "`is_primary` INTEGER NOT NULL, " +
                "PRIMARY KEY(`word_id`, `translated_word_id`, `sense_index`), " +
                "FOREIGN KEY(`word_id`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`translated_word_id`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_word_translations_word_id` " +
                "ON `word_translations` (`word_id`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_word_translations_translated_word_id` " +
                "ON `word_translations` (`translated_word_id`)"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `synonyms` (" +
                "`word_id` INTEGER NOT NULL, `synonym_word_id` INTEGER NOT NULL, " +
                "PRIMARY KEY(`word_id`, `synonym_word_id`), " +
                "FOREIGN KEY(`word_id`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`synonym_word_id`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_synonyms_word_id` ON `synonyms` (`word_id`)")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_synonyms_synonym_word_id` " +
                "ON `synonyms` (`synonym_word_id`)"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `word_exam_cross_ref` (" +
                "`word_id` INTEGER NOT NULL, `exam` TEXT NOT NULL, " +
                "PRIMARY KEY(`word_id`, `exam`))"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_word_exam_cross_ref_word_id` " +
                "ON `word_exam_cross_ref` (`word_id`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_word_exam_cross_ref_exam` " +
                "ON `word_exam_cross_ref` (`exam`)"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `word_of_day` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`wordId` INTEGER NOT NULL, `date` TEXT NOT NULL, " +
                "`wasViewed` INTEGER NOT NULL, " +
                "FOREIGN KEY(`wordId`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_word_of_day_date` " +
                "ON `word_of_day` (`date`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_word_of_day_wordId` ON `word_of_day` (`wordId`)")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `quiz_question_results` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`quizId` INTEGER NOT NULL, `wordId` INTEGER NOT NULL, " +
                "`isCorrect` INTEGER NOT NULL, " +
                "FOREIGN KEY(`quizId`) REFERENCES `quiz_history`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`wordId`) REFERENCES `words`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_quiz_question_results_quizId` " +
                "ON `quiz_question_results` (`quizId`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_quiz_question_results_wordId` " +
                "ON `quiz_question_results` (`wordId`)"
        )
    }

    // ---------------------------------------------------------------- reads

    private class OldWord(
        val word: String,
        val meaning: String,
        val level: String?,
        val lastReviewed: Long?,
        val statId: Long,
        val secondsSpent: Long,
        val nextReviewDate: Long?,
        val easinessFactor: Double,
        val intervalDays: Long,
        val repetitions: Long,
        val isFavorite: Long,
        val favoritedAt: Long?,
        val partOfSpeech: String?
    ) {
        fun hasProgress(): Boolean =
            lastReviewed != null || statId != 0L || secondsSpent != 0L ||
                nextReviewDate != null || intervalDays != 0L || repetitions != 0L ||
                isFavorite != 0L || favoritedAt != null || partOfSpeech != null
    }

    private fun readOldWords(db: SupportSQLiteDatabase): List<OldWord> {
        val result = mutableListOf<OldWord>()
        db.query(
            "SELECT word, meaning, level, last_reviewed, stat_id, seconds_spent, " +
                "next_review_date, easiness_factor, interval_days, repetitions, " +
                "isFavorite, favoritedAt, part_of_speech FROM words"
        ).use { c ->
            while (c.moveToNext()) {
                result.add(
                    OldWord(
                        word = c.getString(0),
                        meaning = c.getString(1),
                        level = c.stringOrNull(2),
                        lastReviewed = c.longOrNull(3),
                        statId = c.getLong(4),
                        secondsSpent = c.getLong(5),
                        nextReviewDate = c.longOrNull(6),
                        easinessFactor = c.getDouble(7),
                        intervalDays = c.getLong(8),
                        repetitions = c.getLong(9),
                        isFavorite = c.getLong(10),
                        favoritedAt = c.longOrNull(11),
                        partOfSpeech = c.stringOrNull(12)
                    )
                )
            }
        }
        return result
    }

    private fun insertCustomWord(
        db: SupportSQLiteDatabase,
        old: OldWord,
        trIdByLemma: HashMap<String, Long>
    ): Long {
        val stmt = db.compileStatement(
            "INSERT INTO words (word, language_id, meaning, level, part_of_speech, " +
                "stat_id, seconds_spent, easiness_factor, interval_days, " +
                "repetitions, isFavorite) VALUES (?, 1, ?, ?, ?, 0, 0, 2.5, 0, 0, 0)"
        )
        stmt.bindString(1, old.word.trim())
        stmt.bindString(2, old.meaning)
        if (old.level == null) stmt.bindNull(3) else stmt.bindString(3, old.level)
        if (old.partOfSpeech == null) stmt.bindNull(4) else stmt.bindString(4, old.partOfSpeech)
        val newId = stmt.executeInsert()

        // Unpack the packed meaning into TR rows + translation edges so
        // custom words participate in the relational model too.
        val parsed = MeaningParser.parse(old.meaning)
        parsed.senses.forEachIndexed { senseIndex, sense ->
            sense.lemmas.forEachIndexed { pos, lemma ->
                val key = MeaningParser.turkishLower(lemma)
                val trId = trIdByLemma.getOrPut(key) {
                    val trStmt = db.compileStatement(
                        "INSERT INTO words (word, language_id, meaning, stat_id, " +
                            "seconds_spent, easiness_factor, interval_days, " +
                            "repetitions, isFavorite) VALUES (?, 2, ?, 0, 0, 2.5, 0, 0, 0)"
                    )
                    trStmt.bindString(1, key)
                    trStmt.bindString(2, old.word.trim().lowercase(Locale.ROOT))
                    trStmt.executeInsert()
                }
                db.execSQL(
                    "INSERT OR IGNORE INTO word_translations (word_id, " +
                        "translated_word_id, sense_index, note, is_primary) " +
                        "VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any?>(newId, trId, senseIndex, sense.note, if (senseIndex == 0 && pos == 0) 1 else 0)
                )
            }
        }
        return newId
    }

    private fun readPairs(db: SupportSQLiteDatabase, sql: String): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        db.query(sql).use { c ->
            while (c.moveToNext()) result.add(c.getString(0) to c.getString(1))
        }
        return result
    }

    private class OldWordOfDay(val id: Long, val word: String, val date: String, val wasViewed: Long)

    private fun readWordOfDay(db: SupportSQLiteDatabase): List<OldWordOfDay> {
        val result = mutableListOf<OldWordOfDay>()
        db.query("SELECT id, wordId, date, wasViewed FROM word_of_day").use { c ->
            while (c.moveToNext()) {
                result.add(OldWordOfDay(c.getLong(0), c.getString(1), c.getString(2), c.getLong(3)))
            }
        }
        return result
    }

    private class OldQuizResult(val id: Long, val quizId: Long, val word: String, val isCorrect: Long)

    private fun readQuizResults(db: SupportSQLiteDatabase): List<OldQuizResult> {
        val result = mutableListOf<OldQuizResult>()
        db.query("SELECT id, quizId, wordId, isCorrect FROM quiz_question_results").use { c ->
            while (c.moveToNext()) {
                result.add(OldQuizResult(c.getLong(0), c.getLong(1), c.getString(2), c.getLong(3)))
            }
        }
        return result
    }

    private fun bindNullableLong(
        stmt: androidx.sqlite.db.SupportSQLiteStatement,
        index: Int,
        value: Long?
    ) {
        if (value == null) stmt.bindNull(index) else stmt.bindLong(index, value)
    }
}

private fun Cursor.stringOrNull(index: Int): String? =
    if (isNull(index)) null else getString(index)

private fun Cursor.longOrNull(index: Int): Long? =
    if (isNull(index)) null else getLong(index)
