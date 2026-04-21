package dev.meow.drizzle.injection.mixins.minecraft.client;

import dev.meow.drizzle.event.CoroutineTicker;
import dev.meow.drizzle.event.EventManager;
import dev.meow.drizzle.events.game.GameShutdownEvent;
import dev.meow.drizzle.events.game.GameStartEvent;
import dev.meow.drizzle.events.game.GameTickEvent;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(
            method = "run",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void onGameStart(CallbackInfo ci) {
        EventManager.INSTANCE.callEvent(GameStartEvent.INSTANCE);
    }
    
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void onBeginTick(CallbackInfo ci) {
        CoroutineTicker.INSTANCE.beginTick();
        CoroutineTicker.INSTANCE.tick();
        EventManager.INSTANCE.callEvent(GameTickEvent.INSTANCE);
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void onEndTick(CallbackInfo ci) {
        CoroutineTicker.INSTANCE.endTick();
    }


    @Inject(method = "destroy", at = @At(value = "HEAD"))
    private void onGameShutdown(CallbackInfo ci) {
        EventManager.INSTANCE.callEvent(GameShutdownEvent.INSTANCE);
    }

}
