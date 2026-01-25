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

1. **TrainvocClient**
   - Game UI screens were deleted (recoverable from git)
   - TTS service exists but not connected to UI
   - Backend sync is placeholder implementation
   - Cloud backup UI-only (no provider)

2. **TrainvocWeb**
   - Hard-coded backend URL (localhost:8080)
   - No authentication implementation
   - Limited error handling

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

### Latest Session: January 25, 2026 (Continued)

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

**Key Documents Updated:**
- `BRANDING.md` - Complete branding reference
- `TrainvocClient/store-listing/README.md` - Screenshot specs
- `docs/PLAY_STORE_SUBMISSION_CHECKLIST.md` - 21-section checklist

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
