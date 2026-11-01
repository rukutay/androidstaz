# Career Explore POC — Student Guide

*How the project works, how it is built, and how to continue it. Focus: Kotlin and Android.*
*Russian version: [STUDENT_GUIDE.ru.md](STUDENT_GUIDE.ru.md)*

---

## 1. What we are building

**Career Explore** is a career-planning app for people aged 18–35. The user goes through **four steps**:

1. **Interests** – list 20+ things that interest you, pick your top 2.
2. **My Best** – describe moments when you were at your best, and why.
3. **Values** – choose exactly 3 values you cannot work without.
4. **Get Excited** – what excites you, your job "wants", up to 3 objectives, and the challenges and resources for each.

At the end the app builds a **personal 3-month plan**. The user can export it as PDF, share it, or contact a coach.

This project is a **POC (proof of concept)**: a working demo that runs on your computer and in the Android emulator. It is not a finished product in an app store.

The product description comes from Career Explore's documents (Development Guidelines, Concept Note, Project Overview). The screen designs come from **Google Stitch** (`Docs/ui/stitch/`).

---

## 2. The big picture

The project has three parts:

```
 ┌──────────────────────┐        internet (HTTP + JSON)       ┌──────────────────┐      ┌────────────┐
 │  Android app         │  ───────────────────────────────▶   │  Server (API)    │ ───▶ │ PostgreSQL │
 │  Kotlin + Compose    │  ◀───────────────────────────────   │  Node.js         │ ◀─── │ database   │
 │  (this is your part) │                                     │  (ready for you) │      │ (Docker)   │
 └──────────────────────┘                                     └──────────────────┘      └────────────┘
```

- The **app** shows the screens, checks what the user types, and saves answers on the phone.
- The **server** stores answers for each user, builds the plan, and counts keywords for Insights.
- The **database** keeps everything on disk.

Why different languages? The internship is for a **Kotlin developer**, so the app is in Kotlin. The company documentation asks for a Node.js server, so the server follows it. The app and the server only "talk" through the **API** (addresses like `/api/v1/journey/current`), which is described in `openapi.yaml`. So you can work on the app without knowing Node.js.

---

## 3. Before you start

Install:
- **Android Studio** (it includes the Android SDK and the emulator)
- **JDK 21** (only needed if you build from the command line)
- **Node.js LTS** and **Docker Desktop** (to run the server and the database)

Run the project (also in `README.md`):

```powershell
# 1. Database + server (one terminal)
docker compose up -d db
cd server
npm install
npm run db:generate    # prepares the database client code
npm run db:migrate     # first time only
npm run db:seed        # first time only
npm run dev            # keep it running

# 2. App: open the folder "client" in Android Studio → choose "androidApp" → press Run
```

> **Important:** inside the emulator, `10.0.2.2` means "my computer". That is why the app talks to `http://10.0.2.2:3000/api/v1`.

---

## 4. Project map (the parts you need)

```
client/
├─ androidApp/                    ← the Android "shell"
│   └─ MainActivity.kt            ← starts the app, plus share and print (PDF)
└─ composeApp/                    ← all app logic and all screens
    └─ src/commonMain/kotlin/dk/careerexplore/poc/
        ├─ domain/                ← WHAT the app knows
        │   ├─ Models.kt          ←   data classes (Journey, Plan, Objective…)
        │   ├─ Rules (Validation.kt) ← rules: "min 5 interests", "exactly 3 values"…
        │   └─ Content.kt         ←   texts, the 20 values, suggestion lists
        ├─ data/                  ← WHERE data comes from
        │   ├─ CareerApi.kt / KtorCareerApi.kt  ← talks to the server
        │   ├─ JourneyRepository.kt             ← saves on the phone + sends to the server (offline)
        │   └─ KeyValueStore.kt                 ← small local storage, the user "session"
        ├─ theme/                 ← colours and fonts (from Stitch)
        ├─ nav/Navigator.kt       ← which screen is open, the Back button
        ├─ ui/                    ← HOW it looks
        │   ├─ AppState.kt        ←   shared app state (user, journey, online/offline)
        │   ├─ AutoSave.kt        ←   auto-save every 30 seconds and when leaving a screen
        │   ├─ Shell.kt           ←   top bar, bottom navigation, step-screen layout
        │   ├─ components/        ←   buttons, text fields, chips, cards, progress ring
        │   └─ screens/           ←   the screens themselves
        ├─ AppContainer.kt        ← "assembles" the app (API + storage + repository)
        └─ App.kt                 ← the root: picks which screen to show
```

Keep three layers in mind: **domain → data → ui**. The UI uses data and domain. Domain knows nothing about the UI. This makes the code easier to test and change.

---

## 5. Dependencies (libraries) in plain words

| Library | What it does for us |
|---|---|
| **Kotlin** | The programming language |
| **Jetpack Compose / Compose Multiplatform** | Draws the screens from Kotlin functions (no XML layouts) |
| **Material 3** | Ready-made building blocks: buttons, text fields, bottom bar, dialogs |
| **Material Icons Extended** | Icons (home, lock, check…) |
| **Compose Resources** | Packs our fonts (Inter, Playfair Display) into the app |
| **Kotlin Coroutines** | Runs slow work (network) without freezing the screen |
| **Ktor Client** + OkHttp | Sends HTTP requests to the server |
| **kotlinx.serialization** | Turns Kotlin objects into JSON and back |
| **AndroidX Activity Compose** | Connects Compose to an Android Activity, handles the system Back button |
| **kotlin.test** + Ktor Mock | Unit tests without a phone and without a real server |

Versions are in `client/gradle/libs.versions.toml`. Change versions only there.

**Gradle modules:** `composeApp` is a *Kotlin Multiplatform library*. Today it targets only Android, but most of its code could later run on iOS or the web. `androidApp` is the thin Android app that uses it.

---

## 6. Key ideas you will meet

### 6.1 A screen is a function
In Compose, a screen is a Kotlin function marked `@Composable`. It describes what to show for the current data:

```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hi, $name")
}
```

### 6.2 State: when data changes, the screen redraws itself
```kotlin
var items by remember { mutableStateOf(listOf<String>()) }
// ...
items = items + "Photography"   // the screen updates automatically
```
`remember` keeps the value while the screen is visible. `mutableStateOf` tells Compose to watch it. Changing the value triggers a "recomposition", meaning the screen is redrawn.

### 6.3 Data classes + JSON
```kotlin
@Serializable
data class InterestsStep(val items: List<String> = emptyList(), val topTwo: List<String> = emptyList())
```
`@Serializable` lets us send this object to the server as JSON: `{"items":[...],"topTwo":[...]}`. The names must match the server. See `openapi.yaml`.

### 6.4 `suspend` and coroutines
Network calls take time. Functions that wait are marked `suspend` and are started inside a coroutine (`launch { ... }`), so the UI keeps working:
```kotlin
app.launch { val plan = app.container.api.generatePlan(journeyId) }
```

### 6.5 Rules live outside the screens
All step rules are in one object, `Rules` (`domain/Validation.kt`). The texts come from the Guidelines (section 8.3):
```kotlin
fun canProceedInterests(items: List<String>): Check =
    if (items.size >= 5) Check.Ok
    else Check.fail("Add a few more interests to continue - aim for at least 5 to start seeing patterns.")
```
Screens only ask "is it OK?" and show the message. Because `Rules` does not depend on the UI, it is easy to unit-test.

---

## 7. How data moves (example: the user adds an interest)

```
User taps "Add"
   → InterestsScreen: items = items + "Photography"     (screen updates)
   → after 30 s, or on "Save & Continue" / "Back":
       AppState.saveStep(...)
         → JourneyRepository.saveStep(...)
             1) save a copy on the phone (SharedPreferences)  ← always works
             2) send PUT /api/v1/journey/{id}/interests        ← needs internet
             3) no internet? → put it in a waiting queue → send later
```

**Offline mode:** if the server is unreachable, the user can keep working. Answers wait in the queue. When the connection returns (next app start, "Try again" or Profile → "Sync now"), the queue is sent.

**Conflict rule (Guidelines 6.6):** if the server copy is more than 5 minutes **newer** than the waiting local change, the server copy wins. This is in `data/SyncPolicy.kt`.

The **plan** is built on the server (`POST /plan/generate`). The app only shows it. Each new generation is a new version: v1, v2…

---

## 8. How the app was built, step by step

This is the order we followed. If you rebuild it or add features, use the same order: **logic first, screens last**.

### Step 1 — Project skeleton
- Created the Gradle project with two modules (`composeApp`, `androidApp`) and a simple "Hello" screen.
- Checked that it builds and runs in the emulator.
- *You learn:* Gradle, modules, `MainActivity`, `setContent { }`.

### Step 2 — Data models
- Wrote the `data class`es for everything the server sends and receives (`domain/Models.kt`).
- Checked them against real server answers (`commonTest/Fixtures.kt`).
- *You learn:* data classes, nullable types (`?`), `@Serializable`, enums.

### Step 3 — Rules + tests
- Put every rule from the Guidelines into `Rules` (min 5 interests, exactly 3 values, min 3 excitements, …).
- Wrote unit tests for them (`RulesTest.kt`).
- *You learn:* pure functions, unit tests, why logic should not live inside screens.

### Step 4 — Talking to the server
- `CareerApi` is an **interface** (a list of what we can ask the server).
- `KtorCareerApi` is the real implementation with Ktor.
- In tests we use a fake server (Ktor `MockEngine`), so tests do not need the internet.
- *You learn:* interfaces, HTTP GET/PUT/POST, JSON, `suspend` functions, error handling (`ApiException`).

### Step 5 — Offline saving
- `JourneyRepository` first saves locally, then sends. If sending fails, it keeps the change in a queue.
- Tested with a fake API that can go "offline" (`JourneyRepositoryTest.kt`).
- *You learn:* the repository pattern, local storage, thinking about bad network.

### Step 6 — Design system (from Stitch)
- Took the colours, fonts and shapes from the Stitch file `DESIGN.md` and put them into `theme/`.
- Built small reusable **components**: `PrimaryButton`, `CeTextField`, `RemovableChip`, `CeCard`, `ProgressRing`, `SegmentedControl`…
- *You learn:* themes, reusable composables, `Modifier`.

### Step 7 — App shell and navigation
- `Navigator` keeps a list of opened screens (a "back stack"). `push()` opens a screen, `back()` closes it.
- `App.kt` shows the top bar, the bottom navigation, and the current screen with a slide animation.
- `AppState` holds the user, the journey and the online/offline status for all screens.
- *You learn:* app structure, shared state, the system Back button.

### Step 8 — Screens, one at a time
Order: Onboarding → Profile → Home → Step 1 → Step 2 → Step 3 → 4a → 4b → 4c → My Plan → Insights → Coach.
Each step screen uses the same pattern (see the next section).

### Step 9 — Test on the emulator and polish
- Went through the full journey on the emulator, including offline mode, and fixed what was wrong (messages covering buttons, a counter not updating, font details).
- *You learn:* manual testing is as important as unit tests.

---

## 9. Anatomy of a step screen (simplified)

Every step screen looks like this (the real code is in `ui/screens/StepScreens.kt`):

```kotlin
@Composable
fun InterestsScreen(app: AppState, nav: Navigator) {
    // 1. Local state, started from saved data
    var items by remember { mutableStateOf(app.steps.interests?.items ?: emptyList()) }
    var input by remember { mutableStateOf("") }

    // 2. Auto-save: every 30 s and when the screen closes
    val saver = rememberAutoSaver(app, InterestsStep(items)) {
        app.saveStep(StepKey.INTERESTS, it, InterestsStep.serializer())
    }

    // 3. Common layout: header, progress, title, Back / Save & Continue
    StepScreen(
        app = app, stepLabel = "Step 1 of 4 • Interests", minutes = 30, progress = 0.25f,
        title = "Interests", instruction = Content.Copy.STEP1_INSTRUCTION,
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = {
            val check = Rules.canProceedInterests(items)           // 4. ask the rules
            if (!check.ok) app.message(check.message!!)             //    show the message
            else { app.launchNow { saver.saveNow() }; nav.push(Screen.MY_BEST) }
        },
    ) {
        // 5. The screen's own content
        CeTextField(value = input, onValueChange = { input = it }, label = "Add an interest")
        PrimaryButton("Add", onClick = { items = items + input.trim(); input = "" })
        items.forEach { item -> RemovableChip(item, onRemove = { items = items - item }) }
    }
}
```

To add a new step screen: copy the pattern, add the rules to `Rules`, add the screen to `Screen` in `Navigator.kt`, and add it to `ScreenContent` in `App.kt`.

---

## 10. The rules of the four steps

| Step | To continue you need | Other rules |
|---|---|---|
| 1 Interests | at least **5** | aim for 20+; max 60 characters each; no duplicates; top 2 appears at 20 |
| 2 My Best | **1** moment with at least 1 reason | up to 6 moments; 300 characters; gentle hint if fewer than 3 |
| 3 Values | exactly **3** of the 20 | a 4th cannot be picked; a reflection question appears at 3 |
| 4a Get Excited | at least **3** | up to 10 rows; hint "what do these have in common?" at 5 |
| 4b Objectives | at least **1** with "I will" + "so that" | up to 5 wants, up to 3 objectives |
| 4c Challenges | – | up to 5 challenges and 5 resources per objective |
| Plan | all 4 steps done | each generation = new version (v1, v2…) |

Unlocking: **My Plan** opens after Step 1, **Insights** after Step 2 (Guidelines 2.1).

---

## 11. Testing

**Unit tests** (fast, no phone needed):
```powershell
cd client
.\gradlew.bat :composeApp:testAndroidHostTest
```
- `RulesTest` — the step rules and the conflict rule
- `KtorCareerApiTest` — that we read the server's real JSON correctly
- `JourneyRepositoryTest` — offline saving and the queue

**Manual test on the emulator:** follow the demo walkthrough in `README.md`. To test offline: stop the server, edit a step, look at Profile ("Offline", unsynced changes = 1), start the server, tap "Sync now".

---

## 12. Common problems

| Problem | Solution |
|---|---|
| Gradle fails with a strange Java error | Use JDK 21 (`JAVA_HOME`); Android Studio already uses its own JDK |
| The app says "No connection" | Is `npm run dev` running? Is Docker running? |
| A physical phone cannot reach the server | Use your PC's IP: `-PapiBaseUrl=http://<pc-ip>:3000/api/v1` and `HOST=0.0.0.0` in `.env` |
| Emulator screenshots are black | Start the emulator with `-gpu swiftshader_indirect` |
| Changed a server JSON field | Update `Models.kt`, `Fixtures.kt` and `openapi.yaml` together |

---

## 13. Ideas for your next tasks

1. **Plan history screen** — the server already has `GET /journey/{id}/plans`. Show a list and let the user open v1 or v2.
2. **Danish language** — move texts from `Content.kt` into string resources (Guidelines 9.2).
3. **Accessibility** — add `contentDescription` everywhere, check large fonts and TalkBack (Guidelines 9.1).
4. **Daily prompt favourites** — show a list of favourite prompts.
5. **Tests for a screen** — try Compose UI tests for the Interests screen.

---

## 14. Glossary

| Word | Meaning |
|---|---|
| **API** | The "menu" of requests the server understands, e.g. `GET /journey/current` |
| **JSON** | Text format for data: `{"name":"Anna"}` |
| **Composable** | A Kotlin function that draws part of the screen |
| **State** | Data that, when changed, makes the screen redraw |
| **Recomposition** | Compose redrawing the screen after state changes |
| **Coroutine / suspend** | A way to wait (e.g. for the network) without freezing the app |
| **Repository** | A class that hides where data comes from (phone or server) |
| **Offline queue** | Changes waiting to be sent when the internet is back |
| **POC** | Proof of concept: a working demo, not a final product |
| **Emulator** | A virtual Android phone on your computer |
| **10.0.2.2** | Inside the emulator: the address of your own computer |
