package com.gultekinahmetabdullah.trainvoc.classes.enums

object Route {
    const val HOME = "home"
    const val MAIN = "main"
    const val SPLASH = "splash"
    const val STORY = "story"
    const val QUIZ = "quiz"
    const val QUIZ_MENU = "quiz_menu"
    const val QUIZ_EXAM_MENU = "quiz_exam_menu"
    const val MANAGEMENT = "management"
    const val USERNAME = "username"
    const val WELCOME = "welcome"
    const val HELP = "help"
    const val ABOUT = "about"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val DICTIONARY = "dictionary"
    const val BACKUP = "backup"
    const val WORD_DETAIL = "word_detail/{wordId}"

    fun wordDetail(wordId: String) = "word_detail/$wordId"
}