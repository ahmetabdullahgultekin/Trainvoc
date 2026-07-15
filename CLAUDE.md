# Trainvoc Monorepo - Claude AI Development Guide

## Project Overview

**Trainvoc** is a multi-platform vocabulary learning ecosystem designed to help users master English-Turkish vocabulary through engaging, gamified learning experiences.

| Component | Technology | Purpose |
|-----------|------------|---------|
| **TrainvocClient** | Kotlin/Android | Mobile app for vocabulary learning |
| **TrainvocWeb** | React/TypeScript | Web platform for multiplayer games |
| **TrainvocBackend** | Java/Spring Boot | REST API and WebSocket game server |

---

## Quick Start for Claude

### Understanding the Codebase

```
/home/user/Trainvoc/
├── TrainvocClient/          # Android app (Kotlin, Jetpack Compose)
│   ├── CLAUDE.md            # Detailed Android development guide
│   ├── app/src/main/java/   # Source code
│   └── docs/                # 70+ documentation files
├── TrainvocWeb/             # Web frontend (React, TypeScript, Vite)
│   ├── CLAUDE.md            # Web development guide
│   └── src/                 # React components and pages
├── TrainvocBackend/         # Server (Spring Boot 4, Java 21 LTS)
│   ├── CLAUDE.md            # Backend development guide
│   └── src/                 # Controllers, services, models
├── TODO.md                  # ⭐ UNIFIED ISSUE TRACKER - Single source of truth
├── README.md                # Public repository README
├── ARCHITECTURE.md          # System architecture documentation
├── CONTRIBUTING.md          # Contribution guidelines
├── LICENSE                  # MIT License
└── INVESTIGATION_REPORT.md  # Codebase analysis and recommendations
```

### Component-Specific Guides

Each module has its own `CLAUDE.md` with detailed information:

1. **TrainvocClient/CLAUDE.md** - Android development, 257 Kotlin files, MVVM architecture
2. **TrainvocWeb/CLAUDE.md** - React/TypeScript, 38 files, multiplayer game platform
3. **TrainvocBackend/CLAUDE.md** - Spring Boot, 39 Java files, REST/WebSocket APIs

---

## 🚨 CRITICAL: Issue Tracking Workflow

### TODO.md - Unified Issue Tracker

**Location:** `/TODO.md` (root of repository)

This is the **SINGLE SOURCE OF TRUTH** for all issues, bugs, TODOs, and improvements across the entire project.

### Claude AI Session Rules

#### When You Find an Issue:
1. **IMMEDIATELY add it to TODO.md** with the next available number
2. Use the correct severity level (🔴 CRITICAL, 🟠 HIGH, 🟡 MEDIUM, 🟢 LOW, ⚪ INFO)
3. Include: Component, File:Line, Description, Status (⬜ OPEN)
4. Do NOT fix the issue in the same session (unless explicitly requested)

#### When You Fix an Issue:
1. Mark the issue in TODO.md with ✅ FIXED and add the date
2. Add your entry to the "Recently Fixed" section
3. Update the Quick Stats counts
4. Never delete issues - keep for history

#### Issue Format:
```markdown
| #ID | Component | File:Line | Description | Status |
| 151 | Android | `SomeFile.kt:42` | Description of the issue | ⬜ OPEN |
```

#### Session Types:
- **Discovery Session**: Find issues → Add to TODO.md → Do NOT fix
- **Fix Session**: Read TODO.md → Fix issues → Mark as ✅ FIXED
- **Mixed Session**: User explicitly requests both

### Priority Order for Fixes:
1. 🔴 CRITICAL - Production blockers, security, crashes
2. 🟠 HIGH - Major features broken
3. 🟡 MEDIUM - Features work but have issues
4. 🟢 LOW - Polish and improvements
5. ⚪ INFO - Documentation and cleanup

---

## Architecture Overview

### System Diagram

```
                    ┌──────────────────────────────────────────────┐
                    │              User Devices                     │
                    │  ┌─────────────┐      ┌─────────────────┐   │
                    │  │   Android   │      │   Web Browser   │   │
                    │  │   (Client)  │      │     (Web)       │   │
                    │  └──────┬──────┘      └────────┬────────┘   │
                    └─────────┼──────────────────────┼────────────┘
                              │                      │
                              ▼                      ▼
                    ┌─────────────────────────────────────────────┐
                    │            TrainvocBackend                   │
                    │  ┌─────────────┐      ┌─────────────────┐   │
                    │  │  REST API   │      │   WebSocket     │   │
                    │  │  (CRUD)     │      │  (Multiplayer)  │   │
                    │  └──────┬──────┘      └────────┬────────┘   │
                    │         │                      │            │
                    │         ▼                      ▼            │
                    │  ┌──────────────────────────────────────┐   │
                    │  │        Spring Boot Services          │   │
                    │  └──────────────────────────────────────┘   │
                    │                    │                        │
                    └────────────────────┼────────────────────────┘
                                         │
                    ┌────────────────────┼────────────────────────┐
                    │                    ▼                        │
                    │  ┌─────────────────────────────────────┐    │
                    │  │         PostgreSQL Database          │    │
                    │  │  ┌─────────┐    ┌──────────────┐    │    │
                    │  │  │trainvoc │    │trainvoc-words│    │    │
                    │  │  │(players)│    │ (vocabulary) │    │    │
                    │  │  └─────────┘    └──────────────┘    │    │
                    │  └─────────────────────────────────────┘    │
                    └─────────────────────────────────────────────┘
```

### Data Flow

1. **TrainvocClient (Android)**
   - Local Room database for offline vocabulary learning
   - Optional sync with backend for multiplayer features
   - Google Play Games for achievements and leaderboards

2. **TrainvocWeb (React)**
   - Connects to backend via REST API and WebSocket
   - Real-time multiplayer game sessions
   - No local persistence (server-driven)

3. **TrainvocBackend (Spring Boot)**
   - Dual PostgreSQL databases (gameplay + vocabulary)
   - WebSocket for real-time game state
   - REST API for CRUD operations

---

## Tech Stack Summary

| Layer | TrainvocClient | TrainvocWeb | TrainvocBackend |
|-------|---------------|-------------|-----------------|
| **Language** | Kotlin 2.3.20 | TypeScript 5.8.3 | Java 21 (LTS) |
| **Framework** | Jetpack Compose | React 19.1.0 | Spring Boot 4.0.6 |
| **Build** | Gradle (KTS) | Vite 8.0 | Gradle |
| **Database** | Room/SQLite | N/A | PostgreSQL |
| **State** | ViewModel/StateFlow | React State | Spring Session |
| **DI** | Hilt | N/A | Spring DI |
| **Network** | Retrofit | Axios | RestTemplate |
| **Real-time** | N/A | WebSocket | Spring WebSocket |

---

## Development Commands

### TrainvocClient (Android)
```bash
cd TrainvocClient
./gradlew build          # Build the project
./gradlew test           # Run unit tests
./gradlew assembleDebug  # Create debug APK
./gradlew assembleRelease # Create release APK
```

### TrainvocWeb (React)
```bash
cd TrainvocWeb
npm install              # Install dependencies
npm run dev              # Start development server
npm run build            # Production build
npm run preview          # Preview production build
```

### TrainvocBackend (Spring Boot)
```bash
cd TrainvocBackend
./gradlew bootRun        # Start development server
./gradlew build          # Build the project
./gradlew test           # Run unit tests
```

---

## Critical Information

### Current Project Status (refreshed 2026-06-05 — see `ROADMAP.md`)

| Component | Build Status | Feature Completeness | Notes |
|-----------|-------------|---------------------|-------|
| **TrainvocClient** | Builds (clean checkout, #221 fixed) | ~85% of v1 | Games + TTS present (legacy "deleted/unwired" claims were STALE); auth + leaderboard shipped |
| **TrainvocWeb** | Builds; `npm audit` = **0 vulns** | ~80% | Multiplayer functional; security + safe dep bumps merged |
| **TrainvocBackend** | Builds on JDK 21 LTS (Spring Boot 4.0.6) | ~85% | Dual DB working; not yet deployed/hardened |

### Known Issues (corrected — prior entries here were stale)

> ⚠️ **Stale-claim corrections (2026-06-05):** the single-player games and multiplayer UI are **present and wired** (the "games UI deleted" note was false on HEAD), and **TTS is connected**. "Story Mode" was renamed to "Learning Path" so it no longer over-promises (#168). The unit-test suite now compiles and runs (#222). See `TODO.md` for the live issue list.

1. **TrainvocClient**
   - ✅ Clean-checkout build fixed (#221 — google-services plugin conditional).
   - ✅ Auth: Google Sign-In (#192), email-verify UI (#191), session timeout (#193).
   - ✅ Leaderboard: local "Your Progress" + honest "global coming soon" (#194).
   - ⚠️ Cloud/Drive backup still local-only (honestly "coming soon").
   - ⚠️ 30 pre-existing unit-test runtime failures tracked as #223 (Android-SDK/mock issues, not blockers).

2. **TrainvocWeb**
   - ✅ Security alerts resolved; `npm audit` = 0; safe Dependabot patches merged.
   - No authentication implementation (by design until backend identity exists).

3. **TrainvocBackend**
   - SSL terminated at Nginx (disabled in-app by design).
   - Auth wired (Firebase + JWT) but not yet enforced end-to-end.
   - Rate limiting present (bucket4j); DTO layer / API versioning / pagination still deferred to backend-hardening phase.
   - Never deployed — see `ROADMAP.md` Phase 3.

### Important Files to Know

**Root:**
- `README.md` - Public repository information
- `ARCHITECTURE.md` - System architecture details
- `CONTRIBUTING.md` - How to contribute
- `INVESTIGATION_REPORT.md` - Detailed analysis and recommendations

**TrainvocClient:**
- `NON_IMPLEMENTED_COMPONENTS_AUDIT.md` - 55 incomplete features
- `GAMES_UI_INVESTIGATION.md` - Deleted games recovery plan
- `RECOMMENDED_HOOKS_GUIDE.md` - Development workflow hooks

---

## Common Tasks

### Adding a New Feature

1. **Identify scope**: Which components need changes?
2. **Read component CLAUDE.md**: Understand patterns and conventions
3. **Check existing code**: Don't duplicate functionality
4. **Follow architecture**: Maintain separation of concerns
5. **Update documentation**: Keep CLAUDE.md files current

### Debugging Issues

1. **Check logs**: Each component has different logging
   - Android: Logcat (`Log.d`, `Log.e`)
   - Web: Browser DevTools console
   - Backend: Spring Boot logs

2. **Verify connectivity**:
   - Backend running on `localhost:8080`?
   - PostgreSQL databases accessible?
   - CORS configured properly?

### Making Database Changes

**TrainvocClient (Room):**
- Update entity classes
- Increment database version
- Add migration in `AppDatabase.kt`

**TrainvocBackend (JPA):**
- Update entity classes
- JPA auto-updates schema (dev mode)
- For production: use Flyway migrations

---

## Git Workflow

### Branch Naming
```
claude/<task-description>-<session-id>
feature/<feature-name>
bugfix/<issue-number>-<description>
```

### Commit Messages
```
type(scope): description

Types: feat, fix, docs, style, refactor, test, chore
Scopes: client, web, backend, root
```

### Before Committing
- [ ] Code compiles in all affected components
- [ ] No new TODOs without tracking
- [ ] Documentation updated if needed
- [ ] Tests pass (where applicable)

---

## Environment Setup

### Required Tools

| Tool | Version | Purpose |
|------|---------|---------|
| JDK | 21 (LTS) | TrainvocBackend |
| Node.js | 18+ | TrainvocWeb |
| Android Studio | Latest | TrainvocClient |
| PostgreSQL | 15+ | TrainvocBackend database |

### Database Setup

```bash
# Create databases
createdb trainvoc
createdb trainvoc-words

# Run SQL scripts
psql -d trainvoc -f TrainvocBackend/sql-queries/trainvoc-mp-db-for-postgre.sql
psql -d trainvoc-words -f TrainvocBackend/sql-queries/trainvoc-words-db-for-postgre.sql
```

### Environment Variables

**TrainvocClient:**
- Signing keys in `local.properties` (for release builds)
- Google Play Services config in `google-services.json`

**TrainvocBackend:** (see `.env.example`)
- `DB_PASSWORD` - PostgreSQL password
- `SECOND_DB_PASSWORD` - Words database password
- `SSL_KEY_STORE_PASSWORD` - SSL keystore password
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins (comma-separated)
- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod)

**TrainvocWeb:** (see `.env.example`)
- `VITE_API_URL` - Backend API URL

---

## Contact & Resources

### Documentation Priority

1. Start with this file (root `CLAUDE.md`)
2. Read component-specific `CLAUDE.md`
3. Check `INVESTIGATION_REPORT.md` for analysis
4. Review `ARCHITECTURE.md` for system design

### Key Documentation

| Document | Location | Purpose |
|----------|----------|---------|
| Root CLAUDE.md | `/CLAUDE.md` | This file - overview |
| Client CLAUDE.md | `/TrainvocClient/CLAUDE.md` | Android details |
| Web CLAUDE.md | `/TrainvocWeb/CLAUDE.md` | React details |
| Backend CLAUDE.md | `/TrainvocBackend/CLAUDE.md` | Spring Boot details |
| Architecture | `/ARCHITECTURE.md` | System design |
| Investigation | `/INVESTIGATION_REPORT.md` | Analysis & recommendations |

---

## Session Notes

### Latest Session: July 15, 2026 (Onboarding Bug + Relational Multilingual Dictionary v18)

**Branch:** `claude/trainvoc-login-database-l0covn`
**Focus:** Fix the "asks for name every launch" bug; redesign the word database into a relational, multilingual, numeric-id schema.

**Shipped:**
- **Onboarding bug fixed:** username was written to encrypted `secure_user_prefs` but Splash read plain `user_prefs` → always treated as new user. Single source of truth = `PreferencesRepository` + one-time `LegacyPrefsMigrator`; all reader/writer sites unified (Splash/Home/Drawer/Settings/Profile/Importer/Exporter). Avatar had the same split and is fixed too.
- **Room v18 relational schema:** `languages` (en=1, tr=2), `words` with numeric autoincrement PK + `UNIQUE(word, language_id)`, `word_translations` (sense-grouped cross-lingual M:N), id-based `synonyms`, id-keyed exam/word-of-day/quiz-results tables with real FKs. Turkish words are first-class rows (5,074 TR + 5,466 EN). `meaning` column kept as a display cache (drop planned v19).
- **MIGRATION_17_18** rebuilds from the bundled `seed_v18.json` manifest; user progress carried by lemma; custom words get ids ≥ 1,000,000; validated by Robolectric tests (progress carry-over + fresh-install asset validation).
- **tools/dictgen** (Python stdlib): packed-gloss parser (shared fixtures with Kotlin `MeaningParser`), manifest emitter, DB builder that executes Room's exported 18.json DDL verbatim, `ids.lock.json` permanent id ledger.
- **UI:** Word detail shows numbered senses + translation chips + relational synonyms; Settings has a Learning Language section (EN→TR active; AR/DE/PT/ES/FR "coming soon").
- **CI fixed** (was dead: triggered on `[main, develop]`, default branch is `master`); JDK 21; `release.yml` publishes APKs to GitHub Releases on `v*` tags.

**Key invariants for future sessions:**
- Word ids are permanent; regeneration must go through `tools/dictgen` (never renumber; `ids.lock.json` is authoritative).
- Adding a language is data-only (languages row + word rows + translation edges).
- The prepopulated asset DB must be rebuilt with `generate.py build-db --schema .../18.json` whenever entities change (DDL comes from Room's export, never hand-written).

---

### Previous Session: June 6, 2026 (Spaced-Repetition / FSRS Engine — Phase 6)

**Branch:** `feat/srs-engine` (off `master`)
**Focus:** Build the SRS engine's first shippable slices per `docs/design/srs-spaced-repetition-engine.md` + ADR-0001 (FSRS over SM-2), and clear the last held dependency.

**Shipped (all behind `srs_engine_enabled`, default OFF — flag wiring is S2/S3):**
- **S1 — FSRS-5 algorithm (Android, pure Kotlin)**: `TrainvocClient/.../srs/algorithm/` — `FsrsAlgorithm` (two-component stability+difficulty memory model, published universal weights, forgetting curve, interval/retrievability), `FsrsCard`/`FsrsRating`/`FsrsState`, `Sm2ToFsrsMigrator` (SM-2→FSRS seeding for the future Room V18). **22 headless JVM unit tests green** (`:app:testDebugUnitTest`, no emulator needed). No UI yet — Review Queue (S2) + quiz auto-schedule hook (S3) are the next slices; Room V18 migration + `ReviewScheduleDao` are the remaining S1 persistence wiring.
- **S4 — backend cross-device sync (Spring Boot 4 / JDK 21)**: `POST /api/v1/srs/reviews` (batch upsert, last-write-wins on `clientUpdatedAt`) + `GET /api/v1/srs/schedule`. `SrsSchedule` entity (**additive** `srs_schedule` table, primary DB, Hibernate ddl-auto — no existing table touched), `SrsService`, `SrsController`, `dto/srs/` records, auth-gated in `SecurityConfig`. **14 backend tests green**; full suite still 230 tests / 20 pre-existing failures (zero new).
- **Dependabot #16 (Gradle 9.4.1)**: verified the SB4 backend builds + tests **identically** on Gradle 9.4.1 → shipped the wrapper bump on this branch; stale conflicting PR #16 retired.

**Build/test commands used (host JDK 21, `/opt/android-sdk`, no emulator):**
- Backend: `cd TrainvocBackend && ./gradlew test` (JDK 21; wrapper now 9.4.1).
- Android SRS only: `cd TrainvocClient && ./gradlew :app:testDebugUnitTest --tests "com.gultekinahmetabdullah.trainvoc.srs.*"`.

**Boundary noted:** the Android FSRS logic is pure Kotlin and fully unit-tested headless; the Review Queue UI + ViewModel + Room V18 persistence (S2/S3) require Compose/Room wiring that can only be end-to-end-verified on a device/emulator (none available here).

---

### Previous Session: January 25, 2026 (Authentication & Firebase Integration)

**Branch:** `master`
**Focus:** Firebase Authentication Integration + Issue Tracker Completion

**Authentication System Added:**
- [x] Backend: Firebase Auth integration (`FirebaseConfig.java`, `FirebaseTokenProvider.java`)
- [x] Backend: JWT filter updated with Firebase token support
- [x] Backend: AuthController enhanced with Firebase endpoints
- [x] Backend: User model updated with AuthProvider enum
- [x] Android: Firebase Auth repository (`FirebaseAuthRepository.kt`)
- [x] Android: Login/Register screens with Google Sign-In support
- [x] Android: AuthViewModel with Firebase integration
- [x] Android: NetworkModule updated for auth headers

**Issue Tracker Status (TODO.md):**
- **Web: ✅ COMPLETE** - All 15 issues fixed or noted
- **Backend: ✅ COMPLETE** - All 3 issues fixed
- **Database/Infra: ✅ READY** - Docker Compose, SSL docs created
- **Android: 92 issues remaining** (mostly UI polish)

**Infrastructure Completed:**
- [x] `docker-compose.yml` - PostgreSQL + Backend + Nginx
- [x] `docker.env.example` - Environment template
- [x] `SSL_SETUP.md` - Let's Encrypt + Nginx guide
- [x] `.env.example` updates for production URLs
- [x] `application-prod.properties` - CORS env override

**Security:**
- [x] Added `google-services.json` to .gitignore (API keys)
- [x] Added `.claude/settings.local.json` to .gitignore

---

### Previous Session: January 25, 2026 (Android Pre-Deployment Fixes)

**Branch:** `master`
**Focus:** Android App Pre-Deployment Bug Fixes

**Critical Android Fixes Completed (18 issues):**
- [x] #004: Password hashing - Now uses SHA-256 instead of hashCode()
- [x] #005: Play Games achievements - Gracefully skipped when not configured
- [x] #006-007: Cloud backup - UI updated to show "Coming Soon" honestly
- [x] #016-017: DictionaryScreen - Fixed hardcoded color, increased touch targets
- [x] #018: HomeScreen - Now shows actual username from SharedPreferences
- [x] #019: FAQ backup description - Updated to mention local backup
- [x] #021: Leaderboard - Removed fake mock users, shows "Coming Soon"
- [x] #023: ImageService - Now uses Lorem Picsum for reliable images
- [x] #027: ProfileScreen accuracy - Now shows real mastery rate
- [x] #028-033: Hardcoded strings - Replaced with stringResource for i18n

**Build Status:** ✅ Debug build succeeds

**Issue Tracker Progress:**
- Total: 150 issues
- Fixed: 55 issues
- WONTFIX: 3 issues
- Remaining: 92 issues (mostly Android UI polish)

---

### Previous Session: January 25, 2026 (Branding & Play Store)

**Branch:** `master`
**Focus:** Branding Consistency & Play Store Preparation

**Branding Fixes Completed:**
- [x] Unified brand name to "Trainvoc" (fixed "TrainVoc" inconsistencies)
- [x] Updated copyright to "© 2024-2026"
- [x] Updated support email to `rollingcat.help@gmail.com`
- [x] Updated website URL to `trainvoc.rollingcatsoftware.com`
- [x] Created Privacy Policy page (`/privacy`) - bilingual EN/TR
- [x] Created Terms of Service page (`/terms`) - bilingual EN/TR
- [x] Added routes for legal pages in App.tsx
- [x] Removed incomplete translations (ES, DE, FR, AR)
- [x] Updated BRANDING.md with all decisions

**Play Store Preparation:**
- [x] Created `TrainvocClient/store-listing/` folder structure
- [x] Added store listing content (EN/TR) in `store-listing.md`
- [x] Created standalone `privacy-policy.html` for hosting
- [x] Created comprehensive `PLAY_STORE_SUBMISSION_CHECKLIST.md`

---

### Remaining Work

**High Priority (Play Store):**
- [ ] Create feature graphic (1024x500 px)
- [ ] Capture 2-8 app screenshots
- [ ] Host privacy policy at public URL
- [ ] Generate upload keystore
- [ ] Complete Play Console forms (Data Safety, Content Rating)

**High Priority (Deployment):**
- [ ] Deploy to GCP Compute Engine VM
- [ ] Configure SSL certificates for production
- [ ] Verify production WebSocket connections (wss://)

**Medium Priority:**
- [ ] Migrate Android package ID to `com.rollingcatsoftware.trainvoc`
- [ ] Phase 4: DRY & Code Deduplication (from Master Fix Plan)
- [ ] Phase 5: Architecture Improvements
- [ ] Add automated integration tests for WebSocket flows

**Low Priority:**
- [ ] Add WebSocket reconnection logic in frontend
- [ ] Implement proper authentication (JWT)
- [ ] Add comprehensive error handling for edge cases

---

### Previous Session: January 25, 2026 (Earlier)

**Branch:** `master`
**Focus:** Fix WebSocket Database Persistence & Manual Testing

**Critical Bug Fixed:**
- WebSocket Room Creation Not Persisting to Database
- Solution: Direct EntityManager management with explicit transaction control

**All WebSocket flows verified working.**

---

### Previous Session: January 22, 2026

**Branch:** `claude/implement-master-fixes-zM4J3`
**Focus:** Implement Master Fix Plan (Phases 1-3)

**Completed:**
- [x] Phase 1: Critical Security & Blockers
- [x] Phase 2: SOLID Principle Fixes
- [x] Phase 3: Design Pattern Implementation

---

*This document should be updated after each significant development session.*
