# Trainvoc Architecture Documentation

This document provides a comprehensive overview of the Trainvoc application architecture, design patterns, and technical decisions.

## Table of Contents

- [Overview](#overview)
- [Architecture Layers](#architecture-layers)
- [Design Patterns](#design-patterns)
- [Database Schema](#database-schema)
- [Navigation](#navigation)
- [Dependency Injection](#dependency-injection)
- [State Management](#state-management)
- [Testing Strategy](#testing-strategy)
- [Performance Considerations](#performance-considerations)

## Overview

Trainvoc follows **Clean Architecture** principles combined with **MVVM** (Model-View-ViewModel) pattern, ensuring:
- **Separation of concerns**
- **Testability**
- **Maintainability**
- **Scalability**

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│                 (Jetpack Compose + ViewModels)              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Screens  │  │ViewModels│  │Components│  │Navigation│  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
└───────┼─────────────┼─────────────┼─────────────┼─────────┘
        │             │             │             │
┌───────┼─────────────┼─────────────┼─────────────┼─────────┐
│       │      Domain Layer (Use Cases)           │         │
│  ┌────▼─────┐  ┌───▼──────┐  ┌──▼───────┐  ┌──▼──────┐  │
│  │Quiz Logic│  │Gamificat.│  │Game Logic│  │Analytics│  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
└───────┼─────────────┼──────────────┼─────────────┼────────┘
        │             │              │             │
┌───────┼─────────────┼──────────────┼─────────────┼────────┐
│       │         Data Layer (Repositories)        │        │
│  ┌────▼─────┐  ┌───▼──────┐  ┌───▼──────┐  ┌───▼─────┐  │
│  │WordRepo  │  │StreakRepo│  │GameRepo  │  │CloudRepo│  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
└───────┼─────────────┼──────────────┼─────────────┼────────┘
        │             │              │             │
┌───────┼─────────────┼──────────────┼─────────────┼────────┐
│       │     Infrastructure Layer                 │        │
│  ┌────▼─────┐  ┌───▼──────┐  ┌───▼──────┐  ┌───▼─────┐  │
│  │Room DB   │  │Work Mgr  │  │Play Games│  │Security │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Architecture Layers

### 1. Presentation Layer

**Location**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/`

Handles all UI-related code using Jetpack Compose and ViewModels.

#### Components:
- **Screens**: Top-level composables for each app screen
- **ViewModels**: Manage UI state and business logic
- **Components**: Reusable UI components
- **Theme**: Material 3 theming configuration

#### Key Principles:
- ViewModels expose **StateFlow/Flow** for reactive UI updates
- Screens are **stateless** and driven by ViewModel state
- UI components are **composable** and reusable
- No business logic in composables

#### Example:

```kotlin
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(level: WordLevel, quizType: QuizType) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            wordRepository.getWordsByLevel(level)
                .onSuccess { words ->
                    _uiState.value = QuizUiState.Success(words)
                }
                .onFailure { error ->
                    _uiState.value = QuizUiState.Error(error.message)
                }
        }
    }
}
```

### 2. Domain Layer

**Location**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/domain/`

Contains business logic and use cases.

#### Components:
- **Use Cases**: Encapsulate business rules
- **Models**: Domain-specific data models
- **Interfaces**: Repository interfaces

#### Example:

```kotlin
class GetQuizWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(
        parameter: QuizParameter,
        quizType: QuizType,
        limit: Int = 10
    ): Result<List<Word>> {
        return when (parameter) {
            is QuizParameter.Level -> wordRepository.getWordsByLevel(parameter.level)
            is QuizParameter.ExamType -> wordRepository.getWordsByExam(parameter.examType)
        }.map { words ->
            applyQuizStrategy(words, quizType, limit)
        }
    }

    private fun applyQuizStrategy(
        words: List<Word>,
        quizType: QuizType,
        limit: Int
    ): List<Word> {
        return when (quizType) {
            QuizType.RANDOM -> words.shuffled().take(limit)
            QuizType.LEAST_CORRECT -> words.sortedBy { it.statistics.correctCount }.take(limit)
            // ... other strategies
        }
    }
}
```

### 3. Data Layer

**Location**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/repository/`

Manages data sources and provides data to the domain layer.

#### Components:
- **Repositories**: Implement repository interfaces from domain layer
- **Data Sources**: Local (Room) and remote (Cloud) data sources
- **Mappers**: Convert between data models and domain models

#### Example:

```kotlin
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val cloudDataSource: CloudDataSource,
    private val dispatchers: DispatcherProvider
) : WordRepository {

    override suspend fun getWordsByLevel(level: WordLevel): Result<List<Word>> =
        withContext(dispatchers.io) {
            try {
                val words = wordDao.getWordsByLevel(level)
                Result.success(words.map { it.toDomain() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun syncWithCloud(): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val localData = wordDao.getAllWords()
                val cloudData = cloudDataSource.loadFromCloud()
                val merged = mergeData(localData, cloudData)
                wordDao.insertAll(merged)
                cloudDataSource.saveToCloud(merged)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
```

### 4. Infrastructure Layer

**Location**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/`

Contains framework-specific implementations.

#### Components:
- **Database**: Room database configuration
- **Network**: API clients and services
- **Cloud**: Google Play Games integration
- **Workers**: Background tasks with WorkManager
- **Security**: Encryption and secure storage

## Design Patterns

### 1. MVVM (Model-View-ViewModel)

**Purpose**: Separation of UI and business logic

```
View (Composable) → ViewModel → Repository → Data Source
         ↑              |
         └──── State ───┘
```

### 2. Repository Pattern

**Purpose**: Abstract data sources

```kotlin
interface WordRepository {
    suspend fun getWordsByLevel(level: WordLevel): Result<List<Word>>
    suspend fun insertWord(word: Word): Result<Unit>
}

class WordRepositoryImpl : WordRepository {
    // Implementation using Room DAO
}
```

### 3. Dependency Injection (Hilt)

**Purpose**: Loose coupling and testability

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWordRepository(
        wordDao: WordDao,
        cloudDataSource: CloudDataSource,
        dispatchers: DispatcherProvider
    ): WordRepository {
        return WordRepositoryImpl(wordDao, cloudDataSource, dispatchers)
    }
}
```

### 4. Strategy Pattern

**Purpose**: Different quiz algorithms

```kotlin
sealed interface QuizStrategy {
    fun selectWords(words: List<Word>, limit: Int): List<Word>
}

object RandomStrategy : QuizStrategy {
    override fun selectWords(words: List<Word>, limit: Int) = words.shuffled().take(limit)
}

object LeastCorrectStrategy : QuizStrategy {
    override fun selectWords(words: List<Word>, limit: Int) =
        words.sortedBy { it.statistics.correctCount }.take(limit)
}
```

### 5. Observer Pattern (Flow/StateFlow)

**Purpose**: Reactive state updates

```kotlin
class QuizViewModel : ViewModel() {
    private val _quizState = MutableStateFlow<QuizState>(QuizState.Initial)
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    init {
        viewModelScope.launch {
            wordRepository.wordsFlow
                .collect { words ->
                    _quizState.value = QuizState.Ready(words)
                }
        }
    }
}
```

## Database Schema

### Room Database Version: 11

#### Core Entities

**Word**
```kotlin
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val english: String,
    val turkish: String,
    val level: WordLevel,
    val example: String?,
    val pronunciation: String?,
    val imageUrl: String?
)
```

**Statistic**
```kotlin
@Entity(tableName = "statistics")
data class Statistic(
    @PrimaryKey val wordId: Long,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val skipCount: Int = 0,
    val lastReviewDate: Long?,
    val easeFactor: Float = 2.5f, // SM-2 algorithm
    val interval: Int = 0
)
```

**StreakTracking**
```kotlin
@Entity(tableName = "streak_tracking")
data class StreakTracking(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: Long?
)
```

**DailyGoals**
```kotlin
@Entity(tableName = "daily_goals")
data class DailyGoals(
    @PrimaryKey val id: Int = 1,
    val wordsGoal: Int = 10,
    val reviewsGoal: Int = 20,
    val quizzesGoal: Int = 3,
    val timeGoal: Int = 15 // minutes
)
```

**GameScore**
```kotlin
@Entity(tableName = "game_scores")
data class GameScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: String,
    val score: Int,
    val difficulty: String,
    val completedAt: Long,
    val duration: Long
)
```

#### Database Relationships

```kotlin
data class WordWithStatistics(
    @Embedded val word: Word,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId"
    )
    val statistics: Statistic?
)

data class ExamWithWords(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "id",
        entityColumn = "examId",
        associateBy = Junction(WordExamCrossRef::class)
    )
    val words: List<Word>
)
```

#### Migration Strategy

```kotlin
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE game_scores ADD COLUMN difficulty TEXT NOT NULL DEFAULT 'MEDIUM'"
        )
    }
}
```

## Navigation

Using **Jetpack Navigation Compose** with type-safe navigation.

### Navigation Graph

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz/{level}/{type}") {
        fun createRoute(level: WordLevel, type: QuizType) =
            "quiz/${level.name}/${type.name}"
    }
    object Games : Screen("games")
    object GameDetail : Screen("games/{gameType}") {
        fun createRoute(gameType: String) = "games/$gameType"
    }
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("level") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            QuizScreen(
                level = WordLevel.valueOf(backStackEntry.arguments?.getString("level")!!),
                type = QuizType.valueOf(backStackEntry.arguments?.getString("type")!!)
            )
        }
    }
}
```

## Dependency Injection

Using **Hilt** for dependency injection.

### Module Structure

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "trainvoc-db"
        )
        .createFromAsset("databases/word-db.db")
        .addMigrations(MIGRATION_10_11)
        .build()
    }

    @Provides
    fun provideWordDao(database: AppDatabase): WordDao {
        return database.wordDao()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object DispatcherModule {

    @Provides
    fun provideDispatchers(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}
```

## State Management

### UI State Pattern

```kotlin
sealed interface QuizUiState {
    object Loading : QuizUiState
    data class Success(
        val currentQuestion: Int,
        val totalQuestions: Int,
        val word: Word,
        val options: List<String>,
        val score: Int
    ) : QuizUiState
    data class Completed(val score: Int, val total: Int) : QuizUiState
    data class Error(val message: String) : QuizUiState
}
```

### State Updates

```kotlin
class QuizViewModel : ViewModel() {
    private val _state = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    fun submitAnswer(answer: String) {
        val currentState = _state.value
        if (currentState is QuizUiState.Success) {
            viewModelScope.launch {
                val isCorrect = checkAnswer(answer, currentState.word)
                updateStatistics(currentState.word.id, isCorrect)

                if (currentState.currentQuestion < currentState.totalQuestions) {
                    loadNextQuestion()
                } else {
                    _state.value = QuizUiState.Completed(
                        score = currentState.score + if (isCorrect) 1 else 0,
                        total = currentState.totalQuestions
                    )
                }
            }
        }
    }
}
```

## Testing Strategy

### Unit Tests

Testing business logic and ViewModels.

```kotlin
@Test
fun `loadQuiz emits success state with words`() = runTest {
    // Given
    val expectedWords = listOf(testWord1, testWord2)
    coEvery { wordRepository.getWordsByLevel(any()) } returns Result.success(expectedWords)

    // When
    viewModel.loadQuiz(WordLevel.A1, QuizType.RANDOM)

    // Then
    viewModel.uiState.test {
        assertEquals(QuizUiState.Loading, awaitItem())
        val successState = awaitItem() as QuizUiState.Success
        assertEquals(expectedWords.size, successState.totalQuestions)
    }
}
```

### Integration Tests

Testing repository implementations.

```kotlin
@Test
fun `repository syncs data with cloud successfully`() = runTest {
    // Given
    val localWords = listOf(testWord1)
    val cloudWords = listOf(testWord2)

    // When
    val result = repository.syncWithCloud()

    // Then
    assertTrue(result.isSuccess)
    verify { wordDao.insertAll(any()) }
    verify { cloudDataSource.saveToCloud(any()) }
}
```

### UI Tests

Testing UI components with Compose Test.

```kotlin
@Test
fun `quiz screen displays question and options`() {
    composeTestRule.setContent {
        QuizScreen(
            level = WordLevel.A1,
            type = QuizType.RANDOM
        )
    }

    composeTestRule
        .onNodeWithText("What is the translation?")
        .assertIsDisplayed()
}
```

## Performance Considerations

### 1. Database Optimization

- **Indexed columns** for frequently queried fields
- **Lazy loading** for large datasets
- **Pagination** for word lists
- **Background threading** for all database operations

### 2. Memory Management

- **Image caching** with Coil
- **ViewModel scoping** to prevent leaks
- **Flow cancellation** when screens are disposed

### 3. Compose Optimization

- **Remember** for expensive calculations
- **LazyColumn** for scrollable lists
- **Key parameters** for efficient recomposition
- **Stable classes** to prevent unnecessary recompositions

### 4. Background Tasks

- **WorkManager** for periodic sync
- **Coroutines** for asynchronous operations
- **Dispatcher optimization** (IO, Main, Default)

---

## Additional Resources

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Jetpack Compose Best Practices](https://developer.android.com/jetpack/compose/performance)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
