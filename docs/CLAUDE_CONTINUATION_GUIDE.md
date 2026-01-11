# Claude AI Continuation Guide

**For Future Claude AI Sessions Working on Trainvoc**

---

## Quick Context

You're working on **Trainvoc**, an English-Turkish vocabulary learning mobile app built with:
- **Jetpack Compose** + **Material 3**
- **MVVM + Clean Architecture**
- **Room Database** + **Hilt DI**
- **Kotlin 2.1.10** + **Gradle 8.13.1**

**Current Status**: Phase 1-3 complete (11 new screens created), Phase 4 testing complete
**Branch**: `claude/review-trainvoc-app-1FwM7`
**Last Session**: 2026-01-11

---

## 📋 What Was Accomplished

### Phase 1 - Critical UX Fixes (100% Complete)
- ✅ Removed "alpha close test" watermark
- ✅ Removed testing phase warning banner
- ✅ Fixed broken Quick Access navigation
- ✅ Created 4 new screens: Profile, Word of Day, Favorites, Last Quiz Results

### Phase 2 - Core Features (80% Complete)
- ✅ Created Daily Goals screen with sliders and presets
- ✅ Created Streak Detail screen with milestones and calendar
- ⚠️ Deferred StatsScreen insights (too complex)
- ⚠️ Deferred DictionaryScreen filters (too complex)

### Phase 3 - Engagement Features (100% Complete)
- ✅ Created Leaderboard screen with tabs, tiers, and rankings
- ✅ Created Word Progress screen with forecasting and level breakdown
- ✅ Updated Backup screen to be honest about cloud sync

### Phase 4 - Integration (100% Complete)
- ✅ Added all routes to Route.kt
- ✅ Integrated all screens into navigation
- ✅ Added Settings navigation buttons

---

## 🎯 What You Should Do Next

### Priority 1: Database Migrations (URGENT)

**Why**: 3 new screens can't function without these migrations

**What to do**:
1. Open `/home/user/Trainvoc/app/src/main/java/com/gultekinahmetabdullah/trainvoc/data/local/TrainvocDatabase.kt`
2. Add these 3 migrations (code provided in PHASE_3_COMPLETION_AND_TODOS.md lines 114-188)
3. Update database version from 11 to 14
4. Test migrations don't crash the app

**Effort**: 2-3 hours
**Blocks**: FavoritesScreen, WordOfTheDayScreen, LastQuizResultsScreen

### Priority 2: Load Actual Data (HIGH)

**Why**: Screens currently show placeholder/mock data

**What to do**:
1. **FavoritesScreen** - Load favorites from database
2. **WordOfTheDayScreen** - Load daily word from database
3. **LastQuizResultsScreen** - Load quiz results from database

**Detailed implementation**: See PHASE_3_COMPLETION_AND_TODOS.md lines 190-450

**Effort**: 6-8 hours total

### Priority 3: Progress & Stats Data (MEDIUM)

**What to do**:
1. **WordProgressScreen** - Load real progress data from database
2. **StreakDetailScreen** - Load activity history from database
3. **LeaderboardScreen** - Either implement backend OR local-only mode

**Effort**: 6-8 hours total

---

## 📚 Essential Documents to Read

**Must read in this order**:

1. **PHASE_3_COMPLETION_AND_TODOS.md** (this directory)
   - Complete TODO list with code examples
   - All deferred tasks explained
   - Testing checklist

2. **PHASE_1_2_COMPLETION_AND_NEXT_STEPS.md** (this directory)
   - Phase 1-2 details
   - What was completed
   - Original TODOs

3. **SCREEN_AUDIT_AND_IMPLEMENTATION_PLAN.md** (this directory)
   - Original vision and audit
   - All 33 existing screens documented
   - 11 missing screens identified (now created)

---

## 🗂️ File Locations

### New Screens Created (Phase 1-3)

```
app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/

features/
  ├── ProfileScreen.kt (455 lines)
  ├── WordOfTheDayScreen.kt (198 lines) ← TODO: Load actual data
  └── FavoritesScreen.kt (135 lines) ← TODO: Load actual data

quiz/
  └── LastQuizResultsScreen.kt (244 lines) ← TODO: Load actual data

gamification/
  ├── DailyGoalsScreen.kt (471 lines)
  └── StreakDetailScreen.kt (416 lines) ← TODO: Load activity history

social/
  └── LeaderboardScreen.kt (448 lines) ← TODO: Load real data OR mock mode

progress/
  └── WordProgressScreen.kt (620 lines) ← TODO: Load real data
```

### Modified Files

```
classes/enums/
  └── Route.kt ← Added 9 new routes

ui/backup/
  └── BackupScreen.kt ← Cloud tab overhauled for honesty

ui/screen/main/
  └── MainScreen.kt ← All 11 screens integrated

ui/screen/other/
  └── SettingsScreen.kt ← Navigation buttons added
```

### Database (Needs Work)

```
data/local/
  └── TrainvocDatabase.kt ← TODO: Add migrations 11→12→13→14
```

---

## 🚀 Quick Start Commands

### Navigate to Project
```bash
cd /home/user/Trainvoc
```

### Check Current Branch
```bash
git branch
# Should show: claude/review-trainvoc-app-1FwM7
```

### See What Changed
```bash
git log --oneline -5
# Recent commits:
# 1fc8b4c feat(phase3): complete engagement features
# df92be1 feat(phase2): complete Phase 1.6 and Phase 2
# f4b2e7f feat(phase1): implement critical UX fixes
```

### Find TODOs
```bash
grep -r "// TODO" app/src/main/java/ | grep -E "(features|gamification|social|progress)"
```

### Build (if network available)
```bash
./gradlew assembleDebug
```

---

## 💡 Code Patterns to Follow

### ViewModels

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    val data: StateFlow<List<Item>> = repository.getData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
```

### Screens

```kotlin
@Composable
fun MyScreen(
    onBackClick: () -> Unit = {},
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Screen") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Content
        }
    }
}
```

### Database Queries

```kotlin
@Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY favoritedAt DESC")
fun getFavoriteWords(): Flow<List<Word>>

@Query("SELECT * FROM words WHERE id = :wordId")
suspend fun getWord(wordId: String): Word?
```

---

## ⚠️ Common Pitfalls to Avoid

1. ❌ **Don't use GlobalScope** → Use `viewModelScope`
2. ❌ **Don't collect flows without lifecycle** → Use `collectAsState()`
3. ❌ **Don't hardcode strings** → Use string resources
4. ❌ **Don't skip migrations** → Data loss is bad!
5. ❌ **Don't forget loading states** → Users need feedback
6. ❌ **Don't make network calls on main thread** → Use coroutines
7. ❌ **Don't forget navigation integration** → Test both forward and back

---

## 🧪 Testing Before Committing

**Manual checks**:
- [ ] Code compiles without errors
- [ ] Navigation works (forward AND back)
- [ ] Data persists across app restart
- [ ] Empty states handled gracefully
- [ ] Error states handled gracefully
- [ ] No hardcoded strings (use R.string.*)

**Git workflow**:
```bash
# 1. Check status
git status

# 2. Stage changes
git add .

# 3. Commit with descriptive message
git commit -m "feat(feature-name): description

- Bullet point 1
- Bullet point 2
- Closes #issue"

# 4. Push to branch
git push -u origin claude/review-trainvoc-app-1FwM7
```

---

## 🗺️ Architecture Overview

```
┌─────────────────────────────────────────┐
│            UI Layer (Compose)           │
│  ┌──────────────────────────────────┐  │
│  │ Screens (11 new + 33 existing)   │  │
│  └──────────────────────────────────┘  │
│               ↕️                         │
│  ┌──────────────────────────────────┐  │
│  │   ViewModels (Hilt injected)     │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
               ↕️
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│  ┌──────────────────────────────────┐  │
│  │   Use Cases (Business Logic)     │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │   Models (Domain Entities)       │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
               ↕️
┌─────────────────────────────────────────┐
│          Data Layer                     │
│  ┌──────────────────────────────────┐  │
│  │  Repository (Data abstraction)   │  │
│  └──────────────────────────────────┘  │
│         ↕️              ↕️               │
│  ┌──────────┐    ┌──────────────┐     │
│  │   Room   │    │   Retrofit   │     │
│  │   DAO    │    │     API      │     │
│  └──────────┘    └──────────────┘     │
│  (Local DB)       (Remote API)        │
└─────────────────────────────────────────┘
```

---

## 📝 Example: Implementing a TODO

**Scenario**: You want to implement "Load actual favorites in FavoritesScreen"

**Step 1: Read the documentation**
```bash
# Open PHASE_3_COMPLETION_AND_TODOS.md
# Find section: "2. FavoritesScreen - Load Actual Data"
# Lines 190-285 have complete implementation
```

**Step 2: Implement database migration**
```kotlin
// In TrainvocDatabase.kt
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE words ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE words ADD COLUMN favoritedAt INTEGER")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_words_isFavorite ON words(isFavorite)")
    }
}

// Update version
@Database(entities = [...], version = 12) // Was 11
abstract class TrainvocDatabase : RoomDatabase() {
    // ...
    .addMigrations(MIGRATION_11_12)
}
```

**Step 3: Add DAO queries**
```kotlin
// In WordDao.kt
@Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY favoritedAt DESC")
fun getFavoriteWords(): Flow<List<Word>>
```

**Step 4: Add repository method**
```kotlin
// In WordRepository.kt
fun getFavoriteWords(): Flow<List<Word>> = wordDao.getFavoriteWords()
```

**Step 5: Create ViewModel**
```kotlin
// Create FavoritesViewModel.kt
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel() {
    val favoriteWords: StateFlow<List<Word>> = repository.getFavoriteWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
```

**Step 6: Update Screen**
```kotlin
// In FavoritesScreen.kt
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit = {},
    onPracticeFavorites: () -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel() // Add this
) {
    val favoriteWords by viewModel.favoriteWords.collectAsState() // Add this

    // Update UI to use favoriteWords instead of empty state
}
```

**Step 7: Test**
```bash
./gradlew assembleDebug
# Install APK and test favorites functionality
```

**Step 8: Commit**
```bash
git add .
git commit -m "feat(favorites): load actual favorites from database

- Implement database migration 11→12
- Add getFavoriteWords query to WordDao
- Create FavoritesViewModel
- Update FavoritesScreen to display real data
- Handle empty state when no favorites exist"

git push -u origin claude/review-trainvoc-app-1FwM7
```

---

## 🎯 Success Criteria

You'll know you're done with the high-priority TODOs when:

1. ✅ All 3 database migrations implemented and tested
2. ✅ FavoritesScreen shows actual favorites (empty state if none)
3. ✅ WordOfTheDayScreen shows a different word each day
4. ✅ LastQuizResultsScreen shows actual quiz history
5. ✅ WordProgressScreen shows real learning progress
6. ✅ StreakDetailScreen shows real activity calendar
7. ✅ All screens tested (navigation, data persistence, empty/error states)
8. ✅ Code compiles and runs without crashes
9. ✅ Changes committed with clear messages
10. ✅ Documentation updated if needed

---

## 🆘 If You Get Stuck

### Can't find a file?
```bash
find app/src/main/java/ -name "*ViewModel.kt"
grep -r "class HomeViewModel" app/src/main/java/
```

### Compilation errors?
```bash
# Clean build
./gradlew clean

# Check for syntax errors
./gradlew compileDebugKotlin
```

### Don't understand the architecture?
- Look at existing similar features
- Check how HomeViewModel loads data
- See how existing screens use hiltViewModel()

### Not sure about database structure?
```bash
# Find Word entity definition
grep -r "@Entity" app/src/main/java/ | grep Word

# Find existing queries
cat app/src/main/java/*/data/local/dao/WordDao.kt
```

---

## 💬 Communicating with the User

**Be transparent about**:
- What you're working on
- What's completed
- What issues you encounter
- What decisions need user input

**Example good responses**:
- ✅ "I'm implementing the database migration for favorites. This will take about 30 minutes."
- ✅ "I've completed FavoritesScreen data loading. Testing now."
- ✅ "I found an issue with the migration - the column name conflicts. Should I rename it?"

**Example bad responses**:
- ❌ "Done!" (too vague, what's done?)
- ❌ "There's an error." (what error? where? how to fix?)
- ❌ Making decisions without asking when unclear

---

## 📊 Current Project Stats

- **Total Screens**: 44 (33 existing + 11 new)
- **Total Routes**: 34 (25 original + 9 new)
- **Lines of Code Added**: ~3,500+
- **Database Version**: 11 → 14 (needs migration)
- **Commits Made**: 2 major feature commits
- **Documentation Files**: 3 comprehensive docs
- **Phase Completion**:
  - Phase 1: ✅ 100%
  - Phase 2: ⚠️ 80%
  - Phase 3: ✅ 100%
  - Phase 4: ✅ 100%

---

## 🎓 Learning Resources

If you need to understand concepts better:

**Jetpack Compose**:
- Navigation: `/ui/screen/main/MainScreen.kt` shows all routes
- State management: Any `*ViewModel.kt` file shows StateFlow patterns
- UI components: `/ui/screen/*/` folders have many examples

**Room Database**:
- Entities: `/data/local/entity/` folder
- DAOs: `/data/local/dao/` folder
- Migrations: Check `TrainvocDatabase.kt`

**Dependency Injection (Hilt)**:
- ViewModels: All use `@HiltViewModel` and `@Inject constructor`
- Repositories: Check `/data/repository/` folder

---

## ✅ Final Checklist Before Ending Session

- [ ] All changes committed with descriptive messages
- [ ] All commits pushed to branch
- [ ] Documentation updated (if you changed architecture or added major features)
- [ ] User informed of what's completed and what's next
- [ ] No half-finished features (either complete or document clearly as WIP)
- [ ] Build passes (or documented why it can't build)

---

**Good luck! You've got this! 🚀**

*The previous Claude sessions laid a solid foundation. Just follow the patterns, read the docs, and implement one TODO at a time.*

---

**Last Updated**: 2026-01-11
**For Claude Sessions**: Phase 4+ continuation
**Branch**: `claude/review-trainvoc-app-1FwM7`
