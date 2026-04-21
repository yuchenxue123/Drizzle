package dev.meow.drizzle.utils.logger

import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger private constructor(private val name: String) {

    companion object {

        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        const val ANSI_RESET = "\u001B[0m"
        const val ANSI_BLACK = "\u001B[30m"
        const val ANSI_GREY = "\u001B[90m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
        const val ANSI_PURPLE = "\u001B[35m"
        const val ANSI_CYAN = "\u001B[36m"
        const val ANSI_WHITE = "\u001B[37m"

        fun getLogger(name: String) = Logger(name)

        fun getLogger(clazz: Class<*>) = getLogger(clazz.simpleName)

        fun getLogger(obj: Any) = getLogger(obj::class.java)

    }

    val out = PrintStream(System.out, true, Charsets.UTF_8)
    val err = PrintStream(System.err, true, Charsets.UTF_8)

    fun subLogger(subname: String): Logger {
        return Logger("$name/$subname")
    }

    fun verbose(message: String) {
        log(Level.VERBOSE, message)
    }

    fun debug(message: String) {
        log(Level.DEBUG, message)
    }

    fun info(message: String) {
        log(Level.INFO, message)
    }

    fun warn(message: String) {
        log(Level.WARN, message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        log(Level.ERROR, message)
        throwable?.printStackTrace(err)
    }

    fun wtf(message: String, throwable: Throwable? = null) {
        log(Level.ASSERT, message)
        throwable?.printStackTrace(err)
    }

    private fun log(lv: Level, message: String) {
        val level = lv.str()

        val time = LocalDateTime.now().format(FORMATTER).brackets().color(ANSI_CYAN)
        val thread = Thread.currentThread().name.brackets().color(ANSI_BLUE)
        val from = name.brackets(1).color(ANSI_PURPLE)
        val msg = if (lv >= Level.ERROR) message.color(ANSI_RED) else message

        val formatted = "$time $thread $level $from -> $msg"

        out.println(formatted)
    }

    private fun String.color(color: String): String {
        return "$color$this${ANSI_RESET}"
    }

    private fun String.brackets(type: Int = 0): String {
        return when (type) {
            1 -> "($this)"
            else -> "[$this]"
        }
    }

    private fun Level.str(): String {
        return name.brackets().color(color)
    }

    private enum class Level(val color: String) {
        VERBOSE(ANSI_GREY),
        DEBUG(ANSI_WHITE),
        INFO(ANSI_GREEN),
        WARN(ANSI_YELLOW),
        ERROR(ANSI_RED),
        ASSERT(ANSI_PURPLE),
    }
}