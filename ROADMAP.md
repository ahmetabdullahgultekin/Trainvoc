# TRAINVOC ROADMAP

> **Purpose**: Strategic sequencing toward a shippable **Android v1** (production track on Google Play), then web and iOS. This complements `TODO.md` (the issue-level tracker) and folds in `MASTER_FIX_PLAN.md`.
> **Last Updated**: 2026-06-04
> **Grounded in**: HEAD of `master` (commit `1f020c3`), GitHub `ahmetabdullahgultekin/Trainvoc`.
> **Convention**: This file is strategic ("what next, in what order, why"). For per-issue detail and done-conditions, see `TODO.md`.

---

## TL;DR

Trainvoc is **much further along than legacy docs imply**, but **not shippable from a clean checkout** and **blocked from Google Play production by policy, not code**.

- The Android app is **already in Google Play closed testing** (v1.1.2 released, v1.2.0 submitted). Production access was **DENIED 2026-01-25** — Google requires 20+ testers active for 14 consecutive days. This is the single biggest ship-blocker and it is **non-technical**.
- A **clean checkout of the Android app does NOT build**: the `com.google.gms.google-services` Gradle plugin is applied unconditionally but `google-services.json` is gitignored/absent. This is the #1 **technical** blocker.
- Two legacy "critical" claims are **stale**: the multiplayer & single-player game UIs were deleted in Jan 2026 but have since been **fully restored and wired into navigation** (commit `fb6d0bc`). Issue #171 should be closed.
- The one **genuine** product-critical gap remains: **"Story Mode" has no story content** (#168) — it is a CEFR level-picker that launches a normal quiz. Either build real story content or rename/reposition the feature before marketing it.

---

## Current State (honest completeness per component)

### TrainvocClient (Android) — Kotlin 2.1, Compose, Room, Hilt — ~80% of v1 scope, **does not build clean**

| Area | State | Evidence |
|------|-------|----------|
| Core learning (dictionary, quiz, word CRUD, offline Room) | ✅ Working | `ui/screen/dictionary`, `ui/screen/quiz`, `WordViewModel.kt` |
| Single-player games (11 games) | ✅ Implemented + wired | `ui/games/*Screen.kt` (11 screens) + `navigation/GamesNavigation.kt` → reached via `Route.GAMES_MENU` |
| Multiplayer UI (6 screens) | ✅ Implemented + wired | `ui/multiplayer/*` + `navigation/MultiplayerNavigation.kt` → reached via `Route.MULTIPLAYER_HOME`; **#171 is STALE** |
| Story Mode | ⚠️ **Misnamed** — no narrative content | `ui/screen/main/StoryScreen.kt` is a leaf-button CEFR level picker; `onLevelSelected` just calls `quizViewModel.startQuiz(...)`. **#168 CONFIRMED** |
| TTS / audio | ✅ Connected | `audio/TextToSpeechService.kt` used in `WordViewModel`, `ListeningQuizViewModel`, `AudioButton` (legacy "TTS not connected" note is stale) |
| Auth (Firebase + email/password) | ⚠️ Partial | Login/Register screens exist; **no Google Sign-In button** (#192), no email-verification UI (#191), no session-timeout handling (#193) |
| Cloud backup / sync | ⚠️ Local only | `sync/CloudBackupManager.kt` Drive upload/download are `TODO` (lines 437/459); local backup works |
| Leaderboard | ⚠️ "Coming Soon" placeholder | `LeaderboardScreen.kt` (#194) |
| Build from clean checkout | ❌ **Broken** | `app/build.gradle.kts:7` applies `google.services`; `google-services.json` is gitignored (`.gitignore:9`) and absent |
| Release signing | ⚠️ Env-gated | `signingConfigs.release` only populates if `TRAINVOC_KEYSTORE_*` env vars set, else release build is **unsigned**; keystore lives only on dev's Windows box (`D:\...\password.jks`) |
| Tests | ✅ Present | ~22 test files under `app/src/test` + `androidTest` |

Released: versionCode 13, versionName 1.2.0, minSdk 24, targetSdk/compileSdk 35, jvmTarget 17, locales en+tr.

### TrainvocWeb (React 19 + TS + Vite + MUI) — ~80% — builds, no auth

| Area | State | Evidence |
|------|-------|----------|
| Pages (home, play, game menu, lobby, room, leaderboard, profile, legal) | ✅ Present | `src/pages/*` (18 pages incl. Privacy/Terms) |
| API + WebSocket config | ✅ Env-driven | `src/api.ts`, `src/services/WebSocketService.ts` use `import.meta.env.VITE_API_URL`; `.env.example` complete (#001–003 fixed) |
| i18n (en/tr) | ✅ Present | `src/locales`; incomplete ES/DE/FR/AR removed per BRANDING.md |
| Auth | ❌ None | No login/session implementation (web is server-driven, anonymous) |
| Dependency health | ❌ **Debt** | 22 open Dependabot PRs + security PR #32 (5 alerts) unmerged |
| Tests | ✅ Present | ~14 vitest/playwright spec files |
| Build | ✅ `tsc && vite build` | `package.json` |

### TrainvocBackend (Java 24, Spring Boot 3.5, dual PostgreSQL, WebSocket, Security) — ~85% — builds, dev-grade security

| Area | State | Evidence |
|------|-------|----------|
| REST controllers | ✅ 6 controllers | Quiz, Auth, Sync, Leaderboard, Game, Word |
| WebSocket multiplayer | ✅ Working | room create/join persists (fixed via EntityManager tx control) |
| Dual datasource | ✅ Configured | `application-prod.properties` primary + `spring.second-datasource` (trainvoc-words), env-var passwords |
| Auth | ⚠️ Firebase + JWT wired, not enforced | `SecurityConfig.java`, `JwtTokenProvider.java`, `FirebaseConfig.java` (guarded by `firebaseEnabled` flag + try/catch — builds without creds) |
| SSL | ⚠️ Disabled | `application-prod.properties:13 server.ssl.enabled=false` (terminate at Nginx per `SSL_SETUP.md`) |
| Hardening | ❌ Gaps | No rate limiting, broad `permitAll`, no API versioning/pagination/DTO layer (all `⏳ Deferred` in MASTER_FIX_PLAN Phase 5) |
| Deployment | ❌ Never deployed | `docker-compose.yml` + `SSL_SETUP.md` exist but no live env |
| Tests | ✅ Present | ~19 Java test files |

### Infrastructure — scaffolded, never run

`docker-compose.yml` (Postgres + backend + nginx), `SSL_SETUP.md` (Let's Encrypt), `deploy.sh`, target domain `trainvoc.rollingcatsoftware.com` / `api.trainvoc.rollingcatsoftware.com`. No GCP VM provisioned; no DNS; no certs.

---

## Strategic Assessment

**The web and multiplayer stack (web + backend + WebSocket) is NOT required for Android v1.** The Android app is offline-first (local Room DB) and its core loop — dictionary, quizzes, single-player games, stats, gamification — works without any backend. The fastest path to a shipped product is to **decouple Android v1 from the backend** and ship it standalone, deferring multiplayer/sync/leaderboard to a v1.x once the backend is actually deployed.

Two things gate Android v1:
1. **Build reproducibility** (google-services blocker) and **honest feature scope** (Story Mode).
2. **Google's testing policy** — a 14-day clock that should be started *now*, in parallel with everything else, because it cannot be shortened.

---

## Phases toward Android v1 ship

### Phase 0 — Unblock the build & start the policy clock (Week 1) — **do these first, in parallel**
- **P0-A Fix clean-checkout build**: make the `google-services` plugin optional (apply-conditionally on `google-services.json` presence, or commit a CI-safe template / move Firebase Auth behind a build flag). Without this nobody but the original dev can build. *(see TODO P0)*
- **P0-B Resolve Story Mode (#168)**: decide build-content vs rename. Minimum-viable v1 = rename "Story Mode" → "Levels / Learning Path" and drop the "learn through stories" marketing claim, OR ship a thin first chapter. Do not ship a feature whose name lies.
- **P0-C Start Play testing clock**: recruit 20+ closed testers, keep active 14 days (#219). This is the long pole — start day 1.
- **P0-D Document the keystore** as a project secret (currently only on dev's `D:\` drive); back it up. Losing it = cannot update the app ever.

### Phase 1 — Stabilize Android for standalone ship (Weeks 1–2)
- Close stale issues: verify & close #171 (multiplayer UI exists), reconcile TODO statuses.
- Gracefully hide/disable backend-dependent features for a standalone build via a feature flag: multiplayer, cloud sync, online leaderboard (#194) → present as "Coming Soon" honestly rather than broken.
- Fix auth gaps that affect standalone UX or remove auth from the v1 surface: #191 (email-verify UI), #192 (Google Sign-In), #193 (session timeout).
- Confirm release build is **signed** (wire keystore env vars into the release process; today an env-less release is unsigned).
- Run the existing unit/instrumented tests green; capture screenshots + feature graphic (Play Store assets).

### Phase 2 — Android v1 production submission (Week 2–3, after 14-day test window)
- Re-apply for production access (#220) once the testing requirement is satisfied.
- Complete Play Console: Data Safety form, content rating, store listing (assets in `TrainvocClient/store-listing/`).
- Optional: migrate package id to `com.rollingcatsoftware.trainvoc` (note: changing applicationId on an already-published app means a **new listing** — decide before production).

### Phase 3 — Deploy backend & light up online features (post-v1)
- Provision GCP VM (or equivalent), DNS, Let's Encrypt (`SSL_SETUP.md`), run `docker-compose.yml` with real secrets.
- Merge the Dependabot/security backlog (PR #32 + Spring Boot/Postgres/JWT bumps) **before** exposing the backend publicly.
- Enable real online leaderboard, cloud sync (Drive), and multiplayer end-to-end against the deployed backend; verify `wss://`.
- Backend hardening from MASTER_FIX_PLAN Phase 5: rate limiting, tighten `permitAll`, DTO layer, API versioning, pagination, Swagger.

### Phase 4 — Web v1 (post-backend)
- Merge web Dependabot backlog; ship web at `trainvoc.rollingcatsoftware.com` pointed at the live API.
- Add a minimal auth/session story for web if multiplayer identity is needed.

### Phase 5 — iOS (future)
- Not started. Evaluate KMP/Compose Multiplatform reuse vs native SwiftUI. Out of scope until Android v1 + backend are live.

---

## Folded-in MASTER_FIX_PLAN status

Phases 1–6 of `MASTER_FIX_PLAN.md` are marked ✅ COMPLETED (security baseline, SOLID, design patterns, DRY, architecture, test infra), **but a large number of sub-items are `⏳ Deferred`** — notably backend DTO layer, API versioning, pagination, rate limiting, Swagger, and most architecture/state-management refactors. Treat those Deferred items as **Phase 3 (backend) hardening backlog**, not as done work. They are not v1-Android blockers.

---

## Risks & watch-items
- **Keystore single point of failure** — only on dev's Windows machine; back it up immediately.
- **Stale documentation** — multiple legacy `.md` files (root CLAUDE.md, TrainvocClient/CLAUDE.md, GAMES_UI_INVESTIGATION.md) still claim games are deleted and TTS is unwired; both are false on HEAD. They mislead planning.
- **33 open PRs** (22 currently open: Dependabot dep bumps across all three components + security PR #32). Unmerged security fixes are a liability the moment the backend/web go live.
- **No live environment anywhere** — backend deployment is greenfield; budget real time for first deploy + TLS + DNS.
- **Google production-access policy** is a hard 14-day wall, independent of engineering. Start it before anything else.
