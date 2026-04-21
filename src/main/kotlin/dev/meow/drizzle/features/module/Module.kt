package dev.meow.drizzle.features.module

import dev.meow.drizzle.Drizzle
import dev.meow.drizzle.config.Configurable
import dev.meow.drizzle.config.Toggleable
import dev.meow.drizzle.event.EventListener
import dev.meow.drizzle.event.EventManager
import dev.meow.drizzle.event.removeEventListenerScope
import dev.meow.drizzle.events.client.ModuleToggleEvent
import dev.meow.drizzle.utils.client.ingame

abstract class Module(
    name: String,
    val category: ModuleCategory,
    key: Int = -1,
    val locked: Boolean = false,
    defaultState: Boolean = false
) : Configurable(name), EventListener, Toggleable, MinecraftShortcut {

    val bind by bind("Bind", key)

    var state: Boolean = defaultState
        set(newState) {
            if (newState == field) return

            field = newState

            this.onToggled(newState)
        }

    override fun onToggled(state: Boolean): Boolean {
        if (!ingame) {
            return state
        }

        if (!state) {
            runCatching {
                removeEventListenerScope()
            }.onFailure {
                Drizzle.logger.error("Failed cancel sequences: ${it.message}", it)
            }
        }

        val state = super.onToggled(state)
        inner.filterIsInstance<Toggleable>().forEach { it.onToggled(state) }

        // Call module toggle event
        EventManager.callEvent(ModuleToggleEvent(this, state))
        return state
    }

    fun toggle() {
        state = !state
    }

}