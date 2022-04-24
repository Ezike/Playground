package redux

operator fun <T, U, V> ((T) -> U).plus(other: (U) -> V): (T) -> V = fun(t: T) = other(this(t))
operator fun <T, U> T.plus(other: U) = Pair(this, other)
operator fun <T, U, V> Pair<T, U>.plus(other: V) = Triple(first, second, other)

val er = { i: Int -> i.toString() } + { i: String -> i }

fun interface Listener<State> {
    fun invoke(state: State)
}

interface Action
typealias Unsubscribe = () -> Unit
typealias Reducer<State> = (Action, State) -> State
typealias DispatchFunction = (Action) -> Unit
typealias GetState<State> = () -> State

// { getState, dispatch ->
//     { next ->
//         { action ->  }
typealias Next = (DispatchFunction) -> DispatchFunction
typealias Middleware<State> = (GetState<State>, DispatchFunction) -> Next
typealias MiddlewareItem<State> = (GetState<State>, Action, DispatchFunction) -> Unit

typealias CreateStoreReturnSignature<State> = Triple<GetState<State>, DispatchFunction, (Listener<State>) -> Unsubscribe>
typealias CreateStoreSignature<State> = (Reducer<State>, State) -> CreateStoreReturnSignature<State>
typealias StoreEnhancerSignature<State> = (CreateStoreSignature<State>) -> CreateStoreSignature<State>

fun <State> createStore(reducer: Reducer<State>, initialState: State): CreateStoreReturnSignature<State> {
    var state = initialState
    var listeners = mutableListOf<Listener<State>>()
    var isDispatching = false

    val getState = { state }
    val dispatch = fun(action: Action) {
        if (isDispatching) error(
            "_storeDispatch called while dispatching. " +
                "This might indicate a threading problem or an unwanted " +
                " side-effect in one of the reducers (calling `dispatch` itself)"
        )

        isDispatching = true
        state = reducer(action, state)
        isDispatching = false
        listeners.forEach {
            it.invoke(state)
        }
    }

    val subscribe = fun(listener: Listener<State>): Unsubscribe {
        listeners.add(listener)
        return { listeners = listeners.filter { it !== listener }.toMutableList() }
    }

    return getState + dispatch + subscribe
}

fun <State> applyMiddleware(vararg middlewares: Middleware<State>): StoreEnhancerSignature<State> =
    { createStore ->
        { reducer: Reducer<State>, state: State ->
            val (getState, mainDispatch, subscribe) = createStore(reducer, state)
            var patchedDispatch = mainDispatch
            val internalDispatch = fun(action: Action) {
                patchedDispatch(action)
            }
            patchedDispatch = middlewares
                .reversed()
                .fold({ action ->
                    mainDispatch(action)
                }) { previousDispatch, nextDispatch ->
                    nextDispatch(getState, internalDispatch)(previousDispatch)
                }

            getState + patchedDispatch + subscribe
        }
    }

fun <State> createMiddleware(vararg items: MiddlewareItem<State>): Middleware<State> =
    { getState, dispatch ->
        { next ->
            { action ->
                next(action)
                items.forEach { it(getState, action, dispatch) }
            }
        }
    }

val middleware = applyMiddleware<AppState>(createMiddleware())


val reducer: Reducer<AppState> = {action: Action, state: AppState -> state}

data class AppState(
    val id: Int = 0,
    val text: String = ""
)

fun main() {

    val (getState, dispatch, subscribe) = middleware(::createStore)(reducer, AppState())

    println(getState())
}
