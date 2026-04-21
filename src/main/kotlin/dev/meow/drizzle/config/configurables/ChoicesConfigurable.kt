package dev.meow.drizzle.config.configurables

import dev.meow.drizzle.Drizzle
import dev.meow.drizzle.config.Configurable
import dev.meow.drizzle.config.Mode
import dev.meow.drizzle.config.Setting
import dev.meow.drizzle.config.Toggleable
import dev.meow.drizzle.config.settings.collection.ModeSetting
import dev.meow.drizzle.event.EventListener
import dev.meow.drizzle.event.removeEventListenerScope

class ChoicesConfigurable<T : Choice>(
    name: String,
    private val choices: Array<T>,
    value: T = choices[0],
    val listener: EventListener? = null,
    displayable: () -> Boolean = { true },
) : Configurable(name), Toggleable {

    var activeChoice: T = value

    private val modeSetting = ModeSetting(
        name,
        choices.map { it.showName }.toTypedArray(),
        value.showName,
        displayable
    ).onValueChange(::onValueChange)

    val mode by setting(modeSetting)

    init {
        choices.forEach(::tree)
    }

    override fun onToggled(state: Boolean): Boolean {
        val state = activeChoice.onToggled(state)
        inner.filterIsInstance<Toggleable>().forEach { it.onToggled(state) }
        return state
    }

    fun onValueChange(oldValue: String, newValue: String) {
        val newChoice = choices.firstOrNull {
            it.showName.equals(newValue, true)
        } ?: choices[0]

        if (activeChoice == newChoice) {
            return
        }

        if (activeChoice.running) {
            activeChoice.onToggled(false)
        }

        activeChoice = newChoice

        if (activeChoice.running) {
            activeChoice.onToggled(true)
        }
    }

}

abstract class Choice(
    name: String
): Configurable(name), Toggleable, Mode, EventListener {

    override val showName: String
        get() = this.name

    abstract val controller: ChoicesConfigurable<*>

    val isActive: Boolean
        get() = controller.activeChoice == this

    override fun <T : Setting<*>> setting(setting: T): T {
        setting.displayable { isActive }
        return super.setting(setting)
    }

    override fun onToggled(state: Boolean): Boolean {
        if (!state) {
            runCatching {
                removeEventListenerScope()
            }.onFailure {
                Drizzle.logger.error("Failed cancel sequences: ${it.message}", it)
            }
        }

        val state = super.onToggled(state)
        inner.filterIsInstance<Toggleable>().forEach { it.onToggled(state) }
        return state
    }

    override fun parent(): EventListener? = controller.listener

    override val running: Boolean
        get() = super.running && isActive

}