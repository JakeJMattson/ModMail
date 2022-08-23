package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.dsl.precondition
import me.jakejmattson.modmail.services.Configuration

@Suppress("unused")
fun prefixPrecondition(configuration: Configuration) = precondition {
    if (guild == null) return@precondition
    if (message == null) return@precondition
    if (author.isBot) return@precondition

    val guildConfig = configuration[guild!!] ?: return@precondition
    val content = message!!.content

    if (content.startsWith(guildConfig.prefix) || content.startsWith("/"))
        fail()
}