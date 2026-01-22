# Contributing to Trainvoc

Thank you for your interest in contributing to Trainvoc! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Guidelines](#issue-guidelines)
- [Component-Specific Guidelines](#component-specific-guidelines)

---

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inclusive environment for all contributors. We expect everyone to:

- Be respectful and considerate
- Welcome newcomers and help them get started
- Accept constructive criticism gracefully
- Focus on what is best for the community
- Show empathy towards other community members

### Unacceptable Behavior

- Harassment, discrimination, or offensive comments
- Personal attacks or trolling
- Publishing others' private information
- Other conduct that could be considered inappropriate

### Enforcement

Violations may result in temporary or permanent bans from the project. Report issues to the maintainers.

---

## Getting Started

### Prerequisites

| Component | Requirements |
|-----------|-------------|
| **TrainvocClient** | Android Studio, JDK 17+, Android SDK 35 |
| **TrainvocWeb** | Node.js 18+, npm |
| **TrainvocBackend** | JDK 24+, PostgreSQL 15+ |

### Fork and Clone

```bash
# Fork the repository on GitHub, then clone your fork
git clone https://github.com/YOUR_USERNAME/Trainvoc.git
cd Trainvoc

# Add upstream remote
git remote add upstream https://github.com/ahmetabdullahgultekin/Trainvoc.git

# Keep your fork updated
git fetch upstream
git checkout main
git merge upstream/main
```

### Setup Development Environment

#### TrainvocClient (Android)

```bash
cd TrainvocClient
# Open in Android Studio
# Sync Gradle files
# Build -> Make Project
```

#### TrainvocWeb (React)

```bash
cd TrainvocWeb
npm install
npm run dev
```

#### TrainvocBackend (Spring Boot)

```bash
cd TrainvocBackend

# Create databases
createdb trainvoc
createdb trainvoc-words

# Run SQL scripts
psql -d trainvoc -f sql-queries/trainvoc-mp-db-for-postgre.sql
psql -d trainvoc-words -f sql-queries/trainvoc-words-db-for-postgre.sql

# Start server
./gradlew bootRun
```

---

## Development Workflow

### Branch Strategy

```
main
 │
 ├── feature/feature-name    # New features
 ├── bugfix/issue-123        # Bug fixes
 ├── docs/documentation-name # Documentation
 └── refactor/area-name      # Code refactoring
```

### Creating a Branch

```bash
# Update main
git checkout main
git pull upstream main

# Create feature branch
git checkout -b feature/your-feature-name

# Or bugfix branch
git checkout -b bugfix/issue-123-description
```

### Making Changes

1. Make your changes in small, focused commits
2. Write/update tests for your changes
3. Ensure all tests pass
4. Update documentation if needed
5. Follow the coding standards

### Syncing with Upstream

```bash
git fetch upstream
git checkout main
git merge upstream/main
git checkout your-branch
git rebase main
```

---

## Coding Standards

### General Guidelines

- Write clear, self-documenting code
- Keep functions small and focused
- Use meaningful variable and function names
- Add comments only when necessary (explain "why", not "what")
- Follow the DRY principle (Don't Repeat Yourself)
- Prefer composition over inheritance

### TrainvocClient (Kotlin)

```kotlin
// Use Kotlin idioms
val items = list.filter { it.isValid }
              .map { it.name }
              .sorted()

// Use data classes for DTOs
data class Word(
    val id: Long,
    val english: String,
    val turkish: String
)

// Use sealed classes for states
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Word>) : UiState()
    data class Error(val message: String) : UiState()
}

// Follow naming conventions
class WordRepository           // PascalCase for classes
fun getWordById()              // camelCase for functions
val wordList                   // camelCase for variables
const val MAX_WORDS = 100      // SCREAMING_SNAKE_CASE for constants
```

**Android-Specific**:
- Use ViewModel for UI state
- Use Hilt for dependency injection
- Use Compose for UI components
- Follow Material 3 guidelines
- Support accessibility (contentDescription, etc.)

### TrainvocWeb (TypeScript)

```typescript
// Use TypeScript interfaces
interface Word {
  id: number;
  english: string;
  turkish: string;
}

// Use functional components
const WordCard: React.FC<WordCardProps> = ({ word }) => {
  return (
    <Card>
      <Typography>{word.english}</Typography>
    </Card>
  );
};

// Use custom hooks for logic
const useWords = () => {
  const [words, setWords] = useState<Word[]>([]);
  // ...
  return { words, loading, error };
};

// Follow naming conventions
interface WordProps {}         // PascalCase for interfaces/types
const WordCard = () => {}      // PascalCase for components
const getWords = () => {}      // camelCase for functions
const wordList = []            // camelCase for variables
```

### TrainvocBackend (Java)

```java
// Use constructor injection
@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
}

// Use Optional for nullable returns
public Optional<GameRoom> findByRoomCode(String roomCode) {
    return repository.findByRoomCode(roomCode);
}

// Follow naming conventions
public class GameService {}    // PascalCase for classes
public void startGame() {}     // camelCase for methods
private final int maxPlayers;  // camelCase for fields
public static final int MAX = 10; // SCREAMING_SNAKE_CASE for constants
```

---

## Commit Guidelines

### Commit Message Format

```
type(scope): short description

[optional body]

[optional footer]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, no code change |
| `refactor` | Code restructuring |
| `test` | Adding/updating tests |
| `chore` | Maintenance tasks |
| `perf` | Performance improvement |

### Scopes

| Scope | Description |
|-------|-------------|
| `client` | TrainvocClient (Android) |
| `web` | TrainvocWeb (React) |
| `backend` | TrainvocBackend (Spring) |
| `root` | Root project files |

### Examples

```bash
# Good commits
feat(client): add word pronunciation with TTS
fix(backend): resolve null pointer in GameService
docs(root): update README with setup instructions
refactor(web): extract GameCard component

# Bad commits
fix: fixed stuff                    # Too vague
feat: added new feature and fixed bug  # Multiple changes
Update files                        # No type, unclear
```

### Commit Best Practices

- Keep commits small and focused
- One logical change per commit
- Write in imperative mood ("add" not "added")
- Reference issues when applicable: `fix(client): resolve crash #123`

---

## Pull Request Process

### Before Submitting

- [ ] Code follows the style guidelines
- [ ] Self-reviewed the changes
- [ ] Added/updated tests (if applicable)
- [ ] All tests pass
- [ ] Updated documentation (if applicable)
- [ ] No merge conflicts with main
- [ ] Commit messages follow guidelines

### PR Title Format

```
type(scope): short description
```

Examples:
- `feat(client): implement streak widget`
- `fix(backend): resolve room cleanup issue`
- `docs(root): add architecture documentation`

### PR Description Template

```markdown
## Summary
Brief description of what this PR does.

## Changes
- Change 1
- Change 2
- Change 3

## Related Issues
Fixes #123
Related to #456

## Testing
Describe how you tested these changes.

## Screenshots (if applicable)
Add screenshots for UI changes.

## Checklist
- [ ] Tests pass
- [ ] Documentation updated
- [ ] No breaking changes
```

### Review Process

1. Submit PR against `main` branch
2. Request review from maintainers
3. Address feedback and push changes
4. Maintain a clean commit history (squash if needed)
5. Once approved, maintainer will merge

### After Merge

```bash
# Clean up local branch
git checkout main
git pull upstream main
git branch -d your-branch-name

# Delete remote branch (if applicable)
git push origin --delete your-branch-name
```

---

## Issue Guidelines

### Bug Reports

Include:
- Clear title and description
- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, version, etc.)
- Screenshots/logs if applicable

### Feature Requests

Include:
- Clear description of the feature
- Use case / motivation
- Proposed implementation (if any)
- Mockups (if applicable)

### Issue Labels

| Label | Description |
|-------|-------------|
| `bug` | Something isn't working |
| `feature` | New feature request |
| `docs` | Documentation improvement |
| `good first issue` | Good for newcomers |
| `help wanted` | Extra attention needed |
| `client` | Android app related |
| `web` | Web platform related |
| `backend` | Server related |

---

## Component-Specific Guidelines

### TrainvocClient (Android)

**Architecture**:
- Follow MVVM pattern
- Use Hilt for dependency injection
- Put UI logic in ViewModels
- Keep Composables stateless

**Testing**:
- Unit tests for ViewModels and Use Cases
- Integration tests for Repositories
- UI tests for critical flows

**Accessibility**:
- Add `contentDescription` to interactive elements
- Support screen readers
- Follow WCAG 2.1 AA guidelines

### TrainvocWeb (React)

**Components**:
- Functional components with hooks
- Props destructuring
- Extract reusable logic to custom hooks

**Styling**:
- Use MUI `sx` prop
- Follow Material 3 design
- Support dark/light themes

**State**:
- Use React state for local state
- Consider Zustand for complex state

### TrainvocBackend (Spring Boot)

**API Design**:
- RESTful endpoints
- Consistent response format
- Proper HTTP status codes

**Database**:
- Use JPA entities
- Add migrations for schema changes
- Optimize queries

**Security**:
- Validate all inputs
- Sanitize outputs
- Use parameterized queries

---

## Questions?

If you have questions:

1. Check existing documentation
2. Search existing issues
3. Create a new issue with the `question` label
4. Reach out to maintainers

---

## Recognition

Contributors will be recognized in:
- Project README
- Release notes
- Contributors file

Thank you for contributing to Trainvoc!

---

*Last Updated: January 22, 2026*
