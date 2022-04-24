package samples

class LinkedList : Iterable<Any>{

    private data class Node(
        val value: Any,
        var next: Node? = null
    )

    private var head: Node? = null
    private var tail: Node? = null

    fun append(vararg value: Any) {
        for (item in value) {
            appendInternal(item)
        }
    }

    fun append(list: List<Any>) {
        for (item in list) {
            appendInternal(item)
        }
    }

    fun prepend(value: Any) {
        if (head == null) {
            head = Node(value)
            return
        }
        val newNode = Node(value)
        newNode.next = head
        head = newNode
    }

    private fun appendInternal(item: Any) {
        if (head == null) {
            head = Node(item)
            tail = head
        } else {
            tail?.next = Node(item)
            tail = tail?.next
        }
    }

    fun traverse(action: (Any) -> Unit) {
        var current = head
        while (current != null) {
            action(current.value)
            current = current.next
        }
    }

    override fun iterator(): Iterator<Any> {
        var current = head
        return object: Iterator<Any> {
            override fun hasNext(): Boolean {
                return current != null
            }

            override fun next(): Any {
                val cached = current?.value
                current = current?.next
                return cached!!
            }
        }
    }
}

val linkedList = LinkedList()
linkedList.append(32, false, Pair(3, 5))
linkedList.append(listOf(45, "true", 5))

// linkedList.traverse(::println)

fun reverse(list: LinkedList): LinkedList {

    val backlist = LinkedList()

    for(value in list) {

        backlist.prepend(value)
    }

    return backlist
}
linkedList.chunked(3)
.forEach(::println)