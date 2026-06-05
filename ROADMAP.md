# TRAINVOC ROADMAP

> **Purpose**: The long-range, production-grade plan for taking Trainvoc from "feature-complete-but-unshipped" to a launched, multi-platform, growing product. This is the strategic narrative — *what next, in what order, and why*. For per-issue detail and done-conditions, see [`TODO.md`](./TODO.md). The historical refactor checklist lives in [`MASTER_FIX_PLAN.md`](./MASTER_FIX_PLAN.md) and is folded into the phases below.
>
> **Last Updated**: 2026-06-05 (branch `dev/2026-06-05`)
> **Grounded in**: HEAD of `master` + `dev/2026-06-05`, GitHub `ahmetabdullahgultekin/Trainvoc`.
> **Convention**: Phases are sequenced by dependency and value. Phase 0 is the only thing standing between today and a shipped Android v1; everything after compounds on a live product.

---

## TL;DR — where we are, where we're going

Trainvoc is a three-component vocabulary-learning ecosystem (Android app, React web, Spring Boot backend) that is **much further along than the legacy docs imply** but **not yet shipped**. The single biggest blocker to launch is **non-technical**: Google Play requires 20+ testers active for 14 consecutive days before granting production access, and that clock must be started *now*.

The engineering is in good shape: the clean-checkout build blocker is fixed (#221), the unit-test suite compiles and runs again (#222), the "Story Mode" honesty problem is resolved (#168), the auth surface now has Google Sign-In, email verification, and session-timeout handling (#191/#192/#193), the leaderboard shows real local progress (#194), and the web frontend has **zero known vulnerabilities**.

**The plan, in one breath**: ship Android v1 standalone (Phase 0–2) → stand up and harden the backend (Phase 3) → launch the web app (Phase 4) → reach iOS (Phase 5) → then grow the product with more games, social, analytics, and monetization (Phases 6–9), all on a foundation of real observability, testing, and CI/CD (Phase 10, continuous).

---

## Current state — honest completeness per component

### TrainvocClient (Android) — Kotlin 2.1, Compose, Room, Hilt — ~85% of v1 scope

| Area | State | Notes |
|------|-------|-------|
| Core learning (dictionary, quiz, word CRUD, offline Room) | ✅ Working | Offline-first; no backend required |
| Single-player games (11) + multiplayer UI (6 screens) | ✅ Implemented + wired | `#171` was a stale claim — verified present |
| Story Mode → **Learning Path** | ✅ Honest | Renamed; no false narrative promise (`#168`) |
| TTS / audio, gamification, stats | ✅ Connected | |
| Auth (Firebase email/password **+ Google Sign-In**) | ✅ v1-ready | Google Sign-In (`#192`), email-verify UI (`#191`), session timeout (`#193`) all shipped on `dev/2026-06-05` |
| Leaderboard | ✅ Local "Your Progress" + honest "global coming soon" | `#194`; online board awaits backend |
| Cloud sync / Drive backup | ⚠️ Local only | Drive upload/download are `TODO` — honestly "coming soon" for v1 |
| Clean-checkout build | ✅ Fixed | google-services plugin now conditional (`#221`) |
| Unit tests | ✅ Compile + run | 178 tests, 148 passing; 30 pre-existing Android-SDK/mock failures tracked as `#223` |
| Release signing | ⚠️ Env-gated | Keystore lives only on dev's `D:\` — **back it up** (operator) |

### TrainvocWeb (React 19 + TS + Vite + MUI) — ~80% — builds, **0 vulnerabilities**

| Area | State | Notes |
|------|-------|-------|
| Pages, API/WebSocket config, i18n (en/tr) | ✅ Present | Env-driven; legacy hardcoded URLs fixed |
| Auth | ❌ None | Server-driven, anonymous (by design until backend identity exists) |
| Dependency health | ✅ Clean | Security alerts resolved + safe Dependabot patches merged (`dev/2026-06-05`); `npm audit` = 0 |
| Tests | ✅ 118 vitest passing | + Playwright e2e specs |
| Build | ✅ `tsc && vite build` | Verified green |

### TrainvocBackend (Java 24, Spring Boot 3.5, dual PostgreSQL, WebSocket, Security) — ~85% — dev-grade security

| Area | State | Notes |
|------|-------|-------|
| REST controllers (6) + WebSocket multiplayer | ✅ Working | Room create/join persists |
| Dual datasource, Firebase+JWT auth wiring, rate limiting (bucket4j) | ✅ Configured | Auth not yet enforced end-to-end |
| Hardening | ❌ Gaps | No DTO layer, API versioning, pagination, Swagger exposure, broad `permitAll` (MASTER_FIX_PLAN Phase 5 `⏳ Deferred`) |
| Deployment | ❌ Never deployed | `docker-compose.yml` + `SSL_SETUP.md` exist; no live env, no DNS, no certs |
| Dependencies | ✅ Safe patches merged | postgres/org.json/jjwt/firebase/caffeine bumped (`dev/2026-06-05`); **build not locally verifiable** (host lacks JDK 24 toolchain) — CI must confirm |
| Tests | ✅ ~19 Java tests | |

---

## Strategic assessment

**The web + multiplayer + backend stack is NOT required for Android v1.** The Android app is offline-first; its core loop (dictionary → quiz → games → stats → gamification) works with no network. The fastest path to a *shipped product* is to decouple Android v1 from the backend and ship it standalone, deferring online multiplayer/sync/global-leaderboard to a v1.x once the backend is actually deployed.

Two things — and only two — gate Android v1:
1. **Build reproducibility + honest scope** — both now **done** (`#221`, `#168`).
2. **Google's 14-day testing wall** — a hard, non-engineering constraint that must be started immediately and run in parallel with everything else.

---

# PHASES

## Phase 0 — Unblock & start the clock (Week 1) — *do these in parallel, first*

Status: engineering items **largely complete on `dev/2026-06-05`**; the policy clock and keystore backup are **operator actions**.

- ✅ **P0-A Clean-checkout build** — google-services plugin applied conditionally on `google-services.json` presence (`#221`).
- ✅ **P0-B Story Mode honesty** — renamed to "Learning Path"; no narrative promised (`#168`).
- ✅ **P0-C Test suite compiles + runs** — `mockito-kotlin` declared, drifted tests reconciled, scheduler defect fixed (`#222`).
- ✅ **P0-E Auth gaps for v1** — Google Sign-In (`#192`), email-verify UI (`#191`), session timeout (`#193`).
- ✅ **P0-F Leaderboard honesty + value** — local "Your Progress" + "global coming soon" (`#194`).
- ⏳ **P0-C* Start Play closed-testing clock** *(operator)* — recruit **20+ testers**, keep active **14 consecutive days** (`#219` / `#220`). This is the long pole; nothing engineering can shorten it.
- ⏳ **P0-D Back up the signing keystore** *(operator)* — currently only at `D:\…\password.jks`. Losing it = the app can never be updated. Store it in a secrets manager / encrypted off-machine backup, and wire `TRAINVOC_KEYSTORE_*` into CI so release builds are reliably signed.

**Exit criteria**: a fresh `git clone` builds `assembleDebug`; Story Mode tells no lie; the test task runs; the 14-day tester window has *started*; the keystore is backed up.

## Phase 1 — Stabilize Android for standalone ship (Weeks 1–2)

- Drive remaining `#223` unit-test failures to green (Robolectric for `android.util.Log`/`DateFormat`/Play-Services static init; fix the genuine `UnfinishedStubbing` mock bug). Gate CI on `:app:testDebugUnitTest`.
- Feature-flag backend-dependent surfaces for a clean standalone build: online multiplayer, cloud sync, **global** leaderboard — all presented honestly as "coming soon", never broken.
- Finish the auth surface polish: surface email-verify / session-expiry on more entry points; add a "skip and play offline" fast path that never dead-ends.
- Confirm the **release build is signed** (env vars wired) and shrunk (R8) without stripping needed classes; smoke-test the AAB.
- Capture Play Store assets: 2–8 screenshots, 1024×500 feature graphic, short/long descriptions (EN + TR) — most copy already in `TrainvocClient/store-listing/`.

## Phase 2 — Android v1 production submission (Weeks 2–3, after the 14-day window)

- Re-apply for production access (`#220`) once the testing requirement is satisfied.
- Complete Play Console: **Data Safety** form, content rating questionnaire, target-audience, store listing.
- Decide the package-id question **before** production: staying on `com.gultekinahmetabdullah.trainvoc` vs migrating to `com.rollingcatsoftware.trainvoc` (a rename on a *published* app means a brand-new listing — decide once, decide early).
- Staged rollout (e.g. 10% → 50% → 100%) with crash/ANR watch via Play vitals.

**Milestone: Trainvoc Android v1 is live on Google Play production.**

## Phase 3 — Deploy & harden the backend (post-v1, Weeks 3–6)

This is where the online product comes alive. Sequencing matters: **deploy privately → harden → expose**.

1. **Provision**: a small cloud VM (GCP Compute Engine or equivalent), DNS for `api.trainvoc.rollingcatsoftware.com`, Let's Encrypt certs per `SSL_SETUP.md`, `docker-compose.yml` with real secrets (`--env-file`, never committed).
2. **Verify the build in CI with JDK 24** (the host used for `dev/2026-06-05` could not compile the backend) — make the Spring Boot build a required gate before any deploy.
3. **Merge the dependency backlog before exposure**: the safe non-major bumps are in; schedule dedicated test-then-merge for the **held majors** — Spring Boot 4 (`#25`), springdoc 3 (`#23`), gradle 9 (`#16`).
4. **Harden** (MASTER_FIX_PLAN Phase 5 backlog, now mandatory): introduce a **DTO layer**, **API versioning** (`/api/v1`), **pagination**, tighten `permitAll` to least-privilege, enforce Firebase/JWT auth end-to-end, finalize CORS allowlist, add **rate-limit tuning** + abuse protection, structured request/response logging, and a documented OpenAPI/Swagger surface (admin-gated).
5. **Light up online features against the live API**: real global leaderboard, Google Drive cloud sync/backup (the `CloudBackupManager` TODOs), end-to-end multiplayer over `wss://` with reconnection logic.

**Milestone: a hardened backend serves authenticated, versioned APIs over TLS.**

## Phase 4 — Web v1 launch (post-backend, Weeks 6–8)

- Point `trainvoc.rollingcatsoftware.com` at the live API; ship the web app.
- Add a minimal web auth/session story (reuse the backend identity) so multiplayer identity is coherent across web and mobile.
- Implement the deferred web items: WebSocket reconnection/backoff, error boundaries, richer loading states, and the Observer-pattern WebSocket refactor (MASTER_FIX_PLAN 3.2).
- Lighthouse/perf budget, accessibility pass, PWA install polish.

**Milestone: web and mobile share one live backend.**

## Phase 5 — iOS (Weeks 8–14)

- Evaluate **Compose Multiplatform / KMP** (reuse domain + data layers from Android) vs native SwiftUI. KMP is the leading candidate given the Kotlin codebase.
- Port the offline-first core loop first (dictionary/quiz/games/stats), then auth, then online features.
- App Store Connect setup, TestFlight beta, privacy nutrition labels, review submission.

**Milestone: Trainvoc on iOS.**

---

# GROWTH PHASES (post-launch, value-ordered, parallelizable)

## Phase 6 — Learning depth & engagement
- **Adaptive difficulty** (`#179`): quizzes that respond to performance.
- **Hint system** (`#181`): reveal-letter / eliminate-option.
- **Quiz feedback & speed bonuses** (`#180`, `#199`, `#200`): audio cues, time-pressure scoring.
- **Real chapter/story content** (`#177`, `#178`): build the narrative "Learning Path" that #168 deliberately deferred — themed word groupings, branching lessons, contextual usage.
- **Spaced repetition tuning** (SM-2 already in `Word`): surface "due for review" queues prominently.
- Flip-Cards readability (`#184`): pinch-zoom / manual enlarge.

## Phase 7 — Social & multiplayer growth
- **Friend system** (`#189`): add/view friends (needs deployed backend).
- Real-time multiplayer matchmaking, room discovery, rematch flows, post-game social.
- Shareable results, invite links, and (later) Firestore for social data (`#206`).
- Cross-device sync of progress through the authenticated account.

## Phase 8 — Analytics & data-driven iteration
- **Google Analytics / Firebase Analytics events** (`#205`): funnel + retention instrumentation.
- A/B testing harness for onboarding and difficulty curves.
- Surface `lastAnswered` and other collected-but-unshown stats (`#217`); session-length recommendations (`#214`).
- Backend metrics + dashboards (Prometheus/Grafana or hosted equivalent), error tracking (Sentry/Crashlytics).

## Phase 9 — Monetization & sustainability
- Billing client is already a dependency — design a **fair, non-predatory** model: optional "Trainvoc Plus" (advanced analytics, unlimited cloud sync, exclusive word packs/themes), one-time or subscription.
- Server-side receipt validation; entitlement gating behind feature flags.
- Keep the core learning loop free and complete — monetize *depth*, not access.

## Phase 10 — Engineering excellence (continuous, underpins everything)
- **CI/CD**: required gates per component (Android unit + lint; web `tsc`/vitest/build; backend JDK-24 build + tests); branch protection; reproducible release pipelines that sign the AAB from CI secrets.
- **Test coverage**: drive `#223` to zero, add instrumentation/e2e for critical flows (login, quiz, multiplayer), contract tests for the API.
- **Observability**: structured logs, traces, alerting before users notice.
- **Dependency hygiene**: keep Dependabot current; scheduled major-bump windows; `npm audit` / OWASP gates kept at zero.
- **Docs as a product**: keep `CLAUDE.md`/`README`/`CHANGELOG` per component in lockstep with the code (see the per-component guides).

---

## Folded-in MASTER_FIX_PLAN status

`MASTER_FIX_PLAN.md` Phases 1–6 are marked ✅ COMPLETED (security baseline, SOLID, design patterns, DRY, architecture, test infra), **but many sub-items are `⏳ Deferred`** — notably the backend DTO layer, API versioning, pagination, rate-limit tuning, Swagger exposure, and the web Observer-pattern WebSocket refactor. Those Deferred items are **not Android-v1 blockers**; they are absorbed into **Phase 3 (backend hardening)** and **Phase 4 (web)** above and become mandatory the moment those surfaces go public.

---

## Risks & watch-items

- **Keystore is a single point of failure** — only on the dev's Windows box. Back it up *before* Phase 2. (operator)
- **Google's 14-day testing wall** — a hard, non-engineering gate. Start it on day one. (operator)
- **Backend build needs JDK 24** — not available on every host; make the CI toolchain authoritative and gate deploys on a green backend build.
- **Web major bumps LANDED 2026-06-05** (vite 6→8, @vitejs/plugin-react 4→6, @playwright/test 1.57→1.60, vitest 4.0→4.1) — verified green: `npm run build`, 118/118 unit, 22/23 backend-independent e2e on playwright 1.60 (the 1 fail needs the live Spring API). See `TrainvocWeb/`.
- **Held major bumps** (Spring Boot 4, springdoc 3, gradle 9) — backend only; each needs a dedicated test-then-merge on a JDK that satisfies the toolchain (host JDK 21 cannot verify these yet). Don't let them rot, but don't rush them into a release either.
- **No live environment anywhere yet** — the first backend deploy + TLS + DNS is greenfield; budget real time.
- **Stale documentation drift** — legacy `.md` files have repeatedly mis-described the project (deleted games, unwired TTS, mockito vs MockK). Keep docs honest; treat doc updates as part of "done".

---

## See also: Future / Professionalization

A dedicated section on raising Trainvoc to professional engineering standards — security, compliance, accessibility, internationalization, and operational maturity — lives at the end of [`README.md`](./README.md#future--professionalization) and is cross-referenced from each component's `CLAUDE.md`.
