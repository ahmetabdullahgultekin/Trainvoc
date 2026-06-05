# Design: Spaced-Repetition (SRS) Engine (Trainvoc Phase 6)

> **How we build features (the process this document enforces).**
> 1. Design-doc first — no non-trivial feature starts as code. This doc is reviewed before implementation.
> 2. ADR for significant algorithmic decisions — `docs/adr/0001-srs-algorithm-fsrs-vs-sm2.md`.
> 3. Sequence and state-machine diagrams in `docs/diagrams/srs-scheduler.mmd` (Mermaid).
> 4. Contract-first — Room schema (migration V18), optional backend sync DTO, and Kotlin interfaces defined here before implementation; tests assert the contract.
> 5. Vertical-slice agile — 4 independently shippable slices (S1–S4), each behind feature flag `srs_engine_enabled` (default OFF).
> 6. Reversible rollout — flag default-OFF is byte-identical to today; kill-switch by flag, no redeploy.
> 7. TDD + green CI gate — unit tests for the algorithm, DAO, and ViewModel written first.
> 8. Verify in the real product — a slice is "done" only when demonstrated end-to-end in the running app.

| | |
|---|---|
| **Status** | Draft — for review |
| **Author** | Engineering (2026-06-05) |
| **Reviewers** | (owner) |
| **Feature flag** | `srs_engine_enabled` (default **OFF**) |
| **ADR** | [ADR-0001 SRS Algorithm: FSRS vs SM-2](../adr/0001-srs-algorithm-fsrs-vs-sm2.md) |
| **Tracking** | ROADMAP Phase 6 "Spaced repetition tuning" → ships as 4 vertical slices (S1–S4) |

---

## 1. Context & problem

Trainvoc already collects every ingredient needed for retention-aware scheduling: the `Word` entity carries SM-2 fields (`easinessFactor`, `intervalDays`, `repetitions`, `nextReviewDate`) added in migration V3, and the `srs_cards` table (migration V11, `SRSCard` entity) stores a working SM-2 `calculateNext()` implementation. Quiz results accumulate in `quiz_history`/`quiz_question_results` (migration V14). Streaks and daily goals are tracked in `streak_tracking`/`daily_goals` (migration V10).

Despite all this data, **none of it feeds a review queue that a user can actually see**. `getDueCards()` and `getDueCountFlow()` exist in `GamesDao` but are wired only as background inputs to game sessions — there is no dedicated "Review" surface in the navigation, no badge on the home screen showing due-card count, no way to complete a structured review session.

The result: words a user got wrong three days ago are silently forgotten. Trainvoc behaves like a quiz app, not a learning tool. The SRS engine is this project's highest-leverage retention feature; ROADMAP Phase 6 explicitly calls it out as the next growth lever after the Android v1 ship.

### Why now

- The data model and algorithm are already ~60% in place (SM-2 fields on `Word`, `SRSCard` entity, DAO queries). This is a _surface and integrate_ task, not a greenfield one.
- Android v1 is shipping offline-first; a meaningful "due review" experience requires no backend at all (S1–S3 are fully local).
- Retention without scheduled review is the leading cause of churn in vocabulary apps (Duolingo's own research; Anki's entire premise).

---

## 2. Goals / Non-goals

### Goals

- A user who answers a word correctly/incorrectly in any quiz or game session sees that word re-scheduled for optimal review using the FSRS algorithm.
- A **Review Queue** screen lets users complete their due cards daily with a flippable card UI (word → meaning reveal → quality rating 1–5).
- A persistent **"X cards due"** badge on the Home screen and a notification (opt-in) drives daily return visits.
- The scheduler runs fully **offline-first** — no backend required for S1–S3.
- The existing `srs_cards` table is the sole source of truth for scheduling; the `Word`-level SM-2 fields remain as a secondary, denormalized cache for backward compatibility with game queries.
- Optional **backend sync** of the review schedule (S4) enables cross-device continuity when the user is signed in.

### Non-goals (this phase)

- Replacing any existing quiz or game mode. The SRS engine is additive; it *feeds* from quiz outcomes, it does not replace them.
- Per-word optimizer / parameter personalization (FSRS optimizer using user's full review history). That requires 1,000+ reviews per user — deferred to Phase 8 analytics work.
- iOS / Web clients (deferred to Phase 5 iOS and Phase 4 web).
- Global leaderboard integration for SRS streaks (deferred to Phase 7 social).
- Adaptive algorithm parameter tuning on the device (computationally expensive without the user's full history matrix).

---

## 3. Current state (what exists)

### Android client

| Existing artifact | Location | State |
|---|---|---|
| `SRSCard` entity + SM-2 `calculateNext()` | `games/GamesDao.kt` | Working; SM-2 only; not exposed in UI |
| `GamesDao.getDueCards()` / `getDueCountFlow()` | `games/GamesDao.kt` | Written; not called from any ViewModel |
| SM-2 fields on `Word` (V3 migration) | `classes/word/EntitiesAndRelations.kt` | Present; denormalized; out of sync with `srs_cards` |
| `quiz_history` + `quiz_question_results` tables | `quiz/QuizHistory.kt` | Populated on quiz completion; not wired to SRS |
| `StreakTracking` + `DailyGoal` gamification | `gamification/` | Working; `reviews_today` counter exists but never incremented |
| Feature flag infrastructure | `features/database/` | Global + user flags with rollout % and daily-usage controls |
| `SyncQueue` offline action queue | `offline/SyncQueue.kt` | Working; used for general offline sync; SRS events can reuse this path |

### What is missing

1. **FSRS algorithm** — `SRSCard.calculateNext()` implements SM-2 (`interval*easeFactor` growth). FSRS replaces this with a memory-stability model (see ADR-0001). The change is confined to one pure function — no DB schema change required for S1.
2. **Review Queue screen** — `ReviewQueueScreen.kt`, `ReviewQueueViewModel.kt` do not exist.
3. **Due-badge wiring** — `HomeScreen.kt` does not observe `getDueCountFlow()`.
4. **Quiz-outcome → SRS update hook** — `QuizViewModel.kt` records `QuizHistory` but never calls `GamesDao.updateSRSCard()`.
5. **Backend sync schema** — no REST endpoint; `SyncQueue` pattern can carry review events once the backend is deployed.

---

## 4. Proposed design

### High-level architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│  Android Client (offline-first)                                          │
│                                                                          │
│  ┌──────────────────┐   rate answer   ┌──────────────────────────────┐  │
│  │ ReviewQueueScreen│ ──────────────► │   ReviewQueueViewModel       │  │
│  │                  │                 │   (StateFlow<ReviewUiState>)  │  │
│  └──────────────────┘                 └──────────┬───────────────────┘  │
│                                                  │                       │
│  ┌──────────────────┐  quiz completes  ┌────────▼─────────────────────┐ │
│  │  QuizViewModel   │ ───────────────► │   SrsSchedulerService        │ │
│  │  (existing)      │                  │   (domain, no Android deps)  │ │
│  └──────────────────┘                  │   - computeNextReview(card,  │ │
│                                        │       rating) → SRSCard      │ │
│  ┌──────────────────┐                  │   - FsrsAlgorithm (pure)     │ │
│  │  HomeScreen      │◄── dueCount ─────└──────────┬───────────────────┘ │
│  │  (badge)         │                             │                      │
│  └──────────────────┘                  ┌──────────▼───────────────────┐ │
│                                        │   GamesDao (Room)             │ │
│                                        │   srs_cards table (V11)       │ │
│                                        │   + NEW review_schedule V18   │ │
│                                        └──────────────────────────────┘ │
│                                                   │                      │
│                                        ┌──────────▼───────────────────┐ │
│                                        │   SyncQueueDao               │ │
│                                        │   (enqueue SRS_REVIEW_EVENT  │ │
│                                        │    for backend sync — S4)    │ │
│                                        └──────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────┘
                              │  (S4 — requires backend deployed)
                     ┌────────▼─────────────────────────────────────┐
                     │  TrainvocBackend  (Spring Boot / Java 21)     │
                     │  POST /api/v1/srs/reviews  (batch upsert)     │
                     │  GET  /api/v1/srs/schedule (pull on login)    │
                     │  PostgreSQL: srs_schedule table (words DB)    │
                     └──────────────────────────────────────────────┘
```

### Key flow: completing a review

1. User opens the app; `HomeScreen` observes `getDueCountFlow()` → badge shows "12 due".
2. User taps the badge → navigates to `ReviewQueueScreen`.
3. `ReviewQueueViewModel` loads the first batch of due `SRSCard`s (up to 50 per session) via `GamesDao.getDueCards()`.
4. Card is shown: front = English word. User taps to flip → meaning, example sentence, TTS audio. User selects a quality rating (Again / Hard / Good / Easy — mapped to FSRS grades 1–4).
5. `ReviewQueueViewModel` calls `SrsSchedulerService.computeNextReview(card, rating)` → receives updated `SRSCard` with new `stability`, `difficulty`, `due`, `state`.
6. ViewModel calls `GamesDao.updateSRSCard()` and enqueues a `SyncQueue` event (type `SRS_REVIEW`) for later backend sync.
7. `GamificationManager.onReviewCompleted()` increments `reviews_today`, checks streak, fires achievement unlocks.
8. At session end, `ReviewQueueViewModel` emits summary (cards reviewed, new cards, retention rate).

### Key flow: quiz outcome → SRS auto-schedule

1. `QuizViewModel` finishes a question. It already calls `QuizService.recordAnswer()`.
2. New hook: after recording, `QuizViewModel` calls `SrsSchedulerService.onQuizAnswer(wordId, wasCorrect)`.
3. If no `SRSCard` exists for this word → create one at initial state (new card, first review now).
4. Map quiz correctness to FSRS rating: correct = Good (3), incorrect = Again (1).
5. Persist via `GamesDao.upsertSRSCard()`.

This means every quiz session silently builds and maintains the review schedule without the user needing to use the Review Queue at all — the queue simply surfaces what the quiz already knows.

---

## 5. Data model

### 5a. Algorithm fields: replace SM-2 with FSRS in `srs_cards` (Room Migration V18)

The existing `srs_cards` table carries SM-2 fields (`ease_factor`, `interval`, `repetitions`). FSRS requires different state: **stability** (estimated days to 90% retention), **difficulty** (intrinsic hardness 0–1), and **state** (New / Learning / Review / Relearning).

The migration adds FSRS columns alongside the SM-2 columns (additive, not destructive) and seeds them from existing SM-2 data using a best-effort approximation. The old SM-2 columns are retained for backward compatibility with existing game code and can be removed in a future cleanup migration.

```sql
-- Room Migration V18: Add FSRS fields to srs_cards
-- File: AppDatabase.kt  MIGRATION_17_18

ALTER TABLE srs_cards ADD COLUMN stability      REAL    NOT NULL DEFAULT 1.0;
ALTER TABLE srs_cards ADD COLUMN difficulty     REAL    NOT NULL DEFAULT 0.3;
ALTER TABLE srs_cards ADD COLUMN due            INTEGER NOT NULL DEFAULT 0;
ALTER TABLE srs_cards ADD COLUMN card_state     TEXT    NOT NULL DEFAULT 'NEW';
  -- Values: 'NEW' | 'LEARNING' | 'REVIEW' | 'RELEARNING'
ALTER TABLE srs_cards ADD COLUMN elapsed_days   INTEGER NOT NULL DEFAULT 0;
ALTER TABLE srs_cards ADD COLUMN scheduled_days INTEGER NOT NULL DEFAULT 0;
ALTER TABLE srs_cards ADD COLUMN reps           INTEGER NOT NULL DEFAULT 0;
ALTER TABLE srs_cards ADD COLUMN lapses         INTEGER NOT NULL DEFAULT 0;

-- Seed FSRS stability from existing SM-2 interval (rough approximation)
UPDATE srs_cards
   SET stability  = CASE WHEN interval > 0 THEN CAST(interval AS REAL) ELSE 1.0 END,
       due        = next_review_date,
       card_state = CASE
                      WHEN repetitions = 0 THEN 'NEW'
                      WHEN repetitions < 3  THEN 'LEARNING'
                      ELSE 'REVIEW'
                    END;

CREATE INDEX IF NOT EXISTS index_srs_cards_due
    ON srs_cards(due);

CREATE INDEX IF NOT EXISTS index_srs_cards_card_state
    ON srs_cards(card_state);
```

### 5b. New `review_schedule` table (Room Migration V18, continued)

A dedicated **summary** table is added so the Review Queue can show aggregate stats (total due today, upcoming schedule graph) without scanning `srs_cards` on every frame. It is updated atomically when a review is committed.

```sql
-- New table: review_schedule (per-word scheduler view — one row per word per user)
CREATE TABLE IF NOT EXISTS review_schedule (
    word_id          TEXT    NOT NULL,
    user_id          TEXT    NOT NULL DEFAULT 'local_user',
    due_at           INTEGER NOT NULL,           -- epoch ms; indexed; the primary scheduling key
    stability        REAL    NOT NULL DEFAULT 1.0,
    difficulty       REAL    NOT NULL DEFAULT 0.3,
    last_reviewed_at INTEGER,
    card_state       TEXT    NOT NULL DEFAULT 'NEW',
    synced_to_server INTEGER NOT NULL DEFAULT 0,  -- 0 = dirty, 1 = synced (for S4)
    created_at       INTEGER NOT NULL,
    updated_at       INTEGER NOT NULL,
    PRIMARY KEY (word_id, user_id)
);

CREATE INDEX IF NOT EXISTS index_review_schedule_due_at
    ON review_schedule(user_id, due_at);

CREATE INDEX IF NOT EXISTS index_review_schedule_synced
    ON review_schedule(synced_to_server)
    WHERE synced_to_server = 0;   -- partial index for dirty-row sync (SQLite 3.8.9+)
```

**Invariants:**
- One row per `(word_id, user_id)` pair — `INSERT OR REPLACE` semantics.
- `due_at` is always the wall-clock epoch ms of the next scheduled review. Rows with `due_at <= now()` are "due".
- `card_state = 'NEW'` rows are created lazily on first quiz encounter; they do not pre-exist for every word in the dictionary.
- `synced_to_server = 0` rows are the pending sync queue for S4; the existing `SyncQueue` table carries the full event payload, while this flag is a cheap index for the sync service to find dirty rows.

### 5c. Optional backend sync schema (PostgreSQL, `trainvoc-words` DB — Phase S4)

```sql
-- Flyway V5__srs_schedule.sql (new migration in TrainvocBackend)
CREATE TABLE IF NOT EXISTS srs_schedule (
    word_id         TEXT        NOT NULL,
    user_id         TEXT        NOT NULL,   -- Firebase UID
    due_at          TIMESTAMPTZ NOT NULL,
    stability       REAL        NOT NULL DEFAULT 1.0,
    difficulty      REAL        NOT NULL DEFAULT 0.3,
    last_reviewed_at TIMESTAMPTZ,
    card_state      TEXT        NOT NULL DEFAULT 'NEW',
    client_updated_at TIMESTAMPTZ NOT NULL,  -- last-write-wins on conflict
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (word_id, user_id)
);

CREATE INDEX idx_srs_schedule_user_due
    ON srs_schedule(user_id, due_at);
```

**Conflict resolution**: last-write-wins on `client_updated_at`. The client always wins — the server is a sync mirror, not the source of truth. This keeps offline-first semantics intact.

---

## 6. API / protocol / contract

These endpoints are only needed for S4 (backend sync). S1–S3 are fully local.

### `POST /api/v1/srs/reviews` — batch upsert

**Request:**
```json
{
  "reviews": [
    {
      "wordId": "abandon",
      "dueAt": 1749340800000,
      "stability": 8.43,
      "difficulty": 0.27,
      "lastReviewedAt": 1749254400000,
      "cardState": "REVIEW",
      "clientUpdatedAt": 1749254400000
    }
  ]
}
```

**Response:** `204 No Content` (fire-and-forget; client does not wait for confirmation).

Batch size: max 500 rows per request. Client sends dirty rows on app foreground or on WiFi, whichever comes first.

### `GET /api/v1/srs/schedule` — pull full schedule on login

**Response:**
```json
{
  "schedule": [
    {
      "wordId": "abandon",
      "dueAt": 1749340800000,
      "stability": 8.43,
      "difficulty": 0.27,
      "lastReviewedAt": 1749254400000,
      "cardState": "REVIEW",
      "clientUpdatedAt": 1749254400000
    }
  ],
  "totalDue": 12,
  "nextDueAt": 1749340800000
}
```

Used on first login from a new device to seed local `review_schedule`. Subsequent sync uses the batch-push approach.

---

## 7. Files to add / change

```
TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/

  srs/                                                          (+) new package
    algorithm/
      FsrsAlgorithm.kt                                          (+) pure FSRS-5 state machine
        -- fun schedule(card: FsrsCard, rating: Rating,
                        now: Long): FsrsCard
        -- no Android imports; fully unit-testable on JVM
      FsrsCard.kt                                               (+) FSRS value object
        -- data class: stability, difficulty, due, state, etc.
      FsrsRating.kt                                             (+) enum Again(1)/Hard(2)/Good(3)/Easy(4)
      FsrsState.kt                                              (+) enum New/Learning/Review/Relearning
      Sm2ToFsrsMigrator.kt                                      (+) seed FSRS fields from existing SM-2 SRSCard
    domain/
      SrsSchedulerService.kt                                    (+) orchestrates FsrsAlgorithm + DAO
        -- suspend fun computeNextReview(wordId: String,
                                         rating: FsrsRating): SRSCard
        -- suspend fun onQuizAnswer(wordId: String,
                                    wasCorrect: Boolean)
        -- fun getDueCountFlow(): Flow<Int>
      ISrsSchedulerService.kt                                   (+) interface (for test injection)
    sync/
      SrsBackendSyncService.kt                                  (+) S4 — batch-push dirty rows via Retrofit
      SrsBackendSyncWorker.kt                                   (+) S4 — WorkManager periodic task

  database/
    AppDatabase.kt                                              (~) +MIGRATION_17_18; +reviewScheduleDao()
    ReviewScheduleDao.kt                                        (+) Room DAO for review_schedule table
      -- fun getDueFlow(now: Long): Flow<List<ReviewScheduleRow>>
      -- fun getDueCount(now: Long): Flow<Int>
      -- suspend fun upsert(row: ReviewScheduleRow)
      -- suspend fun getDirtyRows(): List<ReviewScheduleRow>
      -- suspend fun markSynced(wordIds: List<String>)
    ReviewScheduleRow.kt                                        (+) @Entity for review_schedule table

  ui/review/
    ReviewQueueScreen.kt                                        (+) Compose screen
      -- Card flip animation (front: word; back: meaning + example)
      -- Rating bar (Again / Hard / Good / Easy)
      -- Session summary card
      -- Empty state ("All caught up!")
    ReviewQueueViewModel.kt                                     (+) Hilt ViewModel
      -- StateFlow<ReviewUiState>
      -- fun rateCard(wordId: String, rating: FsrsRating)
      -- fun skipCard(wordId: String)
      -- fun startSession() / fun endSession()
    ReviewUiState.kt                                            (+) sealed state class
      -- Loading / Active(card, remaining) / Summary / Empty

  ui/screen/main/
    HomeScreen.kt                                               (~) observe dueCountFlow → badge + CTA card

  navigation/
    ReviewNavigation.kt                                         (+) composable("review_queue") route
    AppNavigation.kt (or equivalent)                            (~) add ReviewNavigation

  viewmodel/
    QuizViewModel.kt                                            (~) call SrsSchedulerService.onQuizAnswer()
                                                                    after each answer

  di/
    SrsModule.kt                                                (+) Hilt @Module binding ISrsSchedulerService
                                                                    → SrsSchedulerService (singleton)

  features/database/
    GlobalFeatureFlag seeds                                     (~) add 'srs_engine_enabled' seed row

docs/design/srs-spaced-repetition-engine.md                    (+) this doc
docs/adr/0001-srs-algorithm-fsrs-vs-sm2.md                     (+) algorithm ADR
docs/diagrams/srs-scheduler.mmd                                (+) Mermaid sequence diagram

TrainvocBackend/src/main/java/com/trainvoc/backend/  (S4 only)
  srs/
    SrsScheduleEntity.java                                      (+) JPA entity for srs_schedule
    SrsReviewRequest.java                                       (+) DTO (batch upsert body)
    SrsScheduleResponse.java                                    (+) DTO (pull response)
    SrsController.java                                          (+) @RestController /api/v1/srs
    SrsService.java                                             (+) upsert logic, conflict resolution
    SrsRepository.java                                          (+) Spring Data JPA repository

TrainvocBackend/src/main/resources/db/migration/
  V5__srs_schedule.sql                                          (+) S4 Flyway migration (words DB)
```

---

## 8. Rollout & flags

The feature is gated behind a single Room `GlobalFeatureFlag` row with key `srs_engine_enabled`.

| State | Behaviour |
|---|---|
| `srs_engine_enabled = false` (default) | Byte-identical to today. `ReviewQueueScreen` is not reachable via navigation. `QuizViewModel` does not call `SrsSchedulerService`. `HomeScreen` shows no badge. All `srs_cards`/`review_schedule` data is inert. |
| `srs_engine_enabled = true` | Full SRS flow active. The flag can also be set per-user via `UserFeatureFlag` for A/B testing. |

**Rollout sequence:**

1. **Dark** — flag OFF. Migration V18 runs silently on all devices; tables exist, no UI surface.
2. **Dev dogfood** — flag ON via dev menu / `UserFeatureFlag` for the developer's own account.
3. **Beta testers** — enable for the closed-testing track (the 20+ testers already needed for the Play Store wall). Collect crash/ANR data.
4. **Broad (all users)** — set `GlobalFeatureFlag.enabled = true` in a follow-up patch; no APK redeploy required (flag is runtime-checked).

**Kill-switch**: set `GlobalFeatureFlag.enabled = false` in a hotfix; takes effect on next app foreground. No redeploy, no data loss. All `review_schedule` rows are preserved and will resume working when re-enabled.

**Backwards compatibility**: migration V18 is additive — new columns with NOT NULL defaults, new tables. Rollback to a flag-OFF build leaves V18 tables in place but inert; no data loss.

---

## 9. Agile iteration plan (vertical slices)

Each slice is independently shippable behind the `srs_engine_enabled` flag.

### S1 — FSRS algorithm + data model (1 sprint)

**Scope**: Replace `SRSCard.calculateNext()` SM-2 logic with `FsrsAlgorithm.schedule()`. Add Migration V18 (new FSRS columns on `srs_cards`, new `review_schedule` table). Add `ReviewScheduleDao`. No UI change.

**Done =** `FsrsAlgorithmTest` passes all standard FSRS test vectors (published by open-spaced-repetition); Migration V18 runs cleanly on an existing V17 database without data loss; `ReviewScheduleDao.getDueCount()` returns correct counts in unit tests.

---

### S2 — Review Queue screen (1 sprint)

**Scope**: `ReviewQueueScreen` + `ReviewQueueViewModel`. Navigable from a new `HomeScreen` "Review due" card. Session flow: load due cards → flip → rate → persist → summary. Gamification hook: increment `reviews_today`, trigger streak check.

**Done =** A user can open the review queue, flip through 5+ due cards, rate each one, see the updated due date persist in the database, and reach the summary screen. `HomeScreen` badge shows the correct live count. Streak counter increments after the session.

---

### S3 — Quiz-outcome → SRS auto-schedule hook (½ sprint)

**Scope**: Wire `QuizViewModel` to call `SrsSchedulerService.onQuizAnswer()` after each answer. New `SrsModule.kt` Hilt binding. Upsert creates a new `review_schedule` row if the word is unseen; updates if it exists.

**Done =** After completing a 10-question quiz, 10 rows exist in `review_schedule` with correct `due_at` values. Words answered correctly have a longer interval than words answered incorrectly. All without the user ever visiting the Review Queue screen.

---

### S4 — Backend sync (1 sprint, requires backend deployed per ROADMAP Phase 3)

**Scope**: `SrsBackendSyncService` batches dirty `review_schedule` rows and POST them to `/api/v1/srs/reviews`. On login, GET `/api/v1/srs/schedule` seeds the local table. `WorkManager` periodic sync every 6 hours on WiFi.

**Done =** A user who clears their app data and re-logs in on the same device sees their review schedule restored from the server within 10 seconds. Due count matches the pre-reinstall state. Verified with two sequential installs against a live backend.

---

## 10. Test plan

### Unit tests (JVM — `app/src/test/`)

| Test class | What it proves |
|---|---|
| `FsrsAlgorithmTest` | All standard FSRS test vectors from open-spaced-repetition/fsrs4anki; pure function, no Android deps |
| `SrsSchedulerServiceTest` | `computeNextReview()` updates SRSCard stability/difficulty; `onQuizAnswer(correct=true)` produces longer interval than `onQuizAnswer(correct=false)` |
| `Sm2ToFsrsMigratorTest` | All SM-2 `repetitions` values map to the correct FSRS `card_state` |
| `ReviewQueueViewModelTest` | `rateCard()` emits correct `ReviewUiState` transitions; `endSession()` produces accurate summary stats |
| `ReviewScheduleDaoTest` | `getDueCount()` returns only rows with `due_at <= now`; upsert replaces on conflict; `getDirtyRows()` excludes synced rows |

### Room / integration tests (`app/src/androidTest/`)

| Test class | What it proves |
|---|---|
| `MigrationV17ToV18Test` | V18 migration runs without crashing on a seeded V17 database; existing SM-2 data is preserved; FSRS columns are seeded with non-null defaults |
| `SrsEndToEndTest` | Full flow: quiz answer → `SRSCard` upserted → `review_schedule` row with `card_state = LEARNING`; second correct answer → `REVIEW`; due count drops to zero after reviewing all cards |

### UI / smoke tests

| Test | What it proves |
|---|---|
| `ReviewQueueScreenTest` (Compose Preview / manual) | Card flip animation renders; rating buttons are tappable at ≥48dp; empty state shows "All caught up!"; session summary displays correct card count |
| `HomeScreen` badge (manual) | After seeding 5 due cards in DB directly, badge shows "5" on Home; disappears after review session |

### Backward-compatibility test

Run all 178 existing unit tests with `srs_engine_enabled = false` (default). No regressions permitted — the flag being OFF must be byte-identical to pre-SRS HEAD.

---

## 11. Risks & open questions

### R1 — FSRS algorithm tuning (medium risk)

**Risk**: The default FSRS-5 parameters (`w0`–`w17`) are calibrated on Anki's English-language deck population. Trainvoc's EN↔TR vocabulary may have a different forgetting-curve shape (Turkish learners may forget faster or slower than the average Anki user population).

**Mitigation**: Ship with default parameters (Phase 6). Once Phase 8 analytics is in place and users have 1,000+ reviews each, run the FSRS optimizer on collected review logs to generate per-user optimal parameters. Store personalized parameters in a new `srs_parameters` table (Phase 8 design decision).

**Acceptance**: Measure 90-day retention rate (% of reviews answered "Good" or "Easy") at 4 weeks post-launch. If it falls below 65%, schedule optimizer work.

### R2 — Cold-start problem (medium risk)

**Risk**: New users have zero `srs_cards` rows. The Review Queue is empty. The value proposition of SRS is invisible until they complete several quiz sessions.

**Mitigation (S3)**: The quiz-outcome hook (S3) silently creates `review_schedule` rows on the very first quiz answer. After a 10-word quiz, 10 cards are scheduled. The first due cards arrive 1 day later for newly-seen words. To further reduce cold-start, seed 5 "today" due cards from the user's weakest-performing words (lowest `correctCount / totalCount` ratio from `Statistic`) when the user first enables the feature.

**Open question for owner**: should the cold-start seeding use the user's existing `Statistic` data, or start fresh with no prior influence on FSRS difficulty?

### R3 — Cross-device schedule conflicts (low risk until S4)

**Risk**: User reviews on two devices offline simultaneously. Both push to the backend. Last-write-wins on `client_updated_at` may discard a review done on the second device.

**Mitigation**: For the same word reviewed on two devices within the same day, the conflict is minor — both reviews moved the card forward; the "lost" review just means slightly less credit. True schedule divergence requires reviewing the same word twice in the same hour, which is rare in practice. A more principled merge (take the later `due_at`, sum the `lapses`) can be added in a Phase 8 sync refinement. The current last-write-wins is pragmatic and honest.

### R4 — Room migration V18 on large databases (low risk)

**Risk**: The `ALTER TABLE srs_cards ADD COLUMN` + `UPDATE srs_cards SET ...` migration may be slow if a user has thousands of SRS cards (unlikely at v1 scale but possible for power users).

**Mitigation**: SQLite `ALTER TABLE ADD COLUMN` is O(1) (schema change only). The `UPDATE` statement seeds the new FSRS columns; for a table with 10,000 rows this takes ~50ms on mid-range hardware. Migration runs in a background thread via Room's `createFromAsset` + `addMigrations` path. No ANR risk.

### R5 — Feature flag bootstrap race (low risk)

**Risk**: On a fresh install, `GlobalFeatureFlag` is seeded from the pre-packaged database asset. If the asset does not include the `srs_engine_enabled` row, the flag query returns null and the feature fails silently.

**Mitigation**: `SrsSchedulerService` treats a null flag as `false` (safe default). A `SrsModule` `@Provides` injects a flag accessor with null-safe fallback. The feature flag seed is also added to the `MIGRATION_17_18` block (INSERT OR IGNORE) so it is guaranteed to exist on fresh installs and upgrades.

---

## 12. Rollback

**Flag OFF** is the kill-switch. Setting `GlobalFeatureFlag.srs_engine_enabled = false` (or shipping a hotfix patch that reads this flag as false) immediately:
- Hides the Review Queue screen (navigation guard).
- Stops `QuizViewModel` from calling `SrsSchedulerService`.
- Removes the Home badge (observer detaches).

No data is deleted. Migration V18 is additive; there is no corresponding down-migration, and none is needed — the tables sit inert. When the flag is re-enabled (after a fix), all previously accumulated review events resume exactly where they left off.

**If migration V18 causes a crash on a specific device**: Room's `fallbackToDestructiveMigration()` is not enabled (by design in `AppDatabase.DatabaseBuilder.buildRoomDB()`). The app will refuse to open rather than destroy user data. The fix path is a V19 migration that corrects V18. User data in all pre-existing tables is preserved because V18 is additive-only.

**Backend rollback (S4 only)**: the `/api/v1/srs/*` endpoints can be disabled at the load balancer (Nginx upstream comment-out) with no data loss. The `srs_schedule` PostgreSQL table is append-only from the client's perspective; it can be left in place or dropped at operator discretion.
