package com.gultekinahmetabdullah.trainvoc.database.seed

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Fixture-driven parity test: MeaningParser (Kotlin) must produce exactly
 * the same output as tools/dictgen/meaning_parser.py for every case in the
 * shared fixture file. Change the fixtures first, then keep both green.
 *
 * Robolectric runner is only used for org.json (android stub otherwise).
 */
@RunWith(RobolectricTestRunner::class)
class MeaningParserTest {

    private fun fixtures(): JSONObject {
        // Module dir (app/) is the working directory for unit tests.
        val candidates = listOf(
            File("../../tools/dictgen/fixtures/parser_cases.json"),
            File("../tools/dictgen/fixtures/parser_cases.json"),
            File("tools/dictgen/fixtures/parser_cases.json")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error("parser_cases.json not found from ${File(".").absolutePath}")
        return JSONObject(file.readText(Charsets.UTF_8))
    }

    @Test
    fun `all shared fixture cases pass`() {
        val cases = fixtures().getJSONArray("cases")
        assertTrue("fixture file has cases", cases.length() > 0)

        for (i in 0 until cases.length()) {
            val case = cases.getJSONObject(i)
            val name = case.getString("name")
            val parsed = MeaningParser.parse(case.getString("input"))

            val expectedSenses = case.getJSONArray("senses")
            assertEquals("[$name] sense count", expectedSenses.length(), parsed.senses.size)
            for (s in 0 until expectedSenses.length()) {
                val expected = expectedSenses.getJSONObject(s)
                val actual = parsed.senses[s]
                assertEquals(
                    "[$name] sense $s lemmas",
                    expected.getJSONArray("lemmas").let { arr -> List(arr.length()) { arr.getString(it) } },
                    actual.lemmas
                )
                assertEquals(
                    "[$name] sense $s synHints",
                    expected.getJSONArray("synHints").let { arr -> List(arr.length()) { arr.getString(it) } },
                    actual.synHints
                )
                val expectedNote = if (expected.isNull("note")) null else expected.getString("note")
                assertEquals("[$name] sense $s note", expectedNote, actual.note)
            }
            val expectedWordNote = if (case.isNull("wordNote")) null else case.getString("wordNote")
            assertEquals("[$name] wordNote", expectedWordNote, parsed.wordNote)
        }
    }

    @Test
    fun `turkish lowercasing maps dotted and dotless I correctly`() {
        assertEquals("istanbul", MeaningParser.turkishLower("İstanbul"))
        assertEquals("ılık", MeaningParser.turkishLower("Ilık"))
    }
}
