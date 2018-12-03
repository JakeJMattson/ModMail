package me.aberrantfox.warmbot.extensions

import net.dv8tion.jda.core.entities.Message

fun Message.fullContent() =
    contentRaw + "\n" +
        if(attachments.isNotEmpty())
            attachments.map { it.url }.reduce { a, b -> "$a\n $b" }
        else
            ""