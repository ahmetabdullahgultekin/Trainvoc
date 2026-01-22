# TrainvocBackend - Claude AI Development Guide

## Overview

**TrainvocBackend** is a Spring Boot-based game server providing REST API and WebSocket support for the Trainvoc multiplayer vocabulary game platform. It manages game rooms, players, quizzes, and real-time game state.

---

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Framework** | Spring Boot | 3.5.0 |
| **Language** | Java | 24 |
| **Build Tool** | Gradle | Latest |
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
│   │   │   │   ├── CorsConfig.java        # CORS settings
│   │   │   │   ├── PrimaryDataSourceConfig.java  # Main DB config
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
│   │   │   ├── repository/                # Data access layer
│   │   │   │   ├── GameRoomRepository.java
│   │   │   │   ├── PlayerAnswerRepository.java
│   │   │   │   ├── PlayerRepository.java
│   │   │   │   ├── QuestionRepository.java
│   │   │   │   └── word/                  # Words DB repositories
│   │   │   │       ├── ExamRepository.java
│   │   │   │       ├── WordExamCrossRefRepository.java
│   │   │   │       └── WordRepository.java
│   │   │   ├── service/                   # Business logic
│   │   │   │   ├── GameService.java       # Game management
│   │   │   │   ├── LeaderboardService.java
│   │   │   │   ├── PlayerAnswerService.java
│   │   │   │   ├── QuizService.java       # Quiz generation
│   │   │   │   └── RoomCleanupService.java # Scheduled cleanup
│   │   │   ├── websocket/                 # WebSocket handlers
│   │   │   │   └── GameWebSocketHandler.java
│   │   │   └── TrainvocMultiplayerApplication.java  # Main entry
│   │   └── resources/
│   │       ├── application.properties     # App configuration
│   │       ├── keystore.p12              # SSL keystore
│   │       └── static/                   # Static files
│   └── test/
│       └── java/                         # Unit tests
├── sql-queries/                          # Database scripts
│   ├── trainvoc-backup.sql
│   ├── trainvoc-words-backup.sql
│   ├── trainvoc-mp-db-for-postgre.sql
│   └── trainvoc-words-db-for-postgre.sql
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

**Secondary Database (trainvoc-words):**

| Entity | Table | Description |
|--------|-------|-------------|
| `Word` | `words` | Vocabulary words (EN-TR) |
| `Exam` | `exams` | Exam categories |
| `WordExamCrossRef` | `word_exam_cross_ref` | Word-Exam relations |
| `Statistic` | `statistics` | Word statistics |

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

1. **Hardcoded Credentials in application.properties**
   - Database passwords visible in source
   - AWS RDS credentials in comments
   - SSL keystore password exposed
   - **Fix**: Use environment variables or secrets management

2. **No Authentication/Authorization**
   - All endpoints publicly accessible
   - No user authentication
   - Room passwords are only hashed, not properly secured

3. **CORS Allows All Origins**
   - `setAllowedOrigins("*")` is too permissive
   - Should restrict to specific domains in production

### High

4. **No Rate Limiting**
   - API vulnerable to abuse
   - Could be DDoS'd easily

5. **No Input Validation**
   - Limited validation on request parameters
   - Potential for injection attacks

6. **Missing Error Handling**
   - Some endpoints return raw exceptions
   - Should use consistent error responses

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

## File Count Summary

| Type | Count |
|------|-------|
| Java Classes | 39 |
| Configuration | 5 |
| SQL Scripts | 4+ |
| Test Classes | 1 |

---

## Related Documentation

- **Root CLAUDE.md**: `/CLAUDE.md` - Monorepo overview
- **Web CLAUDE.md**: `/TrainvocWeb/CLAUDE.md` - Frontend docs
- **Architecture**: `/ARCHITECTURE.md` - System design
- **SQL Queries**: `/TrainvocBackend/sql-queries/` - Database scripts

---

*Last Updated: January 22, 2026*
