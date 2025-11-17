package com.gultekinahmetabdullah.trainvoc.classes.quiz

import androidx.compose.runtime.Immutable
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

@Immutable
data class Question(
    val correctWord: Word,
    val incorrectWords: List<Word>
) {
    val choices: List<Word> = (incorrectWords + correctWord).shuffled()
}