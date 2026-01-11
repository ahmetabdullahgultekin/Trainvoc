# Trainvoc - Quick Reference for Claude AI Sessions

**⚡ Ultra-Fast Context - Read This First!**

## 🎯 What You Need to Know (30 seconds)

**Project**: English-Turkish vocabulary learning app (Android)
**Stack**: Kotlin + Jetpack Compose + Material 3 + Room + Hilt
**Architecture**: MVVM + Clean Architecture
**Status**: v1.1.2 - Phase 1-3 Complete ✅
**Branch**: `claude/review-trainvoc-app-1FwM7`

## 📋 Immediate Next Steps

### 🔴 URGENT (Do These First)
1. **Database Migrations** - 3 screens broken without these
   - Migration 11→12: Favorites (code: lines 75-86 in PHASE_3_COMPLETION_AND_TODOS.md)
   - Migration 12→13: Word of Day (code: lines 89-105)
   - Migration 13→14: Quiz History (code: lines 109-136)

2. **Load Real Data** - Replace placeholders
   - FavoritesScreen.kt (lines 49-51, 105-107)
   - WordOfTheDayScreen.kt (lines 41, 61-66, 81)
   - LastQuizResultsScreen.kt (line 47)

### 🟡 HIGH PRIORITY (Do Next)
3. WordProgressScreen - Connect to database queries
4. StreakDetailScreen - Load activity history
5. LeaderboardScreen - Backend integration OR local mode

## 📂 File Structure (Know Before You Search)

```
app/src/main/java/com/gultekinahmetabdullah/trainvoc/
├── ui/screen/
│   ├── features/       # ProfileScreen, WordOfDayScreen, FavoritesScreen
│   ├── gamification/   # DailyGoalsScreen, StreakDetailScreen
│   ├── social/         # LeaderboardScreen
│   ├── progress/       # WordProgressScreen
│   └── quiz/           # LastQuizResultsScreen
├── data/local/         # Room database (TrainvocDatabase.kt - NEEDS MIGRATIONS!)
├── viewmodel/          # All ViewModels use Hilt + StateFlow
└── classes/enums/Route.kt  # Navigation routes (9 new routes added)
```

## 🧠 Code Patterns (Copy These)

**ViewModel Pattern:**
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    val data: StateFlow<List<Item>> = repository.getData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
```

**Screen Pattern:**
```kotlin
@Composable
fun MyScreen(
    onBackClick: () -> Unit = {},
    viewModel: MyViewModel = hiltViewModel()
) {
    val data by viewModel.data.collectAsState()
    Scaffold(topBar = { ... }) { padding -> /* content */ }
}
```

## 📖 Detailed Documentation (Read When You Need Details)

1. **[CLAUDE_CONTINUATION_GUIDE.md](CLAUDE_CONTINUATION_GUIDE.md)** - Full onboarding (5 min read)
2. **[PHASE_3_COMPLETION_AND_TODOS.md](PHASE_3_COMPLETION_AND_TODOS.md)** - All TODOs with code (15 min read)
3. **[SCREEN_AUDIT_AND_IMPLEMENTATION_PLAN.md](SCREEN_AUDIT_AND_IMPLEMENTATION_PLAN.md)** - Original vision (reference only)

## ⚠️ Common Pitfalls (Avoid These!)

❌ Don't use `GlobalScope` → Use `viewModelScope`
❌ Don't hardcode strings → Use `R.string.*`
❌ Don't skip migrations → Data loss!
❌ Don't forget `@HiltViewModel` → Injection fails
❌ Don't collect flows without `collectAsState()` → Memory leaks

## ✅ Quick Win Checklist (Before Asking)

- [ ] Read this file (you're here! ✅)
- [ ] Scanned the TODO list above
- [ ] Checked file structure for relevant files
- [ ] Reviewed code patterns

**Ready to code!** 🚀

---

**Last Updated**: 2026-01-11
**Session**: Post Phase 3 Completion
**For**: Claude AI Sessions
