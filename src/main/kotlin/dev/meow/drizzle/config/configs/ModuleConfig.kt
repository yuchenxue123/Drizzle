package dev.meow.drizzle.config.configs

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.meow.drizzle.config.Config
import dev.meow.drizzle.features.module.ModuleManager
import kotlin.collections.component1
import kotlin.collections.component2

object ModuleConfig : Config {

    override val configName: String
        get() = "modules"

    override fun toJson(): JsonElement = JsonObject().apply {
        ModuleManager.modules.forEach { m -> add(m.name, m.toJson()) }
    }

    override fun fromJson(json: JsonElement) {
        json.asJsonObject.entrySet().forEach {(name, value) ->
            ModuleManager.modules.find { it.name.equals(name, true) }?.fromJson(value)
        }
    }

}