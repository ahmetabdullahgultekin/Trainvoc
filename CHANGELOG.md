# Changelog

All notable changes to the Trainvoc project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

*For detailed progress tracking, see [MASTER_FIX_PLAN.md](./MASTER_FIX_PLAN.md)*
