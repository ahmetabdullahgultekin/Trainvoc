# TrainvocBackend - Claude AI Development Guide

## Overview

**TrainvocBackend** is a Spring Boot-based game server providing REST API and WebSocket support for the Trainvoc multiplayer vocabulary game platform. It manages game rooms, players, quizzes, and real-time game state.

> **Status (2026-06-05):** **Migrated to Spring Boot 4.0.6 + Jackson 3 + springdoc 3 on JDK 21 LTS** (`migrate/backend-spring-boot-4-2026-06-05`) — drops the EOL JDK 24 toolchain (Spring Boot 3.5 OSS support ends 2026-06-30) and makes the build verifiable on the JDK-21 host. `./gradlew clean build -x test` is **BUILD SUCCESSFUL** and the bootJar assembles; **the test suite is fully green since 2026-07-15 (PR #106): 230 tests / 0 failures / 10 skipped (@Disabled integration/performance tags) and the suite is BLOCKING in CI** (the former 20 pre-existing failures — unwired EntityManagerFactory mocks in RoomServiceTest, missing @MockitoBean's in GameControllerTest, one assertion drift in QuizControllerTest — are fixed). This closes held majors **Spring Boot 4 (#25)** and **springdoc 3 (#23)**. **Gradle wrapper 9.4.1 (#16) verified + shipped 2026-06-06** on `feat/srs-engine` — SB4 `clean build` is SUCCESSFUL on Gradle 9.4.1 and the full test suite behaves identically (same 20 pre-existing failures, 0 new); stale conflicting PR #16 retired. Safe non-major bumps already merged on `dev/2026-06-05` — postgresql 42.7.10, org.json 20251224, jjwt 0.13.0, firebase-admin 9.8.0, caffeine 3.2.3. Not yet deployed/hardened — see `../ROADMAP.md` Phase 3 (DTO layer, API versioning, pagination, auth enforcement).
>
> **Spring Boot 4 package moves applied** (verified against the resolved 4.0.6 jars): `JpaProperties` → `org.springframework.boot.jpa.autoconfigure`; `HibernateProperties`/`HibernateSettings` → `org.springframework.boot.hibernate.autoconfigure`; `ConfigurableServletWebServerFactory` → `org.springframework.boot.web.server.servlet`. Tests: `@WebMvcTest`/`@AutoConfigureMockMvc` → `org.springframework.boot.webmvc.test.autoconfigure.*` (needs the new `spring-boot-starter-webmvc-test` test dependency); `@MockBean` → `@MockitoBean` (`org.springframework.test.context.bean.override.mockito`). Jackson annotations (`@JsonManagedReference`/`@JsonBackReference`) stay on `com.fasterxml.jackson.annotation` (the jackson-annotations module is the documented exception to the `tools.jackson` rename), so no source change was needed for them.

---

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Framework** | Spring Boot | 4.0.6 |
| **Language** | Java | 21 (LTS) |
| **Build Tool** | Gradle | 9.4.1 (wrapper) |
| **Database** | PostgreSQL | 15+ |
| **ORM** | Spring Data JPA / Hibernate | Latest |
| **Security** | Spring Security | Latest |
| **WebSocket** | Spring WebSocket | Latest |
| **JSON** | org.json | 20240303 |
| **Utilities** | Lombok | Latest |
| **Testing** | JUnit 5 | Latest |

---

## Project Structure

```
TrainvocBackend/
├── src/
│   ├── main/
│   │   ├── java/com/rollingcatsoftware/trainvocmultiplayerapplication/
│   │   │   ├── config/                    # Spring configurations
│   │   │   │   ├── CorsConfig.java        # Environment-based CORS
│   │   │   │   ├── GameConstants.java     # Centralized game constants
│   │   │   │   ├── PrimaryDataSourceConfig.java  # Main DB config
│   │   │   │   ├── RateLimitingConfig.java # Rate limiting (bucket4j)
│   │   │   │   ├── SecondDataSourceConfig.java   # Words DB config
│   │   │   │   ├── SecurityConfig.java    # Security settings
│   │   │   │   └── WebSocketConfig.java   # WebSocket setup
│   │   │   ├── controller/                # REST controllers
│   │   │   │   ├── GameController.java    # Game/room management
│   │   │   │   ├── LeaderboardController.java
│   │   │   │   ├── QuizController.java    # Quiz operations
│   │   │   │   └── WordController.java    # Word CRUD
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   └── AnswerRequest.java     # Answer submission DTO
│   │   │   ├── exception/                 # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── RoomPasswordException.java
│   │   │   ├── model/                     # JPA Entities
│   │   │   │   ├── Answer.java
│   │   │   │   ├── AnswerHistory.java
│   │   │   │   ├── Exam.java
│   │   │   │   ├── GameRoom.java          # Game room entity
│   │   │   │   ├── GameState.java         # Game state enum
│   │   │   │   ├── Player.java            # Player entity
│   │   │   │   ├── PlayerAnswer.java
│   │   │   │   ├── Question.java
│   │   │   │   ├── QuizQuestion.java
│   │   │   │   ├── QuizSettings.java      # Quiz configuration
│   │   │   │   ├── Statistic.java
│   │   │   │   ├── Word.java              # Vocabulary word
│   │   │   │   └── WordExamCrossRef.java  # Word-Exam relation
│   │   │   ├── pattern/                   # Design patterns
│   │   │   │   └── state/                 # State pattern for game states
│   │   │   │       ├── GameStateHandler.java      # State handler interface
│   │   │   │       ├── GameStateMachine.java      # State machine coordinator
│   │   │   │       ├── LobbyStateHandler.java     # Waiting state
│   │   │   │       ├── CountdownStateHandler.java # Countdown state
│   │   │   │       ├── QuestionStateHandler.java  # Question state
│   │   │   │       ├── AnswerRevealStateHandler.java # Answer reveal state
│   │   │   │       ├── RankingStateHandler.java   # Ranking state
│   │   │   │       └── FinalStateHandler.java     # Game finished state
│   │   │   ├── repository/                # Data access layer
│   │   │   │   ├── GameRoomRepository.java
│   │   │   │   ├── PlayerAnswerRepository.java
│   │   │   │   ├── PlayerRepository.java
│   │   │   │   ├── QuestionRepository.java
│   │   │   │   └── word/                  # Words DB repositories
│   │   │   │       ├── ExamRepository.java
│   │   │   │       ├── WordExamCrossRefRepository.java
│   │   │   │       └── WordRepository.java
│   │   │   ├── service/                   # Business logic (SRP-compliant)
│   │   │   │   ├── GameService.java       # Facade/orchestrator
│   │   │   │   ├── GameStateService.java  # Game state calculations
│   │   │   │   ├── IRoomService.java      # Room service interface (DIP)
│   │   │   │   ├── IPlayerService.java    # Player service interface (DIP)
│   │   │   │   ├── RoomService.java       # Room CRUD operations
│   │   │   │   ├── PlayerService.java     # Player management
│   │   │   │   ├── RoomPasswordService.java # Password validation
│   │   │   │   ├── LeaderboardService.java
│   │   │   │   ├── PlayerAnswerService.java
│   │   │   │   ├── QuizService.java       # Quiz generation
│   │   │   │   └── RoomCleanupService.java # Scheduled cleanup
│   │   │   ├── websocket/                 # WebSocket handlers
│   │   │   │   ├── GameWebSocketHandler.java
│   │   │   │   └── handler/               # Message handlers (Strategy pattern)
│   │   │   │       ├── WebSocketMessageHandler.java  # Handler interface
│   │   │   │       ├── WebSocketContext.java         # Session management
│   │   │   │       ├── MessageDispatcher.java        # Message routing
│   │   │   │       ├── CreateRoomHandler.java        # Room creation
│   │   │   │       └── JoinRoomHandler.java          # Room joining
│   │   │   └── TrainvocMultiplayerApplication.java  # Main entry
│   │   └── resources/
│   │       ├── application.properties     # Environment-based config
│   │       ├── application-dev.properties # Development profile
│   │       ├── keystore.p12              # SSL keystore
│   │       └── static/                   # Static files
│   └── test/
│       └── java/                         # Unit tests
├── sql-queries/                          # Database scripts
│   ├── trainvoc-backup.sql
│   ├── trainvoc-words-backup.sql
│   ├── trainvoc-mp-db-for-postgre.sql
│   └── trainvoc-words-db-for-postgre.sql
├── .env.example                          # Environment variable template
├── Dockerfile                            # Docker build config
├── build.gradle                          # Build configuration
├── settings.gradle                       # Project settings
├── gradlew                               # Gradle wrapper
├── kill-port.bat                         # Windows port killer
└── CLAUDE.md                             # This file
```

---

## Database Architecture

### Dual Database Setup

The application uses **two PostgreSQL databases**:

| Database | Purpose | Repository Package |
|----------|---------|-------------------|
| `trainvoc` | Game data (rooms, players, scores) | `repository/*` |
| `trainvoc-words` | Vocabulary data (words, exams) | `repository/word/*` |

### Configuration

```properties
# Primary Database (trainvoc)
spring.datasource.url=jdbc:postgresql://localhost:5432/trainvoc
spring.datasource.username=postgres
spring.datasource.password=<your-password>

# Secondary Database (trainvoc-words)
spring.second-datasource.url=jdbc:postgresql://localhost:5432/trainvoc-words
spring.second-datasource.username=postgres
spring.second-datasource.password=<your-password>
```

### Key Entities

**Primary Database (trainvoc):**

| Entity | Table | Description |
|--------|-------|-------------|
| `GameRoom` | `game_rooms` | Multiplayer game sessions |
| `Player` | `players` | Players in game rooms |
| `PlayerAnswer` | `player_answers` | Answer submissions |
| `Question` | `questions` | Quiz questions per game |

**Secondary Database (trainvoc-words) — relational multilingual schema v18 (#96 PR-B):**

Entities live in their OWN package `words.model` (a sibling of `model`, NOT a subpackage —
`SecondDataSourceConfig.setPackagesToScan` is recursive, so a subpackage would map the word
entities into BOTH persistence units, the ghost-table / double-validation hazard the re-key
removes). `SecondDataSourceConfig` scans `words.model`; `PrimaryDataSourceConfig` keeps `model`.

| Entity (`words.model`) | Table | Key | Description |
|------------------------|-------|-----|-------------|
| `Language` | `languages` | `Long id` (assigned) | Learning language; en=1, tr=2; `code` unique |
| `Word` | `words` | `Long id` (assigned) | A lemma in one language; `UNIQUE(lemma, language_id)`; `meaning` is a display cache; every language is first-class (5,466 EN + 5,074 TR rows) |
| `WordTranslation` | `word_translations` | `(word_id, translated_word_id, sense_index)` | Directed EN→TR sense-grouped edges |
| `Synonym` | `synonyms` | `(word_id, synonym_word_id)` | Same-language pairs, stored once with `word_id < synonym_word_id` |
| `Exam` | `exams` | `String exam` | Exam categories (TOEFL, IELTS, YDS, YÖKDİL, KPDS, Mixed) |
| `WordExamCrossRef` | `word_exam_cross_ref` | `(Long word_id, String exam)` | Word↔exam membership (id-keyed; manifest ships YDS only) |

Entities are deliberately `@ManyToOne`-free (plain FK columns) so `/api/words` JSON stays flat;
real PostgreSQL FKs live in `sql-queries/trainvoc-words-db-for-postgre.sql` (DDL-only now — the
old ~12k-line INSERT dump was retired). **Ids are opaque and permanent; holes are legal — never
renumber or assume contiguity.** The dead `Statistic` entity + `Word.statId` were removed.

**Seeding:** `service/seed/WordSeedImporter` (an `ApplicationRunner`) loads
`classpath:seed/seed_v18.json` at boot (copied from the client asset by a Gradle
`processResources` task — single source of truth, no committed copy), asserts
`manifestVersion==1 && dbVersion==18`, then `WordSeedService` (transaction on
`secondTransactionManager`) inserts in FK order: languages → exams → words → translations →
synonyms → word-exam. **Idempotent** (fast-path skip when word+language counts already match) and
**tolerant of an absent manifest** (a jar built without the client tree — e.g. the Docker build
context is `./TrainvocBackend` only — logs a warning and skips; wiring the manifest into the
compose deploy is a tracked follow-up).

---

## REST API Endpoints

### Game Controller (`/api/game`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/create` | Create new game room |
| `POST` | `/join` | Join existing room |
| `GET` | `/{roomCode}` | Get room details |
| `GET` | `/rooms` | List all rooms |
| `GET` | `/players` | Get room players |
| `POST` | `/rooms/{roomCode}/start` | Start the game |
| `POST` | `/rooms/{roomCode}/disband` | Delete room |
| `POST` | `/rooms/{roomCode}/leave` | Leave room |
| `POST` | `/answer` | Submit answer |
| `GET` | `/state` | Get game state |
| `GET` | `/state-simple` | Get simple state |
| `POST` | `/next` | Go to next question |

### Quiz Controller (`/api/quiz`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/questions` | Get quiz questions |
| `POST` | `/generate` | Generate new quiz |

### Word Controller (`/api/words`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | List all words |
| `GET` | `/{id}` | Get word by ID |
| `GET` | `/level/{level}` | Words by CEFR level |
| `GET` | `/exam/{examId}` | Words by exam type |

### Leaderboard Controller (`/api/leaderboard`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Global leaderboard |
| `GET` | `/room/{roomCode}` | Room leaderboard |

### SRS Controller (`/api/v1/srs`) — **authenticated**

Cross-device sync for the spaced-repetition (FSRS) engine (S4 of
`../docs/design/srs-spaced-repetition-engine.md`). The FSRS scheduling algorithm
runs on the **Android client**; these endpoints only persist/serve the resulting
schedule. Conflict resolution is **last-write-wins on `clientUpdatedAt`** (the
client is the source of truth). Timestamps in the wire contract are epoch-ms.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/reviews` | Batch upsert (≤500 rows) of dirty schedule rows → `204` |
| `GET` | `/schedule` | Pull full schedule + `totalDue`/`nextDueAt` to seed a new device |

Backed by `SrsController` → `SrsService` → `SrsScheduleRepository` (`SrsSchedule`
entity, additive `srs_schedule` table in the **primary** DB, Hibernate `ddl-auto`).
DTOs are records under `dto/srs/`. Tests: `SrsServiceTest` (8, Mockito) +
`SrsControllerTest` (6, `@WebMvcTest` / `MockMvc`).

---

## Game State Machine

```
         WAITING
            │
            ▼ (host starts)
       COUNTDOWN (3s)
            │
            ▼
    ┌─── QUESTION ◄──────┐
    │       │             │
    │       ▼ (all answered / timeout)
    │  ANSWER_REVEAL      │
    │       │             │
    │       ▼ (has more)  │
    │       └─────────────┘
    │       │
    │       ▼ (no more questions)
    └─► FINISHED
```

### GameState Enum

```java
public enum GameState {
    WAITING,        // Lobby, waiting for players
    COUNTDOWN,      // 3-second countdown before start
    QUESTION,       // Showing question, accepting answers
    ANSWER_REVEAL,  // Showing correct answer and scores
    FINISHED        // Game completed
}
```

---

## WebSocket Support

### Configuration

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler(), "/ws/game/{roomCode}")
                .setAllowedOrigins("*");
    }
}
```

### Connection URL

```
ws://localhost:8080/ws/game/{roomCode}
```

### Message Types

| Type | Direction | Purpose |
|------|-----------|---------|
| `PLAYER_JOINED` | Server → Client | New player notification |
| `PLAYER_LEFT` | Server → Client | Player left notification |
| `GAME_STARTED` | Server → Client | Game has started |
| `QUESTION` | Server → Client | New question |
| `ANSWER_REVEAL` | Server → Client | Show answer |
| `GAME_FINISHED` | Server → Client | Game ended |

---

## Development Commands

```bash
# Start development server (port 8080)
./gradlew bootRun

# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Create executable JAR
./gradlew bootJar
```

---

## Configuration

### application.properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | HTTP port |
| `server.ssl.enabled` | false | SSL/TLS |
| `spring.datasource.url` | localhost:5432/trainvoc | Main DB |
| `spring.second-datasource.url` | localhost:5432/trainvoc-words | Words DB |
| `spring.jpa.hibernate.ddl-auto` | update | Schema management |
| `spring.jpa.show-sql` | true | Log SQL queries |

### Security Configuration

Currently configured to permit all requests (development mode):

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}
```

---

## Known Issues

### Critical

1. ~~**Hardcoded Credentials in application.properties**~~ ✅ FIXED
   - ~~Database passwords visible in source~~
   - ~~AWS RDS credentials in comments~~
   - ~~SSL keystore password exposed~~
   - **Status**: Now uses environment variables via `${DB_PASSWORD}`, etc.

2. **No Authentication/Authorization** (Deferred)
   - All endpoints publicly accessible
   - No user authentication
   - Room passwords are only hashed, not properly secured
   - **Status**: Requires user management system first

3. ~~**CORS Allows All Origins**~~ ✅ FIXED
   - ~~`setAllowedOrigins("*")` is too permissive~~
   - **Status**: Now uses `CORS_ALLOWED_ORIGINS` environment variable

### High

4. ~~**No Rate Limiting**~~ ✅ FIXED
   - ~~API vulnerable to abuse~~
   - **Status**: Implemented with bucket4j in `RateLimitingConfig.java`

5. ~~**No Input Validation**~~ ✅ FIXED
   - ~~Limited validation on request parameters~~
   - **Status**: Added Jakarta Bean Validation with `@Valid` annotations

6. **Missing Error Handling** (Partially Fixed)
   - ~~Some endpoints return raw exceptions~~
   - **Status**: `GlobalExceptionHandler` handles validation errors
   - **Remaining**: Standardize all error responses

### Medium

7. **No API Documentation**
   - No Swagger/OpenAPI
   - Endpoints not documented

8. **Hardcoded Turkish Comments**
   - Mixed language in code comments
   - Should use English consistently

9. **No Logging Strategy**
   - Minimal logging
   - No request/response logging

---

## Security Recommendations

### Immediate Actions

1. **Environment Variables for Secrets**
   ```properties
   spring.datasource.password=${DB_PASSWORD}
   server.ssl.key-store-password=${SSL_PASSWORD}
   ```

2. **Proper CORS Configuration**
   ```java
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration config = new CorsConfiguration();
       config.setAllowedOrigins(List.of(
           "https://trainvoc.com",
           "http://localhost:5173"
       ));
       config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
       config.setAllowCredentials(true);
       // ...
   }
   ```

3. **Add Rate Limiting**
   - Use Spring Boot rate limiting
   - Or integrate with API Gateway

### Future Security

- Implement JWT authentication
- Add user registration/login
- Use proper password hashing (BCrypt)
- Add input validation (Bean Validation)
- Implement audit logging

---

## Database Setup

### Create Databases

```bash
# PostgreSQL commands
createdb trainvoc
createdb trainvoc-words

# Or using psql
psql -U postgres -c "CREATE DATABASE trainvoc;"
psql -U postgres -c "CREATE DATABASE \"trainvoc-words\";"
```

### Initialize Schema

```bash
# Run SQL scripts
psql -U postgres -d trainvoc -f sql-queries/trainvoc-mp-db-for-postgre.sql
psql -U postgres -d trainvoc-words -f sql-queries/trainvoc-words-db-for-postgre.sql
```

### Sample Data

```bash
# Load backup data
psql -U postgres -d trainvoc -f sql-queries/trainvoc-backup.sql
psql -U postgres -d trainvoc-words -f sql-queries/trainvoc-words-backup.sql
```

---

## Architecture Patterns

### Layered Architecture

```
Controller Layer (REST endpoints)
        │
        ▼
Service Layer (Business logic)
        │
        ▼
Repository Layer (Data access)
        │
        ▼
Database Layer (PostgreSQL)
```

### Dependency Injection

Uses Spring's constructor injection:

```java
@RestController
public class GameController {
    private final GameService gameService;
    private final PlayerRepository playerRepo;

    public GameController(GameService gameService, PlayerRepository playerRepo) {
        this.gameService = gameService;
        this.playerRepo = playerRepo;
    }
}
```

---

## Design Patterns Implemented

### State Pattern (Game States)

The game state machine uses the State pattern for extensibility:

```java
public interface GameStateHandler {
    GameState getState();
    int calculateRemainingTime(GameRoom room, long elapsedSeconds);
    int getStateDuration(GameRoom room);
    GameState getNextState();
    boolean hasAutomaticTransition();
}
```

State handlers: `LobbyStateHandler`, `CountdownStateHandler`, `QuestionStateHandler`, `AnswerRevealStateHandler`, `RankingStateHandler`, `FinalStateHandler`

### Strategy Pattern (WebSocket Messages)

WebSocket messages are handled via the Strategy pattern:

```java
public interface WebSocketMessageHandler {
    String getMessageType();
    void handle(WebSocketSession session, JSONObject message, WebSocketContext context);
}
```

`MessageDispatcher` routes messages to appropriate handlers.

### Service Layer (SRP Compliance)

Services are split by responsibility:

| Service | Responsibility |
|---------|---------------|
| `GameService` | Facade/orchestrator |
| `RoomService` | Room CRUD operations |
| `PlayerService` | Player management |
| `RoomPasswordService` | Password validation |
| `GameStateService` | State calculations |

---

## File Count Summary

| Type | Count |
|------|-------|
| Java Classes | 55+ |
| Configuration | 8 |
| SQL Scripts | 4+ |
| Test Classes | 1 |

---

## Related Documentation

- **Root CLAUDE.md**: `/CLAUDE.md` - Monorepo overview
- **Web CLAUDE.md**: `/TrainvocWeb/CLAUDE.md` - Frontend docs
- **Architecture**: `/ARCHITECTURE.md` - System design
- **SQL Queries**: `/TrainvocBackend/sql-queries/` - Database scripts
- **Changelog**: `/CHANGELOG.md` - Version history
- **Master Fix Plan**: `/MASTER_FIX_PLAN.md` - Issue tracking

---

*Last Updated: January 22, 2026*
