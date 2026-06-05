# Trainvoc Monorepo — Code Quality & Architecture Review

**Date:** 2026-06-05
**Reviewer:** Senior code-quality & software-architecture review (automated, Claude Opus 4.8)
**Scope:** Current committed code at `master` HEAD across all three components.
**Branch for safe fixes:** `quality/2026-06-05`

This review reads the *actual committed code*, not the per-component `CLAUDE.md` summaries.
Several `CLAUDE.md`/`README` claims are **stale** and are flagged inline (the code is, in
most cases, *more* mature than the docs admit — see "Documentation drift" below).

---

## Component scorecards (1 = poor, 5 = excellent)

### TrainvocBackend (Java 24 / Spring Boot 3.5) — Overall **B+ (3.9/5)**

| Dimension | Score | Notes |
|---|---|---|
| Architecture (Spring layering) | 4 | Clean controller→service→repository layering; State + Strategy patterns are real and used; dual-datasource cleanly separated. |
| SOLID | 4 | Services split by SRP; `IRoomService`/`IPlayerService` interfaces for DIP. Loses a point for `GameService` facade leaking `getRoomService()`/`getPlayerService()` etc. (Law of Demeter) and one field-injection holdout (now fixed). |
| DRY / dead code | 4 | Mappers + DTO `response/` package reduce duplication; little dead code. |
| Error handling | 3 | `GlobalExceptionHandler` + typed `ErrorResponse` exist, but `QuizController` leaked raw `ex.getMessage()` and Turkish strings (fixed in this PR). |
| Type / null safety | 4 | `Optional` used in repos/services; `@NotBlank`/`@Valid` on inputs. |
| Naming / readability | 4 | Consistent, English-dominant; a few Turkish comments remained (fixed). |
| Test quality | 4 | **19 test classes** (services, controllers, websocket handlers, integration, perf) — *not* "1 test class" as `CLAUDE.md` claims. H2-backed, integration tagged & excluded by default. |
| Security | 3 | Real JWT + Firebase dual auth, BCrypt room passwords, env-based CORS, restricted WS origins, security headers, bucket4j rate-limit. **Held back by the hardcoded default JWT secret (P0) and a permissive `anyRequest().permitAll()` default (P1).** |
| Consistency w/ CLAUDE.md | 2 | Backend `CLAUDE.md` is badly out of date (see drift section). |

### TrainvocWeb (React 19 / TypeScript / Vite) — Overall **B (3.6/5)**

| Dimension | Score | Notes |
|---|---|---|
| Architecture (React structure) | 4 | Clear `pages/ services/ hooks/ components/ interfaces/` split; service layer abstracts axios; custom polling hooks are tidy. |
| SOLID | 4 | Services are cohesive single-purpose modules; hooks compose well. |
| DRY / dead code | 3 | **Duplicate test suites**: `src/services/RoomService.test.ts` *and* `src/services/__tests__/RoomService.test.ts` (same for `LeaderboardService`) — divergent copies of the same intent. |
| Error handling | 4 | Centralized `utils/errors.ts`; services surface typed errors. Missing top-level React error boundary. |
| Type / null safety | 4 | `tsc --noEmit` **passes clean**; strict mode on; only ~8 `any` and 16 `console.*` in `src` (acceptable). |
| Naming / readability | 4 | Idiomatic, consistent. |
| Test quality | 4 | Vitest suite present and green per docs (118 tests); good service coverage. The duplication (above) inflates the count. |
| Security | 3 | No auth by design (deferred). Client-side `hashPassword.ts` (SHA-256, room access only) is honestly documented. `withCredentials:true` + prod baseURL falls back to `window.location.origin` if `VITE_API_URL` unset — acceptable but fragile. |
| Consistency w/ CLAUDE.md | 3 | Web `CLAUDE.md` still says "No Unit Tests" (false — 7 test files). |

### TrainvocClient (Android / Kotlin / Jetpack Compose) — Overall **C+ (3.2/5)**

| Dimension | Score | Notes |
|---|---|---|
| Architecture (MVVM/Compose) | 3 | MVVM + Hilt DI + Room + use-case layer present and genuine. **But** several Compose "screen" files are monolithic: `ProfileScreen.kt` 1272 LOC, `HomeScreen.kt` 1052, `WordDetailScreen.kt` 1023, `DictionaryScreen.kt` 896, `SettingsScreen.kt` 895. Heavy package sprawl (~50 top-level packages incl. `examples/`, `features/` duplicating `repository/`+`viewmodel/`). |
| SOLID | 3 | Use cases + repositories show good intent; oversized screens violate SRP; `features/` vs root duplication blurs ownership. |
| DRY / dead code | 3 | `StateComponents.kt` unified states (good); but `features/` mirror packages and an `examples/` package suggest leftover scaffolding. 51 TODO/FIXME markers. |
| Error handling | 3 | Sealed `AuthResult`/`AuthState` is clean; many `catch (e: Exception)` swallow-and-degrade patterns; backend-sync failures silently downgrade to offline. |
| Type / null safety | 3 | Kotlin nullability used well in repos; **34 `!!` non-null assertions** across `main` are latent crash points. |
| Naming / readability | 4 | Consistent Kotlin style; descriptive names. |
| Test quality | 2 | 25 test files, **but ~30 of 178 unit tests fail at runtime (#223)** — Android-SDK/mock issues. Tests *compile and run* now (#222 fixed). A failing suite this large materially lowers confidence. |
| Security | 4 | No hardcoded secrets; `EncryptionHelper.kt` uses Android Keystore + AES-GCM; `google-services.json` gitignored and the plugin is conditionally applied. SHA-256 used appropriately (sync integrity, not auth). |
| Consistency w/ CLAUDE.md | 4 | Client `CLAUDE.md` was refreshed 2026-06-05 and correctly retracts the stale "games deleted/TTS unwired" saga. |

---

## Prioritized findings

### P0 — Critical (fix before any deployment)

**P0-1 — Hardcoded default JWT signing secret (backend).**
`security/JwtTokenProvider.java:26` (pre-fix) ships a constant fallback
`defaultSecretKeyThatShouldBeChangedInProduction123456` for `${jwt.secret}`. The property is
**not set in any `application*.properties`**, so unless the `JWT_SECRET` env var is supplied,
*every* environment signs and validates tokens with this publicly-known key → tokens are
forgeable (full auth bypass for the user/sync surface). `.env.example:42` documents `JWT_SECRET`
but nothing enforces it.
*Fix in this PR (partial, safe):* added a loud boot-time `WARN` when the insecure default is in
use, and named/documented the constant. *Roadmap (not auto-applied — would break local dev
startup):* fail-fast (throw) when `spring.profiles.active=prod` and the secret equals the default.

### P1 — High

**P1-1 — `anyRequest().permitAll()` default in SecurityConfig (backend).**
`config/SecurityConfig.java:48`. Only `/api/v1/sync/**` requires auth; the catch-all is
`permitAll`. Game/quiz/word/leaderboard being public is intentional, but a permissive *default*
means any future endpoint is unauthenticated unless someone remembers to add a matcher. Flip the
default to `.anyRequest().authenticated()` and explicitly allow the public set. (Not auto-applied:
changes runtime authz surface — needs the feature-flagged/staged rollout the user prefers for
auth-path changes.)

**P1-2 — Information disclosure via raw exception messages (backend).**
`controller/QuizController.java:40,60` returned `ex.getMessage()` straight to the client, plus
Turkish error strings ("Oda bulunamadı.", "Oda ayarları eksik."). **Fixed in this PR** — generic
English messages, no internal detail leaked.

**P1-3 — Android unit suite ~30 failures (#223).**
178 tests, ~148 pass. A persistently-red suite of this size erodes the safety net and will mask
regressions. These are Android-SDK/mock-environment failures (Robolectric/MockK), not product
logic, but they need to be driven to green or quarantined with `@Ignore` + tracking so CI is
meaningful.

### P2 — Medium

**P2-1 — Monolithic Compose screens (client).** `ProfileScreen.kt` (1272), `HomeScreen.kt`
(1052), `WordDetailScreen.kt` (1023), etc. Extract sections into composables/sub-files; these are
the hardest files to review and the most regression-prone.

**P2-2 — `GameService` facade leaks its collaborators (backend).**
`service/GameService.java:116-130` exposes `getRoomService()/getPlayerService()/
getGameStateService()/getRoomPasswordService()`. Callers reach through the facade, defeating the
SRP boundary. Add the few delegating methods callers actually need and drop the getters.

**P2-3 — Duplicate, divergent web test suites.**
`src/services/RoomService.test.ts` vs `src/services/__tests__/RoomService.test.ts` (and the
`LeaderboardService` pair) are different copies of the same tests. Keep the `__tests__/` copies,
delete the siblings, de-dupe assertions. (Left as doc rather than auto-deleted to avoid silently
dropping any unique case.)

**P2-4 — 34 `!!` non-null assertions (client).** Each is a potential `NullPointerException`.
Audit and replace with safe-call/`requireNotNull(...)` with messages, or restructure.

### P3 — Low / polish

- **P3-1** Field injection holdout `RoomCleanupService` `@Autowired` field → **fixed** (constructor injection + English comments) in this PR.
- **P3-2** `application.properties` ships `firebase.credentials.path=classpath:firebase-service-account.json` default — ensure that file is never committed (it is not present; keep it gitignored).
- **P3-3** Android `examples/` package and `features/` packages that mirror root `repository/`/`viewmodel/` — clarify or remove to reduce navigation cost.
- **P3-4** Web has no top-level React `ErrorBoundary` (docs note this as pending).

---

## Documentation drift (CLAUDE.md vs reality)

The component `CLAUDE.md` files under-report the codebase and misstate maturity. Concrete deltas:

| Claim in docs | Reality on HEAD |
|---|---|
| Backend "39 Java files" / "Java Classes 55+" | **105** Java files in `src/main`. |
| Backend "Test Classes: 1" | **19** test classes (service/controller/websocket/integration/perf). |
| Backend SecurityConfig "permit all requests (development mode)" w/ `anyRequest().permitAll()` only | Real config has JWT+Firebase filter, BCrypt encoder, STATELESS sessions, security headers, per-path matchers, env CORS. |
| Backend "CORS Allows All Origins `*`" + WebSocket `setAllowedOrigins("*")` | `CorsConfig` + `WebSocketConfig` both read `cors.allowed-origins` (env), default to the prod domain. |
| Backend "No Swagger/OpenAPI" | springdoc wired (`OpenApiConfig`, `@Tag`/`@Operation` annotations). |
| Web "No Unit Tests / No test files present" | 7 Vitest files, suite green (118 tests). |
| Web "38 files" / Client "257 Kotlin files" | Web **119** TS/TSX; Client **285** Kotlin. |

**Recommendation:** treat `ROADMAP.md` + this report as the source of truth; refresh the three
`CLAUDE.md` file-count/test/security sections (the root `CLAUDE.md` already carries a 2026-06-05
correction note, but the backend & web component files still carry the stale "Last Updated:
January 22, 2026" content).

---

## Honest strengths

- **Backend is the strongest component.** Genuine design patterns (State machine for game phases,
  Strategy for WS messages), SRP-split services with DIP interfaces, BCrypt + timing-safe room
  password comparison (`RoomPasswordService`), env-driven secrets/CORS, rate limiting, typed
  error responses, and a real multi-layer test suite incl. integration + performance tests.
- **Web is clean and modern.** `tsc --noEmit` passes with strict mode; service/hook layering is
  textbook; i18n (en/tr) wired; security alerts cleared (`npm audit` 0).
- **Client security hygiene is good.** Android Keystore + AES-GCM encryption helper, no committed
  secrets, conditional google-services plugin so a clean checkout builds.
- **The team retracts its own stale claims.** The root + client `CLAUDE.md` 2026-06-05 notes
  honestly correct earlier "games deleted / TTS unwired" panic — good engineering discipline.

---

## Build / verification notes

- **Web:** `npx tsc --noEmit` → **exit 0 (clean)** on this branch. No web source changed.
- **Android:** `./gradlew :app:help --offline` → **config evaluates clean on JDK 21**. Full
  `assemble`/unit-test requires the Android SDK + a JDK-24 *download* (toolchain), not run here
  (shared prod host, no emulator). No client source changed in this PR.
- **Backend:** toolchain pins **JDK 24** (`build.gradle` `languageVersion = 24`); host has only
  **JDK 21**. The backend **cannot be compiled/built on this host** without Gradle auto-provisioning
  JDK 24 (not triggered here). The three backend edits in this PR are minimal and review-verified
  (SLF4J logging, compile-time-constant `@Value`, generic error strings, constructor injection) but
  **CI must compile them on JDK 24.**

---

## Biggest roadmap refactor

**Decompose the Android UI layer and consolidate the package topology.** The five 850–1272 LOC
Compose screens plus the parallel `features/` ↔ root (`repository/`/`viewmodel/`) package
duplication and leftover `examples/` scaffolding are the dominant maintainability cost in the repo.
Extract screen sections into focused composables, collapse the duplicate package trees into one
canonical layout, and drive the #223 unit suite to green so the refactor has a real safety net.
Track in `ROADMAP.md` under a "Client professionalization" phase.

Secondary roadmap item (backend hardening, already partly in `ROADMAP.md` Phase 3): flip the
security default to `authenticated()`, fail-fast on the default JWT secret in `prod`, and tighten
the `GameService` facade encapsulation.
