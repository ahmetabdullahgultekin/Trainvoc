package com.gultekinahmetabdullah.trainvoc.classes.quiz

import com.gultekinahmetabdullah.trainvoc.classes.word.Word

data class Question(
    val correctWord: Word,
    val incorrectWords: List<Word>
) {
    val choices: List<Word> = (incorrectWords + correctWord).shuffled()
}