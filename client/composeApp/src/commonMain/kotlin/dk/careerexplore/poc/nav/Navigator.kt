package dk.careerexplore.poc.nav

import androidx.compose.runtime.mutableStateListOf

enum class Tab { HOME, JOURNEY, PLAN, INSIGHTS, PROFILE }

/** All destinations (Guidelines 2.3 screen inventory, POC subset). */
enum class Screen(val tab: Tab?) {
    ONBOARDING(null),
    PROFILE_SETUP(null),
    HOME(Tab.HOME),
    JOURNEY(Tab.JOURNEY),
    INTERESTS(Tab.JOURNEY),
    MY_BEST(Tab.JOURNEY),
    VALUES(Tab.JOURNEY),
    EXCITED(Tab.JOURNEY),
    WANTS(Tab.JOURNEY),
    CHALLENGES(Tab.JOURNEY),
    PLAN(Tab.PLAN),
    INSIGHTS(Tab.INSIGHTS),
    COACH(Tab.PLAN),
    PROFILE(Tab.PROFILE),
}

/** Minimal back-stack navigator (no navigation library needed for 14 screens). */
class Navigator(start: Screen) {
    private val stack = mutableStateListOf(start)

    val current: Screen get() = stack.last()
    val canGoBack: Boolean get() = stack.size > 1

    /** Slide direction for the transition: true when the last move was forward. */
    var forward: Boolean = true
        private set

    fun push(screen: Screen) {
        if (screen == current) return
        forward = true
        stack.add(screen)
    }

    fun back() {
        if (!canGoBack) return
        forward = false
        stack.removeAt(stack.lastIndex)
    }

    /** Bottom-nav switch: Home is always the root, so Back from a tab returns Home. */
    fun openTab(screen: Screen) {
        forward = true
        stack.clear()
        stack.add(Screen.HOME)
        if (screen != Screen.HOME) stack.add(screen)
    }

    fun resetTo(screen: Screen) {
        forward = true
        stack.clear()
        stack.add(screen)
    }
}
