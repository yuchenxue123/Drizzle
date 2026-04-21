package dev.meow.drizzle.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject

class Configs(
    vararg val configs: Config
) : Jsonable {

    override fun toJson(): JsonElement = JsonObject().apply {
        configs.forEach { config ->
            add(config.configName, config.toJson())
        }
    }

    override fun fromJson(json: JsonElement) {
        json.asJsonObject.entrySet().forEach { (name, value) ->
            configs.find { it.configName.equals(name, true) }?.fromJson(value)
        }
    }
}