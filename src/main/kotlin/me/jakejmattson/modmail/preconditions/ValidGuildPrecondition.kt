package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.dsl.precondition
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Locale

@Suppress("unused")
fun validGuildPrecondition(configuration: Configuration) = precondition {
    if ("configure".equals(command?.name, true))
        return@precondition

    guild?.let { configuration[it] } ?: fail(Locale.FAIL_GUILD_NOT_CONFIGURED)
}