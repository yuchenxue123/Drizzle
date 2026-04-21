package dev.meow.drizzle.config.settings.number

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.meow.drizzle.config.settings.NumberSetting
import dev.meow.drizzle.utils.math.step

class IntegerSetting(
    name: String,
    value: Int,
    range: IntRange,
    step: Int,
    displayable: () -> Boolean = { true }
) : NumberSetting<Int>(name, value, range, step, displayable) {

    override fun setPercent(value: Float) {
        val percent = value.coerceIn(0f, 1f)
        val newValue = (min + (max - min) * percent).step(step.toFloat()).toInt()
        this.set(newValue)
    }

    override fun toJson(): JsonElement = JsonPrimitive(inner)

    override fun fromJson(json: JsonElement) {
        inner = json.asJsonPrimitive.asInt
    }

}