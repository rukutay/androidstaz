# Career Explore — Quarterlife Planning App (POC)

Proof of concept of the Career Explore four-step career planning journey (Interests → My Best → Values → Get Excited & Objectives → personalised 3-month plan). An Android app runnable from Android Studio, talking to a local API. No production deployment.

- **Android client:** Kotlin, Compose Multiplatform — `client/` (`composeApp` shared module + `androidApp`)
- **Server:** Node.js + TypeScript, Fastify, Prisma, PostgreSQL (Docker) — `server/`
- **Contract:** `openapi.yaml`
- **Student guide (start here):** [English](Docs/STUDENT_GUIDE.md) · [Русский](Docs/STUDENT_GUIDE.ru.md)
- **Architecture and plan:** [Docs/ARCHITECTURE.md](Docs/ARCHITECTURE.md) · **UI prompts:** [Docs/ui/STITCH_PROMPT.md](Docs/ui/STITCH_PROMPT.md)

## Prerequisites

- Android Studio (2026.1 used) with Android SDK and an emulator (e.g. Pixel 6, API 37)
- JDK 21 for command-line Gradle (Android Studio uses its own), Node.js LTS (26 used), Docker Desktop

## Configuration

Copy `.env.example` to `.env` at the repo root and adjust local values. `.env` is git-ignored; the POC has no real secrets.

## Run

**1. Database and API**
```powershell
docker compose up -d db
cd server
npm install
npm run db:generate     # generates the Prisma client (after install and after schema changes)
npm run db:migrate      # first time: creates tables
npm run db:seed         # first time: daily prompts
npm run dev             # http://127.0.0.1:3000/api/v1/health
```

**2. Android app**
- Open the `client/` folder in Android Studio, let Gradle sync, select the `androidApp` run configuration and an emulator, press Run.
- The app calls `http://10.0.2.2:3000/api/v1` (the emulator's alias for your PC's localhost), so keep `npm run dev` running.
- Command line alternative: `cd client; .\gradlew.bat :androidApp:installDebug` (with `JAVA_HOME` on JDK 21 and an emulator running).
- Physical phone on the same Wi-Fi: set `HOST=0.0.0.0` in `.env`, then build with `.\gradlew.bat :androidApp:installDebug -PapiBaseUrl=http://<your-pc-ip>:3000/api/v1`.

**Demo walkthrough (~5 min):** introduction → create account (any fictional name/email) → Step 1: add 20 interests (type or use "Need ideas?"), pick top 2 → Step 2: one moment with a reason → Step 3: three values + reflection → 4a: three things that excite you → 4b: one objective → 4c: a challenge and a resource → Generate My Plan → Export as PDF / Share / Talk to a coach → Insights. Offline: stop the server, edit a step (Profile shows "Offline", unsynced changes), start the server, Profile → Sync now.

## Tests

```powershell
cd server; npm test                                   # API tests against the local Docker DB
cd client; .\gradlew.bat :composeApp:testAndroidHostTest   # client unit tests (JVM, no emulator)
```

## Status

POC complete and verified on the emulator (full journey, plan, PDF export, insights, offline sync). UI follows the Google Stitch designs; differences and DRAFT content are listed in [Docs/ui/UI_NOTES.md](Docs/ui/UI_NOTES.md). See `Docs/AI_STATE.md` for what was and was not tested.

## Known risks

- Auth is a stub (`X-User-Id` header); the server binds to 127.0.0.1. Never expose it.
- Cleartext HTTP is allowed in the Android app for the local demo only.
- Accessibility (WCAG 2.1 AA) is not audited in the POC.

## Troubleshooting

- Gradle fails on a very new JDK (e.g. 26): point `JAVA_HOME` to JDK 21.
- Emulator shows a black screen in automated screenshots: start it with `-gpu swiftshader_indirect`.
- App cannot reach the API: is `npm run dev` running? On a physical device use the PC's LAN IP and bind the server to `0.0.0.0` (`HOST` in `.env`).
