package me.jakejmattson.modmail.extensions

import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.x.emoji.*
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.arguments.ReportChannel

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
    .dropLast(1)
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

fun User.descriptor() = "$mention\n$tag\n${id.value}"

suspend fun Message.addFailReaction() = addReaction(Emojis.x.toReaction())
suspend fun CommandEvent<*>.reactSuccess() = reactWith(Emojis.whiteCheckMark)

suspend fun CommandEvent<*>.handleInvocation(reportChannel: ReportChannel) {
    if (reportChannel.wasTargeted)
        reactSuccess()
    else
        message.delete()
}