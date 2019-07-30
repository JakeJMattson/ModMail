package me.aberrantfox.warmbot.extensions

import java.util.ArrayDeque

private val unitStrings = arrayOf("day", "hour", "minute", "second")

private fun Long.toPluralString() = if (this == 1L) "" else "s"

fun Long.toMinimalTimeString(): String {
    val info = arrayOf(0, 0, 0, this@toMinimalTimeString)
    val stack = ArrayDeque<String>()

    fun applyStr(index: Int) =
        stack.push("${info[index]} ${unitStrings[index]}${info[index].toPluralString()}")

    fun evaluate(index: Int, maxValue: Int) =
        with(index + 1) {
            info[index] = info[this] / maxValue
            info[this] = info[this] % maxValue
            applyStr(this)
            info[index] != 0L
        }

    if (evaluate(2, 60) && evaluate(1, 60) && evaluate(0, 24))
        applyStr(0)

    return stack.joinToString(" ")
}