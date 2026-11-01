package dk.careerexplore.poc.data

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

enum class Winner { LOCAL, SERVER }

/**
 * Conflict rule (Guidelines 6.6, "server wins if timestamps diverge by more than 5 minutes").
 * Interpreted directionally: the server wins only when it is NEWER than the local edit by more than 5 minutes
 * (someone edited elsewhere later). A local edit made offline is newer than the server copy, so it must not be
 * discarded - it wins and is pushed.
 */
fun resolveConflict(localEditedAt: Instant, serverUpdatedAt: Instant): Winner =
    if (serverUpdatedAt - localEditedAt > 5.minutes) Winner.SERVER else Winner.LOCAL
