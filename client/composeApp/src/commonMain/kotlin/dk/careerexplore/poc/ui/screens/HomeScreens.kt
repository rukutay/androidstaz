package dk.careerexplore.poc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.DailyPrompt
import dk.careerexplore.poc.domain.JourneySteps
import dk.careerexplore.poc.domain.Rules
import dk.careerexplore.poc.nav.Navigator
import dk.careerexplore.poc.nav.Screen
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.theme.CeTheme
import dk.careerexplore.poc.ui.AppState
import dk.careerexplore.poc.ui.PageColumn
import dk.careerexplore.poc.ui.PageTitle
import dk.careerexplore.poc.ui.SectionHeader
import dk.careerexplore.poc.ui.components.Badge
import dk.careerexplore.poc.ui.components.CeCard
import dk.careerexplore.poc.ui.components.GhostButton
import dk.careerexplore.poc.ui.components.IconBadge
import dk.careerexplore.poc.ui.components.InfoNote
import dk.careerexplore.poc.ui.components.Overline
import dk.careerexplore.poc.ui.components.PrimaryButton
import dk.careerexplore.poc.ui.components.ProgressRing
import dk.careerexplore.poc.ui.components.ReflectionBlock
import dk.careerexplore.poc.ui.components.SecondaryButton

enum class StepStatus(val label: String) { NOT_STARTED("Not started"), IN_PROGRESS("In progress"), COMPLETE("Completed") }

/** The four steps as shown on the dashboard and the Journey tab. */
data class StepInfo(val number: Int, val name: String, val description: String, val minutes: Int, val screen: Screen)

val stepInfos = listOf(
    StepInfo(1, "Interests", "List the activities, jobs and tasks that genuinely interest you.", 30, Screen.INTERESTS),
    StepInfo(2, "My Best", "Reflect on moments when you were at your best, and why.", 40, Screen.MY_BEST),
    StepInfo(3, "Values", "Choose the three values you need in place to feel fulfilled.", 20, Screen.VALUES),
    StepInfo(4, "Get Excited", "What excites you, your job wants, objectives, challenges and resources.", 30, Screen.EXCITED),
)

fun stepDone(s: JourneySteps): List<Boolean> =
    listOf(Rules.isStep1Done(s), Rules.isStep2Done(s), Rules.isStep3Done(s), Rules.isStep4Done(s))

fun stepStatus(s: JourneySteps, index: Int): StepStatus {
    if (stepDone(s)[index]) return StepStatus.COMPLETE
    val started = when (index) {
        0 -> s.interests?.items?.isNotEmpty() == true
        1 -> s.best?.examples?.isNotEmpty() == true
        2 -> s.values?.selected?.isNotEmpty() == true
        else -> s.excited?.let { it.excitementItems.isNotEmpty() || it.objectives.isNotEmpty() } == true
    }
    return if (started) StepStatus.IN_PROGRESS else StepStatus.NOT_STARTED
}

/** Where "Continue" should take the user: the first unfinished step (Step 4 resolved to its sub-screen). */
fun resumeScreen(s: JourneySteps): Screen? {
    val done = stepDone(s)
    val first = done.indexOfFirst { !it }
    if (first == -1) return null
    if (first < 3) return stepInfos[first].screen
    val excited = s.excited
    return when {
        excited == null || !Rules.canProceedExcited(excited.excitementItems).ok -> Screen.EXCITED
        !Rules.canProceedObjectives(excited.objectives).ok -> Screen.WANTS
        else -> Screen.CHALLENGES
    }
}

private fun statusIcon(status: StepStatus): ImageVector = when (status) {
    StepStatus.COMPLETE -> Icons.Outlined.Check
    StepStatus.IN_PROGRESS -> Icons.Outlined.PlayArrow
    StepStatus.NOT_STARTED -> Icons.Outlined.RadioButtonUnchecked
}

/** S-05 Home dashboard. */
@Composable
fun HomeScreen(app: AppState, nav: Navigator) {
    val steps = app.steps
    val done = stepDone(steps)
    var prompt by remember { mutableStateOf<DailyPrompt?>(null) }
    var favourite by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        prompt = app.quietly { app.container.api.dailyPrompt() }
        favourite = prompt?.let { app.container.store.get("prompt.fav.${it.id}") == "true" } == true
    }

    PageColumn {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Overline("Welcome back")
            Text("Hi, ${app.user?.name ?: "there"}", style = MaterialTheme.typography.displaySmall)
        }
        if (app.offline) {
            InfoNote(Icons.Outlined.CloudOff, Content.Copy.OFFLINE_NOTICE) {
                GhostButton("Try again", { app.launch { app.load() } }, leadingIcon = Icons.Outlined.Refresh)
            }
        }

        CeCard {
            Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                ProgressRing(done, onSegmentClick = { nav.push(stepInfos[it].screen) })
            }
            Text("Your self-discovery journey", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text("Tap a segment to open that step", style = MaterialTheme.typography.labelSmall, color = Brand.MidGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }

        ResumeCard(app, nav, steps)

        SectionHeader("Journey milestones") { Text("Tap to open", style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray) }
        stepInfos.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEachIndexed { col, info ->
                    val status = stepStatus(steps, row * 2 + col)
                    CeCard(
                        modifier = Modifier.weight(1f),
                        color = if (status == StepStatus.IN_PROGRESS) Brand.Peach.copy(alpha = 0.45f) else Brand.White,
                        padding = 12.dp,
                        onClick = { nav.push(info.screen) },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            IconBadge(
                                statusIcon(status),
                                size = 32.dp,
                                container = if (status == StepStatus.COMPLETE) Brand.Peach else Brand.SandMid,
                            )
                            Column {
                                Text(info.name, style = MaterialTheme.typography.titleSmall)
                                Text(status.label, style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
                            }
                        }
                    }
                }
            }
        }

        prompt?.let { p ->
            ReflectionBlock {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FormatQuote, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Overline("Daily reflection", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        favourite = !favourite
                        app.container.store.put("prompt.fav.${p.id}", favourite.toString())
                    }) {
                        Icon(
                            if (favourite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (favourite) "Remove from favourites" else "Add to favourites",
                            tint = Brand.CareerBrown,
                        )
                    }
                }
                Text("“${p.text}”", style = CeTheme.extra.quote)
            }
        }

        // Guidelines S-05: quick stats once Steps 1 and 2 are complete
        if (done[0] && done[1]) {
            CeCard {
                Overline("Your inventory", color = Brand.DarkGray)
                Row(Modifier.fillMaxWidth()) {
                    Stat("${steps.interests?.items?.size ?: 0}", "interests", Modifier.weight(1f))
                    Stat("${steps.values?.selected?.size ?: 0}", "values", Modifier.weight(1f))
                    Stat("${steps.excited?.objectives?.count(Rules::isObjectiveComplete) ?: 0}", "objectives", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Brand.CareerBrown)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
    }
}

@Composable
private fun ResumeCard(app: AppState, nav: Navigator, steps: JourneySteps) {
    val next = resumeScreen(steps)
    val notStarted = stepDone(steps).none { it } && stepStatus(steps, 0) == StepStatus.NOT_STARTED
    val planReady = app.journey?.status == "plan_generated"
    CeCard {
        when {
            next == null && planReady -> {
                Overline("Your plan is ready")
                Text("Review my plan", style = MaterialTheme.typography.headlineMedium)
                Text("Ground your next steps in what you know about yourself.", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                PrimaryButton("Review my plan", { nav.openTab(Screen.PLAN) }, Modifier.fillMaxWidth(), trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward)
            }
            next == null -> {
                Overline("All four steps done")
                Text("Generate your plan", style = MaterialTheme.typography.headlineMedium)
                Text(Content.Copy.EMPTY_PLAN, style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                PrimaryButton("Go to Challenges & Resources", { nav.push(Screen.CHALLENGES) }, Modifier.fillMaxWidth(), trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward)
            }
            else -> {
                val info = stepInfos.first { it.screen == next || (it.number == 4 && next in listOf(Screen.WANTS, Screen.CHALLENGES)) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Overline(if (notStarted) "Start your journey" else "Continue where you left off", modifier = Modifier.weight(1f))
                    Badge("Step ${info.number} of 4")
                }
                Text("Step ${info.number}: ${info.name}", style = MaterialTheme.typography.headlineMedium)
                Text(info.description, style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Brand.DarkGray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("~${info.minutes} min", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray, modifier = Modifier.weight(1f))
                    PrimaryButton(if (notStarted) "Start" else "Continue", { nav.push(next) }, trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward)
                }
            }
        }
    }
}

/** "My Journey" tab: the four steps with status; Step 4 has three parts. */
@Composable
fun JourneyScreen(app: AppState, nav: Navigator) {
    val steps = app.steps
    PageColumn {
        PageTitle("My Journey", "Work through the steps at your own pace. You can return to any step at any time.")
        stepInfos.forEachIndexed { i, info ->
            val status = stepStatus(steps, i)
            CeCard(onClick = { nav.push(info.screen) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconBadge(statusIcon(status), container = if (status == StepStatus.COMPLETE) Brand.Peach else Brand.SandMid)
                    Column(Modifier.weight(1f)) {
                        Text("Step ${info.number}: ${info.name}", style = MaterialTheme.typography.titleMedium)
                        Text("${status.label} · ~${info.minutes} min", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Brand.MidGray)
                }
                if (info.number == 4) {
                    HorizontalDivider(color = Brand.BorderSubtle)
                    listOf("4a  What excites you" to Screen.EXCITED, "4b  Wants & objectives" to Screen.WANTS, "4c  Challenges & resources" to Screen.CHALLENGES)
                        .forEach { (label, screen) ->
                            GhostButton(label, { nav.push(screen) }, trailingIcon = Icons.Outlined.ChevronRight)
                        }
                }
            }
        }
        if (Rules.canGeneratePlan(steps)) {
            PrimaryButton("Generate my plan", { nav.push(Screen.CHALLENGES) }, Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.AutoAwesome)
        }
    }
}

/** S-15 (POC): profile, sync status, new journey, sign out. */
@Composable
fun ProfileScreen(app: AppState, nav: Navigator) {
    var confirmNewJourney by remember { mutableStateOf(false) }
    val user = app.user
    val pending = app.pendingCount

    PageColumn {
        PageTitle("Profile")
        CeCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconBadge(Icons.Outlined.AutoAwesome, size = 48.dp)
                Column {
                    Text(user?.name.orEmpty(), style = MaterialTheme.typography.titleLarge)
                    Text(user?.email.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                }
            }
            HorizontalDivider(color = Brand.BorderSubtle)
            InfoRow("Country / region", user?.country ?: "Not set")
            InfoRow("Journey", "Version ${app.journey?.version ?: "-"}")
            InfoRow("Server", app.container.baseUrl)
            InfoRow("Connection", if (app.offline) "Offline" else "Online")
            InfoRow("Unsynced changes", pending.toString())
        }
        if (pending > 0 || app.offline) {
            SecondaryButton("Sync now", { app.launch { app.load() } }, Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Sync)
        }
        SecondaryButton("Start a new journey", { confirmNewJourney = true }, Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Replay)
        SecondaryButton("Show the introduction again", {
            app.onboardingDone = false
            nav.resetTo(Screen.ONBOARDING)
        }, Modifier.fillMaxWidth())
        GhostButton("Sign out", {
            app.signOut()
            nav.resetTo(Screen.PROFILE_SETUP)
        }, leadingIcon = Icons.AutoMirrored.Outlined.Logout)
        Text(
            "Notifications, data export and account deletion are not part of this demo.",
            style = MaterialTheme.typography.bodySmall,
            color = Brand.MidGray,
        )
    }

    if (confirmNewJourney) {
        AlertDialog(
            onDismissRequest = { confirmNewJourney = false },
            title = { Text("Start a new journey?") },
            text = { Text("Your current journey and its plans stay saved. The new journey starts empty.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmNewJourney = false
                    app.launch {
                        app.online { app.container.api.createJourney() }?.let {
                            app.load()
                            nav.openTab(Screen.HOME)
                        }
                    }
                }) { Text("Start new journey") }
            },
            dismissButton = { TextButton(onClick = { confirmNewJourney = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Brand.NearBlack, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
    }
}
