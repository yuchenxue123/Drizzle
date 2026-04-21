package dev.meow.drizzle.event

import dev.meow.drizzle.Drizzle
import dev.meow.drizzle.utils.client.mc
import it.unimi.dsi.fastutil.objects.ReferenceArrayList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.function.BooleanSupplier
import java.util.function.IntPredicate
import java.util.function.Predicate
import kotlin.coroutines.resume

typealias SuspendableEventHandler<T> = suspend CoroutineScope.(T) -> Unit
typealias SuspendableHandler = suspend CoroutineScope.() -> Unit

object CoroutineTicker : EventListener {

    private val logger = Drizzle.logger.subLogger("CoroutineTicker")

    private var tickDepth = 0

    private val runningList = ReferenceArrayList<BooleanSupplier>()
    private val pendingList = ReferenceArrayList<BooleanSupplier>()

    fun beginTick() {
        tickDepth++
    }

    fun tick() {
        if (tickDepth > 1) {
            return
        }

        runningList.addAll(pendingList)
        pendingList.clear()
        runningList.removeIf(Predicate {
            try {
                it.asBoolean
            } catch (e: Exception) {
                logger.error("error in tick", e)
                false
            }
        })
    }

    fun endTick() {
        if (tickDepth <= 0) {
            logger.warn("tick depth is greater than zero")
            tickDepth = 0
            return
        }

        tickDepth--
    }

    fun register(task: BooleanSupplier) {
        mc.execute { pendingList.add(task) }
    }

}

/**
 * Ticks with [stopAt] until it returns true.
 * The elapsed ticks (starting from 1) will be passed to [stopAt].
 *
 * Resumes on Render thread.
 *
 * Example:
 * - `tickUntil { true }` --> `1`
 * - `tickUntil { it >= 2 }` --> `2`
 *
 * @param stopAt the callback of elapsed ticks. Will be called on game tick.
 * @return the times of [stopAt] to be executed (equals to elapsed ticks)
 */
suspend fun tickUntil(
    stopAt: IntPredicate,
): Int = suspendCancellableCoroutine { continuation ->
    var elapsedTicks = 0
    CoroutineTicker.register {
        when {
            !continuation.isActive -> true

            stopAt.test(++elapsedTicks) -> {
                continuation.resume(elapsedTicks)
                true
            }

            else -> false
        }
    }
}

/**
 * Ticks until the fixed amount of ticks ran out or the [breakLoop] says to continue.
 *
 * @returns if we passed the time of [ticks] without breaking the loop.
 */
suspend fun tickConditional(ticks: Int, breakLoop: BooleanSupplier): Boolean {
    // Don't wait if ticks is 0
    if (ticks == 0) {
        return !breakLoop.asBoolean
    }

    return tickUntil { breakLoop.asBoolean || it >= ticks } >= ticks
}

/**
 * Waits a fixed amount of ticks before continuing.
 * Re-entry at the game tick.
 */
suspend fun waitTicks(ticks: Int) {
    // Don't wait if ticks is 0
    if (ticks == 0) {
        return
    }

    tickUntil { it >= ticks }
}

/**
 * Waits a fixed amount of seconds on tick level before continuing.
 * Re-entry at the game tick.
 *
 * Note: When TPS is not 20, this won't be actual `seconds`.
 */
suspend fun waitSeconds(seconds: Int) = waitTicks(seconds * 20)

fun EventListener.launchSequence(
    dispatcher: CoroutineDispatcher? = null,
    onCancellation: Runnable? = null,
    handler: SuspendableHandler,
): Job =
    eventListenerScope.launch(
        context = continuationInterceptor(dispatcher),
        start = CoroutineStart.UNDISPATCHED
    ) {
        if (running) {
            handler()
        }
    }.apply {
        onCancellation?.let {
            this.invokeOnCompletion { t ->
                if (t is CancellationException) {
                    it.run()
                }
            }
        }
    }
