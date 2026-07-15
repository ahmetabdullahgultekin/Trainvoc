package com.gultekinahmetabdullah.trainvoc.database

import androidx.sqlite.db.SupportSQLiteProgram
import androidx.sqlite.db.SupportSQLiteQuery
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for WordQueryBuilder's generated SQL (schema v18).
 *
 * The builder replaced 30+ hand-written DAO methods, so the exact SQL is
 * load-bearing: every quiz query must scope to English rows
 * (language_id = 1), join exams through the id-based cross-ref, and bind
 * user-supplied filters as parameters rather than concatenating them.
 */
class WordQueryBuilderTest {

    // ---- language scoping ---------------------------------------------

    @Test
    fun `every quiz type scopes to English words`() {
        for (quizType in QuizType.entries) {
            val sql = WordQueryBuilder.buildQuery(quizType).sql
            assertTrue(
                "$quizType query must filter w.language_id = 1 but was: $sql",
                sql.contains("w.language_id = 1")
            )
        }
    }

    // ---- joins -----------------------------------------------------------

    @Test
    fun `exam filter joins the cross-ref table by word id`() {
        val sql = WordQueryBuilder.buildQuery(QuizType.RANDOM, exam = "TOEFL").sql

        assertTrue(
            "exam join must use the v18 id-based cross-ref: $sql",
            sql.contains("JOIN word_exam_cross_ref wec ON w.id = wec.word_id")
        )
        assertTrue(sql.contains("JOIN exams e ON wec.exam = e.exam"))
    }

    @Test
    fun `random quiz without filters does not join statistics or exams`() {
        val sql = WordQueryBuilder.buildQuery(QuizType.RANDOM).sql

        assertFalse(sql.contains("JOIN statistics"))
        assertFalse(sql.contains("JOIN word_exam_cross_ref"))
    }

    @Test
    fun `statistics-based quiz types join the statistics table`() {
        val sql = WordQueryBuilder.buildQuery(QuizType.MOST_WRONG).sql

        assertTrue(sql.contains("JOIN statistics s ON w.stat_id = s.stat_id"))
    }

    // ---- parameterization (SQL injection safety) -------------------------

    @Test
    fun `level filter is bound as a parameter not concatenated`() {
        val query = WordQueryBuilder.buildQuery(QuizType.RANDOM, level = "B2")

        assertTrue(query.sql.contains("w.level = ?"))
        assertFalse(query.sql.contains("B2"))
        assertEquals(listOf<Any?>("B2"), query.boundArgs())
    }

    @Test
    fun `exam filter is bound as a parameter not concatenated`() {
        val query = WordQueryBuilder.buildQuery(QuizType.RANDOM, exam = "IELTS")

        assertTrue(query.sql.contains("e.exam = ?"))
        assertFalse(query.sql.contains("IELTS"))
        assertEquals(listOf<Any?>("IELTS"), query.boundArgs())
    }

    @Test
    fun `level and exam filters bind in declaration order`() {
        val query = WordQueryBuilder.buildQuery(
            QuizType.LEAST_CORRECT, level = "A1", exam = "YDS"
        )

        assertEquals(listOf<Any?>("A1", "YDS"), query.boundArgs())
    }

    // ---- ordering and limit ----------------------------------------------

    @Test
    fun `order by clause matches the quiz type`() {
        assertTrue(
            WordQueryBuilder.buildQuery(QuizType.MOST_WRONG).sql
                .contains("ORDER BY s.wrong_count DESC")
        )
        assertTrue(
            WordQueryBuilder.buildQuery(QuizType.LEAST_REVIEWED).sql
                .contains("ORDER BY (s.correct_count + s.wrong_count + s.skipped_count) ASC")
        )
        assertTrue(
            WordQueryBuilder.buildQuery(QuizType.LEAST_RECENT).sql
                .contains("ORDER BY w.last_reviewed ASC")
        )
    }

    @Test
    fun `limit defaults to five and honours the override`() {
        assertTrue(WordQueryBuilder.buildQuery(QuizType.RANDOM).sql.endsWith("LIMIT 5"))
        assertTrue(
            WordQueryBuilder.buildQuery(QuizType.RANDOM, limit = 50).sql.endsWith("LIMIT 50")
        )
    }

    // ---- helpers -----------------------------------------------------------

    /** Captures the arguments a query would bind to its SQLite statement. */
    private fun SupportSQLiteQuery.boundArgs(): List<Any?> {
        val recorder = RecordingProgram()
        bindTo(recorder)
        return recorder.args.toSortedMap().values.toList()
    }

    private class RecordingProgram : SupportSQLiteProgram {
        val args = mutableMapOf<Int, Any?>()
        override fun bindNull(index: Int) { args[index] = null }
        override fun bindLong(index: Int, value: Long) { args[index] = value }
        override fun bindDouble(index: Int, value: Double) { args[index] = value }
        override fun bindString(index: Int, value: String) { args[index] = value }
        override fun bindBlob(index: Int, value: ByteArray) { args[index] = value }
        override fun clearBindings() = args.clear()
        override fun close() = Unit
    }
}
