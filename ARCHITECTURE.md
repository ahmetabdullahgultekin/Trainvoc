# Trainvoc System Architecture

## Table of Contents

- [Overview](#overview)
- [System Components](#system-components)
- [Data Flow](#data-flow)
- [Technology Decisions](#technology-decisions)
- [Database Design](#database-design)
- [API Design](#api-design)
- [Security Architecture](#security-architecture)
- [Scalability Considerations](#scalability-considerations)
- [Future Architecture](#future-architecture)

---

## Overview

Trainvoc is a multi-platform vocabulary learning ecosystem consisting of three main components that work together to provide both solo and multiplayer learning experiences.

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              PRESENTATION LAYER                              │
│  ┌─────────────────────────────┐    ┌─────────────────────────────┐        │
│  │       TrainvocClient        │    │        TrainvocWeb          │        │
│  │   (Android Application)     │    │    (React Application)      │        │
│  │                             │    │                             │        │
│  │  ┌───────────────────────┐  │    │  ┌───────────────────────┐  │        │
│  │  │    Jetpack Compose    │  │    │  │     React + MUI       │  │        │
│  │  │    Material 3 UI      │  │    │  │    TypeScript UI      │  │        │
│  │  └───────────────────────┘  │    │  └───────────────────────┘  │        │
│  │  ┌───────────────────────┐  │    │  ┌───────────────────────┐  │        │
│  │  │      ViewModels       │  │    │  │    React State        │  │        │
│  │  │    StateFlow/Flow     │  │    │  │    Custom Hooks       │  │        │
│  │  └───────────────────────┘  │    │  └───────────────────────┘  │        │
│  └─────────────────────────────┘    └─────────────────────────────┘        │
└────────────────────────────────────────┬────────────────────────────────────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
                    ▼                    ▼                    ▼
          ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
          │   REST API      │  │   WebSocket     │  │  Google Play    │
          │   (HTTP)        │  │   (WS)          │  │  Games Services │
          └────────┬────────┘  └────────┬────────┘  └────────┬────────┘
                   │                    │                    │
┌──────────────────┼────────────────────┼────────────────────┼────────────────┐
│                  │         SERVICE LAYER                   │                │
│                  ▼                    ▼                    │                │
│  ┌────────────────────────────────────────────────────┐    │                │
│  │                  TrainvocBackend                    │    │                │
│  │              (Spring Boot Application)              │    │                │
│  │                                                     │    │                │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │    │                │
│  │  │ Controllers │ │  Services   │ │  WebSocket  │   │    │                │
│  │  │   (REST)    │ │  (Logic)    │ │  Handlers   │   │    │                │
│  │  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘   │    │                │
│  │         │               │               │          │    │                │
│  │         └───────────────┼───────────────┘          │    │                │
│  │                         ▼                          │    │                │
│  │              ┌─────────────────────┐               │    │                │
│  │              │    Repositories     │               │    │                │
│  │              │    (Data Access)    │               │    │                │
│  │              └──────────┬──────────┘               │    │                │
│  └─────────────────────────┼──────────────────────────┘    │                │
└────────────────────────────┼───────────────────────────────┼────────────────┘
                             │                               │
┌────────────────────────────┼───────────────────────────────┼────────────────┐
│                   DATA LAYER                               │                │
│                            ▼                               ▼                │
│  ┌─────────────────────────────────────┐    ┌─────────────────────────┐    │
│  │           PostgreSQL                 │    │      Google Cloud       │    │
│  │  ┌─────────────┐ ┌─────────────┐    │    │     (Play Games)        │    │
│  │  │  trainvoc   │ │trainvoc-    │    │    │  ┌─────────────────┐    │    │
│  │  │  (Game DB)  │ │  words      │    │    │  │  Achievements   │    │    │
│  │  │             │ │(Vocabulary) │    │    │  │  Leaderboards   │    │    │
│  │  └─────────────┘ └─────────────┘    │    │  │  Cloud Save     │    │    │
│  └─────────────────────────────────────┘    │  └─────────────────┘    │    │
│                                              └─────────────────────────┘    │
│  ┌─────────────────────────────────────┐                                   │
│  │         Room Database (SQLite)       │   (Android Local Storage)        │
│  │  ┌───────────────────────────────┐  │                                   │
│  │  │   Words | Statistics | Goals  │  │                                   │
│  │  │   Achievements | Streaks      │  │                                   │
│  │  └───────────────────────────────┘  │                                   │
│  └─────────────────────────────────────┘                                   │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## System Components

### 1. TrainvocClient (Android Application)

**Purpose**: Primary learning platform for individual users

**Architecture Pattern**: MVVM + Clean Architecture

```
TrainvocClient/
├── Presentation Layer
│   ├── Screens (Jetpack Compose)
│   ├── ViewModels (StateFlow)
│   └── Components (Reusable UI)
│
├── Domain Layer
│   ├── Use Cases
│   ├── Models
│   └── Repository Interfaces
│
├── Data Layer
│   ├── Repository Implementations
│   ├── Local Data Source (Room)
│   └── Remote Data Source (Optional)
│
└── Infrastructure
    ├── Database (Room)
    ├── DI (Hilt)
    ├── Workers (WorkManager)
    └── Services (TTS, Notifications)
```

**Key Components**:

| Component | Responsibility |
|-----------|---------------|
| `ui/screen/*` | UI screens (Compose) |
| `viewmodel/*` | State management |
| `repository/*` | Data abstraction |
| `database/*` | Room database |
| `games/*` | Game logic |
| `gamification/*` | Achievements, streaks |

### 2. TrainvocWeb (React Application)

**Purpose**: Web-based multiplayer game platform

**Architecture Pattern**: Component-based with custom hooks

```
TrainvocWeb/
├── Pages (Route components)
│   ├── HomePage
│   ├── PlayLayout (Game area)
│   ├── GamePage
│   └── LeaderboardPage
│
├── Components
│   ├── Shared (Navbar, Footer, etc.)
│   └── Game (Question, Ranking, etc.)
│
├── Hooks
│   ├── useNick
│   └── useProfile
│
├── Interfaces (TypeScript types)
│
└── Locales (i18n translations)
```

**Key Components**:

| Component | Responsibility |
|-----------|---------------|
| `pages/*` | Route-level components |
| `components/shared/*` | Reusable UI |
| `components/Game*` | Game-specific UI |
| `api.ts` | Axios configuration |
| `i18n.ts` | Internationalization |

### 3. TrainvocBackend (Spring Boot)

**Purpose**: Game server and API

**Architecture Pattern**: Layered Architecture

```
TrainvocBackend/
├── Controller Layer
│   ├── GameController
│   ├── QuizController
│   ├── WordController
│   └── LeaderboardController
│
├── Service Layer
│   ├── GameService
│   ├── QuizService
│   └── LeaderboardService
│
├── Repository Layer
│   ├── GameRoomRepository
│   ├── PlayerRepository
│   └── WordRepository
│
├── Model Layer
│   ├── Entities (JPA)
│   └── DTOs
│
└── Configuration
    ├── Security
    ├── CORS
    ├── WebSocket
    └── DataSources
```

---

## Data Flow

### Solo Learning Flow (Android)

```
User Action → ViewModel → Use Case → Repository → Room DB
     ↑                                              │
     └──────────── StateFlow Update ←───────────────┘
```

### Multiplayer Game Flow

```
┌─────────┐     ┌─────────┐     ┌──────────┐     ┌──────────┐
│  Web    │────→│ Backend │────→│PostgreSQL│     │  Other   │
│ Client  │←────│ Server  │←────│ Database │     │ Clients  │
└─────────┘     └────┬────┘     └──────────┘     └────┬─────┘
                     │                                 │
                     └──────── WebSocket ─────────────┘
```

### Game State Synchronization

```
1. Host creates room → Backend generates room code
2. Players join → Backend broadcasts PLAYER_JOINED
3. Host starts → Backend sets COUNTDOWN state
4. Question phase → Backend sends QUESTION to all
5. Answer submission → Backend validates, calculates score
6. All answered → Backend sends ANSWER_REVEAL
7. Next question → Repeat steps 4-6
8. Final question → Backend sends FINISHED with rankings
```

---

## Technology Decisions

### Why Kotlin for Android?

- **Modern language features**: Coroutines, null safety, extension functions
- **Official Google support**: First-class Android language
- **Jetpack Compose compatibility**: Native UI toolkit
- **Reduced boilerplate**: Compared to Java

### Why React for Web?

- **Component reusability**: Efficient UI development
- **Large ecosystem**: MUI, Axios, i18next
- **TypeScript support**: Type safety
- **Fast development**: Vite hot reload

### Why Spring Boot for Backend?

- **Enterprise-ready**: Production-grade features
- **WebSocket support**: Real-time communication
- **JPA/Hibernate**: Database abstraction
- **Security**: Spring Security integration

### Why PostgreSQL?

- **Relational data**: Structured vocabulary and game data
- **ACID compliance**: Data integrity
- **JSON support**: Flexible data storage
- **Scalability**: Handles concurrent connections

---

## Database Design

### Android (Room SQLite)

```
┌─────────────────┐     ┌─────────────────┐
│      Word       │     │    Statistic    │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │←───→│ wordId (PK,FK)  │
│ english         │     │ correctCount    │
│ turkish         │     │ wrongCount      │
│ level           │     │ skipCount       │
│ example         │     │ lastReview      │
└─────────────────┘     └─────────────────┘
         │
         │ M:N
         ▼
┌─────────────────┐     ┌─────────────────┐
│WordExamCrossRef │     │      Exam       │
├─────────────────┤     ├─────────────────┤
│ wordId (FK)     │────→│ id (PK)         │
│ examId (FK)     │     │ name            │
└─────────────────┘     └─────────────────┘

┌─────────────────┐     ┌─────────────────┐
│  StreakTracking │     │   DailyGoals    │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │     │ id (PK)         │
│ currentStreak   │     │ wordsGoal       │
│ longestStreak   │     │ reviewsGoal     │
│ lastActivity    │     │ quizzesGoal     │
└─────────────────┘     └─────────────────┘
```

### Backend (PostgreSQL)

**trainvoc database (Game Data)**:

```
┌─────────────────┐     ┌─────────────────┐
│    GameRoom     │     │     Player      │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │←───→│ id (PK)         │
│ roomCode        │     │ room (FK)       │
│ hostName        │     │ name            │
│ currentState    │     │ score           │
│ settings (JSON) │     │ avatarId        │
└─────────────────┘     └─────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────┐
│    Question     │
├─────────────────┤
│ id (PK)         │
│ room (FK)       │
│ wordId          │
│ options (JSON)  │
│ correctAnswer   │
└─────────────────┘
```

**trainvoc-words database (Vocabulary)**:

```
Same structure as Android Room database
(Words, Exams, Statistics)
```

---

## API Design

### REST API Principles

- **Resource-based URLs**: `/api/game/rooms`, `/api/words`
- **HTTP verbs**: GET (read), POST (create), PUT (update), DELETE (remove)
- **JSON responses**: Consistent response format
- **Error handling**: Standardized error messages

### WebSocket Protocol

**Connection**: `ws://host:port/ws/game/{roomCode}`

**Message Format**:
```json
{
  "type": "MESSAGE_TYPE",
  "payload": { ... },
  "timestamp": "ISO-8601"
}
```

**Message Types**:
- `PLAYER_JOINED` / `PLAYER_LEFT`
- `GAME_STARTED` / `GAME_FINISHED`
- `QUESTION` / `ANSWER_REVEAL`
- `SCORE_UPDATE`

---

## Security Architecture

### Current State

| Area | Status | Notes |
|------|--------|-------|
| Authentication | Missing | No user auth |
| Authorization | Missing | Open endpoints |
| HTTPS | Configurable | SSL disabled in dev |
| Input Validation | Partial | Basic validation |
| CORS | Permissive | Allows all origins |

### Recommended Security Model

```
┌─────────────────────────────────────────────────────────────┐
│                    Security Layers                           │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              API Gateway / Load Balancer             │   │
│  │         (Rate Limiting, DDoS Protection)            │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │                                   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                  TLS Termination                     │   │
│  │              (HTTPS/WSS Encryption)                  │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │                                   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │               Authentication Layer                   │   │
│  │            (JWT / OAuth2 / Session)                  │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │                                   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │               Authorization Layer                    │   │
│  │           (Role-based Access Control)                │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │                                   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │               Input Validation                       │   │
│  │          (Bean Validation / Sanitization)            │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Scalability Considerations

### Current Limitations

1. **Single Server**: No horizontal scaling
2. **In-Memory State**: Game state not distributed
3. **Database Bottleneck**: Single PostgreSQL instance
4. **WebSocket Scaling**: Single server connections

### Scaling Strategy

```
                    ┌─────────────────┐
                    │  Load Balancer  │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
    ┌──────────┐       ┌──────────┐       ┌──────────┐
    │ Server 1 │       │ Server 2 │       │ Server N │
    └────┬─────┘       └────┬─────┘       └────┬─────┘
         │                  │                  │
         └──────────────────┼──────────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
        ┌──────────┐  ┌──────────┐  ┌──────────┐
        │  Redis   │  │ Primary  │  │ Replica  │
        │ (Cache)  │  │ Postgres │  │ Postgres │
        └──────────┘  └──────────┘  └──────────┘
```

### Required Changes for Scale

1. **Redis for Sessions**: Shared session storage
2. **Redis Pub/Sub**: WebSocket message broadcasting
3. **Database Replication**: Read replicas
4. **Containerization**: Docker + Kubernetes
5. **CDN**: Static asset delivery

---

## Future Architecture

### Planned Enhancements

1. **Microservices Migration**
   - Game Service
   - User Service
   - Vocabulary Service
   - Notification Service

2. **Event-Driven Architecture**
   - Message queue (Kafka/RabbitMQ)
   - Event sourcing for game state
   - CQRS for read/write optimization

3. **Cloud-Native Deployment**
   - Kubernetes orchestration
   - Auto-scaling
   - Service mesh (Istio)

### Target Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           API Gateway                                    │
│                    (Authentication, Rate Limiting)                       │
└──────────────────────────────┬──────────────────────────────────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
         ▼                     ▼                     ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│  Game Service   │   │  User Service   │   │ Vocab Service   │
│  (Multiplayer)  │   │ (Auth, Profile) │   │   (Words)       │
└────────┬────────┘   └────────┬────────┘   └────────┬────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               │
                    ┌──────────┴──────────┐
                    │    Message Queue    │
                    │   (Kafka/RabbitMQ)  │
                    └──────────┬──────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
         ▼                     ▼                     ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│   PostgreSQL    │   │     Redis       │   │ Elasticsearch   │
│   (Primary)     │   │   (Cache/PubSub)│   │   (Search)      │
└─────────────────┘   └─────────────────┘   └─────────────────┘
```

---

## Component Communication

### Sequence Diagram: Multiplayer Game

```
Client A          Backend           Database         Client B
    │                │                  │                │
    │──Create Room──→│                  │                │
    │                │──Save Room──────→│                │
    │←─Room Code─────│                  │                │
    │                │                  │                │
    │                │                  │                │──Join Room──→
    │                │←─Join Request────│                │
    │                │──Validate────────→│                │
    │←─PLAYER_JOINED─│──────────────────│─PLAYER_JOINED─→│
    │                │                  │                │
    │──Start Game───→│                  │                │
    │                │──Generate Quiz──→│                │
    │←─COUNTDOWN─────│──────────────────│─COUNTDOWN─────→│
    │                │                  │                │
    │←─QUESTION──────│──────────────────│─QUESTION──────→│
    │                │                  │                │
    │──Answer───────→│                  │                │
    │                │──Calculate Score─│                │
    │                │                  │────Answer─────→│
    │                │──Calculate Score─│                │
    │                │                  │                │
    │←─ANSWER_REVEAL─│──────────────────│─ANSWER_REVEAL─→│
    │                │                  │                │
```

---

## Related Documentation

- [README.md](README.md) - Project overview
- [CONTRIBUTING.md](CONTRIBUTING.md) - How to contribute
- [CLAUDE.md](CLAUDE.md) - AI development guide
- [TrainvocClient/docs/ARCHITECTURE.md](TrainvocClient/docs/ARCHITECTURE.md) - Android architecture

---

*Last Updated: January 22, 2026*
