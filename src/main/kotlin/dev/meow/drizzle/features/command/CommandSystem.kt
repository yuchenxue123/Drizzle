package dev.meow.drizzle.features.command

import com.mojang.brigadier.CommandDispatcher
import dev.meow.drizzle.features.command.commands.ModuleCommand
import dev.meow.drizzle.features.module.ModuleManager
import dev.meow.drizzle.features.module.misc.ModuleGlobalSettings
import net.minecraft.client.multiplayer.ClientSuggestionProvider

object CommandSystem {

    val prefix: String
        inline get() = ModuleGlobalSettings.commandPrefix

    @JvmField
    val dispatcher = CommandDispatcher<ClientSuggestionProvider>()

    val commands = mutableListOf<Command>()

    init {
        registerCommands(
        )

        ModuleManager.modules.forEach { module ->
            registerCommands(ModuleCommand(module))
        }
    }

    fun ensureIsCommand(input: String): Boolean = input.startsWith(prefix)

    fun registerCommands(vararg commands: Command) {
        commands.forEach { command ->
            val node = command.build(dispatcher)

            command.aliases.forEach { alias ->

            }

            this.commands.add(command)
        }
    }

    fun executeCommand(message: String) {

    }


}