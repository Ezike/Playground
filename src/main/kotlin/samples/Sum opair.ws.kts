package samples

val first = listOf(1, 2, 3, 9)
val sec = listOf(4, 0, 8, 5, 5, 6, 4)

fun findSum(list: List<Int>, sum: Int = 8): Boolean {
    val map = hashSetOf<Int>()
    list.forEach {
        if (map.contains(it)) {
            println("$it ${sum - it}")
            return true
        }
        map.add(sum - it)
    }
    return false
}

println(findSum(sec))

fun isValid(s: String): Boolean {
    val alles: Map<Char, Char> = mapOf('(' to ')', '{' to '}', '[' to ']')

    var first = 0
    var second = 1

    var ans = true

    if (s.count() % 2 != 0) return false

    while (second <= s.count()) {
        val a = s[first]
        val b = s[second]
        ans = alles[a] == b
        first += 2
        second += 2
    }

    return ans
}

fun isValid2(s: String): Boolean {
    val alles: MutableMap<Char, Pair<Char, Int>> = mutableMapOf('(' to Pair(')', 0), '{' to Pair('}', 0), '[' to Pair(']', 0))

    if (s.count() % 2 != 0) return false

    s.forEach { a ->
        val pair = alles[a]
        println(a)
        if (alles.containsKey(a)) {
            println(alles)
            alles[a] = pair!!.copy(second = pair.second + 1)
            println(alles)
        } else {
            println(pair?.first)
            if (pair?.first == a) {
                println(alles)
                alles[a] = pair.copy(second = pair.second - 1)
                println(alles)
            } else {
                println(alles)
            }
        }
    }

    return alles.values.map { it.second }.reduce { a, b -> a + b } == 0
}

fun isValid22(s: String): Boolean {
    fun check(open: Char){
        
    }
    val alles: MutableMap<Char, Pair<Char, Int>> = mutableMapOf('(' to Pair(')', 0), '{' to Pair('}', 0), '[' to Pair(']', 0))

    if (s.count() % 2 != 0) return false

    s.forEach { a ->
        val pair = alles[a]
        println(a)
        if (alles.containsKey(a)) {
            println(alles)
            alles[a] = pair!!.copy(second = pair.second + 1)
            println(alles)
        } else {
            println(pair?.first)
            if (pair?.first == a) {
                println(alles)
                alles[a] = pair.copy(second = pair.second - 1)
                println(alles)
            } else {
                println(alles)
            }
        }
    }

    return alles.values.map { it.second }.reduce { a, b -> a + b } == 0
}
println(isValid2("{}()()()[]"))