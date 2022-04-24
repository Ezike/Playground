package samples

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import java.util.Date

sealed interface Event {
    data class Start(val time: Long) : Event
    object Stop : Event
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.alert(eventChannel: ReceiveChannel<Event>) = produce<Long> {
    var event: Event = Event.Stop
    while (isActive) {
        select<Event> {
            eventChannel.onReceive { item ->
                event = (item)
                if (event is Event.Stop) {
                    print("Stop")
                }
                event
            }
            if (event is Event.Start) {
                onSend(Date().time) {
                    delay(1000)
                    event
                }
            }

        }
        if (event is Event.Stop) {
            break
        }
    }
}

fun main() {
    val event = Channel<Event>()
    runBlocking {
        launch {
            event.send(Event.Start(Date().time))
            delay(6000)
            event.send(Event.Stop)
        }
        alert(event).consumeEach { println(it) }

    }
}