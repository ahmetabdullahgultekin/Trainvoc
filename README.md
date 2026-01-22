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
| **TrainvocBackend** | Game server with REST API and WebSocket support | Java 24, Spring Boot, PostgreSQL |

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
| JDK | 24+ | TrainvocBackend |
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
| Language | Kotlin 2.1.10 |
| UI | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Async | Coroutines + Flow |
| Cloud | Google Play Games |

### TrainvocWeb (React)

| Category | Technology |
|----------|------------|
| Language | TypeScript 5.8.3 |
| Framework | React 19.1.0 |
| Build | Vite 6.3.5 |
| UI | Material-UI 7.1.2 |
| HTTP | Axios |
| i18n | i18next |

### TrainvocBackend (Spring Boot)

| Category | Technology |
|----------|------------|
| Language | Java 24 |
| Framework | Spring Boot 3.5.0 |
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
| [INVESTIGATION_REPORT.md](INVESTIGATION_REPORT.md) | Codebase analysis |

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

### Current Status

- [x] Core vocabulary learning (Android)
- [x] 10 interactive games
- [x] Gamification system
- [x] Multiplayer web platform
- [x] Real-time game server
- [x] Offline mode

### Upcoming

- [ ] iOS application
- [ ] Additional language pairs
- [ ] Social features
- [ ] Premium subscription
- [ ] Advanced analytics

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
