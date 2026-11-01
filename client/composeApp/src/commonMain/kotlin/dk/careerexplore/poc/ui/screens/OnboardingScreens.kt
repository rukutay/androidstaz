package dk.careerexplore.poc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.Rules
import dk.careerexplore.poc.nav.Navigator
import dk.careerexplore.poc.nav.Screen
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.ui.AppState
import dk.careerexplore.poc.ui.PageColumn
import dk.careerexplore.poc.ui.PageTitle
import dk.careerexplore.poc.ui.components.Badge
import dk.careerexplore.poc.ui.components.CeCard
import dk.careerexplore.poc.ui.components.CeTextField
import dk.careerexplore.poc.ui.components.GhostButton
import dk.careerexplore.poc.ui.components.IllustrationBox
import dk.careerexplore.poc.ui.components.InfoNote
import dk.careerexplore.poc.ui.components.Overline
import dk.careerexplore.poc.ui.components.PrimaryButton
import kotlinx.coroutines.launch

private val onboardingIcons = listOf(Icons.Outlined.SelfImprovement, Icons.Outlined.Lightbulb, Icons.Outlined.Flag)

/** S-01..S-03: three swipeable cards, Skip top-right, "Get Started" on the last card. */
@Composable
fun OnboardingScreen(app: AppState, nav: Navigator) {
    val pager = rememberPagerState { Content.onboarding.size }
    val scope = rememberCoroutineScope()
    fun finish() {
        app.onboardingDone = true
        nav.resetTo(Screen.PROFILE_SETUP)
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(Content.TAGLINE, style = MaterialTheme.typography.labelMedium, color = Brand.CareerBrown, modifier = Modifier.weight(1f))
            GhostButton("Skip", ::finish)
        }
        HorizontalPager(pager, Modifier.weight(1f).padding(vertical = 8.dp), pageSpacing = 16.dp, verticalAlignment = Alignment.Top) { page ->
            val card = Content.onboarding[page]
            CeCard(padding = 0.dp, color = Brand.White) {
                IllustrationBox(onboardingIcons[page], height = 300.dp) {
                    Badge("Chapter 0${page + 1}", container = Brand.White, icon = onboardingIcons[page])
                }
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Overline(Content.onboardingOverlines[page])
                    Text(card.headline, style = MaterialTheme.typography.displaySmall, color = Brand.NearBlack)
                    Text(card.body, style = MaterialTheme.typography.bodyLarge, color = Brand.DarkGray)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.Center) {
            repeat(Content.onboarding.size) { i ->
                val active = i == pager.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(if (active) 24.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (active) Brand.CareerBrown else Brand.BorderSubtle)
                        .clickable { scope.launch { pager.animateScrollToPage(i) } },
                )
            }
        }
        val last = pager.currentPage == Content.onboarding.lastIndex
        PrimaryButton(
            text = if (last) "Get Started" else "Next",
            onClick = { if (last) finish() else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward,
        )
    }
}

/** S-04 (POC): name + email, optional country, no password. */
@Composable
fun ProfileSetupScreen(app: AppState, nav: Navigator) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var country by remember { mutableStateOf<String?>(null) }
    var countryOpen by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    fun submit() {
        val n = Rules.registerName(name)
        val e = Rules.registerEmail(email)
        nameError = n.message
        emailError = e.message
        if (!n.ok || !e.ok) return
        busy = true
        app.launch {
            val error = app.signIn(name, email, country)
            busy = false
            if (error == null) nav.resetTo(Screen.HOME) else app.message(error)
        }
    }

    PageColumn {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(18.dp))
            Text("Your journal", style = MaterialTheme.typography.labelMedium, color = Brand.CareerBrown)
        }
        PageTitle("Let’s get to know you", "We only ask for what we need.")
        CeTextField(
            value = name,
            onValueChange = { name = it; nameError = null },
            label = "First name *",
            placeholder = "e.g. Maya",
            maxLength = 80,
            error = nameError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        CeTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = "Email address *",
            placeholder = "name@domain.com",
            error = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
        )
        Column {
            Row(Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                Text("Country / region", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text("Optional", style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
            }
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Brand.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand.BorderInput),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).clip(RoundedCornerShape(8.dp)).clickable { countryOpen = true },
                ) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(country ?: "Select your current location", style = MaterialTheme.typography.bodyLarge, color = if (country == null) Brand.MidGray else Brand.NearBlack, modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = Brand.DarkGray)
                    }
                }
                DropdownMenu(expanded = countryOpen, onDismissRequest = { countryOpen = false }) {
                    Content.countries.forEach { c ->
                        DropdownMenuItem(text = { Text(c) }, onClick = { country = c; countryOpen = false })
                    }
                }
            }
        }
        InfoNote(
            Icons.Outlined.Shield,
            "Proof of concept: your journey is saved on this device and on the local demo server.",
            title = "No password required",
        )
        PrimaryButton(
            if (busy) "Creating…" else "Create my account",
            ::submit,
            Modifier.fillMaxWidth(),
            enabled = !busy,
            trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward,
        )
        Text(
            "Demo version — no password needed. Privacy policy and terms will be linked here.",
            style = MaterialTheme.typography.bodySmall,
            color = Brand.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
    }
}
