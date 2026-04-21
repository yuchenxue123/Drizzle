package dev.meow.drizzle.features.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import java.util.concurrent.CompletableFuture

abstract class Command(
    val name: String,
    val usage: String,
    val aliases: List<String> = listOf()
) {

    abstract fun build(dispatcher: CommandDispatcher<ClientSuggestionProvider>): CommandNode<ClientSuggestionProvider>

    protected fun literal(name: String): LiteralArgumentBuilder<ClientSuggestionProvider> {
        return LiteralArgumentBuilder.literal(name)
    }

    protected fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<ClientSuggestionProvider, T> {
        return RequiredArgumentBuilder.argument(name, type)
    }

    protected fun suggest(suggestions: Iterable<String>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val remaining = builder.remaining.lowercase()

        suggestions.forEach { suggestion ->
            if (suggestion.lowercase().startsWith(remaining)) {
                builder.suggest(suggestion)
            }
        }

        return builder.buildFuture()
    }

}