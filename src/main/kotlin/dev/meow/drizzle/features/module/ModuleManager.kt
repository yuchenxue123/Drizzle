package dev.meow.drizzle.features.module

import dev.meow.drizzle.features.module.misc.ModuleGlobalSettings

object ModuleManager {

    val modules = mutableListOf<Module>()

    init {
        registerModules(
            ModuleGlobalSettings
        )
    }

    fun registerModules(vararg modules: Module) {
        modules.forEach(::registerModule)
    }

    fun registerModule(module: Module) {
        modules.add(module)
    }
}