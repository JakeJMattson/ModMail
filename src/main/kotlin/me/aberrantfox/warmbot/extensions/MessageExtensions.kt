package me.aberrantfox.warmbot.extensions

import net.dv8tion.jda.core.entities.*

const val embedNotation = "<---------- Embed ---------->"

fun Message.fullContent() = contentRaw + "\n" + attachmentsString()

fun Message.attachmentsString(): String =
        if(attachments.isNotEmpty()) attachments.map { it.url }.reduce { a, b -> "$a\n $b" } else ""

fun MessageEmbed.toTextString() =
    StringBuilder().apply {
        appendln(embedNotation)
        fields.forEach { append("${it.name}\n${it.value}\n") }
        appendln(embedNotation)
    }.toString()