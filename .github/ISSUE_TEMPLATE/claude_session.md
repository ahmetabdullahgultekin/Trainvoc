---
name: Claude Code Session
about: Start a new Claude Code development session with full project context
title: '[CLAUDE] '
labels: development
assignees: ''
---

## 📚 REQUIRED READING (Read These First!)

**Claude AI: Before analyzing the codebase, read these documents in order:**

1. **[CLAUDE_CONTINUATION_GUIDE.md](../../docs/CLAUDE_CONTINUATION_GUIDE.md)** - Quick start guide with project overview, recent changes, and next steps
2. **[PHASE_3_COMPLETION_AND_TODOS.md](../../docs/PHASE_3_COMPLETION_AND_TODOS.md)** - Comprehensive TODO list with code examples
3. **[PHASE_1_2_COMPLETION_AND_NEXT_STEPS.md](../../docs/PHASE_1_2_COMPLETION_AND_NEXT_STEPS.md)** - Implementation details for completed phases

## 📊 Project Quick Facts

- **Project**: Trainvoc - English-Turkish Vocabulary Learning App
- **Tech Stack**: Kotlin, Jetpack Compose, Material 3, Room, Hilt, MVVM
- **Current Branch**: `claude/review-trainvoc-app-1FwM7`
- **Database Version**: 11 (needs migration to 14)
- **Total Screens**: 44 (33 existing + 11 newly created)
- **Phase Status**: Phase 1-3 ✅ Complete | Phase 4+ ⏳ In Progress

## 🎯 Current Development Status

### ✅ Recently Completed
- 11 new screens created (~3,500+ lines of code)
- Removed all alpha/test watermarks
- Fixed broken Quick Access navigation
- Comprehensive documentation created
- All routes integrated into navigation

### 🔴 High Priority TODOs (Next Steps)
1. **Database Migrations (URGENT)** - 3 migrations needed (11→12→13→14)
2. **Load Actual Data** - Replace placeholder data in FavoritesScreen, WordOfTheDayScreen, LastQuizResultsScreen
3. **Progress Tracking** - Implement real data queries for WordProgressScreen and StreakDetailScreen

See [PHASE_3_COMPLETION_AND_TODOS.md](../../docs/PHASE_3_COMPLETION_AND_TODOS.md) lines 64-660 for detailed implementation specs.

## 📝 Task Description

**What should Claude work on this session?**

<!-- Describe the specific task, feature, or bug fix for this session -->
<!-- Reference specific TODOs from the documentation if applicable -->

**Example:**
- Implement database migration 11→12 for favorites support
- Create FavoritesViewModel and connect to FavoritesScreen
- Test favorites functionality with real data

## 🔍 Additional Context

<!-- Add any additional context, requirements, or constraints here -->
<!-- Link to related PRs, issues, or documentation -->

## ✅ Acceptance Criteria

<!-- What defines "done" for this task? -->
- [ ] Code compiles without errors
- [ ] All navigation works (forward and back)
- [ ] Data persists across app restarts
- [ ] Changes committed with clear messages
- [ ] Documentation updated if needed

## 🚀 Environment Info

- **Working Directory**: `/home/user/Trainvoc`
- **Git Branch**: `claude/review-trainvoc-app-1FwM7`
- **Key Files Location**:
  - New screens: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/ui/screen/`
  - Database: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/data/local/`
  - Routes: `app/src/main/java/com/gultekinahmetabdullah/trainvoc/classes/enums/Route.kt`

---

**Note for Claude**: Follow the code patterns in the continuation guide. Use existing ViewModels as examples. Test incrementally. Mark TODOs as complete when finished.
