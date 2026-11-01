package dk.careerexplore.poc.data

import dk.careerexplore.poc.domain.BestStep
import dk.careerexplore.poc.domain.ExcitedStep
import dk.careerexplore.poc.domain.InterestsStep
import dk.careerexplore.poc.domain.Journey
import dk.careerexplore.poc.domain.StepKey
import dk.careerexplore.poc.domain.ValuesStep
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlin.time.Clock
import kotlin.time.Instant

sealed interface SaveOutcome {
    /** Saved on the server. */
    data object Synced : SaveOutcome

    /** No connection: saved locally, will sync when back online (Content.Copy.OFFLINE_NOTICE). */
    data object QueuedOffline : SaveOutcome

    /** The server refused the data (validation); not queued. */
    data class Rejected(val message: String) : SaveOutcome
}

data class LoadResult(val journey: Journey, val offline: Boolean, val pendingCount: Int)

@Serializable
internal data class PendingSave(val journeyId: String, val step: StepKey, val bodyJson: String, val editedAt: String)

/**
 * Offline-first access to the current journey (Guidelines 6.6):
 * every edit is written to the local cache first, then sent; on transport failure it waits in a queue and is
 * replayed on the next [load]/[flush]. Conflicts follow [resolveConflict].
 */
class JourneyRepository(
    private val api: CareerApi,
    private val store: KeyValueStore,
    private val now: () -> Instant = { Clock.System.now() },
) {
    suspend fun load(): LoadResult {
        val server = try {
            api.currentJourney()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            val cached = cachedJourney() ?: throw e
            return LoadResult(cached, offline = true, pendingCount = readQueue().size)
        }
        val journey = replayQueue(server)
        cache(journey)
        return LoadResult(journey, offline = false, pendingCount = readQueue().size)
    }

    /** Tries to send everything waiting in the queue. Returns the number of items still pending. */
    suspend fun flush(): Int {
        if (readQueue().isEmpty()) return 0
        load()
        return readQueue().size
    }

    fun pendingCount(): Int = readQueue().size

    /** Sign-out: forget the cached journey and unsent edits of the previous user. */
    fun clearLocal() {
        store.remove(JOURNEY_KEY)
        store.remove(QUEUE_KEY)
    }

    fun cachedJourney(): Journey? = store.get(JOURNEY_KEY)?.let { AppJson.decodeFromString<Journey>(it) }

    /** [onCached] receives the locally updated journey before any network call, so the UI never waits for it. */
    suspend fun saveStep(journeyId: String, key: StepKey, bodyJson: String, onCached: (Journey) -> Unit = {}): SaveOutcome {
        applyLocally(journeyId, key, bodyJson)
        cachedJourney()?.let(onCached)
        val pending = PendingSave(journeyId, key, bodyJson, now().toString())
        if (readQueue().isNotEmpty()) {
            enqueue(pending) // keep order: older items must reach the server first
            return if (flushAfterEnqueue()) SaveOutcome.Synced else SaveOutcome.QueuedOffline
        }
        return try {
            cache(api.saveStep(journeyId, key, bodyJson))
            SaveOutcome.Synced
        } catch (e: ApiException) {
            SaveOutcome.Rejected(e.message)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            enqueue(pending)
            SaveOutcome.QueuedOffline
        }
    }

    private suspend fun flushAfterEnqueue(): Boolean = try {
        load()
        readQueue().isEmpty()
    } catch (e: ApiException) {
        false
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        false
    }

    /** Sends queued saves oldest first; stops at the first transport failure. Returns the freshest known journey. */
    private suspend fun replayQueue(server: Journey): Journey {
        var current = server
        val serverTime = Instant.parse(server.updatedAt)
        for (item in readQueue()) {
            if (item.journeyId != server.id || resolveConflict(Instant.parse(item.editedAt), serverTime) == Winner.SERVER) {
                dequeue(item) // someone saved later elsewhere (or the journey changed): server wins
                continue
            }
            try {
                current = api.saveStep(item.journeyId, item.step, item.bodyJson)
                dequeue(item)
            } catch (e: ApiException) {
                dequeue(item) // refused by the server: do not retry forever
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                return applyQueueOver(current)
            }
        }
        return current
    }

    private fun applyQueueOver(journey: Journey): Journey =
        readQueue().filter { it.journeyId == journey.id }.fold(journey) { j, item -> withStep(j, item.step, item.bodyJson, item.editedAt) }

    private fun applyLocally(journeyId: String, key: StepKey, bodyJson: String) {
        val cached = cachedJourney()?.takeIf { it.id == journeyId } ?: return
        cache(withStep(cached, key, bodyJson, now().toString()))
    }

    private fun withStep(journey: Journey, key: StepKey, bodyJson: String, updatedAt: String): Journey {
        val steps = journey.steps
        val updated = when (key) {
            StepKey.INTERESTS -> steps.copy(interests = AppJson.decodeFromString<InterestsStep>(bodyJson))
            StepKey.BEST -> steps.copy(best = AppJson.decodeFromString<BestStep>(bodyJson))
            StepKey.VALUES -> steps.copy(values = AppJson.decodeFromString<ValuesStep>(bodyJson))
            StepKey.EXCITED -> steps.copy(excited = AppJson.decodeFromString<ExcitedStep>(bodyJson))
        }
        return journey.copy(steps = updated, updatedAt = updatedAt)
    }

    private fun cache(journey: Journey) = store.put(JOURNEY_KEY, AppJson.encodeToString(journey))

    private fun readQueue(): List<PendingSave> =
        store.get(QUEUE_KEY)?.let { AppJson.decodeFromString(ListSerializer(PendingSave.serializer()), it) } ?: emptyList()

    private fun writeQueue(items: List<PendingSave>) =
        store.put(QUEUE_KEY, AppJson.encodeToString(ListSerializer(PendingSave.serializer()), items))

    /** Only the latest state of a step matters, so a newer save replaces an older queued one. */
    private fun enqueue(item: PendingSave) =
        writeQueue(readQueue().filterNot { it.journeyId == item.journeyId && it.step == item.step } + item)

    private fun dequeue(item: PendingSave) = writeQueue(readQueue().filterNot { it == item })

    private companion object {
        const val JOURNEY_KEY = "journey.current"
        const val QUEUE_KEY = "journey.pending"
    }
}
