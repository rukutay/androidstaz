package dk.careerexplore.poc

import dk.careerexplore.poc.data.ApiConfig
import dk.careerexplore.poc.data.CareerApi
import dk.careerexplore.poc.data.JourneyRepository
import dk.careerexplore.poc.data.KeyValueStore
import dk.careerexplore.poc.data.KtorCareerApi
import dk.careerexplore.poc.data.Session
import dk.careerexplore.poc.domain.RegisterRequest
import dk.careerexplore.poc.domain.User

/** Composition root: wires API, local store, session and repository. UI layers receive this. */
class AppContainer(
    val store: KeyValueStore,
    val baseUrl: String = ApiConfig.EMULATOR_BASE_URL,
    val api: CareerApi = KtorCareerApi(baseUrl),
) {
    val session = Session(store)
    val journeys = JourneyRepository(api, store)

    init {
        session.user()?.let { api.userId = it.id }
    }

    /** Demo "sign in": registers (or re-finds) the user by email and remembers them on the device. */
    suspend fun signIn(name: String, email: String, country: String? = null): User {
        val user = api.register(RegisterRequest(name.trim(), email.trim(), country))
        session.save(user)
        api.userId = user.id
        return user
    }

    fun signOut() {
        session.clear()
        journeys.clearLocal()
        api.userId = null
    }
}

/** Things only the platform can do (Android: share sheet, print-to-PDF). */
interface PlatformActions {
    fun shareText(subject: String, text: String)

    /** Opens the system print dialog for [html]; "Save as PDF" there produces the PDF export. */
    fun printHtml(jobName: String, html: String)
}
