package dev.meow.drizzle.events.client

import dev.meow.drizzle.event.Event
import dev.meow.drizzle.features.module.Module

class ModuleToggleEvent(
    val module: Module,
    val state: Boolean
) : Event()