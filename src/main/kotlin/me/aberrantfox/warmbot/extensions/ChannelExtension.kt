package me.aberrantfox.warmbot.extensions

import me.jakejmattson.kutils.api.extensions.jda.*
import net.dv8tion.jda.api.entities.MessageChannel

fun MessageChannel.archiveString() = iterableHistory
    .reversed()
    .dropLast(1)
    .joinToString("\n") {
        buildString {
            append("${it.author.fullName()}: ")

            if (it.embeds.isNotEmpty() && !it.containsURL()) {
                it.embeds.forEach { embed ->
                    appendln()
                    append(embed.toTextString())
                }
            } else {
                append(it.fullContent())
            }
        }
    }