# Monitoring & Observability Design

## Overview
Comprehensive monitoring strategy using Firebase and structured logging for production-grade observability.

---

## 1. Firebase Integration

### 1.1 Dependencies

```kotlin
// app/build.gradle.kts

plugins {
    // ... existing plugins
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

dependencies {
    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Firebase Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase Performance Monitoring
    implementation("com.google.firebase:firebase-perf-ktx")

    // Timber for logging
    implementation("com.jakewharton.timber:timber:5.0.1")
}
```

### 1.2 Firebase Configuration

**File**: `app/google-services.json` (Generated from Firebase Console)
- Download from Firebase Console → Project Settings
- Place in `app/` directory
- Add to `.gitignore`

**File**: `build.gradle.kts` (Project level)
```kotlin
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
    }
}
```

---

## 2. Structured Logging with Timber

### 2.1 Logger Configuration

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/util/Logger.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.util

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Centralized logging utility
 * Uses Timber with custom trees for debug and release builds
 */
object Logger {

    fun init(isDebug: Boolean) {
        if (isDebug) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    /**
     * Log debug message
     */
    fun d(message: String, vararg args: Any?) {
        Timber.d(message, *args)
    }

    /**
     * Log info message
     */
    fun i(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }

    /**
     * Log warning
     */
    fun w(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.w(throwable, message)
        } else {
            Timber.w(message)
        }
    }

    /**
     * Log error
     */
    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.e(throwable, message)
        } else {
            Timber.e(message)
        }
    }

    /**
     * Log critical error and send to Crashlytics
     */
    fun critical(message: String, throwable: Throwable) {
        Timber.e(throwable, message)
        FirebaseCrashlytics.getInstance().apply {
            log(message)
            recordException(throwable)
        }
    }

    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String) {
        FirebaseCrashlytics.getInstance().setUserId(userId)
    }

    /**
     * Add custom key-value pair to crash reports
     */
    fun setCustomKey(key: String, value: Any) {
        when (value) {
            is String -> FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            is Int -> FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            is Long -> FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            is Float -> FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            is Double -> FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            is Boolean -> FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            else -> FirebaseCrashlytics.getInstance().setCustomKey(key, value.toString())
        }
    }

    /**
     * Debug tree for development
     * Shows detailed logs with clickable links
     */
    private class DebugTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
            return "(${element.fileName}:${element.lineNumber}) ${element.methodName}"
        }
    }

    /**
     * Crashlytics tree for production
     * Sends errors to Firebase Crashlytics
     */
    private class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == android.util.Log.VERBOSE || priority == android.util.Log.DEBUG) {
                return
            }

            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("$priority/$tag: $message")

            if (t != null && priority == android.util.Log.ERROR) {
                crashlytics.recordException(t)
            }
        }
    }
}
```

### 2.2 Application Initialization

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/TrainVocApplication.kt` (ENHANCED)
```kotlin
package com.gultekinahmetabdullah.trainvoc

import android.app.Application
import android.os.StrictMode
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gultekinahmetabdullah.trainvoc.core.security.PreferencesMigration
import com.gultekinahmetabdullah.trainvoc.core.security.SecurePreferences
import com.gultekinahmetabdullah.trainvoc.core.util.Logger
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TrainVocApplication : Application() {

    @Inject
    lateinit var securePreferences: SecurePreferences

    override fun onCreate() {
        super.onCreate()

        // Initialize logger
        Logger.init(BuildConfig.DEBUG)
        Logger.i("TrainVoc Application Starting - Version ${BuildConfig.VERSION_NAME}")

        // Configure Crashlytics
        configureCrashlytics()

        // Enable StrictMode in debug builds
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }

        // Create notification channels
        NotificationHelper.createNotificationChannels(this)

        // Migrate preferences to encrypted storage
        migratePrefere nces()

        Logger.i("TrainVoc Application Initialized Successfully")
    }

    private fun configureCrashlytics() {
        FirebaseCrashlytics.getInstance().apply {
            // Disable crashlytics in debug builds
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

            // Set app version
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
            setCustomKey("version_code", BuildConfig.VERSION_CODE)
            setCustomKey("build_type", BuildConfig.BUILD_TYPE)

            Logger.d("Crashlytics configured - Enabled: ${!BuildConfig.DEBUG}")
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        Logger.d("StrictMode enabled for debugging")
    }

    private fun migratePreferences() {
        try {
            val migration = PreferencesMigration(this, securePreferences)
            migration.migrateNotificationPreferences()
            Logger.i("Preferences migration completed")
        } catch (e: Exception) {
            Logger.e("Preferences migration failed", e)
        }
    }
}
```

---

## 3. Analytics Events

### 3.1 Analytics Manager

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/analytics/AnalyticsManager.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.gultekinahmetabdullah.trainvoc.core.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized analytics tracking
 * All app events flow through here for consistency
 */
@Singleton
class AnalyticsManager @Inject constructor(
    context: Context
) {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    init {
        // Enable analytics collection (can be toggled by user preference)
        analytics.setAnalyticsCollectionEnabled(true)
        Logger.d("Analytics initialized")
    }

    // User Events
    fun logUserCreated(userId: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, "local")
        }
        Logger.setUserId(userId)
    }

    fun logUserLogin(userId: String) {
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, "local")
        }
        Logger.setUserId(userId)
    }

    // Quiz Events
    fun logQuizStarted(level: String, examType: String) {
        analytics.logEvent(EVENT_QUIZ_STARTED) {
            param(PARAM_LEVEL, level)
            param(PARAM_EXAM_TYPE, examType)
        }
    }

    fun logQuizCompleted(
        level: String,
        examType: String,
        score: Int,
        totalQuestions: Int,
        durationSeconds: Long
    ) {
        analytics.logEvent(EVENT_QUIZ_COMPLETED) {
            param(PARAM_LEVEL, level)
            param(PARAM_EXAM_TYPE, examType)
            param(PARAM_SCORE, score.toLong())
            param(PARAM_TOTAL_QUESTIONS, totalQuestions.toLong())
            param(PARAM_DURATION, durationSeconds)
            param(PARAM_ACCURACY, ((score.toDouble() / totalQuestions) * 100).toLong())
        }
    }

    fun logAnswerSubmitted(
        wordId: String,
        isCorrect: Boolean,
        timeTakenMs: Long
    ) {
        analytics.logEvent(EVENT_ANSWER_SUBMITTED) {
            param(PARAM_WORD_ID, wordId)
            param(PARAM_IS_CORRECT, if (isCorrect) 1L else 0L)
            param(PARAM_TIME_TAKEN_MS, timeTakenMs)
        }
    }

    // Notification Events
    fun logNotificationEnabled(notificationType: String) {
        analytics.logEvent(EVENT_NOTIFICATION_ENABLED) {
            param(PARAM_NOTIFICATION_TYPE, notificationType)
        }
    }

    fun logNotificationDisabled(notificationType: String) {
        analytics.logEvent(EVENT_NOTIFICATION_DISABLED) {
            param(PARAM_NOTIFICATION_TYPE, notificationType)
        }
    }

    fun logNotificationSent(notificationType: String) {
        analytics.logEvent(EVENT_NOTIFICATION_SENT) {
            param(PARAM_NOTIFICATION_TYPE, notificationType)
        }
    }

    fun logNotificationInteraction(
        notificationType: String,
        action: String
    ) {
        analytics.logEvent(EVENT_NOTIFICATION_INTERACTION) {
            param(PARAM_NOTIFICATION_TYPE, notificationType)
            param(PARAM_ACTION, action)
        }
    }

    // Word Events
    fun logWordLearned(wordId: String, level: String) {
        analytics.logEvent(EVENT_WORD_LEARNED) {
            param(PARAM_WORD_ID, wordId)
            param(PARAM_LEVEL, level)
        }
    }

    fun logWordReviewed(wordId: String, wasCorrect: Boolean) {
        analytics.logEvent(EVENT_WORD_REVIEWED) {
            param(PARAM_WORD_ID, wordId)
            param(PARAM_IS_CORRECT, if (wasCorrect) 1L else 0L)
        }
    }

    // Settings Events
    fun logSettingChanged(settingName: String, newValue: String) {
        analytics.logEvent(EVENT_SETTING_CHANGED) {
            param(PARAM_SETTING_NAME, settingName)
            param(PARAM_SETTING_VALUE, newValue)
        }
    }

    // Screen View Events
    fun logScreenView(screenName: String, screenClass: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    // User Properties
    fun setUserLevel(level: String) {
        analytics.setUserProperty(USER_PROPERTY_CURRENT_LEVEL, level)
    }

    fun setTotalWordsLearned(count: Int) {
        analytics.setUserProperty(USER_PROPERTY_WORDS_LEARNED, count.toString())
    }

    fun setQuizStreak(days: Int) {
        analytics.setUserProperty(USER_PROPERTY_QUIZ_STREAK, days.toString())
    }

    companion object {
        // Custom Events
        private const val EVENT_QUIZ_STARTED = "quiz_started"
        private const val EVENT_QUIZ_COMPLETED = "quiz_completed"
        private const val EVENT_ANSWER_SUBMITTED = "answer_submitted"
        private const val EVENT_NOTIFICATION_ENABLED = "notification_enabled"
        private const val EVENT_NOTIFICATION_DISABLED = "notification_disabled"
        private const val EVENT_NOTIFICATION_SENT = "notification_sent"
        private const val EVENT_NOTIFICATION_INTERACTION = "notification_interaction"
        private const val EVENT_WORD_LEARNED = "word_learned"
        private const val EVENT_WORD_REVIEWED = "word_reviewed"
        private const val EVENT_SETTING_CHANGED = "setting_changed"

        // Custom Parameters
        private const val PARAM_LEVEL = "level"
        private const val PARAM_EXAM_TYPE = "exam_type"
        private const val PARAM_SCORE = "score"
        private const val PARAM_TOTAL_QUESTIONS = "total_questions"
        private const val PARAM_DURATION = "duration_seconds"
        private const val PARAM_ACCURACY = "accuracy_percent"
        private const val PARAM_WORD_ID = "word_id"
        private const val PARAM_IS_CORRECT = "is_correct"
        private const val PARAM_TIME_TAKEN_MS = "time_taken_ms"
        private const val PARAM_NOTIFICATION_TYPE = "notification_type"
        private const val PARAM_ACTION = "action"
        private const val PARAM_SETTING_NAME = "setting_name"
        private const val PARAM_SETTING_VALUE = "setting_value"

        // User Properties
        private const val USER_PROPERTY_CURRENT_LEVEL = "current_level"
        private const val USER_PROPERTY_WORDS_LEARNED = "words_learned"
        private const val USER_PROPERTY_QUIZ_STREAK = "quiz_streak"
    }
}
```

---

## 4. Performance Monitoring

### 4.1 Custom Traces

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/performance/PerformanceMonitor.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.performance

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.gultekinahmetabdullah.trainvoc.core.util.Logger

/**
 * Performance monitoring wrapper
 * Tracks custom traces for critical app operations
 */
object PerformanceMonitor {

    private val performance = FirebasePerformance.getInstance()

    /**
     * Start a custom trace
     */
    fun startTrace(name: String): Trace {
        val trace = performance.newTrace(name)
        trace.start()
        Logger.d("Performance trace started: $name")
        return trace
    }

    /**
     * Stop and record a trace
     */
    fun stopTrace(trace: Trace) {
        trace.stop()
        Logger.d("Performance trace stopped: ${trace}")
    }

    /**
     * Add metric to trace
     */
    fun addMetric(trace: Trace, metricName: String, value: Long) {
        trace.putMetric(metricName, value)
    }

    /**
     * Add attribute to trace
     */
    fun addAttribute(trace: Trace, attribute: String, value: String) {
        trace.putAttribute(attribute, value)
    }

    /**
     * Helper to measure operation duration
     */
    inline fun <T> measureOperation(traceName: String, operation: () -> T): T {
        val trace = startTrace(traceName)
        return try {
            operation()
        } finally {
            stopTrace(trace)
        }
    }

    // Predefined trace names
    object Traces {
        const val DATABASE_QUERY = "database_query"
        const val QUIZ_GENERATION = "quiz_generation"
        const val NOTIFICATION_SEND = "notification_send"
        const val WORD_FETCH = "word_fetch"
        const val STATISTICS_UPDATE = "statistics_update"
    }
}
```

### 4.2 Usage Example in Repository

```kotlin
// In WordRepositoryImpl
override suspend fun getRandomWord(
    levels: List<String>,
    includeLearnedWords: Boolean
): Result<Word> = withContext(dispatchers.io) {
    PerformanceMonitor.measureOperation(PerformanceMonitor.Traces.WORD_FETCH) {
        try {
            val wordEntity = if (levels.isNotEmpty()) {
                wordDao.getRandomWordFromLevels(levels, includeLearnedWords)
            } else {
                wordDao.getRandomWordForNotification(includeLearnedWords)
            }

            if (wordEntity != null) {
                Result.Success(wordMapper.toDomain(wordEntity))
            } else {
                Result.Error(AppError.WordNotFoundError())
            }
        } catch (e: Exception) {
            Logger.e("Failed to fetch random word", e)
            Result.Error(e.toAppError())
        }
    }
}
```

---

## 5. Error Reporting

### 5.1 Global Error Handler

**File**: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/core/error/GlobalErrorHandler.kt`
```kotlin
package com.gultekinahmetabdullah.trainvoc.core.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gultekinahmetabdullah.trainvoc.core.util.Logger

/**
 * Global uncaught exception handler
 * Logs critical errors before app crashes
 */
class GlobalErrorHandler(
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            Logger.critical("Uncaught exception in thread: ${thread.name}", throwable)

            FirebaseCrashlytics.getInstance().apply {
                log("FATAL: Uncaught exception in ${thread.name}")
                recordException(throwable)
            }
        } catch (e: Exception) {
            // If logging fails, at least try to print to console
            e.printStackTrace()
        } finally {
            // Call default handler to crash the app
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        fun install() {
            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(GlobalErrorHandler(currentHandler))
        }
    }
}
```

### 5.2 Install in Application

```kotlin
// In TrainVocApplication.onCreate()
override fun onCreate() {
    super.onCreate()

    // Install global error handler
    GlobalErrorHandler.install()

    // ... rest of initialization
}
```

---

## 6. Monitoring Dashboard Setup

### 6.1 Firebase Console Configuration

1. **Crashlytics**:
   - Enable Crashlytics in Firebase Console
   - Set up alerts for critical errors
   - Configure crash-free users threshold (> 99%)

2. **Analytics**:
   - Define custom events and parameters
   - Set up conversion funnels
   - Create user segments

3. **Performance**:
   - Enable automatic traces
   - Define custom trace thresholds
   - Set up performance alerts

### 6.2 Key Metrics to Monitor

#### Stability Metrics
- Crash-free users (Target: > 99.5%)
- ANR rate (Target: < 0.5%)
- Fatal errors per user session

#### Performance Metrics
- App start time (Target: < 2s cold, < 1s warm)
- Screen render time (Target: < 500ms)
- Database query time (Target: < 100ms avg)
- Notification delivery time

#### Engagement Metrics
- Daily active users
- Quiz completion rate
- Notification interaction rate
- Word learning velocity

#### Business Metrics
- User retention (D1, D7, D30)
- Feature adoption rate
- User progression through levels

---

## 7. Logging Best Practices

### 7.1 Log Levels

```kotlin
// DEBUG - Development only, detailed information
Logger.d("User selected level: $level")

// INFO - Important business events
Logger.i("Quiz completed: score=$score, duration=$duration")

// WARNING - Recoverable errors, degraded functionality
Logger.w("Failed to send notification, will retry", exception)

// ERROR - Errors that affect functionality
Logger.e("Database query failed", exception)

// CRITICAL - Fatal errors requiring immediate attention
Logger.critical("Failed to initialize database", exception)
```

### 7.2 Structured Logging

```kotlin
// Good - Structured, searchable
Logger.i("quiz_completed", mapOf(
    "level" to "A2",
    "score" to 85,
    "duration_sec" to 120,
    "user_id" to userId
))

// Bad - Unstructured string
Logger.i("User $userId completed A2 quiz with score 85 in 120 seconds")
```

### 7.3 PII (Personally Identifiable Information) Handling

```kotlin
// NEVER log PII directly
Logger.d("User email: ${user.email}") // ❌ BAD

// Use anonymized IDs
Logger.d("User action: user_id=${user.id.hashCode()}") // ✅ GOOD

// Redact sensitive data
Logger.d("Query result: ${data.redactSensitive()}") // ✅ GOOD
```

---

## 8. Implementation Checklist

### Phase 1: Basic Setup (Week 1)
- [ ] Add Firebase dependencies
- [ ] Configure google-services.json
- [ ] Initialize Timber in Application class
- [ ] Set up basic Crashlytics
- [ ] Test crash reporting

### Phase 2: Analytics (Week 2)
- [ ] Implement AnalyticsManager
- [ ] Add screen view tracking
- [ ] Add quiz event tracking
- [ ] Add notification event tracking
- [ ] Set up Firebase Analytics dashboard

### Phase 3: Performance (Week 3)
- [ ] Add Performance Monitoring dependency
- [ ] Implement custom traces
- [ ] Add traces to critical paths
- [ ] Configure performance alerts
- [ ] Optimize slow operations

### Phase 4: Advanced (Week 4)
- [ ] Set up A/B testing (Optional)
- [ ] Implement Remote Config (Optional)
- [ ] Add custom metrics
- [ ] Create monitoring runbooks
- [ ] Document alert response procedures

---

**Next Document**: CI/CD Pipeline Design
