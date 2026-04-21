package dev.meow.drizzle.utils.client

import dev.meow.drizzle.Drizzle
import java.io.File

object FileSystem {

    val ROOT_FOLD = File(
        mc.gameDirectory, Drizzle.CLIENT_NAME
    ).apply {
        if (!exists()) {
            mkdir()
        }
    }

    val CONFIG_FOLD = File(
        ROOT_FOLD, "configs"
    ).apply {
        if (!exists()) {
            mkdir()
        }
    }

}