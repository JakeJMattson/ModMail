package me.aberrantfox.warmbot.extensions

import me.jakejmattson.kutils.api.extensions.jda.*
import me.jakejmattson.kutils.api.extensions.stdlib.sanitiseMentions
import net.dv8tion.jda.api.entities.*

private const val embedNotation = "<---------- Embed ---------->"

fun Message.fullContent() = contentRaw + "\n" +
    (attachments.takeIf { it.isNotEmpty() }
        ?.map { it.url }
        ?.reduce { a, b -> "$a\n $b" }
        ?: "")

fun Message.cleanContent() = fullContent().trimEnd().sanitiseMentions()

fun MessageEmbed.toTextString() =
    buildString {
        appendln(embedNotation)
        fields.forEach { append("${it.name}\n${it.value}\n") }
        appendln(embedNotation)
    }

fun Message.addFailReaction() = addReaction("❌").queue()

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

fun User.descriptor() = "$asMention :: ${fullName()} :: $id"