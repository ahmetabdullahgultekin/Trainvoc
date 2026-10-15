# Trainvoc Mobile App & Entire Ecosystem Architecture Review

**Date:** 2026-10-15  
**Reviewer role:** Expert Kotlin Developer & Android Engineer  
**Branch:** `review_architecture_ecosystem`  
**Scope:** TrainvocClient (Android), TrainvocBackend (Spring Boot), TrainvocWeb (React/TypeScript), Infrastructure (Nginx, Docker Compose)  
**Methodology:** Full static analysis across codebase, dependencies, data persistence layers, concurrency/lifecycle state machines, networking pipelines, and deployment topology.

---

## 1. Executive Summary & Ecosystem Health Score

The **Trainvoc** ecosystem represents an ambitious, multi-platform language learning solution featuring a native Android client, a Spring Boot backend for multiplayer and synchronization, a React web frontend, and Docker/Nginx deployment infrastructure.

While the ecosystem showcases modern choices (Kotlin 2.3.20, Jetpack Compose Material 3, Room v19 with relational schema, pure-Kotlin FSRS-5 spaced repetition, Spring Boot 4 / Java 21, Vite/Tailwind), an in-depth audit reveals significant architectural divergence from the documented architecture ([`ARCHITECTURE.md`](../ARCHITECTURE.md)):

| Dimension | Score (1-10) | Key Finding |
|---|:---:|---|
| **Clean Architecture & Layering** | **5.0** | **"Phantom" Clean Architecture**: 10 UseCases exist and are tested, but are completely bypassed by presentation. Compose UI dependencies leak into Room database entities. Composables bypass ViewModels via Hilt EntryPoints. |
| **State Management & UDF** | **5.5** | StateFlow fragmentation (10–15 independent flows per ViewModel); mutable collections inside `StateFlow`; redundant background coroutines launched to mirror state into `SavedStateHandle`; `collectAsState()` leaks when backgrounded. |
| **Persistence & Data Integrity** | **4.5** | **Critical Split-Brain Bug**: Settings are saved to `secure_user_prefs`, but 15+ critical components (Workers, `MainActivity`, `ProfileScreen`) read from plaintext `user_prefs`. Massive in-memory full-table scans in workers and dictionary search. |
| **Dependency Injection** | **6.0** | Hilt installed cleanly, but 4 of 5 Workers bypass `HiltWorkerFactory` and manually call static singletons. Duplicate test library versions in `build.gradle.kts`. |
| **Networking & Security** | **5.5** | `runBlocking` in OkHttp interceptor; missing token refresh `Authenticator` (requests fail upon JWT expiry); backend security config has `.anyRequest().permitAll()` leaving endpoints exposed. |
| **Compose & Navigation** | **5.5** | 4 unused ViewModels leaked at `MainActivity` root; ephemeral navigation state stored in UI `remember`; missing `innerPadding` handling in edge-to-edge mode; monolithic Composables exceeding 1,200 lines. |
| **Infrastructure & Deployment** | **7.0** | Robust Docker Compose & Nginx reverse proxy with automated SSL, but production domain/CORS mismatch (`api.trainvoc.com` vs `*.rollingcatsoftware.com`). |

---

## 2. TrainvocClient (Android) Architectural Audit

```
Current Architecture Reality
┌────────────────────────────────────────────────────────────────────────┐
│ UI Layer (Jetpack Compose)                                             │
│   - Monolithic Composables (>1,200 lines in Profile, Home, WordDetail) │
│   - Direct repository access via rememberPreferencesRepository()       │
└──────────────────┬───────────────────────────────┬─────────────────────┘
                   │ Bypasses Domain Layer         │ Direct EntryPoint
                   ▼                               ▼
┌──────────────────────────────────────┐     ┌───────────────────────────┐
│ ViewModels (StateFlow / UDF)         │     │ PreferencesRepository     │
│   - 10-15 StateFlows per VM          │     │ (EncryptedSharedPreferences│
│   - Direct DAO ingestion (QuizDao)   │     │  name: "secure_user_prefs")│
└──────────────────┬───────────────────┘     └─────────────┬─────────────┘
                   │                                       │
                   │                                       │ SPLIT-BRAIN DESYNC!
                   ▼                                       ▼
┌──────────────────────────────────────┐     ┌───────────────────────────┐
│ Room Database / DAOs                 │ ◄───┤ Plain "user_prefs"        │
│   - In-memory full-table scans       │     │ (Workers, MainActivity,   │
│   - Compose Color in Room Entity     │     │  Widgets, Data Importers) │
└──────────────────────────────────────┘     └───────────────────────────┘
```

### 2.1 Layering & "Phantom" Clean Architecture

#### 1. Dead Domain Layer (UseCases Never Invoked)
- **File Location:** `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/domain/usecase/`
- **Details:** The project defines 10 use cases:
  - `CalculateProgressUseCase.kt`
  - `CheckAnswerUseCase.kt`
  - `CheckLevelUnlockedUseCase.kt`
  - `GenerateMultiplayerQuizUseCase.kt`
  - `GenerateQuizQuestionsUseCase.kt`
  - `GetUserStatsUseCase.kt`
  - `StartQuizUseCase.kt`
  - `SubmitMultiplayerAnswerUseCase.kt`
  - `SubmitQuizAnswerUseCase.kt`
  - `UpdateWordStatisticsUseCase.kt`
- **Defect:** **Not a single one of these use cases is injected or called in any production ViewModel, screen, or service.**
  - `QuizViewModel` injects `IQuizService`, `IWordStatisticsService`, `IProgressService`, and `QuizHistoryDao` directly.
  - The use cases are only referenced in `UseCaseModule.kt` (DI) and in unit tests (`UseCasesTest.kt`, `StartQuizUseCaseTest.kt`).
  - **Verdict:** The domain layer is dead code that introduces maintenance overhead without providing any architectural decoupling.

#### 2. Compose UI Types Leaking into Data Layer
- **File Location:** `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/classes/word/EntitiesAndRelations.kt` (Lines 3–4, 186–193)
- **Defect:**
  ```kotlin
  import androidx.compose.runtime.Immutable
  import androidx.compose.ui.graphics.Color
  // ...
  val examColors = mapOf(
      "TOEFL" to Color(0xFF4CAF50),
      "IELTS" to Color(0xFF2196F3),
      "YDS" to Color(0xFF9C27B0),
      "YÖKDIL" to Color(0xFFE91E63),
      "KPDS" to Color(0xFFFFC107),
      "Mixed" to Color(0xFFFF9800)
  )
  ```
  Room Database entities must be platform- and UI-agnostic data contracts. Importing Jetpack Compose UI types (`Color`) into a database entity couples storage persistence directly to the UI layer and corrupts modularization boundaries.

#### 3. Presentation Layer Bypassing ViewModels via EntryPoint Workaround
- **File Location:** `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/util/RememberPreferencesRepository.kt` (Lines 15–18) & `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/di/PreferencesEntryPoint.kt`
- **Defect:** Composables (`HomeScreen.kt`, `ProfileScreen.kt`, `SettingsScreen.kt`, `SplashScreen.kt`, `UsernameScreen.kt`) invoke `rememberPreferencesRepository()` to extract `IPreferencesRepository` directly inside the Composable function using `EntryPointAccessors.fromApplication`.
- **Impact:** Composables are doing data I/O and crypto operations directly. Clean Architecture dictates that UI Composables must be passive, consuming state emitted by a ViewModel and dispatching user intents.

---

### 2.2 Critical Persistence Defect: Split-Brain SharedPreferences

- **File Locations:**
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/PreferencesRepository.kt` (Line 30)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/MainActivity.kt` (Lines 59, 208)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/WordOfDayWorker.kt` (Line 42)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/DailyReminderWorker.kt` (Line 36)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/worker/StreakAlertWorker.kt` (Line 37)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/notification/NotificationScheduler.kt` (Lines 45, 74, 98, 217)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/profile/ProfileScreen.kt` (Line 108)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/sync/DataExporter.kt` (Line 344)
  - `TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/sync/DataImporter.kt` (Line 651)

- **The Defect:**
  `PreferencesRepository` encrypts settings under **`"secure_user_prefs"`** via `EncryptedSharedPreferences`. However, more than **15 critical sites** still read and write directly to plaintext **`"user_prefs"`**.
  
- **Concrete Failure Scenarios:**
  1. A user disables notifications or changes notification times in `SettingsScreen`. `SettingsViewModel` persists this to `"secure_user_prefs"`. `NotificationScheduler`, `DailyReminderWorker`, and `StreakAlertWorker` check `"user_prefs"`, remain unaware of the change, and continue alerting the user.
  2. In `MainActivity.attachBaseContext`, the application checks `"user_prefs"` for locale selection instead of `"secure_user_prefs"`.
  3. `DataExporter` exports data from `"user_prefs"`, leaving out encrypted preferences.

---

### 2.3 State Management & Unidirectional Data Flow (UDF)

#### 1. StateFlow Fragmentation in ViewModels
- In `QuizViewModel.kt` (Lines 68–129), there are **14 separate `MutableStateFlow` properties**:
  `_quiz`, `_quizParameter`, `_quizQuestions`, `_currentQuestion`, `_currentWordStats`, `_timeLeft`, `_isTimeOver`, `_progress`, `_score`, `_isTimeRunning`, `_isAnswered`, `_isQuizFinished`, `_isUserReady`, `_currentQuestionNumber`.
- **Anti-Pattern:** Multiple disjoint flows cause race conditions and intermediate invalid states during updates.
- **Remediation:** Consolidate into a single immutable `QuizUiState` data class:
  ```kotlin
  data class QuizUiState(
      val currentQuestion: Question? = null,
      val questionNumber: Int = 1,
      val totalQuestions: Int = 10,
      val score: Int = 0,
      val timeLeftSeconds: Int = 60,
      val isTimerRunning: Boolean = false,
      val isAnswered: Boolean = false,
      val isQuizFinished: Boolean = false,
      val isLoading: Boolean = false,
      val errorMessage: String? = null
  )
  ```

#### 2. Mutating Collections in StateFlow
- In `QuizViewModel.kt` Line 74:
  ```kotlin
  private val _quizQuestions = MutableStateFlow<MutableList<Question>>(mutableListOf())
  ```
  Mutating elements of a `MutableList` in-place does not change the list reference and therefore does not trigger flow emissions. StateFlow should only contain read-only `List<Question>`.

#### 3. Redundant Coroutine Collectors for SavedStateHandle
- In `QuizViewModel.kt` Lines 144–173, five infinite coroutines are launched in `init` to sync StateFlows into `SavedStateHandle`:
  ```kotlin
  viewModelScope.launch {
      _score.collect { score -> savedStateHandle[KEY_SCORE] = score }
  }
  ```
- **Remediation:** Jetpack Compose & ViewModel provides built-in two-way state persistence via:
  ```kotlin
  val score: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_SCORE, scoreConst)
  ```

#### 4. Lifecycle-Unaware State Collection in Compose
- `MainActivity.kt` Lines 134–135 and `HomeScreen.kt` Line 152 use `collectAsState()` instead of `collectAsStateWithLifecycle()`.
- **Risk:** Flow collection continues even when the Android Activity is in the `STOPPED` state, resulting in battery drain and background work.

---

### 2.4 Compose Architecture & Navigation Leaks

#### 1. Activity-Scoped ViewModel Leaks in `MainActivity`
- In `MainActivity.kt` Lines 121–125:
  ```kotlin
  val quizViewModel: QuizViewModel = hiltViewModel()
  val wordViewModel: WordViewModel = hiltViewModel()
  val statsViewModel: StatsViewModel = hiltViewModel()
  val settingsViewModel: SettingsViewModel = hiltViewModel()
  val storyViewModel: StoryViewModel = hiltViewModel()
  ```
- **Defect:** `quizViewModel`, `wordViewModel`, `statsViewModel`, and `storyViewModel` are initialized at the activity level and never passed down or used. Then, in `MainScreen.kt` Lines 94–99, `hiltViewModel()` is called again for each of them.
- **Impact:** The app retains duplicate ViewModel instances in memory throughout its entire lifecycle.

#### 2. Ephemeral In-Memory Navigation State
- In `MainScreen.kt` Line 89, 175, 194:
  `val parameter = remember { mutableStateOf<QuizParameter?>(null) }`
- **Defect:** When navigating from Exam Menu -> Quiz Menu -> Quiz, the parameter is passed via a local `remember` variable in the `MainScreen` Composable.
- **Impact:** Upon configuration change (rotation) or process death recovery, `parameter` is reset to `null`, causing crashes or broken quiz sessions.
- **Remediation:** Migrate to Navigation Compose 2.8+ Kotlin Serialization type-safe routes (`@Serializable`).

#### 3. Edge-to-Edge System Bar Insets Dropped
- In `MainActivity.kt` Line 113, `enableEdgeToEdge()` is activated.
- In `MainActivity.kt` Lines 169–190, `Scaffold`'s `innerPadding` is forwarded only to `WelcomeScreen`, while `SplashScreen`, `UsernameScreen`, and `MainScreen` completely ignore it.
- **Impact:** Content on non-welcome screens can be obscured by system status bars and navigation gesture pills.

#### 4. Monolithic "God Composables"
- File line count audit:
  - `ProfileScreen.kt`: **1,274 lines**
  - `WordDetailScreen.kt`: **1,126 lines**
  - `HomeScreen.kt`: **1,123 lines**
  - `SettingsScreen.kt`: **943 lines**
  - `DictionaryScreen.kt`: **896 lines**
- **Defect:** These Composables bundle UI styling, animations, dialog states, direct repository queries, and business logic into single files, resulting in long compilation times and low testability.

---

### 2.5 Database & Query Performance

#### 1. Full Database Scans in Background Workers
- In `WordOfDayWorker.kt` Line 53:
  ```kotlin
  val allWords = wordDao.getAllWords().first()
  val wordOfDay = allWords[seed]
  ```
  Loading the entire SQLite table into a Kotlin `List<Word>` in memory just to extract a single element via modulo index is a major memory and CPU spike.
- **Fix:** Add a targeted Room query:
  ```sql
  SELECT * FROM words WHERE language_id = :languageId LIMIT 1 OFFSET :offset
  ```

#### 2. In-Memory Search Filtering in `WordViewModel`
- In `WordViewModel.kt` Lines 107–114:
  Filtering and search operations load the full dataset from SQLite and execute `.filter { ... }` in Kotlin memory.
- **Impact:** Inability to scale to large vocabulary catalogs (10,000+ words).
- **Remediation:** Use SQLite `LIKE` queries with indexing, SQLite FTS5 (Full-Text Search), or Android Paging 3 (`PagingSource`).

#### 3. Static Database Singleton Anti-Pattern
- In `AppDatabase.kt` Line 94, `object DatabaseBuilder` maintains a manual singleton (`private var instance: AppDatabase?`).
- BroadcastReceivers (`NotificationActionReceiver.kt` Lines 68, 159, 178, 200), Widgets (`DailyGoalsWidgetProvider.kt`, `StreakWidgetProvider.kt`), and `WordOfDayWorker.kt` invoke `AppDatabase.DatabaseBuilder.getInstance(context)` directly, bypassing Hilt dependency injection.

---

### 2.6 Dependency Injection & Background Workers

#### 1. Inconsistent Hilt Worker Implementation
- `TrainvocApplication.kt` Line 25 implements `Configuration.Provider` and injects `HiltWorkerFactory`.
- Only `DriveBackupWorker` and `SyncWorker` use `@HiltWorker` and `@AssistedInject`.
- `WordOfDayWorker`, `DailyReminderWorker`, and `StreakAlertWorker` are standard `CoroutineWorker` classes without Hilt annotations. This forces them to fall back on manual singletons and unencrypted SharedPreferences.

#### 2. Version Catalog Conflicts in `build.gradle.kts`
- In `TrainvocClient/app/build.gradle.kts`:
  - `testImplementation(libs.mockk)` (1.13.14) is duplicated by `testImplementation("io.mockk:mockk:1.13.8")` (Line 304).
  - `testImplementation(libs.hilt.android.testing)` (2.57.2) is overridden by `testImplementation("com.google.dagger:hilt-android-testing:2.48.1")` (Line 318).
  - Version catalog (`libs.versions.toml`) must remain the single source of truth.

---

### 2.7 Networking, Security & ProGuard

#### 1. `runBlocking` in OkHttp Auth Interceptor
- In `NetworkModule.kt` Lines 119–121:
  ```kotlin
  val token = runBlocking {
      firebaseAuthRepository.getIdToken()
  }
  ```
  Calling `runBlocking` inside an OkHttp interceptor blocks worker threads and can cause thread starvation or deadlocks under concurrent traffic.

#### 2. Missing Token Refresh `Authenticator`
- The `OkHttpClient` instance has no `okhttp3.Authenticator` attached. When a JWT expires, API calls fail with HTTP 401 instead of intercepting the 401, refreshing the token, and retrying the request.

#### 3. Unmanaged `OkHttpClient` in `GameWebSocketClient`
- In `GameWebSocketClient.kt` Lines 65–69, the WebSocket client instantiates its own `OkHttpClient.Builder().build()` rather than injecting the Hilt-provided client, breaking connection pooling and logging interceptor parity.

#### 4. ProGuard / R8 Over-Preservation
- In `proguard-rules.pro` Lines 64–68:
  ```proguard
  -keep class androidx.compose.** { *; }
  -keep @androidx.compose.runtime.Composable class * { *; }
  ```
  These rules completely disable R8 dead-code stripping and inlining across all Compose code, bloating the release AAB/APK.

---

## 3. TrainvocBackend & Ecosystem Audit

### 3.1 Security Misconfiguration (`.anyRequest().permitAll()`)
- In `TrainvocBackend/src/main/java/com/rollingcatsoftware/trainvocmultiplayerapplication/config/SecurityConfig.java` Lines 49–50:
  ```java
  // All other endpoints require authentication (for future use)
  .anyRequest().permitAll()
  ```
- **Defect:** Despite the developer's comment stating that other endpoints should require authentication, the rule explicitly permits any request. Any newly created endpoint not explicitly matched earlier in the chain will be exposed publicly without authentication.
- **Fix:** Change to `.anyRequest().authenticated()`.

### 3.2 Production Domain & CORS Mismatches
- In `TrainvocClient/app/build.gradle.kts` Lines 120–123:
  - Default API base URL: `https://api.trainvoc.com/`
  - Default WS base URL: `wss://api.trainvoc.com`
- In `TrainvocBackend/src/main/java/.../config/WebSocketConfig.java` Line 15 and `nginx/nginx.conf` Lines 72, 91:
  - Allowed origins & domains: `trainvoc.rollingcatsoftware.com`, `api.trainvoc.rollingcatsoftware.com`
- **Impact:** Releases built with default settings will target `api.trainvoc.com`, failing CORS and SSL verification against the Nginx proxy.

### 3.3 Backend Build Coupling to Android Assets
- In `TrainvocBackend/build.gradle` Lines 71–75:
  ```groovy
  processResources {
      from("${projectDir}/../TrainvocClient/app/src/main/assets/database/seed_v18.json") {
          into 'seed'
      }
  }
  ```
- While keeping a single source of truth for vocabulary seed data is a sound concept, having the backend build traverse into the Android client directory breaks isolated CI/CD builds for the backend.
- **Recommendation:** Extract vocabulary seed generation into a dedicated shared repository submodule or build artifact (`tools/dictgen`).

---

## 4. Spaced Repetition (SRS) Engine Status

The project recently introduced a robust FSRS-5 implementation:
- Pure Kotlin, deterministic algorithm in `srs/algorithm/FsrsAlgorithm.kt` (following ADR `0001-srs-algorithm-fsrs-vs-sm2.md`).
- Persisted via Room in `ReviewScheduleRow.kt` and `ReviewScheduleDao.kt`.
- Migration tool provided in `Sm2ToFsrsMigrator.kt`.
- **Finding:** Legacy SM-2 code in `algorithm/SpacedRepetition.kt` and `games/SRSCard.kt` still exists. It should be fully deprecated and cleaned up once FSRS adoption across all learning modes is finalized.

---

## 5. Prioritized Action Plan

```
Action Plan Sequence
┌────────────────────────────────────────────────────────┐
│ Phase 0: Immediate Integrity & Security Fixes          │
│   1. Eliminate split-brain SharedPreferences bug       │
│   2. Fix SecurityConfig.java .anyRequest().denyAll()   │
│   3. Remove leaked ViewModels from MainActivity        │
│   4. Align release domain constants with Nginx / CORS  │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ Phase 1: Clean Architecture & Performance              │
│   1. Wire or prune dead UseCase domain layer           │
│   2. Convert remaining Workers to @HiltWorker          │
│   3. Move examColors from Room Entity to UI mapper     │
│   4. Optimize WordOfDayWorker SQL query                │
│   5. Clean duplicate versions in build.gradle.kts      │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ Phase 2: Modernization & Scalability                   │
│   1. Decompose monolithic Composables (>1,000 lines)   │
│   2. Consolidate ViewModel state into UiState classes  │
│   3. Implement OkHttp Token Refresh Authenticator      │
│   4. Remove over-broad ProGuard Compose rules          │
│   5. Migrate to Navigation Compose 2.8+ type-safe routes│
└────────────────────────────────────────────────────────┘
```

### Phase 0: Immediate Critical Fixes
1. **Unify Preferences Access:** Update `MainActivity.kt`, `ProfileScreen.kt`, `NotificationScheduler.kt`, `WordOfDayWorker.kt`, `DailyReminderWorker.kt`, `StreakAlertWorker.kt`, and sync tools to exclusively consume `IPreferencesRepository` (backed by `EncryptedSharedPreferences`).
2. **Harden Backend Security:** In `SecurityConfig.java`, replace `.anyRequest().permitAll()` with `.anyRequest().authenticated()`.
3. **Prevent ViewModel Memory Leaks:** In `MainActivity.kt`, delete unused `quizViewModel`, `wordViewModel`, `statsViewModel`, and `storyViewModel` declarations.
4. **Reconcile Domains:** Align `TRAINVOC_API_BASE_URL` in `app/build.gradle.kts` with Nginx and CORS configurations.

### Phase 1: High Priority Refactoring
1. **Resolve Domain Layer Deadlock:** Wire `QuizViewModel` to use `StartQuizUseCase` and `SubmitQuizAnswerUseCase` to realize the Clean Architecture design, or prune unreferenced use cases to avoid maintaining dead boilerplate.
2. **Convert Workers to Hilt:** Annotate `WordOfDayWorker`, `DailyReminderWorker`, and `StreakAlertWorker` with `@HiltWorker` and `@AssistedInject`.
3. **Decouple Entities from UI:** Relocate `examColors` from `EntitiesAndRelations.kt` into `ui/theme/`.
4. **Optimize Word Query:** Replace `getAllWords().first()` in `WordOfDayWorker` with a targeted SQL query.
5. **Clean Version Catalog:** Remove hardcoded version strings for MockK and Hilt in `app/build.gradle.kts`.

### Phase 2: Architecture Modernization
1. **Decompose God Composables:** Break `ProfileScreen.kt`, `HomeScreen.kt`, and `WordDetailScreen.kt` into modular composable components.
2. **Consolidate UI States:** Refactor `QuizViewModel`'s 14 StateFlows into a single `QuizUiState`.
3. **Implement OkHttp Authenticator:** Add an automatic 401 retry and token refresh interceptor.
4. **Refine ProGuard:** Remove `-keep class androidx.compose.**` from `proguard-rules.pro`.
5. **Type-Safe Navigation:** Adopt Jetpack Navigation Compose 2.8+ `@Serializable` destinations to replace string routes and eliminate mutable in-memory navigation state.
