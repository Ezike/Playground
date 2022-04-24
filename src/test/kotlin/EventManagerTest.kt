import handlers.Action
import handlers.ActionHandler
import handlers.Blablaer
import handlers.Decrement
import handlers.EventManager
import handlers.Higher
import handlers.Increment
import handlers.Logger
import handlers.PriorityLinkedList
import org.junit.Assert.assertEquals
import org.junit.Test

class EventManagerTest {

    @Test
    fun `check that stuff happens`() {
        val queue = PriorityLinkedList<ActionHandler>()
        val manager = EventManager(queue = queue)
        manager.add(
            Blablaer,
            Logger,
            Decrement,
            Higher,
            Decrement,
            Blablaer,
            Increment,
            Logger,
            Increment,
            Higher,
            Decrement,
            Blablaer
        )

        manager.enqueue(Action.Add(i = 0))

        val expected = listOf(
            Higher,
            Higher,
            Logger,
            Increment,
            Logger,
            Increment,
            Blablaer,
            Decrement,
            Decrement,
            Blablaer,
            Decrement,
            Blablaer
        )
        assertEquals(expected, queue.toList())
    }
}