package dev.meow.drizzle.features.module.misc

import dev.meow.drizzle.features.module.Module
import dev.meow.drizzle.features.module.ModuleCategory

object ModuleGlobalSettings : Module(
    name = "GlobalSettings",
    category = ModuleCategory.MISC,
) {
    val commandPrefix by text("CommandPrefix", ".")
}