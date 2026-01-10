# Trainvoc

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-757575?logo=material-design&logoColor=white)](https://m3.material.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)
[![Version](https://img.shields.io/badge/version-1.1.2-blue.svg)](CHANGELOG.md)

**A modern, gamified English-Turkish vocabulary learning application**

[Features](#-features) • [Screenshots](#-screenshots) • [Installation](#-installation) • [Architecture](#-architecture) • [Contributing](#-contributing)

</div>

---

## 📖 Table of Contents

- [About](#-about)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Installation](#-installation)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)
- [Authors](#-authors)
- [Contact](#-contact)
- [Acknowledgments](#-acknowledgments)

---

## 📚 About

**Trainvoc** is a comprehensive vocabulary training application designed to help users master English-Turkish vocabulary through engaging, gamified learning experiences. Built with modern Android development practices, Trainvoc combines proven learning techniques with an intuitive user interface to make language learning effective and enjoyable.

### Why Trainvoc?

- **Science-backed learning**: Implements Spaced Repetition System (SRS) algorithm for optimal retention
- **Gamified experience**: 44 achievements, streaks, daily goals, and 10 diverse memory games
- **Offline-first**: Full functionality without internet connection
- **Cloud sync**: Google Play Games integration for cross-device progress sync
- **Completely customizable**: Flexible quiz types, difficulty levels, and learning paths
- **Privacy-focused**: Local data storage with optional cloud backup

---

## ✨ Features

### 🎮 10 Interactive Memory Games

1. **Multiple Choice** - Classic quiz with difficulty scaling and SM-2 algorithm
2. **Fill in the Blank** - Context-based learning with sentence completion
3. **Word Scramble** - Letter rearrangement with hint system
4. **Flip Cards** - Memory matching game with multiple grid sizes
5. **Speed Match** - Time-based matching with combo system
6. **Listening Quiz** - Audio-based learning with TTS integration
7. **Picture Match** - Visual learning with image associations
8. **Spelling Challenge** - Real-time spelling validation
9. **Translation Race** - 90-second rapid-fire translation with APM tracking
10. **Context Clues** - Reading comprehension with contextual hints

### 🏆 Gamification System

- **Streak Tracking**: Build consecutive learning days (up to 365+ days)
- **Daily Goals**: Customizable targets for words, reviews, quizzes, and time
- **44 Achievements**: Bronze to Diamond tiers across 8 categories
  - Streak achievements (3-365 days)
  - Words learned (10-5,000 words)
  - Quiz completion (10-500 quizzes)
  - Perfect scores (10-100 perfect runs)
  - Daily goal milestones
  - Time spent learning
  - Special achievements (Early Bird, Night Owl, Speed Demon, etc.)
- **Progress Dashboard**: Comprehensive statistics and learning analytics

### 🎯 Core Learning Features

- **Adaptive Quiz Types**: 9 different quiz algorithms
  - Random, Least/Most Correct, Least/Most Wrong
  - Least/Most Reviewed, Least/Most Recent
- **Word Categorization**:
  - 6 CEFR levels (A1, A2, B1, B2, C1, C2)
  - Exam-based grouping (TOEFL, IELTS, etc.)
- **Smart Statistics**: Track correct, wrong, and skipped answers per word
- **Spaced Repetition**: SM-2 algorithm for optimal review scheduling

### ☁️ Cloud & Sync

- **Google Play Games Integration**:
  - Cloud save/load across devices
  - Achievement sync
  - Leaderboards support
  - Automatic conflict resolution
- **Secure Backup**: Encrypted local and cloud backups
- **Offline Mode**: Full functionality without internet

### 🎨 Modern UI/UX

- **Material 3 Design**: Beautiful, adaptive theming
- **Dark/Light Modes**: System-responsive themes
- **Smooth Animations**: Lottie animations and Compose animations
- **Accessibility**: Screen reader support, haptic feedback
- **Responsive Layouts**: Optimized for all screen sizes

### 📱 Home Screen Widgets

- **Word of the Day**: Daily vocabulary widget
- **Daily Progress**: Track today's learning goals
- **Streak Counter**: Display current learning streak
- **Quick Quiz**: Launch quiz directly from home screen

### 🔔 Smart Notifications

- **Daily Reminders**: Customizable learning reminders
- **Streak Warnings**: Alerts before streak breaks
- **Achievement Unlocks**: Celebration notifications
- **Goal Progress**: Daily goal completion updates

### 🔒 Privacy & Security

- **Local-first**: All data stored locally by default
- **Encrypted Storage**: Sensitive data encrypted with Android Security Crypto
- **GDPR Compliant**: Full data export and deletion
- **No Tracking**: No analytics without consent

---

## 📸 Screenshots

> Screenshots coming soon! The app features:
> - Modern Material 3 design with dynamic theming
> - Beautiful game interfaces with smooth animations
> - Comprehensive statistics and progress dashboards
> - Intuitive navigation and user-friendly layouts

---

## 🚀 Installation

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK 35
- Minimum Android API Level 24 (Android 7.0)

### Clone and Build

```bash
# Clone the repository
git clone https://github.com/ahmetabdullahgultekin/TrainVoc.git
cd TrainVoc

# Open in Android Studio
# File -> Open -> Select TrainVoc directory

# Sync Gradle files
# Build -> Make Project

# Run on emulator or device
# Run -> Run 'app'
```

### Build Variants

```bash
# Debug build (development)
./gradlew assembleDebug

# Release build (production)
./gradlew assembleRelease

# Run tests
./gradlew test

# Generate code coverage report
./gradlew koverHtmlReport
```

---

## 🏗️ Architecture

Trainvoc follows **Clean Architecture** principles with **MVVM** (Model-View-ViewModel) pattern.

### Architecture Layers

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│    (Jetpack Compose + ViewModels)   │
├─────────────────────────────────────┤
│          Domain Layer               │
│    (Use Cases + Business Logic)     │
├─────────────────────────────────────┤
│           Data Layer                │
│  (Repositories + Data Sources)      │
├─────────────────────────────────────┤
│        Infrastructure Layer         │
│ (Room DB + Google Play + Network)   │
└─────────────────────────────────────┘
```

### Key Design Patterns

- **MVVM**: Separation of UI and business logic
- **Repository Pattern**: Abstract data sources
- **Dependency Injection**: Hilt for DI
- **Observer Pattern**: StateFlow/Flow for reactive updates
- **Factory Pattern**: ViewModel and use case creation
- **Strategy Pattern**: Different quiz algorithms

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed documentation.

---

## 🛠️ Tech Stack

| Category | Technologies |
|----------|-------------|
| **Language** | Kotlin 2.1.10 |
| **UI Framework** | Jetpack Compose (BOM 2025.06.00) |
| **Design System** | Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Hilt 2.52 |
| **Database** | Room 2.7.1 |
| **Navigation** | Navigation Compose 2.9.0 |
| **Async** | Kotlin Coroutines + Flow |
| **Background Tasks** | WorkManager 2.10.1 |
| **Animations** | Lottie Compose 6.1.0 |
| **Cloud Services** | Google Play Games Services 23.2.0 |
| **Security** | Android Security Crypto 1.1.0-alpha06 |
| **Testing** | JUnit, MockK, Turbine, Espresso |
| **Code Coverage** | Kover 0.9.0 |
| **Build System** | Gradle 8.13.1 with Kotlin DSL |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 35 (Android 15) |

---

## 📁 Project Structure

```
trainvoc/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gultekinahmetabdullah/trainvoc/
│   │   │   │   ├── accessibility/      # Accessibility features
│   │   │   │   ├── algorithm/          # Quiz algorithms
│   │   │   │   ├── analytics/          # Analytics & monitoring
│   │   │   │   ├── audio/              # TTS & audio services
│   │   │   │   ├── billing/            # In-app purchases (future)
│   │   │   │   ├── classes/            # Core data classes
│   │   │   │   │   ├── enums/          # Enums (WordLevel, QuizType)
│   │   │   │   │   ├── quiz/           # Quiz-related classes
│   │   │   │   │   └── word/           # Word entities
│   │   │   │   ├── cloud/              # Cloud sync services
│   │   │   │   ├── database/           # Room database
│   │   │   │   ├── di/                 # Hilt modules
│   │   │   │   ├── domain/             # Use cases
│   │   │   │   ├── features/           # Feature flags
│   │   │   │   ├── games/              # 10 memory games logic
│   │   │   │   ├── gamification/       # Achievements, streaks, goals
│   │   │   │   ├── gdpr/               # GDPR compliance
│   │   │   │   ├── images/             # Image management
│   │   │   │   ├── monitoring/         # Performance monitoring
│   │   │   │   ├── navigation/         # Navigation graphs
│   │   │   │   ├── notification/       # Push notifications
│   │   │   │   ├── offline/            # Offline mode
│   │   │   │   ├── performance/        # Performance optimization
│   │   │   │   ├── playgames/          # Google Play Games
│   │   │   │   ├── repository/         # Data repositories
│   │   │   │   ├── security/           # Encryption & security
│   │   │   │   ├── sync/               # Data synchronization
│   │   │   │   ├── ui/                 # UI components
│   │   │   │   │   ├── animations/     # Custom animations
│   │   │   │   │   ├── backup/         # Backup UI
│   │   │   │   │   ├── components/     # Reusable components
│   │   │   │   │   ├── games/          # Game screens & VMs
│   │   │   │   │   ├── screen/         # App screens
│   │   │   │   │   └── theme/          # Material 3 theme
│   │   │   │   ├── utils/              # Utility functions
│   │   │   │   ├── viewmodel/          # ViewModels
│   │   │   │   ├── widget/             # Home screen widgets
│   │   │   │   └── worker/             # Background workers
│   │   │   ├── res/                    # Resources
│   │   │   │   ├── drawable/           # Vector drawables
│   │   │   │   ├── mipmap/             # App icons
│   │   │   │   ├── raw/                # Lottie animations
│   │   │   │   ├── values/             # Strings, colors, themes
│   │   │   │   └── xml/                # Widget configs
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                       # Unit tests
│   │   └── androidTest/                # Instrumented tests
│   ├── build.gradle.kts                # App-level Gradle
│   └── proguard-rules.pro              # ProGuard config
├── gradle/
│   └── libs.versions.toml              # Version catalog
├── build.gradle.kts                    # Project-level Gradle
├── settings.gradle.kts                 # Project settings
├── .github/                            # GitHub configs
│   ├── ISSUE_TEMPLATE/                 # Issue templates
│   ├── workflows/                      # CI/CD workflows
│   ├── pull_request_template.md        # PR template
│   └── FUNDING.yml                     # Funding info
├── docs/                               # Documentation
│   ├── ARCHITECTURE.md                 # Architecture guide
│   └── [other documentation]
├── README.md                           # This file
├── LICENSE                             # MIT License
├── CONTRIBUTING.md                     # Contribution guidelines
├── CODE_OF_CONDUCT.md                  # Code of conduct
└── CHANGELOG.md                        # Version history
```

---

## 🗄️ Database Schema

Trainvoc uses **Room Database** (currently version 11) with the following main entities:

### Core Entities

| Entity | Description | Key Fields |
|--------|-------------|------------|
| `Word` | Vocabulary entries | id, english, turkish, level, example |
| `Exam` | Exam categories | id, name, description |
| `WordExamCrossRef` | Many-to-many word-exam relation | wordId, examId |
| `Statistic` | User performance per word | wordId, correctCount, wrongCount, skipCount |

### Gamification Entities

| Entity | Description | Key Fields |
|--------|-------------|------------|
| `StreakTracking` | Daily learning streaks | currentStreak, longestStreak, lastActivityDate |
| `DailyGoals` | Daily learning targets | wordsGoal, reviewsGoal, quizzesGoal, timeGoal |
| `UserAchievements` | Achievement progress | achievementId, progress, isUnlocked |

### Game Entities

| Entity | Description | Key Fields |
|--------|-------------|------------|
| `GameScore` | Game performance records | gameType, score, difficulty, completedAt |
| `GameStatistics` | Per-game statistics | gameType, timesPlayed, bestScore, avgScore |

**Total Entities**: 16 | **Database Version**: 11 | **Migration Path**: v1 → v11

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed schema documentation.

---

## 🗺️ Roadmap

### ✅ Completed

- [x] Core vocabulary training with 9 quiz types
- [x] CEFR level categorization (A1-C2)
- [x] Exam-based word grouping
- [x] Comprehensive statistics tracking
- [x] 10 interactive memory games
- [x] Gamification system (streaks, goals, 44 achievements)
- [x] Google Play Games integration
- [x] Cloud sync with conflict resolution
- [x] Offline mode with full functionality
- [x] Home screen widgets (4 types)
- [x] TTS integration for listening practice
- [x] Material 3 design system
- [x] Dark/light theme support
- [x] Encrypted local storage
- [x] GDPR compliance features
- [x] Performance monitoring
- [x] Background sync with WorkManager

### 🚧 In Progress

- [ ] Enhanced analytics dashboard
- [ ] Social features (friends, leaderboards)
- [ ] Daily challenges
- [ ] Custom word lists

### 📋 Planned

#### Short-term (1-3 months)
- [ ] Image caching optimization
- [ ] Additional language pairs (e.g., English-Spanish)
- [ ] Advanced word filtering
- [ ] Export/import custom word lists
- [ ] Weekly progress reports

#### Medium-term (3-6 months)
- [ ] Multiplayer game modes
- [ ] Voice recognition for pronunciation practice
- [ ] AI-powered personalized learning paths
- [ ] Community-contributed word sets
- [ ] Tablet-optimized layouts

#### Long-term (6+ months)
- [ ] Wear OS support
- [ ] Web companion app
- [ ] Teacher/classroom mode
- [ ] Premium subscription features
- [ ] Advanced analytics with ML insights

---

## 🤝 Contributing

We welcome contributions from the community! Whether it's:

- 🐛 Bug reports
- ✨ Feature requests
- 📝 Documentation improvements
- 🔧 Code contributions
- 🌍 Translations

Please read our [Contributing Guidelines](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md) before submitting.

### Quick Start for Contributors

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes following our coding standards
4. Write/update tests
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Ahmet Abdullah Gültekin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👨‍💻 Authors

**Ahmet Abdullah Gültekin**

- GitHub: [@ahmetabdullahgultekin](https://github.com/ahmetabdullahgultekin)
- LinkedIn: [Ahmet Abdullah Gültekin](https://linkedin.com/in/ahmetabdullahgultekin)

---

## 📬 Contact

- **GitHub Issues**: [Create an issue](https://github.com/ahmetabdullahgultekin/TrainVoc/issues)
- **Email**: ahmetabdullahgultekin@gmail.com
- **Discussions**: [GitHub Discussions](https://github.com/ahmetabdullahgultekin/TrainVoc/discussions)

---

## 🙏 Acknowledgments

- **Jetpack Compose Team** - For the amazing UI framework
- **Material Design Team** - For comprehensive design guidelines
- **Google Play Games Team** - For cloud sync infrastructure
- **Lottie Team** - For beautiful animations
- **Hilt Team** - For dependency injection simplicity
- **Open Source Community** - For inspiration and support

### Third-Party Libraries

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Room](https://developer.android.com/training/data-storage/room) - Database abstraction
- [Hilt](https://dagger.dev/hilt/) - Dependency injection
- [Lottie](https://airbnb.design/lottie/) - Animations
- [Google Play Games Services](https://developers.google.com/games/services) - Cloud features
- [MockK](https://mockk.io/) - Testing framework
- [Turbine](https://github.com/cashapp/turbine) - Flow testing

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/ahmetabdullahgultekin/TrainVoc?style=social)
![GitHub forks](https://img.shields.io/github/forks/ahmetabdullahgultekin/TrainVoc?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/ahmetabdullahgultekin/TrainVoc?style=social)
![GitHub issues](https://img.shields.io/github/issues/ahmetabdullahgultekin/TrainVoc)
![GitHub pull requests](https://img.shields.io/github/issues-pr/ahmetabdullahgultekin/TrainVoc)
![GitHub last commit](https://img.shields.io/github/last-commit/ahmetabdullahgultekin/TrainVoc)
![GitHub code size](https://img.shields.io/github/languages/code-size/ahmetabdullahgultekin/TrainVoc)

---

<div align="center">

**Made with ❤️ and Kotlin**

If you find this project useful, please consider giving it a ⭐!

[⬆ Back to Top](#trainvoc)

</div>
