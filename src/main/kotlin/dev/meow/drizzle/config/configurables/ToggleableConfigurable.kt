package dev.meow.drizzle.config.configurables

import dev.meow.drizzle.Drizzle
import dev.meow.drizzle.config.Configurable
import dev.meow.drizzle.config.Setting
import dev.meow.drizzle.config.Toggleable
import dev.meow.drizzle.config.settings.BooleanSetting
import dev.meow.drizzle.event.EventListener
import dev.meow.drizzle.event.removeEventListenerScope

class ToggleableConfigurable(
    name: String,
    enabled: Boolean,
    private val parent: EventListener? = null,
    displayable: () -> Boolean = { true },
) : Configurable(name), EventListener, Toggleable {

    private val enabledSetting = BooleanSetting(name, enabled, displayable)
        .onValueChange { old, new -> onToggled(new) }

    val enabled by setting(enabledSetting)

    override fun <T : Setting<*>> setting(setting: T): T {
        if (setting != enabledSetting) {
            setting.displayable { enabled }
        }
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

    override fun parent(): EventListener? = parent

    override val running: Boolean
        get() = super.running && enabled

}