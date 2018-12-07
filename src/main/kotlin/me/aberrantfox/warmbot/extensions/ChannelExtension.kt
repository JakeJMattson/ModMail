package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.core.entities.MessageChannel

private const val embedNotation = "<---------- Embed ---------->"

fun MessageChannel.archiveString(prefix: String) =
    iterableHistory.reversed()
        .filter { !it.contentRaw.toLowerCase().matches(Regex("($prefix){1,2}archive")) }
        .joinToString(System.lineSeparator()) {
            val stringBuilder = StringBuilder("${it.author.fullName()}: ")

            if (it.embeds.isNotEmpty()) {
                it.embeds.forEach {
                    stringBuilder.appendln(embedNotation)
                    it.fields.forEach { field -> stringBuilder.appendln(field.name).appendln(field.value) }
                    stringBuilder.appendln(embedNotation)
                }
            } else {
                stringBuilder.append(it.fullContent())
            }

            stringBuilder.toString()
        }
