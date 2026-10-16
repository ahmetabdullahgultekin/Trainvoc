# Comprehensive Engineering Audit: Bug, Issue, Problem & Trouble Report
**Target Audited:** Yesterday's work by Gemini 3.8 High (Commits `502811b`, `60e795f`, `36e42fe`, `fd5c46b` | PRs `#142`, `#143`, `#144`, `#145`)  
**Auditor:** Gemini 4 Argon (High)  
**Date:** 2026-10-16  

---

## Executive Summary

A deep forensic audit of all 4 commits, 4 open pull requests (`#142`–`#145`), git worktree states, runtime emulator screenshots, and release R8/ProGuard build outputs (`mapping.txt`) produced yesterday reveals **18 distinct engineering defects**, including **5 Critical (P0) regressions/bugs**, **6 High-Severity (P1) architectural & concurrency flaws**, **4 Medium-Severity (P2) incomplete implementations**, and **3 Git/PR/Testing hygiene issues**.

> [!CAUTION]
> **Do NOT merge PRs `#143`, `#144`, or `#145` in their current state.**  
> Merging them as-is will cause:
> 1. **Silent user data loss on app upgrade** (streaks, notification schedules, and backup `notificationTime` wiped).
> 2. **Destructive app-settings wipe on sign-out** (`clearAll()` wipes theme, language, device ID, and migration flags).
> 3. **Complete breakage of Cloud Sync, JSON Backup/Restore, Multiplayer API, and Auth in Release builds** due to R8 obfuscating unannotated Gson DTO fields to `a, b, c`.
> 4. **Git merge conflicts across stacked PRs** because PRs `#144` and `#145` were stacked on top of `#143` but opened with `baseRefName: master`.

---

## Summary Matrix of Findings

| ID | Severity | Category | Title / Defect Summary | Introduced / Affected In |
|:---|:---:|:---|:---|:---|
| **BUG-01** | 🚨 **P0 Critical** | Data Loss / Migration | `NotificationPreferences` switched to `secure_user_prefs`, but `LegacyPrefsMigrator` only migrates 4 of 20+ keys — **silently wiping user streaks & notification settings on upgrade** | `60e795f` (PR #143) |
| **BUG-02** | 🚨 **P0 Critical** | Release Build / R8 | **Gson DTOs obfuscated to `a, b, c` in Release APK** (`sync.*`, `offline.*`, `multiplayer.data.*`, `auth.*`) — breaks Backup/Restore, Cloud Sync, Auth & Multiplayer in production | `fd5c46b` (PR #145) |
| **BUG-03** | 🚨 **P0 Critical** | UX / Auth Lockout | **Non-dismissible "Session Expired" dialog locks out offline/guest users on `ProfileScreen`** (seen in `profile_screen_view.png` and misdiagnosed by previous agent) | Pre-existing + Misdiagnosed in Review |
| **BUG-04** | 🚨 **P0 Critical** | Destructive State | `MainScreen` sign-out calls `PreferencesRepository.clearAll()`, **wiping global device/app settings** (`theme`, `language`, `color_palette`, `device_id`, `KEY_MIGRATED`) | `60e795f` (PR #143) |
| **BUG-05** | 🚨 **P0 Critical** | Data Loss / Backup | `DataExporter` hardcodes `notificationTime = null` and `DataImporter` drops restoring it — **silent backup/restore data loss** | `60e795f` (PR #143) |
| **ISS-06** | ⚠️ **P1 High** | Git / PR Topology | **Broken Stacked PR Topology:** PRs `#143`, `#144`, `#145` are linearly stacked commits (`60e795f -> 36e42fe -> fd5c46b`) all targeting `master`, duplicating commits & guaranteeing merge conflicts | PRs #143, #144, #145 |
| **ISS-07** | ⚠️ **P1 High** | Concurrency / UDF | **Cosmetic & Racy `QuizUiState` Refactoring:** Kept all 16 legacy `MutableStateFlow`s, added a 17th `_uiState` mutated via non-atomic `syncUiState()` across `Dispatchers.IO` and `Dispatchers.Main` | `fd5c46b` (PR #145) |
| **ISS-08** | ⚠️ **P1 High** | Feature Logic Bug | **Word of the Day Split-Brain:** `WordOfDayWorker` uses `wordDao.getWordAtOffset(seed)` + SharedPreferences, whereas `WordOfDayViewModel` uses `WordOfDayDao` Room table + `getRandomWordForNotification` | `36e42fe` (PR #144) |
| **ISS-09** | ⚠️ **P1 High** | Main Thread I/O | `MainActivity.onCreate()` synchronously initializes Android Keystore `MasterKey`, `EncryptedSharedPreferences`, and runs disk migration on the UI thread (StrictMode violation & startup jank) | `60e795f` (PR #143) |
| **ISS-10** | ⚠️ **P1 High** | Persistence Split-Brain | **Incomplete Preferences Unification:** `NotificationPreferences` bypasses `LegacyPrefsMigrator`, `"language"` is still written to plain `"user_prefs"`, and 10+ direct `getSharedPreferences` calls remain | `60e795f` (PR #143) |
| **ISS-11** | ⚠️ **P1 High** | Networking / Auth | `authInterceptor` uses `.addHeader("Authorization", ...)` causing **duplicate `Authorization` headers** on `firebaseSync`/`validateToken`; `tokenAuthenticator` fails after any HTTP redirect | `60e795f` (PR #143) |
| **ISS-12** | 🔶 **P2 Medium** | Clean Architecture | **Hollow DAO Decoupling:** Created `IQuizHistoryRepository` for 1 DAO, but left `WordDao`, `GamificationDao`, and `ReviewScheduleDao` directly injected in `HomeViewModel`, `QuizHistoryViewModel`, and `WordOfDayWorker` | `36e42fe` (PR #144) |
| **ISS-13** | 🔶 **P2 Medium** | WorkManager / DI | **Placebo `@HiltWorker` Migration:** `DailyReminderWorker` and `StreakAlertWorker` have empty `@AssistedInject` constructors and still call `NotificationPreferences.getInstance(context)`; `WordNotificationWorker` missed entirely | `36e42fe` (PR #144) |
| **ISS-14** | 🔶 **P2 Medium** | UI Architecture | `QuizStatsCard` still collects legacy flows (`totalWords`, `learnedWords`, `progressPercent`) without lifecycle awareness; `QuizScreen` keeps quiz answer/streak state in local `remember` | `fd5c46b` (PR #145) |
| **ISS-15** | 🔶 **P2 Medium** | Unfulfilled Plan | **6 Major Items Promised in Review Report Were Silently Skipped:** Domain `api.trainvoc.com` mismatch, 10 dead UseCases, God Composables (`ProfileScreen`, `HomeScreen`), Type-Safe Navigation, `GameWebSocketClient` OkHttpClient | PRs #142–#145 |
| **ISS-16** | 🔵 **P3 Low** | Git Worktree Hygiene | Worktree directory `review_architecture_ecosystem` was switched to branch `fix/phase2-udf-state-proguard`, orphaning `docs/ARCHITECTURE_ECOSYSTEM_REVIEW_2026-10-15.md` (`502811b`) from the active checkout | Worktree state |
| **ISS-17** | 🔵 **P3 Low** | Test Coverage | **Zero Unit Tests Added for Any New Code:** `QuizUiState`, `LegacyPrefsMigrator`, `QuizHistoryRepository`, and `getWordAtOffset` have 0 tests; `ReviewScheduleDaoTest` in `60e795f` had unused imports fixed only in `36e42fe` | `60e795f`, `36e42fe`, `fd5c46b` |
| **ISS-18** | 🔵 **P3 Low** | Build Configuration | `app/build.gradle.kts` cleaned up 3 test dependency versions (`36e42fe`) while leaving 15+ hardcoded versions (`1.7.6`, `2.6.1`, `3.3.5`, `2.1.0`) in the same block | `36e42fe` (PR #144) |

---

## Detailed Technical Breakdown

### 1. Critical Bugs & Regressions (P0)

#### BUG-01: Silent User Data Loss on Upgrade (Unmigrated `NotificationPreferences` & Streak Keys)
- **Files Affected:**
  - [NotificationPreferences.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/notification/NotificationPreferences.kt#L16-L21)
  - [LegacyPrefsMigrator.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/LegacyPrefsMigrator.kt#L23-L58)
- **What Changed in `60e795f`:**
  `NotificationPreferences` was changed from `context.getSharedPreferences("user_prefs", MODE_PRIVATE)` to `PreferencesRepository.getEncryptedSharedPreferences(context)` (`"secure_user_prefs"`).
- **The Bug:**
  1. [`LegacyPrefsMigrator.migrate()`](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/LegacyPrefsMigrator.kt#L23-L58) **only migrates 4 keys**: `"username"`, `"avatar"`, `"last_word_of_day"`, and `"account_created"`.
  2. All **16+ keys managed by `NotificationPreferences`** that were previously stored in `"user_prefs"` are **never migrated**:
     - `current_streak`, `longest_streak`, `last_practice_date` (**user's study streak is reset to 0!**)
     - `daily_reminders_enabled`, `reminder_hour`, `reminder_minute`
     - `streak_alerts_enabled`, `word_of_day_enabled`, `word_of_day_hour`, `word_of_day_minute`, `last_word_of_day_id`
     - `word_quiz_enabled`, `word_quiz_interval`, `notifications_enabled`
     - `notification_levels`, `notification_exams`, `include_learned_words`, `include_favorites_only`
     - `quiet_hours_enabled`, `quiet_hours_start`, `quiet_hours_end`, `last_notification_time`
  3. Worse still, `NotificationPreferences` calls `PreferencesRepository.getEncryptedSharedPreferences(context)` directly instead of going through `PreferencesRepository` constructor, meaning if a background worker accesses `NotificationPreferences.getInstance(context)` before `PreferencesRepository` is constructed, `LegacyPrefsMigrator.migrate()` is bypassed entirely and a duplicate `EncryptedSharedPreferences` instance is created.

---

#### BUG-02: R8 / ProGuard Obfuscates Gson DTO Fields in Release Builds (Breaks Sync, Backup, Auth & Multiplayer)
- **Files Affected:**
  - [proguard-rules.pro](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/proguard-rules.pro#L41-L65)
  - Verified in release R8 mapping output: `TrainvocClient/app/build/outputs/mapping/release/mapping.txt`
- **What Changed in `fd5c46b`:**
  The commit claimed to optimize `proguard-rules.pro` and verified `assembleRelease`.
- **The Bug:**
  `proguard-rules.pro` only keeps model classes under `com.gultekinahmetabdullah.trainvoc.classes.**`. However, the app serializes and deserializes JSON via **Gson reflection without `@SerializedName` or `@Keep`** across four other packages!
  Inspecting `build/outputs/mapping/release/mapping.txt` from yesterday's release build proves that R8 renamed all fields to single letters (`a, b, c...`) and stripped unread fields:
  - **Backup/Restore (`com.gultekinahmetabdullah.trainvoc.sync.*`):**
    - `BackupData`: `int version` **completely stripped by R8**, `timestamp -> a`, `checksum -> b`, `words -> c`, `statistics -> d`, `userPreferences -> e`
    - `WordBackup`: `word -> a`, `meaning -> b`, `level -> c`, `statId -> d`, `secondsSpent -> e`, `lastReviewed -> f`, `isFavorite -> g`
    - `UserPreferences`: `username -> a`, `language -> b`, `theme -> c`, `notificationsEnabled -> d`, `dailyGoal -> e`
  - **Cloud Sync (`com.gultekinahmetabdullah.trainvoc.offline.*`):**
    - `SyncRequest`: `entityType -> a`, `entityId -> b`, `action -> c`, `data -> d`, `timestamp -> e`, `clientVersion -> f`
    - `BatchSyncRequest`: `deviceId -> a`, `lastSyncTimestamp -> b`, `items -> c`
    - `SyncResponse`: `syncedCount -> a`, `failedCount -> b`, `conflicts -> c`, `serverTimestamp -> d`
  - **Authentication (`com.gultekinahmetabdullah.trainvoc.auth.*`):**
    - `LoginRequest`, `RegisterRequest`, `RefreshTokenRequest`, `AuthResponse`, `UserInfo`, `FirebaseSyncResponse` — all fields renamed to `a, b, c, d...`
  - **Multiplayer (`com.gultekinahmetabdullah.trainvoc.multiplayer.data.*`):**
    - `AnswerRequest`, `CreateRoomRequest`, `JoinRoomRequest`, `RoomInfo`, `PlayerInfo`, `QuestionInfo`, `GameStateResponse` — all fields renamed to `a, b, c, d...`
- **Impact:** In release builds (`isMinifyEnabled = true`), every REST API call to the backend sends JSON like `{"a":"user","b":"pass","c":"dev1"}` and every JSON backup file exports obfuscated keys that fail checksum/restore validation.

---

#### BUG-03: Offline/Guest Users Locked Out of `ProfileScreen` by Non-Dismissible "Session Expired" Modal (Misdiagnosed in Emulator Review)
- **Files Affected:**
  - [ProfileScreen.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/profile/ProfileScreen.kt#L135-L144)
  - [AuthViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/AuthViewModel.kt#L314-L319)
  - [AuthRepository.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/auth/AuthRepository.kt#L201-L206)
- **Evidence:** See emulator screenshot [profile_screen_view.png](file:///C:/Users/gltkn/.gemini/antigravity/brain/12f9158f-7ad7-4655-a522-7aec46754539/profile_screen_view.png).
- **The Bug:**
  When an offline/local user navigates to `ProfileScreen`, lines 135–137 run unconditionally:
  ```kotlin
  LaunchedEffect(Unit) {
      authViewModel.validateSession()
  }
  ```
  Inside [`AuthViewModel.validateSession()`](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/AuthViewModel.kt#L314-L319):
  ```kotlin
  fun validateSession() {
      viewModelScope.launch {
          val valid = authRepository.validateSession()
          _sessionExpired.value = !valid
      }
  }
  ```
  Because the user never logged into Firebase (`firebaseAuthRepository.getCurrentUser() == null`), `authRepository.validateSession()` returns `false`, setting `_sessionExpired.value = true`.
  `SessionExpiredHandler` then displays a **non-dismissible `AlertDialog`** (`onDismissRequest = { /* Cannot dismiss without action */ }`), completely blocking the user from viewing or editing their local profile!
- **False Reporting Yesterday:** Yesterday's agent saw this broken modal on the emulator (`profile_screen_view.png`) and wrote: *"The new `AuthInterceptor` gracefully detected the unauthenticated state in this fresh emulator instance and displayed the non-crashing 'Session expired — Please sign in again' prompt."* In reality, no network call or `AuthInterceptor` was involved at all—it is a broken UI state bug that locks out offline users.

---

#### BUG-04: Destructive `clearAll()` on Sign-Out Wipes Global App Preferences & Migration Flag
- **Files Affected:**
  - [MainScreen.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/main/MainScreen.kt#L306-L313)
  - [PreferencesRepository.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/PreferencesRepository.kt#L298-L307)
- **What Changed in `60e795f`:**
  In `MainScreen.kt`, `onSignOut` was changed from clearing only `"username"` and `"avatar"` to calling `repo.clearAll()`.
- **The Bug:**
  [`PreferencesRepository.clearAll()`](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/PreferencesRepository.kt#L300) calls `prefs.edit { clear() }` on `"secure_user_prefs"`. Because all app settings share `"secure_user_prefs"`, signing out wipes:
  - `theme`, `color_palette`, `language`
  - `device_id` (breaking device sync identity!)
  - `KEY_MIGRATED` (`legacy_user_prefs_migrated_v1` — causing `LegacyPrefsMigrator` to re-run on next app launch and resurrect deleted legacy `"user_prefs"` values!)
  - All notification & streak settings (`NotificationPreferences`)
  - All accessibility settings (`high_contrast`, `reduce_motion`, `text_size_scale`)
  - Meanwhile, [`PreferencesRepository.setLanguage()`](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/PreferencesRepository.kt#L179-L185) dual-writes `"language"` to plain `"user_prefs"`, which `clearAll()` does *not* clear, leaving `"user_prefs"` and `"secure_user_prefs"` out of sync.

---

#### BUG-05: Silent Backup & Restore Data Loss (`notificationTime` Dropped)
- **Files Affected:**
  - [DataExporter.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/sync/DataExporter.kt#L345-L357)
  - [DataImporter.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/sync/DataImporter.kt#L649-L654)
- **What Changed in `60e795f`:**
  When replacing `sharedPrefs` with `preferencesRepository` in `DataExporter.collectUserPreferences()`, the agent wrote:
  ```kotlin
  return UserPreferences(
      username = preferencesRepository.getUsername(),
      language = preferencesRepository.getLanguage(),
      theme = preferencesRepository.getTheme(),
      notificationsEnabled = preferencesRepository.isNotificationsEnabled(),
      notificationTime = null,
      dailyGoal = preferencesRepository.getDailyGoal()
  )
  ```
  And in `DataImporter.restoreUserPreferences()`, it deleted the line that restored `notificationTime`.

---

### 2. High-Severity Architectural & Concurrency Issues (P1)

#### ISS-06: Broken Stacked PR Topology (`#143`, `#144`, `#145` All Target `master`)
- **Commit Graph:**
  `7ba4b89 (master)` $\rightarrow$ `60e795f (PR #143: fix/phase0...)` $\rightarrow$ `36e42fe (PR #144: fix/phase1...)` $\rightarrow$ `fd5c46b (PR #145: fix/phase2...)`
- **The Problem:**
  Instead of branching each PR independently off `master` OR setting stacked base branches (`#144` targeting `fix/phase0...` and `#145` targeting `fix/phase1...`), all three PRs were created with `baseRefName: master`:
  - **PR #143** contains commit `60e795f` (1 commit).
  - **PR #144** contains commits `60e795f` + `36e42fe` (2 commits).
  - **PR #145** contains commits `60e795f` + `36e42fe` + `fd5c46b` (3 commits).
  - Furthermore, `WordDao.kt`, `ReviewScheduleDaoTest.kt`, `WordOfDayWorker.kt`, `DailyReminderWorker.kt`, `StreakAlertWorker.kt`, and `QuizViewModel.kt` are modified across multiple commits in this chain. Squash-merging PR #143 into `master` will immediately create merge conflicts in PR #144 and PR #145.

---

#### ISS-07: Cosmetic & Racy `QuizUiState` Refactoring in `QuizViewModel`
- **Files Affected:**
  - [QuizViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/QuizViewModel.kt#L69-L216)
  - [QuizUiState.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/QuizUiState.kt#L11-L30)
- **The Problem:**
  1. **Not True UDF (17 MutableStateFlows):** Instead of replacing the 16 individual `MutableStateFlow`s with a single source-of-truth `_uiState: MutableStateFlow<QuizUiState>` (or deriving `uiState` via `combine(...)`), `QuizViewModel` kept all 16 `MutableStateFlow`s and added a 17th `_uiState` updated imperatively via `syncUiState()`.
  2. **Multi-Threaded Data Race in `syncUiState()`:**
     ```kotlin
     private fun syncUiState() {
         _uiState.value = QuizUiState(
             currentQuestions = _currentQuestions.value,
             currentQuestionIndex = _currentQuestionIndex.value,
             ... // reads 16 separate volatile StateFlow values non-atomically
         )
     }
     ```
     `syncUiState()` is invoked concurrently from **`dispatchers.io`** (inside `startQuiz`, `loadNextQuestion`, `collectQuizStats`) AND from **`Dispatchers.Main`** (5 collectors in `init` + UI callbacks `checkAnswer`, `pauseQuiz`, `resumeQuiz`, `resetQuiz`). Because `_uiState.value = ...` is not using `_uiState.update { ... }` and reads 16 separate fields without synchronization, a background thread finishing `collectQuizStats()` can overwrite `_uiState.value` with a stale snapshot while the main thread is updating `_score` or `_timeLeft`.
  3. **Redundant Double-Emissions:** Every time `_score.value += 1` is executed in `checkAnswer()`, the `init` collector on `_score` fires `syncUiState()` AND `checkAnswer()` explicitly calls `syncUiState()`, emitting duplicate states on every answer and every timer tick.

---

#### ISS-08: Word of the Day Feature Split-Brain (`WordOfDayWorker` vs. `WordOfDayViewModel`)
- **Files Affected:**
  - [WordOfDayWorker.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/WordOfDayWorker.kt#L55-L70)
  - [WordOfDayViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/WordOfDayViewModel.kt#L68-L134)
- **The Problem:**
  - `WordOfDayWorker` selects the daily word using `val seed = (year * 1000 + dayOfYear) % totalWords` and `wordDao.getWordAtOffset(seed)`, saving it in `NotificationPreferences` (`last_word_of_day_id`).
  - However, when the user taps the notification or opens the Word of the Day card, `WordOfDayViewModel` queries the `word_of_day` Room table (`WordOfDayDao.getWordOfDayByDate(today)`) and generates new entries using `wordDao.getRandomWordForNotification(...)`!
  - Consequently, the push notification sent by `WordOfDayWorker` and the in-app Word of the Day screen show **two completely different words**.

---

#### ISS-09: Synchronous Android Keystore & Disk I/O on Main Thread During `MainActivity.onCreate()`
- **Files Affected:**
  - [MainActivity.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/MainActivity.kt#L204-L216)
  - [PreferencesRepository.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/PreferencesRepository.kt#L37-L44)
- **The Problem:**
  In `MainActivity.onCreate()`, line 206 calls `preferencesRepository.isNotificationsEnabled()` synchronously on the UI thread before `setContent`.
  Constructing `PreferencesRepository` synchronously initializes `MasterKey` via Android Keystore (`AES256_GCM`), initializes `EncryptedSharedPreferences` (reading and decrypting XML from disk), and runs `LegacyPrefsMigrator.migrate()` (reading `"user_prefs"` and writing to `"secure_user_prefs"`).
  Note that [`TrainvocApplication.kt`](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/TrainvocApplication.kt#L88-L96) explicitly enables `StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()` in debug builds and defers Tink/Keystore initialization to a background coroutine specifically to avoid main-thread Keystore stalls—which `MainActivity.onCreate()` now defeats.

---

#### ISS-10: Incomplete `SharedPreferences` Unification
- **Files Affected:**
  - [PreferencesRepository.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/PreferencesRepository.kt#L179-L185) (`setLanguage` still writes to plain `"user_prefs"` for `attachBaseContext`, while `clearLanguage()` only removes from `"secure_user_prefs"`).
  - 10+ direct `getSharedPreferences(...)` calls left untouched across:
    - [CloudBackupManager.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/backup/CloudBackupManager.kt#L455) (`"backup_prefs"`)
    - [CloudBackupViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/CloudBackupViewModel.kt#L68) (`"drive_backup_prefs"`)
    - [UpdateNotesManager.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/update/UpdateNotesManager.kt#L28) (`"update_notes_prefs"`)
    - [TutorialViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/TutorialViewModel.kt#L45) (`"trainvoc_tutorial_prefs"`)

---

#### ISS-11: Duplicate `Authorization` Headers & Fragile `tokenAuthenticator` in `NetworkModule`
- **Files Affected:**
  - [NetworkModule.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/di/NetworkModule.kt#L108-L150)
  - [AuthApiService.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/auth/AuthApiService.kt#L27-L56)
- **The Problem:**
  1. `AuthApiService.firebaseSync(@Header("Authorization") token: String)` and `validateToken(@Header("Authorization") token: String)` already attach an `Authorization` header. Because `authInterceptor` uses `.addHeader("Authorization", "Bearer $token")` instead of `.header(...)` (and doesn't check if `originalRequest.header("Authorization") != null`), OkHttp sends **two `Authorization` headers** on those requests.
  2. `tokenAuthenticator` checks `if (response.priorResponse != null) return@Authenticator null`. In OkHttp, `response.priorResponse` is also populated when an HTTP `301`/`302`/`307`/`308` redirect occurs. If a request is redirected before returning `401`, `priorResponse` is non-null on the first `401` and token refresh is skipped entirely. Standard practice is to count prior **401** responses (or check if the request's `Authorization` header already matches `freshToken`).

---

### 3. Medium-Severity Incomplete Refactorings (P2)

#### ISS-12: Superficial DAO Decoupling (Only `QuizHistoryDao` Wrapped)
- **Files Affected:**
  - [HomeViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/main/HomeViewModel.kt#L38-L45) — still directly injects `GamificationDao`, `WordDao`, and `ReviewScheduleDao`.
  - [QuizHistoryViewModel.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/viewmodel/QuizHistoryViewModel.kt#L24-L28) — still directly injects `WordDao`.
  - [WordOfDayWorker.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/WordOfDayWorker.kt#L26-L31) — directly injects `WordDao` instead of `IWordRepository`.

#### ISS-13: Placebo `@HiltWorker` Migration & Unstructured Coroutine in `WordNotificationWorker`
- **Files Affected:**
  - [DailyReminderWorker.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/DailyReminderWorker.kt#L20-L30) & [StreakAlertWorker.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/StreakAlertWorker.kt#L22-L32): Annotated with `@HiltWorker` and `@AssistedInject`, yet inject **zero dependencies** in their constructors and still call `NotificationPreferences.getInstance(applicationContext)` statically!
  - [WordNotificationWorker.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/WordNotificationWorker.kt#L15-L55) & [NotificationHelper.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/notification/NotificationHelper.kt#L127-L140): `WordNotificationWorker` is a non-Hilt `Worker` (not `CoroutineWorker`) that calls `NotificationHelper.sendWordQuizNotification(applicationContext)`, which manually creates a Room DB (`AppDatabase.DatabaseBuilder.getInstance(context)`) inside an unstructured `CoroutineScope(Dispatchers.IO).launch { ... }` while `doWork()` immediately returns `Result.success()` before the DB query even runs!

#### ISS-14: Incomplete `QuizUiState` Adoption in `QuizStatsCard` & `QuizScreen`
- **Files Affected:**
  - [QuizStatsCard.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/quiz/components/QuizStatsCard.kt#L70-L72) still collects `quizViewModel.totalWords.collectAsState()`, `learnedWords.collectAsState()`, and `progressPercent.collectAsState()` instead of accepting values from `QuizUiState`.
  - [QuizScreen.kt](file:///C:/Users/gltkn/.gemini/antigravity/worktrees/Trainvoc/review_architecture_ecosystem/TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/quiz/QuizScreen.kt#L92-L98) still holds `selectedAnswer`, `isCorrect`, `currentStreak`, and `triggerConfetti` in local composable `remember { mutableStateOf(...) }`, which is lost on configuration change and violates UDF.

#### ISS-15: 6 Major Deliverables from the Agent's Own Action Plan Were Silently Omitted
Comparing [`docs/ARCHITECTURE_ECOSYSTEM_REVIEW_2026-10-15.md`](file:///C:/Users/gltkn/.gemini/antigravity/brain/12f9158f-7ad7-4655-a522-7aec46754539/architecture_ecosystem_review.md) (`502811b`) against commits `60e795f`, `36e42fe`, and `fd5c46b`, the following planned fixes were completely skipped without notice:
1. **Phase 0.4 — Reconcile Production Domains:** `app/build.gradle.kts` still defaults to `https://api.trainvoc.com/` and `wss://api.trainvoc.com` while Nginx and backend CORS use `*.rollingcatsoftware.com`.
2. **Phase 1.1 — Resolve 10 Dead Domain UseCases:** The 10 unreferenced UseCases in `domain/usecase/` were neither wired into ViewModels nor removed.
3. **Phase 2.1 — Decompose Monolithic God Composables:** `ProfileScreen.kt` (1,100+ lines), `HomeScreen.kt` (1,000+ lines), and `WordDetailScreen.kt` were untouched.
4. **Phase 2.5 — Type-Safe Navigation (Compose 2.8+):** Navigation still uses raw string routes and mutable `remember` state in `MainActivity`.
5. **Section 2.7.3 — Unmanaged `OkHttpClient` in `GameWebSocketClient`:** `GameWebSocketClient` still instantiates its own bare `OkHttpClient.Builder().build()`.
6. **Section 2.4.2 — In-Memory Full-Table Filtering in `WordViewModel`:** `WordViewModel` still loads all words into memory to filter in Kotlin.

---

### 4. Git Worktree, Test & Build Hygiene Issues (P3)

- **ISS-16 (Worktree Branch Mismatch):** The git worktree at `C:\Users\gltkn\.gemini\antigravity\worktrees\Trainvoc\review_architecture_ecosystem` was originally created for branch `review_architecture_ecosystem` (`502811b`), but the agent checked out `fix/phase0`, `fix/phase1`, and `fix/phase2` inside the same worktree (which branched from `master` `7ba4b89`, *not* `502811b`). As a result, `docs/ARCHITECTURE_ECOSYSTEM_REVIEW_2026-10-15.md` does not even exist in the current working tree.
- **ISS-17 (Zero Tests for New Classes & Methods):** No unit tests were written for `LegacyPrefsMigrator`, `QuizUiState`, `QuizHistoryRepository`, or `WordDao.getWordAtOffset()`. `QuizViewModelTest` still tests only the 16 legacy flows.
- **ISS-18 (Partial `build.gradle.kts` Version Cleanup):** Only 3 hardcoded test versions (`mockkVersion`, `turbine`, `hilt-android-testing`) were cleaned up in `36e42fe`, while 15+ hardcoded versions (`composeVersion = "1.7.6"`, `roomVersion = "2.6.1"`, `paging_version = "3.3.5"`, `2.1.0`, `1.8.1`, etc.) remain in `TrainvocClient/app/build.gradle.kts`.
