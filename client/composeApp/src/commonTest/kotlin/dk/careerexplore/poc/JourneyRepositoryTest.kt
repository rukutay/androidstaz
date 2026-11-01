package dk.careerexplore.poc

import dk.careerexplore.poc.data.ApiException
import dk.careerexplore.poc.data.AppJson
import dk.careerexplore.poc.data.CareerApi
import dk.careerexplore.poc.data.InMemoryKeyValueStore
import dk.careerexplore.poc.data.JourneyRepository
import dk.careerexplore.poc.data.SaveOutcome
import dk.careerexplore.poc.domain.DailyPrompt
import dk.careerexplore.poc.domain.Insights
import dk.careerexplore.poc.domain.InterestsStep
import dk.careerexplore.poc.domain.Journey
import dk.careerexplore.poc.domain.Plan
import dk.careerexplore.poc.domain.PlanVersion
import dk.careerexplore.poc.domain.ReflectQuestions
import dk.careerexplore.poc.domain.RegisterRequest
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.User
import dk.careerexplore.poc.domain.ValuesStep
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/** In-memory server: [online] = false simulates airplane mode. */
private class FakeApi(var serverTime: Instant) : CareerApi {
    override var userId: String? = "u1"
    var online = true
    var journey = Journey("j1", 1, "in_progress", serverTime.toString())
    val saved = mutableListOf<Pair<StepKey, String>>()

    // Any non-ApiException counts as a transport failure for the repository
    private fun net() { if (!online) throw IllegalStateException("offline") }

    override suspend fun currentJourney(): Journey { net(); return journey }

    override suspend fun saveStep(journeyId: String, key: StepKey, bodyJson: String): Journey {
        net()
        if (key == StepKey.VALUES && AppJson.decodeFromString<ValuesStep>(bodyJson).selected.size > 3) {
            throw ApiException(400, "validation_error", "Invalid request body.")
        }
        saved += key to bodyJson
        val steps = when (key) {
            StepKey.INTERESTS -> journey.steps.copy(interests = AppJson.decodeFromString(bodyJson))
            StepKey.VALUES -> journey.steps.copy(values = AppJson.decodeFromString(bodyJson))
            else -> journey.steps
        }
        journey = journey.copy(steps = steps, updatedAt = serverTime.toString())
        return journey
    }

    override suspend fun register(request: RegisterRequest): User = error("unused")
    override suspend fun me(): User = error("unused")
    override suspend fun createJourney(): Journey = error("unused")
    override suspend fun reflect(journeyId: String): ReflectQuestions = error("unused")
    override suspend fun generatePlan(journeyId: String): Plan = error("unused")
    override suspend fun plan(journeyId: String, version: Int?): Plan = error("unused")
    override suspend fun plans(journeyId: String): List<PlanVersion> = error("unused")
    override suspend fun exportPlanHtml(journeyId: String, version: Int?): String = error("unused")
    override suspend fun insights(journeyId: String): Insights = error("unused")
    override suspend fun dailyPrompt(): DailyPrompt = error("unused")
}

class JourneyRepositoryTest {
    private val t0 = Instant.parse("2026-11-01T12:00:00Z")
    private fun interests(vararg items: String) = AppJson.encodeToString(InterestsStep(items.toList()))

    @Test
    fun offlineEditIsCachedQueuedAndReplayed() = runTest {
        val api = FakeApi(t0)
        var clock = t0
        val repo = JourneyRepository(api, InMemoryKeyValueStore()) { clock }
        repo.load()

        api.online = false
        clock = t0 + 30.minutes
        assertEquals(SaveOutcome.QueuedOffline, repo.saveStep("j1", StepKey.INTERESTS, interests("Photography")))
        assertEquals(1, repo.pendingCount())

        val offline = repo.load()
        assertTrue(offline.offline)
        assertEquals(listOf("Photography"), offline.journey.steps.interests!!.items) // local copy survives

        api.online = true
        val online = repo.load()
        assertEquals(0, online.pendingCount)
        assertEquals(listOf("Photography"), api.journey.steps.interests!!.items)
    }

    @Test
    fun queuedEditIsDroppedWhenServerIsNewerByMoreThanFiveMinutes() = runTest {
        val api = FakeApi(t0)
        val repo = JourneyRepository(api, InMemoryKeyValueStore()) { t0 }
        repo.load()
        api.online = false
        repo.saveStep("j1", StepKey.INTERESTS, interests("Local edit"))

        // meanwhile the journey was saved elsewhere 10 minutes later
        api.online = true
        api.journey = api.journey.copy(updatedAt = (t0 + 10.minutes).toString())
        val result = repo.load()

        assertEquals(0, result.pendingCount)
        assertTrue(api.saved.isEmpty())
    }

    @Test
    fun serverRejectionIsReportedNotQueued() = runTest {
        val api = FakeApi(t0)
        val repo = JourneyRepository(api, InMemoryKeyValueStore()) { t0 }
        repo.load()
        val outcome = repo.saveStep("j1", StepKey.VALUES, AppJson.encodeToString(ValuesStep(listOf("a", "b", "c", "d"))))
        assertIs<SaveOutcome.Rejected>(outcome)
        assertEquals(0, repo.pendingCount())
    }

    @Test
    fun newerQueuedSaveReplacesOlderOneForSameStep() = runTest {
        val api = FakeApi(t0)
        val repo = JourneyRepository(api, InMemoryKeyValueStore()) { t0 }
        repo.load()
        api.online = false
        repo.saveStep("j1", StepKey.INTERESTS, interests("first"))
        repo.saveStep("j1", StepKey.INTERESTS, interests("second"))
        assertEquals(1, repo.pendingCount())

        api.online = true
        repo.flush()
        assertEquals(listOf(StepKey.INTERESTS to interests("second")), api.saved)
    }
}
