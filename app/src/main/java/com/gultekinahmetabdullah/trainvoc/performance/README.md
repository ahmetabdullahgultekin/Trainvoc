# Performance Optimization Package

Comprehensive performance optimization utilities for Trainvoc app.

## 📦 Package Contents

### 1. **PerformanceMonitor.kt**
Real-time performance monitoring and metrics tracking.

**Features:**
- Execution time tracking
- Database query performance monitoring
- Memory usage logging
- Performance summary reports

**Usage:**
```kotlin
// Track method execution time
val words = PerformanceMonitor.measureTime("fetchWords") {
    wordRepository.getWords()
}

// Track database query
PerformanceMonitor.trackDatabaseQuery("getWordsByLevel", duration = 45)

// Log memory usage
PerformanceMonitor.logMemoryUsage("AfterWordLoad")

// Print performance summary
PerformanceMonitor.printSummary()
```

**Extension Functions:**
```kotlin
// Simple timing
val result = timeIt("operationName") {
    // Your code here
}

// Suspending functions
val result = timeItSuspend("asyncOperation") {
    // Your suspending code here
}
```

### 2. **MemoryLeakPrevention.kt**
Utilities and patterns to prevent common memory leaks.

**Features:**
- Lifecycle-aware coroutine scopes
- Automatic resource cleanup
- Memory usage monitoring
- Leak-safe listener wrappers

**Usage:**
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    // Lifecycle-aware scope that auto-cancels
    val scope = rememberLifecycleAwareCoroutineScope()

    // Execute on lifecycle destroy
    OnLifecycleDestroy {
        viewModel.cleanup()
    }

    // Launched effect with cleanup
    LaunchedEffectWithCleanup(
        cleanup = { viewModel.stopListening() }
    ) {
        viewModel.startListening()
    }
}
```

**Memory Management:**
```kotlin
// Check if memory is low
if (MemoryManager.isLowMemory()) {
    // Reduce memory usage
    cache.clear()
}

// Get memory usage percentage
val usage = MemoryManager.getMemoryUsagePercentage()

// Suggest GC if needed (use sparingly)
MemoryManager.suggestGcIfNeeded()
```

### 3. **DatabaseOptimization.kt**
Best practices and utilities for Room database optimization.

**Features:**
- Batch insert optimization
- Query performance monitoring
- Database optimization checklist
- Migration best practices

**Usage:**
```kotlin
// Batch insert with transaction (10-100x faster)
batchInsert(database) {
    words.forEach { word ->
        wordDao().insertWord(word)
    }
}

// Monitor query performance
DatabasePerformanceChecklist.monitorQuery("getWords", durationMs = 45)
```

**Optimization Checklist:**
- ✅ Indices on frequently queried columns
- ✅ Transactions for batch operations
- ✅ Flow for reactive queries
- ✅ @Relation for avoiding N+1 queries
- ✅ LIMIT for large result sets
- ✅ Optimized JOIN queries

### 4. **StartupOptimization.kt**
App startup optimization strategies and utilities.

**Features:**
- Lazy initialization
- Background initialization
- Parallel initialization
- StrictMode configuration
- WorkManager optimization

**Usage:**
```kotlin
class TrainvocApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable StrictMode in debug
        StartupOptimizer.enableStrictMode(BuildConfig.DEBUG)

        // Initialize in background
        StartupOptimizer.initInBackground {
            initAnalytics()
            initCrashReporting()
        }
    }

    // Lazy initialization
    private val analytics by StartupOptimizer.lazyInit {
        Analytics.initialize(this)
    }
}
```

**Parallel Initialization:**
```kotlin
StartupOptimizer.initInParallel(
    { initDatabase() },
    { initNetworking() },
    { initAnalytics() }
)
```

## 🎯 Performance Targets

### App Startup
- **Cold start**: < 1 second (target: < 400ms)
- **Warm start**: < 200ms (target: < 100ms)
- **Application.onCreate()**: < 50ms
- **Activity creation**: < 150ms

### Database Queries
- **Simple SELECT**: < 10ms
- **JOIN queries**: < 50ms
- **Bulk INSERT (100 items)**: < 100ms

### Memory Usage
- **Idle memory**: < 50MB
- **Active use**: < 150MB
- **Peak memory**: < 200MB

### UI Performance
- **Frame rate**: 60 FPS (16.67ms per frame)
- **Jank-free scrolling**: < 5% dropped frames
- **Animation smoothness**: 60 FPS

## 📊 Performance Monitoring

### How to Monitor Performance

**1. Enable Performance Monitoring:**
```kotlin
// In debug builds
PerformanceMonitor.enabled = true
```

**2. Track Operations:**
```kotlin
// Wrap expensive operations
PerformanceMonitor.measureTime("loadWords") {
    loadWords()
}
```

**3. View Metrics:**
```kotlin
// Print summary at any time
PerformanceMonitor.printSummary()
```

### Performance Metrics to Track
- Method execution times
- Database query durations
- Memory usage over time
- Network request latency
- Frame drops / Jank

## 🚀 Optimization Strategies

### 1. Database Optimization
- ✅ **Indices**: Added on level, stat_id, last_reviewed, learned, correct_count, wrong_count
- ✅ **Transactions**: Batch operations wrapped in withTransaction()
- ✅ **Flow**: Used for reactive UI updates
- ✅ **Query Optimization**: Efficient JOIN queries, avoiding N+1 problems

### 2. Memory Optimization
- ✅ **ViewModel Pattern**: Data stored in ViewModels, not Activities
- ✅ **Lifecycle-aware**: Coroutines auto-cancel on lifecycle destroy
- ✅ **@Immutable annotations**: Compose optimization
- ✅ **Resource cleanup**: Proper disposal of resources

### 3. Startup Optimization
- ✅ **Lazy Initialization**: Non-critical components initialized on demand
- ✅ **Background Init**: Heavy work moved off main thread
- ✅ **Minimal Application.onCreate()**: Only critical init in Application class
- ✅ **StrictMode**: Enabled in debug to catch violations

### 4. UI Optimization
- ✅ **Compose Optimizations**: @Immutable, remember(), derivedStateOf()
- ✅ **Animation Performance**: Hardware-accelerated animations
- ✅ **Lazy Loading**: LazyColumn with key() for efficient list rendering
- ✅ **Image Optimization**: Proper sizing and caching

## ⚠️ Common Pitfalls to Avoid

### Memory Leaks
- ❌ Static references to Context/Activity
- ❌ Non-cancelled coroutines
- ❌ Unregistered listeners
- ✅ Use applicationContext for long-lived objects
- ✅ Cancel coroutines in onDispose
- ✅ Unregister listeners properly

### Database Performance
- ❌ Queries on main thread
- ❌ Missing indices on filtered columns
- ❌ SELECT * when only need few columns
- ❌ Individual inserts without transaction
- ✅ Use suspend functions or Flow
- ✅ Add @Index annotations
- ✅ Specify column names
- ✅ Batch with withTransaction()

### Startup Performance
- ❌ Heavy work in Application.onCreate()
- ❌ Synchronous initialization
- ❌ Blocking main thread
- ✅ Lazy initialization
- ✅ Background threads
- ✅ Parallel initialization

## 📈 Performance Testing

### Manual Testing
1. Enable StrictMode in debug builds
2. Monitor logcat for performance warnings
3. Use PerformanceMonitor.printSummary()
4. Check memory usage with Android Profiler

### Automated Testing
```kotlin
@Test
fun `test database query performance`() {
    val duration = measureTimeMillis {
        runBlocking {
            wordDao.getAllWords().first()
        }
    }

    // Query should complete in < 50ms
    assert(duration < 50) {
        "Query took too long: ${duration}ms"
    }
}
```

### Profiling Tools
- **Android Studio Profiler**: CPU, Memory, Network profiling
- **Systrace**: System-level performance analysis
- **Method Tracing**: Detailed method execution profiling
- **Layout Inspector**: UI hierarchy and performance

## 🔧 Configuration

### Debug vs Release

**Debug Build:**
- StrictMode enabled
- Performance logging enabled
- Detailed error messages
- Source maps included

**Release Build:**
- StrictMode disabled
- Performance logging disabled (or minimal)
- Proguard/R8 enabled
- APK size optimized

### Feature Flags
```kotlin
object PerformanceConfig {
    val ENABLE_MONITORING = BuildConfig.DEBUG
    val LOG_SLOW_QUERIES = BuildConfig.DEBUG
    val ENABLE_STRICT_MODE = BuildConfig.DEBUG
    val PERFORMANCE_LOGGING_LEVEL = if (BuildConfig.DEBUG) {
        Log.DEBUG
    } else {
        Log.ERROR
    }
}
```

## 📚 Additional Resources

### Documentation
- [Android Performance Patterns](https://developer.android.com/topic/performance)
- [Room Performance Best Practices](https://developer.android.com/training/data-storage/room/performance)
- [Compose Performance](https://developer.android.com/jetpack/compose/performance)

### Tools
- [Android Studio Profiler](https://developer.android.com/studio/profile)
- [Perfetto](https://perfetto.dev/) - System profiling
- [LeakCanary](https://square.github.io/leakcanary/) - Memory leak detection

## 🎓 Best Practices Summary

1. **Measure First** - Use profiling tools before optimizing
2. **Optimize Bottlenecks** - Focus on biggest performance issues
3. **Use Indices** - Database queries should use indexed columns
4. **Batch Operations** - Use transactions for multiple DB operations
5. **Lazy Loading** - Load data incrementally with pagination
6. **Background Work** - Keep heavy operations off main thread
7. **Clean Up Resources** - Cancel coroutines, unregister listeners
8. **Monitor Performance** - Track metrics in production
9. **Test Performance** - Include performance tests in CI/CD
10. **Profile Regularly** - Use Android Studio Profiler often

---

## Sprint 8: Performance Optimization ⚡

This performance package was created as part of Sprint 8 to optimize:
- ✅ Database indexing and query performance
- ✅ Memory management and leak prevention
- ✅ App startup time optimization
- ✅ WorkManager task efficiency
- ✅ Performance monitoring and metrics

**Result**: Significantly improved app performance, faster startup times, optimized database queries, and comprehensive performance monitoring tools for ongoing optimization.
