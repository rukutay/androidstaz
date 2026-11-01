package dk.careerexplore.poc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Interests
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.careerexplore.poc.domain.BestExample
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.Insights
import dk.careerexplore.poc.domain.Plan
import dk.careerexplore.poc.domain.PlanVersion
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
import dk.careerexplore.poc.ui.components.SelectableChip

private fun formatDate(iso: String): String {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val (y, m, d) = iso.take(10).split("-").map { it.toIntOrNull() ?: 0 }
    return if (m in 1..12) "$d ${months[m - 1]} $y" else iso.take(10)
}

private fun planAsText(plan: Plan): String = buildString {
    val p = plan.data
    appendLine("My Career Explore plan (v${plan.version}, ${formatDate(plan.generatedAt)})")
    appendLine()
    appendLine("Top interests: ${p.interests.topTwo.ifEmpty { p.interests.items.take(5) }.joinToString(", ")}")
    appendLine("Non-negotiable values: ${p.values.selected.joinToString(", ")}")
    if (p.excitement.isNotEmpty()) appendLine("What excites me: ${p.excitement.joinToString("; ")}")
    appendLine()
    appendLine("My 3-month objectives:")
    p.objectives.forEachIndexed { i, o -> appendLine("${i + 1}. In the next ${o.timeframe.label} I will ${o.action} so that ${o.outcome}.") }
}

/** S-12 My Plan: the generated, versioned plan with export, share and coach CTA. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanScreen(app: AppState, nav: Navigator) {
    val journeyId = app.journey?.id
    val generated = app.journey?.status == "plan_generated"
    var plan by remember { mutableStateOf<Plan?>(null) }
    var versions by remember { mutableStateOf<List<PlanVersion>>(emptyList()) }
    var version by remember { mutableStateOf<Int?>(null) }
    var loading by remember { mutableStateOf(generated) }
    var historyOpen by remember { mutableStateOf(false) }

    LaunchedEffect(journeyId, version, generated) {
        if (journeyId == null || !generated) return@LaunchedEffect
        loading = true
        plan = app.online { app.container.api.plan(journeyId, version) }
        versions = app.quietly { app.container.api.plans(journeyId) }.orEmpty()
        loading = false
    }

    if (!generated) {
        PageColumn {
            PageTitle("My Plan")
            CeCard {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { ProgressRing(stepDone(app.steps), size = 140.dp, stroke = 10.dp) }
                Text(Content.Copy.EMPTY_PLAN, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                val next = resumeScreen(app.steps)
                PrimaryButton(
                    if (next == null) "Generate my plan" else "Continue my journey",
                    { nav.push(next ?: Screen.CHALLENGES) },
                    Modifier.fillMaxWidth(),
                    trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward,
                )
            }
        }
        return
    }

    val current = plan
    if (current == null) {
        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
            if (loading) CircularProgressIndicator(color = Brand.CareerBrown)
            else Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Your plan needs a connection to load.", style = MaterialTheme.typography.bodyLarge)
                SecondaryButton("Try again", {
                    val id = journeyId ?: return@SecondaryButton
                    app.launch { plan = app.online { app.container.api.plan(id, version) } }
                })
            }
        }
        return
    }
    val data = current.data

    PageColumn {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("My Plan", style = MaterialTheme.typography.displaySmall)
                Text("${data.userName} • ${formatDate(current.generatedAt)}", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
            }
            Box {
                SelectableChip("v${current.version} • History", selected = false, showCheck = false, onClick = { historyOpen = true })
                DropdownMenu(expanded = historyOpen, onDismissRequest = { historyOpen = false }) {
                    versions.forEach { v ->
                        DropdownMenuItem(
                            text = { Text("v${v.version} — ${formatDate(v.generatedAt)}") },
                            leadingIcon = { Icon(Icons.Outlined.History, contentDescription = null) },
                            onClick = { version = v.version; historyOpen = false },
                        )
                    }
                }
            }
        }

        ReflectionBlock {
            Text("Your personal plan is ready", style = MaterialTheme.typography.titleMedium)
            Text("Ground your next steps in what you know about yourself.", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SecondaryButton("Export as PDF", {
                app.launch {
                    app.online { app.container.api.exportPlanHtml(current.journeyId, current.version) }
                        ?.let { app.platform.printHtml("Career Explore plan v${current.version}", it) }
                }
            }, Modifier.weight(1f), leadingIcon = Icons.Outlined.PictureAsPdf)
            SecondaryButton("Share", { app.platform.shareText("My Career Explore plan", planAsText(current)) }, Modifier.weight(0.7f), leadingIcon = Icons.Outlined.Share)
        }
        GhostButton("Revisit a step", { nav.openTab(Screen.JOURNEY) }, leadingIcon = Icons.Outlined.Edit)

        CeCard {
            SectionHeader("My top interests") { Overline("Self-anchor", color = Brand.DarkGray) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                (data.interests.topTwo + data.interests.items.filterNot { it in data.interests.topTwo }).forEach {
                    SelectableChip(it, selected = it in data.interests.topTwo, onClick = {}, showCheck = false)
                }
            }
        }

        if (data.bestMoments.isNotEmpty()) {
            SectionTitle(Icons.Outlined.WbSunny, "When I am at my best")
            data.bestMoments.forEach { m ->
                CeCard {
                    Text(m.headline, style = MaterialTheme.typography.titleMedium)
                    m.topReason?.let { Text("“$it”", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray) }
                }
            }
        }

        SectionTitle(Icons.Outlined.Balance, "Non-negotiable values")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            data.values.selected.forEach { v ->
                CeCard(Modifier.weight(1f), padding = 12.dp) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { IconBadge(valueIcons[v] ?: Icons.Outlined.Lightbulb, size = 36.dp) }
                    Text(v, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        if (data.values.reflection.isNotBlank()) {
            ReflectionBlock {
                Text("“${data.values.reflection}”", style = CeTheme.extra.quote)
                Text("— My reflection", style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
            }
        }

        if (data.excitement.isNotEmpty()) {
            CeCard {
                Text("What excites me", style = MaterialTheme.typography.headlineSmall)
                data.excitement.forEachIndexed { i, e ->
                    Row(verticalAlignment = Alignment.Top) {
                        Badge("${i + 1}", container = Brand.SandMid)
                        Spacer(Modifier.width(10.dp))
                        Text(e, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        SectionTitle(Icons.Outlined.Flag, "My 3-month objectives")
        data.objectives.forEachIndexed { i, o ->
            CeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge("${i + 1}", container = Brand.CareerBrown, content = Brand.White)
                    Spacer(Modifier.width(8.dp))
                    Overline("Target: ${o.timeframe.label}", modifier = Modifier.weight(1f))
                }
                Text("I will ${o.action}", style = MaterialTheme.typography.titleMedium)
                Text("so that ${o.outcome}", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                o.challenges.forEach { LabeledLine(Icons.Outlined.WarningAmber, "Challenge", it) }
                o.resources.forEach { LabeledLine(Icons.Outlined.Diamond, "Resource", it) }
            }
        }

        CoachBanner { nav.push(Screen.COACH) }
    }
}

@Composable
private fun SectionTitle(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun LabeledLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, text: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Brand.Sand).padding(10.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
            Text(text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CoachBanner(onClick: () -> Unit) {
    CeCard(color = Brand.SandMid) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconBadge(Icons.Outlined.Psychology, size = 44.dp, container = Brand.White)
            Column(Modifier.weight(1f)) {
                Text("Ready to go deeper?", style = MaterialTheme.typography.headlineSmall)
                Text("Partner with a Career Explore coach to move confidently toward your goals.", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
            }
        }
        PrimaryButton("Talk to a coach", onClick, Modifier.fillMaxWidth(), trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward)
    }
}

/** Share of [b] within the pair, from the optional "My Best" context tags. Null when no moment has either tag. */
private fun spectrum(examples: List<BestExample>, a: String, b: String): Float? {
    val ca = examples.count { a in it.tags }
    val cb = examples.count { b in it.tags }
    return if (ca + cb == 0) null else cb.toFloat() / (ca + cb)
}

/** S-13 Insights (demo): keyword frequency from the server, work-environment profile from context tags. No AI. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightsScreen(app: AppState, nav: Navigator) {
    val journeyId = app.journey?.id
    var insights by remember { mutableStateOf<Insights?>(null) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(journeyId) {
        if (journeyId != null) insights = app.online { app.container.api.insights(journeyId) }
        loading = false
    }
    val examples = app.steps.best?.examples.orEmpty()

    PageColumn {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PageTitle("Insights", "Patterns across your journey")
        }
        InfoNote(Icons.Outlined.Info, Content.Copy.INSIGHTS_DISCLAIMER)

        CeCard {
            Text("Themes in your answers", style = MaterialTheme.typography.headlineSmall)
            Text("Keywords weighted by how often they appear in your interests and what excites you.", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
            val keywords = insights?.keywords.orEmpty()
            when {
                loading -> CircularProgressIndicator(color = Brand.CareerBrown, modifier = Modifier.size(24.dp))
                keywords.isEmpty() -> Text("Add more interests and excitement items to see themes.", style = MaterialTheme.typography.bodyMedium, color = Brand.MidGray)
                else -> {
                    val max = keywords.maxOf { it.count }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        keywords.forEachIndexed { i, k ->
                            val top = i == 0
                            Box(
                                Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(if (top) Brand.CareerBrown else if (k.count == max) Brand.Peach else Brand.SandMid)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    "${k.word} ×${k.count}",
                                    color = if (top) Brand.White else Brand.CareerBrownPressed,
                                    fontSize = (13 + 5 * k.count / max).sp,
                                    fontWeight = if (k.count == max) FontWeight.SemiBold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                    ReflectionBlock {
                        Overline("Pattern takeaway")
                        Text(
                            "Your answers keep returning to ${keywords.take(3).joinToString(", ") { it.word }}. What do these have in common for you?",
                            style = CeTheme.extra.quote,
                        )
                    }
                }
            }
        }

        CeCard {
            Text("Work environment", style = MaterialTheme.typography.headlineSmall)
            Text("Where you thrive, based on the context tags in My Best.", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
            val rows = Content.contextTagPairs.mapNotNull { (a, b) -> spectrum(examples, a, b)?.let { Triple(a, b, it) } }
            if (rows.isEmpty()) {
                Text("Tag your moments in My Best (solo/team, indoor/outdoor, structured/creative) to see this.", style = MaterialTheme.typography.bodyMedium, color = Brand.MidGray)
                GhostButton("Open My Best", { nav.push(Screen.MY_BEST) })
            }
            rows.forEach { (a, b, share) ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        Text("$a ${((1 - share) * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray, modifier = Modifier.weight(1f))
                        Text("$b ${(share * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, color = Brand.CareerBrown)
                    }
                    dk.careerexplore.poc.ui.components.StepProgressBar(share)
                }
            }
        }

        CoachBanner { nav.push(Screen.COACH) }
    }
}

/** S-14 Coach Connect: mail and website links. Coach details are placeholders until Career Explore provides them. */
@Composable
fun CoachScreen(app: AppState, nav: Navigator) {
    val uri = LocalUriHandler.current
    PageColumn {
        GhostButton("Back", { nav.back() }, leadingIcon = Icons.AutoMirrored.Outlined.ArrowBack)
        Overline("Quarterlife coaching")
        PageTitle("Ready to go deeper?", Content.Copy.COACH_CTA.removePrefix("Ready to go deeper? "))

        Text("Why work with a Career Explore coach", style = MaterialTheme.typography.titleMedium)
        Content.coachBenefits.forEachIndexed { i, (title, body) ->
            CeCard {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconBadge(listOf(Icons.Outlined.Lightbulb, Icons.Outlined.AutoAwesome, Icons.Outlined.Interests)[i % 3], container = Brand.SandMid)
                    Column(Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text(body, style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                    }
                }
            }
        }

        CeCard {
            Overline("Your coach", color = Brand.DarkGray)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(56.dp).clip(RoundedCornerShape(28.dp)).background(Brand.Peach), contentAlignment = Alignment.Center) {
                    Text("A", style = MaterialTheme.typography.headlineMedium, color = Brand.CareerBrown)
                }
                Column {
                    Text("Anita", style = MaterialTheme.typography.headlineSmall)
                    Text("Career Explore coach", style = MaterialTheme.typography.bodySmall, color = Brand.CareerBrown)
                }
            }
            Text("Photo and short bio will be provided by Career Explore.", style = MaterialTheme.typography.bodySmall, color = Brand.MidGray)
        }

        PrimaryButton(
            "Email us to learn more",
            {
                val subject = "Career Explore coaching".replace(" ", "%20")
                val body = "Hi Anita, I have completed my Career Explore plan and would like to learn more about coaching.".replace(" ", "%20")
                uri.openUri("mailto:${Content.Copy.COACH_EMAIL}?subject=$subject&body=$body")
            },
            Modifier.fillMaxWidth(),
            leadingIcon = Icons.Outlined.Email,
        )
        SecondaryButton("Visit our website", { uri.openUri(Content.Copy.COACH_WEBSITE) }, Modifier.fillMaxWidth(), trailingIcon = Icons.AutoMirrored.Outlined.OpenInNew)
        Spacer(Modifier.height(4.dp))
    }
}
