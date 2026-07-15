package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.classes.word.Language
import com.gultekinahmetabdullah.trainvoc.classes.word.Synonym
import com.gultekinahmetabdullah.trainvoc.classes.word.groupBySense
import com.gultekinahmetabdullah.trainvoc.testing.TestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Relational (schema v18) WordDao queries against a real in-memory Room
 * database under Robolectric: translation edges in both directions with
 * sense grouping, bidirectional synonyms, the unique(word, language_id)
 * index, and English-only (language_id = 1) scoping of the study queries.
 */
@RunWith(RobolectricTestRunner::class)
class WordDaoRelationalTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: WordDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.wordDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // ---- fixtures -----------------------------------------------------

    /** en "take" (id 1) -> tr almak (sense 0, primary), tr götürmek (sense 1). */
    private suspend fun seedTakeWithTranslations() {
        dao.insertWord(TestData.word(word = "take", meaning = "almak", id = 1L))
        dao.insertWord(TestData.turkishWord(word = "almak", id = 100L))
        dao.insertWord(TestData.turkishWord(word = "götürmek", id = 101L))
        link(wordId = 1L, translatedId = 100L, sense = 0, primary = true)
        link(wordId = 1L, translatedId = 101L, sense = 1, note = "bir yere")
    }

    private fun link(
        wordId: Long,
        translatedId: Long,
        sense: Int = 0,
        primary: Boolean = false,
        note: String? = null,
    ) {
        db.openHelper.writableDatabase.execSQL(
            """
            INSERT INTO word_translations
                (word_id, translated_word_id, sense_index, note, is_primary)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf<Any?>(wordId, translatedId, sense, note, if (primary) 1 else 0)
        )
    }

    // ---- translations -------------------------------------------------

    @Test
    fun `getTranslationsForWord follows edges stored in the forward direction`() = runTest {
        seedTakeWithTranslations()

        val rows = dao.getTranslationsForWord(1L)

        assertEquals(listOf("almak", "götürmek"), rows.map { it.word.word })
    }

    @Test
    fun `getTranslationsForWord follows edges stored in the reverse direction`() = runTest {
        seedTakeWithTranslations()

        // Edges are stored EN -> TR; querying from the Turkish side must
        // still find the English word.
        val rows = dao.getTranslationsForWord(100L)

        assertEquals(listOf("take"), rows.map { it.word.word })
    }

    @Test
    fun `getTranslationsForWord orders by sense then primary first`() = runTest {
        dao.insertWord(TestData.word(word = "get", meaning = "almak", id = 1L))
        dao.insertWord(TestData.turkishWord(word = "almak", id = 100L))
        dao.insertWord(TestData.turkishWord(word = "edinmek", id = 101L))
        dao.insertWord(TestData.turkishWord(word = "varmak", id = 102L))
        // Insert out of order to prove the ORDER BY does the work.
        link(wordId = 1L, translatedId = 102L, sense = 1, primary = false)
        link(wordId = 1L, translatedId = 101L, sense = 0, primary = false)
        link(wordId = 1L, translatedId = 100L, sense = 0, primary = true)

        val rows = dao.getTranslationsForWord(1L)

        assertEquals(listOf(0, 0, 1), rows.map { it.senseIndex })
        // Within sense 0 the primary translation comes first.
        assertEquals("almak", rows.first().word.word)
        assertTrue(rows.first().isPrimary)
    }

    @Test
    fun `groupBySense groups rows into ascending sense groups`() = runTest {
        seedTakeWithTranslations()

        val senses = dao.getTranslationsForWord(1L).groupBySense()

        assertEquals(listOf(0, 1), senses.map { it.senseIndex })
        assertEquals(listOf("almak"), senses[0].translations.map { it.word })
        assertNull(senses[0].note)
        assertEquals(listOf("götürmek"), senses[1].translations.map { it.word })
        assertEquals("bir yere", senses[1].note)
    }

    // ---- synonyms ------------------------------------------------------

    @Test
    fun `getSynonymsForWord finds pairs stored in either direction`() = runTest {
        dao.insertWord(TestData.word(word = "big", meaning = "büyük", id = 1L))
        dao.insertWord(TestData.word(word = "large", meaning = "büyük", id = 2L))
        // Pairs are stored once with word_id < synonym_word_id.
        db.dictionaryEnrichmentDao().insertSynonyms(
            listOf(Synonym(wordId = 1L, synonymWordId = 2L))
        )

        assertEquals(listOf("large"), dao.getSynonymsForWord(1L).map { it.word })
        assertEquals(listOf("big"), dao.getSynonymsForWord(2L).map { it.word })
    }

    @Test
    fun `getSynonymsForWord returns empty list for a word without synonyms`() = runTest {
        dao.insertWord(TestData.word(word = "alone", meaning = "yalnız", id = 1L))

        assertTrue(dao.getSynonymsForWord(1L).isEmpty())
    }

    // ---- unique(word, language_id) --------------------------------------

    @Test
    fun `insertWords ignores a duplicate lemma in the same language`() = runTest {
        dao.insertWord(TestData.word(word = "run", meaning = "koşmak"))

        val rowIds = dao.insertWords(mutableSetOf(TestData.word(word = "run", meaning = "dup")))

        assertEquals(listOf(-1L), rowIds) // IGNORE reports the conflict as -1
        assertEquals(1, dao.getWordCount())
        assertEquals("koşmak", dao.getWord("run")?.meaning) // original row untouched
    }

    @Test
    fun `same lemma may exist once per language`() = runTest {
        dao.insertWord(TestData.word(word = "test", meaning = "sınav", id = 1L))
        dao.insertWord(TestData.turkishWord(word = "test", id = 2L))

        // Both rows exist; the English-scoped lookup resolves the EN row.
        assertEquals(1L, dao.getWord("test")?.id)
        assertEquals(Language.TURKISH_ID, dao.getWordById(2L)?.languageId)
    }

    @Test
    fun `raw REPLACE insert of an existing lemma mints a new id`() = runTest {
        // Documents the sharp edge WordRepository.insertWord guards against:
        // REPLACE on the unique(word, language_id) index deletes the old row
        // (cascading its relational edges) and assigns a fresh id.
        dao.insertWord(TestData.word(word = "run", meaning = "koşmak"))
        val originalId = dao.getWord("run")!!.id

        dao.insertWord(TestData.word(word = "run", meaning = "updated"))

        val replaced = dao.getWord("run")!!
        assertNotEquals(originalId, replaced.id)
        assertEquals("updated", replaced.meaning)
        assertEquals(1, dao.getWordCount())
    }

    // ---- language scoping ------------------------------------------------

    @Test
    fun `getAllWords returns only English rows`() = runTest {
        dao.insertWord(TestData.word(word = "house", meaning = "ev", id = 1L))
        dao.insertWord(TestData.word(word = "cat", meaning = "kedi", id = 2L))
        dao.insertWord(TestData.turkishWord(word = "ev", id = 100L))
        dao.insertWord(TestData.turkishWord(word = "kedi", id = 101L))

        val words = dao.getAllWords().first()

        assertEquals(listOf("cat", "house"), words.map { it.word }) // ASC order
        assertTrue(words.all { it.languageId == Language.ENGLISH_ID })
    }

    @Test
    fun `getWordCount counts only English rows`() = runTest {
        dao.insertWord(TestData.word(word = "house", meaning = "ev", id = 1L))
        dao.insertWord(TestData.turkishWord(word = "ev", id = 100L))
        dao.insertWord(TestData.turkishWord(word = "kedi", id = 101L))

        assertEquals(1, dao.getWordCount())
    }

    @Test
    fun `getWordById resolves rows in any language`() = runTest {
        dao.insertWord(TestData.turkishWord(word = "ev", id = 100L))

        assertEquals("ev", dao.getWordById(100L)?.word)
        assertNull(dao.getWordById(9999L))
    }
}
