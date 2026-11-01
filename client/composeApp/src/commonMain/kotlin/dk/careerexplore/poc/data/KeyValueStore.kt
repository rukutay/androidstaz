package dk.careerexplore.poc.data

import dk.careerexplore.poc.domain.User
import kotlinx.serialization.encodeToString

interface KeyValueStore {
    fun get(key: String): String?
    fun put(key: String, value: String)
    fun remove(key: String)
}

class InMemoryKeyValueStore : KeyValueStore {
    private val map = mutableMapOf<String, String>()
    override fun get(key: String) = map[key]
    override fun put(key: String, value: String) { map[key] = value }
    override fun remove(key: String) { map.remove(key) }
}

/** The demo "login": the registered user, remembered on the device. */
class Session(private val store: KeyValueStore) {
    fun user(): User? = store.get(KEY)?.let { AppJson.decodeFromString<User>(it) }
    fun save(user: User) = store.put(KEY, AppJson.encodeToString(user))
    fun clear() = store.remove(KEY)

    private companion object { const val KEY = "session.user" }
}
