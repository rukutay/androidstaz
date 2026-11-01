package dk.careerexplore.poc.data

import dk.careerexplore.poc.domain.ApiError
import dk.careerexplore.poc.domain.DailyPrompt
import dk.careerexplore.poc.domain.Insights
import dk.careerexplore.poc.domain.Journey
import dk.careerexplore.poc.domain.Plan
import dk.careerexplore.poc.domain.PlanVersion
import dk.careerexplore.poc.domain.ReflectQuestions
import dk.careerexplore.poc.domain.RegisterRequest
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.User
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val AppJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

fun HttpClientConfig<*>.installApiPlugins() {
    install(ContentNegotiation) { json(AppJson) }
    install(HttpTimeout) {
        connectTimeoutMillis = 5_000
        requestTimeoutMillis = 15_000
    }
}

/** Platform HTTP client (OkHttp on Android). */
expect fun createHttpClient(): HttpClient

class KtorCareerApi(
    private val baseUrl: String,
    private val client: HttpClient = createHttpClient(),
) : CareerApi {
    override var userId: String? = null

    private fun HttpRequestBuilder.auth() {
        userId?.let { header("X-User-Id", it) }
    }

    private suspend fun HttpResponse.checked(): HttpResponse {
        if (status.isSuccess()) return this
        val text = bodyAsText()
        val parsed = runCatching { AppJson.decodeFromString<ApiError>(text) }.getOrNull()
        throw ApiException(status.value, parsed?.error.orEmpty(), parsed?.message?.ifBlank { null } ?: "Request failed (${status.value}).")
    }

    private fun url(path: String) = baseUrl.trimEnd('/') + path

    override suspend fun register(request: RegisterRequest): User =
        client.post(url("/auth/register")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.checked().body()

    override suspend fun me(): User = client.get(url("/user/me")) { auth() }.checked().body()

    override suspend fun currentJourney(): Journey = client.get(url("/journey/current")) { auth() }.checked().body()

    override suspend fun createJourney(): Journey = client.post(url("/journey")) { auth() }.checked().body()

    override suspend fun saveStep(journeyId: String, key: StepKey, bodyJson: String): Journey =
        client.put(url("/journey/$journeyId/${key.path}")) {
            auth()
            contentType(ContentType.Application.Json)
            setBody(bodyJson)
        }.checked().body()

    override suspend fun reflect(journeyId: String): ReflectQuestions =
        client.post(url("/journey/$journeyId/best/reflect")) { auth() }.checked().body()

    override suspend fun generatePlan(journeyId: String): Plan =
        client.post(url("/journey/$journeyId/plan/generate")) { auth() }.checked().body()

    override suspend fun plan(journeyId: String, version: Int?): Plan =
        client.get(url("/journey/$journeyId/plan")) {
            auth()
            version?.let { url.parameters.append("version", it.toString()) }
        }.checked().body()

    override suspend fun plans(journeyId: String): List<PlanVersion> =
        client.get(url("/journey/$journeyId/plans")) { auth() }.checked().body()

    override suspend fun exportPlanHtml(journeyId: String, version: Int?): String =
        client.post(url("/journey/$journeyId/plan/export")) {
            auth()
            version?.let { url.parameters.append("version", it.toString()) }
        }.checked().bodyAsText()

    override suspend fun insights(journeyId: String): Insights =
        client.get(url("/insights/$journeyId")) { auth() }.checked().body()

    override suspend fun dailyPrompt(): DailyPrompt = client.get(url("/prompts/daily")) { auth() }.checked().body()
}
