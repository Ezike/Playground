package samples

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() = runBlocking {
    val state = MutableStateFlow(0)
    withContext(Dispatchers.Default) {
        runNow { state.value++ }
    }
    println(state.value)
}

private suspend fun runNow(
    action: suspend () -> Unit
) {
    coroutineScope {
        repeat(80) {
            launch {
                action()
            }
        }
    }
}