package com.gultekinahmetabdullah.trainvoc.database.seed

import java.util.Locale

/**
 * Kotlin mirror of tools/dictgen/meaning_parser.py.
 *
 * Unpacks the legacy packed Turkish "meaning" strings, e.g.
 * "(1) (birini) terk etmek (= leave) (2) bir şeyden vazgeçmek (= give up)"
 * into senses (Turkish lemmas + synonym hints + usage note).
 *
 * Used by the Room 17→18 migration to convert user-added custom words
 * into the relational schema. Both implementations are verified against
 * the shared fixture file tools/dictgen/fixtures/parser_cases.json —
 * change the fixtures first, then keep both parsers green.
 */
object MeaningParser {

    data class Sense(
        val lemmas: List<String>,
        val synHints: List<String>,
        val note: String?
    )

    data class ParsedMeaning(
        val senses: List<Sense>,
        val wordNote: String?
    )

    private val NUMBER_MARK = Regex("""\(\s*\d+\s*\)""")
    private val SYN_HINT = Regex("""\(\s*=\s*([^)]*)\)""")
    private val EXAMPLE = Regex("""\(\s*\*[^)]*\)|\(\s*\*.*$""")
    private val PAREN_NOTE = Regex("""\(([^)]*)\)""")
    private val SEPARATOR = Regex("""[,/]""")
    private val WHITESPACE = Regex("""\s+""")

    /** Turkish-aware lowercasing (İ->i, I->ı). */
    fun turkishLower(s: String): String = s.lowercase(Locale.forLanguageTag("tr"))

    private fun cleanLemma(raw: String): String =
        raw.replace("---", " ")
            .trim()
            .replace(WHITESPACE, " ")
            .trim(' ', '.', '!', '?', ';', ':', '-')

    private fun parseSense(rawText: String): Sense {
        val synHints = mutableListOf<String>()
        var text = SYN_HINT.replace(rawText) { m ->
            m.groupValues[1].trim().takeIf { it.isNotEmpty() }?.let(synHints::add)
            " "
        }
        text = EXAMPLE.replace(text, " ")

        val notes = mutableListOf<String>()
        text = PAREN_NOTE.replace(text) { m ->
            m.groupValues[1].trim().takeIf { it.isNotEmpty() }?.let(notes::add)
            " "
        }

        val lemmas = SEPARATOR.split(text)
            .map(::cleanLemma)
            .filter { it.isNotEmpty() }
        return Sense(
            lemmas = lemmas,
            synHints = synHints,
            note = notes.takeIf { it.isNotEmpty() }?.joinToString("; ")
        )
    }

    fun parse(raw: String): ParsedMeaning {
        var text = raw.trim()
        if (text.isEmpty()) return ParsedMeaning(emptyList(), null)

        var wordNote: String? = null
        val starIndex = text.indexOf("***")
        if (starIndex >= 0) {
            wordNote = text.substring(starIndex + 3).trim().takeIf { it.isNotEmpty() }
            text = text.substring(0, starIndex)
        }

        val senses = NUMBER_MARK.split(text)
            .filter { it.isNotBlank() }
            .map(::parseSense)
            .filter { it.lemmas.isNotEmpty() }
        return ParsedMeaning(senses, wordNote)
    }
}
