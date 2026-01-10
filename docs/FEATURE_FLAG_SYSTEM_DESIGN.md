# Feature Flag System - Technical Design Document

**Date:** 2026-01-10
**Version:** 1.0
**Purpose:** Comprehensive feature toggle system for cost control, user preferences, and gradual rollout

---

## 🎯 Overview

The Feature Flag System provides **three-tier control** over features:

1. **Global Admin Control** - Owner can enable/disable features globally (cost control, kill switch)
2. **User Preferences** - Users can opt-in/out of features they want
3. **Usage Tracking** - Monitor API calls, costs, and feature adoption

### Key Benefits:
- 💰 **Cost Control**: Disable expensive TTS, Speech Recognition, Image APIs
- 🧪 **A/B Testing**: Enable features for 10%, 50%, 100% of users
- 🚀 **Gradual Rollout**: Release features safely
- 🔒 **Kill Switch**: Instantly disable problematic features
- 📊 **Analytics**: Track feature usage and costs
- 👥 **User Choice**: Let users control their experience

---

## 🏗️ Architecture Design

### Tier 1: Feature Flag Definitions

```kotlin
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
```

---

## 🗄️ Database Schema

### Tier 2A: Global Admin Controls (Room Database)

```kotlin
@Entity(tableName = "feature_flags_global")
data class GlobalFeatureFlag(
    @PrimaryKey
    val featureKey: String,  // FeatureFlag.key

    val enabled: Boolean,  // Global on/off

    val rolloutPercentage: Int = 100,  // 0-100, for gradual rollout

    val maxDailyUsage: Int? = null,  // Max API calls per day (null = unlimited)

    val currentDailyUsage: Int = 0,  // Current usage today

    val lastResetDate: Long = System.currentTimeMillis(),  // For daily reset

    val totalCost: Double = 0.0,  // Estimated total cost ($)

    val notes: String? = null,  // Admin notes

    val lastModified: Long = System.currentTimeMillis(),

    val modifiedBy: String? = null  // Who changed it
)
```

### Tier 2B: User Preferences (Room Database)

```kotlin
@Entity(tableName = "feature_flags_user")
data class UserFeatureFlag(
    @PrimaryKey
    val featureKey: String,  // FeatureFlag.key

    val userEnabled: Boolean,  // User's preference

    val hasUsedFeature: Boolean = false,  // Track if ever used

    val usageCount: Int = 0,  // How many times used

    val lastUsed: Long? = null,  // Last usage timestamp

    val feedbackProvided: Boolean = false,  // Did user rate this feature?

    val feedbackRating: Int? = null  // 1-5 stars
)
```

### Tier 2C: Usage Tracking (Room Database)

```kotlin
@Entity(tableName = "feature_usage_log")
data class FeatureUsageLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val featureKey: String,

    val timestamp: Long = System.currentTimeMillis(),

    val apiCallsMade: Int = 0,  // # of API calls

    val estimatedCost: Double = 0.0,  // Cost for this usage

    val success: Boolean = true,  // Did it work?

    val errorMessage: String? = null  // If failed
)
```

---

## 🔧 Repository Layer

### Interface

```kotlin
interface IFeatureFlagRepository {
    // ========== Runtime Checks ==========
    suspend fun isFeatureEnabled(feature: FeatureFlag): Boolean
    suspend fun isFeatureEnabledForUser(feature: FeatureFlag): Boolean

    // ========== Admin Controls ==========
    suspend fun setGlobalFeatureEnabled(feature: FeatureFlag, enabled: Boolean)
    suspend fun setRolloutPercentage(feature: FeatureFlag, percentage: Int)
    suspend fun setMaxDailyUsage(feature: FeatureFlag, maxUsage: Int?)
    suspend fun getAllGlobalFlags(): Flow<List<GlobalFeatureFlag>>
    suspend fun resetDailyUsage()  // Called daily at midnight

    // ========== User Preferences ==========
    suspend fun setUserFeatureEnabled(feature: FeatureFlag, enabled: Boolean)
    suspend fun getUserFeatureFlags(): Flow<List<UserFeatureFlag>>
    suspend fun getUserFeaturePreference(feature: FeatureFlag): Boolean?

    // ========== Usage Tracking ==========
    suspend fun trackFeatureUsage(
        feature: FeatureFlag,
        apiCalls: Int = 0,
        estimatedCost: Double = 0.0,
        success: Boolean = true,
        errorMessage: String? = null
    )
    suspend fun getFeatureUsageStats(
        feature: FeatureFlag,
        startDate: Long,
        endDate: Long
    ): FeatureUsageStats

    // ========== Cost Management ==========
    suspend fun getTotalCostToday(): Double
    suspend fun getTotalCostThisMonth(): Double
    suspend fun getCostBreakdownByFeature(): Map<FeatureFlag, Double>

    // ========== Sync ==========
    suspend fun syncWithRemoteConfig()  // Firebase Remote Config
}

data class FeatureUsageStats(
    val totalCalls: Int,
    val totalCost: Double,
    val successRate: Double,  // % successful
    val uniqueUsers: Int
)
```

---

## 🎛️ Feature Flag Manager

### Main Service

```kotlin
@Singleton
class FeatureFlagManager @Inject constructor(
    private val repository: IFeatureFlagRepository,
    private val context: Context
) {
    // ========== Quick Check Methods ==========

    /**
     * Main method: Is feature enabled?
     * Checks: Global enabled + Rollout % + User preference + Daily limits
     */
    suspend fun isEnabled(feature: FeatureFlag): Boolean {
        // 1. Check global flag
        if (!repository.isFeatureEnabledForUser(feature)) {
            return false
        }

        // 2. Check user preference (if user-configurable)
        if (!feature.adminOnly) {
            val userPref = repository.getUserFeaturePreference(feature)
            if (userPref == false) {
                return false
            }
        }

        // 3. Check daily limits (for features with cost)
        if (feature.hasCost) {
            val globalFlag = repository.getAllGlobalFlags()
                .first()
                .find { it.featureKey == feature.key }

            globalFlag?.let {
                if (it.maxDailyUsage != null && it.currentDailyUsage >= it.maxDailyUsage) {
                    return false  // Limit reached
                }
            }
        }

        return true
    }

    /**
     * Require a feature to be enabled, throw if not
     */
    suspend fun requireFeature(feature: FeatureFlag) {
        if (!isEnabled(feature)) {
            throw FeatureDisabledException(feature)
        }
    }

    /**
     * Track usage after using a feature
     */
    suspend fun trackUsage(
        feature: FeatureFlag,
        apiCalls: Int = 0,
        estimatedCost: Double = 0.0
    ) {
        repository.trackFeatureUsage(feature, apiCalls, estimatedCost)
    }

    // ========== Composable Helpers ==========

    @Composable
    fun rememberFeatureEnabled(feature: FeatureFlag): State<Boolean> {
        return produceState(initialValue = false, feature) {
            value = isEnabled(feature)
        }
    }
}

class FeatureDisabledException(val feature: FeatureFlag) :
    Exception("Feature ${feature.displayName} is currently disabled")
```

---

## 🎨 UI Components

### Admin Dashboard

```kotlin
@Composable
fun AdminFeatureFlagDashboard(
    viewModel: FeatureFlagViewModel = hiltViewModel()
) {
    val globalFlags by viewModel.globalFlags.collectAsState()
    val costToday by viewModel.costToday.collectAsState()
    val costThisMonth by viewModel.costThisMonth.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Feature Flags (Admin)") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            // Cost Summary Card
            item {
                CostSummaryCard(
                    todayCost = costToday,
                    monthCost = costThisMonth
                )
            }

            // Features by Category
            FeatureCategory.values().forEach { category ->
                val featuresInCategory = FeatureFlag.getAllByCategory(category)

                item {
                    CategoryHeader(category = category)
                }

                items(featuresInCategory) { feature ->
                    AdminFeatureFlagItem(
                        feature = feature,
                        globalFlag = globalFlags.find { it.featureKey == feature.key },
                        onToggle = { viewModel.toggleGlobalFeature(feature) },
                        onRolloutChange = { percentage ->
                            viewModel.setRolloutPercentage(feature, percentage)
                        },
                        onDailyLimitChange = { limit ->
                            viewModel.setDailyLimit(feature, limit)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminFeatureFlagItem(
    feature: FeatureFlag,
    globalFlag: GlobalFeatureFlag?,
    onToggle: () -> Unit,
    onRolloutChange: (Int) -> Unit,
    onDailyLimitChange: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feature.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Badges
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        if (feature.isPremium) {
                            Badge { Text("PREMIUM") }
                        }
                        if (feature.hasCost) {
                            Badge { Text("💰 COST") }
                        }
                    }
                }

                Switch(
                    checked = globalFlag?.enabled ?: feature.defaultEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            // Stats Row
            if (globalFlag != null && globalFlag.enabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rollout: ${globalFlag.rolloutPercentage}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (feature.hasCost) {
                        Text(
                            text = "Today: ${globalFlag.currentDailyUsage}/${globalFlag.maxDailyUsage ?: "∞"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Cost: $${String.format("%.2f", globalFlag.totalCost)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Expandable Controls
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }

                if (expanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        // Rollout Percentage Slider
                        Text("Rollout Percentage")
                        Slider(
                            value = globalFlag.rolloutPercentage.toFloat(),
                            onValueChange = { onRolloutChange(it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 9  // 0, 10, 20, ..., 100
                        )

                        // Daily Limit (if has cost)
                        if (feature.hasCost) {
                            Text("Daily API Call Limit")
                            // ... limit input UI
                        }
                    }
                }
            }
        }
    }
}
```

### User Settings

```kotlin
@Composable
fun UserFeaturePreferences(
    viewModel: FeatureFlagViewModel = hiltViewModel()
) {
    val userFlags by viewModel.userFlags.collectAsState()

    LazyColumn {
        item {
            Text(
                "Feature Preferences",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                "Control which features you want to use",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Only show user-configurable features
        val userConfigurableFeatures = FeatureFlag.getAllUserConfigurable()

        FeatureCategory.values().forEach { category ->
            val featuresInCategory = userConfigurableFeatures.filter {
                it.category == category
            }

            if (featuresInCategory.isNotEmpty()) {
                item {
                    CategoryHeader(category = category)
                }

                items(featuresInCategory) { feature ->
                    UserFeatureFlagItem(
                        feature = feature,
                        userFlag = userFlags.find { it.featureKey == feature.key },
                        onToggle = { viewModel.toggleUserFeature(feature) }
                    )
                }
            }
        }
    }
}
```

---

## 🔌 Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FeatureFlagModule {

    @Provides
    @Singleton
    fun provideFeatureFlagDao(database: AppDatabase): FeatureFlagDao {
        return database.featureFlagDao()
    }

    @Provides
    @Singleton
    fun provideFeatureFlagRepository(
        dao: FeatureFlagDao
    ): IFeatureFlagRepository {
        return FeatureFlagRepository(dao)
    }

    @Provides
    @Singleton
    fun provideFeatureFlagManager(
        repository: IFeatureFlagRepository,
        @ApplicationContext context: Context
    ): FeatureFlagManager {
        return FeatureFlagManager(repository, context)
    }
}
```

---

## 📱 Usage Examples

### In ViewModels

```kotlin
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val featureFlags: FeatureFlagManager,
    // ... other dependencies
) : ViewModel() {

    fun playPronunciation(word: String) {
        viewModelScope.launch {
            if (featureFlags.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)) {
                try {
                    // Play audio
                    ttsService.speak(word)

                    // Track usage
                    featureFlags.trackUsage(
                        feature = FeatureFlag.AUDIO_PRONUNCIATION,
                        apiCalls = 1,
                        estimatedCost = 0.001  // $0.001 per TTS call
                    )
                } catch (e: Exception) {
                    featureFlags.trackUsage(
                        feature = FeatureFlag.AUDIO_PRONUNCIATION,
                        apiCalls = 1,
                        success = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}
```

### In Composables

```kotlin
@Composable
fun WordDetailScreen(
    word: Word,
    featureFlags: FeatureFlagManager = hiltViewModel()
) {
    val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)
    val imagesEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.IMAGES_VISUAL_AIDS)

    Column {
        Text(word.word, style = MaterialTheme.typography.headlineLarge)

        // Conditional UI based on feature flags
        if (audioEnabled) {
            IconButton(onClick = { /* play audio */ }) {
                Icon(Icons.Default.VolumeUp, "Play pronunciation")
            }
        }

        if (imagesEnabled) {
            AsyncImage(
                model = word.imageUrl,
                contentDescription = word.word
            )
        }
    }
}
```

---

## 🧪 Testing

```kotlin
@Test
fun `feature is disabled when global flag is off`() = runTest {
    // Given
    repository.setGlobalFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION, enabled = false)

    // When
    val isEnabled = featureFlags.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)

    // Then
    assertFalse(isEnabled)
}

@Test
fun `feature respects daily usage limit`() = runTest {
    // Given
    repository.setMaxDailyUsage(FeatureFlag.SPEECH_RECOGNITION, maxUsage = 100)
    repeat(100) {
        featureFlags.trackUsage(FeatureFlag.SPEECH_RECOGNITION, apiCalls = 1)
    }

    // When
    val isEnabled = featureFlags.isEnabled(FeatureFlag.SPEECH_RECOGNITION)

    // Then
    assertFalse(isEnabled)
}
```

---

## 🌐 Remote Config (Firebase)

```kotlin
class RemoteConfigSync @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val repository: IFeatureFlagRepository
) {
    suspend fun syncFlags() {
        remoteConfig.fetchAndActivate().await()

        FeatureFlag.values().forEach { feature ->
            val remoteEnabled = remoteConfig.getBoolean("feature_${feature.key}")
            val remoteRollout = remoteConfig.getLong("rollout_${feature.key}").toInt()

            repository.setGlobalFeatureEnabled(feature, remoteEnabled)
            repository.setRolloutPercentage(feature, remoteRollout)
        }
    }
}
```

---

## 📊 Cost Tracking

### Cost per API Call (Estimates)

| Feature | Service | Cost per Call | Daily Limit Suggestion |
|---------|---------|---------------|------------------------|
| Audio Pronunciation | Google TTS | $0.001 | 10,000 calls |
| Speech Recognition | Google Speech API | $0.006 | 1,000 calls |
| Images | Unsplash API | Free (5,000/hr) | Unlimited |
| AI Tutor | OpenAI GPT-4 | $0.03 | 100 calls |
| Video Content | YouTube/Vimeo | Storage cost | N/A |

### Budget Control

```kotlin
class BudgetMonitor @Inject constructor(
    private val repository: IFeatureFlagRepository
) {
    suspend fun checkDailyBudget(maxDailyBudget: Double = 10.0) {
        val currentCost = repository.getTotalCostToday()

        if (currentCost >= maxDailyBudget) {
            // Disable all costly features
            FeatureFlag.getAllWithCost().forEach { feature ->
                repository.setGlobalFeatureEnabled(feature, enabled = false)
            }

            // Send alert to admin
            sendBudgetAlert(currentCost, maxDailyBudget)
        }
    }
}
```

---

## 🚀 Implementation Phases

### Phase 1: Foundation (Week 1)
- [ ] Database schema (entities, DAOs)
- [ ] Repository implementation
- [ ] FeatureFlagManager service
- [ ] Basic unit tests

### Phase 2: Admin UI (Week 1-2)
- [ ] Admin dashboard screen
- [ ] Global toggle controls
- [ ] Rollout percentage controls
- [ ] Cost monitoring dashboard

### Phase 3: User UI (Week 2)
- [ ] User preferences screen
- [ ] Feature opt-in/out toggles
- [ ] Usage statistics for users

### Phase 4: Integration (Week 2-3)
- [ ] Remote config (Firebase)
- [ ] Usage tracking
- [ ] Cost monitoring
- [ ] Daily reset worker

### Phase 5: Polish (Week 3)
- [ ] Documentation
- [ ] Integration tests
- [ ] Performance optimization

---

## ✅ Success Metrics

1. **Control**: Admin can disable any feature in <5 seconds
2. **Visibility**: Real-time cost tracking with <1 minute delay
3. **Safety**: Budget limits prevent overspending
4. **Flexibility**: Users can opt-out of any feature
5. **Performance**: Feature checks add <1ms overhead

---

**Status:** Ready for Implementation
**Next Step:** Create database schema and entities
