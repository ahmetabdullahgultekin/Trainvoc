package com.gultekinahmetabdullah.trainvoc.classes

data class Question(
    val correctWord: Word,
    val incorrectWords: List<Word>
)