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
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val WORD_DETAIL = "word_detail/{wordId}"
    const val FEATURE_FLAGS_ADMIN = "feature_flags_admin"
    const val FEATURE_FLAGS_USER = "feature_flags_user"
    const val GAMES_MENU = "games_menu"

    // Phase 1 - New Routes
    const val PROFILE = "profile"
    const val WORD_OF_DAY = "word_of_day"
    const val FAVORITES = "favorites"
    const val LAST_QUIZ_RESULTS = "last_quiz_results"
    const val DAILY_GOALS = "daily_goals"
    const val ACHIEVEMENTS = "achievements"

    // Phase 2 - Additional Routes
    const val STREAK_DETAIL = "streak_detail"

    // Phase 3 - Engagement Features
    const val LEADERBOARD = "leaderboard"
    const val WORD_PROGRESS = "word_progress"

    // Phase 4 - Accessibility
    const val ACCESSIBILITY_SETTINGS = "accessibility_settings"

    // Phase 5 - Update Notes & Changelog
    const val CHANGELOG = "changelog"

    // Phase 6 - Authentication
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Phase 7 - Multiplayer
    const val MULTIPLAYER_HOME = "multiplayer_home"
    const val MULTIPLAYER_CREATE_ROOM = "multiplayer_create_room"
    const val MULTIPLAYER_JOIN_ROOM = "multiplayer_join_room"
    const val MULTIPLAYER_LOBBY = "multiplayer_lobby/{roomCode}"
    const val MULTIPLAYER_GAME = "multiplayer_game/{roomCode}"
    const val MULTIPLAYER_RESULTS = "multiplayer_results/{roomCode}"

    fun wordDetail(wordId: String) = "word_detail/$wordId"
    fun multiplayerLobby(roomCode: String) = "multiplayer_lobby/$roomCode"
    fun multiplayerGame(roomCode: String) = "multiplayer_game/$roomCode"
    fun multiplayerResults(roomCode: String) = "multiplayer_results/$roomCode"
}