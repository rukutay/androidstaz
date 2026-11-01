package dk.careerexplore.poc.data

import dk.careerexplore.poc.domain.DailyPrompt
import dk.careerexplore.poc.domain.Insights
import dk.careerexplore.poc.domain.Journey
import dk.careerexplore.poc.domain.Plan
import dk.careerexplore.poc.domain.PlanVersion
import dk.careerexplore.poc.domain.ReflectQuestions
import dk.careerexplore.poc.domain.RegisterRequest
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.User

/** Server returned a non-2xx answer. Transport failures (no network, timeout) are NOT wrapped: any other exception means "offline". */
class ApiException(val status: Int, val error: String, override val message: String) : Exception(message)

/** The /api/v1 contract (openapi.yaml). Replaceable by a fake in tests. */
interface CareerApi {
    /** Demo auth stub: sent as the X-User-Id header on every call. */
    var userId: String?

    suspend fun register(request: RegisterRequest): User
    suspend fun me(): User
    suspend fun currentJourney(): Journey
    suspend fun createJourney(): Journey

    /** [bodyJson] is the step payload as JSON, so queued offline saves can be replayed as they were. */
    suspend fun saveStep(journeyId: String, key: StepKey, bodyJson: String): Journey

    suspend fun reflect(journeyId: String): ReflectQuestions
    suspend fun generatePlan(journeyId: String): Plan
    suspend fun plan(journeyId: String, version: Int? = null): Plan
    suspend fun plans(journeyId: String): List<PlanVersion>
    suspend fun exportPlanHtml(journeyId: String, version: Int? = null): String
    suspend fun insights(journeyId: String): Insights
    suspend fun dailyPrompt(): DailyPrompt
}

object ApiConfig {
    /** Android emulator alias for the host machine's localhost. A physical device needs the PC's LAN IP instead. */
    const val EMULATOR_BASE_URL = "http://10.0.2.2:3000/api/v1"
}
