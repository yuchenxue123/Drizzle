package dev.meow.drizzle.event

import dev.meow.drizzle.events.game.GameTickEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

typealias Handler<T> = (event: T) -> Unit

class EventHook<T : Event>(
    val listener: EventListener,
    val priority: EventPriority,
    val handler: Handler<T>,
)

interface EventListener {

    /**
     * Checks if the event listener is currently active and should process events.
     * By default, it defers to its parent listener's running status if a parent exists,
     * otherwise, it's considered running.
     */
    val running: Boolean
        get() = parent()?.running ?: true

    /**
     * Parent listener of this listener,
     * default is null, indicating no parent.
     */
    fun parent(): EventListener? = null


    /**
     * Unregisters this event listener from the EventManager.
     */
    fun unregister() {
        EventManager.unregisterEventHandler(this)
        removeEventListenerScope()
    }
}

inline fun <reified T : Event> EventListener.handler(
    priority: Int = Priorities.DEFAULT,
    noinline handler: Handler<T>
): EventHook<T> {
    return EventManager.registerEventHook(
        T::class.java,
        EventHook(this, priority, handler)
    )
}

inline fun <reified T : Event> EventListener.until(
    priority: Int = Priorities.DEFAULT,
    crossinline condition: (event: T) -> Boolean
): EventHook<T> {
    lateinit var eventHook: EventHook<T>
    eventHook = EventHook(this, priority) {
        if (!this.running || condition.invoke(it)) {
            EventManager.unregisterEventHook(T::class.java, eventHook)
        }
    }
    return EventManager.registerEventHook(T::class.java, eventHook)
}

inline fun <reified T : Event> EventListener.once(
    priority: Int = Priorities.DEFAULT,
    crossinline handler: Handler<T>
): EventHook<T> {
    return until(priority) { event ->
        handler.invoke(event)
        true
    }
}

inline fun <reified T : Event> EventListener.sequenceHandler(
    priority: Int = Priorities.DEFAULT,
    dispatcher: CoroutineDispatcher? = null,
    onCancellation: Runnable? = null,
    crossinline eventHandler: SuspendableEventHandler<T>,
) = handler<T>(priority) { event -> launchSequence(dispatcher, onCancellation) { eventHandler(event) } }

fun EventListener.tickHandler(
    dispatcher: CoroutineDispatcher? = null,
    onCancellation: Runnable? = null,
    eventHandler: SuspendableHandler,
): EventHook<GameTickEvent> {
    var sequence: Job? = null

    return handler<GameTickEvent> {
        // Check if the sequence is already running (completed or null)
        if (sequence == null || !sequence!!.isActive) {
            sequence = launchSequence(dispatcher, onCancellation, eventHandler)
        }
    }
}
