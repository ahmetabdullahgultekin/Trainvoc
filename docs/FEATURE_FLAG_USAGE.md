# Feature Flag System - Developer Guide

**Version:** 1.0
**Date:** 2026-01-10
**Status:** Implemented (Core Infrastructure Complete)

---

## 🎯 Quick Start

### Check if a Feature is Enabled

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val featureFlags: FeatureFlagManager
) : ViewModel() {

    fun playAudio(word: String) {
        viewModelScope.launch {
            if (featureFlags.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)) {
                // Feature is enabled, use it
                ttsService.speak(word)

                // Track usage for cost monitoring
                featureFlags.trackUsage(
                    feature = FeatureFlag.AUDIO_PRONUNCIATION,
                    apiCalls = 1,
                    estimatedCost = 0.001  // $0.001 per TTS call
                )
            }
        }
    }
}
```

### Use in Compose UI

```kotlin
@Composable
fun WordDetailScreen(
    featureFlags: FeatureFlagManager = hiltViewModel()
) {
    val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)

    Column {
        if (audioEnabled) {
            IconButton(onClick = { /* play audio */ }) {
                Icon(Icons.Default.VolumeUp, "Play pronunciation")
            }
        }
    }
}
```

### Conditional UI with FeatureGate

```kotlin
@Composable
fun QuizScreen(featureFlags: FeatureFlagManager) {
    // Only show if feature is enabled
    FeatureGate(
        feature = FeatureFlag.IMAGES_VISUAL_AIDS,
        featureFlags = featureFlags
    ) {
        ImageFlashcard(imageUrl = word.imageUrl)
    }
}
```

---

## 📚 All Available Features

The system includes **45 feature flags** organized by category:

### Core Learning
- `SPACED_REPETITION` - SM-2 algorithm (✅ Active)
- `ADAPTIVE_DIFFICULTY` - AI difficulty adjustment (✅ Active, Premium)

### Multimedia
- `AUDIO_PRONUNCIATION` - TTS pronunciation (💰 Has Cost)
- `TEXT_TO_SPEECH` - Text-to-speech (💰 Has Cost)
- `IMAGES_VISUAL_AIDS` - Visual learning (💰 Has Cost)
- `IMAGE_FLASHCARDS` - Image-based flashcards
- `VIDEO_CONTENT` - Video lessons (💰 Has Cost, Premium)

### Content & Examples
- `EXAMPLE_SENTENCES` - Usage examples
- `USAGE_CONTEXT` - Formal/informal context
- `SYNONYMS_ANTONYMS` - Related words
- `GRAMMAR_TIPS` - Grammar explanations (Premium)
- `CULTURAL_NOTES` - Cultural context (Premium)

### Quiz Types
- `SENTENCE_QUIZZES` - Sentence-based quizzes

### Gamification
- `GAMIFICATION` - Full gamification system (✅ Active)
- `ACHIEVEMENTS` - Badge system (✅ Active)
- `STREAK_TRACKING` - Daily streaks (✅ Active)
- `VIRTUAL_CURRENCY` - Gems/coins

### Social Features
- `SOCIAL_FRIENDS` - Friend system (💰 Has Cost)
- `LEADERBOARDS` - Public rankings (💰 Has Cost)
- `SOCIAL_SHARING` - Share achievements
- `COMMUNITY_DECKS` - User-created content (💰 Has Cost)

### Advanced Input
- `SPEECH_RECOGNITION` - Speaking practice (💰 Has Cost, Premium)
- `PRONUNCIATION_SCORING` - Pronunciation feedback (💰 Has Cost, Premium)
- `VOICE_RECORDING` - Record yourself (Premium)
- `AI_TUTOR` - ChatGPT-style tutor (💰 Has Cost, Premium)
- `HANDWRITING_INPUT` - Draw characters (Premium)

### Platform
- `HOME_SCREEN_WIDGETS` - Home screen widgets

### Sync & Backup
- `CLOUD_BACKUP` - Google Drive backup (✅ Active, Premium)
- `OFFLINE_MODE` - Study without internet
- `OFFLINE_AUDIO_CACHE` - Download audio (Premium)
- `OFFLINE_IMAGE_CACHE` - Download images (Premium)

### Monetization
- `PREMIUM_SUBSCRIPTION` - Subscription system (🔒 Admin Only)
- `IN_APP_PURCHASES` - IAP system (🔒 Admin Only)
- `ADVERTISEMENTS` - Ad system (🔒 Admin Only, Disabled by Default)

### Analytics
- `LEARNING_ANALYTICS` - Progress insights (✅ Active, Premium)

### System
- `PERFORMANCE_MONITORING` - Performance tracking (🔒 Admin Only)
- `GDPR_COMPLIANCE` - Data privacy (🔒 Admin Only, ✅ Always On)
- `ENCRYPTION` - Data encryption (🔒 Admin Only, ✅ Always On)
- `ACCESSIBILITY` - Accessibility features (✅ Active)

**Legend:**
- ✅ Active - Currently implemented and enabled
- 💰 Has Cost - Feature uses paid APIs (tracked for billing)
- 🔒 Admin Only - Only admin can enable/disable
- Premium - Requires premium subscription

---

## 💻 Usage Patterns

### Pattern 1: Simple Check

```kotlin
suspend fun useFeature() {
    if (featureFlags.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)) {
        // Use feature
    }
}
```

### Pattern 2: Require Feature (Throws Exception)

```kotlin
suspend fun criticalOperation() {
    // Will throw FeatureDisabledException if disabled
    featureFlags.requireFeature(FeatureFlag.PREMIUM_SUBSCRIPTION)

    // Continue with premium feature
    unlockPremiumContent()
}
```

### Pattern 3: With Feature (Returns null if disabled)

```kotlin
val result = featureFlags.withFeature(FeatureFlag.AI_TUTOR) {
    aiTutor.getResponse(question)
}

if (result != null) {
    // Feature was enabled, got response
} else {
    // Feature disabled, show upgrade prompt
}
```

### Pattern 4: If-Else

```kotlin
featureFlags.ifFeature(
    feature = FeatureFlag.IMAGES_VISUAL_AIDS,
    enabled = {
        showImageFlashcard()
    },
    disabled = {
        showTextOnlyFlashcard()
    }
)
```

### Pattern 5: Execute with Auto-Tracking

```kotlin
val result = featureFlags.executeWithTracking(
    feature = FeatureFlag.SPEECH_RECOGNITION,
    apiCalls = 1,
    estimatedCost = 0.006
) {
    speechRecognition.recognize(audioData)
}

result.onSuccess { text ->
    // Success automatically tracked
}

result.onFailure { error ->
    // Failure automatically tracked with error message
}
```

---

## 🎛️ Admin Controls

### Toggle Feature Globally

```kotlin
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: IFeatureFlagRepository
) : ViewModel() {

    fun disableExpensiveFeature() {
        viewModelScope.launch {
            repository.setGlobalFeatureEnabled(
                FeatureFlag.SPEECH_RECOGNITION,
                enabled = false
            )
        }
    }
}
```

### Set Rollout Percentage (A/B Testing)

```kotlin
// Enable for 50% of users
repository.setRolloutPercentage(
    FeatureFlag.NEW_QUIZ_MODE,
    percentage = 50
)
```

### Set Daily Limits (Cost Control)

```kotlin
// Max 1000 API calls per day
repository.setMaxDailyUsage(
    FeatureFlag.SPEECH_RECOGNITION,
    maxUsage = 1000
)
```

### Monitor Costs

```kotlin
val costToday = repository.getTotalCostToday()
val costThisMonth = repository.getTotalCostThisMonth()
val breakdown = repository.getCostBreakdownByFeature()

breakdown.forEach { (feature, cost) ->
    println("${feature.displayName}: $$cost")
}
```

---

## 👤 User Preferences

Users can opt-in/out of features (except admin-only features):

```kotlin
// User disables audio (saves battery)
repository.setUserFeatureEnabled(
    FeatureFlag.AUDIO_PRONUNCIATION,
    enabled = false
)
```

---

## 📊 Usage Tracking

### Track Successful Usage

```kotlin
featureFlags.trackUsage(
    feature = FeatureFlag.TEXT_TO_SPEECH,
    apiCalls = 1,
    estimatedCost = 0.001
)
```

### Track Failed Usage

```kotlin
featureFlags.trackFailure(
    feature = FeatureFlag.SPEECH_RECOGNITION,
    errorMessage = "Network timeout",
    apiCalls = 1,
    estimatedCost = 0.006
)
```

### Get Usage Statistics

```kotlin
val stats = repository.getFeatureUsageStats(
    feature = FeatureFlag.AI_TUTOR,
    startDate = startOfMonth,
    endDate = now
)

println("Total calls: ${stats.totalCalls}")
println("Total cost: $${stats.totalCost}")
println("Success rate: ${stats.successRate * 100}%")
```

---

## 🏗️ Architecture

### Components

```
┌─────────────────────────────────────────┐
│          Application Layer               │
│  ┌─────────────────────────────────┐   │
│  │  FeatureFlagManager (Service)   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                   ▼
┌─────────────────────────────────────────┐
│         Repository Layer                 │
│  ┌─────────────────────────────────┐   │
│  │  IFeatureFlagRepository         │   │
│  │  FeatureFlagRepository (Impl)   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                   ▼
┌─────────────────────────────────────────┐
│          Data Layer                      │
│  ┌─────────────────────────────────┐   │
│  │  FeatureFlagDao (Room)          │   │
│  └─────────────────────────────────┘   │
│  ┌─────────────────────────────────┐   │
│  │  GlobalFeatureFlag (Entity)     │   │
│  │  UserFeatureFlag (Entity)       │   │
│  │  FeatureUsageLog (Entity)       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### Database Tables

#### 1. `feature_flags_global` (Admin Controls)
```sql
CREATE TABLE feature_flags_global (
    feature_key TEXT PRIMARY KEY,
    enabled BOOLEAN,
    rollout_percentage INT,      -- 0-100 for A/B testing
    max_daily_usage INT,          -- Daily API call limit
    current_daily_usage INT,      -- Current usage today
    last_reset_date TIMESTAMP,
    total_cost REAL,              -- Total cost ($)
    notes TEXT,
    last_modified TIMESTAMP,
    modified_by TEXT
)
```

#### 2. `feature_flags_user` (User Preferences)
```sql
CREATE TABLE feature_flags_user (
    feature_key TEXT PRIMARY KEY,
    user_enabled BOOLEAN,         -- User's preference
    has_used_feature BOOLEAN,     -- Ever used?
    usage_count INT,              -- Times used
    last_used TIMESTAMP,
    feedback_provided BOOLEAN,
    feedback_rating INT           -- 1-5 stars
)
```

#### 3. `feature_usage_log` (Analytics & Cost Tracking)
```sql
CREATE TABLE feature_usage_log (
    id INTEGER PRIMARY KEY,
    feature_key TEXT,
    timestamp TIMESTAMP,
    api_calls_made INT,
    estimated_cost REAL,
    success BOOLEAN,
    error_message TEXT,
    user_id TEXT
)
```

---

## 🔄 Daily Usage Reset

Automatically resets at midnight via WorkManager:

```kotlin
// Scheduled in TrainvocApplication.onCreate()
DailyUsageResetWorker.schedule(context)
```

Manual reset:

```kotlin
featureFlagManager.resetDailyUsage()
```

---

## 🧪 Testing

### Unit Test Example

```kotlin
@Test
fun `feature disabled when global flag is off`() = runTest {
    // Given
    repository.setGlobalFeatureEnabled(
        FeatureFlag.AUDIO_PRONUNCIATION,
        enabled = false
    )

    // When
    val isEnabled = featureFlags.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)

    // Then
    assertFalse(isEnabled)
}

@Test
fun `feature respects daily usage limit`() = runTest {
    // Given
    repository.setMaxDailyUsage(FeatureFlag.SPEECH_RECOGNITION, maxUsage = 10)

    // Use it 10 times
    repeat(10) {
        featureFlags.trackUsage(
            FeatureFlag.SPEECH_RECOGNITION,
            apiCalls = 1
        )
    }

    // When
    val isEnabled = featureFlags.isEnabled(FeatureFlag.SPEECH_RECOGNITION)

    // Then
    assertFalse(isEnabled) // Limit reached
}
```

---

## 💡 Best Practices

### 1. Always Track Usage for Costly Features

```kotlin
if (featureFlags.isEnabled(FeatureFlag.AI_TUTOR)) {
    val response = aiService.chat(message)

    // IMPORTANT: Track usage for cost monitoring
    featureFlags.trackUsage(
        feature = FeatureFlag.AI_TUTOR,
        apiCalls = 1,
        estimatedCost = 0.03  // GPT-4 cost
    )
}
```

### 2. Use Composable Helpers

```kotlin
// Good: Reactive to changes
val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)

// Avoid: Manual checking in Composable
```

### 3. Handle Disabled Features Gracefully

```kotlin
// Good: Provide fallback
FeatureSwitch(
    feature = FeatureFlag.IMAGES_VISUAL_AIDS,
    featureFlags = featureFlags,
    enabled = { ImageCard() },
    disabled = { TextCard() }
)

// Avoid: Crash or blank screen
```

### 4. Set Realistic Daily Limits

```kotlin
// Good: Based on budget
// TTS: $0.001 per call, $10/day budget = 10,000 calls
repository.setMaxDailyUsage(FeatureFlag.TEXT_TO_SPEECH, 10000)

// Bad: Too low or unlimited for expensive features
```

---

## 🚀 Initialization

The system is automatically initialized in `TrainvocApplication.onCreate()`:

```kotlin
@HiltAndroidApp
class TrainvocApplication : Application() {

    @Inject
    lateinit var featureFlagManager: FeatureFlagManager

    override fun onCreate() {
        super.onCreate()

        // Initialize feature flags in background
        StartupOptimizer.initInBackground {
            featureFlagManager.initialize()
            DailyUsageResetWorker.schedule(this)
        }
    }
}
```

No manual initialization needed!

---

## 📦 What's Included

### ✅ Implemented (Core Infrastructure)

- [x] Database schema (3 tables)
- [x] DAO with all queries
- [x] Repository interface and implementation
- [x] FeatureFlagManager service
- [x] Dependency Injection (Hilt modules)
- [x] WorkManager for daily reset
- [x] Application initialization
- [x] ViewModel for UI
- [x] Composable helpers (FeatureGate, FeatureSwitch)
- [x] 45 feature flag definitions
- [x] Cost tracking and monitoring
- [x] User preferences system
- [x] A/B testing support (rollout percentage)

### 🚧 TODO (UI Screens)

- [ ] Admin Dashboard UI (global controls)
- [ ] User Settings UI (preferences)
- [ ] Cost monitoring dashboard
- [ ] Feature analytics screen

---

## 🎨 UI Screens (To Be Implemented)

### Admin Dashboard
- List all features by category
- Toggle features on/off
- Set rollout percentage sliders
- Set daily API limits
- View cost breakdowns
- Quick actions: "Disable All Expensive Features"

### User Preferences
- List user-configurable features
- Toggle features on/off
- See usage statistics
- Provide feedback on features

---

## 📝 Example: Adding a New Feature

1. **Define the feature** in `FeatureFlag.kt`:

```kotlin
NEW_FEATURE(
    key = "new_feature",
    displayName = "New Amazing Feature",
    description = "Does something awesome",
    category = FeatureCategory.CORE_LEARNING,
    isPremium = true,
    hasCost = true,
    defaultEnabled = false  // Start disabled
)
```

2. **Use the feature**:

```kotlin
if (featureFlags.isEnabled(FeatureFlag.NEW_FEATURE)) {
    doAmazingThing()
    featureFlags.trackUsage(
        FeatureFlag.NEW_FEATURE,
        apiCalls = 1,
        estimatedCost = 0.01
    )
}
```

3. **Enable via admin dashboard** when ready for rollout

4. **Monitor usage and costs** in admin dashboard

---

## 🔐 Security & Privacy

- All feature flags stored in encrypted database (Room)
- User preferences are per-device (not synced by default)
- Admin-only features cannot be toggled by users
- Usage tracking is anonymous (no PII collected)
- GDPR compliant - users can export/delete their preference data

---

## 💰 Cost Estimates

| Feature | API Service | Cost per Call | Suggested Daily Limit |
|---------|-------------|---------------|----------------------|
| Text-to-Speech | Google TTS | $0.001 | 10,000 |
| Speech Recognition | Google Speech | $0.006 | 1,000 |
| AI Tutor | OpenAI GPT-4 | $0.03 | 100 |
| Images | Unsplash | Free (5,000/hr) | Unlimited |
| Video Content | Storage | Storage cost | N/A |

**Monthly budget example:**
- TTS: 10,000 calls/day × 30 days × $0.001 = $300/month
- Speech: 1,000 calls/day × 30 days × $0.006 = $180/month
- AI Tutor: 100 calls/day × 30 days × $0.03 = $90/month
- **Total:** ~$570/month

Use daily limits to stay within budget!

---

## 🐛 Troubleshooting

### Feature always disabled

Check:
1. Global flag enabled? `repository.getGlobalFlag(feature)?.enabled`
2. Within rollout percentage?
3. User preference enabled? (if not admin-only)
4. Daily limit not reached? (if has cost)

### Daily limits not resetting

Check:
1. WorkManager scheduled? `DailyUsageResetWorker.schedule(context)`
2. Check logs for worker execution
3. Manual reset: `featureFlagManager.resetDailyUsage()`

### Costs not tracking

Ensure you call `featureFlags.trackUsage()` after using the feature!

---

**Ready to implement Phase 1 features (Audio, Images, Examples, Offline) with full cost control! 🚀**
