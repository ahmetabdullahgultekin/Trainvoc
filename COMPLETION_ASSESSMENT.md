# Trainvoc Monorepo: Completion Assessment & Plan

> **Date**: 2026-03-26
> **Scope**: Full project assessment across all 3 components, branches, docs, TODOs, and infrastructure
> **Project Overall Completion**: **~67%**

---

## 1. Completion Assessment

### Overall Scores

| Module | Feature | Quality | Tests | Prod-Ready | **Weighted** |
|--------|---------|---------|-------|------------|-------------|
| **TrainvocClient (Android)** | 72% | 85% | 15% | 55% | **62%** |
| **TrainvocWeb (React)** | 87% | 90% | 20% | 70% | **73%** |
| **TrainvocBackend (Spring Boot)** | 82% | 88% | 35% | 65% | **72%** |
| **Infrastructure** | 80% | 85% | N/A | 50% | **68%** |
| **PROJECT OVERALL** | | | | | **~67%** |

### Issue Tracker (TODO.md): 220 total
- **Fixed**: 141 (64%), **WONTFIX**: 56 (25.5%), **OPEN**: 23 (10.5%)

### Branch Status: 31 branches
- `master` (protected, production)
- 8 `claude/*` feature branches (some stale)
- 22 `dependabot/*` PRs pending (security patches)

### Health Scores (from Investigation Report): 7.4/10 average
- Client: 7.0, Web: 7.2, Backend: 8.0

---

### What's Done (Strengths)

| Area | Status |
|------|--------|
| Core vocabulary learning (dictionary, quizzes, 10 games) | 100% |
| Offline mode (Room/SQLite) | 100% |
| Accessibility (WCAG 2.1 AA) | 100% |
| Dark mode / responsive design | 100% |
| Gamification (streaks, achievements, daily goals) | 95% |
| Web multiplayer (room creation, real-time games) | 90% |
| Backend REST API (20+ endpoints) | 95% |
| Backend WebSocket (game state machine, 7 states) | 90% |
| Architecture (MVVM, Clean, State/Strategy patterns) | 100% |
| Master Fix Plan (10 phases, SOLID, DRY, KISS, YAGNI) | 100% |
| Docker Compose (5 services, health checks, SSL) | 100% configured |
| CI/CD (GitHub Actions multi-component pipeline) | 90% |
| Documentation (50+ markdown files) | 95% |
| i18n (English + Turkish) | 100% |

### What's Missing (23 Open Issues + Gaps)

**CRITICAL (4 open)**:
- #168: Story Mode has NO content -- just a level selection shell
- #171: Multiplayer game UI deleted from client (may be restored, needs verification)
- #012: SSL configured but never deployed to production server
- Auth enforcement: `SecurityConfig.java:48` has `.anyRequest().permitAll()` -- JWT infra built but NOT enforced

**HIGH (12 open)**:
- #177-178: No chapter structure or contextual learning in Story mode
- #179: No adaptive difficulty
- #180: No sound effects
- #181: No hint system
- #184: No pinch-zoom for flip cards
- #189: Friend system not implemented
- #191: Email verification -- code exists, no UI
- #192: Google Sign-In -- GoogleAuthManager.kt exists, not wired to UI
- #193: No session timeout handling
- #194: Leaderboard is "Coming Soon" placeholder
- #219: Need 20+ testers for Play Store production

**Code-Level TODOs**: Only 2 files remain (CloudBackupManager.kt -- Google Drive API)

**Zero-Implementation Gaps**:
- Backend sync from client: 0%
- Cloud backup provider: 0% (Google Drive)
- Web authentication: 0%
- iOS app: 0% (future)

---

## 2. Remaining Work Categorized

### MUST-HAVE for v1.0 (Play Store Production)

| ID | Task | Component | Size | Why |
|----|------|-----------|------|-----|
| A1 | Enforce JWT auth on protected backend endpoints | Backend | M | SecurityConfig permits all -- wide open |
| A2 | Wire Android auth flow to backend AuthController | Client+Backend | M | LoginScreen exists but disconnected |
| A3 | Google Sign-In on LoginScreen (#192) | Client | M | Dominant Android auth method |
| A4 | Email verification UI screen (#191) | Client | S | Code exists, no prompt screen |
| A5 | Session timeout / token refresh (#193) | Client | M | Cryptic errors without it |
| A6 | Deploy to GCP VM with SSL (#012) | Infra | M | docker-compose + deploy.sh ready |
| A7 | Connect LeaderboardScreen to backend (#194) | Client+Backend | M | Can't ship "Coming Soon" |
| A8 | Merge 22 Dependabot security PRs | All | S | Vulnerability patches |
| A9 | Recruit 20+ testers, 14-day activity (#219, #220) | Process | M | Play Store gate |
| A10 | Minimum viable test coverage (30%/40%/50%) | All | L | Production safety net |

### SHOULD-HAVE for v1.1

| ID | Task | Component | Size |
|----|------|-----------|------|
| B1 | Story mode content + chapters (#168, #177, #178) | Client | XL |
| B2 | Adaptive difficulty (#179) | Client | L |
| B3 | Hint system (#181) | Client | M |
| B4 | Sound effects (#180) | Client | S |
| B5 | Speed bonuses (#199) | Client | S |
| B6 | Notification badges (#201) | Client | S |
| B7 | Pinch-zoom flip cards (#184) | Client | S |
| B8 | Web authentication | Web+Backend | L |
| B9 | Backend structured logging | Backend | M |
| B10 | Navigation audit (#209) | Client | S |

### NICE-TO-HAVE for v2.0

| ID | Task | Size |
|----|------|------|
| C1 | Friend system (#189) | L |
| C2 | Google Analytics (#205) | M |
| C3 | Cloud backup (Google Drive) | XL |
| C4 | Full data sync (progress, favorites) | XL |
| C5 | Story achievements (#204) | S |
| C6 | Web offline/PWA | L |
| C7 | iOS application | XL |
| C8 | Premium subscription | XL |
| C9 | Additional language pairs | XL |

---

## 3. Dependency Graph

```
A8 (Dependabot) ─────────────────────> standalone

A1 (JWT enforcement) ──┬──> A2 (Android auth) ──┬──> A3 (Google Sign-In)
                       │                         ├──> A4 (Email verification)
                       │                         ├──> A5 (Session timeout)
                       │                         └──> A10 (Tests)
                       ├──> A7 (Leaderboard)
                       ├──> A6 (Deploy) ──────> A9 (Recruit testers)
                       └──> B8 (Web auth)

B1 (Story content) ───> C5 (Story achievements)
A3 (Google Sign-In) ──> C3 (Cloud backup / Google Drive)
A5 (Session timeout) ──> C4 (Data sync)
```

**Critical Chain**: A1 -> A2 -> A5 -> A6 -> A9 (auth -> deploy -> testers)

---

## 4. Sprint Plan (2-Week Sprints)

### Sprint 1: "Auth Pipeline" (Weeks 1-2)
- A8: Merge Dependabot PRs (S)
- A1: Enforce JWT on backend endpoints (M)
- A2: Wire Android auth to backend (M)
- A4: Email verification UI (S)
- B10: Navigation audit (S)

**Goal**: End-to-end auth works. Register -> Login -> JWT -> Protected endpoints.

### Sprint 2: "Deploy and Connect" (Weeks 3-4)
- A3: Google Sign-In on LoginScreen (M)
- A5: Session timeout / token refresh (M)
- A7: Leaderboard wiring (M)
- A6: GCP deployment with SSL (M)
- B9: Backend logging (M)

**Goal**: App live at `trainvoc.rollingcatsoftware.com` with HTTPS. Google Sign-In works.

### Sprint 3: "Testing Gate + Engagement" (Weeks 5-6)
- A9: Recruit 20+ testers (starts, 14-day clock begins)
- A10: Critical path unit tests (L)
- B4: Sound effects (S)
- B3: Hint system (M)
- B5: Speed bonuses (S)

**Goal**: Testing period active. Engagement features shipped to testers.

### Sprint 4: "Story Mode + Polish" (Weeks 7-8)
- B1: Story content for A1-A2 levels (XL, start)
- B2: Adaptive difficulty (L)
- B6: Notification badges (S)
- B7: Pinch-zoom flip cards (S)
- Bug fixes from tester feedback

**Goal**: Testing period completes. Reapply for production. Story mode begins.

### Sprint 5: "v1.0 Launch + v1.1 Start" (Weeks 9-10)
- B1: Story content (continue)
- B8: Web authentication (L)
- C5: Story achievements (S)
- Production release (if approved)

**Minimum time to production: ~7 weeks** (5 dev + 2 weeks mandatory tester activity).

---

## 5. Critical Files to Modify

| File | Why |
|------|-----|
| `TrainvocBackend/.../config/SecurityConfig.java` | Line 48: `.anyRequest().permitAll()` -- THE single most impactful change |
| `TrainvocClient/.../cloud/GoogleAuthManager.kt` | Credential Manager API code to wire to LoginScreen |
| `TrainvocBackend/.../controller/AuthController.java` | Complete auth endpoints ready but not consumed by client |
| `TrainvocClient/.../ui/auth/LoginScreen.kt` | Needs Google Sign-In button and backend JWT exchange |
| `docker-compose.yml` | Ready to deploy -- just needs execution on GCP VM |
| `TODO.md` | Update issue statuses as work progresses |

---

## 6. Key Risks

1. **Play Store tester gate** -- 14 consecutive days with 20+ testers is a hard calendar blocker. Start recruiting in Sprint 1.
2. **SecurityConfig `.anyRequest().permitAll()`** -- Changing this will break Web (zero auth). Need `.permitAll()` carve-outs for game/quiz/words until Web auth is ready (Sprint 5).
3. **Story content is creative work** (XL) -- Requires narrative design, themed word groups, chapter progression. Not just engineering.
4. **CI runs tests with `continue-on-error: true`** -- Broken tests don't fail builds. Fix before production.

---

## 7. Verification Plan

After implementation, verify:
1. `./gradlew test` passes in TrainvocBackend with auth enforcement
2. `./gradlew assembleDebug` succeeds in TrainvocClient with auth flow
3. `npm run build` succeeds in TrainvocWeb
4. End-to-end: Register -> Login -> Play Game -> See Leaderboard on production URL
5. Google Sign-In flow works on real Android device
6. WebSocket multiplayer game works over WSS (production)
7. All 22 Dependabot PRs merged without conflicts

---

## Size Key

| Size | Duration |
|------|----------|
| **S** | 1-2 days |
| **M** | 3-5 days |
| **L** | 1-2 weeks |
| **XL** | 2-4 weeks |
