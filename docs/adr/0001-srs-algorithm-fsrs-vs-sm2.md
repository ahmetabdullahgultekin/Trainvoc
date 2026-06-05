# ADR-0001: SRS Algorithm — FSRS vs SM-2

| | |
|---|---|
| **Status** | Accepted |
| **Date** | 2026-06-05 |
| **Deciders** | Engineering (owner review pending) |
| **Context doc** | [SRS Engine Design](../design/srs-spaced-repetition-engine.md) |

---

## Context

Trainvoc's `SRSCard` entity (introduced in Room migration V11) already implements the **SM-2** algorithm in `calculateNext(quality: Int)`. SM-2 was designed by Piotr Wozniak in 1987 and is the foundation of SuperMemo and early Anki. It is the most widely deployed SRS algorithm in vocabulary apps.

A newer algorithm, **FSRS** (Free Spaced Repetition Scheduler, version 5 as of 2024), was developed by Jarrett Ye and the open-spaced-repetition community and is now Anki's default scheduler since Anki 23.10. FSRS is grounded in the Ebbinghaus forgetting curve, the two-component model of memory (stability + retrievability), and is validated against large-scale review datasets.

This ADR records the decision between them and documents why FSRS was chosen.

---

## Decision drivers

1. **Prediction accuracy** — which algorithm schedules reviews at the moment a word is most likely to be forgotten (maximizing retention per review)?
2. **Implementation simplicity** — can we implement it correctly as a pure Kotlin function with no external library dependency?
3. **Ecosystem momentum** — is the algorithm actively maintained and validated?
4. **Migration cost** — how disruptive is adopting it given that SM-2 data already exists in the database?
5. **Personalization ceiling** — can the algorithm be tuned later to individual learner patterns?

---

## Options considered

### Option A: Keep SM-2 (status quo)

**SM-2 algorithm summary:**
- Input: `quality` (0–5), `easinessFactor` (EF, default 2.5), `interval` (days), `repetitions`.
- Output: new interval = `interval × EF`; EF adjusted by `EF + (0.1 − (5−q)(0.08 + (5−q)×0.02))`.
- First two reps are fixed at 1 and 6 days.

**Pros:**
- Already implemented and tested (`SRSCard.calculateNext()`).
- Extremely simple — 20 lines of pure arithmetic.
- Universally understood; huge community reference material.
- Zero migration work (it's already in prod schema).

**Cons:**
- **EF is a flawed proxy for difficulty.** EF can drift far from 2.5 but the algorithm has no mechanism to detect whether a word is intrinsically hard vs. a bad day for the user.
- **Fixed early intervals (1 day, 6 days) are not calibrated to any real forgetting data.** They are empirically reasonable but not derived from a memory model.
- **No retrievability model.** SM-2 cannot predict what percentage of users would recall a word at a given point in time — it only estimates when to schedule.
- **No stability concept.** Once a word reaches a high EF, a single failure sends the interval back to 1 day regardless of how stable the memory actually is.
- **No personalization architecture.** SM-2 has no parameter set that can be trained on user data — EF is per-card, not per-learner.
- **Anki deprecated it.** Anki 23.10 (released October 2023) switched its default to FSRS. This is the clearest signal from the world's largest SRS deployment that SM-2 is not the best available algorithm.

---

### Option B: FSRS (v5)

**FSRS algorithm summary** (the five-parameter abbreviated model used in this design):

FSRS models memory using two quantities per card:
- **Stability (S)**: the number of days at which retrievability drops to 90%. A card with S=30 will be recalled correctly by 90% of users 30 days after review.
- **Difficulty (D)**: intrinsic hardness of the card, 0.0–1.0, estimated from the history of ratings.

**Retrievability (R)** at elapsed time `t` days is: `R = 0.9^(t/S)`

On each review, FSRS schedules the next interval as the time at which R will drop below 90%: `interval = S × log(0.9) / log(desired_retention)` where `desired_retention` defaults to 0.9.

**Four grades** replace SM-2's 0–5 quality scale:
- `1 = Again` — complete failure (maps from quiz incorrect)
- `2 = Hard` — recalled with difficulty
- `3 = Good` — correct with some effort (maps from quiz correct)
- `4 = Easy` — effortlessly correct

**State machine:** cards move through `New → Learning → Review → Relearning`, with different stability-update formulas at each state.

**Parameters (w0–w17)**: 17 model weights calibrated on the Anki review dataset (800M+ reviews). Default parameters ship with FSRS and are usable immediately. Personalized parameters can be computed by the FSRS optimizer once a user accumulates ~1,000 reviews.

**Reference implementation**: `open-spaced-repetition/fsrs4anki` (JavaScript/TypeScript) and `open-spaced-repetition/fsrs-rs` (Rust). Both are actively maintained. The Kotlin port is a mechanical translation of the state machine — approximately 200 lines of pure functions with no platform dependencies. All FSRS versions publish a set of test vectors that any implementation must pass.

**Pros:**
- **Scientifically grounded.** Derived from Ebbinghaus forgetting curve + ACT-R two-component memory model. Validated against large review datasets.
- **Better long-term accuracy.** Independent benchmarks (cited by the open-spaced-repetition project, 2022–2024) show FSRS reduces review count by 20–40% for equivalent retention versus SM-2.
- **Stability concept prevents over-punishment.** A card with high stability that is rated "Again" once does not collapse to a 1-day interval; stability decays gradually (relearning formula).
- **Personalization path.** The 17 weights can be optimized per-user from their review history. Plugging in personalized weights is a single method substitution — the algorithm structure does not change.
- **Ecosystem momentum.** Anki (the world's largest SRS app, ~10M users) has deployed FSRS as default since October 2023. The open-spaced-repetition GitHub organization actively maintains the algorithm and its implementations in 8+ languages.
- **Simpler rating UX.** 4 grades (Again/Hard/Good/Easy) vs SM-2's 6 (0–5) is less cognitively demanding for mobile users.
- **Published test vectors** allow a deterministic correctness check in unit tests without subjective judgment.

**Cons:**
- **Requires a Kotlin port.** There is no published `fsrs-kotlin` library on Maven Central (as of 2026-06). We must implement the state machine from scratch (~200 lines). This is low-risk (the algorithm is fully specified and test-vectored) but adds implementation time of ~½ sprint.
- **Slightly more complex than SM-2.** 17 parameters, a state machine, and two-component memory model vs. SM-2's single EF adjustment. Complexity is isolated to `FsrsAlgorithm.kt`; the rest of the system sees only `SRSCard` and a rating enum.
- **Default parameters may be slightly miscalibrated for EN↔TR learners.** Mitigated by Phase 8 optimizer work (see Risk R1 in the design doc).
- **Migration from SM-2 data.** Existing `srs_cards` rows carry SM-2 fields; we must seed FSRS `stability` and `difficulty` from SM-2 `interval` and `easiness_factor`. This seeding is approximate (see V18 migration script) but is better than starting from scratch — users keep their scheduling history.

---

### Option C: SM-2 with FSRS retrievability formula only (hybrid)

A middle ground: keep SM-2's interval arithmetic but use FSRS-style 4-grade ratings and retrieve the R-at-next-review display. This was briefly considered as a lower-risk path.

**Rejected** because: it provides SM-2's weaknesses without any of FSRS's benefits (no stability, no difficulty model, no personalization path). It would also require maintaining two mental models indefinitely. The full FSRS port is the cleaner long-term choice.

---

## Decision

**FSRS v5 is chosen.**

The core reasoning:

1. **The existing SM-2 `calculateNext()` is already scheduled for replacement** — it does not surface any UI today, so migration cost to users is zero. This is the lowest-disruption moment to switch.
2. **The ~200-line Kotlin port is fully verifiable** via published test vectors. Risk is low.
3. **SM-2's lack of a stability model** is a concrete defect: a word that a user has reviewed 20 times and knows well should not collapse to a 1-day interval after one bad day. FSRS handles this correctly.
4. **Anki's deployment of FSRS to 10M+ users** is the most credible real-world validation available. If the world's largest SRS deployment chose FSRS as its default, the evidence burden for sticking with SM-2 is very high.
5. **The Phase 8 personalization path** (optimizer + user-specific weights) requires FSRS; SM-2 has no equivalent. Choosing FSRS now avoids a second algorithm migration later.

### SM-2 as a fallback note

SM-2 is retained as a **fallback option** in the following narrow scenarios:
- If the FSRS Kotlin port fails its test vectors and cannot be corrected within S1's time budget, S1 ships with the existing SM-2 implementation. The UI slices (S2, S3) are algorithm-agnostic; they call `SrsSchedulerService.computeNextReview()` regardless.
- The `SRSCard.calculateNext()` SM-2 method is **not deleted** in S1; it is preserved as a deprecated function and kept for reference. It can be removed in a future cleanup once FSRS has been in production for one month.

---

## Consequences

### Positive

- Users get a retention-optimal review schedule backed by the best publicly available algorithm.
- The personalization path (Phase 8) is unblocked — FSRS supports per-user parameter optimization, SM-2 does not.
- 4-grade (Again/Hard/Good/Easy) rating UX is simpler for mobile than SM-2's 6-point quality scale.
- Test vectors provide a deterministic correctness gate in CI.

### Negative / risks

- A ~200-line pure Kotlin implementation of FSRS must be written and validated. Owner should review `FsrsAlgorithm.kt` against the reference TypeScript implementation before S1 ships.
- Default FSRS parameters are calibrated on English-language Anki decks, not EN↔TR vocabulary pairs. Monitor retention metrics post-launch (see Risk R1).
- The V18 migration seeds FSRS fields from SM-2 data using an approximation. For users who have zero SM-2 review history (the vast majority at v1 launch), seeding is trivial (all cards start at `NEW` defaults). For any user who did use the hidden SM-2 fields in an early build, the seeding is good-faith best-effort.

### Neutral

- The FSRS algorithm update formulas are stateless — `schedule(card, rating, now) → card`. This makes them trivially testable and safe to call from any coroutine context.
- No external library dependency is added (we port the algorithm directly). This avoids version-pinning risk and keeps the APK size impact at zero.

---

## References

- Ye, J. et al. "A Stochastic Shortest Path Algorithm for Optimizing Spaced Repetition Scheduling." _SIGKDD 2022_ — primary academic source for FSRS.
- open-spaced-repetition/fsrs4anki GitHub repository — reference implementation + test vectors (High reputation, verified 2026-06-05).
- Anki 23.10 release notes — confirms FSRS as default scheduler since October 2023.
- SuperMemo SM-2 specification (Wozniak, 1987/2019) — reference for the baseline algorithm.
