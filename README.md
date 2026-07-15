# Trainvoc

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://www.android.com/)
[![Web](https://img.shields.io/badge/Platform-Web-4285F4?logo=googlechrome&logoColor=white)](https://trainvoc.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![TypeScript](https://img.shields.io/badge/Language-TypeScript-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

**A multi-platform vocabulary learning ecosystem**

[Features](#features) | [Architecture](#architecture) | [Getting Started](#getting-started) | [Contributing](#contributing)

</div>

---

## Overview

**Trainvoc** is a comprehensive vocabulary learning platform that combines a feature-rich Android application with a web-based multiplayer game system. Built for learners who want to master English-Turkish vocabulary through engaging, gamified experiences.

### Components

| Component | Description | Tech Stack |
|-----------|-------------|------------|
| **TrainvocClient** | Android mobile app with offline support, gamification, and 10+ learning games | Kotlin, Jetpack Compose, Room |
| **TrainvocWeb** | Web platform for real-time multiplayer vocabulary games | React, TypeScript, Vite |
| **TrainvocBackend** | Game server with REST API and WebSocket support | Java 21, Spring Boot 4, PostgreSQL |

---

## Features

### Mobile App (TrainvocClient)

- **10 Interactive Games**: Multiple choice, word scramble, flip cards, speed match, and more
- **Gamification**: Achievements, streaks, daily goals, and leaderboards
- **Offline Learning**: Full functionality without internet connection
- **Spaced Repetition**: SM-2 algorithm for optimal retention
- **CEFR Levels**: A1 to C2 vocabulary categorization
- **Cloud Sync**: Google Play Games integration
- **Accessibility**: WCAG 2.1 AA compliant, screen reader support
- **Home Screen Widgets**: Quick access to learning features

### Web Platform (TrainvocWeb)

- **Multiplayer Games**: Compete with others in real-time
- **Game Rooms**: Create or join vocabulary quiz sessions
- **Live Leaderboards**: Track scores during gameplay
- **Responsive Design**: Works on desktop and mobile browsers
- **Bilingual Support**: English and Turkish interfaces

### Backend (TrainvocBackend)

- **REST API**: Comprehensive game management endpoints
- **WebSocket**: Real-time game state synchronization
- **Dual Database**: Separate databases for game data and vocabulary
- **Room Management**: Create, join, and manage game sessions

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       User Devices                           │
│    ┌──────────────────┐        ┌──────────────────┐        │
│    │  Android App     │        │   Web Browser    │        │
│    │ (TrainvocClient) │        │  (TrainvocWeb)   │        │
│    └────────┬─────────┘        └────────┬─────────┘        │
└─────────────┼──────────────────────────┼───────────────────┘
              │                          │
              │    ┌─────────────────────┤
              │    │                     │
              ▼    ▼                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    TrainvocBackend                           │
│    ┌──────────────────┐        ┌──────────────────┐        │
│    │    REST API      │        │    WebSocket     │        │
│    │   (Spring MVC)   │        │  (Real-time)     │        │
│    └────────┬─────────┘        └────────┬─────────┘        │
│             │                           │                   │
│             └───────────┬───────────────┘                   │
│                         ▼                                   │
│              ┌──────────────────────┐                       │
│              │   Service Layer      │                       │
│              └──────────┬───────────┘                       │
└─────────────────────────┼───────────────────────────────────┘
                          │
┌─────────────────────────┼───────────────────────────────────┐
│                         ▼                                   │
│    ┌──────────────────┐        ┌──────────────────┐        │
│    │    trainvoc      │        │  trainvoc-words  │        │
│    │  (Game Data)     │        │  (Vocabulary)    │        │
│    └──────────────────┘        └──────────────────┘        │
│                    PostgreSQL                               │
└─────────────────────────────────────────────────────────────┘
```

For detailed architecture documentation, see [ARCHITECTURE.md](ARCHITECTURE.md).

---

## Getting Started

### Prerequisites

| Tool | Version | Component |
|------|---------|-----------|
| Android Studio | Latest | TrainvocClient |
| JDK | 21 (LTS) | TrainvocBackend |
| Node.js | 18+ | TrainvocWeb |
| PostgreSQL | 15+ | TrainvocBackend |

### Clone the Repository

```bash
git clone https://github.com/ahmetabdullahgultekin/Trainvoc.git
cd Trainvoc
```

### Setup Components

#### TrainvocClient (Android)

```bash
cd TrainvocClient

# Open in Android Studio
# Build -> Make Project
# Run -> Run 'app'
```

#### TrainvocWeb (React)

```bash
cd TrainvocWeb

# Install dependencies
npm install

# Start development server
npm run dev

# Open http://localhost:5173
```

#### TrainvocBackend (Spring Boot)

```bash
cd TrainvocBackend

# Setup PostgreSQL databases
createdb trainvoc
createdb trainvoc-words

# Initialize schema
psql -d trainvoc -f sql-queries/trainvoc-mp-db-for-postgre.sql
psql -d trainvoc-words -f sql-queries/trainvoc-words-db-for-postgre.sql

# Run the server
./gradlew bootRun

# API available at http://localhost:8080
```

---

## Project Structure

```
Trainvoc/
├── TrainvocClient/          # Android mobile application
│   ├── app/                 # Android app module
│   ├── docs/                # Documentation (70+ files)
│   └── CLAUDE.md            # Development guide
│
├── TrainvocWeb/             # React web application
│   ├── src/                 # React components
│   └── CLAUDE.md            # Development guide
│
├── TrainvocBackend/         # Spring Boot server
│   ├── src/                 # Java source code
│   ├── sql-queries/         # Database scripts
│   └── CLAUDE.md            # Development guide
│
├── README.md                # This file
├── ARCHITECTURE.md          # System architecture
├── CONTRIBUTING.md          # Contribution guidelines
├── LICENSE                  # MIT License
└── CLAUDE.md                # AI development guide
```

---

## Tech Stack

### TrainvocClient (Android)

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.3.20 |
| UI | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Async | Coroutines + Flow |
| Cloud | Google Play Games |

### TrainvocWeb (React)

| Category | Technology |
|----------|------------|
| Language | TypeScript 6.0.3 |
| Framework | React 19.1.0 |
| Build | Vite 8.0 |
| UI | Tailwind CSS 4 + Radix UI |
| HTTP | Axios |
| i18n | i18next |

### TrainvocBackend (Spring Boot)

| Category | Technology |
|----------|------------|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 4.1.0 |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Real-time | Spring WebSocket |
| Security | Spring Security |

---

## Documentation

| Document | Description |
|----------|-------------|
| [ARCHITECTURE.md](ARCHITECTURE.md) | System design and data flow |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute |
| [CLAUDE.md](CLAUDE.md) | AI-assisted development guide |
| [TrainvocClient/README.md](TrainvocClient/README.md) | Android app documentation |
| [docs/history/INVESTIGATION_REPORT.md](docs/history/INVESTIGATION_REPORT.md) | Codebase analysis (historical snapshot, Jan 2026) |

---

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for:

- Code of conduct
- Development workflow
- Pull request process
- Coding standards

### Quick Start

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests and linting
5. Commit (`git commit -m 'feat: add amazing feature'`)
6. Push (`git push origin feature/amazing-feature`)
7. Open a Pull Request

---

## Roadmap

The phased plan is tracked in GitHub **[Milestones](https://github.com/ahmetabdullahgultekin/Trainvoc/milestones)** and the **[project board](https://github.com/users/ahmetabdullahgultekin/projects/7)**. In short:

### Shipped / done
- [x] Core vocabulary learning (Android, offline-first)
- [x] 11 single-player games + multiplayer UI
- [x] Gamification system, stats, TTS
- [x] Multiplayer web platform + real-time game server
- [x] Clean-checkout build, honest "Learning Path", running unit-test suite
- [x] Auth: email/password **+ Google Sign-In**, email verification, session timeout
- [x] Leaderboard: local "Your Progress" (global board pending backend)
- [x] Web: **0 known vulnerabilities**

### Next (see the GitHub Milestones for the full picture)
- [ ] **Phase 0–2**: ship Android v1 to Google Play production (gated by the 14-day tester window — operator action)
- [ ] **Phase 3**: deploy + harden the backend (DTOs, API versioning, pagination, auth enforcement)
- [ ] **Phase 4**: launch the web app against the live API
- [ ] **Phase 5**: iOS (Compose Multiplatform / KMP candidate)
- [ ] **Phases 6–9**: learning depth, social, analytics, fair monetization

---

## Future / Professionalization

Beyond feature work, this section tracks the engineering and product disciplines that take Trainvoc from "works" to "professional-grade". These are cross-referenced from each component's `CLAUDE.md` and tracked as [GitHub Milestones](https://github.com/ahmetabdullahgultekin/Trainvoc/milestones).

### Security & privacy
- Enforce Firebase/JWT auth end-to-end on the backend; replace broad `permitAll` with least-privilege rules.
- Keep dependency vulnerabilities at zero across all three components (`npm audit`, OWASP / Dependabot gates); scheduled windows for breaking major bumps.
- Secrets discipline: keystore backed up off-machine and injected via CI; DB/SSL passwords only via `--env-file`, never committed.
- A documented Data Safety posture (Play Console + a public privacy policy) that matches what the app actually collects.

### Compliance & trust
- GDPR/KVKK-aligned data export + deletion paths for any account-bound data once the backend stores it.
- Transparent, honest feature copy — no "coming soon" feature pretending to be live (the discipline behind the #168 Story Mode rename).

### Accessibility & internationalization
- Maintain WCAG 2.x AA compliance already achieved on Android; extend the same bar to web.
- Keep every user-facing string in `strings.xml` (EN + TR) and the web `locales/` — never hardcode UI text. Be ready to add language pairs.

### Quality & reliability
- CI gates per component: Android unit + lint, web `tsc`/vitest/build, backend JDK-21 build + tests; branch protection on `master`/`main`.
- Drive the unit-test suite to fully green (close `#223`), add instrumentation/e2e for login, quiz, and multiplayer, and add API contract tests.
- Reproducible, signed release pipelines (no manual local signing).

### Operations & observability
- First-class structured logging, metrics, traces, and alerting before users feel pain.
- Crash/ANR reporting (Crashlytics) and error tracking (Sentry) wired into release builds.
- Documented runbooks for deploy, rollback, cert renewal, and incident response.

### Documentation as a product
- Keep `CLAUDE.md` / `README` / `CHANGELOG` per component in lockstep with the code; treat doc updates as part of "done". Legacy docs have repeatedly mis-described this project — honesty here is a feature.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Authors

**Ahmet Abdullah Gultekin**

- GitHub: [@ahmetabdullahgultekin](https://github.com/ahmetabdullahgultekin)
- LinkedIn: [Ahmet Abdullah Gultekin](https://linkedin.com/in/ahmetabdullahgultekin)

---

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI
- [React](https://react.dev/) - Web UI library
- [Spring Boot](https://spring.io/projects/spring-boot) - Java framework
- [Material Design](https://m3.material.io/) - Design system
- [Google Play Games](https://developers.google.com/games/services) - Cloud services

---

<div align="center">

**Made with dedication for language learners worldwide**

If you find this project useful, please consider giving it a star!

[Report Bug](https://github.com/ahmetabdullahgultekin/Trainvoc/issues) | [Request Feature](https://github.com/ahmetabdullahgultekin/Trainvoc/issues)

</div>


---

## More from Ahmet Abdullah Gültekin

Personal portfolio + writing: **[ahmetabdullah.gultek.in](https://ahmetabdullah.gultek.in)**
LinkedIn: **[ahmet-abdullah-gultekin](https://www.linkedin.com/in/ahmet-abdullah-gultekin)**

