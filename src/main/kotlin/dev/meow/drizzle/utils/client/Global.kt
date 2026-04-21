package dev.meow.drizzle.utils.client

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.LocalPlayer

val mc: Minecraft
    inline get() = Minecraft.getInstance()

val player: LocalPlayer
    inline get() = mc.player!!

val world: ClientLevel
    inline get() = mc.level!!

val network: ClientPacketListener
    inline get() = mc.connection!!

val interaction: MultiPlayerGameMode
    inline get() = mc.gameMode!!

val ingame: Boolean
    get() = mc.player != null && mc.level != null


