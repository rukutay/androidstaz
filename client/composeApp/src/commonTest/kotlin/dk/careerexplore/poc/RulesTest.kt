package dk.careerexplore.poc

import dk.careerexplore.poc.data.Winner
import dk.careerexplore.poc.data.resolveConflict
import dk.careerexplore.poc.domain.BestExample
import dk.careerexplore.poc.domain.Content
import dk.careerexplore.poc.domain.ExcitedStep
import dk.careerexplore.poc.domain.InterestsStep
import dk.careerexplore.poc.domain.JourneySteps
import dk.careerexplore.poc.domain.Objective
import dk.careerexplore.poc.domain.Rules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class RulesTest {
    @Test
    fun interestsAddAndProceed() {
        assertFalse(Rules.canAddInterest(listOf("Photography"), " photography ").ok) // duplicate, case-insensitive
        assertFalse(Rules.canAddInterest(emptyList(), "x".repeat(61)).ok)
        assertFalse(Rules.canAddInterest(emptyList(), "   ").ok)
        assertTrue(Rules.canAddInterest(emptyList(), "Photography").ok)

        assertEquals(
            "Add a few more interests to continue - aim for at least 5 to start seeing patterns.",
            Rules.canProceedInterests(List(4) { "i$it" }).message,
        )
        assertTrue(Rules.canProceedInterests(List(5) { "i$it" }).ok)
        assertEquals(Content.Copy.STEP1_COUNTER_LOW, Rules.interestsCounter(19))
        assertEquals(Content.Copy.STEP1_COUNTER_DONE, Rules.interestsCounter(20))
    }

    @Test
    fun topTwoPicker() {
        val items = listOf("a", "b", "c")
        assertTrue(Rules.canPickTopTwo(items, listOf("a"), "b").ok)
        assertFalse(Rules.canPickTopTwo(items, listOf("a", "b"), "c").ok)
        assertTrue(Rules.canPickTopTwo(items, listOf("a", "b"), "a").ok) // deselect
    }

    @Test
    fun bestNeedsOneExampleWithReason() {
        assertFalse(Rules.canProceedBest(listOf(BestExample("Led a workshop", reasons = listOf(" ")))).ok)
        assertTrue(Rules.canProceedBest(listOf(BestExample("Led a workshop", reasons = listOf("I helped")))).ok)
        assertNotNull(Rules.bestNudge(listOf(BestExample("x", listOf("y")))))
    }

    @Test
    fun valuesExactlyThree() {
        val three = listOf("Variety", "Taking risks", "Being an expert")
        assertFalse(Rules.canToggleValue(three, "Individuality").ok)
        assertTrue(Rules.canToggleValue(three, "Variety").ok)
        assertTrue(Rules.canProceedValues(three).ok)
        assertFalse(Rules.canProceedValues(three.take(2)).ok)
        assertNull(Rules.valuesReflectionPrompt(three.take(2)))
        assertEquals(
            "If a role can’t offer Variety, Taking risks, and Being an expert - how will that affect your happiness at work?",
            Rules.valuesReflectionPrompt(three),
        )
        assertEquals(20, Content.values.size)
    }

    @Test
    fun excitedAndObjectives() {
        assertFalse(Rules.canProceedExcited(listOf("a", "b", " ")).ok)
        assertTrue(Rules.canProceedExcited(listOf("a", "b", "c")).ok)
        assertFalse(Rules.canProceedObjectives(emptyList()).ok)
        assertFalse(Rules.canProceedObjectives(listOf(Objective(action = "talk", outcome = ""))).ok)
        assertTrue(Rules.canProceedObjectives(listOf(Objective(action = "talk", outcome = "clarity"))).ok)
    }

    @Test
    fun progressAndUnlocks() {
        val steps = JourneySteps(interests = InterestsStep(items = List(5) { "i$it" }))
        assertEquals(0.25f, Rules.progress(steps))
        assertTrue(Rules.isPlanUnlocked(steps))
        assertFalse(Rules.isInsightsUnlocked(steps))
        assertFalse(Rules.canGeneratePlan(steps.copy(excited = ExcitedStep(listOf("a", "b", "c")))))
    }

    @Test
    fun conflictServerWinsOnlyWhenNewerByMoreThanFiveMinutes() {
        val t = Instant.parse("2026-11-01T12:00:00Z")
        assertEquals(Winner.SERVER, resolveConflict(localEditedAt = t, serverUpdatedAt = t + 6.minutes))
        assertEquals(Winner.LOCAL, resolveConflict(localEditedAt = t, serverUpdatedAt = t + 4.minutes))
        assertEquals(Winner.LOCAL, resolveConflict(localEditedAt = t + 60.minutes, serverUpdatedAt = t)) // offline edit
    }
}
