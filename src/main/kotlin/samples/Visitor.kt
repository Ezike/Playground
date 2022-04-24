package samples

interface Visitor {
    fun visit(viewState: ViewState)
}

class AdditionVisitor : Visitor {
    override fun visit(viewState: ViewState) {
      when(viewState) {
          Loading -> TODO()
          RandomShit -> TODO()
          is Success -> TODO()
      }
    }
}

sealed class ViewState {
    abstract fun accept(visitor: Visitor)
}

data class Success(val id: String) : ViewState() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

object Loading : ViewState() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

object RandomShit : ViewState() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

fun main() {
    val state = Success("e")
    val visitor = AdditionVisitor()
    state.accept(visitor)
}



// samples.Condition.for(2 < 4) { }