package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.jda.*
import net.dv8tion.jda.api.entities.MessageChannel

fun MessageChannel.archiveString() = iterableHistory.reversed().dropLast(1)
    .joinToString(System.lineSeparator()) {
        StringBuilder("${it.author.fullName()}: ").apply {
            if (it.embeds.isNotEmpty() && !it.containsURL()) {
                it.embeds.forEach { embed ->
                    this.appendln()
                    this.append(embed.toTextString())
                }
            } else {
                this.append(it.fullContent())
            }
        }.toString()
    }