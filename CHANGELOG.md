# Changelog

All notable changes to the Trainvoc project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.3.2] - 2026-07-15

> First release carrying Room v19 (FSRS `review_schedule` + additive 18→19
> migration, flag-off). Debug-signed like 1.3.x so far; the 18→19 upgrade is
> Robolectric-verified (no production install base exists yet).

### 2026-07-15 (evening wave 2 — FSRS persistence + docs + data-source research)

#### Added — TrainvocClient (#99 S1, #116)
- FSRS spaced-repetition state is now persisted: Room v19 adds
  `review_schedule` (composite PK word_id+user_id, FK->words.id CASCADE,
  full FsrsCard state + sync flag) with `ReviewScheduleDao`; MIGRATION_18_19
  is additive and seeds from legacy SM-2 history via `Sm2ToFsrsMigrator`
  (lazy: only ever-reviewed words). The prepopulated asset and seed manifest
  stay at v18 (fresh installs migrate 18->19 on first open — proven by a
  Robolectric fresh-install test), so `tools/dictgen` outputs and the
  backend importer are untouched. `srs_engine_enabled` stays OFF; Review
  Queue UI (S2) + quiz hook (S3) need a device and remain open on #99.
  Client suite 255 -> 265 green.

#### Changed — repo/docs (#100, #115)
- Root docs consolidated to the GitHub-native layout: tracking docs stubbed,
  six historical analyses moved to `docs/history/`, root CLAUDE.md's issue
  workflow rewritten (Issues/Milestones/Board #7), stale version claims
  corrected across README/CLAUDE.md files.

#### Research — dictionary data (#97, #98)
- License-verified source studies posted to the issues: kaikki.org
  (CC BY-SA 4.0) is the practical AR and EN/TR enrichment source with a
  separate-pack posture + drop-in attribution text; Leipzig Corpora (CC BY)
  for frequency ranks; WikDict has no EN-AR pair; KELLY/SUBTLEX are
  non-commercial and unusable.

### 2026-07-15 (post-1.3.1 — backend v18 mirror + dependency modernization)

#### Added — TrainvocBackend (#96 COMPLETE: PR-B #108 + PR-C #114; deployability #113)
- **#96 PR-C (#114):** primary-DB user tables re-keyed to the permanent numeric
  v18 word ids (`user_word_progress`, `user_word_statistics`, `srs_schedule`
  String→BIGINT); SRS wire contract is numeric with `[1, 999,999]` bounds
  (per-device custom-word ids ≥1M rejected — cross-device custom-word sync
  deferred) plus optional `lemma`/`languageCode` natural-key hints; SyncService
  reports legacy non-numeric payloads as per-item failures. Suite 248 green.
- **Deployability (#113):** prod profile env names aligned to compose
  (`DB_PRIMARY_*`/`DB_SECONDARY_*`, fail-fast passwords, `jdbc-url` keys);
  backend image builds from the monorepo root so the Docker-built jar embeds
  `seed_v18.json` (verified inside the image); root `.dockerignore` allowlist.

#### Added — TrainvocBackend (#96 PR-B, #108)
- Words DB now mirrors the client's relational multilingual v18 schema:
  `languages` / `words` (permanent numeric ids) / `word_translations` /
  `synonyms` / id-keyed `word_exam_cross_ref`, in a dedicated `words.model`
  entity package per persistence unit (removes the dual-EMF ghost-table
  hazard). Seeded at boot by `WordSeedImporter` from the client's
  `seed_v18.json` (single source of truth, idempotent); the old ~12k-line
  INSERT dump was replaced by a DDL-only script with real foreign keys.

#### Changed — dependencies
- Spring Boot 4.0.6 → **4.1.0** (Hibernate ORM 7.4.1; new fetch-join
  `getSingleResult` regression guard) (#110); backend patch/minors:
  postgresql 42.7.13, firebase-admin 9.10.0, caffeine 3.2.4,
  org.json 20260522, Gradle wrapper **9.6.1** (#109).
- TypeScript 5.9.3 → **6.0.3** (deprecated `baseUrl` removed) +
  react-i18next **16.6.6** (TS6 peer range) (#111); lucide-react 0.563 →
  **1.24.0** (Footer brand icons → inline SVGs + aria-labels; deprecated
  icon names aliased in imports) (#112); web patch/minors: radix-ui ×5,
  @types/node, @types/react, sharp (#107).
- All 16 open Dependabot PRs resolved (batched or superseded); **0 open
  Dependabot alerts**.

## [1.3.1] - 2026-07-15

#### Fixed — TrainvocClient (P0: CI-built APKs crashed at startup, #103)
- The published v1.3.0 APK crashed on every launch: CI builds have no
  `google-services.json` (gitignored), so no default `FirebaseApp` exists,
  and `FirebaseAuthRepository` called `FirebaseAuth.getInstance()` in a
  field initializer during Hilt graph creation. The handle is now lazy +
  guarded; without Firebase config every auth operation degrades gracefully
  (null user / error result) instead of crashing. Regression-covered by
  `FirebaseAuthRepositoryNoFirebaseTest` (9 Robolectric tests); suite now
  255 green. (#104)

#### Fixed — TrainvocWeb (security)
- Bumped transitive `form-data` 4.0.5 → 4.0.6 (CVE-2026-12143, HIGH
  Dependabot alert — CRLF injection); `npm audit` back to 0. (#102)

#### Changed — CI/CD
- `release.yml` can restore `google-services.json` from the optional
  `GOOGLE_SERVICES_JSON_BASE64` secret so released APKs ship with Firebase
  sign-in active. (#105)
- Backend test suite fixed to fully green (was 20 pre-existing failures:
  RoomServiceTest/GameControllerTest/QuizControllerTest) and made
  **blocking** in CI. (#106)

## [1.3.0] - 2026-07-15

> ⚠️ The published v1.3.0 APK crashes at startup (#103) — superseded by 1.3.1.

### 2026-07-15 (branch `claude/trainvoc-login-database-l0covn`)

#### Fixed — TrainvocClient (onboarding bug: name asked on every launch)
- **Root cause**: onboarding saved the username via `PreferencesRepository`
  into the encrypted `secure_user_prefs` file, but `SplashScreen` decided
  returning-vs-new user from the plain `user_prefs` file (always empty), so
  every launch showed the Welcome/Username flow. Avatar had the same split.
- All username/avatar reads and writes now go through `PreferencesRepository`
  (`SplashScreen`, `HomeScreen`, `AppNavigationDrawer`, `SettingsScreen`,
  `ProfileScreen`, `DataImporter/Exporter`); `LegacyPrefsMigrator` moves
  values from the old plain files once, so existing users are recognized
  after updating. Covered by `LegacyPrefsMigratorTest`.

#### Changed — TrainvocClient (relational multilingual dictionary, Room v18)
- Word primary key is now a permanent **numeric id**; every word in every
  language is a first-class row (`languages`: en=1, tr=2 — 5,466 EN +
  5,074 TR words). Meanings are sense-grouped M:N `word_translations`
  edges ("take" → sense 0 almak, sense 1 götürmek…); `synonyms` are
  id-based same-language pairs; `word_exam_cross_ref`, `word_of_day`,
  `quiz_question_results` re-keyed to `words.id` with real FKs.
- `MIGRATION_17_18` rebuilds from the bundled seed manifest and carries all
  user progress (SM-2, favorites, stats, time) over by lemma; user-added
  custom words survive with ids ≥ 1,000,000 and their packed meanings are
  unpacked relationally (`MeaningParser`).
- New `tools/dictgen` generator (Python, stdlib): parses the legacy packed
  glosses, emits `seed_v18.json` + the prepopulated DB from Room's exported
  DDL; `ids.lock.json` pins word ids forever. Adding a language (Arabic…)
  is now data-only.
- Word detail screen shows numbered senses with translation chips and
  relational synonyms; Settings gains a **Learning Language** section
  (EN→TR active; Arabic/German/Portuguese/Spanish/French honestly
  "coming soon").

#### CI / repo
- `ci.yml` actually runs now (triggers were `[main, develop]` but the
  default branch is `master`); JDK 21 everywhere; backend tests blocking;
  Android/web test steps added. New PR/issue templates + CODEOWNERS.
- New `release.yml`: pushing a `v*` tag builds the APK and publishes a
  GitHub Release (signed when keystore secrets are configured; debug-signed
  fallback until then).

### 2026-06-05 (branch `dev/2026-06-05`)

#### Added — TrainvocClient (auth + leaderboard)
- **Google Sign-In** (#192): `FirebaseAuthRepository.signInWithGoogle(idToken)` → `AuthRepository.loginWithGoogle` → `AuthViewModel.loginWithGoogle`; "Sign in with Google" button on `LoginScreen` (GoogleSignIn + ActivityResultLauncher; clean-checkout-safe web-client-id lookup).
- **Email-verification UI** (#191): `EmailVerificationBanner` composable (resend action) wired into `ProfileScreen`; `AuthViewModel.sendEmailVerification()/emailVerificationSent`.
- **Session-timeout handling** (#193): `AuthRepository.validateSession()` (token force-refresh → sign-out on failure) + `SessionExpiredHandler` dialog routing to login.
- **Functional leaderboard** (#194): local "Your Progress" panel (streaks, active days, achievements) via new `LeaderboardViewModel`; global board kept honestly "coming soon". Removed dead `LeaderboardItem`.
- New `AuthViewModelTest` (6 tests) + new `MainDispatcherRule` test utility. EN + TR strings for all new UI.

#### Fixed — TrainvocClient (test infra)
- **Unit-test source set compiles + runs again** (#222): declared `mockito-kotlin 5.4.0` (alongside MockK) and reconciled ~9 drifted test files with current models (`WordLevel` enum, `Statistic`, `QuizType` in `classes.enums`, `DriveBackup*`, `AchievementProgress`, `RoomSettings`/`PlayerRanking`/`RoomListItem`, updated ViewModel ctors). Fixed the pre-existing `TestDispatcherProvider` "different schedulers" defect and added `testOptions.unitTests.isReturnDefaultValues=true`. `:app:testDebugUnitTest` went from 0 (wouldn't compile) → 178 tests, 148 passing. Remaining 30 runtime failures tracked as #223.

#### Fixed — TrainvocWeb (security)
- **All security alerts resolved; `npm audit` = 0** (supersedes PR #32): pinned `serialize-javascript >=7.0.4` via overrides + `npm audit fix` (ajv, vite, vitest, ws — all non-breaking). Merged safe Dependabot patch bumps (axios, i18next, typescript, @types/react-dom, tailwindcss, @tailwindcss/postcss). Held breaking majors (vite 8, @vitejs/plugin-react 6). Verified: `tsc --noEmit`, `npm run build`, `npm run test:run` (118/118) all green.

#### Changed — TrainvocBackend (dependencies)
- Merged safe non-major Dependabot bumps: postgresql 42.7.10, org.json 20251224, jjwt 0.13.0 (verified API-compatible), firebase-admin 9.8.0, caffeine 3.2.3. Held breaking majors (Spring Boot 4, springdoc 3, gradle 9). Backend build not locally verifiable (host lacks JDK 24 toolchain) — CI must confirm.

#### Docs
- Rewrote `ROADMAP.md` into a long, phased, production-grade plan (Phase 0 ship → backend → web → iOS → growth → engineering excellence), folding in `MASTER_FIX_PLAN`.
- Added a "Future / Professionalization" section to `README.md`.
- Corrected stale claims in root + client `CLAUDE.md` (games/TTS present; build/tests fixed).

### Added

#### TrainvocBackend
- **State Pattern Implementation** - Game state management now uses State pattern
  - `GameStateHandler.java` - Interface for state handlers
  - `LobbyStateHandler.java` - Handles waiting/lobby state
  - `CountdownStateHandler.java` - Handles 3-second countdown
  - `QuestionStateHandler.java` - Handles active question state
  - `AnswerRevealStateHandler.java` - Handles answer reveal phase
  - `RankingStateHandler.java` - Handles score ranking display
  - `FinalStateHandler.java` - Handles game completion
  - `GameStateMachine.java` - Coordinates state transitions

- **Service Layer Separation** (SRP compliance)
  - `RoomService.java` - Room CRUD operations
  - `PlayerService.java` - Player management
  - `RoomPasswordService.java` - Password validation
  - `GameStateService.java` - Game state calculations
  - `GameConstants.java` - Centralized constants

- **WebSocket Handler Refactoring** (Strategy pattern)
  - `WebSocketMessageHandler.java` - Handler interface
  - `WebSocketContext.java` - Session management
  - `MessageDispatcher.java` - Message routing
  - `CreateRoomHandler.java` - Room creation handler
  - `JoinRoomHandler.java` - Room join handler

- **Service Interfaces** (DIP compliance)
  - `IRoomService.java` - Room service interface
  - `IPlayerService.java` - Player service interface

- **Security Improvements**
  - Environment-based configuration (`application.properties`)
  - Development profile (`application-dev.properties`)
  - Rate limiting with bucket4j (`RateLimitingConfig.java`)
  - Input validation with Jakarta Bean Validation
  - Environment-based CORS configuration

- **Infrastructure**
  - Updated `Dockerfile` with health checks
  - Updated `docker-compose.yml` with proper configuration
  - GitHub Actions CI/CD workflow (`.github/workflows/ci.yml`)
  - Environment variable templates (`.env.example`)

#### TrainvocWeb
- **Service Layer**
  - `GameService.ts` - Game API operations
  - `RoomService.ts` - Room management operations
  - `LeaderboardService.ts` - Leaderboard operations
  - `services/index.ts` - Centralized exports

- **Custom Hooks**
  - `useRooms.ts` - Room list management with auto-refresh
  - `useGameState.ts` - Game state polling and local timer sync
  - `useLobby.ts` - Lobby state management
  - `usePolling.ts` - Generic polling utility hook
  - `hooks/index.ts` - Centralized exports

- **Error Handling Utilities**
  - `utils/errors.ts` - Centralized error handling
  - `AppError` class for typed errors
  - Error code constants
  - Axios error parsing utilities

- **Security Improvements**
  - Environment-based API URL configuration
  - Environment variable template (`.env.example`)

### Changed

#### TrainvocBackend
- `GameService.java` - Refactored to use injected services (facade pattern)
- `GameWebSocketHandler.java` - Simplified to use MessageDispatcher
- `RoomService.java` - Now implements `IRoomService`
- `PlayerService.java` - Now implements `IPlayerService`
- `application.properties` - Uses environment variables for sensitive data
- `CorsConfig.java` - Environment-based allowed origins

#### TrainvocWeb
- `api.ts` - Now uses `VITE_API_URL` environment variable

### Security
- Moved database credentials to environment variables
- Added rate limiting to prevent API abuse
- Added input validation on all request bodies
- Configured CORS for production environments
- Added security headers in SecurityConfig

## [0.1.0] - 2026-01-22

### Added
- Initial monorepo structure
- TrainvocBackend Spring Boot application
- TrainvocWeb React application
- TrainvocClient Android application
- Comprehensive documentation (CLAUDE.md files)
- Architecture documentation (ARCHITECTURE.md)
- Contributing guidelines (CONTRIBUTING.md)
- Investigation report (INVESTIGATION_REPORT.md)
- SE Principles analysis (SE_PRINCIPLES_ANALYSIS.md)
- Master fix plan (MASTER_FIX_PLAN.md)

---

## Summary of Completed Phases

### Phase 1: Critical Security & Blockers ✅
- Environment-based configuration
- Rate limiting implementation
- Input validation
- CORS configuration
- Infrastructure (Docker, CI/CD)

### Phase 2: SOLID Principle Fixes ✅
- **SRP**: Split GameService into focused services
- **SRP**: Split WebSocket handler into strategy-based handlers
- **OCP**: Implemented State pattern for game states
- **DIP**: Created service interfaces

### Phase 3: Design Pattern Implementation ✅
- State pattern for backend game states
- Service layer for web frontend
- Custom hooks for state management
- Error handling utilities

### Remaining Phases
- Phase 4: DRY & Code Deduplication
- Phase 5: Architecture Improvements
- Phase 6: Testing Infrastructure
- Phase 7: Performance Optimization
- Phase 8: YAGNI & Dead Code Removal
- Phase 9: KISS & Simplification
- Phase 10: Clean Code & Polish

---

*For detailed progress tracking, see [docs/history/MASTER_FIX_PLAN.md](./docs/history/MASTER_FIX_PLAN.md)*
