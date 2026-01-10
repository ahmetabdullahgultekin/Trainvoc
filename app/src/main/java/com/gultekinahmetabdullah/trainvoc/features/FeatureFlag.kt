package com.gultekinahmetabdullah.trainvoc.features

/**
 * All features in the app (existing + planned from FEATURE_GAP_ANALYSIS.md)
 * Each feature can be controlled at global and user level
 */
enum class FeatureFlag(
    val key: String,
    val displayName: String,
    val description: String,
    val category: FeatureCategory,
    val isPremium: Boolean = false,
    val hasCost: Boolean = false,  // API costs money
    val defaultEnabled: Boolean = true,
    val adminOnly: Boolean = false  // Only admin can toggle
) {
    // ========== EXISTING FEATURES (Week 1-8) ==========
    SPACED_REPETITION(
        key = "spaced_repetition",
        displayName = "Spaced Repetition (SM-2)",
        description = "Intelligent review scheduling based on memory strength",
        category = FeatureCategory.CORE_LEARNING,
        isPremium = false
    ),

    ADAPTIVE_DIFFICULTY(
        key = "adaptive_difficulty",
        displayName = "Adaptive Difficulty",
        description = "AI adjusts quiz difficulty based on your performance",
        category = FeatureCategory.CORE_LEARNING,
        isPremium = true
    ),

    GAMIFICATION(
        key = "gamification",
        displayName = "Gamification",
        description = "Achievements, streaks, levels, and rewards",
        category = FeatureCategory.GAMIFICATION,
        isPremium = false
    ),

    ACHIEVEMENTS(
        key = "achievements",
        displayName = "Achievements",
        description = "Unlock badges for milestones",
        category = FeatureCategory.GAMIFICATION,
        isPremium = false
    ),

    STREAK_TRACKING(
        key = "streak_tracking",
        displayName = "Streak Tracking",
        description = "Daily study streak counter",
        category = FeatureCategory.GAMIFICATION,
        isPremium = false
    ),

    CLOUD_BACKUP(
        key = "cloud_backup",
        displayName = "Cloud Backup (Google Drive)",
        description = "Automatic cloud backup to Google Drive",
        category = FeatureCategory.SYNC,
        isPremium = true,
        hasCost = false  // Uses user's Google Drive
    ),

    PERFORMANCE_MONITORING(
        key = "performance_monitoring",
        displayName = "Performance Monitoring",
        description = "App performance tracking and optimization",
        category = FeatureCategory.SYSTEM,
        isPremium = false,
        adminOnly = true
    ),

    LEARNING_ANALYTICS(
        key = "learning_analytics",
        displayName = "Learning Analytics",
        description = "Detailed progress statistics and insights",
        category = FeatureCategory.ANALYTICS,
        isPremium = true
    ),

    // ========== PHASE 1: CORE COMPETITIVENESS (Weeks 9-16) ==========

    AUDIO_PRONUNCIATION(
        key = "audio_pronunciation",
        displayName = "Audio Pronunciation",
        description = "Hear native speaker pronunciation for words",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = false,
        hasCost = true  // TTS API costs
    ),

    TEXT_TO_SPEECH(
        key = "text_to_speech",
        displayName = "Text-to-Speech",
        description = "Convert text to speech for listening practice",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = false,
        hasCost = true  // Google TTS API
    ),

    AUDIO_SPEED_CONTROL(
        key = "audio_speed_control",
        displayName = "Audio Speed Control",
        description = "Adjust pronunciation speed (slow/normal/fast)",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = true
    ),

    IMAGES_VISUAL_AIDS(
        key = "images_visual_aids",
        displayName = "Images & Visual Aids",
        description = "Visual learning with images for words",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = false,
        hasCost = true  // Unsplash API / Image storage
    ),

    IMAGE_FLASHCARDS(
        key = "image_flashcards",
        displayName = "Image Flashcards",
        description = "Study with visual flashcards",
        category = FeatureCategory.QUIZ_TYPES,
        isPremium = false
    ),

    EXAMPLE_SENTENCES(
        key = "example_sentences",
        displayName = "Example Sentences",
        description = "See words used in real sentences",
        category = FeatureCategory.CONTENT,
        isPremium = false
    ),

    USAGE_CONTEXT(
        key = "usage_context",
        displayName = "Usage Context",
        description = "Learn when to use words (formal/informal/slang)",
        category = FeatureCategory.CONTENT,
        isPremium = false
    ),

    SENTENCE_QUIZZES(
        key = "sentence_quizzes",
        displayName = "Sentence-Based Quizzes",
        description = "Quiz mode with full sentences",
        category = FeatureCategory.QUIZ_TYPES,
        isPremium = false
    ),

    OFFLINE_MODE(
        key = "offline_mode",
        displayName = "Offline Mode",
        description = "Study without internet connection",
        category = FeatureCategory.SYNC,
        isPremium = false
    ),

    OFFLINE_AUDIO_CACHE(
        key = "offline_audio_cache",
        displayName = "Offline Audio Cache",
        description = "Download audio for offline use",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = true
    ),

    OFFLINE_IMAGE_CACHE(
        key = "offline_image_cache",
        displayName = "Offline Image Cache",
        description = "Download images for offline use",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = true
    ),

    // ========== PHASE 2: PLATFORM EXPANSION (Weeks 17-28) ==========

    PREMIUM_SUBSCRIPTION(
        key = "premium_subscription",
        displayName = "Premium Subscription",
        description = "Unlock premium features with subscription",
        category = FeatureCategory.MONETIZATION,
        isPremium = false,
        adminOnly = true
    ),

    IN_APP_PURCHASES(
        key = "in_app_purchases",
        displayName = "In-App Purchases",
        description = "Buy features or content individually",
        category = FeatureCategory.MONETIZATION,
        isPremium = false,
        adminOnly = true
    ),

    ADVERTISEMENTS(
        key = "advertisements",
        displayName = "Advertisements",
        description = "Show ads to free users",
        category = FeatureCategory.MONETIZATION,
        isPremium = false,
        adminOnly = true,
        hasCost = false,  // Generates revenue
        defaultEnabled = false
    ),

    HOME_SCREEN_WIDGETS(
        key = "home_screen_widgets",
        displayName = "Home Screen Widgets",
        description = "Quick access widgets on home screen",
        category = FeatureCategory.PLATFORM,
        isPremium = false
    ),

    // ========== PHASE 3: ADVANCED FEATURES (Weeks 29-44) ==========

    SPEECH_RECOGNITION(
        key = "speech_recognition",
        displayName = "Speech Recognition",
        description = "Practice speaking and get pronunciation feedback",
        category = FeatureCategory.ADVANCED_INPUT,
        isPremium = true,
        hasCost = true  // Google Speech API
    ),

    PRONUNCIATION_SCORING(
        key = "pronunciation_scoring",
        displayName = "Pronunciation Scoring",
        description = "Get accuracy score for your pronunciation",
        category = FeatureCategory.ADVANCED_INPUT,
        isPremium = true,
        hasCost = true
    ),

    VOICE_RECORDING(
        key = "voice_recording",
        displayName = "Voice Recording",
        description = "Record and compare your pronunciation",
        category = FeatureCategory.ADVANCED_INPUT,
        isPremium = true
    ),

    SOCIAL_FRIENDS(
        key = "social_friends",
        displayName = "Friends & Following",
        description = "Connect with friends and follow their progress",
        category = FeatureCategory.SOCIAL,
        isPremium = false,
        hasCost = true  // Backend server costs
    ),

    LEADERBOARDS(
        key = "leaderboards",
        displayName = "Leaderboards",
        description = "Compete with others on public leaderboards",
        category = FeatureCategory.SOCIAL,
        isPremium = false,
        hasCost = true  // Backend server costs
    ),

    SOCIAL_SHARING(
        key = "social_sharing",
        displayName = "Social Sharing",
        description = "Share your achievements and progress",
        category = FeatureCategory.SOCIAL,
        isPremium = false
    ),

    COMMUNITY_DECKS(
        key = "community_decks",
        displayName = "Community Decks",
        description = "Access user-created word decks",
        category = FeatureCategory.CONTENT,
        isPremium = false,
        hasCost = true  // Storage + moderation
    ),

    USER_GENERATED_CONTENT(
        key = "user_generated_content",
        displayName = "User-Generated Content",
        description = "Create and share your own word lists",
        category = FeatureCategory.CONTENT,
        isPremium = true,
        hasCost = true  // Storage costs
    ),

    VIDEO_CONTENT(
        key = "video_content",
        displayName = "Video Content",
        description = "Learn with video lessons",
        category = FeatureCategory.MULTIMEDIA,
        isPremium = true,
        hasCost = true  // Video hosting costs
    ),

    INTERACTIVE_STORIES(
        key = "interactive_stories",
        displayName = "Interactive Stories",
        description = "Learn through interactive story mode",
        category = FeatureCategory.CONTENT,
        isPremium = true
    ),

    // ========== MEDIUM PRIORITY ==========

    SYNONYMS_ANTONYMS(
        key = "synonyms_antonyms",
        displayName = "Synonyms & Antonyms",
        description = "Learn related words and opposites",
        category = FeatureCategory.CONTENT,
        isPremium = false
    ),

    GRAMMAR_TIPS(
        key = "grammar_tips",
        displayName = "Grammar Tips",
        description = "Grammar rules and explanations",
        category = FeatureCategory.CONTENT,
        isPremium = true
    ),

    CULTURAL_NOTES(
        key = "cultural_notes",
        displayName = "Cultural Notes",
        description = "Cultural context for words and phrases",
        category = FeatureCategory.CONTENT,
        isPremium = true
    ),

    MULTIPLE_LANGUAGES(
        key = "multiple_languages",
        displayName = "Multiple Languages",
        description = "Support for multiple language pairs",
        category = FeatureCategory.CONTENT,
        isPremium = false,
        adminOnly = true
    ),

    // ========== LOW PRIORITY ==========

    AI_TUTOR(
        key = "ai_tutor",
        displayName = "AI Tutor Chatbot",
        description = "ChatGPT-style conversation practice",
        category = FeatureCategory.ADVANCED_INPUT,
        isPremium = true,
        hasCost = true  // OpenAI API costs
    ),

    VIRTUAL_CURRENCY(
        key = "virtual_currency",
        displayName = "Virtual Currency",
        description = "Earn gems/coins for completing lessons",
        category = FeatureCategory.GAMIFICATION,
        isPremium = false
    ),

    HANDWRITING_INPUT(
        key = "handwriting_input",
        displayName = "Handwriting Input",
        description = "Draw characters for practice",
        category = FeatureCategory.ADVANCED_INPUT,
        isPremium = true
    ),

    // ========== SYSTEM FEATURES ==========

    GDPR_COMPLIANCE(
        key = "gdpr_compliance",
        displayName = "GDPR Compliance",
        description = "Data privacy and GDPR tools",
        category = FeatureCategory.SYSTEM,
        isPremium = false,
        adminOnly = true,
        defaultEnabled = true
    ),

    ENCRYPTION(
        key = "encryption",
        displayName = "Data Encryption",
        description = "Encrypt user data at rest",
        category = FeatureCategory.SYSTEM,
        isPremium = false,
        adminOnly = true,
        defaultEnabled = true
    ),

    ACCESSIBILITY(
        key = "accessibility",
        displayName = "Accessibility Features",
        description = "Screen reader support, high contrast, etc.",
        category = FeatureCategory.SYSTEM,
        isPremium = false,
        defaultEnabled = true
    );

    companion object {
        fun fromKey(key: String): FeatureFlag? = values().find { it.key == key }

        fun getAllByCategory(category: FeatureCategory): List<FeatureFlag> =
            values().filter { it.category == category }

        fun getAllPremium(): List<FeatureFlag> =
            values().filter { it.isPremium }

        fun getAllWithCost(): List<FeatureFlag> =
            values().filter { it.hasCost }

        fun getAllUserConfigurable(): List<FeatureFlag> =
            values().filter { !it.adminOnly }

        fun getAllAdminOnly(): List<FeatureFlag> =
            values().filter { it.adminOnly }
    }
}

enum class FeatureCategory(val displayName: String) {
    CORE_LEARNING("Core Learning"),
    MULTIMEDIA("Multimedia"),
    CONTENT("Content & Examples"),
    QUIZ_TYPES("Quiz Types"),
    GAMIFICATION("Gamification"),
    SOCIAL("Social Features"),
    ADVANCED_INPUT("Advanced Input"),
    SYNC("Sync & Backup"),
    PLATFORM("Platform"),
    MONETIZATION("Monetization"),
    ANALYTICS("Analytics"),
    SYSTEM("System")
}

/**
 * Exception thrown when a disabled feature is accessed
 */
class FeatureDisabledException(val feature: FeatureFlag) :
    Exception("Feature '${feature.displayName}' is currently disabled")
