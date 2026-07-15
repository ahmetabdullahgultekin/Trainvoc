package com.gultekinahmetabdullah.trainvoc.classes.word

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * One translated word together with its sense grouping, as returned by
 * WordDao.getTranslationsForWord (both edge directions).
 */
data class TranslationRow(
    @Embedded val word: Word,
    @ColumnInfo(name = "sense_index") val senseIndex: Int,
    @ColumnInfo(name = "translation_note") val note: String?,
    @ColumnInfo(name = "is_primary") val isPrimary: Boolean
)

/** Translations of one word grouped by sense, for the detail UI. */
data class SenseGroup(
    val senseIndex: Int,
    val note: String?,
    val translations: List<Word>
)

fun List<TranslationRow>.groupBySense(): List<SenseGroup> =
    groupBy { it.senseIndex }
        .toSortedMap()
        .map { (index, rows) ->
            SenseGroup(
                senseIndex = index,
                note = rows.firstNotNullOfOrNull { it.note },
                translations = rows.map { it.word }
            )
        }
