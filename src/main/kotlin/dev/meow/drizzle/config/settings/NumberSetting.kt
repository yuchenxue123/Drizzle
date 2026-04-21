package dev.meow.drizzle.config.settings

import dev.meow.drizzle.config.Setting

abstract class NumberSetting<N>(
    name: String,
    value: N,
    val range: ClosedRange<N>,
    val step: N,
    displayable: () -> Boolean = { true },
) : Setting<N>(
    name, value, displayable
) where N : Number, N : Comparable<N> {

    val min: N = range.start

    val max: N = range.endInclusive

    override fun set(value: N) {
        val newValue = value.coerceIn(range.start, range.endInclusive)
        super.set(newValue)
    }

    abstract fun setPercent(value: Float)

}