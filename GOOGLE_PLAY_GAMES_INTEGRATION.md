# Google Play Games Services Integration Guide

## Overview

This guide covers the complete integration of Google Play Games Services into Trainvoc, including:
- ✅ Cloud save/load with automatic conflict resolution
- ✅ 44 achievements mapped and ready to unlock
- ✅ 6 leaderboards for competitive features
- ✅ Automatic sign-in flow
- ✅ Progress sync across devices

## Architecture

### Core Components

```
PlayGamesManager (Main Coordinator)
├── PlayGamesSignInManager (Authentication)
├── PlayGamesCloudSyncManager (Cloud Saves)
└── PlayGamesAchievementMapper (Achievement IDs)
```

### Files Created

1. **playgames/PlayGamesManager.kt** - Main coordinator for all Play Games features
2. **playgames/PlayGamesSignInManager.kt** - Sign-in flow management
3. **playgames/PlayGamesCloudSyncManager.kt** - Cloud save/load with conflict resolution
4. **playgames/PlayGamesAchievementMapper.kt** - Maps 44 achievements to Play Games IDs

### Files Modified

1. **gradle/libs.versions.toml** - Added Play Games dependency
2. **app/build.gradle.kts** - Added implementation line
3. **gamification/GamificationDao.kt** - Added upsert methods for cloud sync

---

## 📋 Setup Instructions

### Step 1: Google Play Console Setup

1. **Create Play Console App** (if not already done)
   - Go to https://play.google.com/console
   - Create or select your app
   - Navigate to "Play Games Services" → "Setup and management" → "Configuration"

2. **Link Your App**
   - Add OAuth 2.0 client
   - Get SHA-1 fingerprint: `./gradlew signingReport`
   - Add package name: `com.gultekinahmetabdullah.trainvoc`

3. **Enable Play Games Services**
   - Navigate to "Play Games Services"
   - Click "Setup" and follow the wizard

### Step 2: Create Achievements in Play Console

Navigate to Play Games Services → Achievements → Create achievement

Create all 44 achievements with these specifications:

#### Streak Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| STREAK_3 | 3-Day Streak | Complete 3 consecutive days | Standard | - |
| STREAK_7 | Week Warrior | Complete 7 consecutive days | Standard | - |
| STREAK_30 | Month Master | Complete 30 consecutive days | Standard | - |
| STREAK_100 | Centurion | Complete 100 consecutive days | Standard | - |
| STREAK_365 | Year Champion | Complete 365 consecutive days | Standard | - |

#### Word Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| WORDS_10 | First Ten | Learn 10 words | Incremental | 10 |
| WORDS_50 | Half Century | Learn 50 words | Incremental | 50 |
| WORDS_100 | Centurion Learner | Learn 100 words | Incremental | 100 |
| WORDS_500 | Vocabulary Builder | Learn 500 words | Incremental | 500 |
| WORDS_1000 | Word Master | Learn 1000 words | Incremental | 1000 |
| WORDS_5000 | Polyglot | Learn 5000 words | Incremental | 5000 |

#### Quiz Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| QUIZ_10 | Quiz Novice | Complete 10 quizzes | Incremental | 10 |
| QUIZ_50 | Quiz Enthusiast | Complete 50 quizzes | Incremental | 50 |
| QUIZ_100 | Quiz Expert | Complete 100 quizzes | Incremental | 100 |
| QUIZ_PERFECT_FIRST | Perfect First Try | Score 100% on first quiz | Standard | - |

#### Review Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| REVIEW_10 | Reviewing Started | Complete 10 reviews | Incremental | 10 |
| REVIEW_100 | Review Regular | Complete 100 reviews | Incremental | 100 |
| REVIEW_1000 | Review Master | Complete 1000 reviews | Incremental | 1000 |

#### Daily Goal Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| DAILY_GOAL_FIRST | First Goal | Complete your first daily goal | Standard | - |
| DAILY_GOAL_7 | Week of Goals | Complete 7 daily goals | Incremental | 7 |
| DAILY_GOAL_30 | Month of Goals | Complete 30 daily goals | Incremental | 30 |
| DAILY_GOAL_100 | Goal Champion | Complete 100 daily goals | Incremental | 100 |

#### Speed Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| SPEED_DEMON | Speed Demon | Answer 10 questions in under 3 seconds each | Standard | - |
| LIGHTNING_FAST | Lightning Fast | Complete quiz in record time | Standard | - |

#### Accuracy Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| ACCURACY_80 | Accurate Learner | Maintain 80% accuracy over 50 quizzes | Standard | - |
| ACCURACY_90 | Precision Expert | Maintain 90% accuracy over 100 quizzes | Standard | - |
| ACCURACY_95 | Perfection Seeker | Maintain 95% accuracy over 50 quizzes | Standard | - |

#### Memory Game Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| FLIP_CARDS_FIRST | Card Flipper | Complete first flip cards game | Standard | - |
| FLIP_CARDS_PERFECT | Perfect Match | Complete flip cards with no mistakes | Standard | - |
| SPEED_MATCH_FIRST | Speed Matcher | Complete first speed match game | Standard | - |
| SPEED_MATCH_COMBO_10 | Combo Master | Achieve 10-combo in speed match | Standard | - |
| SRS_MASTER_10 | SRS Beginner | Master 10 words using SRS | Incremental | 10 |
| SRS_MASTER_100 | SRS Expert | Master 100 words using SRS | Incremental | 100 |

#### Level Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| LEVEL_UP_A1 | A1 Complete | Complete all A1 words | Standard | - |
| LEVEL_UP_A2 | A2 Complete | Complete all A2 words | Standard | - |
| LEVEL_UP_B1 | B1 Complete | Complete all B1 words | Standard | - |
| LEVEL_UP_B2 | B2 Complete | Complete all B2 words | Standard | - |
| LEVEL_UP_C1 | C1 Complete | Complete all C1 words | Standard | - |
| LEVEL_UP_C2 | C2 Complete | Complete all C2 words | Standard | - |

#### Special Achievements
| Achievement | Name | Description | Type | Steps |
|------------|------|-------------|------|-------|
| EARLY_BIRD | Early Bird | Complete goal before 9 AM | Standard | - |
| NIGHT_OWL | Night Owl | Complete goal after 10 PM | Standard | - |
| WEEKEND_WARRIOR | Weekend Warrior | Complete goals on 10 weekends | Incremental | 10 |
| COMEBACK | Comeback Kid | Restore streak after break | Standard | - |
| GRANDMASTER | Grandmaster | Unlock all other achievements | Standard | - |

**IMPORTANT:** After creating each achievement in Play Console:
1. Copy the achievement ID (format: `CgkI...`)
2. Replace the placeholder ID in `PlayGamesAchievementMapper.kt`

### Step 3: Create Leaderboards in Play Console

Navigate to Play Games Services → Leaderboards → Create leaderboard

Create all 6 leaderboards:

| Leaderboard | Name | Description | Score Order | Format |
|-------------|------|-------------|-------------|---------|
| TOTAL_WORDS | Total Words Learned | Lifetime word count | Larger is better | Numeric |
| LONGEST_STREAK | Longest Streak | Best consecutive days | Larger is better | Numeric |
| TOTAL_QUIZZES | Total Quizzes Completed | Lifetime quiz count | Larger is better | Numeric |
| SPEED_MATCH_BEST | Speed Match Best Time | Fastest completion | Smaller is better | Time (milliseconds) |
| FLIP_CARDS_BEST | Flip Cards Best Moves | Fewest moves to complete | Smaller is better | Numeric |
| WEEKLY_WORDS | Weekly Words Learned | Words learned this week | Larger is better | Numeric |

**IMPORTANT:** After creating each leaderboard:
1. Copy the leaderboard ID
2. Update `PlayGamesManager.kt` with the real IDs

### Step 4: Update Achievement IDs in Code

Open `app/src/main/java/com/gultekinahmetabdullah/trainvoc/playgames/PlayGamesAchievementMapper.kt`

Replace ALL placeholder IDs with real IDs from Play Console:

```kotlin
object PlayGamesAchievementMapper {
    fun getPlayGamesId(achievement: Achievement): String {
        return when (achievement) {
            // Replace these with REAL IDs from Play Console:
            Achievement.STREAK_3 -> "CgkI_REAL_ID_FROM_CONSOLE_1"
            Achievement.STREAK_7 -> "CgkI_REAL_ID_FROM_CONSOLE_2"
            // ... continue for all 44 achievements
        }
    }
}
```

### Step 5: Update Leaderboard IDs in Code

Open `app/src/main/java/com/gultekinahmetabdullah/trainvoc/playgames/PlayGamesManager.kt`

Add companion object with real leaderboard IDs:

```kotlin
@Singleton
class PlayGamesManager @Inject constructor(...) {

    companion object {
        // Replace with real IDs from Play Console
        private const val LEADERBOARD_TOTAL_WORDS = "CgkI_REAL_LEADERBOARD_ID_1"
        private const val LEADERBOARD_LONGEST_STREAK = "CgkI_REAL_LEADERBOARD_ID_2"
        private const val LEADERBOARD_TOTAL_QUIZZES = "CgkI_REAL_LEADERBOARD_ID_3"
        private const val LEADERBOARD_SPEED_MATCH = "CgkI_REAL_LEADERBOARD_ID_4"
        private const val LEADERBOARD_FLIP_CARDS = "CgkI_REAL_LEADERBOARD_ID_5"
        private const val LEADERBOARD_WEEKLY_WORDS = "CgkI_REAL_LEADERBOARD_ID_6"
    }

    suspend fun postScore(leaderboardType: LeaderboardType, score: Long): Result<Unit> {
        // Update to use real IDs
        val leaderboardId = when (leaderboardType) {
            LeaderboardType.TOTAL_WORDS -> LEADERBOARD_TOTAL_WORDS
            LeaderboardType.LONGEST_STREAK -> LEADERBOARD_LONGEST_STREAK
            LeaderboardType.TOTAL_QUIZZES -> LEADERBOARD_TOTAL_QUIZZES
            LeaderboardType.SPEED_MATCH_BEST -> LEADERBOARD_SPEED_MATCH
            LeaderboardType.FLIP_CARDS_BEST -> LEADERBOARD_FLIP_CARDS
            LeaderboardType.WEEKLY_WORDS -> LEADERBOARD_WEEKLY_WORDS
        }
        // ... rest of code
    }
}
```

---

## 🔌 Integration Guide

### Initialize in Application Class

```kotlin
@HiltAndroidApp
class TrainvocApplication : Application() {

    @Inject
    lateinit var playGamesManager: PlayGamesManager

    override fun onCreate() {
        super.onCreate()

        // Initialize Play Games SDK
        playGamesManager.initialize()
    }
}
```

### Sign-In Flow in MainActivity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playGamesManager: PlayGamesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Try silent sign-in on app start
        lifecycleScope.launch {
            val signedIn = playGamesManager.signInSilently()
            if (signedIn) {
                // Automatically sync progress
                playGamesManager.syncProgress()
            }
        }

        setContent {
            TrainvocTheme {
                // Your UI
                PlayGamesIntegrationExample()
            }
        }
    }
}
```

### UI Example: Sign-In Button

```kotlin
@Composable
fun PlayGamesIntegrationExample() {
    val scope = rememberCoroutineScope()
    val playGamesManager = hiltViewModel<PlayGamesViewModel>().playGamesManager

    var playerInfo by remember { mutableStateOf<PlayerInfo?>(null) }
    var isAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAuthenticated = playGamesManager.isAuthenticated()
        if (isAuthenticated) {
            playerInfo = playGamesManager.getPlayerInfo()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isAuthenticated && playerInfo != null) {
            Text("Welcome, ${playerInfo.displayName}!")
            Text("Player ID: ${playerInfo.playerId}")

            Button(onClick = {
                scope.launch {
                    playGamesManager.signOut()
                    isAuthenticated = false
                    playerInfo = null
                }
            }) {
                Text("Sign Out")
            }

            Button(onClick = {
                scope.launch {
                    playGamesManager.showAchievementsUI()
                }
            }) {
                Text("View Achievements")
            }

            Button(onClick = {
                scope.launch {
                    playGamesManager.showAllLeaderboardsUI()
                }
            }) {
                Text("View Leaderboards")
            }

            Button(onClick = {
                scope.launch {
                    val result = playGamesManager.syncProgress()
                    // Handle result
                }
            }) {
                Text("Sync Progress")
            }
        } else {
            Button(onClick = {
                scope.launch {
                    val result = playGamesManager.signIn()
                    if (result.isSuccess) {
                        isAuthenticated = true
                        playerInfo = playGamesManager.getPlayerInfo()
                    }
                }
            }) {
                Text("Sign in with Google Play")
            }
        }
    }
}
```

### Hook Achievements to GamificationManager

Update your `GamificationManager` to unlock Play Games achievements:

```kotlin
@Singleton
class GamificationManager @Inject constructor(
    private val dao: GamificationDao,
    private val playGamesManager: PlayGamesManager
) {

    suspend fun unlockAchievement(achievement: Achievement) {
        // 1. Unlock locally
        val userAchievement = UserAchievement(
            achievementId = achievement.id,
            progress = achievement.maxProgress,
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )
        dao.insertAchievement(userAchievement)

        // 2. Unlock in Play Games
        if (playGamesManager.isAuthenticated()) {
            playGamesManager.unlockAchievement(achievement)
        }
    }

    suspend fun incrementAchievementProgress(achievement: Achievement, steps: Int = 1) {
        // 1. Update locally
        val current = dao.getAchievement(achievementId = achievement.id)
        val newProgress = (current?.progress ?: 0) + steps
        val isUnlocked = newProgress >= achievement.maxProgress

        dao.updateAchievementProgress(
            achievementId = achievement.id,
            progress = newProgress,
            isUnlocked = isUnlocked,
            unlockedAt = if (isUnlocked) System.currentTimeMillis() else null
        )

        // 2. Increment in Play Games
        if (playGamesManager.isAuthenticated()) {
            playGamesManager.incrementAchievement(achievement, steps)
        }
    }
}
```

### Post Scores to Leaderboards

After completing games:

```kotlin
// After completing a quiz
suspend fun onQuizCompleted(score: Int, totalQuestions: Int) {
    // Save locally
    val session = GameSession(
        gameType = "multiple_choice",
        totalQuestions = totalQuestions,
        correctAnswers = score,
        completedAt = System.currentTimeMillis()
    )
    gamesDao.insertGameSession(session)

    // Post to leaderboard
    if (playGamesManager.isAuthenticated()) {
        // Update total quizzes leaderboard
        val totalQuizzes = gamesDao.getCompletedGamesCount(gameType = "multiple_choice")
        playGamesManager.postScore(LeaderboardType.TOTAL_QUIZZES, totalQuizzes.toLong())
    }
}

// After learning new words
suspend fun onWordsLearned(count: Int) {
    // Update local stats
    dao.incrementWordsToday()

    // Post to leaderboard
    if (playGamesManager.isAuthenticated()) {
        val totalWords = wordDao.getTotalLearnedWords()
        playGamesManager.postScore(LeaderboardType.TOTAL_WORDS, totalWords.toLong())
    }
}

// After completing flip cards game
suspend fun onFlipCardsCompleted(moves: Int, gridSize: String) {
    val stats = FlipCardGameStats(
        gridSize = gridSize,
        totalPairs = getTotalPairs(gridSize),
        moves = moves,
        timeSeconds = elapsedSeconds,
        completed = true
    )
    gamesDao.insertFlipCardStats(stats)

    // Post best score to leaderboard
    if (playGamesManager.isAuthenticated()) {
        val bestMoves = gamesDao.getBestMoves(gridSize = gridSize) ?: Int.MAX_VALUE
        playGamesManager.postScore(LeaderboardType.FLIP_CARDS_BEST, bestMoves.toLong())
    }
}
```

### Automatic Cloud Sync

Set up automatic sync on app start and when user signs in:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playGamesManager: PlayGamesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Silent sign-in
            val signedIn = playGamesManager.signInSilently()

            if (signedIn) {
                // Automatic sync
                when (val result = playGamesManager.syncProgress()) {
                    is SyncResult.UploadedToCloud -> {
                        Log.d("Sync", "Progress uploaded to cloud")
                    }
                    is SyncResult.DownloadedFromCloud -> {
                        Log.d("Sync", "Progress downloaded from cloud")
                        // Optionally show notification: "Progress restored from cloud"
                    }
                    is SyncResult.AlreadySynced -> {
                        Log.d("Sync", "Already synced")
                    }
                }
            }
        }
    }
}
```

---

## 🧪 Testing Checklist

### Sign-In Flow
- [ ] App starts and attempts silent sign-in
- [ ] User can manually sign in via button
- [ ] Player name and ID display correctly
- [ ] Sign-out works and clears player info
- [ ] Re-sign-in after sign-out works

### Achievements
- [ ] Unlocking local achievement also unlocks Play Games achievement
- [ ] Incremental achievements progress correctly
- [ ] Achievements UI shows all 44 achievements
- [ ] Achievement notifications appear when unlocked
- [ ] Achievement icons display correctly

### Leaderboards
- [ ] Scores post to correct leaderboards
- [ ] Leaderboard UI opens and displays scores
- [ ] Weekly leaderboard resets properly
- [ ] Best times show in smaller-is-better leaderboards

### Cloud Sync
- [ ] Progress saves to cloud after sign-in
- [ ] Progress loads from cloud on new device
- [ ] Conflict resolution works (newest wins)
- [ ] Sync status shows correct state (uploaded/downloaded/synced)
- [ ] Sync works after internet reconnection

### Edge Cases
- [ ] Offline mode: local achievements work without internet
- [ ] Network error handling: graceful failure messages
- [ ] Multiple device sync: changes propagate correctly
- [ ] Sign-out: local progress preserved
- [ ] First-time user: creates cloud save successfully

---

## 📊 Cloud Save Data Structure

The `CloudSaveData` model saves the following:

```kotlin
data class CloudSaveData(
    val timestamp: Long,              // Sync timestamp for conflict resolution
    val version: Int,                 // Save format version (currently 1)

    // Gamification
    val streakTracking: StreakTracking?,
    val dailyGoal: DailyGoal?,
    val achievements: List<UserAchievement>,

    // Memory Games
    val srsCards: List<SRSCard>,

    // Statistics (optional, for future use)
    val totalWordsLearned: Int = 0,
    val totalQuizzesCompleted: Int = 0,
    val totalReviewsCompleted: Int = 0
)
```

**Maximum size:** 3 MB per save (plenty for our data)

---

## 🔒 Privacy & Security

### Data Stored in Cloud
- User progress (streaks, daily goals, achievements)
- Game statistics (SRS cards, game sessions)
- NO personal information beyond what Play Games already has
- NO passwords or sensitive data

### User Control
- User must explicitly sign in
- User can sign out at any time
- Local data preserved even after sign-out
- User can view/delete cloud saves via Play Console

### Best Practices
- Always check `isAuthenticated()` before Play Games operations
- Handle `Result.failure()` gracefully with user-friendly messages
- Never block UI on sync operations (use coroutines)
- Respect user's offline mode

---

## 🐛 Troubleshooting

### Problem: Sign-in fails with "Developer Error"
**Solution:**
1. Verify SHA-1 fingerprint matches in Play Console
2. Check package name is correct: `com.gultekinahmetabdullah.trainvoc`
3. Ensure app is published (at least to internal testing)
4. Wait 24 hours after Play Console configuration

### Problem: Achievements don't unlock
**Solution:**
1. Verify achievement IDs match Play Console exactly
2. Check `isAuthenticated()` returns true
3. Verify Play Games Services is enabled in Play Console
4. Check logs for error messages

### Problem: Cloud save fails
**Solution:**
1. Check internet connection
2. Verify user is signed in
3. Ensure data size < 3 MB
4. Check Play Games permissions in AndroidManifest.xml

### Problem: Leaderboard scores don't update
**Solution:**
1. Verify leaderboard IDs match Play Console
2. Check score format (larger/smaller is better)
3. Ensure score is valid (not negative for numeric)
4. Wait a few minutes for propagation

---

## 📝 Summary

### ✅ What's Complete
- Sign-in/sign-out flow
- 44 achievements mapped
- 6 leaderboards defined
- Cloud save/load with conflict resolution
- Automatic sync on app start
- Dependency injection with Hilt
- Error handling with Result types

### ⏳ What Needs Configuration
1. Create 44 achievements in Play Console
2. Create 6 leaderboards in Play Console
3. Replace placeholder IDs in code
4. Test on real device with Play Store build
5. Publish to at least internal testing

### 🚀 Next Steps
1. Complete Play Console setup
2. Replace all placeholder IDs
3. Integrate with Application and MainActivity
4. Hook achievements to GamificationManager
5. Add sync UI/notifications
6. Test thoroughly on multiple devices
7. Publish to internal testing

---

## 📚 Additional Resources

- [Play Games Services Documentation](https://developers.google.com/games/services)
- [Achievements Guide](https://developers.google.com/games/services/android/achievements)
- [Leaderboards Guide](https://developers.google.com/games/services/android/leaderboards)
- [Saved Games API](https://developers.google.com/games/services/android/savedgames)
- [Play Console](https://play.google.com/console)

---

**Integration Status:** 🟢 Core Implementation Complete
**Console Setup Required:** 🟡 Pending (Achievement/Leaderboard IDs)
**Testing Status:** 🔴 Not Yet Tested

Last Updated: 2026-01-10
