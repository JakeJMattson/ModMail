package me.jakejmattson.modmail.extensions

import dev.kord.core.entity.Embed
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.extensions.containsURL
import me.jakejmattson.discordkt.extensions.sanitiseMentions

private const val embedNotation = "<---------- Embed ---------->"

fun Message.fullContent() = content + "\n" +
    (attachments.takeIf { it.isNotEmpty() }
        ?.map { it.url }
        ?.reduce { a, b -> "$a\n $b" }
        ?: "")

suspend fun Message.cleanContent(discord: Discord) = fullContent().trimEnd().sanitiseMentions(discord)

fun Embed.toTextString() =
    buildString {
        appendLine(embedNotation)
        fields.forEach { append("${it.name}\n${it.value}\n") }
        appendLine(embedNotation)
    }

suspend fun MessageChannel.archiveString() = messages.toList()
    .reversed()
    .joinToString("\n") {
        buildString {
            append("${it.author?.tag}: ")

            if (it.embeds.isNotEmpty() && !it.containsURL()) {
                it.embeds.forEach { embed ->
                    appendLine()
                    append(embed.toTextString())
                }
            } else {
                append(it.fullContent())
            }
        }
    }

suspend fun Message.addFailReaction() = addReaction(Emojis.x.toReaction())