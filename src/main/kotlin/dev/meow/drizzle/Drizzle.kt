package dev.meow.drizzle

import dev.meow.drizzle.config.ConfigSystem
import dev.meow.drizzle.event.EventListener
import dev.meow.drizzle.event.handler
import dev.meow.drizzle.events.game.GameShutdownEvent
import dev.meow.drizzle.events.game.GameStartEvent
import dev.meow.drizzle.features.command.CommandSystem
import dev.meow.drizzle.features.module.ModuleManager
import dev.meow.drizzle.utils.logger.Logger
import net.fabricmc.api.ModInitializer

object Drizzle : ModInitializer, EventListener {

    const val CLIENT_NAME = "Drizzle"

    @JvmField
    val logger: Logger = Logger.getLogger(CLIENT_NAME)

    private val onInitialize = handler<GameStartEvent> {
        ModuleManager
        CommandSystem

        ConfigSystem.load("default")
    }

    private val onShutdown = handler<GameShutdownEvent> {
        ConfigSystem.save("default")
    }

    override fun onInitialize() {
        logger.info("$CLIENT_NAME is initializing...")
    }

}