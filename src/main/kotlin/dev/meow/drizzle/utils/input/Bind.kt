package dev.meow.drizzle.utils.input

import com.mojang.blaze3d.platform.InputConstants

class Bind(
    val key: InputConstants.Key
) {

    constructor(
        type: InputConstants.Type,
        keycode: Int
    ) : this(type.getOrCreate(keycode))

    constructor(
        keycode: Int,
    ) : this(InputConstants.Type.KEYSYM.getOrCreate(keycode))

    constructor(
        name: String
    ) : this(InputUtils.getKey(name))

    val keyName: String
        get() = when {
            key == InputConstants.UNKNOWN -> "NONE"
            else -> this.key.name
                .split('.')
                .drop(2)
                .joinToString(separator = "_")
                .uppercase()
        }

    val keycode: Int
        get() = key.value

}