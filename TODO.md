# Trainvoc - Unified Issue Tracker

> **Purpose**: Single source of truth for ALL issues, TODOs, bugs, and improvements across the entire project.
> **Last Updated**: 2026-06-04 (refreshed against HEAD `1f020c3`; statuses re-verified in code)
> **Total Issues**: 221
> **Strategic context**: see `ROADMAP.md` for phased path to Android v1 ship.

## How This Document Works

### For Claude AI Sessions:
1. **When you find an issue**: Add it to this document with the next available number
2. **When you fix an issue**: Mark it with ✅ and add the fix date
3. **Never delete issues**: Keep history for tracking

### Issue Format:
```
| #ID | Component | Severity | File:Line | Description | Status |
```

### Severity Levels:
- 🔴 **CRITICAL** - Production blocker, security issue, or crash
- 🟠 **HIGH** - Major feature broken or significant UX problem
- 🟡 **MEDIUM** - Feature works but has issues
- 🟢 **LOW** - Minor polish or improvement
- ⚪ **INFO** - Documentation, cleanup, or nice-to-have

### Status:
- ⬜ **OPEN** - Not yet fixed
- 🔄 **IN PROGRESS** - Being worked on
- ✅ **FIXED** - Resolved (include date)
- ❌ **WONTFIX** - Decided not to fix (include reason)
- 🔁 **DUPLICATE** - Duplicate of another issue (reference #ID)

---

## Quick Stats

| Severity | Open | Fixed | WONTFIX | Total |
|----------|------|-------|---------|-------|
| 🔴 CRITICAL | 2 | 25 | 0 | 27 |
| 🟠 HIGH | 12 | 32 | 6 | 50 |
| 🟡 MEDIUM | 2 | 63 | 11 | 76 |
| 🟢 LOW | 2 | 22 | 19 | 43 |
| ⚪ INFO | 2 | 3 | 21 | 26 |
| **TOTAL** | **20** | **145** | **57** | **222** |

> Changes 2026-06-05 (branch `exec/p0-2026-06-05`): **#221 FIXED** (clean-checkout build blocker — google-services plugin now conditional) and **#168 FIXED** (Story Mode renamed to "Learning Path"; no narrative promised). Both 🔴 CRITICAL moved Open→Fixed. Also **+#222** (new 🟠 HIGH, OPEN): unit-test source set doesn't compile (mockito-kotlin used but undeclared), discovered while verifying #168's test. Net: CRITICAL Open 4→2 / Fixed 23→25; HIGH Open 11→12; TOTAL 221→222, Open 21→20, Fixed 143→145.
> Prior refresh: **+#221** (clean-checkout build blocker, new 🔴). **#171 reclassified** ⬜ OPEN → ✅ RESOLVED-ON-HEAD (multiplayer UI exists & is wired — verified).

---

## ⭐ PRIORITIZED ACTION VIEW (read this first)

Curated, actionable slice of the issue list below, ordered by what blocks an **Android v1 ship**. Full per-issue history stays in the severity tables further down. IDs cross-reference those tables. See `ROADMAP.md` for the phased plan.

## P0 — Ship-blockers (must clear before any v1 build/submission)

- [x] **#221 — Android app does NOT build from a clean checkout** · `TrainvocClient/app/build.gradle.kts:7` applies `alias(libs.plugins.google.services)`, but `google-services.json` is gitignored (`.gitignore:9`) and absent. *Why*: the `com.google.gms.google-services` plugin fails the build when the file is missing → only the original dev's machine can build/CI. *Done when*: a fresh `git clone` + `./gradlew assembleDebug` succeeds with no Firebase file present (apply the plugin conditionally on file existence, OR commit a CI-safe template, OR gate Firebase Auth behind a build flag). **✅ FIXED 2026-06-05** (exec/p0-2026-06-05): the google-services plugin is now declared `apply false` and applied conditionally only when `app/google-services.json` exists; committed `app/google-services.json.sample` template. Verified: `:app:help` config resolves `BUILD SUCCESSFUL` both with the file absent (plugin skipped) and present (plugin applied).
- [x] **#168 — "Story Mode" has NO story content** · `TrainvocClient/app/src/main/java/.../ui/screen/main/StoryScreen.kt` · It is a CEFR leaf-button level picker; `onLevelSelected` just calls `quizViewModel.startQuiz(...)` (`MainScreen.kt` ~L169). *Why*: ships a feature whose name + "learn through stories" marketing is false. *Done when*: EITHER real chapter/story content exists behind level selection, OR the feature + all marketing copy is renamed (e.g. "Learning Path / Levels") so no narrative is promised. (Blocks #177, #178, #204, #214 which all assume story structure.) **✅ FIXED 2026-06-05** (exec/p0-2026-06-05): chose the **rename** option (building branching narrative content is a large feature, out of scope for a safe P0). User-facing labels renamed "Story Mode"/"Learn through stories" → "Learning Path"/"Practice words by CEFR level" (EN) and "Öğrenme Yolu"/"Kelimeleri CEFR seviyesine göre çalışın" (TR); resource names kept stable. Added `StoryModeRenameTest` (pure-JVM JUnit, no emulator) guarding against narrative-promise regression. Internal route/VM/screen symbols left as-is (not user-visible). #177/#178/#204/#214 stay open as future content work.
- [ ] **#219 — Start Google Play closed-testing clock** · Play Console · Production access DENIED 2026-01-25; Google requires **20+ testers active 14 consecutive days**. *Why*: non-technical hard wall, cannot be shortened — must run in parallel with all engineering. *Done when*: 20+ testers enrolled and the 14-day active window has elapsed.
- [ ] **Keystore is a single point of failure** (tracked under Deployment Log) · keystore lives only at `D:\...\password.jks` on the dev's Windows box; release signing is env-gated (`build.gradle.kts` `signingConfigs.release`) so an env-less release build is **unsigned**. *Why*: losing it = the app can never be updated again. *Done when*: keystore is securely backed up off-machine AND the release pipeline reliably signs (env vars wired or documented).

## P1 — v1 quality / auth gaps (clear before production submission)

- [ ] **#192 — No Google Sign-In button** · `LoginScreen.kt` (a `GoogleAuthManager` already exists, just unwired). *Done when*: Google Sign-In flow works end-to-end or is explicitly deferred + hidden.
- [ ] **#191 — No email-verification UI** · `AuthRepository.kt:219` has the code path but no screen prompts the user. *Done when*: a verification screen exists, or email/password signup is gated/removed for v1.
- [ ] **#193 — No session-timeout handling** · `AuthRepository.kt` · no auto-logout on token expiry. *Done when*: expired-token responses route the user to re-auth.
- [ ] **#194 — Leaderboard is a "Coming Soon" placeholder** · `LeaderboardScreen.kt`. *Done when*: either hidden honestly for standalone v1, or wired to the (later-deployed) backend.
- [ ] **Cloud sync / Drive backup is local-only** · `sync/CloudBackupManager.kt:437,459` (`TODO`). *Done when*: presented honestly as "Coming Soon" in standalone v1 (real Drive impl is a Phase-3 item, not a v1 blocker).
- [ ] **Web/Backend dependency + security debt** · 22 open Dependabot PRs + **security PR #32** (5 TrainvocWeb alerts). *Why*: must be merged before web/backend go public (Phase 3/4). *Done when*: security PR #32 merged and Dependabot backlog triaged.

## P2 — Engagement & feature depth (post-submission, pre-1.x)

- [ ] **#179** adaptive difficulty · `QuizScreen.kt` — quiz doesn't adapt to performance.
- [ ] **#181** hint system · `QuizScreen.kt` — no reveal-letter / eliminate-option.
- [ ] **#180 / #199 / #200** quiz audio feedback + speed bonuses · `QuizScreen.kt`.
- [ ] **#177 / #178** chapter structure + contextual/thematic word grouping · `StoryScreen.kt` (depends on #168 decision).
- [ ] **#189** friend system · social — add/view friends (needs deployed backend).
- [ ] **#184** Flip Cards no pinch-zoom / manual enlarge · `GameScreens.kt`.

## P3 — Polish, analytics, cleanup

- [ ] **#205 / #206** Google Analytics events + Firestore for social data.
- [ ] **#201** notification badges on nav · `AppBottomBar.kt`.
- [ ] **#209** audit for unreachable screens · navigation.
- [ ] **#217** `lastAnswered` collected but never displayed · `StatsViewModel.kt:76`.
- [ ] **#214** session-length recommendations · `StoryScreen.kt`.
- [ ] **Stale docs cleanup**: root `CLAUDE.md`, `TrainvocClient/CLAUDE.md`, `GAMES_UI_INVESTIGATION.md` still claim games are deleted / TTS unwired — both false on HEAD. *Done when*: corrected or marked superseded.

---

## 🔴 CRITICAL Issues (Production Blockers)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 001 | Web | `WebSocketService.ts:87` | WebSocket URL fallback uses `localhost:8080` - will fail in production | ✅ FIXED 2026-01-25 |
| 002 | Web | `ContactPage.tsx:79` | Contact form just logs to console - form is completely broken | ✅ FIXED 2026-01-25 |
| 003 | Web | `api.ts:4` | HTTP fallback (`http://localhost:8080`) - insecure if env not set | ✅ FIXED 2026-01-25 |
| 004 | Android | `MultiplayerRepository.kt:299` | Password hashing uses `hashCode()` instead of cryptographic hash - SECURITY | ✅ FIXED 2026-01-25 |
| 005 | Android | `PlayGamesAchievementMapper.kt:15` | Placeholder achievement IDs - not real Play Console IDs | ✅ FIXED 2026-01-25 |
| 006 | Android | `CloudBackupManager.kt:437` | Cloud backup upload not implemented - returns "Coming Soon" exception | ✅ FIXED 2026-01-25 |
| 007 | Android | `CloudBackupManager.kt:459` | Cloud backup download not implemented - returns "Coming Soon" exception | ✅ FIXED 2026-01-25 |
| 008 | Android | `TutorialViewModel.kt:12-65` | Stub implementation - needs real tutorial logic | ✅ FIXED 2026-01-26 |
| 009 | Android | `TutorialOverlay.kt:12-30` | Stub implementation - needs actual overlay UI | ✅ FIXED 2026-01-26 |
| 010 | Backend | `SyncController.java:186` | Now returns actual sync counts and timestamps from database | ✅ FIXED 2026-01-25 |
| 011 | Backend | `SyncServiceTest.java:305` | Proper test coverage with mocked repositories | ✅ FIXED 2026-01-25 |
| 012 | Infra | `SSL_SETUP.md` | SSL setup guide created - needs server execution | 🔄 DOCUMENTED |
| 151 | Android | `AppBottomSheet.kt:119` | "Test Mode - For development use only" banner visible in production build | ✅ FIXED 2026-01-25 |
| 152 | Android | `AndroidManifest.xml` | CLEARTEXT communication to 10.0.2.2 not permitted - Android 9+ blocks HTTP | ✅ FIXED 2026-01-25 |
| 153 | Android | `WordViewModel.kt:175` | Heart/favorite button not responding - toggleFavorite wasn't refreshing UI | ✅ FIXED 2026-01-25 |
| 154 | Android | `DictionaryScreen.kt:364` | Alphabet sidebar overlaps heart icons - added end padding to LazyColumn | ✅ FIXED 2026-01-25 |
| 155 | Android | `ProfileScreen.kt:144-159` | 0% Mastery displayed - now uses learnedWords/totalWords for better UX | ✅ FIXED 2026-01-25 |
| 156 | Android | `ProfileScreen.kt:144-159` | 0m Study Time - now shows total time when today's time is 0 | ✅ FIXED 2026-01-25 |
| 157 | Android | `StatsScreen.kt` | Stats inconsistency: Profile shows TODAY's quizzes, Stats shows TOTAL | ⚪ BY DESIGN |
| 158 | Android | Navigation | No Login/Register access point visible in app - only Sign Out shown | ✅ FIXED 2026-01-25 (added Sign In button, connected Login/Register screens) |
| 168 | Android | `StoryScreen.kt` | Story Mode has NO actual story content - just level selection screen, violates "Learn through stories" promise | ✅ FIXED 2026-06-05 (renamed "Story Mode"→"Learning Path" + subtitle, EN+TR; no narrative promised; StoryModeRenameTest guards it. Real story content deferred to #177/#178.) |
| 169 | Android | `FlipCardsScreen.kt:157` | Cards too small on 6x6 grid (~45dp) - unreadable for longer words, game essentially broken | ✅ FIXED 2026-01-27 (long-press popup, min touch target) |
| 170 | Android | `QuizScreen.kt` | Streaks don't persist between sessions - resets every time user opens app, kills engagement | ✅ FIXED 2026-01-27 (validate streak in HomeViewModel) |
| 171 | Android | `ui/multiplayer/*`, `navigation/MultiplayerNavigation.kt` | Multiplayer game UI was deleted from codebase - major feature missing | ✅ RESOLVED-ON-HEAD 2026-06-04 (restored in `fb6d0bc`; 6 screens — MultiplayerHome/CreateRoom/JoinRoom/Lobby/Game/GameResults — exist and are wired via `multiplayerNavGraph` → `Route.MULTIPLAYER_HOME`. Single-player games likewise restored under `ui/games/`. Stale claim.) |
| 221 | Android | `app/build.gradle.kts:7` + `.gitignore:9` | Clean checkout does NOT build: `google.services` plugin applied unconditionally but `google-services.json` is gitignored/absent → only original dev/CI-with-secret can build | ✅ FIXED 2026-06-05 (plugin now `apply false` + applied conditionally on `app/google-services.json` presence; added `google-services.json.sample`; `:app:help` config resolves BUILD SUCCESSFUL with file absent and present) |
| 172 | Android | `HomeScreen.kt:249-326` | Home screen has 6+ quick action buttons with flat hierarchy - causes decision paralysis, no clear CTA | ✅ FIXED 2026-01-28 (hero CTA button, 2x2 secondary grid) |
| 173 | Android | `ProfileScreen.kt:102` | Auth state checked via SharedPreferences, should use AuthViewModel - causes sync issues on logout | ✅ FIXED 2026-01-27 (AuthViewModel integration) |

---

## 🟠 HIGH Issues (Major Features/UX)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 013 | Web | `QuestionCard.tsx:75-78` | "Play Audio"/"Stop" hardcoded in English - breaks Turkish UI | ✅ FIXED 2026-01-25 |
| 014 | Web | `ErrorBoundary.tsx:77-82` | Button labels hardcoded in Turkish ("Tekrar Dene") | ✅ FIXED 2026-01-25 |
| 015 | Web | `PictureMatchGame.tsx:237` | Image `<img>` missing `alt` attribute - WCAG failure | ✅ FIXED 2026-01-25 |
| 016 | Android | `DictionaryScreen.kt:628` | Hardcoded pink `Color(0xFFE91E63)` for favorites heart | ✅ FIXED 2026-01-25 |
| 017 | Android | `DictionaryScreen.kt:574-584` | Audio button only `24.dp` - below 48dp touch target minimum | ✅ FIXED 2026-01-25 |
| 018 | Android | `HomeScreen.kt:188` | Username shows `username_placeholder` instead of actual user | ✅ FIXED 2026-01-25 |
| 019 | Android | `strings.xml:87` | "Backup features coming soon" contradicts existing CloudBackupScreen | ✅ FIXED 2026-01-25 |
| 020 | Android | `DictionaryScreen.kt:810` | AlphabetFastScroll missing contentDescription for letters | ✅ FIXED 2026-01-25 |
| 021 | Android | `LeaderboardScreen.kt:93-99` | Hardcoded mock users - `Alex, Sarah, Mike, You, Emma` with fake scores | ✅ FIXED 2026-01-25 |
| 022 | Android | `ListeningQuizGame.kt:162-167` | TTS URL placeholder - returns `tts://$language/$text` string | ❌ WONTFIX (not used - Android TTS API used instead) |
| 023 | Android | `ImageService.kt:119-123` | Mock Unsplash URL - uses deprecated random API endpoint | ✅ FIXED 2026-01-25 |
| 024 | Android | `CloudBackupManager.kt:470` | Cloud metadata returns null - placeholder | ❌ WONTFIX (expected - cloud backup is Coming Soon) |
| 025 | Android | `AnalyticsService.kt:66` | Daily stats return totals - no date filtering implemented | ✅ FIXED 2026-01-26 (now uses StatisticDao date-filtered queries) |
| 026 | Android | `AnalyticsService.kt:83` | Weekly stats return totals - no date filtering implemented | ✅ FIXED 2026-01-26 (now uses StatisticDao date-filtered queries) |
| 027 | Android | `ProfileScreen.kt:149-150` | Accuracy shown as "rough estimate" - misleading to users | ✅ FIXED 2026-01-25 |
| 028 | Android | `DictionaryScreen.kt:247` | "Recent Searches" hardcoded instead of stringResource | ✅ FIXED 2026-01-25 |
| 029 | Android | `DictionaryScreen.kt:235` | "Clear search" hardcoded contentDescription | ✅ FIXED 2026-01-25 |
| 030 | Android | `DictionaryScreen.kt:262` | "Search history" hardcoded contentDescription | ✅ FIXED 2026-01-25 |
| 031 | Android | `CloudBackupScreen.kt:71` | "Cloud Backup" hardcoded title instead of stringResource | ✅ FIXED 2026-01-25 |
| 032 | Android | `CloudBackupScreen.kt:156-187` | Multiple hardcoded feature descriptions | ✅ FIXED 2026-01-25 |
| 033 | Android | `ProfileScreen.kt:109` | "Profile" hardcoded title instead of stringResource | ✅ FIXED 2026-01-25 |
| 034 | Android | `Buttons.kt:80,127` | Generic "Button icon" contentDescriptions | ✅ FIXED 2026-01-25 |
| 035 | Android | `ModernComponents.kt:183` | Generic "Icon" contentDescription | ✅ FIXED 2026-01-25 |
| 036 | Infra | N/A | CORS configured - default: trainvoc.rollingcatsoftware.com, dev: localhost | ✅ FIXED 2026-01-25 |
| 159 | Android | `HomeScreen.kt` | Duplicate top bar/header visible - should have exactly one | ✅ FIXED 2026-01-26 |
| 160 | Android | `HomeScreen.kt:360-362` | "Perfect Start" achievement appears twice - added distinctBy filter | ✅ FIXED 2026-01-25 |
| 161 | Android | `StoryScreen.kt:75-83` | Missing level names - now shows both A1/A2 code AND Beginner/Elementary | ✅ FIXED 2026-01-25 |
| 162 | Android | `StoryScreen.kt:147-162` | Progress bar added per level showing learned/total words | ✅ FIXED 2026-01-25 |
| 174 | Android | `HomeScreen.kt:750-762` | HomeScreen shows first letter in circle, should show user's emoji avatar | ✅ FIXED 2026-01-27 (avatar parameter added to HomeHeader) |
| 175 | Android | `LobbyScreen.kt:251` | Multiplayer lobby uses generic Person icon instead of player avatar emoji | ✅ FIXED 2026-01-28 |
| 176 | Android | `GameResultsScreen.kt:251` | Multiplayer results uses generic Person icon instead of player avatar | ✅ FIXED 2026-01-28 |
| 177 | Android | `StoryScreen.kt` | No chapter structure within levels - just flat level selection, no progressive chapters | ⬜ OPEN |
| 178 | Android | `StoryScreen.kt` | No contextual learning - words aren't grouped by narrative themes or usage context | ⬜ OPEN |
| 179 | Android | `QuizScreen.kt` | No adaptive difficulty - quiz doesn't adjust based on user performance | ⬜ OPEN |
| 180 | Android | `QuizScreen.kt` | No sound effects - only haptic feedback, missing audio cues for engagement | ⬜ OPEN |
| 181 | Android | `QuizScreen.kt` | No hint system - no "reveal letter" or "eliminate option" hints available | ⬜ OPEN |
| 182 | Android | `GameScreens.kt:338-343` | Flip Cards font size hardcoded based on char count, user cannot adjust | ✅ FIXED 2026-01-27 (long-press popup with large text) |
| 183 | Android | `GameScreens.kt:318-324` | Flip Cards popup auto-dismisses after 2 seconds, should stay until user closes | ✅ FIXED 2026-01-27 (popup stays until dismissed) |
| 184 | Android | `GameScreens.kt` | Flip Cards no pinch-zoom or manual card enlargement option | ⬜ OPEN |
| 185 | Android | `AppBottomBar.kt` | Only 4 nav items, design intended for 5 with center play button (isCenter param unused) | ✅ FIXED 2026-01-28 (5 items: Home, Games, Play center, Dictionary, Profile) |
| 186 | Android | `HomeScreen.kt` | No welcome/greeting message - doesn't say "Welcome back, [Name]!" | ✅ FIXED 2026-01-27 (time-based greeting added) |
| 187 | Android | `HomeScreen.kt` | No "continue where you left off" feature - users must search for last activity | ✅ FIXED 2026-01-28 (ContinueCard shows last quiz type, accuracy, time ago) |
| 188 | Android | `ProfileScreen.kt:174` | Quizzes count shows TODAY only, but StatsScreen shows ALL-TIME - confusing inconsistency | ✅ FIXED 2026-01-28 (ProfileScreen now uses totalQuizzesAllTime) |
| 189 | Android | Social | Friend system not implemented - no way to add/view friends | ⬜ OPEN |
| 190 | Android | Feedback | No feedback system - users cannot easily report issues or suggestions | ✅ FIXED 2026-01-28 (email-based bug report + feedback in HelpScreen) |
| 191 | Android | `AuthRepository.kt:219` | Email verification code exists but no UI screen to prompt verification | ⬜ OPEN |
| 192 | Android | `LoginScreen.kt` | No Google Sign-In option - only email/password despite GoogleAuthManager existing | ⬜ OPEN |
| 193 | Android | `AuthRepository.kt` | No session timeout handling - no automatic logout on token expiration | ⬜ OPEN |
| 194 | Android | `LeaderboardScreen.kt` | Leaderboard is placeholder "Coming Soon" - not functional | ⬜ OPEN |
| 219 | Android | `QuizScreen.kt` | No visible exit/back button during quiz - bars hidden, user can only use system back gesture (not discoverable) | ✅ FIXED 2026-01-27 (X button added, exit dialog enhanced) |
| 222 | Android | `app/src/test/.../viewmodel/*Test.kt`, `domain/usecase/*Test.kt` | Unit-test source set does NOT compile: ~11 test files use `org.mockito.kotlin` symbols (`whenever`/`any`/`eq`/`verify`/`never`) but **mockito-kotlin is not a declared test dependency** (project uses MockK); some also pass wrong ctor args (e.g. `WordViewModelTest`/`WordOfDayViewModelTest`). `./gradlew :app:testDebugUnitTest` fails at `compileDebugUnitTestKotlin` → no unit tests can run. Found 2026-06-05 while verifying #168's test. | ⬜ OPEN |

---

## 🟡 MEDIUM Issues (Partial Functionality)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 037 | Web | `ProfilePage.tsx:69` | Turkish placeholder "Nick gir..." in English UI | ✅ FIXED 2026-01-25 |
| 038 | Web | `InstallPrompt.tsx:94-98` | Close button missing `aria-label` | ✅ FIXED 2026-01-25 |
| 039 | Web | Multiple game files | All 7 game components now use `resetGame()` state function | ✅ FIXED 2026-01-25 |
| 040 | Web | `CreateRoomPage.tsx:162` | Using `index` as React key - anti-pattern | ✅ FIXED 2026-01-25 |
| 041 | Web | `FeaturesGrid.tsx:135` | Using `index` as React key - anti-pattern | ✅ FIXED 2026-01-25 |
| 042 | Android | `ModernComponents.kt:162` | Hardcoded gray colors for disabled gradient button | ✅ FIXED 2026-01-25 |
| 043 | Android | `ModernComponents.kt:184` | Hardcoded `Color.White` for icon tint | ✅ FIXED 2026-01-25 |
| 044 | Android | `ModernComponents.kt:193` | Hardcoded `Color.White` for text | ✅ FIXED 2026-01-25 |
| 045 | Android | `DictionaryScreen.kt:519` | Hardcoded `Color.White` for chip selected text | ✅ FIXED 2026-01-25 |
| 046 | Android | `SplashScreen.kt:87` | Fixed 96.dp size for splash - should scale for tablets | ✅ FIXED 2026-01-26 |
| 047 | Android | `DictionaryScreen.kt:459` | Fixed 80.dp letter preview box size | ✅ FIXED 2026-01-26 |
| 048 | Android | `QuizScreen.kt:98` | Fixed 180.dp Lottie animation size | ❌ WONTFIX (no Lottie in QuizScreen) |
| 049 | Android | `AnswerOptionCard.kt:181-182` | Fixed 32.dp checkmark icon - below 48dp minimum | ❌ WONTFIX (visual indicator, not touch target) |
| 050 | Android | `CloudBackupScreen.kt:149` | Fixed 80.dp CloudOff icon size | ✅ FIXED 2026-01-26 |
| 051 | Android | `DictionaryScreen.kt:347` | Empty state doesn't handle data loading errors | ✅ FIXED 2026-01-26 |
| 052 | Android | `CloudBackupScreen.kt` | No error state UI when backup operations fail | ✅ FIXED 2026-01-26 |
| 053 | Android | `QuizScreen.kt:288-293` | Shows loading but no error state if questions fail | ✅ FIXED 2026-01-26 |
| 054 | Android | `DictionaryScreen.kt:336-345` | Shimmer loading shown but no error message if search fails | ✅ FIXED 2026-01-26 (error state added in #051) |
| 055 | Android | `CloudBackupScreen.kt:108` | SignedInContent doesn't show clear loading state | ✅ VERIFIED (has ButtonLoader + RollingCatLoader) |
| 056 | Android | `ProfileScreen.kt` | No loading skeleton for stats before they load | ✅ FIXED 2026-01-26 |
| 057 | Android | `DictionaryScreen.kt:315-331` | Simple "Loading" text instead of shimmer animation | ✅ FIXED 2026-01-26 (removed redundant text, using shimmer) |
| 058 | Android | `DictionaryScreen.kt:759-792` | Empty state doesn't handle ALL empty scenarios | ✅ FIXED 2026-01-26 |
| 059 | Android | `CloudBackupScreen.kt:184-188` | Feature list uses emoji checkmarks - inconsistent | ✅ FIXED 2026-01-26 |
| 060 | Android | `QuizScreen.kt:190` | InfoButton missing contentDescription for info icon | ✅ FIXED (uses stringResource) |
| 061 | Android | `HomeScreen.kt:166-172` | Background image contentDescription is "Background decoration" | ✅ FIXED 2026-01-26 |
| 062 | Android | `DictionaryScreen.kt:809` | AlphabetFastScroll fixed 32.dp width | ✅ FIXED 2026-01-25 |
| 063 | Android | `QuizScreen.kt:210` | LazyColumn uses fixed Spacing - no tablet adaptation | ✅ FIXED 2026-01-26 |
| 064 | Android | `SplashScreen.kt` | No responsive considerations for landscape | ✅ FIXED 2026-01-26 (uses minOf for responsive) |
| 065 | Android | `ProfileScreen.kt:81-85` | No horizontal padding adjustment for ultra-wide | ✅ FIXED 2026-01-26 |
| 066 | Android | `DailyGoalsScreen.kt:78-86` | Study time hardcoded to 0/30 - not tracking actual time | ✅ FIXED 2026-01-26 |
| 067 | Android | `WordProgressScreen.kt:299,321` | Learning timeline hardcoded `listOf(12, 18, 15, 22)` | ✅ FIXED 2026-01-26 |
| 068 | Android | `HomeScreen.kt:671` | Missing components marked as stubs | ✅ FIXED 2026-01-26 (removed outdated comment, components are implemented) |
| 069 | Android | `BackupScreen.kt:462` | Cloud backup shows "not yet implemented" message | ❌ WONTFIX (honest status message) |
| 070 | Android | `BackupScreen.kt:514` | Coming Soon section | ❌ WONTFIX (intentional product roadmap) |
| 071 | Android | `SettingsScreen.kt:340,345` | Leaderboard marked "Coming soon" - shows toast only | ✅ FIXED 2026-01-26 |
| 072 | Android | `UpdateNotesCard.kt:162-164` | "Coming Soon" section for features | ❌ WONTFIX (intentional upcoming features list) |
| 073 | Android | `ChangelogScreen.kt:225` | "Coming Soon" label in changelog | ❌ WONTFIX (intentional product roadmap) |
| 074 | Android | `DictionaryScreen.kt:336-345` | Shimmer shows for ALL cards during search | ❌ WONTFIX (appropriate UX for loading) |
| 075 | Android | `QuizScreen.kt:289-293` | CircularProgressIndicator + text "Loading..." redundant | ✅ FIXED 2026-01-26 |
| 076 | Android | `DictionaryScreen.kt:432-451` | AlphabetFastScroll letters are very small touch targets | ✅ FIXED 2026-01-25 |
| 077 | Android | `Buttons.kt:78-82` | Icon button in complex button is 20.dp | ❌ WONTFIX (button is 48dp, icon visual only) |
| 078 | Android | `CloudBackupManager.kt:565` | Placeholder for cloud metadata | ❌ WONTFIX (expected - cloud backup is Coming Soon) |
| 079 | Docs | Multiple | 116 documentation files need audit (see DOCUMENT_AUDIT_TRACKER.md) | ❌ WONTFIX (documentation housekeeping, not affecting app) |
| 080 | Android | `strings.xml` | Email placeholder uses old branding | ✅ VERIFIED (uses rollingcat.help@gmail.com) |
| 081 | Android | Various | Inconsistent MaterialTheme vs hardcoded colors throughout | ❌ WONTFIX (refactoring task, 344 occurrences - many in theme files where expected) |
| 082 | Android | `FavoritesScreen.kt:237` | Level chip onClick empty | ❌ WONTFIX (display-only chip, onClick required by API) |
| 083 | Android | `WordDetailScreen.kt:499` | Exam chips onClick empty | ❌ WONTFIX (display-only chips) |
| 084 | Android | `UserFeatureFlagScreen.kt:246,256` | Premium/Uses Data chips onClick empty | ❌ WONTFIX (display-only chips) |
| 085 | Android | `AdminFeatureFlagScreen.kt:303,310,320` | Filter chips onClick empty | ❌ WONTFIX (display-only chips) |
| 086 | Android | `SubscriptionScreen.kt:321` | Disabled "Current Plan" button onClick empty | ❌ WONTFIX (disabled button, onClick ignored) |
| 087 | Android | `NavigationCard.kt:132,137,151` | Preview defaults with empty onClick | ❌ WONTFIX (preview composables) |
| 088 | Android | `ExampleSentenceCard.kt:117,130` | Preview defaults with empty onClick | ❌ WONTFIX (preview composables) |
| 089 | Android | `CloudBackupScreen.kt:197` | "Sign in" contentDescription should be "Sign in with Google" | ✅ FIXED (already correct) |
| 090 | Android | `CloudBackupScreen.kt:74` | "Back" contentDescription too generic | ✅ FIXED 2026-01-26 |
| 091 | Android | `CloudBackupScreen.kt:80` | "Refresh" should be "Refresh backup list" | ✅ FIXED 2026-01-26 |
| 092 | Android | `WordDetailScreen.kt:1031` | Comment says "Phase 7 Complete" - outdated | ✅ FIXED 2026-01-26 |
| 093 | Web | `Podium.tsx:63` | Fixed max-width truncation 100px for player names | ✅ FIXED 2026-01-25 |
| 094 | Web | `ListeningQuizGame.tsx:263` | Custom motion.button for audio player - intentional design | ❌ WONTFIX |
| 163 | Android | `HelpScreen.kt` | Placeholder phone number "+1 234 567 890" in Contact Support section | ✅ FIXED 2026-01-25 (removed fake phone support) |
| 164 | Android | `StatsScreen.kt` | Performance Trends bar chart shows same value (24) for all periods | ✅ FIXED 2026-01-25 (show only total, note about time-based coming soon) |
| 165 | Android | `StoryScreen.kt` | Repetitive lock messages shown on every locked level | ✅ FIXED 2026-01-25 (simplified locked state UI) |
| 195 | Android | `AppNavigationDrawer.kt` | Missing user profile section (avatar + name) in drawer header | ✅ FIXED 2026-01-28 (avatar, username, inline streak in drawer header) |
| 196 | Android | `SettingsScreen.kt` | Missing user profile card/section at top of settings | ✅ FIXED 2026-01-28 (profile card with avatar at top of settings) |
| 197 | Android | `StatsViewModel.kt:77-78` | dailyCorrect/weeklyCorrect collected but never displayed in UI | ✅ FIXED 2026-01-28 |
| 198 | Android | `QuizScreen.kt:224-230` | Stats card hidden by default - must toggle info icon, poor discoverability | ✅ FIXED 2026-01-28 (default showStats=true) |
| 199 | Android | `QuizScreen.kt` | No speed bonuses - time pressure exists but no reward for fast answers | ⬜ OPEN |
| 200 | Android | `FlipCardsScreen.kt:432-451` | Touch targets below 48dp minimum on 6x6 grids (~45dp cards) | ✅ FIXED 2026-01-27 (added sizeIn(minWidth=48dp)) |
| 201 | Android | `AppBottomBar.kt` | No notification badges on nav items | ⬜ OPEN |
| 202 | Android | `HomeScreen.kt:328-362` | Stats preview section redundant with HomeHeader - shows same info twice | ✅ FIXED 2026-01-28 (replaced with distinct progress: words ratio, all-time quizzes, study time) |
| 203 | Android | `Color.kt` | DEFAULT palette missing AMOLED variant - falls back to dark theme | ✅ FIXED 2026-01-28 |
| 204 | Android | `StoryScreen.kt` | No story-specific achievements - achievements are all generic | ⬜ OPEN |
| 205 | Android | Analytics | Google Analytics not fully integrated - missing event tracking | ⬜ OPEN |
| 206 | Android | Firebase | Firestore not used for leaderboard/user data - could enhance social features | ⬜ OPEN |
| 207 | Android | `GameScreen.kt` | Multiplayer game screen missing player avatars in ranking display | ✅ FIXED 2026-01-28 |
| 208 | Android | `FlipCardsScreen.kt:113-118` | Grid size options too limited - only 3 options, no responsive adjustment | ✅ FIXED 2026-01-28 |
| 209 | Android | Navigation | Some screens may be inaccessible from normal navigation - needs audit | ⬜ OPEN |

---

## 🟢 LOW Issues (Polish)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 095 | Android | `QuizScreen.kt:98` | Fixed Lottie size could adapt | ❌ WONTFIX (no Lottie in QuizScreen) |
| 096 | Android | `AnswerOptionCard.kt:148-150` | ContentDescription could be more specific | ✅ VERIFIED (already has state-aware descriptions) |
| 097 | Android | `DictionaryScreen.kt:336-345` | Loading text during search basic | ✅ FIXED 2026-01-26 (same as #057) |
| 098 | Android | Multiple screens | No empty state for quiz results when no quizzes | ✅ VERIFIED (NoQuizState exists) |
| 099 | Android | `strings.xml:79` | "Theme selection coming soon" - outdated | ✅ FIXED 2026-01-26 |
| 100 | Android | `CloudBackupScreen.kt:156-200` | Features mention Google Drive - should mention Trainvoc | ✅ FIXED 2026-01-26 |
| 101 | Web | `Header.tsx:94-95` | Language button correctly uses sr-only | ✅ NOTED (good pattern) |
| 102 | Android | `ProfileScreen.kt:112` | "Back" hardcoded contentDescription | ✅ FIXED 2026-01-26 |
| 103 | Android | Various | Test files contain hardcoded localhost references | ❌ WONTFIX (tests use predictable URLs) |
| 104 | Docs | `sync/README.md:730` | TODO: Add encryption for backups | ❌ WONTFIX (documentation note) |
| 105 | Docs | `sync/README.md:294` | Cloud provider implementation is placeholder | ❌ WONTFIX (documentation note) |
| 106 | Docs | `sync/README.md:888` | Cloud backup described as placeholder | ❌ WONTFIX (documentation note) |
| 107 | Android | `DataExporter.kt` | Error handling note - non-production-ready | ❌ WONTFIX (helpful code comment) |
| 108 | Android | `GoogleAuthManager.kt:~150` | Simplified version mentioned in comments | ❌ WONTFIX (helpful code comment) |
| 109 | Android | `ListeningQuizGame.kt` | Production note about TTS integration | ❌ WONTFIX (helpful code comment) |
| 110 | Web | `ProfilePage.tsx:29` | Good responsive design | ✅ NOTED (good pattern) |
| 111 | Web | `Header.tsx:145` | Mobile menu overflow handling | ✅ NOTED (good pattern) |
| 112 | Web | `LeaderboardPage.tsx:68-73` | Proper loading state | ✅ NOTED (good pattern) |
| 113 | Web | `CreateRoomPage.tsx:360-363` | Empty state for rooms | ✅ NOTED (good pattern) |
| 114 | Web | `JoinRoomPage.tsx:122-136` | Good error differentiation | ✅ NOTED (good pattern) |
| 115 | Web | `style.css:148-192` | Dark mode CSS properly defined | ✅ NOTED (good pattern) |
| 116 | Web | `JoinRoomPage.tsx:206` | Room code input with validation - shows format hint and error states | ✅ FIXED 2026-01-25 |
| 117 | Android | `ShimmerEffect.kt:246` | Chart placeholder comment | ❌ WONTFIX (descriptive comment) |
| 118 | Android | `ShimmerEffect.kt:259` | Simple text placeholder comment | ❌ WONTFIX (descriptive comment) |
| 119 | Android | `UnifiedStates.kt:194-299` | Multiple placeholder comments for shimmer | ❌ WONTFIX (descriptive comments) |
| 120 | Android | `FillInTheBlankScreen.kt:236` | placeholder prop for TextField | ❌ WONTFIX (valid API usage) |
| 121 | Android | `SpellingChallengeScreen.kt:223` | placeholder prop for TextField | ❌ WONTFIX (valid API usage) |
| 122 | Android | `WordScrambleScreen.kt:205` | placeholder prop for TextField | ❌ WONTFIX (valid API usage) |
| 123 | Android | `GameScreens.kt:472,481` | placeholder parameter in function | ❌ WONTFIX (valid API usage) |
| 124 | Android | `FavoritesScreen.kt:87` | placeholder prop for TextField | ❌ WONTFIX (valid API usage) |
| 125 | Android | `DictionaryScreen.kt:219` | placeholder prop for TextField | ❌ WONTFIX (valid API usage) |
| 126 | Android | `HomeScreen.kt:188` | username_placeholder string resource used | ❌ WONTFIX (valid string resource) |
| 166 | Android | `ProfileScreen.kt` | Edit Profile dialog only has Username field - no avatar, email, etc. | ✅ FIXED 2026-01-26 |
| 167 | Android | `SplashScreen.kt` | Splash screen duration too long for returning users | ✅ FIXED 2026-01-26 |
| 210 | Android | `ProfileScreen.kt:845-851` | No logout confirmation dialog - direct logout on tap, could be accidental | ✅ FIXED 2026-01-27 (confirmation dialog added) |
| 211 | Android | `Theme.kt:362` | High contrast + AMOLED can't work simultaneously - one overrides other | ✅ FIXED 2026-01-28 |
| 212 | Android | `Color.kt:100-103` | AMOLED surface color is #0D0D0D not pure black #000000 - less battery savings | ✅ FIXED 2026-01-28 (all 6 themes updated to pure black 0xFF000000) |
| 213 | Android | `HomeScreen.kt:364-391` | Achievements section shown when most users have none - wastes space | ✅ VERIFIED 2026-01-28 (already guarded with isNotEmpty() check) |
| 214 | Android | `StoryScreen.kt` | Missing session length recommendations - users don't know ideal study time | ⬜ OPEN |
| 215 | Android | `SplashScreen.kt` | No tap-to-skip functionality for splash screen | ✅ FIXED 2026-01-28 (tap anywhere to skip, prevents double navigation) |
| 216 | Android | `AppConfig.kt` | Splash screen configuration not in AppConfig - hardcoded in SplashScreen.kt | ✅ FIXED 2026-01-28 |
| 217 | Android | `StatsViewModel.kt:76` | lastAnswered variable collected but never displayed in UI | ⬜ OPEN |
| 218 | Android | `AuthViewModel.kt:118-121` | Password validation only checks length (6+ chars), no strength requirements | ✅ FIXED 2026-01-28 (8+ chars, uppercase, digit required) |

---

## ⚪ INFO (Documentation/Cleanup)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 127 | Docs | Root | 116 documentation files need consolidation | ❌ WONTFIX (documentation housekeeping) |
| 128 | Docs | `DOCUMENT_AUDIT_TRACKER.md` | Audit not started on any files | ❌ WONTFIX (documentation housekeeping) |
| 129 | Docs | `NON_IMPLEMENTED_COMPONENTS_AUDIT.md` | Some items may be outdated | ❌ WONTFIX (documentation housekeeping) |
| 130 | Docs | Various WEEK_*.md files | Historical docs - consider archiving | ❌ WONTFIX (historical reference) |
| 131 | Docs | Various PHASE_*.md files | Historical docs - consider archiving | ❌ WONTFIX (historical reference) |
| 132 | Docs | `README.old.md` | Old README - should be deleted | ❌ WONTFIX (historical reference) |
| 133 | Docs | Multiple SESSION_SUMMARY files | Historical - consider archiving | ❌ WONTFIX (historical reference) |
| 134 | Docs | `GAMES_UI_INVESTIGATION.md` | May be resolved - verify | ❌ WONTFIX (historical reference) |
| 135 | Docs | `BUILD_ISSUES.md` | May be resolved - verify | ❌ WONTFIX (historical reference) |
| 136 | Docs | `BUILD_STATUS.md` | May be outdated - verify | ❌ WONTFIX (historical reference) |
| 137 | Project | Multiple | Many .md files could be archived to `docs/archive/` | ❌ WONTFIX (documentation housekeeping) |
| 138 | Android | `FeatureFlagManager.kt:226,246` | fallback/disabled params have empty defaults - OK | ❌ WONTFIX (intentional defaults) |
| 139 | Android | `MemoryLeakPrevention.kt:102` | cleanup param has empty default - OK | ❌ WONTFIX (intentional default) |
| 140 | Android | `AnimatedComponents.kt:203` | pressClickable with empty onClick - preview | ❌ WONTFIX (preview composable) |
| 141 | Config | `.env.example` | Production URLs documented for GCP and Hostinger | ✅ FIXED 2026-01-25 |
| 142 | Config | `application-prod.properties` | CORS configured with env var override for Hostinger | ✅ FIXED 2026-01-25 |
| 143 | Infra | `docker-compose.yml` | Docker Compose with PostgreSQL, Backend, Nginx | ✅ FIXED 2026-01-25 |
| 144 | Infra | `SSL_SETUP.md` | Hostinger/GCP deployment partially documented (SSL, Nginx, systemd) | 🔄 PARTIAL |
| 145 | Test | Web | WebSocketService.test.ts has many hardcoded localhost | ❌ WONTFIX (tests should use predictable URLs) |
| 146 | Android | `AppConfig.kt:147` | Skeleton loading placeholder widths comment | ❌ WONTFIX (descriptive comment) |
| 147 | Android | Various | Multiple files have "In production" comments | ❌ WONTFIX (helpful code comments) |
| 148 | Android | `sync/README.md` | Large README could be split | ❌ WONTFIX (documentation housekeeping) |
| 149 | Docs | `TrainvocClient/CLAUDE.md` | Session notes getting long - consider archiving | ❌ WONTFIX (documentation housekeeping) |
| 150 | Docs | Root `CLAUDE.md` | May have outdated session info | ❌ WONTFIX (documentation housekeeping) |

---

## Recently Fixed

| # | Component | Description | Fixed Date | Fixed By |
|---|-----------|-------------|------------|----------|
| 221 | Android | Clean-checkout build unblocked: `google-services` plugin declared `apply false` + applied conditionally on `app/google-services.json` presence; added `google-services.json.sample`. Verified `:app:help` config resolves BUILD SUCCESSFUL with the Firebase file absent and present. | 2026-06-05 | Claude (exec/p0) |
| 168 | Android | Story Mode renamed to honest "Learning Path" (EN) / "Öğrenme Yolu" (TR) + subtitle, dropping the false "learn through stories" promise (no narrative content exists). Added pure-JVM `StoryModeRenameTest` regression guard. | 2026-06-05 | Claude (exec/p0) |
| 001 | Web | WebSocket URL now uses VITE_WS_URL or derives from VITE_API_URL | 2026-01-25 | Claude |
| 002 | Web | Contact form now opens email client with mailto: link | 2026-01-25 | Claude |
| 003 | Web | API URL now warns in dev, uses origin in prod if env not set | 2026-01-25 | Claude |
| 013 | Web | QuestionCard audio buttons now use i18n translations | 2026-01-25 | Claude |
| 014 | Web | ErrorBoundary buttons now use i18n translations | 2026-01-25 | Claude |
| 015 | Web | PictureMatchGame.tsx now uses localized alt text for images | 2026-01-25 | Claude |
| 037 | Web | ProfilePage.tsx now uses i18n for all text (was hardcoded Turkish) | 2026-01-25 | Claude |
| 004 | Android | Password hashing now uses SHA-256 instead of hashCode() | 2026-01-25 | Claude |
| 005 | Android | Play Games achievements gracefully skipped when not configured | 2026-01-25 | Claude |
| 006-007 | Android | Cloud backup UI updated to show "Coming Soon" honestly | 2026-01-25 | Claude |
| 016-017 | Android | DictionaryScreen: Fixed hardcoded color, increased touch targets to 48dp | 2026-01-25 | Claude |
| 018 | Android | HomeScreen now shows actual username from SharedPreferences | 2026-01-25 | Claude |
| 019 | Android | FAQ backup description updated to mention local backup | 2026-01-25 | Claude |
| 021 | Android | Leaderboard mock users removed, shows "Coming Soon" message | 2026-01-25 | Claude |
| 023 | Android | ImageService now uses Lorem Picsum for reliable placeholder images | 2026-01-25 | Claude |
| 027 | Android | ProfileScreen accuracy now shows real mastery rate from data | 2026-01-25 | Claude |
| 028-033 | Android | Hardcoded strings replaced with stringResource for i18n | 2026-01-25 | Claude |
| 038 | Web | InstallPrompt.tsx close button now has localized aria-label | 2026-01-25 | Claude |
| 040 | Web | CreateRoomPage.tsx now uses feature.title as React key | 2026-01-25 | Claude |
| 041 | Web | FeaturesGrid.tsx now uses feature.title.en as React key | 2026-01-25 | Claude |
| 093 | Web | Podium.tsx player names now have responsive max-width | 2026-01-25 | Claude |
| 039 | Web | All 7 game components (PictureMatch, MultipleChoice, FillInBlank, ContextClues, SpellingChallenge, WordScramble, ListeningQuiz) now use proper `resetGame()` state reset instead of `window.location.reload()` | 2026-01-25 | Claude |
| 010 | Backend | SyncController.getSyncStatus() now returns actual counts and last sync timestamps from database via SyncService.getSyncStatus() | 2026-01-25 | Claude |
| 020 | Android | AlphabetFastScroll now has semantics contentDescription for accessibility | 2026-01-25 | Claude |
| 034-035 | Android | Buttons.kt and ModernComponents.kt now use iconContentDescription parameter | 2026-01-25 | Claude |
| 042-044 | Android | ModernComponents.kt hardcoded colors replaced with theme-aware colors | 2026-01-25 | Claude |
| 045 | Android | DictionaryScreen chip Color.White replaced with onPrimary | 2026-01-25 | Claude |
| 062, 076 | Android | AlphabetFastScroll width increased to 40.dp for better touch targets | 2026-01-25 | Claude |
| 008-009 | Android | Tutorial system implemented with step-by-step dialogs, first-play detection, and game-specific content | 2026-01-26 | Claude |
| 159 | Android | HomeScreen duplicate header merged - AppTopBar hidden on HOME, unified HomeHeader with app branding + user info + menu | 2026-01-26 | Claude |
| 166 | Android | Edit Profile dialog enhanced with inline avatar picker and improved UX | 2026-01-26 | Claude |
| 167 | Android | Splash screen duration reduced from 3s to 1.2s for returning users | 2026-01-26 | Claude |
| 053 | Android | QuizScreen now shows error state with timeout if questions fail to load | 2026-01-26 | Claude |
| 066 | Android | DailyGoalsScreen now shows real study time and reviews from gamification data | 2026-01-26 | Claude |
| 067 | Android | WordProgressScreen shows real learning summary instead of hardcoded timeline | 2026-01-26 | Claude |
| 061 | Android | HomeScreen background image contentDescription set to null (decorative) | 2026-01-26 | Claude |
| 071 | Android | SettingsScreen Leaderboard now navigates to LeaderboardScreen instead of toast | 2026-01-26 | Claude |
| 090-091 | Android | CloudBackupScreen uses specific contentDescriptions for Back and Refresh | 2026-01-26 | Claude |
| 092 | Android | Removed outdated "Phase 7 Complete" comment from WordDetailScreen | 2026-01-26 | Claude |
| 099 | Android | Updated theme_coming_soon to theme_changed (theme feature works) | 2026-01-26 | Claude |
| 102 | Android | ProfileScreen Back button uses specific contentDescription | 2026-01-26 | Claude |
| 059 | Android | CloudBackupScreen uses consistent bullet formatting instead of emoji | 2026-01-26 | Claude |
| 100 | Android | CloudBackupScreen uses generic "Secure cloud backup" instead of Google Drive | 2026-01-26 | Claude |
| 046 | Android | SplashScreen Lottie animation now responsive for tablets (80dp-160dp) | 2026-01-26 | Claude |
| 050 | Android | CloudBackupScreen CloudOff icon now responsive for tablets (64dp-96dp) | 2026-01-26 | Claude |
| 051 | Android | DictionaryScreen now shows error state with retry when words fail to load | 2026-01-26 | Claude |
| 056 | Android | ProfileScreen stats now show shimmer skeleton while loading | 2026-01-26 | Claude |
| 047 | Android | DictionaryScreen letter preview now responsive for tablets (60dp-100dp) | 2026-01-26 | Claude |
| 064 | Android | SplashScreen already uses responsive sizing (minOf for landscape) | 2026-01-26 | Claude |
| 065 | Android | ProfileScreen now has responsive horizontal padding for ultra-wide displays | 2026-01-26 | Claude |
| 063 | Android | QuizScreen now has responsive horizontal padding for tablets | 2026-01-26 | Claude |
| 052 | Android | CloudBackupScreen now shows error state with retry button when operations fail | 2026-01-26 | Claude |

---

## Issue Categories Summary

| Category | Count | Description |
|----------|-------|-------------|
| **Web UI/UX** | 15 | React frontend issues |
| **Android UI/UX** | 58 | Mobile app UI issues |
| **Security** | 2 | Password hashing, auth |
| **Placeholder/Stub** | 12 | Non-functional code |
| **Hardcoded Values** | 25 | Strings, colors, URLs |
| **Accessibility** | 15 | WCAG, contentDescription |
| **i18n/l10n** | 8 | Localization issues |
| **Documentation** | 24 | Docs needing update |
| **Infrastructure** | 5 | Deployment, config |

---

## Next Fix Session Priority

When starting a fix session, prioritize in this order:

### PHASE 1 - CRITICAL UX REDESIGN (App Core Experience)
1. **#172** - HomeScreen has 6+ quick action buttons - redesign with hierarchy
2. **#168** - Story Mode has no actual story - needs content/chapter structure
3. **#170** - Quiz streaks don't persist between sessions
4. **#169** - Flip Cards too small on 6x6 grid - card size/popup fix

### PHASE 2 - AUTHENTICATION & IDENTITY
5. **#173** - Auth state uses SharedPreferences instead of AuthViewModel
6. **#192** - Add Google Sign-In to LoginScreen
7. **#191** - Add email verification UI screen
8. **#174** - HomeScreen shows letter instead of avatar emoji

### PHASE 3 - ENGAGEMENT & GAMIFICATION
9. **#179** - Add adaptive difficulty to quizzes
10. **#180** - Add sound effects to quiz (correct/wrong/milestone)
11. **#181** - Add hint system to quizzes
12. **#189** - Implement friend system

### PHASE 4 - NAVIGATION & LAYOUT
13. **#185** - Restore 5-item bottom bar with center play button
14. **#186** - Add welcome message to HomeScreen
15. **#187** - Add "continue where you left off" feature
16. **#195-196** - Add user profile section to drawer and settings

### PHASE 5 - SOCIAL & ANALYTICS
17. **#190** - Implement feedback system
18. **#194** - Make Leaderboard functional
19. **#205-206** - Integrate Google Analytics and Firestore

### PHASE 6 - POLISH
20. **#182-184** - Fix Flip Cards font/popup/zoom issues
21. **#210** - Add logout confirmation dialog
22. **#188** - Fix Profile vs Stats quizzes inconsistency

### Legacy Issues (all now FIXED or WONTFIX)
- All previous issues resolved in Jan 2026 sessions

---

## Deployment Log

| # | Date | Version | Track | Status | Notes |
|---|------|---------|-------|--------|-------|
| D01 | 2025-11-19 | 1.1.2 (versionCode 12) | Closed testing - Alpha | ✅ Released | WorkManager Configuration Fix |
| D02 | 2026-02-01 | 1.2.0 (versionCode 13) | Closed testing - Alpha | 🔄 Submitted | Major UI redesign, i18n, theme improvements |

### Play Store Status
- **Production access**: ❌ DENIED (2026-01-25) — Google requires more testing before production access
- **Requirements for production**: 20+ testers active for 14+ consecutive days
- **Current track**: Closed testing - Alpha
- **Keystore**: `D:\Kişisel\Bitirme\Private\password.jks` (alias: `key0`)

### Pending Play Store Tasks

| # | Description | Status |
|---|-------------|--------|
| 219 | Recruit 20+ closed testing testers and keep active 14 days | ⬜ OPEN |
| 220 | Re-apply for production access after testing requirement met | ⬜ OPEN |

---

*This document is the single source of truth for all project issues. Update it whenever you find or fix an issue.*
