# Trainvoc Feature Implementation Plan

## Overview

This document outlines the implementation plan for enhancing Trainvoc with advanced notifications,
UI improvements, cloud backup, and local import/export features.

---

## Phase 1: Advanced Notification System (Priority: HIGH)

### 1.1 Interactive Word Quiz Notifications

**Concept**: Transform word notifications into micro-learning experiences with actionable buttons
that allow users to learn without opening the app.

#### Notification Flow

```
┌─────────────────────────────────────────┐
│ 🎓 Trainvoc Word Quiz                   │
│ ─────────────────────────────────────── │
│                                         │
│ Do you know this word?                  │
│                                         │
│ "ELOQUENT"                              │
│                                         │
│ [I Know It]  [Show Answer]  [Skip]      │
└─────────────────────────────────────────┘
          │              │           │
          ▼              ▼           ▼
    Update Stats    Expand to    Send Next
    (correct+1)     Show Answer   Word
    Show Success
                         │
                         ▼
┌─────────────────────────────────────────┐
│ 🎓 Trainvoc Word Quiz                   │
│ ─────────────────────────────────────── │
│                                         │
│ ELOQUENT                                │
│ "Fluent or persuasive in speaking       │
│  or writing"                            │
│                                         │
│ [Got It ✓]  [Need Practice]  [Next]     │
└─────────────────────────────────────────┘
          │              │           │
          ▼              ▼           ▼
    Mark Learned    Mark Wrong    Send Next
    (correct+1)     (wrong+1)     Word
```

#### Action Buttons - Phase 1 (Question)

| Button          | Action                                        | Database Update                            |
|-----------------|-----------------------------------------------|--------------------------------------------|
| **I Know It**   | User claims to know - show brief confirmation | `correctCount++`, update `lastReviewed`    |
| **Show Answer** | Reveal meaning in expanded notification       | None (wait for phase 2 response)           |
| **Skip**        | Skip this word, send another                  | `skippedCount++`, trigger new notification |

#### Action Buttons - Phase 2 (After Reveal)

| Button            | Action                      | Database Update                                |
|-------------------|-----------------------------|------------------------------------------------|
| **Got It**        | User confirmed they knew it | `correctCount++`, potentially `learned = true` |
| **Need Practice** | User didn't know it         | `wrongCount++`, reset learning progress        |
| **Next**          | Continue to next word       | None, trigger new notification                 |

#### Technical Implementation

**New Files:**

- `NotificationActionReceiver.kt` - BroadcastReceiver for notification actions
- `NotificationActionService.kt` - Service to handle database updates

**Modified Files:**

- `WordNotificationWorker.kt` - Add action buttons to notifications
- `NotificationHelper.kt` - Add notification update methods
- `AndroidManifest.xml` - Register receiver and service

**Database Updates:**

```kotlin
// StatisticDao.kt - Add quick update methods
@Query("UPDATE statistics SET correct_count = correct_count + 1, last_reviewed = :timestamp WHERE stat_id = :statId")
suspend fun incrementCorrect(statId: Int, timestamp: Long)

@Query("UPDATE statistics SET wrong_count = wrong_count + 1, last_reviewed = :timestamp WHERE stat_id = :statId")
suspend fun incrementWrong(statId: Int, timestamp: Long)

@Query("UPDATE statistics SET skipped_count = skipped_count + 1 WHERE stat_id = :statId")
suspend fun incrementSkipped(statId: Int)

@Query("UPDATE statistics SET learned = 1 WHERE stat_id = :statId AND correct_count >= :threshold")
suspend fun markLearnedIfThreshold(statId: Int, threshold: Int = 5)
```

**Notification Builder with Actions:**

```kotlin
fun createQuizNotification(word: Word, statId: Int): Notification {
    val iKnowIntent = Intent(context, NotificationActionReceiver::class.java).apply {
        action = ACTION_I_KNOW
        putExtra(EXTRA_WORD, word.word)
        putExtra(EXTRA_STAT_ID, statId)
        putExtra(EXTRA_NOTIFICATION_ID, notificationId)
    }

    val showAnswerIntent = Intent(context, NotificationActionReceiver::class.java).apply {
        action = ACTION_SHOW_ANSWER
        putExtra(EXTRA_WORD, word.word)
        putExtra(EXTRA_MEANING, word.meaning)
        putExtra(EXTRA_STAT_ID, statId)
        putExtra(EXTRA_NOTIFICATION_ID, notificationId)
    }

    val skipIntent = Intent(context, NotificationActionReceiver::class.java).apply {
        action = ACTION_SKIP
        putExtra(EXTRA_STAT_ID, statId)
        putExtra(EXTRA_NOTIFICATION_ID, notificationId)
    }

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_quiz)
        .setContentTitle(context.getString(R.string.word_quiz_title))
        .setContentText(word.word)
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Do you know this word?\n\n\"${word.word.uppercase()}\""))
        .addAction(R.drawable.ic_check, getString(R.string.i_know_it),
            PendingIntent.getBroadcast(context, 0, iKnowIntent, FLAGS))
        .addAction(R.drawable.ic_reveal, getString(R.string.show_answer),
            PendingIntent.getBroadcast(context, 1, showAnswerIntent, FLAGS))
        .addAction(R.drawable.ic_skip, getString(R.string.skip),
            PendingIntent.getBroadcast(context, 2, skipIntent, FLAGS))
        .setAutoCancel(false)
        .build()
}
```

---

### 1.2 Word Notification Filtering

**Goal**: Allow users to customize which words appear in notifications.

#### Filter Options

**By Level:**

- [ ] A1 (Beginner)
- [ ] A2 (Elementary)
- [ ] B1 (Intermediate)
- [ ] B2 (Upper Intermediate)
- [ ] C1 (Advanced)
- [ ] C2 (Mastery)

**By Exam Category:**

- [ ] YDS
- [ ] YÖKDİL
- [ ] TOEFL
- [ ] IELTS
- [ ] General

**By Learning Status:**

- [ ] Unlearned words only
- [ ] Learned words (for review)
- [ ] Words with low accuracy
- [ ] Recently missed words

#### Data Model

```kotlin
// NotificationPreferences.kt additions
class NotificationPreferences(context: Context) {
    // Word Filter Settings
    var enabledLevels: Set<String>
        get() = prefs.getStringSet("notification_levels",
            setOf("A1", "A2", "B1", "B2", "C1", "C2")) ?: emptySet()
        set(value) = prefs.edit().putStringSet("notification_levels", value).apply()

    var enabledExams: Set<String>
        get() = prefs.getStringSet("notification_exams",
            setOf("YDS")) ?: emptySet()
        set(value) = prefs.edit().putStringSet("notification_exams", value).apply()

    var includeLearnedWords: Boolean
        get() = prefs.getBoolean("notification_include_learned", false)
        set(value) = prefs.edit().putBoolean("notification_include_learned", value).apply()

    var includeLowAccuracyWords: Boolean
        get() = prefs.getBoolean("notification_low_accuracy", true)
        set(value) = prefs.edit().putBoolean("notification_low_accuracy", value).apply()

    var lowAccuracyThreshold: Int
        get() = prefs.getInt("notification_accuracy_threshold", 50)
        set(value) = prefs.edit().putInt("notification_accuracy_threshold", value).apply()
}
```

#### Database Query

```kotlin
// WordDao.kt
@Query("""
    SELECT w.* FROM words w
    LEFT JOIN statistics s ON w.stat_id = s.stat_id
    LEFT JOIN word_exam_cross_ref we ON w.word = we.word
    WHERE w.level IN (:levels)
    AND (we.exam IN (:exams) OR :includeNoExam = 1)
    AND (s.learned = 0 OR :includeLearned = 1)
    AND (
        :includeLowAccuracy = 0 OR
        (s.correct_count + s.wrong_count = 0) OR
        (CAST(s.correct_count AS REAL) / (s.correct_count + s.wrong_count) * 100 < :accuracyThreshold)
    )
    ORDER BY RANDOM()
    LIMIT 1
""")
suspend fun getFilteredRandomWord(
    levels: List<String>,
    exams: List<String>,
    includeNoExam: Boolean,
    includeLearned: Boolean,
    includeLowAccuracy: Boolean,
    accuracyThreshold: Int
): Word?
```

---

### 1.3 Notification Frequency Control

#### Frequency Options

**Presets:**

- Every 15 minutes
- Every 30 minutes
- Every hour (default)
- Every 2 hours
- Every 4 hours
- Every 8 hours
- Once daily

**Custom:**

- Slider from 5 minutes to 24 hours
- Minutes/Hours toggle

#### Quiet Hours

```kotlin
var quietHoursEnabled: Boolean
var quietHoursStart: Int  // Hour in 24h format (e.g., 22 for 10 PM)
var quietHoursEnd: Int    // Hour in 24h format (e.g., 8 for 8 AM)
```

#### Smart Scheduling

- Avoid notifications during detected sleep hours
- Increase frequency during active learning times
- Reduce notifications after consecutive skips

---

### 1.4 Notification Settings UI

**New Screen: NotificationSettingsScreen.kt**

```kotlin
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: NotificationSettingsViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(Spacing.medium)
    ) {
        // Header
        Text(
            text = stringResource(R.string.notification_settings),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(Spacing.large))

        // Word Quiz Notifications Section
        SectionCard(title = stringResource(R.string.word_quiz_notifications)) {
            // Master toggle
            SettingSwitch(
                title = stringResource(R.string.enable_word_notifications),
                checked = wordNotificationsEnabled,
                onCheckedChange = { viewModel.setWordNotificationsEnabled(it) }
            )

            if (wordNotificationsEnabled) {
                Divider()

                // Frequency
                FrequencySelector(
                    currentMinutes = notificationInterval,
                    onFrequencyChanged = { viewModel.setNotificationInterval(it) }
                )

                Divider()

                // Test button
                Button(
                    onClick = { viewModel.sendTestNotification() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Notifications, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.send_test_notification))
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Word Filters Section
        SectionCard(title = stringResource(R.string.word_filters)) {
            // Levels
            Text(
                text = stringResource(R.string.include_levels),
                style = MaterialTheme.typography.titleSmall
            )
            LevelFilterGrid(
                enabledLevels = enabledLevels,
                onLevelToggled = { level, enabled ->
                    viewModel.toggleLevel(level, enabled)
                }
            )

            Divider()

            // Exams
            Text(
                text = stringResource(R.string.include_exams),
                style = MaterialTheme.typography.titleSmall
            )
            ExamFilterGrid(
                enabledExams = enabledExams,
                onExamToggled = { exam, enabled ->
                    viewModel.toggleExam(exam, enabled)
                }
            )

            Divider()

            // Learning status filters
            SettingSwitch(
                title = stringResource(R.string.include_learned_words),
                subtitle = stringResource(R.string.include_learned_words_desc),
                checked = includeLearnedWords,
                onCheckedChange = { viewModel.setIncludeLearnedWords(it) }
            )

            SettingSwitch(
                title = stringResource(R.string.prioritize_low_accuracy),
                subtitle = stringResource(R.string.prioritize_low_accuracy_desc),
                checked = includeLowAccuracyWords,
                onCheckedChange = { viewModel.setIncludeLowAccuracyWords(it) }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Quiet Hours Section
        SectionCard(title = stringResource(R.string.quiet_hours)) {
            SettingSwitch(
                title = stringResource(R.string.enable_quiet_hours),
                checked = quietHoursEnabled,
                onCheckedChange = { viewModel.setQuietHoursEnabled(it) }
            )

            if (quietHoursEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimePickerField(
                        label = stringResource(R.string.from),
                        hour = quietHoursStart,
                        onHourChanged = { viewModel.setQuietHoursStart(it) }
                    )
                    TimePickerField(
                        label = stringResource(R.string.to),
                        hour = quietHoursEnd,
                        onHourChanged = { viewModel.setQuietHoursEnd(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Other Notifications Section
        SectionCard(title = stringResource(R.string.other_notifications)) {
            SettingSwitch(
                title = stringResource(R.string.daily_reminders),
                checked = dailyRemindersEnabled,
                onCheckedChange = { viewModel.setDailyRemindersEnabled(it) }
            )

            SettingSwitch(
                title = stringResource(R.string.streak_alerts),
                checked = streakAlertsEnabled,
                onCheckedChange = { viewModel.setStreakAlertsEnabled(it) }
            )

            SettingSwitch(
                title = stringResource(R.string.word_of_day),
                checked = wordOfDayEnabled,
                onCheckedChange = { viewModel.setWordOfDayEnabled(it) }
            )
        }
    }
}
```

---

## Phase 2: UI/UX Enhancements

### 2.1 Animations & Transitions

- Screen enter/exit animations
- Card flip animation for quiz answers
- Button press feedback (scale + haptic)
- Progress celebration confetti
- Streak milestone animations
- Loading skeletons

### 2.2 Component Improvements

- Custom styled dropdowns
- Pull-to-refresh
- Empty state illustrations
- Error states with retry
- Swipe gestures for cards

### 2.3 Accessibility

- Content descriptions
- Focus management
- Font scaling
- High contrast support

---

## Phase 3: Cloud Backup (Google Drive)

### Features

- Auto-backup scheduling
- Manual backup/restore
- Conflict resolution
- Backup history
- Encrypted storage

### Implementation

- Google Sign-In integration
- Drive API v3
- Backup serialization (JSON)
- Progress indicators

---

## Phase 4: Local Import/Export

### Export Formats

- JSON (full backup with stats)
- CSV (word list)
- Plain text

### Import Features

- File picker integration
- Format auto-detection
- Duplicate handling
- Import preview

---

## Implementation Priority

| Phase | Feature                        | Effort   | Priority     |
|-------|--------------------------------|----------|--------------|
| 1.1   | Interactive Quiz Notifications | 3-4 days | **CRITICAL** |
| 1.2   | Word Notification Filtering    | 2-3 days | HIGH         |
| 1.3   | Frequency Control              | 1-2 days | HIGH         |
| 1.4   | Notification Settings UI       | 2-3 days | HIGH         |
| 4     | Local Import/Export            | 3-4 days | MEDIUM       |
| 2     | UI/UX Enhancements             | 4-5 days | MEDIUM       |
| 3     | Cloud Backup                   | 5-7 days | LOW          |

---

## File Structure

```
app/src/main/java/.../trainvoc/
├── notification/
│   ├── NotificationHelper.kt (modified)
│   ├── NotificationPreferences.kt (modified)
│   ├── NotificationScheduler.kt (modified)
│   ├── NotificationActionReceiver.kt (NEW)
│   └── NotificationActionService.kt (NEW)
├── worker/
│   └── WordNotificationWorker.kt (modified)
├── ui/
│   └── screen/
│       └── settings/
│           └── NotificationSettingsScreen.kt (NEW)
├── viewmodel/
│   └── NotificationSettingsViewModel.kt (NEW)
└── database/
    ├── WordDao.kt (modified)
    └── StatisticDao.kt (modified)
```

---

## String Resources Required

```xml
<!-- Notification Actions -->
<string name="i_know_it">I Know It</string>
<string name="show_answer">Show Answer</string>
<string name="skip">Skip</string>
<string name="got_it">Got It</string>
<string name="need_practice">Need Practice</string>
<string name="next_word">Next</string>

<!-- Notification Settings -->
<string name="notification_settings">Notification Settings</string>
<string name="word_quiz_notifications">Word Quiz Notifications</string>
<string name="enable_word_notifications">Enable Word Notifications</string>
<string name="notification_frequency">Frequency</string>
<string name="send_test_notification">Send Test Notification</string>
<string name="word_filters">Word Filters</string>
<string name="include_levels">Include Levels</string>
<string name="include_exams">Include Exam Categories</string>
<string name="include_learned_words">Include Learned Words</string>
<string name="include_learned_words_desc">Show words you\'ve already mastered for review</string>
<string name="prioritize_low_accuracy">Prioritize Low Accuracy Words</string>
<string name="prioritize_low_accuracy_desc">Focus on words you often get wrong</string>
<string name="quiet_hours">Quiet Hours</string>
<string name="enable_quiet_hours">Enable Quiet Hours</string>
<string name="other_notifications">Other Notifications</string>

<!-- Frequency Labels -->
<string name="every_15_minutes">Every 15 minutes</string>
<string name="every_30_minutes">Every 30 minutes</string>
<string name="every_hour">Every hour</string>
<string name="every_2_hours">Every 2 hours</string>
<string name="every_4_hours">Every 4 hours</string>
<string name="once_daily">Once daily</string>
```

---

## Testing Checklist

### Interactive Notifications

- [ ] "I Know It" updates stats correctly
- [ ] "Show Answer" reveals meaning
- [ ] "Skip" sends new word notification
- [ ] "Got It" marks progress after reveal
- [ ] "Need Practice" increments wrong count
- [ ] Notifications respect quiet hours
- [ ] Notifications use filtered word list

### Filtering

- [ ] Level filters work correctly
- [ ] Exam category filters work
- [ ] Learned/unlearned filter works
- [ ] Low accuracy prioritization works
- [ ] Empty filter shows warning

### Scheduling

- [ ] Custom frequency applies
- [ ] Quiet hours prevent notifications
- [ ] WorkManager survives device restart

---

## Notes

- Use `commit()` instead of `apply()` for critical preferences that affect immediate behavior
- Notification actions must use explicit intents for security
- BroadcastReceiver needs to be registered in AndroidManifest
- Consider battery optimization whitelisting for reliable delivery
- Test on various Android versions (especially 8.0+ for notification channels)
