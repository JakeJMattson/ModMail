package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.dsl.precondition
import me.jakejmattson.modmail.locale.Locale
import me.jakejmattson.modmail.services.Configuration

@Suppress("unused")
fun validGuildPrecondition(configuration: Configuration) = precondition {
    guild?.let { configuration[it] } ?: fail(Locale.FAIL_GUILD_NOT_CONFIGURED)
}