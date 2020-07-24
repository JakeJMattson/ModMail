package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.preconditions.*

class ValidGuildPrecondition(private val configuration: Configuration) : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val guildId = event.guild?.id ?: return Pass

        if (!configuration.hasGuildConfig(guildId)) return Fail(Locale.FAIL_GUILD_NOT_CONFIGURED)

        return Pass
    }
}