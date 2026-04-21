package dev.meow.drizzle.config

import kotlin.reflect.KProperty

abstract class Setting<T : Any>(
    val name: String,
    val defaultValue: T,
    displayable: () -> Boolean = { true },
): Jsonable {

    protected var inner: T = defaultValue
        set(value) {
            if (field == value) return
            val oldValue = field
            field = value
            listeners.forEach { it.invoke(oldValue, value) }
        }

    private val displayable  = mutableListOf<() -> Boolean>().apply {
        this@apply.add(displayable)
    }

    fun displayable(condition: () -> Boolean) {
        displayable += condition
    }

    open fun get(): T = inner

    open fun set(value: T) {
        this.inner = value
    }

    open fun reset() {
        this.inner = defaultValue
    }

    fun isVisible(): Boolean = displayable.all { it.invoke() }

    private val listeners = mutableListOf<(old: T, new: T) -> Unit>()

    fun onValueChange(listener: (old: T, new: T) -> Unit) = apply { listeners.add(listener) }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = inner

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =  set(value)

}