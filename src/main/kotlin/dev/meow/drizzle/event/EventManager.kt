package dev.meow.drizzle.event

import dev.meow.drizzle.Drizzle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object EventManager {

    private val registry = ConcurrentHashMap<Class<out Event>, CopyOnWriteArrayList<EventHook<in Event>>>()

    private val flows = ConcurrentHashMap<Class<out Event>, MutableSharedFlow<Event>>()

    init {
        CoroutineTicker
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> registerEventHook(eventClass: Class<out Event>, eventHook: EventHook<T>): EventHook<T> {
        val handlers = registry.getOrPut(eventClass) { CopyOnWriteArrayList() }

        val hook = eventHook as EventHook<in Event>

        if (!handlers.contains(hook)) {
            handlers.add(hook)

            handlers.sortByDescending { it.priority }
        }

        return eventHook
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> unregisterEventHook(eventClass: Class<out Event>, eventHook: EventHook<T>) {
        registry[eventClass]?.remove(eventHook as EventHook<in Event>)
    }

    fun unregisterEventHandler(eventHandler: EventListener) {
        registry.values.forEach {
            it.removeIf { hook -> hook.listener == eventHandler }
        }
    }

    fun <T : Event> callEvent(event: T): T {
        val hooks = registry[event.javaClass] ?: return event

        event.isCompleted = false
        for (eventHook in hooks) {

            if (!eventHook.listener.running) continue

            runCatching {
                eventHook.handler(event)
            }.onFailure {
                Drizzle.logger.error("Exception while executing handler.", it)
            }
        }
        event.isCompleted = true

        val mutableSharedFlow = flows.getOrPut(event.javaClass) { MutableSharedFlow() }
        @Suppress("UNCHECKED_CAST")
        (mutableSharedFlow as MutableSharedFlow<T>).tryEmit(event)

        return event
    }

    fun <T : Event> eventFlow(eventClass: Class<T>): SharedFlow<T> {
        @Suppress("UNCHECKED_CAST")
        return flows[eventClass] as SharedFlow<T>
    }
}