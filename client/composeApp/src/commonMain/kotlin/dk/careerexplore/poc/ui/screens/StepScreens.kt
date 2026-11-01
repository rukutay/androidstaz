package dk.careerexplore.poc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.domain.BestExample
import dk.careerexplore.poc.domain.BestStep
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.InterestsStep
import dk.careerexplore.poc.domain.Rules
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.ValuesStep
import dk.careerexplore.poc.nav.Navigator
import dk.careerexplore.poc.nav.Screen
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.theme.CeTheme
import dk.careerexplore.poc.ui.AppState
import dk.careerexplore.poc.ui.SectionHeader
import dk.careerexplore.poc.ui.StepScreen
import dk.careerexplore.poc.ui.components.AddChip
import dk.careerexplore.poc.ui.components.Badge
import dk.careerexplore.poc.ui.components.CardShape
import dk.careerexplore.poc.ui.components.CeCard
import dk.careerexplore.poc.ui.components.CeTextField
import dk.careerexplore.poc.ui.components.GhostButton
import dk.careerexplore.poc.ui.components.IconBadge
import dk.careerexplore.poc.ui.components.InfoNote
import dk.careerexplore.poc.ui.components.Overline
import dk.careerexplore.poc.ui.components.PrimaryButton
import dk.careerexplore.poc.ui.components.QuoteBlock
import dk.careerexplore.poc.ui.components.ReflectionBlock
import dk.careerexplore.poc.ui.components.RemovableChip
import dk.careerexplore.poc.ui.components.SecondaryButton
import dk.careerexplore.poc.ui.components.SelectableChip
import dk.careerexplore.poc.ui.rememberAutoSaver

/** S-06 Step 1: build a list of 20+ interests, pick the top 2. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen(app: AppState, nav: Navigator) {
    val initial = remember { app.steps.interests ?: InterestsStep() }
    var items by remember { mutableStateOf(initial.items) }
    var topTwo by remember { mutableStateOf(initial.topTwo) }
    var input by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    var ideasOpen by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(Content.interestSuggestions.keys.first()) }

    val saver = rememberAutoSaver(app, InterestsStep(items, topTwo)) { app.saveStep(StepKey.INTERESTS, it, InterestsStep.serializer()) }

    fun add(text: String) {
        val check = Rules.canAddInterest(items, text)
        if (check.ok) {
            items = items + text.trim()
            input = ""
            inputError = null
        } else {
            inputError = check.message
        }
    }

    StepScreen(
        app = app,
        stepLabel = "Step 1 of 4 • Interests",
        minutes = 30,
        progress = 0.25f,
        title = "Interests",
        instruction = Content.Copy.STEP1_INSTRUCTION,
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = {
            val check = Rules.canProceedInterests(items)
            if (!check.ok) app.message(check.message!!) else { app.launchNow { saver.saveNow() }; nav.push(Screen.MY_BEST) }
        },
    ) {
        QuoteBlock(Content.STEP1_QUOTE)

        CeCard {
            CeTextField(
                value = input,
                onValueChange = { input = it; inputError = null },
                label = "Add an interest",
                placeholder = "e.g. Graphic design, botany, mentoring",
                maxLength = Rules.MAX_INTEREST_LENGTH,
                showCounter = true,
                error = inputError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { add(input) }),
            )
            PrimaryButton("Add", { add(input) }, Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Add)
        }

        SectionHeader("Your collection") { Badge("${items.size} added • aim for 20+") }
        Text(Rules.interestsCounter(items.size), style = MaterialTheme.typography.bodyMedium, color = if (items.size >= Rules.INTERESTS_GOAL) Brand.SoftGreen else Brand.DarkGray)
        if (items.isEmpty()) {
            Text("Nothing yet — add your first interest above or tap an idea below.", style = MaterialTheme.typography.bodyMedium, color = Brand.MidGray)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item -> RemovableChip(item, onRemove = { items = items - item; topTwo = topTwo - item }) }
        }

        if (Rules.showInterestsPatternNudge(items.size)) {
            InfoNote(Icons.Outlined.Lightbulb, Content.Copy.PATTERN_NUDGE_INTERESTS)
            CeCard(color = Brand.SandMid.copy(alpha = 0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("My top 2 interests", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Badge("${topTwo.size} / 2 chosen", container = Brand.SandMid)
                }
                Text("Select the two that matter most to you.", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { item ->
                        val selected = item in topTwo
                        SelectableChip(item, selected, onClick = {
                            val check = Rules.canPickTopTwo(items, topTwo, item)
                            when {
                                !check.ok -> app.message(check.message!!)
                                selected -> topTwo = topTwo - item
                                else -> topTwo = topTwo + item
                            }
                        })
                    }
                }
            }
        }

        CeCard(padding = 0.dp) {
            Row(
                Modifier.fillMaxWidth().clickable { ideasOpen = !ideasOpen }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = Brand.CareerBrown)
                Spacer(Modifier.width(10.dp))
                Text("Need ideas?", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(if (ideasOpen) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = if (ideasOpen) "Hide ideas" else "Show ideas", tint = Brand.DarkGray)
            }
            if (ideasOpen) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Content.interestSuggestions.keys.forEach { c ->
                            SelectableChip(c, c == category, onClick = { category = c }, showCheck = false)
                        }
                    }
                    Text("Tap to add directly to your collection:", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Content.interestSuggestions.getValue(category).forEach { s ->
                            val added = items.any { it.equals(s, ignoreCase = true) }
                            AddChip(s, onClick = { add(s) }, enabled = !added)
                        }
                    }
                }
            }
        }
    }
}

/** Editable form of one "My Best" moment; always three "because" slots. */
private data class MomentDraft(val text: String = "", val reasons: List<String> = listOf("", "", ""), val tags: Set<String> = emptySet()) {
    fun isBlank() = text.isBlank() && reasons.all(String::isBlank)
    fun toExample() = BestExample(text.trim(), reasons.map(String::trim).filter(String::isNotEmpty), tags.toList())

    companion object {
        fun from(e: BestExample) = MomentDraft(e.text, (e.reasons + List(3) { "" }).take(3), e.tags.toSet())
    }
}

/** S-07 Step 2: 1-6 moments with up to three reasons each; optional context tags; mock "Help me reflect". */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyBestScreen(app: AppState, nav: Navigator) {
    val initial = remember {
        val stored = app.steps.best?.examples.orEmpty().map(MomentDraft::from)
        stored + List((3 - stored.size).coerceAtLeast(0)) { MomentDraft() }
    }
    var moments by remember { mutableStateOf(initial) }
    var expanded by remember { mutableStateOf(0) }
    var exampleOpen by remember { mutableStateOf(false) }
    var questions by remember { mutableStateOf<List<String>?>(null) }

    val cleaned = BestStep(moments.filterNot(MomentDraft::isBlank).map(MomentDraft::toExample))
    val saver = rememberAutoSaver(app, cleaned) { app.saveStep(StepKey.BEST, it, BestStep.serializer()) }

    fun update(index: Int, change: (MomentDraft) -> MomentDraft) {
        moments = moments.mapIndexed { i, m -> if (i == index) change(m) else m }
    }

    StepScreen(
        app = app,
        stepLabel = "Step 2 of 4 • My Best",
        minutes = 40,
        progress = 0.5f,
        title = "My Best",
        instruction = Content.Copy.STEP2_INSTRUCTION,
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = {
            val check = Rules.canProceedBest(cleaned.examples)
            if (!check.ok) app.message(check.message!!) else {
                Rules.bestNudge(cleaned.examples)?.let(app::message)
                app.launchNow { saver.saveNow() }
                nav.push(Screen.VALUES)
            }
        },
    ) {
        CeCard(color = Brand.SandMid, padding = 0.dp) {
            Row(
                Modifier.fillMaxWidth().clickable { exampleOpen = !exampleOpen }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconBadge(Icons.AutoMirrored.Outlined.MenuBook, size = 32.dp)
                Spacer(Modifier.width(12.dp))
                Text("Read an example", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(if (exampleOpen) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = null, tint = Brand.DarkGray)
            }
            if (exampleOpen) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("I was at my best when…", style = MaterialTheme.typography.labelMedium, color = Brand.CareerBrown)
                    Text(Content.bestExample.text, style = MaterialTheme.typography.bodyMedium)
                    Text("because…", style = MaterialTheme.typography.labelMedium, color = Brand.CareerBrown)
                    Content.bestExample.reasons.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }

        moments.forEachIndexed { index, moment ->
            CeCard(padding = 0.dp) {
                Row(
                    Modifier.fillMaxWidth().clickable { expanded = if (expanded == index) -1 else index }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Badge("${index + 1}", container = if (moment.isBlank()) Brand.SandMid else Brand.Peach)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Moment ${index + 1}", style = MaterialTheme.typography.titleMedium)
                        if (expanded != index && moment.text.isNotBlank()) {
                            Text(moment.text, style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (moments.size > 1 && expanded == index) {
                        IconButton(onClick = {
                            moments = moments.filterIndexed { i, _ -> i != index }
                            expanded = -1
                        }) { Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remove moment ${index + 1}", tint = Brand.DarkGray) }
                    }
                    Icon(if (expanded == index) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = null, tint = Brand.DarkGray)
                }
                if (expanded == index) {
                    Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CeTextField(
                            value = moment.text,
                            onValueChange = { v -> update(index) { it.copy(text = v) } },
                            label = "I was at my best when…",
                            singleLine = false,
                            minLines = 3,
                            maxLength = Rules.MAX_BEST_TEXT,
                            showCounter = true,
                        )
                        Text("I was at my best because…", style = MaterialTheme.typography.titleSmall)
                        moment.reasons.forEachIndexed { r, reason ->
                            CeTextField(
                                value = reason,
                                onValueChange = { v -> update(index) { it.copy(reasons = it.reasons.mapIndexed { j, old -> if (j == r) v else old }) } },
                                placeholder = "Reason ${r + 1}",
                                maxLength = 200,
                                leading = { Text("${r + 1}", style = MaterialTheme.typography.labelMedium, color = Brand.MidGray) },
                            )
                        }
                        Overline("Context (optional)", color = Brand.DarkGray)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Content.contextTagPairs.forEach { (a, b) ->
                                listOf(a, b).forEach { tag ->
                                    SelectableChip(tag, tag in moment.tags, showCheck = false, onClick = {
                                        update(index) {
                                            val other = if (tag == a) b else a
                                            it.copy(tags = if (tag in it.tags) it.tags - tag else it.tags - other + tag)
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }

        Rules.bestNudge(cleaned.examples)?.let { nudge ->
            InfoNote(Icons.Outlined.Lightbulb, nudge) {
                if (moments.size < Rules.MAX_BEST_EXAMPLES) {
                    GhostButton("Add another moment", {
                        moments = moments + MomentDraft()
                        expanded = moments.lastIndex
                    }, leadingIcon = Icons.Outlined.AddCircleOutline)
                }
            }
        } ?: run {
            if (moments.size < Rules.MAX_BEST_EXAMPLES) {
                GhostButton("Add another moment", {
                    moments = moments + MomentDraft()
                    expanded = moments.lastIndex
                }, leadingIcon = Icons.Outlined.AddCircleOutline)
            }
        }

        SecondaryButton(
            "Help me reflect",
            {
                val id = app.journey?.id ?: return@SecondaryButton
                app.launch { app.online { app.container.api.reflect(id) }?.let { questions = it.questions } }
            },
            Modifier.fillMaxWidth(),
            leadingIcon = Icons.Outlined.AutoAwesome,
        )
    }

    questions?.let { qs ->
        AlertDialog(
            onDismissRequest = { questions = null },
            title = { Text("Go a little deeper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Think about these, then add what you notice to your reasons:", style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                    qs.forEachIndexed { i, q -> Text("${i + 1}. $q", style = MaterialTheme.typography.bodyLarge) }
                    Text("Demo: fixed questions, no AI.", style = MaterialTheme.typography.bodySmall, color = Brand.MidGray)
                }
            },
            confirmButton = { TextButton(onClick = { questions = null }) { Text("Thanks") } },
        )
    }
}

/** Outline icon per value (Step 3 grid). */
val valueIcons: Map<String, ImageVector> = mapOf(
    "Being busy" to Icons.Outlined.Speed,
    "Getting recognition" to Icons.Outlined.EmojiEvents,
    "Feeling challenged" to Icons.Outlined.Terrain,
    "Position & status" to Icons.Outlined.WorkspacePremium,
    "Caring for others" to Icons.Outlined.VolunteerActivism,
    "Solving problems" to Icons.Outlined.Extension,
    "Work-life balance" to Icons.Outlined.Balance,
    "Having influence" to Icons.Outlined.Campaign,
    "Developing others" to Icons.Outlined.Groups,
    "Using my creativity" to Icons.Outlined.Palette,
    "Taking risks" to Icons.Outlined.Explore,
    "Financial security" to Icons.Outlined.Savings,
    "Working independently" to Icons.Outlined.Person,
    "Good relations with colleagues" to Icons.Outlined.Handshake,
    "Variety" to Icons.Outlined.Shuffle,
    "Being an expert" to Icons.Outlined.School,
    "Making a difference" to Icons.Outlined.Public,
    "Developing myself" to Icons.AutoMirrored.Outlined.TrendingUp,
    "Being given responsibility" to Icons.Outlined.Verified,
    "Individuality" to Icons.Outlined.Fingerprint,
)

/** S-08 Step 3: exactly three non-negotiable values, then a reflection. */
@Composable
fun ValuesScreen(app: AppState, nav: Navigator) {
    val initial = remember { app.steps.values ?: ValuesStep() }
    var selected by remember { mutableStateOf(initial.selected) }
    var reflection by remember { mutableStateOf(initial.reflection) }
    val saver = rememberAutoSaver(app, ValuesStep(selected, reflection.trim())) { app.saveStep(StepKey.VALUES, it, ValuesStep.serializer()) }

    StepScreen(
        app = app,
        stepLabel = "Step 3 of 4 • Happy Standards",
        minutes = 20,
        progress = 0.75f,
        title = "Your must-haves",
        instruction = Content.Copy.STEP3_INSTRUCTION,
        onBack = { app.launchNow { saver.saveNow() }; nav.back() },
        onPrimary = {
            val check = Rules.canProceedValues(selected)
            if (!check.ok) app.message(check.message!!) else { app.launchNow { saver.saveNow() }; nav.push(Screen.EXCITED) }
        },
    ) {
        ReflectionBlock {
            Text("“${Content.STEP3_QUOTE}”", style = CeTheme.extra.quote)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Select your non-negotiables", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray, modifier = Modifier.weight(1f))
                Badge(Rules.valuesCounter(selected), container = Brand.CareerBrown, content = Brand.White, icon = Icons.Outlined.CheckCircle)
            }
        }

        SectionHeader("Core values") { Text("Tap to select or swap", style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Content.values.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { value ->
                        val isSelected = value in selected
                        val disabled = !isSelected && selected.size >= Rules.VALUES_REQUIRED
                        Surface(
                            shape = CardShape,
                            color = if (isSelected) Brand.CareerBrown else Brand.White,
                            border = BorderStroke(1.dp, if (isSelected) Brand.CareerBrown else Brand.BorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .alpha(if (disabled) 0.45f else 1f)
                                .clickable(role = Role.Checkbox) {
                                    val check = Rules.canToggleValue(selected, value)
                                    when {
                                        !check.ok -> app.message(check.message!!)
                                        isSelected -> selected = selected - value
                                        else -> selected = selected + value
                                    }
                                },
                        ) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(valueIcons[value] ?: Icons.Outlined.Lightbulb, contentDescription = null, tint = if (isSelected) Brand.White else Brand.CareerBrown, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    value,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Brand.White else Brand.NearBlack,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                Icon(
                                    if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) Brand.White else Brand.BorderInput,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selected.size == Rules.VALUES_REQUIRED) {
            ReflectionBlock {
                Overline("Reflective checkpoint")
                val prompt = buildAnnotatedString {
                    append("If a role can’t offer ")
                    selected.forEachIndexed { i, v ->
                        withStyle(SpanStyle(fontWeight = CeTheme.extra.quoteEmphasis.fontWeight, color = Brand.CareerBrown)) { append(v) }
                        append(if (i == 0) ", " else if (i == 1) ", and " else "")
                    }
                    append(" — how will that affect your happiness at work?")
                }
                Text(prompt, style = MaterialTheme.typography.headlineSmall.copy(fontStyle = CeTheme.extra.quote.fontStyle))
                CeTextField(
                    value = reflection,
                    onValueChange = { reflection = it },
                    placeholder = "Write a few honest sentences…",
                    singleLine = false,
                    minLines = 4,
                    maxLength = 300,
                    showCounter = true,
                    trailingLabel = null,
                )
                Text("Private note for your plan", style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
            }
        }
    }
}
