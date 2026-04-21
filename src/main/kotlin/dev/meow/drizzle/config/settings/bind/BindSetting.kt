package dev.meow.drizzle.config.settings.bind

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.meow.drizzle.config.Setting
import dev.meow.drizzle.utils.input.Bind
import dev.meow.drizzle.utils.input.InputUtils

class BindSetting(
    name: String,
    value: Bind,
    displayable: () -> Boolean = { true },
) : Setting<Bind>(name, value, displayable) {

    constructor(
        name: String,
        value: Int,
        displayable: () -> Boolean = { true },
    ) : this(name, Bind(value), displayable)

    override fun toJson(): JsonElement = JsonPrimitive(inner.keyName)

    override fun fromJson(json: JsonElement) {
        inner = Bind(InputUtils.getKey(json.asJsonPrimitive.asString))
    }
}