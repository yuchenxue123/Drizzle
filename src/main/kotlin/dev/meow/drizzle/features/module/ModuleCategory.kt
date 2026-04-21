package dev.meow.drizzle.features.module

class ModuleCategory private constructor(
    val name: String
) {
    companion object {

        private val map = mutableMapOf<String, ModuleCategory>()

        @JvmStatic
        val entries: Collection<ModuleCategory>
            get() = map.values

        @JvmField
        val COMBAT = createCategory("Combat")

        @JvmField
        val MOVEMENT = createCategory("Movement")

        @JvmField
        val PLAYER = createCategory("Player")

        @JvmField
        val RENDER = createCategory("Redner")

        @JvmField
        val MISC = createCategory("Misc")

        @JvmStatic
        fun createCategory(name: String): ModuleCategory {
            return map.getOrPut(name) { ModuleCategory(name) }
        }
    }

    val modules: Collection<Module>
        get() = ModuleManager.modules.filter { it.category == this }.sortedBy { it.name }
}