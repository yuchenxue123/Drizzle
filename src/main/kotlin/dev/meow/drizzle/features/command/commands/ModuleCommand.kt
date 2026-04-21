package dev.meow.drizzle.features.command.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.CommandNode
import dev.meow.drizzle.features.command.Command
import dev.meow.drizzle.features.module.Module
import net.minecraft.client.multiplayer.ClientSuggestionProvider

class ModuleCommand(
    private val module: Module
) : Command(
    name = module.name.lowercase(),
    usage = ".${module.name} <setting> [value]"
) {

    override fun build(dispatcher: CommandDispatcher<ClientSuggestionProvider>): CommandNode<ClientSuggestionProvider> {
        return dispatcher.register(
            literal(name)
                .then(argument("setting", StringArgumentType.string())
                    .suggests { _, builder ->
                        val settings = module.flatSettings.map { it.name.lowercase() }
                        suggest(settings, builder)
                    })
                .then(argument("value", StringArgumentType.string()))
                .executes { context ->
                    val value = context.getArgument("value", String::class.java)
                    val settingName = context.getArgument("setting", String::class.java)

                    val setting = module.flatSettings.find { it.name == settingName } ?: return@executes 0

                    return@executes 1
                }
        )
    }

}