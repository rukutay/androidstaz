package dk.careerexplore.poc.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dk.careerexplore.poc.AppContainer
import dk.careerexplore.poc.PlatformActions
import dk.careerexplore.poc.data.ApiException
import dk.careerexplore.poc.data.AppJson
import dk.careerexplore.poc.data.SaveOutcome
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.Journey
import dk.careerexplore.poc.domain.JourneySteps
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

/** App-wide UI state: user, current journey, connectivity and messages. One instance per activity. */
class AppState(
    val container: AppContainer,
    val platform: PlatformActions,
    private val scope: CoroutineScope,
) {
    val snackbar = SnackbarHostState()

    var user: User? by mutableStateOf(container.session.user())
        private set
    var journey: Journey? by mutableStateOf(container.journeys.cachedJourney())
        private set
    var offline by mutableStateOf(false)
        private set
    var loading by mutableStateOf(false)
        private set
    var loadError: String? by mutableStateOf(null)
        private set

    /** Edits saved on the device that have not reached the server yet. */
    var pendingCount by mutableStateOf(container.journeys.pendingCount())
        private set

    /** "Changes saved" / "Saved on this device" status line shown on step screens. */
    var saveStatus: String? by mutableStateOf(null)
        private set

    val steps: JourneySteps get() = journey?.steps ?: JourneySteps()

    var onboardingDone: Boolean
        get() = container.store.get(ONBOARDING_KEY) == "true"
        set(value) = container.store.put(ONBOARDING_KEY, value.toString())

    fun launch(block: suspend CoroutineScope.() -> Unit): Job = scope.launch(block = block)

    /**
     * Starts [block] immediately on the calling thread until its first suspension. Used for "save, then navigate":
     * the local cache and [journey] are updated before the next screen reads them; the network part continues later.
     */
    fun launchNow(block: suspend CoroutineScope.() -> Unit): Job = scope.launch(start = CoroutineStart.UNDISPATCHED, block = block)

    /** Best-effort background fetch (e.g. daily prompt): failures are silent. */
    suspend fun <T> quietly(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    fun message(text: String) {
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(text)
        }
    }

    suspend fun load() {
        loading = true
        try {
            val result = container.journeys.load()
            journey = result.journey
            offline = result.offline
            loadError = null
        } catch (e: ApiException) {
            if (e.status == 401) signOut() else loadError = e.message
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            offline = true
            loadError = "We couldn’t reach the Career Explore server. Is it running?"
        } finally {
            loading = false
            pendingCount = container.journeys.pendingCount()
        }
    }

    /** Returns null on success, or a message to show. */
    suspend fun signIn(name: String, email: String, country: String?): String? = try {
        user = container.signIn(name, email, country)
        load()
        null
    } catch (e: ApiException) {
        e.message
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        "We couldn’t reach the server. Check that it is running and try again."
    }

    fun signOut() {
        container.signOut()
        user = null
        journey = null
        saveStatus = null
        pendingCount = 0
    }

    /** Offline-first save of one step. Returns false only when the server rejected the data. */
    suspend fun <T> saveStep(key: StepKey, value: T, serializer: KSerializer<T>): Boolean {
        val id = journey?.id ?: return false
        val outcome = container.journeys.saveStep(id, key, AppJson.encodeToString(serializer, value)) { journey = it }
        journey = container.journeys.cachedJourney() ?: journey
        pendingCount = container.journeys.pendingCount()
        return when (outcome) {
            SaveOutcome.Synced -> {
                offline = false
                saveStatus = Content.Copy.AUTO_SAVE
                true
            }
            SaveOutcome.QueuedOffline -> {
                if (!offline) message(Content.Copy.OFFLINE_NOTICE)
                offline = true
                saveStatus = "Saved on this device"
                true
            }
            is SaveOutcome.Rejected -> {
                message(outcome.message)
                false
            }
        }
    }

    /** Runs an online-only call (plan, insights, export). Shows a message and returns null on failure. */
    suspend fun <T> online(block: suspend () -> T): T? = try {
        block().also { offline = false }
    } catch (e: ApiException) {
        message(e.message)
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        offline = true
        message("This needs an internet connection. Your journey is safe on this device.")
        null
    }

    fun replaceJourney(newJourney: Journey) {
        journey = newJourney
    }

    private companion object {
        const val ONBOARDING_KEY = "onboarding.done"
    }
}
