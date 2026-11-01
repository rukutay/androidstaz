package dk.careerexplore.poc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.ExcitedStep
import dk.careerexplore.poc.domain.Importance
import dk.careerexplore.poc.domain.JobWant
import dk.careerexplore.poc.domain.Objective
import dk.careerexplore.poc.domain.Rules
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.Timeframe
import dk.careerexplore.poc.domain.WantElement
import dk.careerexplore.poc.nav.Navigator
import dk.careerexplore.poc.nav.Screen
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.theme.CeTheme
import dk.careerexplore.poc.ui.AppState
import dk.careerexplore.poc.ui.SectionHeader
import dk.careerexplore.poc.ui.StepScreen
import dk.careerexplore.poc.ui.components.AddChip
import dk.careerexplore.poc.ui.components.Badge
import dk.careerexplore.poc.ui.components.CeCard
import dk.careerexplore.poc.ui.components.CeTextField
import dk.careerexplore.poc.ui.components.GhostButton
import dk.careerexplore.poc.ui.components.IconBadge
import dk.careerexplore.poc.ui.components.InfoNote
import dk.careerexplore.poc.ui.components.NumberBadge
import dk.careerexplore.poc.ui.components.Overline
import dk.careerexplore.poc.ui.components.QuoteBlock
import dk.careerexplore.poc.ui.components.ReflectionBlock
import dk.careerexplore.poc.ui.components.SegmentedControl
import dk.careerexplore.poc.ui.components.SelectableChip
import dk.careerexplore.poc.ui.rememberAutoSaver

/** Step 4 is one API step ("excited") edited on three screens: each saves its part merged into the latest copy. */
private suspend fun AppState.saveExcited(change: (ExcitedStep) -> ExcitedStep): Boolean =
    saveStep(StepKey.EXCITED, change(steps.excited ?: ExcitedStep()), ExcitedStep.serializer())

/** S-09 Step 4a: up to 10 "When I get to..." items, 3 required. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExcitedScreen(app: AppState, nav: Navigator) {
    val initial = remember { (app.steps.excited?.excitementItems.orEmpty() + List(Rules.MAX_EXCITEMENT_ITEMS) { "" }).take(Rules.MAX_EXCITEMENT_ITEMS) }
    var rows by remember { mutableStateOf(initial) }
    var stuckOpen by remember { mutableStateOf(false) }
    val items = rows.map(String::trim).filter(String::isNotEmpty)
    val saver = rememberAutoSaver(app, items) { list -> app.saveExcited { it.copy(excitementItems = list) } }

    StepScreen(
        app = app,
        stepLabel = "Step 4 of 4 • Get Excited",
        minutes = 30,
        progress = 0.82f,
        title = "What excites you?",
        instruction = Content.Copy.STEP4_INSTRUCTION,
        primaryText = "Continue",
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = {
            val check = Rules.canProceedExcited(items)
            if (!check.ok) app.message(check.message!!) else { app.launchNow { saver.saveNow() }; nav.push(Screen.WANTS) }
        },
    ) {
        QuoteBlock("Follow the subtle spark of genuine curiosity.")
        rows.forEachIndexed { i, value ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                NumberBadge(i + 1, filled = value.isNotBlank())
                Spacer(Modifier.width(10.dp))
                CeTextField(
                    value = value,
                    onValueChange = { v -> rows = rows.mapIndexed { j, old -> if (j == i) v else old } },
                    placeholder = if (i < Rules.MIN_EXCITEMENT_TO_PROCEED) "When I get to…" else "When I get to… (optional)",
                    maxLength = 200,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
            }
            if (i == 4 && Rules.showExcitedPatternNudge(items)) {
                ReflectionBlock {
                    Text(Content.Copy.PATTERN_NUDGE_EXCITED, style = MaterialTheme.typography.headlineSmall)
                    Text("Look for recurring threads like mentoring, autonomy or making things. They point to what drives you.", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
                }
            }
        }

        CeCard(padding = 0.dp) {
            Row(Modifier.fillMaxWidth().clickable { stuckOpen = !stuckOpen }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = Brand.CareerBrown)
                Spacer(Modifier.width(10.dp))
                Text("I’m stuck — show examples", style = MaterialTheme.typography.titleSmall, color = Brand.CareerBrown, modifier = Modifier.weight(1f))
                Icon(if (stuckOpen) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = null, tint = Brand.DarkGray)
            }
            if (stuckOpen) {
                FlowRow(
                    Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Content.excitementExamples.forEach { example ->
                        val used = rows.any { it.trim() == example }
                        AddChip(example, enabled = !used, onClick = {
                            val free = rows.indexOfFirst(String::isBlank)
                            if (free == -1) app.message("All 10 rows are filled. Clear one to add this.")
                            else rows = rows.mapIndexed { j, old -> if (j == free) example else old }
                        })
                    }
                }
            }
        }
    }
}

private val importanceOptions = listOf(Importance.IMPORTANT to "Important", Importance.SOMEWHAT to "Somewhat", Importance.NOT to "Not important")
private val timeframeOptions = listOf(Timeframe.TWO_WEEKS to "2 weeks", Timeframe.ONE_MONTH to "1 month", Timeframe.THREE_MONTHS to "3 months")

private fun JobWant.sentence(): String {
    fun part(e: WantElement, placeholder: String) = e.text.ifBlank { "[$placeholder]" }
    return "I WANT to be ${part(role, "role")} in a ${part(company, "company type")} within ${part(location, "location/commute")}, " +
        "that has ${part(culture, "mission/culture")} and offers ${part(benefit, "benefit")}."
}

private fun JobWant.isBlank() = listOf(role, company, location, culture, benefit).all { it.text.isBlank() }

/** S-10 Step 4b: five job wants with importance per element, then up to three time-bound objectives. */
@Composable
fun WantsScreen(app: AppState, nav: Navigator) {
    val initialWants = remember { (app.steps.excited?.jobWants.orEmpty() + List(Rules.MAX_JOB_WANTS) { JobWant() }).take(Rules.MAX_JOB_WANTS) }
    val initialObjectives = remember { app.steps.excited?.objectives.orEmpty().ifEmpty { listOf(Objective()) } }
    var wants by remember { mutableStateOf(initialWants) }
    var objectives by remember { mutableStateOf(initialObjectives) }
    var expanded by remember { mutableStateOf(0) }

    val draftObjectives = objectives.map { it.copy(action = it.action.trim(), outcome = it.outcome.trim()) }
    val saver = rememberAutoSaver(app, wants to draftObjectives) { (w, o) ->
        app.saveExcited { it.copy(jobWants = w, objectives = o.filterNot { obj -> obj.action.isBlank() && obj.outcome.isBlank() }) }
    }

    fun updateWant(i: Int, change: (JobWant) -> JobWant) { wants = wants.mapIndexed { j, w -> if (j == i) change(w) else w } }
    fun updateObjective(i: Int, change: (Objective) -> Objective) { objectives = objectives.mapIndexed { j, o -> if (j == i) change(o) else o } }

    StepScreen(
        app = app,
        stepLabel = "Step 4 of 4 • Wants & Objectives",
        minutes = 35,
        progress = 0.9f,
        title = "Wants & Objectives",
        instruction = "Translate what excites you into clear job wants, then commit to your top three objectives.",
        primaryText = "Continue",
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = {
            val real = draftObjectives.filterNot { it.action.isBlank() && it.outcome.isBlank() }
            val check = Rules.canProceedObjectives(real)
            if (!check.ok) app.message(check.message!!) else { app.launchNow { saver.saveNow() }; nav.push(Screen.CHALLENGES) }
        },
    ) {
        SectionHeader("My job wants") { Badge("${wants.count { !it.isBlank() }}/${Rules.MAX_JOB_WANTS} started") }
        wants.forEachIndexed { i, want ->
            CeCard(padding = 0.dp) {
                Row(Modifier.fillMaxWidth().clickable { expanded = if (expanded == i) -1 else i }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Badge("${i + 1}", container = if (want.isBlank()) Brand.SandMid else Brand.Peach)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Want ${i + 1}", style = MaterialTheme.typography.titleMedium)
                        if (expanded != i && !want.isBlank()) {
                            Text(listOf(want.role, want.company, want.location).map { it.text }.filter(String::isNotBlank).joinToString(" • "), style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Icon(if (expanded == i) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = null, tint = Brand.DarkGray)
                }
                if (expanded == i) {
                    Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ReflectionBlock { Text(want.sentence(), style = CeTheme.extra.quote.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize)) }
                        listOf(
                            Triple("1. Role", want.role) { e: WantElement -> want.copy(role = e) },
                            Triple("2. Company type", want.company) { e: WantElement -> want.copy(company = e) },
                            Triple("3. Location & commute", want.location) { e: WantElement -> want.copy(location = e) },
                            Triple("4. Mission & culture", want.culture) { e: WantElement -> want.copy(culture = e) },
                            Triple("5. Key benefit", want.benefit) { e: WantElement -> want.copy(benefit = e) },
                        ).forEach { (label, element, set) ->
                            CeTextField(
                                value = element.text,
                                onValueChange = { v -> updateWant(i) { set(element.copy(text = v)) } },
                                label = label,
                                trailingLabel = "Importance",
                                maxLength = 200,
                            )
                            SegmentedControl(
                                options = importanceOptions.map { it.second },
                                selectedIndex = importanceOptions.indexOfFirst { it.first == element.importance },
                                onSelect = { idx -> updateWant(i) { set(element.copy(importance = importanceOptions[idx].first)) } },
                            )
                        }
                    }
                }
            }
        }

        InfoNote(Icons.Outlined.Flag, "Vague ambitions create hesitation. Concrete conditions and commitments give your transition direction.")

        SectionHeader("Turn wants into action") { Badge("${objectives.size} of ${Rules.MAX_OBJECTIVES}") }
        Text("Pick up to three of your most important and realistic wants and turn them into objectives.", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
        objectives.forEachIndexed { i, objective ->
            CeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Objective ${i + 1}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (objectives.size > 1) {
                        IconButton(onClick = { objectives = objectives.filterIndexed { j, _ -> j != i } }) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remove objective ${i + 1}", tint = Brand.DarkGray)
                        }
                    }
                }
                Text("In the next…", style = MaterialTheme.typography.labelMedium, color = Brand.DarkGray)
                SegmentedControl(
                    options = timeframeOptions.map { it.second },
                    selectedIndex = timeframeOptions.indexOfFirst { it.first == objective.timeframe },
                    onSelect = { idx -> updateObjective(i) { it.copy(timeframe = timeframeOptions[idx].first) } },
                )
                CeTextField(objective.action, { v -> updateObjective(i) { it.copy(action = v) } }, label = "I will…", singleLine = false, minLines = 2, maxLength = 300)
                CeTextField(objective.outcome, { v -> updateObjective(i) { it.copy(outcome = v) } }, label = "so that…", singleLine = false, minLines = 2, maxLength = 300)
            }
        }
        if (objectives.size < Rules.MAX_OBJECTIVES) {
            GhostButton("Add an objective", { objectives = objectives + Objective() }, leadingIcon = Icons.Outlined.AddCircleOutline)
        }
    }
}

/** S-11 Step 4c: challenges and resources per objective, then "Generate My Plan". */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChallengesScreen(app: AppState, nav: Navigator) {
    var objectives by remember { mutableStateOf(app.steps.excited?.objectives.orEmpty()) }
    val visible = objectives.withIndex().filter { it.value.action.isNotBlank() }
    var selected by remember { mutableStateOf(visible.firstOrNull()?.index ?: 0) }
    var challengeInput by remember { mutableStateOf("") }
    var resourceInput by remember { mutableStateOf("") }
    var generating by remember { mutableStateOf(false) }

    val saver = rememberAutoSaver(app, objectives) { list -> app.saveExcited { it.copy(objectives = list) } }

    fun update(change: (Objective) -> Objective) {
        objectives = objectives.mapIndexed { i, o -> if (i == selected) change(o) else o }
    }

    fun generate() {
        app.launch {
            if (!saver.saveNow()) return@launch
            val steps = app.steps
            val missing = stepInfos.zip(stepDone(steps)).firstOrNull { !it.second }?.first
            if (missing != null) {
                app.message("Complete Step ${missing.number}: ${missing.name} to generate your plan.")
                return@launch
            }
            val id = app.journey?.id ?: return@launch
            generating = true
            val plan = app.online { app.container.api.generatePlan(id) }
            generating = false
            if (plan != null) {
                app.load()
                app.message(Content.Copy.PLAN_GENERATED)
                nav.openTab(Screen.PLAN)
            }
        }
    }

    StepScreen(
        app = app,
        stepLabel = "Step 4 of 4 • Action mapping",
        minutes = 25,
        progress = 1f,
        title = "Challenges & Resources",
        instruction = "Prepare for reality. For each objective, anticipate what might get in the way and gather what will help you.",
        primaryText = if (generating) "Generating…" else "Generate My Plan",
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = { if (!generating) generate() },
    ) {
        if (visible.isEmpty()) {
            InfoNote(Icons.Outlined.Flag, "Add at least one objective in Wants & Objectives first.") {
                GhostButton("Go to Wants & Objectives", { nav.back() })
            }
            return@StepScreen
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            visible.forEachIndexed { n, (index, o) ->
                SelectableChip(
                    "Obj ${n + 1}: ${o.action.take(18)}${if (o.action.length > 18) "…" else ""}",
                    index == selected,
                    onClick = { selected = index; challengeInput = ""; resourceInput = "" },
                    showCheck = false,
                )
            }
        }
        val objective = objectives.getOrNull(selected) ?: return@StepScreen
        ReflectionBlock {
            Badge("${objective.timeframe.label} objective")
            Text("“In the next ${objective.timeframe.label} I will ${objective.action} so that ${objective.outcome}.”", style = CeTheme.extra.quote)
        }

        ListEditor(
            title = "Anticipated challenges",
            helper = "Outside elements, personal tendencies, circumstances.",
            icon = Icons.Outlined.WarningAmber,
            placeholder = "Anticipate an obstacle",
            items = objective.challenges,
            input = challengeInput,
            onInput = { challengeInput = it },
            onAdd = {
                val check = Rules.canAddChallengeOrResource(objective.challenges, challengeInput)
                if (check.ok) { update { it.copy(challenges = it.challenges + challengeInput.trim()) }; challengeInput = "" } else app.message(check.message!!)
            },
            onRemove = { item -> update { it.copy(challenges = it.challenges - item) } },
        )
        ListEditor(
            title = "Available resources",
            helper = "Skills, contacts, training, self-belief, money, support.",
            icon = Icons.Outlined.Diamond,
            placeholder = "Identify a resource",
            items = objective.resources,
            input = resourceInput,
            onInput = { resourceInput = it },
            onAdd = {
                val check = Rules.canAddChallengeOrResource(objective.resources, resourceInput)
                if (check.ok) { update { it.copy(resources = it.resources + resourceInput.trim()) }; resourceInput = "" } else app.message(check.message!!)
            },
            onRemove = { item -> update { it.copy(resources = it.resources - item) } },
        )
        InfoNote(Icons.Outlined.Lightbulb, "When challenges have named resources, anxiety becomes a checklist.", title = "Mindset anchor")
    }
}

@Composable
private fun ListEditor(
    title: String,
    helper: String,
    icon: ImageVector,
    placeholder: String,
    items: List<String>,
    input: String,
    onInput: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Brand.CareerBrown)
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Badge("${items.size} / ${Rules.MAX_CHALLENGES_OR_RESOURCES}", container = Brand.SandMid)
        }
        Text(helper, style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CeTextField(
                value = input,
                onValueChange = onInput,
                placeholder = placeholder,
                maxLength = 200,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
            )
            IconButton(onClick = onAdd) { Icon(Icons.Outlined.Add, contentDescription = "Add to $title", tint = Brand.CareerBrown) }
        }
        items.forEach { item ->
            CeCard(padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemove(item) }) { Icon(Icons.Outlined.Close, contentDescription = "Remove $item", tint = Brand.DarkGray) }
                }
            }
        }
    }
}
