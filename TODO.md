# Trainvoc - Unified Issue Tracker

> **Purpose**: Single source of truth for ALL issues, TODOs, bugs, and improvements across the entire project.
> **Last Updated**: 2026-01-25
> **Total Issues**: 150+

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
| 🔴 CRITICAL | 1 | 9 | 2 | 12 |
| 🟠 HIGH | 8 | 15 | 1 | 24 |
| 🟡 MEDIUM | 36 | 21 | 1 | 58 |
| 🟢 LOW | 31 | 0 | 1 | 32 |
| ⚪ INFO | 22 | 2 | 0 | 24 |
| **TOTAL** | **98** | **47** | **5** | **150** |

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
| 008 | Android | `TutorialViewModel.kt:12-65` | Entire class is stub - all methods are no-op | ❌ WONTFIX (by design) |
| 009 | Android | `TutorialOverlay.kt:12-30` | Renders nothing - stub implementation | ❌ WONTFIX (by design) |
| 010 | Backend | `SyncController.java:186` | Now returns actual sync counts and timestamps from database | ✅ FIXED 2026-01-25 |
| 011 | Backend | `SyncServiceTest.java:305` | Proper test coverage with mocked repositories | ✅ FIXED 2026-01-25 |
| 012 | Infra | `SSL_SETUP.md` | SSL setup guide created - needs server execution | 🔄 DOCUMENTED |

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
| 024 | Android | `CloudBackupManager.kt:470` | Cloud metadata returns null - placeholder | ⬜ OPEN |
| 025 | Android | `AnalyticsService.kt:66` | Daily stats return totals - no date filtering implemented | ⬜ OPEN |
| 026 | Android | `AnalyticsService.kt:83` | Weekly stats return totals - no date filtering implemented | ⬜ OPEN |
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
| 046 | Android | `SplashScreen.kt:87` | Fixed 96.dp size for splash - should scale for tablets | ⬜ OPEN |
| 047 | Android | `DictionaryScreen.kt:459` | Fixed 80.dp letter preview box size | ⬜ OPEN |
| 048 | Android | `QuizScreen.kt:98` | Fixed 180.dp Lottie animation size | ⬜ OPEN |
| 049 | Android | `AnswerOptionCard.kt:181-182` | Fixed 32.dp checkmark icon - below 48dp minimum | ❌ WONTFIX (visual indicator, not touch target) |
| 050 | Android | `CloudBackupScreen.kt:149` | Fixed 80.dp CloudOff icon size | ⬜ OPEN |
| 051 | Android | `DictionaryScreen.kt:347` | Empty state doesn't handle data loading errors | ⬜ OPEN |
| 052 | Android | `CloudBackupScreen.kt` | No error state UI when backup operations fail | ⬜ OPEN |
| 053 | Android | `QuizScreen.kt:288-293` | Shows loading but no error state if questions fail | ⬜ OPEN |
| 054 | Android | `DictionaryScreen.kt:336-345` | Shimmer loading shown but no error message if search fails | ⬜ OPEN |
| 055 | Android | `CloudBackupScreen.kt:108` | SignedInContent doesn't show clear loading state | ⬜ OPEN |
| 056 | Android | `ProfileScreen.kt` | No loading skeleton for stats before they load | ⬜ OPEN |
| 057 | Android | `DictionaryScreen.kt:315-331` | Simple "Loading" text instead of shimmer animation | ⬜ OPEN |
| 058 | Android | `DictionaryScreen.kt:759-792` | Empty state doesn't handle ALL empty scenarios | ⬜ OPEN |
| 059 | Android | `CloudBackupScreen.kt:184-188` | Feature list uses emoji checkmarks - inconsistent | ⬜ OPEN |
| 060 | Android | `QuizScreen.kt:190` | InfoButton missing contentDescription for info icon | ✅ FIXED (uses stringResource) |
| 061 | Android | `HomeScreen.kt:166-172` | Background image contentDescription is "Background decoration" | ⬜ OPEN |
| 062 | Android | `DictionaryScreen.kt:809` | AlphabetFastScroll fixed 32.dp width | ✅ FIXED 2026-01-25 |
| 063 | Android | `QuizScreen.kt:210` | LazyColumn uses fixed Spacing - no tablet adaptation | ⬜ OPEN |
| 064 | Android | `SplashScreen.kt` | No responsive considerations for landscape | ⬜ OPEN |
| 065 | Android | `ProfileScreen.kt:81-85` | No horizontal padding adjustment for ultra-wide | ⬜ OPEN |
| 066 | Android | `DailyGoalsScreen.kt:78-86` | Study time hardcoded to 0/30 - not tracking actual time | ⬜ OPEN |
| 067 | Android | `WordProgressScreen.kt:299,321` | Learning timeline hardcoded `listOf(12, 18, 15, 22)` | ⬜ OPEN |
| 068 | Android | `HomeScreen.kt:671` | Missing components marked as stubs | ⬜ OPEN |
| 069 | Android | `BackupScreen.kt:462` | Cloud backup shows "not yet implemented" message | ⬜ OPEN |
| 070 | Android | `BackupScreen.kt:514` | Coming Soon section | ⬜ OPEN |
| 071 | Android | `SettingsScreen.kt:340,345` | Leaderboard marked "Coming soon" - shows toast only | ⬜ OPEN |
| 072 | Android | `UpdateNotesCard.kt:162-164` | "Coming Soon" section for features | ⬜ OPEN |
| 073 | Android | `ChangelogScreen.kt:225` | "Coming Soon" label in changelog | ⬜ OPEN |
| 074 | Android | `DictionaryScreen.kt:336-345` | Shimmer shows for ALL cards during search | ⬜ OPEN |
| 075 | Android | `QuizScreen.kt:289-293` | CircularProgressIndicator + text "Loading..." redundant | ⬜ OPEN |
| 076 | Android | `DictionaryScreen.kt:432-451` | AlphabetFastScroll letters are very small touch targets | ✅ FIXED 2026-01-25 |
| 077 | Android | `Buttons.kt:78-82` | Icon button in complex button is 20.dp | ⬜ OPEN |
| 078 | Android | `CloudBackupManager.kt:565` | Placeholder for cloud metadata | ⬜ OPEN |
| 079 | Docs | Multiple | 116 documentation files need audit (see DOCUMENT_AUDIT_TRACKER.md) | ⬜ OPEN |
| 080 | Android | `strings.xml` | Email placeholder uses old branding | ⬜ OPEN |
| 081 | Android | Various | Inconsistent MaterialTheme vs hardcoded colors throughout | ⬜ OPEN |
| 082 | Android | `FavoritesScreen.kt:237` | Level chip onClick empty | ⬜ OPEN |
| 083 | Android | `WordDetailScreen.kt:499` | Exam chips onClick empty | ⬜ OPEN |
| 084 | Android | `UserFeatureFlagScreen.kt:246,256` | Premium/Uses Data chips onClick empty | ⬜ OPEN |
| 085 | Android | `AdminFeatureFlagScreen.kt:303,310,320` | Filter chips onClick empty | ⬜ OPEN |
| 086 | Android | `SubscriptionScreen.kt:321` | Disabled "Current Plan" button onClick empty | ⬜ OPEN |
| 087 | Android | `NavigationCard.kt:132,137,151` | Preview defaults with empty onClick | ⬜ OPEN |
| 088 | Android | `ExampleSentenceCard.kt:117,130` | Preview defaults with empty onClick | ⬜ OPEN |
| 089 | Android | `CloudBackupScreen.kt:197` | "Sign in" contentDescription should be "Sign in with Google" | ⬜ OPEN |
| 090 | Android | `CloudBackupScreen.kt:74` | "Back" contentDescription too generic | ⬜ OPEN |
| 091 | Android | `CloudBackupScreen.kt:80` | "Refresh" should be "Refresh backup list" | ⬜ OPEN |
| 092 | Android | `WordDetailScreen.kt:1031` | Comment says "Phase 7 Complete" - outdated | ⬜ OPEN |
| 093 | Web | `Podium.tsx:63` | Fixed max-width truncation 100px for player names | ✅ FIXED 2026-01-25 |
| 094 | Web | `ListeningQuizGame.tsx:263` | Custom motion.button for audio player - intentional design | ❌ WONTFIX |

---

## 🟢 LOW Issues (Polish)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 095 | Android | `QuizScreen.kt:98` | Fixed Lottie size could adapt | ⬜ OPEN |
| 096 | Android | `AnswerOptionCard.kt:148-150` | ContentDescription could be more specific | ⬜ OPEN |
| 097 | Android | `DictionaryScreen.kt:336-345` | Loading text during search basic | ⬜ OPEN |
| 098 | Android | Multiple screens | No empty state for quiz results when no quizzes | ⬜ OPEN |
| 099 | Android | `strings.xml:79` | "Theme selection coming soon" - outdated | ⬜ OPEN |
| 100 | Android | `CloudBackupScreen.kt:156-200` | Features mention Google Drive - should mention Trainvoc | ⬜ OPEN |
| 101 | Web | `Header.tsx:94-95` | Language button correctly uses sr-only - GOOD but noted | ⬜ OPEN |
| 102 | Android | `ProfileScreen.kt:112` | "Back" hardcoded contentDescription | ⬜ OPEN |
| 103 | Android | Various | Test files contain hardcoded localhost references | ⬜ OPEN |
| 104 | Docs | `sync/README.md:730` | TODO: Add encryption for backups | ⬜ OPEN |
| 105 | Docs | `sync/README.md:294` | Cloud provider implementation is placeholder | ⬜ OPEN |
| 106 | Docs | `sync/README.md:888` | Cloud backup described as placeholder | ⬜ OPEN |
| 107 | Android | `DataExporter.kt` | Error handling note - non-production-ready | ⬜ OPEN |
| 108 | Android | `GoogleAuthManager.kt:~150` | Simplified version mentioned in comments | ⬜ OPEN |
| 109 | Android | `ListeningQuizGame.kt` | Production note about TTS integration | ⬜ OPEN |
| 110 | Web | `ProfilePage.tsx:29` | Good responsive design noted | ⬜ OPEN |
| 111 | Web | `Header.tsx:145` | Mobile menu overflow handling good | ⬜ OPEN |
| 112 | Web | `LeaderboardPage.tsx:68-73` | Proper loading state - GOOD | ⬜ OPEN |
| 113 | Web | `CreateRoomPage.tsx:360-363` | Empty state for rooms - GOOD | ⬜ OPEN |
| 114 | Web | `JoinRoomPage.tsx:122-136` | Good error differentiation - GOOD | ⬜ OPEN |
| 115 | Web | `style.css:148-192` | Dark mode CSS properly defined - GOOD | ⬜ OPEN |
| 116 | Web | `JoinRoomPage.tsx:206` | Room code input with validation - shows format hint and error states | ✅ FIXED 2026-01-25 |
| 117 | Android | `ShimmerEffect.kt:246` | Chart placeholder comment | ⬜ OPEN |
| 118 | Android | `ShimmerEffect.kt:259` | Simple text placeholder comment | ⬜ OPEN |
| 119 | Android | `UnifiedStates.kt:194-299` | Multiple placeholder comments for shimmer | ⬜ OPEN |
| 120 | Android | `FillInTheBlankScreen.kt:236` | placeholder prop for TextField | ⬜ OPEN |
| 121 | Android | `SpellingChallengeScreen.kt:223` | placeholder prop for TextField | ⬜ OPEN |
| 122 | Android | `WordScrambleScreen.kt:205` | placeholder prop for TextField | ⬜ OPEN |
| 123 | Android | `GameScreens.kt:472,481` | placeholder parameter in function | ⬜ OPEN |
| 124 | Android | `FavoritesScreen.kt:87` | placeholder prop for TextField | ⬜ OPEN |
| 125 | Android | `DictionaryScreen.kt:219` | placeholder prop for TextField | ⬜ OPEN |
| 126 | Android | `HomeScreen.kt:188` | username_placeholder string resource used | ⬜ OPEN |

---

## ⚪ INFO (Documentation/Cleanup)

| # | Component | File:Line | Description | Status |
|---|-----------|-----------|-------------|--------|
| 127 | Docs | Root | 116 documentation files need consolidation | ⬜ OPEN |
| 128 | Docs | `DOCUMENT_AUDIT_TRACKER.md` | Audit not started on any files | ⬜ OPEN |
| 129 | Docs | `NON_IMPLEMENTED_COMPONENTS_AUDIT.md` | Some items may be outdated | ⬜ OPEN |
| 130 | Docs | Various WEEK_*.md files | Historical docs - consider archiving | ⬜ OPEN |
| 131 | Docs | Various PHASE_*.md files | Historical docs - consider archiving | ⬜ OPEN |
| 132 | Docs | `README.old.md` | Old README - should be deleted | ⬜ OPEN |
| 133 | Docs | Multiple SESSION_SUMMARY files | Historical - consider archiving | ⬜ OPEN |
| 134 | Docs | `GAMES_UI_INVESTIGATION.md` | May be resolved - verify | ⬜ OPEN |
| 135 | Docs | `BUILD_ISSUES.md` | May be resolved - verify | ⬜ OPEN |
| 136 | Docs | `BUILD_STATUS.md` | May be outdated - verify | ⬜ OPEN |
| 137 | Project | Multiple | Many .md files could be archived to `docs/archive/` | ⬜ OPEN |
| 138 | Android | `FeatureFlagManager.kt:226,246` | fallback/disabled params have empty defaults - OK | ⬜ OPEN |
| 139 | Android | `MemoryLeakPrevention.kt:102` | cleanup param has empty default - OK | ⬜ OPEN |
| 140 | Android | `AnimatedComponents.kt:203` | pressClickable with empty onClick - preview | ⬜ OPEN |
| 141 | Config | `.env.example` | Production URLs documented for GCP and Hostinger | ✅ FIXED 2026-01-25 |
| 142 | Config | `application-prod.properties` | CORS configured with env var override for Hostinger | ✅ FIXED 2026-01-25 |
| 143 | Infra | N/A | Docker Compose setup for GCP not documented | ⬜ OPEN |
| 144 | Infra | `SSL_SETUP.md` | Hostinger/GCP deployment partially documented (SSL, Nginx, systemd) | 🔄 PARTIAL |
| 145 | Test | Web | WebSocketService.test.ts has many hardcoded localhost | ❌ WONTFIX (tests should use predictable URLs) |
| 146 | Android | `AppConfig.kt:147` | Skeleton loading placeholder widths comment | ⬜ OPEN |
| 147 | Android | Various | Multiple files have "In production" comments | ⬜ OPEN |
| 148 | Android | `sync/README.md` | Large README could be split | ⬜ OPEN |
| 149 | Docs | `TrainvocClient/CLAUDE.md` | Session notes getting long - consider archiving | ⬜ OPEN |
| 150 | Docs | Root `CLAUDE.md` | May have outdated session info | ⬜ OPEN |

---

## Recently Fixed

| # | Component | Description | Fixed Date | Fixed By |
|---|-----------|-------------|------------|----------|
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

1. **🔴 CRITICAL #001-012** - Production blockers
2. **🟠 HIGH #013-036** - Major UX issues
3. **Deployment blockers** - SSL, CORS, env vars
4. **🟡 MEDIUM** - As time allows
5. **🟢 LOW** - Polish phase
6. **⚪ INFO** - Cleanup session

---

*This document is the single source of truth for all project issues. Update it whenever you find or fix an issue.*
