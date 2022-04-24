package samples

val exec = fun(a: Int) = fun(b: String) = fun(c: String) = listOf(a.toString(), b, c)


exec(2)("5")("6")