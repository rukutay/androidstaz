package dk.careerexplore.poc

import dk.careerexplore.poc.data.ApiException
import dk.careerexplore.poc.data.KtorCareerApi
import dk.careerexplore.poc.data.installApiPlugins
import dk.careerexplore.poc.domain.Importance
import dk.careerexplore.poc.domain.RegisterRequest
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.Timeframe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KtorCareerApiTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    private fun apiWith(routes: Map<String, Pair<HttpStatusCode, String>>): Pair<KtorCareerApi, MockEngine> {
        val engine = MockEngine { request ->
            val key = "${request.method.value} ${request.url.encodedPath}"
            val (status, body) = routes[key] ?: (HttpStatusCode.NotFound to """{"error":"not_found","message":"no route $key"}""")
            respond(body, status, jsonHeaders)
        }
        return KtorCareerApi("http://test/api/v1", HttpClient(engine) { installApiPlugins() }) to engine
    }

    @Test
    fun decodesRealServerPayloads() = runTest {
        val j = "ff4970a3-91e9-4597-b6e2-4ef888fd458d"
        val (api, _) = apiWith(
            mapOf(
                "POST /api/v1/auth/register" to (HttpStatusCode.Created to Fixtures.USER),
                "GET /api/v1/journey/current" to (HttpStatusCode.OK to Fixtures.JOURNEY_EMPTY),
                "PUT /api/v1/journey/$j/excited" to (HttpStatusCode.OK to Fixtures.JOURNEY_FULL),
                "POST /api/v1/journey/$j/plan/generate" to (HttpStatusCode.Created to Fixtures.PLAN),
                "GET /api/v1/journey/$j/plans" to (HttpStatusCode.OK to Fixtures.PLANS),
                "GET /api/v1/insights/$j" to (HttpStatusCode.OK to Fixtures.INSIGHTS),
                "GET /api/v1/prompts/daily" to (HttpStatusCode.OK to Fixtures.PROMPT),
            ),
        )
        assertEquals("Fixture User", api.register(RegisterRequest("Fixture User", "f@example.test")).name)

        val empty = api.currentJourney()
        assertNull(empty.steps.interests)

        val full = api.saveStep(j, StepKey.EXCITED, "{}")
        val excited = full.steps.excited!!
        assertEquals(Importance.NOT, excited.jobWants.single().location.importance)
        assertEquals(Timeframe.ONE_MONTH, excited.objectives.single().timeframe)

        val plan = api.generatePlan(j)
        assertEquals("I helped others", plan.data.bestMoments.single().topReason)
        assertEquals(1, api.plans(j).single().version)
        assertEquals("teaching", api.insights(j).keywords.first().word)
        assertTrue(api.dailyPrompt().text.isNotBlank())
    }

    @Test
    fun sendsUserHeaderAndStepJson() = runTest {
        val (api, engine) = apiWith(mapOf("PUT /api/v1/journey/abc/values" to (HttpStatusCode.OK to Fixtures.JOURNEY_FULL)))
        api.userId = "user-1"
        api.saveStep("abc", StepKey.VALUES, """{"selected":["Variety"],"reflection":""}""")

        val request = engine.requestHistory.single()
        assertEquals(HttpMethod.Put, request.method)
        assertEquals("user-1", request.headers["X-User-Id"])
        assertEquals("""{"selected":["Variety"],"reflection":""}""", (request.body as TextContent).text)
    }

    @Test
    fun mapsServerErrorsToApiException() = runTest {
        val (api, _) = apiWith(mapOf("PUT /api/v1/journey/abc/values" to (HttpStatusCode.BadRequest to Fixtures.VALIDATION_ERROR)))
        val e = assertFailsWith<ApiException> { api.saveStep("abc", StepKey.VALUES, "{}") }
        assertEquals(400, e.status)
        assertEquals("validation_error", e.error)
    }
}
