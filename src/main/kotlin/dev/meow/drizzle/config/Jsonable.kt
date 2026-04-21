package dev.meow.drizzle.config

import com.google.gson.JsonElement

interface Jsonable {

    fun toJson(): JsonElement

    fun fromJson(json: JsonElement)

}