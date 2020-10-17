package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.precondition
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration

fun validGuildPrecondition(configuration: Configuration) = precondition {
    val guildId = guild?.id?.longValue ?: return@precondition

    if (configuration[guildId] == null)
        fail(Locale.FAIL_GUILD_NOT_CONFIGURED)
}