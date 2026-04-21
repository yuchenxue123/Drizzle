package dev.meow.drizzle.injection.mixins.minecraft.gui;

import dev.meow.drizzle.features.command.CommandSystem;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends MixinScreen {

    @Shadow
    protected EditBox input;

    @Shadow
    public abstract void handleChatInput(String msg, boolean addToRecent);

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void hookCancelLengthLimit(CallbackInfo ci) {
        // 取消输入框的最大长度限制
        this.input.setMaxLength(Integer.MAX_VALUE);
    }

    @Inject(method = "handleChatInput", at = @At(value = "HEAD"), cancellable = true)
    private void hookSendMessage(String msg, boolean addToRecent, CallbackInfo ci) {
        // 不要使用 ChatScreen.normalizeChatMessage 方法，因为会被砍成 256 长度的字符串
        String message = StringUtils.normalizeSpace(msg.trim());

        if (!message.isEmpty() && CommandSystem.INSTANCE.ensureIsCommand(message)) {
            this.minecraft.gui.getChat().addRecentChat(message);
            CommandSystem.INSTANCE.executeCommand(message);
            ci.cancel();
        }

        if (message.length() > 256) {
            // 如果消息长度超过 256，就分段发送
            // 使用 Kotlin 的 String.chunked 将字符串限长分段
            // 因为是一瞬间多次发送，在某些有发言频率限制的服务器上可能会被限制
            List<String> strings = StringsKt.chunked(message, 256);
            strings.forEach(m -> this.handleChatInput(m, false));
        }
    }

}
