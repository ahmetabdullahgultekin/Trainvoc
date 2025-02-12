package com.gultekinahmetabdullah.trainvoc.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey
    val word: String,
    val meaning: String,
    val numberOfCorrectAnswers: Int = 0,
    val numberOfWrongAnswers: Int = 0,
) {
    // Computed properties using functions
    private fun getTotalAnswers(): Int =
        numberOfCorrectAnswers + numberOfWrongAnswers

    fun getCorrectPercentage(): Double = if (getTotalAnswers() > 0) {
        (numberOfCorrectAnswers.toDouble() / getTotalAnswers().toDouble()) * 100
    } else {
        0.0
    }

    fun getWrongPercentage(): Double = if (getTotalAnswers() > 0) {
        (numberOfWrongAnswers.toDouble() / getTotalAnswers().toDouble()) * 100
    } else {
        0.0
    }
}
