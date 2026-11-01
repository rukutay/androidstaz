# Career Explore POC

Local proof of concept of the Career Explore Quarterlife Planning App for an intern Kotlin developer project: Android app in Kotlin (Compose Multiplatform, AGP 9 layout) + Node.js/TypeScript server (Fastify, Prisma 7, PostgreSQL in Docker). Source documentation: `C:\_SS` (three .docx files).

@Docs/AI_CONTEXT.md
@Docs/AI_STATE.md

## Commands
- DB: `docker compose up -d db`
- server: `cd server; npm run dev` · build `npm run build` · tests `npm test` · migrate `npm run db:migrate` · seed `npm run db:seed`
- client (JAVA_HOME = JDK 21): `cd client; .\gradlew.bat :composeApp:testAndroidHostTest` · APK `.\gradlew.bat :androidApp:assembleDebug`
- run the app: Android Studio → open `client/` → `androidApp` on the emulator

## Project rules
- Scope is POC/demo only, everything local; no production deploy (see `Docs/ARCHITECTURE.md`).
- English for code, docs and UI copy; UI copy follows Guidelines section 8 (`domain/Content.kt`).
- Kotlin only on the client; server follows the documentation (Node/TS/Fastify/Prisma/PostgreSQL).
- Ask the user before building UI; base UI comes from Google Stitch (`Docs/ui/STITCH_PROMPT.md`, outputs in `Docs/ui/stitch/`).
- API contract: change `openapi.yaml`, `server/src/schemas`, client `domain/Models.kt` and `commonTest/Fixtures.kt` together.
- Follow Development Guidelines section 5 (brand colours, typography, 8 px grid).
- Auth is a demo stub (`X-User-Id`); never expose the server beyond localhost.
- Only fictional test data; no secrets in the repo (`.env` and `client/local.properties` are git-ignored).
- Pin versions (`client/gradle/libs.versions.toml`, `server/package-lock.json`); verify versions/APIs before using them.
