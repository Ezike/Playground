package samples

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface Samples {
    suspend fun run()
}

class ChannelSample : Samples {

    fun CoroutineScope.numberMaker() = produce<Int> {
        println("started")
        delay(1000)
        println("after delay")
        (1..3).forEach { send(it) }
    }

    override suspend fun run() {
        coroutineScope {
            val numberMaker = numberMaker()
            // launch {
            //     numberMaker.consumeEach { println("first : $it") }
            // }
            // launch {
            //     numberMaker.consumeEach { println("second : $it") }
            // }
        }
    }
}

open class FlowSample : Samples {

    fun numberMaker() = flow<Int> {
        println("started")
        delay(1000)
        (1..3).forEach {
            emit(it)
        }
    }

    override suspend fun run() {
        coroutineScope {
            val numberMaker = numberMaker()
            launch {
                numberMaker.collect { println("first: $it") }
            }
            launch {
                numberMaker.collect { println("second: $it") }
            }
        }
    }
}

class StateFlowSample : FlowSample() {

    override suspend fun run() {
        coroutineScope {
            val numberMaker: StateFlow<Int> = numberMaker().stateIn(this)
            launch {
                println(numberMaker.first())
                numberMaker.collect { println("second: $it") }
            }
        }
    }
}

fun main() {
    // val sample: samples.Samples = samples.StateFlowSample()
    // runBlocking {
    //     sample.run()
    // }

    val booleanCondition: Condition = Condition.isTrue(true)
        .or("".isEmpty())
        .and((7 downTo 6).isEmpty())
        .orThen(Condition.isNull(true))
        .ifTrue {
            println("Some side effect")
        }.orElse {
            println("E no work")
        }

    val result: Boolean = Condition.isNotNull(9)
        .and(listOfNotNull(null))
        .or(null)
        .orThen(booleanCondition)
        .ifTrue {
            println("Some side effect")
        }.orElse {
            println("E no work")
        }.result()

}

fun interface Condition {
    fun isValid(): Boolean

    companion object {
        fun isNull(arg: Any?) = NullCondition { arg == null }
        fun isNotNull(arg: Any?) = NullCondition { arg != null }
        fun isTrue(arg: Boolean?) = BooleanCondition { arg.orFalse }
        fun isFalse(arg: Boolean?) = BooleanCondition { arg == false }
    }
}

fun interface NullCondition : Condition
fun interface BooleanCondition : Condition

fun NullCondition.and(arg: Any?): NullCondition = NullCondition { this.isValid() && arg != null }

fun NullCondition.or(arg: Any?) = NullCondition { this.isValid() || arg != null }

fun BooleanCondition.and(arg: Boolean?) = BooleanCondition { this.isValid() && arg.orFalse }

fun BooleanCondition.or(arg: Boolean?) = BooleanCondition { this.isValid() || arg.orFalse }

fun Condition.andThen(condition: Condition) = Condition {
    this.isValid() and condition.isValid()
}

fun Condition.orThen(condition: Condition) = Condition {
    this.isValid() or condition.isValid()
}

val Boolean?.orFalse
    get() = this ?: false

@OptIn(ExperimentalContracts::class)
inline fun Condition.ifTrue(action: () -> Unit): Condition {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    if (isValid()) {
        action()
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun Condition.orElse(action: () -> Unit): Condition {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    if (!isValid()) {
        action()
    }
    return this
}

fun Condition.result(): Boolean = isValid()
