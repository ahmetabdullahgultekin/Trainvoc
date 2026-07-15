# Trainvoc System Architecture Analysis

> **⚠️ Historical snapshot — January 22, 2026. Superseded; kept for provenance.**
> This analysis describes the codebase at its original date and is **no longer maintained**; some details are stale (e.g. the web stack is Tailwind + Radix, not MUI; games are present, not deleted; versions have moved on). It also predates the Hostinger→Hetzner hosting move.
> For current architecture see the root `ARCHITECTURE.md`; for status see `README.md`, `CLAUDE.md`, and GitHub Issues & Milestones: https://github.com/ahmetabdullahgultekin/Trainvoc/issues

> **Document Version:** 1.0
> **Date:** January 22, 2026
> **Purpose:** Comprehensive analysis for production deployment on Hostinger VPS

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Overview](#system-overview)
3. [Project Analysis](#project-analysis)
4. [Integration & Coupling Matrix](#integration--coupling-matrix)
5. [Network Architecture](#network-architecture)
6. [Technology Stack Analysis](#technology-stack-analysis)
7. [Security Assessment](#security-assessment)
8. [Deployment Architecture for Hostinger VPS](#deployment-architecture-for-hostinger-vps)
9. [Infrastructure Requirements](#infrastructure-requirements)
10. [Migration Roadmap](#migration-roadmap)
11. [Production Checklist](#production-checklist)
12. [Appendix: API Reference](#appendix-api-reference)

---

## Executive Summary

Trainvoc is a **multi-platform language learning ecosystem** consisting of three interconnected applications:

| Component | Purpose | Production Readiness |
|-----------|---------|---------------------|
| **TrainvocBackend** | REST API + WebSocket server | 70% Ready |
| **TrainvocClient** | Android mobile app | 85% Ready |
| **TrainvocWeb** | Browser-based multiplayer game | 75% Ready |

### Critical Findings

| Priority | Issue | Impact |
|----------|-------|--------|
| 🔴 HIGH | No Docker containerization | Difficult deployment |
| 🔴 HIGH | Hardcoded credentials in config files | Security vulnerability |
| 🔴 HIGH | CORS allows localhost in production config | Security vulnerability |
| 🟡 MEDIUM | WebSocket allows only single origin | Limited scalability |
| 🟡 MEDIUM | SSL commented out in backend | Insecure communication |
| 🟡 MEDIUM | Cloud backup not implemented | Data sync not functional |
| 🟢 LOW | No CI/CD for backend/web | Manual deployment required |

---

## System Overview

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           TRAINVOC ECOSYSTEM                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────┐              ┌─────────────────────┐              │
│   │   TRAINVOC CLIENT   │              │    TRAINVOC WEB     │              │
│   │   (Android App)     │              │   (React SPA)       │              │
│   │                     │              │                     │              │
│   │   Kotlin 2.1.10     │              │   React 19.1        │              │
│   │   Compose UI        │              │   TypeScript 5.8    │              │
│   │   Room Database     │              │   Vite 6.3          │              │
│   │   Offline-first     │              │   Material UI 7.1   │              │
│   └──────────┬──────────┘              └──────────┬──────────┘              │
│              │                                    │                         │
│              │ Dictionary API                     │ REST + WebSocket        │
│              │ (External)                         │                         │
│              ▼                                    ▼                         │
│   ┌─────────────────────┐              ┌─────────────────────┐              │
│   │ dictionaryapi.dev   │              │  TRAINVOC BACKEND   │              │
│   │ (3rd Party API)     │              │  (Spring Boot)      │              │
│   └─────────────────────┘              │                     │              │
│                                        │  Java 24            │              │
│   ┌─────────────────────┐              │  Spring Boot 3.5    │              │
│   │   GOOGLE SERVICES   │              │  WebSocket          │              │
│   │                     │              │  JPA/Hibernate      │              │
│   │   • Play Games      │              └──────────┬──────────┘              │
│   │   • Google Drive    │                         │                         │
│   │   • Auth            │                         │                         │
│   └─────────────────────┘                         ▼                         │
│                                        ┌─────────────────────┐              │
│                                        │    POSTGRESQL       │              │
│                                        │                     │              │
│                                        │  • trainvoc         │              │
│                                        │  • trainvoc-words   │              │
│                                        └─────────────────────┘              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              DATA FLOW DIAGRAM                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                          MULTIPLAYER GAME FLOW                              │
│   ┌─────────────┐                                    ┌─────────────┐        │
│   │   Web App   │ ────── HTTP/REST ─────────────────▶│   Backend   │        │
│   │  (Browser)  │ ────── WebSocket ─────────────────▶│  (Spring)   │        │
│   └─────────────┘        (Real-time)                 └──────┬──────┘        │
│                                                              │              │
│         1. Create/Join Room (REST)                           │              │
│         2. Game Events (WebSocket)                           │              │
│         3. Leaderboard Updates (REST)                        ▼              │
│                                                    ┌─────────────────┐      │
│                                                    │   PostgreSQL    │      │
│                                                    │   • Game Rooms  │      │
│                                                    │   • Players     │      │
│                                                    │   • Questions   │      │
│                                                    │   • Scores      │      │
│                                                    └─────────────────┘      │
│                                                                             │
│                          MOBILE APP FLOW                                    │
│   ┌─────────────┐                                    ┌─────────────┐        │
│   │ Android App │ ────── HTTP/REST ─────────────────▶│ Dictionary  │        │
│   │ (Offline-   │        (External API)              │    API      │        │
│   │  first)     │                                    └─────────────┘        │
│   └──────┬──────┘                                                           │
│          │                                                                  │
│          ▼                                           ┌─────────────┐        │
│   ┌─────────────┐                                    │   Google    │        │
│   │ Room SQLite │ ───── OAuth + API ────────────────▶│   Cloud     │        │
│   │  Database   │      (Backup - NOT IMPLEMENTED)    │   Services  │        │
│   └─────────────┘                                    └─────────────┘        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Project Analysis

### 1. TrainvocBackend (Spring Boot)

**Location:** `/TrainvocBackend/`

#### Purpose
- Multiplayer quiz game server
- Word database API
- Real-time game state management via WebSocket

#### Architecture Pattern
```
┌──────────────────────────────────────────────────────────────┐
│                    LAYERED ARCHITECTURE                      │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │              PRESENTATION LAYER                      │   │
│   │                                                      │   │
│   │   GameController    QuizController    WordController │   │
│   │   LeaderboardController    GameWebSocketHandler      │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                SERVICE LAYER                         │   │
│   │                                                      │   │
│   │   GameService    QuizService    LeaderboardService   │   │
│   │   PlayerAnswerService    RoomCleanupService          │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │               REPOSITORY LAYER                       │   │
│   │                                                      │   │
│   │   PlayerRepository    GameRoomRepository             │   │
│   │   QuestionRepository    WordRepository               │   │
│   │   ExamRepository    WordExamCrossRefRepository       │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 DATA LAYER                           │   │
│   │                                                      │   │
│   │   PostgreSQL (trainvoc)    PostgreSQL (trainvoc-words)│   │
│   └─────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

#### Key Components

| Component | File | Purpose |
|-----------|------|---------|
| Main Entry | `TrainvocMultiplayerApplication.java` | Spring Boot bootstrap |
| Game API | `GameController.java` | Room creation, joining, game flow |
| Quiz API | `QuizController.java` | Question generation |
| Words API | `WordController.java` | Word database access |
| Leaderboard | `LeaderboardController.java` | Rankings |
| WebSocket | `GameWebSocketHandler.java` | Real-time game events |
| CORS | `CorsConfig.java` | Cross-origin configuration |
| Security | `SecurityConfig.java` | Authentication (currently open) |
| Primary DB | `PrimaryDataSourceConfig.java` | Main database connection |
| Secondary DB | `SecondDataSourceConfig.java` | Words database connection |

#### Dependencies
```groovy
// Core
Spring Boot 3.5.0
Spring Web
Spring WebSocket
Spring Data JPA
Spring Security

// Database
PostgreSQL 42.7.3

// Utilities
Lombok
JSON (org.json:20240303)
```

#### Issues Identified

| Issue | Severity | Details |
|-------|----------|---------|
| Open Security | 🔴 HIGH | `requestMatchers("/**").permitAll()` - no authentication |
| Hardcoded Password | 🔴 HIGH | `15200403` in application.properties |
| CORS localhost | 🔴 HIGH | `http://localhost:5173` allowed in production config |
| WebSocket single origin | 🟡 MEDIUM | Only `trainvoc.rollingcatsoftware.com` allowed |
| No rate limiting | 🟡 MEDIUM | API vulnerable to abuse |
| No API versioning | 🟡 MEDIUM | Breaking changes will affect clients |
| SQL in comments | 🟢 LOW | AWS RDS credentials commented (should be removed) |

---

### 2. TrainvocClient (Android)

**Location:** `/TrainvocClient/`

#### Purpose
- Vocabulary learning app (English-Turkish)
- Quiz games with spaced repetition (SM-2 algorithm)
- Gamification (achievements, streaks, goals)
- Offline-first architecture

#### Architecture Pattern
```
┌──────────────────────────────────────────────────────────────┐
│                MVVM + CLEAN ARCHITECTURE                     │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │              PRESENTATION LAYER                      │   │
│   │                                                      │   │
│   │   Jetpack Compose UI    ViewModels    Navigation     │   │
│   │   Material 3 Theme      Animations    Accessibility  │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 DOMAIN LAYER                         │   │
│   │                                                      │   │
│   │   Use Cases    Business Logic    Algorithms (SM-2)   │   │
│   │   Quiz Engine    Gamification Logic                  │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                  DATA LAYER                          │   │
│   │                                                      │   │
│   │   Repositories    Data Sources    Mappers            │   │
│   │   API Services    Database DAOs                      │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │              INFRASTRUCTURE LAYER                    │   │
│   │                                                      │   │
│   │   Room Database (v11)    Retrofit/OkHttp             │   │
│   │   WorkManager    Google Play Services                │   │
│   │   Security Crypto    SharedPreferences               │   │
│   └─────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

#### Key Features

| Feature | Status | Notes |
|---------|--------|-------|
| Dictionary/Word Management | ✅ Working | Full CRUD operations |
| Quiz System (9 types) | ✅ Working | Multiple choice, fill blank, etc. |
| Spaced Repetition (SM-2) | ✅ Working | Algorithm fully implemented |
| Gamification | ✅ Working | 44 achievements, streaks, goals |
| Statistics | ✅ Working | Comprehensive analytics |
| Offline Mode | ✅ Working | All data local |
| Accessibility | ✅ Working | WCAG 2.1 AA compliant |
| Widgets (4 types) | ✅ Working | Streak, Goals, etc. |
| Memory Games (10 types) | ⚠️ Deleted | Recoverable from git |
| Cloud Backup | ❌ Placeholder | Returns `NotImplementedException` |
| Backend Sync | ❌ Missing | No implementation exists |
| TTS Integration | ⚠️ Partial | Service exists, not connected to UI |

#### External API Integrations

| Service | URL | Purpose | Status |
|---------|-----|---------|--------|
| Free Dictionary API | `api.dictionaryapi.dev/api/v2/` | Word definitions | ✅ Working |
| Unsplash | `source.unsplash.com` | Word images | ✅ Working |
| Google Play Games | Play Services SDK | Achievements sync | ✅ Configured |
| Google Drive | Drive API | Cloud backup | ❌ Not implemented |

#### Build Configuration

```kotlin
android {
    namespace = "com.gultekinahmetabdullah.trainvoc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gultekinahmetabdullah.trainvoc"
        minSdk = 24    // Android 7.0 (Nougat)
        targetSdk = 35 // Android 15
        versionCode = 13
        versionName = "1.2.0"
    }
}
```

---

### 3. TrainvocWeb (React)

**Location:** `/TrainvocWeb/`

#### Purpose
- Browser-based multiplayer quiz game interface
- Real-time gameplay via WebSocket
- Leaderboard and profile management

#### Architecture Pattern
```
┌──────────────────────────────────────────────────────────────┐
│               COMPONENT-BASED ARCHITECTURE                   │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                    PAGES                             │   │
│   │                                                      │   │
│   │   HomePage    PlayPage    GamePage    LobbyPage      │   │
│   │   LeaderboardPage    ProfilePage    AboutPage        │   │
│   │   CreateRoomPage    JoinRoomPage    RoomDetailPage   │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 COMPONENTS                           │   │
│   │                                                      │   │
│   │   GameQuestion    GameFinal    GameRanking           │   │
│   │   GameStartCountdown    PlaySidebar                  │   │
│   │   Navbar    Footer    Modal    RoomCard    Loader    │   │
│   └─────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 SERVICES                             │   │
│   │                                                      │   │
│   │   api.ts (Axios)    i18n.ts    hashPassword.ts       │   │
│   │   useNick.ts    useProfile.ts                        │   │
│   └─────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

#### Page Routes

| Route | Component | Purpose |
|-------|-----------|---------|
| `/` | HomePage | Landing page |
| `/play` | PlayPage | Game lobby overview |
| `/play/create` | CreateRoomPage | Create new game room |
| `/play/join` | JoinRoomPage | Join existing room |
| `/play/room/:roomCode` | RoomDetailPage | Room details |
| `/play/lobby/:roomCode` | LobbyPage | Waiting room |
| `/play/game/:roomCode` | GamePage | Active game |
| `/leaderboard` | LeaderboardPage | Rankings |
| `/profile` | ProfilePage | User profile |
| `/about` | AboutPage | About the app |
| `/contact` | ContactPage | Contact form |
| `/mobile-app` | MobileAppPage | Android app promo |

#### Build Configuration

```javascript
// vite.config.ts
export default defineConfig({
    server: {
        proxy: {
            '/api': 'http://localhost:8080'  // Dev proxy to backend
        }
    },
    build: {
        rollupOptions: {
            output: {
                manualChunks: {
                    vendor: [...],  // node_modules
                    pages: [...]    // src/pages
                }
            }
        },
        chunkSizeWarningLimit: 800
    }
});
```

---

## Integration & Coupling Matrix

### Component Dependencies

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    INTEGRATION & COUPLING MATRIX                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│                    ┌─────────────┐                                      │
│                    │   Backend   │                                      │
│                    │  (Spring)   │                                      │
│                    └──────┬──────┘                                      │
│                           │                                             │
│           ┌───────────────┼───────────────┐                             │
│           │               │               │                             │
│           ▼               ▼               ▼                             │
│    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                     │
│    │ PostgreSQL  │ │ PostgreSQL  │ │  Web App    │                     │
│    │ (trainvoc)  │ │(trainvoc-   │ │  (React)    │                     │
│    │             │ │  words)     │ │             │                     │
│    └─────────────┘ └─────────────┘ └─────────────┘                     │
│                                           │                             │
│                                    REST + WebSocket                     │
│                                           │                             │
│                                    ┌──────┴──────┐                     │
│                                    │   Browser   │                     │
│                                    │   Client    │                     │
│                                    └─────────────┘                     │
│                                                                         │
│                    ┌─────────────┐                                      │
│                    │ Android App │ ◀── NOT CONNECTED ──▶ Backend       │
│                    │  (Kotlin)   │     (Offline-only)                  │
│                    └──────┬──────┘                                      │
│                           │                                             │
│           ┌───────────────┼───────────────┐                             │
│           │               │               │                             │
│           ▼               ▼               ▼                             │
│    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                     │
│    │   Room DB   │ │ Dictionary  │ │   Google    │                     │
│    │  (SQLite)   │ │    API      │ │  Services   │                     │
│    └─────────────┘ └─────────────┘ └─────────────┘                     │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Coupling Analysis

| Component A | Component B | Coupling Type | Strength | Notes |
|-------------|-------------|---------------|----------|-------|
| Web ↔ Backend | REST API | Loose | ✅ Good | Well-defined API contracts |
| Web ↔ Backend | WebSocket | Medium | ⚠️ Moderate | Shared game state |
| Backend ↔ Primary DB | JPA | Tight | ⚠️ Moderate | ORM coupling |
| Backend ↔ Secondary DB | JPA | Tight | ⚠️ Moderate | ORM coupling |
| Android ↔ Backend | None | N/A | ❌ Missing | No integration exists |
| Android ↔ External APIs | REST | Loose | ✅ Good | Dictionary API |
| Android ↔ Google Cloud | OAuth/API | Medium | ✅ Good | Play Games, Drive (partial) |

### Data Flow Between Components

| Flow | Source | Destination | Protocol | Data Format |
|------|--------|-------------|----------|-------------|
| Create Room | Web | Backend | HTTP POST | JSON |
| Join Room | Web | Backend | HTTP POST | JSON |
| Game Events | Backend | Web | WebSocket | JSON |
| Answer Submit | Web | Backend | HTTP POST | JSON |
| Leaderboard | Backend | Web | HTTP GET | JSON |
| Word Lookup | Android | Dictionary API | HTTP GET | JSON |
| Word Images | Android | Unsplash | HTTP GET | Image URL |

---

## Network Architecture

### Current Configuration

#### Backend (application.properties)

```properties
# Production (Commented - AWS RDS)
# spring.datasource.url=jdbc:postgresql://trainvoc.cnso06sqw3r8.eu-north-1.rds.amazonaws.com:5432/postgres
# spring.second-datasource.url=jdbc:postgresql://trainvoc-words.cnso06sqw3r8.eu-north-1.rds.amazonaws.com:5432/postgres

# Local Development (Active)
server.port=8080
server.ssl.enabled=false
spring.datasource.url=jdbc:postgresql://localhost:5432/trainvoc
spring.second-datasource.url=jdbc:postgresql://localhost:5432/trainvoc-words
```

#### Web App (api.ts)

```typescript
const api = axios.create({
    // Development
    baseURL: "http://localhost:8080/",

    // Production (Commented)
    // baseURL: 'https://api.trainvoc.rollingcatsoftware.com:8443/',

    withCredentials: true
});
```

### Port Assignments

| Service | Development Port | Production Port | Protocol |
|---------|-----------------|-----------------|----------|
| Backend API | 8080 | 8443 (SSL) | HTTP/HTTPS |
| Backend WebSocket | 8080 | 8443 (SSL) | WS/WSS |
| Web Frontend | 5173 (Vite) | 80/443 | HTTP/HTTPS |
| PostgreSQL | 5432 | 5432 | TCP |

### Domain Configuration

| Domain | Purpose | Target |
|--------|---------|--------|
| `trainvoc.rollingcatsoftware.com` | Web frontend | Nginx → React build |
| `api.trainvoc.rollingcatsoftware.com` | Backend API | Nginx → Spring Boot |
| `www.trainvoc.com` | Marketing site | (Future) |

### CORS Configuration

```java
// CorsConfig.java
registry.addMapping("/**")
    .allowedOrigins(
        "https://trainvoc.rollingcatsoftware.com",
        "https://api.trainvoc.rollingcatsoftware.com",
        "https://api.trainvoc.rollingcatsoftware.com:8443",
        "http://localhost:5173"  // ⚠️ REMOVE IN PRODUCTION
    )
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(true);
```

### WebSocket Configuration

```java
// WebSocketConfig.java
registry.addHandler(handler, "/ws/game")
    .setAllowedOrigins("https://trainvoc.rollingcatsoftware.com");
    // ⚠️ Need to add localhost for development
```

---

## Technology Stack Analysis

### Version Comparison

| Technology | Current Version | Latest Stable | Status |
|------------|----------------|---------------|--------|
| **Backend** |
| Java | 24 | 24 (LTS coming in 25) | ✅ Latest |
| Spring Boot | 3.5.0 | 3.5.0 | ✅ Latest |
| PostgreSQL Driver | 42.7.3 | 42.7.3 | ✅ Latest |
| Lombok | Latest | Latest | ✅ Latest |
| **Android** |
| Kotlin | 2.1.10 | 2.1.10 | ✅ Latest |
| Compose BOM | 2025.06.00 | 2025.06.00 | ✅ Latest |
| Room | 2.7.1 | 2.7.1 | ✅ Latest |
| Hilt | 2.57.2 | 2.57.2 | ✅ Latest |
| AGP | 8.13.2 | 8.13.2 | ✅ Latest |
| **Web** |
| React | 19.1.0 | 19.1.0 | ✅ Latest |
| TypeScript | 5.8.3 | 5.8.3 | ✅ Latest |
| Vite | 6.3.5 | 6.3.5 | ✅ Latest |
| Material UI | 7.1.2 | 7.1.2 | ✅ Latest |
| Axios | 1.10.0 | 1.10.0 | ✅ Latest |

### Technology Recommendations

| Component | Current | Recommendation | Priority |
|-----------|---------|----------------|----------|
| Backend Runtime | JAR | Docker container | 🔴 HIGH |
| Database | PostgreSQL local | Managed PostgreSQL | 🔴 HIGH |
| SSL/TLS | Disabled | Let's Encrypt + Nginx | 🔴 HIGH |
| API Gateway | None | Nginx reverse proxy | 🔴 HIGH |
| Secrets Management | Hardcoded | Environment variables | 🔴 HIGH |
| Monitoring | None | Prometheus + Grafana | 🟡 MEDIUM |
| Logging | Console | ELK Stack or Loki | 🟡 MEDIUM |
| CI/CD | GitHub Actions (Android) | Add Backend/Web | 🟡 MEDIUM |

---

## Security Assessment

### Vulnerabilities Found

| ID | Severity | Component | Issue | Recommendation |
|----|----------|-----------|-------|----------------|
| SEC-001 | 🔴 CRITICAL | Backend | Hardcoded database password `15200403` | Use environment variables |
| SEC-002 | 🔴 CRITICAL | Backend | SSL disabled in production | Enable SSL with valid certificate |
| SEC-003 | 🔴 CRITICAL | Backend | All endpoints publicly accessible | Implement authentication |
| SEC-004 | 🔴 HIGH | Backend | CORS allows localhost | Remove localhost in production |
| SEC-005 | 🔴 HIGH | Config | AWS credentials in comments | Remove from version control |
| SEC-006 | 🟡 MEDIUM | Backend | No rate limiting | Add Spring Security rate limits |
| SEC-007 | 🟡 MEDIUM | Backend | No input validation on some endpoints | Add validation annotations |
| SEC-008 | 🟡 MEDIUM | Web | Password hashed client-side only | Add server-side hashing |
| SEC-009 | 🟢 LOW | Android | ProGuard rules may leak class names | Review obfuscation rules |

### Security Configuration Required for Production

```properties
# application-prod.properties (create this file)

# SSL Configuration
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12

# Database (use environment variables)
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}

# Second database
spring.second-datasource.url=${DATABASE_WORDS_URL}
spring.second-datasource.username=${DATABASE_WORDS_USER}
spring.second-datasource.password=${DATABASE_WORDS_PASSWORD}

# Disable debug features
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
```

---

## Deployment Architecture for Hostinger VPS

### Recommended Setup

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     HOSTINGER VPS DEPLOYMENT                            │
│                     (Recommended: KVM 4 or higher)                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   Internet                                                              │
│      │                                                                  │
│      ▼                                                                  │
│   ┌─────────────────────────────────────────────────────────────────┐  │
│   │                         NGINX                                    │  │
│   │              (Reverse Proxy + SSL Termination)                   │  │
│   │                                                                  │  │
│   │   • Port 80  → Redirect to 443                                   │  │
│   │   • Port 443 → SSL/TLS (Let's Encrypt)                           │  │
│   │   • /api/*   → Backend (localhost:8080)                          │  │
│   │   • /ws/*    → Backend WebSocket (localhost:8080)                │  │
│   │   • /*       → React static files                                │  │
│   └─────────────────────────────────────────────────────────────────┘  │
│                      │                                                  │
│         ┌────────────┼────────────┐                                     │
│         ▼            ▼            ▼                                     │
│   ┌──────────┐ ┌──────────┐ ┌──────────┐                               │
│   │  React   │ │ Spring   │ │PostgreSQL│                               │
│   │  Build   │ │  Boot    │ │ (Docker) │                               │
│   │ (static) │ │ (Docker) │ │          │                               │
│   │          │ │ :8080    │ │  :5432   │                               │
│   └──────────┘ └──────────┘ └──────────┘                               │
│                                                                         │
│   System: Ubuntu 22.04 LTS                                              │
│   Docker: 24.x                                                          │
│   Docker Compose: 2.x                                                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Docker Compose Configuration

Create `docker-compose.yml` at repository root:

```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:16-alpine
    container_name: trainvoc-db
    restart: unless-stopped
    environment:
      POSTGRES_USER: ${DB_USER:-trainvoc}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: trainvoc
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./TrainvocBackend/sql-queries/trainvoc-mp-db-for-postgre.sql:/docker-entrypoint-initdb.d/01-init.sql
    ports:
      - "127.0.0.1:5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-trainvoc}"]
      interval: 10s
      timeout: 5s
      retries: 5

  # PostgreSQL Words Database
  postgres-words:
    image: postgres:16-alpine
    container_name: trainvoc-words-db
    restart: unless-stopped
    environment:
      POSTGRES_USER: ${DB_USER:-trainvoc}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: trainvoc-words
    volumes:
      - postgres_words_data:/var/lib/postgresql/data
      - ./TrainvocBackend/sql-queries/trainvoc-words-db-for-postgre.sql:/docker-entrypoint-initdb.d/01-init.sql
    ports:
      - "127.0.0.1:5433:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-trainvoc}"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot Backend
  backend:
    build:
      context: ./TrainvocBackend
      dockerfile: Dockerfile
    container_name: trainvoc-backend
    restart: unless-stopped
    depends_on:
      postgres:
        condition: service_healthy
      postgres-words:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:postgresql://postgres:5432/trainvoc
      DATABASE_USER: ${DB_USER:-trainvoc}
      DATABASE_PASSWORD: ${DB_PASSWORD}
      DATABASE_WORDS_URL: jdbc:postgresql://postgres-words:5432/trainvoc-words
      DATABASE_WORDS_USER: ${DB_USER:-trainvoc}
      DATABASE_WORDS_PASSWORD: ${DB_PASSWORD}
    ports:
      - "127.0.0.1:8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    container_name: trainvoc-nginx
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
      - ./TrainvocWeb/dist:/usr/share/nginx/html:ro
      - certbot_data:/var/www/certbot:ro
      - letsencrypt:/etc/letsencrypt:ro
    healthcheck:
      test: ["CMD", "nginx", "-t"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Certbot for SSL
  certbot:
    image: certbot/certbot
    container_name: trainvoc-certbot
    volumes:
      - certbot_data:/var/www/certbot
      - letsencrypt:/etc/letsencrypt
    entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew; sleep 12h & wait $${!}; done;'"

volumes:
  postgres_data:
  postgres_words_data:
  certbot_data:
  letsencrypt:

networks:
  default:
    name: trainvoc-network
```

### Backend Dockerfile

Create `TrainvocBackend/Dockerfile`:

```dockerfile
# Build stage
FROM eclipse-temurin:24-jdk-alpine AS builder

WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 trainvoc && \
    adduser -u 1000 -G trainvoc -s /bin/sh -D trainvoc

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown -R trainvoc:trainvoc /app
USER trainvoc

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

### Nginx Configuration

Create `nginx/nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Logging
    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=general:10m rate=30r/s;

    # Upstream for backend
    upstream backend {
        server backend:8080;
        keepalive 32;
    }

    # HTTP → HTTPS redirect
    server {
        listen 80;
        server_name trainvoc.rollingcatsoftware.com api.trainvoc.rollingcatsoftware.com;

        location /.well-known/acme-challenge/ {
            root /var/www/certbot;
        }

        location / {
            return 301 https://$host$request_uri;
        }
    }

    # Main HTTPS server
    server {
        listen 443 ssl http2;
        server_name trainvoc.rollingcatsoftware.com;

        ssl_certificate /etc/letsencrypt/live/trainvoc.rollingcatsoftware.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/trainvoc.rollingcatsoftware.com/privkey.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
        ssl_prefer_server_ciphers off;

        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;
        add_header Referrer-Policy "strict-origin-when-cross-origin" always;

        # API proxy
        location /api/ {
            limit_req zone=api burst=20 nodelay;

            proxy_pass http://backend/api/;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        # WebSocket proxy
        location /ws/ {
            proxy_pass http://backend/ws/;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_read_timeout 86400;
        }

        # Static files (React build)
        location / {
            limit_req zone=general burst=50 nodelay;

            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;

            # Cache static assets
            location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
                expires 1y;
                add_header Cache-Control "public, immutable";
            }
        }
    }

    # API subdomain (optional)
    server {
        listen 443 ssl http2;
        server_name api.trainvoc.rollingcatsoftware.com;

        ssl_certificate /etc/letsencrypt/live/trainvoc.rollingcatsoftware.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/trainvoc.rollingcatsoftware.com/privkey.pem;
        ssl_protocols TLSv1.2 TLSv1.3;

        location / {
            limit_req zone=api burst=20 nodelay;

            proxy_pass http://backend/;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        location /ws/ {
            proxy_pass http://backend/ws/;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_read_timeout 86400;
        }
    }
}
```

---

## Infrastructure Requirements

### Hostinger VPS Recommendations

| Plan | vCPU | RAM | Storage | Monthly Cost | Suitable For |
|------|------|-----|---------|--------------|--------------|
| KVM 1 | 1 | 4 GB | 50 GB | ~$6 | Development only |
| **KVM 2** | 2 | 8 GB | 100 GB | ~$12 | **Small production** |
| **KVM 4** | 4 | 16 GB | 200 GB | ~$19 | **Recommended** |
| KVM 8 | 8 | 32 GB | 400 GB | ~$39 | High traffic |

### Minimum Requirements

| Resource | Minimum | Recommended |
|----------|---------|-------------|
| CPU | 2 vCPU | 4 vCPU |
| RAM | 4 GB | 8 GB |
| Storage | 40 GB SSD | 100 GB SSD |
| Bandwidth | 1 TB/mo | Unlimited |
| OS | Ubuntu 22.04 LTS | Ubuntu 22.04 LTS |

### Resource Allocation

| Service | CPU | RAM | Storage |
|---------|-----|-----|---------|
| PostgreSQL (main) | 0.5 | 1 GB | 10 GB |
| PostgreSQL (words) | 0.5 | 512 MB | 5 GB |
| Spring Boot Backend | 1 | 2 GB | 500 MB |
| Nginx | 0.25 | 256 MB | 100 MB |
| OS + Docker | 0.5 | 1 GB | 10 GB |
| **Total** | **2.75** | **4.75 GB** | **~26 GB** |

---

## Migration Roadmap

### Phase 1: Infrastructure Setup (Day 1-2)

```
□ 1.1 Provision Hostinger VPS (KVM 4 recommended)
□ 1.2 Configure DNS records:
      - A record: trainvoc.rollingcatsoftware.com → VPS IP
      - A record: api.trainvoc.rollingcatsoftware.com → VPS IP
□ 1.3 SSH hardening:
      - Disable root login
      - Configure SSH keys
      - Change SSH port
      - Install fail2ban
□ 1.4 Install Docker & Docker Compose
□ 1.5 Configure firewall (UFW):
      - Allow 80, 443, SSH port
      - Block all other incoming
```

### Phase 2: Application Preparation (Day 2-3)

```
□ 2.1 Create production configuration files:
      - application-prod.properties
      - .env file for secrets
□ 2.2 Create Dockerfile for backend
□ 2.3 Create docker-compose.yml
□ 2.4 Create nginx.conf
□ 2.5 Update CORS configuration (remove localhost)
□ 2.6 Update WebSocket allowed origins
□ 2.7 Build React production bundle:
      - npm run build
□ 2.8 Test locally with Docker Compose
```

### Phase 3: Deployment (Day 3-4)

```
□ 3.1 Copy files to VPS:
      - git clone repository
      - or rsync/scp files
□ 3.2 Create .env file with production secrets
□ 3.3 Initialize databases:
      - docker compose up -d postgres postgres-words
      - Wait for healthy status
      - Run SQL initialization scripts
□ 3.4 Start all services:
      - docker compose up -d
□ 3.5 Configure SSL with Certbot:
      - docker compose run certbot certonly ...
□ 3.6 Verify all services running:
      - docker compose ps
      - docker compose logs
```

### Phase 4: Testing & Validation (Day 4-5)

```
□ 4.1 Test HTTPS access to frontend
□ 4.2 Test API endpoints
□ 4.3 Test WebSocket connection
□ 4.4 Test game creation and joining
□ 4.5 Test multiplayer gameplay
□ 4.6 Performance testing
□ 4.7 Security scan with OWASP ZAP
```

### Phase 5: Monitoring & Maintenance (Ongoing)

```
□ 5.1 Set up monitoring (optional):
      - Prometheus + Grafana
      - or Uptime Kuma for basic monitoring
□ 5.2 Configure log rotation
□ 5.3 Set up automated backups:
      - PostgreSQL dumps
      - Upload to external storage
□ 5.4 Configure SSL auto-renewal
□ 5.5 Document runbooks
```

---

## Production Checklist

### Pre-Deployment Security

- [ ] Remove all hardcoded credentials from code
- [ ] Create `.env` file for secrets (not in git)
- [ ] Remove localhost from CORS configuration
- [ ] Enable SSL/TLS
- [ ] Configure secure database passwords
- [ ] Review and remove commented AWS credentials
- [ ] Add rate limiting to all endpoints
- [ ] Implement proper authentication (optional but recommended)

### Backend Configuration

- [ ] Create `application-prod.properties`
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure connection pooling (HikariCP)
- [ ] Add Spring Boot Actuator for health checks
- [ ] Configure proper logging levels

### Frontend Configuration

- [ ] Update `api.ts` with production URL
- [ ] Build production bundle: `npm run build`
- [ ] Configure environment-based API URL
- [ ] Test production build locally

### Infrastructure

- [ ] Configure firewall rules
- [ ] Set up SSH key authentication
- [ ] Disable root SSH login
- [ ] Install and configure fail2ban
- [ ] Set up automatic security updates
- [ ] Configure Docker restart policies
- [ ] Set up database backups

### Post-Deployment

- [ ] Verify all services are running
- [ ] Test all critical user flows
- [ ] Monitor logs for errors
- [ ] Verify SSL certificate is valid
- [ ] Test WebSocket connections
- [ ] Performance test under load
- [ ] Set up uptime monitoring

---

## Appendix: API Reference

### Game Controller (`/api/game`)

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| POST | `/create` | Create game room | `hostName`, `avatarId?`, `hostWantsToJoin?`, `hashedPassword?`, `body: QuizSettings` |
| POST | `/join` | Join game room | `roomCode`, `playerName`, `avatarId?`, `hashedPassword?` |
| GET | `/{roomCode}` | Get room details | Path: `roomCode` |
| GET | `/rooms` | List all rooms | - |
| GET | `/players` | Get room players | `roomCode` |
| POST | `/rooms/{roomCode}/start` | Start game | Path: `roomCode`, `hashedPassword?` |
| POST | `/rooms/{roomCode}/disband` | Delete room | Path: `roomCode`, `hashedPassword?` |
| POST | `/rooms/{roomCode}/leave` | Leave room | Path: `roomCode`, `playerId` |
| POST | `/answer` | Submit answer | `body: AnswerRequest` |
| GET | `/state` | Get game state | `roomCode`, `playerId` |
| GET | `/state-simple` | Get simple state | `roomCode`, `playerId` |
| POST | `/next` | Next question | `roomCode`, `hashedPassword?` |

### Quiz Controller (`/api/quiz`)

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/question` | Generate question | `level`, `optionCount` |
| GET | `/all-questions` | Get room questions | `roomCode` |

### Word Controller (`/api/words`)

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/` | Get all words | - |
| GET | `/by-level` | Get words by level | `level` |
| GET | `/by-exam` | Get words by exam | `exam` |

### Leaderboard Controller (`/api/leaderboard`)

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/` | Get leaderboard | `roomCode` |

### WebSocket (`/ws/game`)

| Event | Direction | Payload | Description |
|-------|-----------|---------|-------------|
| `connect` | Client → Server | - | Establish connection |
| `gameState` | Server → Client | `GameState` | Game state update |
| `playerJoined` | Server → Client | `Player` | New player joined |
| `playerLeft` | Server → Client | `playerId` | Player left |
| `questionStart` | Server → Client | `Question` | New question |
| `answerReveal` | Server → Client | `Answer` | Show correct answer |
| `gameEnd` | Server → Client | `Leaderboard` | Game finished |

---

## Summary

This analysis provides a complete view of the Trainvoc system architecture. The main priorities for production deployment are:

1. **Security hardening** - Remove hardcoded credentials, enable SSL, configure proper CORS
2. **Containerization** - Create Docker images for consistent deployment
3. **Infrastructure** - Set up Hostinger VPS with proper configuration
4. **Monitoring** - Implement health checks and logging

The system is well-architected and uses modern technologies. With the recommended changes, it will be production-ready for deployment on Hostinger VPS.

---

*Document generated by Claude System Architecture Analysis*
