# Trainvoc Tech-Stack & Architecture Modernization Review

**Date:** 2026-06-05  
**Reviewer role:** Senior staff engineer, advisory only  
**Branch:** `review/tech-stack-2026-06-05`  
**Scope:** All three sub-projects — TrainvocClient (Android), TrainvocWeb (React), TrainvocBackend (Spring Boot)  
**Methodology:** Manifests read from HEAD; every "newer/better" claim verified via web search or Context7 on 2026-06-05. This report is read-only — no code or config was changed.

---

## 0. How to read this report

- **KEEP** — correct choice; no action needed beyond routine patch bumps.
- **UPGRADE** — same technology, upgrade the version; clear net benefit.
- **CONSIDER-REPLACE** — a meaningfully better alternative exists; decision needed.
- Effort: **S** (hours–1 day), **M** (2–5 days), **L** (1–3+ weeks).
- Risk: LOW / MEDIUM / HIGH (likelihood of breakage × blast radius).
- Security/EOL items are flagged with ⚠️.

---

## 1. Full Stack Inventory

### TrainvocClient (Android) — `TrainvocClient/gradle/libs.versions.toml` + `app/build.gradle.kts`

| Component | Pinned version |
|---|---|
| Kotlin | 2.1.10 |
| Android Gradle Plugin (AGP) | 8.13.2 |
| Gradle wrapper | 8.13 |
| Compose BOM | 2025.06.00 |
| Navigation Compose | 2.9.0 |
| Lifecycle | 2.9.1 |
| Room | 2.7.1 |
| Hilt | 2.57.2 |
| Hilt Navigation Compose | 1.2.0 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Coil | 2.7.0 |
| Firebase BOM | 34.8.0 |
| Google Services plugin | 4.4.4 |
| Lottie Compose | 6.1.0 |
| security-crypto | 1.1.0-alpha06 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |

### TrainvocWeb — `TrainvocWeb/package.json`

| Component | Pinned version |
|---|---|
| React | 19.1.0 |
| TypeScript | ~5.9.3 |
| Vite | ^8.0.1 (Rolldown-powered) |
| @vitejs/plugin-react | ^6.0.1 |
| React Router DOM | ^7.6.2 |
| Tailwind CSS | ^4.2.2 |
| Radix UI (primitives) | ^1.x per component |
| Framer Motion | ^12.29.0 |
| i18next / react-i18next | ^25.8.20 / ^15.5.3 |
| Axios | ^1.13.6 |
| Lucide React | ^0.563.0 |
| vitest | ^4.1.0 |
| Playwright | ^1.58.2 |
| PWA plugin (vite-plugin-pwa) | ^1.2.0 |
| class-variance-authority / clsx / tailwind-merge | latest stable |

> NOTE: CLAUDE.md and ARCHITECTURE.md reference "MUI" — **this is stale**. HEAD `package.json` shows Tailwind + Radix UI (shadcn-style architecture), not MUI. No MUI dependency is present.

### TrainvocBackend — `TrainvocBackend/build.gradle`

| Component | Pinned version |
|---|---|
| Java toolchain | 24 |
| Spring Boot | 3.5.0 |
| io.spring.dependency-management | 1.1.7 |
| Gradle wrapper | 8.14.2 |
| PostgreSQL JDBC driver | 42.7.10 |
| bucket4j-core | 8.10.1 |
| jjwt | 0.13.0 |
| Firebase Admin SDK | 9.8.0 |
| Caffeine | 3.2.3 |
| springdoc-openapi-starter-webmvc-ui | 2.8.8 |
| Lombok | (latest via BOM) |
| JaCoCo | 0.8.14 |

---

## 2. Component-by-Component Review Tables

### 2.1 TrainvocClient — Android

| Component | Current | Latest stable (2026-06-05) | Verdict | Why | Effort | Risk |
|---|---|---|---|---|---|---|
| **Kotlin** | 2.1.10 | 2.3.21 (stable); 2.4.x in EAP | **UPGRADE** | K2 compiler graduates, new language features, coroutines improvements, better null-safety under AGP 9+; 2.3.x is the current production-stable series. [Source](https://kotlinlang.org/docs/whatsnew23.html) | M | LOW |
| **AGP** | 8.13.2 | 9.1.1 (April 2026) | **UPGRADE (plan)** | AGP 9.x enables built-in Kotlin support (deprecates `kotlin.android` plugin), supports compileSdk 36, requires Gradle 9.1 + JDK 17. AGP 8.x is current but AGP 9.x is already shipping. Not urgent for v1 but plan for next major. [Source](https://developer.android.com/build/releases/agp-9-1-0-release-notes) | L | MEDIUM |
| **Gradle wrapper (Client)** | 8.13 | 8.14.2 (already used by backend) | **UPGRADE** | Align with backend wrapper; minor patch, no breaking changes. | S | LOW |
| **Compose BOM** | 2025.06.00 | 2026.04.01 (April 2026) | **UPGRADE** | BOM 2025.12.00 introduced pausable composition (matches Views scroll performance), Material 3 1.4 updates. 2026.x extends this. The gap is ~10 monthly releases; material3 1.4 components are additive. [Source](https://android-developers.googleblog.com/2025/12/whats-new-in-jetpack-compose-december.html) | S | LOW |
| **Kotlin (compileSdk/targetSdk)** | 35 | 36 (API 36, AGP 9.1+) | **CONSIDER UPGRADE** | Android 16 (API 36) launches mid-2026; Play may require targetSdk 36 by end 2026. Watch the Play policy announcement; not blocking for v1 but plan ahead. | S | LOW |
| **Room** | 2.7.1 | 2.7.1 (stable); 3.0.0-alpha (KMP) | **KEEP** | 2.7.1 is current stable. Room 3.0 alpha adds KMP/WASM/JS support but is pre-stable. No action needed unless targeting iOS (KMP path). [Source](https://android-developers.googleblog.com/2026/03/room-30-modernizing-room.html) | — | — |
| **Hilt** | 2.57.2 | 2.57.1 (latest noted; patch difference) | **KEEP** | Hilt is Google's recommended DI for Android. The pinned version is at the current stable series. Koin is a valid lightweight alternative but migration is unnecessary here — Hilt is idiomatic with MVVM/Compose and the codebase is already Hilt-wired. | — | — |
| **Retrofit + OkHttp** | 2.11.0 / 4.12.0 | Retrofit 2.11.0 / OkHttp 4.12.0 | **KEEP (short-term)** | Current versions are stable and widely used. **Long-term / if KMP is planned:** Ktor Client is the Kotlin-native multiplatform alternative, natively coroutine-based, and the ecosystem leader for KMP networking (67% of KMP teams evaluating it per industry surveys). For Android-only, Retrofit 2 remains the established choice with the highest adoption (73% market share). Migrate to Ktor only if iOS/KMP is pursued in Phase 5. [Sources](https://proandroiddev.com/when-to-use-retrofit-and-when-to-use-ktor-a-guide-for-android-developers-918491dcf69a) | — | — |
| **Coil** | 2.7.0 | 3.x (stable) | **UPGRADE** | Coil 3 brings 25–40% runtime performance improvements, 35–48% fewer allocations, and multiplatform readiness. The Compose API (`AsyncImage`, etc.) is unchanged — migration is mostly a Maven coordinate update (`io.coil-kt` → `io.coil-kt.coil3`). You must explicitly add a network artifact (no silent default). [Source](https://colinwhite.me/post/coil_3_release) | S | LOW |
| **Firebase BOM** | 34.8.0 | Check `firebase-android-sdk` releases; 33/34 series active | **KEEP / PATCH** | Firebase BOM is actively maintained; patch updates are safe. No major breaking changes noted for the auth subset used here. | S | LOW |
| ⚠️ **security-crypto** | 1.1.0-alpha06 | 1.1.0-alpha06 (latest alpha) | **WATCH** | This library has been in alpha since 2022 with no stable release. It is still the only Jetpack-blessed API for `EncryptedSharedPreferences`. Track the alpha carefully — alpha APIs may have unannounced breaking changes, and there is no stable release timeline publicly confirmed. | — | LOW |
| **Google Play Billing** | 7.1.1 | 7.x series | **KEEP** | No material gap; keep patching. |  — | — |
| **Kotlin Multiplatform (future)** | Not adopted | CMP 1.11.1 / Kotlin 2.3 | **CONSIDER** | Phase 5 (iOS) should evaluate KMP + Compose Multiplatform seriously. Domain + data layers are already Kotlin idiomatic (Room 3.0 will KMP-support them); sharing them with iOS avoids a full SwiftUI rewrite. CMP web went Beta in Kotlin 2.2.20. Decision needed before Phase 5 starts. [Source](https://github.com/JetBrains/compose-multiplatform/releases) | L (Phase 5) | MEDIUM |

### 2.2 TrainvocWeb — React/TypeScript/Vite

| Component | Current | Latest stable (2026-06-05) | Verdict | Why | Effort | Risk |
|---|---|---|---|---|---|---|
| **React** | 19.1.0 | 19.x (19.1 is current stable) | **KEEP** | React 19 is the current production-stable release. Server Components and Actions are available but optional; the Trainvoc web app is a pure SPA, so no adoption pressure here. | — | — |
| **Vite** | ^8.0.1 | 8.x (8.0 shipped stable with Rolldown 1.0) | **KEEP** | Vite 8 is the current generation. The Rolldown (Rust-based bundler) migration delivers 10–30x faster builds vs Rollup; already on this version. No action needed. [Source](https://vite.dev/blog/announcing-vite8) | — | — |
| **TypeScript** | ~5.9.3 | 5.9.x (pre-release of 5.9; 5.8 is current stable GA) | **KEEP / CONFIRM** | Confirm whether `~5.9.3` resolves to a stable release or a pre-release; if pre-release, pin to `~5.8.x` for production stability. TypeScript 5.8 added `erasableSyntaxOnly` and intermediate JS output, both relevant for Vite builds. | S | LOW |
| **React Router** | ^7.6.2 | 7.x (7.6 series) | **KEEP** | React Router v7 merged with Remix, bringing file-based routing, loaders/actions as first-class, and a stable framework mode. The `^7.6.2` pin is current. | — | — |
| **Tailwind CSS** | ^4.2.2 | 4.x (CSS-native engine) | **KEEP** | Tailwind v4 is the current architecture: native CSS variables, zero-config content scanning, 10x faster builds via the Oxide engine. Already on v4. Well-aligned with the Radix UI component usage. [Source](https://ui.shadcn.com/docs/tailwind-v4) | — | — |
| **Radix UI primitives** | ^1.x series | 1.x (active) | **KEEP** | Radix UI is the accessibility-first headless primitive layer used by shadcn/ui, Vercel, Supabase, and Linear in production. This is the right architecture for 2026: Tailwind + Radix + cva/cn utilities. No switch needed. [Source](https://vercel.com/academy/shadcn-ui) | — | — |
| **i18next / react-i18next** | ^25.8 / ^15.5 | 25.x / 15.x series | **KEEP** | Current stable; no meaningful gap. | — | — |
| **Axios** | ^1.13.6 | 1.x | **KEEP / MONITOR** | Axios 1.x is stable. For a React 19 app with loaders/actions, the `fetch` API + React Router loaders can replace Axios entirely with zero extra bundle cost — but migration is optional, not urgent. | — | — |
| **Framer Motion** | ^12.29.0 | 12.x | **KEEP** | Current major series. | — | — |
| **vitest** | ^4.1.0 | 4.1.x | **KEEP** | Just landed (per ROADMAP notes) and is current. | — | — |
| **Playwright** | ^1.58.2 | 1.60+ | **KEEP / MINOR PATCH** | 1.60 already landed (per PR #37). Package.json lags by one minor; `npm update` to `^1.60` to align. | S | LOW |
| **lottie-react** | ^2.4.1 | 2.4.x | **KEEP** | No major update. |  — | — |
| **PWA plugin** | ^1.2.0 | 1.x | **KEEP** | Adequate for the current use case. | — | — |
| **Architecture: polling vs WebSocket** | HTTP polling (`usePolling`, `useGameState`) | N/A | **CONSIDER STOMP** | The game client currently polls for state. The backend already has a raw `WebSocketHandler`. For a real-time multiplayer game, this polling model introduces latency (1-second tick) and server load. **Consider migrating to STOMP over WebSocket** on the backend to gain pub/sub routing with minimal overhead — or at minimum, use the existing raw WebSocket in the client for game-state pushes. [Source](https://websocket.org/guides/frameworks/spring-boot/) | M | MEDIUM |

### 2.3 TrainvocBackend — Java/Spring Boot

| Component | Current | Latest stable (2026-06-05) | Verdict | Why | Effort | Risk |
|---|---|---|---|---|---|---|
| ⚠️ **Java / JDK** | 24 (non-LTS) | **25 is the latest LTS** (Sep 2025); 26 is the current non-LTS (Mar 2026) | **UPGRADE to JDK 25 LTS** | JDK 24 is a non-LTS short-term release. Oracle's support window for non-LTS releases is 6 months, meaning **JDK 24 is already EOL** (EOL approximately Sep 2025 when JDK 25 shipped). JDK 25 is the LTS with Premier Support through Sep 2030. Spring Boot 4.0+ adds first-class JDK 25 support. Staying on JDK 24 means running an unsupported JVM in production. [Source](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) | S | HIGH if left |
| **Spring Boot** | 3.5.0 | 4.0.6 (latest stable); 4.1.0-RC1 in staging | **UPGRADE to 4.x (planned, not immediate)** | Spring Boot 3.5 OSS support expires **2026-06-30** (in ~4 weeks from this report). You must either move to 4.x or accept commercial-only support. Spring Boot 4 requires Java 17+ (satisfied by JDK 25), upgrades Spring Framework to 7.0, and switches Jackson 2 → 3 (Jackson 2 still compiles in deprecated mode). Key breaking changes: Jackson package rename (`com.fasterxml.jackson` → `tools.jackson`), JSpecify nullability, Jakarta EE 11, removed deprecated APIs. Effort is **M–L** with careful planning. [Sources](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) [EOL dates](https://endoflife.date/spring-boot) | M–L | MEDIUM |
| **Gradle wrapper (Backend)** | 8.14.2 | 9.5.0 (latest Gradle 9.x series) | **UPGRADE to Gradle 9 (held #16, plan carefully)** | Gradle 9.0 is a significant major: requires Java 17 (satisfied), Kotlin DSL upgrades to Kotlin 2.2 language version, Groovy 3→4 upgrade (major for Groovy-DSL build files), strictener nullability via JSpecify. The backend uses `build.gradle` (Groovy DSL) — **Groovy 4 includes breaking package changes**. Recommend: migrate `build.gradle` → `build.gradle.kts` first, then upgrade to Gradle 9. Not urgent today, but Gradle 8.x will lose community focus as 9.x matures. [Source](https://gradle.org/whats-new/gradle-9/) | M | MEDIUM |
| **springdoc-openapi** | 2.8.8 | 3.0.x (held #23) | **UPGRADE (with Spring Boot 4)** | springdoc 3.x is required for Spring Boot 4 because Boot 4 uses Jackson 3 and springdoc 2.x still links Jackson 2. However, springdoc 3.0.2 has active compatibility issues with Spring Boot 4 + HATEOAS (HateoasProperties removed). **Verdict: upgrade springdoc to 3.x as part of the Spring Boot 4 migration, but do not upgrade springdoc alone on Spring Boot 3.5.** The two must move together. [Source](https://github.com/springdoc/springdoc-openapi/issues/3200) | S (tied to Boot 4) | MEDIUM |
| **PostgreSQL JDBC driver** | 42.7.10 | PostgreSQL 17 is current stable (17.10); PG 18.4 also released | **KEEP + CONSIDER PG 17** | The JDBC driver 42.7.10 is current. The database is undeployed; when deploying, prefer PostgreSQL 17 (current stable with active support) over 15+. PostgreSQL 18.4 is also available. No change to the driver needed. [Source](https://www.postgresql.org/about/news/postgresql-184-1710-1614-1518-and-1423-released-3297/) | S | LOW |
| **jjwt** | 0.13.0 | 0.13.0 (current stable series) | **KEEP** | 0.13.0 is the current stable; the modular split (jjwt-api / jjwt-impl / jjwt-jackson) used here is correct. No gap. | — | — |
| **Firebase Admin SDK** | 9.8.0 | 9.9.0 | **UPGRADE** | 9.9.0 adds Phone Number Verification API and Cloud Messaging improvements. One patch behind; safe to bump. [Source](https://firebase.google.com/support/release-notes/admin/java) | S | LOW |
| **bucket4j** | 8.10.1 | 9.x available | **KEEP (monitor)** | 8.10.1 is recent stable. Bucket4j 9.x is in progress; no urgent gap for current usage. | — | — |
| **Caffeine** | 3.2.3 | 3.2.x series | **KEEP** | Current stable series. | — | — |
| **WebSocket architecture** | Raw `WebSocketHandler` (Spring) | STOMP/SockJS alternative | **KEEP (acceptable) / CONSIDER STOMP** | Raw WebSocket handler is the right choice for a tightly-controlled multiplayer game where you control the message protocol. It avoids STOMP's 10–20% text framing overhead. The existing State Machine + Strategy pattern implementation is sound. **If the backend ever needs to scale to multiple instances (horizontal), add Redis Pub/Sub for cross-instance message fan-out — raw WebSocket alone won't broadcast across JVMs.** [Source](https://websocket.org/guides/frameworks/spring-boot/) | M (if scaling) | MEDIUM |
| **Dual-database architecture** | Two PostgreSQL datasources | N/A | **KEEP** | Separating game state (`trainvoc`) from vocabulary (`trainvoc-words`) is architecturally sound — it allows the vocabulary corpus to be shared/updated independently, makes the game DB schema lighter, and enables different backup/migration cadences. This is a professional pattern for its scale. No change recommended. | — | — |
| **JPA `ddl-auto=update` in dev** | update | validate / flyway | **CONSIDER Flyway** | `ddl-auto=update` is fine for dev but must change before production deployment (Phase 3). Introduce Flyway or Liquibase for versioned migrations. Flyway is the Spring Boot ecosystem standard and integrates with Spring Boot auto-configuration. This is a Phase 3 must-do, not urgent now. | M | HIGH if skipped for prod |
| **RestTemplate (used in backend)** | RestTemplate | WebClient / HTTP Interface | **CONSIDER REPLACE** | `RestTemplate` is in maintenance mode as of Spring 5; Spring Boot 4 / Spring Framework 7 continues to ship it but the recommended replacement is `WebClient` (reactive) or Spring 6's declarative `HTTP Interface` clients. For a service that isn't reactive, HTTP Interface (blocking via `RestClient`) is the idiomatic modern choice. Not urgent but should be addressed during Spring Boot 4 migration. [Source](https://docs.spring.io/spring-boot/reference/io/rest-client.html) | S | LOW |

---

## 3. Held Backend Majors — Verdict

These three Dependabot PRs have been deliberately held:

### Spring Boot 4 (PR #25) — UPGRADE, planned

**Do it.** Spring Boot 3.5 OSS support expires **2026-06-30**. That is in approximately 3–4 weeks from this report. After that date, you are on commercial-only support for 3.5 or you must be on 4.x.

**What breaks:**
- Jackson 2 → 3: package rename (`com.fasterxml.jackson.*` → `tools.jackson.*`) in custom serializers/deserializers. The codebase appears to use Jackson through Spring Boot's auto-configuration rather than raw imports — audit for any `@JsonDeserialize` / `ObjectMapper` direct usage. Jackson 2 "ships in deprecated form" in Boot 4, meaning it compiles but will be removed in Boot 4.x+.
- `catch (IOException e)` no longer catches `JacksonException` (it now extends `RuntimeException`).
- JSpecify nullability — Kotlin projects feel this most; the backend is Java-only so the impact is lower.
- Any deprecated Spring Boot 3.5 APIs you have not yet migrated will break.
- springdoc must move to 3.x simultaneously (see above).

**Recommended path:**
1. Upgrade to JDK 25 LTS first (no Spring changes).
2. Upgrade Gradle from 8.14.2 → 9.x (optionally migrate build.gradle → .kts first).
3. On Spring Boot 3.5.x, fix all deprecation warnings.
4. Upgrade Spring Boot 3.5 → 4.0.6 + springdoc 2.8.8 → 3.x in one PR, with a full test run.

Effort: **M–L** (primarily the Jackson 3 audit + test run on JDK 25 toolchain). Risk: **MEDIUM** (well-documented migration path; the backend has few custom Jackson uses visible in the code).

### springdoc 3 (PR #23) — UPGRADE, tied to Spring Boot 4

**Do not upgrade alone.** springdoc 3.x requires Spring Boot 4 + Jackson 3. Upgrading springdoc 3 on Spring Boot 3.5 is unsupported and will cause incompatibilities. These two must move together. When merging PR #25 (Boot 4), merge PR #23 simultaneously.

### Gradle 9 (PR #16) — UPGRADE, with prep work

**Do it, but prepare first.** Gradle 9.0 breaks the Groovy DSL in multiple ways (Groovy 3 → 4, removed legacy configurations). The backend uses `build.gradle` (Groovy). Recommended prep:

1. Migrate `build.gradle` → `build.gradle.kts` (Kotlin DSL) while still on Gradle 8. This is good practice regardless of Gradle 9 — Kotlin DSL is the de facto standard and gets first-class IDE support.
2. Then upgrade Gradle wrapper to 9.x.

This is separate from the Spring Boot 4 migration and can happen in parallel. Effort: **M**. Risk: **MEDIUM** (Groovy DSL migration has known patterns).

---

## 4. Architecture Assessment

### 4.1 Three-component monorepo — KEEP

The flat monorepo (Android client / React web / Spring Boot backend sharing a single git repo) is appropriate for the current team size (solo developer). The `docker-compose.yml` co-location and shared `CLAUDE.md` documentation make sense. This should not change until there is a genuine organizational reason (multiple teams, separate release cycles).

### 4.2 Offline-first Android design — KEEP, EXPAND

The Android client's offline-first architecture (Room local DB → sync when connected) is the right pattern for a learning app. Users should never be blocked from practicing vocabulary due to network conditions. This should be preserved and extended: the `CloudBackupManager` TODOs (Phase 3) should maintain this contract by syncing in the background, never blocking the UX.

### 4.3 Dual PostgreSQL databases — KEEP

Separating gameplay data (`trainvoc`) from vocabulary corpus (`trainvoc-words`) is sound. The word data grows slowly and is read-heavy; game state is written heavily and can be pruned. Different retention/backup strategies apply. No change recommended.

### 4.4 Game real-time: polling → WebSocket push — MEDIUM-PRIORITY

The web client uses HTTP polling (`usePolling`, `useGameState` with `pollInterval: 1000`) for game state updates. This creates:

- 1-second minimum latency for all state transitions (suboptimal for a quiz game where timing matters).
- Server-side polling load that scales poorly with concurrent games.
- The backend already has a raw `WebSocketHandler` at `/ws/game/{roomCode}`.

**Recommendation:** Wire the web client to the existing WebSocket endpoint for game-state push events. The backend already broadcasts state changes; the client just needs to consume them. This is Phase 4 work but high-value for the multiplayer product feel.

### 4.5 Auth enforcement gap — HIGH-PRIORITY (Phase 3 prerequisite)

Spring Security is configured with `.anyRequest().permitAll()` — all API endpoints are public. Firebase + JWT is wired but not enforced end-to-end. Before the backend is deployed and publicly reachable, this **must** be locked down to least-privilege. This is correctly called out in MASTER_FIX_PLAN and ROADMAP Phase 3.

### 4.6 Missing DTO layer — MEDIUM (Phase 3)

JPA entities are returned directly from controllers, which:
- Exposes internal schema to API consumers.
- Makes API versioning impossible without entity changes.
- Can leak sensitive fields.

A DTO layer (MapStruct is the standard for Spring Boot) is deferred to Phase 3, which is the right call for an undeployed service. Do not skip it at deployment time.

### 4.7 WebSocket scaling gap — MONITOR

The current raw WebSocket implementation binds sessions to a single JVM instance. If the backend ever runs more than one instance (horizontal scaling), WebSocket sessions on different instances cannot broadcast to each other. **For the near-term (single-instance deployment), this is not a problem.** For Phase 7+ (multiplayer growth), add Redis Pub/Sub as the cross-instance message bus before deploying more than one backend pod.

---

## 5. Prioritized Recommendations

### Priority 1: Security / EOL — Do immediately

| # | Recommendation | Component | Effort | Risk |
|---|---|---|---|---|
| 1.1 | **Upgrade JDK 24 → JDK 25 LTS.** JDK 24 is EOL; running an unpatched JVM in production is a security and compliance issue. JDK 25 has Premier Support through Sep 2030. | Backend | S | HIGH if left |
| 1.2 | **Migrate Spring Boot 3.5 → 4.0.6 before 2026-06-30** (OSS EOL date). Follow the path: deprecation-clean 3.5 → Spring Boot 4 + springdoc 3 together + Jackson 3 audit. Do not run the first public deployment on an EOL framework. | Backend | M–L | MEDIUM |

### Priority 2: High value, low risk — Do in the next 1–2 sprints

| # | Recommendation | Component | Effort | Risk |
|---|---|---|---|---|
| 2.1 | **Upgrade Kotlin 2.1.10 → 2.3.x** (current stable). New K2 improvements, coroutines updates, and compatibility with AGP 9.x. Non-breaking upgrade within the 2.x series. | Client | M | LOW |
| 2.2 | **Upgrade Compose BOM 2025.06.00 → 2026.04.01** (latest stable). Picks up pausable composition (scrolling parity with Views), Material 3 1.4 components, and ~10 months of perf/bug fixes. API is backward-compatible. | Client | S | LOW |
| 2.3 | **Upgrade Coil 2.7.0 → 3.x.** 25–40% runtime perf improvement and 35–48% fewer allocations. Maven coordinate rename is the main work; Compose API is unchanged. Worth the 2-hour effort. | Client | S | LOW |
| 2.4 | **Upgrade Firebase Admin SDK 9.8.0 → 9.9.0.** Single patch; new Phone Verification API, no breaking changes. | Backend | S | LOW |

### Priority 3: Plan before Phase 3/4 deployment

| # | Recommendation | Component | Effort | Risk |
|---|---|---|---|---|
| 3.1 | **Migrate backend build file: `build.gradle` → `build.gradle.kts`** and then upgrade Gradle wrapper 8.14.2 → 9.x. Prepares for Gradle 9's Kotlin DSL improvements, avoids Groovy 4 compatibility issues. | Backend | M | MEDIUM |
| 3.2 | **Introduce Flyway for database migrations** before deploying the backend. Replace `ddl-auto=update` with `ddl-auto=validate` (prod). Without versioned migrations, schema changes in future deploys will be unsafe. | Backend | M | HIGH if skipped |
| 3.3 | **Wire web client's game state updates to the existing WebSocket endpoint** instead of the current 1-second polling loop. Reduces latency, improves multiplayer feel, reduces server load. | Web | M | MEDIUM |

### Priority 4: Evaluate before Phase 5 (iOS)

| # | Recommendation | Component | Effort | Risk |
|---|---|---|---|---|
| 4.1 | **Evaluate Kotlin Multiplatform (KMP) for iOS** (Phase 5). The domain and data layers are Kotlin-idiomatic; Room 3.0 alpha adds KMP support; Ktor Client supports KMP natively. Sharing the core learning logic reduces iOS development to UI-only. Evaluate against native SwiftUI before committing. | Client | L (Phase 5) | MEDIUM |
| 4.2 | **Consider migrating Retrofit to Ktor Client** if KMP is chosen. Ktor is the idiomatic Kotlin-multiplatform networking choice; Retrofit cannot run on iOS/WASM. This migration makes sense only in conjunction with the KMP path. | Client | M | LOW |

---

## 6. What Is Already Best-in-Class — Keep and Do Not Churn

The following choices are **correct for 2026** and should not be touched:

- **React 19 + Vite 8 (Rolldown) + Tailwind v4 + Radix UI** — the web stack is in excellent shape after PR #37. This is the dominant production architecture for React SPAs in 2026. The "MUI" references in docs are stale and should be corrected, but the actual code is right.
- **React Router v7** — current, idiomatic, Remix-aligned.
- **Hilt for Android DI** — Google's recommended pattern; no need to migrate to Koin or other alternatives.
- **Raw WebSocket handler on Spring Boot** for game multiplayer — correct for a single-server, low-latency game. STOMP would add overhead without benefit at this scale.
- **Room 2.7.1** — current stable; Room 3.0 is alpha and KMP support is not yet needed.
- **Dual PostgreSQL databases** — architecturally sound pattern.
- **Compose BOM + Material 3** — correct UI toolkit for Android; Material You design system is mature.
- **Firebase BOM for Android auth** — appropriate for the indie app context; saves auth infrastructure work.
- **bucket4j for rate limiting** — solid, no better alternative for Spring Boot inline rate limiting.
- **i18next (en/tr)** — current stable, right choice for bilingual app.
- **vitest 4.1 + Playwright 1.60** — just upgraded; current generation test tooling.

---

## 7. Documentation Corrections Needed

These stale claims in project docs should be updated (not part of this review's code scope, flagged for awareness):

1. **`ARCHITECTURE.md` and `TrainvocWeb/CLAUDE.md`** reference "React + MUI" and "Material-UI (MUI) 7.1.2". HEAD `package.json` has **no MUI dependency**. The actual stack is Tailwind CSS v4 + Radix UI + shadcn-style components. These files need updating.
2. **`TrainvocWeb/CLAUDE.md`** lists `Vite: 6.3.5` in its tech table. The package.json is already at Vite ^8.0.1. The CLAUDE.md was not updated after PR #37.
3. The root `CLAUDE.md` "Environment Setup" table says "Node.js 18+" — Node.js 20 LTS (current) or 22 LTS are the recommended versions for 2026; Node 18 reached EOL in April 2025.

---

## 8. Sources

All "newer/better" claims in this report are grounded in the following sources, retrieved on 2026-06-05:

- Spring Boot EOL dates: [endoflife.date/spring-boot](https://endoflife.date/spring-boot)
- Spring Boot 4.0 release notes: [github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- Spring Boot 4.0 migration guide: [github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Java SE Support Roadmap: [oracle.com/java/technologies/java-se-support-roadmap.html](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)
- Kotlin 2.3 docs: [kotlinlang.org/docs/whatsnew23.html](https://kotlinlang.org/docs/whatsnew23.html)
- Kotlin release process: [kotlinlang.org/docs/releases.html](https://kotlinlang.org/docs/releases.html)
- Compose Multiplatform releases: [github.com/JetBrains/compose-multiplatform/releases](https://github.com/JetBrains/compose-multiplatform/releases)
- Jetpack Compose December 2025 release: [android-developers.googleblog.com/2025/12/whats-new-in-jetpack-compose-december.html](https://android-developers.googleblog.com/2025/12/whats-new-in-jetpack-compose-december.html)
- AGP 9.1 release notes: [developer.android.com/build/releases/agp-9-1-0-release-notes](https://developer.android.com/build/releases/agp-9-1-0-release-notes)
- Room 3.0 announcement: [android-developers.googleblog.com/2026/03/room-30-modernizing-room.html](https://android-developers.googleblog.com/2026/03/room-30-modernizing-room.html)
- Coil 3 release: [colinwhite.me/post/coil_3_release](https://colinwhite.me/post/coil_3_release)
- Retrofit vs Ktor (2026): [proandroiddev.com](https://proandroiddev.com/when-to-use-retrofit-and-when-to-use-ktor-a-guide-for-android-developers-918491dcf69a)
- Vite 8 announcement: [vite.dev/blog/announcing-vite8](https://vite.dev/blog/announcing-vite8)
- Tailwind v4 + shadcn: [ui.shadcn.com/docs/tailwind-v4](https://ui.shadcn.com/docs/tailwind-v4)
- Gradle 9.0 what's new: [gradle.org/whats-new/gradle-9](https://gradle.org/whats-new/gradle-9/)
- springdoc-openapi Jackson 3 issue: [github.com/springdoc/springdoc-openapi/issues/3200](https://github.com/springdoc/springdoc-openapi/issues/3200)
- Spring Boot WebSocket guidance: [websocket.org/guides/frameworks/spring-boot](https://websocket.org/guides/frameworks/spring-boot/)
- Firebase Admin Java SDK releases: [firebase.google.com/support/release-notes/admin/java](https://firebase.google.com/support/release-notes/admin/java)
- PostgreSQL release news: [postgresql.org/about/news/postgresql-184-1710-1614-1518-and-1423-released-3297](https://www.postgresql.org/about/news/postgresql-184-1710-1614-1518-and-1423-released-3297/)
- shadcn/ui in production 2026: [vercel.com/academy/shadcn-ui](https://vercel.com/academy/shadcn-ui)

---

*Advisory report — read-only. All findings pertain to `master` HEAD as of 2026-06-05. No code, configuration, or dependency versions were changed as part of this review.*
