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
├── TrainvocBackend/         # Server (Spring Boot, Java 24)
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
| **Language** | Kotlin 2.1.10 | TypeScript 5.8.3 | Java 24 |
| **Framework** | Jetpack Compose | React 19.1.0 | Spring Boot 3.5.0 |
| **Build** | Gradle (KTS) | Vite 6.3.5 | Gradle |
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

### Current Project Status

| Component | Build Status | Feature Completeness | Notes |
|-----------|-------------|---------------------|-------|
| **TrainvocClient** | Builds | ~70% | Games UI deleted, TTS not connected |
| **TrainvocWeb** | Builds | ~80% | Multiplayer functional |
| **TrainvocBackend** | Builds | ~85% | Dual DB setup working |

### Known Issues

1. **TrainvocClient** (117 open issues in TODO.md)
   - Game UI screens were deleted (recoverable from git)
   - Backend sync is placeholder implementation
   - Cloud backup shows "Coming Soon" (no provider yet)
   - ~~Password hashing~~ ✅ FIXED - Uses SHA-256
   - ~~Mock leaderboard data~~ ✅ FIXED - Shows "Coming Soon"
   - ~~Hardcoded UI strings~~ ✅ FIXED - Uses stringResource

2. **TrainvocWeb**
   - ~~Hard-coded backend URL~~ ✅ FIXED - Uses VITE_API_URL
   - No authentication implementation
   - ~~Limited error handling~~ ✅ FIXED - Error utilities added

3. **TrainvocBackend**
   - SSL disabled for development
   - No authentication/authorization
   - Missing rate limiting

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
| JDK | 24+ | TrainvocBackend |
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

### Latest Session: January 25, 2026 (Android Pre-Deployment Fixes)

**Branch:** `master`
**Focus:** Android App Pre-Deployment Bug Fixes

**Critical Android Fixes Completed (18 issues):**
- [x] #004: Password hashing - Now uses SHA-256 instead of hashCode()
- [x] #005: Play Games achievements - Gracefully skipped when not configured
- [x] #006-007: Cloud backup - UI updated to show "Coming Soon" honestly
- [x] #008-009: Tutorial stubs - Marked WONTFIX (working as designed)
- [x] #016-017: DictionaryScreen - Fixed hardcoded color, increased touch targets
- [x] #018: HomeScreen - Now shows actual username from SharedPreferences
- [x] #019: FAQ backup description - Updated to mention local backup
- [x] #021: Leaderboard - Removed fake mock users, shows "Coming Soon"
- [x] #022: TTS placeholder - Marked WONTFIX (Android TTS API used)
- [x] #023: ImageService - Now uses Lorem Picsum for reliable images
- [x] #027: ProfileScreen accuracy - Now shows real mastery rate
- [x] #028-033: Hardcoded strings - Replaced with stringResource for i18n

**Build Status:** ✅ Debug build succeeds

**Issue Tracker Progress:**
- Total: 150 issues
- Fixed: 30 issues
- WONTFIX: 3 issues
- Remaining: 117 issues (mostly non-critical)

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
