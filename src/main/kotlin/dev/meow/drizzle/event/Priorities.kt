package dev.meow.drizzle.event

typealias EventPriority = Int

object Priorities {

    const val BOTTOM: EventPriority = -9999

    const val LOW: EventPriority = 50

    const val DEFAULT: EventPriority = 100

    const val MEDIUM: EventPriority = 150

    const val HIGH: EventPriority = 200

    const val HIGHEST: EventPriority = 500

    const val TOP: EventPriority = 9999

}