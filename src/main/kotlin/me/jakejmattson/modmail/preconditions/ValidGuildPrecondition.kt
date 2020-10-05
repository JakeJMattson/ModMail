package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration

class ValidGuildPrecondition(private val configuration: Configuration) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val guildId = event.guild?.id?.longValue ?: return Pass

        if (configuration[guildId] == null) return Fail(Locale.FAIL_GUILD_NOT_CONFIGURED)

        return Pass
    }
}