package dev.meow.drizzle

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Drizzle : ModInitializer {

    @JvmField
    val logger: Logger = LogManager.getLogger("Drizzle")

    fun initialize() {
    }

    fun shutdown() {
    }

    override fun onInitialize() {
        logger.info("Drizzle is initializing...")
    }

}