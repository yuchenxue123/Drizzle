package dev.meow.drizzle.config.settings.collection

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.meow.drizzle.config.Setting

open class ModeSetting(
    name: String,
    val modes: Array<String>,
    value: String = modes[0],
    displayable: () -> Boolean = { true },
) : Setting<String>(name, value, displayable) {

    override fun set(value: String) {
        if (inner == value) {
            return
        }

        val newValue = modes.find { mode ->
            mode.equals(value, true)
        } ?: modes[0]

        super.set(newValue)
    }

    open fun asString() = inner

    open fun fromString(value: String) {
        this.set(value)
    }

    override fun toJson(): JsonElement = JsonObject().apply {
        add(
            "modes",
            JsonArray().apply {
                modes.forEach(this::add)
            }
        )
        addProperty("value", inner)
    }

    override fun fromJson(json: JsonElement) {
        val value = json.asJsonObject.get("value").asString
        this.set(value)
    }

}