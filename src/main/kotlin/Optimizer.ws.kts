fun interface Callable<S> {
    fun call(): S
}

object Caller : Callable<String> {

    private val array = arrayOf(2, 0, 1, 3)
    private val items = listOf("enugbe", "ama", "teaches", "sienna")
    private var result: String? = null

    override fun call(): String = result ?: expensiveOperation().also(::result::set)
    private fun expensiveOperation() = items[array.random()]
}

fun <T> CachingCaller(caller: Callable<T>): Callable<T> {
    val value by lazy { caller.call() }
    return Callable { value }
}

val a = Caller
a.call()
a.call()
a.call()
a.call()
a.call()