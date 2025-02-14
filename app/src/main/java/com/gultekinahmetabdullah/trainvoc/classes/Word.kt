package com.gultekinahmetabdullah.trainvoc.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey val word: String,
    val meaning: String,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val skippedCount: Int = 0,
    val timeSpentMs: Long = 0,
    val lastAnswered: Long? = null
) {
    private val totalAttempts: Int
        get() = correctCount + wrongCount + skippedCount

    val accuracy: Double
        get() = if (totalAttempts > 0) (correctCount.toDouble() / totalAttempts) * 100 else 0.0
}