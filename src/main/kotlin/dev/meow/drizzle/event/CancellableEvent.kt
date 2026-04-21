package dev.meow.drizzle.event

import dev.meow.drizzle.Drizzle

abstract class CancellableEvent : Event() {

    private var isCancelled: Boolean = false

    fun cancelEvent() {
        if (isCompleted) {
            Drizzle.logger.warn("Cannot cancel an event while already completed!")
            return
        }

        isCancelled = true
    }

    fun isCancelled() = isCancelled

}