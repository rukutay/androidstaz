package dk.careerexplore.poc.domain

/** Client-side step rules (Guidelines S-06..S-11) with the messages from section 8.3. The server only enforces hard limits. */
data class Check(val ok: Boolean, val message: String? = null) {
    companion object {
        val Ok = Check(true)
        fun fail(message: String) = Check(false, message)
    }
}

object Rules {
    const val MAX_INTEREST_LENGTH = 60
    const val MIN_INTERESTS_TO_PROCEED = 5
    const val INTERESTS_GOAL = 20
    const val MAX_BEST_EXAMPLES = 6
    const val RECOMMENDED_BEST_EXAMPLES = 3
    const val MAX_BEST_TEXT = 300
    const val VALUES_REQUIRED = 3
    const val MAX_EXCITEMENT_ITEMS = 10
    const val MIN_EXCITEMENT_TO_PROCEED = 3
    const val MAX_JOB_WANTS = 5
    const val MAX_OBJECTIVES = 3
    const val MAX_CHALLENGES_OR_RESOURCES = 5

    // Step 1
    fun canAddInterest(existing: List<String>, input: String): Check {
        val text = input.trim()
        return when {
            text.isEmpty() -> Check.fail("Type something you’re interested in first.")
            text.length > MAX_INTEREST_LENGTH -> Check.fail("Keep it to $MAX_INTEREST_LENGTH characters or fewer.")
            existing.any { it.equals(text, ignoreCase = true) } -> Check.fail("You’ve already added that one.")
            else -> Check.Ok
        }
    }

    fun canProceedInterests(items: List<String>): Check =
        if (items.size >= MIN_INTERESTS_TO_PROCEED) Check.Ok
        else Check.fail("Add a few more interests to continue - aim for at least 5 to start seeing patterns.")

    fun interestsCounter(count: Int): String =
        if (count >= INTERESTS_GOAL) Content.Copy.STEP1_COUNTER_DONE else Content.Copy.STEP1_COUNTER_LOW

    fun showInterestsPatternNudge(count: Int) = count >= INTERESTS_GOAL

    fun canPickTopTwo(items: List<String>, topTwo: List<String>, candidate: String): Check = when {
        candidate !in items -> Check.fail("Pick from your own list.")
        candidate in topTwo -> Check.Ok // toggling off is always allowed
        topTwo.size >= 2 -> Check.fail("You’ve already picked your top 2. Remove one to change your selection.")
        else -> Check.Ok
    }

    // Step 2
    fun canProceedBest(examples: List<BestExample>): Check =
        if (examples.any { it.text.isNotBlank() && it.reasons.any(String::isNotBlank) }) Check.Ok
        else Check.fail("Share at least one moment when you were at your best to continue.")

    /** Soft gate: shown as a gentle nudge, not a blocker. */
    fun bestNudge(examples: List<BestExample>): String? =
        if (examples.size < RECOMMENDED_BEST_EXAMPLES) "Three moments give a clearer picture - add more if you can." else null

    // Step 3
    fun canToggleValue(selected: List<String>, value: String): Check =
        if (value in selected || selected.size < VALUES_REQUIRED) Check.Ok
        else Check.fail("You’ve already chosen your 3 must-haves. Remove one to change your selection.")

    fun canProceedValues(selected: List<String>): Check =
        if (selected.size == VALUES_REQUIRED) Check.Ok else Check.fail("Choose exactly 3 values to continue.")

    fun valuesReflectionPrompt(selected: List<String>): String? =
        if (selected.size == VALUES_REQUIRED) {
            "If a role can’t offer ${selected[0]}, ${selected[1]}, and ${selected[2]} - how will that affect your happiness at work?"
        } else null

    fun valuesCounter(selected: List<String>) = "${selected.size} / $VALUES_REQUIRED selected"

    // Step 4a
    fun canProceedExcited(items: List<String>): Check =
        if (items.count(String::isNotBlank) >= MIN_EXCITEMENT_TO_PROCEED) Check.Ok
        else Check.fail("Add at least 3 things that excite you - these will shape your objectives.")

    fun showExcitedPatternNudge(items: List<String>) = items.count(String::isNotBlank) >= 5

    // Step 4b
    fun isObjectiveComplete(o: Objective) = o.action.isNotBlank() && o.outcome.isNotBlank()

    fun canProceedObjectives(objectives: List<Objective>): Check =
        if (objectives.isNotEmpty() && objectives.all(::isObjectiveComplete)) Check.Ok
        else Check.fail("Add at least one objective with an action and a measurable outcome.")

    // Step 4c
    fun canAddChallengeOrResource(existing: List<String>, input: String): Check = when {
        input.isBlank() -> Check.fail("Type something first.")
        existing.size >= MAX_CHALLENGES_OR_RESOURCES -> Check.fail("You can add up to $MAX_CHALLENGES_OR_RESOURCES.")
        else -> Check.Ok
    }

    // Journey-level gating from the information architecture (Guidelines 2.1)
    fun isStep1Done(j: JourneySteps) = j.interests?.let { canProceedInterests(it.items).ok } == true
    fun isStep2Done(j: JourneySteps) = j.best?.let { canProceedBest(it.examples).ok } == true
    fun isStep3Done(j: JourneySteps) = j.values?.let { canProceedValues(it.selected).ok } == true
    fun isStep4Done(j: JourneySteps) =
        j.excited?.let { canProceedExcited(it.excitementItems).ok && canProceedObjectives(it.objectives).ok } == true

    /** Overall completion 0..1 for the dashboard ring (one quarter per step). */
    fun progress(j: JourneySteps): Float =
        listOf(isStep1Done(j), isStep2Done(j), isStep3Done(j), isStep4Done(j)).count { it } / 4f

    /** "My Plan" unlocks after Step 1, "Insights" after Step 2 (Guidelines 2.1). */
    fun isPlanUnlocked(j: JourneySteps) = isStep1Done(j)
    fun isInsightsUnlocked(j: JourneySteps) = isStep2Done(j)
    fun canGeneratePlan(j: JourneySteps) = isStep1Done(j) && isStep2Done(j) && isStep3Done(j) && isStep4Done(j)

    fun registerName(name: String): Check = if (name.isBlank()) Check.fail("Tell us your first name.") else Check.Ok

    fun registerEmail(email: String): Check =
        if (Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(email.trim())) Check.Ok
        else Check.fail("Enter a valid email address.")
}
