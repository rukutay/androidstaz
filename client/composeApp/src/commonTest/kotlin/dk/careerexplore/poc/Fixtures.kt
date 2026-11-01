package dk.careerexplore.poc

// Real responses captured from the local server on 2026-11-01 (fictional test user), to keep DTOs in sync with the API.
object Fixtures {
    const val USER =
        """{"id":"f1f4e2d6-dddf-4cac-b0e1-b7d5e7b2dff6","name":"Fixture User","email":"fixture-capture@example.test","country":"DK","createdAt":"2026-11-01T13:07:59.777Z"}"""

    const val JOURNEY_EMPTY =
        """{"id":"ff4970a3-91e9-4597-b6e2-4ef888fd458d","version":1,"status":"in_progress","updatedAt":"2026-11-01T13:07:59.835Z","steps":{"interests":null,"best":null,"values":null,"excited":null}}"""

    const val JOURNEY_FULL =
        """{"id":"ff4970a3-91e9-4597-b6e2-4ef888fd458d","version":1,"status":"steps_complete","updatedAt":"2026-11-01T13:07:59.910Z","steps":{"interests":{"items":["Teaching people","Writing stories"],"topTwo":["Teaching people"]},"best":{"examples":[{"tags":[],"text":"Led a workshop","reasons":["I helped others"]}]},"values":{"selected":["Variety","Making a difference","Work-life balance"],"reflection":"It would drain me."},"excited":{"jobWants":[{"role":{"text":"a coach","importance":"important"},"benefit":{"text":"flexibility","importance":"somewhat"},"company":{"text":"a small firm","importance":"somewhat"},"culture":{"text":"warm","importance":"important"},"location":{"text":"near home","importance":"not"}}],"objectives":[{"action":"talk to two coaches","outcome":"I understand the path","resources":["a mentor"],"timeframe":"1 month","challenges":["time"]}],"excitementItems":["Teaching a new skill"]}}}"""

    const val PLAN =
        """{"journeyId":"ff4970a3-91e9-4597-b6e2-4ef888fd458d","version":1,"generatedAt":"2026-11-01T13:08:23.863Z","data":{"values":{"selected":["Variety","Making a difference","Work-life balance"],"reflection":"It would drain me."},"jobWants":[{"role":{"text":"a coach","importance":"important"},"benefit":{"text":"flexibility","importance":"somewhat"},"company":{"text":"a small firm","importance":"somewhat"},"culture":{"text":"warm","importance":"important"},"location":{"text":"near home","importance":"not"}}],"userName":"Fixture User","interests":{"items":["Teaching people","Writing stories"],"topTwo":["Teaching people"]},"excitement":["Teaching a new skill"],"objectives":[{"action":"talk to two coaches","outcome":"I understand the path","resources":["a mentor"],"timeframe":"1 month","challenges":["time"]}],"bestMoments":[{"headline":"Led a workshop","topReason":"I helped others"}]}}"""

    const val PLANS = """[{"version":1,"generatedAt":"2026-11-01T13:08:23.863Z"}]"""

    const val INSIGHTS =
        """{"keywords":[{"word":"teaching","count":2},{"word":"new","count":1},{"word":"people","count":1}],"disclaimer":"These are reflections for self-exploration, not a professional assessment."}"""

    const val PROMPT = """{"id":"af9fe1b7-f8d2-41a8-83e5-28f274d2bb3e","text":"This takes effort, and it's worth it.","category":"quote"}"""

    const val VALIDATION_ERROR =
        """{"error":"validation_error","message":"Invalid request body.","issues":[{"path":"selected","message":"Too big: expected array to have <=3 items"}]}"""
}
