package dev.meow.drizzle.features.module

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.LocalPlayer

interface MinecraftShortcut {

    val mc: Minecraft
        get() = Minecraft.getInstance()

    val player: LocalPlayer
        get() = mc.player!!

    val world: ClientLevel
        get() = mc.level!!

    val network: ClientPacketListener
        get() = mc.connection!!

    val interaction: MultiPlayerGameMode
        get() = mc.gameMode!!

}