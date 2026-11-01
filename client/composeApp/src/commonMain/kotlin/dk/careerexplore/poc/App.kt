package dk.careerexplore.poc

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.nav.Navigator
import dk.careerexplore.poc.nav.Screen
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.theme.CareerExploreTheme
import dk.careerexplore.poc.ui.AppState
import dk.careerexplore.poc.ui.BottomNav
import dk.careerexplore.poc.ui.CeTopBar
import dk.careerexplore.poc.ui.components.InfoNote
import dk.careerexplore.poc.ui.components.PrimaryButton
import dk.careerexplore.poc.ui.screens.ChallengesScreen
import dk.careerexplore.poc.ui.screens.CoachScreen
import dk.careerexplore.poc.ui.screens.ExcitedScreen
import dk.careerexplore.poc.ui.screens.HomeScreen
import dk.careerexplore.poc.ui.screens.InsightsScreen
import dk.careerexplore.poc.ui.screens.InterestsScreen
import dk.careerexplore.poc.ui.screens.JourneyScreen
import dk.careerexplore.poc.ui.screens.MyBestScreen
import dk.careerexplore.poc.ui.screens.OnboardingScreen
import dk.careerexplore.poc.ui.screens.PlanScreen
import dk.careerexplore.poc.ui.screens.ProfileScreen
import dk.careerexplore.poc.ui.screens.ProfileSetupScreen
import dk.careerexplore.poc.ui.screens.ValuesScreen
import dk.careerexplore.poc.ui.screens.WantsScreen

/**
 * Root composable. [backHandler] is supplied by the platform (Android: activity-compose BackHandler) so the
 * system Back button walks the in-app back stack.
 */
@Composable
fun App(
    container: AppContainer,
    platform: PlatformActions,
    backHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit,
) {
    CareerExploreTheme {
        val scope = rememberCoroutineScope()
        val app = remember { AppState(container, platform, scope) }
        val nav = remember {
            Navigator(
                when {
                    app.user != null -> Screen.HOME
                    app.onboardingDone -> Screen.PROFILE_SETUP
                    else -> Screen.ONBOARDING
                },
            )
        }
        backHandler(nav.canGoBack) { nav.back() }
        LaunchedEffect(app.user?.id) { if (app.user != null) app.load() }

        val screen = nav.current
        Scaffold(
            containerColor = Brand.Sand,
            topBar = {
                if (screen.tab != null) {
                    CeTopBar(topBarTitle(screen), app.user?.name?.take(1)?.uppercase()) { nav.openTab(Screen.PROFILE) }
                }
            },
            bottomBar = { if (screen.tab != null) BottomNav(app, nav) },
            // On step screens keep messages above the sticky Back / Continue bar so they never cover it
            snackbarHost = { SnackbarHost(app.snackbar, Modifier.padding(bottom = if (screen in stepScreens) 72.dp else 0.dp)) },
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                val needsJourney = screen.tab != null
                when {
                    needsJourney && app.journey == null && app.loading ->
                        CircularProgressIndicator(color = Brand.CareerBrown, modifier = Modifier.align(Alignment.Center))
                    needsJourney && app.journey == null -> ConnectionProblem(app)
                    else -> AnimatedContent(
                        targetState = screen,
                        transitionSpec = {
                            val dir = if (nav.forward) 1 else -1
                            (slideInHorizontally(tween(280)) { it * dir / 4 } + fadeIn(tween(280)))
                                .togetherWith(slideOutHorizontally(tween(280)) { -it * dir / 4 } + fadeOut(tween(200)))
                        },
                        label = "screen",
                    ) { target -> ScreenContent(target, app, nav) }
                }
            }
        }
    }
}

private val stepScreens = setOf(Screen.INTERESTS, Screen.MY_BEST, Screen.VALUES, Screen.EXCITED, Screen.WANTS, Screen.CHALLENGES)

private fun topBarTitle(screen: Screen): String = when (screen) {
    Screen.HOME -> "Home"
    Screen.PLAN, Screen.COACH -> "My Plan"
    Screen.INSIGHTS -> "Insights"
    Screen.PROFILE -> "Profile"
    else -> "My Journey"
}

@Composable
private fun ScreenContent(screen: Screen, app: AppState, nav: Navigator) {
    when (screen) {
        Screen.ONBOARDING -> OnboardingScreen(app, nav)
        Screen.PROFILE_SETUP -> ProfileSetupScreen(app, nav)
        Screen.HOME -> HomeScreen(app, nav)
        Screen.JOURNEY -> JourneyScreen(app, nav)
        Screen.INTERESTS -> InterestsScreen(app, nav)
        Screen.MY_BEST -> MyBestScreen(app, nav)
        Screen.VALUES -> ValuesScreen(app, nav)
        Screen.EXCITED -> ExcitedScreen(app, nav)
        Screen.WANTS -> WantsScreen(app, nav)
        Screen.CHALLENGES -> ChallengesScreen(app, nav)
        Screen.PLAN -> PlanScreen(app, nav)
        Screen.INSIGHTS -> InsightsScreen(app, nav)
        Screen.COACH -> CoachScreen(app, nav)
        Screen.PROFILE -> ProfileScreen(app, nav)
    }
}

/** First start without a server and without cached data: explain and offer a retry. */
@Composable
private fun ConnectionProblem(app: AppState) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
        InfoNote(
            Icons.Outlined.CloudOff,
            app.loadError ?: "We couldn’t load your journey.",
            title = "No connection",
        ) {
            Text("Start the local server (cd server; npm run dev) and try again.")
        }
        PrimaryButton("Try again", { app.launch { app.load() } }, leadingIcon = Icons.Outlined.Refresh)
    }
}
