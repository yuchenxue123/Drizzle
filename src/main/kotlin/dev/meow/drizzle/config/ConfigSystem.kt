package dev.meow.drizzle.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import dev.meow.drizzle.Drizzle
import dev.meow.drizzle.config.configs.ModuleConfig
import dev.meow.drizzle.utils.client.FileSystem
import java.io.File

object ConfigSystem {

    val logger = Drizzle.logger.subLogger("ConfigSystem")

    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val configs = Configs(
        ModuleConfig
    )

    fun save(name: String) {
        val file = File(FileSystem.CONFIG_FOLD, "$name.json")
            .apply {
                if (!exists()) {
                    createNewFile()
                }
            }

        val json = configs.toJson()

        runCatching {
            file.writeText(gson.toJson(json))
        }.onSuccess {
            logger.debug("配置 $name.json 保存成功")
        }.onFailure { err ->
            logger.error( "配置 $name.json 保存失败", err)
        }
    }

    fun load(name: String) {
        val file = File(FileSystem.CONFIG_FOLD, "$name.json")

        if (!file.exists()) {
            logger.warn("配置文件 $name.json 不存在 ")
            return
        }

        val json = JsonParser.parseReader(file.reader())
        configs.fromJson(json)
    }

}