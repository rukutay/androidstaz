package dk.careerexplore.poc.domain

/**
 * Static content from the Development Guidelines. Microcopy follows section 8.2/8.3.
 * Items marked DRAFT are placeholders written for the POC; Career Explore should review them (Guidelines 12.2).
 */
object Content {
    const val TAGLINE = "Enabling Your Professional Best"

    // Section 2.1 / 3
    const val STEP1_QUOTE = "No one can discover you until you do"
    const val STEP3_QUOTE = "Your happiness should never be a trade-off"

    val onboarding = listOf(
        OnboardingCard("What’s Next?", "Quarterlife is a moment of questions. This app helps you find your direction, one step at a time."),
        OnboardingCard("Know Yourself Better", "Four short steps: your interests, your best moments, your must-have values, and what excites you."),
        OnboardingCard("Turn Insight Into Action", "Your answers become a personal 3-month plan you can revise, export, and take to a coach."),
    )

    /** Section 8.2 microcopy library */
    object Copy {
        const val STEP1_INSTRUCTION = "List every job or task that interests you - big or small, conventional or not. There’s no wrong answer here."
        const val STEP2_INSTRUCTION = "Think of a moment you were genuinely at your best. What was happening? What made it that way?"
        const val STEP3_INSTRUCTION = "Your happiness should never be a trade-off. Choose the three values you need in place to feel truly fulfilled."
        const val STEP4_INSTRUCTION = "What gets you excited to start the day? Be as specific as you can."
        const val EMPTY_PLAN = "Your plan is waiting. Complete all four steps to generate it."
        const val PLAN_GENERATED = "Your personal plan is ready. Ground your next steps in what you know about yourself."
        const val COACH_CTA = "Ready to go deeper? Partner with a Career Explore coach to move confidently toward your goals."
        const val AUTO_SAVE = "Changes saved"
        const val OFFLINE_NOTICE = "You’re offline. Your changes are saved locally and will sync when you’re back online."
        const val NETWORK_SAVE_ERROR = "We couldn’t save your progress right now. Tap to retry - your data is still here."
        const val STEP1_COUNTER_LOW = "Keep going - aim for at least 20"
        const val STEP1_COUNTER_DONE = "Great work. Now look for patterns."
        const val PATTERN_NUDGE_INTERESTS = "Great! Now read back your list. Do you notice any themes?"
        const val PATTERN_NUDGE_EXCITED = "Do you notice what these have in common?"
        const val INSIGHTS_DISCLAIMER = "These are reflections for self-exploration, not a professional assessment."
        const val COACH_EMAIL = "anita@careerexplore.dk"
        const val COACH_WEBSITE = "https://www.careerexplore.dk"
    }

    /** Step 3 values, Guidelines 3 / S-08 (exactly 20). */
    val values = listOf(
        "Being busy", "Getting recognition", "Feeling challenged", "Position & status", "Caring for others",
        "Solving problems", "Work-life balance", "Having influence", "Developing others", "Using my creativity",
        "Taking risks", "Financial security", "Working independently", "Good relations with colleagues", "Variety",
        "Being an expert", "Making a difference", "Developing myself", "Being given responsibility", "Individuality",
    )

    /** Step 1 suggestion drawer, Guidelines Appendix 13.1. */
    val interestSuggestions: Map<String, List<String>> = linkedMapOf(
        "Creative & Expressive" to listOf(
            "Composing music", "Writing stories", "Designing visual concepts", "Illustrating or drawing", "Performing on stage",
            "Making films or videos", "Photography", "Crafting or making things by hand", "Interior or space design", "Fashion or costume design",
        ),
        "People & Relationships" to listOf(
            "Teaching or coaching others", "Caring for people’s wellbeing", "Counselling or listening deeply", "Organising social events",
            "Building communities", "Working with children", "Mentoring younger people", "Facilitating group discussions", "Volunteering or community work",
        ),
        "Analytical & Problem-Solving" to listOf(
            "Analysing data or patterns", "Scientific research", "Solving complex puzzles", "Financial modelling", "Strategic planning",
            "Debugging systems", "Optimising processes", "Conducting experiments", "Writing code or algorithms",
        ),
        "Leadership & Influence" to listOf(
            "Leading a team", "Negotiating and persuading", "Starting or growing organisations", "Public speaking", "Advocating for a cause",
            "Managing projects", "Making high-stakes decisions", "Setting direction and strategy",
        ),
        "Physical & Practical" to listOf(
            "Building or constructing things", "Working outdoors", "Cooking or preparing food", "Gardening or working with nature",
            "Sports coaching or training", "Physical therapy or body work", "Mechanical work or engineering",
        ),
        "Business & Enterprise" to listOf(
            "Selling ideas or products", "Launching new ventures", "Developing business strategies", "Customer experience design",
            "Brand building", "Marketing campaigns", "E-commerce or retail",
        ),
    )

    /** Step 4a "I'm stuck" helper. DRAFT content for POC review. */
    val excitementExamples = listOf(
        "When I get to explain something complex in a simple way",
        "When I get to start something from a blank page",
        "When I get to help someone take their next step",
        "When I get to solve a problem nobody has solved yet",
        "When I get to work with a small, close team",
        "When I get to present my ideas to an audience",
        "When I get to organise a messy situation into a clear plan",
        "When I get to learn a completely new skill",
        "When I get to see the direct result of my work",
        "When I get to work outdoors or with my hands",
    )

    /** Step 2 optional context tags (Guidelines S-07), as mutually exclusive pairs. */
    val contextTagPairs = listOf("Solo" to "Team", "Indoor" to "Outdoor", "Structured" to "Creative")

    /** Step 2 example card. DRAFT: the Guidelines refer to a sample from the printed guide that we do not have. */
    val bestExample = BestExample(
        text = "I organised a weekend fundraiser with my student association and we beat our goal.",
        reasons = listOf(
            "I could bring different people together around one plan",
            "I had the freedom to decide how to do it",
            "I saw the result of my work straight away",
        ),
    )

    /** Coach Connect value proposition (S-14). DRAFT copy for Career Explore to review. */
    val coachBenefits = listOf(
        "Turn self-discovery into practical roles" to "Test your wants and objectives against real roles and workplaces.",
        "Move past hesitation" to "Find what is holding you back and build steady momentum, two weeks at a time.",
        "A confidential sounding board" to "Time that is only about you and what matters to you at work.",
    )

    /** Country/region choices for the profile (S-04). */
    val countries = listOf("Denmark", "Sweden", "Norway", "Germany", "Poland", "United Kingdom", "Other")

    val onboardingOverlines = listOf("Clarity & purpose", "Self-discovery", "From insight to action")
}

data class OnboardingCard(val headline: String, val body: String)
