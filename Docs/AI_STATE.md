# State
Updated: 2026-11-01

## Now
Android POC is **ready for testing in Android Studio**: all 10 core screens + Insights, Coach Connect and Profile are built from the Stitch designs and run against the local server. Next is user review on the emulator.

## Current artifact
`C:\career-explore-poc` — client androidApp versionName 0.3.0 (versionCode 2), server 0.1.0 (package.json). Git: https://github.com/rukutay/androidstaz (branch `main`, initial commit 98b2d6a, 2026-11-01). Local DB and emulator app data were cleared before the first commit (only the 12 seed prompts remain).

## Verified (2026-11-01)
- server: `npm run build` OK; `npm test` 7/7 passed (Vitest + inject, local Docker Postgres)
- client: `:composeApp:testAndroidHostTest` 14/14 passed; `:androidApp:installDebug` OK
- UI on emulator (Pixel_6, API 37.1, `-gpu swiftshader_indirect`), driven via adb + screenshots: onboarding → profile (register) → dashboard → Step 1 (20 interests, duplicate check, top-2 limit, min-5 message) → Step 2 (moment, reason, context tags) → Step 3 (3-value limit, reflection prompt) → 4a → 4b (want + objective) → 4c (challenge + resource) → Generate My Plan (v1) → Plan view → Coach → Insights → Export as PDF (print preview with branded plan) → app restart keeps session → offline edit (server stopped: "Offline", 1 unsynced) → server restarted, Sync now → edit present in DB, counter 0
- NOT verified: physical device, Save-as-PDF file actually written, Share sheet, mailto/website intents, "Help me reflect" dialog, plan v2 + history menu, "Start a new journey", sign out, accessibility (TalkBack, font scaling), landscape (locked to portrait)

## Open
- Brand assets (logo, app icon, photos, fonts licence check) from Career Explore; current icon and illustrations are placeholders.
- DRAFT content listed in `Docs/ui/UI_NOTES.md` needs Career Explore review.
- Emulator with host GPU produced black screenshots; `-gpu swiftshader_indirect` worked (Android Studio's own emulator window may be fine).
- npm reports unapproved install scripts (esbuild, prisma, @prisma/engines); everything worked without them.
- Repo is public on GitHub: never commit `.env`, `client/local.properties`, real user data or screenshots with personal data.

## Next step
User opens `client/` in Android Studio, runs `androidApp` on an emulator with the server running, and reviews the flow; collect UI feedback.
