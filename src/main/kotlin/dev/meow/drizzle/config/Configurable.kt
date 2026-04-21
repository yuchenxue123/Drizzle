package dev.meow.drizzle.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.meow.drizzle.config.settings.BooleanSetting
import dev.meow.drizzle.config.settings.TextSetting
import dev.meow.drizzle.config.settings.bind.BindSetting
import dev.meow.drizzle.config.settings.collection.EnumSetting
import dev.meow.drizzle.config.settings.number.FloatSetting
import dev.meow.drizzle.config.settings.number.IntegerSetting
import org.apache.logging.log4j.util.StringMap

open class Configurable(
    name: String,
    value: MutableCollection<Setting<*>> = mutableListOf()
) : Setting<MutableCollection<Setting<*>>>(name, value), Jsonable {

    val flatSettings: List<Setting<*>>
        get() = inner.flatMap { setting ->
            if (setting is Configurable) setting.flatSettings else listOf(setting)
        }

    open fun initialize() {
        inner.filterIsInstance<Configurable>().forEach(Configurable::initialize)
    }

    override fun toJson(): JsonElement = JsonObject().apply {
        inner.forEach { setting ->
            add(setting.name, setting.toJson())
        }
    }

    override fun fromJson(json: JsonElement) {
        json.asJsonObject.entrySet().forEach { (name, setting) ->
            inner.find { it.name.equals(name, true) }?.apply {
                fromJson(setting)
            }
        }
    }

    fun <T : Configurable> tree(configurable: T): T {
        inner.add(configurable)
        return configurable
    }

    open fun <T : Setting<*>> setting(setting: T): T {
        inner.add(setting)
        return setting
    }

    fun boolean(
        name: String,
        value: Boolean,
        displayable: () -> Boolean = { true },
    ) = setting(BooleanSetting(name, value, displayable))

    fun int(
        name: String,
        value: Int,
        range: IntRange,
        step: Int = 1,
        displayable: () -> Boolean = { true },
    ) = setting(IntegerSetting(name, value, range, step, displayable))

    fun float(
        name: String,
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        step: Float = 0.01f,
        displayable: () -> Boolean = { true },
    ) = setting(FloatSetting(name, value, range, step, displayable))

    fun text(
        name: String,
        value: String,
        displayable: () -> Boolean = { true },
    ) = setting(TextSetting(name, value, displayable))

    inline fun <reified E> enum(
        name: String,
        value: E,
        noinline visibility: () -> Boolean = { true }
    ) where E : Enum<E>, E : Mode = setting(EnumSetting(name, value, visibility))

    fun bind(
        name: String,
        value: Int,
        displayable: () -> Boolean = { true },
    ) = setting(BindSetting(name, value, displayable))

}