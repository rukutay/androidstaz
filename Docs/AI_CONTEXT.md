# Context
Updated: 2026-11-01

## Goal and users
POC/demo of the Career Explore Quarterlife Planning App (Gentofte, DK) built by an intern Kotlin developer. Users of the final product: quarterlifers 18–35, career changers, students, coaches/HR. The POC demonstrates the four-step journey and the generated plan, not the full product. Deliverable: an Android app runnable from Android Studio against a local server.

## Scope / out of scope
Full list in `Docs/ARCHITECTURE.md` (§1). In: 10 screens (onboarding, profile, dashboard, 4 steps incl. 4a/4b/4c, plan), mock Insights/AI-assist/PDF/Coach CTA. Out: OAuth, JWT, Redis, S3, email, push, analytics, GDPR flows, iOS, web/PWA, CI/CD, hosting.

## Architecture
- `client/` Gradle, AGP 9 layout:
  - `composeApp` — Kotlin Multiplatform library (`com.android.kotlin.multiplatform.library`, target Android only). `domain/` (DTOs, Content, Rules), `data/` (CareerApi + KtorCareerApi, JourneyRepository offline queue, Session, KeyValueStore), `AppContainer` (composition root), `theme/`, `nav/Navigator`, `ui/` (AppState, AutoSave, Shell, components, screens), `App.kt` (root, screen switch, transitions).
  - `androidApp` — `com.android.application`, `MainActivity` → `App()`; implements `PlatformActions` (share sheet, print-to-PDF). Cleartext HTTP allowed (POC) for `http://10.0.2.2:3000/api/v1`.
- `server/` Node 26 + TypeScript + Fastify 5 + Zod 4 + Prisma 7 (`prisma.config.ts`, `@prisma/adapter-pg`, client generated to `src/generated/prisma`) + PostgreSQL 16 in Docker.
- Contract: `openapi.yaml` (0.2.0). Step shapes: `server/src/schemas/steps.ts` ↔ `client/.../domain/Models.kt`.
- Source documentation: `C:\_SS` (Development Guidelines, Concept Note, Project Overview).

## Environment
- Dev machine: Node 26.13.0, Temurin JDK 21.0.13 (for Gradle; default `java` is 26), Docker 29.7.2, Android Studio 2026.1 (AI-261), SDK at `%LOCALAPPDATA%\Android\Sdk` (platforms 36.1/37.0, emulator, AVD `Pixel_6` API 37.1). `client/local.properties` (git-ignored) has `sdk.dir`.
- Built versions: Kotlin 2.4.21, AGP 9.3.1, Gradle 9.5.0, Compose MP 1.12.2, material3 1.9.0, Ktor 3.6.1, serialization 1.12.0, coroutines 1.11.0, activity-compose 1.13.0; compileSdk 37, targetSdk 36, minSdk 26.

## Decisions
- 2026-11-01 — Client in Kotlin (Compose Multiplatform), server per docs (Node/TS/Fastify/Prisma/PostgreSQL) — intern's Kotlin role; docs-conformant backend.
- 2026-11-01 — Target switched from web (wasmJs) to Android in Android Studio (user). wasmJs target removed; Fastify still serves `server/public` if present (unused).
- 2026-11-01 — UI built only after user-approved Google Stitch designs (`Docs/ui/STITCH_PROMPT.md`).
- 2026-11-01 — Server tests use Vitest + Fastify `inject` instead of Jest + Supertest (Guidelines 11.1): ts-jest is not expected to work with TypeScript 7 (native compiler); Vitest has a Jest-compatible API.
- 2026-11-01 — Validation duplicated (Kotlin `Rules` with 8.3 messages + Zod hard limits).
- 2026-11-01 — Auth stub `X-User-Id`; `/auth/register` returns the existing user for a known email (200) instead of 409, so the demo can restart without a login flow.
- 2026-11-01 — Conflict rule (6.6) applied directionally: server wins only when it is newer than the queued local edit by >5 min; offline edits are not discarded.
- 2026-11-01 — Steps stored as JSON columns on `Journey`; plan versions in `Plan` (v1, v2…); PDF export = HTML + print CSS, printed on Android via WebView + PrintManager ("Save as PDF").
- 2026-11-01 — UI built from Google Stitch designs (`Docs/ui/stitch/`); deviations (no stock photos, no invented coach credentials/testimonials, data-backed insights only) in `Docs/ui/UI_NOTES.md`.
- 2026-11-01 — Drafts may be incomplete: server accepts empty My Best text and empty objective action/outcome (auto-save); the plan generator skips them.
- 2026-11-01 — No navigation/ViewModel libraries: own `Navigator` (back stack) + `AppState` (Compose state) + `AutoSaver` (30 s + on leave). Portrait only.
- 2026-11-01 — Repository on GitHub `rukutay/androidstaz` (user's choice instead of Azure DevOps); commits as `rukutay <rukutay@users.noreply.github.com>` (repo-local config). Prisma client in `server/src/generated` is not committed (`npm run db:generate`).
- 2026-11-01 — Fonts Inter + Playfair Display bundled as Compose resources (OFL, licences in `client/composeApp/licenses/`); icons from material-icons-extended 1.7.3 (frozen upstream).

## Patterns and pitfalls
- material3 has its own version (1.9.0), not the Compose MP version.
- AGP 9 + KMP: shared code must be a `com.android.kotlin.multiplatform.library` module; the app is a separate `com.android.application` module (no kotlin-android plugin). Host tests: `withHostTest {}` → task `:composeApp:testAndroidHostTest`.
- Gradle must run on JDK 21 (`JAVA_HOME`); Android Studio uses its bundled JBR.
- Prisma 7: `.env` is not auto-loaded — `prisma.config.ts`, `prisma/seed.ts` and `test/setup.ts` load `../.env` via dotenv; npm scripts use `--env-file=../.env`.
- Fastify static plugin throws if the root dir is missing, so `server/public` is registered only when it exists.
- Client tests: any non-`ApiException` from `CareerApi` means "offline" to `JourneyRepository`.
- Client fixtures (`commonTest/Fixtures.kt`) are real server responses — re-capture them when the API changes.
- Save-then-navigate uses `AppState.launchNow` (UNDISPATCHED) so the local cache and `app.journey` update before the next screen reads them; never await the network before navigating.
- Playfair needs `fontFeatureSettings = "lnum"`, otherwise "1" renders as old-style "ı".
- targetSdk 36 forces edge-to-edge: the custom top bar pads for status bars; screens without a top bar rely on Scaffold insets.
- Snackbars on step screens are lifted 72dp so they never cover the sticky Back/Continue bar.
- Emulator screenshots via `adb exec-out screencap` were black with host GPU; start the AVD with `-gpu swiftshader_indirect` for automated checks.
- API base URL is a BuildConfig field: `-PapiBaseUrl=http://<pc-ip>:3000/api/v1` for a physical device (server must then bind `HOST=0.0.0.0`).
