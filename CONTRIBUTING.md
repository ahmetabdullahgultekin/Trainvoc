# Contributing to Trainvoc

First off, thank you for considering contributing to Trainvoc! It's people like you that make Trainvoc such a great tool for language learners worldwide.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

## Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to ahmetabdullahgultekin@gmail.com.

## Getting Started

### Prerequisites

Before you begin, ensure you have:

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK 35
- Git installed and configured
- A GitHub account

### Finding Issues to Work On

- Check the [Issues](https://github.com/ahmetabdullahgultekin/TrainVoc/issues) page
- Look for issues tagged with:
  - `good first issue` - Perfect for newcomers
  - `help wanted` - Issues we need help with
  - `bug` - Bug fixes needed
  - `enhancement` - Feature requests
  - `documentation` - Documentation improvements

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When creating a bug report, include as many details as possible:

**Use the bug report template** which includes:

- A clear and descriptive title
- Exact steps to reproduce the issue
- Expected vs. actual behavior
- Screenshots/videos if applicable
- Device information (Android version, device model)
- App version
- Relevant logs/error messages

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion:

- Use a clear and descriptive title
- Provide a detailed description of the proposed feature
- Explain why this enhancement would be useful
- Include mockups or examples if applicable
- Consider the scope and complexity

### Contributing Code

1. **Fork the repository**
2. **Create a feature branch** from `main`:
   ```bash
   git checkout -b feature/amazing-feature
   ```
   or for bug fixes:
   ```bash
   git checkout -b fix/bug-description
   ```

3. **Make your changes** following our coding standards
4. **Write/update tests** for your changes
5. **Ensure all tests pass**:
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```
6. **Commit your changes** with clear commit messages
7. **Push to your fork**:
   ```bash
   git push origin feature/amazing-feature
   ```
8. **Open a Pull Request**

### Contributing Translations

We welcome translations to make Trainvoc accessible to more users:

1. Check `app/src/main/res/values/strings.xml` for English strings
2. Create a new values folder for your language (e.g., `values-es` for Spanish)
3. Translate all strings while maintaining:
   - String keys unchanged
   - Placeholders like `%1$s`, `%2$d` in the same positions
   - Consistent tone and terminology
4. Test the UI with your translations

## Development Setup

### Clone and Build

```bash
# Fork the repository on GitHub first, then:
git clone https://github.com/YOUR_USERNAME/TrainVoc.git
cd TrainVoc

# Add upstream remote
git remote add upstream https://github.com/ahmetabdullahgultekin/TrainVoc.git

# Open in Android Studio
# File -> Open -> Select TrainVoc directory

# Sync Gradle files
# Build -> Make Project
```

### Keep Your Fork Updated

```bash
# Fetch upstream changes
git fetch upstream

# Merge upstream changes into your main branch
git checkout main
git merge upstream/main
```

## Coding Standards

### Kotlin Style Guide

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable and function names
- Prefer `val` over `var` when possible
- Use type inference when the type is obvious

### Architecture Guidelines

- Follow **MVVM** pattern
- Use **Clean Architecture** layers
- Keep ViewModels free of Android dependencies
- Use **Hilt** for dependency injection
- Repository pattern for data access
- Use Kotlin **Coroutines** and **Flow** for async operations

### Code Examples

#### Good Example ✅

```kotlin
class WordRepository @Inject constructor(
    private val wordDao: WordDao,
    private val dispatchers: DispatcherProvider
) {
    suspend fun getWordsByLevel(level: WordLevel): Result<List<Word>> =
        withContext(dispatchers.io) {
            try {
                Result.success(wordDao.getWordsByLevel(level))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
```

#### Bad Example ❌

```kotlin
// Don't do this:
class WordRepository {
    fun getWords(l: String): List<Word> {
        // Direct database call on main thread
        return database.wordDao().getWordsByLevel(l)
    }
}
```

### UI/UX Guidelines

- Use **Material 3** design components
- Follow **Jetpack Compose** best practices
- Support both **light and dark themes**
- Ensure **accessibility** (content descriptions, screen reader support)
- Use **responsive layouts** for different screen sizes
- Prefer **stateless composables** when possible

## Commit Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/):

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no logic change)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `build`: Build system changes
- `ci`: CI/CD changes
- `chore`: Other changes that don't modify src or test files

### Examples

```bash
feat(games): add Word Scramble game with hint system

Implements a new memory game where users unscramble letters.
- Added WordScrambleViewModel with game logic
- Created WordScrambleScreen with UI
- Integrated hint system with penalty scoring
- Added unit tests for scramble algorithm

Closes #123

fix(database): resolve migration crash from v10 to v11

Fixed a schema mismatch causing app crashes on upgrade.
The migration was missing the new 'difficulty' column in GameScore table.

Fixes #456

docs(readme): update installation instructions

Added prerequisites section and clarified build steps.
```

### Commit Best Practices

- Use the imperative mood ("Add feature" not "Added feature")
- First line should be 50 characters or less
- Use the body to explain **what** and **why**, not **how**
- Reference issues and pull requests when relevant

## Pull Request Process

### Before Submitting

1. ✅ Update the README.md with details of changes (if applicable)
2. ✅ Update documentation for any changed functionality
3. ✅ Add tests for new features
4. ✅ Ensure all tests pass locally
5. ✅ Follow the coding standards
6. ✅ Write clear commit messages
7. ✅ Rebase on the latest `main` branch

### Pull Request Template

When you open a PR, please fill out the template:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe the tests you ran

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] My code follows the style guidelines
- [ ] I have performed a self-review
- [ ] I have commented my code where needed
- [ ] I have updated the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing unit tests pass locally
```

### Review Process

1. At least one maintainer will review your PR
2. Address any requested changes
3. Once approved, a maintainer will merge your PR
4. Your contribution will be included in the next release!

## Testing Guidelines

### Unit Tests

- Write unit tests for **all business logic**
- Use **MockK** for mocking dependencies
- Use **Turbine** for testing Flows
- Aim for **>80% code coverage** on new code

#### Example Unit Test

```kotlin
@Test
fun `getWordsByLevel returns correct words`() = runTest {
    // Given
    val expectedWords = listOf(
        Word(id = 1, english = "hello", turkish = "merhaba", level = WordLevel.A1)
    )
    coEvery { wordDao.getWordsByLevel(WordLevel.A1) } returns expectedWords

    // When
    val result = repository.getWordsByLevel(WordLevel.A1)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedWords, result.getOrNull())
}
```

### UI Tests

- Write UI tests for **critical user flows**
- Use **Espresso** for instrumented tests
- Test on **multiple screen sizes**
- Test both **light and dark themes**

### Running Tests

```bash
# Unit tests
./gradlew test

# UI tests (requires emulator/device)
./gradlew connectedAndroidTest

# Code coverage report
./gradlew koverHtmlReport
# Report location: build/reports/kover/html/index.html
```

## Documentation

### Code Documentation

- Add **KDoc** comments for public APIs
- Document **complex algorithms** and business logic
- Include **usage examples** in documentation

#### Example KDoc

```kotlin
/**
 * Retrieves words filtered by the specified CEFR level.
 *
 * @param level The CEFR level to filter by (A1, A2, B1, B2, C1, C2)
 * @return Result containing list of words or error
 * @throws DatabaseException if database access fails
 *
 * Example:
 * ```kotlin
 * val result = repository.getWordsByLevel(WordLevel.B2)
 * result.onSuccess { words ->
 *     // Process words
 * }
 * ```
 */
suspend fun getWordsByLevel(level: WordLevel): Result<List<Word>>
```

### Architecture Documentation

- Update `docs/ARCHITECTURE.md` for architectural changes
- Add diagrams for complex flows (use Mermaid or PlantUML)
- Document design decisions and trade-offs

## Questions?

- Check our [FAQ](docs/FAQ.md)
- Open a [Discussion](https://github.com/ahmetabdullahgultekin/TrainVoc/discussions)
- Ask in your PR/issue
- Email: ahmetabdullahgultekin@gmail.com

## Recognition

Contributors will be:

- Listed in our [Contributors](https://github.com/ahmetabdullahgultekin/TrainVoc/graphs/contributors) page
- Mentioned in release notes for significant contributions
- Featured in the app's About section (for major features)

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Thank you for contributing to Trainvoc!** 🎉
