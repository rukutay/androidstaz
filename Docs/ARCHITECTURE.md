# Career Explore — POC/demo architecture and minimal local deploy plan (Kotlin client + Node backend)

> **Update 2026-11-01 — target changed to Android.** The client is now an Android app run from Android Studio, not a web (wasmJs) app. Layout: `client/composeApp` (Kotlin Multiplatform library, AGP 9 `com.android.kotlin.multiplatform.library`) + `client/androidApp` (`com.android.application`). The app reaches the local API at `http://10.0.2.2:3000/api/v1` (emulator alias of the PC's localhost); offline cache uses SharedPreferences instead of localStorage. "Mode B" (single-process web demo) no longer applies. Server tests use Vitest + Fastify `inject` instead of Jest + Supertest. UI is built from Google Stitch designs (`Docs/ui/STITCH_PROMPT.md`). Everything else below still holds; current state is in `Docs/AI_STATE.md`.

## Context

Career Explore (Gentofte, DK) wants a "Quarterlife Planning" app: a four-step self-discovery journey (Interests → My Best → Values → Get Excited/Objectives) that produces a personalised 3-month career plan. The documentation in `C:\_SS` (Development Guidelines, Concept Note, Project Overview) describes the full product (18 screens, React Native + Node, 15 weeks, App Store/Play). For an intern Kotlin developer the scope is a **POC/demo**: 8–10 screens that carry the experience, one responsive web version, **everything local, no real deployment**. Project Overview: "a working prototype, not just clickable", start from scratch, use the Guidelines as the blueprint for structure, tone and visual style.

**Technology split (agreed):**
- **Client — Kotlin**: Compose Multiplatform, web target (`wasmJs`). Replaces React Native/React from the docs (the only deviation, driven by the intern's Kotlin role).
- **Server — per the documentation (section 6.3)**: Node.js + TypeScript, Fastify, REST `/api/v1/`, Prisma + PostgreSQL.

The API contract (`/api/v1/...` from section 6.5) is the only link between client and server, so the Kotlin client can later be replaced with React Native without touching the backend.

## 1. POC scope

**In scope (10 screens)** — numbering from the Guidelines:

| # | Screen | POC notes |
|---|---|---|
| 1 | S-01..03 Onboarding | one screen with a 3-card pager + "Skip" |
| 2 | S-04 Profile | name + email only (no password/OAuth) |
| 3 | S-05 Dashboard | progress ring (4 arcs), Resume card, step chips, daily prompt |
| 4 | S-06 Step 1 Interests | tags, 20+ counter, suggestion drawer (Appendix 13.1), top 2, validation (min 5, duplicates, 60 chars) |
| 5 | S-07 Step 2 My Best | 1–6 blocks, 3 "because" reasons, min 1 required |
| 6 | S-08 Step 3 Values | 20-value grid, exactly 3, reflection |
| 7 | S-09 Step 4a Get Excited | 10 rows, min 3 |
| 8 | S-10 Step 4b Wants + Objectives | 5 "wants" with 3-state importance selector, 3 objectives (timeframe / I will / so that) |
| 9 | S-11 Step 4c Challenges & Resources | up to 5 each per objective, "Generate My Plan" |
| 10 | S-12 My Plan | aggregate, versioning v1/v2, "PDF", Coach CTA |

**Demo elements (mocks/simplifications):**
- **Insights (S-13)** — keyword clustering (word frequency across interests + excited items) computed on the server in TS, `GET /insights/:journeyId`; no AI; with the disclaimer from the Guidelines.
- **AI-assist "Help me reflect" (S-07)** — a mock endpoint returning 3 canned follow-up questions (no Claude API; a real call is an option beyond the POC, key only in server env).
- **Coach Connect (S-14)** — static screen + `mailto:anita@careerexplore.dk` and website link.
- **PDF export** — `POST /plan/export` returns branded HTML with `@media print`; "Save as PDF" from the browser (zero dependencies). Post-POC: Puppeteer/PDFKit.
- **Daily prompt** — ~10 prompts instead of 60+ (seeded in DB, `GET /prompts/daily`).
- **Auto-save/offline** — simplified, client-side (see §2).

**Out of scope:** Google/Apple OAuth, JWT + refresh, Redis, S3/R2, email (Resend/Postmark), push, analytics, Sentry, GDPR export/delete, rate limiting, TLS, i18n beyond EN resources, iOS/Android/PWA, App Store/Play, UAT/a11y audit/cross-device testing, CI/CD, hosting.

## 2. Architecture

Two projects in one repo (or two repos in Azure DevOps `dev.azure.com/aterima`):

```
career-explore-poc/
├─ client/                 Gradle (Kotlin DSL, version catalog)
│  └─ composeApp/          Compose Multiplatform, wasmJs target
│     ├─ theme/             colours 5.1, typography 5.2 (Playfair Display + Inter in resources), 8 px grid
│     ├─ ui/components/     Chip, TagCloud, ProgressRing (Canvas), SegmentedControl, ValueCard, StepHeader
│     ├─ ui/screens/        Onboarding, Profile, Dashboard, Step1..Step4c, Plan, Insights, Coach
│     ├─ nav/               simple NavHost (state + back stack; 280 ms transitions from 5.6)
│     ├─ domain/            @Serializable models (DTOs matching the API), step validation, microcopy 8.2/8.3,
│     │                      suggestions, the 20 values
│     ├─ data/              ApiClient (Ktor client), LocalStore (localStorage), SyncQueue
│     └─ commonTest/        kotlin.test: validation, conflict rule, SyncQueue
├─ server/                 Node.js LTS + TypeScript
│  ├─ src/routes/           auth-stub, user, journey, steps, plan, insights, prompts, health
│  ├─ src/services/         planGenerator, keywordClustering, printTemplate (HTML)
│  ├─ src/schemas/          Zod (input validation, consistent with client rules)
│  ├─ prisma/               schema.prisma, migrations, seed (daily prompts)
│  ├─ public/               built wasmJs bundle (copied by script — demo mode)
│  └─ test/                 Jest + Supertest (2–3 endpoints)
├─ docker-compose.yml      PostgreSQL only (local)
├─ openapi.yaml            /api/v1 contract (single source of truth for both sides)
└─ README.md, .env.example, CLAUDE.md, Docs/ (memory bank)
```

### Data flow
```
 Browser (Compose/Wasm)  ──HTTP JSON──►  Fastify :3000  ──Prisma──►  PostgreSQL :5432 (Docker)
   ViewModel ─ LocalStore (localStorage)      │
   SyncQueue ─────────────────────────────────┘     GET /  → static UI bundle (demo mode)
                                                    POST /api/v1/journey/:id/plan/export → HTML for PDF
```

### Data (simplified 6.4)
Prisma: `User`, `Journey` (id, userId, version, status, timestamps), steps as `Json` columns on `Journey` or a separate `JourneyStep(journeyId, stepKey, data Json, updatedAt)` table, `Plan(journeyId, version, data Json, generatedAt)`, `DailyPrompt`. A full relational model (Objective as a table) is a post-POC decision.

### API (subset of 6.5)
| Method | Path | Change vs. docs |
|---|---|---|
| POST | `/auth/register` (simplified) | name + email, no password; returns `userId`, client sends an `X-User-Id` header. `authenticate` hook is a seam for later JWT — **demo only** |
| GET | `/user/me` | as in docs |
| GET / POST | `/journey/current`, `/journey` | as in docs |
| PUT | `/journey/:id/{interests\|best\|values\|excited}` | as in docs |
| POST | `/journey/:id/plan/generate` | as in docs; new version v1, v2… |
| GET | `/journey/:id/plan` | as in docs |
| POST | `/journey/:id/plan/export` | HTML for print/PDF (instead of a file in S3) |
| GET | `/insights/:journeyId` | keyword clustering, no AI |
| POST | `/journey/:id/best/reflect` | AI-assist mock (new demo endpoint) |
| GET | `/prompts/daily`, `/health` | as in docs / extra health check |

Omitted: `/auth/login`, `/auth/oauth`, `/auth/refresh`, `PATCH /user/me`.

### Offline / auto-save (client, Kotlin)
- Change in a step → write to `localStorage` (debounced) → `PUT`; network error → entry in `SyncQueue` + toast "You're offline…" (8.2).
- Auto-save every 30 s while editing; flush the queue when the connection returns.
- Conflict: server wins when `updatedAt` diverges by >5 min (6.6) — one function + one test.
- Offline demo: DevTools → Network → Offline.

### Validation in two places
Step rules (limits, minimums, duplicates) live in Kotlin (UX, 8.3 messages) and in Zod on the server (API protection). The duplication is deliberate — no shared code between client and server with this split. It is limited: the server validates structure and hard limits only; messages stay on the client.

## 3. Technologies (with rationale)

**Client**
| Area | Choice | Rationale / docs equivalent |
|---|---|---|
| Language | **Kotlin** | internship requirement |
| UI | **Compose Multiplatform**, `wasmJs` | replaces RN/React; one responsive web version (Overview) |
| Serialization | **kotlinx.serialization** | DTOs matching `openapi.yaml` |
| HTTP | **Ktor client** (JS/Wasm engine) | replaces fetch/axios |
| State | `ViewModel` + `StateFlow` (coroutines) | replaces Zustand/Redux |
| Navigation | custom NavHost (state + stack) | replaces React Navigation; no extra dependency |
| Styling | own tokens from §5, Playfair Display + Inter | per 5.1–5.3 |
| Icons | Phosphor/Tabler → ImageVector (a few) | per 5.5 |
| Tests | `kotlin.test` | replaces client-side Jest |
| Build | Gradle wrapper, JDK 17+ | |

**Server (per docs)**
| Area | Choice | Rationale / docs equivalent |
|---|---|---|
| Runtime | **Node.js LTS + TypeScript** | 6.3 |
| Framework | **Fastify** (docs: "Express or Fastify") | fast start, built-in hooks, static/cors plugins |
| API | REST JSON, `/api/v1/` | 6.3, 6.5 |
| Validation | **Zod** | named in 6.2 (RHF + Zod); here on the API side |
| ORM/DB | **Prisma + PostgreSQL** | 6.3 |
| DB environment | **Docker Compose (DB only)** | no local Postgres install; *fallback:* Prisma on SQLite if Docker is unavailable |
| Tests | **Jest + Supertest** | 11.1 |
| Out of POC | Redis, S3/R2, Resend, Claude API, JWT/OAuth | 6.3/6.7 — deliberately skipped |

**Versions verified online on 2026-11-01 (sources: kotlinlang.org, nodejs.org, GitHub releases):** Kotlin 2.4.21 (Gradle 7.6.3–9.7.0 supported); Compose Multiplatform 1.12.2 (plugin `org.jetbrains.compose`); Node LTS lines v22 / v24 / v26. **Not verified:** Wasm target stability label, Ktor/Fastify/Prisma exact versions (Prisma 7 uses `prisma.config.ts` and requires a driver adapter — check docs at M1), JDK range for Kotlin 2.4. Pin versions in `libs.versions.toml` and `package-lock.json`.

## 4. Minimal "deploy" — local only

**Workstation requirements:** JDK 17+ (toolchain), Node.js LTS, Docker Desktop (PostgreSQL only), IntelliJ IDEA, current Chrome/Edge/Firefox.

**Mode A — dev (daily work):**
```powershell
docker compose up -d db                                           # PostgreSQL :5432
cd server;  npm install; npm run dev                              # API :3000
cd client;  .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun # UI with hot reload, /api proxied to :3000
```

**Mode B — single-process demo (for mentor/client):**
```powershell
.\scripts\build-demo.ps1    # gradlew wasmJsBrowserDistribution → copy to server\public → npm run build
cd server; npm run start    # http://localhost:3000 (API + UI, one port)
```
Demo data reset: `scripts\reset-demo.ps1` (local only).

**Configuration:** `PORT`, `DATABASE_URL`, `DEMO=true` via environment variables; `.env.example` in the repo, `.env` git-ignored. No secrets in the POC.

**Demo artifact:** `dist/career-explore-poc-<version>/` with `server` (build + `public`) and `run-demo.ps1` + a short step-by-step guide. Every released artifact carries a new version in its name.

## 5. Build order (milestones for the intern)

Rough estimates, depend on the intern's experience — to be adjusted with the mentor.

| M | Goal | Result / done when |
|---|---|---|
| M0 | Skeleton | both projects start; "Hello" in Compose Web; `/health`; Postgres in Docker; `openapi.yaml` (draft); `README.md`, `.gitignore`, `.env.example`, `CLAUDE.md`, memory bank |
| M1 | Backend core | Prisma schema + seed, `auth/register`, `user/me`, `journey/*`, step PUTs, `health`; Jest+Supertest for 2–3 endpoints |
| M2 | Design system + client navigation | §5 tokens, components, Onboarding + Profile + empty Dashboard (wired to the API) |
| M3 | Steps 1–3 end-to-end | UI + Kotlin validation + PUT/GET + auto-save/localStorage |
| M4 | Step 4a/b/c + plan | wants/objectives, `plan/generate` (TS), Plan screen, versioning v1/v2 |
| M5 | Dashboard and demo elements | ring, Resume, daily prompt, Insights, Coach CTA, `plan/export` (HTML→PDF), AI-assist mock, SyncQueue |
| M6 | Demo wrap-up | mode B (`build-demo.ps1`), reset script, 5–7 min demo script, run instructions |

**Demo scenario:** Onboarding → profile → 20 interests → 1 "best" → 3 values → 3 wants/objectives → Generate Plan → edit a step → plan v2 → Print/PDF → go offline in DevTools and back online.

## 6. Risks and decisions to confirm

1. **Compose/Wasm maturity** — younger than React; possible text-field/IME issues, bundle size and startup time. *Fallback:* the same UI code as Desktop (JVM) — a target change; backend unchanged.
2. **Accessibility (WCAG 2.1 AA)** — canvas-rendered UI has limited screen-reader support; out of scope for the POC, record in README as a risk before a product.
3. **Two languages = duplicated validation rules** (Kotlin + Zod) — deliberately limited (§2); `openapi.yaml` guards the contract. Kotlin DTOs by hand; an OpenAPI generator only if the mentor wants it.
4. **Docker for Postgres** — if unavailable on the intern's machine, use Prisma + SQLite (one line in `schema.prisma`); this deviates from 6.3 and should be reported to the mentor.
5. **Authentication** — the `X-User-Id` header is not a security control; localhost only, never expose beyond the machine.
6. **Personal data** — email and user reflections: fictional test data only; database volume outside the repo.

## 7. Verification after implementation

- `cd server; npm test` — Jest + Supertest (level: API on local DB).
- `cd client; .\gradlew.bat :composeApp:wasmJsTest` (or `allTests`, depending on configured targets) — validation, SyncQueue, conflict rule (level: unit).
- Manual run of the demo scenario in mode B on a clean DB (level: UI; mentor confirmation).
- Final report: files, artifact version, commands and results, what was not checked (a11y, browsers beyond one, offline beyond DevTools).
