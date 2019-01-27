package me.aberrantfox.warmbot.extensions

import net.dv8tion.jda.core.entities.*

fun Message.fullContent() = contentRaw + "\n" + attachmentsString()

fun Message.attachmentsString(): String =
        if(attachments.isNotEmpty()) attachments.map { it.url }.reduce { a, b -> "$a\n $b" } else ""

fun MessageEmbed.toTextString(): String {
    val embedNotation = "<---------- Embed ---------->"
    val stringBuilder = StringBuilder()

    stringBuilder.appendln(embedNotation)
    fields.forEach { stringBuilder.append("${it.name}\n${it.value}\n") }
    stringBuilder.appendln(embedNotation)

    return stringBuilder.toString()
}