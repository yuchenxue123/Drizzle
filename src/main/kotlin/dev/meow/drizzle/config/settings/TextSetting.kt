package dev.meow.drizzle.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.meow.drizzle.config.Setting

class TextSetting(
    name: String,
    value: String,
    displayable: () -> Boolean = { true },
) : Setting<String>(name, value, displayable) {

    override fun toJson(): JsonElement = JsonPrimitive(inner)

    override fun fromJson(json: JsonElement) {
        inner = json.asJsonPrimitive.asString
    }

}