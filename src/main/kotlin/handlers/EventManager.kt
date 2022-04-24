package handlers

class EventManager(private val queue: PriorityLinkedList<ActionHandler> = PriorityLinkedList()) {

    fun add(vararg handler: ActionHandler) {
        queue.add(*handler)
    }

    fun enqueue(action: Action) {
        queue.forEach { it.handle(action) }
    }
}

sealed class ActionHandler(private val priority: Priority) : Comparable<ActionHandler> {

    abstract fun handle(action: Action)

    enum class Priority {
        SUPER_HIGH, HIGH, MEDIUM, LOW
    }

    override fun compareTo(other: ActionHandler): Int {
        return priority.compareTo(other.priority)
    }

    override fun toString(): String {
        return "${this.name} ==> ${priority.ordinal}"
    }
}

sealed interface Action {
    val i: Int

    data class Add(override val i: Int) : Action
}

object Logger : ActionHandler(Priority.MEDIUM) {

    override fun handle(action: Action) {
        println("$name")
    }
}

object Increment : ActionHandler(Priority.MEDIUM) {

    override fun handle(action: Action) {
        println("$name")
    }
}

object Higher : ActionHandler(Priority.HIGH) {

    override fun handle(action: Action) {
        println("$name")
    }
}

val Any.name
    get() = this::class.simpleName

object Decrement : ActionHandler(Priority.LOW) {

    override fun handle(action: Action) {
        println("$name")
    }
}

object Blablaer : ActionHandler(Priority.LOW) {

    override fun handle(action: Action) {
        println("$name")
    }
}

fun main() {
    val manager = EventManager()
    manager.add(Decrement, Blablaer, Logger, Increment, Higher, Decrement)
    manager.enqueue(Action.Add(i = 0))
}

@Suppress("UNCHECKED_CAST")
class PriorityLinkedList<T> : Iterable<T> {

    private data class Node(
        val value: Any,
        var next: Node? = null
    )

    private var head: Node? = null

    fun add(vararg value: Any) {
        for (item in value) {
            appendInternal(item)
        }
    }

    private fun appendInternal(value: Any) {
        value as Comparable<Any>
        val node = Node(value)
        if (head == null || (head?.value as? Comparable<Any>).greater(value)) {
            node.next = head
            head = node
            return
        }
        val dummy = Node(value = Any())
        dummy.next = head
        var current: Node? = dummy

        while (current?.next != null && (current.next?.value as? Comparable<Any>).less(value)) {
            current = current.next
        }

        node.next = current?.next
        current?.next = node
        head = dummy.next
    }

    private fun Comparable<Any>?.less(
        v: Comparable<Any>
    ): Boolean = (if (this != null) this <= v else false)

    private fun Comparable<Any>?.greater(
        v: Comparable<Any>
    ): Boolean = (if (this != null) this >= v else false)

    override fun iterator(): Iterator<T> {
        var current = head
        return object : Iterator<T> {
            override fun hasNext(): Boolean {
                return current != null
            }

            override fun next(): T {
                val cached = current?.value
                current = current?.next
                return (cached as T)
            }
        }
    }
}
