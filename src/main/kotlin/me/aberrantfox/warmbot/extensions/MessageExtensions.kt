package me.aberrantfox.warmbot.extensions

import net.dv8tion.jda.core.entities.Message

fun Message.fullContent(): String {
    val message = contentRaw
    var attachString = "\n"

    if(attachments.isNotEmpty()) {
        attachString += attachments.map { it.url }.reduce { a, b -> "$a\n $b" }
    }

    return message + attachString
}