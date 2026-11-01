package dk.careerexplore.poc.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTOs matching openapi.yaml and the server Zod schemas (server/src/schemas/steps.ts).

@Serializable
data class User(val id: String, val name: String, val email: String, val country: String? = null)

@Serializable
data class RegisterRequest(val name: String, val email: String, val country: String? = null)

@Serializable
data class InterestsStep(val items: List<String> = emptyList(), val topTwo: List<String> = emptyList())

@Serializable
data class BestExample(
    val text: String,
    val reasons: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

@Serializable
data class BestStep(val examples: List<BestExample> = emptyList())

@Serializable
data class ValuesStep(val selected: List<String> = emptyList(), val reflection: String = "")

@Serializable
enum class Importance {
    @SerialName("important") IMPORTANT,
    @SerialName("somewhat") SOMEWHAT,
    @SerialName("not") NOT,
}

@Serializable
data class WantElement(val text: String = "", val importance: Importance = Importance.IMPORTANT)

@Serializable
data class JobWant(
    val role: WantElement = WantElement(),
    val company: WantElement = WantElement(),
    val location: WantElement = WantElement(),
    val culture: WantElement = WantElement(),
    val benefit: WantElement = WantElement(),
)

@Serializable
enum class Timeframe(val label: String) {
    @SerialName("2 weeks") TWO_WEEKS("2 weeks"),
    @SerialName("1 month") ONE_MONTH("1 month"),
    @SerialName("3 months") THREE_MONTHS("3 months"),
}

@Serializable
data class Objective(
    val timeframe: Timeframe = Timeframe.THREE_MONTHS,
    val action: String = "",
    val outcome: String = "",
    val challenges: List<String> = emptyList(),
    val resources: List<String> = emptyList(),
)

@Serializable
data class ExcitedStep(
    val excitementItems: List<String> = emptyList(),
    val jobWants: List<JobWant> = emptyList(),
    val objectives: List<Objective> = emptyList(),
)

@Serializable
data class JourneySteps(
    val interests: InterestsStep? = null,
    val best: BestStep? = null,
    val values: ValuesStep? = null,
    val excited: ExcitedStep? = null,
)

@Serializable
data class Journey(
    val id: String,
    val version: Int,
    val status: String,
    val updatedAt: String,
    val steps: JourneySteps = JourneySteps(),
)

@Serializable
data class PlanInterests(val items: List<String> = emptyList(), val topTwo: List<String> = emptyList())

@Serializable
data class BestMoment(val headline: String, val topReason: String? = null)

@Serializable
data class PlanValues(val selected: List<String> = emptyList(), val reflection: String = "")

@Serializable
data class PlanData(
    val userName: String,
    val interests: PlanInterests,
    val bestMoments: List<BestMoment> = emptyList(),
    val values: PlanValues,
    val excitement: List<String> = emptyList(),
    val jobWants: List<JobWant> = emptyList(),
    val objectives: List<Objective> = emptyList(),
)

@Serializable
data class Plan(val journeyId: String, val version: Int, val generatedAt: String, val data: PlanData)

@Serializable
data class PlanVersion(val version: Int, val generatedAt: String)

@Serializable
data class Keyword(val word: String, val count: Int)

@Serializable
data class Insights(val keywords: List<Keyword> = emptyList(), val disclaimer: String = "")

@Serializable
data class DailyPrompt(val id: String, val text: String, val category: String)

@Serializable
data class ReflectQuestions(val questions: List<String> = emptyList())

@Serializable
data class ApiError(val error: String = "", val message: String = "")

/** The four steps as the API names them: PUT /journey/{id}/{key}. */
enum class StepKey(val path: String) { INTERESTS("interests"), BEST("best"), VALUES("values"), EXCITED("excited") }
