package com.gultekinahmetabdullah.trainvoc.testing

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Language
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

/**
 * Shared test-data builders for the schema-v18 entity shapes.
 *
 * Keeps the `Word` construction in one place so tests don't each repeat the
 * full 15-field constructor and silently drift when the schema changes.
 */
object TestData {

    /** An English (language_id = 1) word row, the default study language. */
    fun word(
        word: String = "test",
        meaning: String = "deneme",
        level: WordLevel? = WordLevel.A1,
        id: Long = 0L,
        languageId: Long = Language.ENGLISH_ID,
        statId: Int = 0,
        isFavorite: Boolean = false,
        favoritedAt: Long? = null,
        partOfSpeech: String? = null,
        note: String? = null,
    ): Word = Word(
        word = word,
        meaning = meaning,
        level = level,
        statId = statId,
        isFavorite = isFavorite,
        favoritedAt = favoritedAt,
        partOfSpeech = partOfSpeech,
        id = id,
        languageId = languageId,
        note = note,
    )

    /** A Turkish (language_id = 2) word row; TR rows carry no CEFR level. */
    fun turkishWord(
        word: String,
        meaning: String = "",
        id: Long = 0L,
    ): Word = word(
        word = word,
        meaning = meaning,
        level = null,
        id = id,
        languageId = Language.TURKISH_ID,
    )

    fun statistic(
        statId: Int = 0,
        correctCount: Int = 0,
        wrongCount: Int = 0,
        skippedCount: Int = 0,
        learned: Boolean = false,
    ): Statistic = Statistic(
        statId = statId,
        correctCount = correctCount,
        wrongCount = wrongCount,
        skippedCount = skippedCount,
        learned = learned,
    )
}
