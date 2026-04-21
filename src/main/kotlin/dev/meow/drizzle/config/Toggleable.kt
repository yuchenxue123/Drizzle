package dev.meow.drizzle.config

interface Toggleable {

    fun onToggled(state: Boolean): Boolean {
        if (state) {
            onEnable()
        } else {
            onDisable()
        }

        return state
    }

    fun onEnable() {}

    fun onDisable() {}

}