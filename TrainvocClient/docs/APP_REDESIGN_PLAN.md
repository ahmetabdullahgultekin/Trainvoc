# Trainvoc Android App Redesign - Comprehensive Planning Document

> **Created**: 2026-01-27
> **Purpose**: Professional analysis and planning for major app improvements
> **Session Branch**: `claude/app-redesign-planning-Xs22W`

---

## Executive Summary

This document consolidates findings from a comprehensive investigation of the Trainvoc Android app covering:
- Authentication flow
- App navigation and screen accessibility
- Story Mode design flaws
- Quiz UX and engagement gaps
- User identity (avatar/badge) consistency
- Statistics discrepancies
- Theme implementation
- Game-specific issues (Flip Cards)
- Navigation bar design
- Home screen information architecture
- Engagement mechanics comparison with industry leaders

**Total New Issues Identified**: 51 issues across CRITICAL, HIGH, MEDIUM, and LOW severity levels.

---

## Table of Contents

1. [Authentication Flow Issues](#1-authentication-flow-issues)
2. [Story Mode Redesign](#2-story-mode-redesign)
3. [Quiz Screen Enhancement](#3-quiz-screen-enhancement)
4. [User Avatar/Badge Consistency](#4-user-avatarbadge-consistency)
5. [Profile vs Stats Discrepancy](#5-profile-vs-stats-discrepancy)
6. [Home Screen Redesign](#6-home-screen-redesign)
7. [Navigation Enhancement](#7-navigation-enhancement)
8. [Flip Cards Game Fixes](#8-flip-cards-game-fixes)
9. [AMOLED Theme Explanation](#9-amoled-theme-explanation)
10. [Splash Screen Configuration](#10-splash-screen-configuration)
11. [Debug Mode Text Verification](#11-debug-mode-text-verification)
12. [Google Features Integration](#12-google-features-integration)
13. [Friend System Design](#13-friend-system-design)
14. [Feedback System Design](#14-feedback-system-design)
15. [Candy Crush-Style Engagement](#15-candy-crush-style-engagement)
16. [Implementation Roadmap](#16-implementation-roadmap)

---

## 1. Authentication Flow Issues

### Current State
- Firebase Auth integrated with email/password
- Login and Register screens exist and are functional
- Auth state managed via `AuthViewModel` with `AuthRepository`

### Issues Identified

| Issue | Severity | File Location |
|-------|----------|---------------|
| Auth state checked via SharedPreferences instead of AuthViewModel | CRITICAL | `ProfileScreen.kt:102` |
| No Google Sign-In option despite GoogleAuthManager existing | HIGH | `LoginScreen.kt` |
| Email verification code exists but no UI | HIGH | `AuthRepository.kt:219` |
| No session timeout handling | HIGH | `AuthRepository.kt` |
| No logout confirmation dialog | LOW | `ProfileScreen.kt:845-851` |
| Password validation only checks length (6+ chars) | LOW | `AuthViewModel.kt:118-121` |

### Recommended Fixes

1. **Unify Auth State Checking**
   ```kotlin
   // Replace in ProfileScreen.kt:
   val isLoggedIn = prefs.getString("email", null) != null

   // With:
   val authState by authViewModel.authState.collectAsState()
   val isLoggedIn = authState is AuthState.Authenticated
   ```

2. **Add Google Sign-In to LoginScreen**
   - GoogleAuthManager already exists for Drive backup
   - Wire it to Firebase Auth for social login
   - Add Google Sign-In button below email/password form

3. **Add Email Verification Flow**
   - Create `EmailVerificationScreen.kt`
   - After registration, navigate to verification screen
   - Show "Resend verification email" button
   - Check verification status on app launch

---

## 2. Story Mode Redesign

### Current State: The Problem
Story Mode is **not a story** - it's just a level selection screen with CEFR levels (A1-C2). Users are promised "Learn through stories" but receive:
- No narrative content
- No chapter structure
- No contextual word grouping
- No story-driven progression

### The Gap Analysis

| Promised | Actual |
|----------|--------|
| "Learn through stories" | Level selection only |
| Story-driven journey | CEFR level names |
| Contextual learning | Random word quizzes |
| Chapters/episodes | 6 flat levels |
| Narrative progression | 80% mastery gates |

### Candy Crush-Inspired Design

Based on research into [Candy Crush's addictive psychology](https://yukaichou.com/gamification-study/game-mechanics-research-candy-crush-addicting/):

**Key Engagement Mechanics to Implement:**

1. **Progressive Disclosure**
   - Don't show all levels at once
   - Unlock chapters one at a time
   - Create anticipation for what's next

2. **Variable Rewards**
   - Random bonus XP/rewards
   - "Lucky" word challenges
   - Mystery boxes for streaks

3. **Loss Aversion**
   - "One more word to complete chapter!"
   - Show how close user is to goal
   - Hearts/lives system (optional)

4. **Near-Miss Psychology**
   - "You were 1 answer away from perfect!"
   - Progress bars that are almost full
   - Encourages "just one more try"

5. **Scarcity & Impatience**
   - Daily bonus words (available for 24h)
   - Limited-time challenges
   - Special events

### Proposed Story Mode Structure

```
STORY MODE → WORLD (A1 Beginner)
    ├── Chapter 1: "Daily Life" (25 words)
    │   ├── Lesson 1: "Morning Routine" (5 words)
    │   ├── Lesson 2: "At Home" (5 words)
    │   ├── Lesson 3: "Family" (5 words)
    │   ├── Lesson 4: "Hobbies" (5 words)
    │   ├── Lesson 5: "Review Challenge" (all 20)
    │   └── Chapter Boss: "Daily Life Quiz" (unlock next chapter)
    │
    ├── Chapter 2: "Food & Drink" (25 words)
    │   ├── Lesson 1: "Fruits" (5 words)
    │   ... etc
    │
    └── World Boss: "A1 Master Quiz" (unlock A2)
```

### Implementation Steps

1. **Database Schema Changes**
   - Add `Chapter` entity
   - Add `Lesson` entity
   - Add `chapter_id` to Word entity
   - Add `lesson_progress` tracking

2. **UI Components**
   - Create `ChapterSelectionScreen.kt`
   - Create `LessonScreen.kt`
   - Add progress visualization per chapter
   - Add celebration animations

3. **Content Creation**
   - Group existing 5000 words into chapters
   - Create chapter descriptions
   - Add contextual example sentences

---

## 3. Quiz Screen Enhancement

### Current Implementation Score: 6.5/10

**What Trainvoc Does Well:**
- Immediate visual feedback (green/red)
- Streak counter with milestone celebrations
- Animated progress bar
- Haptic feedback
- Beautiful results screen

**What's Missing vs. Duolingo/Quizlet:**

| Feature | Duolingo | Quizlet | Trainvoc |
|---------|----------|---------|----------|
| Persistent daily streaks | 365+ days | Multi-day | Session only |
| XP/Level system | Visible | Level badges | Hidden |
| Sound effects | Extensive | Yes | None |
| Adaptive difficulty | Dynamic | SRS | None |
| Hints | Multiple types | Study mode | None |
| Leaderboards | Friends/Global | Class-based | Coming Soon |
| Speed bonuses | Yes | Timed mode | None |

### Recommended Enhancements

#### Quick Wins (1-2 days)

1. **Add Sound Effects**
   ```kotlin
   // Create SoundManager.kt
   object SoundManager {
       fun playCorrect() // cheerful ding
       fun playWrong() // soft buzzer
       fun playMilestone() // fanfare
       fun playQuizComplete() // triumphant chime
   }
   ```

2. **Make Stats Visible by Default**
   - Remove toggle requirement
   - Show word stats inline during quiz
   - Add difficulty badge to question

3. **Show Speed Bonus**
   - "+20 bonus for fast answer!"
   - Animate bonus points adding

#### Medium Effort (3-5 days)

4. **Persistent Daily Streaks**
   ```kotlin
   // Add to GamificationDao
   @Query("SELECT COUNT(DISTINCT date(timestamp)) FROM statistics WHERE ...")
   fun getStreakDays(): Int

   // Store in SharedPreferences
   - last_activity_date
   - current_streak_days
   - longest_streak_days
   ```

5. **Hint System**
   - 2 hints per quiz
   - "Reveal first letter" hint
   - "Eliminate one option" hint
   - Cost: -5 points per hint

6. **XP Level System**
   ```
   Level 1: 0-100 XP (Novice)
   Level 2: 100-300 XP (Beginner)
   Level 3: 300-600 XP (Intermediate)
   Level 4: 600-1000 XP (Advanced)
   Level 5: 1000+ XP (Master)
   ```

#### Major Features (1-2 weeks)

7. **Adaptive Difficulty**
   - Track accuracy per word
   - Weight word selection by weakness
   - Gradually increase difficulty

8. **Multiplayer Quiz Restoration**
   - Git history shows it was deleted
   - Restore from previous commits
   - Add real-time competition

---

## 4. User Avatar/Badge Consistency

### Current State

| Screen | Avatar Display | Status |
|--------|---------------|--------|
| ProfileScreen | Emoji (128dp) | Correct |
| HomeScreen | First letter only | **BROKEN** |
| LobbyScreen | Person icon | **BROKEN** |
| GameScreen | None | **BROKEN** |
| GameResultsScreen | Person icon | **BROKEN** |
| Navigation Drawer | None | **MISSING** |
| Settings | None | **MISSING** |

### Avatar System Architecture

The app has a complete avatar system that's underutilized:

```kotlin
// constants/Avatars.kt
object Avatars {
    val AVATAR_LIST = listOf(
        "fox", "cat", "dog", "penguin", ... // 20 emojis
    )
    fun getAvatarByIndex(index: Int): String
}
```

### Fix Implementation

1. **Create Reusable UserAvatar Composable**
   ```kotlin
   @Composable
   fun UserAvatar(
       size: Dp = 48.dp,
       fallbackToInitial: Boolean = false
   ) {
       val prefs = LocalContext.current.getSharedPreferences(...)
       val avatar = prefs.getString("avatar", null)

       Box(modifier = Modifier.size(size)) {
           if (avatar != null) {
               Text(avatar, fontSize = (size.value * 0.6).sp)
           } else if (fallbackToInitial) {
               // Show first letter
           }
       }
   }
   ```

2. **Update All Screens**
   - Replace `Text(username.take(1))` with `UserAvatar()`
   - Replace `Icon(Icons.Default.Person)` with `UserAvatar()`
   - Add to Navigation Drawer header
   - Add to Settings profile section

---

## 5. Profile vs Stats Discrepancy

### The Problem

| Metric | ProfileScreen | StatsScreen | Confusion |
|--------|--------------|-------------|-----------|
| Quizzes | TODAY only | ALL-TIME | "Did I do 3 or 47 quizzes?" |
| Study Time | Today or Total | Total only | Different logic |
| Accuracy | Mastery % | Success Rate | Different metrics |

### Solution

**ProfileScreen** = Personal summary (interesting highlights)
- Today's activity
- Current streak
- Level progress
- Recent achievements

**StatsScreen** = Comprehensive analytics (all data)
- All-time totals
- Weekly/monthly trends
- Category breakdown
- Weakness analysis

### Implementation

1. Add clear time labels:
   ```kotlin
   // ProfileScreen
   Text("Today's Quizzes: 3")

   // StatsScreen
   Text("Total Quizzes (All Time): 47")
   ```

2. Use `dailyCorrect` and `weeklyCorrect` that are already collected but unused

---

## 6. Home Screen Redesign

### Current Issues

1. **No Welcome Experience** - Jumps straight to features
2. **6 Quick Action Buttons** - Flat hierarchy, decision paralysis
3. **Stats Section Redundant** - Same as header
4. **Achievements Shown** - Most users have none
5. **No "Continue" Feature** - Users search for last activity

### Proposed Redesign

```
┌─────────────────────────────────────────┐
│ Welcome back, Sarah!             👋🦊   │
│ Level 5 • 450/600 XP to Level 6        │
├─────────────────────────────────────────┤
│ ⏰ Daily Progress                       │
│ ████████░░░░░░░░ 3/5 Words             │
│ ████░░░░░░░░░░░░ 1/3 Quizzes           │
│ ████████████░░░░ 12/20 Reviews         │
├─────────────────────────────────────────┤
│ 👉 Continue Learning                    │
│ ┌─────────────────────────────────────┐ │
│ │ B1 Intermediate - Chapter 3         │ │
│ │ "Travel & Transportation"           │ │
│ │ 23 words remaining    [CONTINUE >]  │ │
│ └─────────────────────────────────────┘ │
├─────────────────────────────────────────┤
│ 🎯 Quick Start                          │
│ [Start Quiz]  [Dictionary]  [Games]     │
│                                         │
│ [ More Options ▼ ]                      │
│ (expands: Story, Favorites, WotD, MP)   │
├─────────────────────────────────────────┤
│ 🔥 5 Day Streak!            📈 View All │
└─────────────────────────────────────────┘
```

### Key Changes

1. **Personalized Greeting** with avatar
2. **Daily Progress Front & Center**
3. **"Continue Learning" Primary CTA**
4. **Only 3 Primary Quick Actions**
5. **"More Options" Collapsible**
6. **Streak Prominently Displayed**
7. **Stats Moved to Stats Screen**
8. **Achievements Hidden (or show 1 recent)**

---

## 7. Navigation Enhancement

### Bottom Navigation Bar

**Current**: 4 items (Home, Quiz, Dictionary, Profile)
**Design Intent**: 5 items with center play button

**Evidence**: `isCenter` parameter exists but unused in `AppBottomBar.kt`

**Proposed Layout**:
```
[Home] [Dictionary]  [🎮]  [Stats] [Profile]
                      ↑
              Center Play Button
              (Games Menu)
```

### Implementation

1. Add 5th item with `isCenter = true`
2. Route to `GAMES_MENU` or new unified gaming screen
3. Add notification badges
4. Add streak indicator to Home icon

### Unified Gaming Screen Concept

```
GAMES & CHALLENGES
├── Single Player
│   ├── Quick Quiz
│   ├── Story Mode
│   └── Practice
├── Games
│   ├── Flip Cards
│   ├── Word Scramble
│   ├── Speed Match
│   └── ... (10 games)
├── Multiplayer
│   ├── Create Room
│   └── Join Room
└── Challenges
    ├── Daily Challenge
    └── Weekly Tournament
```

---

## 8. Flip Cards Game Fixes

### Issues

1. **Cards too small** on 6x6 grid (~45dp)
2. **Font hardcoded** based on character count
3. **Popup auto-dismisses** after 2 seconds
4. **No zoom/enlarge** option
5. **Touch targets below 48dp** minimum

### Solutions

#### Option A: Responsive Card Sizing
```kotlin
// Calculate columns based on screen width
val screenWidth = LocalConfiguration.current.screenWidthDp
val columns = when {
    screenWidth < 360 -> 4
    screenWidth < 600 -> 6
    else -> 8 // tablets
}
```

#### Option B: Long-Press Popup (Recommended)
```kotlin
// Replace auto-dismiss with manual control
Card(
    modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { showEnlargedCard = true }
            )
        }
)

// Popup stays until dismissed
if (showEnlargedCard) {
    ModalBottomSheet(
        onDismissRequest = { showEnlargedCard = false }
    ) {
        Text(content, style = headlineLarge)
        Button("Close") { showEnlargedCard = false }
    }
}
```

#### Option C: Settings-Based Size Control
- Add "Card Size" preference: Small, Medium, Large
- Add "Font Size" preference: Small, Medium, Large, Extra Large
- Store in PreferencesRepository

---

## 9. AMOLED Theme Explanation

### What is AMOLED Theme?

AMOLED (Active-Matrix Organic Light-Emitting Diode) displays can **turn off individual pixels** to show pure black. This:
- Saves battery (pixels are completely off)
- Reduces eye strain in dark environments
- Prevents OLED burn-in

### Trainvoc Implementation

| Color | Dark Theme | AMOLED Theme |
|-------|-----------|--------------|
| Background | `#121212` | `#000000` (pure black) |
| Surface | `#1E1E1E` | `#0D0D0D` (near black) |

### Current Issues

1. **Surface not pure black** (`#0D0D0D` instead of `#000000`)
   - Slightly reduces battery savings
   - Fix: Use `#000000` for surfaces too

2. **DEFAULT palette missing AMOLED variant**
   - Falls back to dark theme
   - Fix: Add AMOLED colors to default palette

3. **High Contrast + AMOLED conflict**
   - Can't use both simultaneously
   - Document this as expected behavior

### Why Keep AMOLED?

AMOLED theme is **valuable** for:
- Users with OLED phones (most modern devices)
- Night-time learners
- Battery-conscious users
- Accessibility (high contrast option)

---

## 10. Splash Screen Configuration

### Current Implementation

- **New users**: 3000ms (3 seconds)
- **Returning users**: 1200ms (1.2 seconds)
- **Animation**: Lottie cat animation

### User Request

> "Revert duration to 3000ms and add disable option"

### Recommended Professional Solution

1. **Add SplashScreenConfig to AppConfig.kt**
   ```kotlin
   object SplashScreenConfig {
       const val DURATION_NEW_USER_MS = 3000L
       const val DURATION_RETURNING_USER_MS = 1500L
       const val SHOW_FOR_RETURNING_USERS = true
       const val TAP_TO_SKIP_ENABLED = false
   }
   ```

2. **Add to PreferencesRepository**
   ```kotlin
   fun isSplashEnabled(): Boolean
   fun setSplashEnabled(enabled: Boolean)
   fun getSplashDuration(): Long
   ```

3. **Add to SettingsScreen**
   - "Show splash screen" toggle
   - Hidden under "Advanced" or "Developer" options

4. **Add Tap-to-Skip**
   ```kotlin
   Box(modifier = Modifier.clickable { navigateAway() }) {
       // splash content
   }
   ```

---

## 11. Debug Mode Text Verification

### Investigation Result: SAFE

Debug mode text is **properly guarded** by `BuildConfig.DEBUG`:

```kotlin
// AppBottomSheet.kt:119
if (BuildConfig.DEBUG) {
    Text("Test Mode - For development use only", ...)
}

// AppNavigationDrawer.kt:100
if (BuildConfig.DEBUG) {
    Text("Debug Mode", ...)
}
```

### How BuildConfig.DEBUG Works

- **Debug builds** (`./gradlew assembleDebug`): `DEBUG = true`
- **Release builds** (`./gradlew assembleRelease`): `DEBUG = false`

The text will **NOT appear** in Play Store releases.

---

## 12. Google Features Integration

### Current State

| Feature | Status | Notes |
|---------|--------|-------|
| Firebase Auth | Integrated | Email/password only |
| Google Sign-In | Code exists | Not used for auth |
| Google Drive | Integrated | For backup only |
| Firebase Analytics | Not integrated | Missing |
| Firebase Firestore | Not integrated | Missing |
| Firebase Crashlytics | Not integrated | Missing |

### Recommended Integrations

1. **Firebase Analytics**
   - Track screen views
   - Track quiz completion
   - Track feature usage
   - Monitor retention

2. **Firebase Firestore** (for social features)
   - Leaderboard data
   - User profiles (public)
   - Friend connections
   - Multiplayer rooms

3. **Firebase Crashlytics**
   - Crash reporting
   - ANR detection
   - Performance monitoring

### Implementation Priority

1. Analytics (essential for app understanding)
2. Crashlytics (production stability)
3. Firestore (enables social features)
4. Google Sign-In (user convenience)

---

## 13. Friend System Design

### Proposed Architecture

```
FRIEND SYSTEM
├── Friend Discovery
│   ├── Search by username
│   ├── Share invite link
│   └── Connect via contacts
├── Friend List
│   ├── Online status
│   ├── Current level
│   └── Quick challenge button
├── Friend Activity
│   ├── Recent achievements
│   ├── Learning streaks
│   └── Quiz scores
└── Challenges
    ├── Send challenge
    ├── Receive challenge
    └── Challenge history
```

### Database Schema (Firestore)

```
/users/{userId}
  - displayName
  - avatarEmoji
  - level
  - currentStreak
  - isOnline
  - lastActive

/friendships/{friendshipId}
  - user1Id
  - user2Id
  - status: "pending" | "accepted" | "blocked"
  - createdAt

/challenges/{challengeId}
  - senderId
  - receiverId
  - wordSetId
  - status: "pending" | "accepted" | "completed"
  - scores: { senderId: 85, receiverId: 92 }
```

### Security Considerations

1. **Rate limiting** on friend requests
2. **Validation** of usernames
3. **Blocking** capability
4. **Privacy settings** (who can find me)
5. **Data minimization** (don't expose sensitive data)

---

## 14. Feedback System Design

### Requirements

- Easy to use (minimal friction)
- Categories (bug, feature, general)
- Screenshots attachment
- Device info auto-collection
- Secure transmission

### Option A: Firebase + Email (Simple)

```kotlin
fun submitFeedback(
    category: String,
    message: String,
    screenshot: Bitmap?
) {
    // 1. Upload screenshot to Firebase Storage
    // 2. Save feedback to Firestore
    // 3. Trigger Cloud Function to send email
}
```

### Option B: Third-Party Service

- **Instabug** - Shake to report
- **Zendesk** - Full support system
- **Intercom** - Chat-based

### Recommended: In-App + Firestore

```
FEEDBACK SCREEN
├── Category Selection
│   ○ Bug Report
│   ○ Feature Request
│   ○ General Feedback
├── Description (TextField)
├── [Attach Screenshot] (optional)
├── Device Info (auto-collected)
│   - App version
│   - Android version
│   - Device model
└── [Submit Feedback]
```

### Security Implementation

1. **Client-side validation**
2. **Rate limiting** (max 5/day)
3. **Content filtering** (profanity, spam)
4. **Firebase Security Rules** (authenticated users only)
5. **No PII collection** without consent

---

## 15. Candy Crush-Style Engagement

### Research Summary

Based on [game addiction psychology research](https://www.sciencedirect.com/science/article/abs/pii/S073658531500132X):

> "Candy Crush uses a behaviorist psychology strategy. It stimulates a positive feedback loop that encourages repetitive behavior."

### Key Mechanics to Implement

| Mechanic | Candy Crush | Trainvoc Application |
|----------|-------------|---------------------|
| **Variable Rewards** | Random candy combos | Random bonus XP, mystery boxes |
| **Near-Miss** | One move from winning | "1 more answer to level up!" |
| **Loss Aversion** | Losing a level | "Don't lose your streak!" |
| **Progressive Difficulty** | New blockers each level | Harder words, shorter time |
| **Social Proof** | Friends' progress | Friend leaderboard |
| **Scarcity** | Limited lives | Daily bonus words (24h only) |
| **Celebration** | Candy explosions | Confetti, sound effects |
| **Collection** | Stars per level | Word mastery badges |

### Implementation Priorities

1. **Persistent Streaks** (highest ROI)
   - "42 day streak!" on home screen
   - Streak freeze (premium feature)
   - Streak recovery challenges

2. **Celebration Moments**
   - Sound effects on correct/wrong
   - Confetti on milestones
   - Level up animations

3. **Progress Visibility**
   - XP bar always visible
   - "3 more words to Level 6!"
   - Weekly progress summary

4. **Daily Hooks**
   - Daily challenge (unique each day)
   - Daily bonus word
   - Streak reminder notification

5. **Social Competition**
   - Friend leaderboard
   - Weekly tournaments
   - Challenge friends

---

## 16. Implementation Roadmap

### Sprint 1: Foundation (Week 1)

| Task | Priority | Effort | Issue |
|------|----------|--------|-------|
| Fix auth state (SharedPrefs → AuthViewModel) | CRITICAL | 2h | #173 |
| Add persistent streaks | CRITICAL | 4h | #170 |
| Fix HomeScreen hierarchy (3 primary CTAs) | CRITICAL | 6h | #172 |
| Add sound effects to quiz | HIGH | 4h | #180 |
| Fix user avatar in HomeScreen | HIGH | 1h | #174 |

### Sprint 2: Engagement (Week 2)

| Task | Priority | Effort | Issue |
|------|----------|--------|-------|
| Add welcome message to HomeScreen | HIGH | 2h | #186 |
| Add "continue where you left off" | HIGH | 4h | #187 |
| Add hint system to quiz | HIGH | 6h | #181 |
| Restore 5-item bottom bar | HIGH | 4h | #185 |
| Add logout confirmation | LOW | 1h | #210 |

### Sprint 3: Story Mode (Week 3-4)

| Task | Priority | Effort | Issue |
|------|----------|--------|-------|
| Design chapter structure | CRITICAL | 8h | #168 |
| Create Chapter entity & DB | CRITICAL | 6h | #177 |
| Group words into chapters | CRITICAL | 8h | #178 |
| Build ChapterSelectionScreen | HIGH | 8h | #168 |
| Add story achievements | MEDIUM | 4h | #204 |

### Sprint 4: Social (Week 5-6)

| Task | Priority | Effort | Issue |
|------|----------|--------|-------|
| Implement Firestore | MEDIUM | 8h | #206 |
| Build friend system | HIGH | 16h | #189 |
| Make leaderboard functional | HIGH | 8h | #194 |
| Add feedback system | HIGH | 8h | #190 |

### Sprint 5: Polish (Week 7-8)

| Task | Priority | Effort | Issue |
|------|----------|--------|-------|
| Fix Flip Cards sizing | CRITICAL | 8h | #169, #182-184 |
| Add Google Sign-In | HIGH | 6h | #192 |
| Add adaptive difficulty | HIGH | 12h | #179 |
| Fix multiplayer avatars | HIGH | 4h | #175-176, #207 |
| Add Google Analytics | MEDIUM | 4h | #205 |

---

## Appendix A: File Locations Reference

| Component | File Path |
|-----------|-----------|
| HomeScreen | `ui/screen/main/HomeScreen.kt` |
| ProfileScreen | `ui/screen/profile/ProfileScreen.kt` |
| StatsScreen | `ui/screen/other/StatsScreen.kt` |
| StoryScreen | `ui/screen/main/StoryScreen.kt` |
| QuizScreen | `ui/screen/quiz/QuizScreen.kt` |
| FlipCardsScreen | `ui/games/FlipCardsScreen.kt` |
| AppBottomBar | `ui/screen/main/components/AppBottomBar.kt` |
| AppNavigationDrawer | `ui/screen/main/components/AppNavigationDrawer.kt` |
| SplashScreen | `ui/screen/welcome/SplashScreen.kt` |
| LoginScreen | `ui/screen/auth/LoginScreen.kt` |
| AuthViewModel | `viewmodel/AuthViewModel.kt` |
| AuthRepository | `repository/AuthRepository.kt` |
| Avatars | `constants/Avatars.kt` |
| Theme | `ui/theme/Theme.kt` |
| Colors | `ui/theme/Color.kt` |
| AppConfig | `config/AppConfig.kt` |

---

## Appendix B: Database Schema Changes Required

### New Entities

```kotlin
@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val worldId: String, // A1, A2, etc.
    val name: String,
    val description: String,
    val orderIndex: Int,
    val wordCount: Int
)

@Entity(tableName = "user_chapter_progress")
data class UserChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val wordsLearned: Int,
    val isCompleted: Boolean,
    val completedAt: Long?
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val fromUserId: String,
    val toUserId: String,
    val status: String,
    val createdAt: Long
)
```

### New Preferences Keys

```kotlin
// PreferencesRepository
KEY_STREAK_LAST_DATE = "streak_last_date"
KEY_STREAK_CURRENT = "streak_current"
KEY_STREAK_LONGEST = "streak_longest"
KEY_SPLASH_ENABLED = "splash_enabled"
KEY_SOUND_ENABLED = "sound_enabled"
KEY_HINT_COUNT = "hint_count"
```

---

*Document created as part of comprehensive app redesign planning session.*
*All issues tracked in `/TODO.md`*
