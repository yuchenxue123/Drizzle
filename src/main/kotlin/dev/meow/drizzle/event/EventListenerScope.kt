package dev.meow.drizzle.event

import dev.meow.drizzle.Drizzle
import dev.meow.drizzle.utils.client.mc
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor

private val eventListenerScopeHolder = ConcurrentHashMap<EventListener, CoroutineScope>()

val EventListener.eventListenerScope: CoroutineScope
    get() = eventListenerScopeHolder.computeIfAbsent(this) {
        CoroutineScope(
            SupervisorJob()
                    + CoroutineExceptionHandler { _, throwable ->
                if (throwable is EventListenerNotListeningException) {
                    Drizzle.logger.debug("${throwable.eventListener} is not listening, job cancelled")
                } else {
                    Drizzle.logger.error("Exception occurred in CoroutineScope of $it", throwable)
                }
            }
                    + CoroutineName(it.toString()) // Name
                    // Render thread + Auto cancel on not listening
                    + it.continuationInterceptor(mc.asCoroutineDispatcher())
        )
    }

internal fun EventListener.continuationInterceptor(
    original: ContinuationInterceptor? = null,
): ContinuationInterceptor = original as? EventListenerRunningContinuationInterceptor
    ?: EventListenerRunningContinuationInterceptor(original, this)

fun EventListener.removeEventListenerScope() {
    eventListenerScopeHolder.remove(this)?.cancel(EventListenerNotListeningException(this))
}

class EventListenerNotListeningException(val eventListener: EventListener) :
    CancellationException("EventListener $eventListener is not running")

private class EventListenerRunningContinuationInterceptor(
    private val original: ContinuationInterceptor?,
    private val eventListener: EventListener,
) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

    override fun <T> interceptContinuation(
        continuation: Continuation<T>
    ): Continuation<T> {
        // Process with original interceptor
        val delegate = original?.interceptContinuation(continuation) ?: continuation

        return object : Continuation<T> {
            override val context get() = continuation.context

            override fun resumeWith(result: Result<T>) {
                // if the event listener is no longer active, abort the result
                val result = if (eventListener.running) {
                    result
                } else {
                    Result.failure(EventListenerNotListeningException(eventListener))
                }
                delegate.resumeWith(result)
            }
        }
    }
}
