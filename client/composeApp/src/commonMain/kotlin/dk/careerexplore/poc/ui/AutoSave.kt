package dk.careerexplore.poc.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

/** Holds the latest draft of a step and saves it when it changed. */
class AutoSaver<T>(initial: T, private val save: suspend (T) -> Boolean) {
    var current: T = initial
    private var saved: T = initial

    val dirty: Boolean get() = current != saved

    suspend fun saveNow(): Boolean {
        val value = current
        if (value == saved) return true
        val previous = saved
        saved = value // optimistic: avoids a duplicate save if another trigger fires while this one is in flight
        val ok = save(value)
        if (!ok) saved = previous
        return ok
    }
}

/**
 * Auto-save per Guidelines S-06: every 30 seconds while the screen is open, and when the user leaves the screen
 * (the save then runs in the app scope so it is not cancelled with the screen).
 */
@Composable
fun <T> rememberAutoSaver(app: AppState, draft: T, save: suspend (T) -> Boolean): AutoSaver<T> {
    val saver = remember { AutoSaver(draft, save) }
    SideEffect { saver.current = draft }
    LaunchedEffect(saver) {
        while (true) {
            delay(30_000)
            saver.saveNow()
        }
    }
    DisposableEffect(saver) {
        onDispose { if (saver.dirty) app.launchNow { saver.saveNow() } }
    }
    return saver
}
