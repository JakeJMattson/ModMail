package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.precondition
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration

@Suppress("unused")
fun validGuildPrecondition(configuration: Configuration) = precondition {
    guild?.let { configuration[it] } ?: fail(Locale.FAIL_GUILD_NOT_CONFIGURED)
}