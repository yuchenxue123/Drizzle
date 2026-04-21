package dev.meow.drizzle.injection.mixins.minecraft.gui;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.meow.drizzle.features.command.CommandSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class MixinCommandSuggestions {

    @Shadow
    private @Nullable ParseResults<ClientSuggestionProvider> currentParse;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private EditBox input;

    @Shadow
    private CommandSuggestions.@Nullable SuggestionsList suggestions;

    @Shadow
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    public abstract void showSuggestions(boolean immediateNarration);

    @Inject(
            method = "updateCommandInfo",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void injectSuggestions(CallbackInfo ci, @Local(name = "reader") StringReader reader) {
        String prefix = CommandSystem.INSTANCE.getPrefix();

        int length = prefix.length();

        if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor())) {
            reader.setCursor(reader.getCursor() + length);

            if (this.currentParse == null && this.minecraft.player != null) {
                this.currentParse = CommandSystem.dispatcher.parse(reader, this.minecraft.player.connection.getSuggestionsProvider());
            }

            int cursor = input.getCursorPosition();
            if (cursor >= length && (this.suggestions == null)) {
                this.pendingSuggestions = CommandSystem.dispatcher.getCompletionSuggestions(this.currentParse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.showSuggestions(false);
                    }
                });
            }

            ci.cancel();
        }
    }
}
