package dk.careerexplore.poc.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.domain.Rules
import dk.careerexplore.poc.nav.Navigator
import dk.careerexplore.poc.nav.Screen
import dk.careerexplore.poc.nav.Tab
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.ui.components.Badge
import dk.careerexplore.poc.ui.components.PrimaryButton
import dk.careerexplore.poc.ui.components.SecondaryButton
import dk.careerexplore.poc.ui.components.StepProgressBar

/** Top bar from the Stitch screens: app mark + title, avatar opens Profile. Handles the status bar inset. */
@Composable
fun CeTopBar(title: String, initial: String?, onAvatarClick: () -> Unit) {
    Surface(color = Brand.Sand) {
        Column(Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
            Row(
                Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Explore, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.headlineSmall, color = Brand.NearBlack, modifier = Modifier.weight(1f))
                Surface(
                    shape = CircleShape,
                    color = Brand.CareerBrown,
                    modifier = Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onAvatarClick),
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(initial ?: "?", style = MaterialTheme.typography.titleMedium, color = Brand.White)
                    }
                }
            }
        }
    }
}

private data class NavItem(val tab: Tab, val screen: Screen, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Tab.HOME, Screen.HOME, "Home", Icons.Outlined.Home),
    NavItem(Tab.JOURNEY, Screen.JOURNEY, "Journey", Icons.Outlined.Route),
    NavItem(Tab.PLAN, Screen.PLAN, "Plan", Icons.AutoMirrored.Outlined.Assignment),
    NavItem(Tab.INSIGHTS, Screen.INSIGHTS, "Insights", Icons.Outlined.Lightbulb),
    NavItem(Tab.PROFILE, Screen.PROFILE, "Profile", Icons.Outlined.AccountCircle),
)

/** Bottom navigation (Guidelines 2.1). My Plan unlocks after Step 1, Insights after Step 2. */
@Composable
fun BottomNav(app: AppState, nav: Navigator) {
    Column {
        HorizontalDivider(color = Brand.BorderSubtle)
        NavigationBar(containerColor = Brand.White, tonalElevation = 0.dp) {
            navItems.forEach { item ->
                val locked = when (item.tab) {
                    Tab.PLAN -> !Rules.isPlanUnlocked(app.steps)
                    Tab.INSIGHTS -> !Rules.isInsightsUnlocked(app.steps)
                    else -> false
                }
                NavigationBarItem(
                    selected = nav.current.tab == item.tab,
                    onClick = {
                        when {
                            locked && item.tab == Tab.PLAN -> app.message("Complete Step 1 to unlock your plan.")
                            locked -> app.message("Complete Step 2 to unlock your insights.")
                            else -> nav.openTab(item.screen)
                        }
                    },
                    icon = { Icon(if (locked) Icons.Outlined.Lock else item.icon, contentDescription = null) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Brand.CareerBrown,
                        selectedTextColor = Brand.CareerBrown,
                        indicatorColor = Brand.SandMid,
                        unselectedIconColor = if (locked) Brand.MidGray else Brand.DarkGray,
                        unselectedTextColor = if (locked) Brand.MidGray else Brand.DarkGray,
                    ),
                )
            }
        }
    }
}

/** Scrollable page body with the standard 16dp gutter and 24dp section spacing. */
@Composable
fun PageColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        content()
        Spacer(Modifier.height(24.dp))
    }
}

/** Page title (Playfair) with an optional supporting paragraph. */
@Composable
fun PageTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.displaySmall, color = Brand.NearBlack)
        subtitle?.let { Text(it, style = MaterialTheme.typography.bodyLarge, color = Brand.DarkGray) }
    }
}

/**
 * Layout shared by the four steps (Guidelines S-06..S-11): step header with progress, save status and estimated
 * time, title and instruction, scrollable content, and a sticky Back / primary action bar.
 */
@Composable
fun StepScreen(
    app: AppState,
    stepLabel: String,
    minutes: Int,
    progress: Float,
    title: String,
    instruction: String,
    onBack: () -> Unit,
    onPrimary: () -> Unit,
    primaryText: String = "Save & Continue",
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().imePadding()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stepLabel, style = MaterialTheme.typography.labelMedium, color = Brand.CareerBrownPressed, modifier = Modifier.weight(1f))
                    app.saveStatus?.let {
                        Icon(
                            if (app.offline) Icons.Outlined.CloudOff else Icons.Outlined.CloudDone,
                            contentDescription = null,
                            tint = Brand.DarkGray,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(it, style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
                        Spacer(Modifier.width(8.dp))
                    }
                    Badge("~$minutes min", container = Brand.SandMid, content = Brand.DarkGray, icon = Icons.Outlined.Schedule)
                }
                StepProgressBar(progress)
            }
            PageTitle(title, instruction)
            content()
            Spacer(Modifier.height(8.dp))
        }
        Surface(color = Brand.Sand, border = BorderStroke(0.dp, Brand.Sand)) {
            Row(
                Modifier.fillMaxWidth().background(Brand.Sand).padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SecondaryButton("Back", onBack, Modifier.weight(0.38f), leadingIcon = Icons.AutoMirrored.Outlined.ArrowBack)
                PrimaryButton(primaryText, onPrimary, Modifier.weight(0.62f), trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward)
            }
        }
    }
}

/** Section header used inside pages ("Your Collection", "Journey Milestones"). */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, trailing: @Composable () -> Unit = {}) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Brand.NearBlack, modifier = Modifier.weight(1f))
        trailing()
    }
}
