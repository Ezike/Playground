fun solve(magazine: Array<String>, note: Array<String>) {
    val mainMap = note.associateWith { 0 }.toMutableMap()
    note.forEach {
        mainMap[it] = mainMap[it]!!.inc()
    }

    magazine.forEach {
        val op = mainMap[it]
        if (op != null) mainMap[it] = op.dec().takeIf { it >= 0 } ?: 0
    }

    val op = mainMap.values.sum()
    val vibe = if (op == 0) "YES" else "NO"
    println(vibe)
}

solve(
    arrayOf("two", "times", "is", "four", "three"),
    arrayOf("two", "times", "two", "is", "four")
)
// two times two is four = 4