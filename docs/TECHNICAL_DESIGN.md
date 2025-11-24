# TrainVoc - Technical Design Document
## SE-Compliant Production-Ready Implementation

**Version**: 2.0.0
**Date**: 2025-11-24
**Status**: Ready for Implementation
**Author**: Development Team

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Architecture Overview](#architecture-overview)
3. [Testing Infrastructure](#testing-infrastructure)
4. [Security Hardening](#security-hardening)
5. [Error Handling & Resilience](#error-handling--resilience)
6. [Monitoring & Observability](#monitoring--observability)
7. [CI/CD Pipeline](#cicd-pipeline)
8. [Legal & Compliance](#legal--compliance)
9. [Performance Optimization](#performance-optimization)
10. [Implementation Phases](#implementation-phases)

---

## Executive Summary

### Current State
- **Compliance Score**: 62/100
- **Critical Gaps**: Testing (10%), Security (45%), Compliance (30%)
- **Strengths**: Architecture (75%), Android Specifics (80%)

### Target State
- **Compliance Score**: 90+/100
- **Test Coverage**: 80%+
- **Security**: Enterprise-grade
- **All critical gaps addressed**

### Implementation Timeline
- **Phase 1**: 2 weeks (Critical Foundation)
- **Phase 2**: 2 weeks (Quality & Automation)
- **Phase 3**: 2 weeks (Enhancement)
- **Phase 4**: 2 weeks (Polish)
- **Total**: 8 weeks

---

## Architecture Overview

### 1.1 Enhanced Clean Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Compose    │  │  ViewModels  │  │     UI       │  │
│  │     UI       │  │   + State    │  │  Components  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                     Domain Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Use Cases   │  │    Models    │  │  Repository  │  │
│  │ (Business    │  │   (Domain    │  │  Interfaces  │  │
│  │   Logic)     │  │   Entities)  │  │              │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Repositories │  │   Data       │  │   Local DB   │  │
│  │    (Impl)    │  │   Sources    │  │   (Room)     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Networking  │  │   Storage    │  │  Monitoring  │  │
│  │   (Future)   │  │  (Encrypted) │  │  (Firebase)  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 1.2 Package Structure (Enhanced)

```
com.gultekinahmetabdullah.trainvoc/
│
├── core/                           # NEW - Core utilities
│   ├── common/
│   │   ├── Result.kt              # Result wrapper
│   │   ├── Constants.kt           # App-wide constants
│   │   └── Extensions.kt          # Extension functions
│   ├── error/
│   │   ├── AppError.kt            # Error types
│   │   ├── ErrorHandler.kt        # Error handling
│   │   └── ErrorMapper.kt         # Error mapping
│   ├── network/                   # Future API support
│   │   ├── NetworkMonitor.kt
│   │   └── ConnectivityObserver.kt
│   └── util/
│       ├── Logger.kt              # Structured logging
│       ├── DateTimeUtils.kt
│       └── ValidationUtils.kt
│
├── domain/                         # ENHANCED - Business logic
│   ├── model/                     # NEW - Domain models
│   │   ├── Word.kt                # Domain entity
│   │   ├── Quiz.kt                # Domain entity
│   │   └── UserProgress.kt        # Domain entity
│   ├── repository/                # Interfaces
│   │   ├── WordRepository.kt
│   │   ├── QuizRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/                   # ENHANCED
│       ├── word/
│       │   ├── GetRandomWordUseCase.kt
│       │   ├── GetWordsByLevelUseCase.kt
│       │   └── UpdateWordStatisticsUseCase.kt
│       ├── quiz/
│       │   ├── StartQuizUseCase.kt
│       │   ├── SubmitAnswerUseCase.kt
│       │   └── GetQuizResultsUseCase.kt
│       └── notification/
│           ├── ScheduleNotificationUseCase.kt
│           └── HandleNotificationActionUseCase.kt
│
├── data/                           # ENHANCED - Data layer
│   ├── local/
│   │   ├── database/              # Room DB
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   └── entity/
│   │   ├── preferences/           # ENHANCED
│   │   │   ├── SecurePreferences.kt    # NEW
│   │   │   ├── EncryptedPrefsImpl.kt   # NEW
│   │   │   └── PreferencesManager.kt
│   │   └── cache/                 # NEW - Caching layer
│   │       └── MemoryCache.kt
│   ├── mapper/                    # NEW - Entity mappers
│   │   ├── WordMapper.kt
│   │   └── QuizMapper.kt
│   └── repository/                # Repository implementations
│       ├── WordRepositoryImpl.kt
│       ├── QuizRepositoryImpl.kt
│       └── UserRepositoryImpl.kt
│
├── presentation/                   # REORGANIZED
│   ├── common/
│   │   ├── components/            # Reusable UI components
│   │   │   ├── LoadingState.kt
│   │   │   ├── ErrorState.kt
│   │   │   └── EmptyState.kt
│   │   └── state/                 # NEW - UI state management
│   │       ├── UiState.kt
│   │       └── UiEvent.kt
│   ├── quiz/
│   │   ├── QuizScreen.kt
│   │   ├── QuizViewModel.kt
│   │   └── QuizUiState.kt         # NEW
│   ├── settings/
│   │   ├── SettingsScreen.kt
│   │   ├── SettingsViewModel.kt
│   │   ├── NotificationSettingsScreen.kt
│   │   └── NotificationSettingsViewModel.kt
│   └── navigation/
│       └── NavGraph.kt
│
├── di/                            # Dependency Injection
│   ├── AppModule.kt               # NEW - App-level DI
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   ├── NetworkModule.kt           # NEW - Future
│   └── SecurityModule.kt          # NEW
│
├── notification/                  # ENHANCED
│   ├── NotificationManager.kt     # Renamed/Enhanced
│   ├── NotificationScheduler.kt
│   ├── NotificationActionReceiver.kt
│   └── NotificationHelper.kt
│
├── worker/                        # Background work
│   ├── WordNotificationWorker.kt
│   └── SyncWorker.kt             # NEW - Future
│
└── TrainVocApplication.kt         # ENHANCED - App initialization
```

---

## Testing Infrastructure

### 2.1 Testing Strategy

#### Test Pyramid
```
           ╱╲
          ╱  ╲         E2E Tests (5%)
         ╱────╲        - Full user flows
        ╱      ╲       - Critical paths
       ╱────────╲
      ╱          ╲     Integration Tests (25%)
     ╱────────────╲    - Repository + DB
    ╱              ╲   - ViewModel + UseCase
   ╱────────────────╲
  ╱                  ╲ Unit Tests (70%)
 ╱────────────────────╲ - Use cases
╱                      ╲ - ViewModels
────────────────────────  - Utilities
```

#### Target Coverage
- **Overall**: 80%+
- **Domain Layer**: 90%+
- **Data Layer**: 85%+
- **Presentation Layer**: 70%+

### 2.2 Dependencies

```kotlin
// app/build.gradle.kts - Testing Dependencies

dependencies {
    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.20")

    // Mocking
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")

    // Coroutines Testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Flow Testing
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // Architecture Components Testing
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Hilt Testing
    testImplementation("com.google.dagger:hilt-android-testing:2.48.1")
    kspTest("com.google.dagger:hilt-compiler:2.48.1")

    // Room Testing
    testImplementation("androidx.room:room-testing:2.6.1")

    // Truth Assertions
    testImplementation("com.google.truth:truth:1.1.5")

    // Android Instrumented Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")

    // Compose Testing
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
}
```

### 2.3 Test Implementation Specifications

#### 2.3.1 Base Test Classes

**File**: `app/src/test/java/com/gultekinahmetabdullah/trainvoc/testing/BaseTest.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Base test class providing common test infrastructure
 * - Coroutine test dispatcher
 * - InstantTaskExecutor for LiveData/StateFlow
 * - MockK cleanup
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val testDispatcher = StandardTestDispatcher()

    @Before
    open fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    open fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }
}
```

**File**: `app/src/test/java/com/gultekinahmetabdullah/trainvoc/testing/TestDispatcherProvider.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

/**
 * Test implementation of DispatcherProvider
 * Uses TestDispatcher for all coroutines
 */
class TestDispatcherProvider : DispatcherProvider {
    private val testDispatcher = StandardTestDispatcher()

    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}

/**
 * Production DispatcherProvider interface
 * Inject this to make ViewModels testable
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
```

#### 2.3.2 ViewModel Test Example

**File**: `app/src/test/java/com/gultekinahmetabdullah/trainvoc/viewmodel/NotificationSettingsViewModelTest.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import com.gultekinahmetabdullah.trainvoc.notification.NotificationScheduler
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NotificationSettingsViewModelTest : BaseTest() {

    private lateinit var viewModel: NotificationSettingsViewModel
    private lateinit var mockPreferences: NotificationPreferences
    private lateinit var mockScheduler: NotificationScheduler

    @Before
    override fun setup() {
        super.setup()

        mockPreferences = mockk(relaxed = true)
        mockScheduler = mockk(relaxed = true)

        // Stub default values
        every { mockPreferences.wordQuizEnabled } returns false
        every { mockPreferences.wordQuizIntervalMinutes } returns 60
        every { mockPreferences.enabledLevels } returns setOf("A1", "A2")

        viewModel = NotificationSettingsViewModel(mockPreferences, mockScheduler)
    }

    @Test
    fun `when word quiz enabled, scheduler is called`() = runTest {
        // When
        viewModel.setWordQuizEnabled(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferences.wordQuizEnabled = true }
        verify { mockScheduler.scheduleWordQuiz(any()) }
    }

    @Test
    fun `when word quiz disabled, scheduler cancels work`() = runTest {
        // Given
        every { mockPreferences.wordQuizEnabled } returns true

        // When
        viewModel.setWordQuizEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferences.wordQuizEnabled = false }
        verify { mockScheduler.cancelWordQuiz(any()) }
    }

    @Test
    fun `when frequency changed, preference is updated and scheduler reschedules`() = runTest {
        // Given
        every { mockPreferences.wordQuizEnabled } returns true

        // When
        viewModel.setWordQuizInterval(30)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferences.wordQuizIntervalMinutes = 30 }
        verify { mockScheduler.scheduleWordQuiz(any()) }
    }

    @Test
    fun `when level toggled on, level is added to preferences`() = runTest {
        // Given
        val initialLevels = setOf("A1", "A2")
        every { mockPreferences.enabledLevels } returns initialLevels

        // When
        viewModel.toggleLevel("B1", true)

        // Then
        verify { mockPreferences.enabledLevels = setOf("A1", "A2", "B1") }
    }

    @Test
    fun `when level toggled off, level is removed from preferences`() = runTest {
        // Given
        val initialLevels = setOf("A1", "A2", "B1")
        every { mockPreferences.enabledLevels } returns initialLevels

        // When
        viewModel.toggleLevel("B1", false)

        // Then
        verify { mockPreferences.enabledLevels = setOf("A1", "A2") }
    }

    @Test
    fun `wordQuizEnabled state flow emits correct values`() = runTest {
        // Given
        every { mockPreferences.wordQuizEnabled } returns false

        viewModel.wordQuizEnabled.test {
            // Initial value
            assertThat(awaitItem()).isFalse()

            // When enabled
            viewModel.setWordQuizEnabled(true)
            assertThat(awaitItem()).isTrue()

            // When disabled
            viewModel.setWordQuizEnabled(false)
            assertThat(awaitItem()).isFalse()
        }
    }
}
```

#### 2.3.3 Repository Test Example

**File**: `app/src/test/java/com/gultekinahmetabdullah/trainvoc/repository/WordRepositoryImplTest.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.core.common.Result
import com.gultekinahmetabdullah.trainvoc.data.local.database.dao.WordDao
import com.gultekinahmetabdullah.trainvoc.data.local.database.entity.WordEntity
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WordRepositoryImplTest : BaseTest() {

    private lateinit var repository: WordRepositoryImpl
    private lateinit var mockWordDao: WordDao

    @Before
    override fun setup() {
        super.setup()
        mockWordDao = mockk(relaxed = true)
        repository = WordRepositoryImpl(mockWordDao)
    }

    @Test
    fun `getRandomWord returns success with word when dao returns word`() = runTest {
        // Given
        val wordEntity = WordEntity(
            wordId = 1,
            word = "serendipity",
            meaning = "finding something good without looking for it",
            level = "C1",
            statId = 1
        )
        coEvery { mockWordDao.getRandomWordForNotification(any(), any()) } returns wordEntity

        // When
        val result = repository.getRandomWord(includeLearnedWords = false)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data.word).isEqualTo("serendipity")
    }

    @Test
    fun `getRandomWord returns error when dao returns null`() = runTest {
        // Given
        coEvery { mockWordDao.getRandomWordForNotification(any(), any()) } returns null

        // When
        val result = repository.getRandomWord(includeLearnedWords = false)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getRandomWord handles database exception`() = runTest {
        // Given
        coEvery { mockWordDao.getRandomWordForNotification(any(), any()) } throws Exception("DB Error")

        // When
        val result = repository.getRandomWord(includeLearnedWords = false)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception.message).contains("DB Error")
    }
}
```

#### 2.3.4 Use Case Test Example

**File**: `app/src/test/java/com/gultekinahmetabdullah/trainvoc/domain/usecase/GetRandomWordUseCaseTest.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.core.common.Result
import com.gultekinahmetabdullah.trainvoc.domain.model.Word
import com.gultekinahmetabdullah.trainvoc.domain.repository.WordRepository
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetRandomWordUseCaseTest : BaseTest() {

    private lateinit var useCase: GetRandomWordUseCase
    private lateinit var mockRepository: WordRepository

    @Before
    override fun setup() {
        super.setup()
        mockRepository = mockk()
        useCase = GetRandomWordUseCase(mockRepository)
    }

    @Test
    fun `invoke returns word when repository succeeds`() = runTest {
        // Given
        val expectedWord = Word(
            id = 1,
            word = "ephemeral",
            meaning = "lasting for a very short time",
            level = "C1"
        )
        coEvery { mockRepository.getRandomWord(any(), any()) } returns Result.Success(expectedWord)

        // When
        val result = useCase(
            levels = listOf("C1"),
            includeLearnedWords = false
        )

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(expectedWord)
    }

    @Test
    fun `invoke propagates repository error`() = runTest {
        // Given
        val expectedError = Exception("No words found")
        coEvery { mockRepository.getRandomWord(any(), any()) } returns Result.Error(expectedError)

        // When
        val result = useCase(
            levels = listOf("C1"),
            includeLearnedWords = false
        )

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).exception.message).isEqualTo("No words found")
    }
}
```

### 2.4 Compose UI Test Example

**File**: `app/src/androidTest/java/com/gultekinahmetabdullah/trainvoc/ui/NotificationSettingsScreenTest.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.gultekinahmetabdullah.trainvoc.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NotificationSettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun notificationSettings_displayCorrectly() {
        // Navigate to notification settings
        composeTestRule.onNodeWithText("Notification Settings").performClick()

        // Verify main components are visible
        composeTestRule.onNodeWithText("Word Quiz Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enable Word Quiz").assertIsDisplayed()
        composeTestRule.onNodeWithText("Frequency").assertIsDisplayed()
    }

    @Test
    fun toggleWordQuiz_updatesState() {
        composeTestRule.onNodeWithText("Notification Settings").performClick()

        // Find and toggle the switch
        composeTestRule.onNode(
            hasText("Enable Word Quiz")
                .and(hasAnyAncestor(hasTestTag("word_quiz_switch")))
        ).performClick()

        // Verify frequency options become visible
        composeTestRule.onNodeWithText("Frequency").assertIsDisplayed()
    }

    @Test
    fun sendTestNotification_buttonWorks() {
        composeTestRule.onNodeWithText("Notification Settings").performClick()

        // Enable quiz first
        composeTestRule.onNode(hasText("Enable Word Quiz")).performClick()

        // Click test notification button
        composeTestRule.onNodeWithText("Send Test Notification").performClick()

        // Verify (in real test, you'd verify notification was sent)
        // This is a smoke test to ensure button is clickable
    }
}
```

---

## Security Hardening

### 3.1 Encrypted Storage Implementation

#### 3.1.1 Secure Preferences Interface

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/security/SecurePreferences.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure preferences interface
 * Provides encrypted storage for sensitive data
 */
interface SecurePreferences {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?
    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Long
    fun putStringSet(key: String, value: Set<String>)
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String>
    fun remove(key: String)
    fun clear()
    fun contains(key: String): Boolean
}

/**
 * Implementation using Android EncryptedSharedPreferences
 * Uses AES256-GCM encryption for data at rest
 */
@Singleton
class SecurePreferencesImpl @Inject constructor(
    context: Context
) : SecurePreferences {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun putString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return encryptedPrefs.getString(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        encryptedPrefs.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return encryptedPrefs.getInt(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return encryptedPrefs.getBoolean(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        encryptedPrefs.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return encryptedPrefs.getLong(key, defaultValue)
    }

    override fun putStringSet(key: String, value: Set<String>) {
        encryptedPrefs.edit().putStringSet(key, value).apply()
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
        return encryptedPrefs.getStringSet(key, defaultValue) ?: defaultValue
    }

    override fun remove(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }

    override fun clear() {
        encryptedPrefs.edit().clear().apply()
    }

    override fun contains(key: String): Boolean {
        return encryptedPrefs.contains(key)
    }

    companion object {
        private const val ENCRYPTED_PREFS_FILE_NAME = "trainvoc_secure_prefs"
    }
}
```

#### 3.1.2 Migration from Plain to Encrypted Preferences

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/security/PreferencesMigration.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.security

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

/**
 * Migrates existing plain SharedPreferences to encrypted storage
 * Should be run once on app upgrade
 */
class PreferencesMigration(
    private val context: Context,
    private val securePreferences: SecurePreferences
) {

    fun migrateNotificationPreferences() {
        try {
            val plainPrefs = context.getSharedPreferences(PLAIN_PREFS_NAME, Context.MODE_PRIVATE)

            if (plainPrefs.getBoolean(MIGRATION_COMPLETE_KEY, false)) {
                Timber.d("Migration already complete")
                return
            }

            // Migrate each preference
            migrateBoolean(plainPrefs, "word_quiz_enabled")
            migrateInt(plainPrefs, "word_quiz_interval")
            migrateStringSet(plainPrefs, "enabled_levels")
            migrateStringSet(plainPrefs, "enabled_exams")
            migrateBoolean(plainPrefs, "include_learned_words")
            migrateBoolean(plainPrefs, "quiet_hours_enabled")
            migrateInt(plainPrefs, "quiet_hours_start")
            migrateInt(plainPrefs, "quiet_hours_end")

            // Mark migration complete
            plainPrefs.edit().putBoolean(MIGRATION_COMPLETE_KEY, true).apply()

            Timber.i("Preferences migration completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to migrate preferences")
        }
    }

    private fun migrateBoolean(plainPrefs: SharedPreferences, key: String) {
        if (plainPrefs.contains(key)) {
            val value = plainPrefs.getBoolean(key, false)
            securePreferences.putBoolean(key, value)
        }
    }

    private fun migrateInt(plainPrefs: SharedPreferences, key: String) {
        if (plainPrefs.contains(key)) {
            val value = plainPrefs.getInt(key, 0)
            securePreferences.putInt(key, value)
        }
    }

    private fun migrateString(plainPrefs: SharedPreferences, key: String) {
        if (plainPrefs.contains(key)) {
            val value = plainPrefs.getString(key, null)
            value?.let { securePreferences.putString(key, it) }
        }
    }

    private fun migrateStringSet(plainPrefs: SharedPreferences, key: String) {
        if (plainPrefs.contains(key)) {
            val value = plainPrefs.getStringSet(key, emptySet()) ?: emptySet()
            securePreferences.putStringSet(key, value)
        }
    }

    companion object {
        private const val PLAIN_PREFS_NAME = "notification_prefs"
        private const val MIGRATION_COMPLETE_KEY = "migration_complete_v1"
    }
}
```

#### 3.1.3 Updated NotificationPreferences

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/notification/NotificationPreferences.kt` (REFACTORED)
```kotlin
package com.gultekinahmetabdullah.trainvoc.notification

import com.gultekinahmetabdullah.trainvoc.core.security.SecurePreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification preferences using encrypted storage
 * All sensitive user settings are encrypted at rest
 */
@Singleton
class NotificationPreferences @Inject constructor(
    private val securePreferences: SecurePreferences
) {

    // Word Quiz Settings
    var wordQuizEnabled: Boolean
        get() = securePreferences.getBoolean(KEY_WORD_QUIZ_ENABLED, false)
        set(value) = securePreferences.putBoolean(KEY_WORD_QUIZ_ENABLED, value)

    var wordQuizIntervalMinutes: Int
        get() = securePreferences.getInt(KEY_WORD_QUIZ_INTERVAL, 60)
        set(value) = securePreferences.putInt(KEY_WORD_QUIZ_INTERVAL, value)

    // Filter Settings
    var enabledLevels: Set<String>
        get() = securePreferences.getStringSet(KEY_ENABLED_LEVELS, DEFAULT_LEVELS)
        set(value) = securePreferences.putStringSet(KEY_ENABLED_LEVELS, value)

    var enabledExams: Set<String>
        get() = securePreferences.getStringSet(KEY_ENABLED_EXAMS, DEFAULT_EXAMS)
        set(value) = securePreferences.putStringSet(KEY_ENABLED_EXAMS, value)

    var includeLearnedWords: Boolean
        get() = securePreferences.getBoolean(KEY_INCLUDE_LEARNED, false)
        set(value) = securePreferences.putBoolean(KEY_INCLUDE_LEARNED, value)

    var includeLowAccuracyWords: Boolean
        get() = securePreferences.getBoolean(KEY_INCLUDE_LOW_ACCURACY, true)
        set(value) = securePreferences.putBoolean(KEY_INCLUDE_LOW_ACCURACY, value)

    // Quiet Hours Settings
    var quietHoursEnabled: Boolean
        get() = securePreferences.getBoolean(KEY_QUIET_HOURS_ENABLED, false)
        set(value) = securePreferences.putBoolean(KEY_QUIET_HOURS_ENABLED, value)

    var quietHoursStart: Int
        get() = securePreferences.getInt(KEY_QUIET_HOURS_START, 22)
        set(value) = securePreferences.putInt(KEY_QUIET_HOURS_START, value)

    var quietHoursEnd: Int
        get() = securePreferences.getInt(KEY_QUIET_HOURS_END, 8)
        set(value) = securePreferences.putInt(KEY_QUIET_HOURS_END, value)

    // Other Notification Settings
    var dailyRemindersEnabled: Boolean
        get() = securePreferences.getBoolean(KEY_DAILY_REMINDERS, true)
        set(value) = securePreferences.putBoolean(KEY_DAILY_REMINDERS, value)

    var streakAlertsEnabled: Boolean
        get() = securePreferences.getBoolean(KEY_STREAK_ALERTS, true)
        set(value) = securePreferences.putBoolean(KEY_STREAK_ALERTS, value)

    var wordOfDayEnabled: Boolean
        get() = securePreferences.getBoolean(KEY_WORD_OF_DAY, true)
        set(value) = securePreferences.putBoolean(KEY_WORD_OF_DAY, value)

    companion object {
        // Keys
        private const val KEY_WORD_QUIZ_ENABLED = "word_quiz_enabled"
        private const val KEY_WORD_QUIZ_INTERVAL = "word_quiz_interval"
        private const val KEY_ENABLED_LEVELS = "enabled_levels"
        private const val KEY_ENABLED_EXAMS = "enabled_exams"
        private const val KEY_INCLUDE_LEARNED = "include_learned"
        private const val KEY_INCLUDE_LOW_ACCURACY = "include_low_accuracy"
        private const val KEY_QUIET_HOURS_ENABLED = "quiet_hours_enabled"
        private const val KEY_QUIET_HOURS_START = "quiet_hours_start"
        private const val KEY_QUIET_HOURS_END = "quiet_hours_end"
        private const val KEY_DAILY_REMINDERS = "daily_reminders"
        private const val KEY_STREAK_ALERTS = "streak_alerts"
        private const val KEY_WORD_OF_DAY = "word_of_day"

        // Defaults
        private val DEFAULT_LEVELS = setOf("A1", "A2", "B1", "B2", "C1", "C2")
        private val DEFAULT_EXAMS = setOf("YDS")
    }
}
```

### 3.2 ProGuard Rules Enhancement

**File**: `app/proguard-rules.pro` (ADDITIONS)
```proguard
# Security - Keep encryption classes
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# Keep custom security classes
-keep class com.gultekinahmetabdullah.trainvoc.core.security.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Obfuscate notification internals
-keep class com.gultekinahmetabdullah.trainvoc.notification.NotificationActionReceiver { *; }
-keep public class * extends android.content.BroadcastReceiver

# Keep Room database entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class **_HiltComponents$** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
-keep class androidx.work.** { *; }

# Keep data classes
-keepclassmembers class com.gultekinahmetabdullah.trainvoc.domain.model.** {
    *;
}

# Remove debug code
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(...);
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void checkFieldIsNotNull(...);
}
```

### 3.3 Root Detection (Optional)

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/security/RootDetection.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.security

import android.os.Build
import java.io.File

/**
 * Simple root detection
 * Note: This is security through obscurity - determined attackers can bypass
 * Use SafetyNet/Play Integrity API for production
 */
object RootDetection {

    private val ROOT_INDICATORS = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    /**
     * Check if device appears to be rooted
     * This is a basic check and can be bypassed
     */
    fun isDeviceRooted(): Boolean {
        return checkRootFiles() || checkRootBuildTags() || checkSuBinary()
    }

    private fun checkRootFiles(): Boolean {
        return ROOT_INDICATORS.any { path ->
            File(path).exists()
        }
    }

    private fun checkRootBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkSuBinary(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("which su")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            reader.close()
            output.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
```

---

## Error Handling & Resilience

### 4.1 Result Wrapper

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/common/Result.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.common

/**
 * A generic class that holds a value or an exception
 * Used to represent success or failure in operations
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * Extension to check if result is successful
 */
val Result<*>.succeeded: Boolean
    get() = this is Result.Success

/**
 * Extension to get data or null
 */
fun <T> Result<T>.dataOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

/**
 * Extension to map success data
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception)
        Result.Loading -> Result.Loading
    }
}

/**
 * Extension to handle both success and error
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (Exception) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

/**
 * Extension to get data or throw exception
 */
fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> throw exception
        Result.Loading -> throw IllegalStateException("Result is still loading")
    }
}

/**
 * Extension to get data or default value
 */
fun <T> Result<T>.getOrDefault(defaultValue: T): T {
    return when (this) {
        is Result.Success -> data
        else -> defaultValue
    }
}
```

### 4.2 Error Types

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/error/AppError.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.error

/**
 * Application-specific errors
 * Provides type-safe error handling
 */
sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    // Database Errors
    class DatabaseError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class WordNotFoundError : AppError("Word not found in database")
    class StatisticsNotFoundError : AppError("Statistics not found")

    // Notification Errors
    class NotificationSchedulingError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class NotificationPermissionDeniedError : AppError("Notification permission denied")

    // Validation Errors
    class InvalidInputError(message: String) : AppError(message)
    class InvalidQuizParameterError(message: String) : AppError(message)

    // Network Errors (Future)
    class NetworkError(message: String, cause: Throwable? = null) : AppError(message, cause)
    class NoInternetError : AppError("No internet connection")
    class TimeoutError : AppError("Operation timed out")

    // Unknown Errors
    class UnknownError(message: String = "An unknown error occurred", cause: Throwable? = null) : AppError(message, cause)
}

/**
 * Convert generic exceptions to AppError
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is java.net.UnknownHostException -> AppError.NoInternetError()
        is java.net.SocketTimeoutException -> AppError.TimeoutError()
        else -> AppError.UnknownError(message ?: "Unknown error", this)
    }
}
```

### 4.3 UI State Pattern

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/presentation/common/state/UiState.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.presentation.common.state

/**
 * Generic UI state wrapper
 * Represents different states a UI can be in
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}

/**
 * Extension to check if UI state is loading
 */
val UiState<*>.isLoading: Boolean
    get() = this is UiState.Loading

/**
 * Extension to check if UI state is success
 */
val UiState<*>.isSuccess: Boolean
    get() = this is UiState.Success

/**
 * Extension to check if UI state is error
 */
val UiState<*>.isError: Boolean
    get() = this is UiState.Error

/**
 * Convert Result to UiState
 */
fun <T> com.gultekinahmetabdullah.trainvoc.core.common.Result<T>.toUiState(): UiState<T> {
    return when (this) {
        is com.gultekinahmetabdullah.trainvoc.core.common.Result.Success -> UiState.Success(data)
        is com.gultekinahmetabdullah.trainvoc.core.common.Result.Error -> UiState.Error(
            message = exception.message ?: "An error occurred",
            throwable = exception
        )
        com.gultekinahmetabdullah.trainvoc.core.common.Result.Loading -> UiState.Loading
    }
}
```

### 4.4 Reusable UI State Composables

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/presentation/common/components/StateComponents.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Loading state composable
 * Shows circular progress indicator with optional message
 */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            if (message != null) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Error state composable
 * Shows error message with retry button
 */
@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (onRetry != null) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

/**
 * Empty state composable
 * Shows when there's no data to display
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    action: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            action?.invoke()
        }
    }
}

/**
 * Generic state handler composable
 * Automatically displays loading, error, or success content
 */
@Composable
fun <T> StateHandler(
    state: com.gultekinahmetabdullah.trainvoc.presentation.common.state.UiState<T>,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { LoadingState() },
    errorContent: @Composable (String) -> Unit = { message ->
        ErrorState(message = message, onRetry = onRetry)
    },
    successContent: @Composable (T) -> Unit
) {
    when (state) {
        is com.gultekinahmetabdullah.trainvoc.presentation.common.state.UiState.Idle -> {
            // Show nothing or placeholder
        }
        is com.gultekinahmetabdullah.trainvoc.presentation.common.state.UiState.Loading -> {
            loadingContent()
        }
        is com.gultekinahmetabdullah.trainvoc.presentation.common.state.UiState.Error -> {
            errorContent(state.message)
        }
        is com.gultekinahmetabdullah.trainvoc.presentation.common.state.UiState.Success -> {
            successContent(state.data)
        }
    }
}
```

This is getting very long. Should I continue with the remaining sections (Monitoring, CI/CD, etc.) or would you like me to commit what we have and create separate documents for each remaining section?