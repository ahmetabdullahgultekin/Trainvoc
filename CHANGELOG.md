# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Open source documentation (README, CONTRIBUTING, LICENSE)
- GitHub issue and PR templates

## [1.1.2] - 2024-01-10

### Added
- 10 interactive memory games with full UI implementation
  - Multiple Choice with difficulty scaling
  - Fill in the Blank with context learning
  - Word Scramble with hint system
  - Flip Cards memory matching
  - Speed Match with combo system
  - Listening Quiz with TTS
  - Picture Match with visual learning
  - Spelling Challenge with validation
  - Translation Race with APM tracking
  - Context Clues with reading comprehension
- Games navigation integrated into main app
- Games button on home screen
- All game routes configured
- String resources for all games

### Changed
- Enhanced navigation system to support games
- Updated main app structure for game integration

### Fixed
- Navigation routing for game screens
- String resource references

## [1.1.1] - 2024-01-09

### Added
- Google Play Games Services integration
  - Cloud save/load functionality
  - Cross-device progress sync
  - Automatic conflict resolution
- 44 achievements system
  - Streak achievements (3-365 days)
  - Words learned (10-5,000 words)
  - Quiz completion (10-500 quizzes)
  - Perfect scores (10-100 perfect runs)
  - Daily goal milestones
  - Time spent achievements
  - Special achievements (Early Bird, Night Owl, etc.)
- Achievement sync with Google Play Games
- Leaderboard infrastructure support

### Changed
- Enhanced gamification system with cloud sync
- Improved achievement tracking and display

### Fixed
- Cloud sync conflict resolution
- Achievement unlock timing

## [1.1.0] - 2024-01-08

### Added
- Gamification system
  - Streak tracking (up to 365+ days)
  - Daily goals with customizable targets
  - Progress dashboard with analytics
- Complete offline mode with full functionality
- 4 home screen widgets
  - Word of the Day
  - Daily Progress
  - Streak Counter
  - Quick Quiz launcher
- Smart notifications system
  - Daily learning reminders
  - Streak warnings
  - Achievement unlock celebrations
  - Goal progress updates
- Background sync with WorkManager
- Performance monitoring system

### Changed
- Enhanced UI with Material 3 design
- Improved statistics tracking
- Updated database schema (v10 → v11)

### Fixed
- Database migration issues
- Widget update timing
- Notification scheduling

## [1.0.0] - 2024-01-01

### Added
- Core vocabulary training functionality
- 9 adaptive quiz types
  - Random
  - Least/Most Correct
  - Least/Most Wrong
  - Least/Most Reviewed
  - Least/Most Recent
- CEFR level categorization (A1-C2)
- Exam-based word grouping (TOEFL, IELTS, etc.)
- Comprehensive statistics tracking
- Spaced Repetition System (SM-2 algorithm)
- Material 3 design system
- Dark/light theme support
- TTS integration for listening practice
- Encrypted local storage
- GDPR compliance features
- Room database with preloaded words
- Jetpack Compose UI
- Hilt dependency injection
- Kotlin Coroutines and Flow

### Security
- Android Security Crypto for data encryption
- Secure local data storage
- Privacy-focused design with no tracking

---

## Version History Summary

- **1.1.2** - Memory Games Integration
- **1.1.1** - Google Play Games Services
- **1.1.0** - Gamification & Widgets
- **1.0.0** - Initial Release

---

## Migration Notes

### Upgrading to 1.1.2
- No database migration required
- All game features are automatically available
- No user action needed

### Upgrading to 1.1.1
- Cloud sync requires Google Play Games sign-in
- Achievements will sync automatically on first sign-in
- No data loss during upgrade

### Upgrading to 1.1.0
- Database migration from v10 to v11
- Widgets can be added to home screen after update
- Notifications require permission grant
- Streaks will start tracking from upgrade date

---

## Links

- [GitHub Repository](https://github.com/ahmetabdullahgultekin/TrainVoc)
- [Issue Tracker](https://github.com/ahmetabdullahgultekin/TrainVoc/issues)
- [Contributing Guidelines](CONTRIBUTING.md)
