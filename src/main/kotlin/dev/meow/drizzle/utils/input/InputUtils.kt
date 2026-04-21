package dev.meow.drizzle.utils.input

import com.mojang.blaze3d.platform.InputConstants

object InputUtils {

    @Suppress("SpellCheckingInspection")
    private val KEY_ALIAS = mapOf(
        "lshift" to "left.shift",
        "leftshift" to "left.shift",
        "left_shift" to "left.shift",

        "rshift" to "right.shift",
        "rightshift" to "right.shift",
        "right_shift" to "right.shift",

        "lctrl" to "left.control",
        "leftcontrol" to "left.control",
        "left_control" to "left.control",

        "rctrl" to "right.control",
        "rightcontrol" to "right.control",
        "right_control" to "right.control",

        "lalt" to "left.alt",
        "leftalt" to "left.alt",
        "left_alt" to "left.alt",

        "ralt" to "right.alt",
        "rightalt" to "right.alt",
        "right_alt" to "right.alt",

        "esc" to "escape",
        "del" to "delete"
    )

    fun getKey(simpleName: String): InputConstants.Key {
        if (simpleName.equals("none", true)) {
            return InputConstants.UNKNOWN
        }

        val lowercase = simpleName.lowercase()

        val name = when {
            KEY_ALIAS.containsKey(lowercase) -> KEY_ALIAS[lowercase]!!
            else -> lowercase
        }

        val key = "key.keyboard.$name"

        return InputConstants.getKey(key)
    }

}