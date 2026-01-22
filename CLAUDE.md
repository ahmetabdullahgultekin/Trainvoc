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

**TrainvocBackend:**
- Database credentials in `application.properties`
- SSL keystore path (if enabled)

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

### Latest Session: January 22, 2026

**Branch:** `claude/explore-project-structure-Fvomz`
**Focus:** Prepare repository for public release and conduct investigation

**Tasks:**
- [ ] Create/update CLAUDE.md files for all modules
- [ ] Create README.md for monorepo
- [ ] Create ARCHITECTURE.md
- [ ] Create CONTRIBUTING.md
- [ ] Create LICENSE
- [ ] Conduct comprehensive investigation
- [ ] Create INVESTIGATION_REPORT.md

---

*This document should be updated after each significant development session.*
